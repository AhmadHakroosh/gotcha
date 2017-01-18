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
import javax.servlet.http.HttpSession;

import gotcha.model.*;
import gotcha.globals.Globals;

/**
 * Servlet implementation class Messages
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
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Data forwarded from login servlet
		HttpSession session = request.getSession();
		String data = (String)session.getAttribute("data");
		User user = (User)session.getAttribute("user");
		// 1. Find user subscriptions:
		ArrayList<String> channels = getUserSubscriptions(user);
		// 2. Find user direct chats
		ArrayList<String> users = getUserDirectChats(user);
		
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
	 * 
	 */
	private ArrayList<String> getUserSubscriptions (User user) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		ArrayList<String> channels = new ArrayList<String>();
		
		where.add(user.nickName());
		ResultSet resultSet = Globals.execute(Globals.SELECT_SUBSCRIPTON_BY_USER, values, where);
		try {
			while (resultSet.next()) {
				channels.add(resultSet.getString("CHANNEL"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return channels;
	}

	/**
	 * 
	 */
	private ArrayList<String> getUserDirectChats (User user) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		ArrayList<String> users = new ArrayList<String>();
		ArrayList<String> channels = new ArrayList<String>(Channel.getAllChannels());
		
		where.add(user.nickName());
		ResultSet resultSet = Globals.execute(Globals.SELECT_MESSAGE_BY_SENDER, values, where);
		try {
			while (resultSet.next()) {
				String receiver = resultSet.getString("TO");
				if (!users.contains(receiver) && !channels.contains(receiver)) {
					users.add(receiver);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		resultSet = Globals.execute(Globals.SELECT_MESSAGE_BY_RECEIVER, values, where);
		try {
			while (resultSet.next()) {
				String sender = resultSet.getString("FROM");
				if (!users.contains(sender)) {
					users.add(sender);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return users;
	}
}
