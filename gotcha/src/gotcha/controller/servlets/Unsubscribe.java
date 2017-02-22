package gotcha.controller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
 * This Servlet is responsible about Unsubscribing a user from a Channel.
 */
@WebServlet("/unsubscribe")
public class Unsubscribe extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Unsubscribe() {
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
	 * Unsubscribe a user from a Channel.
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link #delete(Subscription)} - Delete the subscription from the database.</dd>
	 * @param request Http request
	 * @param response Http response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 *
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().create();
		// Convert JSON object from request input to User object
		Subscription subscription = gson.fromJson(request.getReader(), Subscription.class);
		// Prepare a JSON to be forwarded to a new servlet or returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		if (delete(subscription)) {
			data = "{"
				+ 		"\"status\": \"success\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".unsubscription-notification\","
				+ 			"\"message\": \"You've successfully unsubscribed from '" + subscription.channel() + "'.\""
				+ 		"}"
				+  "}"
				;
		} else {
			data = "{"
				+ 		"\"status\": \"danger\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".unsubscription-notification\","
				+ 			"\"message\": \"Something went wrong, please try again later\""
				+ 		"}"
				+  "}"
				;
		}
		out.println(data);
		out.close();
	}
	/**
	 * A method to delete a user subscription from the database.
	 * @param subscription {@link gotcha.model.Subscription} object that contain the subscription data.
	 * @return True in case the subscription deleted successfully, False otherwise.
	 */
	private boolean delete (Subscription subscription) {
		int rows = 0;
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.DELETE_SUBSCRIPTON);
			
			statement.setString(1, subscription.nickname());
			statement.setString(2, subscription.channel());
			
			rows = statement.executeUpdate();
			connection.commit();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
		
		if (rows > 0) {
			Globals.channels.get(subscription.channel()).remove(subscription.nickname());
		}
		
		return rows > 0;
	}
}
