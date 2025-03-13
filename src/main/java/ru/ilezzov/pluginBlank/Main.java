package ru.ilezzov.pluginBlank;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ilezzov.pluginBlank.commands.PluginCommand;
import ru.ilezzov.pluginBlank.events.PluginEvent;
import ru.ilezzov.pluginBlank.logging.Logger;
import ru.ilezzov.pluginBlank.logging.PaperLogger;
import ru.ilezzov.pluginBlank.utils.LegacySerialize;

import static org.bukkit.Bukkit.*;

public final class PluginBlank extends JavaPlugin {
    //Serializer message color
    public static final LegacySerialize legacySerialize = new LegacySerialize();

    //Plugin logger
    private final Logger logger = new PaperLogger();

    @Override
    public void onEnable() {
        logger.info("&aПлагин успешно запущен!");
    }

    @Override
    public void onDisable() {
        logger.info("&cПлагин успешно выключен!");
    }

    //Register your commands. Add a new command in plugin.yml to register her
    private void registerCommands() {
        getCommand("plugin-blank").setExecutor(new PluginCommand());

        //TODO: getCommand("your-command").setExecutor(new YourCommandClass());
    }

    //Register your events. Add a new event class to register her
    private void registerEvents() {
        getPluginManager().registerEvents(new PluginEvent(), this);

        //TODO: getPluginManager().registerEvents(new YourEventClass(), this)
    }
}
