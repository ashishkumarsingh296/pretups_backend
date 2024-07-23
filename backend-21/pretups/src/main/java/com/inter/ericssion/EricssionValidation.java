package com.inter.ericssion;

/**
 * @(#)EricssionValidation.java
 *                              Copyright(c) 2005, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Abhijit Chauhan June 22,2005 Initial Creation
 *                              Ashish Kumar July 12,2006 Modification
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 */
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class EricssionValidation {
    private static Log _log = LogFactory.getLog(EricssionValidation.class.getName());
    private static int _counter = 0;

    public static boolean validation(HashMap p_map) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validation", "Entered p_map = " + p_map);
        int transIDLength = 6;
        int transdatetimeLength = 15;
        // int statusLength = 3; //Delete after confirming the validation of
        // status length
        // int serviceremovalLength = 8;//This should be flag basis,if INFile
        // defines SERVICE_REMOVAL=N no need to apply and if SERVICE_REMOVAL=Y
        // ,this will apply.
        int destMinLength = 1;
        int destMaxLength = 11;
        int originMinLength = 1;
        // int originMaxLength = 11;
        int originMaxLength = 20;
        int amtLength = 12;
        int failedcountLength = 3;
        int firstcallLength = 1;
        // int stringLen = 0;
        boolean validation = true;
        // Validation for TransId
        String TransId = (String) p_map.get("TransId");
        String interfaceID = (String) p_map.get("INTERFACE_ID");
        String networkID = (String) p_map.get("NETWORK_CODE");
        try {
            if (TransId != null && BTSLUtil.NullToString(TransId).length() != transIDLength) {
                _log.error("validation", "TransId:" + TransId);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of Trans Id recieved from response is not equal to " + transIDLength);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            }// End of TransId Validation.
             // Validation for TransDateTime
            String TransDateTime = (String) p_map.get("TransDateTime");
            if (TransDateTime != null && BTSLUtil.NullToString(TransDateTime).length() != transdatetimeLength) {
                _log.error("validation", "TransDateTime:" + TransDateTime);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of Trans date time recieved from response is not equal to " + transdatetimeLength);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            }
            /*
             * String Status=(String)p_map.get("Status");
             * //Confirm for this method is it still required?,since before this
             * we already checked the status in Handler class????
             * if (Status!=null)
             * {
             * if(BTSLUtil.NullToString(Status).length() != statusLength)
             * {
             * if(_log.isDebugEnabled())_log.debug("validation","Status:"+Status)
             * ;
             * 
             * return false;
             * }
             * try
             * {
             * int st = Integer.parseInt(Status);
             * }
             * catch (NumberFormatException n)
             * {
             * if(_log.isDebugEnabled())_log.debug("validation","Status:"+Status)
             * ;
             * return false;
             * }
             * }
             */// Validation for MSISDN

            String MSISDN = (String) p_map.get("MSISDN");
            if (BTSLUtil.isNullString(MSISDN)) {
                _log.error("validation", "MSISDN:" + MSISDN);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "MSISDN Recieved from the response is NULL");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            }
            /*
             * //Confirm for this method??????
             * String ServiceRemoval=(String)p_map.get("ServiceRemoval");
             * if (ServiceRemoval!=null)
             * {
             * if(BTSLUtil.NullToString(ServiceRemoval).length() !=
             * serviceremovalLength)
             * {
             * if(_log.isDebugEnabled())_log.debug("validation","ServiceRemoval:"
             * +ServiceRemoval);
             * return false;
             * }
             * else
             * { try
             * {
             * long st = Long.parseLong(ServiceRemoval);
             * }
             * catch (NumberFormatException n)
             * {
             * if(_log.isDebugEnabled())_log.debug("validation","ServiceRemoval:"
             * +ServiceRemoval);
             * flag = false;
             * }
             * }
             * }
             */// Valilidation for Dest
            String Dest = (String) p_map.get("Dest");
            if (Dest != null && (Dest.length() < destMinLength) && (Dest.length() > destMaxLength)) {
                _log.error("validation", "Dest:" + Dest);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of Dest is not between " + destMinLength + "And " + destMaxLength);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            }
            // Validation for Origin
            String Origin = (String) p_map.get("Origin");
            if (Origin != null && (Origin.length() < originMinLength) && (Origin.length() > originMaxLength)) {
                _log.error("validation", "Origin:" + Origin);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of Origin is not between " + originMinLength + "And " + originMaxLength);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            }
            // Validation for TransAmt
            String TransAmt = (String) p_map.get("TransAmt");
            if (TransAmt != null) {
                if (TransAmt.length() != amtLength) {
                    _log.error("validation", "TransAmt:" + TransAmt);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of TransAmt from response is not equal to " + amtLength);
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
                String sub = TransAmt;
                // If trans amount is negative remove negative sighn from the
                // transAmt and also check whether it numeric or not.
                if (TransAmt.startsWith("-")) // to be confirmed with supratim
                    sub = TransAmt.substring(1, TransAmt.length() - 1);
                try {
                    Long.parseLong(sub);
                } catch (NumberFormatException n) {
                    _log.error("validation", "TransAmt:" + TransAmt);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of TransAmt from response is not Numeric " + amtLength);
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
            }
            // Validation of FailCount
            String FailedCount = (String) p_map.get("FailedCount");
            if (FailedCount != null) {
                try {
                    Long.parseLong(FailedCount);
                } catch (NumberFormatException n) {
                    _log.error("validation", "FailedCount:" + FailedCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "FailedCount fetched from the response is not numeric");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
                if (FailedCount.length() != failedcountLength) {
                    _log.error("validation", "FailedCount:" + FailedCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of FailedCount from response is not Numeric " + failedcountLength);
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
            }
            // Validation of FailCount
            String RechargeValue = (String) p_map.get("RechargeValue");
            if (RechargeValue != null) {
                if (RechargeValue.length() != amtLength) {
                    _log.error("validation", "RechargeValue:" + RechargeValue);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of RechargeValue from response is not Numeric " + amtLength);
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
                try {
                    Long.parseLong(RechargeValue);
                } catch (NumberFormatException n) {
                    _log.error("validation", "RechargeValue:" + RechargeValue);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, " RechargeValue [" + RechargeValue + " ] is not Numeric");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
            }

            // Validation for PayAmt
            String PayAmt = (String) p_map.get("PayAmt");
            if (PayAmt != null) {
                if (PayAmt.length() != amtLength) {
                    if (_log.isDebugEnabled())
                        _log.debug("validation", "PayAmt:" + PayAmt);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, "Length of  PayAmt[ " + PayAmt + " ] is not equal to " + amtLength);
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
                try {
                    Long.parseLong(PayAmt);
                } catch (NumberFormatException n) {
                    _log.error("validation", "PayAmt:" + PayAmt);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, " value PayAmt [ " + PayAmt + " ] is not numeric");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
            }
            // Validation for FirstCall
            String firstCall = (String) p_map.get("FirstCall");
            String firsFlag = FileCache.getValue(interfaceID, "FIRST_FLAG");
            if (!"N".equals(firsFlag)) {
                // if(firstCall!=null&&(BTSLUtil.NullToString(firstCall).length()
                // != firstcallLength|| (firstCall.equalsIgnoreCase("N") &&
                // firstCall.equalsIgnoreCase("Y"))))
                if (firstCall != null && (InterfaceUtil.NullToString(firstCall).length() != firstcallLength)) {
                    if (_log.isDebugEnabled())
                        _log.debug("validation", "FirstCall:" + firstCall);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, " FirstCall length from the response is equal to [ " + firstcallLength + " ]");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
            }
            // Validation of BarInd
            String barInd = (String) p_map.get("BarInd");
            // if(BarInd!=null&&(BTSLUtil.NullToString(BarInd).length() !=
            // firstcallLength|| (BarInd.equalsIgnoreCase("N") &&
            // BarInd.equalsIgnoreCase("Y") && BarInd.equalsIgnoreCase("M"))))
            if (barInd != null && (BTSLUtil.NullToString(barInd).length() != firstcallLength)) {
                if (_log.isDebugEnabled())
                    _log.debug("validation", "BarInd:" + barInd);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, " BarInd length from the response is equal to [ " + firstcallLength + " ]");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            }
        } catch (BTSLBaseException be) {
            validation = false;
        } catch (Exception e) {
            validation = false;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", " INTERFACE ID = " + interfaceID, "", "networkID = " + networkID, " Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validation", "Exiting validation is " + validation);
        }
        return validation;
    }// End of validation Method

    /**
     * This method is used to get the transaction id.
     * Counter is incremented to 1 each time and reinitializes the counter when
     * its max value is reached.
     * 
     * @return String
     * @throws BTSLBaseException
     */
    public static synchronized String getIncrCounter() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getIncrCounter", "Entered");
        try {
            if (_counter == 9999)
                _counter = 0;
            _counter++;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getIncrCounter", e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionValidation[validation]", "", "", "", " Error occurs while getting transaction id Exception is " + e.getMessage());
            throw new BTSLBaseException(e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getIncrCounter", "Exiting counter = " + _counter);
        }
        return String.valueOf(_counter);
    }
}// End of PAMIValidation class
