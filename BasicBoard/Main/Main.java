package BasicBoard.Main;

import BasicBoard.Commands.Commands;
import BasicBoard.Listeners.BoardListeners;
import BasicBoard.Manager.BoardManager;
import BasicBoard.Placeholders.Placeholders;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.List;

public class Main extends JavaPlugin {

    private FileConfiguration fileConfiguration;
    private File file;
    private static Economy econ = null;
    private static Permission perms = null;
    public static Chat chat = null;
    private final String name = Bukkit.getServer().getClass().getPackage().getName();
    private final String version = name.substring(name.lastIndexOf('.') + 1);
    private final DecimalFormat format = new DecimalFormat("##.##");
    private Object serverInstance;
    private Field tpsField;
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    @Override
    public void onEnable() {
        Placeholders pch = new Placeholders(this);
        TPS();
        registerListeners();
        registerConfig();
        registerCommands();
        Bukkit.getOnlinePlayers().forEach(this::update);
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[WARN] Vault placeholders doesn't work without vault!"));
        }
        if (getServer().getPluginManager().isPluginEnabled("Vault") == true) {
            setupPermissions();
            setupChat();
        }
        console.sendMessage(ChatColor.translateAlternateColorCodes('&',"&9&lBasic&b&lBoard &7> &aThe plugin has been &2enabled &aon this server!"));

    }
    public void update(Player player) {
        BoardManager boardManager = new BoardManager();
        List<String> worlds = getConfig().getStringList("disable-worlds");
        if (getConfig().getBoolean("BoardSettings.enable-board") && !Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            if(!worlds.contains(player.getWorld().getName())) {
                boardManager.updateBoard(player);
            } else {
                if(boardManager.task != null) {
                    Bukkit.getScheduler().cancelTask(boardManager.task.getTaskId());
                }
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }
    }

    private void registerListeners() {
        new BoardManager();
        new BoardListeners(this);
    }

    private void registerConfig() {
        registerMessageConfig();
        saveDefaultConfig();
    }

    private void registerCommands() {
        getCommand("basicboard").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        BoardManager boardManager = new BoardManager();
        boardManager.unregisterThings();
        console.sendMessage(ChatColor.translateAlternateColorCodes('&',"&9&lBasic&b&lBoard &7> &cThe plugin has been &4disabled &con this server!"));
    }

    public FileConfiguration getMessage() {
        return fileConfiguration;
    }

    public void registerMessageConfig() {
         file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        fileConfiguration = new YamlConfiguration();

        try {
            fileConfiguration.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveMessageConfig() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadMessageConfig() {
        try {
            fileConfiguration.save(file);
            fileConfiguration.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
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
