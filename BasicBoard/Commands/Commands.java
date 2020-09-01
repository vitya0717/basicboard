package BasicBoard.Commands;

import BasicBoard.Main.Main;
import BasicBoard.Placeholders.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class Commands implements CommandExecutor {

    private Main plugin = Main.getPlugin(Main.class);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Placeholders pch = new Placeholders(Main.getPlugin(Main.class));
        if(sender.hasPermission("basicboard.reload") || sender.hasPermission("basicboard.*")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getMessage().getString("Messages.command_usage"))));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if(args.length == 1) {
                    sender.sendMessage("/basicboard reload all|config|messages");
                    return true;
                }
                if (args[1].equals("config")) {
                    plugin.saveDefaultConfig();
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getMessage().getString("Messages.reload_config_config-yml"))));
                    return true;
                } else if (args[1].equalsIgnoreCase("messages")) {
                    plugin.saveMessageConfig();
                    plugin.reloadMessageConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getMessage().getString("Messages.reload_config_messages-yml"))));
                    return true;
                } else if (args[1].equalsIgnoreCase("all")) {
                    plugin.saveDefaultConfig();
                    plugin.reloadConfig();
                    plugin.saveMessageConfig();
                    plugin.reloadMessageConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getMessage().getString("Messages.reload_config_all"))));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getMessage().getString("Messages.unknown_subcommand"))));
                    return true;
                }
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getMessage().getString("Messages.unknown_command"))));
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getMessage().getString("Messages.no_permission"))));
        return true;
    }
}
