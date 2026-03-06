package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.objects.AdvancedSection;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractAbility extends AdvancedSection {

    private static final Map<String, Long> COOLDOWN_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> TIMES_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Long> LAST_ACCESS_MAP = new ConcurrentHashMap<>();

    private static final long CLEANUP_INTERVAL_MS = 30000L;
    private static final long STALE_ENTRY_MS = 30 * 60 * 1000L;
    private static volatile long lastCleanupAt = 0L;

    public AbstractAbility(String id, ConfigurationSection section) {
        super(id, section);
    }

    public abstract boolean execute(AbilityContext context);

    public abstract TargetEntityType getDefaultTargetEntityType();

    protected Entity getTargetEntity(AbilityContext context) {
        return switch (section.getString("target", getDefaultTargetEntityType().name()).toUpperCase()) {
            case "SOURCE" -> context.handler().sourceEntity;
            case "SKILL" -> context.handler().skillEntity;
            default -> context.handler().targetEntity;
        };
    }

    public boolean shouldExecute(AbilityContext context) {
        Entity source = context.handler().sourceEntity;
        if (source == null) {
            return true;
        }

        long now = System.currentTimeMillis();
        String cooldownKey = source.getUniqueId() + ":" + context.power().getId() + ":" + section.getCurrentPath();
        if (now - lastCleanupAt >= CLEANUP_INTERVAL_MS) {
            cleanup(now);
        }

        LAST_ACCESS_MAP.put(cooldownKey, now);

        int maxTimes = Math.max(0, getInt("times", getInt("limit.times", 0, context.level()), context.level()));
        if (maxTimes > 0 && TIMES_MAP.getOrDefault(cooldownKey, 0) >= maxTimes) {
            return false;
        }

        long nextTime = COOLDOWN_MAP.getOrDefault(cooldownKey, 0L);
        if (now < nextTime) {
            return false;
        }

        double randomChance = getDouble("random", getDouble("limit.random", 1.0, context.level()), context.level());
        if (randomChance < 1.0 && ThreadLocalRandom.current().nextDouble() > randomChance) {
            return false;
        }

        double cooldownSeconds = getDouble("cooldown", getDouble("limit.cooldown", 0.0, context.level()), context.level());
        if (cooldownSeconds > 0) {
            COOLDOWN_MAP.put(cooldownKey, now + (long) (cooldownSeconds * 1000));
        }

        if (maxTimes > 0) {
            TIMES_MAP.merge(cooldownKey, 1, Integer::sum);
        }

        return true;
    }

    private static void cleanup(long now) {
        lastCleanupAt = now;
        COOLDOWN_MAP.entrySet().removeIf(entry -> now >= entry.getValue());

        LAST_ACCESS_MAP.entrySet().removeIf(entry -> now - entry.getValue() >= STALE_ENTRY_MS);
        TIMES_MAP.keySet().removeIf(key -> !LAST_ACCESS_MAP.containsKey(key));
    }

    protected Location getLocation(AbilityContext context) {
        Location base = context.handler().location;
        if (base == null) {
            return null;
        }
        Location modified = base.clone();
        double offsetX = getDouble("location.offset-x", 0, context.level());
        double offsetY = getDouble("location.offset-y", 0, context.level());
        double offsetZ = getDouble("location.offset-z", 0, context.level());
        modified.add(offsetX, offsetY, offsetZ);
        return modified;
    }
}
