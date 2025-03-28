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
