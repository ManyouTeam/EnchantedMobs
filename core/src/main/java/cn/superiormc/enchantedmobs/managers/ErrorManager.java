package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.utils.SchedulerUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;

public class ErrorManager {

    public static ErrorManager errorManager;

    public boolean getError = false;

    private String lastErrorMessage = "";

    public ErrorManager(){
        errorManager = this;
    }

    public void sendErrorMessage(String message){
        if (!getError || !message.equals(lastErrorMessage)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " " + message);
            lastErrorMessage = message;
            getError = true;
            try {
                SchedulerUtil.runTaskLater(() -> getError = false, 100);
            } catch (Exception ignored) {
            }
        }
    }
}
