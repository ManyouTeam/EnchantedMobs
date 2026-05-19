package cn.superiormc.enchantedmobs.objects.matchentity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

public class Monster extends AbstractMatchEntityRule {

    public Monster() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        boolean result = entity instanceof Monster;
        if (section.getBoolean("monster")) {
            return result;
        }
        return !result;
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return !section.contains("monster");
    }
}
