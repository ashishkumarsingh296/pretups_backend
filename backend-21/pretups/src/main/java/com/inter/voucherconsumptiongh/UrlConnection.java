/*
 * Created on June 18, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.voucherconsumptiongh;

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
 * @author abhay.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class UrlConnection extends BTSLConnection {
    private HttpURLConnection _urlConnection = null;

    public UrlConnection(String p_url, int p_connectTimeout, int p_readTimeout, String p_keepAlive, long p_contentLength, String p_soapAction) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("UrlConnection", "Entered p_url: " + p_url + "p_connecttimeout=" + p_connectTimeout + " p_readtimeout=" + p_readTimeout + " p_keepAlive=" + p_keepAlive + " p_soapAction=" + p_soapAction);
        try {
            URL url = new URL(p_url);
            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.setConnectTimeout(p_connectTimeout);
            _urlConnection.setReadTimeout(p_readTimeout);
            _urlConnection.setDoOutput(true);
            _urlConnection.setDoInput(true);
            _urlConnection.setRequestMethod("POST");
            setRequestHeader(p_contentLength, p_soapAction, p_keepAlive);
            setPrintWriter();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("ComverseUrlConnection", "Exception " + e.getMessage());
            if (_log.isDebugEnabled())
                _log.debug("ComverseUrlConnection", "Exception " + e.getMessage());
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("ComverseUrlConnection", "Exiting");
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
            _log.error("close", "Exception " + e.getMessage());
        }
        try {
            if (_in != null)
                _in.close();
        } catch (Exception e) {
            _log.error("close", "Exception " + e.getMessage());
        }
        try {
            if (_urlConnection != null)
                _urlConnection.disconnect();
        } catch (Exception e) {
            _log.error("close", "Exception " + e.getMessage());
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
    private void setRequestHeader(long p_contenetLength, String p_soapAction, String p_keepAlive) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setRequestHeader", "Entered... p_contenetLength:" + p_contenetLength + ", p_soapAction:" + p_soapAction + ", p_keepAlive:" + p_keepAlive);
        try {
            _urlConnection.setRequestProperty("Cache-Control", "no-cache");
            // _urlConnection.setRequestProperty("Expect","100-continue");
            _urlConnection.setRequestProperty("SOAPAction", p_soapAction);
            _urlConnection.setRequestProperty("Content-Length", String.valueOf(p_contenetLength));
            _urlConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            _urlConnection.setUseCaches(false);
            if ("Y".equalsIgnoreCase(p_keepAlive))
                _urlConnection.setRequestProperty("Connection", "Keep-Alive");
            else
                _urlConnection.setRequestProperty("Connection", "Close");
        } catch (Exception e) {
            _log.error("setRequestHeader", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setRequestHeader", "Exited");
        }

    }
}
