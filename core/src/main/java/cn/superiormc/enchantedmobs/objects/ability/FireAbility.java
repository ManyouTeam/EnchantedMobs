package cn.superiormc.enchantedmobs.objects.ability;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class FireAbility extends AbstractAbility {

    public FireAbility(ConfigurationSection section) {
        super("Fire", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity target = getTargetEntity(context);
        if (!(target instanceof LivingEntity living)) {
            return false;
        }
        int fireTicks = Math.max(0, getInt("fire-ticks", 60, context.level()));
        living.setFireTicks(fireTicks);
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
