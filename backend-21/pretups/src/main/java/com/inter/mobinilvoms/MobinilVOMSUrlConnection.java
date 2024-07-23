package com.inter.mobinilvoms;

/*
 * @(#)MobinilVOMSUrlConnection.java
 * ----------------------------------------------------------------------
 * Name Date History
 * ------------------------------------------------------------------------
 * Vinay Kumar Singh 22/11/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * Connector class for Voucher Management System to handle the connection with
 * IN.
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

public class MobinilVOMSUrlConnection extends BTSLConnection {
    private Log _log = LogFactory.getLog(MobinilVOMSUrlConnection.class.getName());
    private HttpURLConnection _urlConnection = null;

    /**
     * Constructor to create a new URL connection
     * 
     * @param p_url
     * @param p_connecttimeout
     * @param p_readtimeout
     * @param p_keepAlive
     * @throws Exception
     */
    public MobinilVOMSUrlConnection(String p_url, int p_connecttimeout, int p_readtimeout, String p_keepAlive, String p_userAgent, String p_contentType, String p_authorization) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("MobinilVOMSUrlConnection[constructor]", "Entered p_url=" + p_url + ", p_connecttimeout=" + p_connecttimeout + ", p_readtimeout=" + p_readtimeout + ", p_keepAlive=" + p_keepAlive + ", p_userAgent=" + p_userAgent + ", p_contentType=" + p_contentType + ", p_authorization=" + p_authorization);
        try {
            URL url = new URL(p_url);
            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.setConnectTimeout(p_connecttimeout);
            _urlConnection.setReadTimeout(p_readtimeout);
            _urlConnection.setDoOutput(true);
            _urlConnection.setDoInput(true);
            _urlConnection.setRequestMethod("POST");
            setRequestHeader(p_keepAlive, p_userAgent, p_contentType, p_authorization);
            setPrintWriter();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("MobinilVOMSUrlConnection[constructor]", " Exception=" + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("MobinilVOMSUrlConnection[constructor] ", "Exiting");
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
            _log.error("setPrintWriter", "Exception=" + e.getMessage());
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
            _log.error("setBufferedReader", "Exception=" + e.getMessage());
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
            _log.error("setPrintWriter", "Exception=" + e.getMessage());
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
    private void setRequestHeader(String p_keepAlive, String p_userAgent, String p_contentType, String p_authorization) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setRequestHeader", "Entered p_keepAlive=" + p_keepAlive + " ,p_userAgent=" + p_userAgent + ", p_contentType=" + p_contentType + ", p_authorization=" + p_authorization);
        try {
            // _urlConnection.setRequestProperty("User-Agent","etopup/2.4.01/1.0");
            _urlConnection.setRequestProperty("User-Agent", p_userAgent);
            _urlConnection.setRequestProperty("Content-Type", p_contentType);
            _urlConnection.setRequestProperty("Authorization", p_authorization);

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