package ru.ilezzov.pluginBlank;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import lombok.Getter;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.relocation.Relocation;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginBlank.color.Colorizer;
import ru.ilezzov.pluginBlank.command.CommandManager;
import ru.ilezzov.pluginBlank.event.EventManager;
import ru.ilezzov.pluginBlank.file.ConfigFile;
import ru.ilezzov.pluginBlank.file.MessageFile;
import ru.ilezzov.pluginBlank.logger.PluginLogger;
import ru.ilezzov.pluginBlank.message.game.MessageManager;
import ru.ilezzov.pluginBlank.model.Response;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;
import ru.ilezzov.pluginBlank.properties.PluginProperties;
import ru.ilezzov.pluginBlank.properties.PropertiesManager;
import ru.ilezzov.pluginBlank.version.VersionControl;
import ru.ilezzov.pluginBlank.version.VersionData;
import ru.ilezzov.pluginBlank.version.VersionManager;
import ru.ilezzov.pluginBlank.version.VersionType;

import java.io.File;

import static ru.ilezzov.pluginBlank.message.console.ErrorConstants.PROPERTIES_NOT_LOADED;

public final class Main extends JavaPlugin {
    @Getter
    private BukkitAudiences audiences;

    // logger
    @Getter
    private PluginLogger pluginLogger;

    // message
    @Getter
    private MessageManager messageManager;

    // properties
    @Getter
    private PluginProperties properties;

    // version control
    @Getter
    private VersionManager versionManager;
    @Getter
    private VersionControl versionControl;

    // files
    @Getter
    private ConfigFile configFile;
    @Getter
    private MessageFile messageFile;

    // events
    @Getter
    private EventManager eventManager;

    // metrics
    private Metrics metrics;


    @Override
    public void onLoad() {
        loadLibraries();
    }

    @Override
    public void onEnable() {

        // load audience
        this.audiences = BukkitAudiences.create(this);

        // load logger
        this.pluginLogger = new PluginLogger(this, this.getName());

        // load properties
        final Response<PluginProperties> pluginPropertiesResponse = PropertiesManager.loadProperties("plugin.properties");

        if (!pluginPropertiesResponse.success()) {
            pluginLogger.error(PROPERTIES_NOT_LOADED.formatted(pluginPropertiesResponse.message()), pluginPropertiesResponse.error());
            stop();
            return;
        }

        this.properties = pluginPropertiesResponse.data();
        if (properties == null) {
            return;
        }

        // load files
        this.configFile = loadConfig();
        if (configFile.debug) {
            pluginLogger.setDebug(true);
            pluginLogger.debug("Debug mode is enabled");
        }

        this.messageFile = loadMessageFile(this.configFile.language.concat(".yml"));
        this.messageManager = new MessageManager(this, this.audiences, this.messageFile);
        this.messageFile = loadMessageFile(this.configFile.language.concat(".yml"));
        this.messageManager = new MessageManager(this, this.audiences, this.messageFile);


        // check version
        this.versionManager = new VersionManager(pluginLogger, properties);
        if (this.configFile.versionControl.checkOnStartup) {
            if (!checkVersion()) {
                stop();
                return;
            }
        }

        // version control
        this.versionControl = new VersionControl(this);
        versionControl.startBackgroundCheckTask();
        versionControl.startCriticalNotifyTask();

        // events
        this.eventManager = new EventManager(this);
        eventManager.registerEvents();

        // commands
        new CommandManager(this).loadCommands();

        // load metrics
        this.metrics = new Metrics(this, properties.bstats());
    }

    @Override
    public void onDisable() {
        if (versionControl != null) {
            versionControl.stop();
        }

        if (eventManager != null) {
            eventManager.unregisterEvents();
        }

        if (audiences != null) {
            audiences.close();
        }
    }

    public void stop() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    private boolean checkVersion() {
        final VersionData versionData = versionManager.getVersionData();

        if (versionData == null) {
            return true;
        }

        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.messageFile.plugin.prefix, this.messageFile.plugin.prefixError
        );
        
        final Colorizer colorizer = this.messageFile.colorizer;

