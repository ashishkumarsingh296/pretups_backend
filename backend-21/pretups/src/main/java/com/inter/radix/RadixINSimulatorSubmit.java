package com.inter.radix;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RadixINSimulatorSubmit extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public RadixINSimulatorSubmit() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		try{
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Server", "no-cache");
			response.setHeader("Status-Msg", "queued");
			response.setHeader("Request-Id", "123459");
			response.setHeader("Transaction-Id", "157318");
			response.setHeader("DATE", "Mon, 08 Sep 2014 05:23:46 GMT");
			response.setHeader("Content-Type", "text/html");
			response.setHeader("Status-Code", "0");

			out.println(RadixAirtelI.RESPONSE_QUEUED);

		}
		catch (Exception pex)
		{
			//TBD : populate strParser
			pex.printStackTrace();

		}
		finally
		{
			out.flush();
			out.close();
			out=null;
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
