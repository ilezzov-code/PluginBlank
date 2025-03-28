package ru.ilezzov.pluginblank.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.ilezzov.pluginblank.Main;
import ru.ilezzov.pluginblank.messages.PluginMessages;
import ru.ilezzov.pluginblank.models.PluginPlaceholder;
import ru.ilezzov.pluginblank.utils.PermissionsChecker;

public class VersionCheckEvent implements Listener {
    private final PluginPlaceholder eventPlaceholders = new PluginPlaceholder();
    private final boolean isEnable = (Main.getConfigFile().getBoolean("check_updates"));

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        if (!isEnable) {
            return;
        }

        if (!Main.isOutdatedVersion()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!PermissionsChecker.hasPermission(player)) {
            return;
        }

        eventPlaceholders.addPlaceholder("{OUTDATED_VERS}", Main.getPluginVersion());
        eventPlaceholders.addPlaceholder("{LATEST_VERS}", Main.getVersionManager().getCurrentPluginVersion());
        eventPlaceholders.addPlaceholder("{DOWNLOAD_LINK}", Main.getPluginSettings().getUrlToDownloadLatestVersion());

        player.sendMessage(PluginMessages.pluginOutdatedVersionMessage(eventPlaceholders));
    }
}
