package com.inter.cs5moldova;

/**
 * @(#)CS5MoldovaUrlConnection.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Zeeshan Aleem        Jul 08, 2017		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import com.btsl.pretups.inter.connection.BTSLConnection;


public class CS5MoldovaUrlConnection extends BTSLConnection
{
	private HttpURLConnection urlConnection=null;

	public CS5MoldovaUrlConnection(String urlParam,String username,String password,int connectTimeout,int readTimeout,String keepAlive,long contentLength, String hostName,String userAgent) throws Exception
	{
		final String methodName = "CS5MoldovaUrlConnection";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_url: "+urlParam+"p_connecttimeout="+connectTimeout+" p_readtimeout="+readTimeout+" p_keepAlive="+keepAlive+" p_hostName="+hostName+" p_userAgent="+userAgent);
		try
		{
			URL url=new URL(urlParam);
			urlConnection=(HttpURLConnection)url.openConnection();
			urlConnection.setConnectTimeout(connectTimeout);
			urlConnection.setReadTimeout(readTimeout);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("POST");
			setRequestHeader(username,password,contentLength,hostName,userAgent,keepAlive);
			setPrintWriter();
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception ::" + e.getMessage());
			throw e;
		}
		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting");
	}

	/**
	 * This method is used to set the Print Writer object for the Connection
	 * @throws	Exception
	 */
	protected void setPrintWriter() throws Exception
	{
		final String methodName = "setPrintWriter";
		try 
		{
			_out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream())),true);
		} 
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception " + e.getMessage());
			throw e;
		}
	}

	/** 
	 * This method is used to set the Buffered Reader for the connection object
	 * @throws	Exception
	 */
	protected void setBufferedReader() throws Exception
	{
		final String methodName = "setBufferedReader";
		try 
		{
			_in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		}
		catch(Exception e) 
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception " + e.getMessage());
			throw e;
		}
	}

	/** 
	 * This method is used to set the HttpUrl connection time out
	 * @param	int p_timeout
	 * @throws	Exception
	 */
	public void setTimeout(int timeout) throws Exception 
	{
		final String methodName = "setTimeout";
		try
		{
			urlConnection.setConnectTimeout(timeout);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception " + e.getMessage());
			throw e;
		}
	}

	/**
	 * This method is used to close the Output,Input Stream and and also Close the HttpUrl Connection.
	 * @throws	Exception
	 */
	public void close() throws Exception
	{
		try{if(_out!=null)_out.close();}catch(Exception e){}
		try{if(_in!=null)_in.close();}catch(Exception e){}
		try{if(urlConnection!=null)urlConnection.disconnect();}catch(Exception e){}
	}

	/**
	 * Method to get the Status Code
	 * @return String
	 * @throws IOException
	 */
	public String getResponseCode() throws IOException 
	{
		int httpStatus =urlConnection.getResponseCode();
		String str=Integer.toString(httpStatus);
		return str;
	}

	/**
	 * This method is used to set the header informations
	 * 1.Host
	 * 2.User Agent
	 * 3.Content length.
	 * 4.Content type. 
	 * @param p_contenetLength
	 * @param p_host
	 * @param p_userAgent
	 * @param p_keepAlive
	 */
	private void setRequestHeader(String username,String password,long contenetLength,String host,String userAgent,String keepAlive) throws Exception
	{
		final String methodName = "setRequestHeader";
		if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_username::"+password+"p_password::"+password+"p_contenetLength::"+contenetLength+" p_host::"+host+" p_userAgent::"+userAgent);
//		BASE64Encoder encode = new BASE64Encoder();
		String userPass=username+":"+password;
//		String encodedPass=encode.encode(userPass.getBytes());
		java.util.Base64.Encoder enc = java.util.Base64.getEncoder();
		String encodedPass =   enc.encodeToString(userPass.getBytes());
		try
		{
			urlConnection.setRequestProperty("Host",host);
			urlConnection.setRequestProperty("User-Agent",userAgent);
			urlConnection.setRequestProperty("Authorization","Basic "+encodedPass);
			urlConnection.setRequestProperty("Content-Length",String.valueOf(contenetLength));
			urlConnection.setRequestProperty("Content-Type","text/xml");
			if("Y".equalsIgnoreCase(keepAlive))
				urlConnection.setRequestProperty("Connection", "keep-alive");
			else
				urlConnection.setRequestProperty("Connection", "close");
		}
		catch(Exception e)
		{
			_log.error("setRequestHeader","Exception e="+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(methodName,"Exited");
		}
	}
}
