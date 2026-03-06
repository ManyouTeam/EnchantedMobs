package cn.superiormc.enchantedmobs.objects.matchentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

public class EntityHealth extends AbstractMatchEntityRule {

    public EntityHealth() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        return entity.getHealth() >= section.getDouble("entity-health");
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return !section.contains("entity-health");
    }
}
