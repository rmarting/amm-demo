package org.rmarting.jee.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/delay"}, displayName = "Delay Servlet", loadOnStartup = 1)
public class DelayServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1960809459301119633L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// Delay to startup the application
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// Nothing Special
		}
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("DelayServlet is working");
	}
	
}