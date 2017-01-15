package gotcha.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import gotcha.globals.Globals;

public class Channel {
	private String name;
	private String description;
	private String createdBy;
	private Timestamp created;
	private int subscribers;
	
	public Channel (String name, String description, String createdBy) {
		this.name = name;
		this.description = description;
		this.createdBy = createdBy;
		this.created = new Timestamp(System.currentTimeMillis());
		this.subscribers = 1;
	}
	
	public Channel () {
		
	}
	
	public String name () {
		return this.name;
	}
	
	public String description () {
		return this.description;
	}
	
	public String createdBy () {
		return this.createdBy;
	}
	
	public Timestamp created () {
		return this.created;
	}
	
	public int subscribers () {
		return this.subscribers;
	}
	
	public void subscribe () {
		this.subscribers++;
	}
	
	public void unsubscribe () {
		this.subscribers--;
	}
	
	public static ArrayList<String> getSubscribersList (String channel) {
		ArrayList<String> subscribers = new ArrayList<String>();
		
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		where.add(channel);
		
		ResultSet resultSet = Globals.execute(Globals.SELECT_SUBSCRIPTON_BY_CHANNEL, values, where);
		try {
			while (resultSet.next()) {
				subscribers.add(resultSet.getString("USER"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscribers;
	}
	
	public static ArrayList<String> getAllChannels () {
		ArrayList<String> channels = new ArrayList<String>();
		
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		ResultSet resultSet = Globals.execute(Globals.SELECT_ALL_CHANNELS, values, where);
		try {
			while (resultSet.next()) {
				channels.add(resultSet.getString("NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return channels;
	}
}
