package cn.superiormc.enchantedmobs.objects.power;

import cn.superiormc.enchantedmobs.managers.AbilityManager;
import cn.superiormc.enchantedmobs.managers.ConfigManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import cn.superiormc.enchantedmobs.objects.AdvancedSection;
import cn.superiormc.enchantedmobs.objects.ability.AbilityContext;
import cn.superiormc.enchantedmobs.objects.power.events.*;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import com.google.common.base.Enums;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectPower extends AdvancedSection {

    protected boolean enabled;

    private final Map<UUID, Long> cooldownMap = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> timesMap = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastAccessMap = new ConcurrentHashMap<>();

    private static final long CLEANUP_INTERVAL_MS = 30000L;
    private static final long STALE_ENTRY_MS = 30 * 60 * 1000L;

    private long lastCooldownMapCleanupTime = 0;

    public ObjectPower(String id) {
        super(id, ConfigManager.configManager.getPowerConfig(id));
        this.id = id;
        this.enabled = section.getBoolean("enabled", true);
    }

    public ObjectPower(String id, ConfigurationSection section) {
        super(id, section);
        this.id = id;
        this.enabled = section.getBoolean("enabled", true);
    }

    public boolean onProjectileHit(int level, ProjectileHitHandler handler) {
        if (!matchEntityHealthConditions("on-projectile-hit.conditions", level, handler.sourceEntity)) {
            return false;
        }
        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-projectile-hit.abilities"), context);
    }

    public boolean onShootProjectile(int level, ShootBowHandler handler) {
        if (!matchEntityHealthConditions("on-shoow-bow.conditions", level, handler.sourceEntity)) {
            return false;
        }
        applyShootBowModifier(level, handler);
        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-shoot-bow.abilities"), context);
    }

    private void applyShootBowModifier(int level, ShootBowHandler handler) {
        String type = getString("on-shoot-bow.modifier.projectile",
                getString("on-shoot-bow.modifier.projectile-type", "")).toUpperCase();
        if (type.isEmpty()) {
            return;
        }

        if (!(handler.skillEntity instanceof Projectile oldProjectile)) {
            return;
        }

        Location location = oldProjectile.getLocation();
        Entity shooter = handler.shooter;

        if (type.equals("TNT") || type.equals("PRIMED_TNT") || type.equals("TNT_PRIMED")) {
            TNTPrimed tnt = location.getWorld().spawn(location, TNTPrimed.class);
            tnt.setFuseTicks(Math.max(1, getInt("on-shoot-bow.modifier.fuse", 40, level)));
            tnt.setVelocity(oldProjectile.getVelocity().multiply(1.2));
            if (shooter instanceof LivingEntity living) {
                tnt.setSource(living);
            }
            oldProjectile.remove();
            return;
        }

        EntityType entityType = Enums.getIfPresent(EntityType.class, type).orNull();
        if (entityType == null || !entityType.isSpawnable()) {
            return;
        }

        Entity spawned = location.getWorld().spawnEntity(location, entityType);
        if (!(spawned instanceof Projectile projectile)) {
            spawned.remove();
            return;
        }

        projectile.setVelocity(oldProjectile.getVelocity());
        projectile.setGravity(oldProjectile.hasGravity());
        if (shooter instanceof LivingEntity living) {
            projectile.setShooter(living);
        }

        if (projectile instanceof Fireball fireball) {
            fireball.setYield((float) getDouble("on-shoot-bow.modifier.fireball-yield", 1.0, level));
            fireball.setIsIncendiary(getBoolean("on-shoot-bow.modifier.fireball-incendiary", true));
        }

        if (projectile instanceof ThrownPotion potion) {

            ItemStack item = new ItemStack(Material.SPLASH_POTION);
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            // 基础药水类型
            String potionTypeStr = getString("on-shoot-bow.modifier.potion-type", "");
            if (!potionTypeStr.isEmpty()) {
                PotionType potionType = Enums.getIfPresent(PotionType.class, potionTypeStr.toUpperCase()).orNull();
                if (potionType != null) {
                    meta.setBasePotionType(potionType);
                }
            }

            // 自定义药水效果
            ConfigurationSection effects = getSection("on-shoot-bow.modifier.potion-effects");
            if (effects != null) {
                for (String key : effects.getKeys(false)) {

                    PotionEffectType effectType = PotionEffectType.getByName(key.toUpperCase());
                    if (effectType == null) {
                        continue;
                    }

                    ConfigurationSection sec = effects.getConfigurationSection(key);
                    if (sec == null) {
                        continue;
                    }

                    int duration = getInt(sec.getCurrentPath() + ".potion-duration", 100, level);
                    int amplifier = getInt(sec.getCurrentPath() + ".potion.amplifier", 0, level);

                    meta.addCustomEffect(
                            new PotionEffect(effectType, duration, amplifier),
                            true
                    );
                }
            }

            item.setItemMeta(meta);
            potion.setItem(item);
        }

        if (projectile instanceof ShulkerBullet bullet) {
            bullet.setTarget(handler.targetEntity);
        }

        oldProjectile.remove();
        handler.setNewProjectile(projectile);
    }

    public boolean onProjectileTick(int level, ShootBowHandler handler) {
        if (!matchEntityHealthConditions("on-projectile-tick.conditions", level, handler.sourceEntity)) {
            return false;
        }
        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-projectile-tick.abilities"), context);
    }

    public boolean onTick(int level, TickHandler handler) {
        if (!matchEntityHealthConditions("on-tick.conditions", level, handler.sourceEntity)) {
            return false;
        }
        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-tick.abilities"), context);
    }

    public boolean onTargetTick(int level, TickTargetHandler handler) {
        if (!matchEntityHealthConditions("on-target-tick.conditions", level, handler.sourceEntity)) {
            return false;
        }
        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-target-tick.abilities"), context);
    }

    public boolean onSpawn(int level, SpawnHandler handler) {
        if (!matchEntityHealthConditions("on-spawn.conditions", level, handler.sourceEntity)) {
            return false;
        }
        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-spawn.abilities"), context);
    }

    public boolean onCombust(int level, CombustHandler handler) {
        if (getBoolean("on-combust.conditions.by-block", false) && !handler.byBlock) {
            return false;
        }
        if (getBoolean("on-combust.conditions.by-entity", false) && !handler.byEntity) {
            return false;
        }
        double minDuration = getDouble("on-combust.conditions.min-duration", 0, level);
        if (handler.originalDuration < minDuration) {
            return false;
        }

        double maxDuration = getDouble("on-combust.conditions.max-duration", Double.MAX_VALUE, level);
        if (handler.originalDuration > maxDuration) {
            return false;
        }

        if (section.contains("on-combust.modifier")) {
            handler.setNewDuration((float) getDouble("on-combust.modifier.duration", 0, level, "original", String.valueOf(handler.originalDuration)));
        }

        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-combust.abilities"), context);
    }

    public boolean onDamage(int level, DamageHandler handler) {
        if (!matchEntityHealthConditions("on-damage.conditions", level, handler.sourceEntity)) {
            return false;
        }
        if (getBoolean("on-damage.conditions.by-block", false) && !handler.byBlock) {
            return false;
        }
        if (getBoolean("on-damage.conditions.by-entity", false) && !handler.byEntity) {
            return false;
        }
        double minDamage = getDouble("on-damage.conditions.min-damage", 0, level);
        if (handler.originalDamage < minDamage) {
            return false;
        }
        double maxDamage = getDouble("on-damage.conditions.max-damage", Double.MAX_VALUE, level);
        if (handler.originalDamage > maxDamage) {
            return false;
        }
        List<String> includeCauses = getStringList("on-damage.conditions.damage-cause")
                .stream().map(String::toUpperCase).toList();
        if (!includeCauses.isEmpty() && (handler.cause == null || !includeCauses.contains(handler.cause.name().toUpperCase()))) {
            return false;
        }
        List<String> ignoreCauses = getStringList("on-damage.conditions.ignore-damage-cause")
                .stream().map(String::toUpperCase).toList();
        if (!ignoreCauses.isEmpty() && handler.cause != null && ignoreCauses.contains(handler.cause.name().toUpperCase())) {
            return false;
        }
        if (section.contains("on-damage.modifier")) {
            handler.setNewDamage(getDouble("on-damage.modifier.damage", 4, level, "original", String.valueOf(handler.originalDamage)));
        }

        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-damage.abilities"), context);
    }

    public boolean onRegain(int level, RegainHandler handler) {
        if (!matchEntityHealthConditions("on-regain.conditions", level, handler.sourceEntity)) {
            return false;
        }
        double minAmount = getDouble("on-regain.conditions.min-amount", 0, level);
        if (handler.originalAmount < minAmount) {
            return false;
        }
        double maxAmount = getDouble("on-regain.conditions.max-amount", Double.MAX_VALUE, level);
        if (handler.originalAmount > maxAmount) {
            return false;
        }
        if (section.contains("on-regain.modifier")) {
            handler.setNewAmount(getDouble("on-regain.modifier.amount", 4, level, "original", String.valueOf(handler.originalAmount)));
        }

        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-regain.abilities"), context);
    }

    public boolean onMeleeAttack(int level, MeleeAttackHandler handler) {
        if (!matchEntityHealthConditions("on-melee-attack.conditions", level, handler.sourceEntity)) {
            return false;
        }
        if (!getBoolean("on-melee-attack.conditions.accept-source", false) && !handler.isMelee) {
            return false;
        }
        double minDamage = getDouble("on-melee-attack.conditions.min-damage", 0, level);
        if (handler.originalDamage < minDamage) {
            return false;
        }
        double maxDamage = getDouble("on-melee-attack.conditions.max-damage", Double.MAX_VALUE, level);
        if (handler.originalDamage > maxDamage) {
            return false;
        }
        if (section.contains("on-melee-attack.modifier")) {
            handler.setNewDamage(getDouble("on-melee-attack.modifier.damage", 4, level, "original", String.valueOf(handler.originalDamage)));
        }

        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-melee-attack.abilities"), context);
    }

    public boolean onDeath(int level, DeathHandler handler) {
        if (section.contains("on-death.modifier")) {
            double reviveHealth = getDouble("on-death.modifier.revive-health", 20, level, "original", String.valueOf(CommonUtil.getMaxHealth(handler.sourceEntity)));
            handler.setReviveHealth(reviveHealth);
            boolean noDrops = getBoolean("on-death.modifier.no-drops", false);
            if (noDrops) {
                handler.setSetsNoDrops();
            }
        }

        AbilityContext context = new AbilityContext(this, level, handler);
        boolean cancel = AbilityManager.abilityManager.execute(section.getConfigurationSection("on-death.abilities"), context);
        return cancel || handler.cancelEvent;
    }

    public boolean onTarget(int level, TargetHandler handler) {
        if (!matchEntityHealthConditions("on-target.conditions", level, handler.sourceEntity)) {
            return false;
        }
        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-target.abilities"), context);
    }

    public boolean onExplode(int level, ExplodeHandler handler) {
        if (!matchEntityHealthConditions("on-explode.conditions", level, handler.sourceEntity)) {
            return false;
        }
        if (!matchCompareCondition("on-explode.conditions.yield", level, handler.originalYield)) {
            return false;
        }
        double minYield = getDouble("on-explode.conditions.min-yield", Double.NEGATIVE_INFINITY, level);
        if (handler.originalYield < minYield) {
            return false;
        }
        double maxYield = getDouble("on-explode.conditions.max-yield", Double.POSITIVE_INFINITY, level);
        if (handler.originalYield > maxYield) {
            return false;
        }
        if (section.contains("on-explode.modifier")) {
            handler.setNewYield((float) getDouble("on-explode.modifier.yield", 4, level, "original", String.valueOf(handler.originalYield)));
        }

        AbilityContext context = new AbilityContext(this, level, handler);
        return AbilityManager.abilityManager.execute(section.getConfigurationSection("on-explode.abilities"), context);
    }

    private boolean matchEntityHealthConditions(String basePath, int level, Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return true;
        }
        double nowHealth = living.getHealth();
        double maxHealth = CommonUtil.getMaxHealth(living);
        return matchCompareCondition(basePath + ".now-health", level, nowHealth, "health", String.valueOf(nowHealth), "max-health", String.valueOf(maxHealth))
                && matchCompareCondition(basePath + ".max-health", level, maxHealth, "health", String.valueOf(nowHealth), "max-health", String.valueOf(maxHealth));
    }

    private boolean matchCompareCondition(String path, int level, double current, String... args) {
        if (getSection(path) == null) {
            return true;
        }
        String compare = getString(path + ".compare", ">=").trim();
        double target = getDouble(path + ".value", current, level, args);
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

    public boolean willUseThisPower(Entity entity, Entity skillEntity, int level, String eventKey) {
        if (!enabled) {
            return false;
        }

        if (eventKey != null && !eventKey.isEmpty() && !section.contains(eventKey)) {
            return false;
        }

        if (skillEntity != null && PowerManager.powerManager.isUsedPower(skillEntity)) {
            return true;
        }

        long now = System.currentTimeMillis();
        if (now - lastCooldownMapCleanupTime >= CLEANUP_INTERVAL_MS) {
            cleanCooldown();
        }

        UUID uuid = entity.getUniqueId();
        lastAccessMap.put(uuid, now);

        int maxTimes = Math.max(0, getInt("limit.times", 0, level));
        if (maxTimes > 0 && timesMap.getOrDefault(uuid, 0) >= maxTimes) {
            return false;
        }
        if (cooldownMap.containsKey(uuid)) {
            long nextTime = cooldownMap.get(uuid);
            if (now < nextTime) {
                return false;
            }
        }

        double randomChance = getDouble("limit.random", 1.0, level);

        if (randomChance < 1.0 && ThreadLocalRandom.current().nextDouble() > randomChance) {
            return false;
        }

        double cooldownSeconds = getDouble("limit.cooldown", 0, level);
        if (cooldownSeconds > 0) {
            long nextUse = now + (long) (cooldownSeconds * 1000);
            cooldownMap.put(uuid, nextUse);
        }

        if (maxTimes > 0) {
            timesMap.merge(uuid, 1, Integer::sum);
        }

        return true;
    }

    public void cleanCooldown() {
        long now = System.currentTimeMillis();
        lastCooldownMapCleanupTime = now;
        cooldownMap.entrySet().removeIf(entry -> now >= entry.getValue());
        lastAccessMap.entrySet().removeIf(entry -> now - entry.getValue() >= STALE_ENTRY_MS);
        timesMap.keySet().removeIf(uuid -> !lastAccessMap.containsKey(uuid));
        cooldownMap.keySet().removeIf(uuid -> !lastAccessMap.containsKey(uuid));
    }

    public String getPlaceholder() {
        return section.getString("placeholder", id);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
