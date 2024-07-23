package com.inter.nokia;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.btsl.pretups.logging.TransactionLog;
import com.inter.pool.PoolManager;

/**
 * @(#)NokiaINHandler
 *                    Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                    All Rights Reserved
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Ashish Kumar Jan 5, 2007 Initial Creation
 *                    ----------------------------------------------------------
 *                    --------------------------------------
 */
public class NokiaINPoolHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog("NokiaINPoolHandler".getClass().getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private static long _counter = 0;
    private final long _MAX_VALUE = 99999;

    /**
     * validate Method is used for getting the account information of user
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_map:" + p_map);
        _requestMap = p_map;
        try {
            // Generate the IN transaction id
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "Multiplication factor defined in INFile[MULTIPLICATION_FACTOR] is " + multFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File ");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set the interface parameters to the request map.
            setInterfaceParameters();

            getINReconTxnID();

            // Sending the request to IN , Here we have to change INFrame work
            // while sending the request to IN
            sendRequestToIN(NokiaI.ACTION_ACCOUNT_INFO);

            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            // Set the balance after multiplied it with multiplication factor.
            String amountStr = (String) _responseMap.get("balance");

            String amount = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
            _requestMap.put("INTERFACE_PREV_BALANCE", amount);

            if (!InterfaceUtil.isNullString((String) _responseMap.get("profileId")))
                _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("profileId"));

            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NCEDate"), "dd-MM-yy"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NSEDate"), "dd-MM-yy"));
            // _requestMap.put("ACCOUNT_STATUS", (String)
            // _responseMap.get("acc_status"));

            Object[] rechargeAllowed = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "RECHARGE_ALLOWED")).split(",");
            if (!Arrays.asList(rechargeAllowed).contains(_responseMap.get("rechargeAllowed"))) {
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
            }

            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping();
        }// try
        catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// catch
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug(this, "validate", "Exited _requestMap=" + _requestMap);
        }// finally
    }// end of validate

    /**
     * This method is responsible for the credit of subscriber account.
     * 1.Interface specific parameters are set and added to the request map.
     * 2.For sending request to IN this method internally calls private method
     * sendRequestToIN
     * 3.Process the response.
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_map =" + p_map);
        _requestMap = p_map;
        double multFac = 0;
        try {
            // Generate the IN transaction id using the getINTransactionID()
            // method of InterfaceUtil class and
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");

            // Fetch the interface id from the request map.
            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor:" + multFactor);

            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "multFactor:" + multFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Set the interface parameters to the request map
            setInterfaceParameters();
            // Generate the INReconID and set it into the requestMap
            getINReconTxnID();

            double requestedAmount = 0;
            // Get the requested amount from the requestMap with key as
            // INTERFACE_AMOUNT and use the
            try {
                requestedAmount = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not a number");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the system amount in request map after deviding it to
            // multiplication factor.
            multFac = Double.parseDouble(multFactor.trim());
            double amountDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedAmount, multFac);
            String amountStr = String.valueOf(amountDouble);
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("credit", "roundFlag:" + roundFlag);
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if ("Y".equalsIgnoreCase(roundFlag.trim())) {
                // Round the interface amount using the round method of Math
                // class.
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            // Set the rounded amount into request map with key as
            // transfer_amount.
            _requestMap.put("transfer_amount", amountStr);
            _requestMap.put("MULTIPLICATION_FACTOR", multFactor);

            // sending the credit request to IN
            sendRequestToIN(NokiaI.ACTION_RECHARGE_CREDIT);

            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            // From the INFile decide whether the validity and expiry would be
            // obtained after credit.
            String balanceQuery = (String) _requestMap.get("balanceQuery");

            // Based on the value of balanceQuery set the
            // NEW_EXPIRY,NEW_GRACE_DATE,ACCOUNT_STATUS(if present-check) into
            // _requestMap from the responseMap.
            // Also check the value of PBLENQ in the _responseMap.
            String afterRechBalance = (String) _responseMap.get("PBLENQ");
            if (!"N".equals(afterRechBalance)) {
                // Set the post balance after multiplied by multiplication
                // factor.
                try {
                    String postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount((String) _responseMap.get("balance"), multFac);
                    _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
                } catch (Exception e) {
                    _log.error("credit", "Exception e:" + e.getMessage());
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile , Exception e:" + e.getMessage());
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NCEDate"), "dd-MM-yy"));
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NSEDate"), "dd-MM-yy"));
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            } else
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            _log.error("credit", "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }

    /**
     * debitAdjust Method is used for debit.
     * 
     * @param p_map
     *            HashMap
     * @throws Exception
     */
    public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_map =" + p_map);
        _requestMap = p_map;
        String multFactor = null;
        double multFac = 0;
        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetch the interface id from the request map. Get the
            // multiplication factor from the
            // FileCache with the help of interface id.
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("debitAdjust", "MULTIPLICATION_FACTOR is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFac = Double.parseDouble(multFactor.trim());

            setInterfaceParameters();
            getINReconTxnID();

            double requestedAmount = 0;
            // Get the requested amount from the requestMap with key as
            // INTERFACE_AMOUNT and use the
            try {
                requestedAmount = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "INTERFACE_AMOUNT is " + requestedAmount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not a number");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // getDisplayAmount method of InterfaceUtil class to get the system
            // amount
            double amountDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(requestedAmount, multFac);
            String amountStr = String.valueOf(amountDouble);

            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                _log.error("debitAdjust", "ROUND_FLAG is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("ROUND_FLAG", roundFlag);
            if ("Y".equalsIgnoreCase(roundFlag.trim())) {
                // Round the interface amount using the round method of Math
                // class
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            _requestMap.put("transfer_amount", amountStr);
            // key value of HashMap is formatted into XML string for the
            // debitAdjust request.
            // Sending the request to IN
            // Here we have to change INFrame work while sending the request to
            // IN
            // Eiether we use the method arguments as _requestMap and action
            // both or only Action(_requestMap is class level variable)
            sendRequestToIN(NokiaI.ACTION_IMMEDIATE_DEBIT);
            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // No balance is obtained in the debit response. Confirm whether to
            // calculate the
            // post balance of subscriber or set POST_BALANCE_ENQ_SUCCESS as N
            // into requestMap.
            String afterRechBalance = (String) _responseMap.get("PBLENQ");

            if (!"N".equals(afterRechBalance)) {
                // Set the post balance after multiplied by multiplication
                // factor.
                try {
                    String postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount((String) _responseMap.get("balance"), multFac);
                    _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                } catch (Exception e) {
                    _log.error("debitAdjust", "Exception e:" + e.getMessage());
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "debitAdjust balance from response is not numeric or multiplication factor defined in the INFile, Exception e:" + e.getMessage());
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
            } else
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            _log.error("debitAdjust", "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While debit get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }

    /**
     * This method is used to adjust the credit
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        try {
            // Call the credit method.
            credit(p_map);
        } catch (BTSLBaseException be) {
            _log.error("creditAdjust", "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), ",While creditAdjust get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap:" + _requestMap);
        }// end of finally
    }// end of creditAdjust

    /**
     * This method is responsible to send the request to IN.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    // public void sendRequestToIN(HashMap p_requestMap, int p_action) throws
    // BTSLBaseException//Only for testing since a HashMap is passed by
    // TestServlet
    public void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered  p_action:" + p_action);
        // In the transaction log for the request, requestMap would be logged
        // instead of requestString.
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, " INTERFACE_ID:" + _interfaceID + " Request Map:" + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "p_action=" + p_action);
        String responseStr = null;// Store the response from the IN for a
                                  // request
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        Object clientObject = null;
        Vector freeList = null;
        Vector busyList = null;
        NokiaClientMix nokiaClient = null;
        HashMap tempRequestMap = null;
        boolean isC2SModule = false;
        boolean isClientObjectFree = false;
        int retryConInval = 0;
        int retry = 0;// Used to count the no of retires.
        try {
            String inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // Fetch the no of retry count defined in the INFile, that would be
            // used to
            // send the request to IN if any error[Object invalidation]occurs.
            String retryConInvalidateStr = FileCache.getValue(_interfaceID, "RETRY_CON_INVAL");
            if (InterfaceUtil.isNullString(retryConInvalidateStr) || !InterfaceUtil.isNumeric(retryConInvalidateStr)) {
                // Hadle the event
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                // Hadle the event
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            warnTime = Long.parseLong(warnTimeStr);
            retryConInval = Integer.parseInt(retryConInvalidateStr);
            // Get the client object from the PoolManager
            if (InterfaceErrorCodesI.MODULE_C2S.equals(_requestMap.get("MODULE")))
                isC2SModule = true;
            freeList = (Vector) PoolManager._freeBucket.get(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Corresponding to interfaceID[" + _interfaceID + "] freeList::" + freeList);
            // Get the busy list corresponding to interface id and add the the
            // client object to busylist.
            busyList = (Vector) PoolManager._busyBucket.get(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Corresponding to interfaceID[" + _interfaceID + "] freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
            // Getting the client object for C2S module(Module decide the sleep
            // time and number of time to which
            // Thread would sleep to get the Client object from the
            // PoolManager).
            clientObject = PoolManager.getClientObject(_interfaceID, isC2SModule);
            if (!(clientObject instanceof NokiaClientMix)) {
                _log.error("sendRequestToIN", "In the InFile instead of defining [" + NokiaClient.class.getName() + "] client name is defined as[" + clientObject.getClass().getName() + "]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "In the INFile Client class path is not defined for NokiaClient]");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            // [Down]Cast the client object as per required class.
            nokiaClient = (NokiaClientMix) clientObject;
            try {
                // A new Temp request from _requestMap is created that would be
                // send to the IN
                // Because at the client implementation the parameter value
                // would extracted to assign those as method args.
                tempRequestMap = new HashMap(_requestMap);
                // Get the start time whent the request is send to IN and put
                // into requestMap.
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "startTime :" + startTime);
                for (retry = 0; retry <= retryConInval; retry++) {
                    try {
                        // Based on the action value call the various method of
                        // NokiaClient.
                        responseStr = nokiaClient.sendRequest(tempRequestMap, p_action);
                        if (_log.isDebugEnabled())
                            _log.debug("sendRequestToIN", "responseStr::" + responseStr + " at retry attempt number:" + retry);
                        // Log the number of retry attempt to sent the
                        // successfull request.
                        endTime = System.currentTimeMillis();
                        break;
                    } catch (BTSLBaseException be) {
                        // Confirm in the case while sending the request we got
                        // the excpetion like Access Denied
                        // 1. whether we need to get new Client object and
                        // continue to retry
                        // 2. Or throwing BTSLBaseException fail the trasaction.
                        _log.error("sendRequestToIN", "BTSLBaseException e:" + be.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Exception while sending request to Nokia IN CLient :" + be.getMessage());
                        throw be;
                    } catch (Exception e) {
                        _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                        e.printStackTrace();
                        if (retry >= retryConInval) {
                            // Handle the event.
                            _log.error("sendRequestToIN", "Retry Attempted Exceeded Failing Request");
                            throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                        }
                        // Get a new Connection from pool manager and add the
                        // object into freeBucket
                        try {
                            _log.error("sendRequestToIN", "Trying replaing of connection try=" + retry);
                            // Thread may sleep for the configured time,if the
                            // connection is invalid.
                            // Before getting the new client object destroy the
                            // old one.
                            if (nokiaClient != null)
                                nokiaClient.destroy();
                            Object tempClientObject = PoolManager.getNewClientObject(_interfaceID);
                            busyList.remove(clientObject);// Remove the old
                                                          // client object
                            clientObject = tempClientObject;
                            busyList.add(clientObject);
                            nokiaClient = (NokiaClientMix) clientObject;
                            continue;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            _log.error("sendRequestToIN", "Exception e:" + e1.getMessage());
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Exception while Retrying :" + e1.getMessage());
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
                        }
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Number of retry attempt done to get the response ::" + retry);
                // If warn time in the IN file exist and the difference of start
                // and end time of writing the request
                // and reading the response is greater than the Warn time log
                // the info and handle the event.
                if (endTime - startTime > warnTime) {
                    _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Nokia IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                }
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE_ID = " + _interfaceID + "Stage = " + p_action, "Exception:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } finally {
                if ((busyList != null && freeList != null)) {
                    busyList.remove(clientObject);
                    freeList.add(clientObject);
                    isClientObjectFree = true;
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "After the completion of request of referenceID::" + _referenceID + " size of freeList:[" + freeList.size() + "] and size of busyList:[" + busyList.size() + "]");
                }
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "IN_END_TIME :" + endTime);
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            }
            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, " INTERFACE_ID:" + _interfaceID + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
            // Check for the Null response from Nokia IN.
            if (InterfaceUtil.isNullString(responseStr)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Blank response from Ericssion IN");
                _log.info("sendRequestToIN", "NULL response for interface");
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            _responseMap = new HashMap();
            InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");
            // After getting the Client object send the request
            String status = (String) _responseMap.get("statusInd");
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Status from the interface::" + status);
            _requestMap.put("INTERFACE_STATUS", status);
            // Check the Ambiguous cases
            Object[] ambList = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "AMBIGUOUS_CASES")).split(",");
            if (!NokiaI.RESULT_OK.equals(status) && Arrays.asList(ambList).contains(status)) // Confirm
                                                                                             // whether
                                                                                             // Amb
                                                                                             // cases
                                                                                             // would
                                                                                             // be
                                                                                             // present
                                                                                             // in
                                                                                             // the
                                                                                             // INFile.
            {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Status from the response  Status = " + status);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            } else if (!NokiaI.RESULT_OK.equals(status)) {
                if (NokiaI.SUBSCRIBER_NOT_FOUND.equals(status)) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Subscriber is not defined on the IN");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINPoolHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE_ID = " + _interfaceID + "Stage = " + p_action, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            // if(_log.isDebugEnabled())
            // _log.debug("sendRequestToIN","Exiting p_stage:"+p_stage+" responseStr:"+responseStr);
            // At the end of request-response processing(Successfull or
            // Fail),Client from busyList and added to the freeList.
            if (!isClientObjectFree && (busyList != null && freeList != null)) {
                busyList.remove(clientObject);
                freeList.add(clientObject);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "After the completion of request of referenceID::" + _referenceID + " size of freeList:[" + freeList.size() + "] and size of busyList:[" + busyList.size() + "]");
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting");
        }// end of finally
    }// end of sendRequestToIN

    /**
     * This method is used to get interface specific values from FileCache(load
     * at starting)based on
     * interface id and set to the requested map.These parameters are
     * 1.FIRST_FLAG
     * 2.APPLICATION
     * 3.BALANCE_QUERY
     * 
     * @param String
     *            p_interfaceID
     * @throws Exception
     */
    private void setInterfaceParameters() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            // Set the interface parameters to the request map, these are as
            // bellow
            // fetch the value of the FIRST_FLAG from the INFile and set into
            // requestMap.
            // fetch the value of the APPLICATION from the INFile and set into
            // requestMap.
            // fetch the value of BALANCE_QUERY from the INFile and set into
            // requestMap.
            String firstCallFlag = FileCache.getValue(_interfaceID, "FIRST_FLAG");
            if (InterfaceUtil.isNullString(firstCallFlag)) {
                _log.error("setInterfaceParameters", "Value of FIRST_FLAG is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "TRANS_CURRENCY is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("FIRST_FLAG", firstCallFlag.trim());
            String applicationId = FileCache.getValue(_interfaceID, "APPLICATION");
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "applicationId:" + applicationId);
            if (InterfaceUtil.isNullString(applicationId)) {
                _log.error("setInterfaceParameters", "Value of APPLICATION is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "APPLICATION is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("applicationId", applicationId.trim());
            String balanceQuery = FileCache.getValue(_interfaceID, "BALANCE_QUERY");
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "balanceQuery:" + balanceQuery);
            if (InterfaceUtil.isNullString(balanceQuery)) {
                _log.error("setInterfaceParameters", "Value of BALANCE_QUERY is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "BALANCE_QUERY is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("balanceQuery", balanceQuery.trim());
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
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
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString::" + mappingString);
            if (InterfaceUtil.isNullString(mappingString)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LANGUAGE_MAPPING is not defined in IN file");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            langFromIN = (String) _responseMap.get("subscriberLanguage");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "langFromIN::" + langFromIN);
            if (InterfaceUtil.isNullString(langFromIN)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Subscriber's language is not recieved from the IN.");
                return;
            }
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINPoolHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

    /**
     * Get IN Reconciliation Txn ID
     * 
     * @param void
     * @return String
     * @throws Exception
     */
    private void getINReconTxnID() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Enetered ");
        String inReconID = null;
        try {
            /*
             * String userType=(String)_requestMap.get("USER_TYPE");
             * if(userType!=null)
             * inReconID=
             * ((String)_requestMap.get("TRANSACTION_ID")+"."+userType);
             * else
             * inReconID= ((String)_requestMap.get("TRANSACTION_ID"));
             */
            if (_counter >= _MAX_VALUE)
                _counter = 0;
            _requestMap.put("IN_RECON_ID", String.valueOf(_counter));
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

    public void validityAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {

    }
}
