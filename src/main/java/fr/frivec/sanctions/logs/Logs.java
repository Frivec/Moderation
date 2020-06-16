package fr.frivec.sanctions.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.frivec.Moderation;
import fr.frivec.database.BasicRequests;
import fr.frivec.items.ItemCreator;
import fr.frivec.sanctions.abstracts.AbstractSanction.SanctionType;
import fr.frivec.utils.Utils;

public class Logs {
	
	private int id;
	private UUID victim, moderator;
	private String reason;
	private SanctionType sanctionType;
	private boolean permanent, finished;
	private long end, start;
	
	public Logs(int id, UUID victim, UUID moderator, String reason, SanctionType sanctionType, boolean permanent, boolean finished, long end, long start) {
		
		this.id = id;
		this.victim = victim;
		this.moderator = moderator;
		this.reason = reason;
		this.sanctionType = sanctionType;
		this.permanent = permanent;
		this.finished = finished;
		this.end = end;
		this.start = start;
	
	}
	
	public void sendInDatabase() {
			
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
				
			final PreparedStatement saveStatement = connection.prepareStatement("INSERT INTO " + BasicRequests.logsTable + " (MODERATOR, PLAYER, TYPE, DEF, REASON, START, END, FINISH) VALUES (?,?,?,?,?,?,?,?)");
				
			saveStatement.setString(1, this.moderator.toString());
			saveStatement.setString(2, this.victim.toString());
			saveStatement.setString(3, this.sanctionType.getName());
			saveStatement.setByte(4, this.permanent ? (byte) 1 : 0);
			saveStatement.setString(5, this.reason);
			saveStatement.setLong(6, this.start);
			saveStatement.setLong(7, this.end);
			saveStatement.setByte(8, this.finished ? (byte) 1 : 0);
			
			saveStatement.executeUpdate();
			saveStatement.close();
			
			final PreparedStatement idStatement = connection.prepareStatement("SELECT ID FROM " + BasicRequests.logsTable + " WHERE PLAYER = ? AND TYPE = ? AND START = ?");
			
			idStatement.setString(1, this.victim.toString());
			idStatement.setString(2, this.sanctionType.getName());
			idStatement.setLong(3, this.start);
			
			idStatement.executeQuery();
			
			if(idStatement.getResultSet().next())
				
				this.id = idStatement.getResultSet().getInt("ID");
			
			idStatement.close();
			
			connection.close();
				
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		Moderation.getInstance().getLogs().add(this);
		
	}
	
	public void updateLog() {
		
		this.finished = true;
		
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
			
			final PreparedStatement statement = connection.prepareStatement("UPDATE " + BasicRequests.logsTable + " SET FINISH = ? WHERE ID = ?");
			
			statement.setByte(1, (byte) 1);
			statement.setInt(2, this.id);
			
			statement.executeUpdate();
			statement.close();
			
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	public static void loadLogs() {
		
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
			
			final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + BasicRequests.logsTable);
			
			statement.executeQuery();
			
			final ResultSet resultSet = statement.getResultSet();
			
			while(resultSet.next()) {
				
				final Logs log = new Logs(resultSet.getInt("ID"), UUID.fromString(resultSet.getString("PLAYER")), UUID.fromString(resultSet.getString("MODERATOR")), resultSet.getString("REASON"),
						SanctionType.getTypeFromString(resultSet.getString("TYPE")), resultSet.getBoolean("DEF"), resultSet.getBoolean("FINISH"), resultSet.getLong("END"), resultSet.getLong("START"));
				
				Moderation.getInstance().getLogs().add(log);
				
			}
			
			statement.close();
			
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	public static boolean resetAllLogs(final UUID uuid) {
			
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
				
			final PreparedStatement statement = connection.prepareStatement("DELETE FROM " + BasicRequests.logsTable + " WHERE PLAYER = ?");
				
			statement.setString(1, uuid.toString());
			statement.executeUpdate();
			
			statement.close();
			connection.close();
			
			for(final Logs logs : Moderation.getInstance().getLogs())
				
				if(logs.getVictim().equals(uuid))
					
					Moderation.getInstance().getLogs().remove(logs);
				
			return true;
				
		} catch (SQLException e) {
			e.printStackTrace();
				
			return false;
		}
		
	}
	
