package ru.ilezzov.pluginBlank.logging;

import org.bukkit.command.ConsoleCommandSender;
import ru.ilezzov.pluginBlank.PluginBlank;

import static org.bukkit.Bukkit.*;
import static ru.ilezzov.pluginBlank.PluginBlank.legacySerialize;

public class PaperLogger implements Logger {
    private final ConsoleCommandSender consoleCommandSender;

    public PaperLogger() {
        this.consoleCommandSender = getConsoleSender();
    }

    @Override
    public void info(final String message) {
        consoleCommandSender.sendMessage(legacySerialize.serialize(message));
    }

    @Override
    public void error(final String message) {

    }
}
