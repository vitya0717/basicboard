package com.fighter.basicboard.BoardManager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import com.fighter.basicboard.Main.Main;
import com.fighter.basicboard.Placeholders.Placeholders;

public class BoardManager {

	enum Board {

		GLOBAL_BOARD;

	}

	private static Main plugin = Main.getPlugin(Main.class);
	public static List<String> lines = Main.getPlugin(Main.class).getConfig().getStringList("Scoreboard.text");
	public static ScoreboardManager manager = Bukkit.getScoreboardManager();
	public static Scoreboard board;
	public static Score score;
	public static Objective obj;
	public static ArrayList<String> blanks = new ArrayList<>();
	@SuppressWarnings("unused")
	private static int global_board;

	@SuppressWarnings("deprecation")
	public static void createScoreboard(Player player, Board b) {

		switch (b) {

		case GLOBAL_BOARD:

			BoardManager.board = manager.getNewScoreboard();

			obj = board.registerNewObjective("basicboard", "dummy");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);

//			Scoreboard displayName
			obj.setDisplayName(Placeholders.Colorize(plugin.getConfig().getString("Scoreboard.displayName")));

			if (lines != null) {

				for (int i = 0; i < lines.size(); i++) {

					String line = lines.get(i);

					if (!line.equals(" ")) {
						obj.getScore(Placeholders.Colorize(line)).setScore(lines.size() - i);
					} else {
						obj.getScore(Placeholders.Colorize(line+StringUtils.repeat(" ", i))).setScore(lines.size() - i);

					}
				}
				player.setScoreboard(board);
			}

			break;

		default:
			break;

		}
	}

	public static String blank(String s) {
		for (int i = 0; i < lines.size(); i++) {

			StringUtils.repeat(" ", i);
		}
		return s;
	}

	private static int time = plugin.getConfig().getInt("Scoreboard.updateTime");

	public static void updateBoard(Player player) {
		Placeholders.player = player;
		global_board = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				BoardManager.createScoreboard(player, Board.GLOBAL_BOARD);
			}

		}, 20, time * 20);

	}

}
