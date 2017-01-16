package gotcha.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.lang.Object;

import com.google.gson.*;

import gotcha.model.User;
import gotcha.globals.Globals;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login/auth")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
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
		User user = gson.fromJson(request.getReader(), User.class);
		// Get the user from Database (if exists)
		User registered = get(user);
		// Prepare a JSON to be forwarded to a new servlet or returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		// Write user data to the response of type JSON
		if (registered != null) {
			HttpSession session = request.getSession();
			session.setAttribute("user", registered);
			request.setAttribute("httpSession", session);
			String jsonUser = gson.toJson(registered, User.class);
			data = "{"
				+ 		"\"status\": \"success\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".login-notification\","
				+ 			"\"message\": \"Logged in successfully\""
				+ 		"},"
				+ 		"\"user\": {"
				+			"\"status\": \"active\","
				+ 			"\"profile\": " + jsonUser
				;

			request.setAttribute("data", data + ",");
			session.setAttribute("data", data + ",");
			request.setAttribute("user", registered);
			request.getRequestDispatcher("/messages").forward(request, response);
			data += "}";		
		// Write "failure" status to the response
		} else {
			data = "{"
				+ 		"\"status\": \"danger\","
				+ 		"\"route\": \"login\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".login-notification\","
				+ 			"\"message\": \"Incorrect username and/or password\""
				+ 		"}"
				+ 	"}"
				;
		}
		
		out.println(data);
		out.close();
	}
	
	private User get (User user) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		where.add(user.username());
		where.add(user.password());
		
		ResultSet resultSet = Globals.execute(Globals.SELECT_USER_BY_USERNAME_AND_PASSWORD, values, where);
		try {
			// The user exists in our system, get his data
			if (resultSet.next()) {
				User registered = new User();
				registered.username(resultSet.getString("USERNAME"));
				registered.password(resultSet.getString("PASSWORD"));
				registered.description(resultSet.getString("DESCRIPTION"));
				registered.nickName(resultSet.getString("NICKNAME"));
				registered.photoUrl(resultSet.getString("PHOTO_URL"));
				return registered;
			// He is not existing, return null
			} else {
				return null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}