package ru.ilezzov.pluginBlank.placeholder;

import lombok.Getter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class PluginPlaceholder {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)}");

    private final HashMap<String, Object> placeholders;

    public PluginPlaceholder(final String prefix, final String prefixError) {
        this.placeholders = new HashMap<>();
        this.placeholders.put("{P}", prefix);
        this.placeholders.put("{P_E}", prefixError);
    }

    public void addPlaceholder(final String placeholder, final Object value) {
        this.placeholders.put(placeholder, value);
    }

    public static String replacePlaceholder(final String message, final PluginPlaceholder placeholders) {
        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        final StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            final String placeholderKey = matcher.group();
            final String value = placeholders.getPlaceholders().getOrDefault(placeholderKey, placeholderKey).toString();
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}