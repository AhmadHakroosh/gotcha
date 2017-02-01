package gotcha.model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map.Entry;

public class User {
	// User Attributes
	private String username;
	private String password;
	private String nickName;
	private String description;
	private String photoUrl;
	private String status;
	private String lastOpenChat;
	private Timestamp signedUp;
	private Timestamp lastSeen;
	
	// Default constructor
	public User () {
		// Do nothing...
	}
	
	public User (String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User (String username, String password, String nickName) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
	}
	
	public User (String username, String password, String nickName, String description) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.description = description;
	}

	public User (String username, String password, String nickName, String description, String photoUrl, String status) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.description = description;
		this.photoUrl = photoUrl;
		this.status = status;
	}
	
	public String username () {
		return this.username;
	}
	
	public String password () {
		return this.password;
	}
	
	public String description () {
		return this.description;
	}
	
	public String nickName () {
		return this.nickName;
	}
	
	public String photoUrl () {
		return this.photoUrl;
	}
	
	public String status () {
		return this.status;
	}
	
	public String lastOpenChat () {
		return this.lastOpenChat;
	}
	
	public Timestamp signedUp () {
		return this.signedUp;
	}
	
	public Timestamp lastSeen () {
		return this.lastSeen;
	}

	public void username (String username) {
		this.username = username;
	}
	
	public void password (String password) {
		this.password = password;
	}
	
	public void description (String description) {
		this.description = description;
	}
	
	public void nickName (String nickName) {
		this.nickName = nickName;
	}
	
	public void photoUrl (String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	public void status (String status) {
		this.status = status;
	}
	
	public void lastOpenChat (String lastOpenChat) {
		this.lastOpenChat = lastOpenChat;
	}
	
	public void signedUp (Timestamp time) {
		this.signedUp = time;
	}
	
	public void lastSeen (Timestamp time) {
		this.lastSeen = time;
	}
	
	public boolean isEmpty () {
		return this.username == null && this.nickName == null;
	}
	
	// User attributes update method
	public void update (HashMap<String, String> attributes) {
		for (Entry<String, String> attribute : attributes.entrySet()) {
			switch (attribute.getKey()) {
				case "username":
					this.username = attribute.getValue();
					break;
				case "password":
					this.password = attribute.getValue();
					break;
				case "nickName":
					this.nickName = attribute.getValue();
					break;
				case "description":
					this.description = attribute.getValue();
					break;
				case "photoUrl":
					this.photoUrl = attribute.getValue();
					break;
				case "status":
					this.status = attribute.getValue();
			}
		}
	}
}
