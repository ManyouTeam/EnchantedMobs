package cn.superiormc.enchantedmobs.objects.ability;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Locale;

public class DamageItemAbility extends AbstractAbility {

    public DamageItemAbility(ConfigurationSection section) {
        super("DamageItem", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity entity = getTargetEntity(context);
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) {
            return false;
        }
        int amount = Math.max(0, getInt("amount", 1, context.level()));
        for (String slot : getStringList("slots")) {
            damageSlot(equipment, slot, amount);
        }
        return false;
    }

    private void damageSlot(EntityEquipment equipment, String slot, int amount) {
        switch (slot.toUpperCase(Locale.ROOT)) {
            case "MAIN_HAND" -> damage(equipment.getItemInMainHand(), amount);
            case "OFF_HAND" -> damage(equipment.getItemInOffHand(), amount);
            case "HELMET" -> damage(equipment.getHelmet(), amount);
            case "CHESTPLATE" -> damage(equipment.getChestplate(), amount);
            case "LEGGINGS" -> damage(equipment.getLeggings(), amount);
            case "BOOTS" -> damage(equipment.getBoots(), amount);
            case "ARMOR" -> {
                for (String armorSlot : List.of("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS")) {
                    damageSlot(equipment, armorSlot, amount);
                }
            }
        }
    }

    private void damage(ItemStack item, int amount) {
        if (item == null || item.getType() == Material.AIR || item.getType().getMaxDurability() <= 0) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable) || meta.isUnbreakable()) {
            return;
        }
        int max = item.getType().getMaxDurability();
        damageable.setDamage(Math.min(max, damageable.getDamage() + amount));
        item.setItemMeta(meta);
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
