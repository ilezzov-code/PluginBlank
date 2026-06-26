package ru.ilezzov.pluginBlank.properties;

public record PluginProperties (
    String currentVersion,
    String versionFileUrl,
    int bstats,
    String mainCommand,
    String website
) {}
