package cn.superiormc.enchantedmobs.objects.ability;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class ArrowRainAbility extends AbstractAbility {

    public ArrowRainAbility(ConfigurationSection section) {
        super("ArrowRain", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity source = context.handler().sourceEntity;
        Entity target = getTargetEntity(context);
        if (target == null) {
            return false;
        }

        Location center = getLocation(context);
        if (center.getWorld() == null) {
            return false;
        }

        int count = Math.max(1, getInt("count", 10, context.level()));
        double radius = Math.max(0.1, getDouble("radius", 4.0, context.level()));
        double height = Math.max(2.0, getDouble("height", 12.0, context.level()));
        double spread = Math.max(0.0, getDouble("spread", 0.18, context.level()));
        double damage = getDouble("damage", -1.0, context.level());
        int pierceLevel = Math.max(0, getInt("pierce-level", 0, context.level()));
        boolean critical = getBoolean("critical", false);

        for (int i = 0; i < count; i++) {
            double x = ThreadLocalRandom.current().nextDouble(-radius, radius);
            double z = ThreadLocalRandom.current().nextDouble(-radius, radius);
            Location spawn = center.clone().add(x, height, z);

            Arrow arrow = center.getWorld().spawn(spawn, Arrow.class);
            if (source instanceof LivingEntity shooter) {
                arrow.setShooter(shooter);
            }

            Vector velocity = new Vector(
                    ThreadLocalRandom.current().nextDouble(-spread, spread),
                    -1.0,
                    ThreadLocalRandom.current().nextDouble(-spread, spread)
            );
            arrow.setVelocity(velocity);
            arrow.setCritical(critical);
            arrow.setPierceLevel(pierceLevel);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

            if (damage >= 0) {
                arrow.setDamage(damage);
            }
        }

        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
