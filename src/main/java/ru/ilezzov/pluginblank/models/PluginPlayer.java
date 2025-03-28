package ru.ilezzov.pluginblank.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PluginPlayer {
    private final UUID uniqueId;
}
