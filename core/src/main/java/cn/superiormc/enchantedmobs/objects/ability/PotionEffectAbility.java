package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import com.google.common.base.Enums;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAbility extends AbstractAbility {

    public PotionEffectAbility(ConfigurationSection section) {
        super("PotionEffect", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity target = getTargetEntity(context);
        if (!(target instanceof LivingEntity living)) {
            return false;
        }
        String effectKey = getString("effect", "SLOWNESS");
        PotionEffectType type = PotionEffectType.getByKey(CommonUtil.parseNamespacedKey(effectKey));
        if (type == null) {
            return false;
        }

        int durationTicks = getInt("duration", 100, context.level());
        int amplifier = Math.max(0, getInt("amplifier", 0, context.level()));
        boolean ambient = getBoolean("ambient", false);
        boolean particles = getBoolean("particles", true);
        boolean icon = getBoolean("icon", true);
        living.addPotionEffect(new PotionEffect(type, Math.max(1, durationTicks), amplifier, ambient, particles, icon));
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
