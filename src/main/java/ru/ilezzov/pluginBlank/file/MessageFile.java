package ru.ilezzov.pluginBlank.file;


import eu.okaeri.configs.OkaeriConfig;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import ru.ilezzov.pluginBlank.color.Colorizer;

public class MessageFile extends OkaeriConfig {
    @Comment({
            "The plugin supports all types of message formatting.",
            "LEGACY — Color via & / § and HEX via &#rrggbb / §#rrggbb or &x&r&r&g&g&b&b / §x§r§r§g§g§b§b",
            "LEGACY_ADVANCED — Color and HEX via &##rrggbb / §##rrggbb",
            "MINI_MESSAGE — Color via <color> More details — https://docs.advntr.dev/minimessage/index.html",
            "And all formats available at https://www.birdflop.com/resources/rgb/",
            "",
            "MINI_MESSAGE (RECOMMENDED) — color via <color> More details — https://docs.advntr.dev/minimessage/index.html",
            "ALL — support for all formats simultaneously. Slower, not all MINI_MESSAGE tags are supported"
    })
    public Colorizer colorizer = Colorizer.MINI_MESSAGE;

    @Comment("")
    public PluginSubConfig plugin = new PluginSubConfig();

    @Comment("")
    public VersionSubConfig version = new VersionSubConfig();

    @Comment("")
    @CustomKey("main-command")
    public MainCommandSubConfig mainCommand = new MainCommandSubConfig();

    public static class PluginSubConfig extends OkaeriConfig {
        @Comment("Plugin prefix for regular messages. Use {P} in messages")
        public String prefix = "<gold>PluginBlank</gold> <dark_gray>|</dark_gray>";

        @Comment("Plugin prefix for error messages. Use {P_E} in messages")
        @CustomKey("prefix-error")
        public String prefixError = "<red>PluginBlank</red> <dark_gray>|</dark_gray>";

        public String reload = "{P} <gray>The plugin has been successfully <green>reloaded</gray>";

        @CustomKey("no-console")
        public String noConsole = "{P_E} <red>This command can only be executed by players</red>";

        @CustomKey("no-perms")
        public String noPerms = "{P_E} <red>You do not have permission to use this command</red>";

        @CustomKey("command-disable")
        public String commandDisable = "{P_E} <red>This command has been disabled by the server administrator</red>";

        public String cooldown = "{P} <gray>Please wait another <yellow>{COOLDOWN}s</yellow> before using this command again";
    }

    public static class VersionSubConfig extends OkaeriConfig {
        public String latest = "{P} <gray>You are running the latest version of the plugin: <green>{CURRENT_VERSION}</green></gray>";
        public String supported = "{P} <gray>You are running a supported version of the plugin (<yellow>{CURRENT_VERSION}</yellow>), however we recommend updating to the latest version (<green>{LATEST_VERSION}</green>)";
        public String outdated = "{P} <gray>You are running an unsupported version of the plugin (<red>{CURRENT_VERSION}). <bold>{ACTION}</bold></red> Please immediately install the latest version (<green>{LATEST_VERSION}</green>)";
        public String blacklist = "{P} <gray>You are running a blacklisted version of the plugin (<red>{CURRENT_VERSION}). <bold>{ACTION}.</bold></red> Please immediately install the latest version (<green>{LATEST_VERSION}</green>)";
        public String download = "{P} Download here — <yellow><click:open_url:{DOWNLOAD_LINK}>{DOWNLOAD_LINK}</click></yellow>";
        public String error = "{P_E} <red>An error occurred while fetching the plugin version. Learn more in the console";
        public String loading = "{P} <gray>Loading the latest version of the plugin</gray>";

        public ActionSubConfig action = new ActionSubConfig();

        public static class ActionSubConfig extends OkaeriConfig {
            @CustomKey("impossible-launch")
            public String impossibleLaunch = "PLUGIN LAUNCH IS IMPOSSIBLE";

            @CustomKey("no-recommended")
            public String noRecommended = "WE DO NOT RECOMMEND USING THIS PATCH";

            @CustomKey("auto-stopping")
            public String autoStopping = "AUTOMATIC PLUGIN DISABLE";
        }
    }

    public static class MainCommandSubConfig extends OkaeriConfig {
        public String help = "{P} <gray>Plugin command help: \\n" +
                " <yellow>• <click:suggest_command:/$command reload>/$command reload</click></yellow> — reload the plugin\\n" +
                " <yellow>• <click:suggest_command:/$command version>/$command version</click></yellow> — get the latest plugin version\\n" +
                "Contact the developer — <blue><click:open_url:{CONTACT}>{CONTACT}</click></gray>";
    }
}