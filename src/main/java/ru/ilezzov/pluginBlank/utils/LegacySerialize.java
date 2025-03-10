package ru.ilezzov.pluginBlank.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.lang.module.ModuleFinder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacySerialize {
    private final Pattern HEX_PATTERN = Pattern.compile("§[0-9a-fk-orA-FK-OR]");
    private final HashMap<String, String> colors;

    public LegacySerialize() {
        this.colors = new HashMap<>();

        colors.put("§0", "#000000");
        colors.put("§1", "#0000AA");
        colors.put("§2", "#00AA00");
        colors.put("§3", "#00AAAA");
        colors.put("§4", "#AA0000");
        colors.put("§5", "#AA00AA");
        colors.put("§6", "#FFAA00");
        colors.put("§7", "#AAAAAA");
        colors.put("§8", "#555555");
        colors.put("§9", "#5555FF");
        colors.put("§a", "#55FF55");
        colors.put("§b", "#55FFFF");
        colors.put("§c", "#FF5555");
        colors.put("§d", "#FF55FF");
        colors.put("§e", "#FFFF55");
        colors.put("§f", "#FFFFFF");
        colors.put("§k", "obf");
        colors.put("§l", "b");
        colors.put("§m", "st");
        colors.put("§n", "u");
        colors.put("§o", "i");
        colors.put("§r", "reset");
    }

    public Component serialize(final String message) {
        return MiniMessage.miniMessage().deserialize(legacySerialize(message));
    }

    public String legacySerialize(final String message) {
        return replaceLegacyColor(translateAlternateCodeColor('&', message));
    }

    private String replaceLegacyColor(final String s) {
        final Matcher matcher = HEX_PATTERN.matcher(s);
        final StringBuilder result = new StringBuilder(s.length());
        int lastIndex = 0;

        while (matcher.find()) {
            result.append(s, lastIndex, matcher.start()).append("<").append(colors.get(matcher.group())).append(">");
            lastIndex = matcher.end();
        }

        result.append(s, lastIndex, s.length());
        return result.toString();
    }

    private String translateAlternateCodeColor(char code, String s) {
        char[] b = s.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == code && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }


}
