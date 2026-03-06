package cn.superiormc.enchantedmobs.objects.matchitem;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MaterialTag extends AbstractMatchItemRule {

    public MaterialTag() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, ItemStack item, ItemMeta meta) {
        for (String singleMaterial : section.getStringList("material-tag")) {
            Tag<Material> tempVal1 = Bukkit.getTag(Tag.REGISTRY_ITEMS, CommonUtil.parseNamespacedKey(singleMaterial), Material.class);
            if (tempVal1 != null && tempVal1.isTagged(item.getType())) {
                return true;
            }
            Tag<Material> tempVal2 = Bukkit.getTag(Tag.REGISTRY_BLOCKS, CommonUtil.parseNamespacedKey(singleMaterial), Material.class);
            if (tempVal2 != null && tempVal2.isTagged(item.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return section.getStringList("material-tag").isEmpty();
    }
}
