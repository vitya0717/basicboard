package BasicBoard.Listeners;

import BasicBoard.Main.Main;
import BasicBoard.Manager.BoardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;


public class BoardListeners implements Listener {
    private final Main plugin;
    public BoardListeners(Main plugin) {
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final BoardManager boardManager = new BoardManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
       plugin.update(event.getPlayer());

    }

    @EventHandler
    public void onClearBoard(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        List<String> worlds = plugin.getConfig().getStringList("disable-worlds");
        if(worlds.contains(player.getWorld().getName())) {
            BoardManager boardManager = new BoardManager();
            boardManager.removeBoard(player);
        }
    }

}
