package gotcha.controller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import gotcha.model.*;
import gotcha.globals.Globals;

/**
 * This Servlet is responsible about getting messages after success log in.
 */
@WebServlet("/messages")
public class Messages extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Messages() {
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
	 * Get User's Messages after logging in successfully.
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link #getUserSubscriptions(User)} - get a list of User's subscribed Channels.</dd>
	 * <br/>
	 * <dd>{@link #getUserDirectChats(User)} - get a list of User's direct Messages.</dd>
	 * @param request Http request
	 * @param response Http response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Data forwarded from login servlet
		HttpSession session = request.getSession();
		String data = (String)session.getAttribute("data");
		User user = (User)session.getAttribute("user");
		// 1. Find user subscriptions:
		ArrayList<String> channels = new ArrayList<String>();
		ArrayList<String> users = new ArrayList<String>();
		if (user != null) {
			channels = getUserSubscriptions(user);
			// 2. Find user direct chats
			users = getUserDirectChats(user);
		}
		
		// 3. Append the found data with the forwarded data and return into response
		int i = 1;
		data += "\"channels\": [";
		for (String channel : channels) {
			data += i++ < channels.size() ? "\"" + channel + "\"," : "\"" + channel + "\"";
		}
		data +=   "],";
		
		i = 1;
		data += "\"directMessages\": [";
		for (String someone : users) {
			data += i++ < users.size() ? "\"" + someone + "\"," : "\"" + someone + "\"";
		}
		data +=	  "]"
			 + "}";
		
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(data);
		out.close();
	}

	/**
	 * A method to get a list of User subscriptions.
	 * @param user {@link gotcha.model.User} object that contain the user nickname.
	 * @return An list that contain the user subscriptions. 
	 */
	private ArrayList<String> getUserSubscriptions (User user) {
		ArrayList<String> channels = new ArrayList<String>();
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_SUBSCRIPTON_BY_USER);
			
			statement.setString(1, user.nickName());
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				channels.add(resultSet.getString("CHANNEL"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return channels;
	}

	/**
	 * A method to get a list of User direct Messages.
	 * @param user {@link gotcha.model.User} object that contain the user nickname.
	 * @return An list that contain the user direct Messages. 
	 */
	private ArrayList<String> getUserDirectChats (User user) {
		ArrayList<String> users = new ArrayList<String>();
		ArrayList<String> channels = Globals.getAllChannels();
		PreparedStatement statement;
		ResultSet resultSet;

		try {
			Connection connection = Globals.database.getConnection();
			statement = connection.prepareStatement(Globals.SELECT_MESSAGE_BY_SENDER);
			statement.setString(1, user.nickName());
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				String receiver = resultSet.getString("RECEIVER");
				if (!users.contains(receiver) && !channels.contains(receiver)) {
					users.add(receiver);
				}
			}

			statement = connection.prepareStatement(Globals.SELECT_MESSAGE_BY_RECEIVER);
			statement.setString(1, user.nickName());
			resultSet = statement.executeQuery();
		
			while (resultSet.next()) {
				String sender = resultSet.getString("SENDER");
				if (!users.contains(sender)) {
					users.add(sender);
				}
			}
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}

		return users;
	}
}
