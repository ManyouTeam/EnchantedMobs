package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import fr.skytasul.guardianbeam.Laser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

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
        if (!caster.getWorld().equals(victim.getWorld())
                || caster.getLocation().distanceSquared(victim.getLocation()) > range * range) {
            return false;
        }

        int chargeTicks = Math.max(1, getInt("charge-ticks", 30, context.level()));
        double damage = Math.max(0.0, getDouble("damage", 6.0, context.level()));
        Location start = caster.getEyeLocation();

        try {
            Laser laser = new Laser.GuardianLaser(
                        start,
                        victim,
                        chargeTicks,
                        (int) Math.ceil(range)
                ).durationInTicks();

            laser.executeEnd(() -> {
                if (!caster.isValid() || !victim.isValid() || victim.isDead()) {
                    return;
                }
                if (!caster.getWorld().equals(victim.getWorld())) {
                    return;
                }
                if (caster.getLocation().distanceSquared(victim.getLocation()) > range * range) {
                    return;
                }

                Bukkit.getScheduler().runTask(EnchantedMobs.instance, () -> {
                    victim.damage(damage, caster);
                    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GUARDIAN_ATTACK, 1f, 1f);
                });
            });

            laser.start(EnchantedMobs.instance);

            // 持续让激光起点跟随施法者眼睛位置
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!laser.isStarted()) {
                        cancel();
                        return;
                    }

                    if (!caster.isValid() || caster.isDead() || !victim.isValid() || victim.isDead()) {
                        laser.stop();
                        cancel();
                        return;
                    }

                    if (!caster.getWorld().equals(victim.getWorld())
                            || caster.getLocation().distanceSquared(victim.getLocation()) > range * range) {
                        laser.stop();
                        cancel();
                        return;
                    }

                    try {
                        laser.moveStart(caster.getEyeLocation());
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                        laser.stop();
                        cancel();
                    }
                }
            }.runTaskTimer(EnchantedMobs.instance, 1L, 1L);

            return false;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}