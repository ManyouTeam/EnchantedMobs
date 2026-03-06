package cn.superiormc.enchantedmobs.objects.matchentity;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class EntityTag extends AbstractMatchEntityRule {

    public EntityTag() {
        super();
    }

    @Override
    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        for (String singleEntity : section.getStringList("entity-tag")) {
            Tag<EntityType> tempVal1 = Bukkit.getTag(Tag.REGISTRY_ENTITY_TYPES, CommonUtil.parseNamespacedKey(singleEntity), EntityType.class);
            if (tempVal1 != null && tempVal1.isTagged(entity.getType())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean configNotContains(ConfigurationSection section) {
        return section.getStringList("entity-tag").isEmpty();
    }
}
