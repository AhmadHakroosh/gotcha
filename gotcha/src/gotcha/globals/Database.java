package gotcha.globals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	// Database parameters
	private final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private final String dbName = "gotchaDB";
	private final String protocol = "jdbc:derby:";
	private final String username = "gotcha";
	private final String password = "gotcha";
	
	private Connection connection = null;
	
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
	
	public Connection getConnection () {
		return this.connection;
	}

	public void executeUpdate (String query) throws SQLException {
		
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	}
	
	public ResultSet executeQuery (String query) throws SQLException {
		
		Statement statement = connection.createStatement();
		return statement.executeQuery(query);
	}

	public void shutdown() {
		try {
			
			Class.forName(driver);
			this.connection = DriverManager.getConnection(protocol + dbName + ";user=" + username + ";password=" + password + ";shutdown=true");
		
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
}
