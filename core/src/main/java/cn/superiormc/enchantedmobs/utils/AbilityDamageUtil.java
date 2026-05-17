package cn.superiormc.enchantedmobs.utils;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

public class AbilityDamageUtil {

    private static final NamespacedKey DAMAGE = new NamespacedKey(EnchantedMobs.instance, "ability_damage");

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
}
