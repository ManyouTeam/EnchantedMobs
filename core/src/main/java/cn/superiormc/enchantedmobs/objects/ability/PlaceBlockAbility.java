package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.managers.TempBlockManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class PlaceBlockAbility extends AbstractAbility {

    public PlaceBlockAbility(ConfigurationSection section) {
        super("PlaceBlock", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Material material = Material.matchMaterial(section.getString("block", "COBWEB").toUpperCase());
        if (material == null) {
            return false;
        }
        Location loc = getLocation(context);
        if (loc == null || !loc.getBlock().getType().isAir()) {
            return false;
        }
        int duration = getInt("duration", 40, context.level());
        TempBlockManager.tempBlockManager.createTempBlock(loc.getBlock().getLocation(), material, Math.max(1, duration));
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return null;
    }
}
