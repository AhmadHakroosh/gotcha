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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gotcha.globals.Globals;
import gotcha.model.Channel;
import gotcha.model.User;

/**
 * This Servlet is responsible about getting a Channel's stored data.
 */
@WebServlet("/channelData")
public class GetChannelData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetChannelData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see #handleRequest(HttpServletRequest, HttpServletResponse)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
	
	/**
	 * @see #handleRequest(HttpServletRequest, HttpServletResponse)
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
	/**
	 * 	
	 * Handles an HTTP request.
	 * Gets channel data from the database and send it to the client.
	 * <p>
	 * <b>Used methods:</b>
	 * <br/>
	 * <dd>{@link gotcha.globals.Globals#getSubscribersList(String)} - to get the subscribers list.</dd>
	 * <br/>
	 * <dd>{@link #getChannelData(String)} - to get channel data.</dd>
	 * @param request Http request
	 * @param response Http response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 *
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		Channel inputChannel = gson.fromJson(request.getReader(), Channel.class);
		Channel channel = getChannelData(inputChannel.name());
	
		ArrayList<String> subscribers = Globals.getSubscribersList(channel.name());
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		String data;
		
		data = "{"
				+ "\"name\": \"" + channel.name() + "\","
				+ "\"description\": \"" + channel.description() + "\","
				+ "\"subscribers\": {"
				;
		
		int i = 1;
		for (String someone : subscribers) {
			User user = getUserData(someone);
			someone = "\"" + user.nickName() + "\": " + gson.toJson(user, User.class);
			data += i++ < subscribers.size() ? someone + "," : someone;
		}
		
		data += 	"}"
			 + "}";
		
		out.println(data);
		out.close();
	}
	/**
	 * The main method to get a Channel data.
	 * @param name Channel name.
	 * @return {@link gotcha.model.Channel} object, in case the Channel doesn't exist, it return null.
	 */
	private Channel getChannelData (String name) {
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_CHANNEL_BY_NAME);
			
			statement.setString(1, name);
			
			ResultSet resultSet = statement.executeQuery();
			
			// The channel exists in our system, get its data
			if (resultSet.next()) {
				Channel channel = new Channel();
				channel.name(resultSet.getString("NAME"));
				channel.description(resultSet.getString("DESCRIPTION"));

				statement.close();
				connection.close();
				return channel;
			// it is not existing, return null
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
	/**
	 * A method to get user data by his nickname.
	 * @param nickname Required user nickname.
	 * @return {@link gotcha.model.User} object, in case no user with this nickname,
	 * it returns null.
	 */
	private User getUserData (String nickname) {
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_USER_BY_NICKNAME);
			
			statement.setString(1, nickname);
			
			ResultSet resultSet = statement.executeQuery();
		
			// The user exists in our system, get his data
			if (resultSet.next()) {
				User tempUser = new User();
				tempUser.nickName(resultSet.getString("NICKNAME"));
				tempUser.description(resultSet.getString("DESCRIPTION"));
				tempUser.status(resultSet.getString("STATUS"));
				tempUser.lastSeen(resultSet.getTimestamp("LAST_SEEN"));
				tempUser.photoUrl(resultSet.getString("PHOTO_URL"));

				statement.close();
				connection.close();
				return tempUser;
			// He is not existing, returns null
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
