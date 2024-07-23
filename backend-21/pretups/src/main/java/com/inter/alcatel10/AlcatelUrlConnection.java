package com.inter.alcatel10;

/**
 * @(#)AlcatelUrlConnection.java
 *                               Copyright(c) 2005, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Gurjeet Singh Bedi Oct 17,2005 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 *                               Connection class for the alcatel interface
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.inter.connection.BTSLConnection;

public class AlcatelUrlConnection extends BTSLConnection {

    private HttpURLConnection _urlConnection = null;

    /**
     * Constructor to create a new URL connection
     * 
     * @param p_url
     * @param p_userName
     * @param p_password
     * @param p_protocolVersion
     * @throws Exception
     */
    public AlcatelUrlConnection(String p_url, int p_connecttimeout, int p_readtimeout, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("AlcatelUrlConnection", "Entered p_url: " + p_url + "p_connecttimeout=" + p_connecttimeout + " p_readtimeout=" + p_readtimeout + " p_keepAlive=" + p_keepAlive);
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
            // setBufferedReader();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("AlcatelUrlConnection", "Exception " + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("AlcatelUrlConnection", "Exiting");
    }

    /**
     * To set the Out stream
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
            _log.debug("setRequestHeader", "Entered p_keepAlive::" + p_keepAlive);
        try {
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

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
