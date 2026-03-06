package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.utils.DebugUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class ExplosionAbility extends AbstractAbility {

    public ExplosionAbility(ConfigurationSection section) {
        super("Explosion", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        int level = context.level();
        float yield = (float) getDouble("yield", 2.0, level); // 爆炸威力
        boolean setFire = getBoolean("set-fire", false); // 是否产生火
        boolean breakBlocks = getBoolean("break-blocks", false); // 是否破坏方块

        Location loc = getLocation(context);
        if (loc == null || loc.getWorld() == null) {
            return false;
        }
        DebugUtil.log("ability", "Create explosion ability executed.");

        EnchantedMobs.methodUtil.createExplosion(loc, getTargetEntity(context), yield, setFire, breakBlocks);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        return false;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.TARGET;
    }

}
