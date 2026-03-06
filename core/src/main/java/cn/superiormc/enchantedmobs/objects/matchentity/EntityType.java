package cn.superiormc.enchantedmobs.objects.matchentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

public class EntityType extends AbstractMatchEntityRule {

    public EntityType() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        for (String mobID : section.getStringList("entity-types")) {
            if (entity.getType().name().equalsIgnoreCase(mobID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return section.getStringList("entity-types").isEmpty();
    }
}
