package cn.superiormc.enchantedmobs.commands;

import cn.superiormc.enchantedmobs.managers.CommandManager;
import cn.superiormc.enchantedmobs.managers.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 &&CommandManager.commandManager.getSubCommandsMap().get(args[0]) != null) {
            AbstractCommand object = CommandManager.commandManager.getSubCommandsMap().get(args[0]);
            if (object.getOnlyInGame() && !(sender instanceof Player)) {
                LanguageManager.languageManager.sendStringText("error.in-game");
                return true;
            }
            if (object.getRequiredPermission() != null && !object.getRequiredPermission().isEmpty()
                    && !sender.hasPermission(object.getRequiredPermission())) {
                LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
                return true;
            }
            if (!object.getLengthCorrect(args.length, sender)) {
                LanguageManager.languageManager.sendStringText(sender, "error.args");
                return true;
            }
            if (sender instanceof Player) {
                object.executeCommandInGame(args, (Player) sender);
                return true;
            }
            object.executeCommandInConsole(args);
            return true;
        } else {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
            return true;
        }
    }
}
