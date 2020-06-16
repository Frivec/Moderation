package fr.frivec.database;

import java.sql.Connection;
import java.sql.SQLException;

import fr.frivec.Moderation;

public class BasicRequests {
	
	public static String sanctionTable = "SANCTIONS", logsTable = "SANCTIONS_HISTORY";
	
	public static void createTables() {
		
		createTableIfNotExists(sanctionTable, "MODERATOR varchar(36) NOT NULL, PLAYER varchar(36) NOT NULL, TYPE varchar(10) NOT NULL, DEF tinyint(1) NOT NULL, REASON varchar(150) NOT NULL, START bigint(20) NOT NULL, END bigint(20) NOT NULL");
		createTableIfNotExists(logsTable, "ID int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT, MODERATOR varchar(36) NOT NULL, PLAYER varchar(36) NOT NULL, TYPE varchar(4) NOT NULL, DEF tinyint(1) NOT NULL, REASON varchar(150) NOT NULL, START bigint(20) NOT NULL, END bigint(20) NOT NULL, FINISH tinyint(1) NOT NULL");
		
	}
	
	public static void createTableIfNotExists(final String tableName, final String var) {
		
		try (Connection connection = Moderation.getInstance().getDatabase().getConnection()) {
			
			connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" + var + ")").executeUpdate();
			connection.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
