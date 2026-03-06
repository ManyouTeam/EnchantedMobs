package cn.superiormc.enchantedmobs.utils;


import cn.superiormc.enchantedmobs.managers.ConfigManager;
import cn.superiormc.enchantedmobs.managers.ErrorManager;
import redempt.crunch.Crunch;

public class MathUtil {

    public static double doCalculate(String mathStr) {
        try {
            if (!ConfigManager.configManager.getBoolean("math.enabled")) {
                return Double.parseDouble(mathStr);
            }
            return Crunch.evaluateExpression(mathStr);
        }
        catch (NumberFormatException ep) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                ep.printStackTrace();
            }
            ErrorManager.errorManager.sendErrorMessage("§cError: Your number option value " +
                    mathStr + " can not be read as a number, maybe" +
                    "set math.enabled to false in config.yml maybe solve this problem!");
            return 0;
        }
    }

}
