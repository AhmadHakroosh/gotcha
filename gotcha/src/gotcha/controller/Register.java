package gotcha.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gotcha.globals.Globals;
import gotcha.model.User;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
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
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create();
		// Convert JSON object from request input to User object
		User user = gson.fromJson(request.getReader(), User.class);
		// Prepare a JSON to be forwarded to a new servlet or returned in the response
		PrintWriter out = response.getWriter();
		response.setContentType("application/json; charset=UTF-8");
		String data;
		if (insert(user)) {
			// Write user data to the response of type JSON
			HttpSession session = request.getSession();
			request.setAttribute("user", user);
			session.setAttribute("user", user);
			request.setAttribute("httpSession", session);
			String jsonUser = gson.toJson(user, User.class);
			data = "{"
				+ 		"\"status\": \"success\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".register-notification\","
				+ 			"\"message\": \"Registered successfully\""
				+ 		"},"
				+ 		"\"user\": {"
				+			"\"status\": \"active\","
				+ 			"\"profile\": " + jsonUser
				;
	
			request.setAttribute("data", data + ",");
			session.setAttribute("data", data + ",");
			request.getRequestDispatcher("/messages").forward(request, response);
			data += "}";
		} else {
			data = "{"
				+ 		"\"status\": \"danger\","
				+ 		"\"route\": \"messages\","
				+ 		"\"notification\": {"
				+ 			"\"selector\": \".register-notification\","
				+ 			"\"message\": \"Something went wrong, please try again later\""
				+ 		"},"
				+	"}"
				;
		}
		out.println(data);
		out.close();
	}
	
	private boolean insert (User user) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		values.add(user.username());
		values.add(user.password());
		values.add(user.nickName());
		values.add(user.description());
		values.add(user.photoUrl());
		values.add(user.status());
		values.add(user.lastSeen());
		
		int rows = Globals.executeUpdate(Globals.INSERT_USER, values, where);
		
		return rows > 0 ? true : false;
	}
}
