/*
 * Created on Jun 10, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.comversetr;

import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Date;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.comversetr.comversetrstub.BalanceCreditAccount;
import com.btsl.pretups.inter.comversetr.comversetrstub.ServiceSoap;
import com.btsl.pretups.inter.comversetr.comversetrstub.SubscriberRetrieve;
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

/**
 * @author dhiraj.tiwari
 * 
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ComverseTRINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(ComverseTRINHandler.class.getName());
    private ComverseTRRequestFormatter _formatter = null;
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inReconID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;
    private String _serviceType = null;

    /**
	 * 
	 */
    public ComverseTRINHandler() {
        _formatter = new ComverseTRRequestFormatter();
    }

    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _userType = (String) _requestMap.get("USER_TYPE");
            _msisdn = (String) _requestMap.get("MSISDN");
            _serviceType = (String) _requestMap.get("REQ_SERVICE");
            if (!BTSLUtil.isNullString(_msisdn)) {
                _msisdn = InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }
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
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTRI.ACTION_ACCOUNT_DETAILS);

            // sending the AccountInfo request to IN along with validate action
            // defined in Huawei84I interface
            sendRequestToIN(ComverseTRI.ACTION_ACCOUNT_DETAILS);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // get value of BALANCE from response map (BALANCE was set in
            // response map in sendRequestToIN method.)
            String amountStr = (Double) _responseMap.get("RESP_BALANCE") + "";

            try {
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            _requestMap.put("IN_CURRENCY", (String) _responseMap.get("IN_CURRENCY"));
            // _requestMap.put("CAL_OLD_EXPIRY_DATE",String.valueOf(((Calendar)_responseMap.get("OLD_EXPIRY_DATE")).getTimeInMillis()));
            String expDate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
            _requestMap.put("OLD_EXPIRY_DATE", expDate);
            _requestMap.put("CAL_OLD_EXPIRY_DATE", String.valueOf(((Calendar) _responseMap.get("OLD_EXPIRY_DATE")).getTimeInMillis()));
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("ACCOUNT_STATUS"));
            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("SERVICE_CLASS"));
            _requestMap.put("AON", _responseMap.get("AON"));
            try {
                String lmbAccoutnMissing = (String) _responseMap.get("LMB_ACC_MISSING");
                if ((!BTSLUtil.isNullString(lmbAccoutnMissing)) && lmbAccoutnMissing.equals("Y")) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LMB Account is not associated with subscriber:" + _msisdn);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            String lmbAmountStr = (Double) _responseMap.get("LMB_ALLOWED_VALUE") + "";
            try {
                lmbAmountStr = InterfaceUtil.getSystemAmountFromINAmount(lmbAmountStr, Double.parseDouble(multFactor));
                _requestMap.put("LMB_ALLOWED_VALUE", lmbAmountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LMB Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            HashMap tempMap = new HashMap();
            tempMap = (HashMap) _responseMap.get("BALANCE_MAP");
            for (Object key : tempMap.keySet()) {
                Object value = tempMap.get(key);
                _requestMap.put(key, value);
                if (_log.isDebugEnabled())
                    _log.debug("validate", "Map entries of Balance Map=" + key + " = " + value);
            }
            setLanguageFromMapping();
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validating the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
        }
    }

    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double huaweiMultFactorDouble = 0;
        String amountStr = null;
        // int retryCountCredit=0;
        _requestMap = p_requestMap;
        String _tempBal = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            /*
             * _interfaceClosureSupport=FileCache.getValue(_interfaceID,
             * "INTFCE_CLSR_SUPPORT");
             * if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
             * checkInterfaceB4SendingRequest();
             */
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);
            _msisdn = (String) _requestMap.get("MSISDN");
            _tempBal = (String) _requestMap.get("INTERFACE_PREV_BALANCE");
            _serviceType = (String) _requestMap.get("REQ_SERVICE");
            _requestMap.put("RETRY_BAL_QUERY", _tempBal);
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTRI.ACTION_RECHARGE_CREDIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            try {
                huaweiMultFactorDouble = Double.parseDouble(multFactor);
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTRINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            sendRequestToIN(ComverseTRI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                try {
                    String coreBalanceName = (String) _requestMap.get("CORE_BAL_NAME");
                    String previousCoreAmt = (String) _requestMap.get(coreBalanceName + "_RESP_BALANCE");
                    new ComverseTRINHandler().validate(_requestMap);
                    String newLMBAmt = (String) _requestMap.get(coreBalanceName + "_RESP_BALANCE");
                    if (Double.parseDouble(newLMBAmt) > Double.parseDouble(previousCoreAmt)) {
                        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    } else {
                        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                        throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                    }
                } catch (BTSLBaseException e1) {
                    throw e1;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }
        // post credit enquiry after promotion credit and cos update
        try {
            _requestMap.put("IN_START_TIME", "0");
            _requestMap.put("IN_END_TIME", "0");
            if (((PretupsI.YES).equals((String) _requestMap.get("ENQ_POSTBAL_ALLOW"))) && ((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS))
                postCreditEnquiry(_requestMap);
            // Lohit
            _requestMap.put("IN_POSTCREDIT_VAL_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
            throw e;
        }
    }

    /**
     * Method : creditAdjust
     * 
     * @param p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double huaweiMultFactorDouble = 0;
        String amountStr = null;
        String serviceType;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _referenceID);
            _msisdn = (String) _requestMap.get("MSISDN");
            _serviceType = (String) _requestMap.get("REQ_SERVICE");
            _inReconID = getINReconID();
            String _tempBal = (String) _requestMap.get("INTERFACE_PREV_BALANCE");
            _requestMap.put("RETRY_BAL_QUERY", _tempBal);
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);
            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTRI.ACTION_CREDIT_ADJUST);
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            serviceType = (String) _requestMap.get("REQ_SERVICE");
            if ((!BTSLUtil.isNullString(serviceType)) && serviceType.equals(PretupsI.SERVICE_TYPE_LEND_ME_BALANCE) && _requestMap.get("LMB_DEBIT") != null && _requestMap.get("LMB_DEBIT").equals(PretupsI.YES)) {
                try {
                    _requestMap.put("LMB_ACTION", ComverseTRI.ACTION_LMB_CREDIT_ADJUST);
                    LMBUpdate(_requestMap);
                    _requestMap.put("IN_LMB_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                    _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                } catch (BTSLBaseException be) {
                    _log.error("creditAdjust", "BTSLBaseException be=" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("creditAdjust", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While LMB update in credit adjust, get the Exception e:" + e.getMessage());
                    throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                } finally {
                    if (((String) _requestMap.get("LMB_TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.FAIL))
                        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    if (_log.isDebugEnabled())
                        _log.debug("creditAdjust", "Exiting with  _requestMap: " + _requestMap);
                }
            }

            String lmbTransactionStatus = (String) _requestMap.get("LMB_TRANSACTION_STATUS");
            System.out.println("LMB transaction status:" + lmbTransactionStatus + ",service type =" + serviceType);
            if (!(serviceType.equals(PretupsI.SERVICE_TYPE_LEND_ME_BALANCE) && BTSLUtil.isNullString(lmbTransactionStatus) && (!lmbTransactionStatus.equals(InterfaceErrorCodesI.SUCCESS)))) {
                try {
                    huaweiMultFactorDouble = Double.parseDouble(multFactor);
                    double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                    systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, huaweiMultFactorDouble);
                    amountStr = String.valueOf(systemAmtDouble);
                    // Based on the INFiles ROUND_FLAG flag, we have to decide
                    // to round the transfer amount or not.
                    String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                    if (_log.isDebugEnabled())
                        _log.debug("creditAdjust", "From file cache roundFlag = " + roundFlag);
                    // If the ROUND_FLAG is not defined in the INFile
                    if (InterfaceUtil.isNullString(roundFlag)) {
                        roundFlag = "Y";
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTRINHandler[creditAdjust]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                    }
                    // If rounding of amount is allowed, round the amount value
                    // and put this value in request map.
                    if ("Y".equals(roundFlag.trim())) {
                        amountStr = String.valueOf(Math.round(systemAmtDouble));
                        _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("creditAdjust", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                    throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                if (_log.isDebugEnabled())
                    _log.debug("creditAdjust", "transfer_amount:" + amountStr);
                // set transfer_amount in request map as amountStr (which is
                // round value of INTERFACE_AMOUNT)
                _requestMap.put("transfer_amount", amountStr);
                try {
                    sendRequestToIN(ComverseTRI.ACTION_CREDIT_ADJUST);
                    // set TRANSACTION_STATUS as Success in request map
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
                // sending the Re-charge request to IN along with re-charge
                // action defined in ComverseTRI interface
                catch (BTSLBaseException be) {
                    p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
                    _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
                    if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                        try {
                            String coreBalanceName = (String) _requestMap.get("CORE_BAL_NAME");
                            String previousCoreAmt = (String) _requestMap.get(coreBalanceName + "_RESP_BALANCE");
                            new ComverseTRINHandler().validate(_requestMap);
                            String newLMBAmt = (String) _requestMap.get(coreBalanceName + "_RESP_BALANCE");
                            if (Double.parseDouble(newLMBAmt) > Double.parseDouble(previousCoreAmt)) {
                                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                            } else {
                                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                            }
                        } catch (BTSLBaseException e1) {
                            throw e1;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                    } else
                        throw be;
                }
            }
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While creditAdjust get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
        }
        // post credit enquiry after promotion credit and cos update
        try {
            _requestMap.put("IN_START_TIME", "0");
            _requestMap.put("IN_END_TIME", "0");
            if (((PretupsI.YES).equals((String) _requestMap.get("ENQ_POSTBAL_ALLOW"))) && ((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS))
                postCreditEnquiry(_requestMap);
            // lohit
            _requestMap.put("IN_POSTCREDIT_VAL_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
            throw e;
        }
    }

    /**
     * Method : debitAdjust
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double huaweiMultFactorDouble = 0;
        String amountStr = null;
        String serviceType;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _referenceID);
            _msisdn = (String) _requestMap.get("MSISDN");
            _serviceType = (String) _requestMap.get("REQ_SERVICE");
            String _tempBal = (String) _requestMap.get("INTERFACE_PREV_BALANCE");
            _requestMap.put("RETRY_BAL_QUERY", _tempBal);
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("debitAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);
            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTRI.ACTION_DEBIT_ADJUST);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            serviceType = (String) _requestMap.get("REQ_SERVICE");
            try {
                huaweiMultFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, huaweiMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTRINHandler[debitAdjust]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            try {
                sendRequestToIN(ComverseTRI.ACTION_DEBIT_ADJUST);
                // set TRANSACTION_STATUS as Success in request map
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            catch (BTSLBaseException be) {
                p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
                _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
                if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    try {
                        String coreBalanceName = (String) _requestMap.get("CORE_BAL_NAME");
                        String previousCoreAmt = (String) _requestMap.get(coreBalanceName + "_RESP_BALANCE");
                        new ComverseTRINHandler().validate(_requestMap);
                        String newLMBAmt = (String) _requestMap.get(coreBalanceName + "_RESP_BALANCE");
                        if (Double.parseDouble(newLMBAmt) > Double.parseDouble(previousCoreAmt)) {
                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                        } else {
                            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                            throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                        }
                    } catch (BTSLBaseException e1) {
                        throw e1;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                } else
                    throw be;
            } finally {
                if (((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.FAIL))
                    _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "Exiting with  _requestMap: " + _requestMap);
            }

            String transactionStatus = (String) _requestMap.get("TRANSACTION_STATUS");
            // YES IN CASE YOU NEED TO ADJUST DEBIT ADJUST FOR LMB
            if ((!BTSLUtil.isNullString(transactionStatus)) && transactionStatus.equals(InterfaceErrorCodesI.SUCCESS) && _requestMap.get("LMB_DEBIT") != null && _requestMap.get("LMB_DEBIT").equals(PretupsI.YES)) {
                try {
                    _requestMap.put("LMB_ACTION", ComverseTRI.ACTION_LMB_DEBIT_ADJUST);
                    LMBUpdate(_requestMap);
                    _requestMap.put("IN_LMB_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                    _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                } catch (BTSLBaseException be) {
                    _log.error("debitAdjust", "BTSLBaseException be=" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("debitAdjust", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While LMB update in debit adjust, get the Exception e:" + e.getMessage());
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                } finally {
                    if (_log.isDebugEnabled())
                        _log.debug("debitAdjust", "Exiting with  _requestMap: " + _requestMap);
                }
            }
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }
        // post credit enquiry after promotion credit and cos update
        try {
            _requestMap.put("IN_START_TIME", "0");
            _requestMap.put("IN_END_TIME", "0");
            if (((PretupsI.YES).equals((String) _requestMap.get("ENQ_POSTBAL_ALLOW"))) && ((PretupsI.YES).equals((String) _requestMap.get("ENQ_POSTBAL_ALLOW"))) && ((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS))
                postCreditEnquiry(_requestMap);
            // lohit
            _requestMap.put("IN_POSTCREDIT_VAL_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
            throw e;
        }
    }

    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }

    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " p_action=" + p_action + " __msisdn=" + _msisdn);
        if (!BTSLUtil.isNullString(_msisdn)) {
            _msisdn = InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
        }
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " p_action=" + p_action + " __msisdn=" + _msisdn);
        // Put the request string, action, interface id, network code in the
        // Transaction log.
        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request Parameters : ", "transfer_amount=" + (String) _requestMap.get("transfer_amount"));
        long startTime = 0, endTime = 0, sleepTime, warnTime = 0;
        ServiceSoap clientStub = null;

        try {
            /*
             * if("Y".equalsIgnoreCase(_interfaceClosureSupport) &&
             * !("Y".equals(_requestMap.get("ADJUST")) &&
             * "C".equals(_requestMap.get("INTERFACE_ACTION"))&&
             * "S".equals(_userType)))
             * {
             * _isSameRequest=true;
             * checkInterfaceB4SendingRequest();
             * }
             */
            // Get the start time when the request is send to IN.
            startTime = System.currentTimeMillis();
            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
            ComverseTRConnector serviceConnection = new ComverseTRConnector(_requestMap);
            clientStub = serviceConnection.getService();
            if (clientStub == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Unable to get Client Object");
                _log.error("sendRequestToIN", "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            try {
                try {
                    switch (p_action) {
                    case ComverseTRI.ACTION_ACCOUNT_DETAILS: {
                        SubscriberRetrieve subscriberRetrieve = clientStub.retrieveSubscriberWithIdentityNoHistory(_msisdn, "", 1);
                        _requestMap.put("ACCINFO_RESP_OBJ", subscriberRetrieve);
                        break;
                    }
                    case ComverseTRI.ACTION_RECHARGE_CREDIT: {
                        boolean rechargeStatus = clientStub.nonVoucherRecharge(_msisdn, "", Double.parseDouble((String) _requestMap.get("transfer_amount")), Integer.parseInt((String) _requestMap.get("VALIDITY_DAYS")), _inReconID + (String) _requestMap.get("RECH_COMMENT"));
                        if (!rechargeStatus) {
                            _requestMap.put("INTERFACE_STATUS", "ERR_RESP");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                        break;
                    }
                    case ComverseTRI.ACTION_CREDIT_ADJUST: {
                        _formatter.setRequestObjectInMap(ComverseTRI.ACTION_CREDIT_ADJUST, _requestMap);
                        boolean creditAdjustStatus = clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("CR_ADJ_REQ_OBJ"), "", _inReconID + (String) _requestMap.get("XFER_COMMENT"));
                        if (!creditAdjustStatus) {
                            _requestMap.put("INTERFACE_STATUS", "ERR_RESP");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                        break;
                    }
                    case ComverseTRI.ACTION_DEBIT_ADJUST: {
                        _formatter.setRequestObjectInMap(ComverseTRI.ACTION_DEBIT_ADJUST, _requestMap);
                        boolean debitAdjustStatus = clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("DR_ADJ_REQ_OBJ"), "", _inReconID + (String) _requestMap.get("XFER_COMMENT"));
                        if (!debitAdjustStatus) {
                            _requestMap.put("INTERFACE_STATUS", "ERR_RESP");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                        break;
                    }
                    case ComverseTRI.ACTION_LMB_DEBIT_ADJUST: {
                        _formatter.setRequestObjectInMap(ComverseTRI.ACTION_LMB_DEBIT_ADJUST, _requestMap);
                        boolean lmbCreditStatus = clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("DR_LMB_ADJ_REQ_OBJ"), "", _inReconID + (String) _requestMap.get("XFER_COMMENT"));
                        if (!lmbCreditStatus) {
                            _requestMap.put("LMB_INTERFACE_STATUS", "ERR_RESP");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                        break;
                    }
                    // for lmb credit adjust from 0 to 1 in case of p2p
                    case ComverseTRI.ACTION_LMB_CREDIT_ADJUST: {
                        _formatter.setRequestObjectInMap(ComverseTRI.ACTION_LMB_CREDIT_ADJUST, _requestMap);
                        boolean lmbDebitAdjustStatus = clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("CR_LMB_ADJ_REQ_OBJ"), "", _inReconID + (String) _requestMap.get("XFER_COMMENT"));
                        if (!lmbDebitAdjustStatus) {
                            _requestMap.put("LMB_INTERFACE_STATUS", "ERR_RESP");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                        break;
                    }
                    }
                } catch (java.rmi.RemoteException re) {
                    re.printStackTrace();
                    // /EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ComverseTRINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String)
                    // _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+re.getMessage());
                    String respCode = null;
                    // parse error code
                    String requestStr = re.getMessage();
                    int index = requestStr.indexOf("<ErrorCode>");
                    if (index == -1) {
                        if (re.getMessage().contains("java.net.ConnectException")) {
                            _log.error("sendRequestToIN", "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseTRINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for Comverse Soap Stub with INTERFACE_ID=[" + _interfaceID + "]");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.FAIL);
                            throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                        } else if (re.getMessage().contains("java.net.SocketTimeoutException")) {
                            re.printStackTrace();
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "RMI java.net.SocketTimeoutException Message:" + re.getMessage());
                            _log.error("sendRequestToIN", "RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        } else
                            throw new Exception(re);
                    } else {
                        respCode = requestStr.substring(index + "<ErrorCode>".length(), requestStr.indexOf("</ErrorCode>", index));
                        index = requestStr.indexOf("<ErrorDescription>");
                        String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(), requestStr.indexOf("</ErrorDescription>", index));
                        _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                        _requestMap.put("INTERFACE_STATUS", respCode);
                        if (respCode.equals(ComverseTRI.MSISDN_NOT_FOUND)) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTRINHandler[ sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error Message:" + re.getMessage());
                            _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc: " + respCodeDesc);
                            throw new BTSLBaseException(PretupsErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                        } else {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler [sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error Message:" + re.getMessage());
                            _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                    }
                } catch (SocketTimeoutException se) {
                    se.printStackTrace();
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error Message:" + se.getMessage());
                    _log.error("sendRequestToIN", "Error Message :" + se.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                } catch (Exception e) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error Message:" + e.getMessage());
                    _log.error("sendRequestToIN", "Error Message :" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                } finally {
                    _requestMap.remove("CR_ADJ_REQ_OBJ");
                    _requestMap.remove("DR_ADJ_REQ_OBJ");
                    _requestMap.remove("DR_LMB_ADJ_REQ_OBJ");
                    _requestMap.remove("CR_LMB_ADJ_REQ_OBJ");
                }
                endTime = System.currentTimeMillis();
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error Message:" + e.getMessage());
                _log.error("sendRequestToIN", "Error Message :" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } finally {
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
            }

            _responseMap = _formatter.parseResponseObject(p_action, _requestMap);
            // put value of response
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response Map: " + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
            // End time would be stored into request map with
            // key as IN_END_TIME as soon as the response of the request is
            // fetched from the IN.
            // Difference of start and end time would be compared against the
            // warn time,
            // if request and response takes more time than that of the warn
            // time,
            // an event with level INFO is handled

            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            warnTime = Long.parseLong((String) _requestMap.get("WARN_TIMEOUT"));
            if (endTime - startTime > warnTime) {
                _log.info("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ComverseTRINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Comverse IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
            }
            String status = (String) _responseMap.get("RESP_STATUS");
            _requestMap.put("INTERFACE_STATUS", status);

            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
            }

            // If the status is Not OK, exception with error code as
            // RESPONSE_ERROR is thrown.
            /*
             * if(!ComverseTRI.RESULT_OK.equals(status))
             * {
             * //Check the status whether the subscriber's msisdn defined in the
             * IN
             * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
             * EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,
             * "ComverseTRINHandler[sendRequestToIN]"
             * ,"REFERENCE ID = "+_referenceID
             * +"MSISDN = "+_msisdn,"INTERFACE ID = "
             * +_interfaceID,"Network code = "+(String)
             * _requestMap.get("NETWORK_CODE"
             * )+" Action = "+p_action,"Response Status fron IN is: "+status+
             * ". So marking response as FAIL");
             * _log.error("sendRequestToIN","Response Status fron IN is: "+status
             * + ". So marking response as FAIL");
             * throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
             * }
             */
            String inRespID = (String) _responseMap.get("RESP_TXN_ID");
            /*
             * if(!_inTXNID.equals(inRespID))
             * {
             * _log.error("sendRequestToIN","Transaction id set in the request ["
             * +_inTXNID+
             * "] does not match with the transaction id fetched from response["
             * +inRespID+"]");
             * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID
             * , EventComponentI.INTERFACES, EventStatusI.RAISED,
             * EventLevelI.FATAL
             * ,"ComverseTRINHandler[sendRequestToIN]",_referenceID, _msisdn,
             * (String)
             * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+
             * " p_action = "
             * +p_action,"Transaction id set in the request ["+_inTXNID
             * +"] does not match with the transaction id fetched from response["
             * +inRespID+"],Hence marking the transaction as AMBIGUOUS");
             * _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
             * throw new
             * BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI
             * .AMBIGOUS);
             * }
             */
            String lmbStatus = (String) _responseMap.get("LMB_RESP_STATUS");
            _requestMap.put("LMB_INTERFACE_STATUS", lmbStatus);
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            clientStub = null;
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action=" + p_action);
        }// end of finally
    }

    public void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

            String userId = FileCache.getValue(_interfaceID, "USER_NAME");
            if (InterfaceUtil.isNullString(userId)) {
                _log.error("setInterfaceParameters", "Value of USER_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SAFCOM_USER_ID is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("USER_NAME", userId.trim());

            String passwd = FileCache.getValue(_interfaceID, "PASSWORD");
            if (InterfaceUtil.isNullString(passwd)) {
                _log.error("setInterfaceParameters", "Value of PASSWORD is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SAFCOM_PASSWD is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("PASSWORD", passwd.trim());

            String endUrl = FileCache.getValue(_interfaceID, "END_URL");
            if (InterfaceUtil.isNullString(endUrl)) {
                _log.error("setInterfaceParameters", "Value of END_URL is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "END_URL is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("END_URL", endUrl.trim());

            String actionUri = FileCache.getValue(_interfaceID, "SOAP_ACTION_URI");
            if (InterfaceUtil.isNullString(actionUri)) {
                _log.error("setInterfaceParameters", "Value of SOAP_ACTION_URI is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SOAP_ACTION_URI is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SOAP_ACTION_URI", actionUri.trim());

            String wssddLoc = FileCache.getValue(_interfaceID, "WSDD_LOCATION");
            if (InterfaceUtil.isNullString(wssddLoc)) {
                _log.error("setInterfaceParameters", "Value of WSDD_LOCATION is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "WSDD_LOCATION is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WSDD_LOCATION", wssddLoc.trim());
            if (!BTSLUtil.isNullString(_serviceType) && _serviceType.equalsIgnoreCase("LMB")) {
                String xferCommentLMB = FileCache.getValue(_interfaceID, "XFER_COMMENT_LMB");
                if (InterfaceUtil.isNullString(xferCommentLMB)) {
                    _log.error("setInterfaceParameters", "Value of XFER_COMMENT_LMB is not defined in the INFile");
                    _requestMap.put("RECH_COMMENT", "comvivaLMB");
                }
                _requestMap.put("RECH_COMMENT", xferCommentLMB.trim());
            } else {
                String rechComment = FileCache.getValue(_interfaceID, "RECH_COMMENT");
                if (InterfaceUtil.isNullString(rechComment)) {
                    _log.error("setInterfaceParameters", "Value of RECH_COMMENT is not defined in the INFile");
                    _requestMap.put("RECH_COMMENT", "comvivaC2S");
                }
                _requestMap.put("RECH_COMMENT", rechComment.trim());
            }
            if (!BTSLUtil.isNullString(_serviceType) && _serviceType.equalsIgnoreCase("LMB")) {
                String xferCommentLMB = FileCache.getValue(_interfaceID, "XFER_COMMENT_LMB");
                if (InterfaceUtil.isNullString(xferCommentLMB)) {
                    _log.error("setInterfaceParameters", "Value of XFER_COMMENT_LMB is not defined in the INFile");
                    _requestMap.put("XFER_COMMENT", "comvivaLMB");
                } else
                    _requestMap.put("XFER_COMMENT", xferCommentLMB.trim());
            } else {
                String xferComment = FileCache.getValue(_interfaceID, "XFER_COMMENT");
                if (InterfaceUtil.isNullString(xferComment)) {
                    _log.error("setInterfaceParameters", "Value of XFER_COMMENT is not defined in the INFile");
                    _requestMap.put("XFER_COMMENT", "comvivaP2P");
                }
                _requestMap.put("XFER_COMMENT", xferComment.trim());
            }
            String coreBalName = FileCache.getValue(_interfaceID, "CORE_BAL_NAME");
            if (InterfaceUtil.isNullString(coreBalName)) {
                _log.error("setInterfaceParameters", "Value of CORE_BAL_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CORE_BAL_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }

            _requestMap.put("CORE_BAL_NAME", coreBalName.trim());

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String retryCountCreditStr = (String) FileCache.getValue(_interfaceID, "RETRY_COUNT_CREDIT");
            if (InterfaceUtil.isNullString(retryCountCreditStr) || !InterfaceUtil.isNumeric(retryCountCreditStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "RETRY_COUNT_CREDIT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RETRY_COUNT_CREDIT", retryCountCreditStr.trim());

            String retryCountDebitStr = (String) FileCache.getValue(_interfaceID, "RETRY_COUNT_DEBIT");
            if (InterfaceUtil.isNullString(retryCountDebitStr) || !InterfaceUtil.isNumeric(retryCountDebitStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "RETRY_COUNT_DEBIT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RETRY_COUNT_DEBIT", retryCountDebitStr.trim());

            String readTimeOut = (String) FileCache.getValue(_interfaceID, "READ_TIME_OUT");
            if (InterfaceUtil.isNullString(readTimeOut) || !InterfaceUtil.isNumeric(readTimeOut)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "READ_TIME_OUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("READ_TIME_OUT", readTimeOut.trim());

            String LMBBalName = FileCache.getValue(_interfaceID, "LMB_BAL_NAME");
            if (InterfaceUtil.isNullString(LMBBalName)) {
                _log.error("setInterfaceParameters", "Value of LMB_BAL_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "LMB_BAL_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("LMB_BAL_NAME", LMBBalName.trim());

        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
        }// end of finally
    }

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

    private String getINReconID() {
        String inReconId = null;
        String userType = (String) _requestMap.get("USER_TYPE");
        inReconId = _referenceID + userType;
        _requestMap.put("IN_RECON_ID", inReconId);
        _requestMap.put("IN_TXN_ID", inReconId);
        return inReconId;
    }

    /**
     * This method used to get the system language mapped in FileCache based on
     * the INLanguge.Includes following
     * If the Mapping key not defined in IN file handle the event as System
     * Error with level FATAL.
     * If the Mapping is not defined handle the event as SYSTEM INFO with level
     * MAJOR and set empty string.
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
        try {
            // Get the mapping string from the FileCache and storing all the
            // mappings into array which are separated by ','.
            String mappingString = (String) FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
            // langFromIN = (String)_responseMap.get("LanguageName");
            langFromIN = (String) _responseMap.get("IN_LANG");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString::" + mappingString + " langFromIN::" + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0; in < mappingArr.length; in++) {
                tempArr = mappingArr[in].split(":");
                if (langFromIN.equalsIgnoreCase(tempArr[0].trim())) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ComverseINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e::" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang::" + mappedLang);
        }
    }// end of setLanguageFromMapping

    /**
     * Implements the logic that update the LMB allowed flag of the subscriber
     * from the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    private void LMBUpdate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("LMBUpdate", "Entered p_requestMap: " + p_requestMap);
        _requestMap = p_requestMap;
        double lmbMultFactorDouble;
        double systemAmtDouble = 0;
        String amountStr;
        int action = 0;
        String multFactor = (String) _requestMap.get("MULT_FACTOR");
        _serviceType = (String) _requestMap.get("REQ_SERVICE");
        _inReconID = getINReconID();
        if (_log.isDebugEnabled())
            _log.debug("LMBUpdate", "multFactor:" + multFactor);
        multFactor = multFactor.trim();
        _requestMap.put("IN_START_TIME", "0");
        _requestMap.put("IN_END_TIME", "0");
        lmbMultFactorDouble = Double.parseDouble(multFactor);
        double lmbAmtDouble = Double.parseDouble((String) _requestMap.get("LMB_CREDIT_AMT"));
        systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(lmbAmtDouble, lmbMultFactorDouble);
        amountStr = String.valueOf(systemAmtDouble);
        // Based on the INFiles ROUND_FLAG flag, we have to decide to round the
        // transfer amount or not.
        String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
        if (_log.isDebugEnabled())
            _log.debug("LMBUpdate", "From file cache roundFlag = " + roundFlag);
        // If the ROUND_FLAG is not defined in the INFile
        if (InterfaceUtil.isNullString(roundFlag)) {
            roundFlag = "Y";
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTRINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
        }
        // If rounding of amount is allowed, round the amount value and put this
        // value in request map.
        if ("Y".equals(roundFlag.trim())) {
            amountStr = String.valueOf(Math.round(systemAmtDouble));
            _requestMap.put("LMB_INTERFACE_ROUND_AMOUNT", amountStr);
        }
        if (_log.isDebugEnabled())
            _log.debug("LMBUpdate", "LMB Transfer_amount:" + amountStr);
        // set transfer_amount in request map as amountStr (which is round value
        // of INTERFACE_AMOUNT)
        _requestMap.put("lmb_transfer_amount", amountStr);

        try {
            if (_requestMap.get("LMB_ACTION") != null)
                action = (Integer) _requestMap.get("LMB_ACTION");
            if (_log.isDebugEnabled())
                _log.debug("LMBUpdate", "action decided: " + action);
            if (action == ComverseTRI.ACTION_LMB_CREDIT_ADJUST)
                sendRequestToIN(ComverseTRI.ACTION_LMB_CREDIT_ADJUST);
            else if (action == ComverseTRI.ACTION_LMB_DEBIT_ADJUST)
                sendRequestToIN(ComverseTRI.ACTION_LMB_DEBIT_ADJUST);
        } catch (BTSLBaseException be) {
            _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("LMBUpdate", "BTSLBaseException be:" + be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                try {
                    String lmbBalanceName = (String) _requestMap.get("LMB_BAL_NAME");
                    String previousLMBAmt = (String) _requestMap.get(lmbBalanceName + "_RESP_BALANCE");
                    new ComverseTRINHandler().validate(_requestMap);
                    String newLMBAmt = (String) _requestMap.get(lmbBalanceName + "_RESP_BALANCE");
                    if (action == ComverseTRI.ACTION_LMB_CREDIT_ADJUST && Double.parseDouble(newLMBAmt) > Double.parseDouble(previousLMBAmt)) {
                        _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    } else if (action == ComverseTRI.ACTION_LMB_DEBIT_ADJUST && Double.parseDouble(newLMBAmt) < Double.parseDouble(previousLMBAmt)) {
                        _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    } else {
                        _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                        throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                    }
                } catch (BTSLBaseException e1) {
                    throw e1;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("LMBUpdate", "Exception e:" + e.getMessage());
            _requestMap.put("LMB_TRANSACTION_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[LMBUpdate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While LMBUpdate get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "LMBUpdate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("LMBUpdate", "Exited _requestMap=" + _requestMap);
            _requestMap.remove("LMB_ACTION");
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTRINHandler[COSUpdate]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
    }

    /**
     * Method : postCreditEnquiry
     * Function : to check the user balance after query
     * 
     * @param p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void postCreditEnquiry(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("postCreditEnquiry", "Entered p_requestMap:" + p_requestMap);

        _requestMap = p_requestMap;
        String multFactor = "";
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _userType = (String) _requestMap.get("USER_TYPE");
            _msisdn = (String) _requestMap.get("MSISDN");
            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);

            multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("postCreditEnquiry", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("postCreditEnquiry", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            if (PretupsI.YES.equals((String) _requestMap.get("ENQ_POSTBAL_IN"))) {
                // Set the interface parameters into requestMap
                setInterfaceParameters(ComverseTRI.ACTION_ACCOUNT_DETAILS);
                // sending the AccountInfo request to IN along with validate
                // action defined in interface
                sendRequestToIN(ComverseTRI.ACTION_ACCOUNT_DETAILS);
                // set TRANSACTION_STATUS as Success in request map
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", InterfaceErrorCodesI.SUCCESS);
                // get value of BALANCE from response map (BALANCE was set in
                // response map in sendRequestToIN method.)
                try {
                    String amountStr = (Double) _responseMap.get("RESP_BALANCE") + "";
                    amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                    _requestMap.put("INTERFACE_POST_BALANCE", amountStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[postCreditEnquiry]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                    throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                String expdate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
                _requestMap.put("NEW_EXPIRY_DATE", expdate);
            } else {
                long postBalLong = 0, interfaceAmtLong = 0;
                _requestMap.put("IN_START_TIME", String.valueOf(System.currentTimeMillis()));
                try {
                    postBalLong = Long.parseLong((String) _requestMap.get("INTERFACE_PREV_BALANCE"));
                    interfaceAmtLong = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
                    String postBalStr = "";
                    if (!((String) _requestMap.get("INTERFACE_ACTION")).equals(PretupsI.INTERFACE_DEBIT_ACTION))
                        postBalStr = String.valueOf(postBalLong + interfaceAmtLong);
                    else
                        postBalStr = String.valueOf(postBalLong - interfaceAmtLong);
                    _requestMap.put("INTERFACE_POST_BALANCE", postBalStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTRINHandler[postCreditEnquiry]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "POSTCRE_INTERFACE_POST_BALANCE  is not Numeric");
                    throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                // set TRANSACTION_STATUS as Success in request map
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", InterfaceErrorCodesI.SUCCESS);
                _requestMap.put("IN_END_TIME", String.valueOf(System.currentTimeMillis()));
            }
        } catch (BTSLBaseException be) {
            _log.error("postCreditEnquiry", "BTSLBaseException be=" + be.getMessage());
            _requestMap.put("POSTCRE_TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[postCreditEnquiry]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("postCreditEnquiry", "Exiting with  _requestMap: " + _requestMap);
        }
    }
}