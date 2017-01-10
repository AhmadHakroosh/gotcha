package gotcha.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import gotcha.model.User;

/**
 * Servlet implementation class Router
 */
@WebServlet("/welcome")
public class Router extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Router() {
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
	
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		response.setContentType("application/JSON; charset=UTF-8");
	    PrintWriter out = response.getWriter();
		String data;
		if (session == null) {
			data = "{\"status\": \"success\", \"route\": \"login\"}";
		} else {
			data = (String)session.getAttribute("data");
			User user = (User)session.getAttribute("user");
			request.setAttribute("user", user);
			request.setAttribute("data", data);
			request.getRequestDispatcher("/messages").forward(request, response);
			data += "}";
		}
	    
	    out.println(data);
	    out.close();
	}
}
