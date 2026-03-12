package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;

public class TargetHandler extends AbstractHandler {

    public final EntityTargetEvent.TargetReason reason;

    public TargetHandler(Entity source, Entity target, EntityTargetEvent.TargetReason reason) {
        super(source, source, target);
        this.location = target.getLocation();
        this.reason = reason;
    }
}
