package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityScannerManager {

    public static EntityScannerManager entityScannerManager;

    private final NamespacedKey ENTITY_POWERS = new NamespacedKey(EnchantedMobs.instance, "powers");

    private final NamespacedKey ENTITY_LEVEL = new NamespacedKey(EnchantedMobs.instance, "level");

    private final Map<LivingEntity, List<String>> entitiesPowerCache = new ConcurrentHashMap<>();

    private final Map<LivingEntity, Integer> entitiesLevelCache = new ConcurrentHashMap<>();

    public EntityScannerManager() {
        entityScannerManager = this;
    }

    private void initEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                Integer level = initEntityLevel(entity);
                if (level == null || level < 0) {
                    continue;
                }
                entitiesLevelCache.put(entity, level);
                entitiesPowerCache.put(entity, initEntityPowers(entity));
            }
        }
    }

    private List<String> initEntityPowers(Entity entity) {
        if (!entity.getPersistentDataContainer().has(ENTITY_POWERS, PersistentDataType.STRING)) {
            return null;
        }
        String powerIDs = entity.getPersistentDataContainer().get(ENTITY_POWERS, PersistentDataType.STRING);
        if (powerIDs == null) {
            return null;
        }
        return Arrays.stream(powerIDs.split(";;")).toList();
    }

    public Integer initEntityLevel(Entity entity) {
        if (!entity.getPersistentDataContainer().has(ENTITY_LEVEL, PersistentDataType.INTEGER)) {
            return -1;
        }
        return entity.getPersistentDataContainer().get(ENTITY_LEVEL, PersistentDataType.INTEGER);
    }

    public Collection<LivingEntity> getLivingEntities() {
        if (!ConfigManager.configManager.getBoolean("optimize.enabled-entity-scanner-cache") || entitiesPowerCache.isEmpty()) {
            initEntities();
        }
        for (LivingEntity entity : entitiesPowerCache.keySet()) {
            if (entity == null || !entity.isValid() || entity.isDead()) {
                entitiesPowerCache.remove(entity);
            }
        }
        return entitiesPowerCache.keySet();
    }

    public void updateEntityCache(Entity entity) {
        if (entity instanceof LivingEntity living) {
            Integer level = initEntityLevel(living);
            if (level == null || level < 0) {
                return;
            }
            //Bukkit.getConsoleSender().sendMessage("[Debug] Add entity: " + living.getName());
            entitiesLevelCache.put(living, level);
            entitiesPowerCache.put(living, initEntityPowers(living));
        }
    }

    public void removeEntityCache(Entity entity) {
        if (entity instanceof LivingEntity living) {
            entitiesLevelCache.remove(living);
            entitiesPowerCache.remove(living);
        }
    }

    public List<String> getEntityPowerCache(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return null;
        }
        List<String> cache = entitiesPowerCache.get(living);
        if (cache != null) {
            return cache;
        }
        List<String> loaded = initEntityPowers(living);
        if (loaded != null) {
            entitiesPowerCache.put(living, loaded);
        }
        return loaded;
    }

    public int getEntityLevelCache(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return -1;
        }
        Integer cache = entitiesLevelCache.get(living);
        if (cache != null) {
            return cache;
        }
        Integer loaded = initEntityLevel(living);
        if (loaded != null && loaded >= 0) {
            entitiesLevelCache.put(living, loaded);
            return loaded;
        }
        return -1;
    }

    public void addEntityPower(Entity entity, String power) {
        List<String> powers = getEntityPowerCache(entity);
        if (powers == null) {
            powers = new ArrayList<>();
        } else {
            powers = new ArrayList<>(powers);
        }
        if (!powers.contains(power)) {
            powers.add(power);
            setEntityPowers(entity, powers, -1);
        }
    }

    public void setEntityPowers(Entity entity, List<String> powers, int level) {
        if (entity instanceof LivingEntity living) {
            PersistentDataContainer pdc = entity.getPersistentDataContainer();

            if (powers == null || powers.isEmpty()) {
                pdc.remove(ENTITY_POWERS);
                return;
            }

            String value = String.join(";;", powers);
            pdc.set(ENTITY_POWERS, PersistentDataType.STRING, value);
            if (level > 0) {
                pdc.set(ENTITY_LEVEL, PersistentDataType.INTEGER, level);
            }

            entitiesPowerCache.put(living, powers);
            if (level > 0) {
                entitiesLevelCache.put(living, level);
            }
            PowerManager.powerManager.handleSpawn(entity);
        }
    }

    public boolean containsEntityCache(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return entitiesPowerCache.containsKey(living);
        }
        return false;
    }
}
