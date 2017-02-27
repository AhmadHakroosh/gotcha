package gotcha.controller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gotcha.controller.search.model.GotchaQuery;
import gotcha.globals.Globals;
import gotcha.model.Channel;
import gotcha.model.User;

/**
 * This Servlet is responsible about searching channels or users.
 */
@WebServlet("/search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 * 	
	 * Handles an HTTP request.
	 * Search for a specified query in the database (user name or channel name).
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link gotcha.controller.search.GotchaSearchEngine#search(GotchaQuery)} - 
	 * Search for the required query.</dd>
	 * <br/>
	 * <dd>{@link gotcha.globals.Globals#getSubscribersList(String)} - 
	 * to get the subscribers list.</dd>
	 * @param request Http request
	 * @param response Http response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().create();
		GotchaQuery query = gson.fromJson(request.getReader(), GotchaQuery.class);
		// Prepare a JSON to be forwarded to a new servlet or returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		// Search the index for the provided query
		HashMap<String, Object> results = Globals.searchEngine.search(query);
		ArrayList<Channel> channels = new ArrayList<Channel>();
		ArrayList<User> users = new ArrayList<User>();
		int i = 1;
		String data = "{";
		// For each result..
		for (Object object : results.values()) {
			// If it is a user, add it to users
			if (object instanceof User) users.add((User) object);
			// else, add it to channels
			else channels.add((Channel) object);
		}
		// Start building the response
		data += "\"channels\": {";
		
		for (Channel channel : channels) {
			int subscribers = Globals.getSubscribersList(channel.name()).size();
			data += "\"" + channel.name() + "\": {"
				 + 		"\"name\": \"" + channel.name() + "\","
				 + 		"\"description\": \"" + channel.description() + "\","
				 + 		"\"subscriptions\": " + subscribers
				 +	"}"
				 ;		
			data += i++ < channels.size() ? "," : ""; 
		}
		
		i = 1;
		data += "},"
			 +  "\"users\": {"
			 ;
		
		for (User user : users) {
			data += "\"" + user.nickName() + "\":";
			data += gson.toJson(user, User.class);
			data += i++ < users.size() ? "," : "";
		}
		
		data += "}"
			 + "}";
		// Respond
		out.println(data);
		out.close();
	}
}
