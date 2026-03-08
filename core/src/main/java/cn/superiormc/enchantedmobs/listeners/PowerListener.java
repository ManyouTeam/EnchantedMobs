package cn.superiormc.enchantedmobs.listeners;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.*;
import cn.superiormc.enchantedmobs.utils.DebugUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class PowerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        PowerManager.powerManager.handleProjectileHit(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Monster) {
            PowerManager.powerManager.handleShootBow(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCombust(EntityCombustEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Monster) {
            PowerManager.powerManager.handleCombust(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!ConfigManager.configManager.getBoolean("mob-power-generator.enabled", true)) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("mob-power-generator.ignore-custom-spawn") && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            return;
        }

        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }
        if (ConfigManager.configManager.getStringList("mob-power-generator.disabled-worlds").contains(entity.getWorld().getName())) {
            return;
        }
        if (EntityScannerManager.entityScannerManager.containsEntityCache(entity)) {
            return;
        }
        ConfigurationSection disabledEntity = ConfigManager.configManager.getSection("mob-power-generator.disabled-entity");
        if (!disabledEntity.getKeys(false).isEmpty() && MatchEntityManager.matchEntityManager.getMatch(disabledEntity, entity)) {
            return;
        }

        DebugUtil.log("spawn", "Trying to assign powers to spawned mob.");
        int averagePower = PowerManager.powerManager.getNearbyAveragePlayerPower(
                entity.getLocation(),
                ConfigManager.configManager.getInt("mob-power-generator.player-scan-range", 48));
        int level;
        if (averagePower > 0) {
            level = averagePower;
        } else {
            String levelRange = ConfigManager.configManager.getString("mob-power-generator.default-level", "25");
            level = PowerManager.powerManager.parseLevelValue(levelRange, 25);
        }
        if (!PowerManager.powerManager.shouldAssignPowerForSpawn()) {
            return;
        }

        if (!PowerManager.powerManager.assignRandomPowersByLevel(level, entity).isEmpty()) {
            notifyPoweredMobSpawn(entity);
            DebugUtil.log("spawn", "Mob powers assigned successfully.");
        }
    }

    private void notifyPoweredMobSpawn(LivingEntity entity) {
        Location loc = entity.getLocation();
        String x = String.valueOf(loc.getBlockX());
        String y = String.valueOf(loc.getBlockY());
        String z = String.valueOf(loc.getBlockZ());
        String world = loc.getWorld() == null ? "unknown" : loc.getWorld().getName();

        for (Player player : entity.getWorld().getPlayers()) {
            if (player.hasPermission("enchantedmobs.nodify") && ConfigManager.configManager.getBoolean("display-spawn-message", false)) {
                LanguageManager.languageManager.sendStringText(player,
                        "mob-powered-notify",
                        "world", world,
                        "x", x,
                        "y", y,
                        "z", z,
                        "mob", EnchantedMobs.methodUtil.getEntityName(entity));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }

        if (ConfigManager.configManager.getBoolean("mob-combat.disable-powered-mob-friendly-fire", false)
                && event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            Entity damager = EnchantedMobs.methodUtil.getDamager(damageByEntityEvent.getDamager());
            if (damager == null) {
                damager = damageByEntityEvent.getDamager();
            }
            if (damager instanceof Monster) {
                event.setCancelled(true);
                return;
            }
        }

        PowerManager.powerManager.handleOnDamage(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegainHealth(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Monster) {
            PowerManager.powerManager.handleRegainHealth(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMeleeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Monster)) {
            return;
        }

        if (ConfigManager.configManager.getBoolean("mob-combat.disable-powered-mob-friendly-fire", false)
                && event.getEntity() instanceof Monster) {
            event.setCancelled(true);
            return;
        }

        PowerManager.powerManager.handleMeleeAttack(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            PowerManager.powerManager.handleDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Monster) {
            PowerManager.powerManager.handleTarget(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Monster) {
            PowerManager.powerManager.handleExplode(event);
        }
    }
}
