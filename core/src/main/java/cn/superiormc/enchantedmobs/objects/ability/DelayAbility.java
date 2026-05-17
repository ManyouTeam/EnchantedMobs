package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.managers.AbilityManager;
import cn.superiormc.enchantedmobs.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class DelayAbility extends AbstractAbility {

    public DelayAbility(ConfigurationSection section) {
        super("Delay", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        int delay = getInt("ticks", section.getInt("delay", 1), context.level());
        if (delay <= 0) {
            delay = 1;
        }
        Entity source = context.handler().sourceEntity;
        if (source != null) {
            SchedulerUtil.runTaskLater(source, () ->
                    AbilityManager.abilityManager.execute(section.getConfigurationSection("abilities"), context), delay);
        } else {
            SchedulerUtil.runTaskLater(() ->
                    AbilityManager.abilityManager.execute(section.getConfigurationSection("abilities"), context), delay);
        }
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return null;
    }
}
