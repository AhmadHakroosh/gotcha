package gotcha.controller.chat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gotcha.model.Message;
import gotcha.globals.Globals;

/**
 * The WebSocket Server End-Point.
 * This class is responsible for messages delivery from users to users/channel, and storing messages in the database.
 */
@ServerEndpoint(
	value = "/{nickname}",
	decoders = MessageDecoder.class,
	encoders = MessageEncoder.class
)

public class GotChaServerEndpoint {
	
	
	/**
	 * A map that track current online users in the system
	 */
	private static Map<String, Session> active = Collections.synchronizedMap(new HashMap<String, Session>()); 
	
	// Decoder & Encoder
	MessageDecoder decoder = new MessageDecoder();
	MessageEncoder encoder = new MessageEncoder();
	
	/**
	 * log in the user to the site
	 */
	@OnOpen
	public void login (Session session, @PathParam("nickname") String user) throws IOException {
		if (session.isOpen()) {
			active.put(user, session);
			connectUser(user);
			System.out.println("The user \"" + user + "\" is now active.");
		}
	}
	
	/**
	 * Sends a message that has came to the server end-point.
	 * @param String jsonMessage - a stringified JSON object that describes the message.
	 */
	@OnMessage
	public void send (String jsonMessage) throws IOException {
		// Convert the retrieved message into Message object
		try {
			if (decoder.willDecode(jsonMessage)) {
				Message message = decoder.decode(jsonMessage);
				int messageId = store(message);
				message.id(messageId);
				String encodedMessage = recreate(jsonMessage, messageId);
				// The message must be sent to a channel
				if (Globals.channels.containsKey(message.to())) {
					broadcast(message.to(), encodedMessage);
				// The message must be sent to a specific user
				} else {
					if (!message.from().equals(message.to())) {
						notify(message.to(), encodedMessage);
						notify(message.from(), encodedMessage);
					} else {
						notify(message.to(), encodedMessage);
					}
				}
			} 
		} catch (DecodeException | messageDeliveryException e) {
			System.out.println("Something went wrong!");
		}	
	}

	/**
	 * This method is responsible for removing a logging out user from the active list.
	 * @param Session session
	 * @throws IOException
	 */
	@OnClose
	public void logout (Session session) throws IOException {
		active.values().remove(session);
	}
	
	/**
	 * Called when an error has occurred while the websocket connection was active.
	 * 
	 * @param Session session
	 * @param Throwable t
	 */
	@OnError
	public void log (Session session, Throwable t) {
		// Generally, this occurs on connection reset
		for (Entry<String, Session> one : active.entrySet()) {
			if (one.getValue().equals(session)) {
				System.out.println("The user \"" + one.getKey() + "\" has gone away.");
				forceLogOff(one.getKey());
				try {
					logout(one.getValue());
				} catch (IOException e) {
					// Something went wrong
					System.out.println("Something went wrong");
				}
			}
		}
	}

	/**
	 * This is the responsible for delivering a message to all subscribed and active users of a channel.
	 * 
	 * @param String channel - The required channel.
	 * @param String jsonMessage - a stringified JSON object of the message to be sent.
	 * 
	 * @throws messageDeliveryException
	 */
	private void broadcast (String channel, String jsonMessage) throws messageDeliveryException {
		ArrayList<String> subscribers = Globals.channels.get(channel);
		for (String subscriber : subscribers) {
			notify(subscriber, jsonMessage);
		}		
	}

	/**
	 * Notify the specified user with the required message.
	 * 
	 * @param String user - The required user.
	 * @param String jsonMessage - a stringified JSON object of the message to be sent.
	 * 
	 * @throws messageDeliveryException
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
	
	/**
	 * Stores a message into the database.
	 * 
	 * @param Message message - a message to be stored.
	 * 
	 * @return int - the stored message id.
	 * 
	 */
	private int store (Message message) {
		int messageId = 1;
		if (message.parentId() != 0) updateParent(message);
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.INSERT_MESSAGE, messageId);

			statement.setInt(1, message.parentId());
			statement.setString(2, message.from());
			statement.setString(3, message.to());
			statement.setString(4, message.text());
			statement.setTimestamp(5, message.lastUpdate());
			statement.setTimestamp(6, message.time());
			
			statement.executeUpdate();
			
			ResultSet id = statement.getGeneratedKeys();
			if (id.next()) {
				messageId = id.getInt(1);
			}
			
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e ){
			System.out.println("An error has occured while trying to execute the query!");
		}
		
		return messageId;
	}
	/**
	 * Updates the message parent LastUpdate (in case it is a reply).
	 * 
	 * @param Message message - a child message to update its parent
	 * 
	 */
	private void updateParent (Message message) {
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.UPDATE_MESSAGE_LAST_UPDATE_TIME);

			statement.setTimestamp(1, message.time());
			statement.setInt(2, message.parentId());
			
			statement.executeUpdate();
			
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e ) {
			System.out.println("An error has occured while trying to execute the query!");
		}
	}
	/**
	 * Logs the user off, update his status to "away" , last seen to "now".
	 * Called only when the closes the application window without logging out, or on an error.
	 * 
	 * @param String user - the user to be logged off.
	 * 
	 */
    private void forceLogOff (String user) {
    	String status = "away";
		Timestamp last_seen = new Timestamp(System.currentTimeMillis());
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.UPDATE_USER_STATUS);
			
			statement.setString(1, status);
			statement.setTimestamp(2, last_seen);
			statement.setString(3, user);
			
			statement.executeUpdate();
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
    }
    /**
	 * update the new logged in user's status (active) , 
	 * last seen(now) in the USERS database.
	 * 
	 * @param String user - the user to be set as active.
	 */
    private void connectUser (String user) {
    	String status = "active";
		Timestamp last_seen = new Timestamp(System.currentTimeMillis());
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.UPDATE_USER_STATUS);
			
			statement.setString(1, status);
			statement.setTimestamp(2, last_seen);
			statement.setString(3, user);
			
			statement.executeUpdate();
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
    }
    /**
     * Recreates the message before sending it to the destination, it adds its correct id.
     * 
     * @param String textMessage - a stringified JSON object of the message.
     * @param int id - the message id
     * 
     * @return String the modified message
     */
    public String recreate (String textMessage, int id) {
    	JsonParser parser = new JsonParser();
    	JsonElement jsonMessage = parser.parse(textMessage);
    	JsonElement messageId = parser.parse("{\"id\": \"" + id + "\"}");
    	JsonObject messageIdObject = messageId.getAsJsonObject();
    	JsonObject messageObject = new JsonObject();
    	messageObject.add("message", jsonMessage);
    	messageObject = messageObject.getAsJsonObject("message");
    	messageObject.add("id", messageIdObject.get("id"));
    	return messageObject.toString();
    }
}