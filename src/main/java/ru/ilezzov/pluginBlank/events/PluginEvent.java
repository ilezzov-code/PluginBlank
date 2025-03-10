package ru.ilezzov.pluginBlank.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import static ru.ilezzov.pluginBlank.PluginBlank.*;

public class PluginEvent implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        event.getPlayer().sendMessage(legacySerialize.serialize("&aДобро пожаловать!"));
    }
}
