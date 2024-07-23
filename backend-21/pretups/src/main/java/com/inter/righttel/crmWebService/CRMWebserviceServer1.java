package com.inter.righttel.crmWebService;

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


public class CRMWebserviceServer1 extends HttpServlet {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private String cs5lResponseFilePath;

	/**
	 * Constructor of the object.
	 */
	public CRMWebserviceServer1() {
		super();
	}
	public void init(ServletConfig conf) throws ServletException
	{
		if(_log.isDebugEnabled()) _log.debug("init","Entered");
		super.init(conf);
		cs5lResponseFilePath = getInitParameter("crmaxmlfilepath");
		if(_log.isDebugEnabled())_log.debug("init","Exiting cs5lResponseFilePath="+cs5lResponseFilePath);
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (_log.isDebugEnabled()) _log.debug("CRMWebserviceServer1","Entered...Connected to CRMWebserviceServer1");
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
		if (_log.isDebugEnabled()) _log.debug("CRMWebserviceServer1","Entered...Connected to CRMWebserviceServer1");
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
			
			
			if (_log.isDebugEnabled()) _log.debug("CRMWebserviceServer1","message = "+message);
			Properties properties = new Properties();
			
			File file = new File(cs5lResponseFilePath);
			properties.load(new FileInputStream(file));
			
			if(message.contains("DIRECT_DEBITING")){
				responseStr = properties.getProperty("DIRECT_DEBITING");
				out.print(responseStr);
			}
			else{
			responseStr = properties.getProperty("DIRECT_DEBITING");
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
