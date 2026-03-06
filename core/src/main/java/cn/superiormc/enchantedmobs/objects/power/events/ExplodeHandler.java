package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ExplodeHandler extends AbstractHandler {

    public final float originalYield;

    public float yield;

    public boolean replacedNewYield;

    public ExplodeHandler(Entity source, Location location, float yield) {
        super(source, source, source);
        this.location = location;
        this.originalYield = yield;
        this.yield = yield;
    }

    public void setNewYield(float yield) {
        this.yield = yield;
        this.replacedNewYield = true;
    }
}
