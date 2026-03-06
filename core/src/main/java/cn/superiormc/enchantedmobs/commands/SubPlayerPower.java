package cn.superiormc.enchantedmobs.commands;

import cn.superiormc.enchantedmobs.managers.LanguageManager;
import cn.superiormc.enchantedmobs.managers.PlayerPowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubPlayerPower extends AbstractCommand {

    public SubPlayerPower() {
        this.id = "power";
        this.requiredPermission = "enchantedmobs." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1, 2};
        this.requiredConsoleArgLength = new Integer[]{2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        Player target = player;
        if (args.length > 1) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", args[1]);
                return;
            }
        }
        LanguageManager.languageManager.sendStringText(player, "command.player-power", "player", target.getName(), "power", String.valueOf(PlayerPowerManager.playerPowerManager.getPlayerPower(target)));
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[1]);
            return;
        }
        LanguageManager.languageManager.sendStringText("command.player-power", "player", target.getName(), "power", String.valueOf(PlayerPowerManager.playerPowerManager.getPlayerPower(target)));
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                list.add(target.getName());
            }
        }
        return list;
    }
}
