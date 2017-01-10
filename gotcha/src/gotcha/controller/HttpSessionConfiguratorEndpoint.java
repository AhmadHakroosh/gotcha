package gotcha.controller;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/config", configurator = HttpSessionConfigurator.class)
public class HttpSessionConfiguratorEndpoint {
	
	@OnOpen
	public void open(Session session, EndpointConfig config) {
		HttpSession httpSession = (HttpSession)config.getUserProperties().get("HTTP_SESSION");
		
		if (httpSession != null) {
			httpSession.setAttribute("WEBSOCKET_SESSION", session);
		}
	}
}
