package com.inter.cs3guinea;

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
import com.inter.cs3guinea.cs3guineascheduler.NodeCloser;
import com.inter.cs3guinea.cs3guineascheduler.NodeManager;
import com.inter.cs3guinea.cs3guineascheduler.NodeScheduler;
import com.inter.cs3guinea.cs3guineascheduler.NodeVO;

/**
 * @CS3GuineaINHandler.java
 *                          Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                          All Rights Reserved
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Chetan Kothari March 13,2009 Initial Creation
 *                          ----------------------------------------------------
 *                          --------------------------------------------
 *                          This is the main class for IN Handling of CS3CP5 IN
 *                          .
 */
public class CS3GuineaINHandler implements InterfaceHandler {

    private static Log _log = LogFactory.getLog(CS3GuineaINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the respose of the request as
                                       // key and value pair.
    private HashMap _responseMap = null;// Contains the request parameter as key
                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;
    private String _userType = null;
    private static SimpleDateFormat _sdf = new SimpleDateFormat("yyMMddHHmm");
    private static int _transactionIDCounter = 0;
    private static int _prevMinut = 0;
    private static CS3GuineaRequestFormatter _cs3GuineaRequestFormatter = null;
    private NodeCloser _nodeCloser = null;
    static {
        if (_log.isDebugEnabled())
            _log.debug("CS3GuineaINHandler[static]", "Entered");
        try {
            _cs3GuineaRequestFormatter = new CS3GuineaRequestFormatter();
        } catch (Exception e) {
            _log.error("CS3GuineaINHandler[static]", "While instantiation of CS3GuineaRequestFormatter get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[static]", "", "", "", "While instantiation of CS3GuineaRequestFormatter get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("CS3GuienaINHandler[static]", "Exited");
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
            _inTXNID = getINTransactionID(_requestMap);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();

            // key value of requestMap is formatted into XML string for the
            // validate request.
            String inStr = _cs3GuineaRequestFormatter.generateRequest(CS3GuineaI.ACTION_ACCOUNT_INFO, _requestMap);
            // sending the AccountInfo request to IN along with validate action
            // defined in CS3GuineaI interface
            sendRequestToIN(inStr, CS3GuineaI.ACTION_ACCOUNT_INFO);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // get value of accountValue1 from response map (accountValue1 was
            // set in response map in sendRequestToIN method.)
            String amountStr = (String) _responseMap.get("accountValue1");
            try {
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:" + e.getMessage());
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

            Date supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("supervisionDate"), "yyyyMMdd");
            Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("serviceFeeDate"), "yyyyMMdd");
            Date removalDate = InterfaceUtil.getDateFromDateString((String) _responseMap.get("serviceRemovalDate"), "yyyyMMdd");

            Date currentDate = new Date();
            if (currentDate.after(removalDate))
                _requestMap.put("ACCOUNT_STATUS", "EXHAUSTED");
            else if ((currentDate.after(supervisionExpiryDate)) && (currentDate.after(serviceFeeExpiryDate)))
                _requestMap.put("ACCOUNT_STATUS", "EXPIRED");
            else if ((currentDate.after(supervisionExpiryDate)))
                _requestMap.put("ACCOUNT_STATUS", "DEACTIVE");
            else if (supervisionExpiryDate.after(currentDate))
                _requestMap.put("ACCOUNT_STATUS", "ACTIVE");

            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
            // set OLD_EXPIRY_DATE in request map as returned from _responseMap.
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("supervisionDate")).trim(), "yyyyMMdd"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("serviceFeeDate")).trim(), "yyyyMMdd"));
            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping();
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:" + e.getMessage());
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
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // CHANGED FOR HANDLING OF CREDIT TRANSFER BETWEEN DEDICATED
            // ACCOUNT.
            if (((String) _requestMap.get("REQ_SERVICE")).equals(PretupsI.SERVICE_TYPE_P2PRECHARGE)) {
                creditAdjust(_requestMap);
                return;
            }
            _inTXNID = getINTransactionID(_requestMap);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _userType = (String) _requestMap.get("USER_TYPE");
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3GuienaINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            // key value of requestMap is formatted into XML string for the
            // validate request.
            String inStr = _cs3GuineaRequestFormatter.generateRequest(CS3GuineaI.ACTION_RECHARGE_CREDIT, _requestMap);

            // sending the Re-charge request to IN along with re-charge action
            // defined in CS3GuineaI interface
            sendRequestToIN(inStr, CS3GuineaI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set NEW_EXPIRY_DATE into request map

            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("supervisionDateAfter"), "yyyyMMdd"));
            _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("serviceFeeDateAfter"), "yyyyMMdd"));

            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            try {
                String postBalanceStr = (String) _responseMap.get("accountValueAfter1");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
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
        int deactiveValidityDays = 0;// Defines the validity days by which
                                     // adjustment to be made in case of account
                                     // is deactive
        int deactiveGraceDays = 0;// Defines the grace period by which
                                  // adjustment to be made. in case of account
                                  // is deactive
        int expiredValidityDays = 0;// Defines the validity days by which
                                    // adjustment to be made in case of accont
                                    // is Expired
        int expiredGraceDays = 0;// Defines the grace period by which adjustment
                                 // to be made. in case of account is Expired

        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // interface
                                                                    // id form
                                                                    // request
                                                                    // map
            _inTXNID = getINTransactionID(_requestMap);// Generate the IN
                                                       // transaction id and set
                                                       // in _requestMap
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            _msisdn = (String) _requestMap.get("MSISDN");// get MSISDN from
                                                         // request map
            // Get the Multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");

            deactiveValidityDays = Integer.parseInt(((String) FileCache.getValue(_interfaceID, "DEACTIVE_VALIDITY")).trim());
            deactiveGraceDays = Integer.parseInt(((String) FileCache.getValue(_interfaceID, "DEACTIVE_GRACE")).trim());
            expiredValidityDays = Integer.parseInt(((String) FileCache.getValue(_interfaceID, "EXPIRED_VALIDITY")).trim());
            expiredGraceDays = Integer.parseInt(((String) FileCache.getValue(_interfaceID, "EXPIRED_GRACE")).trim());
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3GuienaINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "transfer_amount:" + amountStr + " multFactor:" + multFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            boolean p2pGraceSendFlag = true;
            Date currentDate = new Date();
            // Check for the validity Adjustment-VALIDITY_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("VALIDITY_DAYS"))) {
                try {
                    // For the adjust ment make validity days as negative.
                    validityDays = Integer.parseInt(((String) _requestMap.get("VALIDITY_DAYS")).trim());

                    if ("PRC".equals((String) _requestMap.get("REQ_SERVICE")) && ("DEACTIVE".equals((String) _requestMap.get("ACCOUNT_STATUS")) || "EXPIRED".equals((String) _requestMap.get("ACCOUNT_STATUS")))) {
                        String previousSupervisionExpiryDate = (String) _requestMap.get("OLD_EXPIRY_DATE");
                        if (_log.isDebugEnabled())
                            _log.debug("creditAdjust", "SSSS previousSupervisionExpiryDate::" + previousSupervisionExpiryDate + ", CardgroupValidity" + previousSupervisionExpiryDate);
                        Date prevSupExpDate = InterfaceUtil.getDateFromDateString(previousSupervisionExpiryDate.trim(), PretupsI.DATE_FORMAT);
                        // to make subscriber Deactive subscriber active add 1
                        // day as per FSD,
                        int daysValidityDiffrence = InterfaceUtil.getDifferenceInUtilDates(currentDate, prevSupExpDate);
                        if ("DEACTIVE".equals((String) _requestMap.get("ACCOUNT_STATUS")))
                            validityDays = daysValidityDiffrence + deactiveValidityDays;
                        else
                            validityDays = daysValidityDiffrence + expiredValidityDays;
                        p2pGraceSendFlag = false;
                    }

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
            if (p2pGraceSendFlag && !InterfaceUtil.isNullString((String) _requestMap.get("GRACE_DAYS"))) {
                try {
                    // For the adjust ment make grace days as negative.
                    graceDays = Integer.parseInt(((String) _requestMap.get("GRACE_DAYS")).trim());
                    if (!p2pGraceSendFlag) {
                        String previousGraceExpiryDate = (String) _requestMap.get("OLD_GRACE_DATE");
                        if (_log.isDebugEnabled())
                            _log.debug("creditAdjust", "SSSS previousGraceExpiryDate::" + previousGraceExpiryDate + ", card group graceDays" + graceDays);
                        Date prevGraceExpDate = InterfaceUtil.getDateFromDateString(previousGraceExpiryDate.trim(), PretupsI.DATE_FORMAT);
                        // to make subscriber Deactive subscriber active add 1
                        // day as per FSD,deactiveGraceDays
                        if (currentDate.after(prevGraceExpDate))
                            graceDays = InterfaceUtil.getDifferenceInUtilDates(currentDate, prevGraceExpDate) + expiredGraceDays;
                        else
                            graceDays = deactiveGraceDays;
                    }
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

            String inStr = _cs3GuineaRequestFormatter.generateRequest(CS3GuineaI.ACTION_IMMEDIATE_DEBIT, _requestMap);

            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in CS3GuineaI interface
            sendRequestToIN(inStr, CS3GuineaI.ACTION_IMMEDIATE_DEBIT);
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
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
            // Get the Multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3GuienaINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
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
                    _log.error("debitAdjust", "Exception e::" + e.getMessage());
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
                    _log.error("debitAdjust", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }// end of checking graceAdjustment

            String inStr = _cs3GuineaRequestFormatter.generateRequest(CS3GuineaI.ACTION_IMMEDIATE_DEBIT, _requestMap);

            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in CS3GuineaI interface
            sendRequestToIN(inStr, CS3GuineaI.ACTION_IMMEDIATE_DEBIT);
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuienaINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
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
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr::" + p_inRequestStr + " p_action::" + p_action);
        TransactionLog.log(_referenceID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
        String responseStr = "";
        NodeVO cs3NodeVO = null;
        NodeScheduler cs3NodeScheduler = null;
        CS3GuineaUrlConnection cs3URLConnection = null;
        long startTime = 0;
        long endTime = 0;
        int conRetryNumber = 0;
        long warnTime = 0;
        int readTimeOut = 0;
        String inReconID = null;
        long retrySleepTime = 0;
        try {
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
            try {
                long startTimeNode = System.currentTimeMillis();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                // If the connection for corresponding node is failed, retry to
                // get the node with configured number of times.
                // If connection eshtablished then break the loop.
                for (int loop = 1; loop <= conRetryNumber; loop++) {
                    try {
                        cs3NodeVO = cs3NodeScheduler.getNodeVO(inReconID);
                        if (_log.isDebugEnabled())
                            _log.error("sendRequestToIN", "URL PICKED IS ......." + cs3NodeVO.getUrl());
                        // Check if Node is foud or not.Confirm for Error
                        // code(INTERFACE_CONNECTION_NULL)if required-It should
                        // be new code like ERROR_NODE_FOUND!
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
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Sender Credit Back case. But all nodes are suspended. So marking the response as AMBIGUOUS");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                }
                                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MAXIMUM SHIFTING OF NODE IS REACHED");
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
                        if (CS3GuineaI.ACTION_ACCOUNT_INFO == p_action)
                            readTimeOut = cs3NodeVO.getValReadTimeOut();
                        else
                            readTimeOut = cs3NodeVO.getTopReadTimeOut();
                        // Confirm for the service name servlet for the url
                        // consturction whether URL will be specified in INFile
                        // or IP,PORT and ServletName.
                        cs3URLConnection = new CS3GuineaUrlConnection(cs3NodeVO.getUrl(), cs3NodeVO.getUsername(), cs3NodeVO.getPassword(), cs3NodeVO.getConnectionTimeOut(), readTimeOut, cs3NodeVO.getKeepAlive(), p_inRequestStr.length(), hostName, userAgent);
                        // break the loop on getting the successfull connection
                        // for the node;
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
                        // 4.Handle the event with level INFO, show the message
                        // that Node is blocked for some time (expiry time).
                        // Continue the retry loop till success;
                        // Check if the max retry attempt is reached raise
                        // exception with error code.
                        _log.error("sendRequestToIN", "Exception while creating connection e::" + e.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for CS3GuineaIN with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");

                        _log.info("sendRequestToIN", "Setting the Node [" + cs3NodeVO.getNodeNumber() + "] as blocked for duration ::" + cs3NodeVO.getExpiryDuration() + " miliseconds");
                        cs3NodeVO.setBlocked(true);
                        cs3NodeVO.setBlokedAt(System.currentTimeMillis());
                        _nodeCloser.resetCounters(cs3NodeVO);
                        if (loop == conRetryNumber) {
                            if ("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)) {
                                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Sender Credit Back case. But all nodes are Blocked. So marking the response as AMBIGUOUS");
                                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            }
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MAXIMUM SHIFTING OF NODE IS REACHED");
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
                // try{cs3NodeVO.decrementConNumber();}catch(Exception e){}-to
                // Check properly
                throw be;
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]" + " Exception ::" + e.getMessage());
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
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to CS3Guinea IN INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "] Exception::" + e.getMessage());
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
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3GuineaINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "CS3GuineaIN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the CS3GuineaIN for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]" + "Exception=" + e.getMessage());
                    _nodeCloser.updateCountersOnAmbiguousResp(cs3NodeVO);
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }// end of catch-Exception
                finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + readTimeOut);
                }// end of finally
                responseStr = buffer.toString();
                TransactionLog.log(_referenceID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr::" + responseStr);
                // TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
                String httpStatus = cs3URLConnection.getResponseCode();
                _requestMap.put("PROTOCOL_STATUS", httpStatus);
                if (!CS3GuineaI.HTTP_STATUS_200.equals(httpStatus))
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                // Check if there is no response, handle the event showing Blank
                // response from CS3Guinea and stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from CS3GuineaIN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _nodeCloser.updateCountersOnAmbiguousResp(cs3NodeVO);
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                if (cs3NodeVO.isSuspended())
                    _nodeCloser.resetCounters(cs3NodeVO);
                // Parse the response string and get the response Map.
                _responseMap = _cs3GuineaRequestFormatter.parseResponse(p_action, responseStr, (String) _requestMap.get("CARD_GROUP_SELECTOR"), (String) _requestMap.get("REQ_SERVICE"), _interfaceID);
                // Here the various checks would be done based on the response.
                // Check the fault code if it is not null then handle the event
                // with message as fault string and error code.
                // First check whether the responseCode is null
                // If the response code is null,check the fault code,if present
                // get the fault string and
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
                    _requestMap.put("INTERFACE_STATUS", faultCode);// Put the
                                                                   // interface_status
                                                                   // in
                                                                   // requestMap
                    _log.error("sendRequestToIN", "faultCode=" + _responseMap.get("faultCode") + "faultString = " + _responseMap.get("faultString"));
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, (String) _responseMap.get("faultString"));
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                // Check if responseCode=102,then throw exception with message
                // INTERFACE_MSISDN_NOT_FOUND.
                String responseCode = (String) _responseMap.get("responseCode");
                _requestMap.put("INTERFACE_STATUS", responseCode);

                Object[] successList = CS3GuineaI.RESULT_OK.split(",");

                // if(!CS3GuineaI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode)) {
                    if (CS3GuineaI.SUBSCRIBER_NOT_FOUND.equals(responseCode)) {
                        _log.error("sendRequestToIN", "Subscriber not found with MSISDN::" + _msisdn);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber is not found at IN");
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }// end of checking the subscriber existance.
                    else {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
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
                _log.error("sendRequestToIN", "While closing CS3GuineaIN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + cs3NodeVO.getNodeNumber() + "]");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String nodeType = FileCache.getValue(_interfaceID, "NODE_TYPE");
            if (InterfaceUtil.isNullString(nodeType)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "NODE_TYPE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("NODE_TYPE", nodeType.trim());

            String hostName = FileCache.getValue(_interfaceID, "HOST_NAME");
            if (InterfaceUtil.isNullString(hostName)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "HOST_NAME is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("HOST_NAME", hostName.trim());

            String subscriberNAI = FileCache.getValue(_interfaceID, "NAI");
            if (InterfaceUtil.isNullString(subscriberNAI) || !InterfaceUtil.isNumeric(subscriberNAI)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "NAI is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SubscriberNumberNAI", subscriberNAI.trim());

            String currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CURRENCY", currency.trim());

            String RefillAccountAfterFlag = FileCache.getValue(_interfaceID, "REFILL_ACNT_AFTER_FLAG");
            if (InterfaceUtil.isNullString(RefillAccountAfterFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "REFILL_ACNT_AFTER_FLAG is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("REFILL_ACNT_AFTER_FLAG", RefillAccountAfterFlag.trim());

            String extData1 = FileCache.getValue(_interfaceID, "EXTERNAL_DATA1");
            if (InterfaceUtil.isNullString(extData1)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "EXTERNAL_DATA1 is not defined in IN File ");

            }
            _requestMap.put("EXTERNAL_DATA1", extData1.trim());

            String extData2 = FileCache.getValue(_interfaceID, "EXTERNAL_DATA2");
            if (InterfaceUtil.isNullString(extData2)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3GuineaINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "EXTERNAL_DATA2 is not defined in IN File ");

            }
            _requestMap.put("EXTERNAL_DATA2", extData2.trim());

            // Getting dedicated account ID's and selector mappings
            String dedicatedAccountString = FileCache.getValue(_interfaceID, "DEDICATED_ACC_IDS");
            _requestMap.put("DEDICATED_ACC_INFO", dedicatedAccountString);

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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3GuineaINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e::" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
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
            else if ("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType))
                serviceType = "6";

            userType = (String) p_requestMap.get("USER_TYPE");
            if ("S".equals(userType))
                userType = "3";
            else if ("R".equals(userType))
                userType = "2";

            instanceID = FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "INSTANCE_ID");
            if (InterfaceUtil.isNullString(instanceID)) {
                _log.error("validate", "Parameter INSTANCE_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3GuineaINHandler[validate]", "", "", (String) p_requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
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
}
