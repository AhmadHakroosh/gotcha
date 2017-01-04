package gotcha.model;

import java.sql.Timestamp;

public class Message {
	private int id;
	private String text;
	private String sender;
	private String receiver;
	private String channel;
	private Timestamp sent;
	
	// Constructor
	public Message (int id, String text, String sender, String receiver, String channel) {
		this.id = id;
		this.text = text;
		this.sender = sender;
		this.receiver = receiver;
		this.channel = channel;
		this.sent = new Timestamp(System.currentTimeMillis());
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
	
	public String receiver () {
		return this.receiver;
	}
	
	public String channel () {
		return this.channel;
	}
	
	public Timestamp sent () {
		return this.sent;
	}
	
	public boolean sentToChannel () {
		return this.channel.isEmpty() ? false : true;
	}
}
