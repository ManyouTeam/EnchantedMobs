package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class RemovePotionEffectAbility extends AbstractAbility {

    public RemovePotionEffectAbility(ConfigurationSection section) {
        super("RemovePotionEffect", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity target = getTargetEntity(context);
        if (!(target instanceof LivingEntity living)) {
            return false;
        }

        List<String> potionKeys = new ArrayList<>(getStringList("potions"));
        if (potionKeys.isEmpty()) {
            potionKeys.add(getString("potion", ""));
        }

        for (String potionKey : potionKeys) {
            if (potionKey == null || potionKey.isBlank()) {
                continue;
            }
            if (potionKey.equalsIgnoreCase("ALL")) {
                for (PotionEffect activeEffect : living.getActivePotionEffects()) {
                    living.removePotionEffect(activeEffect.getType());
                }
                continue;
            }

            PotionEffectType type = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(potionKey));
            if (type == null) {
                continue;
            }
            living.removePotionEffect(type);
        }

        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
