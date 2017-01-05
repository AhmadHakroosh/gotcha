package gotcha.globals;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class CreateDataBase
 *
 */
@WebListener
public class CreateDataBase implements ServletContextListener {

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  {
    	
    	ServletContext context = event.getServletContext();
        Database database = new Database();
        try {
        	
        	database.executeUpdate("CREATE TABLE USERS ("
						+ 		"NAME VARCHAR(10) NOT NULL PRIMARY KEY,"
						+ 		"PASSWORD VARCHAR(8) NOT NULL,"
						+ 		"NICKNAME VARCHAR(20) UNIQUE,"
						+		"DESCRIPTION VARCHAR(50),"
						+ 		"PHOTO VARCHAR(512)"
						+ 	  ")"
						     );

        	database.executeUpdate("CREATE TABLE CHANNELS ("
			 			+ 		"NAME VARCHAR(40) PRIMARY KEY,"
			 			+ 		"DESCRIPTION VARCHAR(100) NOT NULL,"
			 			+ 		"SUBSCRIBERS INTEGER"
			 			+ 	  ")"
					   	     );

        	database.executeUpdate("CREATE TABLE SUBSCRIPTIONS ("
						+ 		"ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
						+ 		"USERNAME VARCHAR(10) NOT NULL REFERENCES APP.USERS(NAME) ON DELETE CASCADE,"
						+ 		"CHANNEL VARCHAR(40) NOT NULL REFERENCES APP.CHANNELS(NAME) ON DELETE CASCADE"
			 			+ 	  ")"
				   	     	 );

        	database.executeUpdate("CREATE TABLE MESSAGES ("
						+ 		"ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
						+ 		"TEXT VARCHAR(2500) NOT NULL,"
						+ 		"SENDER VARCHAR(10) NOT NULL REFERENCES APP.USERS(NAME) ON DELETE CASCADE,"
						+ 		"RECEIVER VARCHAR(10) NOT NULL REFERENCES APP.USERS(NAME) ON DELETE CASCADE,"
						+ 		"CHANNEL VARCHAR(40) NOT NULL REFERENCES APP.CHANNELS(NAME) ON DELETE CASCADE,"
						+ 		"SENT TIMESTAMP NOT NULL"
						+ 	  ")"
						     );
        	
        	context.setAttribute("database", database);
        	
		} catch (SQLException e) {
			
			System.out.println(e.getSQLState().equals("X0Y32") ? e.getMessage() : "An unknown error occured while creating the database.");
		
		}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event)  { 
         ServletContext context = event.getServletContext();
         Database database = (Database) context.getAttribute("database");
         database.shutdown();
         context.removeAttribute("database");
    }
	
}
