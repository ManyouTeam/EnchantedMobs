package cn.superiormc.enchantedmobs.managers;

import cn.gtemc.itembridge.api.ItemBridge;
import cn.gtemc.itembridge.api.util.Pair;
import cn.gtemc.itembridge.core.BukkitItemBridge;
import cn.superiormc.enchantedmobs.hooks.EMPlaceholderExpansion;
import cn.superiormc.enchantedmobs.hooks.items.*;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HookManager {

    public static HookManager hookManager;

    private Map<String, AbstractItemHook> itemHooks;

    private ItemBridge<ItemStack, Player> itemBridgeHook = null;

    public HookManager() {
        hookManager = this;
        if (ConfigManager.configManager.getString("hook-item-method").equalsIgnoreCase("DEFAULT")) {
            initItemHook();
        } else {
            itemBridgeHook = BukkitItemBridge.builder()
                    .onHookSuccess(p -> TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fUSItemBridge successfully hook into " + p + "."))
                    .detectSupportedPlugins()
                    .build();
        }
        initPlaceholders();
    }


    private void initPlaceholders() {
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            new EMPlaceholderExpansion().register();
        }
    }
    private void initItemHook() {
        itemHooks = new HashMap<>();
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            registerNewItemHook("ItemsAdder", new ItemItemsAdderHook());
        }
        if (CommonUtil.checkPluginLoad("Oraxen")) {
            registerNewItemHook("Oraxen", new ItemOraxenHook());
        }
        if (CommonUtil.checkPluginLoad("MMOItems")) {
            registerNewItemHook("MMOItems", new ItemMMOItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoItems")) {
            registerNewItemHook("EcoItems", new ItemEcoItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoArmor")) {
            registerNewItemHook("EcoArmor", new ItemEcoArmorHook());
        }
        if (CommonUtil.checkPluginLoad("MythicMobs")) {
            registerNewItemHook("MythicMobs", new ItemMythicMobsHook());
        }
        if (CommonUtil.checkPluginLoad("eco")) {
            registerNewItemHook("eco", new ItemecoHook());
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            registerNewItemHook("NeigeItems", new ItemNeigeItemsHook());
        }
        if (CommonUtil.checkPluginLoad("ExecutableItems")) {
            registerNewItemHook("ExecutableItems", new ItemExecutableItemsHook());
        }
        if (CommonUtil.checkPluginLoad("Nexo")) {
            registerNewItemHook("Nexo", new ItemNexoHook());
        }
        if (CommonUtil.checkPluginLoad("CraftEngine")) {
            registerNewItemHook("CraftEngine", new ItemCraftEngineHook());
        }
    }

    public void registerNewItemHook(String pluginName,
                                    AbstractItemHook itemHook) {
        if (!itemHooks.containsKey(pluginName)) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into " + pluginName + "...");
            itemHooks.put(pluginName, itemHook);
        }
    }

    public ItemStack getHookItem(Player player, String pluginName, String itemID) {
        if (itemBridgeHook != null) {
            Optional<ItemStack> tempVal1 = itemBridgeHook.build(pluginName, player, itemID);
            if (tempVal1.isPresent()) {
                return tempVal1.get();
            }
        }
        if (!itemHooks.containsKey(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return null;
        }
        AbstractItemHook itemHook = itemHooks.get(pluginName);
        return itemHook.getHookItemByID(player, itemID);
    }

    public String parseItemID(ItemStack hookItem, boolean useTier) {
        if (!hookItem.hasItemMeta()) {
            return hookItem.getType().name().toLowerCase();
        }
        for (AbstractItemHook itemHook : itemHooks.values()) {
            String tempVal1 = itemHook.getSimplyIDByItemStack(hookItem, useTier);
            if (tempVal1 != null) {
                return tempVal1;
            }
        }
        return hookItem.getType().name().toLowerCase();
    }

    public String[] getHookItemPluginAndID(ItemStack hookItem) {
        if (itemBridgeHook != null) {
            Pair<String, String> tempVal1 = itemBridgeHook.getFirstId(hookItem);
            if (tempVal1 != null) {
                return new String[]{tempVal1.left, tempVal1.right};
            }
        }
        for (AbstractItemHook itemHook : itemHooks.values()) {
            String itemID = itemHook.getIDByItemStack(hookItem);
            if (itemID != null) {
                return new String[]{itemHook.getPluginName(), itemHook.getIDByItemStack(hookItem)};
            }
        }
        return null;
    }

}