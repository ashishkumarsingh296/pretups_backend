package com.inter.nokia;

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
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
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
public class NokiaINHandlerPP implements InterfaceHandler {
    private Log _log = LogFactory.getLog("NokiaINHandlerPP".getClass().getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    // private boolean _isRetryRequest=false;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

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
        double currAmount = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // Generate the IN transaction id
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor)) {
                _log.error("validate", "Multiplication factor defined in INFile[MULTIPLICATION_FACTOR] is " + multFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is either not defined in IN File or not NUMERIC");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set the interface parameters to the request map.
            setInterfaceParameters();
            // Generate the INReconID
            getINReconTxnID();

            // Sending the request to IN
            // Here we have to change INFrame work while sending the request to
            // IN
            // Eiether we use the method arguments as _requestMap and action
            // both or only Action(_requestMap is class level variable)
            sendRequestToIN(NokiaI.ACTION_ACCOUNT_INFO);

            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            // Set the balance after multiplied it with multiplication factor.
            // Set the value of INTERFACE_PREV_BALANCE as the credit_balance of
            // responseMap after multiplying with
            // the multiplication factor.
            String amountStr = (String) _responseMap.get("balance");

            try {
                currAmount = Double.parseDouble(amountStr);
            } catch (Exception e) {
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[validate]", _referenceID, "INTERFACE_ID:" + _interfaceID + " MSISDN" + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "credit_balance obtained from response is not numeric, Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            long amount = InterfaceUtil.getSystemAmount(currAmount, Integer.parseInt(multFactor));
            _requestMap.put("INTERFACE_PREV_BALANCE", String.valueOf(amount));

            // Setting the Servicve class into request map,if it is present in
            // the response elso its value is NULL
            // Controller will set ALL, if it is not set by the Handler.
            if (!InterfaceUtil.isNullString((String) _responseMap.get("profileId")))
                _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("profileId"));
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NCEDate"), "dd-MM-yy"));
            // _requestMap.put("OLD_EXPIRY_DATE",
            // InterfaceUtil.getInterfaceDateFromDateString((String)
            // _responseMap.get("CreditExpiry"), "dd-MM-yy"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NSEDate"), "dd-MM-yy"));
            // _requestMap.put("OLD_GRACE_DATE",
            // InterfaceUtil.getInterfaceDateFromDateString((String)
            // _responseMap.get("ServiceExpiry"), "dd-MM-yy"));
            // _requestMap.put("ACCOUNT_STATUS", (String)
            // _responseMap.get("acc_status"));
            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            setLanguageFromMapping();
        }// try
        catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        }// catch
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
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
        int multFacInt = 0;
        double postBalanceDble = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // Generate the IN transaction id using the getINTransactionID()
            // method of InterfaceUtil class and
            // store into request map with key as IN_TXN_ID.
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetch the interface id from the request map.
            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor:" + multFactor);
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor)) {
                _log.error("credit", "multFactor:" + multFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFacInt = Integer.parseInt(multFactor);
            // Set the interface parameters to the request map
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            // Generate the INReconID and set it into the requestMap
            getINReconTxnID();
            long requestedAmount = 0;
            // Get the requested amount from the requestMap with key as
            // INTERFACE_AMOUNT and use the

            try {
                requestedAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(requestedAmount, Integer.parseInt(multFactor.trim()));

            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("credit", "roundFlag:" + roundFlag);
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if ("Y".equalsIgnoreCase(roundFlag.trim())) {
                double amountDouble = Double.parseDouble(amountStr);
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
            // Here we have to change INFrame work while sending the request to
            // IN
            // Eiether we use the method arguments as _requestMap and action
            // both or only Action(_requestMap is class level variable)
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
            if ("Y".equals(balanceQuery) && !"N".equals(afterRechBalance)) {
                // Set the post balance after multiplied by multiplication
                // factor.
                try {
                    postBalanceDble = Double.parseDouble((String) _responseMap.get("balance"));
                    long postBalanceLong = InterfaceUtil.getSystemAmount(postBalanceDble, multFacInt);
                    _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceLong));
                } catch (Exception e) {
                    _log.error("credit", "Exception e:" + e.getMessage());
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
                // Set the value of NEW_EXPIRY_DATE as the value of end_val_date
                // from responseMap, after converting the format as per
                // interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NCEDate"), "dd-MM-yy"));
                // _requestMap.put("NEW_EXPIRY_DATE",
                // InterfaceUtil.getInterfaceDateFromDateString((String)
                // _responseMap.get("CreditExpiry"), "dd-MM-yy"));
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NSEDate"), "dd-MM-yy"));
                // _requestMap.put("NEW_GRACE_DATE",
                // InterfaceUtil.getInterfaceDateFromDateString((String)
                // _responseMap.get("ServiceExpiry"), "dd-MM-yy"));
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            } else
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get Exception e:" + e.getMessage());
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
        int multFacInt = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetch the interface id from the request map. Get the
            // multiplication factor from the
            // FileCache with the help of interface id.
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor)) {
                _log.error("debitAdjust", "MULTIPLICATION_FACTOR is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFacInt = Integer.parseInt(multFactor);
            // Set the interface parameters to the requestMap
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            // Generate the INReconID and set it into requestMap
            getINReconTxnID();
            long requestedAmount = 0;
            // Get the requested amount from the requestMap with key as
            // INTERFACE_AMOUNT and use the

            try {
                requestedAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "INTERFACE_AMOUNT is " + requestedAmount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // getDisplayAmount method of InterfaceUtil class to get the system
            // amount
            String amountStr = InterfaceUtil.getDisplayAmount(requestedAmount, multFacInt);

            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "roundFlag:" + roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                _log.error("debitAdjust", "ROUND_FLAG is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("ROUND_FLAG", roundFlag);
            if ("Y".equalsIgnoreCase(roundFlag.trim())) {
                double amountDouble = Double.parseDouble(amountStr);
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
            if (!InterfaceUtil.isNullString((String) _responseMap.get("balance"))) {
                try {
                    double postBalanceDble = Double.parseDouble((String) _responseMap.get("balance"));
                    long postBalanceLong = InterfaceUtil.getSystemAmount(postBalanceDble, multFacInt);
                    _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceLong));
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                } catch (Exception e) {
                    _log.error("debitAdjust", "Exception e:" + e.getMessage());
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "debitAdjust balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
            } else
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
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While debit get Exception e:" + e.getMessage());
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
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_map =" + p_map);
        _requestMap = p_map;
        int multFacInt = 0;
        double postBalanceDble = 0;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            // Generate the IN transaction id using the getINTransactionID()
            // method of InterfaceUtil class and
            // store into request map with key as IN_TXN_ID.
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetch the interface id from the request map.
            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "multFactor:" + multFactor);
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multFactor) || !InterfaceUtil.isNumeric(multFactor)) {
                _log.error("creditAdjust", "multFactor:" + multFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFacInt = Integer.parseInt(multFactor);
            // Set the interface parameters to the request map
            setInterfaceParameters();
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            // Generate the INReconID and set it into the requestMap
            getINReconTxnID();
            long requestedAmount = 0;
            // Get the requested amount from the requestMap with key as
            // INTERFACE_AMOUNT and use the

            try {
                requestedAmount = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // Put the system amount in request map after deviding it to
            // multiplication factor.
            String amountStr = InterfaceUtil.getDisplayAmount(requestedAmount, Integer.parseInt(multFactor.trim()));

            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "roundFlag:" + roundFlag);
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if ("Y".equalsIgnoreCase(roundFlag.trim())) {
                double amountDouble = Double.parseDouble(amountStr);
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
            // Here we have to change INFrame work while sending the request to
            // IN
            // Eiether we use the method arguments as _requestMap and action
            // both or only Action(_requestMap is class level variable)
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
            if ("Y".equals(balanceQuery) && !"N".equals(afterRechBalance)) {
                // Set the post balance after multiplied by multiplication
                // factor.
                try {
                    postBalanceDble = Double.parseDouble((String) _responseMap.get("balance"));
                    long postBalanceLong = InterfaceUtil.getSystemAmount(postBalanceDble, multFacInt);
                    _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceLong));
                } catch (Exception e) {
                    _log.error("creditAdjust", "Exception e:" + e.getMessage());
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
                // Set the value of NEW_EXPIRY_DATE as the value of end_val_date
                // from responseMap, after converting the format as per
                // interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NCEDate"), "dd-MM-yy"));
                // _requestMap.put("NEW_EXPIRY_DATE",
                // InterfaceUtil.getInterfaceDateFromDateString((String)
                // _responseMap.get("CreditExpiry"), "dd-MM-yy"));
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("NSEDate"), "dd-MM-yy"));
                // _requestMap.put("NEW_GRACE_DATE",
                // InterfaceUtil.getInterfaceDateFromDateString((String)
                // _responseMap.get("ServiceExpiry"), "dd-MM-yy"));
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            } else
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While creditAdjust get Exception e:" + e.getMessage());
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
        _log.warn("sendRequest", "123", "Abiguous Entered");
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
        NokiaClientPP nokiaClient = null;
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
            if ((InterfaceErrorCodesI.MODULE_C2S.equals(_requestMap.get("MODULE"))))
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
            if (!(clientObject instanceof NokiaClientPP)) {
                _log.error("sendRequestToIN", "In the InFile instead of defining [" + NokiaClient.class.getName() + "] client name is defined as[" + clientObject.getClass().getName() + "]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "In the INFile Client class path is not defined for NokiaClient]");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }
            // [Down]Cast the client object as per required class.
            nokiaClient = (NokiaClientPP) clientObject;
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
                for (retry = 0; retry < retryConInval; retry++) {
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
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Exception while sending request to Nokia IN CLient :" + be.getMessage());
                        throw be;
                    } catch (Exception e) {
                        _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                        e.printStackTrace();
                        if (retry > retryConInval) {
                            // Handle the event.
                            _log.error("sendRequestToIN", "Retry Attempted Exceeded Failing Request");
                            throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                        }
                        // Get a new Connection from pool manager and add the
                        // object into freeBucket
                        try {
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
                            nokiaClient = (NokiaClientPP) clientObject;
                            continue;
                        } catch (Exception e1) {
                            // Confirm what to do for this case.
                            // 1.Either make the transaction as Fail
                            // 2. Or Identify the exception and continue the
                            // loop till the max retryAttempt.
                            e1.printStackTrace();
                            _log.error("sendRequestToIN", "Exception e:" + e1.getMessage());
                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Exception while Retrying :" + e1.getMessage());
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Nokia IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                }
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE_ID = " + _interfaceID + "Stage = " + p_action, "Exception:" + e.getMessage());
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Blank response from Ericssion IN");
                _log.info("sendRequestToIN", "NULL response for interface");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // commented code may be used in future to support on line
                // cancel request
                /*
                 * if(NokiaI.ACTION_TXN_CANCEL == p_stage)
                 * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                 * AMBIGOUS);
                 */
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            _responseMap = new HashMap();
            InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");
            // After getting the Client object send the request
            String status = (String) _responseMap.get("statusInd");
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Status from the interface::" + status);
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
             * if(NokiaI.ACTION_TXN_CANCEL == p_stage)
             * {
             * _requestMap.put("CANCEL_RESP_STATUS",result);
             * cancelTxnStatus =
             * InterfaceUtil.getErrorCodeFromMapping(_requestMap
             * ,result,"SYSTEM_STATUS_MAPPING");
             * throw new BTSLBaseException(cancelTxnStatus);
             * }
             */

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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Status from the response  Status = " + status);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // commented code may be used in future to support on line
                // cancel request
                /*
                 * if(NokiaI.ACTION_TXN_CANCEL == p_stage)
                 * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                 * AMBIGOUS);
                 */
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            } else if (!NokiaI.RESULT_OK.equals(status)) {
                if (NokiaI.SUBSCRIBER_NOT_FOUND.equals(status)) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + "Stage = " + p_action, "Subscriber is not defined on the IN");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE_ID = " + _interfaceID + "Stage = " + p_action, "System Exception:" + e.getMessage());
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
            _log.warn("sendRequest", "123", "Abiguous Exiting");
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "TRANS_CURRENCY is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("FIRST_FLAG", firstCallFlag.trim());
            String applicationId = FileCache.getValue(_interfaceID, "APPLICATION");
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "applicationId:" + applicationId);
            if (InterfaceUtil.isNullString(applicationId)) {
                _log.error("setInterfaceParameters", "Value of APPLICATION is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "APPLICATION is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("applicationId", applicationId.trim());
            String balanceQuery = FileCache.getValue(_interfaceID, "BALANCE_QUERY");
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "balanceQuery:" + balanceQuery);
            if (InterfaceUtil.isNullString(balanceQuery)) {
                _log.error("setInterfaceParameters", "Value of BALANCE_QUERY is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "BALANCE_QUERY is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("balanceQuery", balanceQuery.trim());
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NokiaINHandlerPP[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LANGUAGE_MAPPING is not defined in IN file");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            langFromIN = (String) _responseMap.get("subscriberLanguage");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "langFromIN::" + langFromIN);
            if (InterfaceUtil.isNullString(langFromIN)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Subscriber's language is not recieved from the IN.");
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "NokiaINHandlerPP[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
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
            String userType = (String) _requestMap.get("USER_TYPE");
            if (userType != null)
                inReconID = ((String) _requestMap.get("TRANSACTION_ID") + "." + userType);
            else
                inReconID = ((String) _requestMap.get("TRANSACTION_ID"));
            _requestMap.put("IN_RECON_ID", inReconID);
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
             * String inStr =
             * _formatter.generateRequest(NokiaI.ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * initializeConnectionParameters(NokiaI.ACTION_TXN_CANCEL);
             * sendRequestToIN(inStr,NokiaI.ACTION_TXN_CANCEL);
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
}
