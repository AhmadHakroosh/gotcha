package gotcha.model;

import java.sql.Timestamp;

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
}
