package ru.ilezzov.pluginBlank.command.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import ru.ilezzov.pluginBlank.Main;
import ru.ilezzov.pluginBlank.file.PluginMessage;
import ru.ilezzov.pluginBlank.message.game.MessageManager;
import ru.ilezzov.pluginBlank.permission.PermissionManager;
import ru.ilezzov.pluginBlank.permission.Permissions;
import ru.ilezzov.pluginBlank.placeholder.PluginPlaceholder;
import ru.ilezzov.pluginBlank.version.VersionData;
import ru.ilezzov.pluginBlank.version.VersionManager;
import ru.ilezzov.pluginBlank.version.VersionType;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MainCommand implements CommandExecutor, TabExecutor {
    private final Main plugin;
    private final VersionManager versionManager;
    private final MessageManager messageManager;
    private final String website;
    private final String currentVersion;

    private static final Map<String, String> ROOT_COMMANDS = Map.of(
            "reload", Permissions.RELOAD,
            "version", Permissions.VERSION_COMMAND
    );

    public MainCommand(final Main plugin) {
        this.plugin = plugin;

        this.versionManager = plugin.getVersionManager();
        this.messageManager = plugin.getMessageManager();
        this.website = plugin.getProperties().website();
        this.currentVersion = plugin.getProperties().currentVersion();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );

        placeholder.addPlaceholder("{CONTACT}", website);

        if (args.length == 0) {
            handleHelp(sender, placeholder);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "version" -> handleVersion(sender);
            case "reload" -> handleReload(sender, placeholder);
            default -> handleHelp(sender, placeholder);
        };

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            final String input = args[0];
            for (Map.Entry<String, String> entry : ROOT_COMMANDS.entrySet()) {
                final String subCommand = entry.getKey();
                final String permission = entry.getValue();

                if (PermissionManager.hasPermission(sender, permission) && StringUtil.startsWithIgnoreCase(subCommand, input)) {
                    completions.add(subCommand);
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private void handleHelp(final CommandSender sender, final PluginPlaceholder placeholder) {
        this.messageManager.sendMessageFromThread(
                sender, this.message().mainCommand.help, placeholder
        );
    }

    private void handleVersion(@NotNull CommandSender sender) {
        final PluginPlaceholder placeholder = new PluginPlaceholder(
                this.message().plugin.prefix, this.message().plugin.prefixError
        );

        if (!PermissionManager.hasPermission(sender, Permissions.VERSION_COMMAND)) {
            this.messageManager.sendMessage(
                    sender, this.message().plugin.noPerms, placeholder
            );
            return;
        }

        this.messageManager.sendMessage(
                sender, this.message().version.loading,placeholder
        );

        CompletableFuture.runAsync(this.versionManager::loadVersionData).thenRun(() -> {
            final VersionData versionData = this.versionManager.getVersionData();

            if (versionData == null) {
                this.messageManager.sendMessageFromThread(
                       sender, this.message().version.error, placeholder
                );
                return;
            }

            placeholder.addPlaceholder("{CURRENT_VERSION}", this.currentVersion);
            placeholder.addPlaceholder("{LATEST_VERSION}", versionData.getLatest().getVersion());
            placeholder.addPlaceholder("{ACTION}", this.message().version.action.noRecommended);

            if (this.versionManager.getVersionType() == VersionType.LATEST) {
                this.messageManager.sendMessageFromThread(
                        sender, this.message().version.latest, placeholder
                );
                return;
            }

            switch (versionManager.getVersionType()) {
                case SUPPORTED -> this.messageManager.sendMessageFromThread(
                        sender, this.message().version.supported, placeholder
                );
                case BLACKLIST -> this.messageManager.sendMessageFromThread(
                        sender, this.message().version.blacklist, placeholder
                );
                case OUTDATED -> this.messageManager.sendMessageFromThread(
                        sender, this.message().version.outdated, placeholder
                );
            }

            this.messageManager.sendMessageFromThread(
                    sender, this.message().version.download, placeholder
            );
        });
    }

    private void handleReload(final CommandSender sender, final PluginPlaceholder placeholder) {
        if (!PermissionManager.hasPermission(sender, Permissions.RELOAD)) {
            this.messageManager.sendMessage(
                    sender, this.message().plugin.noPerms, placeholder
            );
            return;
        }

        final String oldMessageFile = this.plugin.getPluginConfig().language;

        this.plugin.getPluginConfig().load();

        final String messageFile = this.plugin.getPluginConfig().language;
        this.plugin.reloadMessageFile(oldMessageFile, messageFile);

        this.plugin.getEventManager().reloadEvents();

        this.plugin.getVersionControl().stop();
        this.plugin.getVersionControl().startBackgroundCheckTask();
        this.plugin.getVersionControl().startCriticalNotifyTask();

        this.messageManager.sendMessage(
                sender, this.message().plugin.reload, placeholder
        );

    }

    private PluginMessage message() {
        return this.plugin.getMessage();
    }
}
