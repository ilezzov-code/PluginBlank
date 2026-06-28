package ru.ilezzov.pluginBlank.event.listeners;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ru.ilezzov.pluginBlank.color.Colorizer;
import ru.ilezzov.pluginBlank.file.MessageFile;
import ru.ilezzov.pluginBlank.permission.PermissionManager;
import ru.ilezzov.pluginBlank.permission.Permissions;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;
import ru.ilezzov.pluginBlank.properties.PluginProperties;
import ru.ilezzov.pluginBlank.version.VersionManager;

public class PlayerJoinEvent implements Listener {
    private final VersionManager versionManager;
    private final MessageFile message;
    private final BukkitAudiences audiences;
    private final PluginProperties properties;

    public PlayerJoinEvent(final VersionManager versionManager, final MessageFile message, final BukkitAudiences audiences, final PluginProperties properties) {
        this.versionManager = versionManager;
        this.message = message;
        this.audiences = audiences;
        this.properties = properties;
    }

    @EventHandler
    public void onPlayerJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!PermissionManager.hasPermission(player, Permissions.VERSION_NOTIFY)) {
            return;
        }

        if (this.versionManager.getVersionData() == null) {
            return;
        }

        final Colorizer colorizer = this.message.colorizer;
        final PluginPlaceholder placeholder = getPlaceholder();

        final Component message = switch (versionManager.getVersionType()) {
            case BLACKLIST -> colorizer.parse(
                    this.message.version.blacklist, placeholder
            );
            case OUTDATED -> colorizer.parse(
                    this.message.version.outdated, placeholder
            );
            case SUPPORTED -> colorizer.parse(
                    this.message.version.supported, placeholder
            );
            default -> null;
        };

        if (message != null) {
            this.audiences.player(player).sendMessage(message);
            this.audiences.player(player).sendMessage(
                    colorizer.parse(
                            this.message.version.download, placeholder
                    )
            );
        }
    }

    private @NotNull PluginPlaceholder getPlaceholder() {
        final PluginPlaceholder placeholder = new PluginPlaceholder(this.message.plugin.prefix, this.message.plugin.prefixError);

        placeholder.addPlaceholder("{CURRENT_VERSION}", this.properties.currentVersion());
        placeholder.addPlaceholder("{DOWNLOAD_LINK}", this.versionManager.getVersionData().getLatest().getDownloadUrl());
        placeholder.addPlaceholder("{LATEST_VERSION}", this.versionManager.getVersionData().getLatest().getVersion());
        placeholder.addPlaceholder("{ACTION}", this.message.version.action.noRecommended);
        return placeholder;
    }
}
