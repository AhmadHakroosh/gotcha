package gotcha.globals;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
/**
 * A Class that hold Database related variables and methods.
 * @author Ahmad Hakroosh, Mohammad Dahamshi
 *
 */
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
	/**
	 * Get a connection to the database.
	 * @return Connection to the database.
	 */
	public Connection getConnection () {
		Connection connection = null;
		
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to connect to gotchaDB!");
		}
		
		return connection;
	}
	/**
	 * Shutdown database connection and instance
	 * @param servletContext
	 */
	public void shutdown(ServletContext servletContext) {
		try {
			String gotchaDB = servletContext.getInitParameter(Globals.dbName);
			dataSource = (BasicDataSource) Globals.context.lookup(gotchaDB + "Close");
			dataSource.getConnection();
			dataSource = null;
			Globals.database = null;
		} catch (NamingException e) {
			System.out.println("An error has occured while trying to shutdown gotchaDB!");
		} catch (SQLException e) {
			// The database was shutdown successfully
			if (e.getErrorCode() == 0) {
				System.out.println("The database was shutdown successfully.");
			// Something went wrong
			} else {
				System.out.println("An error has occurred while trying to shutdown the database.");
			}
		}
	}
}
