package gotcha.controller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gotcha.globals.Globals;
import gotcha.model.Channel;

/**
 * This Servlet is responsible for creating a new channel
 */
@WebServlet("/createChannel")
public class CreateChannel extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     *
     * @see HttpServlet#HttpServlet()
     */
    public CreateChannel() {
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
	 * Handles an HTTP request.
	 * Gets data for a channel to be created, and inserts it to DB.
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link #insert(Channel)} - to insert the new Channel to the database.</dd>
	 * @param request Http request
	 * @param response Http response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		// Convert JSON object from request input to Channel object
		Channel channel = gson.fromJson(request.getReader(), Channel.class);
		// Prepare a JSON to be returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		if (insert(channel)) {
			data = "{"
				+ 		"\"status\": \"success\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 	  		"\"selector\": \".channel-creation-notification\","
				+ 	  		"\"message\": \"Congratulations! You've created '" + channel.name() + "' channel.\""
				+ 		"},"
				+ 		"\"channel\": {"
				+ 			"\"name\": \"" + channel.name() +"\","
				+ 			"\"description\": \"" + channel.description() + "\""
				+ 		"}"
				+  "}"
				;
		} else {
			data = "{"
				+ 		"\"status\": \"danger\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".channel-creation-notification\","
				+ 			"\"message\": \"Something went wrong, please try again later\""
				+ 		"}"
				+	"}"
				;
		}
		
		out.println(data);
		out.close();
	}
	/**
	 * Gets a Channel object to be inserted to the database.
	 * <p>
	 * @param channel {@link gotcha.model.Channel} object.
	 * @return true: if the channels was inserted successfully.
	 *         false: otherwise.
	 */
	private boolean insert (Channel channel) {
		
		int rows = 0;
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.INSERT_CHANNEL);
			
			statement.setString(1, channel.name());
			statement.setString(2, channel.description());
			statement.setString(3, channel.createdBy());
			statement.setTimestamp(4, channel.createdTime());
			
			rows = statement.executeUpdate();
			
			if (rows > 0) {
				Globals.channels.put(channel.name(), new ArrayList<String>());
			}
			
			connection.commit();
			statement.close();
			connection.close();
			Globals.searchEngine.add(channel);
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}

		return rows > 0;
	}
}
