/* 
 * #RadixAirtelUrlConnection.java
 *
 *------------------------------------------------------------------------------------------------
 *  Name                  Version		 Date            	History
 *-------------------------------------------------------------------------------------------------
 *  Mahindra Comviva       1.0     		04/09/2014         	Initial Creation
 *-------------------------------------------------------------------------------------------------
 *
 * Copyright(c) 2005 Comviva Technologies Ltd.
 *
 */

package com.inter.radix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.btsl.pretups.inter.cache.FileCache;
import com.inter.connection.BTSLConnection;
import com.btsl.util.Constants;

public class RadixAirtelUrlConnection extends BTSLConnection{
	
	private HttpURLConnection _urlConnection=null;
	Map<String, List<String>> responseMap=null;
	
	
	public RadixAirtelUrlConnection(String p_url,String p_interfaceID,int p_connectTimeout,int p_readTimeout) throws Exception
    {
		if(_log.isDebugEnabled())_log.debug("RadixAirtelUrlConnection","Entered p_url: "+p_url+"p_connecttimeout="+p_connectTimeout+" p_readtimeout="+p_readTimeout);
		try
		{
			String allowedProtocolstatus=null;
			try{
			 allowedProtocolstatus=FileCache.getValue(p_interfaceID,"HTTPS_ALLOWED_FOR_RADIX");
			}
			catch (Exception e) {
				allowedProtocolstatus="Y";
			}
			if(allowedProtocolstatus!=null && allowedProtocolstatus.equalsIgnoreCase("Y"))
			{
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					for (int i = 0; i < certs.length; i++) {

					}
				}
			} };

			try {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
				e.printStackTrace();
			}

			HostnameVerifier hv = new HostnameVerifier() {

				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
			}
			 URL url=new URL(p_url);
			 _urlConnection = (HttpURLConnection) url.openConnection();
			_urlConnection.setConnectTimeout(p_connectTimeout);
			_urlConnection.setReadTimeout(p_readTimeout);
			_urlConnection.setDoOutput(true);
			_urlConnection.setDoInput(true);
			_urlConnection.setRequestMethod("POST");
			setPrintWriter();
			responseHeader();
		}
		catch(Exception e)
		{
			e.printStackTrace();
	       _log.error("RadixAirtelUrlConnection", "Exception " + e.getMessage());
	        throw e;
  		}
		if(_log.isDebugEnabled())_log.debug("RadixAirtelUrlConnection","Exiting");
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
			_out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())),true);
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("setPrintWriter", "Exception " + e.getMessage());
	        throw e;
		}
    }
    protected Map<String, List<String>> responseHeader() throws Exception
    {
    	
		try 
		{
		responseMap	=_urlConnection.getHeaderFields();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("setPrintWriter", "Exception " + e.getMessage());
	        throw e;
		}
		if(_log.isDebugEnabled())_log.debug("responseHeader Exiting",responseMap.toString());
		return responseMap;
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
			_in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			_log.error("setBufferedReader", "Exception " + e.getMessage());
			throw e;
		}
	}
    /** 
     * This method is used to set the HttpUrl connection time out
     * @param	int p_timeout
     * @throws	Exception
     */
    @Override
	public void setTimeout(int p_timeout) throws Exception 
    {
		try
		{
			_urlConnection.setConnectTimeout(p_timeout);
		}
		catch(Exception e)
		{
			e.printStackTrace();
	       _log.error("setPrintWriter", "Exception " + e.getMessage());
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
		try{if(_out!=null)_out.close();}catch(Exception e){}
		try{if(_in!=null)_in.close();}catch(Exception e){}
		try{if(_urlConnection!=null)_urlConnection.disconnect();}catch(Exception e){}
	}
	/**
	 * Method to get the Status Code
	 * @return String
	 * @throws IOException
	 */
   public String getResponseCode() throws IOException {
        int httpStatus =_urlConnection.getResponseCode();
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

	    

}
