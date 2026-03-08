package cn.superiormc.enchantedmobs.utils;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public interface SpecialMethodUtil {

    String methodID();

    ItemStack getItemObject(Object object);

    Object makeItemToObject(ItemStack item);

    SkullMeta setSkullMeta(SkullMeta meta, String skull);

    void setItemName(ItemMeta meta, String name, Player player);

    void setItemItemName(ItemMeta meta, String itemName, Player player);

    void setItemLore(ItemMeta meta, List<String> lore, Player player);

    void sendChat(Player player, String text);

    void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

    void sendActionBar(Player player, String message);

    void sendBossBar(Player player,
                     String title,
                     float progress,
                     String color,
                     String style,
                     String key);

    void hideBossBar(Player player, String key);

    String legacyParse(String text);

    String getItemName(ItemMeta meta);

    String getItemItemName(ItemMeta meta);

    List<String> getItemLore(ItemMeta meta);

    String getEntityName(LivingEntity entity);

    void setEntityName(LivingEntity entity, String name);

    ItemStack editItemStack(ItemStack item,
                            Player player,
                            ConfigurationSection section,
                            int amount,
                            String... args);

    ConfigurationSection serializeItemStack(ItemStack item);

    void dropPrivateItem(Player player, ItemStack itemStack, Location loc);

    void tempBlockManager();

    void entityScannerManager();

    void playerPowerManager();

    Entity getDamager(Entity damager);

    void createExplosion(Location loc,
                                       Entity entity,
                                       float yield,
                                       boolean setFire,
                                       boolean breakBlocks);
}
