package com.fighter.basicboard.Main;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.fighter.basicboard.BoardManager.BoardManager;
import com.fighter.basicboard.Placeholders.Placeholders;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

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

		for (Player online : Bukkit.getOnlinePlayers()) {
			BoardManager.updateBoard(online);
		}
		registerConfig();
		TPS();
		registerListeners();
		registerBoardListeners();
		registerCommands();
		setupVault();

	}

	private void registerCommands() {

	}

	private void registerBoardListeners() {
		new BoardManager();
		new Placeholders(this);

	}

	private void registerListeners() {

	}

	private void registerConfig() {
		this.saveDefaultConfig();

	}

	@Override
	public void onDisable() {

	}

	private void setupVault() {
		if (!setupEconomy()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&e[WARN] Vault placeholders doesn't work without vault!"));
		}
		if (getServer().getPluginManager().isPluginEnabled("Vault") == true) {
			setupPermissions();
			setupChat();
		}

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

	public boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) {
			return false;
		}
		perms = rsp.getProvider();
		return perms != null;
	}

	public boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		if (rsp == null) {
			return false;
		}
		chat = rsp.getProvider();
		return chat != null;
	}

	public boolean setupEconomy() {
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

}
