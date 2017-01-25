package gotcha.controller.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.DecodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import gotcha.model.Message;
import gotcha.globals.Globals;

@ServerEndpoint(
	value = "/{nickname}",
	decoders = MessageDecoder.class,
	encoders = MessageEncoder.class
)
public class GotChaServerEndpoint {
	
	// Track online users in the system
	private static Map<String, Session> active = Collections.synchronizedMap(new HashMap<String, Session>()); 
	
	// Decoder & Encoder
	MessageDecoder decoder = new MessageDecoder();
	MessageEncoder encoder = new MessageEncoder();
	
	/**
	 * 
	 */
	@OnOpen
	public void login (Session session, @PathParam("nickname") String user) throws IOException {
		if (session.isOpen()) {
			active.put(user, session);
		}
	}
	
	/**
	 * 
	 */
	@OnMessage
	public void send (String jsonMessage) throws IOException {
		// Convert the retrieved message into Message object
		try {
			if (decoder.willDecode(jsonMessage)) {
				Message message = decoder.decode(jsonMessage);
				// The message must be sent to a channel
				if (Globals.channels.containsKey(message.to())) {
					broadcast(message.to(), jsonMessage);
				// The message must be sent to a specific user
				} else {
					if (!message.from().equals(message.to())) {
						notify(message.to(), jsonMessage);
						notify(message.from(), jsonMessage);
					} else {
						notify(message.to(), jsonMessage);
					}
				}
			}
		} catch (DecodeException | messageDeliveryException e) {
			System.out.println("Something went wrong!");
		}	
	}

	/**
	 * 
	 */
	@OnClose
	public void logout (Session session) throws IOException {
		for (Entry<String, Session> user : active.entrySet()) {
			if (user.getValue().equals(session)) {
				active.remove(user.getKey());
			}
		}
	}
	
	@OnError
	public void log (Session session, Throwable t) {
		t.printStackTrace();
	}

	/**
	 * 
	 */
	private void broadcast (String channel, String jsonMessage) throws messageDeliveryException {
		ArrayList<String> subscribers = Globals.channels.get(channel);
		for (String subscriber : subscribers) {
			notify(subscriber, jsonMessage);
		}		
	}

	/**
	 * 
	 */
	private boolean notify (String user, String jsonMessage) throws messageDeliveryException {
		if (active.containsKey(user)) {
			Session session = active.get(user);
			try {
				session.getBasicRemote().sendText(jsonMessage);
			} catch (IOException e) {
				messageDeliveryException exception = new messageDeliveryException("Something went wrong, the message was not delivered to: @" + user);
				throw exception;
			}
			return true;
		} else {
			return false;
		}
	}
}