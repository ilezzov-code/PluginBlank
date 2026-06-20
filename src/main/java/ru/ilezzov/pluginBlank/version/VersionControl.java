package ru.ilezzov.pluginBlank.version;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.pluginBlank.file.PluginConfig;
import ru.ilezzov.pluginBlank.logger.PluginLogger;
import ru.ilezzov.pluginBlank.properties.PluginProperties;

import java.util.concurrent.TimeUnit;

import static ru.ilezzov.pluginBlank.message.MessageConstants.*;
import static ru.ilezzov.pluginBlank.message.MessageConstants.BLACKLIST_VERSION;
import static ru.ilezzov.pluginBlank.message.MessageConstants.DOWNLOAD_VERSION_LINK;
import static ru.ilezzov.pluginBlank.message.MessageConstants.OUTDATED_VERSION;
import static ru.ilezzov.pluginBlank.message.MessageConstants.VERSION_CONTROL_COMMAND;

@RequiredArgsConstructor
public class VersionControl {
    private final Plugin plugin;
    private final PluginLogger logger;
    private final PluginConfig pluginConfig;
    private final VersionManager versionManager;
    private final PluginProperties properties;

    private BukkitTask backgroundCheckTask;
    private BukkitTask criticalNotifyTask;

    public void startBackgroundCheckTask() {
        final PluginConfig.VersionControl versionControl = pluginConfig.versionControl;
        final PluginConfig.Interval interval = versionControl.checkInterval;
        final PluginConfig.Security versionSecurity = versionControl.security;
        final long period = TimeUnit.valueOf(interval.unit).toSeconds(interval.value) * 20L;

        if (interval.enable) {
            logger.debug("VersionControl.backgroundCheckTask is started. Period in seconds: %s".formatted(period / 20));
            this.backgroundCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    () -> {
                        logger.debug("The version is automatically checked");
                        this.versionManager.loadVersionData();
                        final VersionData versionData = versionManager.getVersionData();;

                        switch (versionManager.getVersionType()) {
                            case LATEST -> logger.info(LATEST_VERSION.formatted(properties.currentVersion()));
                            case SUPPORTED -> {
                                logger.info(SUPPORTED_VERSION.formatted(properties.currentVersion(), versionData.getLatest().getVersion()));
                                logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                                logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                            }
                            case BLACKLIST -> {
                                if (versionSecurity.lockdownOnCritical) {
                                    logger.info(BLACKLIST_VERSION.formatted(properties.currentVersion(), AUTO_STOPPING_PLUGIN, versionData.getLatest().getVersion()));
                                    this.pluginStop();
                                } else {
                                    logger.info(BLACKLIST_VERSION.formatted(properties.currentVersion(), NOT_RECOMMENDED, versionData.getLatest().getVersion()));
                                }

                                logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                                logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                            }
                            case OUTDATED -> {
                                if (versionSecurity.lockdownOnCritical) {
                                    logger.info(OUTDATED_VERSION.formatted(properties.currentVersion(), AUTO_STOPPING_PLUGIN, versionData.getCompatibility().getMinRequiredVersion()));
                                    this.pluginStop();
                                } else {
                                    logger.info(OUTDATED_VERSION.formatted(properties.currentVersion(), NOT_RECOMMENDED, versionData.getCompatibility().getMinRequiredVersion()));
                                }
                                logger.info(DOWNLOAD_VERSION_LINK.formatted(versionData.getLatest().getDownloadUrl()));
                                logger.info(VERSION_CONTROL_COMMAND.formatted(properties.mainCommand()));
                            }
                        }
                    },
                    period, period
            );
        }
    }

    public void startCriticalNotifyTask() {
        final PluginConfig.Interval interval = this.pluginConfig.versionControl.security.criticalNotifyInterval;
        final long period = TimeUnit.valueOf(interval.unit).toSeconds(interval.value) * 20L;

        if (interval.enable) {
            logger.debug("VersionControl.criticalNotifyTask is started. Period in seconds: %s".formatted(period / 20));
            this.criticalNotifyTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    () -> {
                        logger.debug("Sending critical notify message");

                        final Component component = MiniMessage.miniMessage().deserialize("<red>CRITICAL");

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Bukkit.broadcast(component, this.pluginConfig.versionControl.notifyAdminsOnJoin.permission);
                            logger.info(component);
                        });
                    },
                    period, period
            );
        }
    }

    private void pluginStop() {
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().disablePlugin(plugin));
    }

    public void stop() {
        if (this.backgroundCheckTask != null) {
            this.backgroundCheckTask.cancel();

            this.logger.debug("VersionControl.backgroundCheckTask is stopped");
        }

        if (this.criticalNotifyTask != null) {
            this.criticalNotifyTask.cancel();

            this.logger.debug("VersionControl.criticalNotifyTask is stopped");
        }
    }
}
