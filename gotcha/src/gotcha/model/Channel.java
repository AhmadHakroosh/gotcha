package gotcha.model;

public class Channel {
	private String name;
	private String description;
	private int subscribers;
	
	public Channel (String name) {
		this.name = name;
		this.subscribers = 0;
	}
	
	public Channel (String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String name () {
		return this.name;
	}
	
	public String description () {
		return this.description;
	}
	
	public int subscribers () {
		return this.subscribers;
	}
	
	public void subscribe () {
		this.subscribers++;
	}
	
	public void unsubscribe () {
		this.subscribers--;
	}
}
