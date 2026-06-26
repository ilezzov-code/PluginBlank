package ru.ilezzov.pluginBlank.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;

public enum Colorizer {
    MINI_MESSAGE {
        @Override
        public Component parse(final String text, final PluginPlaceholder placeholder) {

            return MiniMessage.miniMessage().deserialize(
                    PluginPlaceholder.replacePlaceholder(text, placeholder)
            );
        }
    },
    ALL {
        @Override
        public Component parse(final String text, final PluginPlaceholder placeholder) {
            return LegacySerialize.serialize(
                    PluginPlaceholder.replacePlaceholder(text, placeholder)
            );
        }
    }
    ;
    public abstract Component parse(final String text, final PluginPlaceholder placeholder);
}
