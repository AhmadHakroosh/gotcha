package gotcha.model;

import java.sql.Timestamp;

public class Message {
	private int id;
	private String from;
	private String to;
	private String text;
	private String reply_for;
	private String reply_text;
	private Timestamp time;
	
	// Constructor
	public Message () {
		
	}
	
	public Message (String from, String to, String text, String reply_for, String reply_text, Timestamp time) {
		this.from = from;
		this.to = to;
		this.text = text;
		this.reply_for = reply_for;
		this.reply_text = reply_text;
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
	
	public String reply_for () {
		return this.reply_for;
	}
	
	public String reply_text () {
		return this.reply_text;
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
	
	public void reply_for (String reply_for) {
		this.reply_for = reply_for;
	}
	
	public void reply_text (String reply_text) {
		this.reply_text = reply_text;
	}
	
	public void time (Timestamp time) {
		this.time = time;
	}
}