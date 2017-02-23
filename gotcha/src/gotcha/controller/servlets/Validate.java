package gotcha.controller.servlets;

import java.io.BufferedReader;
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
import gotcha.model.Channel;
import gotcha.model.User;

/**
 * This Servlet is responsible about validating if a user name/nickname is Available
 * for a new user, or a Channel name for a new Channel.
 */
@WebServlet("/validate")
public class Validate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Validate() {
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
	 * Validate user name/nickname or channel name availability.
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link #checkUsernames(String)} - Check if the required user name is Available.</dd>
	 * <br/>
	 * <dd>{@link #checkNicknames(String)} - Check if the required nickname is Available.</dd>
	 * <br/>
	 * <dd>{@link #checkChannels(String)} - Check if the required Channel name is Available.</dd>
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
		String json = "{}";
		// Convert JSON object from request input to User object
		try {
	        // Read from request
	        StringBuilder buffer = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            buffer.append(line);
	        }
	        json = buffer.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		Channel channel = gson.fromJson(json, Channel.class);
		User user = gson.fromJson(json, User.class);
		
		// Check whether the passed was a username or a nickname
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		if (!user.isEmpty()) {
			data = (checkUsernames(user.username()) || checkNicknames(user.username()) || checkChannels(user.username()) || checkUsernames(user.nickName()) || checkNicknames(user.nickName()) || checkChannels(user.nickName())) ? "{\"valid\": \"ban\"}" : "{\"valid\": \"ok\"}";
		} else {
			data = (checkUsernames(channel.name()) || checkNicknames(channel.name()) || checkChannels(channel.name())) ? "{\"valid\": \"ban\"}" : "{\"valid\": \"ok\"}";
		}
		out.println(data);
		out.close();
	}
	/**
	 * A method to check if a nickname is Available and not
	 * used by another user.
	 * @param nickname The required nickname.
	 * @return True if the nickname already exist, False otherwise.
	 */
	private boolean checkNicknames (String nickname) {
		
		if (nickname == null) return false;
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_USER_BY_NICKNAME);
			statement.setString(1, nickname);
			
			ResultSet users = statement.executeQuery();
			
			if (users.next()) {
				statement.close();
				connection.close();
				return true;
			} else {
				statement.close();
				connection.close();
				return false;
			}
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
			return false;
		}
	}
	/**
	 * A method to check if a user name is Available and not
	 * used by another user.
	 * @param username The required username.
	 * @return True if the username already exist, False otherwise.
	 */
	private boolean checkUsernames (String username) {
		
		if (username == null) return false;
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_USER_BY_USERNAME);
			statement.setString(1, username);
			
			ResultSet users = statement.executeQuery();
			
			if (users.next()) {
				statement.close();
				connection.close();
				return true;
			} else {
				statement.close();
				connection.close();
				return false;
			}
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
			return false;
		}
	}
	/**
	 * A method to check if a Channel name is Available and not
	 * used by another Channel.
	 * @param name The required Channel name.
	 * @return True if the Channel name already exist, False otherwise.
	 */
	private boolean checkChannels (String name) {

		if (name == null) return false;
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_CHANNEL_BY_NAME);
			statement.setString(1, name);
			
			ResultSet users = statement.executeQuery();
			
			if (users.next()) {
				statement.close();
				connection.close();
				return true;
			} else {
				statement.close();
				connection.close();
				return false;
			}
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
			return false;
		}
	}
}