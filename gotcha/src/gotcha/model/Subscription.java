package gotcha.model;

public class Subscription {
	private int id;
	private String username;
	private String channel;

	// Constructor
	public Subscription (int id, String username, String channel) {
		this.id = id;
		this.username = username;
		this.channel = channel;
	}
	
	public int id () {
		return this.id;
	}

	public String username () {
		return this.username;
	}

	public String channel () {
		return this.channel;
	}

	public void id (int id) {
		this.id = id;
	}

	public void username (String username) {
		this.username = username;
	}

	public void channel (String channel) {
		this.channel = channel;
	}
}
