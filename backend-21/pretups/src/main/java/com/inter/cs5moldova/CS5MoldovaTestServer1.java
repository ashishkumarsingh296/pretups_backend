package com.inter.cs5moldova;

/**
 * @(#)CS5MoldovaTestServer1.java
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


public class CS5MoldovaTestServer1 extends HttpServlet 
{
	private static final long serialVersionUID = -7508779441513245611L;
	private Log log = LogFactory.getLog(this.getClass().getName());
	private String cs5lResponseFilePath;

	public CS5MoldovaTestServer1() 
	{
		super();
	}

	public void init(ServletConfig conf) throws ServletException
	{
		final String methodName = "init";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered");
		super.init(conf);
		cs5lResponseFilePath = getServletContext().getRealPath(getInitParameter("cs5moldovaxmlfilepath"));
		if(log.isDebugEnabled())log.debug(methodName,"Exiting cs5lResponseFilePath="+cs5lResponseFilePath);
	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		final String methodName = "doGet";
		if (log.isDebugEnabled()) log.debug(methodName,"Entered...Connected to CS5MoldovaTestServer1");
		doPost(request, response);
	}

	@Override
	public void destroy()
	{
		super.destroy(); 
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		final String methodName = "doPost";
		if (log.isDebugEnabled()) 
			log.debug(methodName,"Entered...Connected to CS5MoldovaTestServer1");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String responseStr = null;
		StringBuffer lineBuff=null;
		int indexStart =0;
		int indexEnd=0;
		int tempIndex=0;
		String resMethodName="";
		String lineSep="";
		String msisdn="";
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
			indexStart = message.indexOf("<methodName>");
			indexEnd = message.indexOf("</methodName>",indexStart);
			resMethodName = message.substring("<methodName>".length()+indexStart,indexEnd);
			indexStart = message.indexOf("<name>subscriberNumber");
			indexEnd = message.indexOf("</string>",indexStart);
			tempIndex= message.indexOf("<string>",indexStart);
			msisdn = message.substring("<string>".length()+tempIndex,indexEnd);
			if(log.isDebugEnabled()) 
				log.debug(methodName,"IN-Request for msisdn="+msisdn+" methodName="+resMethodName+" Request String="+message);
			Properties properties = new Properties();
			File file = new File(cs5lResponseFilePath);
			properties.load(new FileInputStream(file));
			//Response for GetBalanceAndDate request
			if ("GetBalanceAndDate".equalsIgnoreCase(resMethodName.trim()))
				responseStr = properties.getProperty("ACCOUNT_INFO");
			//Response for UpdateBalanceAndDate request
			else if ("UpdateBalanceAndDate".equalsIgnoreCase(resMethodName.trim()))
			{
				if(message.contains("<name>dedicatedAccountUpdateInformation"))
					responseStr = properties.getProperty("DEDICATED_ACC_CREDIT_DEBIT");
				if (message.contains("<string>-"))
					responseStr = properties.getProperty("ACCOUNT_DEBIT");
				else
					responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");
			}
			//Response for Refill request
			else if ("Refill".equalsIgnoreCase(resMethodName.trim()))
				responseStr = properties.getProperty("ACCOUNT_CREDIT");
			//Response for GetAccountDetails request
			else if ("GetAccountDetails".equalsIgnoreCase(resMethodName.trim()))
				responseStr = properties.getProperty("ACCOUNT_DETAILS");
			if(log.isDebugEnabled()) 
				log.debug(methodName,"IN-Response for msisdn="+msisdn+" methodName="+resMethodName+" Response String="+responseStr);
			out.print(responseStr);
		} 
		catch (Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception e:"+e.getMessage());
		}
	}
}
