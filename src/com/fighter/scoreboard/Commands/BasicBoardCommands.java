package com.fighter.scoreboard.Commands;

import com.fighter.scoreboard.Main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BasicBoardCommands implements CommandExecutor {
    private final Main plugin;

    public BasicBoardCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Colorize("&aBasic&2Board&7(&c" + plugin.getDescription().getVersion() + "&7)"));
            sender.sendMessage(Colorize("&7/basicboard reload"));
            return true;
        }

        if (args.length >= 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                if(sender.hasPermission("basicboard.reload") || sender.hasPermission("basicboard.*")) {
                    plugin.reloadConfig();

                    sender.sendMessage(Colorize("{prefix} &aPlugin succesfully reloaded!"));
                    return true;
                }
                sender.sendMessage(Colorize("{prefix} &cYou don't have enough permission!"));
                return true;
            }
        }

        sender.sendMessage(Colorize("&c{prefix} Unknown command or subcommand"));
        return true;
    }

    private String Colorize(String s) {
        s = ChatColor.translateAlternateColorCodes('&', s);
        s = s.replace("{prefix}", ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("BoardSettings.board-prefix")));

        return s;
    }
}
