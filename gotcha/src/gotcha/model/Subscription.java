package gotcha.model;

import java.sql.Timestamp;
/**
 * A Class that represent a Subscription.
 * @author mohammad
 *
 */
public class Subscription {
	/**
	 * Hold the Subscription ID.
	 */
	private int id;
	/**
	 * Hold the Subscriber nickname.
	 */
	private String nickname;
	/**
	 * Hold the name of the Channel being Subscribed to.
	 */
	private String channel;
	/**
	 * Hold the Channel Subscribed to last read time.
	 */
	private Timestamp lastRead;

	// Constructor
	public Subscription (String nickname, String channel) {
		this.nickname = nickname;
		this.channel = channel;
	}
	
	public int id () {
		return this.id;
	}

	public String nickname () {
		return this.nickname;
	}

	public String channel () {
		return this.channel;
	}
	
	public Timestamp lastRead () {
		return this.lastRead;
	}

	public void id (int id) {
		this.id = id;
	}

	public void nickname (String nickname) {
		this.nickname = nickname;
	}

	public void channel (String channel) {
		this.channel = channel;
	}
	
	public void lastRead (Timestamp time) {
		this.lastRead = time;
	}
}
