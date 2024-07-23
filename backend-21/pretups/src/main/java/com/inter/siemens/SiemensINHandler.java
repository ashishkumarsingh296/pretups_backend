package com.inter.siemens;

import java.io.BufferedReader;
import java.net.URLEncoder;
import java.util.Arrays;
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

/**
 * @(#)SiemensINHandler.java
 *                           Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Ashish Kumar Jun 15,2006 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 *                           Handler class for the Siemens Interface.
 */
public class SiemensINHandler implements InterfaceHandler {
    private HashMap _requestMap;// Contains the requested information.
    private HashMap _responseMap;// Used to store the response values get from
                                 // the IN.
    private String _interfaceID;// Store the interface id.
    private String _inTXNID;// Used to represent the IN transaction id.
    private String _msisdn;// Store the msisdn
    private String _referenceID;// Store the transaction id for the reference of
                                // transaction
    private SiemensRequestResponse _siemensRequestResponse;
    public static final int _VALIDATE_ACTION = 1;
    public static final int _DEBIT_ACTION = 2;
    public static final int _CREDIT_ACTION = 3;
    public int _ACTION_TXN_CANCEL = 5;
    public static final String _ACCOUNT_SELECTOR_ONPEAK = "1";
    public static final String _ACCOUNT_SELECTOR_SMSDATA = "2";
    public final String _HTTP_STATUS = "200";
    // As per discussed with Abhijit/Sanjay Sir
    private final String _ALLOWED_SELECTOR = "1,2";
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    public Log _log = LogFactory.getLog(this.getClass().getName());

    public SiemensINHandler() {
        // Create an instance of SiemenseRequestResponse
        _siemensRequestResponse = new SiemensRequestResponse();
    }

