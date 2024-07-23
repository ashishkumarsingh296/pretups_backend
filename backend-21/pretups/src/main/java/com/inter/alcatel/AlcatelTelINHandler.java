package com.inter.alcatel;

/**
 * @(#)AlcatelTelINHandler.java
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
 *                              Gurjeet Singh Bedi Oct 19,2005 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              Handler Class for interface
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.inter.telnet.TelnetWrapper;

public class AlcatelTelINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private final long PROCESS_TIMEOUT_TIME = 50000;
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private String _responseStr = null;
    AlcatelTelRequestResponse _formatter = null;
    private boolean isTimeOut = false;
    private long processStartTime = 0;
    private static long _requestID = 0;
    private String telnetResponseStr = null;
    private String _interfaceID = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    public void validate(HashMap p_map) throws BTSLBaseException, Exception {
        _requestMap = p_map;
        try {
            _requestMap.put("Stage", "VAL");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

            setInterfaceParameters();
            // Get TransId
            populateINTransactionID();
            _formatter = new AlcatelTelRequestResponse();
            String inStr = _formatter.getValidateRequest(_requestMap);
            // Sending the request to IN
            sendRequestToIN(inStr, "Balance");
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("A_CURSTA"));
            _requestMap.put("FIRST_CALL", (String) _responseMap.get("D_TAC"));
            _requestMap.put("INTERFACE_PREV_BALANCE", (String) _responseMap.get("CREDIT"));
            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("PROFILE"));
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("D_TMC"), PretupsI.DATE_FORMAT_DDMMYYYY));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("D_TR"), PretupsI.DATE_FORMAT_DDMMYYYY));
            _requestMap.put("GRACE_DAYS", (String) _responseMap.get("OP_UNDUR"));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            if (_log.isDebugEnabled())
                _log.debug("credit", "response=" + _requestMap);
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[validate]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw e;
        }
    }

    public void credit(HashMap p_map) throws BTSLBaseException, Exception {
        _requestMap = p_map;
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered " + InterfaceUtil.getPrintMap(_requestMap));
        try {
            _requestMap.put("Stage", "CR");
            _requestMap.put("BLOCK", "0");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            // Get TransId
            populateINTransactionID();
            _formatter = new AlcatelTelRequestResponse();
            String inStr = _formatter.getCreditRequest(_requestMap);
            // Sending the request to IN
            sendRequestToIN(inStr, "Credit");
            if (_responseStr.indexOf(FileCache.getValue(_interfaceID, "CR_RESPONSE_FORSUCCESS")) > 0) {
                String sendBalanceAfterCr = FileCache.getValue(_interfaceID, "BAL_ENQ_AFTER_CR_REQD");
                if ("Y".equals(sendBalanceAfterCr)) {
                    try {
                        validate(p_map);
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                    } catch (BTSLBaseException be) {
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                        throw be;
                    } catch (Exception e) {
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                        throw e;
                    }
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("INTERFACE_POST_BALANCE", (String) _responseMap.get("CREDIT"));
                    _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("D_TMC"), PretupsI.DATE_FORMAT_DDMMYYYY));
                    _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("D_TR"), PretupsI.DATE_FORMAT_DDMMYYYY));
                }
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "response=" + _requestMap);

        } catch (BTSLBaseException be) {
            p_map.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[credit]", "", "", (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[credit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw e;
        }

    }

    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        _requestMap = p_map;
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered " + InterfaceUtil.getPrintMap(_requestMap));
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _requestMap.put("Stage", "CRA");
            _requestMap.put("BLOCK", "0");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();

            setInterfaceParameters();
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            // Get TransId
            populateINTransactionID();
            _formatter = new AlcatelTelRequestResponse();
            String inStr = _formatter.getCreditRequest(_requestMap);
            // Sending the request to IN
            sendRequestToIN(inStr, "CreditBack");
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            /*
             * String interfaceID=(String)_requestMap.get("INTERFACE_ID");
             * if(_responseStr.indexOf(FileCache.getValue(interfaceID,
             * "CR_RESPONSE_FORSUCCESS"))>0)
             * {
             * String sendBalanceAfterCr=FileCache.getValue(interfaceID,
             * "BAL_ENQ_AFTER_CR_REQD");
             * if("Y".equals(sendBalanceAfterCr))
             * {
             * try
             * {
             * validate(p_map);
             * _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
             * }
             * catch(BTSLBaseException be)
             * {
             * _requestMap.put("POST_BALANCE_ENQ_SUCCESS","N");
             * throw be;
             * }
             * catch(Exception e)
             * {
             * _requestMap.put("POST_BALANCE_ENQ_SUCCESS","N");
             * throw e;
             * }
             * _requestMap
             * .put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
             * _requestMap.put("INTERFACE_POST_BALANCE",(String)_responseMap.get(
             * "CREDIT"));
             * _requestMap.put("NEW_EXPIRY_DATE",InterfaceUtil.
             * getInterfaceDateFromDateString
             * ((String)_responseMap.get("D_TMC"),PretupsI.DATE_FORMAT_DDMMYYYY));
             * 
             * }
             * _requestMap
             * .put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
             * }
             * else
             * {
             * _requestMap
             * .put("TRANSACTION_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
             * }
             */
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "response=" + _requestMap);

        } catch (BTSLBaseException be) {
            p_map.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[creditAdjust]", "", "", (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[creditAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust");
            throw e;
        }
    }

    public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        _requestMap = p_map;
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered " + InterfaceUtil.getPrintMap(_requestMap));
        try {
            _requestMap.put("Stage", "DR");
            _requestMap.put("BLOCK", "0");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            // Get TransId
            populateINTransactionID();
            _formatter = new AlcatelTelRequestResponse();
            String inStr = _formatter.getDebitRequest(_requestMap);
            // Sending the request to IN
            sendRequestToIN(inStr, "Debit");

            if (_responseStr.indexOf(FileCache.getValue(_interfaceID, "DR_RESPONSE_FORSUCCESS")) > 0) {
                String sendBalanceAfterDr = FileCache.getValue(_interfaceID, "BAL_ENQ_AFTER_DR_REQD");
                if ("Y".equals(sendBalanceAfterDr)) {
                    try {
                        validate(p_map);
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                    } catch (BTSLBaseException be) {
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                        throw be;
                    } catch (Exception e) {
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                        throw e;
                    }
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("INTERFACE_POST_BALANCE", (String) _responseMap.get("CREDIT"));
                    _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("D_TMC"), PretupsI.DATE_FORMAT_DDMMYYYY));
                    _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("D_TR"), PretupsI.DATE_FORMAT_DDMMYYYY));
                }
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "response=" + _requestMap);
        } catch (BTSLBaseException be) {
            p_map.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[debitAdjust]", "", "", (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[debitAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while debitAdjust");
            throw e;
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

    public synchronized void populateINTransactionID() throws BTSLBaseException {
        long id = _requestID++;
        if (id > 999999)
            id = 1;
        String idStr = String.valueOf(id);
        // get unique ID
        if (idStr.length() < 6) {
            int paddingLength = 6 - idStr.length();
            for (int i = 0; i < paddingLength; i++) {
                idStr = "0" + idStr;
            }
        }
        _requestMap.put("IN_TXN_ID", idStr);
    }

    public void sendRequestToIN(String p_inRequestStr, String p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_inRequestStr + " p_stage:" + p_stage);
        TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), p_stage, PretupsI.TXN_LOG_REQTYPE_REQ, "Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
        String responseStr = null;
        boolean isSuccess = false;
        TelnetConnection telnetConnection = null;
        TelnetConnectionPool pool = null;
        try {
            processStartTime = System.currentTimeMillis();
            _responseMap = new HashMap();
            String transID = (String) _requestMap.get("IN_TXN_ID");
            // String interfaceID=(String)_requestMap.get("INTERFACE_ID");
            String inRequestStr = p_inRequestStr;
            // In creditAdjust (sender credit back )don't check interface
            // status, simply send the request to IN.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }
            // TelnetConnectionPoolLoader telnetConnectionPoolLoader=new
            // TelnetConnectionPoolLoader (interfaceID);
            pool = TelnetConnectionPoolLoader.getPool(_interfaceID);
            TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_GETCONN, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Getting Connection", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "Stage=" + p_stage);
            telnetConnection = (TelnetConnection) pool.getConnection();
            TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_GETCONN, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "After Getting Connection", PretupsI.TXN_LOG_STATUS_SUCCESS, "Stage=" + p_stage);
            TelnetWrapper telnetWrapper = telnetConnection.getTelnetWrapper();
            try {
                int retryCount = 10;
                long sleepTimer = 10;
                retryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "RETRY_COUNT"));
                sleepTimer = Long.parseLong(FileCache.getValue(_interfaceID, "SLEEP_TIME"));
                // for testing we replace comma by space.
                // inRequestStr=inRequestStr.replace("," , " ");
                isSuccess = processTelnetRequest(telnetConnection, telnetWrapper, _interfaceID, inRequestStr + "\r", sleepTimer, telnetConnection.getTelnetLoginTimeout(), retryCount, null, true, p_stage);
                if (!isSuccess) {
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Unsuccessful in sending request and fetching response to IN");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                responseStr = telnetResponseStr;
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr:" + responseStr);
                TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), p_stage, PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
                if (BTSLUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Blank response from Alcatel IN");
                    _log.error("sendRequestToIN", "NULL response for interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(AlcatelI.ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                } else {
                    // the PPSSES10P will be replaced by the prompt later.
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "After Prompt" + FileCache.getValue(_interfaceID, "PROMPT"));
                    int index = responseStr.indexOf(FileCache.getValue(_interfaceID, "PROMPT"), 1);
                    if (index != -1)
                        responseStr = responseStr.substring(index);
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
                     * if(AlcatelI.ACTION_TXN_CANCEL == p_stage)
                     * {
                     * _requestMap.put("CANCEL_RESP_STATUS",result);
                     * cancelTxnStatus =
                     * InterfaceUtil.getErrorCodeFromMapping(_requestMap
                     * ,result,"SYSTEM_STATUS_MAPPING");
                     * throw new BTSLBaseException(cancelTxnStatus);
                     * }
                     */

                    // _requestMap.put("INTERFACE_STATUS",result);//THIS SHOULD
                    // ALSO BE DONE
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "After Subtring responseStr:" + responseStr);
                    if (p_stage.equals("Balance")) {
                        InterfaceUtil.populateStringToHash(_responseMap, responseStr, ",", "=");
                        if (_log.isDebugEnabled())
                            _log.debug("sendRequestToIN", "_responseMap" + _responseMap);
                        if (!((String) _responseMap.get("MSISDN")).equals((String) _requestMap.get("MSISDN"))) {
                            _log.info("sendRequestToIN", "MSISDN:" + _responseMap.get("MSISDN") + " current TransId=" + (String) _requestMap.get("MSISDN") + " Mismatch");
                            // send alert message(TO BE IMPLEMENTED)
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Request and Response Transaction id from Alcatel IN does not match");

                            // invalid response
                            throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);
                        } else {
                            _responseStr = responseStr;
                        }
                    } else
                        _responseStr = responseStr;
                }
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                // reconciliation case
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while getting response from IN :" + e.getMessage());
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // commented code may be used in future to support on line
                // cancel request
                /*
                 * if(AlcatelI.ACTION_TXN_CANCEL == p_stage)
                 * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                 * AMBIGOUS);
                 */
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            // send alert message(TO BE IMPLEMENTED)
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (pool != null && telnetConnection != null)
                pool.freeConnection(telnetConnection);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_stage:" + p_stage + " responseStr:" + responseStr);
        }
    }

    /**
     * Method to send and receive request response
     * 
     * @param p_telnetConnection
     * @param p_telnetWrapper
     * @param p_interfaceID
     * @param command
     * @param p_sleepTime
     * @param timeout
     * @param counter
     * @param notExpectedOutput
     * @param sendAlarm
     * @param p_stage
     * @return
     * @throws BTSLBaseException
     */
    private boolean processTelnetRequest(TelnetConnection p_telnetConnection, TelnetWrapper p_telnetWrapper, String p_interfaceID, String command, long p_sleepTime, long timeout, int counter, String notExpectedOutput, boolean sendAlarm, String p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("processTelnetRequest", "Enterd p_interfaceID=" + p_interfaceID + " command:" + command + "     timeout:" + timeout + "  counter:" + counter + " notExpectedOutput:" + notExpectedOutput);
        int i = 0;
        telnetResponseStr = null;
        while (i++ < counter) {
            if ((System.currentTimeMillis() - processStartTime) > PROCESS_TIMEOUT_TIME) {
                if (_log.isDebugEnabled())
                    _log.debug("processTelnetRequest", "PROCESS_TIMEOUT ");
                isTimeOut = true;
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_TIMEOUT);
            }
            if (_log.isDebugEnabled())
                _log.debug("processTelnetRequest", "processTelnetRequest() i=" + i);
            try {
                TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_SENDREQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Before Sending Request=" + command, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "Stage=" + p_stage);
                p_telnetWrapper.send(command + "\r");
                telnetResponseStr = p_telnetWrapper.receiveUntil(p_telnetConnection.getTelnetPrompt(), timeout);
                telnetResponseStr = telnetResponseStr.replaceAll("\r", "");
                telnetResponseStr = telnetResponseStr.replaceAll("\n", "");
                TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), "", PretupsI.TXN_LOG_TXNSTAGE_GETRESPONSE, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Got Response=" + telnetResponseStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
                if (_log.isDebugEnabled())
                    _log.debug("processTelnetRequest", "telnetResponseStr:" + telnetResponseStr);
                if (telnetResponseStr == null || (notExpectedOutput == null || telnetResponseStr.indexOf(notExpectedOutput) == -1))
                    return true;
                Thread.sleep(p_sleepTime);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    Thread.sleep(p_sleepTime);
                    if (_log.isDebugEnabled())
                        _log.debug("processTelnetRequest", "Exception:" + ex.getMessage() + " Trying to Reconnect");
                    if (p_telnetWrapper != null) {
                        try {
                            p_telnetWrapper.disconnect();
                        } catch (Exception e) {
                        }
                        TelnetConnectionPool pool = TelnetConnectionPoolLoader.getPool(p_interfaceID);
                        TelnetConnection oldTelnetConnection = p_telnetConnection;
                        TransactionLog.log((String) _requestMap.get("IN_TXN_ID"), (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_GETCONN, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Getting Connection", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "Stage=" + p_stage);
                        // p_telnetConnection = new
                        // TelnetConnection(p_interfaceID,FileCache.getValue(p_interfaceID,"URL"),FileCache.getValue(p_interfaceID,"USER"),FileCache.getValue(p_interfaceID,"PASSWORD"));
                        if (!pool.isUnderProcess())
                            p_telnetConnection = (TelnetConnection) pool.getNewConnection();
                        else
                            p_telnetWrapper = p_telnetConnection.getTelnetWrapper();
                        pool.replaceBusyConnection(oldTelnetConnection, p_telnetConnection);
                    }
                } catch (Exception e) {
                    _log.error("processRequest", "Exception:" + ex.getMessage() + " during Reconnect");
                    e.printStackTrace();
                    throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                }
            }
        }
        return false;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Constants.load("C:\\eclipse\\workspace\\pretups\\src\\configfiles\\Constants.props");
            org.apache.log4j.PropertyConfigurator.configure("C:\\eclipse\\workspace\\pretups\\src\\configfiles\\LogConfig.props");
            FileCache.loadAtStartUp();
            HashMap _map = null;
            _map = new HashMap();
            _map.put("MSISDN", "9818693281");
            _map.put("BLOCK", "0");
            _map.put("OP_ACDUR", "62");
            _map.put("OP_CRED", "100");
            _map.put("OP_DDATE", "17/01/2005");
            _map.put("OP_UNDUR", "93");
            _map.put("SIGN_F", "0");
            _map.put("INTERFACE_ID", "IN001");
            AlcatelTelINHandler alcatelINHandler = new AlcatelTelINHandler();
            alcatelINHandler.debitAdjust(_map);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN =", (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN =", (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN =", (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN =", (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN =", (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN =", (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelTelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN ", (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
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
             * String inStr =
             * _formatter.generateRequest(AlcatelI.ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,AlcatelI.ACTION_TXN_CANCEL);
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
