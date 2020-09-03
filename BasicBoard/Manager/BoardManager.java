package BasicBoard.Manager;

import BasicBoard.Main.Main;
import BasicBoard.Placeholders.Placeholders;
import net.minecraft.server.v1_16_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BoardManager {

    private final Main plugin = Main.getPlugin(Main.class);
    public static String[] colors = {"&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0", "&b", "&c", "&a", "&e", "&d"};
    private final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
    private Objective objective = null;
    public BukkitTask task;

    public void createBoardfor(Player player) {
        Placeholders pch = new Placeholders(plugin);
        Set<String> entry = scoreboard.getEntries();
        for (String entr : entry) {
            scoreboard.resetScores(entr);
        }
        if (objective == null) {
            objective = scoreboard.registerNewObjective("basicboard", "dummy");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> arrays = plugin.getConfig().getStringList("scoreboard.text");

        String displayTitle = plugin.getConfig().getString("scoreboard-displayname");

        assert displayTitle != null;
        objective.setDisplayName(pch.Colorize(player, displayTitle));

        for (int i = 0; i < arrays.size(); i++) {
            String line = arrays.get(i);

            if (line.equals("")) {
                line = line.replace("", colors[i]);
            }
            objective.getScore(pch.Colorize(player, line+colors[i])).setScore(arrays.size() - i);

            player.setScoreboard(scoreboard);

        }
    }

    public void updateBoard(Player player) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                createBoardfor(player);
            }
        }.runTaskTimerAsynchronously(plugin, 0, plugin.getConfig().getInt("BoardSettings.scoreboard-update"));
    }

    public void unregisterThings() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeBoard(player);
            HandlerList.unregisterAll(plugin);

        }
    }

    public void removeBoard(Player player) {
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
    }

}
