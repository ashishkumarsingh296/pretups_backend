package com.btsl.util;

/*
 * @(#)BTSLURLConnector.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 05/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class BTSLURLConnector {

    private static Log _log = LogFactory.getLog(BTSLURLConnector.class.getName());
    public static final String TIMEOUT = "TIMEOUT";
    public static final String FAILED = "FAILED";
    private static final String ENTRY_KEY = "Entered: p_requestVO=";
    
    /**
	 * to ensure no class instantiation 
	 */
    private BTSLURLConnector() {
        
    }

    /**
     * Get Response
     * 
     * @param p_url
     *            ,fully qualified URL
     * @param p_timeout
     *            ,in seconds
     * @return
     */
    public static String getResponse(String p_url, int p_timeout) throws BTSLBaseException {
    	final String METHOD_NAME = "getResponse";
        StringBuilder loggerValue= new StringBuilder();
    	if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(ENTRY_KEY);
        	loggerValue.append("p_url:");
        	loggerValue.append(p_url);
        	loggerValue.append(", p_timeout:");
        	loggerValue.append(p_timeout);        	
        	_log.debug(METHOD_NAME, loggerValue);        	
        }        
        URL url = null;
        URLConnection urlConnection = null;
        BufferedReader in = null;
        StringBuffer strBuff = null;
        try {
            url = new URL(p_url);
            urlConnection = url.openConnection();
            String line = null;
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            final long startTimeinMills = System.currentTimeMillis();
            strBuff = new StringBuffer();
            while (true) {
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Inside While Loop Entered");
                	_log.debug(METHOD_NAME, loggerValue);
                }
                if (System.currentTimeMillis() - startTimeinMills > (p_timeout * 1000)) {
                    throw new BTSLBaseException(TIMEOUT);
                }
                line = in.readLine();
                if (line != null) {
                    strBuff.append(line);
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("line=");
                    	loggerValue.append(line);
                    	_log.debug(METHOD_NAME, loggerValue);
                    }
                    break;
                } else {
                    throw new BTSLBaseException(FAILED);
                }
            }// end of while
             // MessageSentLog.logMessage(strBuff.toString());
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append("Exiting While Loop");
            	_log.debug(METHOD_NAME, loggerValue);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exeption e:");
        	loggerValue.append(e.getMessage());
        	_log.debug(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            // MessageSentLog.logMessage(msisdn+"   "+msgStr+"  "+ce.getMessage());
            throw new BTSLBaseException(FAILED);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            urlConnection = null;
            url = null;
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append("Exiting response str:");
            	loggerValue.append(strBuff.toString());
            	_log.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return strBuff.toString();
    }
}
