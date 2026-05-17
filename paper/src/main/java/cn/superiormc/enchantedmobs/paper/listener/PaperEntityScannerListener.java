package cn.superiormc.enchantedmobs.paper.listener;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.EntityScannerManager;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PaperEntityScannerListener extends EntityScannerManager implements Listener {

    public PaperEntityScannerListener() {
        super();
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdd(EntityAddToWorldEvent event) {
        updateEntityCache(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRemove(EntityRemoveFromWorldEvent event) {
        removeEntityCache(event.getEntity());
    }
}

