package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.managers.AbilityManager;
import cn.superiormc.enchantedmobs.managers.MatchEntityManager;
import cn.superiormc.enchantedmobs.objects.power.events.AbstractHandler;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class NearbyEntitiesAbility extends AbstractAbility {

    public NearbyEntitiesAbility(ConfigurationSection section) {
        super("NearbyEntities", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        ConfigurationSection abilities = section.getConfigurationSection("abilities");
        Location baseLocation = getBaseLocation(context);
        if (abilities == null || baseLocation == null || baseLocation.getWorld() == null) {
            return false;
        }
        executeAround(baseLocation, abilities, context);
        return false;
    }

    private Location getBaseLocation(AbilityContext context) {
        Entity target = getTargetEntity(context);
        if (target != null) {
            return target.getLocation();
        }
        return getLocation(context);
    }

    private void executeAround(Location baseLocation, ConfigurationSection abilities, AbilityContext context) {
        double radius = Math.max(0, getDouble("radius", 5, context.level()));
        double radiusX = Math.max(0, getDouble("radius-x", radius, context.level()));
        double radiusY = Math.max(0, getDouble("radius-y", radius, context.level()));
        double radiusZ = Math.max(0, getDouble("radius-z", radius, context.level()));

        for (Entity entity : baseLocation.getWorld().getNearbyEntities(baseLocation, radiusX, radiusY, radiusZ)) {
            if (!acceptEntity(entity, context)) {
                continue;
            }
            AbstractHandler handler = copyHandlerForTarget(context.handler(), entity);
            AbilityManager.abilityManager.execute(abilities, new AbilityContext(context.power(), context.level(), handler));
        }
    }

    private boolean acceptEntity(Entity entity, AbilityContext context) {
        if (!(entity instanceof LivingEntity living) || !entity.isValid()) {
            return false;
        }
        Entity source = context.handler().sourceEntity;
        if (source != null && entity.getUniqueId().equals(source.getUniqueId())) {
            return false;
        }
        return MatchEntityManager.matchEntityManager.getMatch(section.getConfigurationSection("match-entity"), living);
    }

    private AbstractHandler copyHandlerForTarget(AbstractHandler original, Entity target) {
        AbstractHandler handler = new AbstractHandler(original.sourceEntity, original.skillEntity, target);
        handler.location = target.getLocation();
        return handler;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}
