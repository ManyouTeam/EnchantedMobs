package cn.superiormc.enchantedmobs.objects.matchentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

public class None extends AbstractMatchEntityRule {

    public None() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        return false;
    }
    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return !section.contains("none");
    }
}
