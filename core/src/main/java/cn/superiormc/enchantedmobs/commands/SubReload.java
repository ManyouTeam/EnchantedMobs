package cn.superiormc.enchantedmobs.commands;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.*;
import org.bukkit.entity.Player;

public class SubReload extends AbstractCommand {

    public SubReload() {
        this.id = "reload";
        this.requiredPermission =  "enchantedmobs." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        EnchantedMobs.instance.reloadConfig();
        PowerManager.powerManager.cancelTask();
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        new PowerManager();
        PlayerPowerManager.playerPowerManager.reload();
        LanguageManager.languageManager.sendStringText(player, "plugin.reloaded");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        EnchantedMobs.instance.reloadConfig();
        PowerManager.powerManager.cancelTask();
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        new PowerManager();
        PlayerPowerManager.playerPowerManager.reload();
        LanguageManager.languageManager.sendStringText("plugin.reloaded");
    }
}
