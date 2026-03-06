package cn.superiormc.enchantedmobs.objects.matchentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public abstract class AbstractMatchEntityRule {

    public AbstractMatchEntityRule() {
        // Empty...
    }

    public abstract boolean getMatch(ConfigurationSection section, LivingEntity entity);

    public abstract boolean configNotContains(ConfigurationSection section);

    @Override
    public String toString() {
        return getClass().getName();
    }
}
