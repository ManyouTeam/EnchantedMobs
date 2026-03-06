package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Mob;

public class TickTargetHandler extends AbstractHandler {

    public TickTargetHandler(Mob mob) {
        super(mob, mob, mob.getTarget());
        this.location = mob.getTarget().getLocation();
    }
}
