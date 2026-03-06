package cn.superiormc.enchantedmobs.objects.ability;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DisarmAbility extends AbstractAbility {

    public DisarmAbility(ConfigurationSection section) {
        super("Disarm", section);
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

        String slot = getString("slot", "MAIN_HAND").toUpperCase();
        boolean drop = getBoolean("drop", true);
        ItemStack item;

        switch (slot) {
            case "OFF_HAND" -> {
                ItemStack offHand = equipment.getItemInOffHand();
                if (isEmpty(offHand)) {
                    return false;
                }
                item = offHand.clone();
                equipment.setItemInOffHand(new ItemStack(Material.AIR));
                if (!(equipment instanceof PlayerInventory)) {
                    equipment.setItemInOffHandDropChance(0f);
                }
            }
            default -> {
                ItemStack mainHand = equipment.getItemInMainHand();
                if (isEmpty(mainHand)) {
                    return false;
                }
                item = mainHand.clone();
                equipment.setItemInMainHand(new ItemStack(Material.AIR));
                if (!(equipment instanceof PlayerInventory)) {
                    equipment.setItemInMainHandDropChance(0f);
                }
            }
        }

        if (drop) {
            Item dropped = living.getWorld().dropItemNaturally(living.getLocation(), item);
            dropped.setPickupDelay(Math.max(0, getInt("pickup-delay", 20, context.level())));
        }

        return false;
    }

    private boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
