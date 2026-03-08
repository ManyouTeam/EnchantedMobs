package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.objects.power.ObjectPower;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.objects.power.events.*;
import cn.superiormc.enchantedmobs.utils.DebugUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class PowerManager {

    public static PowerManager powerManager;

    private final NamespacedKey USED_POWER = new NamespacedKey(EnchantedMobs.instance, "used_power");

    private final Map<String, ObjectPower> powers = new HashMap<>();

    private BukkitTask task;

    private BukkitTask bossBarTask;

    private final Map<UUID, Set<String>> playerMobBossBars = new HashMap<>();

    public PowerManager() {
        powerManager = this;
        initPowers();
        initTask();
    }

    private void initPowers() {
        for (Map.Entry<String, ConfigurationSection> entry : ConfigManager.configManager.powerConfigs.entrySet()) {
            registerNewPower(entry.getKey(), new ObjectPower(entry.getKey(), entry.getValue()));
        }
    }

    private void initTask() {
        this.task = Bukkit.getScheduler().runTaskTimer(EnchantedMobs.instance, () -> {
            //Bukkit.getConsoleSender().sendMessage("[Debug] 1");
            for (LivingEntity entity : EntityScannerManager.entityScannerManager.getLivingEntities()) {
                if (!(entity instanceof Monster monster)) {
                    continue;
                }
                //Bukkit.getConsoleSender().sendMessage("[Debug] 2");
                int level = EntityScannerManager.entityScannerManager.getEntityLevelCache(entity);
                if (level < 0) {
                    continue;
                }
                TickHandler handler = new TickHandler(entity);
                forEachActivePower(entity, null, "on-tick", (power, l) -> power.onTick(l, handler), null);
                //Bukkit.getConsoleSender().sendMessage("[Debug] Tick entity: " + monster.getName());
                if (monster.getTarget() != null) {
                    //Bukkit.getConsoleSender().sendMessage("[Debug] Target entity: " + monster.getName());
                    TickTargetHandler tickTargetHandler = new TickTargetHandler(monster);
                    forEachActivePower(entity, null, "on-target-tick", (power, l) -> power.onTargetTick(l, tickTargetHandler), null);
                }
            }
        }, 1L, 1L);
        this.bossBarTask = Bukkit.getScheduler().runTaskTimer(EnchantedMobs.instance, this::updateBossBars, 20L, 20L);
    }

    private void updateBossBars() {
        int minPowers = ConfigManager.configManager.getInt("mob-display.bossbar.min-powers", 2);
        double radius = ConfigManager.configManager.getInt("mob-display.bossbar.radius", 16);
        String title = ConfigManager.configManager.getString("mob-display.bossbar.title", "&c{entity} &7[{health}/{max-health}] &f{powers_full}");
        String color = ConfigManager.configManager.getString("mob-display.bossbar.color", "RED");
        String style = ConfigManager.configManager.getString("mob-display.bossbar.style", "SOLID");

        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<String> visibleKeys = new HashSet<>();
            if (player.isDead()) {
                hideStaleMobBossBars(player, visibleKeys);
                continue;
            }
            for (Entity nearby : player.getNearbyEntities(radius, radius, radius)) {
                if (!(nearby instanceof Monster living) || living.isDead()) {
                    continue;
                }
                List<String> powerIDs = EntityScannerManager.entityScannerManager.getEntityPowerCache(nearby);
                if (powerIDs == null || powerIDs.size() < minPowers) {
                    continue;
                }
                String bossBarKey = "mob-" + nearby.getUniqueId();
                visibleKeys.add(bossBarKey);

                int level = Math.max(1, EntityScannerManager.entityScannerManager.getEntityLevelCache(nearby));
                String powersFull = String.join(separatorFromConfig(), getPowerPlaceholders(powerIDs, level, player));
                double maxHealth = CommonUtil.getMaxHealth(living);
                if (maxHealth <= 0) {
                    continue;
                }
                float progress = (float) Math.max(0, Math.min(1, living.getHealth() / maxHealth));

                String parsed = title
                        .replace("{entity}", getBossBarEntityName(living, player))
                        .replace("{mob}", EnchantedMobs.methodUtil.getEntityName(living))
                        .replace("{health}", String.format(Locale.US, "%.1f", living.getHealth()))
                        .replace("{max-health}", String.format(Locale.US, "%.1f", maxHealth))
                        .replace("{powers_full}", powersFull);

                EnchantedMobs.methodUtil.sendBossBar(player, parsed, progress, color, style, bossBarKey);
            }
            hideStaleMobBossBars(player, visibleKeys);
        }
    }

    private String getBossBarEntityName(LivingEntity entity, Player player) {
        String langKey = "override-lang.entity." + entity.getType().name().toLowerCase(Locale.ROOT);
        return LanguageManager.languageManager.getStringText(player, langKey, entity.getType().name());
    }

    private void hideStaleMobBossBars(Player player, Set<String> visibleKeys) {
        Set<String> trackedKeys = playerMobBossBars.computeIfAbsent(player.getUniqueId(), id -> new HashSet<>());
        Set<String> staleKeys = new HashSet<>(trackedKeys);
        staleKeys.removeAll(visibleKeys);

        for (String staleKey : staleKeys) {
            EnchantedMobs.methodUtil.hideBossBar(player, staleKey);
        }

        trackedKeys.clear();
        trackedKeys.addAll(visibleKeys);
    }

    public void cancelTask() {
        task.cancel();
        bossBarTask.cancel();
    }

    public void registerNewPower(String id, ObjectPower power) {
        powers.put(id, power);
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded power: " + id + "!");
    }

    public int getNearbyAveragePlayerPower(Location location, double range) {
        if (location.getWorld() == null) {
            return -1;
        }
        return getNearbyAveragePlayerPower(location.getWorld(), location.getX(), location.getZ(), range);
    }

    private int getNearbyAveragePlayerPower(World world, double centerX, double centerZ, double range) {
        double validRange = Math.max(0, range);
        double rangeSquared = validRange * validRange;
        int total = 0;
        int count = 0;
        for (Player player : world.getPlayers()) {
            double dx = player.getLocation().getX() - centerX;
            double dz = player.getLocation().getZ() - centerZ;
            if ((dx * dx + dz * dz) <= rangeSquared) {
                total += PlayerPowerManager.playerPowerManager.getPlayerPower(player);
                count++;
            }
        }
        if (count == 0) {
            return -1;
        }
        return Math.max(1, total / count);
    }

    public boolean shouldAssignPowerForSpawn() {
        String rawChance = ConfigManager.configManager.getString("mob-power-generator.spawn-chance", "100");
        double chance = parseChance(rawChance);
        return Math.random() <= chance;
    }

    private double parseChance(String rawChance) {
        if (rawChance == null || rawChance.isEmpty()) {
            return 1.0;
        }
        double parsed = parseLevelValue(rawChance, 100);
        if (parsed > 1.0) {
            parsed = parsed / 100.0;
        }
        return Math.max(0.0, Math.min(1.0, parsed));
    }

    public List<String> assignRandomPowersByLevel(int level, LivingEntity entity) {
        if (entity == null || level <= 0) {
            return Collections.emptyList();
        }
        if (level > ConfigManager.configManager.getInt("mob-power-generator.max-level", 400)) {
            level = ConfigManager.configManager.getInt("mob-power-generator.max-level", 400);
        }
        List<String> tempVal1 = EntityScannerManager.entityScannerManager.getEntityPowerCache(entity);
        if (tempVal1 != null) {
            return tempVal1;
        }

        Map<String, List<CandidatePower>> groupedCandidates = new HashMap<>();
        Map<String, Boolean> uniqueGroupMap = new HashMap<>();

        for (Map.Entry<String, ObjectPower> entry : powers.entrySet()) {
            ObjectPower power = entry.getValue();
            if (!power.isEnabled()) {
                continue;
            }
            ConfigurationSection matchEntity = power.getSection("match-entity");
            if (!MatchEntityManager.matchEntityManager.getMatch(matchEntity, entity)) {
                continue;
            }

            int cost = parseLevelWeight(power.getInt("apply-rules.level-weight", 0, level), 0);
            if (cost <= 0) {
                continue;
            }

            String group = power.getString("apply-rules.group", "default");
            int weight = Math.max(1, power.getInt("apply-rules.weight", 1, level));
            boolean groupUnique = power.getBoolean("apply-rules.group-unique", false);

            groupedCandidates.computeIfAbsent(group, k -> new ArrayList<>())
                    .add(new CandidatePower(entry.getKey(), cost, weight));
            uniqueGroupMap.put(group, uniqueGroupMap.getOrDefault(group, false) || groupUnique);
        }

        if (groupedCandidates.isEmpty()) {
            return Collections.emptyList();
        }

        int remainLevel = level;
        List<String> selectedPowers = new ArrayList<>();

        Map<String, List<CandidatePower>> currentPools = new HashMap<>();
        Set<String> consumedUniqueGroups = new HashSet<>();
        for (Map.Entry<String, List<CandidatePower>> entry : groupedCandidates.entrySet()) {
            currentPools.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        while (remainLevel > 0) {
            List<String> groupOrder = new ArrayList<>(groupedCandidates.keySet());
            Collections.shuffle(groupOrder);
            boolean pickedThisRound = false;

            for (String group : groupOrder) {
                if (uniqueGroupMap.getOrDefault(group, false) && consumedUniqueGroups.contains(group)) {
                    continue;
                }

                List<CandidatePower> pool = currentPools.computeIfAbsent(group, k -> new ArrayList<>());
                if (pool.isEmpty()) {
                    if (uniqueGroupMap.getOrDefault(group, false)) {
                        continue;
                    }
                    pool.addAll(groupedCandidates.getOrDefault(group, Collections.emptyList()));
                }

                CandidatePower chosen = weightedPick(pool, remainLevel);
                if (chosen == null) {
                    continue;
                }

                selectedPowers.add(chosen.powerId());
                remainLevel -= chosen.cost();
                pickedThisRound = true;
                pool.remove(chosen);

                if (uniqueGroupMap.getOrDefault(group, false)) {
                    consumedUniqueGroups.add(group);
                }
            }

            if (!pickedThisRound) {
                break;
            }
        }

        if (!selectedPowers.isEmpty()) {
            EntityScannerManager.entityScannerManager.setEntityPowers(entity, selectedPowers, level);
        }

        return selectedPowers;
    }

    private CandidatePower weightedPick(List<CandidatePower> pool, int remainLevel) {
        List<CandidatePower> candidates = new ArrayList<>();
        int totalWeight = 0;

        for (CandidatePower candidate : pool) {
            if (candidate.cost() > remainLevel) {
                continue;
            }
            candidates.add(candidate);
            totalWeight += Math.max(1, candidate.weight());
        }

        if (candidates.isEmpty()) {
            return null;
        }

        int random = ThreadLocalRandom.current().nextInt(totalWeight) + 1;
        int current = 0;
        for (CandidatePower candidate : candidates) {
            current += Math.max(1, candidate.weight());
            if (random <= current) {
                return candidate;
            }
        }

        return candidates.get(candidates.size() - 1);
    }

    public int parseLevelValue(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }

        try {
            if (value.contains("~")) {
                String[] split = value.split("~", 2);
                int min = Integer.parseInt(split[0].trim());
                int max = Integer.parseInt(split[1].trim());
                if (max < min) {
                    int temp = min;
                    min = max;
                    max = temp;
                }
                return min + new Random().nextInt(max - min + 1);
            }

            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    public void updateEntityPowerDisplay(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        List<String> powerIDs = EntityScannerManager.entityScannerManager.getEntityPowerCache(entity);
        if (powerIDs == null || powerIDs.isEmpty()) {
            return;
        }

        int maxShown = ConfigManager.configManager.getInt("mob-display.name.max-show", 3);
        String separator = ConfigManager.configManager.getString("mob-display.name.separator", ", ");
        String format = ConfigManager.configManager.getString("mob-display.name.format", "&6[{powers}] &f{mob}");
        String etcFormat = ConfigManager.configManager.getString("mob-display.name.more", "...(+{count})");

        List<String> shown = new ArrayList<>();
        for (int i = 0; i < powerIDs.size(); i++) {
            if (i < maxShown) {
                shown.add(getPowerDisplayName(powerIDs.get(i), EntityScannerManager.entityScannerManager.getEntityLevelCache(entity), null));
            }
        }

        int more = Math.max(0, powerIDs.size() - shown.size());
        String powersText = String.join(separator, shown);
        if (more > 0) {
            powersText += separator + etcFormat.replace("{count}", String.valueOf(more));
        }

        String displayName = format
                .replace("{powers}", powersText)
                .replace("{mob}", EnchantedMobs.methodUtil.getEntityName(living));

        EnchantedMobs.methodUtil.setEntityName(living, displayName);
        living.setCustomNameVisible(true);
    }

    private List<String> getPowerPlaceholders(List<String> powerIDs, int level, Player player) {
        List<String> display = new ArrayList<>();
        for (String powerID : powerIDs) {
            display.add(getPowerDisplayName(powerID, level, player));
        }
        return display;
    }

    private String getPowerDisplayName(String powerID, int level, Player player) {
        ObjectPower power = powers.get(powerID);
        if (power == null) {
            return powerID;
        }
        return CommonUtil.parseLang(player, power.getPlaceholder().replace("{level}", String.valueOf(level)));
    }

    private String separatorFromConfig() {
        return ConfigManager.configManager.getString("mob-display.name.separator", ", ");
    }

    private int parseLevelWeight(Object rawWeight, int defaultValue) {
        if (rawWeight == null) {
            return defaultValue;
        }
        if (rawWeight instanceof Number number) {
            return number.intValue();
        }
        if (rawWeight instanceof String string) {
            return parseLevelValue(string, defaultValue);
        }
        return defaultValue;
    }

    public void handleShootBow(EntityShootBowEvent event) {
        Entity entity = event.getProjectile();
        if (!(entity instanceof Projectile projectile)) {
            return;
        }
        Entity shooter = event.getEntity();
        ItemStack bow = event.getBow();
        ItemStack consume = event.getConsumable();
        EquipmentSlot equipmentSlot = event.getHand();
        float force = event.getForce();
        ShootBowHandler handler = new ShootBowHandler(projectile, shooter, bow, consume, equipmentSlot, force);
        forEachActivePower(shooter, projectile, "on-shoot-bow", (power, level) -> power.onShootProjectile(level, handler), event::setCancelled);
        if (handler.replacedNewProjectile && !event.isCancelled()) {
            DebugUtil.log("projectile", "Projectile has been replaced by on-shoot-bow modifier/ability.");
            event.setProjectile(handler.projectile);
        }
        if (ConfigManager.configManager.getBoolean("optimize.enabled-projectile-tick")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!handler.projectile.isValid() || handler.projectile.isOnGround()) {
                        cancel();
                        return;
                    }
                    ShootBowHandler tickHandler = new ShootBowHandler(handler, handler.projectile.getLocation());
                    forEachActivePower(shooter, handler.projectile, "on-projectile-tick", (power, level) -> power.onProjectileTick(level, tickHandler), (b) -> cancel());
                    if (tickHandler.replacedNewProjectile && tickHandler.projectile != null) {
                        handler.projectile = tickHandler.projectile;
                    }
                }
            }.runTaskTimer(EnchantedMobs.instance, 1L, 1L);
        }
    }

    public void handleProjectileHit(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        ProjectileSource source = entity.getShooter();
        if (!(source instanceof LivingEntity shooter)) {
            return;
        }
        Entity hitEntity = event.getHitEntity();
        Block hitBlock = event.getHitBlock();
        ProjectileHitHandler handler = new ProjectileHitHandler(entity, shooter, hitEntity, hitBlock);
        forEachActivePower(shooter, entity, "on-projectile-hit", (power, level) -> power.onProjectileHit(level, handler), event::setCancelled);
    }

    public void handleCombust(EntityCombustEvent event) {
        Entity entity = event.getEntity();
        CombustHandler handler = new CombustHandler(entity, event.getDuration(), event instanceof EntityCombustByEntityEvent, event instanceof EntityCombustByBlockEvent);
        forEachActivePower(entity, null, "on-combust", (power, level) -> power.onCombust(level, handler), event::setCancelled);
        if (handler.replacedNewDuration && !event.isCancelled()) {
            event.setDuration(handler.duration);
        }
    }

    public void handleSpawn(Entity entity) {
        updateEntityPowerDisplay(entity);
        SpawnHandler handler = new SpawnHandler(entity);
        forEachActivePower(entity, null, "on-spawn", (power, level) -> power.onSpawn(level, handler), null);
    }

    public void handleOnDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Entity damageEntity = null;
        boolean byEntity = false;
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            byEntity = true;
            damageEntity = entityEvent.getDamager();
        }
        DamageHandler handler = new DamageHandler(entity, damageEntity, event.getDamage(), byEntity, event instanceof EntityDamageByBlockEvent, event.getCause());
        forEachActivePower(entity, null, "on-damage", (power, level) -> power.onDamage(level, handler), event::setCancelled);
        if (handler.replacedNewDamage && !event.isCancelled()) {
            event.setDamage(Math.max(0.1, handler.damage));
        }
    }

    public void handleRegainHealth(EntityRegainHealthEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        RegainHandler handler = new RegainHandler(entity, event.getAmount());
        forEachActivePower(entity, null, "on-regain", (power, level) -> power.onRegain(level, handler), event::setCancelled);
        if (handler.replacedNewAmount && !event.isCancelled()) {
            event.setAmount(Math.max(0, handler.amount));
        }
    }

    public void handleMeleeAttack(EntityDamageByEntityEvent event) {
        Entity attacker = EnchantedMobs.methodUtil.getDamager(event.getDamager());
        if (attacker == null) {
            attacker = event.getDamager();
        }
        MeleeAttackHandler handler = new MeleeAttackHandler(attacker, event.getEntity(), event.getDamage(), event.getDamager() instanceof LivingEntity);
        forEachActivePower(attacker, null, "on-melee-attack", (power, level) -> power.onMeleeAttack(level, handler), event::setCancelled);
        if (handler.replacedNewDamage && !event.isCancelled()) {
            event.setDamage(Math.max(0, handler.damage));
        }
    }

    public void handleDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DeathHandler handler = new DeathHandler(entity);
        forEachActivePower(entity, null, "on-death", (power, level) -> power.onDeath(level, handler), null);
        if (handler.replacedReviveHealth && handler.cancelEvent && EnchantedMobs.methodUtil.methodID().equals("paper")) {
            event.setCancelled(true);
            event.setReviveHealth(Math.max(0, handler.reviveHealth));
        }
        if (handler.setNoDrops) {
            event.getDrops().clear();
        }
    }

    public void handleTarget(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        if (target == null) {
            return;
        }
        TargetHandler handler = new TargetHandler(entity, target);
        forEachActivePower(entity, null, "on-target", (power, level) -> power.onTarget(level, handler), event::setCancelled);
    }

    public void handleExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        ExplodeHandler handler = new ExplodeHandler(entity, event.getLocation(), event.getYield());
        forEachActivePower(entity, null, "on-explode", (power, level) -> power.onExplode(level, handler), null);
        if (handler.replacedNewYield && !event.isCancelled()) {
            event.setYield(Math.max(0, handler.yield));
        }
    }


    private void forEachActivePower(Entity owner, Entity skillEntity, String eventKey, PowerExecution execution, Consumer<Boolean> cancelCallback) {
        List<String> powerIDs = EntityScannerManager.entityScannerManager.getEntityPowerCache(owner);
        if (powerIDs == null) {
            return;
        }
        int level = EntityScannerManager.entityScannerManager.getEntityLevelCache(owner);
        if (level < 0) {
            return;
        }

        for (String powerID : powerIDs) {
            ObjectPower power = powers.get(powerID);
            if (power == null || !power.willUseThisPower(owner, skillEntity, level, eventKey)) {
                continue;
            }
            boolean cancel = execution.execute(power, level);
            if (cancel && cancelCallback != null) {
                cancelCallback.accept(true);
            }
        }
    }

    private record CandidatePower(String powerId, int cost, int weight) {
    }

    @FunctionalInterface
    private interface PowerExecution {
        boolean execute(ObjectPower power, int level);
    }

    public Map<String, ObjectPower> getPowers() {
        return powers;
    }

    public void markUsedPower(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(USED_POWER, PersistentDataType.BOOLEAN, true);
    }

    public boolean isUsedPower(Entity entity) {
        if (!entity.getPersistentDataContainer().has(USED_POWER, PersistentDataType.BOOLEAN)) {
            return false;
        }
        return entity.getPersistentDataContainer().get(USED_POWER, PersistentDataType.BOOLEAN);
    }
}
