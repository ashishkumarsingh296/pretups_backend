package com.inter.zteethopia;

/**
 * @ZTEINHandler.java
 *                    Copyright(c) 2013, Mahindra Comviva Technologies Ltd.
 *                    All Rights Reserved
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Vipan Kumar Sep 13, 2013 Initial Creation
 *                    ----------------------------------------------------------
 *                    -------------------------------------
 *                    This class is the Handler class for the ZTEIN interface
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
import com.btsl.util.BTSLUtil;

public class ZTEINHandler implements InterfaceHandler {

    private Log _log = LogFactory.getLog(ZTEINHandler.class.getName());
    private HashMap<String, String> _requestMap = null;// Contains the request
                                                       // parameter as key and
                                                       // value pair.
    private HashMap<String, String> _responseMap = null;// Contains the response
                                                        // of the request as key
                                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private ZTEINRequestFormatter _formatter = null;
    boolean logsEnable = false;
    OutputStream _out = null;
    InputStream _in = null;
    static int localIPCounter = 0, INPortCounter = 0;

    public ZTEINHandler() {
        _formatter = new ZTEINRequestFormatter();
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
            String fileCacheId = null;
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            fileCacheId = _interfaceID;

            String ztLogs = FileCache.getValue(fileCacheId, "TransactionLogsFlag");
            if (InterfaceUtil.isNullString(ztLogs)) {
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [validate] TransactionLogsFlag is not defined in the INFile");
                ztLogs = "N";
            }

            String oldExpriyDate = FileCache.getValue(fileCacheId, "OLD_EXPIRY_DATE");
            if (InterfaceUtil.isNullString(oldExpriyDate)) {
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [validate] OLD_EXPIRY_DATE is not defined in the INFile");
                oldExpriyDate = "20991230";
            }

            if (ztLogs != null && ztLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            _msisdn = (String) _requestMap.get("MSISDN");
            String validateRequired = FileCache.getValue(fileCacheId, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if (_log.isDebugEnabled())
                _log.debug("validate", "Entered validateRequired : " + validateRequired + " for REQ_SERVICE_USER_TYPE" + _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));

            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                _requestMap.put("SERVICE_CLASS", "ALL");
                _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(oldExpriyDate, "yyyyMMdd"));
                return;
            }

            _inTXNID = InterfaceUtil.getINTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEINHandler] [MSISDN] =" + _msisdn + " [_inTXNID] =" + _inTXNID + ", For Interface Id" + _interfaceID);

            String huaweiMultiplicationFactor = FileCache.getValue(fileCacheId, "ZTEETHIO_MULT_FACTOR");

            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("validate", "ZTEETHIO_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[validate]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEINHandler] [huaweiMultiplicationFactor] =" + huaweiMultiplicationFactor + ", For Interface Id" + _interfaceID);

            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();

            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEINHandler] [HuaweiI.ACTION_ACCOUNT_INFO] =" + ZTEINI.ACTION_ACCOUNT_INFO + "[Status](Validation Before sending to sendRequestToIN), For Interface Id" + _interfaceID);

            sendRequestToIN(fileCacheId, ZTEINI.ACTION_ACCOUNT_INFO);
            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEINHandler] [HuaweiI.ACTION_ACCOUNT_INFO] =" + ZTEINI.ACTION_ACCOUNT_INFO + "[Status](Validation After sending to sendRequestToIN), For Interface Id" + _interfaceID);

            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            // _requestMap.put("ACCOUNT_STATUS",_responseMap.get("ACNTSTAT"));
            _requestMap.put("SERVICE_CLASS", _responseMap.get("BRANDINDEX"));

            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(oldExpriyDate, "yyyyMMdd"));
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            ZTEINTransactionLogger.logMessage("[ZTEINHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in validation process), For Interface Id" + _interfaceID);
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[validate]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
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
        double zteEthioMultFactorDouble = 0;
        String amountStr = null;
        _requestMap = p_requestMap;
        String fileCacheId = null;
        try {

            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            fileCacheId = _interfaceID;

            String hbLogs = FileCache.getValue(fileCacheId, "TransactionLogsFlag");
            if (InterfaceUtil.isNullString(hbLogs)) {
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [Status] ZTEININLOGS is not defined in the INFile");
                hbLogs = "N";
            }
            if (hbLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            _msisdn = (String) _requestMap.get("MSISDN");

            // Fetching the ZTEIN_MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.

            String zteEthioMultiplicationFactor = FileCache.getValue(fileCacheId, "ZTEETHIO_MULT_FACTOR");
            if (InterfaceUtil.isNullString(zteEthioMultiplicationFactor)) {
                _log.error("credit", "ZTEETHIO_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[credit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ZTEETHIO_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("ZTEETHIO_MULT_FACTOR", zteEthioMultiplicationFactor.trim());

            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");

            try {
                zteEthioMultFactorDouble = Double.parseDouble(zteEthioMultiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, zteEthioMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(fileCacheId, "ROUND_FLAG");
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ZTEINHandler[credit]", _inTXNID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEININHandler[credit]", "REFERENCE ID = " + _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [ZTEINI.ACTION_RECHARGE_CREDIT] =" + ZTEINI.ACTION_RECHARGE_CREDIT + "[Status](Credit Before sending to sendRequestToIN), For Interface Id " + _interfaceID + " [inTXNID] " + _inTXNID + " [msisdn] " + _msisdn + " [zteINMultiplicationFactor] " + _inTXNID + " [amountStr] " + _inTXNID);

            if (_log.isDebugEnabled())
                _log.debug("credit ", " For Interface Id " + _interfaceID + " [inTXNID] " + _inTXNID + " [msisdn] " + _msisdn + " [zteINMultiplicationFactor] " + _inTXNID + " [amountStr] " + _inTXNID);

            sendRequestToIN(fileCacheId, ZTEINI.ACTION_RECHARGE_CREDIT);

            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [ZTEINI.ACTION_RECHARGE_CREDIT] =" + ZTEINI.ACTION_RECHARGE_CREDIT + "[Status](Credit After sending to sendRequestToIN), For Interface Id" + _interfaceID);

            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            try {
                String prevBalanceStr = (String) _responseMap.get("accountPrevBalance");
                if (logsEnable)
                    ZTEINTransactionLogger.logMessage("[ZTEEthioINHandler] [Amount] =" + prevBalanceStr + "[Status](Return previous Amount credit), For Interface Id" + _interfaceID);
                prevBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(prevBalanceStr, zteEthioMultFactorDouble);
                _requestMap.put("INTERFACE_PREV_BALANCE", prevBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEEthioINHandler[credit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            try {
                String postBalanceStr = (String) _responseMap.get("accountPostBalance");
                if (logsEnable)
                    ZTEINTransactionLogger.logMessage("[ZTEEthioINHandler] [Amount] =" + postBalanceStr + "[Status](Return Amount validation), For Interface Id" + _interfaceID);
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, zteEthioMultFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEEthioINHandler[credit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            ZTEINTransactionLogger.logMessage("[ZTEININHandler] [BTSLBaseException] =" + be.getMessage() + "[Exception](Exception in credit process), For Interface Id" + _interfaceID);

            if ((be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "CR");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                handleCancelTransaction(fileCacheId);
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in credit process), For Interface Id" + _interfaceID);
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEININHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ZTEINTransactionLogger.logMessage("[ZTEININHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in credit process), For Interface Id" + _interfaceID);
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEININHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
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
            _log.debug("debit", "Entered p_requestMap: " + p_requestMap);

        double systemAmtDouble = 0;
        double zteEthioMultFactorDouble = 0;
        String amountStr = null;
        _requestMap = p_requestMap;
        String fileCacheId = null;
        try {

            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            fileCacheId = _interfaceID;

            String hbLogs = FileCache.getValue(fileCacheId, "TransactionLogsFlag");
            if (InterfaceUtil.isNullString(hbLogs)) {
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [Status] ZTEININLOGS is not defined in the INFile");
                hbLogs = "N";
            }
            if (hbLogs.equalsIgnoreCase("Y"))
                logsEnable = true;

            /*
             * _inTXNID=InterfaceUtil.getINTxnID();
             * _requestMap.put("IN_TXN_ID",_inTXNID);
             */
            _msisdn = (String) _requestMap.get("MSISDN");

            // Fetching the ZTEIN_MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.

            String zteEthioMultiplicationFactor = FileCache.getValue(fileCacheId, "ZTEETHIO_MULT_FACTOR");
            if (InterfaceUtil.isNullString(zteEthioMultiplicationFactor)) {
                _log.error("debit", "ZTEETHIO_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[debit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ZTEETHIO_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "debit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("ZTEETHIO_MULT_FACTOR", zteEthioMultiplicationFactor.trim());

            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_ADJUST");

            try {
                zteEthioMultFactorDouble = Double.parseDouble(zteEthioMultiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, zteEthioMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(fileCacheId, "ROUND_FLAG");
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ZTEINHandler[debit]", _inTXNID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEININHandler[debit]", "REFERENCE ID = " + _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [ZTEINI.ACTION_RECHARGE_ADJUST] =" + ZTEINI.ACTION_RECHARGE_ADJUST + "[Status](debit Before sending to sendRequestToIN), For Interface Id " + _interfaceID + " [inTXNID] " + _inTXNID + " [msisdn] " + _msisdn + " [zteINMultiplicationFactor] " + _inTXNID + " [amountStr] " + _inTXNID);

            if (_log.isDebugEnabled())
                _log.debug("debit ", " For Interface Id " + _interfaceID + " [inTXNID] " + _inTXNID + " [msisdn] " + _msisdn + " [zteINMultiplicationFactor] " + _inTXNID + " [amountStr] " + _inTXNID);

            sendRequestToIN(fileCacheId, ZTEINI.ACTION_RECHARGE_ADJUST);

            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [ZTEINI.ACTION_RECHARGE_ADJUST] =" + ZTEINI.ACTION_RECHARGE_ADJUST + "[Status](debit After sending to sendRequestToIN), For Interface Id" + _interfaceID);

            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // _requestMap.put("NEW_EXPIRY_DATE",
            // InterfaceUtil.getInterfaceDateFromDateString((String)
            // _responseMap.get("AFTERVALIDITY"), "yyyyMMdd"));

            try {
                String prevBalanceStr = (String) _responseMap.get("accountPrevBalance");
                if (logsEnable)
                    ZTEINTransactionLogger.logMessage("[ZTEEthioINHandler] [Amount] =" + prevBalanceStr + "[Status](Return previous Amount debit), For Interface Id" + _interfaceID);
                prevBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(prevBalanceStr, zteEthioMultFactorDouble);
                _requestMap.put("INTERFACE_PREV_BALANCE", prevBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEEthioINHandler[debit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "debit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            try {
                String postBalanceStr = (String) _responseMap.get("accountPostBalance");
                if (logsEnable)
                    ZTEINTransactionLogger.logMessage("[ZTEEthioINHandler] [Amount] =" + postBalanceStr + "[Status](Return Amount validation), For Interface Id" + _interfaceID);
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, zteEthioMultFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEEthioINHandler[debit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "debit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debit", "BTSLBaseException be:" + be.getMessage());
            ZTEINTransactionLogger.logMessage("[ZTEININHandler] [BTSLBaseException] =" + be.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);

            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "CR");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                handleCancelTransaction(fileCacheId);
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                ZTEINTransactionLogger.logMessage("[ZTEININHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);
                _log.error("debit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEININHandler[debit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debit");
                throw new BTSLBaseException(this, "debit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ZTEINTransactionLogger.logMessage("[ZTEININHandler] [Exception] =" + e.getMessage() + "[Exception](Exception in debit process), For Interface Id" + _interfaceID);
            _log.error("debit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEININHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debit", "Exited _requestMap=" + _requestMap);
        }
    }// end of debitAdjust

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
    private void sendRequestToIN(String fileCacheId, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " Entered p_action:" + p_action);
        // TransactionLog.log(_inTXNID,_inTXNID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"","action="+p_action+" _requestMap = "+_requestMap);
        String responseStr = null;
        String inRequest = null;
        ZTEINSocket socket = null;
        ZTEINSocketWrapper socketConnection = null;
        long startTime = 0;
        boolean isHua = true;
        OutputStream out = null;
        // InputStream in = null;
        BufferedReader in = null;
        long endTime = 0;
        String sessionID = null;
        Vector<Object> busyList = null;
        Vector<Object> freeList = null;
        Utility utilObj = new Utility();
        // StringBuffer responseBuffer = null;
        StringBuffer responseBuffer = null;
        try {
            String _interfaceid = _interfaceID;
            int count = 0;
            boolean flag = true;
            int nodes = Integer.parseInt(FileCache.getValue(fileCacheId, "NODES_SIZE_" + _interfaceid));
            if (logsEnable)
                ZTEINTransactionLogger.logMessage("total nodes of the interface " + _interfaceid + " is =" + nodes);

            String txnID = new ZTEINSocketWrapper().getINHeaderTxnID();

            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "txnID: " + txnID);

            _requestMap.put("TRANSACTIONHEADERID", txnID);

            while (flag) {
                ++count;
                if (logsEnable)
                    ZTEINTransactionLogger.logMessage(" Check for node " + count + " Is check all nodes" + (count < nodes));

                if (count <= nodes) {
                    try {
                        if (logsEnable)
                            ZTEINTransactionLogger.logMessage("Recharge No=" + _inTXNID + "Txn No=" + txnID + "Mobile No=" + _msisdn + "Network Code=" + (String) _requestMap.get("NETWORK_CODE") + " Action " + String.valueOf(p_action) + " Type=" + PretupsI.TXN_LOG_REQTYPE_RES + "Before getting socket connection:" + inRequest + " Response string:" + responseStr + " " + PretupsI.TXN_LOG_STATUS_UNDERPROCESS + " INTERFACE ID = " + _interfaceID + " action=" + p_action);

                        socket = ZTEINPoolManager.getClientObject(_interfaceid, _inTXNID, isHua);
                        socketConnection = socket.getZteINSocketWrapper();

                        if (logsEnable)
                            ZTEINTransactionLogger.logMessage("Recharge No=" + _inTXNID + "Txn No=" + txnID + "Mobile No=" + _msisdn + "Network Code=" + (String) _requestMap.get("NETWORK_CODE") + " Action " + String.valueOf(p_action) + " Type=" + PretupsI.TXN_LOG_REQTYPE_RES + "After getting socket connection:" + inRequest + " Response string:" + responseStr + " " + PretupsI.TXN_LOG_STATUS_UNDERPROCESS + " INTERFACE ID = " + _interfaceID + " action=" + p_action);

                        if (socketConnection != null)
                            break;
                        else {
                            // raj-barring
                            if (ZTEINStatus.getInstance().isFailCountReached(fileCacheId, _interfaceID)) {
                                if (logsEnable)
                                    ZTEINTransactionLogger.logMessage("IP & Port" + FileCache.getValue(fileCacheId, "ZTE_IN_IP_" + _interfaceid) + ":" + FileCache.getValue(fileCacheId, "ZTE_IN_PORT_" + _interfaceid) + " MSISDN = " + _msisdn + ":: PTRefId = " + txnID + "::Stage::" + p_action + " :::StatusCode::200::IN Interface::" + _interfaceID + "Message:: ZTE Node reached Max FailCount, going to be barred");
                                ZTEINStatus.getInstance().barredAir(_interfaceID);
                                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, _interfaceID, EventStatusI.RAISED, EventLevelI.FATAL, "IN :" + _interfaceID + " has been Barred : ", "", "" + FileCache.getValue(fileCacheId, "ZTE_IN_IP_" + _interfaceid) + "" + FileCache.getValue(fileCacheId, "ZTE_IN_PORT_" + _interfaceid), "", "");

                            } else {
                                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, _interfaceID, EventStatusI.RAISED, EventLevelI.FATAL, "IN :" + _interfaceID + " Un known Exception at making connetion with ZTE IN IP : ", "", "" + FileCache.getValue(fileCacheId, "ZTE_IN_IP_" + _interfaceid) + "" + FileCache.getValue(fileCacheId, "ZTE_IN_PORT_" + _interfaceid), "", "");

                            }
                        }
                    } catch (Exception e) {
                        ZTEINTransactionLogger.logMessage("Exception Connection is null" + e.getMessage());
                    }
                } else {
                    ZTEINTransactionLogger.logMessage("No node is active" + InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT + "total nodes of the interface " + _interfaceid + " is =" + nodes);
                    throw new BTSLBaseException("No node is active " + InterfaceErrorCodesI.ERROR_FETCH_CLIENT_OBJECT);
                }
            }
        } catch (BTSLBaseException be) {
            ZTEINTransactionLogger.logMessage("[ZTEINHandler] [Exception]{Exception while creating the conection}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
            _log.error("sendRequestToIN", "Exception ex:" + be.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while gettting new connnection in case of read time out,Exception e :" + be.getMessage());
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } catch (Exception be) {
            ZTEINTransactionLogger.logMessage("[ZTEINHandler] [Exception]{Exception while creating the conection}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
            _log.error("sendRequestToIN", "Exception ex:" + be.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while gettting new connnection in case of read time out,Exception e :" + be.getMessage());
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }

        try {

            if (logsEnable)
                ZTEINTransactionLogger.logMessage("sendRequestToIN  socketConnection:" + socketConnection);
            _interfaceID = socketConnection.getInterfaceID();
            busyList = (Vector<Object>) ZTEINPoolManager._busyBucket.get(_interfaceID);// get
                                                                                       // busy
                                                                                       // and
                                                                                       // free
                                                                                       // pool
                                                                                       // from
                                                                                       // pool
                                                                                       // mgr.
            freeList = (Vector<Object>) ZTEINPoolManager._freeBucket.get(_interfaceID);
            sessionID = socketConnection.generateSessionID();
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " getting sessionID : " + sessionID);
            if (InterfaceUtil.isNullString(sessionID)) {
                _log.error("sendRequestToIN", "Session id obtained from socket wrapper is NULL");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEEthioINHandler[sendRequestToIN]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Session id obtained from socket wrapper is NULL");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("SESSIONID", sessionID);
        } catch (Exception e) {
            e.printStackTrace();
            ZTEINTransactionLogger.logMessage("[ZTEINHandler] [Exception]{ Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[sendRequestToIN]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Exception occurs while getting new Client object");
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }

        try {
            inRequest = _formatter.generateRequest(fileCacheId, p_action, _requestMap);
            if (logsEnable)
                ZTEINTransactionLogger.logMessage("[ZTEINHandler] [Request]=" + inRequest + " [Status]{After generating request } ,for Interface ID =" + _interfaceID + " _inTXNID = " + _inTXNID);
        } catch (BTSLBaseException be) {
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", _inTXNID, "Exception while getting the request " + be.getMessage());
            ZTEINTransactionLogger.logMessage("[ZTEINHandler] [Exception]{ BTSLBaseException while getting the request}" + be.getMessage() + " ,for Interface ID =" + _interfaceID);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
        } catch (Exception e) {
            ZTEINTransactionLogger.logMessage("[ZTEINHandler] [Exception]{ Exception while getting the request}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
        }

        try {

            try {

                TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Before writing request in scoket :" + inRequest + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                out = socketConnection.getPrintWriter();
                TransactionLog.log(_inTXNID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "After writing request in scoket :" + inRequest + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", _inTXNID, "Sending request to IN _socketConnection: " + socketConnection + " _interfaceID: " + _interfaceID + " inRequestByte: " + inRequest);
                // get start time of transaction and put it into request map.
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                // Send message to IN.
                out.write(inRequest.getBytes());
                out.flush();
            } catch (SocketException e) {

                ZTEINTransactionLogger.logMessage("[ZTENHandler] [Exception]{ Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                _log.error("sendRequestToIN", "Exception ex :" + e.getMessage());

                if (ZTEINStatus.getInstance().isFailCountReached(fileCacheId, _interfaceID)) {
                    ZTEINTransactionLogger.logMessage("IP & Port" + socketConnection.getIP() + ":" + socketConnection.getport() + " MSISDN = " + _msisdn + ":: TxnId = " + _inTXNID + "::Stage::" + p_action + " :::StatusCode::200::IN Interface::" + _interfaceID + "Message:: ZTE Node reached Max FailCount, going to be barred");
                    ZTEINStatus.getInstance().barredAir(_interfaceID);
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "INTERFACE_ID=" + _interfaceID + " has been Barred: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());

                } else {
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "Un known Exception at making connetion with ZTE IN IP: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());
                }
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            } catch (SocketTimeoutException e) {

                ZTEINTransactionLogger.logMessage("[ZTENHandler] [Exception]{ Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                _log.error("sendRequestToIN", "Exception ex :" + e.getMessage());

                if (ZTEINStatus.getInstance().isFailCountReached(fileCacheId, _interfaceID)) {
                    ZTEINTransactionLogger.logMessage("IP & Port" + socketConnection.getIP() + ":" + socketConnection.getport() + " MSISDN = " + _msisdn + ":: TxnId = " + _inTXNID + "::Stage::" + p_action + " :::StatusCode::200::IN Interface::" + _interfaceID + "Message:: ZTE Node reached Max FailCount, going to be barred");
                    ZTEINStatus.getInstance().barredAir(_interfaceID);
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "INTERFACE_ID=" + _interfaceID + " has been Barred: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());

                } else {
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "Un known Exception at making connetion with ZTE IN IP: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());
                }

                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            } catch (IOException e) {
                ZTEINTransactionLogger.logMessage("[ZTENHandler] [Exception]{ Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                _log.error("sendRequestToIN", "Exception ex :" + e.getMessage());

                if (ZTEINStatus.getInstance().isFailCountReached(fileCacheId, _interfaceID)) {
                    ZTEINTransactionLogger.logMessage("IP & Port" + socketConnection.getIP() + ":" + socketConnection.getport() + " MSISDN = " + _msisdn + ":: TxnId = " + _inTXNID + "::Stage::" + p_action + " :::StatusCode::200::IN Interface::" + _interfaceID + "Message:: ZTE Node reached Max FailCount, going to be barred");
                    ZTEINStatus.getInstance().barredAir(_interfaceID);
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "INTERFACE_ID=" + _interfaceID + " has been Barred: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());

                } else {
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "Un known Exception at making connetion with ZTE IN IP: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());
                }

                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            } catch (Exception e) {
                ZTEINTransactionLogger.logMessage("[ZTENHandler] [Exception]{ Exception while creating the conection}" + e.getMessage() + " ,for Interface ID =" + _interfaceID);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                _log.error("sendRequestToIN", "Exception ex :" + e.getMessage());

                if (ZTEINStatus.getInstance().isFailCountReached(fileCacheId, _interfaceID)) {
                    ZTEINTransactionLogger.logMessage("IP & Port" + socketConnection.getIP() + ":" + socketConnection.getport() + " MSISDN = " + _msisdn + ":: TxnId = " + _inTXNID + "::Stage::" + p_action + " :::StatusCode::200::IN Interface::" + _interfaceID + "Message:: ZTE Node reached Max FailCount, going to be barred");
                    ZTEINStatus.getInstance().barredAir(_interfaceID);
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "INTERFACE_ID=" + _interfaceID + " has been Barred: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());

                } else {
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEINHandler[SendREquestToON]", "Un known Exception at making connetion with ZTE IN IP: ", "", "", " =" + _interfaceID + " get Exception=" + e.getMessage());
                }

                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            if (logsEnable)
                ZTEINTransactionLogger.logMessage("sendRequestToIN WAITING FOR IN RESPONSE _socketConnection:" + socketConnection + "::::::::::IN ID= " + _interfaceID);

            try {
                // get BufferedReader from SocketWrapper
                in = socketConnection.getBufferedReader();

                if (logsEnable)
                    ZTEINTransactionLogger.logMessage("sendRequestToIN reading message");
                int c = 0;
                int count = 0;
                int messagelength = 0;
                responseBuffer = new StringBuffer(1028);
                boolean flag = false;
                while ((c = in.read()) != -1) {

                    responseBuffer.append((char) c);
                    if (!flag)
                        if (responseBuffer.toString().contains(FileCache.getValue(fileCacheId, "START_FLAG"))) {
                            ++count;
                            if (count == 7) {
                                flag = true;
                                int i = responseBuffer.toString().lastIndexOf("`") + 1;
                                messagelength = Integer.parseInt(responseBuffer.toString().substring(i, i + 4), 16);
                                messagelength = messagelength + 16;

                            }
                        }
                    if (flag)
                        if (messagelength == responseBuffer.toString().length())
                            break;
                }
                // END TIME OF transaction.
                endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                ZTEINStatus.getInstance().unbarredAirAfterSucces(_interfaceID);

            } catch (SocketTimeoutException e) {
                _requestMap.put("ReconCode", "550");
                _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);

                try {
                    busyList.remove(socket);
                    if (socketConnection != null) {
                        socketConnection.destroy(fileCacheId);
                    }
                } catch (Exception ex) {
                    ZTEINTransactionLogger.logMessage("Error while destroy the connection" + ex.getMessage());
                }

                try {
                    if (logsEnable)
                        ZTEINTransactionLogger.logMessage("Creating new connection conecction during socket time out  Conecttion is null going to create the connection");
                    ZTEINSocketWrapper newSocketConnection = new ZTEINNewClientConnection().getNewClientObject(fileCacheId, _interfaceID, socket);
                    socketConnection = newSocketConnection;
                    socket.setZteINSocketWrapper(socketConnection);
                    busyList.add(socket);
                } catch (Exception e1) {
                    socketConnection = null;
                    busyList.add(socket);
                }
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
            } catch (SocketException e) {
                _requestMap.put("ReconCode", "550");
                _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);

                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
            } catch (IOException e) { // set status code 500
                _requestMap.put("ReconCode", "550");
                _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);

                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);

            } catch (Exception e) { // set status code 500
                _requestMap.put("ReconCode", "550");
                _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            try {
                responseStr = responseBuffer.toString();

                if (logsEnable)
                    ZTEINTransactionLogger.logMessage("sendRequestToIN p_action" + p_action + " 0=VAL,2=RECHARGE" + _inTXNID + " RESPONSE FROM IN IS:" + responseStr);
                if (BTSLUtil.isNullString(responseStr)) {
                    _requestMap.put("ReconCode", "550");
                    _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                    _requestMap.put("status", InterfaceErrorCodesI.AMBIGOUS);
                    _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);

                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }

                try {
                    _responseMap = _formatter.parseResponse(p_action, responseStr);
                } catch (Exception e) {
                    _requestMap.put("ReconCode", "550");
                    _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                    _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                    _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }

                try {

                    String res_tx_id = BTSLUtil.NullToString((String) _responseMap.get("IN_TRANSACTIONHEADERID"));
                    String req_tx_id = BTSLUtil.NullToString((String) _requestMap.get("TRANSACTIONHEADERID"));
                    _requestMap.put("IN_TXN_ID", res_tx_id);
                    _requestMap.put("IN_RECON_ID", res_tx_id);

                    if (!(req_tx_id.equalsIgnoreCase(res_tx_id))) {
                        try {
                            busyList.remove(socket);
                            if (socketConnection != null) {
                                socketConnection.destroy(fileCacheId);
                            }
                        } catch (Exception ex) {
                            ZTEINTransactionLogger.logMessage("Error while destroy the connection" + ex.getMessage());
                        }

                        try {
                            ZTEINTransactionLogger.logMessage("Creating new connection conecction Conecttion is null going to create the connection");
                            ZTEINSocketWrapper newSocketConnection = new ZTEINNewClientConnection().getNewClientObject(fileCacheId, _interfaceID);
                            socketConnection = newSocketConnection;
                            socket.setZteINSocketWrapper(socketConnection);
                            busyList.add(socket);
                        } catch (Exception e) {
                            socketConnection = null;
                            busyList.add(socket);
                        }
                        ZTEINTransactionLogger.logMessage("sendRequestToIN Transaction id set in the request header [" + _inTXNID + "] does not matched with the transaction id fetched from response[" + res_tx_id + " reequest" + req_tx_id + "]");
                        _requestMap.put("ReconCode", "550");
                        _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                        _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                        _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    } else {
                        if (logsEnable)
                            ZTEINTransactionLogger.logMessage("sendRequestToIN _responseMap:" + _responseMap);

                        if (_responseMap.containsKey("RETN")) {
                            _responseMap.put("responseCode", _responseMap.get("RETN").trim());
                            if (_responseMap.get("RETN").trim().equalsIgnoreCase("0000")) {

                                _requestMap.put("StatusCode", "200");
                                _requestMap.put("Status", _responseMap.get("RETN"));
                                _requestMap.put("INTERFACE_STATUS", _responseMap.get("RETN"));
                                _responseMap.put("StatusCode", "200");
                                _responseMap.put("Status", _responseMap.get("RETN"));
                            } else {
                                _requestMap.put("StatusCode", _responseMap.get("RETN").trim());
                                _requestMap.put("Status", _responseMap.get("RETN"));
                                _requestMap.put("INTERFACE_STATUS", _responseMap.get("RETN"));
                                _responseMap.put("StatusCode", _responseMap.get("RETN").trim());
                                _responseMap.put("Status", _responseMap.get("RETN"));
                            }
                        } else {
                            _requestMap.put("ReconCode", "550");
                            _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                            _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                            _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                            _responseMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                            _responseMap.put("ReconCode", "550");
                            _responseMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                            _responseMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                    }

                } catch (Exception ex) {
                    _requestMap.put("ReconCode", "550");
                    _requestMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                    _requestMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                    _requestMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);

                    _responseMap.put("StatusCode", InterfaceErrorCodesI.AMBIGOUS);
                    _responseMap.put("ReconCode", "550");
                    _responseMap.put("Status", InterfaceErrorCodesI.AMBIGOUS);
                    _responseMap.put("ErrorStage", InterfaceErrorCodesI.ERROR_RESPONSE);
                }

                String status = null;
                if (!_responseMap.containsKey("Status"))
                    status = "500";
                else
                    status = _responseMap.get("Status");

                ZTEINErrorStatus errorStatus = new ZTEINErrorStatus(); // to be

                errorStatus.ValidateErrorCode(status);

                switch (p_action) {
                case ZTEINI.ACTION_ACCOUNT_INFO: {
                    String accountState = null;
                    if (!_responseMap.containsKey("ACNTSTAT"))
                        accountState = "0";
                    else
                        accountState = _responseMap.get("ACNTSTAT");

                    errorStatus.ValidateAccountStateCode(accountState);
                    if (!"N".equals(_responseMap.get("BLACKLIST"))) {
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_BLACKLISTED);// MSISDN
                                                                                                                        // Not
                                                                                                                        // Found
                    }
                    break;
                }
                }

                _requestMap = utilObj.compareHashtable(_requestMap, _responseMap);
            } catch (BTSLBaseException be) {
                ZTEINTransactionLogger.logMessage("sendRequestToIN BTSLBaseException be:" + be.getMessage());
                if (be.getMessage() == null || be.getMessage().equalsIgnoreCase("")) {
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

                } else {
                    throw be;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ZTEINTransactionLogger.logMessage("sendRequestToIN Exception be:" + e.getMessage());
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
        } finally {
            busyList.remove(socket);
            freeList.add(socket);
            if (logsEnable) {
                ZTEINTransactionLogger.logMessage("sendRequestToIN  p_action" + p_action + " 0=VAL,2=RECHARGE" + _inTXNID + "In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
                ZTEINTransactionLogger.logMessage("Exiting the sendRequestToIN p_action" + p_action + " 0=VAL,2=RECHARGE" + _inTXNID + " Final _requestTable=" + _requestMap.toString());
            }
        }
    }// end of setLanguageFromMapping

    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * 
     * @throws BTSLBaseException
     */

    private void handleCancelTransaction(String fileCacheId) throws BTSLBaseException {
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
            _requestMap.put("REMARK1", FileCache.getValue(fileCacheId, "REMARK1"));
            _requestMap.put("REMARK2", FileCache.getValue(fileCacheId, "REMARK2"));
            // _requestMap.put("INTERFACE_STATUS",FileCache.getValue(_interfaceID,"INTERFACE_STATUS"));
            _requestMap.put("SYSTEM_STATUS_MAPPING", FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING"));
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", FileCache.getValue(fileCacheId, "CANCEL_COMMAND_STATUS_MAPPING"));
            // get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "reconLog." + reconLog);
            cancelTxnAllowed = (String) FileCache.getValue(fileCacheId, "CANCEL_TXN_ALLOWED");
            // if cancel transaction is not supported by IN, get error codes
            // from mapping present in IN fILE,write it
            // into reconciliation log and throw exception (This exception tells
            // the final status of transaction which was ambiguous) which would
            // be handled by validate, credit or debitAdjust methods
            if ("N".equals(cancelTxnAllowed)) {
                cancelNA = (String) FileCache.getValue(fileCacheId, "CANCEL_NA");// Cancel
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
}// end of class
