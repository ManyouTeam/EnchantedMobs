package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.AbilityDamageUtil;
import cn.superiormc.enchantedmobs.utils.SchedulerUtil;
import com.google.common.base.Enums;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class LaunchProjectileAbility extends AbstractAbility {

    public LaunchProjectileAbility(ConfigurationSection section) {
        super("LaunchProjectile", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity source = getTargetEntity(context);
        if (!(source instanceof LivingEntity livingEntity)) {
            return false;
        }

        EntityType projectileType = Enums.getIfPresent(EntityType.class, getString("entity-type", "ARROW")).orNull();
        if (projectileType == null || !projectileType.isSpawnable()) {
            return false;
        }

        Location eye = livingEntity.getEyeLocation();
        Vector direction = getLaunchDirection(eye, context);
        direction.setY(direction.getY() + getDouble("extra-y", 0, context.level()));
        Location spawnLocation = getSpawnLocation(eye, direction, context);
        Entity spawned = eye.getWorld().spawnEntity(spawnLocation, projectileType);
        if (!(spawned instanceof Projectile projectile)) {
            spawned.remove();
            return false;
        }

        projectile.setShooter(livingEntity);
        projectile.setVelocity(direction);

        if (projectile instanceof Fireball fireball) {
            fireball.setYield((float) getDouble("fireball-yield", 1.0, context.level()));
            fireball.setIsIncendiary(getBoolean("fireball-incendiary", true));
        }

        if (projectile instanceof ShulkerBullet bullet) {
            Entity target = context.handler().targetEntity;
            if (target != null && target.isValid()) {
                bullet.setTarget(target);
            }
        }

        if (section.contains("damage")) {
            AbilityDamageUtil.markDamage(projectile, getDouble("damage", 0.0D, context.level()));
        }
        return false;
    }

    private Location getSpawnLocation(Location eye, Vector direction, AbilityContext context) {
        double offset = getDouble("spawn-offset", 0.0D, context.level());
        if (offset <= 0.0D || direction.lengthSquared() <= 1.0E-4D) {
            return eye;
        }
        return eye.clone().add(direction.clone().normalize().multiply(offset));
    }

    private Vector getLaunchDirection(Location eye, AbilityContext context) {
        double speed = getDouble("speed", 1.5D, context.level());
        Entity aimTarget = context.handler().targetEntity;
        if (aimTarget != null && aimTarget.getWorld().equals(eye.getWorld()) && aimTarget instanceof LivingEntity livingEntity) {
            Vector targetDirection = livingEntity.getEyeLocation().toVector().subtract(eye.toVector());
            if (targetDirection.lengthSquared() > 1.0E-4D) {
                return targetDirection.normalize().multiply(speed);
            }
        }
        return eye.getDirection().normalize().multiply(speed);
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}
