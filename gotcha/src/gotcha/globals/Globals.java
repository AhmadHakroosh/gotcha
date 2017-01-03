package gotcha.globals;

import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface Globals {
	
	// Database parameters
	public final String dbName = "gotchaDB";
	public final String protocol = "jdbc:derby://localhost:1527/";
	
	// A static class which could be used for retrieving a connection for the database
	public static class DataBase {
		// A flag to help us know whether we have created the database before 
		private static boolean createdOnce = false;
		
		public static Connection getConnection () {
			Connection connection = null;
			// The database hasn't been created before
			if (!createdOnce) {
				try {
					// Create it
					connection = DriverManager.getConnection(protocol + dbName + ";create=true");
					Statement statement = connection.createStatement();
					// Add the tables
					statement.executeUpdate("CREATE TABLE USERS ("
											+ "NAME VARCHAR(10) NOT NULL PRIMARY KEY,"
											+ "PASSWORD VARCHAR(8) NOT NULL,"
											+ "NICKNAME VARCHAR(20) UNIQUE,"
											+ "DESCRIPTION VARCHAR(50),"
											+ "PHOTO VARCHAR(512)"
											+ ");"
											);
	        		 
	        		statement.executeUpdate("CREATE TABLE CHANNELS ("
					        				+ "NAME VARCHAR(40) PRIMARY KEY,"
					        				+ "DESCRIPTION VARCHAR(100) NOT NULL,"
					        				+ "SUBSCRIBERS INTEGER"
					        				+ ");"
					        				);

	        		statement.executeUpdate("CREATE TABLE SUBSCRIPTIONS ("
	        								+ "ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
	        								+ "USERNAME VARCHAR(10) NOT NULL REFERENCES APP.USERS(NAME) ON DELETE CASCADE,"
	        								+ "CHANNEL VARCHAR(40) NOT NULL REFERENCES APP.CHANNELS(NAME) ON DELETE CASCADE"
	        								+ ");"
	        								);

	        		statement.executeUpdate("CREATE TABLE MESSAGE ("
	        								+ "ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
	        								+ "SENT TIMESTAMP NOT NULL,"
	        								+ "TEXT VARCHAR(2500) NOT NULL"
	        								+ ");"
	        								);

	        		statement.executeUpdate("CREATE TABLE MESSAGES ("
	        								+ "ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
	        								+ "SENDER VARCHAR(10) NOT NULL REFERENCES APP.USERS(NAME) ON DELETE CASCADE,"
	        								+ "RECEIVER VARCHAR(10) NOT NULL REFERENCES APP.USERS(NAME) ON DELETE CASCADE,"
	        								+ "CHANNEL VARCHAR(40) NOT NULL REFERENCES APP.CHANNELS(NAME) ON DELETE CASCADE,"
	        								+ "MESSAGE INTEGER NOT NULL REFERENCES APP.MESSAGE(ID) ON DELETE CASCADE"
	        								+ ");"
	        								);
					// Commit the changes
	        		connection.commit();
	        		statement.close();
	        		createdOnce = true;
	        		
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			// The database has already been created
			} else {
				try {
					// get a connection to it
					connection = DriverManager.getConnection(protocol + dbName);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Return the connection
			return connection;
		}
		
		public static void execute (String query) throws SQLException {
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			System.out.println(resultSet.toString());
			statement.close();
		}
	}
}
