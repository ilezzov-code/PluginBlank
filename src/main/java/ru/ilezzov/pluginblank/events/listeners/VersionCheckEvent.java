package ru.ilezzov.pluginblank.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.ilezzov.pluginblank.version.VersionManager;
import ru.ilezzov.pluginblank.permission.PermissionsChecker;
import ru.ilezzov.pluginblank.placeholder.PluginPlaceholder;

import static ru.ilezzov.pluginblank.Main.*;
import static ru.ilezzov.pluginblank.messages.PluginMessages.pluginHasErrorCheckVersionMessage;
import static ru.ilezzov.pluginblank.messages.PluginMessages.pluginUseOutdatedVersionMessage;

public class VersionCheckEvent implements Listener {
    private final PluginPlaceholder eventPlaceholders = new PluginPlaceholder();
    private final boolean isEnable = (getConfigFile().getBoolean("check_updates"));

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        if (!isEnable) {
            return;
        }

        if (!isOutdatedVersion()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!PermissionsChecker.hasPermission(player)) {
            return;
        }

        final VersionManager versionManager = getVersionManager();

        if (versionManager == null) {
            player.sendMessage(pluginHasErrorCheckVersionMessage(eventPlaceholders));
            return;
        }

        eventPlaceholders.addPlaceholder("{OUTDATED_VERS}", getPluginVersion());
        eventPlaceholders.addPlaceholder("{LATEST_VERS}", versionManager.getCurrentVersion());
        eventPlaceholders.addPlaceholder("{DOWNLOAD_LINK}", getPluginSettings().getUrlToDownloadLatestVersion());

        player.sendMessage(pluginUseOutdatedVersionMessage(eventPlaceholders));
    }
}
