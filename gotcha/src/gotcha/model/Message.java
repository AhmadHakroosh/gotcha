package gotcha.model;

import java.sql.Timestamp;

public class Message {
	private int id;
	private String from;
	private String to;
	private String text;
	private Timestamp time;
	
	// Constructor
	public Message (String from, String to, String text, Timestamp time) {
		this.from = from;
		this.to = to;
		this.text = text;
		this.time = time;
	}
	
	public int id () {
		return this.id;
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
	
	public Timestamp time () {
		return this.time;
	}
	
	public void id (int id) {
		this.id = id;
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
	
	public void time (Timestamp time) {
		this.time = time;
	}
}