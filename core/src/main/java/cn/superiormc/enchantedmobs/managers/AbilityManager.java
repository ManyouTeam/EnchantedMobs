package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.objects.ability.*;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class AbilityManager {

    public static AbilityManager abilityManager;

    private final Map<String, Function<ConfigurationSection, AbstractAbility>> abilityParsers = new ConcurrentHashMap<>();

    public AbilityManager() {
        abilityManager = this;
    }

    public void registerAbilityParser(String type, Function<ConfigurationSection, AbstractAbility> parser) {
        if (type == null || parser == null) {
            return;
        }
        abilityParsers.put(normalizeAbilityType(type), parser);
    }

    public boolean execute(ConfigurationSection eventSection, AbilityContext context) {
        if (eventSection == null) {
            return false;
        }
        boolean cancel = false;
        for (AbstractAbility action : parseActions(eventSection)) {
            if (action == null || !action.shouldExecute(context)) {
                continue;
            }
            if (action.execute(context)) {
                cancel = true;
            }
        }
        return cancel;
    }

    public List<AbstractAbility> parseActions(ConfigurationSection eventSection) {
        List<AbstractAbility> actions = new ArrayList<>();
        if (eventSection == null) {
            return actions;
        }

        eventSection.getKeys(false).forEach(key -> {
            ConfigurationSection single = eventSection.getConfigurationSection(key);
            if (single == null) {
                return;
            }
            AbstractAbility action = toAction(single);
            if (action != null) {
                actions.add(action);
            }
        });
        return actions;
    }

    private AbstractAbility toAction(ConfigurationSection section) {
        String type = normalizeAbilityType(section.getString("type", ""));
        Function<ConfigurationSection, AbstractAbility> parser = abilityParsers.get(type);
        return parser == null ? null : parser.apply(section);
    }

    private void registerDefaultAbilities() {
        registerAbilityParser("mark", MarkAbility::new);
        registerAbilityParser("delay", DelayAbility::new);
        registerAbilityParser("place_block", PlaceBlockAbility::new);
        registerAbilityParser("explosion", ExplosionAbility::new);
        registerAbilityParser("remove", RemoveAbility::new);
        registerAbilityParser("cancel_event", CancelEventAbility::new);
        registerAbilityParser("lightning", LightningAbility::new);
        registerAbilityParser("particle", ParticleAbility::new);
        registerAbilityParser("sound", SoundAbility::new);
        registerAbilityParser("set_attribute", SetAttributeAbility::new);
        registerAbilityParser("set_health", SetHealthAbility::new);
        registerAbilityParser("pull_target", PullTargetAbility::new);
        registerAbilityParser("potion_cloud", PotionCloudAbility::new);
        registerAbilityParser("potion_effect", PotionEffectAbility::new);
        registerAbilityParser("freeze", FreezeAbility::new);
        registerAbilityParser("fire", FireAbility::new);
        registerAbilityParser("homing_projectile", HomingProjectileAbility::new);
        registerAbilityParser("teleport_near_target", TeleportNearTargetAbility::new);
        registerAbilityParser("artillery", ArtilleryAbility::new);
        registerAbilityParser("guardian_beam", GuardianBeamAbility::new);
        registerAbilityParser("vanilla_animation", VanillaAnimationAbility::new);
        registerAbilityParser("arrow_rain", ArrowRainAbility::new);
        registerAbilityParser("enhance_equipment", EnhanceEquipmentAbility::new);
        registerAbilityParser("enhance_helditem", EnhanceHeldItemAbility::new);
        registerAbilityParser("replace_item", ReplaceItemAbility::new);
        registerAbilityParser("disarm", DisarmAbility::new);
        registerAbilityParser("shuffle_inventory", ShuffleInventoryAbility::new);
        registerAbilityParser("launch_projectile", LaunchProjectileAbility::new);
        registerAbilityParser("send_message", SendMessageAbility::new);
        registerAbilityParser("execute_command", ExecuteCommandAbility::new);
        registerAbilityParser("limit", LimitAbility::new);
        registerAbilityParser("mythic_skill", MythicSkillAbility::new);
        registerAbilityParser("summon", SummonAbility::new);
        registerAbilityParser("creeper_stats", CreeperStatsAbility::new);
    }

    private String normalizeAbilityType(String type) {
        return type == null ? "" : type.toLowerCase().replace('-', '_');
    }
}
