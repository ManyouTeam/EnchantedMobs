package cn.superiormc.enchantedmobs.paper.listener;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.LanguageManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DroppedItemListener implements Listener {

    public static DroppedItemListener droppedItemCache;

    public DroppedItemListener() {
        droppedItemCache = this;
        Bukkit.getPluginManager().registerEvents(this, EnchantedMobs.instance);
    }

    public Map<UUID, List<ItemStack>> cache = new HashMap<>();

    public void removeExtra() {
        if (cache.size() >= 1000) {
            Iterator<?> it = cache.keySet().iterator();
            for (int i = 0; i < 400; i++) {
                it.next();
                it.remove();
            }
        }
    }

    public void backItem(Player player) {
        if (cache.containsKey(player.getUniqueId())) {
            CommonUtil.giveOrDrop(player, cache.get(player.getUniqueId()).toArray(ItemStack[]::new));
            cache.remove(player.getUniqueId());
            LanguageManager.languageManager.sendStringText(player, "item-back");
        }
    }

    public void addItem(Item item) {
        removeExtra();
        UUID uuid = item.getOwner();
        if (uuid != null && PowerManager.powerManager.isUsedPower(item)) {
            ItemStack dropItem = item.getItemStack().clone();
            item.setItemStack(new ItemStack(Material.AIR));
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                CommonUtil.giveOrDrop(player, dropItem);
                LanguageManager.languageManager.sendStringText(player, "item-back");
            } else {
                List<ItemStack> tempVal1 = cache.get(uuid);
                if (tempVal1 == null) {
                    tempVal1 = new ArrayList<>();
                }
                tempVal1.add(dropItem);
                cache.put(uuid, tempVal1);
            }
        }
    }

    @EventHandler
    public void onDespawn(EntityRemoveEvent event) {
        if (event.getCause().equals(EntityRemoveEvent.Cause.PICKUP)) {
            return;
        }
        if (!(event.getEntity() instanceof Item item)) {
            return;
        }
        DroppedItemListener.droppedItemCache.addItem(item);
    }
}
