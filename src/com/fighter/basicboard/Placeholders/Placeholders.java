package com.fighter.basicboard.Placeholders;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.fighter.basicboard.Main.Main;

import me.clip.placeholderapi.PlaceholderAPI;

public class Placeholders {

	private static Main plugin;
	public static Player player;
	private final static Pattern hex = Pattern.compile("<#([A-Fa-f0-9]){6}>");

	public Placeholders(Main plugin) {
		Placeholders.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public static String Colorize(String s) {

		Matcher matcher = hex.matcher(s);

		while (matcher.find()) {

			net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
			
			String s1 = s.substring(0, matcher.start());
			String s2 = s.substring(matcher.end());

			s = s1 + hexColor + s2;
			
			matcher = hex.matcher(s);

		}
		
		s = ChatColor.translateAlternateColorCodes('&', s);
		s = s.replace("{server_online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
		s = s.replace("{server_max_online}", String.valueOf(Bukkit.getMaxPlayers()));
		if (Double.parseDouble(plugin.getTPS(0)) == 20) {
			s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&', "&2" + plugin.getTPS(0) + "*"));
		} else if (Double.parseDouble(plugin.getTPS(0)) > 17.0) {
			s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&', "&a" + plugin.getTPS(0)));
		} else if (Double.parseDouble(plugin.getTPS(0)) <= 17.15 && Double.parseDouble(plugin.getTPS(0)) != 14.15) {
			s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&', "&e" + plugin.getTPS(0)));
		} else if (Double.parseDouble(plugin.getTPS(0)) <= 14.0) {
			s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&', "&c" + plugin.getTPS(0)));
		}

		Long maxRam = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		Long freeRam = Runtime.getRuntime().freeMemory() / 1024L / 1024L;
		Long usedRam = maxRam - freeRam / 1024L / 1024;
		s = s.replace("{server_max_ram}", String.valueOf(maxRam));
		s = s.replace("{server_free_ram}", String.valueOf(freeRam));
		s = s.replace("{server_used_ram}", String.valueOf(usedRam));

		List<World> world = Bukkit.getWorlds();
		for (World value : world) {
			String worldname = value.getName();
			s = s.replace("{" + "<" + worldname + ">" + "_online_players}",
					String.valueOf(Bukkit.getWorld(worldname).getPlayers().size()));
		}
		try {
			Object field = player.getClass().getMethod("getHandle").invoke(player);
			s = s.replace("{player_ping}", String.valueOf(field.getClass().getDeclaredField("ping").get(field)));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		s = s.replace("{player_world}", player.getWorld().getName());
		s = s.replace("{player_exp}", String.valueOf(player.getExp()));
		s = s.replace("{player_health}", String.valueOf(player.getHealth()));
		s = s.replace("{player_max_health}", String.valueOf(player.getMaxHealth()));
		s = s.replace("{player_food}", String.valueOf(player.getFoodLevel()));
		s = s.replace("{player_kills}", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)));
		s = s.replace("{mob_kills}", String.valueOf(player.getStatistic(Statistic.MOB_KILLS)));
		s = s.replace("{player_deaths}", String.valueOf(player.getStatistic(Statistic.DEATHS)));
		s = s.replace("{player_gamemode}", player.getGameMode().name());

		s = s.replace("{player_is_op}", String.valueOf(player.isOp()));
		s = s.replace("{player_item_cursor}", player.getItemOnCursor().toString());
		s = s.replace("{player_last_damage}", String.valueOf(player.getLastDamage()));
		s = s.replace("{player_location_X}", String.valueOf(player.getLocation().getBlockX()));
		s = s.replace("{player_location_Y}", String.valueOf(player.getLocation().getBlockY()));
		s = s.replace("{player_location_Z}", String.valueOf(player.getLocation().getBlockZ()));
		s = s.replace("{player_walk_speed}", String.valueOf(player.getWalkSpeed()));
		s = s.replace("{player_jump}", String.valueOf(player.getStatistic(Statistic.JUMP)));
		s = s.replace("{player_leave_game}", String.valueOf(player.getStatistic(Statistic.LEAVE_GAME)));

		if (player != null) {
			s = s.replace("{player_name}", player.getName());
		}
//		s = s.replace("{prefix}", ChatColor.translateAlternateColorCodes('&',
//				plugin.getConfig().getString("BoardSettings.board-prefix")));

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") == true
				&& plugin.getConfig().getBoolean("PlaceholderAPI.enable") == true) {
			s = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, s);
		}
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault") == true) {
			if (plugin.setupEconomy()) {
				s = s.replace("{player_balance}", String.valueOf(Main.getEconomy().getBalance(player)));
			}
			if (plugin.setupChat()) {
				s = s.replace("{player_prefix}",
						ChatColor.translateAlternateColorCodes('&', Main.getChat().getPlayerPrefix(player)));
				s = s.replace("{player_suffix}",
						ChatColor.translateAlternateColorCodes('&', Main.getChat().getPlayerSuffix(player)));
				s = s.replace("{player_group}", Main.getChat().getPrimaryGroup(player));
			}

		}

		return s;
	}
}
