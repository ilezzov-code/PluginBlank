package ru.ilezzov.pluginblank;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginblank.database.DatabaseType;
import ru.ilezzov.pluginblank.database.SQLDatabase;
import ru.ilezzov.pluginblank.database.adapter.MySQLDatabase;
import ru.ilezzov.pluginblank.database.adapter.PostgreSQLDatabase;
import ru.ilezzov.pluginblank.database.adapter.SQLiteDatabase;
import ru.ilezzov.pluginblank.events.EventManager;
import ru.ilezzov.pluginblank.file.PluginFile;
import ru.ilezzov.pluginblank.logging.Logger;
import ru.ilezzov.pluginblank.logging.PaperLogger;
import ru.ilezzov.pluginblank.version.VersionManager;
import ru.ilezzov.pluginblank.messages.ConsoleMessages;
import ru.ilezzov.pluginblank.settings.PluginSettings;
import ru.ilezzov.pluginblank.stats.PluginStats;
import ru.ilezzov.pluginblank.utils.ListUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static ru.ilezzov.pluginblank.commands.CommandManager.loadCommands;
import static ru.ilezzov.pluginblank.messages.ConsoleMessages.*;

public final class Main extends JavaPlugin {
    // API
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
    @Getter
    private static String messageLanguage;
    @Getter
    private static boolean enableLogging;

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
    private static SQLDatabase database;

    // Events
    @Getter
    private static EventManager eventManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginLogger = new PaperLogger(this);
        instance = this;

        // Load files
        loadSettings();
        loadFiles();

        // Load plugin info
        loadPluginInfo();

        // Check plugin version
        checkPluginVersion();

        // Connect to the database
        createDatabase();

        try {
            database.connect();
            database.initialize();
            pluginLogger.info(successConnectToDatabase());
        } catch (SQLException | IOException e) {
            pluginLogger.info(errorOccurred(e.getMessage()));
            throw new RuntimeException(e);
        }

        // Load managers
        loadManagers();

        // Load commands and events
        loadCommands();
        loadEvents();

        // Load Metrics
        loadMetrics();

        // Send enable message
        sendEnableMessage();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (database != null) {
            try {
                database.disconnect();
            } catch (SQLException e) {
                pluginLogger.info(ConsoleMessages.errorOccurred(e.getMessage()));
            }
        }
    }

    public static void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(Main.getInstance());
    }

    public static void checkPluginVersion() {
        if (configFile.getBoolean("check_updates")) {
            versionManager = new VersionManager(pluginVersion, pluginSettings.getUrlToFileVersion());

            switch (versionManager.check()) {
                case LATEST -> {
                    pluginLogger.info(latestPluginVersion(pluginVersion));
                    outdatedVersion = false;
                }
                case SUPPORTING -> {
                    pluginLogger.info(legacyPluginVersion(pluginVersion, versionManager.getCurrentVersion(), versionManager.getCurrentVersionUrl()));
                    outdatedVersion = true;
                }
                case NOT_SUPPORTED -> {
                    pluginLogger.info(versionNotSupported(pluginVersion, versionManager.getCurrentVersion(), versionManager.getCurrentVersionUrl()));
                    disablePlugin();
                }
            }
        }
    }

    public static void createDatabase() {
        final ConfigurationSection section = databaseFile.getConfig().getConfigurationSection("Database");
        assert section != null;
        final String type = section.getString("type", "SQLITE");

        database = switch (type.toUpperCase()) {
            case "MYSQL" -> new MySQLDatabase(
                    section.getString("host"),
                    section.getInt("port"),
                    section.getString("database"),
                    section.getString("username"),
                    section.getString("password"),
                    DatabaseType.MYSQL
            );
            case "POSTGRESQL" -> new PostgreSQLDatabase(
                    section.getString("host"),
                    section.getInt("port"),
                    section.getString("database"),
                    section.getString("username"),
                    section.getString("password"),
                    DatabaseType.POSTGRESQL
            );
            default -> new SQLiteDatabase(new File(Main.getInstance().getDataFolder().getPath(), "data/database.db").getPath(), DatabaseType.SQLITE);
        };
    }

    private void loadSettings() {
        try {
            pluginSettings = new PluginSettings();
        } catch (IOException e) {
            pluginLogger.info(errorOccurred(e.getMessage()));
        }
    }

    private void loadFiles() {
        configFile = new PluginFile(Main.getInstance(), "config.yml");
        messageLanguage = configFile.getString("language");
        messagesFile = new PluginFile(Main.getInstance(), "messages/".concat(messageLanguage).concat(".yml"));
        databaseFile = new PluginFile(Main.getInstance(), "data/database.yml");
    }

    private void loadEvents() {
        eventManager = new EventManager(this);
        eventManager.loadEvents();
    }

    private void loadPluginInfo() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
        pluginVersion = this.getDescription().getVersion();
        pluginDevelopers = this.getDescription().getAuthors();
        pluginContactLink = this.getDescription().getWebsite();
    }

    public static void reloadPluginInfo() {
        prefix = getMessagesFile().getString("Plugin.plugin-prefix");
    }

    public static void reloadFiles() {
        configFile.reload();

        final String messageLanguage = configFile.getString("language");

        if (!messageLanguage.equals(getMessageLanguage())) {
            messagesFile = new PluginFile(Main.getInstance(), "messages/".concat(messageLanguage).concat(".yml"));
        } else {
            messagesFile.reload();
        }

        databaseFile.reload();
    }

    private void loadManagers() {
    }

    private void loadMetrics() {
        new PluginStats(this);
    }

    private void sendEnableMessage() {
        pluginLogger.info(pluginEnable(ListUtils.listToString(getPluginDevelopers()), getPluginVersion(), getPluginContactLink()));
    }
}
