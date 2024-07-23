package com.inter.alcatel432;

/**
 * @(#)AlcatelIN432Handler.java
 *                              Copyright(c) 2006, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ashish Kumar May 04,2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              Interface class for the Alcatel432 Interface
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.inter.connection.BTSLConnection;

public class Alcatel432UrlConnection extends BTSLConnection {

    private HttpURLConnection _urlConnection = null;

    /**
     * Constructor to create a new URL connection
     * 
     * @param String
     *            p_url
     * @param int p_connecttimeout
     * @param int p_readtimeout
     * @param String
     *            p_keepAlive
     * @throws Exception
     */
    public Alcatel432UrlConnection(String p_url, int p_connectTimeout, int p_readTimeout, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("Alcatel432UrlConnection", "Entered p_url: " + p_url + "p_connecttimeout=" + p_connectTimeout + " p_readtimeout=" + p_readTimeout + " p_keepAlive=" + p_keepAlive);
        try {
            URL url = new URL(p_url);
            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.setConnectTimeout(p_connectTimeout);
            _urlConnection.setReadTimeout(p_readTimeout);
            _urlConnection.setDoOutput(true);
            _urlConnection.setDoInput(true);
            _urlConnection.setRequestMethod("POST");
            setKeepAlive(p_keepAlive);
            setPrintWriter();
            // setBufferedReader();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("Alcatel432UrlConnection", "Exception " + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("Alcatel432UrlConnection", "Exiting");
    }

    private void setKeepAlive(String p_keepAlive) throws Exception {
        try {
            if ("Y".equalsIgnoreCase(p_keepAlive))
                _urlConnection.setRequestProperty("Connection", "keep-alive");
            else
                _urlConnection.setRequestProperty("Connection", "close");
        } catch (Exception e) {
            _log.error("setKeepAlive", "Exception e=" + e.getMessage());
            throw e;
        }
    }

    /**
     * To set the Out stream
     * 
     * @return void
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
     * Method to set the Input stream
     * 
     * @return void
     * @throws Exception
     */
    protected void setBufferedReader() throws Exception {
        try {
            _in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setBufferedReader", "Exception " + e.getMessage());
            throw e;
        }

    }

    /**
     * Method to set the Timeout value
     * 
     * @return void
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
     * Method to close the URL Connection
     * 
     * @return void
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
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
