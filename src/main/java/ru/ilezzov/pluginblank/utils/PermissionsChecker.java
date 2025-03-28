package ru.ilezzov.pluginblank.utils;

import org.bukkit.command.CommandSender;
import ru.ilezzov.pluginblank.enums.Permission;

public class PermissionsChecker {
    public static boolean hasPermission(final CommandSender sender, final Permission permission) {
        return sender.hasPermission(Permission.MAIN.getPermission()) || sender.hasPermission(permission.getPermission());
    }

    public static boolean hasPermission(final CommandSender sender) {
        return sender.hasPermission(Permission.MAIN.getPermission());
    }
}
