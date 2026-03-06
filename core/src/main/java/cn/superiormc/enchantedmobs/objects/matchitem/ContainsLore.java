package cn.superiormc.enchantedmobs.objects.matchitem;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ContainsLore extends AbstractMatchItemRule {

    public ContainsLore() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, ItemStack item, ItemMeta meta) {
        if (!meta.hasLore()) {
            return false;
        }
        for (String hasLore : EnchantedMobs.methodUtil.getItemLore(meta)) {
            for (String requiredLore : section.getStringList("contains-lore")) {
                if (TextUtil.clear(hasLore).contains(TextUtil.clear(requiredLore))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return section.getStringList("contains-lore").isEmpty();
    }
}
