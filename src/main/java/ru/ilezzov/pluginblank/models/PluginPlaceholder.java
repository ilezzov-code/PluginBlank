package ru.ilezzov.pluginblank.models;

import lombok.Getter;
import ru.ilezzov.pluginblank.Main;

import java.util.HashMap;

@Getter
public class PluginPlaceholder {
    private final HashMap<String, String> placeholders;

    public PluginPlaceholder() {
        this.placeholders = new HashMap<>();
        this.placeholders.put("{P}", Main.getPrefix());
    }

    public void addPlaceholder(final String placeholder, final String value) {
        this.placeholders.put(placeholder, value);
    }

}
