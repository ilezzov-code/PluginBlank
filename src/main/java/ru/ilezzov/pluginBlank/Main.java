package ru.ilezzov.pluginBlank;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginBlank.logger.PluginLogger;
import ru.ilezzov.pluginBlank.model.Response;
import ru.ilezzov.pluginBlank.properties.PluginProperties;
import ru.ilezzov.pluginBlank.properties.PropertiesManager;
import ru.ilezzov.pluginBlank.version.VersionData;
import ru.ilezzov.pluginBlank.version.VersionManager;
import ru.ilezzov.pluginBlank.version.VersionType;

import static ru.ilezzov.pluginBlank.message.ErrorConstants.*;
import static ru.ilezzov.pluginBlank.message.MessageConstants.*;

public final class Main extends JavaPlugin {
    // logger
    private static PluginLogger logger;

    @Getter
    private static PluginProperties properties;

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

        // check version
        final Response<VersionData> versionDataResponse = VersionManager.loadVersionDate(properties.versionFileUrl());

        if (versionDataResponse.success()) {
            final VersionData versionData = versionDataResponse.data();
            final Response<VersionType> versionTypeResponse = VersionManager.getVersionType(versionData, properties.currentVersion());

            if (versionTypeResponse.success()) {
                boolean acceptVersion = switch (versionTypeResponse.data()) {
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
                        logger.info(BLACKLIST_VERSION.formatted(properties.currentVersion(), versionData.getLatest().getVersion()));
                        logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                        logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                        yield false;
                    }
                    case OUTDATED -> {
                        logger.info(OUTDATED_VERSION.formatted(properties.currentVersion(), versionData.getCompatibility().getMinRequiredVersion()));
                        logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                        logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                        yield false;
                    }
                    case UNREACHABLE -> {
                        logger.error(VERSION_CHECK_FAILED);
                        yield true;
                    }
                };

                if (!acceptVersion) {
                    stop();
                    return;
                }

            } else {
                logger.error(VERSION_CHECK_FAILED.formatted(versionTypeResponse.error()), versionTypeResponse.error());
            }

        } else {
            final Exception e = versionDataResponse.error();

            if (e != null) {
                logger.error(VERSION_DATA_NOT_LOADED.formatted(versionDataResponse.message()), e);
            } else {
                logger.error(VERSION_DATA_NOT_LOADED.formatted(versionDataResponse.message()));
            }
        }

        metrics = new Metrics(this, properties.bstats());
    }

    @Override
    public void onDisable() {

    }

    public void stop() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
