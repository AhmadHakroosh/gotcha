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

/**
 * Servlet implementation class GetMessageRepliesNumber
 */
@WebServlet("/getMessageRepliesNumber")
public class GetMessageRepliesNumber extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMessageRepliesNumber() {
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
		Message message = gson.fromJson(request.getReader(), Message.class);
		String data = "{\"replies\": 0}";
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.SELECT_MESSAGE_REPLIES, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			statement.setInt(1, message.id());
			
			ResultSet allReplies = statement.executeQuery();
			
			allReplies.last();
			int replies = allReplies.getRow();
			
			data = "{\"replies\":" + replies + "}";
			
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occurred while trying to get message replies number from database!");
		}
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		out.println(data);
		out.close();
	}
}
