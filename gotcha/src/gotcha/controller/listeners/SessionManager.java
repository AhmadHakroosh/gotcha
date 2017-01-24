package gotcha.controller.listeners;

import java.sql.Timestamp;
import java.util.ArrayList;

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
		
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		Timestamp last_seen = new Timestamp(System.currentTimeMillis());
		
		values.add(status);
		values.add(last_seen);
		
		where.add(user.username());
		
		Globals.executeUpdate(Globals.UPDATE_USER_STATUS, values, where);
    }
}
