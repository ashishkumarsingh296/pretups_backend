package com.inter.urlcon;

/**
 * @(#)FermaUrlConnection.java
 *                             Copyright(c) 2005, Bharti Telesoft Int. Public
 *                             Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Abhijit Chauhan Oct 10,2005 Initial Creation
 *                             Manoj kumar 20 December Modify
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.inter.connection.BTSLConnection;
import com.inter.ferma.FermaRequestResponse;

public class FermaUrlConnection extends BTSLConnection {
    private HttpURLConnection _urlConnection = null;
    private String _userName = null;
    private String _password = null;
    private String _protocolVersion = null;
    private String _url = null;
    private String _status = null;
    private static FermaRequestResponse _formatter = new FermaRequestResponse();
    private static int _transactionID = 0;

    public FermaUrlConnection(String p_url, int p_connecttimeout, int p_readtimeout, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("FermaUrlConnection", "Entered p_url: " + p_url + " p_connecttimeout: " + p_connecttimeout + " p_readtimeout: " + p_readtimeout + " p_keepAlive=" + p_keepAlive);
        try {
            URL url = new URL(p_url);
            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.setConnectTimeout(p_connecttimeout);
            _urlConnection.setReadTimeout(p_readtimeout);
            _urlConnection.setDoOutput(true);
            _urlConnection.setDoInput(true);
            _urlConnection.setRequestMethod("POST");
            setRequestHeader(p_keepAlive);

        } catch (Exception e) {
            _log.error("FermaUrlConnection", "Exception e:" + e.getMessage());
            // e.printStackTrace(); //commented because done where this method
            // has been used
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("FermaUrlConnection", "Exiting ");
        }
    }

    /**
     * Set the print writer
     */
    public void setPrintWriter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setPrintWriter", "Entered");
        try {
            _out = new PrintWriter(_urlConnection.getOutputStream());
        } catch (Exception e) {
            // e.printStackTrace(); //commented because done where this method
            // has been used
            _log.error("setPrintWriter", "Exception " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setPrintWriter", "Exiting ");
        }
    }

    /**
     * Set the buffered input stream
     */
    public void setBufferedReader() {
        if (_log.isDebugEnabled())
            _log.debug("setBufferedReader", "Entered");
        try {
            _in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("setBufferedReader", "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setBufferedReader", "Exiting ");
        }
    }

    /**
     * @int p_timeout
     */
    public void setTimeout(int p_timeout) {
        if (_log.isDebugEnabled())
            _log.debug("setTimeout", "Entered");
        try {
            _urlConnection.setConnectTimeout(p_timeout);
        } catch (Exception e) {
            e.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("setTimeout", "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setTimeout", "Exiting ");
        }
    }

    /**
     * This method is used to close the connection
     */
    public void close() {
        if (_log.isDebugEnabled())
            _log.debug("close", "Entered");
        // logout request for clearing interface id on FERMA IN
        try {
            if (_out != null)
                _out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (_in != null)
                _in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (_urlConnection != null)
                _urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("close", "Exited");
    }

    /**
     * @return String
     * @throws IOException
     */
    public String getResponseCode() throws IOException {
        if (_log.isDebugEnabled())
            _log.debug("getResponseCode", "Entered");
        int httpStatus = _urlConnection.getResponseCode();
        String str = Integer.toString(httpStatus);
        if (_log.isDebugEnabled())
            _log.debug("getResponseCode", "Exited HTTP STATUS Code" + str);
        return str;
    }

    /**
     * This method is used to set the header informations
     * 1.keep-Alive
     * 
     * @param p_keepAlive
     */
    private void setRequestHeader(String p_keepAlive) throws Exception {
        // if(_log.isDebugEnabled())
        // _log.debug("setRequestHeader","Entered p_keepAlive::"+p_keepAlive);
        try {
            if ("Y".equals(p_keepAlive))
                _urlConnection.setRequestProperty("Connection", "keep-alive");
            else
                _urlConnection.setRequestProperty("Connection", "close");
        } catch (Exception e) {
            _log.error("setRequestHeader", "Exception e=" + e.getMessage());
            throw e;
        }
        /*
         * finally
         * {
         * if(_log.isDebugEnabled()) _log.debug("setRequestHeader","Exited");
         * }
         */
    }
}
