package com.inter.claro.cs5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.util.Base64;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.inter.connection.BTSLConnection;

public class CS5ClaroUrlConnection extends BTSLConnection{
	
	private static final Log log = LogFactory.getLog(CS5ClaroUrlConnection.class);
	
    private HttpURLConnection urlConnection=null;
    
    public CS5ClaroUrlConnection(String pUrl,String pUsername,String pPassword,int pConnectTimeout,int pReadTimeout,String pKeepAlive,long pContentLength, String pHostName,String pUserAgent) throws Exception
    {
    	final String methodName = "CS5ClaroUrlConnection";
		if(log.isDebugEnabled())
			log.debug(methodName, PretupsI.ENTERED + " pUrl: "+pUrl+"p_connecttimeout="+pConnectTimeout+" p_readtimeout="+pReadTimeout+" pKeepAlive="+pKeepAlive+" pHostName="+pHostName+" pUserAgent="+pUserAgent);
		try
		{
			URL url=new URL(pUrl);
			urlConnection=(HttpURLConnection)url.openConnection();
			urlConnection.setConnectTimeout(pConnectTimeout);
			urlConnection.setReadTimeout(pReadTimeout);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Cache-Control", "no-cache");
			
			setRequestHeader(pUsername,pPassword,pContentLength,pHostName,pUserAgent,pKeepAlive);
			urlConnection.connect();
			setPrintWriter();
			
		}catch(SocketTimeoutException ste)
		{
	       log.error(methodName, PretupsI.EXCEPTION + ste.getMessage());
	       throw ste;
  		}
		catch(Exception e)
		{
	       log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
	       throw e;
  		}
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.EXITED);
	}
    /**
     * This method is used to set the Print Writer object for the Connection
     * @throws	Exception
     */
    
    @Override
    protected void setPrintWriter() throws Exception
    {
		try 
		{
			_out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(),"UTF-8")),true);
		} 
		catch(Exception e)
		{
			log.error("setPrintWriter",PretupsI.EXCEPTION+ e.getMessage());
	        throw e;
		}
    }
    
    
    /**
     * @return
     */
    @Override
    public BufferedReader getBufferedReader() {
        return _in;
    }
    
    
    
    /** 
     * This method is used to set the Buffered Reader for the connection object
     * @throws	Exception
     */
    
    @Override
    protected void setBufferedReader() throws Exception
    {
		try 
		{
			_in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		}
		catch(Exception e) 
		{
			log.error("setBufferedReader",PretupsI.EXCEPTION+e.getMessage());
			throw e;
		}
	}
    /** 
     * This method is used to set the HttpUrl connection time out
     * @param	int pTimeout
     * @throws	Exception
     */
    
    @Override
    public void setTimeout(int pTimeout) throws Exception 
    {
		try
		{
			urlConnection.setConnectTimeout(pTimeout);
		}
		catch(Exception e)
		{
	        log.error("setPrintWriter",PretupsI.EXCEPTION+e.getMessage());
	        throw e;
		}
	}
    /**
     * This method is used to close the Output,Input Stream and and also Close the HttpUrl Connection.
     * @throws	Exception
     */
    
    @Override
    public void close() throws Exception
    {
		try
		{
			if(_out!=null)
				_out.close();
			}
		catch(Exception e)
		{
			throw e;
		}
		
		try
		{
			if(_in!=null)
				_in.close();
			}
		catch(Exception e)
		{
			throw e;
		}
		
		try
		{
			if(urlConnection!=null)
				urlConnection.disconnect();
			}
		catch(Exception e)
		{
			throw e;
		}
	}
	/**
	 * Method to get the Status Code
	 * @return String
	 * @throws IOException
	 */
   public String getResponseCode() throws IOException {
        int httpStatus =urlConnection.getResponseCode();
        return Integer.toString(httpStatus);
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
	private void setRequestHeader(String pUsername,String pPassword,long pContenetLength,String pHost,String pUserAgent,String pKeepAlive) throws Exception
	{
		final String setRequestMethodName = "setRequestHeader";
		
	    if(log.isDebugEnabled())
	    	log.debug(setRequestMethodName,PretupsI.ENTERED+" pUsername::"+pPassword+"pPassword::"+pPassword+"pContenetLength::"+pContenetLength+" pHost::"+pHost+" pUserAgent::"+pUserAgent);
	    BASE64Encoder encode = new BASE64Encoder();
		String userPass=pUsername+":"+pPassword;
		String encodedPass=encode.encode(userPass.getBytes()); 
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
	        log.error(setRequestMethodName,PretupsI.EXCEPTION+e.getMessage());
	        throw e;
	    }
	    finally
	    {
	        if(log.isDebugEnabled())
	        	log.debug(setRequestMethodName,PretupsI.EXITED);
	    }
	    
	}
}
