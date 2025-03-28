package ru.ilezzov.pluginblank.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ilezzov.pluginblank.Main;
import ru.ilezzov.pluginblank.enums.Permission;
import ru.ilezzov.pluginblank.messages.ConsoleMessages;
import ru.ilezzov.pluginblank.messages.PluginMessages;
import ru.ilezzov.pluginblank.utils.ListUtils;
import ru.ilezzov.pluginblank.models.PluginPlaceholder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.ilezzov.pluginblank.utils.PermissionsChecker.hasPermission;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final PluginPlaceholder commandPlaceholders = new PluginPlaceholder();

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
        commandPlaceholders.addPlaceholder("{DEVELOPER}", ListUtils.listToString(Main.getPluginDevelopers()));
        commandPlaceholders.addPlaceholder("{CONTACT_LINK}", Main.getPluginContactLink());

        if(args.length == 0) {
            sender.sendMessage(PluginMessages.commandMainCommandMessage(commandPlaceholders));
            return true;
        }

        switch (args[0]) {
            case "reload" -> {
                if (hasPermission(sender, Permission.RELOAD)) {
                    Main.loadFiles();
                    Main.reloadPrefix();
                    Main.registerCommands();
                    Main.reloadEvents();

                    try {
                        Main.getDbConnect().connect();
                    } catch (SQLException e) {
                        Main.getPluginLogger().info(ConsoleMessages.errorOccurred("Couldn't close database connect: " + e.getMessage()));
                        throw new RuntimeException(e);
                    }

                    commandPlaceholders.addPlaceholder("{P}", Main.getPrefix());
                    sender.sendMessage(PluginMessages.pluginReloadMessage(commandPlaceholders));

                    return true;
                }

                sender.sendMessage(PluginMessages.pluginNoPermsMessage(commandPlaceholders));
                return true;
            }

            default -> sender.sendMessage(PluginMessages.commandMainCommandMessage(commandPlaceholders));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
        final List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            if(hasPermission(sender)) {
                completions.add("reload");
            }
        }

        return completions;
    }
}
