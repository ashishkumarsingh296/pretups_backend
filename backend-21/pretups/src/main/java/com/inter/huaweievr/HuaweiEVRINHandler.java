package com.inter.huaweievr;

/**
 * @(#)HuaweiEVRINHandler.java
 *                             Copyright(c) 2007, Bharti Telesoft Int. Public
 *                             Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Vinay Kumar Singh December 10, 2007 Initial
 *                             Creation
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 *                             This class is the Handler class for the HuaweiEVR
 *                             interface.
 */
import java.io.BufferedReader;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
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
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;

public class HuaweiEVRINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(HuaweiEVRINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private HuaweiEVRRequestFormatter _formatter = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;
    private static String DB_CONN_FAILED = "00000";

    // Constructor
    public HuaweiEVRINHandler() {
        _formatter = new HuaweiEVRRequestFormatter();
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
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetch value of VALIDATION key from IN File. (this is used to
            // ensure that validation will be done on IN or not)
            // If validation of subscriber is not required set the SUCCESS code
            // into request map and return.
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            // String validateRequired="Y";
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            setInterfaceParameters(HuaweiEVRI.ACTION_ACCOUNT_INFO);
            // -------String inStr =
            // _formatter.generateRequest(HuaweiEVRI.ACTION_ACCOUNT_INFO,
            // _requestMap);
            sendDBRequestToIN(HuaweiEVRI.ACTION_ACCOUNT_INFO);
            // -------sendHttpRequestToIN(HuaweiEVRI.ACTION_ACCOUNT_INFO,inStr);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND))
                throw be;
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // throw new
            // BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_responseMap != null) {
                if (InterfaceUtil.isNullString((String) _responseMap.get("ACCOUNTSTATUS"))) {
                    _log.error("validate", "ACCOUNTSTATUS :" + _responseMap.get("ACCOUNTSTATUS"));
                    _responseMap.put("ACCOUNTSTATUS", FileCache.getValue(_interfaceID, "DEFAULT_ACCOUNT_STATUS"));
                }
                _requestMap.put("SERVICE_CLASS", _responseMap.get("SERVICECLASS"));
                _requestMap.put("ACCOUNT_STATUS", _responseMap.get("ACCOUNTSTATUS"));
            }
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
        double huaweiMultFactorDouble = 0;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            if (InterfaceUtil.isNullString((String) _requestMap.get("PIN")) || !InterfaceUtil.isNumeric((String) _requestMap.get("PIN"))) {
                _log.error("credit", "PIN is either is null or non-numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PIN is either is null or non-numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Fetching the HUAWEI_MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("credit", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
            _requestMap.put("HUAWEI_MULT_FACTOR", huaweiMultiplicationFactor);
            huaweiMultFactorDouble = Double.parseDouble(huaweiMultiplicationFactor);
            // Set the interface parameters into requestMap
            setInterfaceParameters(HuaweiEVRI.ACTION_RECHARGE_CREDIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            // sending the Re-charge request to IN along with recharge action
            // defined in HuaweiI interface
            sendRequestToIN(HuaweiEVRI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set NEW_EXPIRY_DATE into request map
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NEWACTIVESTOP"), "yyyyMMdd"));
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            try {
                String postBalanceStr = (String) _responseMap.get("NEWBALANCE");

                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, huaweiMultFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
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
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
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
    }// end of debitAdjust

    /**
     * This method would be used to adjust the validity of subscriber account at
     * the IN.
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
     * debitAdjust requests to IN depending on the action value.
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
        // TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"","action="+p_action+" _requestMap = "+_requestMap);
        String responseStr = null;
        String inRequestStr = null;
        HuaweiEVRSocketWrapper socketConnection = null;
        long startTime = 0;
        boolean isC2S = false;
        int retryCountConInvalid = 0;// Represent the Number of retries in case
                                     // of exception to get
                                     // HuaweiEVRSocketWrapper
        OutputStream out = null;
        // InputStream in = null;
        BufferedReader in = null;
        long endTime = 0;
        long sleepTimeConInvalid = 0;
        int retryCount = 0;
        String sessionID = null;
        Vector busyList = null;
        Vector freeList = null;
        StringBuffer responseBuffer = null;
        boolean isConnectionFree = false;
        try {
            // while sending or receiving request. handle event when this value
            // is null.
            String retryCountConInvalidStr = FileCache.getValue(_interfaceID, "RETRY_CON_INVAL");
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " Sleep time between retries,SLEEP_CON_INVAL=" + retryCountConInvalidStr);
            if (InterfaceUtil.isNullString(retryCountConInvalidStr) || !InterfaceUtil.isNumeric(retryCountConInvalidStr)) {
                _log.error("sendRequestToIN", "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            retryCountConInvalid = Integer.parseInt(retryCountConInvalidStr);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " Number of retries, RETRY_CON_INVAL=" + retryCountConInvalid);
            // Get the sleep time between retries (from IN file) when
            // SocketConnection is null or exception occurs
            // while sending request.
            String sleepTimeConInvalidStr = FileCache.getValue(_interfaceID, "SLEEP_CON_INVAL");
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " Sleep time between retries,SLEEP_CON_INVAL = " + sleepTimeConInvalidStr);
            if (InterfaceUtil.isNullString(sleepTimeConInvalidStr) || !InterfaceUtil.isNumeric(sleepTimeConInvalidStr)) {
                _log.error("sendRequestToIN", "SLEEP_CON_INVAL is either not defined in the INFile or it is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "SLEEP_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            sleepTimeConInvalid = Long.parseLong(sleepTimeConInvalidStr);
            // Fetch the pool size from the INFile.
            String module = (String) _requestMap.get("MODULE");
            if (InterfaceErrorCodesI.MODULE_C2S.equalsIgnoreCase(module))
                isC2S = true;
            try {
                // get a HuaweiEVRSocketWrapper object from PoolManager.
                socketConnection = HuaweiEVRPoolManager.getClientObject(_interfaceID, isC2S);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " socketConnection:" + socketConnection);
                // Get the Txn id to be sent to IN
                String txnID = socketConnection.getINHeaderTxnID();
                // Check the txnID whether txn id reaches the Max, if it reaches
                // Max create new connection and add in the list.
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "txnID: " + txnID);
                busyList = (Vector) HuaweiEVRPoolManager._busyBucket.get(_interfaceID);// get
                                                                                       // busy
                                                                                       // and
                                                                                       // free
                                                                                       // pool
                                                                                       // from
                                                                                       // pool
                                                                                       // mgr.
                freeList = (Vector) HuaweiEVRPoolManager._freeBucket.get(_interfaceID);

                // In creditAdjust (sender credit back )don't check interface
                // status, simply send the request to IN.
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                    _isSameRequest = true;
                    checkInterfaceB4SendingRequest();
                }
                if (HuaweiEVRI.MAX_TXN_REACH.equals(txnID)) {
                    try {
                        // destroy the older socket whose MAX limit of
                        // transaction id is reached.//Confirm for the LOG OUT
                        // request.
                        if (socketConnection != null)
                            socketConnection.destroy();
                        HuaweiEVRSocketWrapper newSocketConnection = HuaweiEVRPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                        txnID = socketConnection.getINHeaderTxnID();
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        e.printStackTrace();
                        _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Exception occurs while getting new Client object");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
                _requestMap.put("IN_HEADER_TXN_ID", txnID);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " txnID: " + txnID);
                // get session id associated with socketConnection by calling
                // generateSessionID of HuaweiEVRSocketWrapper class.
                // put session id in request map. if it is null handle event and
                // throw exception.
                sessionID = socketConnection.getSessionID();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " getting sessionID : " + sessionID);
                if (InterfaceUtil.isNullString(sessionID)) {
                    _log.error("sendRequestToIN", "Session id obtained from socket wrapper is NULL");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Session id obtained from socket wrapper is NULL");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("SESSIONID", sessionID);
                String inReconID = (String) _requestMap.get("IN_RECON_ID");
                if (inReconID == null)
                    inReconID = _inTXNID;
            } catch (BTSLBaseException be) {
                _log.error("sendRequestToIN", "Error while attempt to get SocketConnection object from PoolManager.");
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Exception occurs while getting new Client object");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // this is to count number of retries done.
            while (retryCount++ <= retryCountConInvalid) {
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Attemp Number:" + retryCount);
                try {
                    try {
                        inRequestStr = _formatter.generateRequest(p_action, _requestMap);
                        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, inRequestStr, "action=" + p_action + " _requestMap = " + _requestMap);
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    }
                    out = socketConnection.getPrintWriter();
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", _inTXNID, "Sending request to IN _socketConnection: " + socketConnection + " _interfaceID: " + _interfaceID + " inRequestStr: " + inRequestStr);
                    // get start time of txn and put it into request map.
                    startTime = System.currentTimeMillis();
                    _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                    // Send message to IN.
                    out.write(inRequestStr.getBytes());
                    out.flush();
                    // break;
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    // Check, retry count reaches to maximum attempts, throw
                    // exception.
                    if (retryCount > retryCountConInvalid) {
                        _log.error("sendRequestToIN", "Error while writing on output stream.");
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiEVRINHandler[sendRequestToIN]", _interfaceID, "", "", "Number of retry reached to MAX" + _interfaceID);
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                    try {
                        // get a new HuaweiEVRSocketWrapper object
                        // remove old HuaweiEVRSocketWrapper object from busy
                        // pool.
                        // add new HuaweiEVRSocketWrapper object in busy pool.
                        // get txn id and session id
                        // generate request
                        // Destroy the previous socket connection before ceating
                        // a new one.
                        if (socketConnection != null)
                            socketConnection.destroy();
                        HuaweiEVRSocketWrapper newSocketConnection = HuaweiEVRPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                        String txnID = socketConnection.getINHeaderTxnID();
                        if (InterfaceUtil.isNullString(txnID))
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        _requestMap.put("IN_HEADER_TXN_ID", txnID);
                        sessionID = socketConnection.getSessionID();
                        if (InterfaceUtil.isNullString(sessionID))
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        _requestMap.put("SESSIONID", sessionID);
                        Thread.sleep(sleepTimeConInvalid);
                        continue;
                    } catch (Exception ex) {
                        _log.error("sendRequestToIN", "Exception ex :" + ex.getMessage());
                        // throw ex;
                    }
                    // continue;
                }
                // }//End of while loop
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "WAITING FOR IN RESPONSE _socketConnection:" + socketConnection + "::::::::::IN ID= " + _interfaceID);
                try {
                    // get BufferedReader from HuaweiEVRSocketWrapper
                    in = socketConnection.getBufferedReader();
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "reading message");
                    int c = 0;
                    responseBuffer = new StringBuffer(1028);
                    while ((c = in.read()) != -1) {
                        responseBuffer.append((char) c);
                        if (c == 59)
                            break;
                    }
                    // END TIME OF txn.
                    endTime = System.currentTimeMillis();
                    String warnTimeStr = FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                    if (!InterfaceUtil.isNullString(warnTimeStr)) {
                        long warnTime = Long.parseLong(warnTimeStr);
                        if (endTime - startTime > warnTime) {
                            _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiEVRINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Huawei IN is taking more time than the warning threshold. Total Time taken is: " + (endTime - startTime));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", " Error occoured while reading response message Exception e :" + e.getMessage() + " creating new connection and replacing it with the older one");
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
                        HuaweiEVRSocketWrapper newSocketConnection = HuaweiEVRPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                    } catch (Exception ex) {
                        _log.error("sendRequestToIN", "Exception ex:" + ex.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while gettting new connnection in case of read time out,Exception e :" + e.getMessage());
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        // commented code may be used in future to support on
                        // line cancel request
                        /*
                         * if(HuaweiI.ACTION_TXN_CANCEL == p_stage)
                         * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI
                         * .AMBIGOUS);
                         */
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                    }
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while reading response message Exception e :" + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(HuaweiI.ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
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
                responseStr = responseBuffer.toString();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr : " + responseStr);
                // write _inTXNID, _referenceID, NETWORK_CODE, action, request
                // map in transaction log
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Request String :" + inRequestStr + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                // if response string is null then put status as AMBIGOUS in
                // request map. Handle event and throw exception.
                // else parse response.
                if (InterfaceUtil.isNullString(responseStr)) {
                    _requestMap.put("status", InterfaceErrorCodesI.AMBIGOUS);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Blank response from Huawei IN");
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
                // call parseResponse of formater to get response map
                _responseMap = new HashMap();// instantiate response mapwhich is
                                             // a instance var

                try {
                    _responseMap = _formatter.parseResponse(p_action, responseStr);
                } catch (BTSLBaseException be) {
                    if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                        _log.error("sendRequestToIN", "Incorrect response received , so during parsing response ,error occured hence marking txn as AMBIGUOUS");
                        EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Incorrect response received , so during parsing response error occured hence marking txn as AMBIGUOUS");
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        throw be;
                    }
                    throw be;
                }

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "_responseMap:" + _responseMap);
                // Check if txn id sent and received from IN are equal or not.
                String status = (String) _responseMap.get("status");
                _requestMap.put("INTERFACE_STATUS", status);

                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                    if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                        _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                    _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
                }
                String huaweiTxnID = (String) _requestMap.get("IN_HEADER_TXN_ID");
                String txnID = (String) _responseMap.get("transaction_id");
                // get status from response map.
                // status is ok then log txn else handle event and throw
                // exception.
                Object[] ambList = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "CON_INVAL_CASES")).split(",");
                if (!HuaweiEVRI.RESULT_OK.equals(status)) {
                    if (HuaweiEVRI.SUBSCRIBER_NOT_FOUND.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response SUBSCRIBER_NOT_FOUND");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " SUBSCRIBER_NOT_FOUND AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);// MSISDN
                                                                                                                              // Not
                                                                                                                              // Found
                    } else if (HuaweiEVRI.VOUCHER_ALREADY_USED.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response VOUCHER_ALREADY_USED");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " VOUCHER_ALREADY_USED AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_VOUCHER_ALREADY_USED);// INTERFACE_VOUCHER_ALREADY_USED
                    } else if (Arrays.asList(ambList).contains(status)) // When
                                                                        // session
                                                                        // is
                                                                        // closed
                                                                        // forcibly,A
                                                                        // new
                                                                        // connection
                                                                        // would
                                                                        // be
                                                                        // created
                                                                        // and
                                                                        // request
                                                                        // would
                                                                        // resent
                                                                        // to
                                                                        // the
                                                                        // IN.
                    {
                        _log.error("sendRequestToIN", "Status from the IN for connection with sessionID=" + socketConnection.getSessionID() + " is:: " + status);

                        if (socketConnection != null)
                            socketConnection.destroy();
                        HuaweiEVRSocketWrapper newSocketConnection = HuaweiEVRPoolManager.getNewClientObject(_interfaceID);
                        freeList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                        isConnectionFree = false;
                        String newTxnID = socketConnection.getINHeaderTxnID();
                        if (InterfaceUtil.isNullString(newTxnID))
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        _requestMap.put("IN_HEADER_TXN_ID", newTxnID);
                        sessionID = socketConnection.getSessionID();
                        if (InterfaceUtil.isNullString(sessionID))
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        _requestMap.put("SESSIONID", sessionID);
                        continue;
                    }
                    _log.error("sendRequestToIN", "Error in response with" + " status=" + status);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
                } else if (!(huaweiTxnID.equals(txnID))) {
                    _log.error("sendRequestToIN", "Transaction id set in the request header [" + huaweiTxnID + "] does not matched with the transaction id fetched from response[" + txnID + "]");
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " p_action = " + p_action, "Transaction id set in the request header [" + txnID + "] does not matched with the transaction id fetched from response[" + huaweiTxnID + "],Hence marking the transaction as AMBIGUOUS");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }
                break;
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (!isConnectionFree && (busyList != null && freeList != null)) {
                busyList.remove(socketConnection);
                freeList.add(socketConnection);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action:" + p_action);
        }
    }// end of sendRequestToIN

    /*
     * public void sendHttpRequestToIN(int p_action,String p_inRequestStr)
     * throws BTSLBaseException
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("sendHttpRequestToIN", "Entered p_inRequestStr:" +
     * p_inRequestStr + " p_action:" + p_action);
     * TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get(
     * "NETWORK_CODE"
     * ),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string:"
     * +p_inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
     * String responseStr = "";
     * String result=null;
     * String subscriberType=null;
     * UmniahUrlConnection umniahUrlConnection = null;
     * long startTime=0;
     * try
     * {
     * _responseMap = new HashMap();
     * String inReconID=(String)_requestMap.get("IN_RECON_ID");
     * if(inReconID==null)
     * inReconID=_inTXNID;
     * 
     * int readTimeOut ;
     * String readTimeOutStr = FileCache.getValue(_interfaceID,
     * "READ_TIMEOUT_VAL");
     * if(readTimeOutStr==null)
     * {
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_referenceID,_msisdn
     * +" INTERFACE ID = "+_interfaceID, (String)
     * _requestMap.get("NETWORK_CODE")+" Stage = "+p_action,
     * "Read time out VAL is not defined in INFile");
     * throw new
     * BTSLBaseException(this,"sendHttpRequestToIN",InterfaceErrorCodesI
     * .INTERFACE_HANDLER_EXCEPTION);
     * }
     * _requestMap.put("READ_TIMEOUT_VAL",readTimeOutStr);
     * readTimeOut = Integer.parseInt(readTimeOutStr);
     * if
     * (_log.isDebugEnabled())_log.debug("sendHttpRequestToIN"," READ TIMEOUT VAL "
     * +readTimeOut);
     * try
     * {
     * umniahUrlConnection = new
     * UmniahUrlConnection(FileCache.getValue(_interfaceID,
     * "URL"),Integer.parseInt(FileCache.getValue(_interfaceID,
     * "CONNECT_TIMEOUT")),readTimeOut,FileCache.getValue(_interfaceID,
     * "KEEP_ALIVE"));
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,
     * EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",
     * " INTERFACE ID = "+_interfaceID, " Stage = "+p_action, "",
     * "Not able to create connection, getting Exception:" + e.getMessage());
     * throw new
     * BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
     * }
     * try
     * {
     * PrintWriter out = umniahUrlConnection.getPrintWriter();
     * out.flush();
     * startTime=System.currentTimeMillis();
     * _requestMap.put("IN_START_TIME",String.valueOf(startTime));
     * out.println(p_inRequestStr);
     * out.flush();
     * }
     * catch (Exception e)
     * {
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED,
     * EventLevelI.FATAL,"HuaweiEVRINHandler[sendHttpRequestToIN]"
     * ,_referenceID,_msisdn, (String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action,"Exception while sending request to IN");
     * throw new
     * BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
     * }
     * try
     * {
     * // Create buffered reader and Read Response from the IN
     * StringBuffer buffer = new StringBuffer();
     * String response = "";
     * long endTime=0;
     * try
     * {
     * umniahUrlConnection.setBufferedReader();
     * BufferedReader in = umniahUrlConnection.getBufferedReader();
     * while ((response = in.readLine()) != null)
     * {
     * buffer.append(response);
     * }
     * endTime=System.currentTimeMillis();
     * String warnTimeStr=(String)FileCache.getValue(_interfaceID,
     * "WARN_TIMEOUT");
     * if(!InterfaceUtil.isNullString(warnTimeStr))
     * {
     * long warnTime=Long.parseLong(warnTimeStr);
     * if(endTime-startTime>warnTime)
     * {
     * _log.info("sendHttpRequestToIN",
     * "WARN time reaches startTime: "+startTime
     * +" endTime: "+endTime+" warnTime: "
     * +warnTime+" time taken: "+(endTime-startTime));
     * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.MAJOR,"HuaweiEVRINHandler[sendHttpRequestToIN]",_inTXNID
     * ,_msisdn,(String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "
     * +p_action," IN is taking more time than the warning threshold. Time: "
     * +(endTime-startTime));
     * }
     * }
     * }
     * catch (Exception e)
     * {
     * _log.error("sendHttpRequestToIN",
     * " response form interface is null exception is " + e.getMessage());
     * EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_inTXNID,_msisdn,(String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action,
     * "Exception while getting response from IN e: "+e.getMessage());
     * throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
     * }
     * finally
     * {
     * if(endTime==0) endTime=System.currentTimeMillis();
     * _requestMap.put("IN_END_TIME",String.valueOf(endTime));
     * _log.error("sendRequestToIN","IN_START_TIME="+String.valueOf(startTime)+
     * " IN_END_TIME="
     * +String.valueOf(endTime)+" READ_TIMEOUT_VAL ="+_requestMap.
     * get("READ_TIMEOUT_VAL"));
     * }
     * responseStr = buffer.toString();
     * if (_log.isDebugEnabled())_log.debug("sendHttpRequestToIN",
     * "responseStr:" + responseStr);
     * _requestMap.put("RESPONSE_STR",responseStr);
     * TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get(
     * "NETWORK_CODE"
     * ),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"
     * +responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," INTERFACE ID = "+
     * _interfaceID+" action="+p_action);
     * String httpStatus = umniahUrlConnection.getResponseCode();
     * _requestMap.put("PROTOCOL_STATUS", httpStatus);
     * if (!httpStatus.equals(HuaweiEVRI.HTTP_STATUS_200))
     * throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
     * if (InterfaceUtil.isNullString(responseStr) )
     * {
     * _log.error("sendHttpRequestToIN", " Blank response from IN");
     * EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_inTXNID,_msisdn,(String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action, "Blank response from IN ");
     * throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
     * }
     * _responseMap = _formatter.parseResponse(p_action, responseStr);
     * result=(String) _responseMap.get("STATUS");
     * _requestMap.put("INTERFACE_STATUS",result);
     * subscriberType=(String)_responseMap.get("SUBSCRIBERTYPE");
     * _requestMap.put("SUBSCRIBER_TYPE",subscriberType);
     * if (InterfaceUtil.isNullString(result) ||
     * !result.equals(HuaweiI.RESULT_OK))
     * {
     * _log.error("sendHttpRequestToIN", "Status received from IN is "+result);
     * EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_inTXNID,_msisdn,(String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action, "Status received from IN is"+result);
     * throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
     * }
     * else
     * if("POST".equalsIgnoreCase(subscriberType))
     * {
     * _log.error("sendHttpRequestToIN",
     * "Subscriber type received from IN is POST");
     * EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_inTXNID,_msisdn,(String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action, "Subscriber type received from IN is POST");
     * throw new
     * BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
     * }
     * }
     * catch (BTSLBaseException be)
     * {
     * throw be;
     * }
     * catch (Exception e)
     * {
     * _log.error("sendHttpRequestToIN", "Exception e:" + e.getMessage() +
     * InterfaceUtil.getPrintMap(_requestMap));
     * EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_inTXNID,_msisdn,(String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action, "Exception while getting response from IN :" +
     * e.getMessage());
     * throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
     * }
     * }
     * catch (BTSLBaseException be)
     * {
     * throw be;
     * }
     * catch (Exception e)
     * {
     * e.printStackTrace();
     * _log.error("sendHttpRequestToIN", "Exception:" + e.getMessage());
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_referenceID,_msisdn, (String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action, "System Exception:" + e.getMessage());
     * throw new
     * BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
     * }
     * finally
     * {
     * try
     * {
     * if (umniahUrlConnection != null)
     * umniahUrlConnection.close();
     * }
     * catch (Exception e)
     * {
     * e.printStackTrace();
     * _log.error("sendHttpRequestToIN", "Exception ehile closing Connection:" +
     * e.getMessage());
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "HuaweiEVRINHandler[sendHttpRequestToIN]",_referenceID, _msisdn, (String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
     * " Stage = "+p_action, "Not able to close connection:" + e.getMessage());
     * }
     * if (_log.isDebugEnabled())
     * _log.debug("sendHttpRequestToIN", "Exiting p_action:" + p_action +
     * " responseStr:" + responseStr);
     * }//end of finally
     * }
     */
    /**
     * This method would be used to send validate request to IN by data base.
     * 
     * @param int p_action
     * @param String
     *            p_inRequestStr
     * @throws BTSLBaseException
     */
    public void sendDBRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendDBRequestToIN ", "Entered p_action : " + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Stored Proceedure is Called for stage:" + p_action, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
        Object dbUtility = null; // Reference of an Object
        Connection dbConnection = null; // Reference of Connection
        String inReconID = null; // Variable for IN Reconciliation Id
        String responseStr = null; // Field for response string
        String result = null; // Field for result string
        String subscriberType = null; // Field for subscriber type
        long startTime = 0; // Variable to store start time of the transaction
        long endTime = 0; // Variable to store end time of the transaction
        long warnTime = 0; // Variable to store warn time for the transaction
        try {
            // IN Reconciliation ID
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            // creating a new response map
            _responseMap = new HashMap();

            dbUtility = HuaweiEVRDBPoolManager._dbUtilityObjectMap.get(_interfaceID);
            if (dbUtility == null) {
                _log.error("sendDBRequestToIN", "dbUtility: " + dbUtility);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendDBRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Pool is not initialized properly");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
            }
            dbConnection = ((HuaweiEVRDBUtility) dbUtility).getConnection();
            // If dbConnection is null, throw an exception
            if (dbConnection == null) {
                _log.error("sendDBRequestToIN", "dbConnection= " + dbConnection);// Error
                // Confirm for the Event handling.
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendDBRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "DBConnection is NULL");
                throw new BTSLBaseException(this, "sendDBRequestToIN", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);// Confrim
                                                                                                                            // for
                                                                                                                            // the
                                                                                                                            // new
                                                                                                                            // key
            }
            // Start time
            startTime = System.currentTimeMillis();
            // Put the start time into the request map
            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
            // Calling the stored procedure based on the action type by passing
            // connection.
            sendAccountInfoRequest(dbConnection);
            // Commit the result after successful query.
            dbConnection.commit();
            // End time
            endTime = System.currentTimeMillis();
            // Put the end time into the request map
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            // Warn time
            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (!InterfaceUtil.isNullString(warnTimeStr)) {
                warnTime = Long.parseLong(warnTimeStr);
                if (endTime - startTime > warnTime) {
                    _log.info("sendDBRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiEVRINHandler[sendDBRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, " IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                }
            }
            // Get the request from the request map
            responseStr = (String) _requestMap.get("RESPONSE_STR");
            // Show the values onto the transaction log
            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Request String :" + "" + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
            // If response string is null, throw an exception
            if (InterfaceUtil.isNullString(responseStr)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendDBRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Response from the IN:" + responseStr);
                _log.error("sendDBRequestToIN", " Blank response from IN");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            // Convert the responseString into hash map.
            InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");
            // Get the status from rsponse map
            result = (String) _responseMap.get("STATUS");
            _requestMap.put("INTERFACE_STATUS", result);
            subscriberType = (String) _responseMap.get("SUBSCRIBERTYPE");
            _requestMap.put("SUBSCRIBER_TYPE", subscriberType);
            if (_log.isDebugEnabled())
                _log.debug("sendDBRequestToIN ", "SUBSCRIBER_TYPE : " + subscriberType);
            // Check for the POST
            if ("POST".equalsIgnoreCase(subscriberType)) {
                _log.error("sendDBRequestToIN", "Subscriber type received from IN is POST");
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiEVRINHandler[sendDBRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber type received from IN is POST");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            }
        }// end of try block
         // Exception handling blocks
        catch (BTSLBaseException be) {
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (Exception e) {
            }
            if (be.getMessage().equals(DB_CONN_FAILED))
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendDBRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "DB connection object is null in the pool");
            throw be;
        }// end of catch-BTSLBaseException block of sendDBRequestToIN
        catch (Exception e) {
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
            _log.error("sendDBRequestToIN", "Exception e: " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendDBRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "While calling the stored proc get Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "sendDBRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

        }// end of catch-Exception block of sendDBRequestToIN
        finally {
            try {
                if (dbConnection != null)
                    dbConnection.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("sendDBRequestToIN ", " Exited  responseStr: " + responseStr);
        }// end of finally block of sendDBRequestToIN
    }

    /**
     * This method calls stored procedure getAccountInformation and sets the
     * response parameters in response map.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void sendAccountInfoRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendAccountInfoRequest", " Entered");
        CallableStatement callStmt = null; // reference of CallableStatement
        StringBuffer responseBuffer = null; // reference of StringBuffer
        String responseStr = null; // Field for response string
        _responseMap = new HashMap();
        try {
            String procStr = "call XXUMN_ENG_PKG.XXUMN_MSISDN_INQUIRY(?,?,?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, HuaweiEVRI.SUBINFO);
            callStmt.setString(2, InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN")));
            callStmt.setDate(3, InterfaceUtil.getSQLDateFromUtilDate(new java.util.Date()));
            // callStmt.setString(3,InterfaceUtil.getDateTimeFormat("dd-MM-yyyy hh:mm:ss aa"));
            callStmt.setString(4, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.INTEGER);
            callStmt.registerOutParameter(7, Types.VARCHAR);
            callStmt.registerOutParameter(8, Types.INTEGER);
            callStmt.registerOutParameter(9, Types.VARCHAR);
            callStmt.registerOutParameter(10, Types.VARCHAR);

            callStmt.execute();
            responseBuffer = new StringBuffer(1024);
            responseBuffer.append("TXNID=" + callStmt.getString(5));
            responseBuffer.append("&STATUS=" + String.valueOf(callStmt.getInt(6)));
            responseBuffer.append("&SUBSCRIBERTYPE=" + callStmt.getString(7));
            responseBuffer.append("&SERVICECLASS=" + String.valueOf(callStmt.getInt(8)));
            responseBuffer.append("&ACCOUNTSTATUS=" + callStmt.getString(9));
            responseBuffer.append("&DATE=" + callStmt.getString(10));

            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendAccountInfoRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + HuaweiEVRI.ACTION_ACCOUNT_INFO, "While sendAccountInfoRequest the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-SQLException block of sendAccountInfoRequest
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendAccountInfoRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + HuaweiEVRI.ACTION_ACCOUNT_INFO, "While sendAccountInfoRequest the subscriber get Exception e:" + e.getMessage());
            // throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of sendAccountInfoRequest
        finally {
            try {
                if (callStmt != null)
                    callStmt.clearParameters();
            } catch (Exception e) {
            }
            try {
                if (callStmt != null)
                    callStmt.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("sendAccountInfoRequest", " Exited ");
        }// end of finally block of sendAccountInfoRequest
    }// end of sendAccountInfoRequest

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
            switch (p_action) {
            case HuaweiEVRI.ACTION_ACCOUNT_INFO:
                break;
            case HuaweiEVRI.ACTION_RECHARGE_CREDIT:
                // Since only for recharge Pretups will send request to
                // HuaweiEVR IN. So MML parameters are being set in this case
                // only
                String versionNumber = FileCache.getValue(_interfaceID, "VERSION_NUMBER");
                if (InterfaceUtil.isNullString(versionNumber)) {
                    _log.error("setInterfaceParameters", "Value of VERSION_NUMBER is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "VERSION_NUMBER is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("VERSION_NUMBER", versionNumber.trim());
                // get TERM from IN File Cache and put it it request map
                String term = FileCache.getValue(_interfaceID, "TERM");
                if (InterfaceUtil.isNullString(term)) {
                    _log.error("setInterfaceParameters", "Value of TERM is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TERM is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("TERM", term.trim());
                String msgHeaderLanguage = FileCache.getValue(_interfaceID, "MSG_HEAD_LANGUAGE");
                if (InterfaceUtil.isNullString(msgHeaderLanguage)) {
                    _log.error("setInterfaceParameters", "Value of MSG_HEAD_LANGUAGE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MSG_HEAD_LANGUAGE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("MSG_HEAD_LANGUAGE", msgHeaderLanguage.trim());
                String dlgLgn = FileCache.getValue(_interfaceID, "DLGLGN");
                if (InterfaceUtil.isNullString(dlgLgn)) {
                    _log.error("setInterfaceParameters", "Value of DLGLGN is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DLGLGN is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("DLGLGN", dlgLgn.trim());
                // get RSV from IN File Cache and put it it request map
                String rsv = FileCache.getValue(_interfaceID, "RSV");
                if (InterfaceUtil.isNullString(rsv)) {
                    _log.error("setInterfaceParameters", "Value of RSV is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RSV is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("RSV", rsv.trim());
                // get DLGCTRL from IN File Cache and put it it request map
                String dlgCtrl = FileCache.getValue(_interfaceID, "DLGCTRL");
                if (InterfaceUtil.isNullString(dlgCtrl)) {
                    _log.error("setInterfaceParameters", "Value of DLGCTRL is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DLGCTRL is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("DLGCTRL", dlgCtrl.trim());
                // get TSRV from IN File Cache and put it it request map
                String tsrv = FileCache.getValue(_interfaceID, "TSRV");
                if (InterfaceUtil.isNullString(tsrv)) {
                    _log.error("setInterfaceParameters", "Value of TSRV is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TSRV is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("TSRV", tsrv.trim());
                // get START_FLAG from IN File Cache and put it it request map
                String startFlag = FileCache.getValue(_interfaceID, "START_FLAG");
                if (InterfaceUtil.isNullString(startFlag)) {
                    _log.error("setInterfaceParameters", "Value of START_FLAG is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "START_FLAG is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("START_FLAG", startFlag.trim());
                // get DLGCON from IN File Cache and put it it request map
                String dlgCon = FileCache.getValue(_interfaceID, "DLGCON");
                if (InterfaceUtil.isNullString(startFlag)) {
                    _log.error("setInterfaceParameters", "Value of DLGCON is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DLGCON is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("DLGCON", dlgCon.trim());

                String rechargeCommand = FileCache.getValue(_interfaceID, "RECHARGE_COMMAND");
                if (InterfaceUtil.isNullString(rechargeCommand)) {
                    _log.error("setInterfaceParameters", "Value of RECHARGE_COMMAND is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RECHARGE_COMMAND is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("RECHARGE_COMMAND", rechargeCommand.trim());

                // get CHRGTYPE from IN File Cache and put it it request map
                String chargeType = FileCache.getValue(_interfaceID, "CHRGTYPE");
                if (InterfaceUtil.isNullString(chargeType)) {
                    _log.error("setInterfaceParameters", "Value of CHRGTYPE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CHRGTYPE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CHRGTYPE", chargeType.trim());

                // This defines that operator's query for RECHARGE_SERIVICE
                String rechargeService = FileCache.getValue(_interfaceID, "RECHARGE_SERIVICE");
                if (InterfaceUtil.isNullString(rechargeService)) {
                    _log.error("setInterfaceParameters", "Value of RECHARGE_SERIVICE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RECHARGE_SERIVICE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("RECHARGE_SERIVICE", rechargeService.trim());
                // Following parameters are related to Interface closer so being
                // set in all actions
                String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
                if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                    _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

                String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
                if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

                String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
                if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

                String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
                if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

                String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
                if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

                String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
                if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                    _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

                String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
                if (InterfaceUtil.isNullString(cancelNA)) {
                    _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiEVRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CANCEL_NA", cancelNA.trim());
                break;
            case HuaweiEVRI.ACTION_IMMEDIATE_DEBIT:
                break;
            }
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
            langFromIN = (String) _responseMap.get("LANGUAGETYPE");
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiEVRINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiEVRINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "while setting the language mapping get Exception=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

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
