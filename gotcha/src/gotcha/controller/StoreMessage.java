package gotcha.controller;

import java.io.IOException;
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
 * Servlet implementation class StoreMessage
 */
@WebServlet("/storeMessage")
public class StoreMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StoreMessage() {
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
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create();
		// Retrieve sent message from request
		Message message = gson.fromJson(request.getReader(), Message.class);
		insert(message);
	}
	
	private boolean insert (Message message) {
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		
		values.add(message.from());
		values.add(message.to());
		values.add(message.text());
		values.add(message.time());
		
		int rows = Globals.executeUpdate(Globals.INSERT_MESSAGE, values, where);
		return rows > 0;
	}
}
