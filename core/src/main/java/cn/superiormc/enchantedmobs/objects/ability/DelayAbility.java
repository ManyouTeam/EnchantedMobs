package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.AbilityManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

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
        Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, () ->
                AbilityManager.abilityManager.execute(section.getConfigurationSection("abilities"), context), delay);
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return null;
    }
}
