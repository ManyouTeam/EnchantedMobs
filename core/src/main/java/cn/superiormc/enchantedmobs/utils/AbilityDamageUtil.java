package cn.superiormc.enchantedmobs.utils;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AbilityDamageUtil {

    private static final NamespacedKey DAMAGE = new NamespacedKey(EnchantedMobs.instance, "ability_damage");
    private static final NamespacedKey INVULNERABLE_UNTIL = new NamespacedKey(EnchantedMobs.instance, "ability_invulnerable_until");

    private AbilityDamageUtil() {
    }

    public static void markDamage(Entity entity, double damage) {
        if (entity == null || damage < 0.0D) {
            return;
        }
        entity.getPersistentDataContainer().set(DAMAGE, PersistentDataType.DOUBLE, damage);
    }

    public static void applyMarkedDamage(EntityDamageByEntityEvent event) {
        PersistentDataContainer pdc = event.getDamager().getPersistentDataContainer();
        Double damage = pdc.get(DAMAGE, PersistentDataType.DOUBLE);
        if (damage == null) {
            return;
        }
        event.setDamage(Math.max(0.0D, damage));
    }

    public static void setInvulnerable(Entity entity, boolean value, long durationTicks) {
        if (entity == null) {
            return;
        }
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (!value) {
            pdc.remove(INVULNERABLE_UNTIL);
            return;
        }
        if (durationTicks <= 0L) {
            pdc.set(INVULNERABLE_UNTIL, PersistentDataType.LONG, Long.MAX_VALUE);
            return;
        }
        long expiresAt = System.currentTimeMillis() + durationTicks * 50L;
        pdc.set(INVULNERABLE_UNTIL, PersistentDataType.LONG, expiresAt);
        SchedulerUtil.runTaskLater(entity, () -> clearInvulnerableIfExpired(entity, expiresAt), durationTicks);
    }

    public static boolean isInvulnerable(Entity entity) {
        if (entity == null) {
            return false;
        }
        Long expiresAt = entity.getPersistentDataContainer().get(INVULNERABLE_UNTIL, PersistentDataType.LONG);
        if (expiresAt == null) {
            return entity.isInvulnerable();
        }
        if (expiresAt == Long.MAX_VALUE || expiresAt > System.currentTimeMillis()) {
            return true;
        }
        clearInvulnerableIfExpired(entity, expiresAt);
        return entity.isInvulnerable();
    }

    private static void clearInvulnerableIfExpired(Entity entity, long expectedExpiresAt) {
        if (entity == null || !entity.isValid()) {
            return;
        }
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        Long currentExpiresAt = pdc.get(INVULNERABLE_UNTIL, PersistentDataType.LONG);
        if (currentExpiresAt == null || currentExpiresAt > expectedExpiresAt) {
            return;
        }
        pdc.remove(INVULNERABLE_UNTIL);
    }
}
