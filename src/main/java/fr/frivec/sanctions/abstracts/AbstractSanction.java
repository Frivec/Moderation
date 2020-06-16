package fr.frivec.sanctions.abstracts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Material;

import fr.frivec.Moderation;
import fr.frivec.database.BasicRequests;
import fr.frivec.sanctions.Ban;
import fr.frivec.sanctions.Kick;
import fr.frivec.sanctions.Mute;
import fr.frivec.sanctions.logs.Logs;

public abstract class AbstractSanction {
	
	protected SanctionType sanctionType;
	protected UUID moderator,
					victim;
	protected String reason;
	protected boolean permanent;
	protected long start, end;
	
	public AbstractSanction(SanctionType sanctionType, UUID moderator, UUID victim, String reason, boolean permanent,
			Date end) {
		
		this.sanctionType = sanctionType;
		this.moderator = moderator;
		this.victim = victim;
		this.reason = reason;
		this.permanent = permanent;
		this.start = Date.from(Instant.now()).getTime();
		this.end = end.getTime();
	
	}
	
	public AbstractSanction(SanctionType sanctionType, UUID moderator, UUID victim, String reason, boolean permanent,
			long end) {
		
		this.sanctionType = sanctionType;
		this.moderator = moderator;
		this.victim = victim;
		this.reason = reason;
		this.permanent = permanent;
		this.start = Date.from(Instant.now()).getTime();
		this.end = end;
	
	}
	
	public abstract void applySanction();
	
	public void sendInDatabase() {
		
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
			
			final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + BasicRequests.sanctionTable + " (MODERATOR, PLAYER, TYPE, DEF, REASON, START, END) VALUES (?,?,?,?,?,?,?)");
			
			preparedStatement.setString(1, this.moderator.toString());
			preparedStatement.setString(2, this.victim.toString());
			preparedStatement.setString(3, this.sanctionType.getName());
			preparedStatement.setByte(4, (byte) (this.permanent ? 1 : 0));
			preparedStatement.setString(5, this.reason);
			preparedStatement.setLong(6, this.start);
			preparedStatement.setLong(7, this.end);
			
			preparedStatement.executeUpdate();
			
			preparedStatement.close();
			
			connection.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		
		}
		
		Moderation.getInstance().getSanctions().add(this);
		
	}
	
	public void deleteFromDatabase() {
		
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
			
			final PreparedStatement statement = connection.prepareStatement("DELETE FROM " + BasicRequests.sanctionTable + " WHERE PLAYER = ? AND TYPE = ?");
			
			statement.setString(1, this.victim.toString());
			statement.setString(2, this.sanctionType.getName());
			
			statement.executeUpdate();
			
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		
		}
		
		Moderation.getInstance().getSanctions().remove(this);
		
	}
	
	public Logs toLog() {
		
		for(Logs log : Moderation.getInstance().getLogs())
			
			if(log.getVictim().equals(this.victim) && log.getModerator().equals(this.moderator) && log.getSanctionType().equals(this.sanctionType) && log.getEnd() == this.end && log.getStart() == this.start)
				
				return log;
		
		return new Logs(0, victim, moderator, reason, sanctionType, permanent, false, end, start);
		
	}
	
	public static void loadSanctions() throws InstantiationException, IllegalAccessException {
		
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
			
			final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + BasicRequests.sanctionTable);
			
			statement.executeQuery();
			
			final ResultSet resultSet = statement.getResultSet();
			
			while (resultSet.next()) {
				
				final SanctionType type = SanctionType.getTypeFromString(resultSet.getString("TYPE"));
				final UUID mod = UUID.fromString(resultSet.getString("MODERATOR")), victim = UUID.fromString(resultSet.getString("PLAYER"));
				
				boolean permanent = resultSet.getBoolean("DEF");
				
				final String reason = resultSet.getString("REASON");
				final long start = resultSet.getLong("START"), end = resultSet.getLong("END");
				
				switch (type) {
				
				case BAN:
					
					final AbstractSanction ban = new Ban(mod, victim, reason, permanent, Date.from(Instant.now()));
					
					ban.setEnd(end);
					ban.setStart(start);
					
					Moderation.getInstance().getSanctions().add(ban);
					
					break;
					
				case MUTE:
					
					final AbstractSanction mute = new Mute(mod, victim, reason, permanent, Date.from(Instant.now()));
					
					mute.setEnd(end);
					mute.setStart(start);
					
					Moderation.getInstance().getSanctions().add(mute);
					
					break;
					
				case KICK:
					
					final AbstractSanction kick = new Kick(mod, victim, reason);
					
					kick.setPermanent(permanent);
					kick.setEnd(end);
					kick.setStart(start);
					
					Moderation.getInstance().getSanctions().add(kick);
					
					break;

				default:
					break;
				}
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		
		}
		
	}
	
	public static AbstractSanction getSanction(final UUID uuid, final SanctionType sanctionType) {
		
		for(AbstractSanction sanctions : Moderation.getInstance().getSanctions())
			
			if(sanctions.getSanctionType().equals(sanctionType) && sanctions.getVictim().equals(uuid))
				
				return sanctions;
		
		return null;
		
	}
	
	public static boolean hasSanction(final UUID uuid, final SanctionType sanctionType) {
		
		for(AbstractSanction sanctions : Moderation.getInstance().getSanctions())
			
			if(sanctions.getSanctionType().equals(sanctionType) && sanctions.getVictim().equals(uuid))
				
				return true;
		
		return false;
		
	}
	
	public SanctionType getSanctionType() {
		return sanctionType;
	}

	public void setSanctionType(SanctionType sanctionType) {
		this.sanctionType = sanctionType;
	}

	public UUID getModerator() {
		return moderator;
	}

	public void setModerator(UUID moderator) {
		this.moderator = moderator;
	}

	public UUID getVictim() {
		return victim;
	}

	public void setVictim(UUID victim) {
		this.victim = victim;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public enum SanctionType {
		
		BAN("Ban", Material.BED, 13),
		MUTE("Mute", Material.PUMPKIN, 12),
		KICK("Kick", Material.ANVIL, 14);
		
		private String name;
		private Material material;
		private int slot;
		
		private SanctionType(final String name, Material material, int slot) {
			
			this.name = name;
			this.material = material;
			this.slot = slot;
			
		}
		
		public String getName() {
			return name;
		}
		
		public Material getMaterial() {
			return material;
		}
		
		public int getSlot() {
			return slot;
		}
		
		public static SanctionType getTypeFromString(final String str) {
			
			for(SanctionType type : values())
				
				if(type.getName().equalsIgnoreCase(str))
					
					return type;
			
			return null;
			
		}
		
	}
	
	public enum TimeUnit {
		
		DAY("d", Calendar.DAY_OF_MONTH),
		MONTH("Mo", Calendar.MONTH),
		HOURS("h", Calendar.HOUR_OF_DAY),
		MINUTES("m", Calendar.MINUTE);
		
		private String timeCode;
		private int calendar;
		
		private TimeUnit(String timeCode, int calendar) {
		
			this.timeCode = timeCode;
			this.calendar = calendar;
			
		}
		
		public String getTimeCode() {
			return timeCode;
		}
		
		public int getCalendar() {
			return calendar;
		}
		
		public static TimeUnit getTimeUnitBySympbole(final String symbole) {
			
			for(TimeUnit unit : values())
				
				if(unit.getTimeCode().equalsIgnoreCase(symbole))
					
					return unit;
			
			return null;
			
		}
		
	}

}
