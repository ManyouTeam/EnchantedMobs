package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class CreeperExplodeHandler extends AbstractHandler {

    public final float originalRadius;

    public float radius;

    public boolean replacedNewRadius;

    public CreeperExplodeHandler(Entity damager, Entity source, Location location, float radius) {
        super(damager, source, source);
        this.location = location;
        this.originalRadius = radius;
        this.radius = radius;
    }

    public void setNewRadius(float radius) {
        this.radius = radius;
        this.replacedNewRadius = true;
    }
}
