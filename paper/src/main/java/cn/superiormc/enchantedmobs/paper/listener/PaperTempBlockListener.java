package cn.superiormc.enchantedmobs.paper.listener;

import java.util.Iterator;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.TempBlockManager;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
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
import org.bukkit.event.entity.*;

public class PaperTempBlockListener extends TempBlockManager implements Listener {

    public PaperTempBlockListener() {
        super();
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        if (isTempBlock(location)) {
            event.setDropItems(false);
            event.setExpToDrop(0);
            removeTempBlock(location);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block exploded = iterator.next();
            if (isTempBlock(exploded.getLocation())) {
                iterator.remove();
                exploded.setType(Material.AIR);
                removeTempBlock(exploded.getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block exploded = iterator.next();
            if (isTempBlock(exploded.getLocation())) {
                iterator.remove();
                exploded.setType(Material.AIR);
                removeTempBlock(exploded.getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreakBlock(BlockBreakBlockEvent event) {
        Block block = event.getBlock();
        if (isTempBlock(block.getLocation())) {
            event.getDrops().clear();
            removeTempBlock(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        if (isTempBlock(block.getLocation())) {
            event.setWillDrop(false);
            removeTempBlock(block.getLocation());
        }
    }
}

