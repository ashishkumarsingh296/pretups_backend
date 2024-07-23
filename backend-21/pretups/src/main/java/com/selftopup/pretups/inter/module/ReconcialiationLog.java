/*
 * Created on Mar 16, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.selftopup.pretups.inter.module;

import java.util.HashMap;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

/**
 * (#)ReconcialiationLog.java
 * Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Dhiraj Tiwari Mar 16, 2007 Initial Creation
 * ----------------------------------------------------------------------------
 * --------------------
 * 
 */
public class ReconcialiationLog {

    public static HashMap _logObjectMap = new HashMap();// Contains logger
                                                        // objects corresponding
                                                        // to interfaces.
    private static Log _log = LogFactory.getFactory().getInstance(ReconcialiationLog.class.getName());

    /**
     * This method will first check if reconciliation logger object required for
     * interface is present in the static map or not. If it is present in map
     * return reconcilation log object from map.
     * else get reconcilation log object add it into map and then return it
     * 
     * @param int p_interfaceID
     * @return Log
     */
    public static Log getLogObject(String p_interfaceID) {
        Log reconLog = null;
        try {
            if (_logObjectMap.containsKey(p_interfaceID))
                reconLog = (Log) _logObjectMap.get(p_interfaceID);
            else {
                reconLog = LogFactory.getLog(p_interfaceID);
                _logObjectMap.put(p_interfaceID, reconLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getLogObject", " Not able to get ReconcialiationLog obiect, getting Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconcialiationLog[getLogObject]", "", "", "", "Not able to get ReconcialiationLog obiect, getting Exception :" + e.getMessage());
        }
        return reconLog;
    }

    /**
     * Method to prepare and return Reconciliation log format.
     * 
     * @param p_map
     * @return String
     */
    public static String getReconciliationLogFormat(HashMap p_map) {
        if (_log.isDebugEnabled())
            _log.debug("getReconciliationLogFormat", "Entered p_map=" + p_map);
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("," + (String) p_map.get("NETWORK_CODE"));
        strBuff.append("," + (String) p_map.get("TRANSACTION_ID"));// TRANSACTION_ID
        strBuff.append("," + (String) p_map.get("IN_RECON_ID"));
        strBuff.append("," + (String) p_map.get("TRANSACTION_TYPE"));
        strBuff.append("," + (String) p_map.get("MAPPED_SYS_STATUS"));// PRETUPS_TXN_STATUS
        strBuff.append("," + (String) p_map.get("MAPPED_CANCEL_STATUS"));// CANCEL_COMM_STATUS
        strBuff.append("," + (String) p_map.get("MSISDN"));
        String transferAmount = (String) p_map.get("INTERFACE_AMOUNT");
        if (InterfaceUtil.isNullString(transferAmount))
            transferAmount = "0";
        strBuff.append("," + transferAmount);
        String validityDays = (String) p_map.get("VALIDITY_DAYS");
        if (InterfaceUtil.isNullString(validityDays))
            validityDays = "";
        strBuff.append("," + validityDays);
        String graceDays = (String) p_map.get("GRACE_DAYS");
        if (InterfaceUtil.isNullString(graceDays))
            graceDays = "";
        strBuff.append("," + graceDays);
        strBuff.append("," + (String) p_map.get("USER_TYPE"));
        strBuff.append("," + (String) p_map.get("REQ_SERVICE"));
        String cardGroup = (String) p_map.get("CARD_GROUP");
        if (InterfaceUtil.isNullString(cardGroup))
            cardGroup = "";
        strBuff.append("," + cardGroup);
        String serviceClass = (String) p_map.get("SERVICE_CLASS");
        if (InterfaceUtil.isNullString(serviceClass))
            serviceClass = "";
        strBuff.append("," + serviceClass);
        strBuff.append("," + (String) p_map.get("MODULE"));
        strBuff.append("," + (String) p_map.get("AMBGUOUS_TIME"));
        strBuff.append("," + (String) p_map.get("SOURCE_TYPE"));
        String remark1 = (String) p_map.get("REMARK1");
        if (InterfaceUtil.isNullString(remark1))
            remark1 = "";
        strBuff.append("," + remark1);
        String remark2 = (String) p_map.get("REMARK2");
        if (InterfaceUtil.isNullString(remark2))
            remark2 = "";
        strBuff.append("," + remark2);

        if (_log.isDebugEnabled())
            _log.debug("getReconciliationLogFormat", "Exited");
        return strBuff.toString();
    }
}