	public static void openIndexMenu(final String targetName, final Player mod) {
		
		final Set<Logs> logsList = Moderation.getInstance().getLogs();
		final Inventory inventory = Bukkit.createInventory(null, 9*4, "§aIndex historique: " + targetName);
		final UUID targetUUID = UUID.fromString(Utils.addUUIDDashes(Moderation.getInstance().getJson().getUUIDFromMojang(targetName)));
		
		boolean hasLogs = false;
		
		for(Logs log : logsList)
			
			if(log.getVictim().equals(targetUUID))
				
				hasLogs = true;
		
		if(!hasLogs) {
			
			mod.sendMessage("§cCe joueur n'a aucun historique sur le serveur.");
			return;
			
		}
			
		int mute = 0, kick = 0, ban = 0;
			
		for(Logs log : logsList) {
			
			if(log.getSanctionType().equals(SanctionType.BAN))
					
				ban++;
				
			else if(log.getSanctionType().equals(SanctionType.MUTE))
					
				mute++;
				
			else if(log.getSanctionType().equals(SanctionType.KICK))
					
				kick++;
			
			for(int i = 0; i < inventory.getSize(); i++)
				
				inventory.setItem(i, new ItemCreator(Material.STAINED_GLASS_PANE, 1).setDisplayName("").build());
			
			final ItemStack head = new ItemCreator(Material.SKULL_ITEM, 1).skull(targetName).setDisplayName("§c§oJoueur: §4§l" + targetName).build(),
					viewSanctions = new ItemCreator(Material.ARROW, 1).setDisplayName("§6§uVoir les sanctions").setLores(new String[] {"§eClique pour voir les différentes sanctions de §4" + targetName + "§e."}).build();
			
			for(SanctionType type : SanctionType.values()) {
				
				int sanctionNumber = 0;
				
				if(type.equals(SanctionType.BAN))
					
					sanctionNumber = ban;
				
				else if(type.equals(SanctionType.MUTE))
					
					sanctionNumber = mute;
				
				else if(type.equals(SanctionType.KICK))
					
					sanctionNumber = kick;
				
				final ItemStack item = new ItemCreator(type.getMaterial(), 1).setDisplayName("§aNombre de §2" + type.getName()).setLores(new String[] {"§4" + targetName + " §aa été " + type.getName().toLowerCase() + " §c" + sanctionNumber + " §afois.", "§6§uVoir les sanctions"}).build();
				
				inventory.setItem(type.getSlot(), item);
				
			}
			
			inventory.setItem(4, head);
			inventory.setItem(22, viewSanctions);
			
		}
		
		mod.sendMessage("§aHistorique chargé. Ouverture de l'index.");
		mod.closeInventory();
		mod.openInventory(inventory);
		
	}
	
