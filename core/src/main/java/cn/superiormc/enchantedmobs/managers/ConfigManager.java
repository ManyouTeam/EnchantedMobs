package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ConfigManager {

    public static ConfigManager configManager;

    public FileConfiguration config;

    public Map<String, ConfigurationSection> powerConfigs = new HashMap<>();

    public ConfigManager() {
        configManager = this;
        config = EnchantedMobs.instance.getConfig();
        initPowerConfigs();
    }

    private void initPowerConfigs() {
        File dir = new File(EnchantedMobs.instance.getDataFolder(), "powers");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                String substring = fileName.substring(0, fileName.length() - 4);
                powerConfigs.put(substring, YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public String getString(String path, String... args) {
        String s = config.getString(path);
        if (s == null) {
            if (args.length == 0) {
                return null;
            }
            s = args[0];
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                s = s.replace(var, "");
            }
            else {
                s = s.replace(var, args[i + 1]);
            }
        }
        return s.replace("{plugin_folder}", String.valueOf(EnchantedMobs.instance.getDataFolder()));
    }


    public java.util.List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public ConfigurationSection getSection(String path) {
        if (config.getConfigurationSection(path) == null) {
            return new MemoryConfiguration();
        }
        return config.getConfigurationSection(path);
    }

    public ConfigurationSection getPowerConfig(String id) {
        if (!powerConfigs.containsKey(id)) {
            powerConfigs.put(id, new MemoryConfiguration());
        }
        return powerConfigs.get(id);
    }
}