    /**
     * This method used to get the account information of subscriber that
     * includes the following
     * 1.Interface specific parameters are set and added to the requested map.
     * 2.Format the request into predefined xml,for this method internally calls
     * generateRequest method
     * of SiemensRequestResponse.
     * 3.For sending request internally calls private method sendRequestToIN.
     * 4.Process the response.
     * 5.Check the barring of msisdn and service allowed by IN
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_map =" + p_map);
        _requestMap = p_map;
        int multFactor = 0;// Multiplication factor(defined on INFile) by which
                           // amount is multiplied to get the system amount.
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get the
                                                                    // interface
                                                                    // id from
                                                                    // the
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID(); // get the
                                                           // INTransaction id
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");// Get the
                                                                      // TransactionID
                                                                      // from
                                                                      // the
                                                                      // request
                                                                      // map
            _msisdn = (String) _requestMap.get("MSISDN");// Get the MSISDN from
                                                         // the request map
            _requestMap.put("IN_TXN_ID", _inTXNID);// Set the INTransactionID
                                                   // into request map
            _requestMap.put("Stage", "VAL");// Set the stage into request map.
            // Construct the reconcialation id from the transaction id.This
            // includes following(Confirm)
            // 1.get the transaction id.
            // 2.Get the USER_TYPE and STAGE.
            // 3.Based on the user type and stage add the last bit(N,C,R,S or B)
            // to the in recoincialation id.

            // Call the generateRequest method of SiemensRequestResponse and
            // pass action to
            // generate the request string for getAccountInformation.
            // Action value 1 should be defined in a interface CONFIRM?.

            String instanceID = FileCache.getValue((String) _requestMap.get("INTERFACE_ID"), "INSTANCE_ID");
            if (InterfaceUtil.isNullString(instanceID)) {
                _log.error("getINReconTxnID", "Parameter INSTANCE_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("INSTANCE_ID", instanceID.trim());

            String multFactorStr = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactorStr == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                multFactor = Integer.parseInt(FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR"));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor defined in IN File should be NUMERIC");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if (_log.isDebugEnabled())
                _log.debug("validate", "Current time in milisecond before generating the validate request = " + System.currentTimeMillis());

            // Get the card group selector from the request map.
            // Card group selector used to define the type of
            // account(Onpeak-1,SMSData-2)
            String cardGroupSelector = (String) _requestMap.get("CARD_GROUP_SELECTOR");

            // Check for the requested selector, whether it is allowed or not,if
            // not allowed then Throw exception with Interface Error
            // code//Discussed with Abhijit/Sanjay sir.
            if (!BTSLUtil.isNullString(cardGroupSelector) && !_ALLOWED_SELECTOR.contains(cardGroupSelector)) {
                _log.error("validate", "Allowed Selectors are [" + _ALLOWED_SELECTOR + " ]Requested Selector " + cardGroupSelector + " is not allowed ");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_SELECTOR);
            }
            setInterfaceParameters(_interfaceID);
            String inStr = _siemensRequestResponse.generateRequest(_VALIDATE_ACTION, _requestMap);
            if (_log.isDebugEnabled())
                _log.debug("validate", "Current time in milisecond after generating the validate request = " + System.currentTimeMillis());

            // get the multiplication factor from the file cache(INFile)

            // sending the AccountInfo request to IN
            sendRequestToIN(inStr, _VALIDATE_ACTION);

            // Set the response parameter into request map these parameters are
            // SERVICE_CLASS,ACCOUNT_STATUS,TRANSACTION_STATUS,OLD_EXPIRY_DATE_ONPEAK
            // INTERFACE_PREV_BALANCE_ONPEAK,OLD_EXPIRY_DATE_SMS,INTERFACE_PREV_BALANCE_SMS
            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("service_classID"));// storing
                                                                                           // service
                                                                                           // class
                                                                                           // to
                                                                                           // request
                                                                                           // map.
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("account_status"));// storing
                                                                                           // account
                                                                                           // status
                                                                                           // to
                                                                                           // request
                                                                                           // map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            if (_log.isDebugEnabled())
                _log.debug("validate", "cardGroupSelector = " + cardGroupSelector);
            if (_ACCOUNT_SELECTOR_ONPEAK.equals(cardGroupSelector)) {
                long onpeakBalance = Long.parseLong((String) _responseMap.get("onpeak_account_balance"));
                long onPeakBalance = InterfaceUtil.getSystemAmount(onpeakBalance, multFactor);
                // Put the onpeak interface amount and validity to request map
                // after multiplied the interface amount with multFactor.
                // _requestMap.put("INTERFACE_PREV_BALANCE_ONPEAK",
                // String.valueOf(onPeakBalance));
                _requestMap.put("INTERFACE_PREV_BALANCE", String.valueOf(onPeakBalance));
                // Previous
                // _requestMap.put("OLD_EXPIRY_DATE",
                // InterfaceUtil.getDateFromDateString((String)_responseMap.get("onpeak_activity_end_date"),PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                // From IN,string date (dd/mm/yyyy) is converted as per required
                // //onpeak_inactivity_period
                _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("onpeak_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            } else if (_ACCOUNT_SELECTOR_SMSDATA.equals(cardGroupSelector)) {
                long smsdataBalance = Long.parseLong((String) _responseMap.get("sms_data_account_balance"));
                long smsDataBalance = InterfaceUtil.getSystemAmount(smsdataBalance, multFactor);
                // Put the smsdata interface amount and expiry to request map
                // after multiplied the interface amount with multFactor.
                _requestMap.put("INTERFACE_PREV_BALANCE", String.valueOf(smsDataBalance));
                // _requestMap.put("OLD_EXPIRY_DATE",
                // InterfaceUtil.getDateFromDateString((String)_responseMap.get("sms_data_activity_end_date"),PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("sms_data_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            }
            // double offpeakBalance= Double.parseDouble((String)
            // _responseMap.get("offpeak_account_balance"));
            // long offPeakBalance = (long)(offpeakBalance*multFactor);
            // Put the smsdata interface amount and expiry to request map after
            // multiplied the interface amount with multFactor.
            // _requestMap.put("INTERFACE_PREV_BALANCE_OFFPEAK",String.valueOf(offPeakBalance));
            // _requestMap.put("OLD_EXPIRY_DATE_OFFPEAK",
            // InterfaceUtil.getDateFromDateString((String)_responseMap.get("offpeak_activity_end_date"),PretupsI.DATE_FORMAT_DDMMYYYY));
            // CONFIRM FOR THE BONUS?
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        }// End of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// End of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap=" + _requestMap);
        }// End of finally
    }// end of validate

    /**
     * This method is responsible for credit back of senders for this it
     * interanlly calls credit method.
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_map = " + p_map);
        _requestMap = p_map;
        int multFactor = 0;// Multiplication factor(defined on INFile) by which
                           // amount is multiplied to get the system amount.
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            // Get the INTransactionID from
            _inTXNID = InterfaceUtil.getINTransactionID(); // get the
                                                           // INTransaction id
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");// Get the
                                                                      // TransactionID
                                                                      // from
                                                                      // the
                                                                      // request
                                                                      // map
            _msisdn = (String) _requestMap.get("MSISDN");// Get the MSISDN from
                                                         // the request map
            _requestMap.put("IN_TXN_ID", _inTXNID);

            // Construct the reconcialation id from the transaction id.This
            // includes following(Confirm)
            // 1.get the transaction id.
            // 2.Get the USER_TYPE and STAGE.
            // 3.Based on the user type and stage add the last bit(N,C,R,S or B)
            // to the in recoincialation id.

            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");

            String instanceID = FileCache.getValue((String) _requestMap.get("INTERFACE_ID"), "INSTANCE_ID");
            if (InterfaceUtil.isNullString(instanceID)) {
                _log.error("getINReconTxnID", "Parameter INSTANCE_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("INSTANCE_ID", instanceID.trim());
            String multFactorStr = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactorStr == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                multFactor = Integer.parseInt(multFactorStr);
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor defined in IN File should be NUMERIC");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            long amount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));

            String amountStr = InterfaceUtil.getDisplayAmount(amount, multFactor);

            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (roundFlag == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double amountDouble = Double.parseDouble(amountStr);
            if ("Y".equalsIgnoreCase(roundFlag)) {
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }

            _requestMap.put("transfer_amount", amountStr);

            // Get the card_group_selector value from the request map.
            String cardGroupSelector = (String) _requestMap.get("CARD_GROUP_SELECTOR");
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "cardGroupSelector = " + cardGroupSelector);

            // Check for the requested selector, whether it is allowed or not,if
            // not allowed then Throw exception with Interface Error
            // code//Discussed with Abhijit/Sanjay sir.
            if (!_ALLOWED_SELECTOR.contains(cardGroupSelector)) {
                _log.error("creditAdjust", "Allowed Selectors are [" + _ALLOWED_SELECTOR + " ]Requested Selector " + cardGroupSelector + " is not allowed ");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_SELECTOR);
            }
            // Call generateRequest method of SiemensRequestResponse to formate
            // credit request.
            // Action value 3 should be defined in a interface CONFIRM?.
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Current time in milisecond before generating the creditAdjust request = " + System.currentTimeMillis());

            setInterfaceParameters(_interfaceID);
            if ("R".equals(_requestMap.get("USER_TYPE")))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_requestMap.get("USER_TYPE")))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            String inStr = _siemensRequestResponse.generateRequest(_CREDIT_ACTION, _requestMap);
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Current time in milisecond after generating the creditAdjust request = " + System.currentTimeMillis());

            // sending the credit request to IN
            sendRequestToIN(inStr, _CREDIT_ACTION);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // After getting the resposne set the following parameters to
            // request map.
            // Based on the card_gruop_selector(Onpeak ammount,validity or
            // SmsData amount or validity) values will be
            // set in the request map.
            // a.TRANSACTION_STATUS
            // b.NEW_EXPIRY_DATE
            // c.INTERFACE_POST_BALANCE
            // PretupsE
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);// storing
                                                                                // the
                                                                                // execution
                                                                                // status
                                                                                // to
                                                                                // request
                                                                                // map
            if (_ACCOUNT_SELECTOR_ONPEAK.equals(cardGroupSelector)) {
                long postOnpeak = Long.parseLong((String) _responseMap.get("new_onpeak_account_balance"));
                long postBalanceOnpeak = InterfaceUtil.getSystemAmount(postOnpeak, multFactor);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceOnpeak));
                // _requestMap.put("NEW_EXPIRY_DATE",InterfaceUtil.getDateFromDateString((String)
                // _responseMap.get("new_onpeak_activity_end_date"),
                // PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("new_onpeak_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            } else if (_ACCOUNT_SELECTOR_SMSDATA.equals(cardGroupSelector)) {
                long postSmsData = Long.parseLong((String) _responseMap.get("new_sms_data_account_balance"));
                long postBalanceSmsData = InterfaceUtil.getSystemAmount(postSmsData, multFactor);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceSmsData));
                // _requestMap.put("NEW_EXPIRY_DATE",InterfaceUtil.getDateFromDateString((String)
                // _responseMap.get("new_sms_data_activity_end_date"),
                // PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("new_sms_data_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            }
        }// end of try-block
        catch (BTSLBaseException be) {
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited  _requestMap = " + _requestMap);
        }// end of finally.
    }// end of creditAdjust

    /**
     * This method is used to credit the user account and includes the following
     * activities.
     * 1.Get the intrfaceTransactionID from InterfaceUtil
     * 2.Generate the formatted request string for credit from
     * SiemensRequstResponse
     * 3.Call the method sendRequestToIN that creates connection and get the
     * response.
     * 4.After getting the response from IN set the following parameters to
     * requestMap
     * Confirm for
     * a.TRANSACTION_STATUS
     * b.OLD_EXPIRY_DATE_ONPEAK
     * OR
     * a.TRANSACTION_STATUS
     * b.NEW_EXPIRY_DATE
     * c.INTERFACE_POST_BALANCE
     * d.NEW_GRACE_DATE
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void credit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_map = " + p_map);
        _requestMap = p_map;
        int multFactor = 0;// Multiplication factor(defined on INFile) by which
                           // amount is multiplied to get the system amount.
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // Get the INTransactionID from
            _inTXNID = InterfaceUtil.getINTransactionID(); // get the
                                                           // INTransaction id
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");// Get the
                                                                      // TransactionID
                                                                      // from
                                                                      // the
                                                                      // request
                                                                      // map
            _msisdn = (String) _requestMap.get("MSISDN");// Get the MSISDN from
                                                         // the request map
            _requestMap.put("IN_TXN_ID", _inTXNID);

            // Construct the reconcialation id from the transaction id.This
            // includes following(Confirm)
            // 1.get the transaction id.
            // 2.Get the USER_TYPE and STAGE.
            // 3.Based on the user type and stage add the last bit(N,C,R,S or B)
            // to the in recoincialation id.
            String instanceID = FileCache.getValue(_interfaceID, "INSTANCE_ID");
            if (InterfaceUtil.isNullString(instanceID)) {
                _log.error("getINReconTxnID", "Parameter INSTANCE_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("INSTANCE_ID", instanceID.trim());
            String multFactorStr = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactorStr == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                multFactor = Integer.parseInt(multFactorStr);
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor defined in IN File should be NUMERIC");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            long amount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));

            String amountStr = InterfaceUtil.getDisplayAmount(amount, multFactor);

            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (roundFlag == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double amountDouble = Double.parseDouble(amountStr);
            if ("Y".equalsIgnoreCase(roundFlag)) {
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }

            _requestMap.put("transfer_amount", amountStr);

            // Get the card_group_selector value from the request map.
            String cardGroupSelector = (String) _requestMap.get("CARD_GROUP_SELECTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "cardGroupSelector = " + cardGroupSelector);

            // Check for the requested selector, whether it is allowed or not,if
            // not allowed then Throw exception with Interface Error
            // code//Discussed with Abhijit/Sanjay sir.
            if (!_ALLOWED_SELECTOR.contains(cardGroupSelector)) {
                _log.error("credit", "Allowed Selectors are [" + _ALLOWED_SELECTOR + " ]Requested Selector " + cardGroupSelector + " is not allowed ");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_SELECTOR);
            }
            // Call generateRequest method of SiemensRequestResponse to formate
            // credit request.
            // Action value 3 should be defined in a interface CONFIRM?.
            setInterfaceParameters(_interfaceID);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            if (_log.isDebugEnabled())
                _log.debug("credit", "Current time in milisecond before generating the Credit request = " + System.currentTimeMillis());
            String inStr = _siemensRequestResponse.generateRequest(_CREDIT_ACTION, _requestMap);
            if (_log.isDebugEnabled())
                _log.debug("credit", "Current time in milisecond after generating the Credit request = " + System.currentTimeMillis());

            // sending the credit request to IN
            sendRequestToIN(inStr, _CREDIT_ACTION);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // After getting the resposne set the following parameters to
            // request map.
            // Based on the card_gruop_selector(Onpeak ammount,validity or
            // SmsData amount or validity) values will be
            // set in the request map.
            // a.TRANSACTION_STATUS
            // b.NEW_EXPIRY_DATE
            // c.INTERFACE_POST_BALANCE
            // PretupsE
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);// storing
                                                                                // the
                                                                                // execution
                                                                                // status
                                                                                // to
                                                                                // request
                                                                                // map
            if (_ACCOUNT_SELECTOR_ONPEAK.equals(cardGroupSelector)) {
                long postOnpeak = Long.parseLong((String) _responseMap.get("new_onpeak_account_balance"));
                long postBalanceOnpeak = InterfaceUtil.getSystemAmount(postOnpeak, multFactor);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceOnpeak));
                // _requestMap.put("NEW_EXPIRY_DATE",InterfaceUtil.getDateFromDateString((String)
                // _responseMap.get("new_onpeak_activity_end_date"),
                // PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("new_onpeak_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            } else if (_ACCOUNT_SELECTOR_SMSDATA.equals(cardGroupSelector)) {
                long postSmsData = Long.parseLong((String) _responseMap.get("new_sms_data_account_balance"));
                long postBalanceSmsData = InterfaceUtil.getSystemAmount(postSmsData, multFactor);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceSmsData));
                // _requestMap.put("NEW_EXPIRY_DATE",InterfaceUtil.getDateFromDateString((String)
                // _responseMap.get("new_sms_data_activity_end_date"),
                // PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("new_sms_data_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            }
        }// end of try-block
        catch (BTSLBaseException be) {
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited  _requestMap = " + _requestMap);
        }// end of finally.
    }// end of credit.

    /**
     * This method is used to debit the user amount on IN. This includes the
     * following activities.
     * 1.Get the IN transaction ID with the help of getINTransactionID of
     * InterfaceUtil.
     * 2.set the interface parameters into requestMap
     * 3.Get the request string of debitAdjust request from the
     * SiemensRequestResponse.
     * 4.After getting the request string of debitAdjust,call sendRequestToIN
     * method to send
     * the request,it will set the response parameters into response map.
     * 5.Get the CARD_GROUP_SELECTOR value to set the appropriate
     * account(ONPEAK,SMSDATA or OFFPEAK).
     * 6.Based on the CARD_GROUP_SELECTOR value set INTERFACE_POST_BALANCE into
     * requestMap.
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_map =" + p_map);
        _requestMap = p_map;// Assignment of requested map
        int multFactor = 0;// Multiplication factor(defined on INFile) by which
                           // amount is multiplied to get the system amount.
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID(); // get the
                                                           // INTransaction id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");// Get the
                                                                      // TransactionID
                                                                      // from
                                                                      // the
                                                                      // request
                                                                      // map
            _msisdn = (String) _requestMap.get("MSISDN");// Get the MSISDN from
                                                         // the request map
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // Get the multiplication factor from FileCache(INFile)
            String instanceID = FileCache.getValue(_interfaceID, "INSTANCE_ID");
            if (InterfaceUtil.isNullString(instanceID)) {
                _log.error("getINReconTxnID", "Parameter INSTANCE_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("INSTANCE_ID", instanceID.trim());

            String multFactorStr = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactorStr == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                multFactor = Integer.parseInt(multFactorStr);
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor defined in IN File should be NUMERIC");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Construct the reconcialation id from the transaction id.This
            // includes following(Confirm)
            // 1.get the transaction id.
            // 2.Get the USER_TYPE and STAGE.
            // 3.Based on the user type and stage add the last bit(N,C,R,S or B)
            // to the in recoincialation id.

            // Call the generateRequest method of SiemensRequestResponse to
            // generate the debit request.
            // Action value 2 should be defined in a interface CONFIRM?.
            // Devide the Interface amount by multiplication factor before
            // sending the request to IN
            long amount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            String amountStr = InterfaceUtil.getDisplayAmount(amount, multFactor);

            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (roundFlag == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double amountDouble = Double.parseDouble(amountStr);
            if ("Y".equalsIgnoreCase(roundFlag)) {
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            _requestMap.put("transfer_amount", amountStr);
            String cardGroupSelector = (String) _requestMap.get("CARD_GROUP_SELECTOR");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "cardGroupSelector = " + cardGroupSelector);

            // Check for the requested selector, whether it is allowed or not,if
            // not allowed then Throw exception with Interface Error
            // code//Discussed with Abhijit/Sanjay sir.
            if (!_ALLOWED_SELECTOR.contains(cardGroupSelector)) {
                _log.error("debitAdjust", "Allowed Selectors are [" + _ALLOWED_SELECTOR + " ]Requested Selector " + cardGroupSelector + " is not allowed ");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_SELECTOR);
            }
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Current time in milisecond before generating the debitAdjust request = " + System.currentTimeMillis());
            setInterfaceParameters(_interfaceID);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            String inStr = _siemensRequestResponse.generateRequest(_DEBIT_ACTION, _requestMap);
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "generated request string inStr = " + inStr);
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Current time in milisecond after generating the debitAdjust request = " + System.currentTimeMillis());

            // sending the debit request to the IN
            sendRequestToIN(inStr, _DEBIT_ACTION);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Get the value of CARD_GROUP_SELECTOR from request.
            // Card group selector used to define the type of
            // account(Onpeak-1,SMSData-2)

            // Based on the CARD_GROUP_SELECTOR value set the ON PEAK post
            // amount, SMS DATA post amount or OFF PEAK post amount.
            if (_ACCOUNT_SELECTOR_ONPEAK.equals(cardGroupSelector)) {
                long postOnpeak = Long.parseLong((String) _responseMap.get("new_onpeak_account_balance"));
                long postBalanceOnpeak = InterfaceUtil.getSystemAmount(postOnpeak, multFactor);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceOnpeak));
                // _requestMap.put("VALIDITY",InterfaceUtil.getDateFromDateString((String)
                // _responseMap.get("new_onpeak_activity_end_date"),
                // PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                _requestMap.put("VALIDITY", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("new_onpeak_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            } else if (_ACCOUNT_SELECTOR_SMSDATA.equals(cardGroupSelector)) {
                long postSmsData = Long.parseLong((String) _responseMap.get("new_sms_data_account_balance"));
                long postBalanceSmsData = InterfaceUtil.getSystemAmount(postSmsData, multFactor);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceSmsData));
                // _requestMap.put("VALIDITY",InterfaceUtil.getDateFromDateString((String)
                // _responseMap.get("new_sms_data_activity_end_date"),
                // PretupsI.DATE_FORMAT_DDMMYYYY).toString());
                _requestMap.put("VALIDITY", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("new_sms_data_activity_end_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            p_map.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while debitAdjust");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }// end of finally
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
     * This method is used to send the request to IN and does the following.
     * 1.Make entry of transaction log in the start of the method, this log
     * contains request string with other details.
     * 2.Create a HttpUrlConnection object with specified URL, if any error
     * occurs handle the event.
     * 3.Get PrintWriter object and write the request,if any error occurs handle
     * the event.
     * 4.Read the response line by line.If the response is NULL mark this case
     * as AMBIGUOUS and handle the event.
     * 5.Check the time after creating connection and getting response from IN
     * against the warn time,if it is greater handle the event and stop the
     * processing.
     * 6.Parse the response string by using parseResponse method of the
     * SiemensRequestResponse based on the action.
     * 7.Check the http response code, if it is not equal to 200 stop the
     * processing and handle the event.Set it to requestMap.
     * 8.Get the execution status of the response if its value is not equal to
     * '1' then it checks following.
     * a.Find whether it is and AMBIGUOUS Case (execution status =
     * -103,execution status = -109),if found send the AMBIGUOUS message.
     * b.If it is not AMBIGUOUS case then stop processing with setting the value
     * of execution status in exception.
     * 
     * @param String
     *            p_requestStr
     * @param int p_action
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr = " + p_inRequestStr + " p_action = " + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
        String responseStr = "";// Contains the response
        long startTime = 0;// represent the current time when request is send.
        long endTime = 0;
        SiemensURLConnection siemensURLConnection = null;
        Object[] ambList = null;
        String ambStr = null;
        int readTimeOut = 0;
        try {
            _responseMap = new HashMap();
            // Creation of new HttpUrl connection.Arguments are stored in the
            // INFile and loaded through FileCache.
            // if any error occurs during the creation of url connection handle
            // the event and stop the processing.
            String inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            try {
                // Fetch the url,connect timeout,read timeout(validation or
                // topup) and keep alive values from the INFile
                String urlFromFileCache = FileCache.getValue(_interfaceID, "URL");
                int connectTimeOut = Integer.parseInt(FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT").trim());

                String readTimeOutStr = null;
                if (p_action == _VALIDATE_ACTION) {
                    readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_VAL");
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "READ_TIMEOUT_VAL::" + readTimeOutStr);
                } else {
                    readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_TOP");
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "READ_TIMEOUT_TOP::" + readTimeOutStr);
                }
                try {
                    readTimeOut = Integer.parseInt(readTimeOutStr.trim());
                } catch (Exception e) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Check the value of Read time out in the INFile");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                String contentType = (BTSLUtil.NullToString(FileCache.getValue(_interfaceID, "CONTENT_TYPE").trim()));
                String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "urlFromFileCache = " + urlFromFileCache + "  connectTimeOut = " + connectTimeOut + " readTimeOut =" + readTimeOut + " keepAlive = " + keepAlive + " contentType = " + contentType);
                String encodeAmp = URLEncoder.encode("&");
                String encodeEqual = URLEncoder.encode("=");
                String incodeUrl = URLEncoder.encode(p_inRequestStr);
                incodeUrl = incodeUrl.replace(encodeAmp, "&");
                incodeUrl = incodeUrl.replace(encodeEqual, "=");
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                siemensURLConnection = new SiemensURLConnection(urlFromFileCache + incodeUrl, connectTimeOut, readTimeOut, keepAlive, contentType);
            } catch (Exception e) {
                _log.error("sendRequestToIN", "Exception e=" + e);
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", "INTERFACE_ID = " + _interfaceID, "Stage = " + p_action, "", "Not able to create connection, getting Exception:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }// end of catch-Exception

            // In creditAdjust don't check interface status, simply send the
            // request to IN.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }
            // Getting the PrintWriter object to write the request string.If any
            // error occurs during the writting the request
            // stop the processing, handle the event showing there is Exception
            // while sending the request.
            try {
                // Create buffered reader and Read Response from the IN
                StringBuffer buffer = new StringBuffer();
                String response = "";
                try {
                    // Reading the response line by line using buffered reader
                    // and if response is not get properly stop the processing
                    // Handle the event showing that Exception while getting the
                    // response from the IN
                    siemensURLConnection.setBufferedReader();
                    BufferedReader in = siemensURLConnection.getBufferedReader();

                    // Getting the HTTP status code and set to the hashmap.
                    String httpStatus = siemensURLConnection.getResponseCode();
                    // Store the HTTP status code to request map.
                    _requestMap.put("PROTOCOL_STATUS", httpStatus);

                    // if the HTTP response status is not OK(code=200) stop the
                    // processing and set the error code as Bad request.
                    if (!_HTTP_STATUS.equals(httpStatus))
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);// invalid
                                                                                            // request

                    // Reading the response from buffered reader.
                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                    // If warn time in the IN file exist and the difference of
                    // start and end time of writing the request
                    // and reading the response is greater than the Warn time
                    // log the info and handle the event.
                    if (!InterfaceUtil.isNullString(warnTimeStr)) {
                        long warnTime = Long.parseLong(warnTimeStr);
                        if (endTime - startTime > warnTime) {
                            _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SiemensINHandler[sendRequestToIN]", _inTXNID, "INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, (String) _requestMap.get("NETWORK_CODE"), "Siemens IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                        }
                    }// end of if blok-checking the warn time should not null.
                }// end of try block
                catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", " response form interface is null, exception is " + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "SiemensINHandler[sendRequestToIN]", _inTXNID, "INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting response from Siemens IN e: " + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(_ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }// end of catch-Exception
                finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));

                }
                // Storing the response in responseStr.
                responseStr = buffer.toString();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr:" + responseStr);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);

                // Check if there is no response, handle the event showing Blank
                // response from Siemens and stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    // reconciliation case
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", _referenceID, "INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, (String) _requestMap.get("NETWORK_CODE"), "Blank response from Siemens IN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(_ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                // Remove the \n from the response string
                String finalResponseString = responseStr.replaceAll(System.getProperty("line.separator"), "");
                // Removing the double code from transparentData
                finalResponseString = finalResponseString.replace("\"", "");
                // To parse the response string,call the parseResponseMethod of
                // SiemensRequestResponse.
                _responseMap = _siemensRequestResponse.parseResponse(p_action, finalResponseString);

                // Get the value of execution status that is used to decide
                // whether the request is successful or not.
                String executionStatus = (String) _responseMap.get("execution_status");

                // Log the value of executionStatus for corresponding
                // msisdn,recieved from the response.
                _log.info("sendRequestToIN", "executionStatus: " + executionStatus + "_inTXNID = " + _inTXNID + " _msisdn = " + _msisdn);
                _requestMap.put("INTERFACE_STATUS", _responseMap.get("execution_status"));// Put
                                                                                          // the
                                                                                          // interface_status
                                                                                          // in
                                                                                          // requestMap

                // source Code which resumes the interface.
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
                 * if(_ACTION_TXN_CANCEL == p_stage)
                 * {
                 * _requestMap.put("CANCEL_RESP_STATUS",result);
                 * cancelTxnStatus =
                 * InterfaceUtil.getErrorCodeFromMapping(_requestMap
                 * ,result,"SYSTEM_STATUS_MAPPING");
                 * throw new BTSLBaseException(cancelTxnStatus);
                 * }
                 */

