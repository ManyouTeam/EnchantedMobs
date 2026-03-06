package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class SetHealthAbility extends AbstractAbility {

    public SetHealthAbility(ConfigurationSection section) {
        super("SetHealth", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity entity = getTargetEntity(context);
        if (entity == null) {
            return false;
        }
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        double max = CommonUtil.getMaxHealth(living);
        double heal = getDouble("amount", 10.0, context.level(), "health", String.valueOf(living.getHealth()), "max-health", String.valueOf(max));
        if (heal >= 2048) {
            heal = 2048;
        }
        living.setHealth(Math.min(max, heal));
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}
