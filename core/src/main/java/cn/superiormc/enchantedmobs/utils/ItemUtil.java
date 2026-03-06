package cn.superiormc.enchantedmobs.utils;

import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    public static boolean isValid(ItemStack item) {
        return item != null && !item.getType().isAir();
    }
}
