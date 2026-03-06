package cn.superiormc.enchantedmobs.objects.power.events;

import org.bukkit.entity.Entity;

public class CombustHandler extends AbstractHandler {

    public float originalDuration;

    public float duration;

    public boolean replacedNewDuration;

    public boolean byBlock;

    public boolean byEntity;

    public CombustHandler(Entity entity,
                          float duration,
                          boolean byEntity,
                          boolean byBlock) {
        super(entity, entity, entity);
        this.originalDuration = duration;
        this.duration = duration;
        this.location = entity.getLocation();
        this.byBlock = byBlock;
        this.byEntity = byEntity;
    }

    public void setNewDuration(float time) {
        this.duration = time;
        this.replacedNewDuration = true;
    }
}
