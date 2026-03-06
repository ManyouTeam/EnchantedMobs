package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ShootBowHandler extends AbstractHandler {

    public Projectile projectile;

    public Entity shooter;

    public ItemStack bow;

    public ItemStack consume;

    public EquipmentSlot equipmentSlot;

    public float force;

    public boolean replacedNewProjectile;

    public ShootBowHandler(Projectile projectile,
                           Entity shooter,
                           ItemStack bow,
                           ItemStack consume,
                           EquipmentSlot equipmentSlot,
                           float force) {
        super(shooter, projectile, projectile);
        this.projectile = projectile;
        this.shooter = shooter;
        this.bow = bow;
        this.consume = consume;
        this.equipmentSlot = equipmentSlot;
        this.force = force;
        this.location = projectile.getLocation();
    }

    public ShootBowHandler(ShootBowHandler handler,
                           Location location) {
        super(handler.shooter, handler.projectile, handler.projectile);
        this.projectile = handler.projectile;
        this.shooter = handler.shooter;
        this.bow = handler.bow;
        this.consume = handler.consume;
        this.equipmentSlot = handler.equipmentSlot;
        this.force = handler.force;
        this.location = location;
    }

    public void setNewProjectile(Projectile projectile) {
        this.projectile = projectile;
        this.replacedNewProjectile = true;
    }
}
