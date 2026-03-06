package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class ProjectileHitHandler extends AbstractHandler {

    public Projectile projectile;

    public LivingEntity shooter;

    public Entity hitEntity;

    public Block hitBlock;

    public Block offsetBlock;

    public boolean hitAir;

    public ProjectileHitHandler(Projectile projectile,
                                LivingEntity shooter,
                                Entity hitEntity,
                                Block hitBlock) {
        super(shooter, projectile, hitEntity);
        this.projectile = projectile;
        this.shooter = shooter;
        this.hitEntity = hitEntity;
        this.hitBlock = hitBlock;
        if (hitBlock != null) {
            this.location = hitBlock.getLocation().add(0, 1, 0);
        } else if (hitEntity != null) {
            this.location = hitEntity.getLocation();
        } else {
            return;
        }
        this.offsetBlock = location.getBlock();
        this.hitAir = offsetBlock.getType() != Material.AIR;
    }

}
