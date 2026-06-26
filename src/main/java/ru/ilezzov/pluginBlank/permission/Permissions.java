package ru.ilezzov.pluginBlank.permission;

public class Permissions {
    private static final String BASE_PERMISSION = "pluginblank.";

    public static final String ALL = create("*");
    public static final String RELOAD = create("reload");

    public static final String VERSION_NOTIFY = BASE_PERMISSION + "version.notify";
    public static final String VERSION_COMMAND = BASE_PERMISSION + "version.command";

    private static String create(final String key) {
        return BASE_PERMISSION.concat(key);
    }

}
