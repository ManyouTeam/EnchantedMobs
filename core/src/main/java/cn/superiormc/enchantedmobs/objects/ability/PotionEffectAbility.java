package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import com.google.common.base.Enums;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAbility extends AbstractAbility {

    private static final int DEFAULT_INFINITE_DURATION_THRESHOLD = 999999;

    public PotionEffectAbility(ConfigurationSection section) {
        super("PotionEffect", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity target = getTargetEntity(context);
        if (!(target instanceof LivingEntity living)) {
            return false;
        }
        String effectKey = getString("potion", "SLOWNESS");
        PotionEffectType type = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(effectKey));
        if (type == null) {
            return false;
        }

        int durationTicks = getInt("duration", 100, context.level());
        int infiniteThreshold = Math.max(0, getInt("infinite-duration-threshold", DEFAULT_INFINITE_DURATION_THRESHOLD, context.level()));
        if (infiniteThreshold > 0 && durationTicks >= infiniteThreshold) {
            durationTicks = PotionEffect.INFINITE_DURATION;
        }
        int amplifier = Math.max(0, getInt("amplifier", 0, context.level()));
        boolean ambient = getBoolean("ambient", false);
        boolean particles = getBoolean("particles", true);
        boolean icon = getBoolean("icon", true);
        int appliedDuration = durationTicks == PotionEffect.INFINITE_DURATION ? PotionEffect.INFINITE_DURATION : Math.max(1, durationTicks);
        if (getBoolean("accumulate", false)) {
            PotionEffect existing = living.getPotionEffect(type);
            if (existing != null) {
                appliedDuration = accumulateDuration(existing.getDuration(), appliedDuration);
                amplifier = Math.max(amplifier, existing.getAmplifier());
            }
        }
        living.addPotionEffect(new PotionEffect(type, appliedDuration, amplifier, ambient, particles, icon));
        return false;
    }

    private int accumulateDuration(int existingDuration, int newDuration) {
        if (existingDuration == PotionEffect.INFINITE_DURATION || newDuration == PotionEffect.INFINITE_DURATION) {
            return PotionEffect.INFINITE_DURATION;
        }
        long total = (long) existingDuration + newDuration;
        return total >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
