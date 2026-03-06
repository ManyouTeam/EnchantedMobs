package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TempBlockManager {

    public static TempBlockManager tempBlockManager;

    private final Map<String, TempBlockData> tempBlocks = new ConcurrentHashMap<>();

    public TempBlockManager() {
        tempBlockManager = this;
    }

    public void createTempBlock(Location location, Material material, int duration) {
        if (location == null || material == null) {
            return;
        }

        String key = toKey(location);

        // 已存在则不重复创建
        if (tempBlocks.containsKey(key)) {
            return;
        }

        Block block = location.getBlock();

        // 保存原始数据
        BlockData originalData = block.getBlockData().clone();

        // 设置为临时方块
        block.setType(material, false);

        // 创建定时任务
        BukkitTask task = Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, () -> removeTempBlock(location), duration);

        tempBlocks.put(key, new TempBlockData(originalData, task));
    }

    public void removeTempBlock(Location location) {
        String key = toKey(location);

        TempBlockData data = tempBlocks.remove(key);
        if (data == null) {
            return;
        }

        Block block = location.getBlock();

        // 恢复原始方块
        block.setBlockData(data.originalData, false);

        // 取消任务
        if (data.task != null) {
            data.task.cancel();
        }
    }

    public boolean isTempBlock(Location location) {
        return tempBlocks.containsKey(toKey(location));
    }

    public void clearAll() {
        for (String key : tempBlocks.keySet()) {
            Location location = fromKey(key);
            removeTempBlock(location);
        }
        tempBlocks.clear();
    }

    private String toKey(Location loc) {
        return loc.getWorld().getName() + ";"
                + loc.getBlockX() + ";"
                + loc.getBlockY() + ";"
                + loc.getBlockZ();
    }

    private Location fromKey(String key) {
        String[] split = key.split(";");
        return new Location(
                Bukkit.getWorld(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]),
                Integer.parseInt(split[3])
        );
    }

    private record TempBlockData(BlockData originalData, BukkitTask task) {

    }
}
