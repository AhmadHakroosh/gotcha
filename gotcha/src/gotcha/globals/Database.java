package gotcha.globals;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

public class Database {
	private BasicDataSource dataSource;
	
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
	public static void setDatabase (ServletContext servletContext) {
		if (Globals.database == null) {
			Globals.database = new Database(servletContext);
		}
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
			dataSource.getConnection();
			dataSource = null;
			Globals.database = null;
		} catch (NamingException | SQLException e) {
			System.out.println("An error has occured while trying to shutdown gotchaDB!");
		}
	}
}
