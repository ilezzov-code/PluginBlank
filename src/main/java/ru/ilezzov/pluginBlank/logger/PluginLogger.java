package ru.ilezzov.pluginBlank.logger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class PluginLogger {
    private final Plugin plugin;
    private final String prefix;
    private final ConsoleCommandSender console;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PluginLogger(final Plugin plugin, final String pluginName) {
        this.plugin = plugin;
        this.prefix = "<gold>" + pluginName + "</gold> <dark_gray>| ";
        this.console = Bukkit.getConsoleSender();
    }

    private void send(String message) {
        final Component component = mm.deserialize(prefix + message);
        console.sendMessage(component);
    }

    public void info(String message) {
        send("<white>" + message + "</white>");
    }

    public void success(String message) {
        send("<green>" + message + "</green>");
    }

    public void warn(String message) {
        send("<yellow>" + message + "</yellow>");
    }

    public void error(String message) {
        send("<red>" + message + "</red>");
    }

    public void error(String message, Exception e) {
        error(message);

        if (e != null) {
            plugin.getLogger().log(Level.SEVERE, "Technical details: ",  e);
        }
    }
}
