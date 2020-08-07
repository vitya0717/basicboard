package com.fighter.scoreboard.Scoreboard;

import com.fighter.scoreboard.Main.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardM {

	private static Main plugin;
	public static int scheduler;
	public static long tick = 20L;
	public static Player player;
	private static ScoreboardManager boardManager;
	private static Scoreboard board;
	public static ArrayList<String> players = new ArrayList<>();
	public static String[] colors = { "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0", "&b", "&c", "&a",
			"&e", "&d" };
	private static List<String> displayTitle;
	private static String finalTitle = "";
	private static int i = 0;

	public ScoreboardM(Main plugin) {
		ScoreboardM.plugin = plugin;
	}

	public static void updateScoreboard(final Player player) {
		Placeholders.player = player;

		if (plugin.getConfig().getBoolean("BoardSettings.enable-board") == true
				&& !Bukkit.getServer().getOnlinePlayers().isEmpty()) {

			Bukkit.getWorlds();

			scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

				@Override
				public void run() {

					createDefaultScorebard(player);
				}
			}, 20L, plugin.getConfig().getLong("BoardSettings.scoreboard-update") * tick);
		}

	}

	private static void createDefaultScorebard(Player player) {
		ScoreboardM.player = player;
		if (plugin.getConfig().getBoolean("BoardSettings.enable-board") == true
				&& !Bukkit.getServer().getOnlinePlayers().isEmpty()) {

			List<String> worlds = plugin.getConfig().getStringList("disable-worlds");

			if (!worlds.contains(player.getWorld().getName())) {

				ScoreboardM.player = player;
				boardManager = Bukkit.getScoreboardManager();
				assert boardManager != null;
				board = boardManager.getNewScoreboard();

				@SuppressWarnings("deprecation")
				Objective objective = board.registerNewObjective("basicboard", "dummy");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);

				displayTitle = plugin.getConfig().getStringList("scoreboard-displayname");

				if (displayTitle.size() == i) {
					i = 0;
				}

				if (displayTitle.size() > i) {

					objective.setDisplayName(Placeholders.Colorize(displayTitle.get(i)));

					finalTitle = displayTitle.get(i);

				} else {
					if (finalTitle == null) {
						objective.setDisplayName(Placeholders.Colorize("No title"));
					} else {
						objective.setDisplayName(Placeholders.Colorize(finalTitle));
					}
				}

				List<String> rows = plugin.getConfig().getStringList("scoreboard.text");

				rows.size();

				for (int i = 0; i < rows.size(); i++) {
					String line = rows.get(i);

					if (line.equals("")) {
						line = line.replace("", colors[i]);
					}
					Score score = objective.getScore(Placeholders.Colorize(line));
					score.setScore(rows.size() - i);

				}
				player.setScoreboard(board);
			}

			i++;

		} else {
			Bukkit.getScheduler().cancelTask(scheduler);
		}

	}

	public static void removeBoard(final Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
}
