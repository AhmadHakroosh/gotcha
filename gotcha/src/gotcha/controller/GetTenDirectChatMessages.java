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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gotcha.globals.Globals;
import gotcha.model.Message;

/**
 * Servlet implementation class GetTenDirectChatMessages
 */
@WebServlet("/getTenDirectChatMessages")
public class GetTenDirectChatMessages extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTenDirectChatMessages() {
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
		Gson gson = new GsonBuilder().create();
		Message input = gson.fromJson(request.getReader(), Message.class);
		// Set query parameters
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		where.add(input.from());
		where.add(input.to());
		where.add(input.to());
		where.add(input.from());
		where.add(input.id());
		
		ResultSet resultSet = Globals.execute(Globals.SELECT_TEN_DIRECT_MESSAGES, values, where);
		String data = "[";
		int i = 1;
		try {
			resultSet.last();
			int rows = resultSet.getRow();
			resultSet.beforeFirst();
			
			while (resultSet.next()) {
				Message message = new Message();
				message.id(resultSet.getInt("ID"));
				message.from(resultSet.getString("SENDER"));
				message.to(resultSet.getString("RECEIVER"));
				message.text(resultSet.getString("TEXT"));
				message.time(resultSet.getTimestamp("SENT_TIME"));
				String jsonMessage = gson.toJson(message, Message.class);
				data += i++ < rows ? jsonMessage + "," : jsonMessage;
			}
		} catch (SQLException e) {
			System.out.println("An unknown error has occurred while trying to retrieve messages from the system.");
		}
		data += "]";
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		out.println(data);
		out.close();
	}
}
