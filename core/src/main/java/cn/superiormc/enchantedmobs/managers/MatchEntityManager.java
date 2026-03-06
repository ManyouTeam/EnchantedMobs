package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.objects.matchentity.*;
import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.HashSet;

public class MatchEntityManager {

    public static MatchEntityManager matchEntityManager;

    private final Collection<AbstractMatchEntityRule> rules = new HashSet<>();

    public MatchEntityManager() {
        matchEntityManager = this;
        initRules();
    }

    private void initRules() {
        registerNewRule(new EntityType());
        registerNewRule(new None());
        registerNewRule(new EntityContainsName());
        registerNewRule(new EntityNone());
        registerNewRule(new EntityHealth());
        registerNewRule(new EntityTag());
        registerNewRule(new EntityPDC());
        registerNewRule(new Any());
        registerNewRule(new Not());
        registerNewRule(new Equip());
        registerNewRule(new Ranged());
        if (CommonUtil.checkPluginLoad("MythicMobs")) {
            registerNewRule(new MythicMobs());
        }
        if (CommonUtil.checkPluginLoad("LevelledMobs")) {
            registerNewRule(new LevelledMobs());
        }
    }

    public void registerNewRule(AbstractMatchEntityRule rule) {
        rules.add(rule);
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded match entity rule: " + rule.getClass().getSimpleName() + "!");
    }

    public boolean getMatch(ConfigurationSection section, LivingEntity entity) {
        if (section == null) {
            return true;
        }
        if (entity == null) {
            return false;
        }
        for (AbstractMatchEntityRule rule : rules) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fChecking rule: " + rule.getClass().getSimpleName() + "!");
            }
            if (rule.configNotContains(section)) {
                continue;
            }
            if (!rule.getMatch(section, entity)) {
                return false;
            }
        }
        return true;
    }

    public Collection<AbstractMatchEntityRule> getRules() {
        return rules;
    }
}
