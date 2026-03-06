package cn.superiormc.enchantedmobs.commands;

import cn.superiormc.enchantedmobs.managers.LanguageManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

public class SubSpawnRandomMob extends AbstractCommand {

    public SubSpawnRandomMob() {
        this.id = "spawnrandommob";
        this.requiredPermission = "enchantedmobs." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{3};
        this.requiredConsoleArgLength = new Integer[7];
        this.unlimitedArgLength = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (args.length == 3) {
            Integer level = parseLevel(args[1]);
            EntityType type = parseEntityType(args[2]);
            if (level == null || type == null) {
                LanguageManager.languageManager.sendStringText(player, "error.args");
                return;
            }
            spawnRandomMob(level, type, player.getLocation(), player);
            return;
        }

        spawnByCoordinate(args, player);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        if (args.length != 7) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }
        spawnByCoordinate(args, null);
    }

    private void spawnByCoordinate(String[] args, Player feedbackPlayer) {
        Integer level = parseLevel(args[1]);
        EntityType type = parseEntityType(args[2]);
        World world = Bukkit.getWorld(args[3]);
        if (level == null || type == null || world == null) {
            sendArgError(feedbackPlayer);
            return;
        }

        try {
            double x = Double.parseDouble(args[4]);
            double y = Double.parseDouble(args[5]);
            double z = Double.parseDouble(args[6]);
            Location location = new Location(world, x, y, z);
            spawnRandomMob(level, type, location, feedbackPlayer);
        } catch (Exception exception) {
            sendArgError(feedbackPlayer);
        }
    }

    private void spawnRandomMob(int level, EntityType type, Location location, Player feedbackPlayer) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        if (!(entity instanceof LivingEntity living)) {
            if (feedbackPlayer == null) {
                LanguageManager.languageManager.sendStringText("error.entity-invalid");
            } else {
                LanguageManager.languageManager.sendStringText(feedbackPlayer, "error.entity-invalid");
            }
            entity.remove();
            return;
        }

        PowerManager.powerManager.assignRandomPowersByLevel(level, living);
        if (feedbackPlayer == null) {
            LanguageManager.languageManager.sendStringText("mob-spawned");
        } else {
            LanguageManager.languageManager.sendStringText(feedbackPlayer, "mob-spawned");
        }
    }

    private void sendArgError(Player player) {
        if (player == null) {
            LanguageManager.languageManager.sendStringText("error.args");
        } else {
            LanguageManager.languageManager.sendStringText(player, "error.args");
        }
    }

    private Integer parseLevel(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (Exception exception) {
            return null;
        }
    }

    private EntityType parseEntityType(String raw) {
        try {
            return EntityType.valueOf(raw.toUpperCase());
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            list.add("25");
            list.add("50");
            list.add("100");
        } else if (args.length == 3) {
            for (EntityType type : EntityType.values()) {
                Class<? extends Entity> clazz = type.getEntityClass();
                if (clazz != null && Monster.class.isAssignableFrom(clazz)) {
                    list.add(type.name().toLowerCase());
                }
            }
        } else if (args.length == 4) {
            for (World world : Bukkit.getWorlds()) {
                list.add(world.getName());
            }
        } else if (args.length == 5 || args.length == 6 || args.length == 7) {
            list.add(String.valueOf(player.getLocation().getBlockX()));
            list.add(String.valueOf(player.getLocation().getBlockY()));
            list.add(String.valueOf(player.getLocation().getBlockZ()));
        }
        return list;
    }
}
