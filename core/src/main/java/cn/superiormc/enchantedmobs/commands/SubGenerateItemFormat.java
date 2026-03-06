package cn.superiormc.enchantedmobs.commands;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.managers.LanguageManager;
import cn.superiormc.enchantedmobs.methods.DebuildItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SubGenerateItemFormat extends AbstractCommand {

    public SubGenerateItemFormat() {
        this.id = "generateitemformat";
        this.requiredPermission =  "enchantedmobs." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        YamlConfiguration itemConfig = new YamlConfiguration();
        DebuildItem.debuildItem(player.getInventory().getItemInMainHand(), itemConfig);
        String yaml = itemConfig.saveToString();
        Bukkit.getScheduler().runTaskAsynchronously(EnchantedMobs.instance, () -> {
            Path path = new File(EnchantedMobs.instance.getDataFolder(), "generated-item-format.yml").toPath();
            try {
                Files.write(path, yaml.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        LanguageManager.languageManager.sendStringText(player, "plugin.generated");
    }
}
