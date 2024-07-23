package com.inter.roam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.inter.connection.BTSLConnection;

/**
 * @(#)RoamUrlConnection
 *                       Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       vikasy feb 20,2009 Initial Creation
 *                       ------------------------------------------------------
 *                       ------------------------------------------
 *                       This Class is responsible to create a HttpUrl
 *                       Connection.
 */
public class RoamUrlConnection extends BTSLConnection {
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
    public RoamUrlConnection(String p_url, int p_connecttimeout, int p_readtimeout, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("RoamUrlConnection", "Entered p_url: " + p_url + "p_connecttimeout=" + p_connecttimeout + " p_readtimeout=" + p_readtimeout + " p_keepAlive=" + p_keepAlive);
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
            _log.error("RoamUrlConnection", "Exception " + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("RoamUrlConnection", "Exiting");
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
     * 1.keep-Alive
     * 
     * @param p_keepAlive
     */
    private void setRequestHeader(String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setRequestHeader", "Entered p_keepAlive::" + p_keepAlive);
        try {
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
