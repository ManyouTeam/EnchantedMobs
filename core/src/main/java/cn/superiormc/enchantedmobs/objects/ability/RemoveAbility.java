package cn.superiormc.enchantedmobs.objects.ability;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RemoveAbility extends AbstractAbility {

    public RemoveAbility(ConfigurationSection section) {
        super("Remove", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity entity = getTargetEntity(context);
        if (entity != null && !(entity instanceof Player)) {
            entity.remove();
        }
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SKILL;
    }
}
