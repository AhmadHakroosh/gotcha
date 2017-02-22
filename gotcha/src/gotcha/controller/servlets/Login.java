package gotcha.controller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.*;

import gotcha.model.User;
import gotcha.globals.Globals;

/**
 * This Servlet is responsible about logging the user in.
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
	/**
	 * 	
	 * Handles an HTTP request.
	 * Verify user data and log him in.
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link #get(User)} - to get user data from the database if he exist.</dd>
	 * <br/>
	 * <dd>{@link #updateUserStatus(HttpSession)} - update the user status to Online.</dd>
	 * @param request Http request
	 * @param response Http response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		// Convert JSON object from request input to User object
		User user = gson.fromJson(request.getReader(), User.class);
		// Connect to database
		// Get the user from Database (if exists)
		User registered = get(user);
		// Prepare a JSON to be forwarded to a new servlet or returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		// Write user data to the response of type JSON
		if (registered != null) {
			HttpSession session = request.getSession();
			registered.status("active");
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
				+ 		"\"user\": "
				+			jsonUser
				;

			request.setAttribute("data", data + ",");
			session.setAttribute("data", data + ",");
			request.setAttribute("user", registered);
			request.getRequestDispatcher("/messages").forward(request, response);
			data += "}";
			updateUserStatus(session);
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
	/**
	 * A method to get the user data.
	 * If the user doesn't exist,or the password is incorrect, return null.
	 * @param user {@link gotcha.model.User} object that contain the user name and password.
	 * @return {@link gotcha.model.User} object that contain required User's 
	 * data in case he exist and his password is correct, else null.
	 */
	private User get (User user) {
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_USER_BY_USERNAME_AND_PASSWORD);
			
			statement.setString(1, user.username());
			statement.setString(2, user.password());
			
			ResultSet resultSet = statement.executeQuery();
			
			// The user exists in our system, get his data
			if (resultSet.next()) {
				User registered = new User();
				registered.username(resultSet.getString("USERNAME"));
				registered.password(resultSet.getString("PASSWORD"));
				registered.description(resultSet.getString("DESCRIPTION"));
				registered.nickName(resultSet.getString("NICKNAME"));
				registered.photoUrl(resultSet.getString("PHOTO_URL"));
				registered.status(resultSet.getString("STATUS"));
				registered.lastSeen(resultSet.getTimestamp("LAST_SEEN"));
				
				statement.close();
				connection.close();
				return registered;
			// He is not existing, return null
			} else {
				statement.close();
				connection.close();
				return null;
			}

		} catch (SQLException e) {
			System.out.println("An error has occured while trying to connect to database!");
			return null;
		}
	}
	/**
	 * A method to update the user status to active after verifying his name and password.
	 * @param session The session of the user who want to log in.
	 */
	private void updateUserStatus (HttpSession session) {
		User user = (User)session.getAttribute("user");
		
		String status = "active";
		Timestamp last_seen = new Timestamp(System.currentTimeMillis());
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.UPDATE_USER_STATUS);
			
			statement.setString(1, status);
			statement.setTimestamp(2, last_seen);
			statement.setString(3, user.username());
			statement.executeUpdate();
			
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
	}
}