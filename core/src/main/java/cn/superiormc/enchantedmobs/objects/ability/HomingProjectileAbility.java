package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.objects.power.events.ShootBowHandler;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class HomingProjectileAbility extends AbstractAbility {

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

    private Player findNearestPlayer(Location center, Entity shooter, double radius) {
        Player nearest = null;
        double nearestDistance = radius * radius;
        for (Entity candidate : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(candidate instanceof Player player) || !player.isOnline() || player.isDead()) {
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

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SKILL;
    }
}
