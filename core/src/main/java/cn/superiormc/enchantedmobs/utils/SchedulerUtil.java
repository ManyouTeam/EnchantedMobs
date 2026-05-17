package cn.superiormc.enchantedmobs.utils;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class SchedulerUtil {

    private final Object task;

    public SchedulerUtil(BukkitTask task) {
        this.task = task;
    }

    public SchedulerUtil(Object task) {
        this.task = task;
    }

    public void cancel() {
        if (task == null) {
            return;
        }
        if (task instanceof BukkitTask bukkitTask) {
            bukkitTask.cancel();
            return;
        }
        try {
            task.getClass().getMethod("cancel").invoke(task);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    public static void runSync(Runnable task) {
        if (EnchantedMobs.isFolia) {
            Bukkit.getGlobalRegionScheduler().execute(EnchantedMobs.instance, task);
        } else {
            Bukkit.getScheduler().runTask(EnchantedMobs.instance, task);
        }
    }

    public static void runSync(Entity entity, Runnable task) {
        if (EnchantedMobs.isFolia) {
            entity.getScheduler().run(EnchantedMobs.instance, scheduledTask -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(EnchantedMobs.instance, task);
        }
    }

    public static void runSync(Location location, Runnable task) {
        if (EnchantedMobs.isFolia) {
            Bukkit.getRegionScheduler().run(EnchantedMobs.instance, location, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTask(EnchantedMobs.instance, task);
        }
    }

    public static void runTaskAsynchronously(Runnable task) {
        if (EnchantedMobs.isFolia) {
            Bukkit.getAsyncScheduler().runNow(EnchantedMobs.instance, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(EnchantedMobs.instance, task);
        }
    }

    public static SchedulerUtil runTaskLater(Runnable task, long delayTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runDelayed(
                    EnchantedMobs.instance, scheduledTask -> task.run(), delayTicks));
        }
        return new SchedulerUtil(Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, task, delayTicks));
    }

    public static SchedulerUtil runTaskLater(Entity entity, Runnable task, long delayTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            return new SchedulerUtil(entity.getScheduler().runDelayed(
                    EnchantedMobs.instance, scheduledTask -> task.run(), null, delayTicks));
        }
        return new SchedulerUtil(Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, task, delayTicks));
    }

    public static SchedulerUtil runTaskLater(Location location, Runnable task, long delayTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            return new SchedulerUtil(Bukkit.getRegionScheduler().runDelayed(
                    EnchantedMobs.instance, location, scheduledTask -> task.run(), delayTicks));
        }
        return new SchedulerUtil(Bukkit.getScheduler().runTaskLater(EnchantedMobs.instance, task, delayTicks));
    }

    public static SchedulerUtil runTaskLaterAsynchronously(Runnable task, long delayTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            return new SchedulerUtil(Bukkit.getAsyncScheduler().runDelayed(
                    EnchantedMobs.instance, scheduledTask -> task.run(), delayTicks * 50L, TimeUnit.MILLISECONDS));
        }
        return new SchedulerUtil(Bukkit.getScheduler().runTaskLaterAsynchronously(EnchantedMobs.instance, task, delayTicks));
    }

    public static SchedulerUtil runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            periodTicks = validTicks(periodTicks);
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    EnchantedMobs.instance, scheduledTask -> task.run(), delayTicks, periodTicks));
        }
        return new SchedulerUtil(Bukkit.getScheduler().runTaskTimer(EnchantedMobs.instance, task, delayTicks, periodTicks));
    }

    public static SchedulerUtil runTaskTimer(Entity entity, Runnable task, long delayTicks, long periodTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            periodTicks = validTicks(periodTicks);
            return new SchedulerUtil(entity.getScheduler().runAtFixedRate(
                    EnchantedMobs.instance, scheduledTask -> task.run(), null, delayTicks, periodTicks));
        }
        return runTaskTimer(task, delayTicks, periodTicks);
    }

    public static SchedulerUtil runTaskTimer(Location location, Runnable task, long delayTicks, long periodTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            periodTicks = validTicks(periodTicks);
            return new SchedulerUtil(Bukkit.getRegionScheduler().runAtFixedRate(
                    EnchantedMobs.instance, location, scheduledTask -> task.run(), delayTicks, periodTicks));
        }
        return runTaskTimer(task, delayTicks, periodTicks);
    }

    public static SchedulerUtil runTaskTimerAsynchronously(Runnable task, long delayTicks, long periodTicks) {
        if (EnchantedMobs.isFolia) {
            delayTicks = validTicks(delayTicks);
            periodTicks = validTicks(periodTicks);
            return new SchedulerUtil(Bukkit.getAsyncScheduler().runAtFixedRate(
                    EnchantedMobs.instance,
                    scheduledTask -> task.run(),
                    delayTicks * 50L,
                    periodTicks * 50L,
                    TimeUnit.MILLISECONDS));
        }
        return new SchedulerUtil(Bukkit.getScheduler().runTaskTimerAsynchronously(
                EnchantedMobs.instance, task, delayTicks, periodTicks));
    }

    public static void teleport(Entity entity, Location location) {
        if (EnchantedMobs.isFolia) {
            entity.teleportAsync(location);
            return;
        }
        entity.teleport(location);
    }

    private static long validTicks(long ticks) {
        return Math.max(1L, ticks);
    }
}
