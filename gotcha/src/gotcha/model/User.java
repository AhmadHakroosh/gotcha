package gotcha.model;

import java.util.HashMap;
import java.util.Map.Entry;

public class User {
	// User Attributes
	private String name;
	private String password;
	private String nickName;
	private String description;
	private String photoUrl;
	
	// Default constructor
	public User () {
		// Do nothing...
	}
	
	public User (String name, String password) {
		this.name = name;
		this.password = password;
	}

	public User (String name, String password, String nickName) {
		this.name = name;
		this.password = password;
		this.nickName = nickName;
	}
	
	public User (String name, String password, String nickName, String description) {
		this.name = name;
		this.password = password;
		this.nickName = nickName;
		this.description = description;
	}

	public User (String name, String password, String nickName, String description, String photoUrl) {
		this.name = name;
		this.password = password;
		this.nickName = nickName;
		this.description = description;
		this.photoUrl = photoUrl;
	}
	
	public String name () {
		return this.name;
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
	// User authentication method
	public boolean authenticate (String password) {
		return password.equals(this.password) ? true : false;
	}
	// User attributes update method
	public void update (HashMap<String, String> attributes) {
		for (Entry<String, String> attribute : attributes.entrySet()) {
			switch (attribute.getKey()) {
				case "name":
					this.name = attribute.getValue();
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
			}
		}
	}
}
