package com.fighter.scoreboard.Scoreboard;

import com.fighter.scoreboard.Main.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardM {

    private static Main plugin;
    public static int scheduler;
    public static long tick = 20;
    public static Player player;

    public ScoreboardM(Main plugin) {
        this.plugin = plugin;
    }


    public static void updateScoreboard(final Player player) {
        if (plugin.getConfig().getBoolean("BoardSettings.enable-board") == true
                && !Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

                @Override
                public void run() {
                    createScorebard(player);
                }
            }, 0, tick * plugin.getConfig().getLong("BoardSettings.scoreboard-update"));
        }
    }

    private static void createScorebard(final Player player) {
        if (plugin.getConfig().getBoolean("BoardSettings.enable-board") == true
                && !Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            List<String> worlds = plugin.getConfig().getStringList("disable-worlds");
            if (!worlds.contains(player.getWorld().getName())) {
                ScoreboardM.player = player;
                ScoreboardManager boardManager = Bukkit.getScoreboardManager();
                assert boardManager != null;
                Scoreboard board = boardManager.getNewScoreboard();
                Objective objective = board.registerNewObjective("basicboard", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                objective.setDisplayName(Colorize(plugin.getConfig().getString("scoreboard-displayname")));

                List<String> rows = plugin.getConfig().getStringList("scoreboard.text");

                int score2 = rows.size();

                for (int i = 0; i < rows.size(); i++) {
                    String line = rows.get(i);
                    Score score = objective.getScore(Colorize(line));
                    score.setScore(rows.size() - i);

                }
                player.setScoreboard(board);
            }
        } else {
            Bukkit.getScheduler().cancelTask(scheduler);
        }

    }

    public static void removeBoard(final Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private static String Colorize(String s) {

        s = ChatColor.translateAlternateColorCodes('&', s);
        s = s.replace("{server_online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        s = s.replace("{server_max_online}", String.valueOf(Bukkit.getMaxPlayers()));
        if (Double.parseDouble(plugin.getTPS(0)) == 20) {
            s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&', "&2" + plugin.getTPS(0)+"*"));
        } else if (Double.parseDouble(plugin.getTPS(0)) > 17.0) {
            s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&',"&a" + plugin.getTPS(0)));
        } else if (Double.parseDouble(plugin.getTPS(0)) <= 17.15 && Double.parseDouble(plugin.getTPS(0)) != 14.15) {
            s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&',"&e" + plugin.getTPS(0)));
        } else if (Double.parseDouble(plugin.getTPS(0)) <= 14.0) {
            s = s.replace("{server_tps}", ChatColor.translateAlternateColorCodes('&',"&c" + plugin.getTPS(0)));
        }

        Long maxRam = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
        Long freeRam = Runtime.getRuntime().freeMemory() / 1024L / 1024L;
        Long usedRam = maxRam - freeRam / 1024L / 1024;
        s=s.replace("{server_max_ram}", String.valueOf(maxRam));
        s=s.replace("{server_free_ram}", String.valueOf(freeRam));
        s=s.replace("{server_used_ram}", String.valueOf(usedRam));



        List<World> world = Bukkit.getWorlds();
        for (World value : world) {
            String worldname = value.getName();
            s = s.replace("{" + "<" + worldname + ">" + "_online_players}", String.valueOf(Bukkit.getWorld(worldname).getPlayers().size()));
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
        s = s.replace("{prefix}", ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("BoardSettings.board-prefix")));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") == true
                && plugin.getConfig().getBoolean("PlaceholderAPI.enable") == true) {
            s = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, s);
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault") == true) {
            s = s.replace("{player_balance}", String.valueOf(plugin.getEconomy().getBalance(player)));
            s = s.replace("{player_prefix}", ChatColor.translateAlternateColorCodes('&',plugin.getChat().getPlayerPrefix(player)));
            s = s.replace("{player_suffix}", ChatColor.translateAlternateColorCodes('&',plugin.getChat().getPlayerSuffix(player)));
            s = s.replace("{player_group}", plugin.getChat().getPrimaryGroup(player));
        }


        return s;
    }
}
