package com.btsl.loadcontroller;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;

public class LoadControllerUtil {

    private static Log _log = LogFactory.getLog(LoadControllerUtil.class.getName());

    /**
	 * ensures no instantiation
	 */
    private LoadControllerUtil(){
    	
    }
    
    
    // Dividing the Last Time and current time and comparing them
    public static boolean checkSameSecondRequest(long p_previousTime, long p_currentTime) {
        if (_log.isDebugEnabled()) {
            _log.debug("checkSameSecondRequest", "Entered with p_previousTime=" + p_previousTime + " p_currentTime=" + p_currentTime);
        }
        boolean flag = false;
        if (Math.floor(p_previousTime / 1000) == Math.floor(p_currentTime / 1000)) {
            flag = true;
        } else {
            flag = false;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("checkSameSecondRequest", "Exiting with status (True = Same Second, False=Different Second)=" + flag);
        }
        return flag;
    }

    /**
     * Checks the response status of the transaction at various stages
     * 
     * @param p_transactionStatus
     * @return
     */
    public static boolean isResponseSuccess(String p_transactionStatus) {
        if (_log.isDebugEnabled()) {
            _log.debug("isResponseSuccess", "Entered with p_transactionStatus=" + p_transactionStatus);
        }

        boolean flag = false;
        if (p_transactionStatus.equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
            flag = true;
        } else {
            flag = false;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("isResponseSuccess", "Exiting with p_transactionStatus=" + p_transactionStatus + " flag=" + flag);
        }

        return flag;
    }

    public static InstanceLoadController getInstanceLoadObject(String p_instanceID) {
        if (_log.isDebugEnabled()) {
            _log.debug("getInstanceLoadObject", "Entered p_instanceID:" + p_instanceID);
        }
        return (InstanceLoadController) LoadControllerCache.getInstanceLoadObjectMap().get(p_instanceID);
    }

    public static NetworkLoadController getNetworkLoadObject(String p_instanceID, String p_networkID) {
        if (_log.isDebugEnabled()) {
            _log.debug("getNetworkLoadObject", "Entered p_instanceID:" + p_instanceID + " p_networkID=" + p_networkID);
        }
        return (NetworkLoadController) LoadControllerCache.getNetworkLoadObjectMap().get(p_instanceID + "_" + p_networkID);
    }

    public static InterfaceLoadController getInterfaceLoadObject(String p_instanceID, String p_networkID, String p_interfaceID) {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_instanceID:");
        	msg.append(p_instanceID);
        	msg.append(" p_networkID=");
        	msg.append(p_networkID);
        	msg.append(" p_interfaceID=");
        	msg.append(p_interfaceID);
        	
        	String message=msg.toString();
            _log.debug("getInterfaceLoadObject", message);
        }
        return (InterfaceLoadController) LoadControllerCache.getInterfaceLoadObjectMap().get(p_instanceID + "_" + p_networkID + "_" + p_interfaceID);
    }

    public static TransactionLoadController getTransactionLoadObject(String p_instanceID, String p_networkID, String p_interfaceID, String p_serviceType) {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_instanceID:");
        	msg.append(p_instanceID);
        	msg.append(" p_networkID=");
        	msg.append(p_networkID);
        	msg.append(" p_interfaceID=");
        	msg.append(p_interfaceID);
        	msg.append(" p_serviceType=");
        	msg.append(p_serviceType);
        	
        	String message=msg.toString();
            _log.debug("getTransactionLoadObject", message);
        }
        return (TransactionLoadController) LoadControllerCache.getTransactionLoadObjectMap().get(p_instanceID + "_" + p_networkID + "_" + p_interfaceID + "_" + p_serviceType);
    }

    public static NetworkLoadController getNetworkLoadObjectByKey(String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getNetworkLoadObjectByKey", "Entered p_key:" + p_key);
        }
        return (NetworkLoadController) LoadControllerCache.getNetworkLoadObjectMap().get(p_key);
    }

    public static InterfaceLoadController getInterfaceLoadObjectByKey(String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceLoadObjectByKey", "Entered p_key:" + p_key);
        }
        return (InterfaceLoadController) LoadControllerCache.getInterfaceLoadObjectMap().get(p_key);
    }

    public static TransactionLoadController getTransactionLoadObjectByKey(String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getTransactionLoadObjectByKey", "Entered p_key:" + p_key);
        }
        return (TransactionLoadController) LoadControllerCache.getTransactionLoadObjectMap().get(p_key);
    }

}