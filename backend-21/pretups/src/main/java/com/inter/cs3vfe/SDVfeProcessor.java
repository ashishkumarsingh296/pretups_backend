/*
 * Created on Nov 21, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cs3vfe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import java.util.Base64;

import com.btsl.pretups.inter.cache.FileCache;
import com.inter.connection.BTSLConnection;

/**
 * @author Administrator
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class SDVfeProcessor extends BTSLConnection {

    private HttpURLConnection _urlConnection = null;

    public SDVfeProcessor() {

    }

    public SDVfeProcessor(String p_interfaceId, String p_msisdn) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("CS3VfeUrlConnection", "Entered p_interfaceId: " + p_interfaceId + "p_msisdn=" + p_msisdn);
        try {
            String p_url = FileCache.getValue(p_interfaceId, "SD_URL");
            String p_username = FileCache.getValue(p_interfaceId, "SD_USER_NAME");
            String p_password = FileCache.getValue(p_interfaceId, "SD_PASSWORD");
            int p_connectTimeout = Integer.parseInt((String) FileCache.getValue(p_interfaceId, "SD_CONNECT_TIMEOUT"));
            int p_readTimeout = Integer.parseInt((String) FileCache.getValue(p_interfaceId, "SD_READ_TIMEOUT"));
            String p_keepAlive = FileCache.getValue(p_interfaceId, "SD_KEEP_ALIVE");
            String p_hostName = FileCache.getValue(p_interfaceId, "SD_HOST_NAME");
            String p_userAgent = FileCache.getValue(p_interfaceId, "SD_USER_AGENT");
            URL url = new URL(p_url);
            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.setConnectTimeout(p_connectTimeout);
            _urlConnection.setReadTimeout(p_readTimeout);
            _urlConnection.setDoOutput(true);
            _urlConnection.setDoInput(true);
            _urlConnection.setRequestMethod("GET");
            // setRequestHeader(p_username,p_password,p_contentLength,p_hostName,p_userAgent,p_keepAlive);
            // setPrintWriter();
            _urlConnection.setRequestProperty("param1", p_msisdn);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("CS3VfeUrlConnection", "Exception " + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("CS3VfeUrlConnection", "Exiting");
    }

    /**
     * This method is used to set the Print Writer object for the Connection
     * 
     * @throws Exception
     */
    protected void setPrintWriter() throws Exception {
        try {
            _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())), true);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setPrintWriter", "Exception " + e.getMessage());
            throw e;
        }
    }

    /**
     * This method is used to set the Buffered Reader for the connection object
     * 
     * @throws Exception
     */
    public void setBufferedReader() throws Exception {
        try {
            _in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setBufferedReader", "Exception " + e.getMessage());
            throw e;
        }
    }

    /**
     * This method is used to set the HttpUrl connection time out
     * 
     * @param int p_timeout
     * @throws Exception
     */
    public void setTimeout(int p_timeout) throws Exception {
        try {
            _urlConnection.setConnectTimeout(p_timeout);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setPrintWriter", "Exception " + e.getMessage());
            throw e;
        }
    }

    /**
     * This method is used to close the Output,Input Stream and and also Close
     * the HttpUrl Connection.
     * 
     * @throws Exception
     */
    public void close() throws Exception {
        try {
            if (_out != null)
                _out.close();
        } catch (Exception e) {
        }
        try {
            if (_in != null)
                _in.close();
        } catch (Exception e) {
        }
        try {
            if (_urlConnection != null)
                _urlConnection.disconnect();
        } catch (Exception e) {
        }
    }

    /**
     * Method to get the Status Code
     * 
     * @return String
     * @throws IOException
     */
    public String getResponseCode() throws IOException {
        int httpStatus = _urlConnection.getResponseCode();
        String str = Integer.toString(httpStatus);
        return str;
    }

    /**
     * This method is used to set the header informations
     * 1.Host
     * 2.User Agent
     * 3.Content length.
     * 4.Content type.
     * 
     * @param p_contenetLength
     * @param p_host
     * @param p_userAgent
     * @param p_keepAlive
     */
    private void setRequestHeader(String p_username, String p_password, long p_contenetLength, String p_host, String p_userAgent, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setRequestHeader", "Entered p_username::" + p_password + "p_password::" + p_password + "p_contenetLength::" + p_contenetLength + " p_host::" + p_host + " p_userAgent::" + p_userAgent);
//        BASE64Encoder encode = new BASE64Encoder();
        String userPass = p_username + ":" + p_password;
//        String encodedPass = encode.encode(userPass.getBytes());
        java.util.Base64.Encoder enc = java.util.Base64.getEncoder();
        String encodedPass =   enc.encodeToString(userPass.getBytes());
        try {
            _urlConnection.setRequestProperty("Host", p_host);
            _urlConnection.setRequestProperty("User-Agent", p_userAgent);
            _urlConnection.setRequestProperty("Authorization", "Basic " + encodedPass);
            _urlConnection.setRequestProperty("Content-Length", String.valueOf(p_contenetLength));
            _urlConnection.setRequestProperty("Content-Type", "text/xml");
            if ("Y".equalsIgnoreCase(p_keepAlive))
                _urlConnection.setRequestProperty("Connection", "keep-alive");
            else
                _urlConnection.setRequestProperty("Connection", "close");
        } catch (Exception e) {
            _log.error("setRequestHeader", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setRequestHeader", "Exited");
        }
    }
}
