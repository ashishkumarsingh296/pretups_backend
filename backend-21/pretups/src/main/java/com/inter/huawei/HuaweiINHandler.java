package com.inter.huawei;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.AmbiguousStatusLog;

;

/**
 * @HuaweiINHandler.java
 *                       Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Ashish K Jan 24, 2007 Initial Creation
 *                       ------------------------------------------------------
 *                       ------------------------------------------
 *                       This class is the Handler class for the HUAWEI
 *                       interface
 */
public class HuaweiINHandler implements InterfaceHandler {

    private Log _log = LogFactory.getLog(HuaweiINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private HuaweiRequestFormatter _formatter = null;
    private String _instanceId = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;
    private int _checkAmbRetryCount = 0;
    private String _errorMsg = null;

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
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

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
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("validate", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(HuaweiI.ACTION_ACCOUNT_INFO);
            // sending the AccountInfo request to IN along with validate action
            // defined in HuaweiI interface
            sendRequestToIN(HuaweiI.ACTION_ACCOUNT_INFO);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // get value of BALANCE from response map (BALANCE was set in
            // response map in sendRequestToIN method.)
            String amountStr = (String) _responseMap.get("BALANCE");
            try {
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(huaweiMultiplicationFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // set OLD_EXPIRY_DATE in request map as returned from _responseMap.
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("ACTIVESTOP"), "yyyyMMdd"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("SUSPENDSTOP"), "yyyyMMdd"));
            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping();
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be:" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
            TransactionLog.log(_referenceID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_INVAL, (String) _requestMap.get("IN_TOTAL_TIME"), (String) _requestMap.get("IN_START_TIME"), (String) _requestMap.get("IN_END_TIME"));
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
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetching the HUAWEI_MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("credit", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
            _requestMap.put("HUAWEI_MULT_FACTOR", huaweiMultiplicationFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters(HuaweiI.ACTION_RECHARGE_CREDIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            try {
                huaweiMultFactorDouble = Double.parseDouble(huaweiMultiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, huaweiMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("credit", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "HuaweiINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Recharge request to IN along with recharge action
            // defined in HuaweiI interface
            sendRequestToIN(HuaweiI.ACTION_RECHARGE_CREDIT);
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            if ((be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))) {
                try {
                    String ambStatusCheckReq = (String) FileCache.getValue(_interfaceID, "CHECK_AMB_STATUS_REQUIRED");
                    if (!InterfaceUtil.isNullString(ambStatusCheckReq) && "Y".equals(ambStatusCheckReq)) {
                        p_requestMap.put("AMB_IN_START_TIME", (String) p_requestMap.get("RETRY_IN_START_TIME"));
                        // p_requestMap.put("AMB_IN_START_TIME","20091124090354");
                        checkAmbigousStatus(p_requestMap);
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                    } else
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                } catch (BTSLBaseException be1) {
                    if (!(be1.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                        throw be1;
                    if ((be1.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))) {
                        try {
                            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                            _requestMap.put("TRANSACTION_TYPE", "CR");
                            // handleCancelTransaction();
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        } catch (BTSLBaseException bte) {
                            throw bte;
                        } catch (Exception e) {
                            e.printStackTrace();
                            _log.error("credit", "Exception e:" + e.getMessage());
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                    }
                } catch (Exception e1) {
                    // e.printStackTrace();
                    _log.error("credit", "Exception e:" + e1.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e1.getMessage());
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
            TransactionLog.log(_referenceID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_INTOP, (String) _requestMap.get("IN_TOTAL_TIME"), (String) _requestMap.get("IN_START_TIME"), (String) _requestMap.get("IN_END_TIME"));
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
            _log.debug("creditAdjust", " Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;// assign map passed from InterfaceModule to
                                   // _requestMap(instance var)
        double systemAmtDouble = 0;
        String amountStr = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();

            _inTXNID = InterfaceUtil.getINTransactionID();// Generate the IN
                                                          // transaction id and
                                                          // set in _requestMap
            _requestMap.put("IN_TXN_ID", _inTXNID);// get TRANSACTION_ID from
                                                   // request map (which has
                                                   // been passed by controller)
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");// get msisdn from
                                                         // request map
            // Get the Huawei Multiplication factor from the FileCache with the
            // help of interface id.
            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");
            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("creditAdjust", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(HuaweiI.ACTION_IMMEDIATE_DEBIT);
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            try {
                double huaweiMultFactorDouble = Double.parseDouble(huaweiMultiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, huaweiMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("creditAdjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "HuaweiINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "transfer_amount:" + amountStr + " huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in HuaweiI interface
            sendRequestToIN(HuaweiI.ACTION_IMMEDIATE_DEBIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            try {
                String postBalanceStr = (String) _responseMap.get("LASTBALANCE");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, Double.parseDouble(huaweiMultiplicationFactor));
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from response is not NUMERIC while parsing the balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
            throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
            TransactionLog.log(_referenceID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), PretupsI.TXN_LOG_TXNSTAGE_INTOP, (String) _requestMap.get("IN_TOTAL_TIME"), (String) _requestMap.get("IN_START_TIME"), (String) _requestMap.get("IN_END_TIME"));
        }
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
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

            _inTXNID = InterfaceUtil.getINTransactionID();// Generate the IN
                                                          // transaction id and
                                                          // set in _requestMap
            _requestMap.put("IN_TXN_ID", _inTXNID);// get TRANSACTION_ID from
                                                   // request map (which has
                                                   // been passed by controller)
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");// get msisdn from
                                                         // request map
            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID, "HUAWEI_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            if (InterfaceUtil.isNullString(huaweiMultiplicationFactor)) {
                _log.error("debitAdjust", "HUAWEI_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "debitAadjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(HuaweiI.ACTION_IMMEDIATE_DEBIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                double huaweiMultFactorDouble = Double.parseDouble(huaweiMultiplicationFactor);
                interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, huaweiMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("debitAadjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "HuaweiINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            String transferDebitAmount = "-" + amountStr;
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "transfer_amount:" + transferDebitAmount + " huaweiMultiplicationFactor:" + huaweiMultiplicationFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", transferDebitAmount);

            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("Y".equals(validateRequired)) {
                String prevBalanceAmountStr = (String) p_requestMap.get("INTERFACE_PREV_BALANCE");
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "prevBalanceAmountStr:" + prevBalanceAmountStr);
                if (InterfaceUtil.isNullString(prevBalanceAmountStr) || !InterfaceUtil.isNumeric(prevBalanceAmountStr)) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE present in the requestMap is either null or not numeric");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT present in the requestMap is either null or not numeric");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                double prevBalanceAmount = Double.parseDouble(prevBalanceAmountStr.trim());
                // Checking the sufficient previous balance, if previous balance
                // is less than that of the debit amount, throw the exception.
                if (prevBalanceAmount < interfaceAmtDouble) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
                }
            }
            sendRequestToIN(HuaweiI.ACTION_IMMEDIATE_DEBIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            try {
                String postBalanceStr = (String) _responseMap.get("LASTBALANCE");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, Double.parseDouble(huaweiMultiplicationFactor));
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from response is not NUMERIC while parsing the balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // set POST_BALANCE_ENQ_SUCCESS as N in request map. why...????
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                _requestMap.put("TRANSACTION_TYPE", "DR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while debitAdjust e:" + e.getMessage());
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
        // The transaction log is commented because request string is not
        // contructed at this point.
        // TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"","action="+p_action+" _requestMap = "+_requestMap);
        String responseStr = null;
        String inRequestStr = null;
        SocketWrapper socketConnection = null;
        long startTime = 0;
        boolean isC2S = false;
        int retryCountConInvalid = 0;// Represent the Number of retries in case
                                     // of exception to get SocketWrapper
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "SLEEP_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            sleepTimeConInvalid = Long.parseLong(sleepTimeConInvalidStr);
            // Fetch the pool size from the INFile.
            String module = (String) _requestMap.get("MODULE");
            if (InterfaceErrorCodesI.MODULE_C2S.equalsIgnoreCase(module))
                isC2S = true;
            try {
                // get a SocketWrapper object from PoolManager.
                socketConnection = HuaweiPoolManager.getClientObject(_interfaceID, isC2S);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " socketConnection:" + socketConnection);
                /*
                 * Get the Txn id to be sent to IN
                 * String txnID = socketConnection.getINHeaderTxnID();
                 * Check the txnID whether txn id reaches the Max, if it reaches
                 * Max create new connection and add in the list.
                 * if(_log.isDebugEnabled())
                 * _log.debug("sendRequestToIN","txnID: "+txnID);
                 */
                busyList = (Vector) HuaweiPoolManager._busyBucket.get(_interfaceID);// get
                                                                                    // busy
                                                                                    // and
                                                                                    // free
                                                                                    // pool
                                                                                    // from
                                                                                    // pool
                                                                                    // mgr.
                freeList = (Vector) HuaweiPoolManager._freeBucket.get(_interfaceID);
                // Get the Txn id to be sent to IN
                String txnID = socketConnection.getINHeaderTxnID();
                if (socketConnection.isDestroyed()) {
                    try {
                        SocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                        txnID = socketConnection.getINHeaderTxnID();
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        e.printStackTrace();
                        _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Exception occurs while getting new Client object");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "txnID: " + txnID);

                // In creditAdjust (sender credit back )don't check interface
                // status, simply send the request to IN.
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                    _isSameRequest = true;
                    checkInterfaceB4SendingRequest();
                }
                // Check the txnID whether txn id reaches the Max, if it reaches
                // Max create new connection and add in the list.
                if (HuaweiI.MAX_TXN_REACH.equals(txnID)) {
                    try {
                        // destroy the older socket whose MAX limit of
                        // transaction id is reached.//Confirm for the LOG OUT
                        // request.
                        if (socketConnection != null)
                            socketConnection.destroy();
                        SocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                        txnID = socketConnection.getINHeaderTxnID();
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        e.printStackTrace();
                        _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Exception occurs while getting new Client object");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
                _requestMap.put("IN_HEADER_TXN_ID", txnID);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " txnID: " + txnID);
                // get session id associated with socketConnection by calling
                // generateSessionID of SocketWrapper class.
                // put session id in request map. if it is null handle event and
                // throw exception.
                sessionID = socketConnection.getSessionID();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " getting sessionID : " + sessionID);
                if (InterfaceUtil.isNullString(sessionID)) {
                    _log.error("sendRequestToIN", "Session id obtained from socket wrapper is NULL");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Session id obtained from socket wrapper is NULL");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("SESSIONID", sessionID);
                String inReconID = (String) _requestMap.get("IN_RECON_ID");
                if (inReconID == null)
                    inReconID = _inTXNID;
            } catch (BTSLBaseException be) {
                _log.error("sendRequestToIN", "Error while attempt to get SocketConnection object from PoolManager.");
                if (HuaweiI.ACTION_CHECK_AMB_STATUS == p_action) {
                    _errorMsg = be.getMessage();
                    _log.error("sendRequestToIN", "Error while attempt to get SocketConnection object from PoolManager." + _referenceID + ", ACTION=" + p_action);
                    AmbiguousStatusLog.log(_referenceID, (String) _requestMap.get("IN_RECON_ID"), String.valueOf(_checkAmbRetryCount), _msisdn, "", ", ErrorMsg=" + _errorMsg);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }
                throw be;
            } catch (Exception e) {
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Exception occurs while getting new Client object");
                if (HuaweiI.ACTION_CHECK_AMB_STATUS == p_action) {
                    _errorMsg = e.getMessage();
                    AmbiguousStatusLog.log(_referenceID, (String) _requestMap.get("IN_RECON_ID"), String.valueOf(_checkAmbRetryCount), _msisdn, "", ", ErrorMsg=" + _errorMsg);
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }
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
                    // Read the Lag or Lead time from the INFile
                    String startLagLead = FileCache.getValue(_interfaceID, "LAG_OR_LEAD");
                    String endLagLead = FileCache.getValue(_interfaceID, "END_LAG_OR_LEAD");
                    if (InterfaceUtil.isNullString(startLagLead))
                        startLagLead = "0";
                    if (InterfaceUtil.isNullString(endLagLead))
                        endLagLead = "0";
                    Date date = new Date(startTime + Long.parseLong(startLagLead));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String currentDateStr = sdf.format(date);
                    _requestMap.put("RETRY_IN_START_TIME", currentDateStr);
                    _requestMap.put("LAG_OR_LEAD", startLagLead);
                    _requestMap.put("END_LAG_OR_LEAD", endLagLead);
                    // Send message to IN.
                    out.write(inRequestStr.getBytes());
                    out.flush();
                    // break;
                } catch (BTSLBaseException be) {
                    if (HuaweiI.ACTION_CHECK_AMB_STATUS == p_action) {
                        _errorMsg = be.getMessage();
                        AmbiguousStatusLog.log(_referenceID, (String) _requestMap.get("IN_RECON_ID"), String.valueOf(_checkAmbRetryCount), _msisdn, "", ", ErrorMsg=" + _errorMsg);
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                    }
                    throw be;
                } catch (Exception e) {
                    // Check, retry count reaches to maximum attempts, throw
                    // exception.
                    if (retryCount > retryCountConInvalid) {
                        _log.error("sendRequestToIN", "Error while writing on output stream.");
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[sendRequestToIN]", _interfaceID, "", "", "Number of retry reached to MAX" + _interfaceID);
                        if (HuaweiI.ACTION_CHECK_AMB_STATUS == p_action) {
                            _errorMsg = e.getMessage();
                            AmbiguousStatusLog.log(_referenceID, (String) _requestMap.get("IN_RECON_ID"), String.valueOf(_checkAmbRetryCount), _msisdn, "", ", ErrorMsg=" + _errorMsg);
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                        }
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                    try {
                        // get a new SocketWrapper object
                        // remove old SocketWrapper object from busy pool.
                        // add new SocketWrapper object in busy pool.
                        // get txn id and session id
                        // generate request
                        // Destroy the previous socket connection before ceating
                        // a new one.
                        if (socketConnection != null)
                            socketConnection.destroy();
                        SocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
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
                    // get BufferedReader from SocketWrapper
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
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Huawei IN is taking more time than the warning threshold. Total Time taken is: " + (endTime - startTime));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", " Error occoured while reading response message Exception e :" + e.getMessage() + " creating new connection and replacing it with the older one, Time =" + System.currentTimeMillis());
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
                        SocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(socketConnection);
                        socketConnection = newSocketConnection;
                        busyList.add(socketConnection);
                    } catch (Exception ex) {
                        _log.error("sendRequestToIN", "Exception ex:" + ex.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while gettting new connnection in case of read time out,Exception e :" + e.getMessage());
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                    }
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " ACTION = " + p_action, "Error occoured while reading response message Exception e :" + e.getMessage());
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    _requestMap.put("IN_TOTAL_TIME", String.valueOf(totalTime));
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    _log.error("sendRequestToIN", "Request sent to IN[startTime] :" + startTime + " Response read time[endTime] :" + endTime);
                    // Free the connection from the busy list and add it to the
                    // busyList.
                    busyList.remove(socketConnection);
                    freeList.add(socketConnection);
                    isConnectionFree = true;
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
                }
                responseStr = responseBuffer.toString();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr : " + responseStr);
                // write _inTXNID, _referenceID, NETWORK_CODE, action, request
                // map in transaction log
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Request String :" + inRequestStr + " Response string:" + responseStr, (String) _requestMap.get("IN_START_TIME"), " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                // if response string is null then put status as AMBIGOUS in
                // request map. Handle event and throw exception.
                // else parse response.
                if (InterfaceUtil.isNullString(responseStr)) {
                    _requestMap.put("status", InterfaceErrorCodesI.NULL_INTERFACE_RESPONSE);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Blank response from Huawei IN on INSTANCE_ID " + _instanceId + " for connection " + socketConnection.getSessionID());
                    _log.error("sendRequestToIN", "Blank response from Huawei IN on INSTANCE_ID " + _instanceId + " for connection " + socketConnection.getSessionID());
                    SocketWrapper newSocketConnection = null;
                    try {
                        newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                    } catch (Exception ex) {
                        _errorMsg = ex.getMessage();
                        _log.error("sendRequestToIN", "Error occoured while gettting new connnection in case of transaction id mismatch. So leaving the in valid connection as it is in busy list and marking transaction as AMBIGUOUS.Exception ex:" + ex.getMessage());
                        // EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"HuaweiINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String)
                        // _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
                        // " ACTION = "+p_action,"Error occoured while gettting new connnection in case of read time out,Exception e :"+ex.getMessage());
                        if (HuaweiI.ACTION_CHECK_AMB_STATUS == p_action) {
                            _errorMsg = ex.getMessage();
                            AmbiguousStatusLog.log(_referenceID, (String) _requestMap.get("IN_RECON_ID"), String.valueOf(_checkAmbRetryCount), _msisdn, "", ", ErrorMsg=" + _errorMsg);
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                        }
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.NULL_INTERFACE_RESPONSE);
                    }
                    if (socketConnection != null)
                        socketConnection.destroy();
                    freeList.remove(socketConnection);
                    socketConnection = newSocketConnection;
                    freeList.add(socketConnection);
                    isConnectionFree = true;
                    if (HuaweiI.ACTION_CHECK_AMB_STATUS == p_action) {
                        AmbiguousStatusLog.log(_referenceID, (String) _requestMap.get("IN_RECON_ID"), String.valueOf(_checkAmbRetryCount), _msisdn, "", ", ErrorMsg=" + _errorMsg);
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                    }
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.NULL_INTERFACE_RESPONSE);
                }
                // call parseResponse of formater to get response map
                _responseMap = new HashMap();// instantiate response mapwhich is
                                             // a instance var
                if (p_action == HuaweiI.ACTION_CHECK_AMB_STATUS) {
                    String reconID = (String) _requestMap.get("IN_RECON_ID");
                    _responseMap = _formatter.parseResponse(p_action, responseStr, reconID);
                } else
                    _responseMap = _formatter.parseResponse(p_action, responseStr, null);
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

                // commented code may be used in future to support on line
                // cancel request
                // If response of cancel request is Successful, then throw
                // exception mapped in IN FILE
                /*
                 * if(HuaweiI.ACTION_TXN_CANCEL == p_stage)
                 * {
                 * _requestMap.put("CANCEL_RESP_STATUS",result);
                 * cancelTxnStatus =
                 * InterfaceUtil.getErrorCodeFromMapping(_requestMap
                 * ,result,"SYSTEM_STATUS_MAPPING");
                 * throw new BTSLBaseException(cancelTxnStatus);
                 * }
                 */

                String huaweiTxnID = (String) _requestMap.get("IN_HEADER_TXN_ID");

                String txnID = (String) _responseMap.get("transaction_id");
                // get status from response map.
                // status is ok then log txn else handle event and throw
                // exception.
                Object[] ambList = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "CON_INVAL_CASES")).split(",");
                if (!HuaweiI.RESULT_OK.equals(status)) {
                    if (HuaweiI.SUBSCRIBER_NOT_FOUND.equals(status)) {
                        _log.error("sendRequestToIN", "Error in response SUBSCRIBER_NOT_FOUND");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " SUBSCRIBER_NOT_FOUND AT IN");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);// MSISDN
                                                                                                                              // Not
                                                                                                                              // Found
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
                        _log.error("sendRequestToIN", "Status from the IN on INSTANCE ID " + _instanceId + " for connection with sessionID=" + socketConnection.getSessionID() + " is:: " + status);

                        if (socketConnection != null)
                            socketConnection.destroy();
                        SocketWrapper newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
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
                    _log.error("sendRequestToIN", "Transaction id set in the request header [" + huaweiTxnID + "] does not matched with the transaction id fetched from response[" + txnID + "].So removing invalid connection from busy list and adding a fresh connection in free list. Transaction is marked as Ambiguous.");
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " p_action = " + p_action, "Transaction id set in the request header [" + txnID + "] does not matched with the transaction id fetched from response[" + huaweiTxnID + "],Hence marking the transaction as AMBIGUOUS");
                    SocketWrapper newSocketConnection = null;
                    try {
                        newSocketConnection = HuaweiPoolManager.getNewClientObject(_interfaceID);
                    } catch (Exception ex) {
                        _errorMsg = ex.getMessage();
                        _log.error("sendRequestToIN", "Error occoured while gettting new connnection in case of transaction id mismatch. So leaving the in valid connection as it is in busy list and marking transaction as AMBIGUOUS.Exception ex:" + ex.getMessage());
                        // EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"HuaweiINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String)
                        // _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
                        // " ACTION = "+p_action,"Error occoured while gettting new connnection in case of read time out,Exception e :"+ex.getMessage());
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                    }
                    if (socketConnection != null)
                        socketConnection.destroy();
                    freeList.remove(socketConnection);
                    socketConnection = newSocketConnection;
                    freeList.add(socketConnection);
                    isConnectionFree = true;
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                }
                break;
            }
            if (p_action == HuaweiI.ACTION_CHECK_AMB_STATUS)
                _errorMsg = InterfaceErrorCodesI.SUCCESS;
        } catch (BTSLBaseException be) {
            _errorMsg = be.getMessage();
            _log.error("sendRequestToIN", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _errorMsg = e.getMessage();
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (!isConnectionFree && (busyList != null && freeList != null)) {
                busyList.remove(socketConnection);
                freeList.add(socketConnection);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
            }
            // If action is ACTION_CHECK_AMB_STATUS, then write the _inTXNID,
            // _reconID, action, response in AmbiguousStatusLog
            if (p_action == HuaweiI.ACTION_CHECK_AMB_STATUS)
                AmbiguousStatusLog.log(_referenceID, (String) _requestMap.get("IN_RECON_ID"), String.valueOf(_checkAmbRetryCount), _msisdn, "Request String :" + inRequestStr + " Response string:" + responseStr, " Response Map = " + _responseMap + " Request Map=" + _requestMap + ", ErrorMsg=" + _errorMsg);

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
            String versionNumber = FileCache.getValue(_interfaceID, "VERSION_NUMBER");
            if (InterfaceUtil.isNullString(versionNumber)) {
                _log.error("setInterfaceParameters", "Value of VERSION_NUMBER is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "VERSION_NUMBER is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("VERSION_NUMBER", versionNumber.trim());
            // get TERM from IN File Cache and put it it request map
            String term = FileCache.getValue(_interfaceID, "TERM");
            if (InterfaceUtil.isNullString(term)) {
                _log.error("setInterfaceParameters", "Value of TERM is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TERM is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("TERM", term.trim());
            String msgHeaderLanguage = FileCache.getValue(_interfaceID, "MSG_HEAD_LANGUAGE");
            if (InterfaceUtil.isNullString(msgHeaderLanguage)) {
                _log.error("setInterfaceParameters", "Value of MSG_HEAD_LANGUAGE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MSG_HEAD_LANGUAGE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("MSG_HEAD_LANGUAGE", msgHeaderLanguage.trim());
            String dlgLgn = FileCache.getValue(_interfaceID, "DLGLGN");
            if (InterfaceUtil.isNullString(dlgLgn)) {
                _log.error("setInterfaceParameters", "Value of DLGLGN is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DLGLGN is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("DLGLGN", dlgLgn.trim());
            // get RSV from IN File Cache and put it it request map
            String rsv = FileCache.getValue(_interfaceID, "RSV");
            if (InterfaceUtil.isNullString(rsv)) {
                _log.error("setInterfaceParameters", "Value of RSV is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RSV is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("RSV", rsv.trim());
            // get DLGCTRL from IN File Cache and put it it request map
            String dlgCtrl = FileCache.getValue(_interfaceID, "DLGCTRL");
            if (InterfaceUtil.isNullString(dlgCtrl)) {
                _log.error("setInterfaceParameters", "Value of DLGCTRL is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DLGCTRL is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("DLGCTRL", dlgCtrl.trim());
            // get TSRV from IN File Cache and put it it request map
            String tsrv = FileCache.getValue(_interfaceID, "TSRV");
            if (InterfaceUtil.isNullString(tsrv)) {
                _log.error("setInterfaceParameters", "Value of TSRV is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TSRV is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("TSRV", tsrv.trim());
            // get START_FLAG from IN File Cache and put it it request map
            String startFlag = FileCache.getValue(_interfaceID, "START_FLAG");
            if (InterfaceUtil.isNullString(startFlag)) {
                _log.error("setInterfaceParameters", "Value of START_FLAG is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "START_FLAG is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("START_FLAG", startFlag.trim());
            // get DLGCON from IN File Cache and put it it request map
            String dlgCon = FileCache.getValue(_interfaceID, "DLGCON");
            if (InterfaceUtil.isNullString(startFlag)) {
                _log.error("setInterfaceParameters", "Value of DLGCON is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "DLGCON is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("DLGCON", dlgCon.trim());
            // set the Account info releted interface parameters.
            if (HuaweiI.ACTION_ACCOUNT_INFO == p_action) {
                String acntInfoCommand = FileCache.getValue(_interfaceID, "ACNTINFO_COMMAND");
                if (InterfaceUtil.isNullString(acntInfoCommand)) {
                    _log.error("setInterfaceParameters", "Value of ACNTINFO_COMMAND is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ACNTINFO_COMMAND is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("ACNTINFO_COMMAND", acntInfoCommand.trim());
                // get ACCOUNT_INFO_SERIVICE from IN File Cache and put it it
                // request map
                String acntInfoService = FileCache.getValue(_interfaceID, "ACCOUNT_INFO_SERIVICE");
                if (InterfaceUtil.isNullString(acntInfoService)) {
                    _log.error("setInterfaceParameters", "Value of ACCOUNT_INFO_SERIVICE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ACCOUNT_INFO_SERIVICE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("ACCOUNT_INFO_SERIVICE", acntInfoService.trim());
                // get ACCOUNT_INFO_PARAMS from IN File Cache and put it it
                // request map
                String acntInfoParams = FileCache.getValue(_interfaceID, "ACCOUNT_INFO_PARAMS");
                if (InterfaceUtil.isNullString(acntInfoParams)) {
                    _log.error("setInterfaceParameters", "Value of ACCOUNT_INFO_PARAMS is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ACCOUNT_INFO_PARAMS is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("ACCOUNT_INFO_PARAMS", acntInfoParams.trim());
                if (_log.isDebugEnabled())
                    _log.debug("setInterfaceParameters", "acntInfoParams=" + acntInfoParams + " acntInfoService=" + acntInfoService + " acntInfoCommand=" + acntInfoCommand + " acntIfoParams=" + acntInfoParams + " versionNumber=" + versionNumber + " term=" + term + " msgHeaderLanguage=" + msgHeaderLanguage + " dlgLgn=" + dlgLgn + " rsv=" + rsv + " dlgCtrl=" + dlgCtrl + " tsrv=" + tsrv + " startFlag=" + startFlag + " dlgCon=" + dlgCon);
            } else if (HuaweiI.ACTION_RECHARGE_CREDIT == p_action) {
                String rechargeCommand = FileCache.getValue(_interfaceID, "RECHARGE_COMMAND");
                if (InterfaceUtil.isNullString(rechargeCommand)) {
                    _log.error("setInterfaceParameters", "Value of RECHARGE_COMMAND is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RECHARGE_COMMAND is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("RECHARGE_COMMAND", rechargeCommand.trim());

                // get CHRGTYPE from IN File Cache and put it it request map
                String chargeType = FileCache.getValue(_interfaceID, "CHRGTYPE");
                if (InterfaceUtil.isNullString(chargeType)) {
                    _log.error("setInterfaceParameters", "Value of CHRGTYPE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CHRGTYPE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CHRGTYPE", chargeType.trim());

                // This defines that operator's query for RECHARGE_SERIVICE
                String rechargeService = FileCache.getValue(_interfaceID, "RECHARGE_SERIVICE");
                if (InterfaceUtil.isNullString(rechargeService)) {
                    _log.error("setInterfaceParameters", "Value of RECHARGE_SERIVICE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RECHARGE_SERIVICE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("RECHARGE_SERIVICE", rechargeService.trim());
                if (_log.isDebugEnabled())
                    _log.debug("setInterfaceParameters", " rechargeService=" + rechargeService + " chargeType=" + chargeType + " rechargeCommand=" + rechargeCommand + " versionNumber=" + versionNumber + " term=" + term + " msgHeaderLanguage=" + msgHeaderLanguage + " dlgLgn=" + dlgLgn + " rsv=" + rsv + " dlgCtrl=" + dlgCtrl + " tsrv=" + tsrv + " startFlag=" + startFlag + " dlgCon=" + dlgCon);
            } else if (HuaweiI.ACTION_IMMEDIATE_DEBIT == p_action) {
                String modifyBalanceCommand = FileCache.getValue(_interfaceID, "MODIFY_BALANCE_COMMAND");
                if (InterfaceUtil.isNullString(modifyBalanceCommand)) {
                    _log.error("setInterfaceParameters", "Value of MODIFY_BALANCE_COMMAND is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MODIFY_BALANCE_COMMAND is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("MODIFY_BALANCE_COMMAND", modifyBalanceCommand.trim());

                // This defines that operator's query for
                // MODIFY_BALANCE_SERIVICE
                String modifyBalanceService = FileCache.getValue(_interfaceID, "MODIFY_BALANCE_SERIVICE");
                if (InterfaceUtil.isNullString(modifyBalanceService)) {
                    _log.error("setInterfaceParameters", "Value of MODIFY_BALANCE_SERIVICE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MODIFY_BALANCE_SERIVICE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("MODIFY_BALANCE_SERIVICE", modifyBalanceService.trim());

                _instanceId = FileCache.getValue(_interfaceID, "INSTANCE_ID");
                if (InterfaceUtil.isNullString(_instanceId)) {
                    _log.error("setInterfaceParameters", "Value of INSTANCE_ID is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "INSTANCE_ID is not defined in the INFile.");
                    // throw new
                    // BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("INSTANCE_ID", _instanceId);

                String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
                if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                    _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

                String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
                if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

                String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
                if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

                String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
                if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

                String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
                if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                    _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

                String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
                if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                    _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

                String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
                if (InterfaceUtil.isNullString(cancelNA)) {
                    _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CANCEL_NA", cancelNA.trim());

                if (_log.isDebugEnabled())
                    _log.debug("setInterfaceParameters", " modifyBalanceCommand=" + modifyBalanceCommand + " modifyBalanceService=" + modifyBalanceService + " versionNumber=" + versionNumber + " term=" + term + " msgHeaderLanguage=" + msgHeaderLanguage + " dlgLgn=" + dlgLgn + " rsv=" + rsv + " dlgCtrl=" + dlgCtrl + " tsrv=" + tsrv + " startFlag=" + startFlag + " dlgCon=" + dlgCon);
            } else if (HuaweiI.ACTION_CHECK_AMB_STATUS == p_action) {

                String ambInfoCommand = FileCache.getValue(_interfaceID, "AMBIGUOUS_INFO_COMMAND");
                if (InterfaceUtil.isNullString(ambInfoCommand)) {
                    _log.error("setInterfaceParameters", "Value of AMBIGUOUS_INFO_COMMAND is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "AMBIGUOUS_INFO_COMMAND is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("AMBIGUOUS_INFO_COMMAND", ambInfoCommand.trim());
                // get ACCOUNT_INFO_SERIVICE from IN File Cache and put it it
                // request map
                String ambInfoService = FileCache.getValue(_interfaceID, "AMBIGUOUS_INFO_SERIVICE");
                if (InterfaceUtil.isNullString(ambInfoService)) {
                    _log.error("setInterfaceParameters", "Value of AMBIGUOUS_INFO_SERIVICE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "AMBIGUOUS_INFO_SERIVICE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("AMBIGUOUS_INFO_SERIVICE", ambInfoService.trim());
                // get ACCOUNT_INFO_PARAMS from IN File Cache and put it it
                // request map
                String ambInfoParams = FileCache.getValue(_interfaceID, "AMBIGUOUS_INFO_PARAMS");
                if (InterfaceUtil.isNullString(ambInfoParams)) {
                    _log.error("setInterfaceParameters", "Value of AMBIGUOUS_INFO_PARAMS is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "AMBIGUOUS_INFO_PARAMS is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("AMBIGUOUS_INFO_PARAMS", ambInfoParams.trim());
                if (_log.isDebugEnabled())
                    _log.debug("setInterfaceParameters", "ambInfoParams=" + ambInfoParams + " ambInfoService=" + ambInfoService + " ambInfoCommand=" + ambInfoCommand + " versionNumber=" + versionNumber + " term=" + term + " msgHeaderLanguage=" + msgHeaderLanguage + " dlgLgn=" + dlgLgn + " rsv=" + rsv + " dlgCtrl=" + dlgCtrl + " tsrv=" + tsrv + " startFlag=" + startFlag + " dlgCon=" + dlgCon);
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "HuaweiINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "while setting the language mapping get Exception=" + e.getMessage());
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
            // into reconciliation log and throw exception (This exception tells
            // the final status of transaction which was ambiguous) which would
            // be handled by validate, credit or debitAdjust methods
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
             * _formatter.generateRequest(HuaweiI.ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,HuaweiI.ACTION_TXN_CANCEL);
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

    /**
     * This method is to query re-charge logs for a subscriber, if transaction
     * found successful on IN then
     * it will mark the same transaction successful in Easy re-charge system.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private void checkAmbigousStatus(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("checkAmbigousStatus", "Enetered p_requestMap =" + p_requestMap);
        int noOfRetry = 1;
        HashMap requestMap = null;
        try {
            ++_checkAmbRetryCount;
            requestMap = p_requestMap;
            if (!InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "CHECK_AMB_RETRY_COUNT")))
                noOfRetry = Integer.parseInt(FileCache.getValue(_interfaceID, "CHECK_AMB_RETRY_COUNT"));
            // Set the interface parameters into requestMap
            setInterfaceParameters(HuaweiI.ACTION_CHECK_AMB_STATUS);
            // sending the request to IN for ambiguous transaction status
            sendRequestToIN(HuaweiI.ACTION_CHECK_AMB_STATUS);
            if (_log.isDebugEnabled())
                _log.debug("checkAmbigousStatus", "_responseMap=" + _responseMap);
            String status = (String) _responseMap.get("status");
            // If status received is not equal to zero, then mark the
            // transaction as AMBIGUOUS.
            if (!InterfaceUtil.isNullString(status)) {
                if (Integer.parseInt(status) != 0)
                    throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// AMBIGUOUS
                else if (Integer.parseInt(status) == 0) {
                    String rowNum = (String) _responseMap.get("ROWNUM");
                    // If ROWNUM==0, that's mean transaction is fail at the IN.
                    if (!InterfaceUtil.isNullString(rowNum)) {
                        if (Integer.parseInt(rowNum) == 0)
                            throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.CHECK_AMB_STATUS_FAIL);// FAIL
                        else if (Integer.parseInt(rowNum) > 0) {
                            String finished = (String) _responseMap.get("FINISHED");
                            if (!InterfaceUtil.isNullString(finished)) {
                                if (Integer.parseInt(finished) == 0)
                                    throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// AMBIGUOUS
                                else if (Integer.parseInt(finished) == 1) {
                                    String sendTxnId = (String) requestMap.get("IN_RECON_ID");
                                    String batchNo = (String) _responseMap.get("BATCHNO");
                                    String serialNo = (String) _responseMap.get("SERIALNO");
                                    String recevTxnId = batchNo + serialNo;
                                    String errorType = (String) _responseMap.get("ERRORTYPE");
                                    if (InterfaceUtil.isNullString(sendTxnId) || InterfaceUtil.isNullString(batchNo) || InterfaceUtil.isNullString(serialNo) || InterfaceUtil.isNullString(errorType))
                                        throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// AMBIGUOUS
                                    // If ROWNUM==1, then match the sent and
                                    // received transaction id and ERRORTYPE
                                    if (Integer.parseInt(rowNum) == 1) {
                                        if (sendTxnId.equals(recevTxnId) && Integer.parseInt(errorType) == 0) {
                                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                                            _errorMsg = InterfaceErrorCodesI.SUCCESS;
                                        } else if (sendTxnId.equals(recevTxnId) && Integer.parseInt(errorType) != 0)
                                            throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.CHECK_AMB_STATUS_FAIL);// FAIL
                                        else
                                            throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// FAIL
                                    }
                                    // If ROWNUM>1, i.e. more than one record
                                    // found for that MSISDN. If transaction id
                                    // for received and sent is same, then check
                                    // the ERRORTYPE
                                    else if (Integer.parseInt(rowNum) > 1 && "Y".equals((String) _responseMap.get("TXNID_MATCH"))) {
                                        if (Integer.parseInt(errorType) == 0) {
                                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                                            _errorMsg = InterfaceErrorCodesI.SUCCESS;
                                        } else if (Integer.parseInt(errorType) != 0)
                                            throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.CHECK_AMB_STATUS_FAIL);// FAIL
                                    }
                                    // If transaction ids are not same, then
                                    // mark the transaction fail, because in
                                    // case of fail transaction no entry goes to
                                    // IN record.
                                    else
                                        throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// FAIL
                                }
                            } else
                                throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// AMBIGUOUS
                        }
                    } else
                        throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// AMBIGUOUS
                }
            } else
                throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.AMBIGOUS);// AMBIGUOUS
        }// end of try block
        catch (BTSLBaseException be) {
            _errorMsg = be.getMessage();
            _log.error("checkAmbigousStatus", "BTSLBaseException be:" + be.getMessage());

            if ((be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))) {
                if (_checkAmbRetryCount < noOfRetry) {
                    Thread.sleep(60000);
                    _errorMsg = null;
                    checkAmbigousStatus(p_requestMap);
                } else {
                    _errorMsg = InterfaceErrorCodesI.FAIL;
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    throw new BTSLBaseException(this, "checkAmbigousStatus", InterfaceErrorCodesI.FAIL);// FAIL
                }
            } else
                throw be;
        } catch (Exception e) {
            _log.error("checkAmbigousStatus", "Exception e=" + e);
            _errorMsg = e.getMessage();
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("checkAmbigousStatus", "Exited No of retry=" + _checkAmbRetryCount + " and _errorMsg=" + _errorMsg);
        }// end of finally
    }// end of checkAmbigousStatus
}
