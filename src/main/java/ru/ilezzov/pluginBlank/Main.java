package ru.ilezzov.pluginBlank;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginBlank.file.PluginConfig;
import ru.ilezzov.pluginBlank.logger.PluginLogger;
import ru.ilezzov.pluginBlank.model.Response;
import ru.ilezzov.pluginBlank.properties.PluginProperties;
import ru.ilezzov.pluginBlank.properties.PropertiesManager;
import ru.ilezzov.pluginBlank.version.VersionControl;
import ru.ilezzov.pluginBlank.version.VersionData;
import ru.ilezzov.pluginBlank.version.VersionManager;

import java.io.File;

import static ru.ilezzov.pluginBlank.message.ErrorConstants.*;
import static ru.ilezzov.pluginBlank.message.MessageConstants.*;
import static ru.ilezzov.pluginBlank.version.VersionType.*;

public final class Main extends JavaPlugin {
    // logger
    private static PluginLogger logger;

    // properties
    @Getter
    private static PluginProperties properties;

    // version control
    @Getter
    private static VersionManager versionManager;
    @Getter
    private static VersionControl versionControl;

    // files
    @Getter
    private static PluginConfig pluginConfig;

    // metrics
    private static Metrics metrics;

    @Override
    public void onEnable() {
        // load logger
        logger = new PluginLogger(this, this.getName());

        // load properties
        final Response<PluginProperties> pluginPropertiesResponse = PropertiesManager.loadProperties("plugin.properties");

        if (!pluginPropertiesResponse.success()) {
            logger.error(PROPERTIES_NOT_LOADED.formatted(pluginPropertiesResponse.message()), pluginPropertiesResponse.error());
            stop();
            return;
        }

        properties = pluginPropertiesResponse.data();
        if (properties == null) {
            return;
        }

        // check version
        versionManager = new VersionManager(logger, properties);
        if (!checkVersion()) {
            stop();
            return;
        }

        // load config
        pluginConfig = loadConfig();
        if (pluginConfig.debug) {
            logger.setDebug(true);
            logger.debug("Debug mode is enabled");
        }

        // version control
        versionControl = new VersionControl(this, logger, pluginConfig, versionManager, properties);
        versionControl.startBackgroundCheckTask();
        versionControl.startCriticalNotifyTask();

        // load metrics
        metrics = new Metrics(this, properties.bstats());
    }

    @Override
    public void onDisable() {
        if (versionControl != null) {
            versionControl.stop();
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

        return switch (versionManager.getVersionType()) {
            case LATEST -> {
                logger.info(LATEST_VERSION.formatted(properties.currentVersion()));
                yield true;
            }
            case SUPPORTED -> {
                logger.info(SUPPORTED_VERSION.formatted(properties.currentVersion(), versionData.getLatest().getVersion()));
                logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                yield true;
            }
            case BLACKLIST -> {
                logger.info(BLACKLIST_VERSION.formatted(properties.currentVersion(), LAUNCHING_IS_NOT_POSSIBLE, versionData.getLatest().getVersion()));
                logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                yield false;
            }
            case OUTDATED -> {
                logger.info(OUTDATED_VERSION.formatted(properties.currentVersion(), LAUNCHING_IS_NOT_POSSIBLE, versionData.getCompatibility().getMinRequiredVersion()));
                logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                yield false;
            }
            case UNREACHABLE -> true;
        };
    }

    private PluginConfig loadConfig() {
        return (PluginConfig) ConfigManager.create(PluginConfig.class)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer());
                    opt.bindFile(new File(this.getDataFolder(), "config.yml"));
                    opt.removeOrphans(true);
                })
                .saveDefaults()
                .load(true);
    }
}
