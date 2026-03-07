package cn.superiormc.enchantedmobs.paper.listener;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.PlayerPowerManager;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class PaperPlayerPowerListener extends PlayerPowerManager implements Listener {

    public PaperPlayerPowerListener() {
        super();
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerPowerManager.playerPowerManager.initPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        PlayerPowerManager.playerPowerManager.removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerInventorySlotChangeEvent event) {
        PlayerPowerManager.playerPowerManager.updateChangedSlots(event.getPlayer(), CommonUtil.arrayToSet(event.getRawSlot(), event.getSlot()));
    }

}
