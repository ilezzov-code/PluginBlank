package ru.ilezzov.pluginBlank.message.game;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.ilezzov.pluginBlank.file.MessageFile;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;

@RequiredArgsConstructor
public class MessageManager {
    private final Plugin plugin;
    private final BukkitAudiences audiences;
    private final MessageFile message;

    public void sendMessageFromThread(final CommandSender sender, final String text, final PluginPlaceholder placeholder) {
        Bukkit.getScheduler().runTask(this.plugin, () -> sendMessage(sender, text, placeholder));
    }

    public void sendMessage(final CommandSender sender, final String text, final PluginPlaceholder placeholder) {
        this.audiences.sender(sender).sendMessage(
                this.parseComponent(text, placeholder)
        );
    }

    private Component parseComponent(final String text, final PluginPlaceholder placeholder) {
        return this.message.colorizer.parse(text, placeholder);
    }
}
