package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class AbstractHandler {

    public Entity sourceEntity;

    public Entity skillEntity;

    public Entity targetEntity;

    public Location location;

    public AbstractHandler(Entity sourceEntity, Entity skillEntity, Entity targetEntity) {
        this.sourceEntity = sourceEntity;
        this.skillEntity = skillEntity;
        this.targetEntity = targetEntity;
    }
}
