package gotcha.model;

import java.sql.Timestamp;

public class Message {
	private int id;
	private String text;
	private String sender;
	private Timestamp sent;
	private Timestamp read;
	
	// Constructor
	public Message (int id, String text, String sender, Timestamp sent) {
		this.id = id;
		this.text = text;
		this.sender = sender;
		this.sent = sent;
	}
	
	public int id () {
		return this.id;
	}
	
	public String text () {
		return this.text;
	}
	
	public String sender () {
		return this.sender;
	}
	
	public Timestamp sent () {
		return this.sent;
	}

	public Timestamp read () {
		return this.read;
	}
}
