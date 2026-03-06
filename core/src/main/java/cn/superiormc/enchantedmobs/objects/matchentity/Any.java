package cn.superiormc.enchantedmobs.objects.matchentity;

import cn.superiormc.enchantedmobs.managers.MatchEntityManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

import java.util.Set;

public class Any extends AbstractMatchEntityRule {

    public Any() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        ConfigurationSection anySection = section.getConfigurationSection("any");
        if (anySection == null) {
            return true;
        }
        Set<String> anyKeys = anySection.getKeys(false);
        if (anyKeys.isEmpty()) {
            return true;
        }
        if (anyKeys.contains("1")) {
            big: for (String anyKey : anyKeys) {
                ConfigurationSection checkSection = anySection.getConfigurationSection(anyKey);
                for (AbstractMatchEntityRule rule : MatchEntityManager.matchEntityManager.getRules()) {
                    if (rule.configNotContains(checkSection)) {
                        continue;
                    }
                    if (!rule.getMatch(checkSection, entity)) {
                        continue big;
                    }
                }
                return true;
            }
        } else {
            for (AbstractMatchEntityRule rule : MatchEntityManager.matchEntityManager.getRules()) {
                if (rule.configNotContains(anySection)) {
                    continue;
                }
                if (rule.getMatch(anySection, entity)) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return !section.contains("any");
    }
}
