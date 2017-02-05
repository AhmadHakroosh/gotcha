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
import gotcha.model.Message;
import gotcha.model.User;

/**
 * Servlet implementation class GetLastThreadMessage
 */
@WebServlet("/getLastThreadMessage")
public class GetLastThreadMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetLastThreadMessage() {
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
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		Message threadParent = gson.fromJson(request.getReader(), Message.class);
		Message reply = new Message();
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_LAST_THREAD_MESSAGE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			statement.setInt(1, threadParent.id());
			
			ResultSet resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				reply.id(resultSet.getInt("ID"));
				reply.parentId(resultSet.getInt("PARENT_ID"));
				reply.from(resultSet.getString("SENDER"));
				reply.to(resultSet.getString("RECEIVER"));
				reply.text(resultSet.getString("TEXT"));
				reply.time(resultSet.getTimestamp("SENT_TIME"));
				reply.lastUpdate(resultSet.getTimestamp("LAST_UPDATE"));// Retrieve sender data
				reply.from(gson.toJson(getUserData(reply.from()), User.class));
			}
			
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occurred while trying to retrieve last reply from database!");
		}
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		out.println(gson.toJson(reply, Message.class));
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
