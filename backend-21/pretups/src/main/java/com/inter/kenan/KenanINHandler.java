package com.inter.kenan;

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
import com.btsl.pretups.logging.TransactionLog;
import com.inter.scheduler.NodeManager;
import com.inter.scheduler.NodeScheduler;
import com.inter.scheduler.NodeVO;

/**
 * @(#)KenanINHandler
 *                    Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                    All Rights Reserved
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Ashish Kumar Nov 22, 2006 Initial Creation
 *                    ----------------------------------------------------------
 *                    --------------------------------------
 *                    Handler class for the Kenan interface
 */
public class KenanINHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(KenanINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the respose of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to storeOc the reference of
                                       // transaction id
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    private static KenanRequestFormatter _kenanRequestFormatter = null; // Store
                                                                        // the
                                                                        // object
                                                                        // of
                                                                        // the
                                                                        // RequestFormatter
                                                                        // class.

    static {
        if (_log.isDebugEnabled())
            _log.debug("KenanINHandler[static]", "Entered");
        try {
            _kenanRequestFormatter = new KenanRequestFormatter();
        } catch (Exception e) {
            _log.error("KenanINHandler[static]", "While instantiation of KenanRequestFormatter get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[static]", "", "", "", "While instantiation of KenanRequestFormatter get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("KenanINHandler[static]", "Exited");
        }
    }

    /**
     * This method, only set the TRANSACTION_STATUS as the SUCCESS into
     * requestMap when it is called by controller.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "p_requestMap::" + p_requestMap);
        _requestMap = p_requestMap;
        try {
            // Set only the SUCCESS CODE in the requestMap
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[validate]", _referenceID + " interfaceID::" + _interfaceID, "Subscriber Account id" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
    }// end of validate

    public void creditAdjust(HashMap p_requestMap) throws Exception {
        // TBD-if any value has to set.
    }// end of creditAdjust

    public void debitAdjust(HashMap p_requestMap) throws Exception {
        // Since there is no P2P transaction hence no need to implement the
        // debit functionality
    }// end of debitAdjust.

    /**
     * Implements the logic that credit the subscriber account on IN,it includes
     * the following activities.
     * 1.Check the value of required interface parameters for example
     * multiplication factor, userid, password etc.
     * 2.Generate the request string in XML format and also check the required
     * value that is to be set into request parameter.
     * a. Check whether the bonus would be sent as separatly or it would be
     * included into requested amount.
     * b. If BONUS_FLAG defined in INFile is 'N', amount includes the bonus and
     * bonus value in the request would be 0.
     * c. Else the bonus is extracted from the interface amount and set
     * remaining amount as transfer_amount and bonus in the transfer_bonus.
     * d. Check whether transfer amount or transfer bonus should be rounded or
     * not.
     * e. If rounding is required round the amount and bonus.
     * d. If any error occurs while chekig the required parameters,handle the
     * event and throw the Exception with InterfaceErrorCode as
     * INTERFACE_HANDLER_EXCEPTION.
     * 3.Send the request to IN and get the response.
     * 4.Fetch the response parameters from the response map.
     * 5.Put the required response parameters to the request map.
     * a. If the subscriber phone is present into the response set the
     * NOTIFICATION_MSISDN as the subscriber phone.
     * b. If the subscriber phone is not present then don't set any thing to
     * NOTIFICATION_MSISDN.
     * c. New balance of the subscriber.
     * d. New expiry date of the subscriber.
     * e. Set the POST_BALANCE_ENQUIRY as Y.
     * f. After successful transaction set TRANSACTION_STATUS as
     * InterfaceErrorCode SUCCESS.
     * 6.Parametes are converted into required format before storing into
     * request map these are as bellow.
     * a. Date format of NEW_EXPIRY_DATE should be as defined for the interface.
     * b. New balance after recharge should be multiplied by multiplication
     * before storing in INTERFACE_POST_BALANCE.
     * 
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap ::" + p_requestMap);
        _requestMap = p_requestMap;
        String multFactor = null;
        long interfaceAmount = 0;
        long bonusAmount = 0;
        int multFactorInt = 0;
        long postBalanceLong = 0;
        long postBalanceSystemAmount = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "MULTIPLICATION_FACTOR::" + multFactor);
            try {
                multFactorInt = Integer.parseInt(multFactor.trim());
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[credit]", _referenceID, "Subscriber id =" + _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Check the Multiplication factor defined in the INFile, it must be numeric value. Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {

                interfaceAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
                bonusAmount = Long.parseLong((String) _requestMap.get("BONUS_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[credit]", _referenceID, "Subscriber id =" + _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set the interface specific parameters USER_ID,PASSWORD into
            // _requestMap
            // setInterfaceParameters();
            // As for performance point of view, it is better to use inline
            // method body instead of calling setParameters method.
            String userID = FileCache.getValue(_interfaceID, "USER_ID");
            if (_log.isDebugEnabled())
                _log.debug("credit", "userID::" + userID);
            if (InterfaceUtil.isNullString(userID)) {
                _log.error("credit", "User id is not defined in the INFile");
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), " USER_ID is not defined for INTERFACE_ID=" + _interfaceID);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("user_id", userID.trim());
            String password = FileCache.getValue(_interfaceID, "PASSWORD");
            if (_log.isDebugEnabled())
                _log.debug("credit", "password::" + password);
            if (InterfaceUtil.isNullString(password)) {
                _log.error("credit", "Password is not defined in the INFile");
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), " PASSWORD is not defined for INTERFACE_ID=" + _interfaceID);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("password", password.trim());

            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(interfaceAmount, multFactorInt);
            String bonusStr = InterfaceUtil.getDisplayAmount(bonusAmount, multFactorInt);
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("credit", "From file cache roundFlag::" + roundFlag);
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                // If the ROUND_FLAG is not defined its default value is taken
                // as 'Y'
                roundFlag = "Y";
                _log.error("credit", "ROUND_FLAG is not defined into INFile hence taken default value as 'Y'.");
                // Confirm for the EVENT.
                // EventHandler.handle(EventIDI.SYSTEM_INFO,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL,
                // "KenanINHandler[credit]",_referenceID,"Subscriber id ="+_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
            }
            double amountDouble = Double.parseDouble(amountStr);
            double bonusDouble = Double.parseDouble(bonusStr);
            // If rounding of amount is allowed, round the amount value and put
            // this value in request map.

            // Check whether the transfer_amount should include the bonus or
            // exclude the bonus, this is decided by the BONUS_FLAG.
            String bonusFlag = FileCache.getValue(_interfaceID, "BONUS_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("credit", "bonusFlag:" + bonusFlag);
            // If the bonusFlag=Y, then bonus should be extracted from the
            // interface amount.
            if (InterfaceUtil.isNullString(bonusFlag)) {
                bonusFlag = "N";
                _log.error("credit", "BONUS_FLAG is not defined into INFile hence taken default value as 'N'.");
                // Confirm for the Event
                // EventHandler.handle(EventIDI.SYSTEM_INFO,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL,
                // "KenanINHandler[credit]",_referenceID,"Subscriber id ="+_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "BONUS_FLAG is not defined in IN File hence system taken the default value=N.");
            }
            // If values has to be rounded, check the ROUND_FLAG defined into
            // INFile.
            // 1.If ROUND_FLAG=Y then round the amount
            // 2.Check whether the bonus has to been sent separate,if yes
            // (decided by the BONUS_FLAG)
            // a.Extract the bonus amount from the interface amount then ROUND
            // this.
            // b.Round the bonus amount.
            // 3.If the ROUND_FLAG is not defined or its Value is other than Y
            // a.Interface amount would contain the bonus value.
            // b.Assign the bonus as 0 (because the bonus is included in the
            // interface amount-CONFIRM???)

            if ("Y".equals(bonusFlag.trim()))
                amountDouble = amountDouble - bonusDouble;

            if ("Y".equals(roundFlag.trim())) {
                amountStr = String.valueOf(Math.round(amountDouble));
                bonusStr = String.valueOf(Math.round(bonusDouble));
            }
            // If bonus has to been send separatly, it should be extracted from
            // the interface amount.

            _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            _requestMap.put("INTERFACE_ROUND_BONUS", bonusStr);
            // Put the amount in request map with key as transfer_amuont
            _requestMap.put("transfer_amount", amountStr);
            _requestMap.put("transfer_bonus", bonusStr);
            setInterfaceParameters(_interfaceID);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            // Generate the request xml from the KenanRequestFormatter class.
            String inStr = _kenanRequestFormatter.generateRequest(KenanI.ACTION_CREDIT, _requestMap);
            // Send the request xml to IN.
            sendRequestToIN(inStr, KenanI.ACTION_CREDIT);

            // After sending the request to IN set the new balance,expiry and
            // new grace to the requestMap.
            // 1.set the interface transaction status as successful
            // 2.Set the post balance to request map.
            // 3.Set the new Expiry to request map
            // 4.Set the new grace to the request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // This flag described whether the subscriber phone in the response
            // would be present for that interface.

            /*
             * String subsPhFlag= FileCache.getValue(_interfaceID,"SUBS_PHONE");
             * 
             * //If subsPhFlag is Y then
             * // 1.check whether the response contains the subscriber phone
             * number in the response.
             * // 2.If subscriber phone is present in the response then set this
             * number into requestMap as NOTIFICATION_MSISDN.
             * // 3.If subscriber phone is not present in the response then set
             * the sender's msisdn as NOTIFICATION_MSIDN.
             * //If subsPhFlag is not defined or its value is other than Y then
             * // 1.set the sender's msisdn as the NOTIFICATION_MSISDN into
             * request map.
             * 
             * if(InterfaceUtil.isNullString(subsPhFlag)||!"Y".equals(subsPhFlag)
             * )
             * _requestMap.put("NOTIFICATION_MSISDN",(String)_requestMap.get(
             * "SENDER_MSISDN"));
             * else if("Y".equals(subsPhFlag.trim()))
             * {
             * if(!InterfaceUtil.isNullString((String)_responseMap.get("sub-ph"))
             * )
             * _requestMap.put("NOTIFICATION_MSISDN",(String)_responseMap.get(
             * "sub-ph"));
             * else
             * _requestMap.put("NOTIFICATION_MSISDN",(String)_requestMap.get(
             * "SENDER_MSISDN")); //Confirm for this case??????
             * }
             */

            // From response if the subscriber phone is obtained then only set
            // NOTIFICATION_MSISDN as subscriber phone
            // Else do not set any value to the NOTIFICATION_MSISDN
            if (!InterfaceUtil.isNullString((String) _responseMap.get("sub-ph")))
                _requestMap.put("NOTIFICATION_MSISDN", (String) _responseMap.get("sub-ph"));
            // Get the New balance of subscriber from response.
            postBalanceLong = Long.parseLong((String) _responseMap.get("new-balance"));
            // Get the system amount for the subscriber's new balance.
            postBalanceSystemAmount = InterfaceUtil.getSystemAmount(postBalanceLong, multFactorInt);
            // Set the subscribers new balance as INTERFACE_POST_BALANCE into
            // request map.
            _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceSystemAmount));
            // Get new expiry date from the response and change its format to
            // the interface then set into request map.
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end-date"), "MM/dd/yyyy"));
            // After getting the balance and expiry from response set
            // POST_BALANCE_ENQ_SUCCESS as 'Y' into request map.
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[credit]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while credit Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exiting _requestMap::" + _requestMap);
        }// end of finally
    }// end of credit

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
     * This method is used to set the interface parameters into request map.
     * 
     * @param String
     *            p_interfaceID
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void setInterfaceParameters(String p_interfaceID) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_interfaceID=" + p_interfaceID);

        try {
            String cancelTxnAllowed = FileCache.getValue(p_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(p_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(p_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
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
     * This method is used to send the request to IN and stored the response
     * after parsing.
     * This method also take care about to handle the errornious satuation to
     * send the alarm and set the error code.
     * 1.Invoke the getNodeVO method of NodeScheduler class and pass the
     * Transaction Id.
     * 2.If the VO is Null then mark the request as fail and throw exception(New
     * Error code that defines No connection for any Node is available).
     * 3.If the VO is not NULL then pass the Node detail to KenanUrlConnection
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
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
        String responseStr = "";
        NodeVO nodeVO = null;
        NodeScheduler nodeScheduler = null;
        KenanUrlConnection kenanURLConnection = null;
        long startTime = 0;
        long endTime = 0;
        int retryNumber = 0;
        long warnTime = 0;
        int readTimeOut = 0;
        String inReconID = null;
        try {
            _responseMap = new HashMap();
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            try {
                // Logic to get the node and Connection-Start
                int loop = 1;// Defines the number of retry attempt to get the
                             // connection.
                // Get the instance of NodeScheduler based on interfaceId.
                nodeScheduler = NodeManager.getScheduler(_interfaceID);
                // check if nodeScheduler is null throw exception.Confirm for
                // Error code(INTERFACE_CONNECTION_NULL)if required-It should be
                // new code like ERROR_NODE_FOUND!
                if (nodeScheduler == null)
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT);// Confirm
                                                                                                               // for
                                                                                                               // New
                                                                                                               // Error
                                                                                                               // code
                                                                                                               // or
                                                                                                               // Change
                                                                                                               // as
                                                                                                               // ERROR_WHILE_GETTING_SCHEDULER_OBJECT
                                                                                                               // specific
                                                                                                               // to
                                                                                                               // scheduler
                                                                                                               // not
                                                                                                               // for
                                                                                                               // the
                                                                                                               // interface.
                // Get the retry number from the object that is used to retry
                // the getNode in case connection is failed.
                retryNumber = nodeScheduler.getRetryNum();
                long startTimeNode = System.currentTimeMillis();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                // If the connection for corresponding node is failed, retry to
                // get the node with configured number of times.
                // If connection eshtablished then break the loop.
                for (loop = 1; loop <= retryNumber; loop++) {
                    try {
                        nodeVO = nodeScheduler.getNodeVO(inReconID);
                        // Check if Node is foud or not.Confirm for Error
                        // code(INTERFACE_CONNECTION_NULL)if required-It should
                        // be new code like ERROR_NODE_FOUND!
                        if (nodeVO == null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_DETAIL_NOT_FOUND);// Confirm
                                                                                                              // for
                                                                                                              // New
                                                                                                              // Error
                                                                                                              // code
                                                                                                              // or
                                                                                                              // Change
                                                                                                              // as
                                                                                                              // ERROR_NODE_DETAIL_NOT_FOUND
                                                                                                              // specific
                                                                                                              // to
                                                                                                              // scheduler
                                                                                                              // not
                                                                                                              // for
                                                                                                              // the
                                                                                                              // interface
                        warnTime = nodeVO.getWarnTime();
                        readTimeOut = nodeVO.getTopReadTimeOut(); // Confirm if
                                                                  // this would
                                                                  // be
                                                                  // interface
                                                                  // wise not
                                                                  // node wise
                                                                  // then
                                                                  // directly
                                                                  // get from
                                                                  // file cache
                                                                  // instead of
                                                                  // nodeVO.
                        // Confirm for the service name servlet for the url
                        // consturction whether URL will be specified in INFile
                        // or IP,PORT and ServletName.
                        kenanURLConnection = new KenanUrlConnection(nodeVO.getUrl(), nodeVO.getConnectionTimeOut(), readTimeOut, nodeVO.getKeepAlive());
                        // break the loop on getting the successfull connection
                        // for the node;
                        if (_log.isDebugEnabled())
                            _log.debug("sendRequestToIN", "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + nodeVO.getNodeNumber() + "] created after the attempt number(loop)::" + loop);
                        break;
                    } catch (BTSLBaseException be) {
                        _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
                        throw be;
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
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for KenanIN with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "]");
                        nodeVO.decrementConNumber(_inTXNID);
                        _log.info("sendRequestToIN", "Setting the Node [" + nodeVO.getNodeNumber() + "] as blocked for duration ::" + nodeVO.getExpiryDuration() + " miliseconds");
                        nodeVO.setBlocked(true);
                        nodeVO.setBlokedAt(System.currentTimeMillis());
                        if (loop == retryNumber) {
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MAXIMUM SHIFTING OF NODE IS REACHED");
                            throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                        }
                        continue;
                    }// end of catch-Exception
                }// end of for loop
                long totalTimeNode = System.currentTimeMillis() - startTimeNode;
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Total time to find the scheduled Node totalTimeNode: " + totalTimeNode);
            } catch (BTSLBaseException be) {
                throw be;
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "]" + " Exception ::" + e.getMessage());
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }// end of catch-Exception
            try {
                // In creditAdjust (sender credit back )don't check interface
                // status, simply send the request to IN.
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                    _isSameRequest = true;
                    checkInterfaceB4SendingRequest();
                }
                PrintWriter out = kenanURLConnection.getPrintWriter();
                out.flush();
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e::" + e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to Kenan IN INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "] Exception::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
            }
            try {
                StringBuffer buffer = new StringBuffer();
                String response = "";
                try {
                    // Get the response from the IN
                    kenanURLConnection.setBufferedReader();
                    BufferedReader in = kenanURLConnection.getBufferedReader();
                    // Reading the response from buffered reader.
                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    if (warnTime <= (endTime - startTime)) {
                        _log.info("sendRequestToIN", "WARN time reaches, startTime::" + startTime + " endTime::" + endTime + " From file cache warnTime::" + warnTime + " time taken (endTime-startTime)::" + (endTime - startTime));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "KenanINHandler[sendRequestToIN]", _inTXNID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "KenanIN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "]");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the KenanIN for INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "]" + "Exception=" + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(KenanI._ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }// end of catch-Exception
                finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                }// end of finally
                responseStr = buffer.toString();
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr::" + responseStr);
                // TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
                String httpStatus = kenanURLConnection.getResponseCode();
                _requestMap.put("PROTOCOL_STATUS", httpStatus);
                if (!KenanI.HTTP_STATUS_200.equals(httpStatus)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "HTTP STATUS from the IN is ::" + httpStatus);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                // Check if there is no response, handle the event showing Blank
                // response from Kenan and stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from KenanIN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(KenanI._ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                // Parse the response string and get the response Map.
                _responseMap = _kenanRequestFormatter.parseResponse(p_action, responseStr);
                // The various checks would be done based on the response code.
                // 1.If the responseCode is other than 17300
                // a.if response code is 17200 then throw BTSLBaseException with
                // error code as Subscriber does not exist.
                // b.else throw BTSLBaseException with error code
                // ERROR_RESPONSE.
                String responseCode = (String) _responseMap.get("error-code");

                _requestMap.put("INTERFACE_STATUS", responseCode);

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
                 * if(KenanI._ACTION_TXN_CANCEL == p_stage)
                 * {
                 * _requestMap.put("CANCEL_RESP_STATUS",result);
                 * cancelTxnStatus =
                 * InterfaceUtil.getErrorCodeFromMapping(_requestMap
                 * ,result,"SYSTEM_STATUS_MAPPING");
                 * throw new BTSLBaseException(cancelTxnStatus);
                 * }
                 */

                if (!KenanI.RESULT_OK.equals(responseCode)) {
                    if (KenanI.SUBSCRIBER_NOT_FOUND.equals(responseCode)) {
                        _log.error("sendRequestToIN", "Subscriber not found with MSISDN::" + _msisdn);
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND); // Confirm
                                                                                                      // for
                                                                                                      // a
                                                                                                      // new
                                                                                                      // Interface
                                                                                                      // error
                                                                                                      // code-ACCOUNT
                                                                                                      // DOES
                                                                                                      // NOT
                                                                                                      // EXIST
                                                                                                      // for
                                                                                                      // this
                                                                                                      // case???
                    }// end of checking the subscriber existance.
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "ERROR-CODE of the resposne is::" + responseCode);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
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
                if (kenanURLConnection != null)
                    kenanURLConnection.close();
                if (nodeVO != null) {
                    _log.info("sendRequestToIN", "Connection of Node [" + nodeVO.getNodeNumber() + "] for INTERFACE_ID=" + _interfaceID + " is closed");
                    // Decrement the connection number for the current Node.
                    nodeVO.decrementConNumber(inReconID);
                    _log.info("sendRequestToIN", "After closing the connection for Node [" + nodeVO.getNodeNumber() + "] USED connections are ::[" + nodeVO.getConNumber() + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "While closing Kenan IN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "KenanINHandler[sendRequestToIN]", _referenceID, "Subscriber id =" + _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "]");
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting  _interfaceID::" + _interfaceID + " Stage::" + p_action + " responseStr::" + responseStr);
        }// end of finally
    }// end of sendRequestToIN

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
             * _formatter.generateRequest(KenanI._ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,KenanI._ACTION_TXN_CANCEL);
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
     * This method is used to set the interface parameters into requestMap,
     * these parameters are as bellow
     * 1.User id.
     * 2.Password.
     * 
     * @throws Exception
     *             ,BTSLBaseException
     */
    /*
     * private void setInterfaceParameters() throws Exception,BTSLBaseException
     * {
     * if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered");
     * String userID=null;
     * String password=null;
     * //String transactionCurrency=null;
     * try
     * {
     * userID = FileCache.getValue(_interfaceID, "USER_ID");
     * if(InterfaceUtil.isNullString(userID))
     * {
     * EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED,
     * EventLevelI.INFO,"KenanINHandler[sendRequestToIN]"
     * ,_referenceID,"Subscriber id ="+_msisdn,(String)
     * _requestMap.get("NETWORK_CODE"
     * )," USER_ID is not defined for INTERFACE_ID="+_interfaceID);
     * throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
     * }
     * _requestMap.put("user_id",userID.trim());
     * password = FileCache.getValue(_interfaceID, "PASSWORD");
     * if(InterfaceUtil.isNullString(password))
     * {
     * EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED,
     * EventLevelI.INFO,"KenanINHandler[sendRequestToIN]"
     * ,_referenceID,"Subscriber id ="+_msisdn,(String)
     * _requestMap.get("NETWORK_CODE"
     * )," PASSWORD is not defined for INTERFACE_ID="+_interfaceID);
     * throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
     * }
     * _requestMap.put("password",password.trim());
     * }//end of try block
     * catch(BTSLBaseException be)
     * {
     * _log.error("setInterfaceParameters","BTSLBaseException be::"+be.getMessage
     * ());
     * throw be;
     * }//end of catch-BTSLBaseException
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("setInterfaceParameters","Exception e="+e);
     * EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.
     * INTERFACES, EventStatusI.RAISED,
     * EventLevelI.INFO,"KenanINHandler[sendRequestToIN]"
     * ,_referenceID,"Subscriber id ="+_msisdn,(String)
     * _requestMap.get("NETWORK_CODE"),"INTERFACE_ID="+_interfaceID+
     * "Check the USER_ID & Password into INFile::Exception e="+e.getMessage());
     * throw new
     * BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
     * }//end of catch-Exception
     * finally
     * {
     * if (_log.isDebugEnabled())_log.debug("setInterfaceParameters",
     * "Exited _requestMap::"+ _requestMap);
     * }//end of finally
     * }//end of setInterfaceParameters
     */
}
