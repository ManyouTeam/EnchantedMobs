package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.managers.AbilityManager;
import org.bukkit.configuration.ConfigurationSection;

public class LimitAbility extends AbstractAbility {

    public LimitAbility(ConfigurationSection section) {
        super("Limit", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        ConfigurationSection abilities = section.getConfigurationSection("abilities");
        if (abilities == null) {
            return false;
        }
        return AbilityManager.abilityManager.execute(abilities, context);
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
