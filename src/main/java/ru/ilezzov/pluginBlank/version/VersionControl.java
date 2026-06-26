package ru.ilezzov.pluginBlank.version;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.ilezzov.pluginBlank.Main;
import ru.ilezzov.pluginBlank.color.Colorizer;
import ru.ilezzov.pluginBlank.file.PluginConfig;
import ru.ilezzov.pluginBlank.file.PluginMessage;
import ru.ilezzov.pluginBlank.logger.PluginLogger;
import ru.ilezzov.pluginBlank.permission.PermissionManager;
import ru.ilezzov.pluginBlank.permission.Permissions;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;
import ru.ilezzov.pluginBlank.properties.PluginProperties;

public class VersionControl {
    private final Plugin plugin;
    private final PluginLogger pluginLogger;
    private final PluginConfig pluginConfig;
    private final VersionManager versionManager;
    private final PluginProperties properties;
    private final PluginMessage message;
    private final BukkitAudiences audiences;

    private BukkitTask backgroundCheckTask;
    private BukkitTask criticalNotifyTask;

    public VersionControl(final Main plugin) {
        this.plugin = plugin;
        this.pluginLogger = plugin.getPluginLogger();
        this.pluginConfig = plugin.getPluginConfig();
        this.versionManager = plugin.getVersionManager();
        this.properties = plugin.getProperties();
        this.message = plugin.getMessage();
        this.audiences = plugin.getAudiences();
    }

    public void startBackgroundCheckTask() {
        final PluginConfig.VersionControl versionControl = pluginConfig.versionControl;
        final PluginConfig.Interval interval = versionControl.checkInterval;
        final PluginConfig.Security versionSecurity = versionControl.security;
        final long period = interval.unit.toSeconds(interval.value) * 20L;

        if (interval.enable) {
            this.pluginLogger.debug("VersionControl.backgroundCheckTask is started. Period in seconds: %s".formatted(period / 20));
            this.backgroundCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    () -> {
                        this.pluginLogger.debug("The version is automatically checked");
                        this.versionManager.loadVersionData();
                        final VersionData versionData = versionManager.getVersionData();

                        if (versionData == null) {
                            return;
                        }

                        final PluginPlaceholder placeholder = new PluginPlaceholder(
                                this.message.plugin.prefix, this.message.plugin.prefixError
                        );
                        
                        final Colorizer colorizer = this.message.colorizer;
                        
                        placeholder.addPlaceholder("{CURRENT_VERSION}", this.properties.currentVersion());
                        placeholder.addPlaceholder("{LATEST_VERSION}", versionData.getLatest().getVersion());
                        placeholder.addPlaceholder("{DOWNLOAD_LINK}", versionData.getLatest().getDownloadUrl());

                        if (versionManager.getVersionType() == VersionType.LATEST) {
                            pluginLogger.info(
                                    colorizer.parse(
                                            this.message.version.latest, placeholder
                                    )
                            );
                            return;
                        }

                        switch (versionManager.getVersionType()) {
                            case SUPPORTED -> this.pluginLogger.info(
                                    colorizer.parse(
                                            this.message.version.supported, placeholder
                                    )
                            );
                            case BLACKLIST -> {
                                final String action = versionSecurity.lockdownOnCritical ? 
                                        this.message.version.action.autoStopping : this.message.version.action.noRecommended;
                                placeholder.addPlaceholder("{ACTION}", action);
                                
                                this.pluginLogger.info(
                                        colorizer.parse(
                                                this.message.version.blacklist, placeholder
                                        )
                                );
                                if (versionSecurity.lockdownOnCritical) {
                                    this.pluginStop();
                                }
                            }
                            case OUTDATED -> {
                                final String action = versionSecurity.lockdownOnCritical ?
                                        this.message.version.action.autoStopping : this.message.version.action.noRecommended;
                                placeholder.addPlaceholder("{ACTION}", action);
                                
                                this.pluginLogger.info(
                                        colorizer.parse(
                                                this.message.version.outdated, placeholder
                                        )
                                );
                                if (versionSecurity.lockdownOnCritical) {
                                    this.pluginStop();
                                }
                            }
                        }

                        pluginLogger.info(
                                colorizer.parse(
                                        this.message.version.download, placeholder
                                )
                        );
                    },
                    period, period
            );
        }
    }

    public void startCriticalNotifyTask() {
        final PluginConfig.Interval interval = this.pluginConfig.versionControl.security.criticalNotifyInterval;
        final long period = interval.unit.toSeconds(interval.value) * 20L;

        if (interval.enable) {
            this.pluginLogger.debug("VersionControl.criticalNotifyTask is started. Period in seconds: %s".formatted(period / 20));
            this.criticalNotifyTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    () -> {
                        this.pluginLogger.debug("Sending critical notify message");
                        final Colorizer colorizer = this.message.colorizer;

                        final PluginPlaceholder placeholder = new PluginPlaceholder(this.message.plugin.prefix, this.message.plugin.prefixError);

                        placeholder.addPlaceholder("{CURRENT_VERSION}", this.properties.currentVersion());
                        placeholder.addPlaceholder("{DOWNLOAD_LINK}", this.versionManager.getVersionData().getLatest().getDownloadUrl());
                        placeholder.addPlaceholder("{LATEST_VERSION}", this.versionManager.getVersionData().getLatest().getVersion());
                        placeholder.addPlaceholder("{ACTION}", this.message.version.action.noRecommended);

                        Component message = switch (versionManager.getVersionType()) {
                            case BLACKLIST -> colorizer.parse(
                                    this.message.version.blacklist, placeholder
                            );
                            case OUTDATED -> colorizer.parse(
                                    this.message.version.outdated, placeholder
                            );
                            default -> null;
                        };

                        if (message != null) {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                    if (PermissionManager.hasPermission(player, Permissions.VERSION_NOTIFY)) {
                                        audiences.player(player).sendMessage(message);
                                        audiences.player(player).sendMessage(
                                                colorizer.parse(
                                                        this.message.version.download, placeholder
                                                )
                                        );
                                    }
                                }
                            });
                            this.pluginLogger.info(message);
                            this.pluginLogger.info(colorizer.parse(
                                    this.message.version.download, placeholder
                            ));
                        }
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

            this.pluginLogger.debug("VersionControl.backgroundCheckTask is stopped");
        }

        if (this.criticalNotifyTask != null) {
            this.criticalNotifyTask.cancel();

            this.pluginLogger.debug("VersionControl.criticalNotifyTask is stopped");
        }
    }
}
