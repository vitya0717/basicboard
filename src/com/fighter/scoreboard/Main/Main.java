package com.fighter.scoreboard.Main;

import com.fighter.scoreboard.Commands.BasicBoardCommands;
import com.fighter.scoreboard.Listeners.PlayerJoinListener;
import com.fighter.scoreboard.Scoreboard.ScoreboardM;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_16_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public class Main extends JavaPlugin {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private final String name = Bukkit.getServer().getClass().getPackage().getName();
    private final String version = name.substring(name.lastIndexOf('.') + 1);
    private final DecimalFormat format = new DecimalFormat("##.##");

    private Object serverInstance;
    private Field tpsField;

    @Override
    public void onEnable() {

        if(!setupEconomy()) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[WARN] Vault placeholders doesn't work without vault!"));
        }
        if(getServer().getPluginManager().isPluginEnabled("Vault") == true) {
            setupPermissions();
            setupChat();
        }
        TPS();

        new ScoreboardM(this);
        new PlayerJoinListener(this);
        getCommand("basicboard").setExecutor(new BasicBoardCommands(this));
        saveDefaultConfig();

        for (Player player : Bukkit.getOnlinePlayers()) {
            ScoreboardM.updateScoreboard(player);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(ScoreboardM.scheduler);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEconomy() {
        return econ;
    }
    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }


    private Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void TPS() {
        try {
            serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
            tpsField = serverInstance.getClass().getField("recentTps");
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
    public String getTPS(int time) {
        try {
            double[] tps = ((double[]) tpsField.get(serverInstance));
            return format.format(tps[time]);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
