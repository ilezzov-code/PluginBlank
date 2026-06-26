package ru.ilezzov.pluginBlank.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import ru.ilezzov.pluginBlank.Main;
import ru.ilezzov.pluginBlank.command.executors.MainCommand;
import ru.ilezzov.pluginBlank.logger.PluginLogger;
import ru.ilezzov.pluginBlank.properties.PluginProperties;

import java.util.Map;

public class CommandManager {
    private final Main plugin;
    private final PluginProperties properties;
    private final PluginLogger logger;

    public CommandManager(final Main plugin) {
        this.plugin = plugin;
        this.properties = plugin.getProperties();
        this.logger = plugin.getPluginLogger();
    }

    public void loadCommands() {
        final Map<String, CommandExecutor> commands = getCommands();

        for (final String commandName : commands.keySet()) {
            final PluginCommand command = this.plugin.getCommand(commandName);

            if (command != null) {
                final CommandExecutor commandExecutor = commands.get(commandName);
                command.setExecutor(commandExecutor);

                if (commandExecutor instanceof TabCompleter completer) {
                    command.setTabCompleter(completer);
                }

                logger.debug("The command %s has been loaded".formatted(commandName));
            } else {
                logger.debug("The command %s was not found in plugin.yml".formatted(commandName));
            }
        }
    }

    private Map<String, CommandExecutor> getCommands() {
        return Map.ofEntries(
                Map.entry(this.properties.mainCommand(), new MainCommand(this.plugin))
        );
    }
}
