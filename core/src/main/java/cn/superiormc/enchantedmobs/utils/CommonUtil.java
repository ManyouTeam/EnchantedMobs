package cn.superiormc.enchantedmobs.utils;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.LanguageManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static Map<String, Boolean> loadedPlugins = new HashMap<>();

    public static boolean checkPluginLoad(String pluginName) {
        if (loadedPlugins.containsKey(pluginName)) {
            return loadedPlugins.get(pluginName);
        }
        loadedPlugins.put(pluginName, Bukkit.getPluginManager().isPluginEnabled(pluginName));
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean getClass(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean checkClass(String className, String methodName) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method[] methods = targetClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }

            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean getYearVersion(int year, int majorVersion, int minorVersion) {
        return EnchantedMobs.yearVersion > year || (EnchantedMobs.yearVersion == year && EnchantedMobs.majorVersion >= majorVersion && EnchantedMobs.minorVersion >= minorVersion);
    }

    public static boolean getMajorVersion(int version) {
        return EnchantedMobs.yearVersion > 1 || EnchantedMobs.majorVersion >= version;
    }

    public static boolean getMinorVersion(int majorVersion, int minorVersion) {
        return EnchantedMobs.yearVersion > 1 || EnchantedMobs.majorVersion > majorVersion || (EnchantedMobs.majorVersion == majorVersion &&
                EnchantedMobs.minorVersion >= minorVersion);
    }

    public static String modifyString(Player player, String text, String... args) {
        for (int i = 0 ; i < args.length ; i += 2) {
            text = CommonUtil.parseLang(player, text);
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        return text;
    }

    public static List<String> modifyList(Player player, List<String> config, String... args) {
        List<String> resultList = new ArrayList<>();
        for (String s : config) {
            s = CommonUtil.parseLang(player, s);
            for (int i = 0 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    s = s.replace(var, "");
                } else {
                    s = s.replace(var, args[i + 1]);
                }
            }
            String[] tempVal1 = s.split(";;");
            if (tempVal1.length > 1) {
                for (String string : tempVal1) {
                    resultList.add(TextUtil.withPAPI(string, player));
                }
                continue;
            }
            resultList.add(TextUtil.withPAPI(s, player));
        }
        return resultList;
    }

    public static String parseLang(Player player, String text) {
        Pattern pattern8 = Pattern.compile("\\{lang:(.*?)}");
        Matcher matcher8 = pattern8.matcher(text);
        while (matcher8.find()) {
            String placeholder = matcher8.group(1);
            text = text.replace("{lang:" + placeholder + "}", LanguageManager.languageManager.getStringText(player, "override-lang." + placeholder));
        }
        return text;
    }

    public static void summonMythicMobs(Location location, String mobID, int level) {
        MythicBukkit.inst().getMobManager().getMythicMob(mobID).ifPresent(mob -> mob.spawn(BukkitAdapter.adapt(location), level));
    }

    public static void mkDir(File dir) {
        if (!dir.exists()) {
            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }
            String parentPath = parentFile.getPath();
            mkDir(new File(parentPath));
            dir.mkdir();
        }
    }

    public static NamespacedKey parseNamespacedKey(String key) {
        String[] keySplit = key.split(":");
        if (keySplit.length == 1) {
            return NamespacedKey.minecraft(key.toLowerCase());
        }
        return NamespacedKey.fromString(key);
    }

    public static Color parseColor(String color) {
        if (color == null || color.isEmpty()) {
            return Color.fromRGB(0, 0, 0);
        }

        color = color.trim();

        // 支持 #RRGGBB
        if (color.startsWith("#")) {
            return Color.fromRGB(Integer.parseInt(color.substring(1), 16));
        }

        // 支持 R,G,B
        String[] keySplit = color.replace(" ", "").split(",");
        if (keySplit.length == 3) {
            return Color.fromRGB(
                    Integer.parseInt(keySplit[0]),
                    Integer.parseInt(keySplit[1]),
                    Integer.parseInt(keySplit[2])
            );
        }

        // 默认：单值 RGB int
        return Color.fromRGB(Integer.parseInt(color));
    }

    public static String colorToString(Color color) {
        if (color == null) {
            return "0,0,0";
        }
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    public static List<Color> parseColorList(List<String> rawList) {
        List<Color> colors = new ArrayList<>();

        for (String value : rawList) {
            try {
                colors.add(parseColor(value));
            } catch (Exception e) {
                return colors;
            }
        }

        return colors;
    }

    public static void giveOrDrop(Player player, ItemStack... item) {
        HashMap<Integer, ItemStack> result = player.getInventory().addItem(item);
        if (!result.isEmpty()) {
            for (int id : result.keySet()) {
                player.getWorld().dropItem(player.getLocation(), result.get(id));
            }
        }
    }

    public static double getMaxHealth(Entity entity) {
        if (entity instanceof LivingEntity living) {
            AttributeInstance maxHealth = living.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth == null) {
                return 0;
            }
            return maxHealth.getValue();
        }
        return 0;
    }
}
