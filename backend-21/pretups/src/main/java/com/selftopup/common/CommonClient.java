package com.selftopup.common;

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
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.inter.module.InterfaceModuleI;

public class CommonClient {
    // Common Client information
    public static String COMM_TYPE_SINGLEJVM = "SINGLE_JVM";
    public static String COMM_TYPE_SOCKET = "SOCKET";
    public static String COMM_TYPE_URL = "URL";
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

        } else if (commType.equals(COMM_TYPE_URL)) {

        }
        return responseStr;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    /**
     * Get Handler Object
     * 
     * @param handlerClassName
     * @return
     */
    public InterfaceModuleI getHandlerObj(String handlerClassName) {
        if (_log.isDebugEnabled())
            _log.debug("getHandlerObj", _transactionID, "Entered handlerClassName:" + handlerClassName);
        InterfaceModuleI handlerObj = null;
        try {
            handlerObj = (InterfaceModuleI) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            _log.errorTrace("getHandlerObj Exception print stack trace: ", e);
            _log.error("getHandlerObj", _transactionID, "Exception " + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getHandlerObj", _transactionID, "Exiting");
        }
        return handlerObj;
    }// end of getHandlerObj
}
