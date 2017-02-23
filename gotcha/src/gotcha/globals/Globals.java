package gotcha.globals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.*;

import gotcha.controller.search.GotchaSearchEngine;
import gotcha.model.Channel;
/**
 * A Class that hold our Application Constants ,Database statements 
 * and other Global Variables.
 * @author ahmad,mohammad
 *
 */
public final class Globals {
	public static final String dbName = "gotchaDB";
	public static Context context = null;
	public static Database database;
	// System search engine instantiation
	public static GotchaSearchEngine searchEngine;
	// Public system properties
	public static Map<String, ArrayList<String>> channels = Collections.synchronizedMap(new HashMap<String, ArrayList<String>>());

	// Public variables and statements for SQL queries
	/**
	 * Users Table predefined statement.
	 */
	public static final String SELECT_ALL_USERS = "SELECT * FROM USERS",
	 SELECT_USER_BY_USERNAME = "SELECT * FROM USERS WHERE USERNAME=?",
	 SELECT_USER_BY_NICKNAME = "SELECT * FROM USERS WHERE NICKNAME=?",
	 SELECT_USER_BY_USERNAME_OR_NICKNAME = "SELECT * FROM USERS WHERE USERNAME=? OR NICKNAME=?",
	 SELECT_USER_BY_USERNAME_AND_PASSWORD = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?",
	 INSERT_USER = "INSERT INTO USERS (USERNAME, PASSWORD, NICKNAME, DESCRIPTION, PHOTO_URL, STATUS, LAST_SEEN) VALUES (?,?,?,?,?,?,?)",
	 UPDATE_USER_DETAILS = "UPDATE USERS SET USERNAME=?, PASSWORD=?, NICKNAME=?, DESCRIPTION=?, PHOTOURL=?, STATUS=?, LASTSEEN=? WHERE USERNAME=?",
	 UPDATE_USER_STATUS = "UPDATE USERS SET STATUS=?, LAST_SEEN=? WHERE NICKNAME=?",
	 LOGOFF_USERS = "UPDATE USERS SET STATUS=?, LAST_SEEN=? WHERE STATUS=?";
	
	/**
	 * Messages Table predefined statement.
	 */
	public static final String SELECT_ALL_MESSAGES = "SELECT * FROM MESSAGES",
	 SELECT_MESSAGE_BY_SENDER = "SELECT * FROM MESSAGES WHERE SENDER=?",
	 SELECT_MESSAGE_BY_RECEIVER = "SELECT * FROM MESSAGES WHERE RECEIVER=?",
	 SELECT_TEN_CHANNEL_MESSAGES = "SELECT * FROM MESSAGES WHERE PARENT_ID=0 AND RECEIVER=? ORDER BY LAST_UPDATE DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY",
	 SELECT_TEN_DIRECT_MESSAGES = "SELECT * FROM MESSAGES WHERE PARENT_ID=0 AND ((RECEIVER=? AND SENDER=?) OR (RECEIVER=? AND SENDER=?)) ORDER BY LAST_UPDATE DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY",
	 SELECT_TEN_THREAD_MESSAGES = "SELECT * FROM MESSAGES WHERE PARENT_ID=? ORDER BY LAST_UPDATE DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY",
	 SELECT_LAST_THREAD_MESSAGE = "SELECT * FROM MESSAGES WHERE PARENT_ID=? ORDER BY SENT_TIME DESC FETCH NEXT 1 ROWS ONLY",
	 SELECT_MESSAGE_REPLIES = "SELECT * FROM MESSAGES WHERE PARENT_ID=? ORDER BY LAST_UPDATE DESC",
	 SELECT_MESSAGE_BY_SENDER_AND_RECEIVER = "SELECT * FROM MESSAGES WHERE SENDER=? AND RECEIVER=?",
	 INSERT_MESSAGE = "INSERT INTO MESSAGES (PARENT_ID, SENDER, RECEIVER, TEXT, LAST_UPDATE, SENT_TIME) VALUES (?,?,?,?,?,?)",
	 UPDATE_MESSAGE_LAST_UPDATE_TIME = "UPDATE MESSAGES SET LAST_UPDATE=? WHERE ID=?";

