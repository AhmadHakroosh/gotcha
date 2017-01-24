package gotcha.controller.search.model;

public class GotchaQuery {
	
	private String what;
	private String where;
	// Default constructor
	public GotchaQuery () {
		this.what = "";
		this.where = "";
	}
	// Constructor
	public GotchaQuery (String what) {
		this.what = what;
	}
	// Constructor
	public GotchaQuery (String what, String where) {
		this.what = what;
		this.where = where;
	}
	// Getters
	public String what () {
		return this.what;
	}
	
	public String where () {
		return this.where;
	}
	// toString
	public String toString () {
		return this.what + " in " + this.where;
	}
	// Methods
	public String parse () {
		return new String();
	}
	
}
