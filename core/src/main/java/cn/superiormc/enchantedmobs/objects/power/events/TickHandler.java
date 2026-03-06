package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;

public class TickHandler extends AbstractHandler {

    public TickHandler(Entity entity) {
        super(entity, entity, entity);
        this.location = entity.getLocation();
    }
}
