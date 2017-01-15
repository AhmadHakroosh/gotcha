package gotcha.globals;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

import javax.naming.*;

public final class Globals {
	public static final String dbName = "gotchaDB";
	
	// Public variables and statements for SQL queries
	// USERS TABLE STATEMENTS
	public static final String SELECT_ALL_USERS = "SELECT * FROM USERS";
	public static final String SELECT_USER_BY_USERNAME = "SELECT * FROM USERS WHERE USERNAME=?";
	public static final String SELECT_USER_BY_NICKNAME = "SELECT * FROM USERS WHERE NICKNAME=?";
	public static final String SELECT_USER_BY_USERNAME_OR_NICKNAME = "SELECT * FROM USERS WHERE USERNAME=? OR NICKNAME=?";
	public static final String SELECT_USER_BY_USERNAME_AND_PASSWORD = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";
	public static final String INSERT_USER = "INSERT INTO USERS VALUES (?,?,?,?,?,?,?)";
	public static final String UPDATE_USER_DETAILS = "UPDATE USERS SET USERNAME=?, PASSWORD=?, NICKNAME=?, DESCRIPTION=?, PHOTOURL=?, STATUS=?, LASTSEEN=? WHERE USERNAME=?";
	public static final String UPDATE_USER_STATUS = "UPDATE USERS SET STATUS=?, LAST_SEEN=? WHERE USERNAME=?";
	
	// DIRECT_MESSAGES TABLE STATEMENTS
	public static final String SELECT_MESSAGE_BY_SENDER = "SELECT * FROM MESSAGES WHERE SENDER=?";
	public static final String SELECT_MESSAGE_BY_RECEIVER = "SELECT * FROM MESSAGES WHERE RECEIVER=?";
	public static final String SELECT_MESSAGE_BY_SENDER_AND_RECEIVER = "SELECT * FROM MESSAGES WHERE SENDER=? AND RECEIVER=?";
	public static final String INSERT_MESSAGE = "INSERT INTO MESSAGES VALUES (?,?,?,?)";
	
	// CHANNELS TABLE STATEMENTS
	public static final String SELECT_ALL_CHANNELS = "SELECT * FROM CHANNELS";
	public static final String SELECT_CHANNEL_BY_NAME = "SELECT * FROM CHANNELS WHERE NAME=?";
	public static final String INSERT_CHANNEL = "INSERT INTO CHANNELS (NAME, DESCRIPTION, CREATED_BY, CREATED_TIME) VALUES (?,?,?,?)";
	public static final String UPDATE_CHANNEL_DESCRIPTION = "UPDATE CHANNELS SET DESCRIPTION=? WHERE NAME=?";
	public static final String UPDATE_CHANNEL_NAME = "UPDATE CHANNELS SET NAME=? WHERE NAME=? AND CREATED_BY=?";
	public static final String DELETE_CHANNEL = "DELETE FROM CHANNELS WHERE NAME=? AND CREATED_BY=?";
	
	// SUBSCRIPTIONS TABLE STATEMENTS
	public static final String SELECT_SUBSCRIPTON_BY_USER = "SELECT * FROM SUBSCRIPTIONS WHERE NICKNAME=?";
	public static final String SELECT_SUBSCRIPTON_BY_CHANNEL = "SELECT * FROM SUBSCRIPTIONS WHERE CHANNEL=?";
	public static final String DELETE_SUBSCRIPTON = "DELETE FROM SUBSCRIPTION WHERE NICKNAME=? AND CHANNEL=?";
	
	// Execute query via calling executeQuery method of Database
	public static final ResultSet execute (String query, List<Object> values, List<Object> where) {
		try {
			Context context = new InitialContext();
			Database database = (Database)context.lookup(dbName);
			
			try {
				return database.executeQuery(query, values, where);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Execute query via calling executeQuery method of Database
	public static final int executeUpdate (String query, List<Object> values, List<Object> where) {
		try {
			Context context = new InitialContext();
			Database database = (Database)context.lookup(dbName);
			
			try {
				return database.executeUpdate(query, values, where);
			} catch (SQLException e) {
				e.printStackTrace();
				return 0;
			}
			
		} catch (NamingException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