        placeholder.addPlaceholder("{CURRENT_VERSION}", this.properties.currentVersion());
        placeholder.addPlaceholder("{LATEST_VERSION}", versionData.getLatest().getVersion());
        placeholder.addPlaceholder("{DOWNLOAD_LINK}", versionData.getLatest().getDownloadUrl());

        if (versionManager.getVersionType() == VersionType.LATEST) {
            this.pluginLogger.info(
                    colorizer.parse(
                            this.messageFile.version.latest, placeholder
                    )
            );
            return true;
        }

        final boolean accepted = switch (versionManager.getVersionType()) {
            case SUPPORTED -> {
                pluginLogger.info(
                        colorizer.parse(
                                this.messageFile.version.supported, placeholder
                        )
                );
                yield true;
            }
            case BLACKLIST -> {
                placeholder.addPlaceholder("{ACTION}", this.messageFile.version.action.impossibleLaunch);
                pluginLogger.info(
                        colorizer.parse(
                                this.messageFile.version.blacklist, placeholder
                        )
                );
                yield false;
            }
            case OUTDATED -> {
                placeholder.addPlaceholder("{ACTION}", this.messageFile.version.action.impossibleLaunch);
                pluginLogger.info(
                        colorizer.parse(
                                this.messageFile.version.outdated, placeholder
                        )
                );
                yield false;
            }
            default -> true;
        };

        pluginLogger.info(
                colorizer.parse(
                        this.messageFile.version.download, placeholder
                )
        );

        return accepted;
    }

    private ConfigFile loadConfig() {
        return (ConfigFile) ConfigManager.create(ConfigFile.class)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer());
                    opt.bindFile(new File(this.getDataFolder(), "config.yml"));
                    opt.removeOrphans(true);
                })
                .saveDefaults()
                .load(true);
    }

    public void reloadMessageFile(final String oldFile, final String file) {
        if (oldFile.equalsIgnoreCase(file)) {
            this.messageFile.load();
            return;
        }

        this.messageFile = loadMessageFile(file.concat(".yml"));
    }

    private MessageFile loadMessageFile(final String file) {
        final File messageDir = new File(this.getDataFolder(), "messages");
        final File messageFile = new File(messageDir, file);

        if (!messageFile.exists()) {
            messageFile.getParentFile().mkdirs();
            this.saveResource("messages/".concat(file), false);
        }

        return (MessageFile) ConfigManager.create(MessageFile.class)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer());
                    opt.bindFile(messageFile);
                    opt.removeOrphans(true);
                })
                .saveDefaults()
                .load(true);
    }

    private void loadLibraries() {
        final LibraryManager libraryManager = new BukkitLibraryManager(this);

        libraryManager.addMavenCentral();
        libraryManager.addSonatype();

        final Relocation bStatsRelocation = new Relocation("org{}bstats", "ru{}ilezzov{}pluginBlank{}libs{}stats");
        final Library bStats = Library.builder()
                .groupId("org{}bstats")
                .artifactId("bstats-bukkit")
                .version("3.2.1")
                .relocate(bStatsRelocation)
                .resolveTransitiveDependencies(true)
                .build();

        final Relocation kyoriRelocation = new Relocation("net{}kyori", "ru{}ilezzov{}pluginBlank{}libs{}kyori");
        final Library adventureApi = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-api")
                .version("4.17.0")
                .relocate(kyoriRelocation)
                .resolveTransitiveDependencies(true)
                .build();
        final Library adventureTextMiniMessage = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.17.0")
                .relocate(kyoriRelocation)
                .resolveTransitiveDependencies(true)
                .build();
        final Library adventurePlatformBukkit = Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-platform-bukkit")
                .version("4.4.1")
                .relocate(kyoriRelocation)
                .resolveTransitiveDependencies(true)
                .build();

        libraryManager.loadLibrary(bStats);

        libraryManager.loadLibrary(adventureApi);
        libraryManager.loadLibrary(adventureTextMiniMessage);
        libraryManager.loadLibrary(adventurePlatformBukkit);
    }
}
