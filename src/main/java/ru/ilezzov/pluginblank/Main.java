package ru.ilezzov.pluginblank;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginblank.command.MainCommand;
import ru.ilezzov.pluginblank.database.DBConnection;
import ru.ilezzov.pluginblank.database.H2Connection;
import ru.ilezzov.pluginblank.events.VersionCheckEvent;
import ru.ilezzov.pluginblank.models.PluginFile;
import ru.ilezzov.pluginblank.logging.Logger;
import ru.ilezzov.pluginblank.logging.PaperLogger;
import ru.ilezzov.pluginblank.managers.VersionManager;
import ru.ilezzov.pluginblank.messages.ConsoleMessages;
import ru.ilezzov.pluginblank.models.PluginSettings;
import ru.ilezzov.pluginblank.utils.ListUtils;
import ru.ilezzov.pluginblank.utils.Metrics;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public final class Main extends JavaPlugin {
    // Plugin instance
    @Getter
    private static Main instance;

    // Logger
    @Getter
    private static Logger pluginLogger;

    // Settings
    @Getter
    private static PluginSettings pluginSettings;

    // Plugin info
    @Getter
    private static String prefix;
    @Getter
    private static String pluginVersion;
    @Getter
    private static String pluginContactLink;
    @Getter
    private static List<String> pluginDevelopers;
    @Getter
    private static boolean outdatedVersion;

    // Files
    @Getter
    private static PluginFile configFile;
    @Getter
    private static PluginFile messagesFile;
    @Getter
    private static PluginFile databaseFile;

    // Managers
    @Getter
    private static VersionManager versionManager;

    // Database
    @Getter
    private static DBConnection dbConnect;

    @Override
    public void onEnable() {
        instance = this;
        pluginLogger = new PaperLogger(this);

        // Load plugin files
        loadSettings();
        loadFiles();

        // Connect to Database
        setDbConnect();

        // Set plugin info
        loadPluginInfo();

        // Check Plugin Version
        checkPluginVersion();

        // Register command and events
        registerCommands();
        registerEvents();

        // Connect to Bstats
        createBstatsMetrics();

        sendEnableMessage();
    }

    private void disablePlugin(final Main plugin) {
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    @Override
    public void onDisable() {

        if (dbConnect != null) {
            try {
                dbConnect.close();
            } catch (SQLException e) {
                getPluginLogger().info(ConsoleMessages.errorOccurred("Couldn't close database connect: " + e.getMessage()));
                throw new RuntimeException(e);
            }
        }

        sendDisableMessage();
    }

    public static void loadFiles() {
        configFile = new PluginFile(Main.getInstance(), "config.yml");
        messagesFile = new PluginFile(Main.getInstance(), "messages/".concat(configFile.getString("language").concat(".yml")));
        databaseFile = new PluginFile(Main.getInstance(), "data/database.yml");
    }

    public static void registerCommands() {
        final PluginCommand mainCommand = Main.getInstance().getCommand("plugin-blank");

        if(mainCommand != null) {
            mainCommand.setExecutor(new MainCommand());
            mainCommand.setTabCompleter(new MainCommand());
        }
    }

    public static void reloadPrefix() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
    }

    public static void reloadEvents() {
        HandlerList.unregisterAll();
        registerEvents();
    }

    private void setDbConnect() {
        final HashMap<String, String> dbArgs = new HashMap<>();
        dbArgs.put("PATH", getDBFilePath());

        createDBConnection(databaseFile.getString("Database.type"), dbArgs);

        try {
            dbConnect.connect();
            pluginLogger.info(ConsoleMessages.successConnectToDatabase());
        } catch (SQLException e) {
            getPluginLogger().info(ConsoleMessages.errorOccurred("Couldn't connect to the database: " + e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    private void createDBConnection(final String dbType, final HashMap<String, String> dbArgs) {
        switch (dbType.toLowerCase()) {
            default -> dbConnect = new H2Connection(dbArgs.get("PATH"));
        }
    }

    private String getDBFilePath() {
        return new File(Paths.get(this.getDataFolder().getPath(), "data").toString(), "database.db").getPath();
    }

    private static void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new VersionCheckEvent(), Main.getInstance());
    }

    private void sendEnableMessage() {
        pluginLogger.info(ConsoleMessages.enablePlugin(ListUtils.listToString(getPluginDevelopers()), getPluginVersion(), getPluginContactLink()));
    }

    private void sendDisableMessage() {
        pluginLogger.info(ConsoleMessages.disablePlugin(ListUtils.listToString(getPluginDevelopers()), getPluginVersion(), getPluginContactLink()));
    }

    private void loadSettings() {
        try {
            pluginSettings = new PluginSettings();
        } catch (IOException e) {
            pluginLogger.info("An error occurred when loading the plugin settings");
            throw new RuntimeException(e);
        }
    }

    private void loadPluginInfo() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
        pluginVersion = this.getDescription().getVersion();
        pluginDevelopers = this.getDescription().getAuthors();
        pluginContactLink = this.getDescription().getWebsite();
    }

    private void checkPluginVersion() {
        if (configFile.getBoolean("check_updates")) {
            try {
                versionManager = new VersionManager(pluginVersion, pluginSettings.getUrlToFileVersion());

                if (versionManager.check()) {
                    pluginLogger.info(ConsoleMessages.latestPluginVersion(pluginVersion));
                    outdatedVersion = false;
                } else {
                    pluginLogger.info(ConsoleMessages.outdatedPluginVersion(pluginVersion, versionManager.getCurrentPluginVersion(), pluginSettings.getUrlToDownloadLatestVersion()));
                    outdatedVersion = true;
                }
            } catch (URISyntaxException e) {
                pluginLogger.info(ConsoleMessages.errorOccurred("Invalid link to the GitHub file. link = ".concat(versionManager.getUrlToFileVersion())));
            } catch (IOException | InterruptedException e ) {
                pluginLogger.info(ConsoleMessages.errorOccurred("Couldn't send a request to get the plugin version"));
            }
        }
    }

    private void createBstatsMetrics() {
        if (pluginSettings.isBstatsEnable()) {
            new Metrics(this, pluginSettings.getBstatsPluginId());
        }
    }


}
