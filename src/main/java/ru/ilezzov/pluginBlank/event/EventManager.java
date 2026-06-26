package ru.ilezzov.pluginBlank.event;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import ru.ilezzov.pluginBlank.Main;
import ru.ilezzov.pluginBlank.event.listeners.PlayerJoinEvent;
import ru.ilezzov.pluginBlank.file.PluginConfig;
import ru.ilezzov.pluginBlank.logger.PluginLogger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManager {
    private final Main plugin;
    private final PluginConfig pluginConfig;
    private final PluginLogger logger;

    private final Set<Listener> registeredListener = new HashSet<>();

    public EventManager(final Main plugin) {
        this.plugin = plugin;
        this.pluginConfig = this.plugin.getPluginConfig();
        this.logger = plugin.getPluginLogger();
    }

    public void registerEvents() {
        final Map<Listener, Boolean> listeners = loadListeners();

        for (final Listener listener : listeners.keySet()) {
            if (listeners.get(listener)) {
                Bukkit.getPluginManager().registerEvents(listener, plugin);

                this.registeredListener.add(listener);
                this.logger.debug("%s event has been registered".formatted(listener.getClass().getSimpleName()));
            }
        }
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this.plugin);
        logger.debug("Unregistered all events");
    }

    public void reloadEvents() {
        this.unregisterEvents();
        this.registerEvents();
        logger.debug("Events has been reloaded");
    }

    private Map<Listener, Boolean> loadListeners() {
        return Map.ofEntries(
                Map.entry(new PlayerJoinEvent(plugin.getVersionManager(), plugin.getMessage(), plugin.getAudiences(), plugin.getProperties()),
                        this.pluginConfig.versionControl.notifyAdminsOnJoin)
        );
    }
}
