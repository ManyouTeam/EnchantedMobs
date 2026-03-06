package cn.superiormc.enchantedmobs.paper.listener;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.EntityScannerManager;
import cn.superiormc.enchantedmobs.managers.TempBlockManager;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Iterator;

public class PaperEntityScannerListener extends EntityScannerManager implements Listener {

    public PaperEntityScannerListener() {
        super();
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    @EventHandler
    public void onAdd(EntityAddToWorldEvent event) {
        updateEntityCache(event.getEntity());
    }


    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        updateEntityCache(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        removeEntityCache(event.getEntity());
    }

    @EventHandler
    public void onRemove(EntityRemoveFromWorldEvent event) {
        removeEntityCache(event.getEntity());
    }
}

