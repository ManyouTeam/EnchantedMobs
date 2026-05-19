package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.managers.AbilityManager;
import cn.superiormc.enchantedmobs.managers.MatchEntityManager;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.MathUtil;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConditionalAbility extends AbstractAbility {

    public ConditionalAbility(ConfigurationSection section) {
        super("Conditional", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        ConfigurationSection cases = section.getConfigurationSection("cases");
        if (cases != null) {
            for (String key : cases.getKeys(false)) {
                ConfigurationSection single = cases.getConfigurationSection(key);
                if (single == null || !matches(single, context)) {
                    continue;
                }
                return AbilityManager.abilityManager.execute(single.getConfigurationSection("abilities"), context);
            }
        }
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("default.abilities"), context);
    }

    private boolean matches(ConfigurationSection single, AbilityContext context) {
        Entity conditionTarget = getConditionTarget(single, context);
        if (!matchesEntity(single.getConfigurationSection("match-entity"), conditionTarget)) {
            return false;
        }

        if (!matchesHealth(single, "now-health", conditionTarget)) {
            return false;
        }
        if (!matchesHealth(single, "max-health", conditionTarget)) {
            return false;
        }
        if (!matchesDistance(single, context)) {
            return false;
        }
        if (!matchesPotionList(conditionTarget, single, "target-has-potion", true)) {
            return false;
        }
        if (!matchesPotionList(conditionTarget, single, "target-missing-potion", false)) {
            return false;
        }
        if (!matchesPotionList(conditionTarget, single, "target-not-has-potion", false)) {
            return false;
        }
        double random = single.getDouble("random", 1.0D);
        return random >= 1.0D || ThreadLocalRandom.current().nextDouble() <= random;
    }

    private boolean matchesDistance(ConfigurationSection single, AbilityContext context) {
        double distance = getDistance(context);
        double minDistance = single.getDouble("min-distance", Double.NEGATIVE_INFINITY);
        double maxDistance = single.getDouble("max-distance", Double.POSITIVE_INFINITY);
        if (distance < minDistance || distance > maxDistance) {
            return false;
        }
        return matchesCompareCondition(single.getConfigurationSection("distance"), distance, context);
    }

    private double getDistance(AbilityContext context) {
        Entity source = context.handler().sourceEntity;
        Entity target = context.handler().targetEntity;
        if (source == null || target == null || !source.getWorld().equals(target.getWorld())) {
            return Double.MAX_VALUE;
        }
        return source.getLocation().distance(target.getLocation());
    }

    private Entity getConditionTarget(ConfigurationSection single, AbilityContext context) {
        String targetType = single.getString("condition-target", "TARGET").toUpperCase();
        return switch (targetType) {
            case "SOURCE" -> context.handler().sourceEntity;
            case "SKILL" -> context.handler().skillEntity;
            default -> context.handler().targetEntity;
        };
    }

    private boolean matchesEntity(ConfigurationSection matchSection, Entity entity) {
        if (matchSection == null) {
            return true;
        }
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        return MatchEntityManager.matchEntityManager.getMatch(matchSection, living);
    }

    private boolean matchesHealth(ConfigurationSection single, String key, Entity entity) {
        ConfigurationSection healthSection = single.getConfigurationSection(key);
        if (healthSection == null) {
            return true;
        }
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        double value = key.equals("max-health") ? CommonUtil.getMaxHealth(living) : living.getHealth();
        return matchesCompareCondition(healthSection, value, null);
    }

    private boolean matchesCompareCondition(ConfigurationSection compareSection, double current, AbilityContext context) {
        if (compareSection == null) {
            return true;
        }
        String compare = compareSection.getString("compare", ">=").trim();
        double target = compareSection.getDouble("value", current);
        if (compareSection.isString("value") && context != null) {
            String formula = compareSection.getString("value", String.valueOf(current));
            formula = CommonUtil.modifyString(null, formula, "level", String.valueOf(context.level()));
            target = MathUtil.doCalculate(formula);
        }
        return compareDouble(current, target, compare);
    }

    private boolean compareDouble(double left, double right, String compare) {
        return switch (compare) {
            case ">", "gt" -> left > right;
            case ">=", "=>", "gte" -> left >= right;
            case "<", "lt" -> left < right;
            case "<=", "=<", "lte" -> left <= right;
            case "!=", "<>", "ne" -> left != right;
            case "=", "==", "eq" -> left == right;
            default -> left >= right;
        };
    }

    private boolean matchesPotionList(Entity entity, ConfigurationSection single, String path, boolean shouldHave) {
        if (!single.contains(path)) {
            return true;
        }
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        for (String potion : getPotionKeys(single, path)) {
            PotionEffectType type = getPotionEffectType(potion);
            if (type == null) {
                continue;
            }
            boolean hasPotion = living.hasPotionEffect(type);
            if (shouldHave == hasPotion) {
                return true;
            }
        }
        return false;
    }

    private List<String> getPotionKeys(ConfigurationSection single, String path) {
        List<String> potions = new ArrayList<>(single.getStringList(path));
        String singlePotion = single.getString(path, "");
        if (potions.isEmpty() && singlePotion != null && !singlePotion.isBlank()) {
            potions.add(singlePotion);
        }
        return potions;
    }

    private PotionEffectType getPotionEffectType(String potionKey) {
        PotionEffectType type = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(potionKey));
        if (type != null || potionKey == null) {
            return type;
        }
        return PotionEffectType.getByName(potionKey.toUpperCase(Locale.ROOT));
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return null;
    }
}
