package gotcha.controller.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import gotcha.controller.search.GotchaSearchEngine;
import gotcha.globals.Database;
import gotcha.globals.Globals;

/**
 * Application Lifecycle Listener implementation class CreateDataBase
 *
 */
@WebListener
public class Startup implements ServletContextListener {

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  {
    	ServletContext servletContext = event.getServletContext();
    	Database.setDatabase(servletContext);
        
        try {
        	Connection connection = Globals.database.getConnection();
        	Statement statement = connection.createStatement();
        	statement.executeUpdate("CREATE TABLE USERS ("
								+ 		"USERNAME 		VARCHAR(10) NOT NULL PRIMARY KEY,"
								+ 		"PASSWORD 		VARCHAR(8) NOT NULL,"
								+ 		"NICKNAME 		VARCHAR(20) UNIQUE,"
								+		"DESCRIPTION 	VARCHAR(50),"
								+ 		"PHOTO_URL 		VARCHAR(150),"
								+ 		"STATUS 		VARCHAR(6) NOT NULL,"
								+ 		"LAST_SEEN 		TIMESTAMP NOT NULL"
								+   ")"
								   );

        	statement.executeUpdate("CREATE TABLE CHANNELS ("
					 			+ 		"NAME 			VARCHAR(30) PRIMARY KEY,"
					 			+ 		"DESCRIPTION 	VARCHAR(500),"
					 			+ 		"CREATED_BY 	VARCHAR(10) NOT NULL,"
					 			+ 		"CREATED_TIME 	TIMESTAMP NOT NULL"
					 			+ 	")"
							   	   );

        	statement.executeUpdate("CREATE TABLE SUBSCRIPTIONS ("
								+ 		"ID 			INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
								+ 		"NICKNAME 		VARCHAR(20) NOT NULL REFERENCES USERS(NICKNAME) ON DELETE CASCADE,"
								+ 		"CHANNEL 		VARCHAR(30) NOT NULL REFERENCES CHANNELS(NAME) ON DELETE CASCADE,"
								+ 	")"
						   	       );

        	statement.executeUpdate("CREATE TABLE MESSAGES ("
								+ 		"ID 			INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
								+ 		"PARENT_ID 		INTEGER DEFAULT 0,"
								+ 		"SENDER 		VARCHAR(20) NOT NULL REFERENCES USERS(NICKNAME) ON DELETE CASCADE,"
								+ 		"RECEIVER 		VARCHAR(30) NOT NULL,"
								+ 		"TEXT 			VARCHAR(500) NOT NULL,"
								+ 		"LAST_UPDATE	TIMESTAMP NOT NULL,"
								+ 		"SENT_TIME 		TIMESTAMP NOT NULL"
								+ 	")"
								   );
        	
        	connection.commit();
        	statement.close();
        	connection.close();
        	
        	System.out.println("The database was created successfully, and you're now connected to it.");
			
		} catch (SQLException e) {
			if (e.getSQLState().equals("X0Y32")) {
				System.out.println("The database is already existing, you're now connected to it.");
				ArrayList<String> allChannels = Globals.getAllChannels();
				for (String channel : allChannels) {
					Globals.channels.put(channel, Globals.getSubscribersList(channel));
				}
			} else {
				System.out.println("An unknown error has occured while trying to create the database.");
			}
		}
        // Create an instance of the search engine
        Globals.searchEngine = GotchaSearchEngine.create();
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event)  {
    	ServletContext servletContext = event.getServletContext();
    	logoffAllUsers();
    	Globals.database.shutdown(servletContext);
    }
    
    private void logoffAllUsers () {
		Timestamp last_seen = new Timestamp(System.currentTimeMillis());
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.LOGOFF_USERS);
			
			statement.setString(1, "away");
			statement.setTimestamp(2, last_seen);
			statement.setString(3, "active");
			
			statement.executeUpdate();
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
    }
}
