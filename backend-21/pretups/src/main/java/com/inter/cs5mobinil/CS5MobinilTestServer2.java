package com.inter.cs5mobinil;

/**
 * @(#)CS5MobinilTestServer2.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Vikas Jauhari        Mar 29, 2012		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CS5MobinilTestServer2 extends HttpServlet 
{
	private Log _log = LogFactory.getLog(this.getClass().getName());

	private String cs5lResponseFilePath;
	Properties properties = new Properties();
	
	public CS5MobinilTestServer2() 
	{
		super();
	}
	public void init(ServletConfig conf) throws ServletException
	{
		if(_log.isDebugEnabled()) _log.debug("init","Entered");
		super.init(conf);
		cs5lResponseFilePath = getServletContext().getRealPath(getInitParameter("cs5mobinilxmlfilepath2"));
		if(_log.isDebugEnabled())_log.debug("init","Exiting cs5lResponseFilePath="+cs5lResponseFilePath);
		File file = new File(cs5lResponseFilePath);
		try {
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (_log.isDebugEnabled()) _log.debug("CS5MobinilTestServer2","Get is Called Entered...Connected to CS5MobinilTestServer1");
		//doPost(request, response);
		String msisdn = request.getParameter("msisdn");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String responseStr = null;

		if (_log.isDebugEnabled())
			_log.debug("doGet", "GET PARAMETER ::" + msisdn);

		Properties properties = new Properties();
		File file = new File(cs5lResponseFilePath);
		properties.load(new FileInputStream(file));
		//Response for Validation request
		responseStr = properties.getProperty("ACCOUNT_INFO_MATRIX");
		if(_log.isDebugEnabled()) 
			_log.debug("doGet","IN-Response for msisdn="+msisdn+" methodName= Response String="+responseStr);
		out.print(responseStr);
	}

	public void destroy()
	{
		super.destroy(); 
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (_log.isDebugEnabled()) _log.debug("CS5MobinilTestServerPostCreditMatrix","Entered...Connected to CS5MobinilTestServer2");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		StringBuffer lineBuff=null;
		String methodName="CS5MobinilTestServerPostCreditMatrix";
		String lineSep="";
		//JSONObject jo = new JSONObject();
		int getBalanceSleepCounter=50;
		int refillSleepCounter=55;
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
			if(_log.isDebugEnabled()) 
				_log.debug("doPost","IN-Request for methodName="+methodName+" Request String="+message);
			String responseStr = properties.getProperty("CREDIT_INFO_MATRIX");
			if(_log.isDebugEnabled()) 
				_log.debug("doPost","IN-Response for  methodName= Response String="+responseStr);

			JSONParser parser = new JSONParser();  
			JSONObject json = (JSONObject) parser.parse(responseStr);  

			/*jo.put("mtx_container_name", "MtxRequestSubscriberPurchaseOffer");
            JSONArray ja = new JSONArray();
            Map<String, Object> m1 = new LinkedHashMap<String, Object>(3);
            m1.put("mtx_container_name", "MtxPurchasedOfferData");
            m1.put("CatalogItemId", "s");
            Map<String, String> m2 = new LinkedHashMap<String, String>(7);
            m2.put("mtx_container_name", "home");
            m2.put("CurrencyGrant", "C");
            m2.put("ChannelId", "B");
            m2.put("SourceMSISDN", "a");
            m2.put("TransactionId", "a1");
            m2.put("Remarks", "212 555-1234");
            m2.put("VendorId", "Comviva");
            m1.put("Attr", m2);  

            ja.add(m1);
            jo.put("OfferRequestArray", ja);

            if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+jo.toJSONString());
			 */
			// writing JSON to file:"JSONExample.json" in cwd
			PrintWriter pw = new PrintWriter("JSONExampleCreditResponse.json");
			pw.write(json.toJSONString());

			pw.flush();
			pw.close();
			//stringBuffer.append(jo.toJSONString());
			if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+json.toJSONString());
			out.print(json);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("doPost","Exception e:"+e.getMessage());
		}
	}
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (_log.isDebugEnabled()) _log.debug("CS5MobinilTestServerPutCreditMatrix","Entered...Connected to CS5MobinilTestServer2");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		StringBuffer lineBuff=null;
		String methodName="CS5MobinilTestServerPostCreditMatrix";
		String lineSep="";
		//JSONObject jo = new JSONObject();
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
			if(_log.isDebugEnabled()) 
				_log.debug("doPut","IN-Request for methodName="+methodName+" Request String="+message);
			Properties properties = new Properties();
			File file = new File(cs5lResponseFilePath);
			properties.load(new FileInputStream(file));
			String responseStr = properties.getProperty("CREDIT_INFO_MATRIX");
			if(_log.isDebugEnabled()) 
				_log.debug("doPut","IN-Response for  methodName= Response String="+responseStr);
/*
			JSONParser parser = new JSONParser();  
			JSONObject json = (JSONObject) parser.parse(responseStr);  

			jo.put("mtx_container_name", "MtxRequestSubscriberPurchaseOffer");
            JSONArray ja = new JSONArray();
            Map<String, Object> m1 = new LinkedHashMap<String, Object>(3);
            m1.put("mtx_container_name", "MtxPurchasedOfferData");
            m1.put("CatalogItemId", "s");
            Map<String, String> m2 = new LinkedHashMap<String, String>(7);
            m2.put("mtx_container_name", "home");
            m2.put("CurrencyGrant", "C");
            m2.put("ChannelId", "B");
            m2.put("SourceMSISDN", "a");
            m2.put("TransactionId", "a1");
            m2.put("Remarks", "212 555-1234");
            m2.put("VendorId", "Comviva");
            m1.put("Attr", m2);  

            ja.add(m1);
            jo.put("OfferRequestArray", ja);

            if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+jo.toJSONString());
			
			// writing JSON to file:"JSONExample.json" in cwd
			PrintWriter pw = new PrintWriter("JSONExampleCreditResponse.json");
			pw.write(json.toJSONString());

			pw.flush();
			pw.close();
 */
			//stringBuffer.append(jo.toJSONString());
			if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+responseStr);
			out.print(responseStr);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("doPut","Exception e:"+e.getMessage());
		}
	}


}
