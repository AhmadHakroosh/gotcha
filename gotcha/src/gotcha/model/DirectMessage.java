package gotcha.model;

import java.sql.Timestamp;

public class DirectMessage extends Message {
	private String receiver;
	
	// Constructor
	public DirectMessage (int id, String text, String sender, String receiver, Timestamp sent) {
		super(id, text, sender, sent);
		this.receiver = receiver;
	}
	
	public String receiver () {
		return this.receiver;
	}
}
