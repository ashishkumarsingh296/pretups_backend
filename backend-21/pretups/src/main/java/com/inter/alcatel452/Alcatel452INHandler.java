package com.inter.alcatel452;

/**
 * @(#)Alcatel452INHandler.java
 *                              Copyright(c) 2008, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Vinay Kumar Singh Aug 04, 2008 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              Handler class for the interface
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

public class Alcatel452INHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog("Alcatel452INHandler".getClass().getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private static Alcatel452RequestResponseFormatter _formatter = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    // Static block that would be responsible to create an instance of Request
    // Formatter class
    static {
        if (_log.isDebugEnabled())
            _log.debug("Alcatel452INHandler[static block]: ", "Entered");
        // Creating an instance of Alcatel452RequestResponseFormatter.
        _formatter = new Alcatel452RequestResponseFormatter();
        if (_log.isDebugEnabled())
            _log.debug("Alcatel452INHandler[static block]: ", "Exited");
    }

    /**
     * validate Method is used for getting the account information of
     * sender(CP2P) and receiver(CP2P & RP2P).
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate :: ", "Entered p_map:" + p_map);
        _requestMap = p_map;
        String sysInMultFactor = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            // get intreface id form request map
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // get transaction id from request map and set it in to the
            // reference id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);

            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            sysInMultFactor = (String) FileCache.getValue(_interfaceID, "SYSTEM_IN_MULT_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(sysInMultFactor)) {
                _log.error("validate: ", "SYSTEM_IN_MULT_FACTOR is not defined in INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_IN_MULT_FACTOR is not defined in IN File");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // set the interface parameters to the request map.
            setInterfaceParameters();
            // generate the xml string for validate request and get it in inStr
            String inStr = _formatter.generateRequest(Alcatel452I.ACTION_GET_ACCOUNT_INFO, _requestMap);

            // Sending the request to IN
            sendRequestToIN(inStr, Alcatel452I.ACTION_GET_ACCOUNT_INFO);

            // Check whether to validate the lock flag fetched from the response
            // or not.
            // If the LOCK_FLAG is defined in the INFile and its value is Y then
            // a. check the lock_info of response.
            // b. if value of lock_info is 1 throw BTSLBaseException with error
            // code as INTERFACE_MSISDN_BARRED
            String lockFlag = FileCache.getValue(_interfaceID, "LOCK_FLAG");
            if (!InterfaceUtil.isNullString(lockFlag) && "Y".equals(lockFlag.trim())) {
                String lockInfo = (String) _responseMap.get("lock_info");
                if ("1".equals(lockInfo.trim())) {
                    _log.error("validate: ", "Subscriber is Barred on IN, LockStatus obtained from the IN is:" + lockInfo);
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
            }

            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            try {
                String prevBalanceStr = (String) _responseMap.get("credit_balance");
                prevBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(prevBalanceStr, Double.parseDouble(sysInMultFactor.trim()));
                _requestMap.put("INTERFACE_PREV_BALANCE", prevBalanceStr);
            } catch (Exception e) {
                _log.error("validate: ", "Exception e:" + e.getMessage());
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // Setting the Servicve class into request map,if it is present in
            // the response elso its value is NULL
            // Controller will set ALL, if it is not set by the Handler.
            String profile = (String) _responseMap.get("profile");
            if (!InterfaceUtil.isNullString(profile))
                _requestMap.put("SERVICE_CLASS", profile);
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_val_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_inact_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("acc_status"));
            // Set the bundles name which are defined at the IN in the request
            // map.
            _requestMap.put("BUNDLE_NAMES", (String) _responseMap.get("defined_bundles"));

            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping();

        }// try
        catch (BTSLBaseException be) {
            _log.error("validate: ", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        }// catch
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validate: ", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug(this, "validate ::", " Exited _requestMap= " + _requestMap + " , multplicationFactor: " + sysInMultFactor);
        }// finally
    }// end of validate method

    /**
     * This method is responsible for the credit of subscriber account.
     * 1.Interface specific parameters are set and added to the request map.
     * 2.Format the request into predefined xml for credit request, method
     * internally calls the generateRequest method of
     * Alcatel452RequestResponseFormatter.
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
            _log.debug("credit ::", "Entered p_map =" + p_map);
        _requestMap = p_map;
        String mulFactor = null;
        String sysOutMultFactor = null;
        String cardGrpSelector = null;
        String amountStr = null;
        String seperateBonusAllowed = null;
        String bundleTypesStr = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            // get intreface id from request map
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // get transaction id from request map and set it in to the
            // reference id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _msisdn = (String) _requestMap.get("MSISDN");
            cardGrpSelector = (String) p_map.get("CARD_GROUP_SELECTOR");

            // Get the system out multiplication factor from the FileCache with
            // the help of interface id.
            sysOutMultFactor = FileCache.getValue(_interfaceID, "SYSTEM_OUT_MULT_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(sysOutMultFactor)) {
                _log.error("credit ", "SYSTEM_OUT_MULT_FACTOR is not defined in the INFile. ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_OUT_MULT_FACTOR is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // Get the multiplication factor according to the selector from the
            // FileCache with the help of interface id.
            if ("1".equals(cardGrpSelector))
                mulFactor = FileCache.getValue(_interfaceID, "MAIN_MULT_FACTOR");
            else if ("2".equals(cardGrpSelector))
                mulFactor = FileCache.getValue(_interfaceID, "SMS_MULT_FACTOR");
            else if ("3".equals(cardGrpSelector))
                mulFactor = FileCache.getValue(_interfaceID, "MMS_MULT_FACTOR");

            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(mulFactor)) {
                _log.error("credit ", "MAIN_MULT_FACTOR or SMS_MULT_FACTOR or MMS_MULT_FACTOR is not defined in the INFile. ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // Set the interface parameters to the request map
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");

            // Changed to handle multiplication factor as double
            double requestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedAmtDouble, Double.parseDouble(sysOutMultFactor));
            // Convert the amount in amount according to the selector.
            inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(inAmount, Double.parseDouble(mulFactor));
            // round flag
            String roundFlag = null;
            roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "Y";
                _requestMap.put("ROUND_FLAG", roundFlag);
            }
            // Check whether bonus is allowed or not.
            seperateBonusAllowed = FileCache.getValue(_interfaceID, "SEPERATE_BONUS_ALLOWED").trim();
            // If SEPERATE_BONUS_ALLOWED is not defined in the INFile, set its
            // value as N.
            if (InterfaceUtil.isNullString(seperateBonusAllowed)) {
                seperateBonusAllowed = "N";
                _log.error("credit ", "Value of SEPERATE_BONUS_ALLOWED is not defined in the INFile, hence taking the default value N. ");
            }
            // Set the SEPERATE_BONUS_ALLOWED into request map with key as
            // SEPERATE_BONUS_ALLOWED.
            _requestMap.put("SEPERATE_BONUS_ALLOWED", seperateBonusAllowed);
            // Get the Main bonus amount from the request map.
            String bonusAmt = (String) _requestMap.get("BONUS_AMOUNT");
            if (!InterfaceUtil.isNullString(bonusAmt)) {
                double requestedBonus = Double.parseDouble(bonusAmt);
                requestedBonus = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedBonus, Double.parseDouble(sysOutMultFactor));
                // Convert the bonus amount according to the selector.
                requestedBonus = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedBonus, Double.parseDouble(mulFactor));
                // If bonus is allowed then deduct the main bonus amount from
                // the interface amount.
                if ("Y".equals(seperateBonusAllowed))
                    inAmount = inAmount - requestedBonus;
                // Change the requested bonus amount from double to String
                String reqBonusStr = null;
                reqBonusStr = String.valueOf(requestedBonus);
                // Round the bonus amount if round flag value is Y
                if ("Y".equals(roundFlag))
                    reqBonusStr = String.valueOf(Math.round(requestedBonus));
                // put the BONUS_AMOUNT in to the map.
                _requestMap.put("IN_BONUS_AMOUNT", reqBonusStr);
            }
            // Change the IN amount from double to String
            amountStr = String.valueOf(inAmount);
            // if round flag value is Y, round the amount, else put it as it is.
            if ("Y".equals(roundFlag))
                amountStr = String.valueOf(Math.round(inAmount));

            // Set the amount into request map with key as transfer_amount.
            _requestMap.put("transfer_amount", amountStr);
            // Calling the method generateRequest method of
            // Alcatel452RequestResponseFormatter class with appropriate
            // action value to generate the xml string for credit
            // key value of HashMap is formatted into XML string for the credit
            // request and stored into string.
            String inStr = _formatter.generateRequest(Alcatel452I.ACTION_IMMEDIATE_CREDIT, _requestMap);

            // send the credit request to IN
            sendRequestToIN(inStr, Alcatel452I.ACTION_IMMEDIATE_CREDIT);

            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Set the post balance after multiplied by multiplication factor.
            try {
                String postBalanceStr = (String) _responseMap.get("credit_balance");
                // Get the system multiplication factor from the FileCache with
                // the help of interface id.
                String sysInMulFactor = (String) FileCache.getValue(_interfaceID, "SYSTEM_IN_MULT_FACTOR");
                // Handle the event if the Multiplication factor is not defined
                // to the INFile
                if (InterfaceUtil.isNullString(sysInMulFactor)) {
                    _log.error("validate: ", "SYSTEM_IN_MULT_FACTOR is not defined in INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_IN_MULT_FACTOR is not defined in IN File");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, Double.parseDouble(sysInMulFactor.trim()));
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                _log.error("credit", "Exception e:" + e.getMessage());
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (cardGrpSelector.equals("1")) {
                // Set the value of NEW_EXPIRY_DATE as the value of end_val_date
                // from responseMap, after converting the format as per
                // interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_val_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
                // Get the value of end_inact_date from the response, change its
                // format as per the interface and set it into
                // requestMap with key as NEW_GRACE_DATE
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_inact_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
                // Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            } else {
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit ::", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }// end of credit method

    /**
     * This method is used to debit the sender account in CP2P request.
     * 
     * @param p_map
     *            HashMap
     * @throws Exception
     */
    public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust ::", "Entered p_map =" + p_map);
        _requestMap = p_map;
        String amountStr = null;
        String mulFactor = null;
        String sysOutMultFactor = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            // Get the intreface id form request map
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // get transaction id from request map and set it in to the
            // reference id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _msisdn = (String) _requestMap.get("MSISDN");
            // Get the system out multiplication factor from the FileCache with
            // the help of interface id.
            sysOutMultFactor = FileCache.getValue(_interfaceID, "SYSTEM_OUT_MULT_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(sysOutMultFactor)) {
                _log.error("debitAdjust: ", "SYSTEM_OUT_MULT_FACTOR is not defined in the INFile. ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_OUT_MULT_FACTOR is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            mulFactor = (String) FileCache.getValue(_interfaceID, "MAIN_MULT_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(mulFactor)) {
                _log.error("debitAdjust: ", "MAIN_MULT_FACTOR is not defined in the INFile. ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // Set the interface parameters to the requestMap
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            // Changed to handle multiplication factor as double
            double requestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedAmtDouble, Double.parseDouble(sysOutMultFactor.trim()));
            // Convert the inAmount according to the selector.
            inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(inAmount, Double.parseDouble(mulFactor));
            String roundFlag = null;
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag))
                roundFlag = "Y";
            // set the Round flag value in to the request map.
            _requestMap.put("ROUND_FLAG", roundFlag);
            // if round flag value is Y, round the amount, else put it as it is.
            if ("Y".equals(roundFlag)) {
                amountStr = String.valueOf(Math.round(inAmount));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            } else
                amountStr = String.valueOf(inAmount);
            // Set the rounded amount into request map with key as
            // transfer_amount.
            _requestMap.put("transfer_amount", amountStr);
            // key value of HashMap is formatted into XML string for the
            // debitAdjust request.
            String inStr = _formatter.generateRequest(Alcatel452I.ACTION_IMMEDIATE_DEBIT, _requestMap);
            // sending the debit request to the IN
            sendRequestToIN(inStr, Alcatel452I.ACTION_IMMEDIATE_DEBIT);
            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // No balance is obtained in the debit response. Confirm whether to
            // calculate the
            // post balance of subscriber or set POST_BALANCE_ENQ_SUCCESS as N
            // into requestMap.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
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
                _log.error("debitAdjust: ", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("debitAdjust: ", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust ::", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }// end of debitAdjust

    /**
     * This method is used to credit back the sender in case of ambiguous case.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust ::", "Entered p_map =" + p_map);
        _requestMap = p_map;
        String amountStr = null;
        String mulFactor = null;
        String sysOutMultFactor = null;
        String roundFlag = null;
        String bonusAllowed = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            // get intreface id form request map
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            // get transaction id from request map and set it in to the
            // reference id.
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _msisdn = (String) _requestMap.get("MSISDN");
            // Get the system out multiplication factor from the FileCache with
            // the help of interface id.
            sysOutMultFactor = FileCache.getValue(_interfaceID, "SYSTEM_OUT_MULT_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(sysOutMultFactor)) {
                _log.error("creditAdjust: ", "SYSTEM_OUT_MULT_FACTOR is not defined in the INFile. ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_OUT_MULT_FACTOR is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // Get the main account multiplication factor from the FileCache
            // with the help of interface id.
            mulFactor = (String) FileCache.getValue(_interfaceID, "MAIN_MULT_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(mulFactor)) {
                _log.error("creditAdjust: ", "MAIN_MULT_FACTOR is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "alcatel452INHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // Set the interface parameters to the request map
            setInterfaceParameters();
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");

            // get the interface amount from the map.
            double requestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedAmtDouble, Double.parseDouble(sysOutMultFactor.trim()));
            // Convert the inAmount according to the selector.
            inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(inAmount, Double.parseDouble(mulFactor));

            // get the round flag from the file catche
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            // if round flag is not defined in the file catche, set the default
            // value as Y.
            if (InterfaceUtil.isNullString(roundFlag))
                roundFlag = "Y";
            _requestMap.put("ROUND_FLAG", roundFlag);

            // If bonus for main account is allowed then deduct the bonus amount
            // from the interface amount
            bonusAllowed = (String) FileCache.getValue(_interfaceID, "SEPERATE_BONUS_ALLOWED").trim();
            // If SEPERATE_BONUS_ALLOWED is not defined in the INFile, set its
            // value as N.
            if (InterfaceUtil.isNullString(bonusAllowed)) {
                bonusAllowed = "N";
                _log.error("creditAdjust: ", "Value of SEPERATE_BONUS_ALLOWED is not defined in the INFile, hence taking the default value N. ");
            }
            // Set the SEPERATE_BONUS_ALLOWED into request map with key as
            // SEPERATE_BONUS_ALLOWED.
            _requestMap.put("SEPERATE_BONUS_ALLOWED", bonusAllowed);
            // Get the Main bonus amount from the request map.
            String bonusAmt = (String) _requestMap.get("BONUS_AMOUNT");
            if (!InterfaceUtil.isNullString(bonusAmt)) {
                double requestedBonus = Double.parseDouble((String) _requestMap.get("BONUS_AMOUNT"));
                requestedBonus = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedBonus, Double.parseDouble(sysOutMultFactor));
                // Convert the bonus amount according to the selector.
                requestedBonus = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedBonus, Double.parseDouble(mulFactor));
                // If bonus is allowed then deduct the main bonus amount from
                // the interface amount.
                if ("Y".equals(bonusAllowed))
                    inAmount = inAmount - requestedBonus;
                // Change the requested bonus amount from double to String
                String reqBonusStr = String.valueOf(requestedBonus);
                // Round the bonus amount if round flag value is Y.
                if ("Y".equals(roundFlag))
                    reqBonusStr = String.valueOf(Math.round(requestedBonus));
                // put the BONUS_AMOUNT in to the map.
                _requestMap.put("IN_BONUS_AMOUNT", reqBonusStr);
            }
            // Change the IN amount from double to String
            amountStr = String.valueOf(inAmount);
            // if round flag value is Y, round the interface amount, else put it
            // as it is.
            if ("Y".equals(roundFlag)) {
                amountStr = String.valueOf(Math.round(inAmount));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }

            // Set the rounded amount into request map with key as
            // transfer_amount.
            _requestMap.put("transfer_amount", amountStr);
            // Calling the method generateRequest method of
            // Alcatel452RequestResponseFormatter class with appropriate
            // action value to generate the xml string for credit
            // key value of HashMap is formatted into XML string for the credit
            // request and stored into string.
            String inStr = _formatter.generateRequest(Alcatel452I.ACTION_IMMEDIATE_CREDIT, _requestMap);

            // sending the credit request to IN
            sendRequestToIN(inStr, Alcatel452I.ACTION_IMMEDIATE_CREDIT);

            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Set the post balance after multiplied by multiplication factor.
            try {
                String postBalanceStr = (String) _responseMap.get("credit_balance");
                // Get the system multiplication factor from the FileCache with
                // the help of interface id.
                String sysMulFactor = (String) FileCache.getValue(_interfaceID, "SYSTEM_IN_MULT_FACTOR");
                // Handle the event if the Multiplication factor is not defined
                // to the INFile
                if (InterfaceUtil.isNullString(sysMulFactor)) {
                    _log.error("creditAdjust: ", "SYSTEM_IN_MULT_FACTOR is not defined in INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_IN_MULT_FACTOR is not defined in IN File");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, Double.parseDouble(sysMulFactor.trim()));
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                _log.error("creditAdjust: ", "Exception e:" + e.getMessage());
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "alcatel452INHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Set the value of NEW_EXPIRY_DATE as the value of end_val_date
            // from responseMap, after converting the format as per interface
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_val_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            // Get the value of end_inact_date from the response, change its
            // format as per the interface and set it into
            // requestMap with key as NEW_GRACE_DATE
            _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_inact_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
            // Set the POST_BALANCE_ENQ_SUCCESS equal to N into requestMap.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            p_map.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust: ", "BTSLBaseException be:" + be.getMessage());
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
                _log.error("creditAdjust: ", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "alcatel452INHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust: ", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "alcatel452INHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While creditAdjust get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust ::", "Exited _requestMap=" + _requestMap);
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
     * This method is responsible to send the request to IN.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN: ", "Entered p_inRequestStr:" + p_inRequestStr + " p_action:" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, " INTERFACE_ID:" + _interfaceID + " Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "p_action=" + p_action);
        String responseStr = "";
        Alcatel452UrlConnection alcatel452UrlConnection = null;
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        int readTimeOut = 0;
        String inReconID = null;
        try {
            _responseMap = new HashMap();
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            try {
                // Fetch the url,connect timeout,read timeout(validation or
                // topup) and keep alive values from the INFile
                // Get url from INFile
                String urlFromFileCache = (String) FileCache.getValue(_interfaceID, "URL");
                if (InterfaceUtil.isNullString(urlFromFileCache)) {
                    _log.error("sendRequestToIN: ", "URL is not defined in the INFile. ");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "URL is not defined in IN File");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                // Get connect time out from INFile
                String connectTimeOutStr = (String) FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT").trim();
                // Get keep alive from INFile
                String keepAlive = (String) FileCache.getValue(_interfaceID, "KEEP_ALIVE");
                if (InterfaceUtil.isNullString(connectTimeOutStr) || InterfaceUtil.isNullString(keepAlive)) {
                    _log.error("sendRequestToIN: ", "CONNECT_TIMEOUT or KEEP_ALIVE is not defined in the INFile. ");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "CONNECT_TIMEOUT or KEEP_ALIVE is not defined in IN File");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                int connectTimeOut = Integer.parseInt(connectTimeOutStr);
                // Get read time out from INFile

                if (Alcatel452I.ACTION_GET_ACCOUNT_INFO == p_action) {
                    String readTimeOutValStr = (FileCache.getValue(_interfaceID, "READ_TIMEOUT_VAL").trim());
                    if (InterfaceUtil.isNullString(readTimeOutValStr)) {
                        _log.error("sendRequestToIN: ", "READ_TIMEOUT_VAL is not defined in the INFile. ");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "READ_TIMEOUT_VAL is not defined in IN File");
                        throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    readTimeOut = Integer.parseInt(readTimeOutValStr);
                } else {
                    String readTimeOutTopStr = (FileCache.getValue(_interfaceID, "READ_TIMEOUT_TOP").trim());
                    if (InterfaceUtil.isNullString(readTimeOutTopStr)) {
                        _log.error("sendRequestToIN: ", "READ_TIMEOUT_TOP is not defined in the INFile. ");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "READ_TIMEOUT_TOP is not defined in IN File");
                        throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    readTimeOut = Integer.parseInt(readTimeOutTopStr);
                }
                // Confirm for the service name servlet for the url consturction
                // whether URL will be specified in INFile or IP,PORT and
                // ServletName.
                alcatel452UrlConnection = new Alcatel452UrlConnection(urlFromFileCache, connectTimeOut, readTimeOut, keepAlive);
            } catch (BTSLBaseException be) {
                throw be;
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the http connection for INTERFACE_ID=[" + _interfaceID + "]" + " Exception : " + e.getMessage() + " action: " + p_action);
                _log.error("sendRequestToIN: ", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }// end of catch-Exception
            try {
                // In creditAdjust (sender credit back )don't check interface
                // status, simply send the request to IN.
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                    _isSameRequest = true;
                    checkInterfaceB4SendingRequest();
                }
                // After the successful connection creation get a print writer
                // object from
                // the Alcatel452UrlConnection class.
                PrintWriter out = alcatel452UrlConnection.getPrintWriter();
                out.flush();
                // Post the request string to the connection out put stream and
                // Store the time when request is send to IN under the key
                // IN_START_TIME into requestMap.
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            } catch (BTSLBaseException be) {
                throw be;
            }
            // While writing the request to connections out put steam if any
            // error occurs does the following.
            catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN: ", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Exception while sending request to Alcatel IN" + " action :" + p_action);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
            }
            // Create buffered reader and Read Response from the IN
            StringBuffer buffer = new StringBuffer();
            String response = "";
            try {
                // After sending the request to IN set the Buffered Reader
                // object to
                // read the connection input stream of the
                // alcatel452UrlConnection class.
                alcatel452UrlConnection.setBufferedReader();
                BufferedReader in = alcatel452UrlConnection.getBufferedReader();
                while ((response = in.readLine()) != null)
                    buffer.append(response);
                endTime = System.currentTimeMillis();
                // Check the difference of start time and end time of IN request
                // response
                // against the warn time, if it takes more time Handle the event
                // with level INFO and
                // message as Alcatel IN is taking more time than the threshold
                // time.
                // Warn time
                warnTime = Long.parseLong(FileCache.getValue(_interfaceID, "WARN_TIMEOUT"));
                if (endTime - startTime > warnTime) {
                    _log.info("sendRequestToIN: ", "WARN time reaches for the Alcatel452IN, startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Alcatel452INHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Alcatel452 IN is taking more time than the warning threshold. Time: " + (endTime - startTime) + " action: " + p_action);
                }
            } // try
            catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN: ", " Response form interface is null, exception is: " + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Alcatel452INHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while getting response from Alcatel452IN e: " + e.getMessage());
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            } finally {
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                _log.error("sendRequestToIN: ", "Request sent to IN at:" + startTime + ", Response received from IN at:" + endTime + ", defined read time out is:" + readTimeOut + ", and Time taken by IN is: " + (endTime - startTime));
            }
            responseStr = buffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN: ", "Response Str:" + responseStr);
            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, " INTERFACE_ID:" + _interfaceID + "Request String :" + p_inRequestStr + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
            // Get the HTTP Status code and check whether it is OK or Not??
            String httpStatus = alcatel452UrlConnection.getResponseCode();
            _requestMap.put("PROTOCOL_STATUS", httpStatus);
            if (!Alcatel452I.HTTP_STATUS_OK.equals(httpStatus))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            if (InterfaceUtil.isNullString(responseStr)) {
                // raise the alarm
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from Alcatel IN");
                _log.error("sendRequestToIN: ", "NULL response from interface. ");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            // Parse the response using the parseResponse method of
            // Alcatel452RequestResponseFormatter class.
            _responseMap = _formatter.parseResponse(p_action, responseStr, _interfaceID);

            // Get the interface status and set to the requestMap
            String result = (String) _responseMap.get("result");
            _requestMap.put("INTERFACE_STATUS", result);

            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
            }
            String cpTransID = (String) _responseMap.get("cp_transaction_id");
            String inReconId = (String) _requestMap.get("IN_RECON_ID");
            if (!InterfaceUtil.isNullString(result) && !result.equals(Alcatel452I.RESULT_OK)) {
                _log.info("sendRequestToIN: ", "cp transID:" + cpTransID + " CP ID=" + (String) _responseMap.get("cp_id") + " result=" + result);
                if (result.equals(Alcatel452I.RESULT_ERROR_ACC_NOT_FOUND))
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                // send alert message(TO BE IMPLEMENTED)
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Status of the response, result: " + result);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } else if (InterfaceUtil.isNullString(cpTransID) || InterfaceUtil.isNullString((String) _responseMap.get("cp_id"))) {
                _log.info("sendRequestToIN: ", "transID:" + cpTransID + " CP ID=" + (String) _responseMap.get("cp_id") + " result=" + result);
                // Raise the alarm
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Parameters values blank in response, result: " + result);
                throw new BTSLBaseException(InterfaceErrorCodesI.NULL_INTERFACE_RESPONSE);
            } else if (!((String) _responseMap.get("cp_id")).equals((String) _requestMap.get("cp_id"))) {
                _log.info("sendRequestToIN: ", "Response CP ID:" + _responseMap.get("cp_id") + " Request CP ID=" + _requestMap.get("cp_id") + " Mismatch");
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Request and Response CP id from Alcatel IN does not match");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            } else if (!inReconId.equals(cpTransID)) {
                _log.info("sendRequestToIN: ", "IN_RECON_ID:" + inReconId + " cp_transaction_id: " + cpTransID);
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Request and Response transaction id from Alcatel IN does not match");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN: ", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN: ", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                if (alcatel452UrlConnection != null)
                    alcatel452UrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN: ", "Exception while closing Alcatel452Url Connection:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN: ", "Exiting p_action:" + p_action + " Response Str:" + responseStr);
        }// end of finally
    }// end of sendRequestToIN

    /**
     * This method is used to get interface specific values from FileCache(load
     * at starting)based on
     * interface id and set to the requested map.These parameters are
     * 1.cp_id
     * 2.application
     * 3.transaction_currency
     * 
     * @throws BTSLBaseException
     *             , Exception
     */
    private void setInterfaceParameters() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters ::", " Entered");
        try {
            // Set the interface parameters to the request map, these are as
            // bellow
            // § application: Used to specify the entity for which message has
            // to route.
            // § cp_id: Used to represent the content provider.
            // § transaction_currency: used to represent the currency supported
            // by the interface.
            String cpID = (String) FileCache.getValue(_interfaceID, "CP_ID" + "_" + (String) _requestMap.get("MODULE"));
            if (InterfaceUtil.isNullString(cpID)) {
                _log.error("setInterfaceParameters: ", "Value of CP_ID_MODULE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CP_ID_" + (String) _requestMap.get("MODULE") + " is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("cp_id", cpID.trim());
            String application = (String) FileCache.getValue(_interfaceID, "APPLICATION");
            if (InterfaceUtil.isNullString(application)) {
                _log.error("setInterfaceParameters: ", "Value of APPLICATION is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "APPLICATION is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("application", application.trim());

            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters: ", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters: ", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters: ", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters: ", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters: ", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters: ", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters: ", "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters :: ", "Exited _requestMap:" + _requestMap);
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
            _log.debug("setLanguageFromMapping: ", "Entered");
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
            if (InterfaceUtil.isNullString(mappingString)) {
                mappingString = "";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Alcatel452INHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LANGUAGE_MAPPING is not defined in IN file,Hence setting the Default language");
            }
            langFromIN = (String) _responseMap.get("prof_lang");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping: ", "mappingString = " + mappingString + " langFromIN = " + langFromIN);
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Alcatel452INHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping: ", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "Alcatel452INHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping: ", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

    /**
     * Method to Check interface status before sending request.
     * 
     * @throws BTSLBaseException
     */
    private void checkInterfaceB4SendingRequest() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkInterfaceB4SendingRequest: ", "Entered");
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
                _log.error("checkInterfaceB4SendingRequest: ", "Value of AUTO_RESUME_SUPPORT is not defined in the INFile");
            }

            // If Controller sends 'A' and interface status is suspended, expiry
            // is checked.
            // If Controller sends 'M', request is forwarded to IN after
            // resetting counters.
            if (InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(_interfaceLiveStatus) && _interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND)) {
                // Check if Auto Resume is supported by IN or not.If not then
                // throw exception. request would not be sent to IN.
                if ("N".equals(autoResumeSupported)) {
                    _log.error("checkInterfaceB4SendingRequest: ", "Interface Suspended.");
                    throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest ", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
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
            _log.error("checkInterfaceB4SendingRequest: ", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest ", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkInterfaceB4SendingRequest: ", "Exited");
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
            _log.debug("handleCancelTransaction: ", "Entered.");
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
                _log.debug("handleCancelTransaction: ", "reconLog." + reconLog);
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
                    throw new BTSLBaseException(this, "handleCancelTransaction ", cancelTxnStatus); // //Based
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
            _log.error("handleCancelTransaction: ", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "handleCancelTransaction ", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction: ", "Exited");
        }
    }

    /**
     * Method is used to generate the IN Reconciliation Id.
     */
    private String getINTransactionID() {
        String txnId = null;
        if (_userType != null)
            txnId = _referenceID + _userType;
        _requestMap.put("IN_RECON_ID", txnId);
        return txnId;
    }
}