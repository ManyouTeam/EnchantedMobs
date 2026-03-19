package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;

public class UntagHandler extends AbstractHandler {

    public final EntityTargetEvent.TargetReason reason;

    public UntagHandler(Entity source, EntityTargetEvent.TargetReason reason) {
        super(source, source, source);
        this.location = source.getLocation();
        this.reason = reason;
    }
}
