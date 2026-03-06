package cn.superiormc.enchantedmobs.objects.matchitem;

import cn.superiormc.enchantedmobs.managers.ConfigManager;
import cn.superiormc.enchantedmobs.managers.HookManager;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items extends AbstractMatchItemRule {

    public Items() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, ItemStack item, ItemMeta meta) {
        if (ConfigManager.configManager.getBoolean("debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cItem ID: " +
                    HookManager.hookManager.parseItemID(item, section.getBoolean("use-tier-identify", false)));
        }
        return section.getStringList("items").contains(
                HookManager.hookManager.parseItemID(item, section.getBoolean("use-tier-identify", false)));
    }

    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return section.getStringList("items").isEmpty();
    }
}
