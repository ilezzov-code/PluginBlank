package ru.ilezzov.pluginBlank.command.executors;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.ilezzov.pluginBlank.Main;
import ru.ilezzov.pluginBlank.file.PluginMessage;
import ru.ilezzov.pluginBlank.message.game.MessageManager;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;

public class MainCommand implements CommandExecutor {
    private final MessageManager messageManager;
    private final PluginMessage message;
    private final String website;

    public MainCommand(final Main plugin) {
        this.messageManager = plugin.getMessageManager();
        this.message = plugin.getMessage();
        this.website = plugin.getProperties().website();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.message.plugin.prefix, this.message.plugin.prefixError
        );

        placeholder.addPlaceholder("{CONTACT}", website);

        if (args.length == 0) {
            this.messageManager.sendMessageFromThread(
                    sender, this.message.mainCommand.help, placeholder
            );
            return true;
        }
        return true;
    }
}
