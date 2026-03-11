package cn.superiormc.enchantedmobs.listeners;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.LanguageManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DroppedItemListener implements Listener {

    private static final Set<EntityRemoveEvent.Cause> RETURN_CAUSES = EnumSet.of(
            EntityRemoveEvent.Cause.DEATH,
            EntityRemoveEvent.Cause.DESPAWN,
            EntityRemoveEvent.Cause.OUT_OF_WORLD,
            EntityRemoveEvent.Cause.PLUGIN
    );

    public static DroppedItemListener droppedItemCache;

    public DroppedItemListener() {
        droppedItemCache = this;
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    private final Map<UUID, List<DroppedItem>> cache = new HashMap<>();

    private void removeExtra() {
        if (cache.size() >= 1000) {
            Iterator<?> it = cache.keySet().iterator();
            for (int i = 0; i < 400; i++) {
                it.next();
                it.remove();
            }
        }
    }

    private void backItem(Player player) {
        List<DroppedItem> droppedItems = cache.remove(player.getUniqueId());
        if (droppedItems == null || droppedItems.isEmpty()) {
            return;
        }

        boolean display = false;
        for (DroppedItem droppedItem : droppedItems) {
            Entity entity = Bukkit.getEntity(droppedItem.itemEntityUUID());
            if (entity == null || !entity.isValid()) {
                CommonUtil.giveOrDrop(player, droppedItem.item());
                display = true;
            }
        }
        if (display) {
            LanguageManager.languageManager.sendStringText(player, "item-back");
        }
    }

    private void addItem(Item item) {
        Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, () -> {
            removeExtra();
            UUID uuid = item.getOwner();
            if (uuid != null && PowerManager.powerManager.isDisarmReturnItem(item)) {
                ItemStack dropItem = item.getItemStack().clone();
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    if (item.isValid()) {
                        return;
                    }
                    CommonUtil.giveOrDrop(player, dropItem);
                    LanguageManager.languageManager.sendStringText(player, "item-back");
                } else {
                    List<DroppedItem> tempVal1 = cache.computeIfAbsent(uuid, ignored -> new ArrayList<>());
                    if (tempVal1.stream().anyMatch(saved -> saved.itemEntityUUID().equals(item.getUniqueId()))) {
                        return;
                    }
                    DroppedItem droppedItem = new DroppedItem(dropItem, item.getUniqueId());
                    tempVal1.add(droppedItem);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onRemove(EntityRemoveEvent event) {
        if (!(event.getEntity() instanceof Item item)) {
            return;
        }
        if (!PowerManager.powerManager.isDisarmReturnItem(item)) {
            return;
        }
        if (!RETURN_CAUSES.contains(event.getCause())) {
            return;
        }
        DroppedItemListener.droppedItemCache.addItem(item);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DroppedItemListener.droppedItemCache.backItem(player);
    }

    static record DroppedItem (
        ItemStack item, UUID itemEntityUUID
    ) {

    }
}
