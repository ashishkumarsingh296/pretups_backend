package com.inter.alcatel452;

/**
 * @(#)Alcatel452UrlConnection.java
 *                                  Copyright(c) 2008, Bharti Telesoft Int.
 *                                  Public Ltd.
 *                                  All Rights Reserved
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Vinay Kumar Singh Aug 04, 2008 Initial
 *                                  Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 *                                  This class will stablished the HTTP
 *                                  connection between IN and IN Module.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.connection.BTSLConnection;

public class Alcatel452UrlConnection extends BTSLConnection {
    private HttpURLConnection _urlConnection = null;
    public static Log _log = LogFactory.getLog("Alcatel452UrlConnection".getClass().getName());

    /**
     * Constructor to create a new URL connection
     * 
     * @param p_url
     * @param p_userName
     * @param p_password
     * @param p_protocolVersion
     * @throws Exception
     */
    public Alcatel452UrlConnection(String p_url, int p_connecttimeout, int p_readtimeout, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("Alcatel452UrlConnection ", "Entered p_url: " + p_url + "p_connecttimeout=" + p_connecttimeout + " p_readtimeout=" + p_readtimeout + " p_keepAlive=" + p_keepAlive);
        try {
            URL url = new URL(p_url);
            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.setConnectTimeout(p_connecttimeout);
            _urlConnection.setReadTimeout(p_readtimeout);
            _urlConnection.setDoOutput(true);
            _urlConnection.setDoInput(true);
            _urlConnection.setRequestMethod("POST");
            setRequestHeader(p_keepAlive);
            setPrintWriter();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("Alcatel452UrlConnection", " Exception: " + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("Alcatel452UrlConnection ", "Exiting ");
    }

    /**
     * To set the Out stream
     */
    protected void setPrintWriter() throws Exception {
        try {
            _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())), true);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setPrintWriter", " Exception: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method to set the Input stream
     */
    protected void setBufferedReader() throws Exception {
        try {
            _in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setBufferedReader ", "Exception: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method to set the Timeout value
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
     * Method to close the URL Connection
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
     * 1.keep-Alive
     * 
     * @param p_keepAlive
     */
    private void setRequestHeader(String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setRequestHeader ", "Entered p_keepAlive::" + p_keepAlive);
        try {
            if ("Y".equalsIgnoreCase(p_keepAlive))
                _urlConnection.setRequestProperty("Connection ", "keep-alive");
            else
                _urlConnection.setRequestProperty("Connection ", "close");
        } catch (Exception e) {
            _log.error("setRequestHeader", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setRequestHeader", "Exited");
        }
    }
}
