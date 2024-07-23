package com.inter.upss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Upssservlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private static final String __USERINPUT="param";
	private static int reqNum = 1;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		reqNum++;

		response.setContentType("text/html");

		System.out.println("reqNum--->" + reqNum + "<<>>");

		try {

			BufferedReader br = request.getReader();
			int c = 0;
			String xmlReq = "";

			while ((c = br.read()) != -1) {
				xmlReq += "" + (char) c;
			}

			System.out.println("Request--->" + xmlReq);
			
			
			
			String xml = "";

			System.out.println("Sending Response.....");

			PrintWriter out = response.getWriter();

			if (xmlReq.contains("eventTriggerDTO")) {
			

				if ((reqNum % 3) == 0) {
					out.println("Invalid data");
			
				} else if ((reqNum % 2) == 0) {
					out.println("Success");
			
				} else {
					out.println("Success");
					System.out.println("Response XML:" + xml);
				}

		
			} else {
				if ((reqNum % 3) == 0) {
					out.println(" ");
					
				} else if ((reqNum % 2) == 0) {
					out.println(xmlReq);
					
				} else {
					out.println(xmlReq);
				}
			}
		} catch (Exception pex) {
			pex.printStackTrace();

		}finally{
		
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		reqNum++;

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		System.out.println("reqNum--->" + reqNum + "<<>>");

		try {

			BufferedReader br = request.getReader();
			int c = 0;
			String xmlReq = "";

			while ((c = br.read()) != -1) {
				xmlReq += "" + (char) c;
			}

			System.out.println("Request--->" + xmlReq);
			System.out.println("Validating Xml....1");

			System.out.println("Validating Xml....2");
			String xml = "";

			System.out.println("Sending Response.....");

			if (xmlReq.contains("eventTriggerDTO")) {
				out.println(xmlReq);

				if ((reqNum % 3) == 0) {
					out.println("�	Invalid data");
					
				} else if ((reqNum % 2) == 0) {
					out.println("Success");
					
				} else {
					out.println("Success");
					
				}

				
			} else {
				if ((reqNum % 3) == 0) {
					out.println(" ");
					
				} else if ((reqNum % 2) == 0) {
					out.println(xmlReq);
					
				} else {
					out.println(xmlReq);
					
				}
			}
		} catch (Exception pex) {
			pex.printStackTrace();

		}
	}

}
