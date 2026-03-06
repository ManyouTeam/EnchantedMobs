package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;

public class TargetHandler extends AbstractHandler {

    public TargetHandler(Entity source, Entity target) {
        super(source, source, target);
        this.location = target.getLocation();
    }
}
