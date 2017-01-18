package gotcha.model;

public class Subscription {
	private int id;
	private String nickname;
	private String channel;

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

	public void id (int id) {
		this.id = id;
	}

	public void nickname (String nickname) {
		this.nickname = nickname;
	}

	public void channel (String channel) {
		this.channel = channel;
	}
}