	public static void displayLogs(final String targetName, final Player mod, int page, Filter filter) {
		
		mod.sendMessage("§aChargement du casier judiciaire en cours. Veuillez patienter...");
		
		final Moderation moderation = Moderation.getInstance();
		
		final Set<Logs> logs = moderation.getLogs();
		
		if(logs == null || logs.isEmpty()) {
			
			mod.sendMessage("§cErreur. La requête a mal été exécutée. Veuillez réessayer. Si l'erreur s'affiche trop de fois, merci de contacter un administrateur ou un développeur.");
			return;
			
		}
			
		mod.sendMessage("§aTous les logs ont été chargés. Veuillez patienter pendant la préparation de l'affichage...");
			
		final Inventory inventory = Bukkit.createInventory(null, 9*6, "§bHistorique - " + targetName);
		
		addLogsInInventory(moderation, filter.getSanctionType(), targetName, logs, page, inventory);
		
		inventory.setItem(48, new ItemCreator(Material.SKULL_ITEM, 1).skull("MHF_ArrowLeft").setDisplayName("§aPage précédente §c(en maintenance)").build());
		inventory.setItem(49, new ItemCreator(Material.ARROW, 1).setDisplayName("§cRetour à l'index").build());
		inventory.setItem(50, new ItemCreator(Material.SKULL_ITEM, 1).skull("MHF_ArrowRight").setDisplayName("§aPage suivante §c(en maintenance)").build());
			
		//TODO Le joueur peut effacer l'historique
		inventory.setItem(53, new ItemCreator(Material.BARRIER, 1).setDisplayName("§cEffacer l'historique").build());
		
		mod.closeInventory();
		mod.openInventory(inventory);
			
		mod.sendMessage("§aLe casier judiciaire de §c" + targetName + " §aa été chargé avec succès.");
		
		return;
		
	}
	
	private static void addLogsInInventory(final Moderation moderation, SanctionType type, String targetName, Set<Logs> logs, int page, Inventory inventory) {
		
		int i = 0;
				
		for(Logs log : logs) {
			
			if(log != null) {
				
				final String modName = moderation.getJson().getName(log.getModerator().toString());
				
				boolean def;
					
				if(log.isPermanent())
					
					def = true;
					
				else 
						
					def = false;
					
				if(type != null && log.getSanctionType().equals(type)) {
						
					final ItemStack item = new ItemCreator(log.getSanctionType().getMaterial(), 1)
							.setDisplayName("§a" + new SimpleDateFormat("dd/MM/YY").format(new Date(log.getStart())))
							.setLores(new String[] {"§cJoueur: " + targetName, "§6Modérateur: " + modName, "§6Raison: " + log.getReason(), "§cPermanent: " + def, "§cFin: " + new SimpleDateFormat("dd/MM/YY à HH:mm").format(new Date(log.getEnd()))})
							.build();
							
					inventory.setItem(i, item);
					
					i++;
						
				}else if(type == null) {
					
					final ItemStack item = new ItemCreator(log.getSanctionType().getMaterial(), 1)
							.setDisplayName("§a" + new SimpleDateFormat("dd/MM/YY").format(new Date(log.getStart())))
							.setLores(new String[] {"§cJoueur: " + targetName, "§6Modérateur: " + modName, "§6Raison: " + log.getReason(), "§cPermanent: " + def, "§cFin: " + new SimpleDateFormat("dd/MM/YY à HH:mm").format(new Date(log.getEnd()))})
							.build();
							
					inventory.setItem(i, item);
					
					i++;
					
				}else
					continue;
					
			}else
								
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		if(inventory.getContents().length == 0)
			
			inventory.setItem(22, new ItemCreator(Material.SIGN, 1).setDisplayName("§cAucune sanction trouvée :c").setLores(new String[] {"§bAucune sanction n'a été trouvée", "§bavec ce filtre. Essayez avec un autre."}).build());
		
	}
	
	public enum Filter {
		
		ALL(null),
		BAN("BAN"),
		MUTE("MUTE"),
		KICK("KICK"),
		WARN("WARN"),
		REPORT("REPORT");
		
		private String typeName;
		
		private Filter(final String typeName) {
			this.typeName = typeName;
		}
		
		public SanctionType getSanctionType() {
			
			for(SanctionType sanctionType : SanctionType.values())
				
				if(this.typeName != null && this.typeName.equalsIgnoreCase(sanctionType.getName()))
					
					return sanctionType;
					
			return null;
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UUID getVictim() {
		return victim;
	}

	public void setVictim(UUID victim) {
		this.victim = victim;
	}

	public UUID getModerator() {
		return moderator;
	}

	public void setModerator(UUID moderator) {
		this.moderator = moderator;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public SanctionType getSanctionType() {
		return sanctionType;
	}

	public void setSanctionType(SanctionType sanctionType) {
		this.sanctionType = sanctionType;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

}
