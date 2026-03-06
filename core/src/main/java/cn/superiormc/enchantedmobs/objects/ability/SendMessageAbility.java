package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.utils.CommonUtil;
import cn.superiormc.enchantedmobs.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SendMessageAbility extends AbstractAbility {

    public SendMessageAbility(ConfigurationSection section) {
        super("SendMessage", section);
    }

    @Override
    public boolean execute(AbilityContext context) {
        Entity source = context.handler().sourceEntity;
        Player hateTarget = resolveHateTargetPlayer(context);
        List<Player> players = collectPlayers(source, hateTarget, context);
        List<String> messages = getStringList("messages");
        if (messages.isEmpty()) {
            String single = getString("message", "");
            if (!single.isEmpty()) {
                messages = List.of(single);
            }
        }

        for (Player player : players) {
            for (String message : messages) {
                TextUtil.sendMessage(player, CommonUtil.modifyString(player, message,
                        "player", player.getName(),
                        "target", hateTarget == null ? "" : hateTarget.getName(),
                        "level", String.valueOf(context.level())));
            }
        }
        return false;
    }

    private Player resolveHateTargetPlayer(AbilityContext context) {
        Entity source = context.handler().sourceEntity;
        if (!(source instanceof Mob mob)) {
            return null;
        }
        if (mob.getTarget() instanceof Player player && player.isOnline() && !player.isDead()) {
            return player;
        }
        return null;
    }

    private List<Player> collectPlayers(Entity source, Player hateTarget, AbilityContext context) {
        String mode = getString("mode", "target-player").toLowerCase();
        List<Player> players = new ArrayList<>();
        if (mode.equals("nearby")) {
            if (source == null || source.getWorld() == null) {
                return players;
            }
            double radius = getDouble("radius", 16, context.level());
            Location location = source.getLocation();
            for (Entity nearby : source.getWorld().getNearbyEntities(location, radius, radius, radius)) {
                if (nearby instanceof Player player && player.isOnline() && !player.isDead()) {
                    players.add(player);
                }
            }
            return players;
        }
        if (hateTarget != null) {
            players.add(hateTarget);
        }
        return players;
    }

    @Override
    public TargetEntityType getDefaultTargetEntityType() {
        return TargetEntityType.SOURCE;
    }
}
