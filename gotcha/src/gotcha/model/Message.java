package gotcha.model;

import java.sql.Timestamp;

public class Message {
	private int id;
	private int parentId;
	private String from;
	private String to;
	private String text;
	private Timestamp lastUpdate;
	private Timestamp time;
	
	// Constructor
	public Message () {
		
	}
	
	public Message (int parentId, String from, String to, String text, Timestamp lastUpdate, Timestamp time) {
		this.parentId = parentId;
		this.from = from;
		this.to = to;
		this.text = text;
		this.lastUpdate = lastUpdate;
		this.time = time;
	}
	
	public int id () {
		return this.id;
	}
	
	public int parentId () {
		return this.parentId;
	}
	
	public String from () {
		return this.from;
	}
	
	public String to () {
		return this.to;
	}
	
	public String text () {
		return this.text;
	}
	
	public Timestamp lastUpdate () {
		return this.lastUpdate;
	}
	
	public Timestamp time () {
		return this.time;
	}
	
	public void id (int id) {
		this.id = id;
	}
	
	public void parentId (int id) {
		this.parentId = id;
	}
	
	public void from (String from) {
		this.from = from;
	}
	
	public void to (String to) {
		this.to = to;
	}
	
	public void text (String text) {
		this.text = text;
	}
	
	public void lastUpdate (Timestamp time) {
		this.lastUpdate = time;
	}
	
	public void time (Timestamp time) {
		this.time = time;
	}
}