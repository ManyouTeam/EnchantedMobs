package cn.superiormc.enchantedmobs.objects.ability;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class LightningAbility extends AbstractAbility {

    public LightningAbility(ConfigurationSection section) {
        super("Lightning", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity entity = getTargetEntity(context);
        int level = context.level();
        int count = Math.max(1, getInt("count", 2, level));
        double radius = getDouble("radius", 3.0, level);

        for (int i = 0; i < count; i++) {
            Location strike = entity.getLocation().clone().add(
                    (Math.random() * 2 - 1) * radius,
                    0,
                    (Math.random() * 2 - 1) * radius
            );
            entity.getWorld().strikeLightningEffect(strike);
            for (Entity nearby : entity.getNearbyEntities(radius, 3, radius)) {
                if (nearby instanceof LivingEntity livingEntity) {
                    livingEntity.damage(getDouble("damage", 4.0, level), entity);
                }
            }
        }
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
