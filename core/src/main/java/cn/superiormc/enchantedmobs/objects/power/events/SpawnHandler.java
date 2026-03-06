package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;

public class SpawnHandler extends AbstractHandler {

    public SpawnHandler(Entity spawnEntity) {
        super(spawnEntity, spawnEntity, spawnEntity);
        this.location = spawnEntity.getLocation();
    }
}
