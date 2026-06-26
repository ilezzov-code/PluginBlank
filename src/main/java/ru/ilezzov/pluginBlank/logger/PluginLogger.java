package ru.ilezzov.pluginBlank.logger;

import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import ru.ilezzov.pluginBlank.Main;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;

import java.util.logging.Level;

public class PluginLogger {
    private final Plugin plugin;
    private final String prefix;
    private final String debugPrefix;
    private final Audience console;
    private final MiniMessage mm = MiniMessage.miniMessage();

    @Setter
    private boolean debug = false;

    public PluginLogger(final Main plugin, final String pluginName) {
        this.plugin = plugin;
        this.prefix = "<gold>" + pluginName + "</gold> <dark_gray>| ";
        this.debugPrefix = "<gold>" + pluginName + "</gold> <aqua>DEBUG</aqua> <dark_gray>| ";
        this.console = plugin.getAudiences().console();
    }

    private void send(String message) {
        final Component component = mm.deserialize(prefix + message);
        console.sendMessage(component);
    }

    public void info(String message) {
        send("<white>" + message + "</white>");
    }

    public void info(Component component) {
        this.console.sendMessage(component);
    }

    public void debug(String message) {
        if (debug) {
            final Component component = mm.deserialize(debugPrefix + "<white>" + message + "</white>");
            console.sendMessage(component);
        }
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
