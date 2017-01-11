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
	public static final String INSERT_USER = "INSERT INTO USERS VALUES (?,?,?,?,?)";
	public static final String UPDATE_USER = "UPDATE USERS SET USERNAME=?, PASSWORD=?, NICKNAME=?, DESCRIPTION=?, PHOTOURL=? WHERE NAME=?";
	
	// CHANNEL_MESSAGES TABLE STATEMENTS
	public static final String SELECT_PUBLIC_MESSAGE_BY_SENDER = "SELECT * FROM CHANNEL_MESSAGES WHERE SENDER=?";
	public static final String SELECT_PUBLIC_MESSAGE_BY_CHANNEL = "SELECT * FROM CHANNEL_MESSAGES WHERE CHANNEL=?";
	public static final String SELECT_PUBLIC_MESSAGE_BY_SENDER_AND_CHANNEL = "SELECT * FROM CHANNEL_MESSAGES WHERE SENDER=? AND CHANNEL=?";
	public static final String INSERT_PUBLIC_MESSAGE = "INSERT INTO CHANNEL_MESSAGES VALUES (?,?,?,?,?,?)";
	
	// DIRECT_MESSAGES TABLE STATEMENTS
	public static final String SELECT_DIRECT_MESSAGE_BY_SENDER = "SELECT * FROM DIRECT_MESSAGES WHERE SENDER=?";
	public static final String SELECT_DIRECT_MESSAGE_BY_RECEIVER = "SELECT * FROM DIRECT_MESSAGES WHERE RECEIVER=?";
	public static final String SELECT_DIRECT_MESSAGE_BY_SENDER_AND_RECEIVER = "SELECT * FROM DIRECT_MESSAGES WHERE SENDER=? AND RECEIVER=?";
	public static final String INSERT_DIRECT_MESSAGE = "INSERT INTO DIRECT_MESSAGES VALUES (?,?,?,?,?,?)";
	
	// CHANNELS TABLE STATEMENTS
	public static final String SELECT_CHANNEL_BY_NAME = "SELECT * FROM CHANNELS WHERE NAME=?";
	public static final String INSERT_CHANNEL = "INSERT INTO CHANNELS (NAME, DESCRIPTION, CREATEDBY, CREATED) VALUES (?,?,?,?)";
	public static final String UPDATE_CHANNEL_DESCRIPTION = "UPDATE CHANNELS SET DESCRIPTION=? WHERE NAME=?";
	public static final String UPDATE_CHANNEL_NAME = "UPDATE CHANNELS SET NAME=? WHERE NAME=? AND CREATEDBY=?";
	public static final String DELETE_CHANNEL = "DELETE FROM CHANNELS WHERE NAME=? AND CREATEDBY=?";
	
	// SUBSCRIPTIONS TABLE STATEMENTS
	public static final String SELECT_SUBSCRIPTON_BY_USER = "SELECT * FROM SUBSCRIPTIONS WHERE USERNAME=?";
	public static final String SELECT_SUBSCRIPTON_BY_CHANNEL = "SELECT * FROM SUBSCRIPTIONS WHERE CHANNEL=?";
	public static final String DELETE_SUBSCRIPTON = "DELETE FROM SUBSCRIPTION WHERE USERNAME=? AND CHANNEL=?";
	
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
