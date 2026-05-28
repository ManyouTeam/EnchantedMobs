package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.objects.power.events.ShootBowHandler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class HomingProjectileAbility extends AbstractAbility {

    private static final NamespacedKey HOMING_TICKS = new NamespacedKey(EnchantedMobs.instance, "homing_ticks");

    public HomingProjectileAbility(ConfigurationSection section) {
        super("HomingProjectile", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        if (!(context.handler() instanceof ShootBowHandler handler)) {
            return false;
        }
        Projectile projectile = handler.projectile;
        if (projectile == null || !projectile.isValid()) {
            return false;
        }

        if (isExpired(projectile, context.level())) {
            if (getBoolean("remove-on-expire", true)) {
                projectile.remove();
            }
            return true;
        }

        double radius = getDouble("radius", 16, context.level());
        double strength = Math.max(0.01, getDouble("strength", 0.2, context.level()));
        Entity shooter = handler.shooter;
        Player target = findNearestPlayer(projectile.getLocation(), shooter, radius);
        if (target == null) {
            return false;
        }

        Location eye = target.getEyeLocation();
        Vector desired = eye.toVector().subtract(projectile.getLocation().toVector());
        if (desired.lengthSquared() < 1.0E-4) {
            return false;
        }

        Vector current = projectile.getVelocity();
        double speed = Math.max(0.4, current.length());
        Vector newVelocity = current.multiply(1 - strength).add(desired.normalize().multiply(speed * strength));
        projectile.setVelocity(newVelocity);
        return false;
    }

    private boolean isExpired(Projectile projectile, int level) {
        int maxTicks = getInt("max-ticks", 100, level);
        if (maxTicks <= 0) {
            return false;
        }

        PersistentDataContainer pdc = projectile.getPersistentDataContainer();
        int ticks = pdc.getOrDefault(HOMING_TICKS, PersistentDataType.INTEGER, 0) + 1;
        pdc.set(HOMING_TICKS, PersistentDataType.INTEGER, ticks);
        return ticks > maxTicks;
    }

    private Player findNearestPlayer(Location center, Entity shooter, double radius) {
        Player nearest = null;
        double nearestDistance = radius * radius;
        for (Entity candidate : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(candidate instanceof Player player) || !canTarget(player)) {
                continue;
            }
            if (shooter != null && shooter.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            double dist = player.getLocation().distanceSquared(center);
            if (dist <= nearestDistance) {
                nearestDistance = dist;
                nearest = player;
            }
        }
        return nearest;
    }

    private boolean canTarget(Player player) {
        if (!player.isOnline() || player.isDead()) {
            return false;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }
        return player.getGameMode() != GameMode.SPECTATOR;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SKILL;
    }
}
