package com.inter.huaweicitycell;

/**
 * @HuaweiINHandler.java
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Vipan Nov 16, 2010 Initial Creation
 *                       ------------------------------------------------------
 *                       -----------------------------------------
 *                       This class is the Handler class for the HUAWEI
 *                       interface
 */
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;

public class HuaweiINHandler implements InterfaceHandler {

    private Log _log = LogFactory.getLog(HuaweiINHandler.class.getName());
    private HashMap<String, String> _requestMap = null;// Contains the request
                                                       // parameter as key and
                                                       // value pair.
    private HashMap<String, String> _responseMap = null;// Contains the response
                                                        // of the request as key
                                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private HuaweiRequestFormatter _formatter = null;
    boolean logsEnable = false;
    OutputStream _out = null;
    InputStream _in = null;
    static int localIPCounter = 0, INPortCounter = 0;
    private static Vector<String> IPPortFreeVector = null;

    public HuaweiINHandler() {
        _formatter = new HuaweiRequestFormatter();
    }

    /**
     * This method would be used to validate the subscriber's account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");

            String hbLogs = FileCache.getValue(_interfaceID, "HUAWEIINLOGS");
            if (InterfaceUtil.isNullString(hbLogs)) {
                HuaweiProps.logMessage("[HuaweiINHandler] [Status] HUAWEIINLOGS is not defined in the INFile");
                hbLogs = "N";
            }
            if (hbLogs != null && hbLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            _msisdn = (String) _requestMap.get("MSISDN");
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if (_log.isDebugEnabled())
                _log.debug("validate", "Entered validateRequired : " + validateRequired + " for REQ_SERVICE_USER_TYPE" + _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));

            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }

            _inTXNID = InterfaceUtil.getINTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [MSISDN] =" + _msisdn + " [_inTXNID] =" + _inTXNID + ", For Interface Id" + _interfaceID);

            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");

            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("validate", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[validate]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [huaweiMultiplicationFactor] =" + huaweiMultiplicationFactor + ", For Interface Id" + _interfaceID);

            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
            // Set the interface parameters into requestMap
            // setInterfaceParameters(HuaweiI.ACTION_ACCOUNT_INFO);
            // sending the AccountInfo request to IN along with validate action
            // defined in HuaweiI interface

            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [HuaweiI.ACTION_ACCOUNT_INFO] =" + HuaweiI.ACTION_ACCOUNT_INFO + "[Status](Validation Before sending to sendRequestToIN), For Interface Id" + _interfaceID);
            String heartBeatPoolConnectionAllowedStr = FileCache.getValue(_interfaceID, "HEARTBEAT_POOL_CONNECTION_ALLOWED");
            if (InterfaceUtil.isNullString(heartBeatPoolConnectionAllowedStr)) {
                _log.error("HuaweiHeartBeat[Constructor]", "heartBeatPoolConnectionAllowedStr is not defined in the INFile");
                heartBeatPoolConnectionAllowedStr = "Y";
            }
            if ("Y".equalsIgnoreCase(heartBeatPoolConnectionAllowedStr))
                // sending the Re-charge request to IN along with re-charge
                // action defined in HuaweiI interface
                sendRequestToIN(HuaweiI.ACTION_ACCOUNT_INFO);
            else
                sendRequestToINSingleConnection(HuaweiI.ACTION_ACCOUNT_INFO);

            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [HuaweiI.ACTION_ACCOUNT_INFO] =" + HuaweiI.ACTION_ACCOUNT_INFO + "[Status](Validation After sending to sendRequestToIN), For Interface Id" + _interfaceID);

            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // get value of BALANCE from response map (BALANCE was set in
            // response map in sendRequestToIN method.)
            // String amountStr=(String)_responseMap.get("BALANCE");
            String amountStr = (String) _responseMap.get("accountBalance");

            try {
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(huaweiMultiplicationFactor));

                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [Amount] =" + amountStr + "[Status](Return Amount validation), For Interface Id" + _interfaceID);
                _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
            } catch (Exception e) {
                e.printStackTrace();
                HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in validation process), For Interface Id" + _interfaceID);
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[validate]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("validity"), "yyyyMMdd"));
            setLanguageFromMapping();
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in validation process), For Interface Id" + _interfaceID);
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[validate]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
        }
    }// end of validate

    /**
     * This method would be used to credit the subscriber's account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double huaweiMultFactorDouble = 0;
        String amountStr = null;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            String hbLogs = FileCache.getValue(_interfaceID, "HUAWEIINLOGS");
            if (InterfaceUtil.isNullString(hbLogs)) {
                HuaweiProps.logMessage("[HuaweiINHandler] [Status] HUAWEIINLOGS is not defined in the INFile");
                hbLogs = "N";
            }
            if (hbLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            _inTXNID = InterfaceUtil.getINTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _msisdn = (String) _requestMap.get("MSISDN");

            // Fetching the HUAWEI_MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.

            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");
            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("credit", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("HUAWEI_MULT_FACTOR", huaweiMultiplicationFactor.trim());
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            try {
                huaweiMultFactorDouble = Double.parseDouble(huaweiMultiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, huaweiMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "HuaweiINHandler[credit]", _inTXNID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", "REFERENCE ID = " + _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [HuaweiI.ACTION_RECHARGE_CREDIT] =" + HuaweiI.ACTION_RECHARGE_CREDIT + "[Status](Credit Before sending to sendRequestToIN), For Interface Id " + _interfaceID + " [inTXNID] " + _inTXNID + " [msisdn] " + _msisdn + " [huaweiMultiplicationFactor] " + _inTXNID + " [amountStr] " + _inTXNID);

            if (_log.isDebugEnabled())
                _log.debug("credit ", " For Interface Id " + _interfaceID + " [inTXNID] " + _inTXNID + " [msisdn] " + _msisdn + " [huaweiMultiplicationFactor] " + _inTXNID + " [amountStr] " + _inTXNID);

            String heartBeatPoolConnectionAllowedStr = FileCache.getValue(_interfaceID, "HEARTBEAT_POOL_CONNECTION_ALLOWED");
            if (InterfaceUtil.isNullString(heartBeatPoolConnectionAllowedStr)) {
                _log.error("HuaweiHeartBeat[Constructor]", "heartBeatPoolConnectionAllowedStr is not defined in the INFile");
                heartBeatPoolConnectionAllowedStr = "Y";
            }
            if ("Y".equalsIgnoreCase(heartBeatPoolConnectionAllowedStr))
                // sending the Re-charge request to IN along with re-charge
                // action defined in HuaweiI interface
                sendRequestToIN(HuaweiI.ACTION_RECHARGE_CREDIT);
            else
                sendRequestToINSingleConnection(HuaweiI.ACTION_RECHARGE_CREDIT);
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [HuaweiI.ACTION_RECHARGE_CREDIT] =" + HuaweiI.ACTION_RECHARGE_CREDIT + "[Status](Credit After sending to sendRequestToIN), For Interface Id" + _interfaceID);

            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("AFTERVALIDITY"), "yyyyMMdd"));
            try {
                String postBalanceStr = (String) _responseMap.get("accountBalance");
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [Amount] =" + postBalanceStr + "[Status](Return Amount validation), For Interface Id" + _interfaceID);
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, huaweiMultFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            HuaweiProps.logMessage("[HuaweiINHandler] [BTSLBaseException] =" + be.getMessage() + "[Exception](Exception in credit process), For Interface Id" + _interfaceID);

            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "CR");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in credit process), For Interface Id" + _interfaceID);
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in credit process), For Interface Id" + _interfaceID);
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }
    }// end of credit

    /**
     * This method would be used to adjust the credit of subscriber account at
     * the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust ", "Entered p_requestMap: " + p_requestMap);
        credit(p_requestMap);
    }

    /**
     * This method would be used to adjust the debit of subscriber account at
     * the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust ", "Entered p_requestMap: " + p_requestMap);

        double systemAmtDouble = 0;
        double interfaceAmtDouble = 0;
        String amountStr = null;
        _requestMap = p_requestMap;// assign map passed from InterfaceModule to
                                   // _requestMap(instance var)
        try {
            if ("0".equals((String) p_requestMap.get("INTERFACE_AMOUNT"))) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // interface
                                                                    // id form
                                                                    // request
                                                                    // map
            String hbLogs = FileCache.getValue(_interfaceID, "HUAWEIINLOGS");
            if (InterfaceUtil.isNullString(hbLogs)) {
                HuaweiProps.logMessage("[HuaweiINHandler] [Status] HUAWEIINLOGS is not defined in the INFile");
                hbLogs = "N";
            }
            if (hbLogs != null && hbLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            _inTXNID = InterfaceUtil.getINTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _msisdn = (String) _requestMap.get("MSISDN");// get MSISDN from
                                                         // request map

            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [MSISDN] =" + _msisdn + " [_inTXNID] =" + _inTXNID + ", For Interface Id" + _interfaceID);

            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");

            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("debitAdjust", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "debitAadjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [huaweiMultiplicationFactor] =" + huaweiMultiplicationFactor + ", For Interface Id" + _interfaceID);
            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
            // Set the interface parameters into requestMap
            // setInterfaceParameters(HuaweiI.ACTION_IMMEDIATE_DEBIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                double huaweiMultFactorDouble = Double.parseDouble(huaweiMultiplicationFactor);
                interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, huaweiMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [amountStr] =" + amountStr + ", For Interface Id" + _interfaceID);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("debitAadjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "HuaweiINHandler[debitAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Need to Change Here to handle Negative amount
            // String transferDebitAmount = "-"+amountStr;
            String transferDebitAmount = amountStr;
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [transferDebitAmount] =" + transferDebitAmount + ", For Interface Id" + _interfaceID);

            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "transfer_amount:" + transferDebitAmount + " huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", transferDebitAmount);

            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));

            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [validateRequired for Debit] =" + validateRequired + ", For Interface Id" + _interfaceID);

            if ("Y".equals(validateRequired)) {
                String prevBalanceAmountStr = (String) p_requestMap.get("INTERFACE_PREV_BALANCE");
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "prevBalanceAmountStr:" + prevBalanceAmountStr);
                if (InterfaceUtil.isNullString(prevBalanceAmountStr) || !InterfaceUtil.isNumeric(prevBalanceAmountStr)) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE present in the requestMap is either null or not numeric");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT present in the requestMap is either null or not numeric");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                double prevBalanceAmount = Double.parseDouble(prevBalanceAmountStr.trim());
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [prevBalanceAmount] =" + prevBalanceAmount + "[interfaceAmtDouble] =" + interfaceAmtDouble + ", For Interface Id" + _interfaceID);

                // Checking the sufficient previous balance, if previous balance
                // is less than that of the debit amount, throw the exception.
                if (prevBalanceAmount < interfaceAmtDouble) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
                }
            }
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [HuaweiI.ACTION_IMMEDIATE_DEBIT] =" + HuaweiI.ACTION_IMMEDIATE_DEBIT + "[Status](Debit Before sending to sendRequestToIN), For Interface Id" + _interfaceID);
            String heartBeatPoolConnectionAllowedStr = FileCache.getValue(_interfaceID, "HEARTBEAT_POOL_CONNECTION_ALLOWED");
            if (InterfaceUtil.isNullString(heartBeatPoolConnectionAllowedStr)) {
                _log.error("HuaweiHeartBeat[Constructor]", "heartBeatPoolConnectionAllowedStr is not defined in the INFile");
                heartBeatPoolConnectionAllowedStr = "Y";
            }
            if ("Y".equalsIgnoreCase(heartBeatPoolConnectionAllowedStr))
                // sending the Re-charge request to IN along with re-charge
                // action defined in HuaweiI interface
                sendRequestToIN(HuaweiI.ACTION_IMMEDIATE_DEBIT);
            else
                sendRequestToINSingleConnection(HuaweiI.ACTION_IMMEDIATE_DEBIT);

            // sendRequestToIN(HuaweiI.ACTION_IMMEDIATE_DEBIT);
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [HuaweiI.ACTION_IMMEDIATE_DEBIT] =" + HuaweiI.ACTION_IMMEDIATE_DEBIT + "[Status](Debit After sending to sendRequestToIN), For Interface Id" + _interfaceID);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            try {
                String postBalanceStr = (String) _responseMap.get("accountBalance");
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [Amount] =" + postBalanceStr + "[Status](Return Amount validation), For Interface Id" + _interfaceID);
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, Double.parseDouble(huaweiMultiplicationFactor));
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from response is not NUMERIC while parsing the balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // set POST_BALANCE_ENQ_SUCCESS as N in request map. why...????
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            HuaweiProps.logMessage("[HuaweiINHandler] [BTSLBaseException] =" + be.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "DR");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HuaweiProps.logMessage("[HuaweiINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while debitAdjust e:" + e.getMessage());
            throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }
    }// end of creditAdjust

    /**
     * This method would be used to adjust the validity of the subscriber
     * account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }// end of validityAdjust

    /**
     * This method would be used to send validate, credit, creditAdjust,
     * debitAdjust, and validityAdjust requests to IN depending on the action
     * value.
     * 
     * @param String
     *            p_map
     * @param int p_action
     * @throws BTSLBaseException
     *             , Exception
     */
    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " Entered p_action:" + p_action);
        // TransactionLog.log(_inTXNID,_inTXNID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"","action="+p_action+" _requestMap = "+_requestMap);
        String responseStr = null;
        byte[] inRequestByte = null;
        HuaweiSocketWrapper socketConnection = null;
        long startTime = 0;
        boolean isHua = false;
        int retryCountConInvalid = 0;// Represent the Number of retries in case
                                     // of exception to get SocketWrapper
        OutputStream out = null;
        // InputStream in = null;
        InputStream in = null;
        long endTime = 0;
        long sleepTimeConInvalid = 0;
        int retryCount = 0;
        String sessionID = null;
        Vector<Object> busyList = null;
        Vector<Object> freeList = null;
        // StringBuffer responseBuffer = null;
        boolean isConnectionFree = false;
        byte bytesReadbuf[] = null;
        try {
            // while sending or receiving request. handle event when this value
            // is null.
            String retryCountConInvalidStr = FileCache.getValue(_interfaceID, "RETRY_CON_INVAL");
            if (InterfaceUtil.isNullString(retryCountConInvalidStr) || !InterfaceUtil.isNumeric(retryCountConInvalidStr)) {
                _log.error("sendRequestToIN", "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            retryCountConInvalid = Integer.parseInt(retryCountConInvalidStr);
            // Get the sleep time between retries (from IN file) when
            // SocketConnection is null or exception occurs
            // while sending request.
            String sleepTimeConInvalidStr = FileCache.getValue(_interfaceID, "SLEEP_CON_INVAL");
            if (InterfaceUtil.isNullString(sleepTimeConInvalidStr) || !InterfaceUtil.isNumeric(sleepTimeConInvalidStr)) {
                _log.error("sendRequestToIN", "SLEEP_CON_INVAL is either not defined in the INFile or it is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "SLEEP_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            sleepTimeConInvalid = Long.parseLong(sleepTimeConInvalidStr);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " retryCountConInvalid = " + retryCountConInvalid + " sleepTimeConInvalid = " + sleepTimeConInvalid);

            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [retryCountConInvalidStr] =" + retryCountConInvalidStr + " [sleepTimeConInvalidStr] =" + sleepTimeConInvalidStr + ", For Interface Id" + _interfaceID);

            String txnID = new HuaweiSocketWrapper().getINHeaderTxnID();
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "txnID: " + txnID);
            _requestMap.put("IN_HEADER_TXN_ID", txnID);

            // Fetch the pool size from the INFile.
            String module = (String) _requestMap.get("MODULE");
            if (InterfaceErrorCodesI.MODULE_C2S.equalsIgnoreCase(module))
                isHua = true;

            try {
                TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Before getting socket connection:" + inRequestByte + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                socketConnection = HuaweiPoolManager.getClientObject(_interfaceID, isHua);
                if (_log.isDebugEnabled())
                    _log.debug("[HuaweiINHandler]", " socketConnection = " + socketConnection.getSoLinger() + " socketConnection = " + socketConnection.isBound() + " socketConnection = " + socketConnection.isClosed() + " socketConnection = " + socketConnection.isOutputShutdown());
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [socketConnection] =" + socketConnection.isConnected() + " [Status](Getting client object), For Interface Id" + _interfaceID);

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " socketConnection:" + socketConnection.isConnected());
                // Get the Transaction id to be sent to IN

                busyList = (Vector<Object>) HuaweiPoolManager._busyBucket.get(_interfaceID);// get
                                                                                            // busy
                                                                                            // and
                                                                                            // free
                                                                                            // pool
                                                                                            // from
                                                                                            // pool
                                                                                            // mgr.
                freeList = (Vector<Object>) HuaweiPoolManager._freeBucket.get(_interfaceID);
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [HuaweiPoolManager._busyBucket]=" + HuaweiPoolManager._busyBucket.toString() + "[HuaweiPoolManager._freeBucket]=" + HuaweiPoolManager._freeBucket.toString() + "[Status]{Getting free conncetion from pool } ,for Interface ID =" + _interfaceID);
                // get session id associated with socketConnection by calling
                // generateSessionID of SocketWrapper class.
                // put session id in request map. if it is null handle event and
                // throw exception.
                sessionID = socketConnection.generateSessionID();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " getting sessionID : " + sessionID);
                if (InterfaceUtil.isNullString(sessionID)) {
                    _log.error("sendRequestToIN", "Session id obtained from socket wrapper is NULL");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Session id obtained from socket wrapper is NULL");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("SESSIONID", sessionID);

                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [txnID]=" + txnID + " [SESSIONID]=" + sessionID + " [Status]{After getting the connection} ,for Interface ID =" + _interfaceID);
            } catch (BTSLBaseException be) {
                HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{ BTSLBaseException while creating the conection}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
                _log.error("sendRequestToIN", "Error while attempt to get SocketConnection object from PoolManager.");
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{ Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Exception occurs while getting new Client object");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // this is to count number of retries done.
            while (retryCount++ <= retryCountConInvalid) {
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Attemp Number:" + retryCount);
                try {
                    inRequestByte = _formatter.generateRequest(p_action, _requestMap);
                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] [Request]=" + InterfaceUtil.printByteData(inRequestByte) + " [Status]{After generating request } ,for Interface ID =" + _interfaceID + " _inTXNID = " + _inTXNID);
                } catch (BTSLBaseException be) {
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", _inTXNID, "Exception while getting the request " + be.getMessage());
                    HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{ BTSLBaseException while getting the request}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
                    throw be;
                } catch (Exception e) {
                    HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{ Exception while getting the request}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                try {

                    TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Before writing request in scoket :" + inRequestByte + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                    out = socketConnection.getPrintWriter();
                    TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "After writing request in scoket :" + inRequestByte + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", _inTXNID, "Sending request to IN _socketConnection: " + socketConnection + " _interfaceID: " + _interfaceID + " inRequestByte: " + inRequestByte);
                    // get start time of transaction and put it into request
                    // map.
                    startTime = System.currentTimeMillis();
                    _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                    // Send message to IN.
                    out.write(inRequestByte);
                    out.flush();
                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] [Request]=" + inRequestByte + " [Status]{After Submitting request to IN } ,for Interface ID =" + _interfaceID);

                    // break;
                } catch (Exception e) {
                    e.printStackTrace();
                    HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{Exception while creating the conection " + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                    // Check, retry count reaches to maximum attempts, throw
                    // exception.
                    if (retryCount > retryCountConInvalid) {
                        _log.error("sendRequestToIN", "Error while writing on output stream.");
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[sendRequestToIN]", _interfaceID, "", "", "Number of retry reached to MAX" + _interfaceID);
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                    try {
                        // get a new SocketWrapper object
                        // remove old SocketWrapper object from busy pool.
                        // add new SocketWrapper object in busy pool.
                        // get transaction id and session id
                        // generate request
                        // Destroy the previous socket connection before
                        // creating a new one.
                        if (socketConnection != null)
                            socketConnection.destroy();

                        if (logsEnable)
                            HuaweiProps.logMessage("[HuaweiINHandler], [Status]{Conecttion is null going to create the connection} ,for Interface ID =" + _interfaceID);

                        HuaweiSocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                        if (logsEnable)
                            HuaweiProps.logMessage("[HEARTBEAT] [SocketConnection]=" + socketConnection.isConnected() + " ,for Interface ID =" + _interfaceID);
                        sessionID = socketConnection.getSessionID();
                        if (InterfaceUtil.isNullString(sessionID))
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        _requestMap.put("SESSIONID", sessionID);
                        Thread.sleep(sleepTimeConInvalid);
                        continue;
                    } catch (Exception ex) {
                        HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{ Exception while creating the conection}" + ex.getMessage() + " ,for Interface ID =" + _interfaceID);
                        _log.error("sendRequestToIN", "Exception ex :" + ex.getMessage());
                        // throw ex;
                    }// continue;
                }
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "WAITING FOR IN RESPONSE _socketConnection:" + socketConnection + "::::::::::IN ID= " + _interfaceID);
                try {
                    // get BufferedReader from SocketWrapper
                    TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Before reading from scoket :" + inRequestByte + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                    in = socketConnection.getInputStream();
                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] [socketConnection]=" + socketConnection.isConnected() + " [Status]{Before reading the message from IN } ,for Interface ID =" + _interfaceID);

                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "reading message");
                    bytesReadbuf = new byte[inRequestByte.length];
                    in.read(bytesReadbuf, 0, bytesReadbuf.length);
                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] [responseBuffer]=" + InterfaceUtil.printByteData(bytesReadbuf) + " [Status]{After reading the message from IN } ,for Interface ID =" + _interfaceID + " _inTXNID " + _inTXNID);
                    TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "After reading from scoket :" + inRequestByte + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action + " _inTXNID " + _inTXNID);
                    // END TIME OF transaction.
                    endTime = System.currentTimeMillis();
                    String warnTimeStr = FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                    if (!InterfaceUtil.isNullString(warnTimeStr)) {
                        long warnTime = Long.parseLong(warnTimeStr);
                        if (endTime - startTime > warnTime) {
                            _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Huawei IN is taking more time than the warning threshold. Total Time taken is: " + (endTime - startTime));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                    _log.error("sendRequestToIN", " Error occoured while reading response message Exception e :" + e.getMessage() + " creating new connection and replacing it with the older one");
                    if (PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE.equals(_requestMap.get("REQ_SERVICE")) && PretupsI.INTERFACE_VALIDATE_ACTION.equals((String) _requestMap.get("INTERFACE_ACTION")))
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    // As per discussed the new connection would be added to the
                    // pool, and destroy the older,if error occurs during the
                    // response reading.
                    // 1. Destroy the older connection
                    // 2. Create new connection.
                    // 3. Remove the old connection from busyList.
                    // 4. Add the new one into the busyList.
                    if (socketConnection != null)
                        socketConnection.destroy();
                    try {
                        if (logsEnable)
                            HuaweiProps.logMessage("[HuaweiINHandler], [Status]{Conecttion is null going to create the connection} ,for Interface ID =" + _interfaceID);

                        HuaweiSocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;

                        if (logsEnable)
                            HuaweiProps.logMessage("[HEARTBEAT] [SocketConnection]=" + socketConnection.isConnected() + " ,for Interface ID =" + _interfaceID);

                        busyList.add(socketConnection);
                    } catch (Exception ex) {
                        HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{Exception while creating the conection}" + ex.getMessage() + " ,for Interface ID =" + _interfaceID);
                        _log.error("sendRequestToIN", "Exception ex:" + ex.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while gettting new connnection in case of read time out,Exception e :" + e.getMessage());
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                    }
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while reading response message Exception e :" + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    _requestMap.put("IN_TOTAL_TIME", String.valueOf(totalTime));
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    // Free the connection from the busy list and add it to the
                    // busyList.
                    busyList.remove(socketConnection);
                    freeList.add(socketConnection);
                    isConnectionFree = true;
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());

                    _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
                }

                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [responseBuffer]=" + bytesReadbuf.toString() + " [Total Time] " + endTime + " [Status]{After reading the message from IN } ,for Interface ID =" + _interfaceID);

                // responseStr = bytesReadbuf.toString();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr : " + bytesReadbuf + "responseStr :len" + bytesReadbuf.length);
                // write _inTXNID, _inTXNID, NETWORK_CODE, action, request map
                // in transaction log
                TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Request String :" + inRequestByte + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                // if response string is null then put status as AMBIGOUS in
                // request map. Handle event and throw exception.
                // else parse response.
                if (InterfaceUtil.isNullString(bytesReadbuf.toString())) {
                    _requestMap.put("status", InterfaceErrorCodesI.AMBIGOUS);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Blank response from Huawei IN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(HuaweiI.ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }
                // call parseResponse of formatter to get response map
                _responseMap = new HashMap<String, String>();// instantiate
                                                             // response map
                                                             // which is a
                                                             // instance
                                                             // variable
                _responseMap = _formatter.parseResponse(p_action, bytesReadbuf);

                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [_responseMap]=" + _responseMap + " [Status]{After parsing response message } ,for Interface ID =" + _interfaceID + " _inTXNID = " + _inTXNID);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "_responseMap:" + _responseMap);
                // Check if transaction id sent and received from IN are equal
                // or not.
                String status = (String) _responseMap.get("status");
                _requestMap.put("INTERFACE_STATUS", status);

                String requestHeaderTxnID = (String) _requestMap.get("IN_HEADER_TXN_ID");
                String responseHeaderTxnID = (String) _responseMap.get("IN_HEADER_TXN_ID");
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " huaweiTxnID = " + requestHeaderTxnID + " txnID = " + responseHeaderTxnID);
                // get status from response map.
                // status is o.k. then log transaction else handle event and
                // throw exception.

                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [status]=" + status + " [Status]{Read Recharge Status } ,for Interface ID =" + _interfaceID);

                if (!HuaweiI.RECHARGE_SUCCEEDED.equals(status)) {
                    if (HuaweiI.INVALID_RESPONSE.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Account number not found.");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " INVALID_RESPONSE. AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INVALID_RESPONSE);// MSISDN
                                                                                                                    // Not
                                                                                                                    // Found
                    } else if (HuaweiI.ACCOUNT_NUMBER_NOT_FOUND.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Account number not found.");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_ACCOUNT_NUMBER_NOT_FOUND AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_ACCOUNT_NUMBER_NOT_FOUND);// MSISDN
                                                                                                                               // Not
                                                                                                                               // Found
                    } else if (HuaweiI.ACCOUNT_EXPIRED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Account expired");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_ACCOUNT_EXPIRED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_ACCOUNT_EXPIRED);// MSISDN
                                                                                                                      // Barred
                    } else if (HuaweiI.USED_FIRST_TIME_INVALID.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Used for the first time or invalid.d");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_USED_FIRST_TIME_INVALID AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_USED_FIRST_TIME_INVALID);// MSISDN
                                                                                                                              // Barred
                    } else if (HuaweiI.SUBSCRIBER_NOT_ACTIVATED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Subscriber not activated");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_SUBSCRIBER_NOT_ACTIVATED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_SUBSCRIBER_NOT_ACTIVATED);// MSISDN
                                                                                                                               // Barred
                    } else if (HuaweiI.INCORRECT_PIN.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Incorrect PIN");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_INCORRECT_PIN AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_INCORRECT_PIN);// MSISDN
                                                                                                                    // Barred
                    } else if (HuaweiI.EXCEED_MAX_RECH_AMOUNT.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Exceeding the maximum recharge amount");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_EXCEED_MAX_RECH_AMOUNT AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_EXCEED_MAX_RECH_AMOUNT);// MSISDN
                                                                                                                             // Barred
                    } else if (HuaweiI.NO_PPS_INFO.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response No PPS information");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_NO_PPS_INFO AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_NO_PPS_INFO);// MSISDN
                                                                                                                  // Barred
                    } else if (HuaweiI.INSIFFICENT_ACC_BALANCE.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Insufficient account balance");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_INSIFFICENT_ACC_BALANCE AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_INSIFFICENT_ACC_BALANCE);// MSISDN
                                                                                                                              // Barred
                    } else if (HuaweiI.RECH_FAILED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Recharging failed");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_RECH_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_RECH_FAILED);// MSISDN
                                                                                                                  // Barred
                    } else if (HuaweiI.SYSTEM_EXCEPTION.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response System execution error");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_SYSTEM_EXCEPTION AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_SYSTEM_EXCEPTION);// MSISDN
                                                                                                                       // Barred
                    } else if (HuaweiI.TXN_SN_REPEATED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Transaction SN repeated");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_TXN_SN_REPEATED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_TXN_SN_REPEATED);// MSISDN
                                                                                                                      // Barred
                    } else if (HuaweiI.RECH_SUCCESS_LOGGING_FAILED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Recharging succeeded but logging failed");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_RECH_SUCCESS_LOGGING_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_RECH_SUCCESS_LOGGING_FAILED);// MSISDN
                                                                                                                                  // Barred
                    } else if (HuaweiI.QUERY_AREA_CODE_FAILED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Querying area code failed (reserved, currently not in use)");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_QUERY_AREA_CODE_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_QUERY_AREA_CODE_FAILED);// MSISDN
                                                                                                                             // Barred
                    } else if (HuaweiI.QUERY_VALI_PERD_RECH_AMT_FAILED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Querying the validity period corresponding to the recharge amount failed");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_QUERY_VALI_PERD_RECH_AMT_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_QUERY_VALI_PERD_RECH_AMT_FAILED);// MSISDN
                                                                                                                                      // Barred
                    } else if (HuaweiI.RECHING_MAX_NO_REG_CUST.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Reaching the maximum number of registered subscribers");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_RECHING_MAX_NO_REG_CUST AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_RECHING_MAX_NO_REG_CUST);// MSISDN
                                                                                                                              // Barred
                    } else if (HuaweiI.SERVICE_DATA_NOT_CONFIGURE.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response Service data is not configuration");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_SERVICE_DATA_NOT_CONFIGURE AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.RC_SERVICE_DATA_NOT_CONFIGURE);// MSISDN
                                                                                                                                 // Barred
                    }
                    _log.error("sendRequestToIN", "Error in response with" + " status=" + status);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (!(requestHeaderTxnID.equals(responseHeaderTxnID))) {
                    _log.error("sendRequestToIN", "Transaction id set in the request Header [" + requestHeaderTxnID + "] does not matched with the transaction id fetched from response[" + responseHeaderTxnID + "]");
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " p_action = " + p_action, "Transaction id set in the request header [" + requestHeaderTxnID + "] does not matched with the transaction id fetched from response[" + responseHeaderTxnID + "],Hence marking the transaction as AMBIGUOUS");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }
                break;
            }
        } catch (BTSLBaseException be) {
            HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{BTSLBaseException while creating the conection}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
            _log.error("sendRequestToIN", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (endTime == 0)
                endTime = System.currentTimeMillis();
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            if (!isConnectionFree && (busyList != null && freeList != null)) {
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
                busyList.remove(socketConnection);
                freeList.add(socketConnection);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action:" + p_action);
        }
    }// end of sendRequestToIN

    /**
     * This method is used to set the interface parameters into request map.
     * 
     * @param int p_action
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_action = " + p_action);
        try {
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [p_action] =" + p_action + "[Status](Getting Interface Parameter), For Interface Id" + _interfaceID);

            String msgLength = FileCache.getValue(_interfaceID, "MESSAGELENGTH");
            if (InterfaceUtil.isNullString(msgLength)) {
                _log.error("setInterfaceParameters", "Value of MESSAGELENGTH is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MESSAGELENGTH is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            _requestMap.put("MESSAGELENGTH", msgLength.trim());
            // get TERM from IN File Cache and put it it request map
            String messageType = FileCache.getValue(_interfaceID, "MESSAGETYPE");
            if (InterfaceUtil.isNullString(messageType)) {
                _log.error("setInterfaceParameters", "Value of MESSAGETYPE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MESSAGETYPE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("MESSAGETYPE", messageType.trim());
            String messageId = FileCache.getValue(_interfaceID, "MESSAGEID");
            if (InterfaceUtil.isNullString(messageId)) {
                _log.error("setInterfaceParameters", "Value of MESSAGEID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MESSAGEID is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("MESSAGEID", messageId.trim());

            String srcFE = FileCache.getValue(_interfaceID, "SRCFE");
            if (InterfaceUtil.isNullString(srcFE)) {
                _log.error("setInterfaceParameters", "Value of SRCFE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SRCFE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SRCFE", srcFE.trim());
            // get RSV from IN File Cache and put it it request map
            String dstFE = FileCache.getValue(_interfaceID, "DSTFE");
            if (InterfaceUtil.isNullString(dstFE)) {
                _log.error("setInterfaceParameters", "Value of DSTFE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DSTFE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("DSTFE", dstFE.trim());
            // get DLGCTRL from IN File Cache and put it it request map
            String srcFSM = FileCache.getValue(_interfaceID, "SRCFSM");
            if (InterfaceUtil.isNullString(srcFSM)) {
                _log.error("setInterfaceParameters", "Value of SRCFSM is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SRCFSM is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SRCFSM", srcFSM.trim());
            // get TSRV from IN File Cache and put it it request map
            String dstFSM = FileCache.getValue(_interfaceID, "DSTFSM");
            if (InterfaceUtil.isNullString(dstFSM)) {
                _log.error("setInterfaceParameters", "Value of DSTFSM is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DSTFSM is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("DSTFSM", dstFSM.trim());
            // get START_FLAG from IN File Cache and put it it request map
            String nodeNo = FileCache.getValue(_interfaceID, "NODENO");
            if (InterfaceUtil.isNullString(nodeNo)) {
                _log.error("setInterfaceParameters", "Value of NODENO is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "NODENO is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("NODENO", nodeNo.trim());
            // get DLGCON from IN File Cache and put it it request map
            String serviceKey = FileCache.getValue(_interfaceID, "SERVICEKEY");
            if (InterfaceUtil.isNullString(serviceKey)) {
                _log.error("setInterfaceParameters", "Value of SERVICEKEY is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _inTXNID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SERVICEKEY is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SERVICEKEY", serviceKey.trim());
            if (logsEnable)
                HuaweiProps.logMessage("[HuaweiINHandler] [msgLength] =" + msgLength + "[messageType] =" + messageType + "[messageId] =" + messageId + "[srcFE] =" + srcFE + "[dstFE] =" + dstFE + "[srcFSM] =" + srcFSM + "[DSTFSM] =" + dstFSM + "[nodeNo] =" + nodeNo + "[serviceKey] =" + serviceKey + "[Status](Getting Interface Parameter), For Interface Id" + _interfaceID);

        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap" + _requestMap);
        }// end of finally
    }// end of setInterfaceParameters

    /**
     * This method would be used to map language provided in IN File.
     * 
     * @throws Exception
     */
    private void setLanguageFromMapping() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setLanguageFromMapping", "Entered");
        String mappedLang = "";
        String[] mappingArr;
        String[] tempArr;
        boolean mappingNotFound = true;// Flag defines whether the mapping of
                                       // language is found or not.
        String langFromIN = null;
        String mappingString = null;
        try {
            // Get the mapping string from the FileCache and storing all the
            // mappings into array which are separated by ','.
            mappingString = FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
            if (InterfaceUtil.isNullString(mappingString))
                mappingString = "";
            // langFromIN = (String)_responseMap.get("LANGUAGETYPE");
            langFromIN = (String) _responseMap.get("LAGTYPE");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString = " + mappingString + " langFromIN = " + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0, length = mappingArr.length; in < length; in++) {
                tempArr = mappingArr[in].split(":");
                if (tempArr[0].equals(langFromIN)) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[setLanguageFromMapping]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[setLanguageFromMapping]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "while setting the language mapping get Exception=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * 
     * @throws BTSLBaseException
     */

    private void handleCancelTransaction() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("handleCancelTransaction", "Entered.");
        String cancelTxnAllowed = null;
        String cancelTxnStatus = null;
        String reconciliationLogStr = null;
        String cancelCommandStatus = null;
        String cancelNA = null;
        String interfaceStatus = null;
        Log reconLog = null;
        // int cancelRetryCount=0;
        try {
            _requestMap.put("REMARK1", FileCache.getValue(_interfaceID, "REMARK1"));
            _requestMap.put("REMARK2", FileCache.getValue(_interfaceID, "REMARK2"));
            // _requestMap.put("INTERFACE_STATUS",FileCache.getValue(_interfaceID,"INTERFACE_STATUS"));
            _requestMap.put("SYSTEM_STATUS_MAPPING", FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING"));
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING"));
            // get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "reconLog." + reconLog);
            cancelTxnAllowed = (String) FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            // if cancel transaction is not supported by IN, get error codes
            // from mapping present in IN fILE,write it
            // into reconciliation log and throw exception (This exception tells
            // the final status of transaction which was ambiguous) which would
            // be handled by validate, credit or debitAdjust methods
            if ("N".equals(cancelTxnAllowed)) {
                cancelNA = (String) FileCache.getValue(_interfaceID, "CANCEL_NA");// Cancel
                                                                                  // command
                                                                                  // status
                                                                                  // as
                                                                                  // NA.
                cancelCommandStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, cancelNA, "CANCEL_COMMAND_STATUS_MAPPING");
                _requestMap.put("MAPPED_CANCEL_STATUS", cancelCommandStatus);
                interfaceStatus = (String) _requestMap.get("INTERFACE_STATUS");
                cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, interfaceStatus, "SYSTEM_STATUS_MAPPING"); // PreTUPs
                                                                                                                                // Transaction
                                                                                                                                // status
                                                                                                                                // as
                                                                                                                                // FAIL/AMBIGUOUS
                                                                                                                                // based
                                                                                                                                // on
                                                                                                                                // value
                                                                                                                                // of
                                                                                                                                // SYSTEM_STATUS_MAPPING

                _requestMap.put("MAPPED_SYS_STATUS", cancelTxnStatus);
                reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
                reconLog.info("", reconciliationLogStr);
                if (!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
                    throw new BTSLBaseException(this, "handleCancelTransaction", cancelTxnStatus); // //Based
                                                                                                   // on
                                                                                                   // the
                                                                                                   // value
                                                                                                   // of
                                                                                                   // SYSTEM_STATUS
                                                                                                   // mark
                                                                                                   // the
                                                                                                   // transaction
                                                                                                   // as
                                                                                                   // FAIL
                                                                                                   // or
                                                                                                   // AMBIGUOUS
                                                                                                   // to
                                                                                                   // the
                                                                                                   // system.(//should
                                                                                                   // these
                                                                                                   // be
                                                                                                   // put
                                                                                                   // in
                                                                                                   // error
                                                                                                   // log
                                                                                                   // also.
                                                                                                   // ??????)
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // added to discard amount field from the message.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("handleCancelTransaction", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "handleCancelTransaction", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "Exited");
        }
    }

    public static String Time() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:ms");
        return sdf.format(cal.getTime());

    }

    private void sendRequestToINSingleConnection(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToINSingleConnection", " Entered p_action:" + p_action);
        byte[] inRequestByte = null;
        long startTime = 0;
        int retryCountConInvalid = 0;// Represent the Number of retries in case
                                     // of exception to get SocketWrapper
        OutputStream out = null;
        InputStream in = null;
        long endTime = 0;
        long sleepTimeConInvalid = 0;
        int retryCount = 0;
        String sessionID = null;
        byte bytesReadbuf[] = null;
        Socket socketConnection = null;
        try {
            String retryCountConInvalidStr = FileCache.getValue(_interfaceID, "RETRY_CON_INVAL");
            if (InterfaceUtil.isNullString(retryCountConInvalidStr) || !InterfaceUtil.isNumeric(retryCountConInvalidStr)) {
                _log.error("sendRequestToINSingleConnection", "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            retryCountConInvalid = Integer.parseInt(retryCountConInvalidStr);

            String sleepTimeConInvalidStr = FileCache.getValue(_interfaceID, "SLEEP_CON_INVAL");
            if (InterfaceUtil.isNullString(sleepTimeConInvalidStr) || !InterfaceUtil.isNumeric(sleepTimeConInvalidStr)) {
                _log.error("sendRequestToINSingleConnection", "SLEEP_CON_INVAL is either not defined in the INFile or it is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "SLEEP_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            sleepTimeConInvalid = Long.parseLong(sleepTimeConInvalidStr);
            while (retryCount++ <= retryCountConInvalid) {
                try {
                    sessionID = new HuaweiSocketWrapper().generateSessionID();
                    if (InterfaceUtil.isNullString(sessionID)) {
                        _log.error("sendRequestToINSingleConnection", "Session id obtained from socket wrapper is NULL");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Session id obtained from socket wrapper is NULL");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                    _requestMap.put("SESSIONID", sessionID);
                    String txnID = new HuaweiSocketWrapper().getINHeaderTxnID();
                    _requestMap.put("IN_HEADER_TXN_ID", txnID);
                    inRequestByte = _formatter.generateRequest(p_action, _requestMap);
                    startTime = System.currentTimeMillis();
                    _requestMap.put("IN_START_TIME", String.valueOf(startTime));

                    socketConnection = getNewSocketConnection();

                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] [socketConnection]=" + socketConnection + " [inRequestByte]= " + InterfaceUtil.printByteData(inRequestByte) + " [sessionID]= " + sessionID + " [IN_HEADER_TXN_ID]= " + txnID + " [_inTXNID]= " + _inTXNID);
                } catch (BTSLBaseException be) {
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToINSingleConnection", _inTXNID, "Exception while getting the request " + be.getMessage());
                    HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{ BTSLBaseException while getting the request}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
                    throw be;
                } catch (Exception e) {
                    HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{ Exception while getting the request}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                    throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                try {
                    out = socketConnection.getOutputStream();
                    out.write(inRequestByte);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{Exception while creating the conection " + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                    if (retryCount > retryCountConInvalid) {
                        _log.error("sendRequestToINSingleConnection", "Error while writing on output stream.");
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[sendRequestToINSingleConnection]", _interfaceID, "", "", "Number of retry reached to MAX" + _interfaceID);
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                    Thread.sleep(sleepTimeConInvalid);
                    continue;
                }
                try {
                    in = socketConnection.getInputStream();
                    bytesReadbuf = new byte[inRequestByte.length];
                    in.read(bytesReadbuf, 0, bytesReadbuf.length);
                    endTime = System.currentTimeMillis();
                    String warnTimeStr = FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] [bytesReadbuf]=" + InterfaceUtil.printByteData(bytesReadbuf) + " [_inTXNID]= " + _inTXNID);
                    if (!InterfaceUtil.isNullString(warnTimeStr)) {
                        long warnTime = Long.parseLong(warnTimeStr);
                        if (endTime - startTime > warnTime) {
                            _log.info("sendRequestToINSingleConnection", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Huawei IN is taking more time than the warning threshold. Total Time taken is: " + (endTime - startTime));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE.equals(_requestMap.get("REQ_SERVICE")) && PretupsI.INTERFACE_VALIDATE_ACTION.equals((String) _requestMap.get("INTERFACE_ACTION")))
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while reading response message Exception e :" + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.AMBIGOUS);
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                }
                if (InterfaceUtil.isNullString(bytesReadbuf.toString())) {
                    _requestMap.put("status", InterfaceErrorCodesI.AMBIGOUS);
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.AMBIGOUS);
                }
                _responseMap = new HashMap<String, String>();// instantiate
                                                             // response map
                                                             // which is a
                                                             // instance
                                                             // variable
                _responseMap = _formatter.parseResponse(p_action, bytesReadbuf);
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [_responseMap]=" + _responseMap + " [_inTXNID]= " + _inTXNID);
                String status = (String) _responseMap.get("status");
                _requestMap.put("INTERFACE_STATUS", status);
                String requestHeaderTxnID = (String) _requestMap.get("IN_HEADER_TXN_ID");
                String responseHeaderTxnID = (String) _responseMap.get("IN_HEADER_TXN_ID");
                if (logsEnable)
                    HuaweiProps.logMessage("[HuaweiINHandler] [requestHeaderTxnID]=" + requestHeaderTxnID + " [responseHeaderTxnID]= " + responseHeaderTxnID + " [status]= " + status + " [_inTXNID]= " + _inTXNID);
                if (!HuaweiI.RECHARGE_SUCCEEDED.equals(status)) {
                    if (HuaweiI.INVALID_RESPONSE.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Account number not found.");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " INVALID_RESPONSE. AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.INVALID_RESPONSE);// MSISDN
                                                                                                                                    // Not
                                                                                                                                    // Found
                    } else if (HuaweiI.ACCOUNT_NUMBER_NOT_FOUND.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Account number not found.");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_ACCOUNT_NUMBER_NOT_FOUND AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_ACCOUNT_NUMBER_NOT_FOUND);// MSISDN
                                                                                                                                               // Not
                                                                                                                                               // Found
                    } else if (HuaweiI.ACCOUNT_EXPIRED.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Account expired");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_ACCOUNT_EXPIRED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_ACCOUNT_EXPIRED);// MSISDN
                                                                                                                                      // Barred
                    } else if (HuaweiI.USED_FIRST_TIME_INVALID.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Used for the first time or invalid.d");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_USED_FIRST_TIME_INVALID AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_USED_FIRST_TIME_INVALID);// MSISDN
                                                                                                                                              // Barred
                    } else if (HuaweiI.SUBSCRIBER_NOT_ACTIVATED.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Subscriber not activated");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_SUBSCRIBER_NOT_ACTIVATED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_SUBSCRIBER_NOT_ACTIVATED);// MSISDN
                                                                                                                                               // Barred
                    } else if (HuaweiI.INCORRECT_PIN.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Incorrect PIN");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_INCORRECT_PIN AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_INCORRECT_PIN);// MSISDN
                                                                                                                                    // Barred
                    } else if (HuaweiI.EXCEED_MAX_RECH_AMOUNT.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Exceeding the maximum recharge amount");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_EXCEED_MAX_RECH_AMOUNT AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_EXCEED_MAX_RECH_AMOUNT);// MSISDN
                                                                                                                                             // Barred
                    } else if (HuaweiI.NO_PPS_INFO.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response No PPS information");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_NO_PPS_INFO AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_NO_PPS_INFO);// MSISDN
                                                                                                                                  // Barred
                    } else if (HuaweiI.INSIFFICENT_ACC_BALANCE.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Insufficient account balance");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_INSIFFICENT_ACC_BALANCE AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_INSIFFICENT_ACC_BALANCE);// MSISDN
                                                                                                                                              // Barred
                    } else if (HuaweiI.RECH_FAILED.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Recharging failed");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_RECH_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_RECH_FAILED);// MSISDN
                                                                                                                                  // Barred
                    } else if (HuaweiI.SYSTEM_EXCEPTION.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response System execution error");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_SYSTEM_EXCEPTION AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_SYSTEM_EXCEPTION);// MSISDN
                                                                                                                                       // Barred
                    } else if (HuaweiI.TXN_SN_REPEATED.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Transaction SN repeated");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_TXN_SN_REPEATED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_TXN_SN_REPEATED);// MSISDN
                                                                                                                                      // Barred
                    } else if (HuaweiI.RECH_SUCCESS_LOGGING_FAILED.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Recharging succeeded but logging failed");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_RECH_SUCCESS_LOGGING_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_RECH_SUCCESS_LOGGING_FAILED);// MSISDN
                                                                                                                                                  // Barred
                    } else if (HuaweiI.QUERY_AREA_CODE_FAILED.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Querying area code failed (reserved, currently not in use)");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_QUERY_AREA_CODE_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_QUERY_AREA_CODE_FAILED);// MSISDN
                                                                                                                                             // Barred
                    } else if (HuaweiI.QUERY_VALI_PERD_RECH_AMT_FAILED.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Querying the validity period corresponding to the recharge amount failed");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_QUERY_VALI_PERD_RECH_AMT_FAILED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_QUERY_VALI_PERD_RECH_AMT_FAILED);// MSISDN
                                                                                                                                                      // Barred
                    } else if (HuaweiI.RECHING_MAX_NO_REG_CUST.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Reaching the maximum number of registered subscribers");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_RECHING_MAX_NO_REG_CUST AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_RECHING_MAX_NO_REG_CUST);// MSISDN
                                                                                                                                              // Barred
                    } else if (HuaweiI.SERVICE_DATA_NOT_CONFIGURE.equals(status)) {
                        _log.error("sendRequestToINSingleConnection", "Error in response Service data is not configuration");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " RC_SERVICE_DATA_NOT_CONFIGURE AT IN");
                        throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.RC_SERVICE_DATA_NOT_CONFIGURE);// MSISDN
                                                                                                                                                 // Barred
                    }
                    _log.error("sendRequestToINSingleConnection", "Error in response with" + " status=" + status);
                    throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (!(requestHeaderTxnID.equals(responseHeaderTxnID))) {
                    _log.error("sendRequestToINSingleConnection", "Transaction id set in the request Header [" + requestHeaderTxnID + "] does not matched with the transaction id fetched from response[" + responseHeaderTxnID + "]");
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " p_action = " + p_action, "Transaction id set in the request header [" + requestHeaderTxnID + "] does not matched with the transaction id fetched from response[" + responseHeaderTxnID + "],Hence marking the transaction as AMBIGUOUS");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.AMBIGOUS);
                }
                break;
            }
        } catch (BTSLBaseException be) {
            HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{BTSLBaseException while creating the conection}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
            _log.error("sendRequestToINSingleConnection", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            HuaweiProps.logMessage("[HuaweiINHandler] [Exception]{Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
            e.printStackTrace();
            _log.error("sendRequestToINSingleConnection", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToINSingleConnection]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToINSingleConnection", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (endTime == 0)
                endTime = System.currentTimeMillis();
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            try {
                if (socketConnection != null) {
                    socketConnection.close();
                    socketConnection = null;
                }
                _out.close();
                _in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToINSingleConnection", "Exiting p_action:" + p_action);
        }
    }// end of sendRequestToINSingleConnection

    public Socket getNewSocketConnection() throws BTSLBaseException {
        Socket socketConnection = null;
        boolean connectionMade = false;
        try {
            String INSocketIp = FileCache.getValue(_interfaceID, "SOCKET_IP");
            if (InterfaceUtil.isNullString(INSocketIp)) {
                _log.error("getNewSocketConnection", "SOCKET_IP is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getNewSocketConnection", "", "_interfaceID:" + _interfaceID, "", "SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException(this, "getNewSocketConnection", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            // Fetch the Port from the INFile.
            String INSocketPort = FileCache.getValue(_interfaceID, "SOCKET_PORT");
            if (InterfaceUtil.isNullString(INSocketPort)) {
                _log.error("getNewSocketConnection", "SOCKET_PORT is either not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getNewSocketConnection", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is either not defined in the INFile");
                throw new BTSLBaseException(this, "getNewSocketConnection", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            String[] INSocketPortArray = INSocketPort.split(",");

            if (INSocketPortArray == null) {
                _log.error("getNewSocketConnection", "SOCKET_PORT is not defined in the INFile ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getNewSocketConnection", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is either not defined in the INFile or its value is not numeric");
                throw new BTSLBaseException(this, "getNewSocketConnection", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            String localSocketIP = FileCache.getValue(_interfaceID, "LOCAL_SOCKET_IP");
            if (InterfaceUtil.isNullString(localSocketIP)) {
                _log.error("getNewSocketConnection", "LOCAL_SOCKET_IP is either not defined in the INFile or its value is not numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getNewSocketConnection", "", "_interfaceID:" + _interfaceID, "", "LOCAL_SOCKET_IP is not defined in the INFile");
                throw new BTSLBaseException(this, "getNewSocketConnection", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            String[] localSocketIPArray = localSocketIP.split(",");

            if (localSocketIPArray == null) {
                _log.error("getNewSocketConnection", "LOCAL_SOCKET_IP is not defined in the INFile ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getNewSocketConnection", "", "_interfaceID:" + _interfaceID, "", "LOCAL_SOCKET_IP is not defined in the INFile ");
                throw new BTSLBaseException(this, "getNewSocketConnection", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
            String conTimeOutStr = FileCache.getValue(_interfaceID, "SOCKET_TIMEOUT");
            if (InterfaceUtil.isNullString(conTimeOutStr) || !InterfaceUtil.isNumeric(conTimeOutStr.trim())) {
                _log.error("getNewSocketConnection", "SOCKET_TIMEOUT is either not defined in the INFile or its value is not numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getNewSocketConnection", "", "_interfaceID:" + _interfaceID, "", "SOCKET_PORT is not defined in the INFile");
                throw new BTSLBaseException(this, "getNewSocketConnection", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }

            getIPPortMap();
            int connectionCounter = 0;
            while (connectionCounter++ < IPPortFreeVector.size()) {
                String localIP = null, localIPPortStr = null;
                int localPort, INPort;
                try {
                    if (localIPCounter >= IPPortFreeVector.size())
                        localIPCounter = 0;
                    localIPPortStr = IPPortFreeVector.get(localIPCounter++);
                    String localIPPort[] = localIPPortStr.split(":");
                    localIP = localIPPort[0];
                    localPort = Integer.parseInt(localIPPort[1]);
                    INPort = Integer.parseInt(localIPPort[2]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] getNewSocketConnection [socketConnection]=" + socketConnection + " [localSocketIp]= " + localIPPortStr + " [_inTXNID]= " + _inTXNID + " IP Port Config not done properly [Exception]=" + ex.getMessage());
                    continue;
                }
                try {
                    InetAddress localSocketIp = InetAddress.getByName(localIP);
                    socketConnection = new Socket(INSocketIp, INPort, localSocketIp, localPort);
                    socketConnection.setSoTimeout(Integer.parseInt(conTimeOutStr.trim()));

                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] getNewSocketConnection [socketConnection]=" + socketConnection + " [localSocketIp]= " + localSocketIp + " [Port]= " + localPort + " [_inTXNID]= " + _inTXNID + "Connection made and heartbeat req going to send");
                    if (sendHeartBeat(socketConnection)) {
                        connectionMade = true;
                        if (logsEnable)
                            HuaweiProps.logMessage("[HuaweiINHandler] getNewSocketConnection [socketConnection]=" + socketConnection + " [localSocketIp]= " + localSocketIp + " [Port]= " + localPort + " [_inTXNID]= " + _inTXNID + "Connection made and heartbeat success");
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("getNewSocketConnection while creating direct runtime connection: ", "Exception e:" + e.getMessage());
                    if (logsEnable)
                        HuaweiProps.logMessage("[HuaweiINHandler] getNewSocketConnection [socketConnection]=" + socketConnection + " [localSocketIp]= " + localIP + " [Port]= " + localPort + " [_inTXNID]= " + _inTXNID + " while creating direct runtime connection [Exception]=" + e.getMessage());
                } finally {
                    if (!connectionMade && socketConnection != null) {
                        socketConnection.close();
                    }
                }
            }
            /*
             * for(int loop=0;
             * loop<localSocketIPArray.length*INSocketPortArray.length; loop++)
             * {
             * if(INPortCounter >= INSocketPortArray.length){
             * INPortCounter = 0; localIPCounter++;
             * }
             * if(localIPCounter >= localSocketIPArray.length)
             * localIPCounter = 0;
             * try
             * {
             * InetAddress localSocketIp =
             * InetAddress.getByName(localSocketIPArray[localIPCounter]);
             * int INSocketPortInt =
             * Integer.parseInt(INSocketPortArray[INPortCounter++]);
             * 
             * String localSocketPortStr = String.valueOf(localIPCounter+1) +
             * String.valueOf(INSocketPortInt);
             * int localSocketPortInt = Integer.parseInt(localSocketPortStr);
             * 
             * socketConnection = new
             * Socket(INSocketIp,INSocketPortInt,localSocketIp
             * ,localSocketPortInt);
             * socketConnection.setSoTimeout(Integer.parseInt(conTimeOutStr.trim(
             * )));
             * 
             * if(logsEnable)
             * HuaweiProps.logMessage(
             * "[HuaweiINHandler] getNewSocketConnection [socketConnection]="
             * +socketConnection
             * +" [localSocketIp]= "+localSocketIp+" [Port]= "+INSocketPortInt
             * +" [_inTXNID]= "
             * +_inTXNID+"Connection made and heartbeat req going to send");
             * if(sendHeartBeat(socketConnection)){
             * connectionMade=true;
             * if(logsEnable)
             * HuaweiProps.logMessage(
             * "[HuaweiINHandler] getNewSocketConnection [socketConnection]="
             * +socketConnection
             * +" [localSocketIp]= "+localSocketIp+" [Port]= "+INSocketPortInt
             * +" [_inTXNID]= "
             * +_inTXNID+"Connection made and heartbeat success");
             * break;
             * }
             * }
             * catch(Exception e)
             * {
             * e.printStackTrace();
             * _log.error(
             * "getNewSocketConnection while creating direct runtime connection: "
             * ,"Exception e:"+e.getMessage());
             * if(logsEnable)
             * HuaweiProps.logMessage(
             * "[HuaweiINHandler] getNewSocketConnection [socketConnection]="
             * +socketConnection
             * +" [localSocketIp]= "+localSocketIPArray[localIPCounter
             * ]+" [Port]= "
             * +INSocketPortArray[INPortCounter-1]+" [_inTXNID]= "+_inTXNID
             * +" while creating direct runtime connection [Exception]="
             * +e.getMessage());
             * }
             * finally{
             * if(!connectionMade && socketConnection != null){
             * socketConnection.close();
             * }
             * }
             * }//end of for loop
             */

            if (!connectionMade) {
                _log.error("getNewSocketConnection", "Connection not created ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getNewSocketConnection", "", "_interfaceID:" + _interfaceID, "", "Connection not created");
                throw new BTSLBaseException(this, "getNewSocketConnection ", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
        } catch (BTSLBaseException bbe) {
            bbe.printStackTrace();
            throw bbe;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BTSLBaseException(this, "getNewSocketConnection", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        }
        return socketConnection;
    }

    public boolean sendHeartBeat(Socket p_socket) {
        if (_log.isDebugEnabled())
            _log.debug("sendHeartBeat", "Entered _interfaceID::");
        try {
            if (HuaweiRequestFormatter.heartBeatRequestInByte == null)
                new HuaweiRequestFormatter().heartBeatRequest(_interfaceID);
            _out = p_socket.getOutputStream();
            _in = p_socket.getInputStream();
            _out.write(HuaweiRequestFormatter.heartBeatRequestInByte);
            _out.flush();
            byte buf[] = new byte[HuaweiRequestFormatter.heartBeatRequestInByte.length];
            _in.read(buf, 0, buf.length);
            if (buf == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            else if (buf.length != 5)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            else
                return true;
        } catch (BTSLBaseException bbe) {
            bbe.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("sendHeartBeat", "Exit ");
        }

    }

    public void getIPPortMap() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getIPPortMap ", " Enter");
        try {
            if (IPPortFreeVector == null || IPPortFreeVector.size() == 0) {
                String[] localSocketPortArray = FileCache.getValue(_interfaceID, "LOCAL_SOCKET_IP_PORT").split(",");
                if (localSocketPortArray == null) {
                    _log.error("getIPPortMap ", " IP & Port value in IN file is invalid= " + _interfaceID + " is invalid");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getIPPortMap ", "", "_interfaceID:" + _interfaceID, "", "IP & Port value in IN file = " + _interfaceID + " is invalid ");
                    throw new BTSLBaseException(this, "getIPPortMap ", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
                }
                IPPortFreeVector = new Vector<String>();
                for (int i = 0; i < localSocketPortArray.length; i++) {
                    IPPortFreeVector.add(localSocketPortArray[i]);
                }
                if (IPPortFreeVector == null || IPPortFreeVector.isEmpty())
                    throw new BTSLBaseException(this, "getIPPortMap ", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
            }
        } catch (Exception ex) {
            _log.error("getIPPortMap ", " IP & Port value in IN file is invalid= " + _interfaceID + " is invalid");
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "getIPPortMap ", "", "_interfaceID:" + _interfaceID, "", "IP & Port value in IN file = " + _interfaceID + " is invalid ");
            throw new BTSLBaseException(this, "getIPPortMap ", InterfaceErrorCodesI.ERROR_INIT_SOCKET_CONNECTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getIPPortMap ", " Exit IPPortFreeVector = " + IPPortFreeVector);
        }
    }
}// end of class
