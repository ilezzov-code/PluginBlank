package ru.ilezzov.pluginblank.placeholder;

import lombok.Getter;
import ru.ilezzov.pluginblank.Main;

import java.util.HashMap;

@Getter
public class PluginPlaceholder {
    private final HashMap<String, Object> placeholders;

    public PluginPlaceholder() {
        this.placeholders = new HashMap<>();
        this.placeholders.put("{P}", Main.getPrefix());
    }

    public void addPlaceholder(final String placeholder, final Object value) {
        this.placeholders.put(placeholder, value);
    }
}
