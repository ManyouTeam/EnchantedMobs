package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.objects.power.ObjectPower;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.objects.power.events.*;
import cn.superiormc.enchantedmobs.utils.DebugUtil;
import cn.superiormc.enchantedmobs.utils.SchedulerUtil;
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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class PowerManager {

    public static PowerManager powerManager;

    private final NamespacedKey USED_POWER = new NamespacedKey(EnchantedMobs.instance, "used_power");
    private final NamespacedKey DISARM_RETURN_ITEM = new NamespacedKey(EnchantedMobs.instance, "disarm_return_item");

    private final Map<String, ObjectPower> powers = new HashMap<>();

    private SchedulerUtil task;

    private SchedulerUtil bossBarTask;

    private final Map<UUID, Set<String>> playerMobBossBars = new HashMap<>();
    private final Map<UUID, TargetCombatState> targetCombatStates = new HashMap<>();
    private long lastTargetCombatStateCleanupAt = 0L;

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
        this.task = SchedulerUtil.runTaskTimer(() -> {
            cleanupTargetCombatStates();
            //Bukkit.getConsoleSender().sendMessage("[Debug] 1");
            for (LivingEntity entity : EntityScannerManager.entityScannerManager.getLivingEntities()) {
                if (EnchantedMobs.isFolia) {
                    SchedulerUtil.runSync(entity, () -> tickEntity(entity));
                } else {
                    tickEntity(entity);
                }
            }
        }, 1L, 1L);
        this.bossBarTask = SchedulerUtil.runTaskTimer(this::updateBossBars, 20L, 20L);
    }

    private void tickEntity(LivingEntity entity) {
        if (!(entity instanceof Monster monster)) {
            return;
        }
        int level = EntityScannerManager.entityScannerManager.getEntityLevelCache(entity);
        if (level < 0) {
            return;
        }
        TickHandler handler = new TickHandler(entity);
        forEachActivePower(entity, null, "on-tick", (power, l) -> power.onTick(l, handler), null);
        if (monster.getTarget() != null) {
            trackTargetCombatState(monster, monster.getTarget());
            TickTargetHandler tickTargetHandler = new TickTargetHandler(monster);
            forEachActivePower(entity, null, "on-target-tick", (power, l) -> power.onTargetTick(l, tickTargetHandler), null);
        } else {
            targetCombatStates.remove(monster.getUniqueId());
        }
    }

    private void updateBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (EnchantedMobs.isFolia) {
                SchedulerUtil.runSync(player, () -> updateBossBar(player));
            } else {
                updateBossBar(player);
            }
        }
    }

    private void updateBossBar(Player player) {
        int minPowers = ConfigManager.configManager.getInt("mob-display.bossbar.min-powers", 2);
        double radius = ConfigManager.configManager.getInt("mob-display.bossbar.radius", 16);
        String title = ConfigManager.configManager.getString("mob-display.bossbar.title", "&c{entity} &7[{health}/{max-health}] &f{powers_full}");
        String color = ConfigManager.configManager.getString("mob-display.bossbar.color", "RED");
        String style = ConfigManager.configManager.getString("mob-display.bossbar.style", "SOLID");

        Set<String> visibleKeys = new HashSet<>();
        if (player.isDead()) {
            hideStaleMobBossBars(player, visibleKeys);
            return;
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
        return getNearbyAveragePlayerPower(location.getWorld(), location.getX(), location.getZ(), range, true);
    }

    public int getNearbyAverageCachedPlayerPower(Location location, double range) {
        if (location.getWorld() == null) {
            return -1;
        }
        return getNearbyAveragePlayerPower(location.getWorld(), location.getX(), location.getZ(), range, false);
    }

    private int getNearbyAveragePlayerPower(World world, double centerX, double centerZ, double range, boolean resolvePlaceholders) {
        double validRange = Math.max(0, range);
        double rangeSquared = validRange * validRange;
        int total = 0;
        int count = 0;
        for (Player player : world.getPlayers()) {
            double dx = player.getLocation().getX() - centerX;
            double dz = player.getLocation().getZ() - centerZ;
            if ((dx * dx + dz * dz) <= rangeSquared) {
                total += resolvePlaceholders
                        ? PlayerPowerManager.playerPowerManager.getPlayerPower(player)
                        : PlayerPowerManager.playerPowerManager.getCachedPlayerPower(player);
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
        List<CandidatePower> alwaysSelectedCandidates = new ArrayList<>();

        for (Map.Entry<String, ObjectPower> entry : powers.entrySet()) {
            ObjectPower power = entry.getValue();
            if (!power.isEnabled()) {
                continue;
            }
            ConfigurationSection matchEntity = power.getSection("apply-rules.match-entity");
            if (matchEntity == null) {
                matchEntity = power.getSection("match-entity");
            }
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

            uniqueGroupMap.put(group, uniqueGroupMap.getOrDefault(group, false) || groupUnique);
            CandidatePower candidate = new CandidatePower(entry.getKey(), group, cost, weight, getApplyRuleConflicts(power));
            if (power.getBoolean("apply-rules.always-select", false) || power.getBoolean("apply-rules.always-pick", false)) {
                alwaysSelectedCandidates.add(candidate);
                continue;
            }

            groupedCandidates.computeIfAbsent(group, k -> new ArrayList<>())
                    .add(candidate);
        }

        if (groupedCandidates.isEmpty() && alwaysSelectedCandidates.isEmpty()) {
            return Collections.emptyList();
        }

        int remainLevel = level;
        LinkedHashSet<String> selectedPowerIds = new LinkedHashSet<>();

        Map<String, List<CandidatePower>> currentPools = new HashMap<>();
        Set<String> consumedUniqueGroups = new HashSet<>();
        List<CandidatePower> selectedCandidates = new ArrayList<>();
        for (Map.Entry<String, List<CandidatePower>> entry : groupedCandidates.entrySet()) {
            currentPools.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        alwaysSelectedCandidates.stream()
                .sorted(Comparator.comparing(CandidatePower::powerId))
                .forEach(candidate -> {
                    if (conflictsWithSelected(candidate, selectedCandidates)) {
                        return;
                    }
                    selectedPowerIds.add(candidate.powerId());
                    selectedCandidates.add(candidate);
                    if (uniqueGroupMap.getOrDefault(candidate.group(), false)) {
                        consumedUniqueGroups.add(candidate.group());
                    }
                    removeConflictingCandidates(currentPools, candidate);
                });

        while (remainLevel > 0 && !groupedCandidates.isEmpty()) {
            List<String> groupOrder = new ArrayList<>(groupedCandidates.keySet());
            Collections.shuffle(groupOrder);
            boolean pickedThisRound = false;

            for (String group : groupOrder) {
                if (uniqueGroupMap.getOrDefault(group, false) && consumedUniqueGroups.contains(group)) {
                    continue;
                }

                List<CandidatePower> pool = currentPools.computeIfAbsent(group, k -> new ArrayList<>());
                pool.removeIf(candidate -> conflictsWithSelected(candidate, selectedCandidates));
                if (pool.isEmpty()) {
                    if (uniqueGroupMap.getOrDefault(group, false)) {
                        continue;
                    }
                    pool.addAll(groupedCandidates.getOrDefault(group, Collections.emptyList()));
                    pool.removeIf(candidate -> conflictsWithSelected(candidate, selectedCandidates));
                }

                CandidatePower chosen = weightedPick(pool, remainLevel);
                if (chosen == null) {
                    continue;
                }

                selectedPowerIds.add(chosen.powerId());
                selectedCandidates.add(chosen);
                remainLevel -= chosen.cost();
                pickedThisRound = true;
                pool.remove(chosen);
                removeConflictingCandidates(currentPools, chosen);

                if (uniqueGroupMap.getOrDefault(group, false)) {
                    consumedUniqueGroups.add(group);
                }
            }

            if (!pickedThisRound) {
                break;
            }
        }

        List<String> selectedPowers = new ArrayList<>(selectedPowerIds);
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

    private Set<String> getApplyRuleConflicts(ObjectPower power) {
        Set<String> conflicts = new HashSet<>();
        addConflictIds(conflicts, power.getSection().getStringList("apply-rules.conflicts"));
        addConflictIds(conflicts, power.getSection().getStringList("apply-rules.conflict-with"));
        return conflicts;
    }

    private void addConflictIds(Set<String> conflicts, List<String> values) {
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            conflicts.add(normalizePowerId(value));
        }
    }

    private boolean conflictsWithSelected(CandidatePower candidate, List<CandidatePower> selectedCandidates) {
        for (CandidatePower selected : selectedCandidates) {
            if (candidatesConflict(candidate, selected)) {
                return true;
            }
        }
        return false;
    }

    private boolean candidatesConflict(CandidatePower first, CandidatePower second) {
        String firstId = normalizePowerId(first.powerId());
        String secondId = normalizePowerId(second.powerId());
        return firstId.equals(secondId)
                || first.conflicts().contains(secondId)
                || second.conflicts().contains(firstId);
    }

    private void removeConflictingCandidates(Map<String, List<CandidatePower>> pools, CandidatePower selected) {
        for (List<CandidatePower> pool : pools.values()) {
            pool.removeIf(candidate -> candidatesConflict(candidate, selected));
        }
    }

    private String normalizePowerId(String id) {
        return id == null ? "" : id.toLowerCase(Locale.ROOT).replace('-', '_');
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

        EnchantedMobs.methodUtil.setEntityName(living, null);
        String displayName = format
                .replace("{powers}", powersText)
                .replace("{mob}", getDisplayBaseName(living));

        EnchantedMobs.methodUtil.setEntityName(living, displayName);
        living.setCustomNameVisible(true);
    }

    private String getDisplayBaseName(LivingEntity entity) {
        String baseName = EntityScannerManager.entityScannerManager.getBaseName(entity);
        if (baseName != null && !baseName.isEmpty()) {
            return baseName;
        }
        return EnchantedMobs.methodUtil.getEntityName(entity);
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
            final SchedulerUtil[] task = new SchedulerUtil[1];
            task[0] = SchedulerUtil.runTaskTimer(handler.projectile, () -> {
                if (!handler.projectile.isValid() || handler.projectile.isOnGround()) {
                    task[0].cancel();
                    return;
                }
                ShootBowHandler tickHandler = new ShootBowHandler(handler, handler.projectile.getLocation());
                forEachActivePower(shooter, handler.projectile, "on-projectile-tick", (power, level) -> power.onProjectileTick(level, tickHandler), (b) -> task[0].cancel());
                if (tickHandler.replacedNewProjectile && tickHandler.projectile != null) {
                    handler.projectile = tickHandler.projectile;
                }
            }, 1L, 1L);
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
            damageEntity = EnchantedMobs.methodUtil.getDamager(entityEvent.getDamager());
            if (damageEntity == null) {
                damageEntity = entityEvent.getDamager();
            }
            if (ConfigManager.configManager.getBoolean("mob-combat.disable-powered-mob-friendly-fire", false)) {
                if (damageEntity instanceof Monster) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        DamageHandler handler = new DamageHandler(entity, damageEntity, event.getDamage(), byEntity, event instanceof EntityDamageByBlockEvent, event.getCause());
        forEachActivePower(entity, null, "on-damage", (power, level) -> power.onDamage(level, handler), event::setCancelled);
        if (handler.replacedNewDamage && !event.isCancelled()) {
            event.setDamage(Math.max(ConfigManager.configManager.getDouble("mob-combat.min-damage", 1.0), handler.damage));
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
        Entity damageEntity = EnchantedMobs.methodUtil.getDamager(event.getDamager());
        boolean isMelee = false;
        if (damageEntity == null) {
            damageEntity = event.getDamager();
            isMelee = true;
        }
        if (!(damageEntity instanceof Monster)) {
            return;
        }
        MeleeAttackHandler handler = new MeleeAttackHandler(damageEntity, event.getEntity(), event.getDamage(), isMelee);
        forEachActivePower(damageEntity, null, "on-melee-attack", (power, level) -> power.onMeleeAttack(level, handler), event::setCancelled);
        if (handler.replacedNewDamage && !event.isCancelled()) {
            event.setDamage(Math.max(0, handler.damage));
        }
        if (!event.isCancelled()) {
            trackSuccessfulTargetAttack(damageEntity, event.getEntity());
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
        if (entity instanceof Mob mob) {
            trackTargetCombatState(mob, target);
        }
        TargetHandler handler = new TargetHandler(entity, target, event.getReason());
        forEachActivePower(entity, null, "on-target", (power, level) -> power.onTarget(level, handler), event::setCancelled);
    }

    public void handleUntag(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        targetCombatStates.remove(entity.getUniqueId());
        UntagHandler handler = new UntagHandler(entity, event.getReason());
        forEachActivePower(entity, null, "on-untag", (power, level) -> power.onUntag(level, handler), event::setCancelled);
    }

    public void handleExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        Entity damager = EnchantedMobs.methodUtil.getDamager(entity);
        if (damager == null) {
            damager = entity;
        }
        ExplodeHandler handler = new ExplodeHandler(damager, entity, event.getLocation(), event.getYield());
        forEachActivePower(entity, null, "on-explode", (power, level) -> power.onExplode(level, handler), null);
        if (handler.replacedNewYield && !event.isCancelled()) {
            event.setYield(Math.max(0, handler.yield));
        }
    }

    public void handleCreeperExplode(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        Entity damager = EnchantedMobs.methodUtil.getDamager(entity);
        if (damager == null) {
            damager = entity;
        }
        CreeperExplodeHandler handler = new CreeperExplodeHandler(damager, entity, entity.getLocation(), event.getRadius());
        forEachActivePower(entity, null, "on-creeper-explode", (power, level) -> power.onCreeperExplode(level, handler), null);
        if (handler.replacedNewRadius && !event.isCancelled()) {
            event.setRadius(Math.max(0, handler.radius));
        }
    }

    public boolean hasTargetWithoutAttackFor(Entity source, Entity target, long ticks) {
        if (source == null || target == null) {
            return false;
        }
        TargetCombatState state = targetCombatStates.get(source.getUniqueId());
        if (state == null || !state.targetId.equals(target.getUniqueId())) {
            return false;
        }

        long now = System.currentTimeMillis();
        long requiredMillis = Math.max(0L, ticks) * 50L;
        long lastProgress = Math.max(state.targetAcquiredAt, state.lastAttackAt);
        return now - lastProgress >= requiredMillis;
    }

    private void trackTargetCombatState(Mob mob, Entity target) {
        UUID mobId = mob.getUniqueId();
        UUID targetId = target.getUniqueId();
        long now = System.currentTimeMillis();
        TargetCombatState state = targetCombatStates.get(mobId);
        if (state == null || !state.targetId.equals(targetId)) {
            targetCombatStates.put(mobId, new TargetCombatState(targetId, now, 0L, now));
            return;
        }
        state.lastSeenAt = now;
    }

    private void trackSuccessfulTargetAttack(Entity attacker, Entity victim) {
        TargetCombatState state = targetCombatStates.get(attacker.getUniqueId());
        if (state == null || !state.targetId.equals(victim.getUniqueId())) {
            return;
        }
        state.lastAttackAt = System.currentTimeMillis();
        state.lastSeenAt = state.lastAttackAt;
    }

    private void cleanupTargetCombatStates() {
        long now = System.currentTimeMillis();
        if (now - lastTargetCombatStateCleanupAt < 30000L) {
            return;
        }
        lastTargetCombatStateCleanupAt = now;
        targetCombatStates.entrySet().removeIf(entry -> now - entry.getValue().lastSeenAt >= 60000L);
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

    private record CandidatePower(String powerId, String group, int cost, int weight, Set<String> conflicts) {
    }

    private static class TargetCombatState {
        private final UUID targetId;
        private final long targetAcquiredAt;
        private long lastAttackAt;
        private long lastSeenAt;

        private TargetCombatState(UUID targetId, long targetAcquiredAt, long lastAttackAt, long lastSeenAt) {
            this.targetId = targetId;
            this.targetAcquiredAt = targetAcquiredAt;
            this.lastAttackAt = lastAttackAt;
            this.lastSeenAt = lastSeenAt;
        }
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

    public void markDisarmReturnItem(Item item) {
        PersistentDataContainer pdc = item.getPersistentDataContainer();
        pdc.set(DISARM_RETURN_ITEM, PersistentDataType.BOOLEAN, true);
    }

    public boolean isDisarmReturnItem(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!entity.getPersistentDataContainer().has(DISARM_RETURN_ITEM, PersistentDataType.BOOLEAN)) {
            return false;
        }
        return entity.getPersistentDataContainer().get(DISARM_RETURN_ITEM, PersistentDataType.BOOLEAN);
    }
}
