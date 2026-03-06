package cn.superiormc.enchantedmobs.objects.matchentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

public class EntityNone extends AbstractMatchEntityRule {

    public EntityNone() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        return !section.getBoolean("none");
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return !section.contains("none");
    }
}
