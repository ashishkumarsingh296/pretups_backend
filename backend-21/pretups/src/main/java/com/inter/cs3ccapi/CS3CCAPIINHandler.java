package com.inter.cs3ccapi;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
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
import com.inter.pool.PoolManager;

/**
 * @CS3CCAPIINHandler.java
 *                         Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Ashish K Jan 31, 2007 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 *                         This class is the Handler class for the CS3-Ericssion
 *                         interface
 */
public class CS3CCAPIINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the respose of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private static CS3CCAPIRequestFormatter _formatter = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    static {
        _formatter = new CS3CCAPIRequestFormatter();
    }

    /**
     * This method would be used to validate the subscriber's account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap::" + p_requestMap);
        _requestMap = p_requestMap;
        String multFactor = null;
        try {
            // Fetch the interface id from the request map.
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            // If validation of subscriber based on the key defined in the
            // INFile,is not required set the SUCCESS code into request map and
            // return.
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            _inTXNID = InterfaceUtil.getINTransactionID();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor.trim())) {
                _log.error("validate", "Multiplication factor defined in INFile[MULTIPLICATION_FACTOR] is " + multFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[validate]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR is either not defined in IN File or it is not NUMERIC");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();
            // generate req.
            String inStr = _formatter.generateRequest(CS3CCAPII.ACTION_ACCOUNT_INFO, _requestMap);
            // Sending the request to IN
            sendRequestToIN(inStr, CS3CCAPII.ACTION_ACCOUNT_INFO);
            // Set the value of INTERFACE_PREV_BALANCE as the credit_balance of
            // responseMap
            // after multiplying with the multiplication factor.
            String amountStr = (String) _responseMap.get("credit_balance");
            if (!InterfaceUtil.isNullString(amountStr)) {
                try {
                    double currAmount = Double.parseDouble(amountStr);
                    long amount = InterfaceUtil.getSystemAmount(currAmount, Integer.parseInt(multFactor));
                    _requestMap.put("INTERFACE_PREV_BALANCE", String.valueOf(amount));
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("validate", "Exception e=" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[validate]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the response is not Numeric, while parsing the balance get Exception e= " + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
            }
            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("service_class"));
            // Get the value of end_val_date from the response, change its
            // format as per the interface and set it into requestMap with key
            // as OLD_EXPIRY_DATE.
            String oldExpiryDate = (String) _responseMap.get("end_val_date");
            if (!InterfaceUtil.isNullString(oldExpiryDate))
                _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(oldExpiryDate, "yyyyMMdd"));
            // Get the value of end_inact_date from the response, change its
            // format as per the interface and set it into requestMap with key
            // as OLD_GRACE_DATE.
            String oldGraceDate = (String) _responseMap.get("end_inact_date");
            if (!InterfaceUtil.isNullString(oldGraceDate))
                _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(oldGraceDate, "yyyyMMdd"));
            // Get the value of acc_status from the response and set it into
            // requestMap with key as ACCOUNT_STATUS
            String accountStatus = (String) _responseMap.get("account_staus");
            if (!InterfaceUtil.isNullString(accountStatus))
                _requestMap.put("ACCOUNT_STATUS", accountStatus);
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[validate]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get Exception e=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap::" + _requestMap);
        }
    }// end of validate

    /**
     * This method would be used to credit the subscriber's account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap::" + p_requestMap);
        _requestMap = p_requestMap;
        long interfaceAmountLong = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // Generate the IN transaction id store into request map with key as
            // IN_TXN_ID
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor.trim())) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[credit]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR is either not defined in IN File or it is not NUMERIC");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Only new balance would be devided by RECH_RESP_DIV_FACTOR
            String rechargeRespDivFactor = (String) FileCache.getValue(_interfaceID, "RECH_RESP_DIV_FACTOR");
            if (InterfaceUtil.isNullString(rechargeRespDivFactor)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[credit]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "RECH_RESP_DIV_FACTOR is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            rechargeRespDivFactor = rechargeRespDivFactor.trim();
            // Set the interface parameters
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            String interfaceAmount = (String) _requestMap.get("INTERFACE_AMOUNT");
            try {
                interfaceAmountLong = Long.parseLong(interfaceAmount);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e=" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[credit]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Interface Amount is not valid, while parsing balance get Exception e = " + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(interfaceAmountLong, Integer.parseInt(multFactor));
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("credit", "From file cache roundFlag = " + roundFlag + " multFactor = " + multFactor);
            // If the ROUND_FLAG is not defined in the INFile
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "N";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3EricssionINHandlers[credit]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
            }
            // If rounding of amount is allowed, round the amount value and put
            // this value in request map.
            if ("Y".equals(roundFlag.trim())) {
                double amountDouble = Double.parseDouble(amountStr);
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            // Put the amount in request map with key as transfer_amuont, it
            // would not be converted into display amount as per confirmation
            // from IN.
            _requestMap.put("transfer_amount", amountStr);
            // Generate the request from the CS3CCAPIRequestFormatter class.
            String inStr = _formatter.generateRequest(CS3CCAPII.ACTION_RECHARGE_CREDIT, _requestMap);
            // Send the request to IN.
            sendRequestToIN(inStr, CS3CCAPII.ACTION_RECHARGE_CREDIT);
            // On successful transaction, set the TRANSACTION_STATUS as SUCCES
            // into request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Set the value of INTERFACE_POST_BALANCE as the credit_balance of
            // responseMap after multiplying with the RECH_RESP_DIV_FACTOR.
            String postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount((String) _responseMap.get("credit_balance"), Double.parseDouble(rechargeRespDivFactor));
            _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);

            // Set the value of NEW_EXPIRY_DATE as the value of end_val_date
            // from responseMap, after converting the format as per interface
            String newExpDate = (String) _responseMap.get("end_val_date");
            if (!InterfaceUtil.isNullString(newExpDate))
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(newExpDate, "yyyyMMdd"));
            // Get the value of end_inact_date from the response, change its
            // format as per the interface and set it into requestMap with key
            // as NEW_GRACE_DATE
            String newGraceDate = (String) _responseMap.get("end_inact_date");
            if (!InterfaceUtil.isNullString(newGraceDate))
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(newGraceDate, "yyyyMMdd"));
            // Get the value of account_status from the response and set it into
            // requestMap with key as ACCOUNT_STATUS.
            String accountStatus = (String) _responseMap.get("account_staus");
            if (!InterfaceUtil.isNullString(accountStatus))
                _requestMap.put("ACCOUNT_STATUS", accountStatus);
            // Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[credit]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Exception while credit Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exiting _requestMap::" + _requestMap);
        }// end of finally
    }// end of credit

    /**
     * This method would be used to adjust the credit of subscriber account at
     * the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_requestMap::" + p_requestMap);
        _requestMap = p_requestMap;
        int multFactorInt = 0;
        long interfaceAmount = 0;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            // Generate the IN transaction id store into request map with key as
            // IN_TXN_ID
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor.trim())) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is either not defined in IN File or it is not NUMERIC");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters to the requestMap
            setInterfaceParameters();
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            // Get the requested amount from the requestMap with key as
            // INTERFACE_AMOUNT and use the getDisplayAmount method of
            // InterfaceUtil class to get the system amount
            multFactorInt = Integer.parseInt(multFactor);
            try {
                interfaceAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e=" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(interfaceAmount, multFactorInt);
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "From file cache roundFlag = " + roundFlag + " multFactor = " + multFactor);
            // If the ROUND_FLAG is not defined in the INFile
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "N";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3CCAPIINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
            }
            // If rounding of amount is allowed, round the amount value and put
            // this value in request map.
            if ("Y".equals(roundFlag.trim())) {
                double amountDouble = Double.parseDouble(amountStr);
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            _requestMap.put("transfer_amount", amountStr);
            _requestMap.put("ADJUST_ACTION", "ADD");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "In the requestMap, ADJUST_ACTION is set as ADD" + " transfer_amount=" + amountStr);
            String inStr = _formatter.generateRequest(CS3CCAPII.ACTION_IMMEDIATE_DEBIT, _requestMap);
            sendRequestToIN(inStr, CS3CCAPII.ACTION_IMMEDIATE_DEBIT);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            String accountStatus = (String) _responseMap.get("account_staus");
            if (!InterfaceUtil.isNullString(accountStatus))
                _requestMap.put("ACCOUNT_STATUS", accountStatus);
            try {
                double postBalanceLong = Double.parseDouble((String) _responseMap.get("credit_balance"));
                long postBalanceSystemAmount = InterfaceUtil.getSystemAmount(postBalanceLong, multFactorInt);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceSystemAmount));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e=" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Subscriber Balance obtained from IN is not Numeric, while parsing balance get Exception e = " + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exiting _requestMap:: " + _requestMap);
        }// end of finally
    }// end of creditAdjust

    /**
     * This method would be used to adjust the debit of subscriber account at
     * the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap::" + p_requestMap);
        _requestMap = p_requestMap;
        int multFactorInt = 0;
        long interfaceAmount = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor.trim())) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                multFactorInt = Integer.parseInt(multFactor);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e= " + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR defined in the INFile is not Numeric");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                interfaceAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e=" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(interfaceAmount, multFactorInt);
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "From file cache roundFlag=" + roundFlag + " multFactor=" + multFactor);
            // If the ROUND_FLAG is not defined in the INFile
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "N";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3INHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
            }
            // If rounding of amount is allowed, round the amount value and put
            // this value in request map.
            if ("Y".equals(roundFlag.trim())) {
                double amountDouble = Double.parseDouble(amountStr);
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            // Put the amount in request map with key as transfer_amuont
            _requestMap.put("transfer_amount", amountStr);
            // Set the value of CDADJUST=D into the request map
            _requestMap.put("ADJUST_ACTION", "SUBTRACT");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "In the requestMap, ADJUST_ACTION is set as SUBSTRACT, transfer_amount=" + amountStr + " transfer_amount", amountStr);
            // Generate the request from the CS3CCAPIRequestFormatter class.
            String inStr = _formatter.generateRequest(CS3CCAPII.ACTION_IMMEDIATE_DEBIT, _requestMap);
            // Send the request to IN.
            sendRequestToIN(inStr, CS3CCAPII.ACTION_IMMEDIATE_DEBIT);
            // On successful transaction, set the TRANSACTION_STATUS as SUCCES
            // into request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Get the value of account_status from the response and set it into
            // requestMap with key as ACCOUNT_STATUS.
            String accountStatus = (String) _responseMap.get("account_staus");
            if (!InterfaceUtil.isNullString(accountStatus))
                _requestMap.put("ACCOUNT_STATUS", accountStatus);
            // Set the value of INTERFACE_POST_BALANCE as the credit_balance of
            // responseMap after multiplying with the multiplication factor.
            try {
                double postBalanceLong = Double.parseDouble((String) _responseMap.get("credit_balance"));
                long postBalanceSystemAmount = InterfaceUtil.getSystemAmount(postBalanceLong, multFactorInt);
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceSystemAmount));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e=" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the balance get Exception =" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // Set the POST_BALANCE_ENQ_SUCCESS equal to N into requestMap
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
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
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Exception while debitAdjust Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exiting _requestMap:: " + _requestMap);
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
     * Functionality of the method is to accept the request string and the
     * action as argument.
     * Reuqest is send to IN.
     * Based on the response code, decides whether the transaction is success
     * full or not.
     * 
     * @param String
     *            p_requestStr
     * @param int p_action
     * @return void
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(String p_requestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "p_requestStr=" + p_requestStr + " p_action=" + p_action);
        // Put the request string, action, interface id, network code in the
        // Transaction log.
        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, p_requestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
        long startTime = 0, endTime = 0, sleepTime, warnTime = 0;
        String responseStr = null, retryNumberStr = null, sleepTimeStr = null;
        int retryNumber = 0;
        CS3CCAPISocketWrapper socketWrapper = null;
        StringBuffer responseBuffer = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Vector freeList = null;
        Vector busyList = null;
        long sleepBeforeRespRead = 0;
        boolean isConnectionFree = false;
        try {
            retryNumberStr = FileCache.getValue(_interfaceID, "RETRY_CON_INVAL");
            // Value of retry would checked for NULL and Number //Check the
            // isNumeric method, it return true if string is null
            if (InterfaceUtil.isNullString(retryNumberStr) || !InterfaceUtil.isNumeric(retryNumberStr.trim())) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "RETRY_CON_INVAL is not defined in IN File");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Fetch the sleep time from the INFile, which is described under
            // the key as SLEEP_CON_INVAL
            sleepTimeStr = FileCache.getValue(_interfaceID, "SLEEP_CON_INVAL");
            // Value of retry would checked for NULL
            if (InterfaceUtil.isNullString(sleepTimeStr) || !InterfaceUtil.isNumeric(sleepTimeStr.trim()))// Check
                                                                                                          // the
                                                                                                          // isNumeric
                                                                                                          // method,
                                                                                                          // it
                                                                                                          // return
                                                                                                          // true
                                                                                                          // if
                                                                                                          // string
                                                                                                          // is
                                                                                                          // null
            {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "SLEEP_CON_INVAL is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            String sleepBefReadRespStr = FileCache.getValue(_interfaceID, "SLEEP_RESP_READ");
            if (InterfaceUtil.isNullString(sleepBefReadRespStr) || !InterfaceUtil.isNumeric(sleepBefReadRespStr.trim()))// Check
                                                                                                                        // the
                                                                                                                        // isNumeric
                                                                                                                        // method,
                                                                                                                        // it
                                                                                                                        // return
                                                                                                                        // true
                                                                                                                        // if
                                                                                                                        // string
                                                                                                                        // is
                                                                                                                        // null
            {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "SLEEP_RESP_READ is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            retryNumber = Integer.parseInt(retryNumberStr.trim());
            sleepTime = Long.parseLong(sleepTimeStr.trim());
            sleepBeforeRespRead = Long.parseLong(sleepBefReadRespStr.trim());
            // Fetch the socket connection from the PoolManager by providing the
            // interface id
            try {
                // Get new object from the PoolManager (on the basis of module)
                boolean isC2S = false;
                if (((String) _requestMap.get("MODULE")).equals("C2S"))
                    isC2S = true;
                socketWrapper = (CS3CCAPISocketWrapper) PoolManager.getClientObject(_interfaceID, isC2S);
                // Making the reference of busy and freeList
                freeList = (Vector) PoolManager._freeBucket.get(_interfaceID);
                busyList = (Vector) PoolManager._busyBucket.get(_interfaceID);
                // In creditAdjust (sender credit back )don't check interface
                // status, simply send the request to IN.
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                    _isSameRequest = true;
                    checkInterfaceB4SendingRequest();
                }
            } catch (BTSLBaseException be) {
                _log.error("sendRequestToIN", "BTSLBaseException be:" + be.getMessage());
                throw be;
            }
            // Get the start time when the request is send to IN.
            startTime = System.currentTimeMillis();
            try {
                for (int i = 0; i < retryNumber; i++) {
                    try {
                        out = socketWrapper.getPrintWriter();
                        // send Request String to IN
                        _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                        out.println(p_requestStr);
                        out.flush();
                        if (_log.isDebugEnabled())
                            _log.debug("sendRequestToIN", "Request has been sent to IN");
                        // break;
                    } catch (Exception e) {
                        // e.printStackTrace();
                        if (i + 1 >= retryNumber) {
                            _log.error("sendRequestToIN", "Error while writing on output stream.");
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3CCAPIINHandler[sendRequestToIN]", _interfaceID, "", "", "Number of retry reached to MAX" + _interfaceID);
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                        // Remove the corresponding object from the busy list
                        // Add the new object in the Pool.
                        try {
                            e.printStackTrace();// for testing
                            _log.error("sendRequestToIN", "sending request if failed hence retrying to send the request retryAttempt:" + i);
                            // Get a new client object from the pool.
                            if (socketWrapper != null)
                                socketWrapper.destroy();
                            CS3CCAPISocketWrapper socketWrapperTemp = (CS3CCAPISocketWrapper) PoolManager.getNewClientObject(_interfaceID);
                            // Remove the previous socket from the busy list
                            busyList.remove(socketWrapper);
                            socketWrapper = socketWrapperTemp;
                            // Add the new socket in the busy list.
                            busyList.add(socketWrapper);
                        } catch (BTSLBaseException be) {
                            _log.error("sendRequestToIN", "Exception e=" + e.getMessage());
                            throw be;
                        }
                        // Each retry would take a sleep time
                        Thread.sleep(sleepTime);
                        continue;
                    }
                    // }//for
                    // INTERNAL-Confirm for retry attempt to read the response.
                    try {
                        int c = 0;
                        // Before reading the response from IN, it should wait
                        // for configured time.
                        if (_log.isDebugEnabled())
                            _log.debug("sendRequestToIN", "reading response from IN..");
                        Thread.sleep(sleepBeforeRespRead);
                        in = socketWrapper.getBufferedReader();
                        responseBuffer = new StringBuffer(1028);
                        while ((c = in.read()) != -1) {
                            responseBuffer.append((char) c);
                            if (c == 59)
                                break;
                        }
                        endTime = System.currentTimeMillis();
                        responseStr = responseBuffer.toString();
                        if (_log.isDebugEnabled())
                            _log.debug("sendRequestToIN", "Response from IN is responseStr:" + responseStr);
                    } catch (Exception e) {
                        // While reading response if we get the connection read
                        // time out, mark that transaction AMBIGUOUS
                        // and replace that old connection with the new One.
                        try {
                            e.printStackTrace();
                            _log.error("sendRequestToIN", "Reading response is failed, creating new socket connection and removing the older one from pool");
                            if (socketWrapper != null)
                                socketWrapper.destroy();
                            CS3CCAPISocketWrapper socketWrapperTemp = (CS3CCAPISocketWrapper) PoolManager.getNewClientObject(_interfaceID);
                            // Remove the previous socket from the busy list
                            busyList.remove(socketWrapper);
                            socketWrapper = socketWrapperTemp;
                            // Add the new socket in the busy list.
                            busyList.add(socketWrapper);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            _log.error("sendRequestToIN", "While getting new Socket connection Exception e=" + e.getMessage());
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            // commented code may be used in future to support
                            // on line cancel request
                            /*
                             * if(CS3CCAPII.ACTION_TXN_CANCEL == p_stage)
                             * _requestMap.put("CANCEL_RESP_STATUS",
                             * InterfaceErrorCodesI.AMBIGOUS);
                             */
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);

                            // Confirm for ALARM
                        }
                        _log.error("sendRequestToIN", "Exception e=" + e.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "While reading the response get Exception e=" + e.getMessage());
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        // commented code may be used in future to support on
                        // line cancel request
                        /*
                         * if(CS3CCAPII.ACTION_TXN_CANCEL == p_stage)
                         * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI
                         * .AMBIGOUS);
                         */
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
                    }
                    if (InterfaceUtil.isNullString(responseStr)) {
                        _log.error("sendRequestToIN", "No exception is occured but IN Response responseStr :" + responseStr + " creating new socket connection and removing the older one from pool and resending the request");
                        try {
                            if (socketWrapper != null)
                                socketWrapper.destroy();
                            CS3CCAPISocketWrapper socketWrapperTemp = (CS3CCAPISocketWrapper) PoolManager.getNewClientObject(_interfaceID);
                            // Remove the previous socket from the busyList
                            busyList.remove(socketWrapper);
                            socketWrapper = socketWrapperTemp;
                            // Add the new socket in the busy list.
                            busyList.add(socketWrapper);
                            isConnectionFree = false;
                            continue;
                        } catch (BTSLBaseException be) {
                            _log.error("sendRequestToIN", "Error occured when trying to get new client object in case of  response received is null");
                            throw be;
                        } catch (Exception e) {
                            _log.error("sendRequestToIN", "Exception e=" + e.getMessage());
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error occured when trying to get new client object in case of  response received is null and get Exception e=" + e.getMessage());
                            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_GETTIN_NEW_CLIENT_OBJECT);
                        }

                        /*
                         * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE
                         * ,EventComponentI.INTERFACES,EventStatusI.RAISED,
                         * EventLevelI
                         * .FATAL,"CS3CCAPIINHandler[sendRequestToIN]"
                         * ,"REFERENCE ID = "
                         * +_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "
                         * +_interfaceID,"Network code = "+(String)
                         * _requestMap.get
                         * ("NETWORK_CODE")+" Action = "+p_action,
                         * "Response String found null");
                         * _log.error("sendRequestToIN",
                         * "Response from the IN is null");
                         * throw new
                         * BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                         */
                    }
                    break;
                }
            } finally {
                busyList.remove(socketWrapper);
                freeList.add(socketWrapper);
                isConnectionFree = true;
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());

                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);

            }

            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
            // End time would be stored into request map with
            // key as IN_END_TIME as soon as the response of the request is
            // fetched from the IN.
            // Difference of start and end time would be compared against the
            // warn time,
            // if request and response takes more time than that of the warn
            // time,
            // an event with level INFO is handled
            // to discuss
            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            warnTime = Long.parseLong(warnTimeStr);
            if (endTime - startTime > warnTime) {
                _log.info("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Ericssion IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
            }
            /*
             * if(InterfaceUtil.isNullString(responseStr))
             * {
             * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
             * EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,
             * "CS3CCAPIINHandler[sendRequestToIN]"
             * ,"REFERENCE ID = "+_referenceID
             * +"MSISDN = "+_msisdn,"INTERFACE ID = "
             * +_interfaceID,"Network code = "+(String)
             * _requestMap.get("NETWORK_CODE"
             * )+" Action = "+p_action,"Response String found null");
             * _log.error("sendRequestToIN","Response from the IN is null");
             * _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
             * 
             * //commented code may be used in future to support on line cancel
             * request
             * if(CS3CCAPII.ACTION_TXN_CANCEL == p_stage)
             * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.AMBIGOUS
             * );
             * 
             * throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
             * }
             */
            // parse the response message by using CS3CCAPIRequestFormatter and
            // fetch the execution status.
            _responseMap = _formatter.parseResponse(p_action, responseStr);
            String status = (String) _responseMap.get("response_status");
            _requestMap.put("INTERFACE_STATUS", status);

            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
            }

            // commented code may be used in future to support on line cancel
            // request
            // If response of cancel request is Successful, then throw exception
            // mapped in IN FILE
            /*
             * if(CS3CCAPII.ACTION_TXN_CANCEL == p_stage)
             * {
             * _requestMap.put("CANCEL_RESP_STATUS",result);
             * cancelTxnStatus =
             * InterfaceUtil.getErrorCodeFromMapping(_requestMap
             * ,result,"SYSTEM_STATUS_MAPPING");
             * throw new BTSLBaseException(cancelTxnStatus);
             * }
             */

            // If the status is Not OK, exception with error code as
            // RESPONSE_ERROR is thrown.
            if (!CS3CCAPII.RESULT_OK.equals(status)) {
                // Check the status whether the subscriber's msisdn defined in
                // the IN
                if (CS3CCAPII.SUBSCRIBER_NOT_FOUND.equals(status)) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CS3CCAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Subscriber MSISDN is not found in the IN");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
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
            if (!isConnectionFree && (busyList != null && freeList != null)) {
                busyList.remove(socketWrapper);
                freeList.add(socketWrapper);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action=" + p_action + " responseStr=" + responseStr);
        }// end of finally
    }// end of sendRequestToIN

    /**
     * This method is used to get interface specific values from FileCache,
     * based on
     * interface id and set to the requested map.These parameters are
     * 1.User Name
     * 2.password
     * 3.currency
     * 
     * @throws BTSLBaseException
     */
    private void setInterfaceParameters() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        String userName = null;
        String password = null;
        String currency = null;
        try {
            userName = FileCache.getValue(_interfaceID, "USER_NAME");
            if (InterfaceUtil.isNullString(userName)) {
                _log.error("setInterfaceParameters", "USER_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "USER_NAME is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("USER_NAME", userName.trim());
            password = FileCache.getValue(_interfaceID, "PASSWORD");
            if (InterfaceUtil.isNullString(password)) {
                _log.error("setInterfaceParameters", "PASSWORD is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("PASSWORD", password.trim());
            currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                _log.error("setInterfaceParameters", "CURRENCY is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("CURRENCY", currency.trim());
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CCAPIINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited::userName = " + userName + ",password = " + password + ",currency = " + currency);
        }
    }// end of setInterfaceParameters

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
             * _formatter.generateRequest(CS3CCAPII.ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,CS3CCAPII.ACTION_TXN_CANCEL);
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
