package fr.frivec.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import fr.frivec.Moderation;

public class Database {
	
	private DatabaseAccess access;
	
	public Database(final Credentials credentials) {
		
		this.access = new DatabaseAccess(credentials);
		
	}
	
	public void initAllDatabaseConnections() {
		
		this.access.initPool();
		
		if(this.getConnection() == null) {
			
			Bukkit.getServer().getConsoleSender().sendMessage("§cErreur. Impossible de se connecter � la base de donn�e.");
			Bukkit.getServer().getPluginManager().disablePlugin(Moderation.getInstance());
			
		}
		
	}
	
	public void closeAllDatabaseConnections() {
		
		this.access.closePool();
		
	}
	
	public Connection getConnection() {
		
		return this.access.getConnection();
		
	}
	
	public DatabaseAccess getAccess() {
		return access;
	}
	
	public void setAccess(DatabaseAccess access) {
		this.access = access;
	}
	
	private class DatabaseAccess {
		
		//Classe de configuration de HikariCP
		
		private Credentials credentials;
		private HikariDataSource dataSource;
		
		public DatabaseAccess(Credentials credentials) {
			
			this.credentials = credentials;
			
		}
		
		private void setupHikariCP() {
			
			final HikariConfig config = new HikariConfig();
			
			config.setMaximumPoolSize(3);
			config.setJdbcUrl(credentials.toMySqlURL());
			config.setUsername(credentials.getClient());  
			config.setPassword(credentials.getPassword());
			config.setMaxLifetime(600000L);
			config.setIdleTimeout(300000L);
			config.setLeakDetectionThreshold(300000000L);
			config.setConnectionTimeout(10000L);
			
			this.dataSource = new HikariDataSource(config);
			
		}
		
		public void initPool() {
			setupHikariCP();
		}
		
		public void closePool() {
			this.dataSource.close();
		}
		
		public Connection getConnection() {
			
			if(this.dataSource == null) {
				
				System.out.println("Not connected. Reconnection in proress..");
				setupHikariCP();
				
			}
			
			Connection connection = null;
			
			try {
				connection = this.dataSource.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return connection;
			
		}
		
	}

}
