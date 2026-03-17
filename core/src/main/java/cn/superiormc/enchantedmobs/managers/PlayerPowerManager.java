package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.MathUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class PlayerPowerManager implements Listener {

    public static PlayerPowerManager playerPowerManager;

    private static final int STORAGE_START = 0;
    private static final int STORAGE_END = 35;
    private static final int ARMOR_START = 36;
    private static final int ARMOR_END = 39;
    private static final int OFFHAND_SLOT = 40;
    private static final int BACKPACK_SLOT_COUNT = 36;
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[^%]+%");

    private final Map<UUID, Integer> playerPower = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerCache> playerCaches = new ConcurrentHashMap<>();
    private final List<PowerStatRule> rules = new ArrayList<>();

    private String formula = "{equipment_sum} + ({backpack_max} + {backpack_avg}) / 2";
    private boolean incrementalUpdateEnabled = true;
    private boolean formulaUsesPlaceholders = false;

    protected PlayerPowerManager() {
        playerPowerManager = this;
        loadRules();
    }

    public void reload() {
        loadRules();
        playerPower.clear();
        playerCaches.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            initPlayer(player);
        }
    }

    private void loadRules() {
        rules.clear();
        File file = new File(EnchantedMobs.instance.getDataFolder(), "player-power.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        formula = config.getString("formula", "{equipment_sum} + ({backpack_max} + {backpack_avg}) / 2");
        formulaUsesPlaceholders = PLACEHOLDER_PATTERN.matcher(formula).find();
        incrementalUpdateEnabled = config.getBoolean("incremental-slot-update", true);

        ConfigurationSection section = config.getConfigurationSection("rules");
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection ruleSection = section.getConfigurationSection(key);
            if (ruleSection == null) {
                continue;
            }
            int addWeight = ruleSection.getInt("add-weight", 0);
            ConfigurationSection matchItem = ruleSection.getConfigurationSection("match-item");
            if (matchItem == null) {
                continue;
            }
            rules.add(new PowerStatRule(matchItem, addWeight));
        }
    }

    public int getPlayerPower(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerCache cache = playerCaches.get(uuid);
        if (cache == null) {
            return playerPower.getOrDefault(uuid, 0);
        }

        boolean papiEnabled = CommonUtil.checkPluginLoad("PlaceholderAPI");
        if (!formulaUsesPlaceholders || !Bukkit.isPrimaryThread() || !papiEnabled) {
            return playerPower.compute(uuid, (ignored, oldValue) -> evaluateFormula(null, cache, oldValue == null ? 0 : oldValue));
        }

        int value = evaluateFormula(player, cache, playerPower.getOrDefault(uuid, 0));
        playerPower.put(uuid, value);
        return value;
    }

    public void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack[] contents = cloneContents(player);

        Bukkit.getScheduler().runTaskAsynchronously(EnchantedMobs.instance, () -> {
            PlayerCache cache = new PlayerCache();
            for (int slot = 0; slot < contents.length; slot++) {
                int weight = computeWeight(contents[slot]);
                cache.slotWeights.put(slot, weight);
                if (isBackpackSlot(slot)) {
                    cache.backpackSum += weight;
                    cache.backpackWeightCounts.merge(weight, 1, Integer::sum);
                } else if (isEquipmentSlot(slot)) {
                    cache.equipmentSum += weight;
                }
            }

            Bukkit.getScheduler().runTask(EnchantedMobs.instance, () -> {
                playerCaches.put(uuid, cache);
                playerPower.put(uuid, evaluateFormula(player, cache, playerPower.getOrDefault(uuid, 0)));
            });
        });
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        playerCaches.remove(uuid);
        playerPower.remove(uuid);
    }

    public void updateChangedSlots(Player player, Set<Integer> changedSlots) {
        if (changedSlots.isEmpty()) {
            return;
        }
        if (!incrementalUpdateEnabled) {
            initPlayer(player);
            return;
        }

        UUID uuid = player.getUniqueId();
        Map<Integer, ItemStack> slotItems = new HashMap<>();
        for (int slot : changedSlots) {
            slotItems.put(slot, cloneItem(player.getInventory().getItem(slot)));
        }

        Bukkit.getScheduler().runTaskAsynchronously(EnchantedMobs.instance, () -> {
            Map<Integer, Integer> newWeights = new HashMap<>();
            for (Map.Entry<Integer, ItemStack> entry : slotItems.entrySet()) {
                newWeights.put(entry.getKey(), computeWeight(entry.getValue()));
            }

            Bukkit.getScheduler().runTask(EnchantedMobs.instance, () -> {
                PlayerCache current = playerCaches.computeIfAbsent(uuid, k -> new PlayerCache());

                for (Map.Entry<Integer, Integer> entry : newWeights.entrySet()) {
                    int slot = entry.getKey();
                    int newWeight = entry.getValue();
                    int oldWeight = current.slotWeights.getOrDefault(slot, 0);
                    if (oldWeight == newWeight) {
                        continue;
                    }

                    current.slotWeights.put(slot, newWeight);

                    if (isBackpackSlot(slot)) {
                        current.backpackSum += newWeight - oldWeight;
                        decrementWeightCount(current.backpackWeightCounts, oldWeight);
                        current.backpackWeightCounts.merge(newWeight, 1, Integer::sum);
                    } else if (isEquipmentSlot(slot)) {
                        current.equipmentSum += newWeight - oldWeight;
                    }
                }

                playerPower.put(uuid, evaluateFormula(player, current, playerPower.getOrDefault(uuid, 0)));
            });
        });
    }

    private void decrementWeightCount(Map<Integer, Integer> weightCounts, int weight) {
        Integer count = weightCounts.get(weight);
        if (count == null) {
            return;
        }
        if (count <= 1) {
            weightCounts.remove(weight);
            return;
        }
        weightCounts.put(weight, count - 1);
    }

    private int evaluateFormula(Player player, PlayerCache cache, int fallbackValue) {
        int equipmentSum = cache.equipmentSum;
        int backpackSum = cache.backpackSum;
        int backpackMax = cache.getBackpackMax();
        double backpackAvg = (double) backpackSum / BACKPACK_SLOT_COUNT;
        boolean papiEnabled = CommonUtil.checkPluginLoad("PlaceholderAPI");
        boolean canResolvePlaceholders = player != null
                && formulaUsesPlaceholders
                && Bukkit.isPrimaryThread()
                && papiEnabled;
        String expression = formula
                .replace("{equipment_sum}", String.valueOf(equipmentSum))
                .replace("{backpack_sum}", String.valueOf(backpackSum))
                .replace("{backpack_max}", String.valueOf(backpackMax))
                .replace("{backpack_avg}", String.valueOf(backpackAvg))
                .replace("{backpack_count}", String.valueOf(BACKPACK_SLOT_COUNT));

        if (canResolvePlaceholders) {
            expression = PlaceholderAPI.setPlaceholders(player, expression);
        }

        if (PLACEHOLDER_PATTERN.matcher(expression).find()) {
            if (!papiEnabled || canResolvePlaceholders) {
                expression = PLACEHOLDER_PATTERN.matcher(expression).replaceAll("0");
            } else {
                return fallbackValue;
            }
        }
        return Math.max(0, (int) Math.round(MathUtil.doCalculate(expression)));
    }

    private boolean isBackpackSlot(int slot) {
        return slot >= STORAGE_START && slot <= STORAGE_END;
    }

    private boolean isEquipmentSlot(int slot) {
        return (slot >= ARMOR_START && slot <= ARMOR_END) || slot == OFFHAND_SLOT;
    }

    private ItemStack[] cloneContents(Player player) {
        ItemStack[] source = player.getInventory().getContents();
        ItemStack[] copy = new ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = cloneItem(source[i]);
        }
        return copy;
    }

    private ItemStack cloneItem(ItemStack item) {
        return item == null ? null : item.clone();
    }

    private int computeWeight(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return 0;
        }
        int total = 0;
        for (PowerStatRule rule : rules) {
            if (MatchItemManager.matchItemManager.getMatch(rule.matchItem(), item)) {
                total += rule.addWeight();
            }
        }
        return total;
    }

    private static class PlayerCache {
        private final Map<Integer, Integer> slotWeights = new HashMap<>();
        private final NavigableMap<Integer, Integer> backpackWeightCounts = new TreeMap<>();
        private int equipmentSum = 0;
        private int backpackSum = 0;

        private int getBackpackMax() {
            if (backpackWeightCounts.isEmpty()) {
                return 0;
            }
            return backpackWeightCounts.lastKey();
        }
    }

    private record PowerStatRule(ConfigurationSection matchItem, int addWeight) {}
}