                // Fetching the ambiguous string from IN File.
                ambStr = FileCache.getValue(_interfaceID, "AMBIGUOUS_CASES");
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "ambStr::" + ambStr);
                // Check the value of execution status of the response if it is
                // equal to 1,it means OK.
                // If its value is not 1 then check whether it is an AMBIGUOUS
                // case or not,If it is an ambiguous case mark as AMBIGUOUS.
                if (!"1".equals(executionStatus)) {

                    // Ambiguous case would be raise if and only if these are
                    // defined into INFile and matched with the response's
                    // execution status.
                    if (!InterfaceUtil.isNullString(ambStr))
                        ambList = (ambStr.trim()).split(",");
                    // In this case handle the event and put message as
                    // AMBIGUOUS in the exception.
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "String length = |" + executionStatus.length() + "|   value |" + executionStatus + "| ");
                    // if("-103".equals(executionStatus.trim()) ||
                    // "-109".equals(executionStatus.trim()))
                    // This Case should be discussed while using interface
                    // closer.
                    // source Code which resumes the interface should be placed
                    // according to the client requirement
                    if (ambList != null && Arrays.asList(ambList).contains(executionStatus.trim())) {
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, "Execution Status  from the response = " + executionStatus);
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                    }
                    // This case handle all the situation listed in Siemens
                    // interface and handle the event showing the value of
                    // execution status recieved from the response.
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, "Execution Status  from the response = " + executionStatus);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }// end of if block-when the execution status value is not '1'.
                String cpTransID = ((String) _responseMap.get("transactionID"));
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "TransactionID retruned from the response  cpTransID = " + cpTransID);
                // Checking that whether the transaction id from the respose is
                // same as the requested transaction_id
                if (!inReconID.equals(cpTransID)) {
                    _log.info("sendRequestToIN", "inReconID:" + inReconID + " current TransId=" + cpTransID + " Mismatch");
                    // send alert message(TO BE IMPLEMENTED)
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, "INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", _referenceID, "inReconID:" + inReconID + " current TransId=" + cpTransID, (String) _requestMap.get("NETWORK_CODE"), "Request and Response Transaction id from Siemens IN does not match");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
            }// End of try-block
            catch (BTSLBaseException be) {
                _log.error("sendRequestToIN", "BTSLBaseException be=" + be);
                throw be;
            } // end of catch-BTSLBaseException
            catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, "INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting response from IN :" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }// end of catch-Exception
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", "INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            try {
                // Closing the HttpUrlconnection,InputStream and PrintWriter
                // object,while closing if any error occurs handle the event.
                if (siemensURLConnection != null)
                    siemensURLConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception while closing Siemens Connection:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + "INTERFACE_ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage());
            }// end of catch-Exception
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action :" + p_action + " responseStr: " + responseStr);
        }// end of finally
    }// end of sendRequestToIN

    public void setInterfaceParameters(String p_interfaceID) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_interfaceID=" + p_interfaceID);

        try {
            String cancelTxnAllowed = FileCache.getValue(p_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(p_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(p_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SiemensINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
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
             * String inStr = _formatter.generateRequest(_ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,_ACTION_TXN_CANCEL);
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
     * Main method
     * 
     * @param String
     *            [] ar
     */
    /*
     * public static void main(String[] ar)
     * {
     * SiemensINHandler inhandler = null;
     * try
     * {
     * String argConsFile = ar[0];
     * String argLogConfig = ar[1];
     * //Constants.load("C:\\Constants.props");
     * Constants.load(argConsFile);
     * //org.apache.log4j.PropertyConfigurator.configure("C:\\LogConfig.props");
     * org.apache.log4j.PropertyConfigurator.configure(argLogConfig);
     * FileCache.loadAtStartUp();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * System.out.println("main Exception e="+e.getMessage());
     * }
     * HashMap _requestMap=new HashMap();
     * _requestMap.put("IN_TXN_ID","C586666");
     * _requestMap.put("INTERFACE_ID","INTID00019");
     * _requestMap.put("cp_id","TOPCPID1");
     * _requestMap.put("application","1");
     * _requestMap.put("INTERFACE_AMOUNT","3");
     * _requestMap.put("VALIDITY_DAYS","1");
     * _requestMap.put("GRACE_DAYS","3");
     * _requestMap.put("transaction_currency","CFA");
     * _requestMap.put("op_transaction_id","45");
     * _requestMap.put("TRANSACTION_ID","33000");
     * 
     * //Adding the Type of account in the request map
     * _requestMap.put("CARD_GROUP_SELECTOR","1");
     * _requestMap.put("MODULE","C2S");
     * 
     * //_requestMap.put("ADJUST","Y");
     * //Debit request parameters
     * _requestMap.put("INTERFACE_PREV_BALANCE","1500");
     * 
     * //Credit request parameters
     * _requestMap.put("BONUS_AMOUNT","1");
     * _requestMap.put("CARD_GROUP","1");
     * _requestMap.put("BONUS_VALIDITY_DAYS","2");
     * try
     * {
     * _requestMap.put("MSISDN","2216100009");
     * inhandler = new SiemensINHandler();
     * //inhandler.validate(_requestMap);
     * inhandler.debitAdjust(_requestMap);
     * inhandler.credit(_requestMap);
     * 
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * System.out.println("main Exception e="+e.getMessage());
     * }//end of catch-Exeption
     * }//end of main
     */
}
