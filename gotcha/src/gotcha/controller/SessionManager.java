package gotcha.controller;

import java.io.IOException;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.websocket.Session;

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
    	HttpSession httpSession = event.getSession();
    	Session websocketSession = (Session)httpSession.getAttribute("WEBSOCKET_SESSION");
    	
    	if (websocketSession != null) {
    		try {
    			websocketSession.getBasicRemote().sendText("Logged out");
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
}
