package fr.frivec;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.frivec.commands.ReportCommand;
import fr.frivec.commands.history.HistoryCommand;
import fr.frivec.commands.mods.FreezeCommand;
import fr.frivec.commands.mods.ModCommand;
import fr.frivec.commands.mods.PurgeCommand;
import fr.frivec.commands.mods.SlowChatCommand;
import fr.frivec.commands.sanctions.BanCommand;
import fr.frivec.commands.sanctions.KickCommand;
import fr.frivec.commands.sanctions.MuteCommand;
import fr.frivec.commands.sanctions.UnBanCommand;
import fr.frivec.commands.sanctions.UnMuteCommand;
import fr.frivec.database.BasicRequests;
import fr.frivec.database.Credentials;
import fr.frivec.database.Database;
import fr.frivec.json.GsonManager;
import fr.frivec.listeners.inventory.InventoryLogsListener;
import fr.frivec.listeners.player.ChatListener;
import fr.frivec.listeners.player.PlayerJoinListener;
import fr.frivec.listeners.player.PlayerMoveListener;
import fr.frivec.sanctions.abstracts.AbstractSanction;
import fr.frivec.sanctions.logs.Logs;

public class Moderation extends JavaPlugin {
	
	private static Moderation instance;
	
	private Set<AbstractSanction> sanctions;
	private Set<Player> moderators;
	private Set<Logs> logs;
	private GsonManager json;
	
	private Database database;
	
	private int cooldown = 0;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		saveDefaultConfig();
		
		this.sanctions = new HashSet<AbstractSanction>();
		this.logs = new HashSet<Logs>();
		this.json = new GsonManager();
		this.moderators = new HashSet<Player>();
		
		this.database = new Database(new Credentials(getConfig().getString("MySQL.host"), getConfig().getString("MySQL.username"), getConfig().getString("MySQL.database"), getConfig().getString("MySQL.password"), getConfig().getInt("MySQL.port")));
		this.database.initAllDatabaseConnections();
		
		Bukkit.getServer().getConsoleSender().sendMessage("§aConnecté à la base de donnée.");
		
		BasicRequests.createTables();
		
		try {
		
			AbstractSanction.loadSanctions();
			Logs.loadLogs();
			Bukkit.getServer().getConsoleSender().sendMessage("§aSanctions chargées.");
			
		}catch (IllegalAccessException | InstantiationException e) {
			
			e.printStackTrace();
			
		}
		
		this.getCommand("ban").setExecutor(new BanCommand());
		this.getCommand("mute").setExecutor(new MuteCommand());
		this.getCommand("kick").setExecutor(new KickCommand());
		this.getCommand("unban").setExecutor(new UnBanCommand());
		this.getCommand("unmute").setExecutor(new UnMuteCommand());
		this.getCommand("history").setExecutor(new HistoryCommand());
		this.getCommand("freeze").setExecutor(new FreezeCommand());
		this.getCommand("purge").setExecutor(new PurgeCommand());
		this.getCommand("mod").setExecutor(new ModCommand());
		this.getCommand("slowchat").setExecutor(new SlowChatCommand());
		this.getCommand("report").setExecutor(new ReportCommand());
		
		this.getServer().getPluginManager().registerEvents(new InventoryLogsListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		
		this.database.closeAllDatabaseConnections();
		
		super.onDisable();
	}
	
	public static Moderation getInstance() {
		return instance;
	}

	public Database getDatabase() {
		return database;
	}
	
	public Set<AbstractSanction> getSanctions() {
		return sanctions;
	}
	
	public Set<Logs> getLogs() {
		return logs;
	}
	
	public GsonManager getJson() {
		return json;
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
	
	public Set<Player> getModerators() {
		return moderators;
	}

}
