<div align="center">
    <img src="img/logo/pluginblank.png">
</div>

<div align="center">
    <h1>Заготовка для вашего Paper плагина</h1>
</div>

## <img src="img/flags/ru.svg" width="15"> [Перейти на русский язык](readmes/README_RU.md)

## 💼 Project info
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
* File version manager

## Legacy Serialize
`LegacySerialize` — класс, который включает в себя один статический метод `serialize(String message)` С его помощью Вы можете с легкостью преобразовать любое сообщение в `Component`. Поддерживаемые виды форматирования:

* LEGACY — Цвет через & / § и HEX через &#rrggbb / §#rrggbb или &x&r&r&g&g&b&b / §x§r§r§g§g§b§b
* LEGACY_ADVANCED — Цвет и HEX через &##rrggbb / §##rrggbb
* MINI_MESSAGE — Цвет через <цвет> Подробнее — https://docs.advntr.dev/minimessage/index.html
* И все форматы доступные на https://www.birdflop.com/resources/rgb/
* Вы можете использовать все форматы одновременно в одном сообщении.

Использование:

```java
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import ru.ilezzov.pluginblank.utils.LegacySerialize;

public static void main(String[] args) {
    final String MESSAGE_TEMPLATE = "&6Hello! &#084CFBT&#1559FBh&#2166FBi&#2E73FBs &#478CFCi&#5499FCs &#6EB3FCa &#87CCFDt&#94D9FDe&#A0E6FDs&#ADF3FDt §x§F§B§0§8§4§4m§x§F§8§0§A§4§4e§x§F§4§0§D§4§5s§x§F§1§0§F§4§5s§x§E§D§1§1§4§5a§x§E§A§1§4§4§6g§x§E§6§1§6§4§6e <##FB0844>f<##F80A44>o<##F40D45>r <##ED1145>y<##EA1446>o<##E61646>u <#0854FB>I<#0C5AE9>t<#1060D6>'<#1466C4>s <#1D729F>w<#21788D>o<#257E7A>r<#298468>k";

    final Component component = LegacySerialize.serialize(MESSAGE_TEMPLATE); // <#FFAA00>Hello! <#084CFB>T<#1559FB>h<#2166FB>i<#2E73FB>s <#478CFC>i<#5499FC>s <#6EB3FC>a <#87CCFD>t<#94D9FD>e<#A0E6FD>s<#ADF3FD>t <#FB0844>m<#F80A44>e<#F40D45>s<#F10F45>s<#ED1145>a<#EA1446>g<#E61646>e <#FB0844>f<#F80A44>o<#F40D45>r <#ED1145>y<#EA1446>o<#E61646>u <#0854FB>I<#0C5AE9>t<#1060D6>'<#1466C4>s <#1D729F>w<#21788D>o<#257E7A>r<#298468>k
    Bukkit.broadcast(MESSAGE_TEMPLATE);
}
```
Результат:
<img src="img/screenshots/test_legacy_serialize.png">

## Permission System
В плагин встроена система, которая позволит вам проверять есть ли у игрока определенные права. Для это нужно использовать статический метод `PermissionsChecker.hasPermission(CommandSender sender, Permission permission)`

`Permission` — Enum класс, в котором прописаны все права вашего плагина

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

Использование:

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
В плагин встроена система удобной работы с вашими плейсхолдерами. Для начала вам необходимо создать объект класса `PluginPlaceholder`

```java
import ru.ilezzov.pluginblank.models.PluginPlaceholder;

PluginPlaceholder pluginPlaceholder = new PluginPlaceholder();
```

Теперь вам необходимо добавить плейсхолдеры, которые будут использоваться в следующем сообщении:

```java
pluginPlaceholder.addPlaceholder(String key, String value);
```

`key` — ваш плейсхолдер  
`value` — значение, которое будет вместо плейсхолдера

После этого Вы можете заменить ваши плейсхолдеры на значения с помощью статического метода `PlaceholderReplacer.replacePlaceholder(String message, PluginPlaceholder placeholders)`

Использование:

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

Результат:

<img src="img/screenshots/test_placeholder_replacer.png">

## Cooldown Manager
Вы с легкостью можете создавать задержки на использование каких-либо функций вашего плагина при помощи класса `CooldownManager`

Для начала необходимо создать объект класса:

```java
import ru.ilezzov.pluginblank.managers.CooldownManager;

CooldownManager cooldownManager = new CooldownManager(timeInSecond);
```

`timeInSecond` — время задержки

Чтобы добавить игроку задержку, нужно использовать метод `newCooldown(UUID playerUUID)`

```java
import org.bukkit.entity.Player;

Player player;
cooldownManager.newCooldown(player.getUniqueId());
```

Чтобы проверить прошла ли зажержка, используйте метод `checkCooldown(UUID playerUUID)`

```java
import org.bukkit.entity.Player;

Player player;
cooldownManager.checkCooldown(player.getUniqueId());
```

Чтобы получить время в секундах, которое необходимо еще подождать, используйте метод `getCooldownTime(UUID playerUUID)`

```java
import org.bukkit.entity.Player;

Player player;
cooldownManager.getCooldownTime(player.getUniqueId());
```

Пример использования:

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
В проект встроена проверка версии плагина с помощью запроса к текстовому документу. Для корректной работы измените ссылку на вашу в `plugin-settings.yml` 



