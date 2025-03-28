package ru.ilezzov.pluginblank.managers;

import java.util.HashMap;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;

public class CooldownManager {
    private final HashMap<UUID, Long> cooldowns;
    private final long cooldownTime;

    public CooldownManager(final long cooldownTime) {
        this.cooldowns = new HashMap<>();
        this.cooldownTime = cooldownTime * 1000;
    }

    public void newCooldown(final UUID uuid) {
        this.cooldowns.put(uuid, currentTimeMillis());
    }

    public long getCooldownTime(final UUID uuid) {
        if (!check(uuid))
            return 0;
        return this.cooldowns.get(uuid) + cooldownTime;
    }

    public boolean checkCooldown(final UUID uuid) {
        return getCooldownTime(uuid) < currentTimeMillis();
    }

    private boolean check(final UUID uuid) {
        return this.cooldowns.containsKey(uuid);
    }
}
