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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gotcha.globals.Globals;
import gotcha.model.User;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
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
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		// Convert JSON object from request input to User object
		User user = gson.fromJson(request.getReader(), User.class);
		// Prepare a JSON to be forwarded to a new servlet or returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		if (insert(user)) {
			// Write user data to the response of type JSON
			HttpSession session = request.getSession();
			request.setAttribute("user", user);
			session.setAttribute("user", user);
			request.setAttribute("httpSession", session);
			String jsonUser = gson.toJson(user, User.class);
			data = "{"
				+ 		"\"status\": \"success\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".register-notification\","
				+ 			"\"message\": \"Registered successfully\""
				+ 		"},"
				+ 		"\"user\": "
				+			 jsonUser
				;
	
			request.setAttribute("data", data + ",");
			session.setAttribute("data", data + ",");
			request.getRequestDispatcher("/messages").forward(request, response);
			data += "}";
		} else {
			data = "{"
				+ 		"\"status\": \"danger\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".register-notification\","
				+ 			"\"message\": \"Something went wrong, please try again later\""
				+ 		"}"
				+	"}"
				;
		}
		out.println(data);
		out.close();
	}
	
	private boolean insert (User user) {
		int rows = 0;
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.INSERT_USER);
			
			statement.setString(1, user.username());
			statement.setString(2, user.password());
			statement.setString(3, user.nickName());
			statement.setString(4, user.description());
			statement.setString(5, user.photoUrl());
			statement.setString(6, user.status());
			statement.setTimestamp(7, user.lastSeen());
			
			rows = statement.executeUpdate();
			connection.commit();
			statement.close();
			connection.close();
			Globals.searchEngine.add(user);
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
		
		return rows > 0;
	}
}
