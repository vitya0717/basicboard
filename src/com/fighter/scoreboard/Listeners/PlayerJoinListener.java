package com.fighter.scoreboard.Listeners;

import com.fighter.scoreboard.Main.Main;
import com.fighter.scoreboard.Scoreboard.ScoreboardM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ScoreboardM.updateScoreboard(player);
        if(!ScoreboardM.players.contains(player.getName())) {
            ScoreboardM.players.add(player.getName());
        }

    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        List<String> worlds = plugin.getConfig().getStringList("disable-worlds");
        if(!worlds.contains(player.getWorld().getName())) {
            return;
        } else {
            ScoreboardM.removeBoard(player);
        }
    }

}
