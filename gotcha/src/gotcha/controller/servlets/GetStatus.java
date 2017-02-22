package gotcha.controller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gotcha.globals.Globals;
import gotcha.model.User;

/**
 * This Servlet is responsible about getting a specified User's status.
 */
@WebServlet("/getStatus")
public class GetStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStatus() {
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
	 * Gets thread last written message from the database and send it to the client.
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link #getStatus(User)} - to get user status from the database.</dd>
	 * @param request Http request
	 * @param response Http response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		User user = gson.fromJson(request.getReader(), User.class);
		user = getStatus(user);
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		String jsonUser = gson.toJson(user, User.class);
		
		data = 	"{"
			 + 		"\"user\": "
			 + 			jsonUser
			 +	"}"
			 ;
		
		out.println(data);
		out.close();
	}
	/**
	 * A method to get a specified user status.
	 * @param user  {@link gotcha.model.User} object that contain the user nickname.
	 * @return {@link gotcha.model.User} object that contain required User's status.
	 */
	private User getStatus (User user) {
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_USER_BY_NICKNAME);
			
			statement.setString(1, user.nickName());
			
			ResultSet resultSet = statement.executeQuery();
			// The user exists in our system, get his data
			if (resultSet.next()) {
				User tempUser = new User();
				tempUser.nickName(resultSet.getString("NICKNAME"));
				tempUser.status(resultSet.getString("STATUS"));
				tempUser.lastSeen(resultSet.getTimestamp("LAST_SEEN"));
				
				statement.close();
				connection.close();
				return tempUser;
			// He is not existing, return null
			} else {
				statement.close();
				connection.close();
				return null;
			}
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
			return null;
		}
	}
}
