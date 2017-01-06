package gotcha.globals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	// Database parameters
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String dbName = "gotchaDB";
	private static final String protocol = "jdbc:derby:";
	
	private Connection connection = null;
	// Create a Database, and connect to it
	public Database () {
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(protocol + dbName + ";create=true");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Get a connection to the database
	public Connection getConnection () {
		return this.connection;
	}
	// Execute a given query as a string
	public void execute (String query) throws SQLException {
		
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	}
	// Execute a given query and return the produced data
	public ResultSet executeQuery (String query) throws SQLException {
		Statement statement = connection.createStatement();
		return statement.executeQuery(query);
	}
	// Commit changes to database
	public void commit () throws SQLException {
		connection.commit();
	}
	// Shutdown database connection and instance
	public void shutdown() {
		try {
			
			Class.forName(driver);
			this.connection = DriverManager.getConnection(protocol + ";shutdown=true");
			
		} catch (SQLException e) {
			if (e.getSQLState().equals("XJ015")) {
				System.out.println("Shutting down database...");
			} else {
				System.out.println(e.getMessage());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
