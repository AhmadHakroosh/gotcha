package gotcha.globals;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

public class Database {
	private BasicDataSource dataSource;
	
	private static Database database = null;
	
	// Create a Database, and connect to it
	private Database (ServletContext servletContext) {
		try {
			Globals.context = new InitialContext();
			String gotchaDB = servletContext.getInitParameter(Globals.dbName);
			dataSource = (BasicDataSource) Globals.context.lookup(gotchaDB + "Open");
			Connection connection = getConnection();
			connection.close();
		} catch (SQLException | NamingException e) {
			System.out.println("An unknown error has occured while trying to connect to gochaDB!");
		}
	}
	// Get Database instance
	public static Database getDatabase (ServletContext servletContext) {
		if (database == null) {
			database = new Database(servletContext);
		}
		return database;
	}
	// Get a connection to the database
	public Connection getConnection () {
		Connection connection = null;
		
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to connect to gotchaDB!");
		}
		
		return connection;
	}
	// Shutdown database connection and instance
	public void shutdown(ServletContext servletContext) {
		try {
			String gotchaDB = servletContext.getInitParameter(Globals.dbName);
			dataSource = (BasicDataSource) Globals.context.lookup(gotchaDB + "Close");
			Connection connection = getConnection();
			connection.close();
			dataSource = null;
		} catch (SQLException | NamingException e) {
			System.out.println("An error has occured while trying to shutdown gotchaDB!");
		}
	}
}
