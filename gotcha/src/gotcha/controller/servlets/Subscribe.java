package gotcha.controller.servlets;

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
import gotcha.model.Subscription;

/**
 * Servlet implementation class ChannelSubscribe
 */
@WebServlet("/subscribe")
public class Subscribe extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Subscribe() {
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
		Gson gson = new GsonBuilder().create();
		// Convert JSON object from request input to User object
		Subscription subscription = gson.fromJson(request.getReader(), Subscription.class);
		// Prepare a JSON to be forwarded to a new servlet or returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		if (insert(subscription)) {
			String jsonSubscription = gson.toJson(subscription, Subscription.class);
			data = "{"
				+ 		"\"status\": \"success\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".subscription-notification\","
				+ 			"\"message\": \"Subscribed successfully\""
				+ 		"},"
				+ 		"\"subscription\": " + jsonSubscription
				+  "}"
				;
		} else {
			data = "{"
				+ 		"\"status\": \"danger\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".subscription-notification\","
				+ 			"\"message\": \"Something went wrong, please try again later\""
				+ 		"}"
				+  "}"
				;
		}
		out.println(data);
		out.close();
	}
	
	private boolean insert (Subscription subscription) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		values.add(subscription.nickname());
		values.add(subscription.channel());
		
		int rows = Globals.executeUpdate(Globals.INSERT_SUBSCRIPTON, values, where);
		
		if (rows > 0) {
			Globals.channels.get(subscription.channel()).add(subscription.nickname());
		}
		
		return rows > 0;
	}
}
