package com.inter.ferma;

/**
 * @(#)FermaINHandler.java
 *                         Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Abhijit Chauhan Oct 1,2005 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
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
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.InterfaceTransactionLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.inter.urlcon.FermaUrlConnection;
import com.inter.urlcon.FermaUrlConnectionPool;

public class FermaINHandler implements InterfaceHandler {
    public final static int ACTION_LOGIN = 0;
    public final static int ACTION_ACCOUNT_INFO = 1;
    public final static int ACTION_RECHARGE_BALANCE = 2;
    public final static int ACTION_BALANCE_ADJUST = 3;
    public final static int ACTION_LOGOUT = 4;
    // public final static int ACTION_TXN_CANCEL=5;
    public final static String XML_STATUS_SUCCESS = "0";
    public final static String INVALID_ACCESSTYPE = "2";
    public final static String NOT_CONNECTWITH_SDP = "3";
    public final static String NOT_PRESENT_ON_PREPAID_DB = "10";
    public final static String INVALID_USER_STATUS = "11";
    public final static String OLD_TRANSACTION_ID = "20";
    public final static String INSUFFICIENT_CREDIT_AMOUNT = "22";
    public final static String UNKNOWN_INTERFACE_ID = "401";
    public final static String BAD_REQUEST = "400";
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    // private FermaRequestResponse _formatter = null;
    private FermaRequestFormatter _formatter = null;
    private FermaUrlConnectionPool _pool = null;
    private FermaUrlConnection _fermaUrlConnection = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private String _referenceID = null;
    private String _msisdn = null;
    private FermaUrlConnection _urlConnection;
    private int _retry = 0;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    public FermaINHandler() {
        _formatter = new FermaRequestFormatter();
    }

    /**
     * Validate.This method is used to send validation request to Ferma IN
     * 
     * @param p_requestMap
     *            HashMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        _requestMap = p_requestMap;
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered " + InterfaceUtil.getPrintMap(_requestMap));
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            setInterfaceParameters();
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("AccessType", FileCache.getValue(_interfaceID, "ACCESS_TYPE"));
            _requestMap.put("BalanceId", FileCache.getValue(_interfaceID, "BALANCE_ID"));
            _requestMap.put("Stage", "VAL");
            initializeConnectionParameters(FermaRequestResponse.ACTION_ACCOUNT_INFO);
            String inStr = _formatter.generateRequest(FermaRequestResponse.ACTION_ACCOUNT_INFO, _requestMap);
            // Sending the request to IN
            sendRequestToIN(inStr, FermaRequestResponse.ACTION_ACCOUNT_INFO);
            String amountStr = (String) _responseMap.get("Amount");
            if (!InterfaceUtil.isNullString(amountStr)) {
                double multplicationFactor = Double.parseDouble(((String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR")));
                String amount = InterfaceUtil.getSystemAmountFromINAmount(amountStr, multplicationFactor);
                _requestMap.put("INTERFACE_PREV_BALANCE", amount);
            }

            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("Profile"));
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("AccountStatus"));
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("ValidityDate"), "yyyyMMdd"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("GraceDate"), "yyyyMMdd"));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            String barind = (String) _responseMap.get("LOCK_FLAG");
            // Checking the Condition for Baring and Unbar
            if (barind.equals("1")) {
                if (_log.isDebugEnabled())
                    _log.debug("validate", "Subscriber is Barred on IN LockStatus is 1" + InterfaceUtil.getPrintMap(_requestMap));
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);// The
                                                                                          // MSISDN
                                                                                          // of
                                                                                          // the
                                                                                          // request
                                                                                          // is
                                                                                          // not
                                                                                          // valid
                                                                                          // on
                                                                                          // Prepaid
                                                                                          // system.
            }
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[validate]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw e;
        } finally {
            _fermaUrlConnection.close();
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited ");
        }
    }

    /**
     * Credit (recharge balance)
     * 
     * @param p_requestMap
     *            p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered " + p_requestMap);
        _requestMap = p_requestMap;
        _referenceID = (String) _requestMap.get("TRANSACTION_ID");
        _interfaceID = (String) _requestMap.get("INTERFACE_ID");

        _msisdn = (String) _requestMap.get("MSISDN");
        _requestMap.put("AccessType", FileCache.getValue(_interfaceID, "ACCESS_TYPE"));
        String inStr = null;
        double adjAmount = 0;
        HashMap mapToReturn = null;
        boolean isCrAdjustFired = false;
        String amountStr = null;
        long amtDrCr = 0;
        try {
            setInterfaceParameters();
            double multplicationFactor = Double.parseDouble(((String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR")));
            double requestedAmount = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double minCardAmount = Double.parseDouble((String) _requestMap.get("MIN_CARD_GROUP_AMT"));
            String cardGroup = (String) _requestMap.get("CARD_GROUP");
            if (_log.isDebugEnabled())
                _log.debug("credit", "requestedAmount=" + requestedAmount + " minCardAmount=" + minCardAmount);// To
                                                                                                               // be
                                                                                                               // log
                                                                                                               // in
                                                                                                               // interface
                                                                                                               // logger
            adjAmount = requestedAmount - minCardAmount;
            mapToReturn = p_requestMap;
            if (adjAmount > 0) {
                try {
                    _requestMap = null;
                    _requestMap = new HashMap(p_requestMap);
                    _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
                    if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                        checkInterfaceB4SendingRequest();

                    _requestMap.put("INTERFACE_AMOUNT", String.valueOf((long) adjAmount));
                    // changes Start here for botswana
                    double amountDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(adjAmount, multplicationFactor);
                    amountStr = amountDouble + "";

                    String roundFlag = null;
                    roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
                    if (InterfaceUtil.isNullString(roundFlag)) {
                        roundFlag = "Y";
                    }
                    if ("Y".equals(roundFlag)) {
                        amountStr = String.valueOf(Math.round(amountDouble));
                    }

                    // ends here
                    _requestMap.put("transfer_amount", amountStr);

                    // DO DB INSERT HERE and commit
                    _inTXNID = InterfaceUtil.getINTransactionID();
                    _requestMap.put("IN_TXN_ID", _inTXNID);
                    mapToReturn.put("IN_TXN_ID1", _inTXNID);
                    _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");

                    // InterfaceUtil.insertInDatabase("BAADJCR",_requestMap);
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJCR", _requestMap, "U"));

                    initializeConnectionParameters(FermaRequestResponse.ACTION_BALANCE_ADJUST);
                    inStr = _formatter.generateRequest(FermaRequestResponse.ACTION_BALANCE_ADJUST, _requestMap);
                    isCrAdjustFired = true;
                    sendRequestToIN(inStr, FermaRequestResponse.ACTION_BALANCE_ADJUST);

                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    mapToReturn.put("INT_SET_STATUS", _requestMap.get("INT_SET_STATUS"));

                    // InterfaceUtil.updateInDatabase("BAADJCR",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJCR", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                    mapToReturn.put("UPDATE_STATUS1", InterfaceErrorCodesI.SUCCESS);

                    mapToReturn.put("PROTOCOL_STATUS", _requestMap.get("PROTOCOL_STATUS"));

                    mapToReturn.put("ADJUST_AMOUNT", String.valueOf((long) adjAmount));

                    mapToReturn.put("DATABASE_ENTRY_REQD", "N");
                    // Update the status in database also set the status codes
                    // on return string
                } catch (BTSLBaseException be) {
                    String timeAmbiguousCase = InterfaceUtil.getTimeWhenAmbiguousCaseOccured();
                    mapToReturn.put("UPDATE_STATUS1", _requestMap.get("INTERFACE_STATUS"));
                    mapToReturn.put("INTERFACE_STATUS", _requestMap.get("INTERFACE_STATUS"));
                    mapToReturn.put("PROTOCOL_STATUS", _requestMap.get("PROTOCOL_STATUS"));
                    _requestMap.put("TRANSACTION_STATUS", be.getMessage());
                    mapToReturn.put("DATABASE_ENTRY_REQD", "N");

                    // InterfaceUtil.updateInDatabase("BAADJCR",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJCR", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                    be.printStackTrace();
                    _log.error("credit", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while adjAmount");
                    _requestMap.put("AMBGUOUS_TIME", timeAmbiguousCase);
                    mapToReturn.put("AMBGUOUS_TIME", timeAmbiguousCase);
                    _log.error("credit", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap) + "mapToReturn :" + InterfaceUtil.getPrintMap(mapToReturn));
                    if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                        throw be;
                    try {
                        // _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
                        if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                            _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, mapToReturn);
                        _requestMap.put("TRANSACTION_TYPE", "CR");
                        mapToReturn.put("TRANSACTION_TYPE", "CR");
                        handleCancelTransaction();
                    } catch (BTSLBaseException bte) {
                        throw bte;
                    } catch (Exception e) {
                        e.printStackTrace();
                        _log.error("credit", "Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in of adjAmount in credit method");
                        throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                } catch (Exception e) {
                    mapToReturn.put("UPDATE_STATUS1", _requestMap.get("INTERFACE_STATUS"));
                    mapToReturn.put("PROTOCOL_STATUS", _requestMap.get("PROTOCOL_STATUS"));
                    mapToReturn.put("INTERFACE_STATUS", _requestMap.get("INTERFACE_STATUS"));
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                    mapToReturn.put("DATABASE_ENTRY_REQD", "N");

                    // InterfaceUtil.updateInDatabase("BAADJCR",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJCR", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                    e.printStackTrace();
                    _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while adjAmount");
                    throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                }
            }
            if (cardGroup != null) {
                try {
                    _requestMap = null;
                    _requestMap = new HashMap(p_requestMap);
                    _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
                    if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                        checkInterfaceB4SendingRequest();
                    // _requestMap.put("INTERFACE_AMOUNT",String.valueOf(requestedAmount-minCardAmount));
                    _requestMap.put("INTERFACE_AMOUNT", String.valueOf(minCardAmount));
                    // DO DB INSERT HERE and commit
                    if (_log.isDebugEnabled())
                        _log.debug("credit", "Before getting IN txn ID for credit");
                    _inTXNID = InterfaceUtil.getINTransactionID();
                    _requestMap.put("IN_TXN_ID", _inTXNID);
                    mapToReturn.put("IN_TXN_ID", _inTXNID);

                    // InterfaceUtil.insertInDatabase("RCH",_requestMap);
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("RCH", _requestMap, "U"));

                    mapToReturn.put("DATABASE_ENTRY_REQD", "N");// added on
                                                                // 17/01/06 to
                                                                // avoid extra
                                                                // entry done by
                                                                // Interface
                                                                // Module
                    _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
                    initializeConnectionParameters(FermaRequestResponse.ACTION_RECHARGE_BALANCE);
                    inStr = _formatter.generateRequest(FermaRequestResponse.ACTION_RECHARGE_BALANCE, _requestMap);
                    if (_log.isDebugEnabled())
                        _log.debug("credit", "Recharge inStr=" + inStr);

                    sendRequestToIN(inStr, FermaRequestResponse.ACTION_RECHARGE_BALANCE);
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    mapToReturn.put("INT_SET_STATUS", _requestMap.get("INT_SET_STATUS"));

                    // Update the status in database also set the status codes
                    // on return string
                    mapToReturn.put("UPDATE_STATUS", InterfaceErrorCodesI.SUCCESS);
                    mapToReturn.put("PROTOCOL_STATUS", _requestMap.get("PROTOCOL_STATUS"));

                    // changes done here for botswana
                    mapToReturn.put("INTERFACE_POST_BALANCE", _requestMap.get("INTERFACE_POST_BALANCE"));

                    mapToReturn.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("ValidityDate"), "yyyyMMdd"));
                    mapToReturn.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("GraceDate"), "yyyyMMdd"));

                    // InterfaceUtil.updateInDatabase("RCH",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("RCH", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));
                } catch (BTSLBaseException be) {
                    String timeAmbiguousCase = InterfaceUtil.getTimeWhenAmbiguousCaseOccured();
                    mapToReturn.put("UPDATE_STATUS", _requestMap.get("INTERFACE_STATUS"));
                    mapToReturn.put("INTERFACE_STATUS", _requestMap.get("INTERFACE_STATUS"));
                    mapToReturn.put("PROTOCOL_STATUS", _requestMap.get("PROTOCOL_STATUS"));
                    if (be.isKey())
                        _requestMap.put("TRANSACTION_STATUS", be.getMessageKey());
                    else
                        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

                    // InterfaceUtil.updateInDatabase("RCH",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("RCH", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                    _requestMap.put("AMBGUOUS_TIME", timeAmbiguousCase);
                    mapToReturn.put("AMBGUOUS_TIME", timeAmbiguousCase);
                    _log.error("credit", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap) + "mapToReturn :" + InterfaceUtil.getPrintMap(mapToReturn));
                    if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                        try {
                            // _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
                            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, mapToReturn);

                            _requestMap.put("TRANSACTION_TYPE", "CR");
                            mapToReturn.put("TRANSACTION_TYPE", "CR");
                            handleCancelTransaction();
                        } catch (BTSLBaseException bbe) {
                            if (!isCrAdjustFired)
                                throw bbe;
                        } catch (Exception e) {
                            e.printStackTrace();
                            _log.error("credit", "Exception e:" + e.getMessage());
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                            if (!isCrAdjustFired)
                                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                    }

                    if (isCrAdjustFired) {
                        try {
                            _requestMap = null;
                            _requestMap = new HashMap(p_requestMap);
                            _requestMap.put("INTERFACE_AMOUNT", String.valueOf((long) adjAmount));

                            // DO DB INSERT HERE and commit
                            _inTXNID = InterfaceUtil.getINTransactionID();
                            _requestMap.put("IN_TXN_ID", _inTXNID);
                            mapToReturn.put("IN_TXN_ID2", _inTXNID);

                            // OBO changes start
                            double amountDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(adjAmount, multplicationFactor);
                            amountStr = amountDouble + "";

                            String roundFlag = null;
                            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
                            if (InterfaceUtil.isNullString(roundFlag)) {
                                roundFlag = "Y";
                            }
                            if ("Y".equals(roundFlag)) {
                                amountStr = String.valueOf(Math.round(amountDouble));
                            }
                            // OBO changes ends

                            _requestMap.put("transfer_amount", "-" + amountStr);

                            // InterfaceUtil.insertInDatabase("BAADJDR",_requestMap);
                            InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJDR", _requestMap, "U"));

                            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
                            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                                checkInterfaceB4SendingRequest();

                            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
                            initializeConnectionParameters(FermaRequestResponse.ACTION_BALANCE_ADJUST);
                            inStr = _formatter.generateRequest(FermaRequestResponse.ACTION_BALANCE_ADJUST, _requestMap);
                            sendRequestToIN(inStr, FermaRequestResponse.ACTION_BALANCE_ADJUST);
                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                            mapToReturn.put("INT_SET_STATUS", _requestMap.get("INT_SET_STATUS"));

                            // InterfaceUtil.updateInDatabase("BAADJDR",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                            InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJDR", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                            // Update the status in database also set the status
                            // codes on return string
                            mapToReturn.put("UPDATE_STATUS2", InterfaceErrorCodesI.SUCCESS);
                        } catch (BTSLBaseException bbe) {
                            timeAmbiguousCase = InterfaceUtil.getTimeWhenAmbiguousCaseOccured();
                            if (!(bbe.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                                throw bbe;
                            _requestMap.put("AMBGUOUS_TIME", timeAmbiguousCase);
                            mapToReturn.put("AMBGUOUS_TIME", timeAmbiguousCase);
                            _log.error("credit", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap) + "mapToReturn :" + InterfaceUtil.getPrintMap(mapToReturn));
                            try {
                                // _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
                                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, mapToReturn);

                                _requestMap.put("TRANSACTION_TYPE", "CR");
                                mapToReturn.put("TRANSACTION_TYPE", "CR");
                                handleCancelTransaction();
                            } catch (BTSLBaseException bte) {
                                throw bte;
                            } catch (Exception e) {
                                e.printStackTrace();
                                _log.error("credit", "Exception e:" + e.getMessage());
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in of debit adjAmount in credit method");
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }
                        } catch (Exception ex) {
                            mapToReturn.put("UPDATE_STATUS2", _requestMap.get("INTERFACE_STATUS"));
                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

                            // InterfaceUtil.updateInDatabase("BAADJDR",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                            InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJDR", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                            ex.printStackTrace();
                            _log.error("credit", "Exception ex:" + ex.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while isCrAdjustFired");
                            // Mark Ambigous
                            // throw new
                            // BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                        }
                    }
                    throw be;
                } catch (Exception e) {
                    mapToReturn.put("UPDATE_STATUS", _requestMap.get("INTERFACE_STATUS"));
                    mapToReturn.put("PROTOCOL_STATUS", _requestMap.get("PROTOCOL_STATUS"));
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

                    // InterfaceUtil.updateInDatabase("RCH",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                    InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("RCH", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                    if (isCrAdjustFired) {
                        try {
                            _requestMap = null;
                            _requestMap = new HashMap(p_requestMap);
                            _requestMap.put("INTERFACE_AMOUNT", String.valueOf((long) adjAmount));

                            // DO DB INSERT HERE and commit
                            _inTXNID = InterfaceUtil.getINTransactionID();
                            _requestMap.put("IN_TXN_ID", _inTXNID);
                            mapToReturn.put("IN_TXN_ID2", _inTXNID);

                            // OBO changes start here
                            double amountDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(adjAmount, multplicationFactor);
                            amountStr = amountDouble + "";

                            String roundFlag = null;
                            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
                            if (InterfaceUtil.isNullString(roundFlag)) {
                                roundFlag = "Y";
                            }
                            if ("Y".equals(roundFlag)) {
                                amountStr = String.valueOf(Math.round(amountDouble));
                            }
                            // obo changes ends here

                            _requestMap.put("transfer_amount", "-" + amountStr);

                            // InterfaceUtil.insertInDatabase("BAADJDR",p_requestMap);
                            InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJDR", p_requestMap, "U"));

                            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                                checkInterfaceB4SendingRequest();
                            initializeConnectionParameters(FermaRequestResponse.ACTION_BALANCE_ADJUST);
                            inStr = _formatter.generateRequest(FermaRequestResponse.ACTION_BALANCE_ADJUST, _requestMap);
                            sendRequestToIN(inStr, FermaRequestResponse.ACTION_BALANCE_ADJUST);

                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                            mapToReturn.put("INT_SET_STATUS", _requestMap.get("INT_SET_STATUS"));

                            // InterfaceUtil.updateInDatabase("BAADJDR",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                            InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJDR", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                            // Update the status in database also set the status
                            // codes on return string
                            mapToReturn.put("UPDATE_STATUS2", InterfaceErrorCodesI.SUCCESS);
                            mapToReturn.put("PROTOCOL_STATUS", _requestMap.get("PROTOCOL_STATUS"));
                        } catch (BTSLBaseException bbe) {
                            String timeAmbiguousCase = InterfaceUtil.getTimeWhenAmbiguousCaseOccured();
                            if (!(bbe.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                                throw bbe;
                            _requestMap.put("AMBGUOUS_TIME", timeAmbiguousCase);
                            mapToReturn.put("AMBGUOUS_TIME", timeAmbiguousCase);
                            _log.error("credit", "BTSLBaseException be:" + bbe.getMessage() + InterfaceUtil.getPrintMap(_requestMap) + "mapToReturn :" + InterfaceUtil.getPrintMap(mapToReturn));
                            try {
                                // _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
                                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, mapToReturn);
                                _requestMap.put("TRANSACTION_TYPE", "CR");
                                mapToReturn.put("TRANSACTION_TYPE", "CR");
                                handleCancelTransaction();
                            } catch (BTSLBaseException bte) {
                                throw bte;
                            } catch (Exception ex) {
                                e.printStackTrace();
                                _log.error("credit", "Exception e:" + ex.getMessage());
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }
                        } catch (Exception ex) {
                            mapToReturn.put("UPDATE_STATUS2", _requestMap.get("INTERFACE_STATUS"));
                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

                            // InterfaceUtil.updateInDatabase("BAADJDR",_requestMap,(String)_requestMap.get("INTERFACE_STATUS"));
                            InterfaceTransactionLog.log(InterfaceUtil.getVOFromMap("BAADJDR", _requestMap, (String) _requestMap.get("INTERFACE_STATUS")));

                            ex.printStackTrace();
                            _log.error("credit", "Exception ex:" + ex.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while isCrAdjustFired");
                            // Mark Ambigous
                            // throw new
                            // BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                        }
                    }
                    throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                }
            }// if(cardgroup!=null) condition
        } catch (BTSLBaseException be) {
            // be.printStackTrace();
            _log.error("credit", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[credit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
        } finally {
            _requestMap.put("TRANSACTION_STATUS", "");
            p_requestMap = null;
            p_requestMap = mapToReturn;
            if (_fermaUrlConnection != null)
                _fermaUrlConnection.close();
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited " + p_requestMap);
        }
    }

    /**
     * Credit Adjust
     * 
     * @param p_requestMap
     *            p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered " + p_requestMap);
        try {
            _requestMap = p_requestMap;
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            setInterfaceParameters();
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();

            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("AccessType", FileCache.getValue(_interfaceID, "ACCESS_TYPE"));
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");

            // obo change starts
            double amountDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double multplicaionFactor = Double.parseDouble(((String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR")));

            double amountBalance = InterfaceUtil.getINAmountFromSystemAmountToIN(amountDouble, multplicaionFactor);
            String roundFlag = null;
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "Y";
            }
            String amountBalanceStr = null;
            if ("Y".equals(roundFlag)) {
                amountBalanceStr = String.valueOf(Math.round(amountBalance));
            }

            // obo change ends

            _requestMap.put("transfer_amount", amountBalanceStr);
            String inStr = null;
            initializeConnectionParameters(FermaRequestResponse.ACTION_BALANCE_ADJUST);
            inStr = _formatter.generateRequest(FermaRequestResponse.ACTION_BALANCE_ADJUST, _requestMap);
            sendRequestToIN(inStr, FermaRequestResponse.ACTION_BALANCE_ADJUST);
            p_requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("ValidityDate"), "yyyyMMdd"));
            p_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("GraceDate"), "yyyyMMdd"));

        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                _requestMap.put("TRANSACTION_TYPE", "CR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + ex.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[creditAdjust]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust");
            throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
        } finally {
            _fermaUrlConnection.close();
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited " + _requestMap);
        }
    }

    /**
     * Debit Adjust
     * 
     * @param p_requestMap
     *            p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered " + p_requestMap);
        try {
            _requestMap = p_requestMap;
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            setInterfaceParameters();
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("AccessType", FileCache.getValue(_interfaceID, "ACCESS_TYPE"));
            initializeConnectionParameters(FermaRequestResponse.ACTION_BALANCE_ADJUST);
            String inStr = null;
            int amount = 0;
            if (!BTSLUtil.isNullString((String) _requestMap.get("INTERFACE_AMOUNT"))) {
                amount = amount - Integer.parseInt((String) _requestMap.get("INTERFACE_AMOUNT"));
                _requestMap.put("INTERFACE_AMOUNT", amount + "");

                // obo change starts
                double amountDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                double multplicaionFactor = Double.parseDouble(((String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR")));

                double amountBalance = InterfaceUtil.getINAmountFromSystemAmountToIN(amountDouble, multplicaionFactor);
                String roundFlag = null;
                roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                }
                String amountBalanceStr = null;
                if ("Y".equals(roundFlag)) {
                    amountBalanceStr = String.valueOf(Math.round(amountBalance));
                }

                // obo change ends

                _requestMap.put("transfer_amount", amountBalanceStr);
            }
            inStr = _formatter.generateRequest(FermaRequestResponse.ACTION_BALANCE_ADJUST, _requestMap);
            sendRequestToIN(inStr, FermaRequestResponse.ACTION_BALANCE_ADJUST);

        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                _requestMap.put("TRANSACTION_TYPE", "CR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + ex.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[debitAdjust]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while debitAdjust");
            throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
        } finally {
            _fermaUrlConnection.close();
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited  " + _requestMap);
        }
    }

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
     * Send Request To IN
     * 
     * @param p_inRequestStr
     * @param p_action
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_inRequestStr + " action:" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
        String responseStr = null;
        long startTime = 0;
        try {
            _responseMap = new HashMap();
            PrintWriter out = null;
            int retry = 0;
            // In creditAdjust (sender credit back )don't check interface
            // status, simply send the request to IN.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }
            out = _fermaUrlConnection.getPrintWriter();
            try {
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Exception while sending request to Ferma IN :" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }
            try {
                String httpStatus = null;
                long endTime = 0;
                try {
                    httpStatus = _fermaUrlConnection.getResponseCode();
                    endTime = System.currentTimeMillis();
                    String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                    if (!InterfaceUtil.isNullString(warnTimeStr)) {
                        long warnTime = Long.parseLong(warnTimeStr);
                        if (endTime - startTime > warnTime) {
                            _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Ferma IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                        }
                    }
                    _requestMap.put("PROTOCOL_STATUS", httpStatus);
                    if ("200".equals(httpStatus)) {
                        _fermaUrlConnection.setBufferedReader();
                        StringBuffer sbf = new StringBuffer(1000);
                        BufferedReader in = _fermaUrlConnection.getBufferedReader();
                        while ((responseStr = in.readLine()) != null) {
                            sbf.append(responseStr);
                        }
                        responseStr = sbf.toString();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception while getting response form Ferma IN : " + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while getting response from Ferma IN e: " + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
                }
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
                // handling bad request and unknown interface ID ( for http
                // status=400 , 401)
                if (InterfaceUtil.isNullString(responseStr)) {

                    _log.error("sendRequestToIN", " response form interface is null  HTTP STATUS=" + httpStatus + "   Request=" + p_inRequestStr);
                    if (BAD_REQUEST.equals(httpStatus)) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, "  ", EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Bad Request Response Http Status : " + httpStatus);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    } else if (UNKNOWN_INTERFACE_ID.equals(httpStatus)) {
                        _log.info("sendRequestToIN", "UNKNOWN_INTERFACE_ID from Ferma IN");
                        /**
                         * Load the interface ID again after fire the login
                         * request
                         */
                        _fermaUrlConnection.close();
                        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_GETCONN, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Getting Connection", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "UNKNOWN_INTERFACE_ID Trying to reconnect. action=" + p_action);
                        reconnect();
                        if (_retry < 1) {
                            _retry++;
                            _log.info("sendRequestToIN", "Retrying to send request again due to UNKNOWN_INTERFACE_ID _retry: " + _retry);
                            sendRequestToIN(_formatter.replaceInterfaceID(p_inRequestStr, (String) _requestMap.get("FERMA_INTERFACE_ID")), p_action);
                        } else {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, "  ", EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Unknown Interface ID  Http Status : " + httpStatus);
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_UNKNOWN_INTERFACE_ID);
                        }
                    } else {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, "  ", EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Unknown Http Status : " + httpStatus);
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        // commented code may be used in future to support on
                        // line cancel request
                        /*
                         * if(ACTION_TXN_CANCEL == p_stage)
                         * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI
                         * .AMBIGOUS);
                         */
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                    }
                } else {
                    // _log.info("sendRequestToIN","responseStr="+responseStr);
                    _responseMap = _formatter.parseResponse(p_action, responseStr);
                    String status = (String) _responseMap.get("Status");

                    String amount = (String) _responseMap.get("Amount");
                    double multplicationFactor = Double.parseDouble(((String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR")));
                    String interfacePostBalance = InterfaceUtil.getSystemAmountFromINAmount(amount, multplicationFactor);

                    _requestMap.put("INTERFACE_STATUS", status);

                    if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                        if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                            _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                        _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
                    }

                    // commented code may be used in future to support on line
                    // cancel request
                    // If response of cancel request is Successful, then throw
                    // exception mapped in IN FILE
                    /*
                     * if(ACTION_TXN_CANCEL == p_stage)
                     * {
                     * _requestMap.put("CANCEL_RESP_STATUS",result);
                     * cancelTxnStatus =
                     * InterfaceUtil.getErrorCodeFromMapping(_requestMap
                     * ,result,"SYSTEM_STATUS_MAPPING");
                     * throw new BTSLBaseException(cancelTxnStatus);
                     * }
                     */

                    String inReconID = (String) _requestMap.get("IN_RECON_ID");
                    if (inReconID == null)
                        inReconID = _inTXNID;
                    // validates request/response transaction ID
                    if (!((String) _responseMap.get("TransactionId")).equals(inReconID)) {
                        _log.info("sendRequestToIN", "inReconID:" + inReconID + " current TransId=" + (String) _responseMap.get("TransactionId") + " Mismatch");
                        // send alert message(TO BE IMPLEMENTED)
                        throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);
                    } else {
                        _requestMap.put("INTERFACE_STATUS", status);
                        _requestMap.put("INTERFACE_POST_BALANCE", interfacePostBalance);

                        if (status.equals(XML_STATUS_SUCCESS))
                            return;
                        else if (status.equals(INVALID_ACCESSTYPE)) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Invalid Accesstype");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// invalid
                                                                                                           // access
                                                                                                           // type
                        } else if (status.equals(NOT_CONNECTWITH_SDP)) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Gateway not connected with SDP");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_SDP_CONNECTION_PROBLEM);// no
                                                                                                           // connection
                                                                                                           // with
                                                                                                           // SDP(Service
                                                                                                           // data
                                                                                                           // record)
                        } else if (status.equals(NOT_PRESENT_ON_PREPAID_DB)) {
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);// User
                                                                                                         // not
                                                                                                         // present
                                                                                                         // in
                                                                                                         // prepaid
                                                                                                         // database
                        } else if (status.equals(INVALID_USER_STATUS)) {
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_USER_STATUS);// invalid
                                                                                                        // user
                                                                                                        // status
                                                                                                        // and
                                                                                                        // invalid
                                                                                                        // recharge
                                                                                                        // value
                                                                                                        // code
                        } else if (status.equals(OLD_TRANSACTION_ID)) // recharge
                                                                      // with
                                                                      // old
                                                                      // transaction
                                                                      // id
                        {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Transaction ID mismatch");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// recharge
                                                                                                           // request
                                                                                                           // with
                                                                                                           // old
                                                                                                           // transaction
                                                                                                           // id
                        } else if (status.equals(INSUFFICIENT_CREDIT_AMOUNT)) {
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INSUFFCIENT_CREDIT);// insufficient
                                                                                                       // user
                                                                                                       // credit
                        } else {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error Response status: " + status);
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                    }
                }
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            } finally {

            } // End of Finally
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting action:" + p_action + " responseStr:" + responseStr);
        }
    } // End of equestIN method

    /**
     * Initialize Connection Parameters.This method is used for initializing
     * Ferma Connection Parameters
     * 
     * @throws Exception
     */
    private void initializeConnectionParameters(int p_action) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("initializeConnectionParameters", "Entered Interface ID " + _interfaceID + " _requestMap: " + _requestMap + " p_action=" + p_action);
        FermaINScheduler fermaINScheduler = (FermaINScheduler) FermaConnectionServlet._schedulerMap.get(_interfaceID);
        FermaConnectionManager fermaConnectionManager = (FermaConnectionManager) FermaConnectionServlet._managerMap.get(_interfaceID);
        String fermaInterfaceID = fermaConnectionManager.getFermaInterfaceID();
        int contimeout = 0, readtimeout = 0;
        String keepAlive = "";
        keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
        String urlStr = null;
        int urlID = 0;
        contimeout = Integer.parseInt(FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT"));
        try {
            if (FermaRequestResponse.ACTION_ACCOUNT_INFO == p_action) {
                String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_VAL");
                if (readTimeOutStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[initializeConnectionParameters]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Read time out VAL is not defined in INFile");
                    throw new BTSLBaseException(this, "initializeConnectionParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                readtimeout = Integer.parseInt(readTimeOutStr);

                if (_log.isDebugEnabled())
                    _log.debug("initializeConnectionParameters", " READ TIMEOUT VAL " + readtimeout);
            } else {
                String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_TOP");
                if (readTimeOutStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[initializeConnectionParameters]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Read time out TOP is not defined in INFile");
                    throw new BTSLBaseException(this, "initializeConnectionParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                readtimeout = Integer.parseInt(readTimeOutStr);
                if (_log.isDebugEnabled())
                    _log.debug("initializeConnectionParameters", " READ TIMEOUT TOP " + readtimeout);
            }// end of read timeout setting

            urlStr = fermaINScheduler.getUrlStr();
            urlID = fermaINScheduler.getUrlID();
            _fermaUrlConnection = new FermaUrlConnection(urlStr, contimeout, readtimeout, keepAlive);
            _fermaUrlConnection.setPrintWriter();
            if (_log.isDebugEnabled())
                _log.debug("initializeConnectionParameters", "Interface ID : " + fermaInterfaceID + " urlStr: " + urlStr + " urlID: " + urlID + " keepAlive: " + keepAlive);
            _requestMap.put("FERMA_INTERFACE_ID", fermaInterfaceID);
            _requestMap.put("URL_ID", String.valueOf(urlID));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("initializeConnectionParameters", "Exception e:" + e.getMessage() + " urlID: " + urlID);
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[initializeConnectionParameters]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Connection not established with _interfaceID: " + _interfaceID + " and URL : " + fermaINScheduler.getUrlStr() + " Exception: " + e.getMessage());
            int newUrlID = fermaINScheduler.getUrlID();
            if (newUrlID == urlID)
                fermaINScheduler.replaceUrl();
            String newUrlStr = fermaINScheduler.getUrlStr();
            try {
                _fermaUrlConnection = new FermaUrlConnection(newUrlStr, contimeout, readtimeout, keepAlive);
                _fermaUrlConnection.setPrintWriter();
                if (_log.isDebugEnabled())
                    _log.debug("initializeConnectionParameters", "Inside catch of Exception Interface ID " + fermaInterfaceID + " newUrlStr: " + newUrlStr + "newUrlID : " + newUrlID + " keepAlive: " + keepAlive);
                _requestMap.put("FERMA_INTERFACE_ID", fermaInterfaceID);
                _requestMap.put("URL_ID", String.valueOf(newUrlID));
            } catch (Exception ex) {
                e.printStackTrace();
                _log.error("initializeConnectionParameters", "Inside Exception Exception ex:" + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[initializeConnectionParameters]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Connection not established after switchover with _interfaceID: " + _interfaceID + " and URL : " + fermaINScheduler.getUrlStr() + " Exception: " + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("initializeConnectionParameters", "Exited");
        }
    }

    /**
     * Reconnect.This method is used for reconnect to Ferma IN if ineterface ID
     * becomes invalid.
     * 
     * @throws Exception
     */
    private void reconnect() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("reconnect", "Entered Interface ID " + _interfaceID + " _requestMap: " + _requestMap);
        FermaINScheduler fermaINScheduler = (FermaINScheduler) FermaConnectionServlet._schedulerMap.get(_interfaceID);
        FermaConnectionManager fermaConnectionManager = (FermaConnectionManager) FermaConnectionServlet._managerMap.get(_interfaceID);
        String fermaInterfaceID = fermaConnectionManager.getFermaInterfaceID();
        if (_log.isDebugEnabled())
            _log.debug("reconnect", "Old Interface ID : " + fermaInterfaceID);
        int contimeout = 0, readtimeout = 0;
        String keepAlive = "";
        // try{fermaConnectionManager.logout(fermaINScheduler.getUrlStr());}catch(Exception
        // e){}
        try {
            _fermaUrlConnection.close();
        } catch (Exception e) {
        }
        contimeout = Integer.parseInt(FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT"));
        readtimeout = Integer.parseInt(FileCache.getValue(_interfaceID, "READ_TIMEOUT"));
        keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
        String urlStr = null;
        try {
            urlStr = fermaINScheduler.getUrlStr();
            int urlID = fermaINScheduler.getUrlID();
            fermaConnectionManager.login(urlStr);
            _fermaUrlConnection = new FermaUrlConnection(urlStr, contimeout, readtimeout, keepAlive);
            _fermaUrlConnection.setPrintWriter();
            fermaInterfaceID = fermaConnectionManager.getFermaInterfaceID();
            if (_log.isDebugEnabled())
                _log.debug("reconnect", "New Interface ID : " + fermaInterfaceID + " urlStr: " + urlStr + " keepAlive=" + keepAlive);
            _requestMap.put("FERMA_INTERFACE_ID", fermaInterfaceID);
            _requestMap.put("URL_ID", String.valueOf(urlID));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("reconnect", "urlStr : " + urlStr + " Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[reconnect]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Connection not established during reconnect login with _interfaceID: " + _interfaceID + " urlStr: " + urlStr + " keepAlive=" + keepAlive + " Exception: " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("reconnect", "Exited");
    }

    /**
     * This method is used to set the interface parameters into request map.
     * 
     * @param String
     *            p_interfaceID
     * @throws BTSLBaseException
     *             ,Exception
     */

    public void setInterfaceParameters() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered _interfaceID=" + _interfaceID);

        try {
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FermaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap" + _requestMap);
        }// end of finally
    }// end setInterfaceParameters

    /**
     * Method to Check interface status before sending request.
     * 
     * @throws BTSLBaseException
     */
    private void checkInterfaceB4SendingRequest() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkInterfaceB4SendingRequest", "Entered");

        try {
            _interfaceCloserVO = (InterfaceCloserVO) InterfaceCloserController._interfaceCloserVOTable.get(_interfaceID);
            _interfaceLiveStatus = (String) _requestMap.get("INT_ST_TYPE");
            _interfaceCloserVO.setControllerIntStatus(_interfaceLiveStatus);
            _interfaceCloser = _interfaceCloserVO.getInterfaceCloser();

            // Get AUTO_RESUME_SUPPORT property from IN FILE. If it is not
            // defined then set it as 'N'.
            String autoResumeSupported = FileCache.getValue(_interfaceID, "AUTO_RESUME_SUPPORT");
            if (InterfaceUtil.isNullString(autoResumeSupported)) {
                autoResumeSupported = "N";
                _log.error("checkInterfaceB4SendingRequest", "Value of AUTO_RESUME_SUPPORT is not defined in the INFile");
            }

            // If Controller sends 'A' and interface status is suspended, expiry
            // is checked.
            // If Controller sends 'M', request is forwarded to IN after
            // resetting counters.
            if (InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(_interfaceLiveStatus) && _interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND)) {
                // Check if Auto Resume is supported by IN or not.If not then
                // throw exception. request would not be sent to IN.
                if ("N".equals(autoResumeSupported)) {
                    _log.error("checkInterfaceB4SendingRequest", "Interface Suspended.");
                    throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
                }
                // If "Auto Resume" is supported then only check the expiry of
                // interface, if expired then only request would be sent to IN
                // otherwise checkExpiry method throws exception
                if (_isSameRequest)
                    _interfaceCloser.checkExpiryWithoutExpiryFlag(_interfaceCloserVO);
                else
                    _interfaceCloser.checkExpiry(_interfaceCloserVO);
            }
            // this block is executed when Interface is manually resumed
            // (Controller sends 'M')from suspend state
            else if (InterfaceCloserI.INTERFACE_MANNUAL_ACTIVE.equals(_interfaceCloserVO.getControllerIntStatus()) && _interfaceCloserVO.getFirstSuspendAt() != 0)
                _interfaceCloser.resetCounters(_interfaceCloserVO, null);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkInterfaceB4SendingRequest", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkInterfaceB4SendingRequest", "Exited");
        }
    }

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
        String systemStatusMapping = null;
        // int cancelRetryCount=0;
        try {
            _requestMap.put("REMARK1", FileCache.getValue(_interfaceID, "REMARK1"));
            _requestMap.put("REMARK2", FileCache.getValue(_interfaceID, "REMARK2"));
            // get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "reconLog." + reconLog);
            cancelTxnAllowed = (String) _requestMap.get("CANCEL_TXN_ALLOWED");
            // if cancel transaction is not supported by IN, get error codes
            // from mapping present in IN fILE,write it
            // into recon log and throw exception (This exception tells the
            // final status of transaction which was ambiguous) which would be
            // handled by validate, credit or debitAdjust methods
            if ("N".equals(cancelTxnAllowed)) {
                cancelNA = (String) _requestMap.get("CANCEL_NA");// Cancel
                                                                 // command
                                                                 // status as
                                                                 // NA.
                cancelCommandStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, cancelNA, "CANCEL_COMMAND_STATUS_MAPPING");
                _requestMap.put("MAPPED_CANCEL_STATUS", cancelCommandStatus);
                interfaceStatus = (String) _requestMap.get("INTERFACE_STATUS");
                systemStatusMapping = (String) _requestMap.get("SYSTEM_STATUS_MAPPING");
                cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, interfaceStatus, systemStatusMapping); // PreTUPs
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
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                // added to discard amount field from the message.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }

            // This block is currently not in use. may be used in future to
            // support on line cancel request
            /*
             * try
             * {
             * cancelRetryCount =
             * Integer.parseInt(FileCache.getValue(_interfaceID
             * ,"CNCL_RETRY_CNT"));
             * if(InterfaceCloserI.INTERFACE_SUSPEND.equals(_interfaceCloserVO.
             * getInterfaceStatus()))
             * {
             * _log.error("handleCancelTransaction","Interface Suspended.");
             * throw new
             * BTSLBaseException(this,"handleCancelTransaction",InterfaceErrorCodesI
             * .INTERFACE_SUSPENDED);
             * }
             * //before sending request check the interface status. Depending on
             * status request would be sent to IN.
             * //(Actually this method throws an exception, if
             * INTERFACE_SUSPENDED exception is thrown, request would not be
             * sent to IN else would be sent to IN.)
             * //mapping of error code corresponding to INTERFACE_SUSPENDED is
             * present in the IN File.
             * //mapped error code will be picked from IN File and thrown.
             * checkInterfaceB4SendingRequest();
             * String inStr = _formatter.generateRequest(ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,ACTION_TXN_CANCEL);
             * }
             * catch(BTSLBaseException bte)
             * {
             * if(bte.getMessage().trim().equals(InterfaceErrorCodesI.AMBIGOUS))
             * _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,
             * _requestMap);
             * cancelCommandStatus=(String)
             * _requestMap.get("CANCEL_RESP_STATUS");//this will be null if
             * unable to create connection
             * 
             * if(bte.getMessage().trim().equals(InterfaceErrorCodesI.
             * INTERFACE_SUSPENDED))
             * {
             * cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap
             * ,InterfaceErrorCodesI.INTERFACE_SUSPENDED,
             * "CANCEL_COMMAND_STATUS_MAPPING");
             * cancelTxnStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap,
             * InterfaceErrorCodesI
             * .INTERFACE_SUSPENDED,"SYSTEM_STATUS_MAPPING");
             * }
             * else
             * if(bte.getMessage().trim().equals(InterfaceErrorCodesI.AMBIGOUS)
             * || cancelCommandStatus==null)
             * {
             * cancelCommandStatus =
             * getErrorCodeFromMapping(InterfaceErrorCodesI
             * .AMBIGOUS,"CANCEL_COMMAND_STATUS_MAPPING");
             * cancelTxnStatus =
             * getErrorCodeFromMapping(InterfaceErrorCodesI.AMBIGOUS
             * ,"SYSTEM_STATUS_MAPPING");
             * 
             * cancelCommandStatus =InterfaceErrorCodesI.AMBIGOUS;
             * cancelTxnStatus = InterfaceErrorCodesI.AMBIGOUS;
             * }
             * else
             * {
             * cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap
             * ,cancelCommandStatus,"CANCEL_COMMAND_STATUS_MAPPING");
             * cancelTxnStatus=bte.getMessage().trim();
             * }
             * _requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);
             * _requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
             * throw new
             * BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus
             * ); ////Based on the value of SYSTEM_STATUS mark the transaction
             * as FAIL or AMBIGUOUS to the system.(//should these be put in
             * error log also. ??????)
             * }
             * finally
             * {
             * reconciliationLogStr =
             * ReconcialiationLog.getReconciliationLogFormat(_requestMap);
             * reconLog.info("",reconciliationLogStr);
             * }
             */
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
}
