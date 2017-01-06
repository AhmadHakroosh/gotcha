package gotcha.globals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Database {
	// Database parameters
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String dbName = Globals.dbName;
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
		PreparedStatement statement = connection.prepareStatement(query);
		statement.executeUpdate();
	}
	// Execute a given query and return the produced data
	public ResultSet executeQuery (String query, List<Object> values, List<Object> where) throws SQLException {
		int i, j;
		
		PreparedStatement statement = connection.prepareStatement(query);
		
		for (i = 0; i < values.size(); i++) {
			statement.setObject(i+1, values.get(i));
		}
		
		for (j = 0; j < where.size(); j++) {
			statement.setObject(i+j, where.get(j));
		}
		
		return statement.executeQuery();
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
