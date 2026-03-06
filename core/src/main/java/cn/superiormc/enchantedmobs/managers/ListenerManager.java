package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.listeners.PowerListener;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new PowerListener(), EnchantedMobs.instance);
    }

}
