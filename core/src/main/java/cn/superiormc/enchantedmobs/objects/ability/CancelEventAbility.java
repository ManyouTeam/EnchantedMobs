package cn.superiormc.enchantedmobs.objects.ability;

import org.bukkit.configuration.ConfigurationSection;

public class CancelEventAbility extends AbstractAbility {

    public CancelEventAbility(ConfigurationSection section) {
        super("CancelEvent", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        return true;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return null;
    }
}
