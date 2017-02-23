package gotcha.model;

import java.sql.Timestamp;
/**
 * A Class that represent a Message.
 * @author mohammad
 *
 */
public class Message {
	/**
	 * Hold the Message ID.
	 */
	private int id;
	/**
	 * Hold the Message parent ID (reply case).
	 */
	private int parentId;
	/**
	 * Hold the Message sender name.
	 */
	private String from;
	/**
	 * Hold the Message receiver name.
	 */
	private String to;
	/**
	 * Hold the Message content.
	 */
	private String text;
	/**
	 * Hold the Message last update time (when new reply added).
	 */
	private Timestamp lastUpdate;
	/**
	 * Hold the Message creation time.
	 */
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