package ru.ilezzov.pluginBlank.permission;

import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class PermissionManager {
    public static boolean hasPermission(final CommandSender sender) {
        return sender.hasPermission(Permissions.ALL);
    }

    public static boolean hasPermission(final CommandSender sender, final String... permissions) {
        if (hasPermission(sender)) {
            return true;
        }
        return Arrays.stream(permissions).anyMatch(sender::hasPermission);
    }
}
