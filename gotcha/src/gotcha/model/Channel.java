package gotcha.model;

import java.sql.Timestamp;

public class Channel {
	private String name;
	private String description;
	private String createdBy;
	private Timestamp createdTime;
	
	public Channel (String name, String description, String createdBy) {
		this.name = name;
		this.description = description;
		this.createdBy = createdBy;
		this.createdTime = new Timestamp(System.currentTimeMillis());
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
	
	public Timestamp createdTime () {
		return this.createdTime;
	}
	
	public void name (String name) {
		this.name = name;
	}
	
	public void description (String description) {
		this.description = description;
	}
	
	public void createdBy (String createdBy) {
		this.createdBy = createdBy;
	}
	
	public void createdTime (Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	
	public boolean isEmpty () {
		return this.name == null;
	}
}
