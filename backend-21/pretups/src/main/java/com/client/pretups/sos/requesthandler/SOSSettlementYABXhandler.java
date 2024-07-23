package com.client.pretups.sos.requesthandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceModuleI;
import com.btsl.util.Constants;

import java.util.Base64;

public class SOSSettlementYABXhandler  implements InterfaceModuleI{

	private static Log log = LogFactory.getLog(SOSSettlementYABXhandler.class.getName());
	 private HttpURLConnection urlConnection=null;
	 private PrintWriter oUT = null;
	 private BufferedReader iN = null;
	   
	
	public String process(String requestStr) {
		
		final String methodName = "process";
		
		  if (log.isDebugEnabled()) {
			  StringBuilder loggerValue= new StringBuilder();
			   loggerValue.setLength(0);
	        	loggerValue.append("Entered methodName = " + methodName);
	        	loggerValue.append(",p_requestStr="+requestStr);
	        	log.debug(methodName, loggerValue);
	        }
		return sendRequestToIN(requestStr);
	}

public String sendRequestToIN(String pInRequestStr) {

	final String methodName = "sendRequestToIN";
	StringBuilder loggerValue= new StringBuilder();
	
	if (log.isDebugEnabled()) {
		loggerValue.setLength(0);
       	loggerValue.append("Entered methodName = " + methodName);
       	loggerValue.append(",p_requestStr="+pInRequestStr);
       	log.debug(methodName, loggerValue);
       }
	String url = Constants.getProperty("YABX_SETTLEMENT_URL");
	String username = Constants.getProperty("YABX_SETTLEMENT_USERNAME");
	String password = Constants.getProperty("YABX_SETTLEMENT_PASSWORD");
	int timeout = Integer.parseInt(Constants.getProperty("YABX_SETTLEMENT_TIMEOUT"));
	int readTimeOut= Integer.parseInt(Constants.getProperty("YABX_SETTLEMENT_READTIMEOUT"));
	String keepAlive = Constants.getProperty("YABX_SETTLEMENT_KEEPALIVE");
	String hostname = Constants.getProperty("YABX_SETTLEMENT_HOSTNAME");
	String agent = Constants.getProperty("YABX_SETTLEMENT_AGENT");
	 
	int  startTime=0;
    int endTime =0;
    
	try
     {
		 URL httpURL=new URL(url);
		urlConnection=(HttpURLConnection)httpURL.openConnection();
		urlConnection.setConnectTimeout(timeout);
		urlConnection.setReadTimeout(readTimeOut);
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setUseCaches(false);
		urlConnection.setRequestProperty("Cache-Control", "no-cache");
		setRequestHeader(username,password,pInRequestStr.length(),hostname,agent,keepAlive);
		
		urlConnection.connect();
		setPrintWriter();
		
		if (log.isDebugEnabled()) {
		loggerValue.setLength(0);
       	loggerValue.append("url:");
       	loggerValue.append(url);
       	loggerValue.append("password:");
       	loggerValue.append(password);
       	loggerValue.append("timeout");
       	loggerValue.append(timeout);
       	loggerValue.append("readTimeout:");
       	loggerValue.append(readTimeOut);
       	loggerValue.append("keepAlive");
       	loggerValue.append(keepAlive);
       	loggerValue.append("pInRequestStr:");
       	loggerValue.append(pInRequestStr);
       	loggerValue.append("hostname:");
       	loggerValue.append(hostname);
       	loggerValue.append(",agent=");
       	loggerValue.append(agent);
       	log.debug(methodName, loggerValue);
		}
		 PrintWriter out = getPrintWriter();
		 out.flush();
		 startTime=(int) System.currentTimeMillis();
			
         out.println(pInRequestStr);
         out.flush();
     }
     catch(Exception e)
     {
    	 		loggerValue.setLength(0);
    	       	loggerValue.append(PretupsI.EXCEPTION );
    	       	loggerValue.append(e.getMessage());
    	        log.error(methodName, loggerValue);
    	}
	
     StringBuilder builder = new StringBuilder();
     String response = "";
     try
     {
         //Get the response from the IN 
    	 	setBufferedReader();
         BufferedReader in = getBufferedReader();
         //Reading the response from buffered reader.
         while ((response = in.readLine()) != null)
         {
        	 builder.append(response);
         }
			endTime=(int) System.currentTimeMillis();
			
     }
     catch(SocketTimeoutException ste)
     {
     	 
    	 	loggerValue.setLength(0);
	       	loggerValue.append(PretupsI.EXCEPTION);
	       	loggerValue.append(ste.getMessage());
	       	log.error(methodName, loggerValue);
			
    	 
     }//end of catch-Exception
     catch(Exception e)
     {
         
    	 	loggerValue.setLength(0);
	       	loggerValue.append(PretupsI.EXCEPTION);
	       	loggerValue.append(e.getMessage());
	        log.error(methodName, loggerValue);
			
    	 
     }//end of catch-Exception
     finally
     {
			if(endTime==0)
				endTime=(int) System.currentTimeMillis();
			
			 if(log.isDebugEnabled()) {
				 loggerValue.setLength(0);
			       	loggerValue.append("total time taken at YABX for sttlement:");
			       	loggerValue.append((endTime-startTime));
			       	loggerValue.append("ms");
			        log.debug(methodName, loggerValue); 
			 }
			 	
				 
	   }//end of finally
     
     String responseStr = builder.toString();
     if(log.isDebugEnabled()) {
		 loggerValue.setLength(0);
	       	loggerValue.append("responseStr:");
	       	loggerValue.append(responseStr);
	        log.debug(methodName, loggerValue); 
	 }
    
 	
     return responseStr;
}


/**
 * This method is used to set the header informations
 * 1.Host
 * 2.User Agent
 * 3.Content length.
 * 4.Content type. 
 * @param pContenetLength
 * @param pHost
 * @param pUserAgent
 * @param pKeepAlive
 */
private void setRequestHeader(String pUsername,String pPassword,long pContenetLength,String pHost,String pUserAgent,String pKeepAlive) throws BTSLBaseException
{
	final String setRequestMethodName = "setRequestHeader";
	StringBuilder loggerValue= new StringBuilder();
	
	if(log.isDebugEnabled()) {
		 loggerValue.setLength(0);
	      	loggerValue.append(PretupsI.ENTERED);
	     	loggerValue.append(" pUsername::");
	     	loggerValue.append(pUsername);
	     	loggerValue.append("pPassword::");
	     	loggerValue.append(pPassword);
	     	loggerValue.append("pContenetLength::");
	     	loggerValue.append(pContenetLength);
	     	loggerValue.append(" pHost::");
	    	loggerValue.append(pHost);
	    	loggerValue.append(" pUserAgent::");
	    	loggerValue.append(pUserAgent);
	        log.debug(setRequestMethodName, loggerValue); 
	 }
	
    //BASE64Encoder encode = new BASE64Encoder();
	java.util.Base64.Encoder encode = java.util.Base64.getEncoder();
	//return enc.encodeToString(data);


	String userPass=pUsername+":"+pPassword;
	String encodedPass=encode.encodeToString(userPass.getBytes());
    try
    {
	    urlConnection.setRequestProperty("Host",userPass);//Commented their
	    urlConnection.setRequestProperty("User-Agent",pUserAgent);
	    urlConnection.setRequestProperty("Authorization","Basic "+encodedPass);
	    urlConnection.setRequestProperty("Content-Length",String.valueOf(pContenetLength));//Check
	    urlConnection.setRequestProperty("Content-Type","text/xml");
	    if("Y".equalsIgnoreCase(pKeepAlive))//Check this condition
	        urlConnection.setRequestProperty("Connection", "keep-alive");
	    else
	        urlConnection.setRequestProperty("Connection", "close");
	    
	    urlConnection.setRequestProperty("Cache-Control", "no-cache");
    }
    catch(Exception e)
    {
    	 	loggerValue.setLength(0);
   	      	loggerValue.append(PretupsI.EXCEPTION);
   	     	loggerValue.append(e.getMessage());
   	     	log.error(setRequestMethodName, loggerValue); 
   	 
        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
    }
    finally
    {
        if(log.isDebugEnabled())
    	 	loggerValue.setLength(0);
	      	loggerValue.append(PretupsI.EXITED);
	     	log.debug(setRequestMethodName, loggerValue); 
    }
    
}


private void setPrintWriter() throws BTSLBaseException
{
	try 
	{
		oUT = new PrintWriter(new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(),"UTF-8")),true);
	} 
	catch(Exception e)
	{
		log.error("setPrintWriter",PretupsI.EXCEPTION+ e.getMessage());
		
        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
	}
}

/**
 * @return
 */
private PrintWriter getPrintWriter() {
    return oUT;
}

/**
 * @return
 */
private BufferedReader getBufferedReader() {
    return iN;
}


private void setBufferedReader() throws BTSLBaseException
{
	try 
	{
		iN = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	}
	catch(Exception e) 
	{
		log.error("setBufferedReader",PretupsI.EXCEPTION+e.getMessage());
		throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
	}
}

@Override
public Map process1(String p_requestStr) {
	// TODO Auto-generated method stub
	return null;
}

}
