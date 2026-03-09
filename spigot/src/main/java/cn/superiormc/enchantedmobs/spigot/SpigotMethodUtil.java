package cn.superiormc.enchantedmobs.spigot;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.ConfigManager;
import cn.superiormc.enchantedmobs.managers.ErrorManager;
import cn.superiormc.enchantedmobs.spigot.listener.SpigotEntityScannerListener;
import cn.superiormc.enchantedmobs.spigot.listener.SpigotPlayerPowerListener;
import cn.superiormc.enchantedmobs.spigot.listener.SpigotTempBlockListener;
import cn.superiormc.enchantedmobs.utils.SpecialMethodUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotMethodUtil implements SpecialMethodUtil {

    private final Map<UUID, Map<String, BossBar>> bossBarCache = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> bossBarGenerations = new ConcurrentHashMap<>();

    @Override
    public String methodID() {
        return "spigot";
    }

    @Override
    public ItemStack getItemObject(Object object) {
        if (object instanceof ItemStack) {
            return (ItemStack) object;
        }
        return null;
    }

    @Override
    public Object makeItemToObject(ItemStack item) {
        return item;
    }

    @Override
    public SkullMeta setSkullMeta(SkullMeta meta, String skull) {
        if (EnchantedMobs.newSkullMethod) {
            try {
                Class<?> profileClass = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
                Constructor<?> constroctor = profileClass.getConstructor(GameProfile.class);
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", skull));
                try {
                    Method mtd = meta.getClass().getDeclaredMethod("setProfile", profileClass);
                    mtd.setAccessible(true);
                    mtd.invoke(meta, constroctor.newInstance(profile));
                } catch (Exception exception) {
                    exception.printStackTrace();
                    ErrorManager.errorManager.sendErrorMessage("§cError: Can not parse skull texture in a item!");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", skull));
            try {
                Method mtd = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(meta, profile);
            } catch (Exception exception) {
                exception.printStackTrace();
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not parse skull texture in a item!");
            }
        }
        return meta;
    }

    @Override
    public void setItemName(ItemMeta meta, String name, Player player) {
        meta.setDisplayName(TextUtil.parse(name, player));
    }

    @Override
    public void setItemItemName(ItemMeta meta, String itemName, Player player) {
        if (itemName.isEmpty()) {
            meta.setItemName(" ");
        } else {
            meta.setItemName(TextUtil.parse(itemName, player));
        }
    }

    @Override
    public void setItemLore(ItemMeta meta, List<String> lores, Player player) {
        List<String> newLore = new ArrayList<>();
        for (String lore : lores) {
            for (String singleLore : lore.split("\\\\n")) {
                if (singleLore.isEmpty()) {
                    newLore.add(" ");
                    continue;
                }
                newLore.add(TextUtil.parse(singleLore, player));
            }
        }
        if (!newLore.isEmpty()) {
            meta.setLore(newLore);
        }
    }

    @Override
    public void sendChat(Player player, String text) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.parse(text));
        } else {
            player.sendMessage(TextUtil.parse(text, player));
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }
        player.sendTitle(TextUtil.parse(title, player), TextUtil.parse(subTitle, player), fadeIn, stay, fadeOut);
    }

    @Override
    public void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }
        player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(TextUtil.parse(message, player))
        );
    }

    @Override
    public void sendBossBar(Player player,
                            String title,
                            float progress,
                            String color,
                            String style,
                            String key) {
        if (player == null) {
            return;
        }

        String barKey = key == null || key.isEmpty() ? "temp-" + UUID.randomUUID() : key;
        Map<String, BossBar> playerBars = bossBarCache.computeIfAbsent(player.getUniqueId(), id -> new ConcurrentHashMap<>());

        BossBar bar = playerBars.get(barKey);
        if (bar == null) {
            bar = Bukkit.createBossBar(
                    title,
                    color == null ? BarColor.WHITE : BarColor.valueOf(color.toUpperCase()),
                    style == null ? BarStyle.SOLID : BarStyle.valueOf(style.toUpperCase())
            );
            bar.addPlayer(player);
            bar.setVisible(true);
            playerBars.put(barKey, bar);
        } else {
            bar.setTitle(title);
            bar.setColor(color == null ? BarColor.WHITE : BarColor.valueOf(color.toUpperCase()));
            bar.setStyle(style == null ? BarStyle.SOLID : BarStyle.valueOf(style.toUpperCase()));
        }

        bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));

        Map<String, Long> generations = bossBarGenerations.computeIfAbsent(player.getUniqueId(), id -> new ConcurrentHashMap<>());
        long keepTicks = Math.max(20L, ConfigManager.configManager.getInt("mob-display.bossbar.keep-ticks", 60));
        long generation = generations.getOrDefault(barKey, 0L) + 1L;
        generations.put(barKey, generation);

        BossBar finalBar = bar;
        long finalGeneration = generation;
        Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, () -> {
            Long currentGeneration = generations.get(barKey);
            if (currentGeneration != null && currentGeneration != finalGeneration) {
                return;
            }
            finalBar.removeAll();
            playerBars.remove(barKey);
            generations.remove(barKey);
        }, keepTicks);
    }

    @Override
    public void hideBossBar(Player player, String key) {
        if (player == null || key == null || key.isEmpty()) {
            return;
        }
        Map<String, BossBar> playerBars = bossBarCache.get(player.getUniqueId());
        if (playerBars == null) {
            return;
        }
        BossBar bar = playerBars.remove(key);
        if (bar != null) {
            bar.removeAll();
        }
        Map<String, Long> generations = bossBarGenerations.get(player.getUniqueId());
        if (generations != null) {
            generations.remove(key);
        }
    }

    @Override
    public String legacyParse(String text) {
        if (text == null)
            return "";
        return TextUtil.colorize(text);
    }

    @Override
    public String getItemName(ItemMeta meta) {
        return meta.getDisplayName();
    }

    @Override
    public String getItemItemName(ItemMeta meta) {
        return meta.getItemName();
    }

    @Override
    public List<String> getItemLore(ItemMeta meta) {
        return meta.getLore();
    }

    @Override
    public ItemStack editItemStack(ItemStack item, Player player, ConfigurationSection section, int amount, String... args) {
        return item;
    }

    @Override
    public ConfigurationSection serializeItemStack(ItemStack item) {
        return null;
    }

    @Override
    public Item dropItem(Player player, ItemStack itemStack, Location loc) {
        World world = loc.getWorld();
        if (world == null) {
            return null;
        }
        return world.dropItemNaturally(loc, itemStack);
    }

    @Override
    public String getEntityName(LivingEntity entity) {
        if (entity.getCustomName() != null) {
            return entity.getCustomName();
        }
        return entity.getName();
    }

    @Override
    public void setEntityName(LivingEntity entity, String name) {
        entity.setCustomName(TextUtil.parse(name));
    }

    @Override
    public void tempBlockManager() {
        new SpigotTempBlockListener();
    }

    @Override
    public void entityScannerManager() {
        new SpigotEntityScannerListener();
    }

    @Override
    public void playerPowerManager() {
        new SpigotPlayerPowerListener();
    }

    @Override
    public Entity getDamager(Entity damager) {
        if (damager instanceof Player) {
            return damager;
        }

        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Entity entity) {
                return entity;
            }
        }

        if (damager instanceof TNTPrimed tnt) {
            return tnt.getSource();
        }

        if (damager instanceof AreaEffectCloud cloud) {
            ProjectileSource source = cloud.getSource();
            if (source instanceof Entity entity) {
                return entity;
            }
        }

        if (damager instanceof Tameable tameable) {
            AnimalTamer owner = tameable.getOwner();
            if (owner instanceof Entity entity) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void createExplosion(Location loc,
                                       Entity entity,
                                       float yield,
                                       boolean setFire,
                                       boolean breakBlocks) {
        loc.getWorld().createExplosion(loc, yield, setFire, breakBlocks);
    }
}
