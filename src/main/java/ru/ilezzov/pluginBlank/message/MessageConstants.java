package ru.ilezzov.pluginBlank.message;

public final class MessageConstants {
    private MessageConstants() {}

    public static final String LATEST_VERSION = "You are using the latest version (<green>%s</green>)";
    public static final String SUPPORTED_VERSION = "You are using a supported version (<yellow>%s</yellow>), but we recommend installing the latest version (<green>%s</green>)";
    public static final String OUTDATED_VERSION = "You are using an outdated version (<red>%s</red>). <red><bold>PLUGIN LAUNCHING IS NOT POSSIBLE.</bold></red> You must install the minimum supported version of the plugin (<yellow>%s</yellow>)";
    public static final String BLACKLIST_VERSION = "You're using a banned version (<red>%s</red>). <red><bold>PLUGIN LAUNCHING IS NOT POSSIBLE.</bold></red> You need to install the latest version (<green>%s</green>)";
    public static final String DOWNLOAD_VERSION_LINK = "Download link — <yellow>%s";
    public static final String VERSION_CONTROL_COMMAND = "Version control command — <yellow>/%s version";
}
