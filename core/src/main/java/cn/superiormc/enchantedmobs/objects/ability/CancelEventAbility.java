package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.DebugUtil;
import org.bukkit.configuration.ConfigurationSection;

public class CancelEventAbility extends AbstractAbility {

    public CancelEventAbility(ConfigurationSection section) {
        super("CancelEvent", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        DebugUtil.log("ability", "Cancel event ability executed.");
        return true;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return null;
    }
}
