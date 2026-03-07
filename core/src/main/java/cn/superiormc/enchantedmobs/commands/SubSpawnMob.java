package cn.superiormc.enchantedmobs.commands;

import cn.superiormc.enchantedmobs.managers.EntityScannerManager;
import cn.superiormc.enchantedmobs.managers.LanguageManager;
import cn.superiormc.enchantedmobs.managers.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.*;

public class SubSpawnMob extends AbstractCommand {

    public SubSpawnMob() {
        this.id = "spawnmob";
        this.requiredPermission = "enchantedmobs." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{4};
        this.requiredConsoleArgLength = new Integer[]{8};
        this.unlimitedArgLength = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {

        EntityType type;
        int level;

        // 解析实体类型
        try {
            type = EntityType.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            LanguageManager.languageManager.sendStringText(player, "error.entity-type-not-found", "type", args[1]);
            return;
        }

        // 解析 level
        try {
            level = Integer.parseInt(args[2]);
        } catch (Exception e) {
            LanguageManager.languageManager.sendStringText(player, "error.args");
            return;
        }

        List<String> powers = new ArrayList<>();

        World world = player.getWorld();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();

        int i = 3;

        // 收集 powers，直到遇到 world
        for (; i < args.length; i++) {
            if (Bukkit.getWorld(args[i]) != null) {
                break;
            }
            powers.add(args[i]);
        }

        if (powers.isEmpty()) {
            LanguageManager.languageManager.sendStringText(player, "error.args");
            return;
        }

        // 解析 world + 坐标
        if (i < args.length) {
            world = Bukkit.getWorld(args[i]);
            if (world == null) {
                LanguageManager.languageManager.sendStringText(player, "error.world-not-found", "world", args[i]);
                return;
            }
            i++;

            try {
                if (i < args.length) x = Double.parseDouble(args[i++]);
                if (i < args.length) y = Double.parseDouble(args[i++]);
                if (i < args.length) z = Double.parseDouble(args[i++]);
            } catch (Exception e) {
                LanguageManager.languageManager.sendStringText(player, "error.args");
                return;
            }
        }

        Location loc = new Location(world, x, y, z);
        Entity entity = world.spawnEntity(loc, type);

        if (!(entity instanceof LivingEntity living)) {
            LanguageManager.languageManager.sendStringText(player, "error.entity-invalid");
            return;
        }
        //Bukkit.getConsoleSender().sendMessage("666");
        EntityScannerManager.entityScannerManager.setEntityPowers(living, powers, level);
        LanguageManager.languageManager.sendStringText(player, "mob-spawned");
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        EntityType type;
        int level;

        try {
            type = EntityType.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            LanguageManager.languageManager.sendStringText("error.entity-type-not-found", "type", args[1]);
            return;
        }

        try {
            level = Integer.parseInt(args[2]);
        } catch (Exception e) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }

        int worldIndex = args.length - 4;

        if (worldIndex < 3) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }

        List<String> powers = new ArrayList<>(Arrays.asList(args).subList(3, worldIndex));

        if (powers.isEmpty()) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }

        World world = Bukkit.getWorld(args[worldIndex]);
        if (world == null) {
            LanguageManager.languageManager.sendStringText("error.world-not-found", "world", args[worldIndex]);
            return;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[worldIndex + 1]);
            y = Double.parseDouble(args[worldIndex + 2]);
            z = Double.parseDouble(args[worldIndex + 3]);
        } catch (Exception e) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }

        Location loc = new Location(world, x, y, z);
        Entity entity = world.spawnEntity(loc, type);

        if (!(entity instanceof LivingEntity living)) {
            LanguageManager.languageManager.sendStringText("error.entity-invalid");
            return;
        }

        EntityScannerManager.entityScannerManager.setEntityPowers(living, powers, level);
        LanguageManager.languageManager.sendStringText("mob-spawned");
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {

        List<String> list = new ArrayList<>();

        if (args.length == 2) {
            for (EntityType type : EntityType.values()) {
                Class<? extends Entity> clazz = type.getEntityClass();
                if (clazz != null && Monster.class.isAssignableFrom(clazz)) {
                    list.add(type.name().toLowerCase());
                }
            }
        } else if (args.length == 3) {
            list.add("25");
            list.add("50");
            list.add("100");
        } else if (args.length >= 4) {
            Set<String> used = new HashSet<>();

            for (int i = 3; i < args.length - 1; i++) {
                used.add(args[i].toLowerCase());
            }

            for (String power : PowerManager.powerManager.getPowers().keySet()) {
                if (!used.contains(power.toLowerCase())) {
                    list.add(power);
                }
            }
        }

        return list;
    }
}
