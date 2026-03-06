package cn.superiormc.enchantedmobs.objects.power.events;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.entity.Entity;

public class DeathHandler extends AbstractHandler {

    public double reviveHealth;

    public boolean replacedReviveHealth;

    public boolean setNoDrops;

    public boolean cancelEvent;

    public DeathHandler(Entity entity) {
        super(entity, entity, entity);
        this.location = entity.getLocation();
    }

    public void setReviveHealth(double reviveHealth) {
        if (reviveHealth > CommonUtil.getMaxHealth(sourceEntity)) {
            reviveHealth = CommonUtil.getMaxHealth(sourceEntity);
        }
        this.reviveHealth = reviveHealth;
        this.replacedReviveHealth = true;
        this.cancelEvent = true;
    }

    public void setSetsNoDrops() {
        this.setNoDrops = true;
    }
}
