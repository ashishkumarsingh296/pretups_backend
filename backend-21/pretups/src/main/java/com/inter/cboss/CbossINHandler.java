package com.inter.cboss;

/**
 * @(#)CbossINHandler.java
 *                         Copyright(c) 2007, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Vinay Kumar Singh July 29, 2007 Initial Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         CbossINHandler Class is for PREPAID accounts.
 *                         Controller passes the required information into a
 *                         Hash map as method argument to CbossINHandler.
 *                         Various method (validate, credit, creditAdjust,
 *                         debitAdjust) are implemented to inquire the
 *                         subscriber account, credit and debit the account on
 *                         IN.
 *                         CbossINHandler puts the response parameter into
 *                         request map and sends back to the Controller.
 */
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
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
import com.btsl.pretups.inter.module.HandlerCommonUtility;
import com.btsl.pretups.inter.module.HandlerUtilityManager;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;

public class CbossINHandler extends HandlerCommonUtility implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(CbossINHandler.class.getName());
    private HashMap _requestMap = null; // Contains the request parameter as key
                                        // and value pair.
    private HashMap _responseMap = null; // Contains the respose of the request
                                         // as key and value pair.
    private String _interfaceID = null; // Contains the interfaceID
    private String _inTXNID = null; // Used to represent the Transaction ID
    private String _msisdn = null; // Used to store the MSISDN
    private String _referenceID = null; // Used to store the reference of
                                        // transaction id.
    private String _interfaceLiveStatus = null; // Used to get the interface
                                                // status
    private InterfaceCloserVO _interfaceCloserVO = null; // Reference of class
                                                         // InterfaceCloserVO.
    private InterfaceCloser _interfaceCloser = null; // Reference of class
                                                     // InterfaceCloser.
    private boolean _isSameRequest = false; // Boolean for veryfying requests.
    private String _userType = null; // User type(Sender or Receiver)
    private String _interfaceClosureSupport = null;

    /**
     * This method is used to validate the subscriber number on interface.
     * If required, this method will be used to get the subscriber account
     * information which is defined on the IN.
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
        String responseStr = null; // Response string
        String subsIdentificationRequired = "Y"; // Set the Subscriber
                                                 // Identification Required as Y
        try {
            // interface id(unique in the system)
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // Before sending request to IN, this method is used to check
            // interface status whether interface is Active or not.
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // To get the IN transaction Id.
            _inTXNID = getINReconTxnID();
            // put the in transaction id into the request map.
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // reference to the transaction id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            // MSISDN
            _msisdn = (String) _requestMap.get("MSISDN");
            // Check for subscriber identification
            if ("Y".equals(subsIdentificationRequired)) {
                sendRequestToIN(CbossI.ACTION_SUBSCRIBER_TYPE);
            }
            _requestMap.put("INT_SUBS_TYPE", _responseMap.get("SubscriberType"));
            // Check for Postpaid and Unknown numbers
            if ((CbossI.POST.equals(_responseMap.get("SubscriberType")) || CbossI.UNKNOWN.equals(_responseMap.get("SubscriberType")))) {
                _log.error("validate", "msisdn not found on interface: " + _interfaceID);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            } else if (CbossI.PRE.equals(_responseMap.get("SubscriberType"))) {
                // Fetch value of VALIDATION key from IN File. (this is used to
                // ensure that validation will be done on IN or not)
                String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
                // If validation of subscriber is not required set the SUCCESS
                // code into request map and return.
                if ("N".equals(validateRequired)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    return;
                }
                // Get the multiplication factor
                // While sending the amount to IN, it would be multiplied by
                // this factor, and recieved balance would be devided by this
                // factor.
                String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
                if (_log.isDebugEnabled())
                    _log.debug("validate", "multiplicationFactor:" + multiplicationFactor);
                // Test for null value(value not defined) of multiplication
                // factor
                if (InterfaceUtil.isNullString(multiplicationFactor)) {
                    _log.error("validate", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multiplicationFactor = multiplicationFactor.trim();
                // set the interface specific parameters into _requestMap
                setInterfaceParameters();
                // To get the IN transaction Id.
                _inTXNID = getINReconTxnID();
                _requestMap.put("IN_TXN_ID", _inTXNID);
                // sending the AccountInfo request to IN
                sendRequestToIN(CbossI.ACTION_ACCOUNT_INFO);
                // put the transaction status as SUCCESS in request map
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                responseStr = (String) _requestMap.get("RESPONSE_STR");
                // Get the balance from response map.
                String amountStr = (String) _responseMap.get("Balance");

                // PUT the value of INTERFACE_PREV_BALANCE into request map
                // after multiplying it with the multiplication factor.
                try {
                    amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multiplicationFactor));
                    _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("validate", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
                }
                // set the mapping language of system from FileCache mapping
                // based on the responsed language.
                setLanguageFromMapping();
                // Put the other values into the request map.
                _requestMap.put("SERVICE_CLASS", _responseMap.get("ServiceClass"));
                _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("Validity"), "yyyyMMdd"));
                _requestMap.put("ACCOUNT_STATUS", _responseMap.get("ACCOUNTStatus"));
                _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("GraceDate"), "yyyyMMdd"));
            }
        }// end of try block
         // Exception handling blocks
        catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            // If InterfaceError code is AMBIGUOUS then update the interface
            // closer counters and
            // make an entry in reconciliation file.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        }// end of catch-BTSLBaseException block of validate
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[validate]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While validating the subscriber get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of validate
        finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", " Exited. p_requestMap:" + p_requestMap);
        }// end of finally block of validate
    }// end of validate

    /**
     * This method implements the logic to credit the subscriber account which
     * is defined on the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String responseStr = null; // Response string
        double systemAmtDouble = 0; // System amount.
        double multFactorDouble = 0; // Multiplication factor
        String amountStr = null; // For amount in string

        try {
            // Interface id(unique in the system)
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // Before sending request to IN, this method is used to check
            // interface status whether interface is Active or not.
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // To get the IN transaction Id.
            _inTXNID = getINReconTxnID();
            // Put the in transaction id into the request map.
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // Reference to the transaction id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            // MSISDN
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetching the multiplication factor from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");

            if (InterfaceUtil.isNullString(multiplicationFactor)) {
                _log.error("credit", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multiplicationFactor = multiplicationFactor.trim();
            _requestMap.put("MULTIPLICATION_FACTOR", multiplicationFactor);
            // Set the interface specific parameters into _requestMap
            setInterfaceParameters();
            // Put the SYSTEM_STATUS_MAPPING into the request map as
            // SYSTEM_STATUS_MAPPING_CREDIT
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            try {
                multFactorDouble = Double.parseDouble(multiplicationFactor);
                // Amount on the interface(IN)
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                // System amount after multiplying it with multiplication factor
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("credit", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile, set its
                // value=Y.
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CbossINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            }
            // Exception handler block
            catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // This method is to accept the action as argument and send the
            // request to IN, based on the response code decides whether the
            // transaction is success full or not.
            sendRequestToIN(CbossI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Get the response string
            responseStr = (String) _requestMap.get("RESPONSE_STR");
            // Put the post balance after multiplied by multiplication factor.
            try {
                String postBalanceStr = (String) _responseMap.get("Balance");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            }
            // Exception handler block
            catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // Setting the updated values into the request map
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("ValidityDate"), "yyyyMMdd"));
            _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("GraceDate"), "yyyyMMdd"));
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        }// end of try block
         // Exception handling blocks
        catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            // If InterfaceError code is AMBIGUOUS then update the interface
            // closer counters and
            // make an entry in reconciliation file.
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                _requestMap.put("TRANSACTION_TYPE", "CR");
                // This method is responsible to perform cancel request (If
                // supported by IN) else it makes entry in reconciliation file
                // for all Ambiguous cases.
                handleCancelTransaction();
            }// end of try block
            catch (BTSLBaseException bte) {
                throw bte;
            }// end of catch-BTSLBaseException block
            catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } // end of catch-Exception block
        }// end of catch-BTSLBaseException block of credit
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[credit]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_RECHARGE_CREDIT, "While credit the subscriber's account, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of credit
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited.. p_requestMap:" + p_requestMap);
        }// end of finally block of credit
    }// end of credit

    /**
     * This method would be used for Credit Adjust for the user on the
     * interface.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String responseStr = null; // Response string
        double systemAmtDouble = 0; // System amount in double
        double multFactorDouble = 0; // multiplication factor
        String amountStr = null; // For amount in string

        try {
            // Get the type of the user(Sender or Receiver) for adjustment
            _userType = (String) _requestMap.get("USER_TYPE");
            // Interface id(unique in the system)
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // If the user type is receiver, check the interface for sending
            // request
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            // Get the IN transaction Id.
            _inTXNID = getINReconTxnID();
            // Put the IN Transaction Id into the request map
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // Reference to the transaction id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            // MSISDN
            _msisdn = (String) _requestMap.get("MSISDN");
            // Multiplication factor
            String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Displaying the multiplication factor on to the log.
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "multiplicationFactor:" + multiplicationFactor);
            // If the multiplication factor is null, throw the exception
            if (InterfaceUtil.isNullString(multiplicationFactor)) {
                _log.error("creditAdjust", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // removing the spaces from multiplication factor.
            multiplicationFactor = multiplicationFactor.trim();
            // Put the multiplication factor into the request map
            _requestMap.put("MULTIPLICATION_FACTOR", multiplicationFactor);
            // Set the interface parameters
            setInterfaceParameters();
            // validation for Receiver(R) and Sender(S)
            // If the user type is "R", then set mapping as
            // SYSTEM_STATUS_MAPPING_CREDIT_ADJ, else
            // SYSTEM_STATUS_MAPPING_CREDIT_BCK
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            try {
                multFactorDouble = Double.parseDouble(multiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                // Get the amount from system
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile, set its
                // value=Y
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CbossINHandler[creditAdjust]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            }// End of try block
             // Exception handler blocks
            catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }// end of catch-Exception block of creditAdjust

            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Recharge request to IN along with recharge action
            sendRequestToIN(CbossI.ACTION_IMMEDIATE_DEBIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            responseStr = (String) _requestMap.get("RESPONSE_STR");
            // set INTERFACE_POST_BALANCE into request map as obtained through
            // response map.
            try {
                String postBalanceStr = (String) _responseMap.get("Balance");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            }// end of try block
            catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }// end of catch-Exception block of creditAdjust
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        }// end of try block
         // Exception handler blocks
        catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
            // If InterfaceError code is AMBIGUOUS then update the interface
            // closer counters and
            // make an entry in reconciliation file.
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                _requestMap.put("TRANSACTION_TYPE", "CR");
                handleCancelTransaction();
            }// end of try block
            catch (BTSLBaseException bte) {
                throw bte;
            }// end of catch-BTSLBaseException block of creditAdjust
            catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } // end of catch-Exception block of creditAdjust
        }// end of catch-BTSLBaseException block of creditAdjust
        catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[creditAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_RECHARGE_CREDIT, "While creditAdjust the subscriber's account, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of creditAdjust
        finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", " p_requestMap:" + p_requestMap);
        }// end of finally block of creditAdjust
    }// end of creditAdjust

    /**
     * This method implements the logic to debit the subscriber account which is
     * defined on the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String responseStr = null; // Response string
        _requestMap = p_requestMap;
        double systemAmtDouble = 0; // For system amount in double
        double interfaceAmtDouble = 0; // For amount defined on interface
        String amountStr = null; // For amount in string

        try {
            // get intreface id from request map
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // Check the status of the interface(Active or Suspended)
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // To get the IN transaction Id.
            _inTXNID = getINReconTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // get TRANSACTION_ID from request map (which has been passed by
            // controller)
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            // get msisdn from request map
            _msisdn = (String) _requestMap.get("MSISDN");
            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "multiplicationFactor:" + multiplicationFactor);
            // check for multiplication factor
            if (InterfaceUtil.isNullString(multiplicationFactor)) {
                _log.error("debitAdjust", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "debitAadjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multiplicationFactor = multiplicationFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();
            // set the mapping as debit adjust mapping
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                double multFactorDouble = Double.parseDouble(multiplicationFactor);
                interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("debitAadjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CbossINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            }// end of try block
            catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }// end of catch-Exception block of debitAdjust
             // putting "-" sign before transfer_amount
            String transferDebitAmount = "-" + amountStr;
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "transfer_amount:" + transferDebitAmount + " multiplicationFactor:" + multiplicationFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", transferDebitAmount);
            // Validation required field
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            // finding whether the validation is requires or not
            if ("Y".equals(validateRequired)) {
                String prevBalanceAmountStr = (String) p_requestMap.get("INTERFACE_PREV_BALANCE");
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "prevBalanceAmountStr:" + prevBalanceAmountStr);
                // Throw an exception if prevBalanceAmountStr is null or numeric
                if (InterfaceUtil.isNullString(prevBalanceAmountStr) || !InterfaceUtil.isNumeric(prevBalanceAmountStr)) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE present in the requestMap is either null or not numeric");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT present in the requestMap is either null or not numeric");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                double prevBalanceAmount = Double.parseDouble(prevBalanceAmountStr.trim());
                // Check for sufficient previous balance, if previous balance is
                // less than that of the debit amount, throw the exception.
                if (prevBalanceAmount < interfaceAmtDouble) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
                }
            }
            // Send request to IN for immediate debit
            sendRequestToIN(CbossI.ACTION_IMMEDIATE_DEBIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Get the response string
            responseStr = (String) _requestMap.get("RESPONSE_STR");
            // set INTERFACE_POST_BALANCE into request map as obtained through
            // response map.
            try {
                String postBalanceStr = (String) _responseMap.get("Balance");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, Double.parseDouble(multiplicationFactor));
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            }// end of try block
            catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from response is not NUMERIC while parsing the balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_RESPONSE);
            }// end of catch-Exception block of debitAdjust
             // set POST_BALANCE_ENQ_SUCCESS as N in request map. why...????
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        }// end of try block
        catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            // If InterfaceError code is AMBIGUOUS then update the interface
            // closer counters and
            // make an entry in reconciliation file.
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                // update(increse) the counter for any AMBIGUOUS case.
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                _requestMap.put("TRANSACTION_TYPE", "DR");
                handleCancelTransaction();
            }// end of try block
            catch (BTSLBaseException bte) {
                throw bte;
            }// end of catch-BTSLBaseException block of debitAdjust
            catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } // end of catch-Exception block of debitAdjust
        }// end of catch-BTSLBaseException block of debitAdjust
        catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[debitAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_IMMEDIATE_DEBIT, "While debitAdjsut the the subscriber's account, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of debitAdjust
        finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited.. p_requestMap:" + p_requestMap);
        }// end of finally block of debitAdjust
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
     * This method is responsible to set the interface parameters into request
     * map,
     * (e.g. all interface closer related parameters)Also check the value of
     * each parameters for the NULL and blank,
     * if yes then Handle the event with level FATAL and throws the exception.
     * 
     * @throws Exception
     */
    private void setInterfaceParameters() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered _interfaceID=" + _interfaceID);

        try {
            // Currency code to be sent to IN
            String inCurrency = FileCache.getValue(_interfaceID, "IN_CURRENCY");
            if (InterfaceUtil.isNullString(inCurrency)) {
                inCurrency = "0";
                _log.error("setInterfaceParameters", "Value of IN_CURRENCY is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "IN_CURRENCY is not defined in the INFile.");
                // throw new
                // BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("Currency", inCurrency);
            // Cancel transaction field
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            // Set the cancel transaction allow in request map.
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());
            // system status mapping for credit
            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());
            // system status mapping for credit adjust
            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());
            // system status mapping for Debit adjust
            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());
            // system status mapping for credit back
            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());
            // cancel command status mapping
            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());
            // Cancel NA field(this field is for reconciliation)
            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        }// end of catch-BTSLBaseException block of setInterfaceParameters
        catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception block of setInterfaceParameters
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap: " + _requestMap);
        }// end of finally block of setInterfaceParameters
    }// end of setInterfaceParameters

    /**
     * This method is to accept the action as argument and send the request to
     * IN,
     * based on the response code decides whether the transaction is successfull
     * or not.
     * 
     * @param int p_action
     * @throws BTSLBaseException
     **/
    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN ", "Entered p_action : " + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Stored Proceedure is Called for stage:" + p_action, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
        Object dbUtility = null; // Reference of an Object
        Connection dbConnection = null; // Reference of Connection
        String inReconID = null; // Variable for IN Reconciliation Id
        String responseStr = null; // Field for response string
        long startTime = 0; // Variable to store start time of the transaction
        long endTime = 0; // Variable to store end time of the transaction
        long warnTime = 0; // Variable to store warn time for the transaction
        try {
            // IN Reconciliation ID
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            // In creditAdjust don't check interface status, simply send the
            // request to IN.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && (!("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))) && "S".equals(_userType)) || p_action != CbossI.ACTION_SUBSCRIBER_TYPE) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }

            // Creating the connection based on the action
            if (CbossI.ACTION_SUBSCRIBER_TYPE == p_action) {
                dbUtility = HandlerUtilityManager._utilityObjectMap.get(_interfaceID);
                if (dbUtility == null) {
                    _log.error("sendRequestToIN", "dbUtility: " + dbUtility);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                }
                dbConnection = ((HandlerCommonUtility) dbUtility).getConnection();
            } else {
                dbUtility = CbossDBPoolManager._dbUtilityObjectMap.get(_interfaceID);
                if (dbUtility == null) {
                    _log.error("sendRequestToIN", "dbUtility: " + dbUtility);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);
                }
                dbConnection = ((CbossDBUtility) dbUtility).getConnection();
            }
            // If dbConnection is null, throw an exception
            if (dbConnection == null) {
                _log.error("sendRequestToIN", "dbConnection= " + dbConnection);
                // Confirm for the Event handling.
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CbossINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "DBConnection is NULL");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);// Confrim
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
            switch (p_action) {
            // for validation
            case CbossI.ACTION_ACCOUNT_INFO: {
                sendAccountInfoRequest(dbConnection);
                break;
            }
            // for credit recharge
            case CbossI.ACTION_RECHARGE_CREDIT: {
                sendCreditRechargeRequest(dbConnection);
                break;
            }
            // For credit back or debit back
            case CbossI.ACTION_IMMEDIATE_DEBIT: {
                // For credit adjust request
                if ("C".equals(_requestMap.get("INTERFACE_ACTION")))
                    sendCreditAdjustRequest(dbConnection);
                // For debit adjust request
                else
                    sendDebitAdjustRequest(dbConnection);
                break;
            }
            // for subscriber identification request
            case CbossI.ACTION_SUBSCRIBER_TYPE: {
                // sendSubscriberIdentificationRequest(dbConnection,_requestMap);
                sendSubscriberIdentificationRequest(dbConnection);
                break;
            }
            }// end of switch statement
            dbConnection.commit();
            // End time
            endTime = System.currentTimeMillis();
            // Put the end time into the request map
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            // Warn time
            warnTime = Long.parseLong(FileCache.getValue(_interfaceID, "WARN_TIMEOUT"));
            if (endTime - startTime > warnTime) {
                _log.info("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CbossINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
            }
            // Get the request from the request map
            responseStr = (String) _requestMap.get("RESPONSE_STR");

            // Show the values onto the transaction log
            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Request String :" + "" + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
            // If response string is null, throw an exception
            if (InterfaceUtil.isNullString(responseStr)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Response from the IN:" + responseStr);
                _log.error("sendRequestToIN", " Response from the IN is null");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            // creating a new response map
            _responseMap = new HashMap();
            // Convert the responseString into hash map.
            InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");
            // Get the status from rsponse map
            String status = (String) _responseMap.get("Status");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && (!("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))) && "S".equals(_userType)) || p_action != CbossI.ACTION_SUBSCRIBER_TYPE) {
                if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
            }

            // If status is -ve, throw error response code
            _requestMap.put("INTERFACE_STATUS", status);
            if (status.charAt(0) == '-') {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Response from the IN: " + responseStr);
                _log.error("sendRequestToIN", "Error in response with" + " status=" + status);
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // If status is +ve, throw error response based on the combination
            // of Type & Status
            if (status.charAt(0) == '+')
                status = status.substring(1);

            if (!CbossI.RESULT_OK.equals(status)) {
                // If SUBSCRIBER_TYPE is post, throw INTERFACE_MSISDN_NOT_FOUND
                if (p_action == CbossI.ACTION_SUBSCRIBER_TYPE && CbossI.SUBTYPE_SUBSCRIBER_NOT_FOUND.equals(status)) {
                    _log.error("sendRequestToIN", " Error in response SUBTYPE_SUBSCRIBER_NOT_FOUND");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CbossINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " INTERFACE_MSISDN_NOT_FOUND AT IN");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);// Initialization
                                                                                                                          // Error
                }
                _log.error("sendRequestToIN", "Error in response with " + " status= " + status);
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendRequestToIN] ", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " ERROR_RESPONSE");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // if type is not PRE, throw INTERFACE_INVALID_PREPAID
            else if (!CbossI.PRE.equals(_responseMap.get("Type")) && p_action == CbossI.ACTION_ACCOUNT_INFO) {
                _log.error("sendRequestToIN", " Error in response, not a Prepaid");
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CbossINHandler[sendRequestToIN] ", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " Not a Prepaid" + "Type=" + _responseMap.get("Type") + "INVALID PREPAID MSISDN");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.VALIDATION_ERROR);
            }
            // if BarredFlag is true, throw INTERFACE_MSISDN_BARRED
            else if ("Y".equals(_responseMap.get("BarredFlag")) && p_action == CbossI.ACTION_ACCOUNT_INFO) {
                _requestMap.put("BarredFlag", _responseMap.get("BarredFlag"));
                _log.error("sendRequestToIN", " Error in response, Barred msisdn");
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CbossINHandler[sendRequestToIN] ", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " Barred msisdn AT IN");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);
            }

            // Check for ambiguous case
            String cbossTxnId = (String) _responseMap.get("transactionNumber");
            if (!(_inTXNID.equals(cbossTxnId))) {
                _log.error("sendRequestToIN", "Transaction id set in the request [" + _inTXNID + "] does not match with the transaction id fetched from response [" + cbossTxnId + "] ");
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " p_action = " + p_action, "Transaction id set in the request header [" + _inTXNID + "] does not match with the transaction id fetched from response[" + cbossTxnId + "],Hence marking the transaction as AMBIGUOUS");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
            }

        }// end of try block
         // Exception handling blocks
        catch (BTSLBaseException be) {
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (Exception e) {
            }
            throw be;
        }// end of catch-BTSLBaseException block of sendRequestToIN
        catch (Exception e) {
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e: " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "While calling the stored proc get Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

        }// end of catch-Exception block of sendRequestToIN
        finally {
            try {
                if (dbConnection != null)
                    dbConnection.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN ", " Exited  responseStr: " + responseStr);
        }// end of finally block of sendRequestToIN
    }// end of sendRequestToIN

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
            // String
            // procStr="call VOXTEL_TST.ETOPUP.getAccountInformation(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String procStr = "call cboss.getAccountInformation(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN")));
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.registerOutParameter(3, Types.VARCHAR);
            callStmt.registerOutParameter(4, Types.VARCHAR);
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.INTEGER);
            callStmt.registerOutParameter(7, Types.VARCHAR);
            callStmt.registerOutParameter(8, Types.DOUBLE);
            callStmt.registerOutParameter(9, Types.VARCHAR);
            callStmt.registerOutParameter(10, Types.VARCHAR);
            callStmt.registerOutParameter(11, Types.VARCHAR);
            callStmt.registerOutParameter(12, Types.VARCHAR);
            callStmt.registerOutParameter(13, Types.VARCHAR);
            callStmt.execute();
            responseBuffer = new StringBuffer(1024);
            responseBuffer.append("Status=" + callStmt.getString(3));
            responseBuffer.append("&Type=" + callStmt.getString(4));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(5));
            responseBuffer.append("&ServiceClass=" + String.valueOf(callStmt.getInt(6)));
            responseBuffer.append("&ACCOUNTStatus=" + callStmt.getString(7));
            responseBuffer.append("&Balance=" + callStmt.getDouble(8));
            responseBuffer.append("&Validity=" + callStmt.getString(9));
            responseBuffer.append("&GraceDate=" + callStmt.getString(10));
            responseBuffer.append("&BarredFlag=" + callStmt.getString(11));
            responseBuffer.append("&LanguageID=" + callStmt.getString(12));
            responseBuffer.append("&O_Error=" + callStmt.getString(13));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendAccountInfoRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendAccountInfoRequest the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-SQLException block of sendAccountInfoRequest
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendAccountInfoRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendAccountInfoRequest the subscriber get Exception e:" + e.getMessage());
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
     * This method calls stored procedure Recharge() and sets the response
     * parameters in response map.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void sendCreditRechargeRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendCreditRechargeRequest", " Entered");
        CallableStatement callStmt = null; // reference of CallableStatement
        StringBuffer responseBuffer = null; // reference of StringBuffer
        String responseStr = null; // Field for response string
        _responseMap = new HashMap();
        try {
            // String
            // procStr="call VOXTEL_TST.ETOPUP.Recharge(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String procStr = "call cboss.Recharge(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN")));
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.setDouble(3, Long.parseLong((String) _requestMap.get("transfer_amount")));
            callStmt.setInt(4, Integer.parseInt((String) _requestMap.get("VALIDITY_DAYS")));
            callStmt.setInt(5, Integer.parseInt((String) _requestMap.get("GRACE_DAYS")));
            callStmt.setString(6, (String) _requestMap.get("Currency"));
            callStmt.registerOutParameter(7, Types.VARCHAR);
            callStmt.registerOutParameter(8, Types.VARCHAR);
            callStmt.registerOutParameter(9, Types.DOUBLE);
            callStmt.registerOutParameter(10, Types.VARCHAR);
            callStmt.registerOutParameter(11, Types.VARCHAR);
            callStmt.registerOutParameter(12, Types.VARCHAR);
            callStmt.registerOutParameter(13, Types.VARCHAR);
            callStmt.execute();
            responseBuffer = new StringBuffer(1024);
            responseBuffer.append("Status=" + callStmt.getString(7));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(8));
            responseBuffer.append("&Balance=" + callStmt.getDouble(9));
            responseBuffer.append("&ACCOUNTStatus=" + callStmt.getString(10));
            responseBuffer.append("&ValidityDate=" + callStmt.getString(11));
            responseBuffer.append("&GraceDate=" + callStmt.getString(12));
            responseBuffer.append("&O_Error=" + callStmt.getString(13));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        }// end of try block
         // Exception handling blocks
        catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendCreditRechargeRequest", "SQLException sqe: " + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendCreditRechargeRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendCreditRechargeRequest the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-SQLException block of sendCreditRechargeRequest
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendCreditRechargeRequest", "Exception e: " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendCreditRechargeRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendCreditRechargeRequest the subscriber get Exception e:" + e.getMessage());
            // throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of sendCreditRechargeRequest
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
                _log.debug("sendCreditRechargeRequest", " Entered, MSISDN: ");
        }// end of finally block of sendCreditRechargeRequest
    }// end of sendCreditRechargeRequest

    /**
     * This method calls stored procedure Adjustment() and sets the response
     * parameters in response map.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void sendCreditAdjustRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendCreditAdjustRequest", " Entered");
        CallableStatement callStmt = null; // reference of CallableStatement
        StringBuffer responseBuffer = null; // reference of StringBuffer
        String responseStr = null; // Field for response string
        _responseMap = new HashMap();
        try {
            // String
            // procStr="call VOXTEL_TST.ETOPUP.Adjustment(?,?,?,?,?,?,?,?,?,?,?)";
            String procStr = "call cboss.Adjustment(?,?,?,?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN")));
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.setDouble(3, Long.parseLong((String) _requestMap.get("transfer_amount")));
            callStmt.setString(4, (String) _requestMap.get("Currency"));
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.VARCHAR);
            callStmt.registerOutParameter(7, Types.VARCHAR);
            callStmt.registerOutParameter(8, Types.DOUBLE);
            callStmt.registerOutParameter(9, Types.VARCHAR);
            callStmt.registerOutParameter(10, Types.VARCHAR);
            callStmt.registerOutParameter(11, Types.VARCHAR);
            callStmt.execute();
            responseBuffer = new StringBuffer(1024);
            responseBuffer.append("Status=" + callStmt.getString(5));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(6));
            responseBuffer.append("&ACCOUNTStatus=" + callStmt.getString(7));
            responseBuffer.append("&Balance=" + callStmt.getDouble(8));
            responseBuffer.append("&ValidityDate=" + callStmt.getString(9));
            responseBuffer.append("&GraceDate=" + callStmt.getString(10));
            responseBuffer.append("&O_Error=" + callStmt.getString(11));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        }// end of try block
        catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendCreditAdjustRequest", "SQLException sqe: " + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendCreditAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendCreditAdjustRequest the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-SQLException block of sendCreditAdjustRequest
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendCreditAdjustRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendCreditAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendCreditAdjustRequest the subscriber get Exception e:" + e.getMessage());
            // throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of sendCreditAdjustRequest
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
                _log.debug("sendCreditAdjustRequest", "Exited");
        }// end of finally block of sendCreditAdjustRequest
    }// end of sendCreditAdjustRequest

    /**
     * This method sendDebitAdjustRequest calls stored procedure Adjustment()
     * and sets the response parameters in response map.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void sendDebitAdjustRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendDebitAdjustRequest", "Entered");
        CallableStatement callStmt = null; // reference of CallableStatement
        StringBuffer responseBuffer = null; // reference of StringBuffer
        String responseStr = null; // Field for response string
        _responseMap = new HashMap();
        try {
            // String
            // procStr="call VOXTEL_TST.ETOPUP.Adjustment(?,?,?,?,?,?,?,?,?,?,?)";
            String procStr = "call cboss.Adjustment(?,?,?,?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN")));
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.setDouble(3, Long.parseLong((String) _requestMap.get("transfer_amount")));
            callStmt.setString(4, (String) _requestMap.get("Currency"));
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.VARCHAR);
            callStmt.registerOutParameter(7, Types.VARCHAR);
            callStmt.registerOutParameter(8, Types.DOUBLE);
            callStmt.registerOutParameter(9, Types.VARCHAR);
            callStmt.registerOutParameter(10, Types.VARCHAR);
            callStmt.registerOutParameter(11, Types.VARCHAR);
            callStmt.execute();
            responseBuffer = new StringBuffer(1024);
            responseBuffer.append("Status=" + callStmt.getString(5));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(6));
            responseBuffer.append("&ACCOUNTStatus=" + callStmt.getString(7));
            responseBuffer.append("&Balance=" + callStmt.getDouble(8));
            responseBuffer.append("&ValidityDate=" + callStmt.getString(9));
            responseBuffer.append("&GraceDate=" + callStmt.getString(10));
            responseBuffer.append("&O_Error=" + callStmt.getString(11));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        }// end of try block
        catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendDebitAdjustRequest", "SQLException sqe: " + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendDebitAdjustRequest the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-SQLException block of sendDebitAdjustRequest
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendDebitAdjustRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendDebitAdjustRequest the subscriber get Exception e:" + e.getMessage());
            // throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-Exception block of sendDebitAdjustRequest
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
                _log.debug("sendDebitAdjustRequest", " Exited");
        }// end of finally block of sendDebitAdjustRequest
    }// end of sendDebitAdjustRequest

    /**
     * This method calls stored procedure getSubscriberIdentification() and sets
     * the response parameters in response map.
     * Set the subscriber type in request map with key INT_SUBS_TYPE
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private void sendSubscriberIdentificationRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendSubscriberIdentificationRequest", "Entered ");
        CallableStatement callStmt = null; // reference of CallableStatement
        StringBuffer responseBuffer = null; // reference of StringBuffer
        String responseStr = null; // Field for response string
        _responseMap = new HashMap();
        try {
            // String
            // procStr="{call PB.bharti_tools.getSubscriberIdentification(?,?,?,?,?)}";
            String procStr = "{call mobi.getSubscriberIdentification(?,?,?,?,?)}";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN")));
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.registerOutParameter(3, Types.VARCHAR);
            callStmt.registerOutParameter(4, Types.VARCHAR);
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.execute();
            responseBuffer = new StringBuffer(1024);
            responseBuffer.append("Status=" + callStmt.getString(3));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(4));
            responseBuffer.append("&SubscriberType=" + callStmt.getString(5));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        }// end of try block
        catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendSubscriberIdentificationRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendSubscriberIdentificationRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendSubscriberIdentificationRequest the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }// end of catch-SQLException block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendSubscriberIdentificationRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[sendSubscriberIdentificationRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + CbossI.ACTION_ACCOUNT_INFO, "While sendSubscriberIdentificationRequest the subscriber get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

        }// end of catch-Exception block
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
                _log.debug("sendSubscriberIdentificationRequest", "Exited");
        }// end of finally
    }// end od method sendSubscriberIdentificationRequest

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
        // Flag defines whether the mapping of language is found or not.
        boolean mappingNotFound = true;
        String langFromIN = null;
        try {
            // Get the mapping string from the FileCache and storing all the
            // mappings into array which are separated by ','.
            String mappingString = (String) FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
            langFromIN = (String) _responseMap.get("LanguageID");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString = " + mappingString + " langFromIN = " + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0; in < mappingArr.length; in++) {
                tempArr = mappingArr[in].split(":");
                if (langFromIN.equals(tempArr[0])) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CbossINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CbossINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
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
            } // end of if-autoResumeSupported block
              // If Controller sends 'A' and interface status is suspended,
              // expiry is checked.
              // If Controller sends 'M', request is forwarded to IN after
              // resetting counters.
            if (InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(_interfaceLiveStatus) && _interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND)) {
                // Check if Auto Resume is supported by IN or not.If not then
                // throw exception. request would not be sent to IN.
                if ("N".equals(autoResumeSupported)) {
                    _log.error("checkInterfaceB4SendingRequest", "Interface Suspended.");
                    throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
                } // end of if-autoResumeSupported block
                  // If "Auto Resume" is supported then only check the expiry of
                  // interface, if expired then only request would be sent to IN
                  // otherwise checkExpiry method throws exception
                if (_isSameRequest)
                    _interfaceCloser.checkExpiry(_interfaceCloserVO);
            }
            // this block is executed when Interface is manually resumed
            // (Controller sends 'M')from suspend state
            else if (InterfaceCloserI.INTERFACE_MANNUAL_ACTIVE.equals(_interfaceCloserVO.getControllerIntStatus()) && _interfaceCloserVO.getFirstSuspendAt() != 0)
                _interfaceCloser.resetCounters(_interfaceCloserVO, null);
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        }// end of catch-BTSLBaseException block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("checkInterfaceB4SendingRequest", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception block
        finally {
            if (_log.isDebugEnabled())
                _log.debug("checkInterfaceB4SendingRequest", "Exited");
        }// end of finally block
    }// end of checkInterfaceB4SendingRequest

    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * 
     * @throws BTSLBaseException
     */
    private void handleCancelTransaction() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("handleCancelTransaction", "Entered.");
        String cancelTxnAllowed = null; // cancel transaction allow field
        String cancelTxnStatus = null; // cancel transaction status field
        String reconciliationLogStr = null; // reconciliation log string field
        String cancelCommandStatus = null; // cancel command status field
        String cancelNA = null; // cancel NA field
        String interfaceStatus = null; // field for interface status
        Log reconLog = null; // reference of Log
        String systemStatusMapping = null; // field for system status mapping
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
            // final status of transaction which was ambiguous)
            // which would be handled by validate, credit or debitAdjust
            // methods.
            if ("N".equals(cancelTxnAllowed)) {
                // Cancel command status as NA.
                cancelNA = (String) _requestMap.get("CANCEL_NA");
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
                                                                                                   // system.
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                // added to discard amount field from the message.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }// end of if-cancelTxnAllowed block
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        }// end of catch-BTSLBaseException block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("handleCancelTransaction", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "handleCancelTransaction", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception block
        finally {
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "Exited");
        }// end of finally block
    }// end of handleCancelTransaction

    /**
     * Method to used to get the IN Reconciliation ID from the IN.
     * 
     * @throws Exception
     */

    private String getINReconTxnID() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Enetered ");
        String inReconID = null;
        try {
            // get the user type from request map
            /*
             * String userType=(String)_requestMap.get("USER_TYPE");
             * if(userType!=null)
             * inReconID=
             * ((String)_requestMap.get("TRANSACTION_ID")+"."+userType);
             * else
             */
            inReconID = ((String) _requestMap.get("TRANSACTION_ID"));

            _requestMap.put("IN_RECON_ID", inReconID);
            return inReconID;
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("getINReconTxnID", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID =" + inReconID);
        }// end of finally
    }// end of getINReconTxnID

    public static void main(String[] args) {
        System.out.println(Long.parseLong("-4679"));
        System.out.println(InterfaceUtil.getInterfaceDateFromDateString("20070930", "ddMMyyyy"));
    }
}// end of class CbossINHandler

