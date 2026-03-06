package cn.superiormc.enchantedmobs.objects.matchentity;

import cn.superiormc.enchantedmobs.managers.MatchEntityManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Not extends AbstractMatchEntityRule {

    public Not() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        ConfigurationSection notSection = section.getConfigurationSection("not");
        for (AbstractMatchEntityRule rule : MatchEntityManager.matchEntityManager.getRules()) {
            if (rule.configNotContains(notSection)) {
                continue;
            }
            if (rule.getMatch(notSection, entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return section.getConfigurationSection("not") == null;
    }
}
