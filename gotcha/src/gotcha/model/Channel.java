package gotcha.model;

import java.sql.Timestamp;
/**
 * A Class that represent a Channel.
 * @author mohammad
 *
 */
public class Channel {
	/**
	 * Hold the Channel name.
	 */
	private String name;
	/**
	 * Hold the Channel description.
	 */
	private String description;
	/**
	 * Hold the name of the Channel creator.
	 */
	private String createdBy;
	/**
	 * Hold the Channel creation time.
	 */
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
