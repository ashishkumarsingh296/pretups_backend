package com.btsl.common;

/**
 * @(#)CommonClient.java
 *                       Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Abhijit Chauhan June 18,2005 Initial Creation
 *                       ------------------------------------------------------
 *                       ------------------------------------------
 */
import java.util.Map;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceModuleI;

public class CommonClient {
    // Common Client information
    public static final String COMM_TYPE_SINGLEJVM = "SINGLE_JVM";
    public static final String COMM_TYPE_SOCKET = "SOCKET";
    public static final String COMM_TYPE_URL = "URL";
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _transactionID;

    public CommonClient() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String process(String message, String transactionID, String commType, String ip, int port, String className) {
        String responseStr = null;
        _transactionID = transactionID;
        if (commType.equals(COMM_TYPE_SINGLEJVM)) {
            InterfaceModuleI interfaceModule = getHandlerObj(className);
            responseStr = interfaceModule.process(message);
        } else if (commType.equals(COMM_TYPE_SOCKET)) {
            // Do nothing
        } else if (commType.equals(COMM_TYPE_URL)) {
            // Do nothing
        }
        return responseStr;
    }

    public Map process1(String message, String transactionID, String commType, String ip, int port, String className) {
        Map responseStr = null;
        _transactionID = transactionID;
        if (commType.equals(COMM_TYPE_SINGLEJVM)) {
            InterfaceModuleI interfaceModule = getHandlerObj(className);
            responseStr = interfaceModule.process1(message);
        } else if (commType.equals(COMM_TYPE_SOCKET)) {
            // Do nothing
        } else if (commType.equals(COMM_TYPE_URL)) {
            // Do nothing
        }
        return responseStr;
    }

    /**
     * Get Handler Object
     * 
     * @param handlerClassName
     * @return
     */
    public InterfaceModuleI getHandlerObj(String handlerClassName) {
        if (_log.isDebugEnabled()) {
            _log.debug("getHandlerObj", _transactionID, "Entered handlerClassName:" + handlerClassName);
        }
        final String METHOD_NAME = "getHandlerObj";
        InterfaceModuleI handlerObj = null;
        try {
            handlerObj = (InterfaceModuleI) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getHandlerObj", _transactionID, "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getHandlerObj", _transactionID, "Exiting");
            }
        }
        return handlerObj;
    }// end of getHandlerObj
}
