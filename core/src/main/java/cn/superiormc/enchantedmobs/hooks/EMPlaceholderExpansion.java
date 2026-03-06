package cn.superiormc.enchantedmobs.hooks;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.ConfigManager;
import cn.superiormc.enchantedmobs.managers.PlayerPowerManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class EMPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "enchantedmobs";
    }

    @Override
    public String getAuthor() {
        return "PQguanfang";
    }

    @Override
    public String getVersion() {
        return EnchantedMobs.instance.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || player.getPlayer() == null) {
            return "0";
        }
        if (params.equalsIgnoreCase("player_power")) {
            return String.valueOf(PlayerPowerManager.playerPowerManager.getPlayerPower(player.getPlayer()));
        }
        if (params.equalsIgnoreCase("chunk_power")) {
            return String.valueOf(PowerManager.powerManager.getNearbyAveragePlayerPower(
                    player.getPlayer().getLocation(),
                    ConfigManager.configManager.getInt("mob-power-generator.player-scan-range", 48)));
        }
        return null;
    }
}
