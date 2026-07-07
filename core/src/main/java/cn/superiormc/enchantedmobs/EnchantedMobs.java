package cn.superiormc.enchantedmobs;

import cn.superiormc.enchantedmobs.listeners.DroppedItemListener;
import cn.superiormc.enchantedmobs.managers.*;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.SpecialMethodUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnchantedMobs extends JavaPlugin {

    public static EnchantedMobs instance;

    public static SpecialMethodUtil methodUtil;

    public static boolean isFolia = false;

    public static int yearVersion;

    public static int majorVersion;

    public static int minorVersion;

    @Override
    public void onEnable() {
        instance = this;
        try {
            String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
            yearVersion = versionParts.length > 0 && versionParts[0].matches("\\d+") ? Integer.parseInt(versionParts[0]) : 1;
            majorVersion = versionParts.length > 1 && versionParts[1].matches("\\d+") ? Integer.parseInt(versionParts[1]) : 0;
            minorVersion = versionParts.length > 2 && versionParts[2].matches("\\d+") ? Integer.parseInt(versionParts[2]) : 0;
        } catch (Throwable throwable) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: Can not get your Minecraft version! Default set to 1.0.0.");
        }
        if (CommonUtil.getClass("com.destroystokyo.paper.PaperConfig") && CommonUtil.getMinorVersion(18, 2)) {
            try {
                Class<?> paperClass = Class.forName("cn.superiormc.enchantedmobs.paper.PaperMethodUtil");
                methodUtil = (SpecialMethodUtil) paperClass.getDeclaredConstructor().newInstance();
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPaper is found, entering Paper plugin mode...!");
            } catch (Throwable throwable) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        } else {
            try {
                Class<?> spigotClass = Class.forName("cn.superiormc.enchantedmobs.spigot.SpigotMethodUtil");
                methodUtil = (SpecialMethodUtil) spigotClass.getDeclaredConstructor().newInstance();
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fSpigot is found, entering Spigot plugin mode...!");
            } catch (Throwable throwable) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
        if (CommonUtil.getClass("io.papermc.paper.threadedregions.RegionizedServer")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fFolia is found, enabled Folia compatibility feature!");
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §6Warning: Folia support is not fully test, major bugs maybe found! " +
                    "Please do not use in production environment!");
            isFolia = true;
        }
        new ErrorManager();
        new InitManager();
        new ConfigManager();
        new MatchEntityManager();
        new MatchItemManager();
        new AbilityManager();
        new PowerManager();
        if (ConfigManager.configManager.getBoolean("mob-combat.disarm-auto-return-item", true)) {
            new DroppedItemListener();
        }
        EnchantedMobs.methodUtil.entityScannerManager();
        EnchantedMobs.methodUtil.playerPowerManager();
        EnchantedMobs.methodUtil.tempBlockManager();
        new HookManager();
        new ItemManager();
        new LanguageManager();
        new CommandManager();
        new ListenerManager();
        new LicenseManager();
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fYour server version is: " + yearVersion + "." + majorVersion + "." + minorVersion + "!");
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        TempBlockManager.tempBlockManager.clearAll();
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is disabled. Author: PQguanfang.");
    }
}
