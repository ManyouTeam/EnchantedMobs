package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.AbilityDamageUtil;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.SchedulerUtil;
import com.google.common.base.Enums;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.Locale;

public class LaunchProjectileAbility extends AbstractAbility {

    public LaunchProjectileAbility(ConfigurationSection section) {
        super("LaunchProjectile", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity source = section.contains("source") ? getSourceEntity(context) : getTargetEntity(context);
        if (!(source instanceof LivingEntity livingEntity)) {
            return false;
        }

        EntityType projectileType = Enums.getIfPresent(EntityType.class, getString("entity-type", "ARROW")).orNull();
        if (projectileType == null || !projectileType.isSpawnable()) {
            return false;
        }

        Location eye = livingEntity.getEyeLocation();
        Vector direction = getLaunchDirection(eye, context);
        direction.setY(direction.getY() + getDouble("extra-y", 0, context.level()));
        Location spawnLocation = getSpawnLocation(eye, direction, context);
        Entity spawned = eye.getWorld().spawnEntity(spawnLocation, projectileType);
        if (!(spawned instanceof Projectile projectile)) {
            spawned.remove();
            return false;
        }

        projectile.setShooter(livingEntity);
        projectile.setVelocity(direction);

        if (projectile instanceof Fireball fireball) {
            fireball.setYield((float) getDouble("fireball-yield", 1.0, context.level()));
            fireball.setIsIncendiary(getBoolean("fireball-incendiary", true));
        }

        if (projectile instanceof ShulkerBullet bullet) {
            Entity target = context.handler().targetEntity;
            if (target != null && target.isValid()) {
                bullet.setTarget(target);
            }
        }

        if (projectile instanceof ThrownPotion potion) {
            applyPotionItem(potion, context);
        }

        if (section.contains("damage")) {
            AbilityDamageUtil.markDamage(projectile, getDouble("damage", 0.0D, context.level()));
        }
        return false;
    }

    private void applyPotionItem(ThrownPotion potion, AbilityContext context) {
        ItemStack item = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) {
            return;
        }

        String basePotion = getString("potion-type", "");
        PotionType potionType = Enums.getIfPresent(PotionType.class, basePotion.toUpperCase()).orNull();
        if (potionType != null) {
            meta.setBasePotionType(potionType);
        }

        ConfigurationSection effects = section.getConfigurationSection("potion-effects");
        if (effects != null) {
            for (String key : effects.getKeys(false)) {
                ConfigurationSection effectSection = effects.getConfigurationSection(key);
                if (effectSection == null) {
                    continue;
                }
                String potionKey = effectSection.getString("potion", key);
                PotionEffectType effectType = getPotionEffectType(potionKey);
                if (effectType == null) {
                    continue;
                }
                int duration = Math.max(1, getInt(effectSection.getCurrentPath() + ".duration", 100, context.level()));
                int amplifier = Math.max(0, getInt(effectSection.getCurrentPath() + ".amplifier", 0, context.level()));
                meta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), true);
            }
        } else {
            String potionKey = getString("potion", "");
            PotionEffectType effectType = getPotionEffectType(potionKey);
            if (effectType != null) {
                int duration = Math.max(1, getInt("duration", 100, context.level()));
                int amplifier = Math.max(0, getInt("amplifier", 0, context.level()));
                meta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), true);
            }
        }

        item.setItemMeta(meta);
        potion.setItem(item);
    }

    private PotionEffectType getPotionEffectType(String potionKey) {
        PotionEffectType effectType = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(potionKey));
        if (effectType != null || potionKey == null) {
            return effectType;
        }
        return PotionEffectType.getByName(potionKey.toUpperCase(Locale.ROOT));
    }

    private Location getSpawnLocation(Location eye, Vector direction, AbilityContext context) {
        double offset = getDouble("spawn-offset", 0.0D, context.level());
        if (offset <= 0.0D || direction.lengthSquared() <= 1.0E-4D) {
            return eye;
        }
        return eye.clone().add(direction.clone().normalize().multiply(offset));
    }

    private Vector getLaunchDirection(Location eye, AbilityContext context) {
        double speed = getDouble("speed", 1.5D, context.level());
        Entity aimTarget = context.handler().targetEntity;
        if (aimTarget != null && aimTarget.getWorld().equals(eye.getWorld()) && aimTarget instanceof LivingEntity livingEntity) {
            Vector targetDirection = livingEntity.getEyeLocation().toVector().subtract(eye.toVector());
            if (targetDirection.lengthSquared() > 1.0E-4D) {
                return targetDirection.normalize().multiply(speed);
            }
        }
        return eye.getDirection().normalize().multiply(speed);
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}
