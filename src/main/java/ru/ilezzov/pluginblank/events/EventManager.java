package ru.ilezzov.pluginblank.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginblank.Main;
import ru.ilezzov.pluginblank.events.listeners.*;
import ru.ilezzov.pluginblank.logging.Logger;

import java.util.HashMap;
import java.util.Map;

import static ru.ilezzov.pluginblank.messages.ConsoleMessages.*;

public class EventManager {
    private final Logger logger = Main.getPluginLogger();
    private final JavaPlugin plugin;
    private Map<Class<? extends Listener>, Boolean> listenerClasses;
    private Map<String, Object> events;

    public EventManager(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.events = new HashMap<>();

        listenerClasses = loadListenerClasses();
    }

    public Object get(final String eventName) {
        return events.get(eventName);
    }

    public void loadEvents() {
        final Map<String, Object> events = new HashMap<>();

        for (Class<? extends Listener> listenerClass : listenerClasses.keySet()) {
            try {
                if (listenerClasses.get(listenerClass)) {
                    final Listener listener = listenerClass.getDeclaredConstructor().newInstance();
                    Bukkit.getPluginManager().registerEvents(listener, plugin);
                    events.put(listenerClass.getSimpleName(), listener);
                    logger.info(eventLoaded(listenerClass.getSimpleName()));
                }
            } catch (Exception e) {
                logger.info(errorOccurred(String.format("Couldn't load event %s", listenerClass.getSimpleName())));
            }
        }

        this.events = events;
    }

    public void reloadEvents() {
        HandlerList.unregisterAll(plugin);
        listenerClasses = loadListenerClasses();
        loadEvents();
    }

    private Map<Class<? extends Listener>, Boolean> loadListenerClasses() {
        final FileConfiguration config = Main.getConfigFile().getConfig();
        final ConfigurationSection lobbySettings = config.getConfigurationSection("lobby_settings");

        return Map.ofEntries(
                Map.entry(VersionCheckEvent.class, true)
        );
    }
}
