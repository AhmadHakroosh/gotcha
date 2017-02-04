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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gotcha.globals.Globals;
import gotcha.model.Message;
import gotcha.model.User;

/**
 * Servlet implementation class GetTenThreadMessages
 */
@WebServlet("/getTenThreadMessages")
public class GetTenThreadMessages extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTenThreadMessages() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		String thread = gson.toJson(request.getReader());
		JsonParser parser = new JsonParser();
		JsonElement jsonThread = parser.parse(thread);
		JsonObject threadObject = jsonThread.getAsJsonObject();
		JsonElement offset = threadObject.get("offset");
		JsonElement parentId = threadObject.get("parentId");
		
		String data = "[";
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_TEN_CHANNEL_MESSAGES, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			statement.setInt(1, parentId.getAsInt());
			statement.setInt(2, offset.getAsInt());
			
			ResultSet resultSet = statement.executeQuery();
			
			int i = 1;
			resultSet.last();
			int rows = resultSet.getRow();
			resultSet.beforeFirst();
			
			while (resultSet.next()) {
				Message message = new Message();
				message.id(resultSet.getInt("ID"));
				message.parentId(resultSet.getInt("PARENT_ID"));
				message.from(resultSet.getString("SENDER"));
				message.to(resultSet.getString("RECEIVER"));
				message.text(resultSet.getString("TEXT"));
				message.lastUpdate(resultSet.getTimestamp("LAST_UPDATE"));
				message.time(resultSet.getTimestamp("SENT_TIME"));
				// Retrieve sender data
				message.from(gson.toJson(getUserData(message.from()), User.class));
				String jsonMessage = gson.toJson(message, Message.class);
				data += i++ < rows ? jsonMessage + "," : jsonMessage;
			}
			
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An unknown error has occurred while trying to retrieve messages from the system.");
		}
		
		data += "]";

		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		out.println(data);
		out.close();
	}
	
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
			// He is not existing, return null
			} else {
				statement.close();
				connection.close();
				return null;
			}
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to retrieve user data from database!");
			return null;
		}
	}
}
