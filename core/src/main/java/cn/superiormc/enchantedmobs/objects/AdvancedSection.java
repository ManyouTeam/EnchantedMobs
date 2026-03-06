package cn.superiormc.enchantedmobs.objects;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.MathUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Random;

public abstract class AdvancedSection {

    protected ConfigurationSection section;

    protected String id;

    public AdvancedSection(String id, ConfigurationSection section) {
        this.section = section;
        this.id = id;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public ConfigurationSection getSection(String path) {
        return section.getConfigurationSection(path);
    }

    public double getDouble(String path, double defaultValue, int level, String... args) {
        if (section.get(path) == null) {
            //Bukkit.getConsoleSender().sendMessage("[EnchantedMobs Debug] Type: null, Power: " + id + ", Path: " + path + ", Value: " + defaultValue + ", Level: " + level + ".");
            return defaultValue;
        }
        if (section.get(path) instanceof Number) {
            //Bukkit.getConsoleSender().sendMessage("[EnchantedMobs Debug] Type: double, Power: " + id + ", Path: " + path + ", Value: " + section.getDouble(path) + ", Level: " + level + ".");
            return section.getDouble(path);
        }
        if (section.isString(path)) {
            String formula = section.getString(path, String.valueOf(defaultValue));
            if (formula.contains("~")) {
                String[] split = formula.split("~", 2);
                int min = Integer.parseInt(split[0].trim());
                int max = Integer.parseInt(split[1].trim());
                if (max < min) {
                    int temp = min;
                    min = max;
                    max = temp;
                }
                return min + new Random().nextInt(max - min + 1);
            }
            formula = CommonUtil.modifyString(null, formula, "level", String.valueOf(level));
            formula = CommonUtil.modifyString(null, formula, args);
            //Bukkit.getConsoleSender().sendMessage("[EnchantedMobs Debug] Type: formula, Power: " + id + ", Path: " + path + ", Value: " + MathUtil.doCalculate(formula) + ", Level: " + level + ".");
            return MathUtil.doCalculate(formula);
        }

        if (section.isConfigurationSection(path)) {
            for (String condition : section.getConfigurationSection(path).getKeys(false)) {
                if (matchCondition(condition, level)) {
                    Object valueObj = section.getConfigurationSection(path).get(condition);
                    if (valueObj instanceof Number number) {
                        //Bukkit.getConsoleSender().sendMessage("[EnchantedMobs Debug] Type: level-double, Power: " + id + ", Path: " + path + ", Value: " + number.doubleValue() + ", Level: " + level + ".");
                        return number.doubleValue();
                    }
                    if (valueObj instanceof String str) {
                        str = CommonUtil.modifyString(null, "level", String.valueOf(level));
                        str = CommonUtil.modifyString(null, str, args);
                        //Bukkit.getConsoleSender().sendMessage("[EnchantedMobs Debug] Type: level-formula, Power: " + id + ", Path: " + path + ", Value: " + MathUtil.doCalculate(str) + ", Level: " + level + ".");
                        return MathUtil.doCalculate(str);
                    }
                }
            }
        }
        return defaultValue;
    }

    public int getInt(String path, int defaultValue, int level) {
        return (int) getDouble(path, defaultValue, level);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return section.getBoolean(path, defaultValue);
    }

    public String getString(String path, String defaultValue) {
        return section.getString(path, defaultValue);
    }

    public List<String> getStringList(String path) {
        return section.getStringList(path);
    }

    private boolean matchCondition(String condition, int level) {
        String[] parts = condition.split(";;");

        for (String part : parts) {

            part = part.trim();

            if (part.startsWith(">=")) {
                int value = Integer.parseInt(part.substring(2));
                if (!(level >= value)) return false;
            }

            else if (part.startsWith("<=")) {
                int value = Integer.parseInt(part.substring(2));
                if (!(level <= value)) return false;
            }

            else if (part.startsWith(">")) {
                int value = Integer.parseInt(part.substring(1));
                if (!(level > value)) return false;
            }

            else if (part.startsWith("<")) {
                int value = Integer.parseInt(part.substring(1));
                if (!(level < value)) return false;
            }

            else if (part.startsWith("==")) {
                int value = Integer.parseInt(part.substring(2));
                if (!(level == value)) return false;
            }

            else {
                // 直接写数字，等于匹配
                int value = Integer.parseInt(part);
                if (level != value) return false;
            }
        }

        return true;
    }

    public String getId() {
        return id;
    }
}
