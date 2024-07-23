package com.inter.cs3mobinil;

/**
 * @author dhiraj.tiwari
 *         Created on May 1, 2008
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
import java.io.BufferedReader;
import java.io.PrintWriter;
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
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;
import com.inter.cs3mobinil.cs3scheduler.NodeCloser;
import com.inter.cs3mobinil.cs3scheduler.NodeManager;
import com.inter.cs3mobinil.cs3scheduler.NodeScheduler;
import com.inter.cs3mobinil.cs3scheduler.NodeVO;

public class CS3MobinilINHandler implements InterfaceHandler {

    private static Log _log = LogFactory.getLog(CS3MobinilINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the response of the request
                                       // as key and value pair.
    private HashMap _responseMap = null;// Contains the request parameter as key
                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// UNodesed to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;
    private String _userType = null;
    private static SimpleDateFormat _sdf = new SimpleDateFormat("yyMMddHHmm");
    private static int _transactionIDCounter = 0;
    private static int _prevMinut = 0;
    private static CS3MobinilRequestFormatter _cs3MobinilRequestFormatter = null;
    private int _ambgMaxRetryCount = 0;
    private int _ambgCurrentRetryCount = 0;
    private boolean _isRetryRequest = false;
    private NodeCloser _nodeCloser = null;
    static {
        if (_log.isDebugEnabled())
            _log.debug("CS3MobinilINHandler[static]", "Entered");
        try {
            _cs3MobinilRequestFormatter = new CS3MobinilRequestFormatter();
        } catch (Exception e) {
            _log.error("CS3MobinilINHandler[static]", "While instantiation of CS3MobinilRequestFormatter get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[static]", "", "", "", "While instantiation of CS3MobinilRequestFormatter get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3MobililINHandler[static]", "Exited");
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
        String selectorValue = "";
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            selectorValue = (String) _requestMap.get("CARD_GROUP_SELECTOR");
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
            _inTXNID = getINTransactionID(_requestMap);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();

            // key value of requestMap is formatted into XML string for the
            // validate request.
            String inStr = _cs3MobinilRequestFormatter.generateRequest(CS3MobinilI.ACTION_ACCOUNT_INFO, _requestMap);
            // sending the AccountInfo request to IN along with validate action
            // defined in CS3MobinilI interface
            sendRequestToIN(inStr, CS3MobinilI.ACTION_ACCOUNT_INFO);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // get value of accountValue1 from response map (accountValue1 was
            // set in response map in sendRequestToIN method.)
            String amountStr = (String) _responseMap.get("accountValue1");
            try {
                if (!InterfaceUtil.isNullString(selectorValue) && Integer.parseInt(selectorValue) > 3 && "S".equals((String) _requestMap.get("USER_TYPE"))) {
                    if (!InterfaceUtil.isNullString(selectorValue))
                        amountStr = getDedicatedAccountValue(_responseMap, selectorValue);
                }
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
                // temporary to run CS3 Mobinil as postpaid interface
                _requestMap.put("BILL_AMOUNT_BAL", "0");
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            if ("1".equals(_responseMap.get("temporaryBlockedFlag"))) {
                _requestMap.put("ACCOUNT_STATUS", "BARRED");
            }
            /*
             * Date supervisionExpiryDate =
             * InterfaceUtil.getDateFromDateString((
             * String)_responseMap.get("supervisionExpiryDate"),
             * "yyyyMMddHHmmss");
             * Date serviceFeeExpiryDate =
             * InterfaceUtil.getDateFromDateString((String
             * )_responseMap.get("serviceFeeExpiryDate"), "yyyyMMddHHmmss");
             */
            Date supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("supervisionExpiryDate"), "yyyyMMdd");
            Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");

            Date currentDate = new Date();

            if (supervisionExpiryDate.after(currentDate))
                _requestMap.put("ACCOUNT_STATUS", "ACTIVE");
            else if ((currentDate.after(supervisionExpiryDate)) && (serviceFeeExpiryDate.after(currentDate)))
                _requestMap.put("ACCOUNT_STATUS", "INACTIVE");
            else if (currentDate.after(serviceFeeExpiryDate))
                _requestMap.put("ACCOUNT_STATUS", "DEACTIVE");

            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
            // set OLD_EXPIRY_DATE in request map as returned from _responseMap.
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("supervisionExpiryDate")).trim(), "yyyyMMdd"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("serviceFeeExpiryDate")).trim(), "yyyyMMdd"));
            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping();
            int requestAmount = 0;
            if (_requestMap.get("REQ_AMOUNT") != null)
                requestAmount = Integer.parseInt((String) _requestMap.get("REQ_AMOUNT"));

            int interfacePrevAmount = Integer.parseInt(amountStr);
            // if account status in INACTIVE or
            // requestAmount>interfacePrevAmount then generating Account Details
            // request to check if subscriber is LDCC subscriber or not.
            if ((_requestMap.get("ACCOUNT_STATUS").equals("INACTIVE") || requestAmount > interfacePrevAmount) && ("S".equals(_requestMap.get("USER_TYPE")))) {
                inStr = _cs3MobinilRequestFormatter.generateRequest(CS3MobinilI.ACTION_ACCOUNT_DETAILS, _requestMap);
                _ambgCurrentRetryCount--;
                sendRequestToIN(inStr, CS3MobinilI.ACTION_ACCOUNT_DETAILS);
                if (CS3MobinilI.LDCC_SERVICE_OFFERING_ACT_FLAG.equals(_responseMap.get("serviceOfferingActiveFlag"))) {
                    // if ldcc then it should go to post-paid IN from
                    // controller.
                    // setting MSISDN_NOT_FOUND .so that it will go to
                    _requestMap.put("SERVICE_CLASS", "LDCC");
                    _requestMap.put("INTERFACE_STATUS", CS3MobinilI.SUBSCRIBER_NOT_FOUND);
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }
            }
            _requestMap.put("DEDICATED_ACC_ID", (String) _responseMap.get("DEDICATED_ACC_ID"));
            _requestMap.put("DEDICATED_ACC_VALUE", (String) _responseMap.get("DEDICATED_ACC_VALUE"));
            _requestMap.put("DEDICATED_ACC_EXPIRY", (String) _responseMap.get("DEDICATED_ACC_EXPIRY"));
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
        }
    }// end of validate

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
        String amountStr = null;
        _requestMap = p_requestMap;
        String transactionCode = null;
        String transactionType = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            transactionCode = FileCache.getValue(_interfaceID, "TRANSACTION_CODE_RF");
            transactionType = FileCache.getValue(_interfaceID, "TRANSACTION_TYPE_RF");
            _inTXNID = getINTransactionID(_requestMap);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _userType = (String) _requestMap.get("USER_TYPE");

            if (!InterfaceUtil.isNullString(transactionCode))
                _requestMap.put("TRANSACTION_CODE_RF", transactionCode);

            if (!InterfaceUtil.isNullString(transactionType))
                _requestMap.put("TRANSACTION_TYPE_RF", transactionType);

            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3MobililINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            // key value of requestMap is formatted into XML string for the
            // validate request.
            String inStr = _cs3MobinilRequestFormatter.generateRequest(CS3MobinilI.ACTION_RECHARGE_CREDIT, _requestMap);

            try {
                _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "CREDIT_RETRY_CNT"));
            } catch (Exception e) {
                _ambgMaxRetryCount = 1;
            }

            // sending the Re-charge request to IN along with re-charge action
            // defined in CS3MobililI interface
            sendRequestToIN(inStr, CS3MobinilI.ACTION_RECHARGE_CREDIT);

            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            // set NEW_EXPIRY_DATE into request map
            if (!_isRetryRequest) {
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("supervisionExpiryDate"), "yyyyMMdd"));
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd"));

                // set INTERFACE_POST_BALANCE into request map as obtained thru
                // response map.
                try {
                    String postBalanceStr = (String) _responseMap.get("accountValue1");
                    postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                    _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("credit", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            } else
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "CR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
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
        String transactionCode = null;
        String transactionType = null;
        int selectorCode;
        int action;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // interface
                                                                    // id form
                                                                    // request
                                                                    // map
            transactionCode = FileCache.getValue(_interfaceID, "TRANSACTION_CODE_CR");
            transactionType = FileCache.getValue(_interfaceID, "TRANSACTION_TYPE_CR");

            _inTXNID = getINTransactionID(_requestMap);// Generate the IN
                                                       // transaction id and set
                                                       // in _requestMap
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");// get MSISDN from
                                                         // request map

            // add transaction code and transaction type in request map
            if (!InterfaceUtil.isNullString(transactionCode))
                _requestMap.put("TRANSACTION_CODE_CR", transactionCode);

            if (!InterfaceUtil.isNullString(transactionType))
                _requestMap.put("TRANSACTION_TYPE_CR", transactionType);

            // Get the Multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();

            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3MobililINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
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
            }// end of checking validity days adjustment
             // Check for the grace Adjustment-GRACE_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("GRACE_DAYS"))) {
                try {
                    // For the adjustment make grace days as negative.
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

            try {
                _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "ADJ_RETRY_CNT"));
            } catch (Exception e) {
                _ambgMaxRetryCount = 1;
            }
            // Based on the selector, request will be formed
            selectorCode = Integer.valueOf((String) _requestMap.get("CARD_GROUP_SELECTOR"));
            String inStr = null;
            if (selectorCode <= 3)
                action = CS3MobinilI.ACTION_IMMEDIATE_DEBIT;
            else
                action = CS3MobinilI.ACTION_DEDICATED_ACCOUNT_CD;

            inStr = _cs3MobinilRequestFormatter.generateRequest(action, _requestMap);
            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in CS3MobililI interface
            sendRequestToIN(inStr, action);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "CR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
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
        String transactionCode = null;
        String transactionType = null;
        int selectorCode;
        int action;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // interface
                                                                    // id form
                                                                    // request
                                                                    // map
            transactionCode = FileCache.getValue(_interfaceID, "TRANSACTION_CODE_DR");
            transactionType = FileCache.getValue(_interfaceID, "TRANSACTION_TYPE_DR");
            _inTXNID = getINTransactionID(_requestMap);// Generate the IN
                                                       // transaction id and set
                                                       // in _requestMap
            _requestMap.put("IN_TXN_ID", _inTXNID);// get TRANSACTION_ID from
                                                   // request map (which has
                                                   // been passed by controller)
            _requestMap.put("IN_RECON_ID", _inTXNID);

            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            _msisdn = (String) _requestMap.get("MSISDN");// get MSISDN from
                                                         // request map

            // add transaction code and transaction type in request map
            if (!InterfaceUtil.isNullString(transactionCode))
                _requestMap.put("TRANSACTION_CODE_DR", transactionCode);

            if (!InterfaceUtil.isNullString(transactionType))
                _requestMap.put("TRANSACTION_TYPE_DR", transactionType);

            // Get the Multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3MobililINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
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
                    // For the adjustment make validity days as negative.
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
            }// end of checking validity days adjustment
             // Check for the grace Adjustment-GRACE_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("GRACE_DAYS"))) {
                try {
                    // For the adjustment make grace days as negative.
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
            selectorCode = Integer.valueOf((String) _requestMap.get("CARD_GROUP_SELECTOR"));
            if (selectorCode <= 3)
                action = CS3MobinilI.ACTION_IMMEDIATE_DEBIT;
            else
                action = CS3MobinilI.ACTION_DEDICATED_ACCOUNT_CD;
            String inStr = _cs3MobinilRequestFormatter.generateRequest(action, _requestMap);

            try {
                _ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "ADJ_RETRY_CNT"));
            } catch (Exception e) {
                _ambgMaxRetryCount = 1;
            }
            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in CS3MobililI interface
            sendRequestToIN(inStr, action);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "DR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobililINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
            throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }
    }// end of debitAdjust.

    /**
     * This method is used to send the request to IN and stored the response
     * after parsing.
     * This method also take care about to handle the error situation to send
     * the alarm and set the error code.
     * 1.Invoke the getNodeVO method of NodeScheduler class and pass the
     * Transaction Id.
     * 2.If the VO is Null then mark the request as fail and throw exception(New
     * Error code that defines No connection for any Node is available).
     * 3.If the VO is not NULL then pass the Node detail to CS3UrlConnection
     * class and get connection.
     * 4.After the processing the request(may be successful or fail) decrement
     * the connection counter and pass the
     * transaction id that is removed from the transNodeList.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered _msisdn=" + _msisdn + " p_action=" + p_action + " p_inRequestStr=" + p_inRequestStr);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
        String responseStr = "";
        NodeVO cs3NodeVO = null;
        NodeScheduler cs3NodeScheduler = null;
        CS3MobinilUrlConnection cs3URLConnection = null;
        long startTime = 0;
        long endTime = 0;
        int conRetryNumber = 0;
        long warnTime = 0;
        int readTimeOut = 0;
        String inReconID = null;
        long retrySleepTime = 0;
        try {
            if (p_action != CS3MobinilI.ACTION_ACCOUNT_DETAILS)
                _responseMap = new HashMap();

            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            retrySleepTime = Long.parseLong(FileCache.getValue(_interfaceID, "RETRY_SLEEP_TIME"));
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
                    if (p_action != CS3MobinilI.ACTION_ACCOUNT_INFO && p_action != CS3MobinilI.ACTION_ACCOUNT_DETAILS) {
                        if (_log.isDebugEnabled())
                            _log.error("sendRequestToIN", "SENDING RETRY........" + (_ambgCurrentRetryCount - 1) + "_isRetryRequest " + _isRetryRequest + "IN Transaction Id" + (String) _requestMap.get("IN_RECON_ID"));
                    }
                    long startTimeNode = System.currentTimeMillis();
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                    // If the connection for corresponding node is failed, retry
                    // to get the node with configured number of times.
                    // If connection established then break the loop.
                    for (int loop = 1; loop <= conRetryNumber; loop++) {
                        try {
                            cs3NodeVO = cs3NodeScheduler.getNodeVO(inReconID);
                            if (_log.isDebugEnabled())
                                _log.error("sendRequestToIN", "URL PICKED IS ......." + cs3NodeVO.getUrl());
                            // Check if Node is found or not.Confirm for Error
                            // code(INTERFACE_CONNECTION_NULL)if required-It
                            // should be new code like ERROR_NODE_FOUND!
                            if (cs3NodeVO == null)
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_DETAIL_NOT_FOUND);

                            _nodeCloser = cs3NodeVO.getNodeCloser();
                            try {
                                if (cs3NodeVO.isSuspended())
                                    _nodeCloser.checkExpiry(cs3NodeVO);
                            } catch (BTSLBaseException be) {
                                if (loop == conRetryNumber) {
                                    if ("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)) {
                                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Sender Credit Back case. But all nodes are suspended. So marking the response as AMBIGUOUS");
                                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                    }
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MAXIMUM SHIFTING OF NODE IS REACHED");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                                }
                                cs3NodeVO.decrementConNumber(_inTXNID);
                                continue;
                            } catch (Exception e) {
                                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }

                            warnTime = cs3NodeVO.getWarnTime();
                            // Get the read time out based on the action.
                            if (CS3MobinilI.ACTION_ACCOUNT_INFO == p_action || CS3MobinilI.ACTION_ACCOUNT_DETAILS == p_action)
                                readTimeOut = cs3NodeVO.getValReadTimeOut();
                            else
                                readTimeOut = cs3NodeVO.getTopReadTimeOut();
                            // Confirm for the service name SERVLET for the URL
                            // construction whether URL will be specified in
                            // INFile or IP,PORT and ServletName.
                            cs3URLConnection = new CS3MobinilUrlConnection(cs3NodeVO.getUrl(), cs3NodeVO.getUsername(), cs3NodeVO.getPassword(), cs3NodeVO.getConnectionTimeOut(), readTimeOut, cs3NodeVO.getKeepAlive(), p_inRequestStr.length(), hostName, userAgent);
                            // break the loop on getting the successful
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
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for CS3MobinilIN with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");

                            _log.info("sendRequestToIN", "Setting the Node [" + cs3NodeVO.getNodeNumber() + "] as blocked for duration ::" + cs3NodeVO.getExpiryDuration() + " miliseconds");
                            cs3NodeVO.setBlocked(true);
                            cs3NodeVO.setBlokedAt(System.currentTimeMillis());
                            _nodeCloser.resetCounters(cs3NodeVO);
                            if (loop == conRetryNumber) {
                                if ("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)) {
                                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Sender Credit Back case. But all nodes are Blocked. So marking the response as AMBIGUOUS");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                }
                                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MAXIMUM SHIFTING OF NODE IS REACHED");
                                throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                            }
                            cs3NodeVO.decrementConNumber(_inTXNID);
                            continue;
                        }// end of catch-Exception
                    }// end of for loop
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
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]" + " Exception ::" + e.getMessage());
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }// end of catch-Exception
                try {
                    PrintWriter out = cs3URLConnection.getPrintWriter();
                    out.flush();
                    startTime = System.currentTimeMillis();
                    _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                    out.println(p_inRequestStr);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e);
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to CS3Mobinil IN INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "] Exception::" + e.getMessage());
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
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3MobinilINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "CS3MobinilIN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the CS3MobinilIN for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]" + "Exception=" + e.getMessage());
                        _nodeCloser.updateCountersOnAmbiguousResp(cs3NodeVO);

                        if (_ambgCurrentRetryCount > _ambgMaxRetryCount) {
                            _log.error("sendRequestToIN", "Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
                            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Ferma6INHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Read timeout from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        Thread.sleep(retrySleepTime);
                        _isRetryRequest = true;
                        _requestMap.put("IN_RECON_ID", _inTXNID + "01");
                        _requestMap.put("IN_TXN_ID", _inTXNID + "01");
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
                        _log.debug("sendRequestToIN", "_msisdn=" + _msisdn + " p_action=" + p_action + " responseStr=" + responseStr);
                    // TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
                    String httpStatus = cs3URLConnection.getResponseCode();
                    _requestMap.put("PROTOCOL_STATUS", httpStatus);
                    if (!CS3MobinilI.HTTP_STATUS_200.equals(httpStatus))
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    // Check if there is no response, handle the event showing
                    // Blank response from CS3Mobinil and stop further
                    // processing.
                    if (InterfaceUtil.isNullString(responseStr)) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from CS3MobinilIN");
                        _log.error("sendRequestToIN", "NULL response from interface");
                        _nodeCloser.updateCountersOnAmbiguousResp(cs3NodeVO);

                        if (_ambgCurrentRetryCount > _ambgMaxRetryCount) {
                            _log.error("sendRequestToIN", "Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
                            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3MobinilINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "NULL response from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        Thread.sleep(retrySleepTime);
                        _isRetryRequest = true;
                        _requestMap.put("IN_RECON_ID", _inTXNID + "01");
                        _requestMap.put("IN_TXN_ID", _inTXNID + "01");

                        continue;
                    }
                    if (cs3NodeVO.isSuspended())
                        _nodeCloser.resetCounters(cs3NodeVO);

                    // Parse the response string and get the response Map.
                    _responseMap = _cs3MobinilRequestFormatter.parseResponse(p_action, responseStr, _responseMap);

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
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, (String) _responseMap.get("faultString"));
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                    // Check if responseCode=102,then throw exception with
                    // message INTERFACE_MSISDN_NOT_FOUND.
                    String responseCode = (String) _responseMap.get("responseCode");
                    _requestMap.put("INTERFACE_STATUS", responseCode);

                    Object[] successList = CS3MobinilI.RESULT_OK.split(",");

                    // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        if (CS3MobinilI.SUBSCRIBER_NOT_FOUND.equals(responseCode)) {
                            _log.error("sendRequestToIN", "Subscriber not found with MSISDN::" + _msisdn);
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber is not found at IN");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                        }// end of checking the subscriber existence.
                        else if (CS3MobinilI.OLD_TRANSACTION_ID.equals(responseCode)) {
                            if (!_isRetryRequest) {
                                if (_log.isDebugEnabled())
                                    _log.debug("sendRequestToIN", "_isRetryRequest:" + _isRetryRequest);
                                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Transaction ID mismatch");
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// recharge
                                                                                                               // request
                                                                                                               // with
                                                                                                               // old
                                                                                                               // transaction
                                                                                                               // id
                            }
                        } else {
                            _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
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
                _log.error("sendRequestToIN", "While closing CS3MobinilIN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
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
    private void setInterfaceParameters() throws Exception, BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String nodeType = FileCache.getValue(_interfaceID, "NODE_TYPE");
            if (InterfaceUtil.isNullString(nodeType)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "NODE_TYPE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("NODE_TYPE", nodeType.trim());

            String hostName = FileCache.getValue(_interfaceID, "HOST_NAME");
            if (InterfaceUtil.isNullString(hostName)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "HOST_NAME is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("HOST_NAME", hostName.trim());

            String subscriberNAI = FileCache.getValue(_interfaceID, "NAI");
            if (InterfaceUtil.isNullString(subscriberNAI) || !InterfaceUtil.isNumeric(subscriberNAI)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "NAI is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SubscriberNumberNAI", subscriberNAI.trim());

            String currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CURRENCY", currency.trim());

            String RefillAccountAfterFlag = FileCache.getValue(_interfaceID, "REFILL_ACNT_AFTER_FLAG");
            if (InterfaceUtil.isNullString(RefillAccountAfterFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "REFILL_ACNT_AFTER_FLAG is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("REFILL_ACNT_AFTER_FLAG", RefillAccountAfterFlag.trim());

            String extData1 = FileCache.getValue(_interfaceID, "EXTERNAL_DATA1");
            if (InterfaceUtil.isNullString(extData1))
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "EXTERNAL_DATA1 is not defined in IN File ");

            _requestMap.put("EXTERNAL_DATA1", extData1.trim());

            String extData2 = FileCache.getValue(_interfaceID, "EXTERNAL_DATA2");
            if (InterfaceUtil.isNullString(extData2))
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3MobinilINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "EXTERNAL_DATA2 is not defined in IN File ");

            _requestMap.put("EXTERNAL_DATA2", extData2.trim());
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3MobinilINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e::" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang::" + mappedLang);
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
        String systemStatusMapping = null;

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

    /**
     * Method to generate the transaction ID.
     * 
     * @throws BTSLBaseException
     */
    protected static synchronized String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException {
        String instanceID = null;
        int MAX_COUNTER = 9999;
        int inTxnLength = 4;
        String serviceType = null;
        String userType = null;
        Date mydate = null;
        String minut2Compare = null;
        String dateStr = null;
        String transactionId = null;
        try {
            // <service type>YYMMDD.HHmm.<instance code>XXXX<subscriber type>
            serviceType = (String) p_requestMap.get("REQ_SERVICE");
            if ("RC".equals(serviceType))
                serviceType = "8";
            else if ("PRCMDA".equals(serviceType))
                serviceType = "7";
            else if ("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType) || "C2SENQ".equals(serviceType))
                serviceType = "6";

            userType = (String) p_requestMap.get("USER_TYPE");
            if ("S".equals(userType))
                userType = "3";
            else if ("R".equals(userType))
                userType = "2";

            instanceID = FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "INSTANCE_ID");
            if (InterfaceUtil.isNullString(instanceID)) {
                _log.error("validate", "Parameter INSTANCE_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3MobinilINHandler[validate]", "", "", (String) p_requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            mydate = new Date();
            dateStr = _sdf.format(mydate);
            minut2Compare = dateStr.substring(8, 10);
            int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _transactionIDCounter = 1;
                _prevMinut = currentMinut;
            } else if (_transactionIDCounter > MAX_COUNTER)
                _transactionIDCounter = 1;
            else
                _transactionIDCounter++;

            String txnid = String.valueOf(_transactionIDCounter);

            int length = txnid.length();
            int tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    txnid = "0" + txnid;
            }

            transactionId = serviceType + dateStr + instanceID + txnid + userType;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return transactionId;
    }

    /**
     * Method to is to calculate the dedicated account value for the sender.
     * 
     * @throws BTSLBaseException
     */
    public String getDedicatedAccountValue(HashMap p_responseMap, String p_dedicatedAccID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getDedicatedAccountValue", "Entered with p_dedicatedAccID=" + p_dedicatedAccID);
        String dedicatedAccValue = "";
        String split = "\\|";
        try {
            if (!InterfaceUtil.isNullString((String) p_responseMap.get("DEDICATED_ACC_ID"))) {
                Object[] dedicatedID = ((String) p_responseMap.get("DEDICATED_ACC_ID")).split(split);

                if (Arrays.asList(dedicatedID).contains(p_dedicatedAccID)) {
                    String[] dedAccIDArray = ((String) p_responseMap.get("DEDICATED_ACC_ID")).split(split);
                    String[] dedAccValueArray = ((String) p_responseMap.get("DEDICATED_ACC_VALUE")).split(split);
                    for (int i = 0; i < dedAccIDArray.length; i++) {
                        if (p_dedicatedAccID.equals(dedAccIDArray[i])) {
                            dedicatedAccValue = dedAccValueArray[i];
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getDedicatedAccountValue", "Exited with dedicatedAccValue=" + dedicatedAccValue);
        }
        return dedicatedAccValue;
    }
}
