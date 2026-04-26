package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.VirtualGuardianBeam;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class GuardianBeamAbility extends AbstractAbility {

    public GuardianBeamAbility(ConfigurationSection section) {
        super("GuardianBeam", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        if (!CommonUtil.checkPluginLoad("packetevents")) {
            return false;
        }
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
        VirtualGuardianBeam.start(caster, victim, chargeTicks, range,
                () -> Bukkit.getScheduler().runTask(EnchantedMobs.instance, () -> {
                    if (!caster.isValid() || !victim.isValid() || victim.isDead()) {
                        return;
                    }
                    if (!caster.getWorld().equals(victim.getWorld())) {
                        return;
                    }
                    if (caster.getLocation().distanceSquared(victim.getLocation()) > range * range) {
                        return;
                    }

                    victim.damage(damage, caster);
                    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GUARDIAN_ATTACK, 1f, 1f);
                }));
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}