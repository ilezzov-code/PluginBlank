Here’s the translated version in English:

---

<div align="center">
    <img src="img/logo/pluginblank.png">
</div>

<div align="center">
    <h1>Template for Your Paper Plugin</h1>
</div>

## <img src="img/flags/gb.svg" width="15"> [Перейти на русский](https://github.com/ilezzov-code/PluginBlank/blob/main/language_readme/RU_README.md)

## 💼 Project Info
* Java 17
* Paper 1.18.2
* Lombok 1.18.30
* H2 Database 2.3.232

## 🔥 Features
* Legacy Serialize
* Permissions System
* Placeholder System
* H2 Database
* Cooldown Manager
* Version Manager
* File Version Manager

## Legacy Serialize
`LegacySerialize` is a class that includes a single static method `serialize(String message)`, allowing you to easily convert any message into a `Component`. Supported formatting types:

* LEGACY — Colors via & / § and HEX via &#rrggbb / §#rrggbb or &x&r&r&g&g&b&b / §x§r§r§g§g§b§b
* LEGACY_ADVANCED — Colors and HEX via &##rrggbb / §##rrggbb
* MINI_MESSAGE — Colors via `<color>` More details: https://docs.advntr.dev/minimessage/index.html
* All formats available at https://www.birdflop.com/resources/rgb/
* You can use all formats simultaneously in one message.

Usage:

```java
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import ru.ilezzov.pluginblank.utils.LegacySerialize;

public static void main(String[] args) {
    final String MESSAGE_TEMPLATE = "&6Hello! &#084CFBT&#1559FBh&#2166FBi&#2E73FBs &#478CFCi&#5499FCs &#6EB3FCa &#87CCFDt&#94D9FDe&#A0E6FDs&#ADF3FDt §x§F§B§0§8§4§4m§x§F§8§0§A§4§4e§x§F§4§0§D§4§5s§x§F§1§0§F§4§5s§x§E§D§1§1§4§5a§x§E§A§1§4§4§6g§x§E§6§1§6§4§6e <##FB0844>f<##F80A44>o<##F40D45>r <##ED1145>y<##EA1446>o<##E61646>u <#0854FB>I<#0C5AE9>t<#1060D6>'<#1466C4>s <#1D729F>w<#21788D>o<#257E7A>r<#298468>k";

    final Component component = LegacySerialize.serialize(MESSAGE_TEMPLATE);
    Bukkit.broadcast(MESSAGE_TEMPLATE);
}
```

Result:
<img src="img/screenshots/test_legacy_serialize.png">

## Permission System
The plugin includes a system that allows you to check if a player has certain permissions. To do this, use the static method `PermissionsChecker.hasPermission(CommandSender sender, Permission permission)`

`Permission` is an Enum class that defines all permissions for your plugin:

```java
package ru.ilezzov.pluginblank.enums;

public enum Permission {
    MAIN("cool-lobby.*"),
    NO_COOLDOWN("cool-lobby.no_cooldown"),
    RELOAD("cool-lobby.reload");

    private final String permission;

    Permission(final String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
```

Usage:

```java
import ru.ilezzov.pluginblank.enums.Permission;
import ru.ilezzov.pluginblank.utils.PermissionsChecker;

@Override
public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
    if (PermissionsChecker.hasPermission(sender, Permission.RELOAD)) {
        // TODO: reload plugin
        return true;
    }
    return false;
}
```

## Placeholder System
The plugin includes a convenient system for handling placeholders. First, create an instance of the `PluginPlaceholder` class:

```java
import ru.ilezzov.pluginblank.models.PluginPlaceholder;

PluginPlaceholder pluginPlaceholder = new PluginPlaceholder();
```

Now, add placeholders that will be used in the message:

```java
pluginPlaceholder.addPlaceholder(String key, String value);
```

`key` — your placeholder  
`value` — the value that will replace the placeholder

After that, you can replace placeholders in messages using the static method `PlaceholderReplacer.replacePlaceholder(String message, PluginPlaceholder placeholders)`

Usage:

```java
import org.bukkit.entity.Player;
import ru.ilezzov.pluginblank.models.PluginPlaceholder;
import ru.ilezzov.pluginblank.utils.PlaceholderReplacer;

public static void main(String[] args) {
    final String MESSAGE_TEMPLATE = "Hello, {NAME}! Time in miles: {TIME}";
    final Player player; // Nick = ILeZzoV
    final PluginPlaceholder pluginPlaceholder = new PluginPlaceholder();

    pluginPlaceholder.addPlaceholder("{NAME}", player.getName());
    pluginPlaceholder.addPlaceholder("{TIME}", System.currentTimeMillis());

    player.sendMessage(PlaceholderReplacer.replacePlaceholder(MESSAGE_TEMPLATE, pluginPlaceholder));
}
```

Result:

<img src="img/screenshots/test_placeholder_replacer.png">

## Cooldown Manager
You can easily create cooldowns for your plugin’s functions using the `CooldownManager` class.

First, create an instance:

```java
import ru.ilezzov.pluginblank.managers.CooldownManager;

CooldownManager cooldownManager = new CooldownManager(timeInSeconds);
```

`timeInSeconds` — the cooldown time

To add a cooldown for a player, use the method `newCooldown(UUID playerUUID)`

```java
import org.bukkit.entity.Player;

Player player;
cooldownManager.newCooldown(player.getUniqueId());
```

To check if the cooldown has expired, use the method `checkCooldown(UUID playerUUID)`

```java
import org.bukkit.entity.Player;

Player player;
cooldownManager.checkCooldown(player.getUniqueId());
```

To get the remaining cooldown time in seconds, use `getCooldownTime(UUID playerUUID)`

```java
import org.bukkit.entity.Player;

Player player;
cooldownManager.getCooldownTime(player.getUniqueId());
```

Example usage:

```java
import org.bukkit.entity.Player;
import ru.ilezzov.pluginblank.managers.CooldownManager;

private final CooldownManager cooldownManager = new CooldownManager(10);

@Override
public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String @NotNull [] args) {
    final Player player = (Player) sender;
    
    if (!cooldownManager.checkCooldown(player.getUniqueId())) {
        return false;
    }
    
    cooldownManager.newCooldown(player.getUniqueId());
    return true;
}
```

## Version Manager
The project includes a plugin version check using a request to a text document. To configure it properly, change the link in `plugin-settings.yml`.

## Support the Developer:
* DA: https://www.donationalerts.com/r/ilezzov_dev
* YooMoney: https://yoomoney.ru/fundraise/193CD8F13OH.250319
* Telegram Gift: https://t.me/ilezovofficial
* TON: UQCInXoHOJAlMpZ-8GIHqv1k0dg2E4pglKAIxOf3ia5xHmKV
* BTC: 1KCM1QN9TNYRevvQD63UF81oBRSK67vCon
* Card: 5536914188326494

---

Let me know if you need any refinements! 🚀