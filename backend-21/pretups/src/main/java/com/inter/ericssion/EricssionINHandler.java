package com.inter.ericssion;

/**
 * @(#)EricssionINHandler.java
 *                             Copyright(c) 2005, Bharti Telesoft Int. Public
 *                             Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Abhijit Chauhan June 22,2005 Initial Creation
 *                             Ashish July 12,2006 Modified
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;

/*
 * Modification of This class includes
 * 1.Make sub service removal command as configurable from INFile.
 * Sub service removal will be checked for invocation when user is not barred
 * from IN,it means
 * a.Check user is not barred, means BarInd=N
 * b.Check FirstCall=
 * 2.Exception Handling.
 * 3.Integration with Eric client,InClass & outClass
 * EricClient includes the following responsiblities.
 * a.Initiates two threads(InClass & OutClass)
 * i.InClass-This is responsible to send the request to EricsionIN
 * ii.OutClass-This is reponsible to collect the response from EricsionIN
 */
public class EricssionINHandler implements InterfaceHandler {
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    EricssionRequestFormatter _formatter = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private String _interfaceID = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    public EricssionINHandler() {
        _formatter = new EricssionRequestFormatter();
    }

    /**
     * This method is resposible to handle the request for subscriber
     * validation.
     * For this methods sends various requests to get the information from
     * EricsionIN,These requests are
     * 1.Send a request for balance Enquiry to get the information for different
     * dedicated account.
     * 2.Send Subscriber data request to get the subscriber data
     * information,these informations are
     * a. Serivce class
     * b. Language
     * c. First call flag value(This indicates that subcriber has to send a
     * request to first activate)
     * 3.From the INFile check the FIRST_FLAG, if it is "Y" and subscriber data
     * also has FirstCall value "Y"
     * then send the request of FirstCall.
     * 4.If Operator provides FIRST_FLAG equal to "N" in INFile,there is no need
     * to send request for FirstCall.
     * 5.If Operator provides FIRST_FLAG equal to "N" in INFile and subscriber
     * data has FirstCall value "Y" then CONFIRM the
     * case whether we have to handle the event.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered  p_requestMap =" + InterfaceUtil.getPrintMap(p_requestMap));
        _requestMap = p_requestMap;
        String multFactor = null;
        String msisdn = null;
        String transactionID = null;
        double currAmount = 0;
        long amount = 0;
        try {
            _requestMap.put("Stage", "VAL");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            transactionID = (String) _requestMap.get("TRANSACTION_ID");
            msisdn = (String) _requestMap.get("MSISDN");

            // Set the interface parameter-Confirm
            setInterfaceParameters(_interfaceID);
            // Get TransId
            String inStr = _formatter.getINPPBalance(_requestMap);
            // Sending the request to IN
            sendRequestToIN(inStr, "Balance Enquiry");

            // Put the interface previous balance after converting it into
            // system amount.
            // For this first confirm whether IN provides in lowest denominator
            // or not?
            String amountStr = (String) _responseMap.get("TransAmt");
            multFactor = (String) _requestMap.get("multiplication_factor");
            try {
                currAmount = Double.parseDouble(amountStr);
                amount = InterfaceUtil.getSystemAmount(currAmount, Integer.parseInt(multFactor));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[validate]", "MSISDN = " + msisdn + "TransactionID = " + transactionID, " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "TransAmt recieved from response is not Numeric");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("INTERFACE_PREV_BALANCE", String.valueOf(amount));

            // Here we have to generate a INTransaction ID for this request
            // generate query for subscriber data

            inStr = _formatter.getINRequestToPPSubsData(_requestMap);

            // Sending the request to get the subscriber data.
            sendRequestToIN(inStr, "Subscriber Information");
            _requestMap.put("SERVICE_CLASS", _responseMap.get("Class"));

            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping(_interfaceID);

            // Fetch the bar status from response and check whether it is barred
            // or not.
            // Y or M = Barred , N= not barred
            String barind = (String) _responseMap.get("BarInd");
            // Checking the Condition whether the subscriber is barred from IN.
            // (BarInd=M , Here what M stands for??)
            if (barind.equals("Y") || barind.equals("M"))
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
            if (barind.equalsIgnoreCase("N")) {
                // Here we have to decide whether to send PPIVRFirstCall to IN
                // or not?
                // This can be checked by following checks
                // 1.FirstCall flag(FIRST_CALL) value, set in IN File,This value
                // defines whether the IN supported the PPIVRFirstCall command.
                // 2.FirstCall flag value from response of PPSubsData,This value
                // defines whether the subscriber is activated or not.
                // Meaning of FirstCall value (Y,N),fetched from PPSubsData
                // Y- Flag, FirstCall flag is already set no need to send the
                // PPIVRFirsCall request
                // N- Flag, FirstCall flag is not set for the user need to send
                // the PPIVRFirsCall request.
                String firstCallStr = (String) _responseMap.get("FirstCall");
                if (_log.isDebugEnabled())
                    _log.debug("validate", "firstCallStr = " + firstCallStr);
                _requestMap.put("FIRST_CALL", firstCallStr);

                String firstCallSet = FileCache.getValue(_interfaceID, "FIRST_FLAG");
                if (firstCallSet == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[validate]", "MSISDN = " + msisdn + "TransactionID = " + transactionID, " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "FIRST_FLAG is not defined in IN File");
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                // Get the service removal flag from INFile that will decide
                // whether to invoke PPExpiryDates command or not.
                // Confirm this flag is manadatory or not in INFile.
                String serviceRemoval = FileCache.getValue(_interfaceID, "SERVICE_REMOVAL");

                if (firstCallSet.equalsIgnoreCase("N") && firstCallStr.equalsIgnoreCase("N"))
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);

                // By Default set the ACCOUNT_STATUS as INACTIVE
                // If FirstCall is set Y then only it Account is rechargable and
                // its Account status should be Active.
                _requestMap.put("ACCOUNT_STATUS", "INACTIVE");

                if ("Y".equals(firstCallStr))
                    _requestMap.put("ACCOUNT_STATUS", "ACTIVE");

                if (firstCallSet.equalsIgnoreCase("Y") && firstCallStr.equalsIgnoreCase("N")) {
                    inStr = _formatter.getINPPSubsDataToIVRFirstCall(_requestMap);
                    sendRequestToIN(inStr, "First Call");
                }
                // This case is for the service removal command
                // else if((firstCallStr.equalsIgnoreCase("N") &&
                // firstCallSet.equalsIgnoreCase("A"))||(firstCallStr.equalsIgnoreCase("Y")))
                _requestMap.put("OLD_EXPIRY_DATE", _formatter.getInterfaceDateFromDateString((String) _responseMap.get("AirtimeExpiry"), "yyyyMMdd"));
                if ("Y".equals(serviceRemoval)) {
                    inStr = _formatter.getINPPSubsDataToPPExpiryDate(_requestMap);
                    sendRequestToIN(inStr, "Expiry Check");
                    String serviceRemovalStr = (String) _responseMap.get("ServiceRemoval");
                    if (!serviceRemovalStr.equals("00000000")) {
                        long serviceRemovalLong = Long.parseLong(serviceRemovalStr);
                        // Get Current date
                        String currentDateStr = _formatter.getEricssionCurrentDateTime();
                        String stringcurrentDateStr = currentDateStr.substring(0, 8);
                        long currentDateLong = Long.parseLong(stringcurrentDateStr);
                        if (currentDateLong > serviceRemovalLong)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_EXPIRED);
                    }
                }
            } // End of Else if

            // Set the Transaction status
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage() + " _requestMap" + _requestMap);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[validate]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN") + "INTERFACE_ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting " + _requestMap);
        }
    }

    /**
     * This method is responsible to credit the subscriber account.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered  p_requestMap = " + p_requestMap);
        _requestMap = p_requestMap;// Used to store the reference of requested
                                   // map
        String inStr = null;// Contains the request s
        long newAmount = 0;
        String newAmountStr = null;
        String multFactor = null;

        String transactionID = null;
        String msisdn = null;
        long totalAmount = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            transactionID = (String) _requestMap.get("TRANSACTION_ID");
            msisdn = (String) _requestMap.get("MSISDN");

            // Set the interface parameter
            setInterfaceParameters(_interfaceID);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            String amountStr = getInterfaceAmount(_requestMap);
            _requestMap.put("transfer_amount", amountStr);

            // Before sending the credit request convert the amount
            // Confirm for the recharge type.
            // Is SMSRechargeType as Key will be present in request map?
            String rechargeType = (String) _requestMap.get("SMSRechargeType");
            if (_log.isDebugEnabled())
                _log.debug("credit", "rechargeType = " + rechargeType);
            if (rechargeType == null || rechargeType.equalsIgnoreCase("Amount")) // to
                                                                                 // be
                                                                                 // discussed
            {
                inStr = _formatter.getINPPBalanceToPPAccount(_requestMap);
                sendRequestToIN(inStr, "AmountCredit");
                newAmountStr = (String) _responseMap.get("TransAmt");
                _requestMap.put("NEW_EXPIRY_DATE", _formatter.getInterfaceDateFromDateString((String) _responseMap.get("AirtimeExpiry"), "yyyyMMdd"));
            } else {
                inStr = _formatter.getINPPBalanceToPPRecharge(_requestMap);// to
                                                                           // be
                                                                           // discussed
                sendRequestToIN(inStr, "PIN Based Recharge");
                newAmountStr = (String) _responseMap.get("RechargeValue");

            }// End of Else
            try {
                multFactor = (String) _requestMap.get("multiplication_factor");
                // No need to change INTERFACE_PREV_BALANCE into System Amount
                long oldAmount = Long.parseLong((String) _requestMap.get("INTERFACE_PREV_BALANCE"));
                // newAmount is obtained from the response hence we have to
                // first convert it to system amount.
                newAmount = Long.parseLong(newAmountStr);
                long amount = InterfaceUtil.getSystemAmount(newAmount, Integer.parseInt(multFactor));
                totalAmount = amount + oldAmount;
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[credit]", " INTERFACE ID = " + _interfaceID, "", (String) _requestMap.get("NETWORK_CODE"), "Either in request map previous balance or response map new balance is not numeric");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(totalAmount));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[credit]", "", msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + "_requestMap = " + _requestMap);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[credit]", "", msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while credit e =" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exiting _requestMap = " + _requestMap);
        }
    }

    /**
     * Credit Adjust
     * 
     * @param p_requestMap
     *            p_requestMap
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered  p_requestMap = " + p_requestMap);
        _requestMap = p_requestMap;
        String inStr = null;
        int oldAmount = 0;
        int newAmount = 0;

        String transactionID = null;
        String msisdn = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            transactionID = (String) _requestMap.get("TRANSACTION_ID");
            msisdn = (String) _requestMap.get("MSISDN");

            // Set the interface parameter
            setInterfaceParameters(_interfaceID);
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            String amountStr = getInterfaceAmount(_requestMap);
            _requestMap.put("transfer_amount", amountStr);

            // Getting the requested string for PPAdjust
            inStr = _formatter.getINPPAdjustCredit(_requestMap);

            // Sending request to IN
            sendRequestToIN(inStr, "AmountCredit");
            try {
                oldAmount = Integer.parseInt((String) _requestMap.get("INTERFACE_PREV_BALANCE"));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[credit]", " INTERFACE ID = " + _interfaceID, "MSISDN = " + msisdn + "TransactionID = " + transactionID, (String) _requestMap.get("NETWORK_CODE"), "In request map previous balance is not numeric:: INTERFACE_PREV_BALANCE  = " + _requestMap.get("INTERFACE_PREV_BALANCE"));
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

            }
            try {
                newAmount = Integer.parseInt((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[credit]", "INTERFACE ID = " + _interfaceID, "MSISDN = " + msisdn + " TransactionID = " + transactionID, (String) _requestMap.get("NETWORK_CODE"), "In request map previous balance is not numeric  :: INTERFACE_AMOUNT = " + (String) _requestMap.get("INTERFACE_AMOUNT"));
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            int totalAmount = 0;
            totalAmount = newAmount + oldAmount;
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(totalAmount));
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[creditAdjust]", "INTERFACE ID = " + _interfaceID + " MSISDN = " + msisdn, " TransactionID = " + transactionID, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage() + " _requestMap = " + _requestMap);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[creditAdjust]", "INTERFACE ID = " + _interfaceID + " MSISDN = " + msisdn, " TransactionID = " + transactionID, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e =" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exiting _requestMap = " + _requestMap);
        }
    }

    /**
     * Debit Adjust
     * 
     * @param p_requestMap
     *            p_requestMap
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap = " + p_requestMap);
        _requestMap = p_requestMap;

        String transactionID = null;
        String msisdn = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            transactionID = (String) _requestMap.get("TRANSACTION_ID");
            msisdn = (String) _requestMap.get("MSISDN");

            // Set the interface parameter
            setInterfaceParameters(_interfaceID);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            String amountStr = getInterfaceAmount(_requestMap);
            _requestMap.put("transfer_amount", amountStr);

            String inStr = _formatter.getINPPAdjustDebit(_requestMap);
            sendRequestToIN(inStr, "AmountDebit");
            long oldAmount = Long.parseLong((String) _requestMap.get("INTERFACE_PREV_BALANCE"));
            long newAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            long totalAmount = 0;
            totalAmount = oldAmount - newAmount;
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(totalAmount));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[debitAdjust]", " INTERFACE ID = " + _interfaceID, "MSISDN = " + msisdn + " TransactionID = " + transactionID, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[debitAdjust]", " INTERFACE ID = " + _interfaceID, "MSISDN = " + msisdn + " TransactionID = " + transactionID, (String) _requestMap.get("NETWORK_CODE"), "Check the value in requestMap 1. INTERFACE_PREV_BALANCE 2.INTERFACE_AMOUNT e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exiting _requestMap = " + _requestMap);
        }
    }

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
     * This method is responsible to send the request to IN
     * 
     * @param p_inRequestStr
     * @param p_stage
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(String p_inRequestStr, String p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_inRequestStr + " p_stage:" + p_stage);
        String responseStr = null;// Store the response from the IN for a
                                  // request
        int noOfRetries = 0; // This represents the number of times to retry to
                             // get the response from IN
        long retrySleep = 0; // This represents the time to sleep a thread after
                             // sending the request to get the response,value is
                             // in milisecond.
        long firstSleep = 0;// This represents the time to sleep a thread before
                            // trying to get the response just after sent the
                            // request.

        String msisdn = null;
        String referenceID = null;
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        int i = 0;// Used to count the no of retires.
        try {
            String inReconID = (String) _requestMap.get("IN_RECON_ID");

            _responseMap = new HashMap();

            // Confirm to Abhijit.
            String transID = (String) _requestMap.get("IN_TXN_ID");
            transID = inReconID;
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            msisdn = (String) _requestMap.get("MSISDN");
            referenceID = (String) _requestMap.get("TRANSACTION_ID");
            TransactionLog.log(transID, referenceID, msisdn, (String) _requestMap.get("NETWORK_CODE"), p_stage, PretupsI.TXN_LOG_REQTYPE_REQ, "Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
            // Get a vector reference from that is stored in static map whose
            // key is interfaceID
            // Request strings are added to this vector,while sending the
            // request InClass extract the request string based on interfaceID.
            Vector inBucket = (Vector) EricClient.inBucketMap.get(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "inBucket = " + inBucket);
            // All the response from IN are collected by Out Class and that are
            // stored in a map based on the transactionID.
            Map outBucket = (Map) EricClient.outBucketMap.get(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "outBucket = " + outBucket);
            Vector busyBucket = (Vector) EricClient.busyBucketMap.get(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "busyBucket = " + busyBucket);
            // Storing the request string to the inBucket,This request will be
            // send to IN by InClass Thread.

            // Get the time when request is added to busy bucket.
            startTime = System.currentTimeMillis();
            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
            // In creditAdjust (sender credit back )don't check interface
            // status, simply send the request to IN.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }
            inBucket.add(p_inRequestStr);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "After adding the requestString to inBucket = " + inBucket);

            /*
             * If ambiguousCaseStr is not defined then no need to raise error
             * //Get ambiguous cases from IN file.In INFile We defines the
             * Status codes that are AMBIGUOUS.
             * ambiguousCaseStr =
             * FileCache.getValue(_interfaceID,"AMBIGUOUS_CASES");
             * if(ambiguousCaseStr==null)
             * {
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES
             * ,EventStatusI.RAISED,EventLevelI.FATAL,
             * "EricssionINHandler[sendRequestToIN]"
             * ,(String)_requestMap.get("TRANSACTION_ID"
             * ),(String)_requestMap.get
             * ("MSISDN"),(String)_requestMap.get("NETWORK_CODE"
             * )+" INTERFACE_ID = "+_interfaceID+"Stage = "+p_stage,
             * "Key AMBIGUOUS_CASES is not defined in IN File");
             * throw new
             * BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION
             * );
             * }
             * ambiguousCaseList =
             * InterfaceUtil.NullToString(ambiguousCaseStr).split(",");
             * ambList = new ArrayList();
             * for(int j=0,size=ambiguousCaseList.length;j<size;j++)
             * ambList.add(ambiguousCaseList[j]);
             */
            String noOfRetriesStr = null;
            String retrySleepStr = null;
            String firstSleepStr = null;
            try {
                // Get the retry count and sleep time for the validation
                // requests(BalanceEnquiry,SubscriberInformation) and TOP-UP
                // requests(credit,creditAdjust and debitAdjust)
                if ("AmountCredit".equals(p_stage) || "AmountDebit".equals(p_stage) || "PIN Based Recharge".equals(p_stage)) {
                    noOfRetriesStr = FileCache.getValue(_interfaceID, "RETRY_COUNT_TOP");
                    retrySleepStr = FileCache.getValue(_interfaceID, "SLEEP_TIME_TOP");
                    firstSleepStr = FileCache.getValue(_interfaceID, "FIRST_SLEEP_TIME_TOP");
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "RETRY_COUNT_TOP::" + noOfRetriesStr + " SLEEP_TIME_TOP::" + retrySleepStr + " FIRST_SLEEP_TIME_TOP::" + firstSleepStr);
                } else {
                    noOfRetriesStr = FileCache.getValue(_interfaceID, "RETRY_COUNT_VAL");
                    retrySleepStr = FileCache.getValue(_interfaceID, "SLEEP_TIME_VAL");
                    firstSleepStr = FileCache.getValue(_interfaceID, "FIRST_SLEEP_TIME_VAL");
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "RETRY_COUNT_VAL::" + noOfRetriesStr + " SLEEP_TIME_VAL::" + retrySleepStr + " FIRST_SLEEP_TIME_VAL::" + firstSleepStr);
                }
                // noOfRetries =
                // Integer.parseInt(FileCache.getValue(_interfaceID,"RETRY_COUNT"));
                // retrySleep =
                // Long.parseLong(FileCache.getValue(_interfaceID,"SLEEP_TIME"));
                noOfRetries = Integer.parseInt(noOfRetriesStr.trim());
                retrySleep = Long.parseLong(retrySleepStr.trim());
                if (!BTSLUtil.isNullString(firstSleepStr))
                    firstSleep = Long.parseLong(firstSleepStr.trim());
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", " INTERFACE ID = " + _interfaceID, "", (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_stage, "Check these values in INFile:: RETRY_COUNT_VAL/TOP :" + noOfRetries + " SLEEP_TIME_VAL/TOP ::" + retrySleep + " FIRST_SLEEP_TIME_VAL/TOP" + firstSleepStr);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " noOfRetries = " + noOfRetries + " retrySleep = " + retrySleep);
            // Here the logic is implemented to get the resposne from the
            // HashMap(outBucket)
            // In outBucket response is stored by the OutClass thread.
            // outBucket is checked for the response for a configured number of
            // times with configured time interval.

            // If firstSleep is defined into INFile then only thread will wait
            // before the first attempt
            try {
                if (firstSleep > 0)
                    Thread.sleep(firstSleep);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
            }
            for (i = 0; i < noOfRetries; i++) {
                try {

                    if (i > 0 || firstSleep == 0)
                        Thread.currentThread().sleep(retrySleep);
                    responseStr = outBucket.remove(transID).toString();
                    endTime = System.currentTimeMillis();
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "RETRY ATTEMPT NO. = " + i + ":::::transID: " + transID + " _interfaceID: " + _interfaceID + ":::::HASH MAP DATA in bucket= " + inBucket + " out bucket= " + outBucket + "TIME WHEN READ RESPONSE FROM ECRIC CLIENT = " + System.currentTimeMillis() + " responseStr :: " + responseStr);
                    busyBucket.remove(transID);
                } catch (Exception e) {
                    responseStr = null;
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                }
                if (responseStr != null)
                    break;
            }
            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (!InterfaceUtil.isNullString(warnTimeStr)) {
                try {
                    warnTime = Long.parseLong(warnTimeStr);
                } catch (Exception e) {
                    warnTime = 4000;
                }
                if (endTime - startTime > warnTime) {
                    _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EricssionINHandler[sendRequestToIN]", "INTERFACE_ID: " + _interfaceID, "", (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_stage, "Ericssion IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                }
            }
            // Check whether the response from the IN is NULL then do following.
            // 1.Remove the request string from inBucket
            // 2.Remove the outBucket from outBucketMap based on the
            // transactionID
            // 3.Remove the busyBucket based on the transaction Id.
            // 4.Mark the case as AMBIGUUOUS.
            TransactionLog.log(transID, referenceID, msisdn, (String) _requestMap.get("NETWORK_CODE"), p_stage, PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage + " retry attempt used = " + i);
            if (BTSLUtil.isNullString(responseStr)) {
                try {
                    inBucket.remove(p_inRequestStr);
                } catch (Exception e) {
                }
                try {
                    outBucket.remove(transID);
                } catch (Exception e) {
                }
                try {
                    busyBucket.remove(transID);
                } catch (Exception e) {
                }

                // reconciliation case
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, " INTERFACE_ID = " + _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_stage, "Blank response from Ericssion IN");
                _log.info("sendRequestToIN", "NULL response for interface");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // commented code may be used in future to support on line
                // cancel request
                /*
                 * if(ACTION_TXN_CANCEL == p_stage)
                 * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                 * AMBIGOUS);
                 */
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            // Constructing a response map from response string.
            InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");
            // Putting the _interfaceID to responseMap
            _responseMap.put("INTERFACE_ID", _interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "After populating responseMap from request string _responseMap = " + _responseMap);

            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
            }

            // Matching the requested transaction if with the responsed
            // trasaction id. if it does not matches, handle the event mark as
            // INVALID RESPONSE.
            // if(!((String)_responseMap.get("TransId")).equals(transID))
            if (!((String) _responseMap.get("TransId")).equals(transID)) {
                _log.info("sendRequestToIN", "transID:" + transID + " current TransId=" + (String) _responseMap.get("TransId") + " Mismatch");
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_stage, "transID:" + transID + " Responsed TransId=" + (String) _responseMap.get("TransId") + " Mismatch");
                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);
            }
            // Get the interface Status of the request
            String status = (String) _responseMap.get("Status");
            _requestMap.put("INTERFACE_STATUS", status);

            // Check the Ambiguous cases
            Object[] ambList = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "AMBIGUOUS_CASES")).split(",");
            if (InterfaceErrorCodesI.SUCCESS.equals(status)) {
                if (!EricssionValidation.validation(_responseMap)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_stage, "Status from the response  Status = " + status);
                    throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATION_ERROR);
                }// end of validation on responsed values.
            } else if ("404".equals(status)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), "MSISDN " + (String) _requestMap.get("MSISDN") + "is not found ", (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_stage, "Status from the response  Status = " + status);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            } else if (!InterfaceErrorCodesI.SUCCESS.equals(status) && Arrays.asList(ambList).contains(status)) {
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_stage, "Status from the response  Status = " + status);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            } else {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, (String) _requestMap.get("INTERFACE_ID"), EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_stage, "Status from the response  Status = " + status);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

        }// end of try-Block
        catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[sendRequestToIN]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE") + " INTERFACE_ID = " + _interfaceID + "Stage = " + p_stage, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_stage:" + p_stage + " responseStr:" + responseStr);
        }// end of finally
    }// end of sendRequestToIN

    /**
     * This method used to get the system language mapped in FileCache based on
     * the INLanguge.Includes following
     * If the Mapping key not defined in IN file handle the event as System
     * Error with level FATAL.
     * If the Mapping is not defined handle the event as SYSTEM INFO with level
     * MAJOR and set empty string.
     */
    private void setLanguageFromMapping(String p_interfaceID) {
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
            String mappingString = FileCache.getValue(p_interfaceID, "LANGUAGE_MAPPING");
            if (mappingString == null) {
                mappingString = "";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "EricssionINHandler[setLanguageFromMapping]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), " MAPPING_LANGUAGE is not defined in IN file");
            }
            langFromIN = (String) _responseMap.get("Language");
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
             // found,handle the event //Confirm in handler Transaction Id will
             // be or IN_TXN_ID
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "EricssionINHandler[setLanguageFromMapping]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[setLanguageFromMapping]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    } // End of equestIN method

    /**
     * This method is used to get interface specific values from FileCache(load
     * at starting)based on
     * interface id and set to the requested map.These parameters are
     * 1.service removal flag
     * 2.transaction_currency
     * 
     * @param String
     *            p_interfaceID
     * @throws Exception
     */
    private void setInterfaceParameters(String p_interfaceID) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_interfaceID =" + p_interfaceID);
        String serviceRemovalFlag = null;
        String transactionCurrency = null;
        String multFactor = null;
        try {
            serviceRemovalFlag = FileCache.getValue(p_interfaceID, "SERVICE_REMOVAL");
            if (serviceRemovalFlag == null || BTSLUtil.isNullString(serviceRemovalFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "EricssionINHandler[setInterfaceParameters]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "SERVICE_REMOVAL defined in INFile is =  " + serviceRemovalFlag);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            transactionCurrency = FileCache.getValue(p_interfaceID, "TransCurrency");
            if (transactionCurrency == null || BTSLUtil.isNullString(transactionCurrency)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "EricssionINHandler[setInterfaceParameters]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "TransCurrency defined in INFile is =  " + transactionCurrency);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Fetch the multiplication factor and check whether it is defined
            // or not in IN file.
            multFactor = FileCache.getValue(p_interfaceID, "MULTIPLICATION_FACTOR");
            if (multFactor == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[creditAdjust]", "MSISDN = " + (String) _requestMap.get("MSISDN") + "TransactionID = " + (String) _requestMap.get("TRANSACTION_ID"), " INTERFACE ID = " + p_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File :: MULTIPLICATION_FACTOR = " + multFactor);
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the multiplication factor in request map to convert the
            // amount.
            _requestMap.put("multiplication_factor", multFactor);
            _requestMap.put("service_removal", serviceRemovalFlag);
            _requestMap.put("transaction_currency", transactionCurrency);
            String cancelTxnAllowed = FileCache.getValue(p_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[setInterfaceParameters]", "MSISDN = " + (String) _requestMap.get("MSISDN") + "TransactionID = " + (String) _requestMap.get("TRANSACTION_ID"), " INTERFACE ID = " + p_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN " + (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN " + (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN " + (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelINHandler[setInterfaceParameters]", "", "INTERFACE ID" + _interfaceID + " MSISDN " + (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(p_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[setInterfaceParameters]", "MSISDN = " + (String) _requestMap.get("MSISDN") + "TransactionID = " + (String) _requestMap.get("TRANSACTION_ID"), " INTERFACE ID = " + p_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(p_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[setInterfaceParameters]", "MSISDN = " + (String) _requestMap.get("MSISDN") + "TransactionID = " + (String) _requestMap.get("TRANSACTION_ID"), " INTERFACE ID = " + p_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e=" + e + " Check the SERVICE_REMOVAL,TransCurrency in IN file");
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "EricssionINHandler[setLanguageFromMapping]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }// end of setInterfaceParameters

    /**
     * This method is used to get the interface amount ,multiplied by mult
     * factor.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws BTSLBaseException
     */
    private String getInterfaceAmount(HashMap p_map) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getInterfaceAmount", "Entered p_map = " + p_map);
        String interfaceAmountStr = null;
        String multFactor = null;
        long interfaceAmt = 0;
        try {
            interfaceAmt = Long.parseLong((String) p_map.get("INTERFACE_AMOUNT"));
            multFactor = (String) p_map.get("multiplication_factor");
            interfaceAmountStr = InterfaceUtil.getDisplayAmount(interfaceAmt, Integer.parseInt(multFactor));
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getInterfaceAmount", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionINHandler[getInterfaceAmount]", (String) p_map.get("TRANSACTION_ID"), (String) p_map.get("MSISDN"), (String) p_map.get("NETWORK_CODE"), "System Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getInterfaceAmount", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getInterfaceAmount", "Exiting interfaceAmountStr = " + interfaceAmountStr);
        }
        return interfaceAmountStr;
    }

    /**
     * Method to Check interface status before sending request.
     * 
     * @throws BTSLBaseException
     */
    private void checkInterfaceB4SendingRequest() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkInterfaceB4SendingRequest", "Entered _requestMap : " + _requestMap);

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
                if (_log.isDebugEnabled())
                    _log.debug("handleCancelTransaction", "reconciliationLogStr: " + reconciliationLogStr);
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
             * cancelRetryCount =
             * Integer.parseInt(FileCache.getValue(_interfaceID
             * ,"CNCL_RETRY_CNT"));
             * String inStr = _formatter.generateRequest(ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * initializeConnectionParameters(ACTION_TXN_CANCEL);
             * sendRequestToIN(inStr,ACTION_TXN_CANCEL);
             * }
             * catch(Exception e)
             * {
             * if(e.getMessage().trim().equals(InterfaceErrorCodesI.AMBIGOUS))
             * _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,
             * _requestMap);
             * cancelCommandStatus=(String)
             * _requestMap.get("CANCEL_RESP_STATUS");//this will be null if
             * unable to create connection
             * 
             * if(e.getMessage().trim().equals(InterfaceErrorCodesI.
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
             * if(e.getMessage().trim().equals(InterfaceErrorCodesI.AMBIGOUS) ||
             * cancelCommandStatus==null)
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
             * cancelTxnStatus=e.getMessage().trim();
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

    /*
     * public static void main(String[] args)
     * {
     * 
     * EricssionINHandler inhandler = null;
     * try
     * {
     * //Constants.load(
     * "C:\\abhijit\\workspace\\pretups\\WebRoot\\WEB-INF\\classes\\configfiles\\Constants.props"
     * );
     * //org.apache.log4j.PropertyConfigurator.configure(
     * "C:\\abhijit\\workspace\\pretups\\WebRoot\\WEB-INF\\classes\\configfiles\\LogConfig.props"
     * );
     * Constants.load("C:\\Constants.props");
     * org.apache.log4j.PropertyConfigurator.configure("C:\\LogConfig.props");
     * FileCache.loadAtStartUp();
     * ConfigServlet.loadProcessCache("C:\\Constants.props",
     * "C:\\ProcessLogConfig.props");
     * 
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * System.out.println("main Exception e="+e.getMessage());
     * }
     * HashMap _requestMap=new HashMap();
     * _requestMap.put("IN_TXN_ID","C586666");
     * _requestMap.put("INTERFACE_ID","INTID00012");
     * _requestMap.put("cp_id","TOPCPID1");
     * _requestMap.put("application","1");
     * _requestMap.put("INTERFACE_AMOUNT","1000");
     * _requestMap.put("VALIDITY_DAYS","5");
     * _requestMap.put("NETWORK_CODE","SN");
     * _requestMap.put("GRACE_DAYS","3");
     * _requestMap.put("op_transaction_id","45");
     * _requestMap.put("TRANSACTION_ID","33000");
     * 
     * //Adding the Type of account in the request map
     * _requestMap.put("CARD_GROUP_SELECTOR","1");
     * _requestMap.put("CARD_GROUP","ETopUpProfile");
     * _requestMap.put("MODULE","C2S");
     * 
     * //_requestMap.put("ADJUST","Y");
     * //Debit request parameters
     * _requestMap.put("INTERFACE_PREV_BALANCE","1500");
     * 
     * //Credit request parameters
     * _requestMap.put("BONUS_AMOUNT","2");
     * _requestMap.put("BONUS_VALIDITY_DAYS","1");
     * try
     * {
     * _requestMap.put("MSISDN","9868647394");
     * inhandler = new EricssionINHandler();
     * for(int count=0; count<100;count++)
     * {
     * inhandler.validate(_requestMap);
     * inhandler.credit(_requestMap);
     * inhandler.debitAdjust(_requestMap);
     * inhandler.creditAdjust(_requestMap);
     * }
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * System.out.println("main Exception e="+e.getMessage());
     * }
     * }
     */
}
