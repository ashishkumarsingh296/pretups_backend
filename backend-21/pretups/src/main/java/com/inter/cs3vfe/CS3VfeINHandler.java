/*
 * Created on May 1, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cs3vfe;

import java.io.BufferedReader;
import java.io.PrintWriter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
// import com.btsl.pretups.inter.cs3vfe.sdvfe.SDVfeProcessor;
// import com.btsl.pretups.logging.SDTransactionLog;
import com.btsl.pretups.logging.INNodeLog;
import com.btsl.pretups.logging.TransactionLog;
import com.inter.cs3mobinil.CS3MobinilI;
import com.inter.scheduler.cs3vfe.NodeCloser;
import com.inter.scheduler.cs3vfe.NodeManager;
import com.inter.scheduler.cs3vfe.NodeScheduler;
import com.inter.scheduler.cs3vfe.NodeVO;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CS3VfeINHandler implements InterfaceHandler {

    private static Log _log = LogFactory.getLog(CS3VfeINHandler.class.getName());

    // added by harsh

    private HashMap<String, String> _requestMap = null;// Contains the request
                                                       // parameter as key and
                                                       // value pair.
    private HashMap<String, String> _responseMap = null;// Contains the response
                                                        // of the request as key
                                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// UNodesed to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;
    private String _userType = null;
    private static SimpleDateFormat _sdf = new SimpleDateFormat("yyMMddHHmm");
    private static CS3VfeRequestFormatter _cs3VfeRequestFormatter = null;
    private int _ambgMaxRetryCount = 0;
    private int _ambgCurrentRetryCount = 0;
    private NodeCloser _nodeCloser = null;
    static {
        if (_log.isDebugEnabled())
            _log.debug("CS3VfeINHandler[static]", "Entered");
        try {
            _cs3VfeRequestFormatter = new CS3VfeRequestFormatter();
        } catch (Exception e) {
            _log.error("CS3VfeINHandler[static]", "While instantiation of CS3VfeRequestFormatter get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[static]", "", "", "", "While instantiation of CS3VfeRequestFormatter get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3VfeINHandler[static]", "Exited");
        }
    }

    /**
     * Implements the logic that validate the subscriber and get the subscriber
     * information
     * from the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String serviceClass = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _userType = (String) _requestMap.get("USER_TYPE");
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
            _inTXNID = getINTransactionID(_requestMap, CS3VfeI.ACTION_GET_BAL_DATE);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(CS3VfeI.ACTION_GET_BAL_DATE);

            // key value of requestMap is formatted into XML string for the
            // validate request.
            String inStr = _cs3VfeRequestFormatter.generateRequest(CS3VfeI.ACTION_GET_BAL_DATE, _requestMap);
            try {
                _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "GET_BAL_DATE_RETRY_CNT"));
            } catch (Exception e) {
                _ambgMaxRetryCount = 1;
            }
            // sending the AccountInfo request to IN along with validate action
            // defined in CS3VfeI interface
            sendRequestToIN(inStr, CS3VfeI.ACTION_GET_BAL_DATE);

            if ("1".equals(_responseMap.get("temporaryBlockedFlag"))) {
                _requestMap.put("ACCOUNT_STATUS", "BARRED");
                _log.error("validate", _msisdn + " is barred at IN");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _msisdn + " is barred at IN");
                // throw new
                // BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);
            } else if (InterfaceUtil.isNullString((String) _requestMap.get("ACCOUNT_STATUS"))) {
                // set TRANSACTION_STATUS as Success in request map
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                serviceClass = (String) _responseMap.get("serviceClassCurrent");
                _requestMap.put("SERVICE_CLASS", serviceClass);

                // get value of accountValue1 from response map (accountValue1
                // was set in response map in sendRequestToIN method.)
                String amountStr = (String) _responseMap.get("accountValue1");
                try {
                    amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                    _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("validate", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:" + e.getMessage());
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
                }

                String dateFormat = FileCache.getValue(_interfaceID, "TRANS_DATE_TIME");

                Date supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("supervisionExpiryDate"), dateFormat);
                Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("serviceFeeExpiryDate"), dateFormat);
                Date creditClearanceDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("creditClearanceDate"), dateFormat);

                // NEW ACTIVE GRACE AFTERGRACE DISCONNECTED
                // ---------|-----------|-------------------------|------------------|---------------------
                // supervisionExpiry ServiceFeeExpiry CreditClearanceDate
                // In case of NEW and DISCONNECTED account status, IN will
                // respond with error code.
                // PreTUPS will allow recharge in case of ACTIVE, GRACE and
                // AFTERGARCE account status

                Date currentDate = new Date();
                if (supervisionExpiryDate.after(currentDate))
                    _requestMap.put("ACCOUNT_STATUS", "ACTIVE");
                else if ((currentDate.after(supervisionExpiryDate)) && (serviceFeeExpiryDate.after(currentDate)))
                    _requestMap.put("ACCOUNT_STATUS", "GRACE");
                else if (currentDate.after(serviceFeeExpiryDate) && creditClearanceDate.after(currentDate))
                    _requestMap.put("ACCOUNT_STATUS", "AFTERGRACE");

                _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent")); // getDateStringFromDate
                // set OLD_EXPIRY_DATE in request map as returned from
                // _responseMap.
                _requestMap.put("OLD_EXPIRY_DATE", getDateStringFromDate(supervisionExpiryDate, "yyyyMMdd"));
                _requestMap.put("OLD_GRACE_DATE", getDateStringFromDate(serviceFeeExpiryDate, "yyyyMMdd"));
                // bundles received b4 credit
                _requestMap.put("IN_RESP_BUNDLE_CODES", (String) _responseMap.get("DDCATED_ACNT_CODES_V"));
                _requestMap.put("IN_RESP_BUNDLE_PREV_BALS", (String) _responseMap.get("DDCATED_ACNT_VALUES_V"));
                _requestMap.put("IN_RESP_BUNDLE_PREV_VALIDITY", (String) _responseMap.get("DDCATED_ACNT_EXPIRIES_V"));
                // set the mapping language of our system from FileCache mapping
                // based on the responsed language.
                setLanguageFromMapping();
            }
            if (InterfaceUtil.isNullString((String) _requestMap.get("ACCOUNT_STATUS")))
                _requestMap.put("ACCOUNT_STATUS", "");
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
        }
    }// end of validate

    /**
     * Implements the logic that validate the subscriber and get the subscriber
     * information
     * from the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validateAfterRefill(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validateAfterRefill", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String serviceClass = null;
        try {
            try {
                // key value of requestMap is formatted into XML string for the
                // validate request.
                String inStr = _cs3VfeRequestFormatter.generateRequest(CS3VfeI.ACTION_GET_BAL_DATE, _requestMap);
                try {
                    _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "CREDIT_RETRY_CNT"));
                } catch (Exception e) {
                    _ambgMaxRetryCount = 1;
                }
                // sending the AccountInfo request to IN along with validate
                // action defined in CS3VfeI interface
                sendRequestToIN(inStr, CS3VfeI.ACTION_GET_BAL_DATE);
            } catch (Exception e) {
                _log.error("validateAfterRefill", e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            serviceClass = (String) _responseMap.get("serviceClassCurrent");
            _requestMap.put("SERVICE_CLASS", serviceClass);

            // if post balance in this request is greater than the received in
            // account info mark SUCCESS.
            // if expiry or grace in this request is greater than the received
            // in account info mark SUCCESS.
            // if dedicated balance or expiry of these balances in this request
            // is greater than the received in account info mark SUCCESS.
            // If any one is not true mark transaction as AMBIGUOUS
            // boolean reqStatusSuccess=true;
            boolean reqStatusSuccess = false;
            boolean reqStatusAmb = true;
            boolean ambDdcatedAcntChanged = false;
            // boolean exceptionOccured=false;
            String amountStr = (String) _responseMap.get("accountValue1");

            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("Y".equals(validateRequired)) {
                reqStatusAmb = false;
                try {
                    amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble((String) p_requestMap.get("MULT_FACTOR")));
                    _requestMap.put("INTERFACE_POST_BALANCE", amountStr);
                    String prevBalMain = (String) p_requestMap.get("INTERFACE_PREV_BALANCE");
                    System.out.println("1111111111111111111111111111111111111111");
                    Date prevExpiryDate = InterfaceUtil.getDateFromDateString((String) p_requestMap.get("OLD_EXPIRY_DATE"), "yyyyMMdd");
                    Date prevGraceDate = InterfaceUtil.getDateFromDateString((String) p_requestMap.get("OLD_GRACE_DATE"), "yyyyMMdd");

                    String dateFormat = FileCache.getValue(_interfaceID, "TRANS_DATE_TIME");

                    Date supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("supervisionExpiryDate"), "yyyyMMdd");
                    Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");

                    _requestMap.put("NEW_EXPIRY_DATE", getDateStringFromDate(supervisionExpiryDate, "yyyyMMdd"));
                    _requestMap.put("NEW_GRACE_DATE", getDateStringFromDate(serviceFeeExpiryDate, "yyyyMMdd"));
                    System.out.println("Supervision Date :: prevExpiryDate" + prevExpiryDate + "supervisionExpiryDate" + supervisionExpiryDate);
                    System.out.println("Grace Date :: prevGraceDate" + prevGraceDate + "serviceFeeExpiryDate" + serviceFeeExpiryDate);
                    System.out.println("Amount :: amountStr" + amountStr + "prevBalMain" + prevBalMain);
                    if ((Long.parseLong(amountStr) > Long.parseLong(prevBalMain)) || supervisionExpiryDate.after(prevExpiryDate) || serviceFeeExpiryDate.after(prevGraceDate)) {
                        System.out.println("2222222222222222222222222222222222222");
                        reqStatusSuccess = true;
                    }

                    String prevBundleCodes = (String) _requestMap.get("IN_RESP_BUNDLE_CODES");
                    if (!InterfaceUtil.isNullString(prevBundleCodes))
                        if (!(prevBundleCodes.replace("%2C", ",").equals((String) _responseMap.get("DDCATED_ACNT_CODES_V")))) {
                            System.out.println("3333333333333333333333333333333333333333");
                            ambDdcatedAcntChanged = true;
                            reqStatusSuccess = true;
                        }
                    if (!reqStatusSuccess) {
                        String prevBundleBalances = (String) _requestMap.get("IN_RESP_BUNDLE_PREV_BALS");
                        if (!InterfaceUtil.isNullString(prevBundleBalances))
                            if (!(prevBundleBalances.replace("%2C", ",").equals((String) _responseMap.get("DDCATED_ACNT_VALUES_V")))) {
                                ambDdcatedAcntChanged = true;
                                reqStatusSuccess = true;
                            }
                    }
                    if (!reqStatusSuccess) {
                        String prevBundleExpiries = (String) _requestMap.get("IN_RESP_BUNDLE_PREV_VALIDITY");
                        if (InterfaceUtil.isNullString(prevBundleExpiries))
                            if (!(prevBundleExpiries.replace("%2C", ",").equals((String) _responseMap.get("DDCATED_ACNT_EXPIRIES_V")))) {
                                ambDdcatedAcntChanged = true;
                                reqStatusSuccess = true;
                            }
                    }

                    if (reqStatusSuccess) {
                        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                        try {
                            if (ambDdcatedAcntChanged) {
                                _requestMap.put("IN_RESP_BUNDLE_CODES_CR", (String) _responseMap.get("DDCATED_ACNT_CODES_V"));
                                _requestMap.put("IN_RESP_BUNDLE_POST_BALS", (String) _responseMap.get("DDCATED_ACNT_VALUES_V"));
                                _requestMap.put("IN_RESP_BUNDLE_POST_VALIDITY", (String) _responseMap.get("DDCATED_ACNT_EXPIRIES_V"));

                                String[] namesB4 = ((String) _requestMap.get("IN_RESP_BUNDLE_CODES")).split("%2C");
                                String[] valuesB4 = ((String) _requestMap.get("IN_RESP_BUNDLE_PREV_BALS")).split("%2C");
                                String[] ExpiriesB4 = ((String) _requestMap.get("IN_RESP_BUNDLE_PREV_VALIDITY")).split("%2C");

                                String[] namesAfter = ((String) _requestMap.get("IN_RESP_BUNDLE_CODES_CR")).split(",");
                                String[] valuesAfter = ((String) _requestMap.get("IN_RESP_BUNDLE_POST_BALS")).split(",");
                                String[] ExpiriesAfter = ((String) _requestMap.get("IN_RESP_BUNDLE_POST_VALIDITY")).split(",");

                                String changedAcnts = "";
                                for (int i = 0; i < namesAfter.length; i++) {
                                    boolean afterAcntMatched = false;
                                    for (int j = 0; j < namesB4.length; j++) {
                                        if (namesAfter[i].equals(namesB4[j])) {
                                            afterAcntMatched = true;
                                            if (!(valuesAfter[i].equals(valuesB4[j]))) {
                                                changedAcnts = changedAcnts + "," + namesAfter[i];
                                                break;
                                            } else if (!(ExpiriesAfter[i].equals(ExpiriesB4[j]))) {
                                                changedAcnts = changedAcnts + "," + namesAfter[i];
                                                break;
                                            }
                                        }
                                    }
                                    if (!afterAcntMatched)
                                        changedAcnts = changedAcnts + "," + namesAfter[i];
                                }
                                // GET THE AFTER VALUES OF THESE ACNTS WHILE
                                // SENDING MESSAGE
                                if (changedAcnts.length() > 0 && changedAcnts.charAt(0) == ',')
                                    changedAcnts = changedAcnts.substring(1);
                                _requestMap.put("CHANGED_BUNDLE_CODES", changedAcnts);
                                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                            }
                        } catch (Exception e) {
                            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                            e.printStackTrace();
                        }
                    } else
                        // _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.FAIL);
                        throw new BTSLBaseException(this, "validateAfterRefill", InterfaceErrorCodesI.FAIL);
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    reqStatusAmb = true;
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
            } else {
                reqStatusAmb = true;
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            if (reqStatusAmb) {
                // _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.FAIL);
                throw new BTSLBaseException(this, "validateAfterRefill", InterfaceErrorCodesI.FAIL);
                // balance_not_changed
            }
        } catch (BTSLBaseException be) {
            _log.error("validateAfterRefill", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateAfterRefill", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validateAfterRefill", InterfaceErrorCodesI.AMBIGOUS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateAfterRefill", "Exiting with  _requestMap: " + _requestMap);
        }
    }// end of validateAfterRefill

    /**
     * Implements the logic that credit the subscriber account on IN.
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
        double multFactorDouble = 0;
        String minTransferAmt = null;
        String amountStr = null;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _userType = (String) _requestMap.get("USER_TYPE");
            _inTXNID = getINTransactionID(_requestMap, CS3VfeI.ACTION_RECHARGE_CREDIT);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _msisdn = (String) _requestMap.get("MSISDN");

            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            try {
                multFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("credit", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3VfeINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            // PreTUPS will receive Service Offering Bits during Account
            // Information.
            // In case of some special service classes (configurable in
            // PreTUPS), if bits received
            // from IN, match with the configured bits in PreTUPS, PreTUPS will
            // first check
            // minimum recharge amount (configured) against the bits received.
            // If recharge amount is less than minimum configured amount,
            // PreTUPS will not send
            // Refill request to IN.

            String srvOfferClasses = FileCache.getValue(_interfaceID, "SRVC_OFFER_CLASSES");
            String servcOfferApplicable = FileCache.getValue(_interfaceID, "SRVC_OFFER_APPLICABLE");
            String servcOfferMinRchrgVal = FileCache.getValue(_interfaceID, "SRVC_OFFER_MIN_RCHRG_VALUE");
            //
            if (systemAmtDouble < Integer.parseInt(servcOfferMinRchrgVal))
                if ("Y".equals(servcOfferApplicable)) {
                    // Set the interface parameters into requestMap
                    setInterfaceParameters(CS3VfeI.ACTION_GET_ACCOUNT_DETAILS);
                    String serviceClassVal = (String) p_requestMap.get("SERVICE_CLASS");
                    String[] srvOfferClassesList = srvOfferClasses.split(",");
                    if (Arrays.asList(srvOfferClassesList).contains(serviceClassVal)) {
                        // key value of requestMap is formatted into XML string
                        // for the validate request.
                        String reqStr = _cs3VfeRequestFormatter.generateRequest(CS3VfeI.ACTION_GET_ACCOUNT_DETAILS, _requestMap);
                        try {
                            _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "ACNT_DETAILS_RETRY_CNT"));
                        } catch (Exception e) {
                            _ambgMaxRetryCount = 1;
                        }
                        // sending the AccountInfo request to IN along with
                        // validate action defined in CS3VfeI interface
                        sendRequestToIN(reqStr, CS3VfeI.ACTION_GET_ACCOUNT_DETAILS);
                        String serviceOfferingBits = (String) _responseMap.get("serviceOffering");

                        // if service offering bits match
                        String[] srvOfferBits = ((String) _requestMap.get("SRVC_OFFER_BITS")).split(",");
                        String[] srvOfferIds = ((String) _responseMap.get("SERVICE_OFFERINGS_IDS")).split(",");
                        String[] srvOfferFlags = ((String) _responseMap.get("SERVICE_OFFERINGS_FLAGS")).split(",");
                        String srvOfferBitVal = "";
                        boolean flag = true;

                        /*
                         * for(int i=0;i<srvOfferBits.length;i++)
                         * {
                         * int index=Integer.parseInt(srvOfferBits[i]);
                         * if(!srvOfferFlags[index-1].equals("1"))
                         * {
                         * flag=false;
                         * break;
                         * }
                         * }
                         */

                        for (int i = 0; i < srvOfferBits.length; i++) {
                            int index = Integer.parseInt(srvOfferBits[i]);
                            srvOfferBitVal = srvOfferBitVal + srvOfferFlags[index - 1];
                        }

                        // if(srvOfferBits.equals(serviceOfferingBits))
                        /*
                         * if(flag)
                         * {
                         * String minAmtSrvOfferClasses=(String)_requestMap.get(
                         * "MIN_AMT_SRVC_OFFER_CLASSES");
                         * String[]
                         * minAmtSrvOfferClassesList=minAmtSrvOfferClasses
                         * .split(",");
                         * for(int i=0;i<srvOfferClassesList.length;i++)
                         * {
                         * if(srvOfferClassesList[i].equals((String)p_requestMap.
                         * get("SERVICE_CLASS")))
                         * {
                         * //this will be in lowest demomination
                         * minTransferAmt=minAmtSrvOfferClassesList[i];
                         * break;
                         * }
                         * }
                         * //should equality be checked??
                         * if(Double.parseDouble(amountStr)<=Double.parseDouble(
                         * minTransferAmt))
                         * {
                         * //don't allow recharge if transfer amount is less
                         * than or equal to configured amount.
                         * _log.error("credit","Exception e:"+
                         * "Service offering Case : Transfer amount "+ amountStr
                         * +
                         * " should be greater than minimum transfer amount configured ("
                         * +minTransferAmt+") for service class "+(String)
                         * p_requestMap.get("SERVICE_CLASS"));
                         * EventHandler.handle(EventIDI.SYSTEM_INFO,
                         * EventComponentI.INTERFACES, EventStatusI.RAISED,
                         * EventLevelI.MAJOR,
                         * "CS3VfeINHandler[credit]",_referenceID,_msisdn
                         * +" INTERFACE ID = "+_interfaceID, (String)
                         * _requestMap.get("NETWORK_CODE"),
                         * "Service offering Case : Transfer amount "+ amountStr
                         * +
                         * " should be greater than minimum transfer amount configured ("
                         * +minTransferAmt+") for service class "+(String)
                         * p_requestMap.get("SERVICE_CLASS"));
                         * throw new BTSLBaseException(InterfaceErrorCodesI.
                         * ERROR_RECHARGE_AMOUNT_LESS);
                         * }
                         * }
                         */

                        minTransferAmt = FileCache.getValue(_interfaceID, srvOfferBitVal);
                        if (!InterfaceUtil.isNullString(minTransferAmt)) {
                            if (Double.parseDouble(amountStr) < Double.parseDouble(minTransferAmt)) {
                                // don't allow recharge if transfer amount is
                                // less than or equal to configured amount.
                                _log.error("credit", "Exception e:" + "Service offering Case : Transfer amount " + amountStr + " should be greater than minimum transfer amount configured (" + minTransferAmt + ") for service class " + (String) p_requestMap.get("SERVICE_CLASS"));
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3VfeINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Service offering Case : Transfer amount " + amountStr + " should be greater than minimum transfer amount configured (" + minTransferAmt + ") for service class " + (String) p_requestMap.get("SERVICE_CLASS"));
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RECHARGE_AMOUNT_LESS);
                            }
                        } else {
                            _log.error("credit", "Exception e:" + "Service offering Case : For srvOfferBits " + srvOfferBits + ", srvOfferBitVal " + srvOfferBitVal + " is not configured in conf file.");
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3VfeINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Service offering Case : For srvOfferBits " + srvOfferBits + ", srvOfferBitVal " + srvOfferBitVal + " is not configured in conf file.");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                    }
                }

            setInterfaceParameters(CS3VfeI.ACTION_RECHARGE_CREDIT);
            // key value of requestMap is formatted into XML string for the
            // validate request.
            String inStr = _cs3VfeRequestFormatter.generateRequest(CS3VfeI.ACTION_RECHARGE_CREDIT, _requestMap);
            // sending the Re-charge request to IN along with re-charge action
            // defined in CS3VfeI interface
            sendRequestToIN(inStr, CS3VfeI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set NEW_EXPIRY_DATE into request map
            // if after flag is true then only get these values
            if ("1".equals(_requestMap.get("REFILL_ACNT_AFTER_FLAG"))) {
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("supervisionExpiryDateAfterCr"), "yyyyMMdd"));
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("serviceFeeExpiryDateAfterCr"), "yyyyMMdd"));
                _requestMap.put("IN_RESP_BUNDLE_CODES_CR", (String) _responseMap.get("DDCATED_ACNT_CODES_AfterCr"));
                _requestMap.put("IN_RESP_BUNDLE_POST_BALS", (String) _responseMap.get("DDCATED_ACNT_VALUES_AfterCr"));
                _requestMap.put("IN_RESP_BUNDLE_POST_VALIDITY", (String) _responseMap.get("DDCATED_ACNT_EXPIRIES_AfterCr"));
                // _requestMap.put("IN_RESP_BUNDLE_POST_GRACE",
                _requestMap.put("CHANGED_BUNDLE_CODES", (String) _responseMap.get("CHANGED_DDCATED_ACNTS"));
                // set INTERFACE_POST_BALANCE into request map as obtained thru
                // response map.
                try {
                    String postBalanceStr = (String) _responseMap.get("accountValue1AfterCr");
                    postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                    _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);

                    // Added by Narendra 20 Nov 2013

                    System.out.println("ServiceClassCurrent :): " + _requestMap.get("SERVICE_CLASS"));
                    System.out.println("DDCATED_ACNT_CODES_AfterCr :): " + (String) _responseMap.get("DDCATED_ACNT_CODES_AfterCr"));
                    System.out.println("DDCATED_ACNT_VALUES_AfterCr :): " + (String) _responseMap.get("DDCATED_ACNT_VALUES_AfterCr"));

                    String allowedServiceClassIds = FileCache.getValue(_interfaceID, "EASY_RC_SVCLASS");
                    if (!InterfaceUtil.isNullString(allowedServiceClassIds)) {
                        String esyClass = (String) _responseMap.get("DDCATED_ACNT_CODES_AfterCr");
                        String[] sdSrvcClassList = allowedServiceClassIds.split(",");
                        String serviceClassVal = (String) p_requestMap.get("SERVICE_CLASS");
                        if (Arrays.asList(sdSrvcClassList).contains(serviceClassVal)) {
                            if (esyClass.equals("1")) {
                                postBalanceStr = (String) _responseMap.get("DDCATED_ACNT_VALUES_AfterCr");
                                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
                                System.out.println("Narendra yadav");
                            }

                        }
                    }

                    // Ended by narendra 20 Nov 2013
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("credit", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            } else {
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }

            /*
             * try
             * {
             * String
             * sdSrvcClasses=FileCache.getValue(_interfaceID,"SD_SRVC_CLASSES");
             * if(!InterfaceUtil.isNullString(sdSrvcClasses))
             * {
             * String[] sdSrvcClassList=sdSrvcClasses.split(",");
             * //parse year and compare
             * if(Arrays.asList(sdSrvcClassList).contains((String)_responseMap.get
             * ("serviceClassCurrent")) &&
             * "9999".equals((String)_responseMap.get("supervisionExpiryDate")))
             * {
             * notifySDSystem();
             * }
             * }
             * else
             * {
             * throw new
             * BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM
             * );//config error
             * }
             * }
             * catch(BTSLBaseException be1)
             * {
             * }
             */
        } catch (BTSLBaseException be) {
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            if ((CS3VfeI.ACTION_GET_ACCOUNT_DETAILS == (Integer.parseInt((String) _requestMap.get("ACTION")))) && InterfaceErrorCodesI.AMBIGOUS.equals(be.getMessage()))
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;

            try {
                validateAfterRefill(p_requestMap);
            } catch (BTSLBaseException bte) {
                System.out.println("mESSSGE:" + bte.getMessage());
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage() + ". For " + _referenceID + ", after all retries status of the recharge is not clear. so marking transaction as ambiguous");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), e.getMessage() + "For " + _referenceID + ", after all retries status of the recharge is not clear. so marking transaction as ambiguous");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.AMBIGOUS);
            }

            /*
             * int retryCount=1;
             * while(retryCount<=_ambgMaxRetryCount)
             * {
             * try
             * {
             * validateAfterRefill(p_requestMap);
             * break;
             * }
             * catch(BTSLBaseException bte)
             * {
             * System.out.println("mESSSGE:" +bte.getMessage());
             * if(InterfaceErrorCodesI.AMBIGOUS.equals(bte.getMessage()))
             * retryCount++;
             * if(retryCount>_ambgMaxRetryCount)
             * throw bte;
             * throw bte;
             * }
             * catch(Exception e)
             * {
             * e.printStackTrace();
             * _log.error("credit","Exception e:"+e.getMessage()+". For "+
             * _referenceID+
             * ", after all retries status of the recharge is not clear. so marking transaction as ambiguous"
             * );
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,
             * EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             * "CS3VfeINHandler[credit]", _referenceID,_msisdn, (String)
             * _requestMap.get("NETWORK_CODE"),
             * e.getMessage()+"For "+_referenceID+
             * ", after all retries status of the recharge is not clear. so marking transaction as ambiguous"
             * );
             * throw new
             * BTSLBaseException(this,"credit",InterfaceErrorCodesI.AMBIGOUS);
             * }
             * }
             */
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }
    }// end of credit

    /**
     * This method is used to adjust the following
     * 1.Amount
     * 2.ValidityDays
     * 3.GraceDays
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", " Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;// assign map passed from InterfaceModule to
                                   // _requestMap(instance var)
        double systemAmtDouble = 0;
        String amountStr = null;
        int validityDays = 0;// Defines the validity days by which adjustment to
                             // be made.
        int graceDays = 0;// Defines the grace period by which adjustment to be
                          // made.
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // interface
                                                                    // id form
                                                                    // request
                                                                    // map
            _inTXNID = getINTransactionID(_requestMap, CS3VfeI.ACTION_CREDIT_ADJUST);// Generate
                                                                                     // the
                                                                                     // IN
                                                                                     // transaction
                                                                                     // id
                                                                                     // and
                                                                                     // set
                                                                                     // in
                                                                                     // _requestMap
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            _msisdn = (String) _requestMap.get("MSISDN");// get MSISDN from
                                                         // request map
            // Get the Multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(CS3VfeI.ACTION_CREDIT_ADJUST);

            try {
                double huaweiMultFactorDouble = Double.parseDouble(multFactor);
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3VfeINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "transfer_amount:" + amountStr + " multFactor:" + multFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            // Check for the validity Adjustment-VALIDITY_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("VALIDITY_DAYS"))) {
                try {
                    // For the adjust ment make validity days as negative.
                    validityDays = Integer.parseInt(((String) _requestMap.get("VALIDITY_DAYS")).trim());
                    if (_log.isDebugEnabled())
                        _log.debug("creditAdjust", "validityDays::" + validityDays);
                    // Set the validity days into request map with key as
                    // 'relative_date_adjustment_service_fee'
                    _requestMap.put("supervisionExpiryDateRelative", String.valueOf(validityDays));
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("creditAdjust", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }// end of cheking validity days adjustment
             // Check for the grace Adjustment-GRACE_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("GRACE_DAYS"))) {
                try {
                    // For the adjust ment make grace days as negative.
                    graceDays = Integer.parseInt(((String) _requestMap.get("GRACE_DAYS")).trim());
                    if (_log.isDebugEnabled())
                        _log.debug("creditAdjust", "graceDays::" + graceDays);
                    // Set the grace days into request map with key as
                    // 'relative_date_adjustment_supervision'
                    _requestMap.put("serviceFeeExpiryDateRelative", String.valueOf(graceDays));
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("creditAdjust", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }// end of checking graceAdjustment

            String inStr = _cs3VfeRequestFormatter.generateRequest(CS3VfeI.ACTION_CREDIT_ADJUST, _requestMap);

            try {
                _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "ADJ_RETRY_CNT"));
            } catch (Exception e) {
                _ambgMaxRetryCount = 1;
            }
            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in CS3VfeI interface
            sendRequestToIN(inStr, CS3VfeI.ACTION_CREDIT_ADJUST);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
            throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
        }
    }// end of creditAdjust

    /**
     * Implements the logic that debit the subscriber account on IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", " Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;// assign map passed from InterfaceModule to
                                   // _requestMap(instance var)
        double systemAmtDouble = 0;
        String amountStr = null;
        int validityDays = 0;// Defines the validity days by which adjustment to
                             // be made.
        int graceDays = 0;// Defines the grace period by which adjustment to be
                          // made.
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // interface
                                                                    // id form
                                                                    // request
                                                                    // map
            _inTXNID = getINTransactionID(_requestMap, CS3VfeI.ACTION_DEBIT_ADJUST);// Generate
                                                                                    // the
                                                                                    // IN
                                                                                    // transaction
                                                                                    // id
                                                                                    // and
                                                                                    // set
                                                                                    // in
                                                                                    // _requestMap
            _requestMap.put("IN_TXN_ID", _inTXNID);// get TRANSACTION_ID from
                                                   // request map (which has
                                                   // been passed by controller)
            _requestMap.put("IN_RECON_ID", _inTXNID);

            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            _msisdn = (String) _requestMap.get("MSISDN");// get MSISDN from
                                                         // request map
            // Get the Multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(CS3VfeI.ACTION_DEBIT_ADJUST);
            try {
                double huaweiMultFactorDouble = Double.parseDouble(multFactor);
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3VfeINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "transfer_amount:" + amountStr + " multFactor:" + multFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", "-" + amountStr);

            // Check for the validity Adjustment-VALIDITY_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("VALIDITY_DAYS"))) {
                try {
                    // For the adjust ment make validity days as negative.
                    validityDays = Integer.parseInt(((String) _requestMap.get("VALIDITY_DAYS")).trim());
                    if (_log.isDebugEnabled())
                        _log.debug("debitAdjust", "validityDays::" + validityDays);
                    // Set the validity days into request map with key as
                    // 'relative_date_adjustment_service_fee'
                    _requestMap.put("supervisionExpiryDateRelative", "-" + String.valueOf(validityDays));
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("creditAdjust", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }// end of cheking validity days adjustment
             // Check for the grace Adjustment-GRACE_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("GRACE_DAYS"))) {
                try {
                    // For the adjust ment make grace days as negative.
                    graceDays = Integer.parseInt(((String) _requestMap.get("GRACE_DAYS")).trim());
                    if (_log.isDebugEnabled())
                        _log.debug("debitAdjust", "graceDays::" + graceDays);
                    // Set the grace days into request map with key as
                    // 'relative_date_adjustment_supervision'
                    _requestMap.put("serviceFeeExpiryDateRelative", "-" + String.valueOf(graceDays));
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("creditAdjust", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }// end of checking graceAdjustment

            String inStr = _cs3VfeRequestFormatter.generateRequest(CS3VfeI.ACTION_DEBIT_ADJUST, _requestMap);

            try {
                _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "ADJ_RETRY_CNT"));
            } catch (Exception e) {
                _ambgMaxRetryCount = 1;
            }
            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in CS3VfeI interface
            sendRequestToIN(inStr, CS3VfeI.ACTION_DEBIT_ADJUST);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
            throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }
    }// end of debitAdjust.

    /**
     * This method is used to send the request to IN and stored the response
     * after parsing.
     * This method also take care about to handle the errornious satuation to
     * send the alarm and set the error code.
     * 1.Invoke the getNodeVO method of NodeScheduler class and pass the
     * Transaction Id.
     * 2.If the VO is Null then mark the request as fail and throw exception(New
     * Error code that defines No connection for any Node is available).
     * 3.If the VO is not NULL then pass the Node detail to CS3UrlConnection
     * class and get connection.
     * 4.After the proccessing the request(may be successful or fail) decrement
     * the connection counter and pass the
     * transaction id that is removed from the transNodeList.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */

    // public static void log(String p_referenceID,String p_inTxnId,String
    // p_nodeUrl,String nodeStatus, String p_msisdn,String p_action,String
    // p_network,String p_blockedAt, String p_resumedAt, String p_otherInfo)
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr::" + p_inRequestStr + " p_action::" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
        String responseStr = "";
        NodeVO cs3NodeVO = null;
        NodeScheduler cs3NodeScheduler = null;
        CS3VfeUrlConnection cs3URLConnection = null;
        long startTime = 0;
        long endTime = 0;
        int conRetryNumber = 0;
        long warnTime = 0;
        int readTimeOut = 0;
        String inReconID = null;
        long retrySleepTime = 0;

        int loopV = 0; // Narendra VFE6 CR
        int loop = 1;// Narendra VFE6 CR

        try {
            _responseMap = new HashMap();
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            retrySleepTime = Long.parseLong(FileCache.getValue(_interfaceID, "RETRY_SLEEP_TIME"));
            // Thread.sleep(retrySleepTime);
            // Get the instance of NodeScheduler based on interfaceId.
            cs3NodeScheduler = NodeManager.getScheduler(_interfaceID);
            // Get the retry number from the object that is used to retry the
            // getNode in case connection is failed.
            conRetryNumber = cs3NodeScheduler.getRetryNum();
            // Host name and userAgent may be set into the VO corresponding to
            // each Node for authentication-CONFIRM, if it is not releted with
            // the request xml.
            String hostName = cs3NodeScheduler.getHeaderHostName();
            String userAgent = cs3NodeScheduler.getUserAgent();
            // check if cs3NodeScheduler is null throw exception.Confirm for
            // Error code(INTERFACE_CONNECTION_NULL)if required-It should be new
            // code like ERROR_NODE_FOUND!
            if (cs3NodeScheduler == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT);

            while (_ambgCurrentRetryCount++ <= _ambgMaxRetryCount) {
                try {
                    if (_log.isDebugEnabled())
                        _log.error("sendRequestToIN", "SENDING RETRY........" + (_ambgCurrentRetryCount - 1) + "IN Transaction Id" + (String) _requestMap.get("IN_RECON_ID"));
                    long startTimeNode = System.currentTimeMillis();
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                    // If the connection for corresponding node is failed, retry
                    // to get the node with configured number of times.
                    // If connection eshtablished then break the loop.
                    for (loop = 1; loop <= conRetryNumber; loop++) {
                        try {
                            cs3NodeVO = cs3NodeScheduler.getNodeVO(inReconID, _requestMap, p_action, (_ambgCurrentRetryCount - 1), loop);
                            INNodeLog.log(_referenceID, inReconID, cs3NodeVO.getUrl(), cs3NodeVO.isBlocked(), cs3NodeVO.isSuspended(), _msisdn, p_action, (_ambgCurrentRetryCount - 1), loop, (String) _requestMap.get("NETWORK_CODE"), cs3NodeVO.getConNumber(), "");
                            if (_log.isDebugEnabled())
                                _log.error("sendRequestToIN", "URL PICKED IS =" + cs3NodeVO.getUrl() + ", Txn Id=" + _referenceID + ", Action=" + p_action + ", Node Number: " + cs3NodeVO.getNodeNumber());
                            // Check if Node is foud or not.Confirm for Error
                            // code(INTERFACE_CONNECTION_NULL)if required-It
                            // should be new code like ERROR_NODE_FOUND!
                            if (cs3NodeVO == null)
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_DETAIL_NOT_FOUND);

                            _nodeCloser = cs3NodeVO.getNodeCloser();
                            try {
                                if (cs3NodeVO.isSuspended())
                                    _nodeCloser.checkExpiry(cs3NodeVO);
                            } catch (BTSLBaseException be) {
                                INNodeLog.log(_referenceID, _inTXNID, cs3NodeVO.getUrl(), cs3NodeVO.isBlocked(), cs3NodeVO.isSuspended(), _msisdn, p_action, (_ambgCurrentRetryCount - 1), loop, (String) _requestMap.get("NETWORK_CODE"), cs3NodeVO.getConNumber(), "Node picked is suspended due to AMBIGUOUS cases.");
                                if (loop == conRetryNumber) {
                                    if ("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)) {
                                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Sender Credit Back case. But all nodes are suspended. So marking the response as AMBIGUOUS");
                                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                    }
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MAXIMUM SHIFTING OF NODE IS REACHED");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                                }
                                cs3NodeVO.decrementConNumber(_inTXNID);
                                Thread.sleep(retrySleepTime);
                                continue;
                            } catch (Exception e) {
                                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }
                            warnTime = cs3NodeVO.getWarnTime();
                            // Get the read time out based on the action.
                            if (CS3VfeI.ACTION_GET_ACCOUNT_DETAILS == p_action || CS3VfeI.ACTION_GET_BAL_DATE == p_action)
                                readTimeOut = cs3NodeVO.getValReadTimeOut();
                            else
                                readTimeOut = cs3NodeVO.getTopReadTimeOut();
                            // Confirm for the service name servlet for the url
                            // consturction whether URL will be specified in
                            // INFile or IP,PORT and ServletName.
                            // cs3URLConnection = new
                            // CS3VfeUrlConnection(cs3NodeVO.getUrl(),cs3NodeVO.getUsername(),cs3NodeVO.getPassword(),cs3NodeVO.getConnectionTimeOut(),readTimeOut,cs3NodeVO.getKeepAlive(),p_inRequestStr.length(),hostName,userAgent);
                            cs3URLConnection = new CS3VfeUrlConnection(cs3NodeVO.getUrl(), cs3NodeVO.getUsername(), cs3NodeVO.getConnectionTimeOut(), readTimeOut, cs3NodeVO.getKeepAlive(), p_inRequestStr.length(), hostName, userAgent);
                            INNodeLog.log(_referenceID, _inTXNID, cs3NodeVO.getUrl(), cs3NodeVO.isBlocked(), cs3NodeVO.isSuspended(), _msisdn, p_action, (_ambgCurrentRetryCount - 1), loop, (String) _requestMap.get("NETWORK_CODE"), cs3NodeVO.getConNumber(), "Node picked for serving transaction");
                            // break the loop on getting the successfull
                            // connection for the node;
                            if (_log.isDebugEnabled())
                                _log.debug("sendRequestToIN", "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + cs3NodeVO.getNodeNumber() + "] created after the attempt number(loop)::" + loop);
                            break;
                        } catch (BTSLBaseException be) {
                            _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
                            throw be;// Confirm should we come out of loop or do
                                     // another retry
                        }// end of catch-BTSLBaseException
                        catch (Exception e) {
                            // In case of connection failure
                            // 1.Decrement the connection counter
                            // 2.set the Node as blocked
                            // 3.set the blocked time
                            // 4.Handle the event with level INFO, show the
                            // message that Node is blocked for some time
                            // (expiry time).
                            // Continue the retry loop till success;
                            // Check if the max retry attempt is reached raise
                            // exception with error code.
                            _log.error("sendRequestToIN", "Exception while creating connection e::" + e.getMessage());
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for CS3VfeIN with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");

                            _log.info("sendRequestToIN", "Setting the Node [" + cs3NodeVO.getNodeNumber() + "] as blocked for duration ::" + cs3NodeVO.getExpiryDuration() + " miliseconds");
                            cs3NodeVO.setBlocked(true);
                            cs3NodeVO.setBlokedAt(System.currentTimeMillis());
                            _nodeCloser.resetCounters(cs3NodeVO);
                            if (loop == conRetryNumber) {
                                if ("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)) {
                                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Sender Credit Back case. But all nodes are Blocked. So marking the response as AMBIGUOUS");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                }
                                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MAXIMUM SHIFTING OF NODE IS REACHED");
                                throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                            }
                            cs3NodeVO.decrementConNumber(_inTXNID);
                            Thread.sleep(retrySleepTime);
                            continue;
                        }// end of catch-Exception

                    }
                    loopV = loop;// end of for loop , Narendra VFE6 CR

                    long totalTimeNode = System.currentTimeMillis() - startTimeNode;
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Total time to find the scheduled Node totalTimeNode: " + totalTimeNode);
                } catch (BTSLBaseException be) {
                    // try{cs3NodeVO.decrementConNumber();}catch(Exception
                    // e){}-to Check properly
                    throw be;
                }// end of catch-BTSLBaseException
                catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]" + " Exception ::" + e.getMessage());
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }// end of catch-Exception
                try {
                    if (_log.isDebugEnabled())
                        _log.error("sendRequestToIN", "Actual node to which request is sent =" + cs3NodeVO.getUrl() + ", Txn Id=" + _referenceID + ", Action=" + p_action + ", Node Number: " + cs3NodeVO.getNodeNumber());
                    PrintWriter out = cs3URLConnection.getPrintWriter();
                    out.flush();
                    startTime = System.currentTimeMillis();
                    _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                    out.println(p_inRequestStr);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e);
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to CS3Vfe IN INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "] Exception::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
                }
                try {
                    StringBuffer buffer = new StringBuffer();
                    String response = "";
                    try {
                        // Get the response from the IN
                        cs3URLConnection.setBufferedReader();
                        BufferedReader in = cs3URLConnection.getBufferedReader();
                        // Reading the response from buffered reader.
                        while ((response = in.readLine()) != null) {
                            buffer.append(response);
                        }
                        endTime = System.currentTimeMillis();
                        if (warnTime <= (endTime - startTime)) {
                            _log.info("sendRequestToIN", "WARN time reaches, startTime::" + startTime + " endTime::" + endTime + " From file cache warnTime::" + warnTime + " time taken (endTime-startTime)::" + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3VfeINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "CS3VfeIN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the CS3VfeIN for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]" + "Exception=" + e.getMessage());
                        _nodeCloser.updateCountersOnAmbiguousResp(cs3NodeVO);
                        if (_ambgCurrentRetryCount > _ambgMaxRetryCount) {
                            _log.error("sendRequestToIN", "Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the CS3VfeIN for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]" + "Read timeout from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        Thread.sleep(retrySleepTime);
                        continue;
                    }// end of catch-Exception
                    finally {
                        if (endTime == 0)
                            endTime = System.currentTimeMillis();
                        _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                        _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + readTimeOut);
                    }// end of finally
                    responseStr = buffer.toString();
                    TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "responseStr::" + responseStr + ",,," + responseStr.length());
                    // TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
                    String httpStatus = cs3URLConnection.getResponseCode();
                    _requestMap.put("PROTOCOL_STATUS", httpStatus);
                    if (!CS3VfeI.HTTP_STATUS_200.equals(httpStatus))
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    // Check if there is no response, handle the event showing
                    // Blank response from CS3Vfe and stop further processing.
                    if (InterfaceUtil.isNullString(responseStr)) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from CS3VfeIN" + "Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                        _log.error("sendRequestToIN", "NULL response from interface");
                        _nodeCloser.updateCountersOnAmbiguousResp(cs3NodeVO);
                        if (_ambgCurrentRetryCount > _ambgMaxRetryCount) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from CS3VfeIN, Retry attempts exceeded so throwing AMBIGOUS exception" + " Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                            _log.error("sendRequestToIN", "NULL response from interface. Blank response from CS3VfeIN, Retry attempts exceeded so throwing AMBIGOUS exception " + " Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        Thread.sleep(retrySleepTime);
                        continue;
                    }

                    if (cs3NodeVO.isSuspended())
                        _nodeCloser.resetCounters(cs3NodeVO);

                    // Parse the response string and get the response Map.
                    _responseMap = _cs3VfeRequestFormatter.parseResponse(p_action, responseStr, _interfaceID);

                    // Here the various checks would be done based on the
                    // response.
                    // Check the fault code if it is not null then handle the
                    // event with message as fault string and error code.
                    // First check whether the responseCode is null
                    // If the response code is null,check the fault code,if
                    // present get the fault string and
                    // a.throw the exception with error code
                    // INTERFACE_PROCESS_REQUEST_ERROR.
                    // b.Handle the event with Level FATAL and message as fault
                    // strring
                    // 1.If the responseCode is other than 0
                    // a.check if the code is 102 then throw BTSLBaseException
                    // 2.If the responseCode is 0 then checks the following.

                    String faultCode = (String) _responseMap.get("faultCode");
                    if (!InterfaceUtil.isNullString(faultCode)) {
                        // Log the value of executionStatus for corresponding
                        // msisdn,recieved from the response.
                        _log.info("sendRequestToIN", "faultCode::" + faultCode + "_inTXNID::" + _inTXNID + " _msisdn::" + _msisdn);
                        _requestMap.put("INTERFACE_STATUS", faultCode);// Put
                                                                       // the
                                                                       // interface_status
                                                                       // in
                                                                       // requestMap
                        _log.error("sendRequestToIN", "faultCode=" + _responseMap.get("faultCode") + "faultString = " + _responseMap.get("faultString"));
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, (String) _responseMap.get("faultString"));
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                    // Check if responseCode=102,then throw exception with
                    // message INTERFACE_MSISDN_NOT_FOUND.
                    String responseCode = (String) _responseMap.get("responseCode");
                    _requestMap.put("INTERFACE_STATUS", responseCode);

                    // Added By Narendra Kumar For VFE6 CR
                    INNodeLog.log(_referenceID, _inTXNID, cs3NodeVO.getUrl(), cs3NodeVO.isBlocked(), cs3NodeVO.isSuspended(), _msisdn, p_action, (_ambgCurrentRetryCount - 1), loopV, (String) _requestMap.get("NETWORK_CODE"), cs3NodeVO.getConNumber(), "Node picked for serving transaction", responseCode, (endTime - startTime));

                    Object[] successList = CS3VfeI.RESULT_OK.split(",");

                    // if(!CS3VfeI.RESULT_OK.equals(responseCode))
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        if (CS3VfeI.SUBSCRIBER_NOT_FOUND.equals(responseCode) || CS3VfeI.ACNT_DISCONNECTED.equals(responseCode)) {
                            _log.error("sendRequestToIN", "Subscriber not found with MSISDN::" + _msisdn);
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber is not found at IN");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                        }// end of checking the subscriber existance.

                        // Mesasge is changed from client, so no need to handle
                        // this here
                        /*
                         * else if(CS3VfeI.ACNT_NEW.equals(responseCode) )
                         * {
                         * _requestMap.put("ACCOUNT_STATUS", "NEW");
                         * }
                         */
                        /*
                         * else
                         * if(CS3VfeI.ACNT_DISCONNECTED.equals(responseCode))
                         * {
                         * _requestMap.put("ACCOUNT_STATUS", "DISCONNECTED");
                         * }
                         */
                        else if (CS3VfeI.RESULT_100.equals(responseCode)) {
                            _log.error("sendRequestToIN", "Error code 100 received from IN for " + _referenceID + ". So marking response as AMBIGUOUS. " + "Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code 100 received from IN for " + _referenceID + ". So marking response as AMBIGUOUS. " + "Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                            // throw new
                            // BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);//
                            // If error code is 100, then AMBIGOUS
                            if (_ambgCurrentRetryCount > _ambgMaxRetryCount) {
                                _log.error("sendRequestToIN", "Error code 100 received from IN for " + _referenceID + ". Retry attempts exceeded so throwing AMBIGOUS exception. " + "Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code 100 received from IN for " + _referenceID + ". Retry attempts exceeded so throwing AMBIGOUS exception. " + "Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            }
                            Thread.sleep(retrySleepTime);
                            continue;
                        } else if (CS3VfeI.SUBSCRIBER_NOT_FOUND.equals(responseCode) || CS3VfeI.ACNT_DISCONNECTED.equals(responseCode) || CS3VfeI.ACNT_BARRED_REFILL.equals(responseCode) || CS3VfeI.ACNT_TEMP_BLOCKED.equals(responseCode) || CS3VfeI.DEDICATED_ACNT_NOT_ALLOWED.equals(responseCode) || CS3VfeI.DEDICATED_ACNT_NEGATIVE.equals(responseCode) || CS3VfeI.MAX_LIMIT_EXCEED.equals(responseCode) || CS3VfeI.BELOW_MIN_BAL.equals(responseCode) || CS3VfeI.ACNT_NOT_ACTIVE.equals(responseCode) || CS3VfeI.DEDICATED_ACT_MAX_LIMIT_EXCEED.equals(responseCode)) {
                            if (CS3VfeI.ACNT_NOT_ACTIVE.equals(responseCode)) {
                                _requestMap.put("ACCOUNT_STATUS", "NEW");
                            }
                            _log.error("sendRequestToIN", "Error code " + responseCode + " received from IN for " + _referenceID + ". Retry attempts exceeded so throwing AMBIGOUS exception. " + "Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code " + responseCode + " received from IN for " + _referenceID + ". Retry attempts exceeded so throwing AMBIGOUS exception. " + "Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                            throw new BTSLBaseException(responseCode);
                        } else {
                            _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                    }
                } catch (BTSLBaseException be) {
                    throw be;
                }// end of catch-BTSLBaseException
                catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }// end of catch-Exception
                break;
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            try {
                // Closing the HttpUrl connection
                if (cs3URLConnection != null)
                    cs3URLConnection.close();
                if (cs3NodeVO != null) {
                    _log.info("sendRequestToIN", "Connection of Node [" + cs3NodeVO.getNodeNumber() + "] for INTERFACE_ID=" + _interfaceID + " is closed");
                    // Decrement the connection number for the current Node.
                    cs3NodeVO.decrementConNumber(inReconID);
                    _log.info("sendRequestToIN", "After closing the connection for Node [" + cs3NodeVO.getNodeNumber() + "] USED connections are ::[" + cs3NodeVO.getConNumber() + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "While closing CS3VfeIN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting  _interfaceID::" + _interfaceID + " Stage::" + p_action + " responseStr::" + responseStr);
        }// end of finally

    }// end of sendRequestToIN

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
     * This method is used to set the interface parameters into requestMap,
     * these parameters are as bellow
     * 1.Origin node type.
     * 2.Origin host type.
     * 
     * @throws Exception
     */
    private void setInterfaceParameters(int p_action) throws Exception, BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            _requestMap.put("ACTION", String.valueOf(p_action));
            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            /*
             * String nodeType=FileCache.getValue(_interfaceID, "NODE_TYPE");
             * if(InterfaceUtil.isNullString(nodeType))
             * {
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,
             * EventComponentI.INTERFACES, EventStatusI.RAISED,
             * EventLevelI.FATAL,
             * "CS3VfeINHandler[setInterfaceParameters]","REFERENCE ID = "
             * +_referenceID
             * +"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID,
             * "Network code "+(String) _requestMap.get("NETWORK_CODE") ,
             * "NODE_TYPE is not defined in IN File ");
             * throw new
             * BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI
             * .ERROR_CONFIG_PROBLEM);
             * }
             * _requestMap.put("NODE_TYPE",nodeType.trim());
             */

            // To make Origin NodeType configurable based on catgory code :
            // added by harsh

            String reqService = _requestMap.get("REQ_SERVICE");
            String nodeType = null;
            if (!InterfaceUtil.isNullString(reqService) && PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(reqService)) {
                String categoryCode = _requestMap.get("CATEGORY_CODE");
                nodeType = FileCache.getValue(_interfaceID, categoryCode + "_NODE_TYPE");
                if (InterfaceUtil.isNullString(nodeType))
                    nodeType = FileCache.getValue(_interfaceID, reqService + "_DEFAULT" + "_NODE_TYPE");
            }
            if (!InterfaceUtil.isNullString(reqService) && PretupsI.SERVICE_TYPE_P2PRECHARGE.equals(reqService))
                nodeType = FileCache.getValue(_interfaceID, reqService + "_NODE_TYPE");
            if (nodeType == null)
                nodeType = FileCache.getValue(_interfaceID, reqService + "_NODE_TYPE");
            _requestMap.put("NODE_TYPE", nodeType.trim());

            // end added by harsh

            String hostName = FileCache.getValue(_interfaceID, "HOST_NAME");
            if (InterfaceUtil.isNullString(hostName)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "HOST_NAME is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("HOST_NAME", hostName.trim());

            String currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CURRENCY", currency.trim());

            String subscriberNumberNAI = FileCache.getValue(_interfaceID, "NAI");
            if (InterfaceUtil.isNullString(subscriberNumberNAI))
                subscriberNumberNAI = "2";
            _requestMap.put("SubscriberNumberNAI", subscriberNumberNAI);

            if (p_action == CS3VfeI.ACTION_RECHARGE_CREDIT) {
                String accountAfterFlag = FileCache.getValue(_interfaceID, "REFILL_ACNT_AFTER_FLAG");
                if (InterfaceUtil.isNullString(accountAfterFlag))
                    accountAfterFlag = "1";
                _requestMap.put("REFILL_ACNT_AFTER_FLAG", accountAfterFlag);

                String accountBeforeFlag = FileCache.getValue(_interfaceID, "REFILL_ACNT_B4_FLAG");
                if (InterfaceUtil.isNullString(accountBeforeFlag))
                    accountBeforeFlag = "1";
                _requestMap.put("REFILL_ACNT_B4_FLAG", accountBeforeFlag);

                String externalData1 = FileCache.getValue(_interfaceID, "CR_EXTERNAL_DATA1");
                if (!InterfaceUtil.isNullString(externalData1))
                    _requestMap.put("CR_EXTERNAL_DATA1", externalData1.trim());

                String externalData2 = FileCache.getValue(_interfaceID, "CR_EXTERNAL_DATA2");
                if (!InterfaceUtil.isNullString(externalData2))
                    _requestMap.put("CR_EXTERNAL_DATA2", externalData2.trim());

                String externalData3 = FileCache.getValue(_interfaceID, "CR_EXTERNAL_DATA3");
                if (!InterfaceUtil.isNullString(externalData3))
                    _requestMap.put("CR_EXTERNAL_DATA3", externalData3.trim());

                String externalData4 = FileCache.getValue(_interfaceID, "CR_EXTERNAL_DATA4");
                if (!InterfaceUtil.isNullString(externalData4))
                    _requestMap.put("CR_EXTERNAL_DATA4", externalData4.trim());
            } else if (p_action == CS3VfeI.ACTION_GET_ACCOUNT_DETAILS) {
                String srvOfferApplicable = FileCache.getValue(_interfaceID, "SRVC_OFFER_APPLICABLE");
                if (InterfaceUtil.isNullString(srvOfferApplicable)) {
                    _log.error("credit", "SRVC_OFFER_APPLICABLE is not configured ");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SRVC_OFFER_APPLICABLE is not configured ");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("SRVC_OFFER_APPLICABLE", srvOfferApplicable);

                if ("Y".equals(srvOfferApplicable)) {
                    String srvOfferClasses = FileCache.getValue(_interfaceID, "SRVC_OFFER_CLASSES");
                    if (InterfaceUtil.isNullString(srvOfferClasses)) {
                        _log.error("credit", "Service offering  service classes are not configured.");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Service offering  service classes are not configured.");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("SRVC_OFFER_CLASSES", srvOfferClasses);

                    /*
                     * String
                     * minAmtSrvOfferClasses=FileCache.getValue(_interfaceID
                     * ,"MIN_AMT_SRVC_OFFER_CLASSES");
                     * if(InterfaceUtil.isNullString(minAmtSrvOfferClasses))
                     * {
                     * _log.error("credit",
                     * "Minimum amout of Service offering for different service classes are not configured properly."
                     * );
                     * EventHandler.handle(EventIDI.SYSTEM_ERROR,
                     * EventComponentI.INTERFACES, EventStatusI.RAISED,
                     * EventLevelI.FATAL,
                     * "CS3VfeINHandler[credit]","REFERENCE ID = "
                     * +_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,
                     * (String) _requestMap.get("NETWORK_CODE"),
                     * "Minimum amout of Service offering for different service classes are not configured properly."
                     * );
                     * throw new
                     * BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM
                     * );
                     * }
                     * _requestMap.put("MIN_AMT_SRVC_OFFER_CLASSES",
                     * minAmtSrvOfferClasses);
                     * 
                     * if(srvOfferClasses.split(",").length!=srvOfferClasses.split
                     * (",").length)
                     * {
                     * //amount and service classes should be properly
                     * configured.
                     * _log.error("credit",
                     * "Number of service classe and service offering ammounts are not configured properly."
                     * );
                     * EventHandler.handle(EventIDI.SYSTEM_ERROR,
                     * EventComponentI.INTERFACES, EventStatusI.RAISED,
                     * EventLevelI.FATAL,
                     * "CS3VfeINHandler[credit]","REFERENCE ID = "
                     * +_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,
                     * (String) _requestMap.get("NETWORK_CODE"),
                     * "Number of service classe and service offering ammounts are not configured properly."
                     * );
                     * throw new
                     * BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM
                     * );
                     * }
                     */
                    String srvOfferBits = FileCache.getValue(_interfaceID, "SRVC_OFFER_BITS");
                    if (InterfaceUtil.isNullString(srvOfferBits)) {
                        _log.error("credit", "Service offering bits are not configured");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Service offering bits are not configured");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("SRVC_OFFER_BITS", srvOfferBits);
                }
            }
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
    }// end of setInterfaceParameters

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
            langFromIN = (String) _responseMap.get("languageIDCurrent");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString::" + mappingString + " langFromIN::" + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0; in < mappingArr.length; in++) {
                tempArr = mappingArr[in].split(":");
                if (langFromIN.equals(tempArr[0].trim())) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3VfeINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e::" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang::" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

    protected static synchronized String getINTransactionID(HashMap p_requestMap, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Entered");
        String serviceType = null;
        String userType = null;
        String transactionId = null;
        try {
            serviceType = (String) p_requestMap.get("REQ_SERVICE");
            if ("RC".equals(serviceType))
                serviceType = "1";
            /*
             * else if("PRC".equals(serviceType) || "PCR".equals(serviceType))
             * serviceType="2";
             */

            userType = (String) p_requestMap.get("USER_TYPE");
            if ("S".equals(userType))
                userType = "1";
            else if ("R".equals(userType))
                userType = "2";
            // R091224.0954.100001
            transactionId = (String) p_requestMap.get("TRANSACTION_ID");
            transactionId = transactionId.substring(1).replace(".", "");
            if (CS3VfeI.ACTION_GET_BAL_DATE == p_action)
                transactionId = serviceType + transactionId + userType + "1";
            else if (CS3VfeI.ACTION_RECHARGE_CREDIT == p_action)
                transactionId = serviceType + transactionId + userType + "2";
            else if (CS3VfeI.ACTION_GET_ACCOUNT_DETAILS == p_action)
                transactionId = serviceType + transactionId + userType + "3";
            if (PretupsI.SERVICE_TYPE_SUBSCRIBER_ENQUIRY.equals(p_requestMap.get("REQ_SERVICE"))) {
                transactionId = getDateStringFromDate(new Date(), "ddMMyyHHmmss");
            }
            /*
             * else
             * transactionId = serviceType+transactionId+userType+"4";
             */
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getINTransactionID", "Exception e::" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3VfeINHandler[setLanguageFromMapping]", "", "", "", "Exception occured while generating IN transaction id");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINTransactionID", "Entered");
        }
        return transactionId;
    }

    /*
     * private void notifySDSystem() throws BTSLBaseException
     * {
     * long sdEndTime=0;
     * long sdStartTime=0;
     * long sdWarnTime=0;
     * String sdResponseStr =null;
     * String sdRespStatus=null;
     * try
     * {
     * SDVfeProcessor sdVfeProcessor = new SDVfeProcessor(_interfaceID,_msisdn);
     * StringBuffer buffer = new StringBuffer();
     * String response = "";
     * try
     * {
     * //Get the response from the IN
     * sdVfeProcessor.setBufferedReader();
     * BufferedReader in = sdVfeProcessor.getBufferedReader();
     * //Reading the response from buffered reader.
     * while ((response = in.readLine()) != null)
     * {
     * buffer.append(response);
     * }
     * sdEndTime=System.currentTimeMillis();
     * try{sdWarnTime=Long.parseLong(FileCache.getValue(_interfaceID,"SD_WARN_TIME"
     * ));}catch(Exception e){sdWarnTime=2000;}
     * if(sdWarnTime<=(sdEndTime-sdStartTime))
     * {
     * _log.error("notifySDSystem",
     * "SD WARN time reaches, sdStartTime::"+sdStartTime
     * +" sdEndTime::"+sdEndTime+" From file cache warnTime::"+sdWarnTime+
     * " time taken (sdEndTime-sdStartTime)::"+(sdEndTime-sdStartTime));
     * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,
     * EventStatusI
     * .RAISED,EventLevelI.MAJOR,"CS3VfeINHandler[sendRequestToIN]",_inTXNID
     * ,_msisdn,(String)
     * _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID
     * +" Stage = SD"
     * ,"SD WARN time reaches, sdStartTime::"+sdStartTime+" sdEndTime::"
     * +sdEndTime+" From file cache warnTime::"+sdWarnTime+
     * " time taken (sdEndTime-sdStartTime)::"+(sdEndTime-sdStartTime));
     * }
     * _requestMap.put("SD_INTERFACE_STATUS", sdRespStatus);
     * _requestMap.put("SD_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("sendRequestToIN","Exception e::"+e.getMessage());
     * EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED,
     * EventLevelI.FATAL,"CS3VfeINHandler[sendRequestToIN]"
     * ,_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),
     * "While getting response from IN system got Exception="+e.getMessage());
     * 
     * }//end of catch-Exception
     * finally
     * {
     * if(sdEndTime==0) sdEndTime=System.currentTimeMillis();
     * _requestMap.put("SD_END_TIME",String.valueOf(sdEndTime));
     * _log.error("sendRequestToIN","Request sent to IN at:"+sdStartTime+
     * " Response received from IN at:"
     * +sdEndTime+" defined read time out is:"+FileCache
     * .getValue(_interfaceID,"SD_READ_TIMEOUT"));
     * TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get(
     * "NETWORK_CODE"
     * ),"SD_ACTION",PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"
     * +sdResponseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"");
     * }//end of finally
     * sdResponseStr = buffer.toString();
     * }
     * catch(BTSLBaseException be)
     * {
     * be.getMessage();
     * }catch(Exception e)
     * {
     * e.getMessage();
     * }
     * finally
     * {
     * //
     * SDTransactionLog.log(_referenceID,_referenceID,_msisdn,(String)_requestMap
     * .
     * get("NETWORK_CODE"),"SD_ACTION",PretupsI.TXN_LOG_REQTYPE_RES,"",sdResponseStr
     * ,sdRespStatus,String.valueOf(sdEndTime-sdStartTime),"SD_SYSTEM");
     * }
     * }
     */

    private static String getDateStringFromDate(Date date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

}
