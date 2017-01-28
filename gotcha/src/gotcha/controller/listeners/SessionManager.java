package gotcha.controller.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import gotcha.globals.Globals;
import gotcha.model.User;

/**
 * Application Lifecycle Listener implementation class SessionManager
 *
 */
@WebListener
public class SessionManager implements HttpSessionListener {

    /**
     * Default constructor. 
     */
    public SessionManager() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent event)  { 
         // TODO Auto-generated method stub
    	System.out.println("The session with id: " + event.getSession().getId() + " has logged in.");
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent event)  { 
         // TODO Auto-generated method stub
    	System.out.println("The session with id: " + event.getSession().getId() + " has logged out.");
    	HttpSession session = event.getSession();
    	User user = (User)session.getAttribute("user");
    	logoff(user);
    }
    
    private void logoff (User user) {
    	String status = "away";
		Timestamp last_seen = new Timestamp(System.currentTimeMillis());
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.UPDATE_USER_STATUS);
			
			statement.setString(1, status);
			statement.setTimestamp(2, last_seen);
			statement.setString(3, user.username());
			
			statement.executeUpdate();
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
    }
}
