package cn.superiormc.enchantedmobs.commands;

import cn.superiormc.enchantedmobs.managers.ConfigManager;
import cn.superiormc.enchantedmobs.managers.LanguageManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import org.bukkit.entity.Player;

public class SubChunkPower extends AbstractCommand {

    public SubChunkPower() {
        this.id = "chunkpower";
        this.requiredPermission = "enchantedmobs." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        int power = PowerManager.powerManager.getNearbyAveragePlayerPower(
                player.getLocation(),
                ConfigManager.configManager.getInt("mob-power-generator.player-scan-range", 48));
        LanguageManager.languageManager.sendStringText(player,
                "command.chunk-power",
                "world", player.getWorld().getName(),
                "x", String.valueOf(player.getLocation().getBlockX()),
                "z", String.valueOf(player.getLocation().getBlockZ()),
                "power", String.valueOf(power));
    }
}
