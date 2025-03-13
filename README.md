<div align="center">
    <img src="img/logo/pluginblank.png">
</div>

-----
<div align="center">
    <h1>Blank for your Paper Minecraft</h1>
</div>

<div align="center">
    <a href="https://t.me/ilezovofficial">
    <img src="img/socials/contact_with_me.png" width="200"> 
    </a>
</div>
<div align="center">
    <a href="https://t.me/ilezzov">
    <img src="img/socials/tg_channel.png" width="200"> 
    </a>
</div>

### Select language:
* <img src="img/flags/ru.svg" width="15"> Русский
* <img src="img/flags/en.svg" width="15"> English

### Donation: 
* DA: https://www.donationalerts.com/r/ilezzov_dev
* Gift in Telegram: https://t.me/ilezovofficial

## 🔥 Features:

* File System
* Version Checker
* Legacy formating
* Multilingualism
* Placeholder System

## Get started
Сlone my project using Git or download it from my GitHub

## File System

The `File System` makes it easier to work with a configuration file (.yml). Now you can easily create an unlimited number of files, as well as edit them without restarting the server.

### Create a file
First, you need to create a file in the `resources` folder and enter all the necessary values. After that, you can go to create:
```java
final PluginFile file = FileManager.new(fileName, filePath);
```

`fileName` is the name of the file in the `resources` folder.

`filePath` is the folder where the file will be created. If you specify "", the file will be created in the root folder of your plugin.


The `new` method will create a file and copy its contents from the `resources` folder

#### Пример:

```java
final PluginFile file = new FileManager("config.yml", "");
```

The file `config.yml` will be created in your plugin folder: `plugins/yourPlugin/config.yml`

```java
final PluginFile file = new FileManager("ru-RU.yml", "languages/ru");
```

The file `ru-RU.yml` will be created in your plugin folder: `plugins/yourPlugin/languages/ru/ru-RU.yml

### Перезагрузка файла
If the file has been modified, you can upload new content to use it:

```java
file.reload();
```

### Getting values from a file by key
You can use one of the six methods to get values from files by their key:

```java
final PluginFile file = FileManager.new("config.yml", "");

file.getString(key); //Get String
file.getInt(key); //Get Int
file.getDouble(key); //Get Double
file.getObject(key); //Get Object
file.getList(key); //Get List
```

#### Example:
config.yml
```yml
prefix: "Plugin Prefix"
messages:
  join-message: "Welcome"
```

```java
final PluginFile config = FileManager.new("config.yml", "");
final String prefix = config.getString("prefix");
final String joinMessage = config.getString("messages.join-message");

out.println(prefix); //Plugin Prefix
out.println(joinMessage); //Welcome
```

## Version Checker
The plugin has a built-in update verification system. If your plugin is getting a new update, the user will receive a corresponding message.

In order for this to work, you need to follow several steps::

1) Create a repository of your project on GitHub or a public file on the Internet
2) Add the latest version of your plugin to this file.
3) Copy the link to this file
4) Change the value of the `urlToFileVersion` variable in the `Main` class

Now, every time you run the plugin, it will compare the current version of the plugin with the one you specified in the link file. If the versions are different, a corresponding message will be displayed.

## Legacy color formating
The plugin supports all types of message formatting.

`LEGACY` — Color through **& / §** and HEX through**&#rrggbb / §#rrggbb** or **&x&r&r&g&g&b&b / §x§r§r§g§g§b§b**

`LEGACY_ADVANCED` — Color and HEX via **&##rrggbb / §##rrggbb**

`MINI_MESSAGE` — Color via **<color>** Learn more — https://docs.advntr.dev/minimessage/index.html

And all the formats available on https://www.birdflop.com/resources/rgb/

You can use all formats simultaneously in one message. The plugin supports this

### Usage
Create an object of the class`LegacySerialize`

```java
final LegacySerialize legacySerialize = new LegacySerialize();
```

Get the `Component` of your message using the `serialize` method

```java
final LegacySerialize legacySerialize = new LegacySerialize();
final Component component = legacySerialize.serialize(message);
```

Use the resulting component for its intended purpose

## Multilingualism
The plugin has built-in multilingualism. You can easily create translations of your plugin.

## Placeholder System
The plugin has a built-in Placeholders system, with which you can easily change your placeholders to the desired values.

### Usage
Create or receive a placeholder string:

```java
final String message = "{P}, welcome to the server";
```

Create `HashMap` with your placeholder:

```java
final HashMap<String, String> placeholders = new HashMap<>();
placeholders.put("{P}", player.getName());
```

Convert your string using the static method `Placeholder.replacePlaceholder()`

```java
final String newMessage = Placeholder.replacePlaceholder(message, placeholders); //Player name, welcome to the server
```




