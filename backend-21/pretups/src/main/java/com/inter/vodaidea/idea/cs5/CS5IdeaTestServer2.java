package com.inter.vodaidea.idea.cs5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


public class CS5IdeaTestServer2 extends HttpServlet {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private String cs5lResponseFilePath;

	/**
	 * Constructor of the object.
	 */
	public CS5IdeaTestServer2() {
		super();
	}
	public void init(ServletConfig conf) throws ServletException
	{
		if(_log.isDebugEnabled()) _log.debug("init","Entered");
		super.init(conf);
		cs5lResponseFilePath = getInitParameter("cs5ideaaxmlfilepath");
		if(_log.isDebugEnabled())_log.debug("init","Exiting cs5lResponseFilePath="+cs5lResponseFilePath);
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (_log.isDebugEnabled()) _log.debug("CS5IdeaTestServer2","Entered...Connected to CS5IdeaTestServer2");
		doPost(request, response);
	}
	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy()
	{
		super.destroy(); 
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (_log.isDebugEnabled()) _log.debug("CS5IdeaTestServer2","Entered...Connected to CS5IdeaTestServer2");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String responseStr = null;
		StringBuffer lineBuff=null;
		int indexStart =0;
		int indexEnd=0;
		String methodName="";
		String lineSep="";
		try
		{
			String message = "";
			lineSep=System.getProperty("line.separator");
			lineBuff= new StringBuffer();
			String strReq="";
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			while ((strReq = bufferedReader.readLine()) != null)
				lineBuff.append(strReq+lineSep);
			message = lineBuff.toString();
			
			
			if (_log.isDebugEnabled()) _log.debug("CS5IdeaTestServer2","message = "+message);
			Properties properties = new Properties();
			
			File file = new File(cs5lResponseFilePath);
			properties.load(new FileInputStream(file));
			
			if(message.contains("keySubscriberLookup")){
				responseStr = properties.getProperty("SUBSCRIBER_LOOKUP");
				out.print(responseStr);
			}else if(message.contains("SIDMDNENQREQ")){
				responseStr = properties.getProperty("SUBSCRIBER_SID");
				out.print(responseStr);
			}else if(message.contains("EXRCSTATREQ")){
				responseStr = properties.getProperty("RECHARGE_STATUS");
				out.print(responseStr);
			}else if(message.contains("EXTROAMRCREQ")){
				
				Thread.sleep(Long.parseLong(properties.getProperty("EXTROAMRCREQ_SLEEP").toString()));
				responseStr = properties.getProperty("EXTROAMRCREQ_RES");
				responseStr=responseStr.replace("XXXX", Long.toString(System.currentTimeMillis()));
				out.print(responseStr);
			}
			else{
			indexStart = message.indexOf("<methodName>");
			indexEnd = message.indexOf("</methodName>",indexStart);
			methodName = message.substring("<methodName>".length()+indexStart,indexEnd);
			if(_log.isDebugEnabled()) _log.debug("doPost","methodName::"+methodName);
			
			System.out.println(message);
			
			if ("GetBalanceAndDate".equalsIgnoreCase(methodName.trim()) )
			{
				Thread.sleep(Long.parseLong(properties.getProperty("VALIDATION_SLEEP").toString()));
				responseStr = properties.getProperty("ACCOUNT_INFO");
			}
			else if ("UpdateBalanceAndDate".equalsIgnoreCase(methodName.trim()))
			{
				Thread.sleep(Long.parseLong(properties.getProperty("DEBIT_SLEEP").toString()));

				if (message.contains("<string>-"))
					responseStr = properties.getProperty("ACCOUNT_DEBIT");
				else
					responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");

			}
			else if ("Refill".equalsIgnoreCase(methodName.trim()))
			{
				Thread.sleep(Long.parseLong(properties.getProperty("REFILL_SLEEP").toString()));
				responseStr = properties.getProperty("ACCOUNT_CREDIT");
				 
			}
			out.print(responseStr);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			// out.print(getErrorResponse(CS5I.UNKNOWN_ERROR));
			_log.error("doPost","Exception e:"+e.getMessage());
		}//end of catch-Exception
	}//end of dePost

}
