package cn.superiormc.enchantedmobs.paper;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.ConfigManager;
import cn.superiormc.enchantedmobs.managers.ErrorManager;
import cn.superiormc.enchantedmobs.paper.listener.PaperEntityScannerListener;
import cn.superiormc.enchantedmobs.paper.listener.PaperPlayerPowerListener;
import cn.superiormc.enchantedmobs.paper.listener.PaperTempBlockListener;
import cn.superiormc.enchantedmobs.paper.methods.BuildItemPaper;
import cn.superiormc.enchantedmobs.paper.methods.DebuildItemPaper;
import cn.superiormc.enchantedmobs.paper.utils.PaperTextUtil;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.SpecialMethodUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaperMethodUtil implements SpecialMethodUtil {

    private final Map<UUID, Map<String, BossBar>> bossBarCache = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> bossBarGenerations = new ConcurrentHashMap<>();

    @Override
    public String methodID() {
        return "paper";
    }

    @Override
    public ItemStack getItemObject(Object object) {
        if (object instanceof ItemStack) {
            ErrorManager.errorManager.sendErrorMessage("§6Warning: The item you try obtained is using legacy format!");
            return (ItemStack) object;
        }
        if (CommonUtil.getMajorVersion(15)) {
            return ItemStack.deserializeBytes((byte[]) object);
        }
        return null;
    }

    @Override
    public Object makeItemToObject(ItemStack item) {
        if (CommonUtil.getMajorVersion(15)) {
            return item.serializeAsBytes();
        }
        return item;
    }

    @Override
    public SkullMeta setSkullMeta(SkullMeta meta, String skull) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        profile.setProperty(new ProfileProperty("textures", skull));
        meta.setPlayerProfile(profile);
        return meta;
    }

    @Override
    public String serializeSkull(SkullMeta meta) {
        PlayerProfile profile = meta.getPlayerProfile();
        if (profile != null) {
            for (ProfileProperty property : profile.getProperties()) {
                if ("textures".equalsIgnoreCase(property.getName()) && property.getValue() != null && !property.getValue().isEmpty()) {
                    return property.getValue();
                }
            }
        }

        if (meta.getOwningPlayer() != null) {
            return meta.getOwningPlayer().getName();
        }
        return null;
    }

    @Override
    public void setItemName(ItemMeta meta, String name, Player player) {
        if (PaperTextUtil.containsLegacyCodes(name)) {
            name = "<!i>" + name;
        }
        meta.displayName(PaperTextUtil.modernParse(name, player));
    }

    @Override
    public void setItemItemName(ItemMeta meta, String itemName, Player player) {
        if (!itemName.isEmpty()) {
            if (PaperTextUtil.containsLegacyCodes(itemName)) {
                itemName = "<!i>" + itemName;
            }
            meta.itemName(PaperTextUtil.modernParse(itemName, player));
        } else {
            meta.itemName();
        }
    }

    @Override
    public void setItemLore(ItemMeta meta, List<String> lores, Player player) {
        List<Component> veryNewLore = new ArrayList<>();
        for (String lore : lores) {
            for (String singleLore : lore.split("\\\\n")) {
                if (PaperTextUtil.containsLegacyCodes(singleLore)) {
                    singleLore = "<!i>" + singleLore;
                }
                veryNewLore.add(PaperTextUtil.modernParse(singleLore, player));
            }
        }
        if (!veryNewLore.isEmpty()) {
            meta.lore(veryNewLore);
        }
    }

    @Override
    public void sendChat(Player player, String text) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(PaperTextUtil.modernParse(text));
        } else {
            player.sendMessage(PaperTextUtil.modernParse(text, player));
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }
        player.showTitle(Title.title(PaperTextUtil.modernParse(title, player),
                PaperTextUtil.modernParse(subTitle, player),
                Title.Times.times(Ticks.duration(fadeIn),
                        Ticks.duration(stay),
                        Ticks.duration(fadeOut))));
    }

    @Override
    public void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }
        player.sendActionBar(PaperTextUtil.modernParse(message, player));
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

        if (style != null && style.equalsIgnoreCase("SOLID")) {
            style = "PROGRESS";
        }

        String barKey = key == null || key.isEmpty() ? "temp-" + UUID.randomUUID() : key;
        Map<String, BossBar> playerBars = bossBarCache.computeIfAbsent(player.getUniqueId(), id -> new ConcurrentHashMap<>());

        BossBar bar = playerBars.get(barKey);
        if (bar == null) {
            bar = BossBar.bossBar(
                    title == null ? Component.empty() : PaperTextUtil.modernParse(title, player),
                    Math.max(0f, Math.min(1f, progress)),
                    color == null ? BossBar.Color.PINK : BossBar.Color.valueOf(color.toUpperCase()),
                    style == null ? BossBar.Overlay.PROGRESS : BossBar.Overlay.valueOf(style.toUpperCase())
            );
            playerBars.put(barKey, bar);
            player.showBossBar(bar);
        } else {
            bar.name(title == null ? Component.empty() : PaperTextUtil.modernParse(title, player));
            bar.progress(Math.max(0f, Math.min(1f, progress)));
            bar.color(color == null ? BossBar.Color.PINK : BossBar.Color.valueOf(color.toUpperCase()));
            bar.overlay(style == null ? BossBar.Overlay.PROGRESS : BossBar.Overlay.valueOf(style.toUpperCase()));
        }

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
            player.hideBossBar(finalBar);
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
            player.hideBossBar(bar);
        }
        Map<String, Long> generations = bossBarGenerations.get(player.getUniqueId());
        if (generations != null) {
            generations.remove(key);
        }
    }

    @Override
    public String legacyParse(String text) {
        if (text == null) {
            return "";
        }
        if (!ConfigManager.configManager.getBoolean("config-files.force-parse-mini-message")) {
            return TextUtil.colorize(text);
        }
        return LegacyComponentSerializer.legacySection().serialize(PaperTextUtil.modernParse(text));
    }

    @Override
    public String getItemName(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.displayName());
    }

    @Override
    public String getItemItemName(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.itemName());
    }

    @Override
    public List<String> getItemLore(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.lore());
    }

    @Override
    public ItemStack editItemStack(ItemStack item, Player player, ConfigurationSection section, int amount, String... args) {
        if (!CommonUtil.getMinorVersion(21, 6)) {
            return item;
        }
        return BuildItemPaper.editItemStack(item, player, section, amount, args);
    }

    @Override
    public ConfigurationSection serializeItemStack(ItemStack item) {
        if (!CommonUtil.getMinorVersion(21, 6)) {
            return null;
        }
        return DebuildItemPaper.serializeItemStack(item);
    }

    @Override
    public String getEntityName(LivingEntity entity) {
        if (entity.customName() != null) {
            return PaperTextUtil.changeToString(entity.customName());
        }
        return PaperTextUtil.changeToString(entity.name());
    }

    @Override
    public void setEntityName(LivingEntity entity, String name) {
        entity.customName(PaperTextUtil.modernParse(name));
    }

    @Override
    public Item dropItem(Player player, ItemStack itemStack, Location loc) {
        World world = loc.getWorld();
        if (world == null) {
            return null;
        }
        Item item = world.dropItemNaturally(loc, itemStack);
        if (CommonUtil.getMinorVersion(19, 1)) {
            item.setOwner(player.getUniqueId());
        }
        return item;
    }

    @Override
    public void tempBlockManager() {
        new PaperTempBlockListener();
    }

    @Override
    public void entityScannerManager() {
        new PaperEntityScannerListener();
    }

    @Override
    public void playerPowerManager() {
        new PaperPlayerPowerListener();
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

        if (damager instanceof LightningStrike lightning) {
            return lightning.getCausingEntity();
        }
        return null;
    }

    @Override
    public void createExplosion(Location loc,
                                       Entity entity,
                                       float yield,
                                       boolean setFire,
                                       boolean breakBlocks) {
        loc.getWorld().createExplosion(entity, loc, yield, setFire, breakBlocks);
    }
}
