package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;

public class RegainHandler extends AbstractHandler {

    public final double originalAmount;

    public double amount;

    public boolean replacedNewAmount;

    public RegainHandler(Entity entity, double amount) {
        super(entity, entity, entity);
        this.location = entity.getLocation();
        this.originalAmount = amount;
        this.amount = amount;
    }

    public void setNewAmount(double amount) {
        this.amount = amount;
        this.replacedNewAmount = true;
    }
}
