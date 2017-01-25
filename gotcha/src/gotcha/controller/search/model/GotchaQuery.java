package gotcha.controller.search.model;

import java.util.ArrayList;

import org.apache.lucene.search.BooleanClause;

public class GotchaQuery {
	
	private String in;
	private String what;
	
	private BooleanClause.Occur[] flags;
	
	// Default constructor
	public GotchaQuery () {
		
	}
	// Constructor
	public GotchaQuery (String in, String what) {
		this.in = in;
		this.what = what;
	}
	// Setters
	public void in (String in) {
		this.in = in;
	}
	
	public void what (String what) {
		this.what = what;
	}
	// Getters
	public String in () {
		return this.in;
	}
	
	public String what () {
		return this.what;
	}
	
	public String[] fields () {
		ArrayList<String> fields= new ArrayList<String>();

		if (in != null) fields.add("type");
		if (in.equals("message")) {
			fields.add("text");
		} else {
			fields.add("username");
			fields.add("nickname");
			fields.add("name");
			fields.add("description");
		}
		
		String [] fieldArray = new String [fields.size()];
		fieldArray = fields.toArray(fieldArray);
		return fieldArray;
	}
	
	public String[] queries () {
		ArrayList<String> queries = new ArrayList<String>();
		ArrayList<BooleanClause.Occur> flagsList = new ArrayList<BooleanClause.Occur>();
		
		if (in != null) {
			queries.add(in);
			flagsList.add(BooleanClause.Occur.MUST);
			
		}
		
		if (in.equals("Channel OR User")) {
			queries.add(what);
			flagsList.add(BooleanClause.Occur.SHOULD);
			queries.add(what);
			flagsList.add(BooleanClause.Occur.SHOULD);
			queries.add(what);
			flagsList.add(BooleanClause.Occur.SHOULD);
			queries.add(what);
			flagsList.add(BooleanClause.Occur.SHOULD);
		} else {
			queries.add(what);
			flagsList.add(BooleanClause.Occur.MUST);
		}
		
		flags = new BooleanClause.Occur[flagsList.size()];
		flags = flagsList.toArray(flags);
		
		String [] queryArray = new String[queries.size()];
		queryArray = queries.toArray(queryArray);
		return queryArray;
	}
	
	public BooleanClause.Occur[] flags () {
		return flags;
	}
}
