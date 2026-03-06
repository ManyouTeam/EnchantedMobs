package cn.superiormc.enchantedmobs.objects.ability;

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

        EntityType projectileType = Enums.getIfPresent(EntityType.class, getString("entity-type", "ARROW").toUpperCase()).orNull();
        if (projectileType == null || !projectileType.isSpawnable()) {
            return false;
        }

        Location eye = livingEntity.getEyeLocation();
        Entity spawned = eye.getWorld().spawnEntity(eye, projectileType);
        if (!(spawned instanceof Projectile projectile)) {
            spawned.remove();
            return false;
        }

        projectile.setShooter(livingEntity);
        Vector direction = eye.getDirection().normalize().multiply(getDouble("speed", 1.5, context.level()));
        direction.setY(direction.getY() + getDouble("extra-y", 0, context.level()));
        projectile.setVelocity(direction);

        if (projectile instanceof Fireball fireball) {
            fireball.setYield((float) getDouble("fireball-yield", 1.0, context.level()));
            fireball.setIsIncendiary(getBoolean("fireball-incendiary", true));
        }

        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}