	/**
	 * Channels Table predefined statement.
	 */
	public static final String SELECT_ALL_CHANNELS = "SELECT * FROM CHANNELS",
	 SELECT_CHANNEL_BY_NAME = "SELECT * FROM CHANNELS WHERE NAME=?",
	 INSERT_CHANNEL = "INSERT INTO CHANNELS (NAME, DESCRIPTION, CREATED_BY, CREATED_TIME) VALUES (?,?,?,?)",
	 UPDATE_CHANNEL_DESCRIPTION = "UPDATE CHANNELS SET DESCRIPTION=? WHERE NAME=?",
	 UPDATE_CHANNEL_NAME = "UPDATE CHANNELS SET NAME=? WHERE NAME=? AND CREATED_BY=?",
	 DELETE_CHANNEL = "DELETE FROM CHANNELS WHERE NAME=? AND CREATED_BY=?";
	
	/**
	 * Subscriptions Table predefined statement.
	 */
	public static final String SELECT_SUBSCRIPTON_BY_USER = "SELECT * FROM SUBSCRIPTIONS WHERE NICKNAME=?",
	 SELECT_SUBSCRIPTON_BY_CHANNEL = "SELECT * FROM SUBSCRIPTIONS WHERE CHANNEL=?",
	 SELECT_ALL_SUBSCRIPTIONS = "SELECT * FROM SUBSCRIPTIONS",
	 DELETE_SUBSCRIPTON = "DELETE FROM SUBSCRIPTIONS WHERE NICKNAME=? AND CHANNEL=?",
	 INSERT_SUBSCRIPTON = "INSERT INTO SUBSCRIPTIONS (NICKNAME, CHANNEL) VALUES (?,?)";
	
	/**
	 * A method to get channel's subscribers list.
	 * @param channel The channel name.
	 * @return The list of the channel's subscribers.
	 */
	public static ArrayList<String> getSubscribersList (String channel) {
		ArrayList<String> subscribers = new ArrayList<String>();
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(SELECT_SUBSCRIPTON_BY_CHANNEL);
			statement.setString(1, channel);
			
			ResultSet resultSet = statement.executeQuery();
		
			while (resultSet.next()) {
				subscribers.add(resultSet.getString("NICKNAME"));
			}
			
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occurred while trying to get channel subscriptions data from database!");
		}

		return subscribers;
	}
	/**
	 * A method to get a list of all the available channels.
	 * @return A list of all available channels in the database.
	 */
	public static ArrayList<String> getAllChannels () {
		ArrayList<String> channels = new ArrayList<String>();
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(SELECT_ALL_CHANNELS);
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				channels.add(resultSet.getString("NAME"));
			}
			
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occurred while trying to get channels data from database!");
		}

		return channels;
	}
	/**
	 * A method to get the specified user subscribed channels.
	 * @param nickname The specified user nickname.
	 * @return A list of the user's subscribed channels.
	 */
	public static ArrayList<Channel> getUserSubscriptions (String nickname) {
		ArrayList<Channel> channels = new ArrayList<Channel>();
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(SELECT_SUBSCRIPTON_BY_USER);
			statement.setString(1, nickname);
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				channels.add(getChannel(resultSet.getString("CHANNEL")));
			}
			
			statement.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("An error has occurred while trying to get user subscriptions data from database!");
		}
		
		return channels;
	}
	/**
	 * A method to get Channel by name.
	 * @param name Required Channel name.
	 * @return The required Channel.
	 */
	public static Channel getChannel (String name) {
		Channel channel = new Channel();
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(SELECT_CHANNEL_BY_NAME);
			statement.setString(1, name);
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				channel.name(resultSet.getString("NAME"));
				channel.description(resultSet.getString("DESCRIPTION"));
			}
			
			statement.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("An error has occurred while trying to get channel data from database!");
		}
		
		return channel;
	}
}
