package ru.ilezzov.pluginBlank.utils;

import org.bukkit.command.CommandSender;

public class Permissions {
    //Plugin permission
    public final static String MAIN_PERMISSIONS = "plugin-blank.*";
    public final static String COMMAND_MAIN_COMMAND_RELOAD = "plugin-blank.reload";

    public static boolean hasPermission(final CommandSender sender, final String permission) {
        return sender.hasPermission(MAIN_PERMISSIONS) || sender.hasPermission(permission);
    }
}
