package gotcha.controller;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {
	
	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession)request.getHttpSession();
		
		if (httpSession != null) {
			config.getUserProperties().put("HTTP_SESSION", httpSession);
		}
	}
}
