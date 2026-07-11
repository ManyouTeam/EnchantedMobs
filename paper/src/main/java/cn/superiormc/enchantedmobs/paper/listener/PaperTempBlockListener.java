package cn.superiormc.enchantedmobs.paper.listener;

import java.util.Iterator;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.TempBlockManager;
import cn.superiormc.enchantedmobs.utils.SchedulerUtil;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.*;

public class PaperTempBlockListener extends TempBlockManager implements Listener {

    public PaperTempBlockListener() {
        super();
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        if (isTempBlock(location)) {
            event.setDropItems(false);
            event.setExpToDrop(0);
            removeTempBlock(location);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block exploded = iterator.next();
            if (isTempBlock(exploded.getLocation())) {
                iterator.remove();
                removeTempBlock(exploded.getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block exploded = iterator.next();
            if (isTempBlock(exploded.getLocation())) {
                iterator.remove();
                removeTempBlock(exploded.getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakBlock(BlockBreakBlockEvent event) {
        Block block = event.getBlock();
        if (isTempBlock(block.getLocation())) {
            event.getDrops().clear();
            SchedulerUtil.runTaskLater(block.getLocation(), () -> removeTempBlock(block.getLocation()), 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        if (isTempBlock(block.getLocation())) {
            event.setWillDrop(false);
            removeTempBlock(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (containsTempBlock(event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (containsTempBlock(event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Location location = event.getBlock().getLocation();
        if (isTempBlock(location)) {
            event.setCancelled(true);
            removeTempBlock(location);
        }
    }

    private boolean containsTempBlock(Iterable<Block> blocks, org.bukkit.block.BlockFace direction) {
        for (Block block : blocks) {
            if (isTempBlock(block.getLocation()) || isTempBlock(block.getRelative(direction).getLocation())) {
                return true;
            }
        }
        return false;
    }
}

