package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.AbilityDamageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class SetInvulnerableAbility extends AbstractAbility {

    public SetInvulnerableAbility(ConfigurationSection section) {
        super("SetInvulnerable", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity entity = getTargetEntity(context);
        if (entity == null) {
            return false;
        }
        boolean value = getBoolean("value", true);
        int duration = getInt("duration", 0, context.level());
        AbilityDamageUtil.setInvulnerable(entity, value, duration);
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}
