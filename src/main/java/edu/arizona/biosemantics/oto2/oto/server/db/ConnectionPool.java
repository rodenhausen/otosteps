package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.common.log.LogLevel;

public class ConnectionPool {

	private BoneCP connectionPool;
	private Driver mySqlDriver;
	
	public ConnectionPool() throws ClassNotFoundException, SQLException  {
		Class.forName("com.mysql.jdbc.Driver");
		mySqlDriver = DriverManager.getDriver("jdbc:mysql://localhost:3306/");
		
		String jdbcUrl = "jdbc:mysql://localhost:3306/" + Configuration.databaseName + "?connecttimeout=0&sockettimeout=0&autoreconnect=true";
		
		// setup the connection pool
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(jdbcUrl); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
		config.setUsername(Configuration.databaseUser); 
		config.setPassword(Configuration.databasePassword);
		config.setMinConnectionsPerPartition(10);
		config.setMaxConnectionsPerPartition(20);
		config.setPartitionCount(2);
		config.setPoolName("otoPool");
		config.setDisableJMX(true);
		
		connectionPool = new BoneCP(config);
	}
	
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
	
	public void shutdown() {
		this.connectionPool.shutdown();
		try {
			DriverManager.deregisterDriver(mySqlDriver);
		} catch (SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		try {
		    AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for (Thread t : threadArray) {
			if(t.getName().contains("Abandoned connection cleanup thread") 
		            ||  t.getName().matches("com\\.google.*Finalizer")
		            ) {
		        synchronized(t) {
		            t.stop(); //don't complain, it works
		        }
			}
		}
	}
	
}
