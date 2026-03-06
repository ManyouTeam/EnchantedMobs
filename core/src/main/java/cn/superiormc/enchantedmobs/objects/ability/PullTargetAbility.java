package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.DebugUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class PullTargetAbility extends AbstractAbility {

    public PullTargetAbility(ConfigurationSection section) {
        super("PullTarget", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        DebugUtil.log("ability", "Pull target ability start.");
        Entity attacker = context.handler().sourceEntity;
        if (attacker == null) {
            return false;
        }
        Entity target = getTargetEntity(context);
        if (target == null) {
            return false;
        }
        double strength = getDouble("strength", 1.0, context.level());
        Vector direction = attacker.getLocation().toVector().subtract(target.getLocation().toVector());
        if (direction.lengthSquared() < 0.0001) {
            return false;
        }

        Vector velocity = direction.normalize().multiply(strength);
        velocity.setY(Math.max(0.2, velocity.getY() + 0.2));
        target.setVelocity(velocity);
        DebugUtil.log("ability", "Pull target ability finished.");
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }
}
