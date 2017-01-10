package gotcha.model;

import java.sql.Timestamp;

public class ChannelMessage extends Message {
	private String channel;
	private int replyFor;
	
	// Constructor
	public ChannelMessage (int id, String text, String sender, String channel, int replyFor, Timestamp sent) {
		super(id, text, sender, sent);
		this.channel = channel;
		this.replyFor = replyFor;
	}
	
	public String channel () {
		return this.channel;
	}
	
	public int replyFor () {
		return this.replyFor;
	}
}
