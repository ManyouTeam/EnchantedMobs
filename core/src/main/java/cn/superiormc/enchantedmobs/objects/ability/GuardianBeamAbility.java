package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GuardianBeamAbility extends AbstractAbility {

    public GuardianBeamAbility(ConfigurationSection section) {
        super("GuardianBeam", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity source = context.handler().sourceEntity;
        Entity target = getTargetEntity(context);
        if (!(source instanceof LivingEntity caster) || !(target instanceof LivingEntity victim)) {
            return false;
        }

        double range = getDouble("range", 18.0, context.level());
        if (!caster.getWorld().equals(victim.getWorld()) || caster.getLocation().distanceSquared(victim.getLocation()) > range * range) {
            return false;
        }

        int chargeTicks = Math.max(1, getInt("charge-ticks", 30, context.level()));
        double damage = Math.max(0.0, getDouble("damage", 6.0, context.level()));

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!caster.isValid() || !victim.isValid() || victim.isDead() || caster.getWorld() != victim.getWorld()) {
                    cancel();
                    return;
                }

                drawBeam(caster.getEyeLocation(), victim.getEyeLocation());
                if (++tick < chargeTicks) {
                    return;
                }

                victim.damage(damage, caster);
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GUARDIAN_ATTACK, 1f, 1f);
                cancel();
            }
        }.runTaskTimer(EnchantedMobs.instance, 0L, 1L);
        return false;
    }

    private void drawBeam(Location from, Location to) {
        Vector path = to.toVector().subtract(from.toVector());
        double length = path.length();
        if (length <= 0.001) {
            return;
        }
        Vector step = path.normalize().multiply(0.5);
        Location cursor = from.clone();
        for (double i = 0; i < length; i += 0.5) {
            from.getWorld().spawnParticle(Particle.END_ROD, cursor, 1, 0, 0, 0, 0);
            cursor.add(step);
        }
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
