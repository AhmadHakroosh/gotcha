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
import gotcha.model.Channel;

/**
 * Servlet implementation class GetUserMessages
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
		Gson gson = new GsonBuilder().setDateFormat("MMM dd,yyyy HH:mm:ss").create();
		Channel inputChannel = gson.fromJson(request.getReader(), Channel.class);
		Channel channel = getChannelData(inputChannel.name());
		// Retrieve list of channel subscribers
		ArrayList<String> subscribers = Channel.getSubscribersList(channel.name());
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		String data;
		
		data = "{"
				+ "\"name\": \"" + channel.name() + "\","
				+ "\"description\": \"" + channel.description() + "\","
				+ "\"messages\": [],"
				+ "\"subscribers\": ["
				;
		
		int i = 1;
		for (String someone : subscribers) {
			data += i++ < subscribers.size() ? "\"" + someone + "\"," : "\"" + someone + "\"";
		}
		
		data += 	"]"
			 + "}";
		
		out.println(data);
		out.close();
	}
	
	private Channel getChannelData (String name) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		where.add(name);
		
		ResultSet resultSet = Globals.execute(Globals.SELECT_CHANNEL_BY_NAME, values, where);
		try {
			// The user exists in our system, get his data
			if (resultSet.next()) {
				Channel channel = new Channel();
				channel.name(resultSet.getString("NAME"));
				channel.description(resultSet.getString("DESCRIPTION"));
				
				return channel;
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
