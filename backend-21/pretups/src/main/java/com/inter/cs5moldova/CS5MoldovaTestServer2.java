package com.inter.cs5moldova;

/**
 * @(#)CS5MoldovaTestServer2.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Zeeshan Aleem        Jul 08, 2017		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 */

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

public class CS5MoldovaTestServer2 extends HttpServlet 
{
	private Log log = LogFactory.getLog(this.getClass().getName());

	private String cs5lResponseFilePath;

	public CS5MoldovaTestServer2() 
	{
		super();
	}
	
	@Override
	public void init(ServletConfig conf) throws ServletException
	{
		final String methodName = "init";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered");
		super.init(conf);
		cs5lResponseFilePath = getServletContext().getRealPath(getInitParameter("cs5moldovaxmlfilepath"));
		if(log.isDebugEnabled())log.debug(methodName,"Exiting cs5lResponseFilePath="+cs5lResponseFilePath);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		final String methodName = "doGet";
		if (log.isDebugEnabled()) log.debug(methodName,"Entered...Connected to CS5MoldovaTestServer2");
		doPost(request, response);
	}

	public void destroy()
	{
		super.destroy(); 
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		final String methodName = "doPost";
		if (log.isDebugEnabled()) log.debug(methodName,"Entered...Connected to CS5MoldovaTestServer2");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String responseStr = null;
		StringBuffer lineBuff=null;
		int indexStart =0;
		int indexEnd=0;
		String resMethodName="";
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
			if (log.isDebugEnabled()) log.debug(methodName,"message = "+message);
			indexStart = message.indexOf("<methodName>");
			indexEnd = message.indexOf("</methodName>",indexStart);
			resMethodName = message.substring("<methodName>".length()+indexStart,indexEnd);
			if(log.isDebugEnabled()) log.debug("doPost","methodName::"+resMethodName);
			Properties properties = new Properties();
			File file = new File(cs5lResponseFilePath);
			properties.load(new FileInputStream(file));
			if ("GetBalanceAndDate".equalsIgnoreCase(resMethodName.trim()))
			{
				responseStr = properties.getProperty("ACCOUNT_INFO");
			}
			else if ("UpdateBalanceAndDate".equalsIgnoreCase(resMethodName.trim()))
			{
				if (message.contains("<string>-"))
					responseStr = properties.getProperty("ACCOUNT_DEBIT");
				else
					responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");
			}
			else if ("Refill".equalsIgnoreCase(resMethodName.trim()))
			{
				responseStr = properties.getProperty("ACCOUNT_CREDIT");
			}
			else if ("GetAccountDetails".equalsIgnoreCase(resMethodName.trim()))
			{
				responseStr = properties.getProperty("ACCOUNT_DETAILS");
			}
			out.print(responseStr);
		} 
		catch (Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception e:"+e.getMessage());
		}
	}
}
