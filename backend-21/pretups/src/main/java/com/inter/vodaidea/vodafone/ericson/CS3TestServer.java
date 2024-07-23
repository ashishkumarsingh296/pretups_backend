package com.inter.vodaidea.vodafone.ericson;

	


	import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


	public class CS3TestServer extends HttpServlet 
	{
		private static final long serialVersionUID = -7508779441513245611L;
		private Log _log = LogFactory.getLog(this.getClass().getName());
		private String cs3lResponseFilePath;
		/**
		 * Constructor of the object.
		 */
		public CS3TestServer() {
			super();
		}
		public void init(ServletConfig conf) throws ServletException
		{
			if(_log.isDebugEnabled()) _log.debug("init","Entered");
			super.init(conf);
			cs3lResponseFilePath = getServletContext().getRealPath(getInitParameter("ericsonxmlfilepath"));
			if(_log.isDebugEnabled())_log.debug("init","Exiting cs3lResponseFilePath="+cs3lResponseFilePath);
		}
		public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			if (_log.isDebugEnabled()) _log.debug("CS3TestServer","Entered...Connected to CS3TestServer");
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
			if (_log.isDebugEnabled()) 
				_log.debug("CS3TestServer","Entered...Connected to CS3TestServer");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			String responseStr = null;
			StringBuffer lineBuff=null;
			int indexStart =0;
			int indexEnd=0;
			int tempIndex=0;
			String methodName="";
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
				methodName = message.substring("<methodName>".length()+indexStart,indexEnd);

				indexStart = message.indexOf("<name>subscriberNumber");
				indexEnd = message.indexOf("</string>",indexStart);
				tempIndex= message.indexOf("<string>",indexStart);
				msisdn = message.substring("<string>".length()+tempIndex,indexEnd);

				if(_log.isDebugEnabled()) 
					_log.debug("doPost","IN-Request for msisdn="+msisdn+" methodName="+methodName+" Request String="+message);
				Properties properties = new Properties();
				File file = new File(cs3lResponseFilePath);
				properties.load(new FileInputStream(file));

				//Response for GetBalanceAndDate request
				if ("GetBalanceAndDate".equalsIgnoreCase(methodName.trim()))
					responseStr = properties.getProperty("ACCOUNT_INFO");
				//Response for UpdateBalanceAndDate request
				else if ("UpdateBalanceAndDate".equalsIgnoreCase(methodName.trim()))
				{
					if(message.contains("<name>dedicatedAccountUpdateInformation"))
						responseStr = properties.getProperty("DEDICATED_ACC_CREDIT_DEBIT");
					if (message.contains("<string>-"))
						responseStr = properties.getProperty("ACCOUNT_DEBIT");
					else
						responseStr = properties.getProperty("ACCOUNT_CREDIT_ADJ");
				}
				//Response for Refill request
				else if ("Refill".equalsIgnoreCase(methodName.trim()))
					responseStr = properties.getProperty("ACCOUNT_CREDIT");
				//Response for GetAccountDetails request
				else if ("GetAccountDetails".equalsIgnoreCase(methodName.trim()))
					responseStr = properties.getProperty("ACCOUNT_DETAILS");

				if(_log.isDebugEnabled()) 
					_log.debug("doPost","IN-Response for msisdn="+msisdn+" methodName="+methodName+" Response String="+responseStr);
				//Send the response
				
				// make delay is response for the mobile numbers 
				String msisdnWaitForRefill = properties.getProperty("READ_TIME_OUT_MSISDNS_FOR_CREDIT_REQUEST");
				
				if( msisdnWaitForRefill!= null &&  "Refill".equalsIgnoreCase(methodName.trim()) && (msisdnWaitForRefill.indexOf(msisdn) != -1)){
					System.out.println("Wait at IN for " + msisdnWaitForRefill);
					TimeUnit.SECONDS.sleep(20);
					System.out.println("Wait exit at IN for "+ msisdnWaitForRefill);	
				}
				
				out.print(responseStr);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				// out.print(getErrorResponse(CS3I.UNKNOWN_ERROR));
				_log.error("doPost","Exception e:"+e.getMessage());
			}//end of catch-Exception
		}//end of dePost
	}


