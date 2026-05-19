package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.objects.power.events.DamageHandler;
import cn.superiormc.enchantedmobs.objects.power.events.MeleeAttackHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class DamageEntityAbility extends AbstractAbility {

    public DamageEntityAbility(ConfigurationSection section) {
        super("DamageEntity", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity entity = getTargetEntity(context);
        if (!(entity instanceof LivingEntity living) || living.getHealth() <= 0) {
            return false;
        }
        double original = getOriginalDamage(context);
        double amount = Math.max(0.0D, getDouble("amount", 1.0D, context.level(),
                "damage", String.valueOf(original),
                "original", String.valueOf(original)));
        living.damage(amount, getSourceEntity(context));
        return false;
    }

    private double getOriginalDamage(AbilityContext context) {
        if (context.handler() instanceof DamageHandler damageHandler) {
            return damageHandler.originalDamage;
        }
        if (context.handler() instanceof MeleeAttackHandler meleeAttackHandler) {
            return meleeAttackHandler.originalDamage;
        }
        return 0.0D;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
