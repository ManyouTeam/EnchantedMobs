package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageHandler extends AbstractHandler {

    public final double originalDamage;

    public double damage;

    public final boolean byEntity;

    public final boolean byBlock;

    public boolean replacedNewDamage;

    public final EntityDamageEvent.DamageCause cause;

    public DamageHandler(Entity entity, Entity damageEntity, double damage, boolean byEntity, boolean byBlock, EntityDamageEvent.DamageCause cause) {
        super(damageEntity, entity, entity);
        this.location = entity.getLocation();
        this.originalDamage = damage;
        this.damage = damage;
        this.byEntity = byEntity;
        this.byBlock = byBlock;
        this.cause = cause;
    }

    public void setNewDamage(double damage) {
        this.damage = damage;
        this.replacedNewDamage = true;
    }
}
