package cn.superiormc.enchantedmobs.utils;

import cn.superiormc.enchantedmobs.managers.ConfigManager;
import org.bukkit.Bukkit;

public class DebugUtil {

    private static final String PREFIX = "[EnchantedMobs Debug] ";

    private DebugUtil() {
    }

    public static void log(String category, String message) {
        if (!isEnabled(category)) {
            return;
        }
        Bukkit.getConsoleSender().sendMessage(PREFIX + "[" + category.toUpperCase() + "] " + message);
    }

    private static boolean isEnabled(String category) {
        if (ConfigManager.configManager == null) {
            return false;
        }
        if (!ConfigManager.configManager.getBoolean("debug", false)) {
            return false;
        }
        return ConfigManager.configManager.getBoolean("debug-categories." + category.toLowerCase(), true);
    }
}
