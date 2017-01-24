package gotcha.controller.servlets;

import java.io.BufferedReader;
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
import gotcha.model.User;

/**
 * Servlet implementation class Validate
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
	
	private boolean checkNicknames (String nickname) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		if (nickname == null) return false;
		
		where.add(nickname);
		
		ResultSet users = Globals.execute(Globals.SELECT_USER_BY_NICKNAME, values, where);
		
		try {
			if (users.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean checkUsernames (String username) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		if (username == null) return false;
		
		where.add(username);
		
		ResultSet users = Globals.execute(Globals.SELECT_USER_BY_USERNAME, values, where);
		
		try {
			if (users.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean checkChannels (String name) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		if (name == null) return false;
		
		where.add(name);
		
		ResultSet channels = Globals.execute(Globals.SELECT_CHANNEL_BY_NAME, values, where);
		
		try {
			if (channels.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}