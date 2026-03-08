package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.managers.EntityScannerManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import com.google.common.base.Enums;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class SummonAbility extends AbstractAbility {

    public SummonAbility(ConfigurationSection section) {
        super("Summon", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Location location = getLocation(context);
        if (location == null || location.getWorld() == null) {
            return false;
        }

        String typeString = section.getString("entity", section.getString("entity-type", ""));

        EntityType type = Enums.getIfPresent(EntityType.class, typeString.toUpperCase()).orNull();
        if (type == null || !type.isSpawnable()) {
            return false;
        }

        Entity spawned = location.getWorld().spawnEntity(location, type);
        if (!(spawned instanceof LivingEntity living)) {
            return false;
        }

        applyLivingStats(context, living);
        if (living instanceof Creeper creeper) {
            applyCreeperStats(context, creeper);
        }
        applyPowers(context, living);

        return false;
    }

    private void applyLivingStats(AbilityContext context, LivingEntity living) {
        double maxHealth = getDouble("max-health", -1, context.level());
        if (maxHealth > 0) {
            AttributeInstance maxHealthAttr = living.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealthAttr != null) {
                maxHealthAttr.setBaseValue(maxHealth);
                living.setHealth(Math.min(maxHealth, Math.max(0.1, maxHealth)));
            }
        }

        double health = getDouble("health", -1, context.level());
        if (health > 0) {
            double cap = maxHealth > 0 ? maxHealth : CommonUtil.getMaxHealth(living);
            living.setHealth(Math.min(cap, Math.max(0.1, health)));
        }

        double attack = getDouble("attack-damage", -1, context.level());
        if (attack >= 0) {
            AttributeInstance attackAttr = living.getAttribute(Attribute.ATTACK_DAMAGE);
            if (attackAttr != null) {
                attackAttr.setBaseValue(attack);
            }
        }
    }

    private void applyPowers(AbilityContext context, LivingEntity living) {
        int level = Math.max(1, getInt("power-level", getInt("power.level", context.level(), context.level()), context.level()));

        List<String> configuredPowers = section.getStringList("powers");
        if (configuredPowers.isEmpty()) {
            configuredPowers = section.getStringList("power.list");
        }
        String singlePower = section.getString("power", "").trim();
        if (!singlePower.isEmpty()) {
            configuredPowers = new ArrayList<>(configuredPowers);
            configuredPowers.add(singlePower);
        }

        List<String> validPowers = configuredPowers.stream()
                .filter(id -> PowerManager.powerManager.getPowers().containsKey(id))
                .distinct()
                .toList();
        if (!validPowers.isEmpty()) {
            EntityScannerManager.entityScannerManager.setEntityPowers(living, validPowers, level);
            return;
        }

        boolean randomByLevel = getBoolean("random-power-by-level",
                getBoolean("power.random-by-level", false));
        if (randomByLevel) {
            PowerManager.powerManager.assignRandomPowersByLevel(level, living);
        }
    }

    private void applyCreeperStats(AbilityContext context, Creeper creeper) {
        float explosionRadius = (float) getDouble("creeper.explosion-radius", -1, context.level());
        if (explosionRadius >= 0) {
            creeper.setExplosionRadius(Math.max(0, Math.round(explosionRadius)));
        }

        int maxFuseTicks = getInt("creeper.fuse-ticks", -1, context.level());
        if (maxFuseTicks >= 0) {
            creeper.setMaxFuseTicks(Math.max(1, maxFuseTicks));
        }

        if (section.contains("creeper.powered")) {
            creeper.setPowered(getBoolean("creeper.powered", false));
        }
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}