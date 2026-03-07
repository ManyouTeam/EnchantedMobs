package cn.superiormc.enchantedmobs.spigot.listener;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.PlayerPowerManager;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.Set;

public class SpigotPlayerPowerListener extends PlayerPowerManager implements Listener {

    public SpigotPlayerPowerListener() {
        super();
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        initPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, () -> PlayerPowerManager.playerPowerManager.updateChangedSlots(player, CommonUtil.arrayToSet(event.getRawSlot(), event.getSlot())), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory() instanceof PlayerInventory)) {
            return;
        }
        Set<Integer> tempVal1 = event.getInventorySlots();
        tempVal1.addAll(event.getRawSlots());
        Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, () -> PlayerPowerManager.playerPowerManager.updateChangedSlots(player, tempVal1), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreative(InventoryCreativeEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory() instanceof PlayerInventory)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, () -> PlayerPowerManager.playerPowerManager.updateChangedSlots(player, CommonUtil.arrayToSet(event.getRawSlot(), event.getSlot())), 1L);
    }
}
