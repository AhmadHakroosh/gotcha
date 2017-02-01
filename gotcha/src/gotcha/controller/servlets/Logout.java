package gotcha.controller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import gotcha.globals.Globals;
import gotcha.model.User;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
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
	 * Handles an HTTP request sent from client.
	 * Logs out the user who sent this request.
	 * <p>
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) 
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/JSON; charset=UTF-8");
		PrintWriter out = response.getWriter();
		String data;
		data = "{"
			+ 		"\"status\": \"success\","
			+ 		"\"route\": \"login\","
			+ 		"\"notification\": {"
			+ 			"\"selector\": \".logout-notification\","
			+ 			"\"message\": \"Logged out successfully\""
			+ 		"}"
			+  "}"
			;
		HttpSession session = request.getSession();
		updateUserStatus(session);
		session.invalidate();
		out.println(data);
		out.close();
	}
	/**
	 * Updates the user's status to "away", and updates the last seen time of the user
	 * when he logs out.
	 * <p>
	 * @param session The current HTTP session.
	 */
	private void updateUserStatus (HttpSession session) {
		User user = (User)session.getAttribute("user");		
		String status = "away";
		Timestamp last_seen = new Timestamp(System.currentTimeMillis());
		
		try {
			Connection connection = Globals.database.getConnection();
			PreparedStatement statement = connection.prepareStatement(Globals.UPDATE_USER_STATUS);
			
			statement.setString(1, status);
			statement.setTimestamp(2, last_seen);
			statement.setString(3, user.username());
			
			statement.executeUpdate();
			connection.commit();
			statement.close();
			connection.close();
			
		} catch (SQLException e) {
			System.out.println("An error has occured while trying to execute the query!");
		}
	}
}
