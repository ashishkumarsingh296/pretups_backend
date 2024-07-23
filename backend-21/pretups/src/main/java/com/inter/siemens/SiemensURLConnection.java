package com.inter.siemens;

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
 * @(#)SiemensURLConnection.java
 *                               Copyright(c) 2006, Bharti Telesoft Int. Public
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
 *                               Ashish Kumar Jun 15,2006 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 *                               This class is used to establish HttpUrl
 *                               connection for the Siemense interface.
 */
public class SiemensURLConnection extends BTSLConnection {
    private HttpURLConnection _urlConnection = null;

    public SiemensURLConnection() {
        super();
    }

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
    public SiemensURLConnection(String p_url, int p_connectTimeout, int p_readTimeout, String p_keepAlive, String p_contentType) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("SiemensURLConnection", "Entered p_url = " + p_url + " p_connecttimeout = " + p_connectTimeout + " p_readtimeout = " + p_readTimeout + " p_keepAlive = " + p_keepAlive + " p_contentType = " + p_contentType);
        try {
            URL url = new URL(p_url);
            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.setConnectTimeout(p_connectTimeout);
            _urlConnection.setReadTimeout(p_readTimeout);
            _urlConnection.setDoOutput(true);
            _urlConnection.setDoInput(true);
            // _urlConnection.setRequestMethod("POST");
            _urlConnection.setRequestMethod("GET");
            setContentType(p_contentType);
            setKeepAlive(p_keepAlive);
            // setPrintWriter();
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("SiemensURLConnection", "Exception " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("SiemensURLConnection", "Exiting");
        }// end of finally
    }// end of constructor SiemensURLConnection

    /**
     * This method sets the keppAlive true if argument p_keepAlive is 'Y' else
     * set its value false.
     * 
     * @param String
     *            p_keepAlive
     * @throws Exception
     */
    private void setKeepAlive(String p_keepAlive) throws Exception {
        try {
            if ("Y".equalsIgnoreCase(p_keepAlive))
                _urlConnection.setRequestProperty("Connection", "keep-alive");
            else
                _urlConnection.setRequestProperty("Connection", "close");
        }// end of try block
        catch (Exception e) {
            _log.error("setKeepAlive", "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
    }// end of setKeepAlive

    /**
     * This method is used to set content type provided by INFile.
     * If content type is provided then it sets the content type else not.
     * 
     * @param p_contentType
     * @throws Exception
     */
    private void setContentType(String p_contentType) throws Exception {
        try {
            if (p_contentType.length() > 0)
                ;
            _urlConnection.setRequestProperty("Content-Type", p_contentType);
        } catch (Exception e) {
            _log.error("setContentType", "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
    }// end of setContentType

    /**
     * This method is used to set PrintWriter Object.
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
        }// end of catch-Exception
    }// end of setPriWriter

    /**
     * This method is used to set InputStream object.
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
        }// end of catch-Exception
    }

    /**
     * This method is used to close the PrintWriter, URL connection and
     * InputStream.
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
    }// end of close

    /**
     * This method is used to get the HttpStatus Code of response
     * 
     * @return String
     * @throws IOException
     */
    public String getResponseCode() throws IOException {
        int httpStatus = _urlConnection.getResponseCode();
        String str = Integer.toString(httpStatus);
        return str;
    }// end of getResponseCode

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
        }// end of catch-Exception
    }// end of setTimeout
}
