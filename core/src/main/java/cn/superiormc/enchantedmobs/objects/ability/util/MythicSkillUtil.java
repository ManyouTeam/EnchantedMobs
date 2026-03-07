package cn.superiormc.enchantedmobs.objects.ability.util;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class MythicSkillUtil {

    private MythicSkillUtil() {
    }

    /**
     * 让指定实体释放 MythicMobs 技能
     */
    public static void castSkill(Entity casterEntity, String skillName) {
        castSkill(casterEntity, null, casterEntity != null ? casterEntity.getLocation() : null, skillName, 1.0f);
    }

    /**
     * 让指定实体对目标释放 MythicMobs 技能
     */
    public static void castSkill(Entity casterEntity, Entity targetEntity, String skillName) {
        castSkill(casterEntity, targetEntity, casterEntity != null ? casterEntity.getLocation() : null, skillName, 1.0f);
    }

    /**
     * 完整版：指定施法者、目标、原点、power
     */
    public static void castSkill(Entity casterEntity,
                                    Entity targetEntity,
                                    Location originLocation,
                                    String skillName,
                                    float power) {
        if (casterEntity == null || skillName == null || skillName.isBlank()) {
            return;
        }

        Optional<Skill> optionalSkill = MythicBukkit.inst().getSkillManager().getSkill(skillName);
        if (optionalSkill.isEmpty()) {
            return;
        }

        Skill skill = optionalSkill.get();

        AbstractEntity abstractCasterEntity = BukkitAdapter.adapt(casterEntity);
        AbstractEntity abstractTriggerEntity = BukkitAdapter.adapt(targetEntity);

        AbstractLocation abstractOrigin = BukkitAdapter.adapt(originLocation);

        Collection<AbstractEntity> entityTargets = new ArrayList<>();
        if (targetEntity != null) {
            entityTargets.add(BukkitAdapter.adapt(targetEntity));
        }

        Collection<AbstractLocation> locationTargets = new ArrayList<>();
        if (originLocation != null) {
            locationTargets.add(BukkitAdapter.adapt(originLocation));
        }

        SkillCaster caster = new GenericCaster(abstractCasterEntity);

        SkillTrigger trigger = SkillTriggers.API;
        SkillMetadata metadata = new SkillMetadataImpl(
                trigger,
                caster,
                abstractTriggerEntity,
                abstractOrigin,
                entityTargets,
                locationTargets,
                power
        );

        if (!skill.isUsable(metadata)) {
            return;
        }

        skill.execute(metadata);
    }

    /**
     * 只给位置目标
     */
    public static void castSkillAtLocation(Entity casterEntity,
                                              Location targetLocation,
                                              String skillName,
                                              float power) {
        if (casterEntity == null || targetLocation == null || skillName == null || skillName.isBlank()) {
            return;
        }

        Optional<Skill> optionalSkill = MythicBukkit.inst().getSkillManager().getSkill(skillName);
        if (optionalSkill.isEmpty()) {
            return;
        }

        Skill skill = optionalSkill.get();

        AbstractEntity abstractCasterEntity = BukkitAdapter.adapt(casterEntity);
        AbstractLocation abstractOrigin = BukkitAdapter.adapt(targetLocation);

        Collection<AbstractEntity> entityTargets = Collections.emptyList();

        Collection<AbstractLocation> locationTargets = new ArrayList<>();
        locationTargets.add(abstractOrigin);

        SkillCaster caster = new GenericCaster(abstractCasterEntity);

        SkillMetadata metadata = new SkillMetadataImpl(
                SkillTriggers.API,
                caster,
                abstractCasterEntity,
                abstractOrigin,
                entityTargets,
                locationTargets,
                power
        );

        if (!skill.isUsable(metadata)) {
            return;
        }

        skill.execute(metadata);
    }
}