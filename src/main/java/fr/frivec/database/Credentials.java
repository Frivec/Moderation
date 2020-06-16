package fr.frivec.database;

public class Credentials {
	
	private int port;
	private String host,
					client,
					dbName,
					password;
	
	public Credentials(final String host, String client, String dbName, String password, int port) {
		
		this.port = port;
		this.host = host;
		this.setClient(client);
		this.dbName = dbName;
		this.setPassword(password);
		
	}
	
	public String toMySqlURL() {
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append("jdbc:mysql://")
		.append(host)
		.append(":")
		.append(port)
		.append("/")
		.append(dbName)
		.append("?autoReconnect=true&useSSL=false");
		
		return builder.toString();
		
	}
	
	public String toRedisURL() {
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append(host)
		.append(":")
		.append(port);
		
		return builder.toString();
		
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
