package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;

public class MeleeAttackHandler extends AbstractHandler {

    public final double originalDamage;

    public double damage;

    public boolean isMelee;

    public boolean replacedNewDamage;

    public MeleeAttackHandler(Entity attacker, Entity victim, double damage, boolean isMelee) {
        super(attacker, attacker, victim);
        this.location = victim.getLocation();
        this.originalDamage = damage;
        this.damage = damage;
        this.isMelee = isMelee;
    }

    public void setNewDamage(double damage) {
        this.damage = damage;
        this.replacedNewDamage = true;
    }
}
