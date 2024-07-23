package com.inter.alcatel432;

/**
 * @(#)AlcatelIN432Handler.java
 *                              Copyright(c) 2006, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ashish Kumar May 03,2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              Handler class for the Alcatel interface
 */
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
import com.btsl.util.Constants;

public class AlcatelIN432Handler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;// Contains the respose of the request as
                                       // key and value pair.
    private HashMap _responseMap = null;// Contains the request parameter as key
                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;
    Alcatel432RequestFormatter _formatter = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    public AlcatelIN432Handler() {
        // Creates an instance of RequestFormatter class
        _formatter = new Alcatel432RequestFormatter();
    }

    /**
     * This method used to get the account information of subscriber that
     * includes the following
     * 1.Interface specific parameters are set and added to the requested map.
     * 2.Format the request into predefined xml,for this method internally calls
     * generateRequest method
     * of Alcatel432RequestFormatter.
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
        _requestMap = p_map;// Assign the request map
        String multFactor = null;
        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("Stage", "VAL");
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactor == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set the interface specific parameters cp_id,application and
            // transaction_currency into _requestMap
            setInterfaceParameters(_interfaceID);

            // key value of HashMap is formatted into XML string for the
            // validate request.
            String inStr = _formatter.generateRequest(Alcatel432I.ACTION_ACCOUNT_INFO, _requestMap);

            // sending the AccountInfo request to IN
            sendRequestToIN(inStr, Alcatel432I.ACTION_ACCOUNT_INFO);

            // Set the balance after multiplied it with multiplication factor.
            String amountStr = (String) _responseMap.get("credit_balance");
            double currAmount = Double.parseDouble(amountStr);
            long amount = InterfaceUtil.getSystemAmount(currAmount, Integer.parseInt(multFactor));
            _requestMap.put("INTERFACE_PREV_BALANCE", String.valueOf(amount));
            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("profile"));
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_val_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("acc_status"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_inact_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            // get the language returned from the response and store into
            // requestMap
            // _requestMap.put("PROF_LANG",(String)_responseMap.get("prof_lang"));

            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping();

            // Load the service_type(pure prepaid or postpaid and reqular
            // prepaid or post paid) supproted to the IN from INFile and get the
            // requested service type.
            // If the requested service_type does not supported by IN stop the
            // processing showing error that service type is not allowed.
            String serviceType = (String) _responseMap.get("service_type");
            String allowedServiceType = (String) FileCache.getValue(_interfaceID, "ALLOWED_SERVICE_TYPE");
            if (allowedServiceType != null && serviceType != null && !InterfaceUtil.isStringIn(serviceType, allowedServiceType)) {
                if (_log.isDebugEnabled())
                    _log.debug("validate", "Subscriber Status type is not allowed " + InterfaceUtil.getPrintMap(_requestMap));
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
            }
            // get the value of lock flag and check if the msisdn is barred from
            // IN stop the processing,showing the error that Subscriber is
            // barred on IN.
            // Lock flag value 0 represent that subscriber is barred from IN
            String barind = (String) _responseMap.get("LOCK_FLAG");
            if ("1".equals(barind)) {
                if (_log.isDebugEnabled())
                    _log.debug("validate", "Subscriber is Barred on IN LockStatus is 1" + InterfaceUtil.getPrintMap(_requestMap));
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);// The
                                                                                          // MSISDN
                                                                                          // of
                                                                                          // the
                                                                                          // request
                                                                                          // is
                                                                                          // not
                                                                                          // valid
                                                                                          // on
                                                                                          // Prepaid
                                                                                          // system.
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        }// End of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// End of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap=" + _requestMap);
        }// End of finally
    }// end of validate

    /**
     * This method is responsible for the debit of subscriber account that
     * includes the following
     * 1.Interface specific parameters are set and added to the request map
     * 2.Format the request into predifiend xml for debit request,for this
     * method internaly
     * calls the generateRequest method of Alcatel432RequestFormatter.
     * 3.For sending request to IN this method internally calls private method
     * sendRequestToIN
     * 4.Update the total amount after debitting the requested amount as balance
     * amount.
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_map =" + p_map);
        _requestMap = p_map;// Assignment of requested map
        String multFactor = null;
        long amount = 0;
        long oldAmount = 0;
        long newAmount = 0;
        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            setInterfaceParameters(_interfaceID);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                amount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactor == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (roundFlag == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            String amountStr = InterfaceUtil.getDisplayAmount(amount, Integer.parseInt(multFactor));
            double amountDouble = Double.parseDouble(amountStr);
            if ("Y".equalsIgnoreCase(roundFlag)) {
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            _requestMap.put("transfer_amount", amountStr);
            // key value of HashMap is formatted into XML string for the
            // debitAdjust request.
            String inStr = _formatter.generateRequest(Alcatel432I.ACTION_IMMEDIATE_DEBIT, _requestMap);

            // sending the debit request to the IN
            sendRequestToIN(inStr, Alcatel432I.ACTION_IMMEDIATE_DEBIT);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // updating the total amount after debiting the requested amount.
            try {
                oldAmount = Long.parseLong((String) _requestMap.get("INTERFACE_PREV_BALANCE"));
                newAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Previous balance from response is not numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            long totalAmount = 0;
            totalAmount = oldAmount - newAmount;
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "balance amount = " + totalAmount);
            _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(totalAmount));
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }// end of debitAdjust

    /**
     * This method is responsible for the credit of subscriber account and
     * includes following activities
     * 1.Interface specific parameters are set and added to the request map.
     * 2.Format the request into predifiend xml for credit request,for this
     * method internaly
     * calls the generateRequest method of Alcatel432RequestFormatter.
     * 3.For sending request to IN this method internally calls private method
     * sendRequestToIN
     * 4.Process the response.
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_map =" + p_map);
        _requestMap = p_map;// Assignment of requested map.\
        String multFactor = null;
        String seperateSubAccountAllowed = null;
        long interfaceAmount = 0;
        long validityDays = 0;
        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactor == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            seperateSubAccountAllowed = FileCache.getValue(_interfaceID, "SEPERATE_SUB_ACCOUNT");
            if (seperateSubAccountAllowed == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Separate sub account flag is not defined in IN file");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            setInterfaceParameters(_interfaceID);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");

            // Before formatting the credit request decide whether request will
            // contain Combined Account(Main+Bonus)
            // or separate account,this is done by checking the value of
            // FileCache flag(SEPERATE_SUB_ACCOUNT)
            _requestMap.put("SEPERATE_SUB_ACCOUNT", seperateSubAccountAllowed);
            try {
                interfaceAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(interfaceAmount, Integer.parseInt(multFactor));
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double amountDouble = Double.parseDouble(amountStr);
            if ("Y".equalsIgnoreCase(roundFlag.trim())) {
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            _requestMap.put("transfer_amount", amountStr);
            try {
                validityDays = Long.parseLong((String) _requestMap.get("VALIDITY_DAYS"));
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Validity days is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // If the flag set as 'Y-separate account'.Deduct the bonus amount
            // and bonus validity from
            // the INTERFACE_AMOUNT and VALIDITY_DAYS respectivly and overwrite
            // the deducted amount and validity into request map.
            if ("Y".equalsIgnoreCase(seperateSubAccountAllowed)) {
                long bonusAmount = Integer.parseInt((String) _requestMap.get("BONUS_AMOUNT"));
                long bonusValidityDays = Integer.parseInt((String) _requestMap.get("BONUS_VALIDITY_DAYS"));

                // Extracting the bonus amount from the Interface amount since
                // interface amount includes the bonus amount
                if (interfaceAmount >= bonusAmount)
                    interfaceAmount = interfaceAmount - bonusAmount;
                String interfaceAmtStr = InterfaceUtil.getDisplayAmount(interfaceAmount, Integer.parseInt(multFactor));
                String amountBonusStr = InterfaceUtil.getDisplayAmount(bonusAmount, Integer.parseInt(multFactor));
                if ("Y".equalsIgnoreCase(roundFlag)) {
                    double interfaceAmtDouble = Double.parseDouble(interfaceAmtStr);
                    interfaceAmtStr = String.valueOf(Math.round(interfaceAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", interfaceAmtStr);
                    double bonusAmtDouble = Double.parseDouble(amountBonusStr);
                    amountBonusStr = String.valueOf(Math.round(bonusAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_BONUS", amountBonusStr);
                }
                // After Extracting the interface amount set it to transfer
                // amount with rounding if required.
                _requestMap.put("transfer_amount", interfaceAmtStr);

                // Set transfer bonus amount after dividing by multiplication
                // factor and rounding if required.
                _requestMap.put("transfer_bonus_amount", amountBonusStr);

                if (validityDays >= bonusValidityDays)
                    validityDays = validityDays - bonusValidityDays;
                _requestMap.put("VALIDITY_DAYS", String.valueOf(validityDays));
            }
            // key value of HashMap is formatted into XML string for the credit
            // request and stored into string.
            String inStr = _formatter.generateRequest(Alcatel432I.ACTION_RECHARGE_CREDIT, _requestMap);

            // sending the credit request to IN
            sendRequestToIN(inStr, Alcatel432I.ACTION_RECHARGE_CREDIT);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            // Set the post balance after multiplied by multiplication factor.
            try {
                long prevBalance = Long.parseLong((String) _requestMap.get("INTERFACE_PREV_BALANCE"));
                long postBalance = interfaceAmount + prevBalance;
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalance));
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }// end of credit

    /**
     * This method is responsible for credit back of senders for this it
     * interanlly calls credit method.
     * For this each time when credit is called it checks the flag 'ADJUST' if
     * it is 'Y' it set the validity
     * and grace as '0' while formation of RechargeCredit request.
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_map=" + p_map);
        _requestMap = p_map;// Assignment of requested map.\
        String multFactor = null;
        String seperateSubAccountAllowed = null;
        long interfaceAmount = 0;
        long validityDays = 0;
        String interAmtStr = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();

            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactor == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            seperateSubAccountAllowed = FileCache.getValue(_interfaceID, "SEPERATE_SUB_ACCOUNT");
            if (seperateSubAccountAllowed == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Separate sub account flag is not defined in IN file");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            setInterfaceParameters(_interfaceID);
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");

            // Before formatting the creditAjust request decide whether request
            // will contain Combined Account(Main+Bonus)
            // or separate account,this is done by checking the value of
            // FileCache flag(SEPERATE_SUB_ACCOUNT)
            _requestMap.put("SEPERATE_SUB_ACCOUNT", seperateSubAccountAllowed);

            try {
                interAmtStr = (String) _requestMap.get("INTERFACE_AMOUNT");
                if (!InterfaceUtil.isNullString(interAmtStr))
                    interfaceAmount = Long.parseLong(interAmtStr.trim());
                else
                    interfaceAmount = 0;
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(interfaceAmount, Integer.parseInt(multFactor));
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double amountDouble = Double.parseDouble(amountStr);
            if ("Y".equals(roundFlag.trim())) {
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            _requestMap.put("transfer_amount", amountStr);

            /*
             * try
             * {
             * String validityDaysStr =
             * (String)_requestMap.get("VALIDITY_DAYS");
             * if(!InterfaceUtil.isNullString(validityDaysStr))
             * validityDays = Long.parseLong(validityDaysStr);
             * else
             * validityDays=0;
             * }
             * catch(Exception e)
             * {
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,
             * EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             * "Alcatel432INHandler[creditAdjust]",_referenceID,_msisdn
             * +" INTERFACE ID = "+_interfaceID, (String)
             * _requestMap.get("NETWORK_CODE"), "Validity days is not Numeric");
             * throw new
             * BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI
             * .INTERFACE_HANDLER_EXCEPTION);
             * }
             * 
             * //If the flag set as 'Y-separate account'.Deduct the bonus amount
             * and bonus validity from
             * //the INTERFACE_AMOUNT and VALIDITY_DAYS respectivly and
             * overwrite the deducted amount and validity into request map.
             * if("Y".equalsIgnoreCase(seperateSubAccountAllowed))
             * {
             * long
             * bonusAmount=Integer.parseInt((String)_requestMap.get("BONUS_AMOUNT"
             * ));
             * long bonusValidityDays =
             * Integer.parseInt((String)_requestMap.get("BONUS_VALIDITY_DAYS"));
             * 
             * //Extracting the bonus amount from the Interface amount since
             * interface amount includes the bonus amount
             * if(interfaceAmount >= bonusAmount)
             * interfaceAmount=interfaceAmount-bonusAmount;
             * String interfaceAmtStr =
             * InterfaceUtil.getDisplayAmount(interfaceAmount
             * ,Integer.parseInt(multFactor));
             * String
             * amountBonusStr=InterfaceUtil.getDisplayAmount(bonusAmount,Integer
             * .parseInt(multFactor));
             * if("Y".equalsIgnoreCase(roundFlag))
             * {
             * double interfaceAmtDouble = Double.parseDouble(interfaceAmtStr);
             * interfaceAmtStr = String.valueOf(Math.round(interfaceAmtDouble));
             * _requestMap.put("INTERFACE_ROUND_AMOUNT",interfaceAmtStr);
             * double bonusAmtDouble = Double.parseDouble(amountBonusStr);
             * amountBonusStr= String.valueOf(Math.round(bonusAmtDouble));
             * _requestMap.put("INTERFACE_ROUND_BONUS",amountBonusStr);
             * }
             * //After Extracting the interface amount set it to transfer amount
             * with rounding if required.
             * _requestMap.put("transfer_amount",interfaceAmtStr);
             * 
             * //Set transfer bonus amount after dividing by multiplication
             * factor and rounding if required.
             * _requestMap.put("transfer_bonus_amount",amountBonusStr);
             * 
             * if(validityDays >= bonusValidityDays)
             * validityDays=validityDays-bonusValidityDays;
             * _requestMap.put("VALIDITY_DAYS",String.valueOf(validityDays));
             * }
             */

            // key value of HashMap is formatted into XML string for the credit
            // request and stored into string.
            String inStr = _formatter.generateRequest(Alcatel432I.ACTION_RECHARGE_CREDIT, _requestMap);

            // sending the creditAdjust request to IN
            sendRequestToIN(inStr, Alcatel432I.ACTION_RECHARGE_CREDIT);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            // Set the post balance after multiplied by multiplication factor.
            /*
             * try
             * {
             * long prevBalance =Long.parseLong((String)
             * _requestMap.get("INTERFACE_PREV_BALANCE"));
             * long postBalance = interfaceAmount+prevBalance;
             * _requestMap.put("INTERFACE_POST_BALANCE",String.valueOf(postBalance
             * ));
             * }
             * catch(Exception e)
             * {
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,
             * EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             * "Alcatel432INHandler[creditAdjust]",_referenceID,_msisdn
             * +" INTERFACE ID = "+_interfaceID, (String)
             * _requestMap.get("NETWORK_CODE"),
             * "credit balance from response is not numeric");
             * throw new
             * BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI
             * .INTERFACE_HANDLER_EXCEPTION);
             * }
             */
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("creditAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
        }// end of finally
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
     * This method is used to get interface specific values from FileCache(load
     * at starting)based on
     * interface id and set to the requested map.These parameters are
     * 1.cp_id
     * 2.application
     * 3.transaction_currency
     * 
     * @param String
     *            p_interfaceID
     * @throws Exception
     */
    private void setInterfaceParameters(String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_interfaceID =" + p_interfaceID);
        try {
            _requestMap.put("cp_id", FileCache.getValue(p_interfaceID, "CP_ID" + "_" + (String) _requestMap.get("MODULE")));
            _requestMap.put("application", FileCache.getValue(p_interfaceID, "APPLICATION"));
            _requestMap.put("transaction_currency", FileCache.getValue(p_interfaceID, "TRANS_CURRENCY"));
            String cancelTxnAllowed = FileCache.getValue(p_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelIN432Handler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelIN432Handler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelIN432Handler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelIN432Handler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(p_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(p_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setInterfaceParameters", "Exception e=" + e + " Check the CP_ID,APPLICATION,TRANS_CURRENCY in IN file");
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }// end of setInterfaceParameters

    /**
     * This method is used to send the request to IN and stored the response
     * after parsing.
     * This method also take care about to handle the errornious satuation to
     * send the alarm and set the error code.
     * Handling event at situation like..
     * 1.During the creation of HttpUrlConnection
     * 2.While writting the request
     * 3.While reading the response
     * 4.If response is NULL
     * 5.****WARN TIME?
     * 6.If the value of result element is equal to any Error value defined in
     * Alcatel432I interface.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr =" + p_inRequestStr + " p_action =" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action + " _requestMap = " + _requestMap);
        String responseStr = "";
        Alcatel432UrlConnection alcate432lUrlConnection = null;
        long startTime = 0;
        long endTime = 0;
        int connectTimeOut = 0;
        int readTimeOut = 0;
        try {
            _responseMap = new HashMap();
            String inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            String contentProviderID = (String) _requestMap.get("cp_id");
            // Creation of new Url connection.Arguments are stored in the INFile
            // and loaded through FileCache.
            // if any error occurs during the creation of url connection handle
            // the event and stop the processing.
            try {
                // Fetch the url,connect timeout,read timeout and keep alive
                // values from the INFile
                String urlFromFileCache = FileCache.getValue(_interfaceID, "URL");
                String connectTimeOutStr = FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT");
                if (connectTimeOutStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Connect time out is not defined in INFile");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                try {
                    connectTimeOut = Integer.parseInt(connectTimeOutStr);
                } catch (Exception e) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, e.getMessage());
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                // Fetch the Read time out based on the STAGE
                String readTimeOutStr = null;
                if (p_action == Alcatel432I.ACTION_ACCOUNT_INFO) {
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Read time out is not defined in INFile");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
                if (keepAlive == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Keep Alive is not defined in INFile");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "urlFromFileCache = " + urlFromFileCache + "  connectTimeOut = " + connectTimeOut + " readTimeOut =" + readTimeOut + " keepAlive = " + keepAlive);
                alcate432lUrlConnection = new Alcatel432UrlConnection(urlFromFileCache, connectTimeOut, readTimeOut, keepAlive);
            } catch (Exception e) {
                _log.error("sendRequestToIN", "Exception e=" + e + "Check also whether URL,CONNECT_TIME_OUT,READ_TIMEOUT_VAL,READ_TIMEOUT_TOP,KEEP_ALIVE is properly defined in INFile");
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", "", "Check also whether URL,CONNECT_TIME_OUT,READ_TIMEOUT_VAL,READ_TIMEOUT_TOP,KEEP_ALIVE is properly defined in INFile", " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to create connection, getting Exception:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }// end of catch-Exception

            // In creditAdjust (sender credit back )don't check interface
            // status, simply send the request to IN.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }
            // Getting the PrintWriter object to write the request string.If any
            // error occurs during the writting the request
            // stop the processing, handle the event showing there is Exception
            // while sending the request.
            try {
                PrintWriter out = alcate432lUrlConnection.getPrintWriter();
                out.flush();
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e=" + e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while sending request to Alcatel IN");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }// end of catch-Exception
            try {
                // Create buffered reader and Read Response from the IN
                StringBuffer buffer = new StringBuffer();
                String response = "";
                try {
                    // Reading the response line by line using buffered reader
                    // and if response is not get properly stop the processing
                    // Handle the event showing that Exception while getting the
                    // response from the IN
                    alcate432lUrlConnection.setBufferedReader();
                    BufferedReader in = alcate432lUrlConnection.getBufferedReader();
                    // Reading the response from buffered reader.

                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                    if (warnTimeStr == null) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Warn time is not defined in IN File");
                        throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                    // If warn time in the IN file exist and the difference of
                    // start and end time of writing the request
                    // and reading the response is greater than the Warn time
                    // log the info and handle the event.
                    if (!InterfaceUtil.isNullString(warnTimeStr)) {
                        long warnTime = Long.parseLong(warnTimeStr);
                        if (endTime - startTime > warnTime) {
                            _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "Alcatel432INHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Alcatel IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                        }
                    }// end of if blok-checking the warn time should not null.
                }// end of try block
                catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", " response form interface is null exception is " + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Alcatel432INHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while getting response from Alcatel IN e: " + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(Alcatel432I.ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }// end of catch-Exception
                finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));

                    _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + readTimeOut);

                }
                // Storing the response in responseStr.
                responseStr = buffer.toString();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr:" + responseStr);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);

                // Getting the HTTP status code and set to the hashmap.
                String httpStatus = alcate432lUrlConnection.getResponseCode();
                _requestMap.put("PROTOCOL_STATUS", httpStatus);

                // Check if there is no response, handle the event showing Blank
                // response from Alcatel and stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    // send alert message(TO BE IMPLEMENTED)
                    // reconciliation case
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from Alcatel IN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(Alcatel432I.ACTION_TXN_CANCEL == p_stage)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                // This is the case when the response is not NULL.Here we have
                // to parse the request and get the value of result element.
                // Result value is used to decide whether the request is
                // successful or not.
                _responseMap = _formatter.parseResponse(p_action, responseStr);
                String result = (String) _responseMap.get("result");
                _requestMap.put("INTERFACE_STATUS", result);
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
                 * if(Alcatel432I.ACTION_TXN_CANCEL == p_stage)
                 * {
                 * _requestMap.put("CANCEL_RESP_STATUS",result);
                 * cancelTxnStatus =
                 * InterfaceUtil.getErrorCodeFromMapping(_requestMap
                 * ,result,"SYSTEM_STATUS_MAPPING");
                 * throw new BTSLBaseException(cancelTxnStatus);
                 * }
                 */

                String cpTransID = (String) _responseMap.get("cp_transaction_id");

                // If the result is not null and its value status shows the
                // parsing error or malformed request then handle the event and
                // stop the processing.
                if (!InterfaceUtil.isNullString(result) && (Alcatel432I.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || Alcatel432I.RESULT_ERROR_XML_PARSE.equals(result))) {
                    _log.info("sendRequestToIN", "cp transID:" + cpTransID + " CP ID=" + (String) _responseMap.get("cp_id") + " result=" + result);
                    // send alert message(TO BE IMPLEMENTED)
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Parameters values blank in response");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
                }
                // If content provider id(cp_id) or content provider transaction
                // id(cp_transaction_id) is null,handle the event and stop the
                // processing.
                else if (InterfaceUtil.isNullString(cpTransID) || InterfaceUtil.isNullString((String) _responseMap.get("cp_id"))) {
                    _log.info("sendRequestToIN", "transID:" + cpTransID + " CP ID=" + (String) _responseMap.get("cp_id") + " result=" + result);
                    // send alert message(TO BE IMPLEMENTED)
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Parameters values blank in response");
                    throw new BTSLBaseException(InterfaceErrorCodesI.NULL_INTERFACE_RESPONSE);
                }
                // Checking that whether the transaction id from the respose is
                // same as the requested transaction_id
                if (!cpTransID.equals(inReconID)) {
                    _log.info("sendRequestToIN", "inReconID:" + inReconID + " current TransId=" + cpTransID + " Mismatch");
                    // send alert message(TO BE IMPLEMENTED)
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Request and Response Transaction id from Alcatel IN does not match");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
                // check whether the requested cp_id is same as get from the
                // response.
                else if (!((String) _responseMap.get("cp_id")).equals((String) _requestMap.get("cp_id"))) {
                    _log.info("sendRequestToIN", "Response CP ID:" + _responseMap.get("cp_id") + " Request CP ID=" + _requestMap.get("cp_id") + " Mismatch");
                    // send alert message(TO BE IMPLEMENTED)
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Request and Response CP id from Alcatel IN does not match");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
                // This else block is used to get the value of result element of
                // the resposne.Based on the value handle the error.
                else {
                    String status = (String) _responseMap.get("result");
                    _log.info("sendRequestToIN", "result: " + result + " requestString: " + p_inRequestStr);
                    _requestMap.put("INTERFACE_STATUS", _responseMap.get("result"));
                    _requestMap.put("ACCOUNT_STATUS", _responseMap.get("acc_status"));

                    // if the HTTP response status is not OK(code=200) stop the
                    // processing and set the error code as Bad request.
                    if (!httpStatus.equals(Alcatel432I.HTTP_STATUS_200))
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);// invalid

                    // if the result value is not OK then Find out the result
                    // value and handle the event.
                    if (!status.equals(Alcatel432I.RESULT_OK)) {
                        // If the Account is not found then stop the processing
                        // showing MSISIDN is not found.
                        if (status.equals(Alcatel432I.RESULT_ERROR_ACC_NOT_FOUND))
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);// MSISDN
                                                                                                         // Not
                                                                                                         // Found
                        // This case handle all the situation listed in
                        // Alcatel432I interface and handle the event showing
                        // the result value recieved from the response.
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Result value from the response = " + status);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }// end of if block-when the result value is not '0'.
                }// end of else case in which result value is fetched.
            }// End of try-This block is used to CATCH Exception and
             // BTSLBaseException while getting the response from IN and during
             // the process of response.Controll is passed to outer most catch
             // block
            catch (BTSLBaseException be) {
                _log.error("sendRequestToIN", "BTSLBaseException be=" + be);
                throw be;
            } // end of catch-BTSLBaseException
            catch (Exception e) {
                // send alert message(TO BE IMPLEMENTED)
                // reconciliation case
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting response from IN :" + e.getMessage());
                // throw new
                // BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // commented code may be used in future to support on line
                // cancel request
                /*
                 * if(Alcatel432I.ACTION_TXN_CANCEL == p_stage)
                 * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                 * AMBIGOUS);
                 */
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }// end of catch-Exception
        }// Outer most try block- This block is used to catch the Exception and
         // BTSLBaseException while sending request and processing response and
         // controll is passed to calling method.
        catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be=" + be);
            throw be;
        } // end of catch-BTSLBaseException
        catch (Exception e) {
            // send alert message(TO BE IMPLEMENTED)
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            try {
                // Closing the HttpUrl connection
                if (alcate432lUrlConnection != null)
                    alcate432lUrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception while closing Alcatel Connection:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting  INTERFACE ID = " + _interfaceID + " Stage = " + p_action + " responseStr: " + responseStr);
        }// end of finally
    }// end of sendRequestToIN

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
            langFromIN = (String) _responseMap.get("prof_lang");
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "Alcatel432INHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel432INHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

    public static void main(String[] args) {
        AlcatelIN432Handler inhandler = null;
        try {
            // Constants.load("C:\\abhijit\\workspace\\pretups\\WebRoot\\WEB-INF\\classes\\configfiles\\Constants.props");
            // org.apache.log4j.PropertyConfigurator.configure("C:\\abhijit\\workspace\\pretups\\WebRoot\\WEB-INF\\classes\\configfiles\\LogConfig.props");
            Constants.load("C:\\Constants.props");
            org.apache.log4j.PropertyConfigurator.configure("C:\\LogConfig.props");
            FileCache.loadAtStartUp();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main Exception e=" + e.getMessage());
        }
        HashMap _requestMap = new HashMap();

        _requestMap.put("IN_TXN_ID", "C586666");
        _requestMap.put("INTERFACE_ID", "INTID00011");
        _requestMap.put("cp_id", "TOPCPID1");
        _requestMap.put("application", "1");
        _requestMap.put("INTERFACE_AMOUNT", "1000");
        _requestMap.put("VALIDITY_DAYS", "5");
        _requestMap.put("GRACE_DAYS", "3");
        _requestMap.put("transaction_currency", "1");
        _requestMap.put("op_transaction_id", "45");
        _requestMap.put("TRANSACTION_ID", "33000");

        // Adding the Type of account in the request map
        _requestMap.put("CARD_GROUP_SELECTOR", "1");
        _requestMap.put("MODULE", "C2S");

        // _requestMap.put("ADJUST","Y");
        // Debit request parameters
        _requestMap.put("INTERFACE_PREV_BALANCE", "1500");

        // Credit request parameters
        _requestMap.put("BONUS_AMOUNT", "2");
        _requestMap.put("BONUS_VALIDITY_DAYS", "1");
        try {
            // To check the Keep Alive
            // for(int i=0;i<=25;i++)
            // {
            _requestMap.put("MSISDN", "6999995");

            // String arr[] =
            // {"6999999","6999998","6999997","6999996","6999995","6999994"};
            inhandler = new AlcatelIN432Handler();
            // inhandler.validate(_requestMap);
            inhandler.credit(_requestMap);
            // inhandler.debitAdjust(_requestMap);

            /*
             * for(int i = 0 ; i < arr.length ; i++)
             * {
             * _requestMap.put("MSISDN",arr[i]);
             * inhandler.validate(_requestMap);
             * //inhandler.credit(_requestMap);
             * //inhandler.debitAdjust(_requestMap);
             * }
             */// }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main Exception e=" + e.getMessage());
        }

    }

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
             * _formatter.generateRequest(Alcatel432I.ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,Alcatel432I.ACTION_TXN_CANCEL);
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
