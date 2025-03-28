package ru.ilezzov.pluginblank.messages;

import net.kyori.adventure.text.Component;
import ru.ilezzov.pluginblank.Main;
import ru.ilezzov.pluginblank.models.PluginPlaceholder;
import ru.ilezzov.pluginblank.utils.LegacySerialize;
import ru.ilezzov.pluginblank.utils.PlaceholderReplacer;

import java.util.HashMap;

public class PluginMessages {
    public static Component pluginOutdatedVersionMessage(final PluginPlaceholder placeholders) {
        return getComponent("Plugin.plugin-use-outdated-version", placeholders);
    }

    public static Component pluginReloadMessage(final PluginPlaceholder placeholders) {
        return getComponent("Plugin.plugin-reload", placeholders);
    }

    public static Component pluginNoPermsMessage(final PluginPlaceholder placeholders) {
        return getComponent("Messages.command-permission-error", placeholders);
    }

    public static Component commandMainCommandMessage(final PluginPlaceholder placeholders) {
        return getComponent("Messages.command-main-message", placeholders);
    }

    private static Component getComponent(final String key) {
        final String message = Main.getMessagesFile().getConfig().getString(key);

        return LegacySerialize.serialize(message);
    }

    private static Component getComponent(final String key, final PluginPlaceholder placeholders) {
        String message = Main.getMessagesFile().getString(key);
        message = replacePlaceholder(message, placeholders);

        return LegacySerialize.serialize(message);
    }

    private static String replacePlaceholder(final String message, final PluginPlaceholder placeholders) {
        return PlaceholderReplacer.replacePlaceholder(message, placeholders);
    }
}
