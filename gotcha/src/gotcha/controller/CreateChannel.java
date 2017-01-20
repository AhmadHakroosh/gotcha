package gotcha.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
 * Servlet implementation class CreateChannel
 */
@WebServlet("/createChannel")
public class CreateChannel extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
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
	
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create();
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
	
	private boolean insert (Channel channel) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		values.add(channel.name());
		values.add(channel.description());
		values.add(channel.createdBy());
		values.add(channel.createdTime());
		
		int rows = Globals.executeUpdate(Globals.INSERT_CHANNEL, values, where);
		
		if (rows > 0) {
			Globals.channels.put(channel.name(), new ArrayList<String>());
		}
		return rows > 0;
	}
}
