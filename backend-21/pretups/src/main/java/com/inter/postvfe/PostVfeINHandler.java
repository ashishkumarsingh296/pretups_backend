package com.inter.postvfe;

import java.rmi.RemoteException;
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
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.postqueue.QueueTableHandler;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.inter.postvfe.dbutil.PostVfeDBUtility;
import com.inter.postvfe.dbutil.PostVfeUtilityManager;
import com.inter.postvfe.postvfestub.CMSInvoke;
import com.inter.postvfe.postvfestub.CMSRequest;
import com.inter.postvfe.postvfestub.CMSResponse;

/**
 * VFEReqResFormatter.java
 * Copyright(c) 2011, Comviva Technologies Pvt. Ltd.
 * All Rights Reserved
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Shashank Shukla November 10, 2011 Initial Creation
 * Rahul Dutt November 16, 2012 Modified(Online PPB and New Stub integration)
 * ----------------------------------------------------------------------------
 * --------------------
 * This class is responsible to generate the request and parse the response for
 * the VFEPostPaid interface.
 */

public class PostVfeINHandler implements InterfaceHandler {

    private static Log _log = LogFactory.getLog("PostVfeINHandler".getClass().getName());
    private HashMap<String, String> _requestMap = null;
    private HashMap<String, String> _responseMap = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private VFEReqResFormatter _formatter = null;
    private boolean _online = true;

    /**
     * Default constructor to create formatter object.
     */
    public PostVfeINHandler() {
        _formatter = new VFEReqResFormatter();
    }

    /**
     * validate Method is used for getting the account information of user
     * 
     * @param p_requestMap
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_map:" + p_requestMap);
        _requestMap = p_requestMap;
        String balanceStr = null;
        double minAmountDue = 0, requestedamount = 0, dueAmtpaid = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            _requestMap.put("CUST_ID", _msisdn);
            // Fetch value of VALIDATION key from IN File. (this is used to
            // ensure that subscriber validation is required or not)
            // If validation of subscriber is not required set the SUCCESS code
            // into request map and return.
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }

            // Generate the IN transaction id
            _inTXNID = getINTransactionID(_requestMap, PostVfeI.ACTION_ACCOUNT_INFO);
            _requestMap.put("IN_TXN_ID", _inTXNID);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR_DB");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR_DB  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR_DB  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters(PostVfeI.ACTION_ACCOUNT_INFO);

            // Sending the request to IN
            try {
                sendRequestToIN(PostVfeI.ACTION_ACCOUNT_INFO);
                _requestMap.put("POSTPAID_ONLINE_ENQ", "Y");
            } catch (BTSLBaseException be) {
                //
                if (be.getMessageKey().equals(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION) || be.getMessageKey().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    _online = false;
                    _requestMap.put("POSTPAID_ONLINE_ENQ", "N");
                    sendRequestToIN(PostVfeI.ACTION_ACCOUNT_INFO_DB);
                } else
                    throw be;
            }
            String customerID = null;
            try {
                if (_online) {
                    balanceStr = (String) _responseMap.get("CURRENT_BALANCE");
                    customerID = (String) _responseMap.get("CS_ID");
                } else {
                    balanceStr = (String) _responseMap.get("Balance");
                    customerID = (String) _responseMap.get("customerID");
                }
                double multFactorDouble = Double.parseDouble(multFactor.trim());
                balanceStr = InterfaceUtil.getSystemAmountFromINAmount(balanceStr, multFactorDouble);
                _requestMap.put("INTERFACE_PREV_BALANCE", balanceStr);
            } catch (Exception e) {
                _log.error("validate", "Exception e:" + e.getMessage());
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // //check null

            if (BTSLUtil.isNullString(customerID)) {
                _log.error("validate", "customerID received in response is NULL");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "customerID in the _responseMap[" + customerID + "] is INVALID");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // On successful response, set TRANSACTION_STATUS as SUCCES into
            // request map.
            // ???? do we have to take in to account txn id from IN
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("BILL_AMOUNT_BAL", balanceStr);
            if (_online)
                _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("CUSTOMER_STATUS"));
            else
                _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("accountStatus"));
            _requestMap.put("IN_RECON_ID", customerID);
            _requestMap.put("SERVICE_CLASS", (PretupsI.ALL));
            // _requestMap.put("SERVICE_CLASS",_responseMap.get("rateplan"));
            // set the mapping language of our system from FileCache mapping
            // based on the responsed language.
            _requestMap.put("MIN_AMT_DUE", (String) _responseMap.get("MIN_AMT_DUE"));
            _requestMap.put("DUE_AMT", (String) _responseMap.get("CURRENT_BALANCE"));
            _requestMap.put("DUE_DATE", (String) _responseMap.get("DUE_DATE"));
            if (_log.isDebugEnabled())
                _log.debug("validate", "@@@" + (String) _responseMap.get("MIN_AMT_DUE"));
            try {
                minAmountDue = Double.parseDouble((String) _responseMap.get("MIN_AMT_DUE"));
                minAmountDue = Math.ceil(minAmountDue);
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("validate", "@@@" + minAmountDue);
            if (_online && PretupsI.SERVICE_TYPE_CHNL_BILLPAY.equals((String) _requestMap.get("REQ_SERVICE"))) {
                if (minAmountDue <= 0 || dueAmtpaid > minAmountDue) {
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.CMS_NO_INVOICE_FOUND);
                }
                requestedamount = Double.parseDouble((String) PretupsBL.getDisplayAmount(Long.parseLong((String) _requestMap.get("REQUESTED_AMOUNT"))));
                dueAmtpaid = Double.parseDouble((String) PretupsBL.getDisplayAmount(Long.parseLong((String) _requestMap.get("DUE_AMOUNT_PAID"))));

                if (requestedamount < (minAmountDue - dueAmtpaid)) {
                    _log.error("validate", "BTSLBaseException requestedamount" + requestedamount + ":" + "minAmountDue" + minAmountDue + ":" + "dueAmtpaid" + dueAmtpaid);
                    String[] str = new String[] { Double.toString(requestedamount), Double.toString(minAmountDue), Double.toString(dueAmtpaid), Double.toString(minAmountDue - dueAmtpaid) };
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RECHARGE_AMOUNT_LESS, 0, str, null);
                }
            }
            _requestMap.put("MIN_AMT_DUE", Double.toString(minAmountDue - dueAmtpaid));
            setLanguageFromMapping();
            if (_online)
                _requestMap.put("UPDATE_STATUS1", "Y");
            else
                _requestMap.put("UPDATE_STATUS1", "N");

        }// try
        catch (BTSLBaseException be) {
            be.printStackTrace();
            _log.error("validate", "Exception e:" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw be;
        }// catch
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug(this, "validate", "Exited _requestMap=" + _requestMap);
        }// finally
    }// end of validate DONE

    private void validateAfterPayment(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validateAfterPayment", "Entered p_map:" + p_requestMap);
        _requestMap = p_requestMap;
        String balanceStr = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));

            // Generate the IN transaction id
            _inTXNID = getINTransactionID(_requestMap, PostVfeI.ACTION_ACCOUNT_INFO);
            _requestMap.put("IN_TXN_ID", _inTXNID);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR_DB");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR_DB  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR_DB  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.AMBIGOUS);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            // setInterfaceParameters(PostVfeI.ACTION_ACCOUNT_INFO);

            try {
                if (_online)
                    sendRequestToIN(PostVfeI.ACTION_ACCOUNT_INFO);
                else
                    sendRequestToIN(PostVfeI.ACTION_ACCOUNT_INFO_DB);
            } catch (Exception e1) {
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            try {
                if (_online)
                    balanceStr = (String) _responseMap.get("CURRENT_BALANCE");
                else
                    balanceStr = (String) _responseMap.get("Balance");
                double multFactorDouble = Double.parseDouble(multFactor.trim());
                balanceStr = InterfaceUtil.getSystemAmountFromINAmount(balanceStr, multFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", balanceStr);
            } catch (Exception e) {
                _log.error("validate", "Exception e:" + e.getMessage());
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.AMBIGOUS);
            }

            if (Integer.parseInt(balanceStr) < Integer.parseInt((String) _requestMap.get("INTERFACE_PREV_BALANCE"))) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                throw new BTSLBaseException(InterfaceErrorCodesI.CHECK_AMB_STATUS_FAIL);
            }

        }// try
        catch (BTSLBaseException be) {
            be.printStackTrace();
            _log.error("validateAfterPayment", "Exception e:" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validateAfterPayment]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validateAfterPayment");
            throw be;
        }// catch
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validateAfterPayment", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[validateAfterPayment]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validateAfterPayment");
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
        }// catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug(this, "validateAfterPayment", "Exited _requestMap=" + _requestMap);
        }// finally
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
     * This method would be used to adjust the validity of the subscriber
     * account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }// end of validityAdjust

    /**
     * This method would be used to adjust the validity of the subscriber
     * account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }// end of validityAdjust

    /**
     * This method credit the balance of user.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_map" + p_map);
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        String online = "N";
        _requestMap = p_map;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            _inTXNID = getINTransactionID(_requestMap, PostVfeI.ACTION_BILL_POST);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR_CORBA");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR_CORBA  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR_CORBA  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);
            online = _requestMap.get("UPDATE_STATUS1");
            if (PretupsI.NO.equals(online.trim()))
                _online = false;
            else
                _online = true;
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "PostVfeINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            setInterfaceParameters(PostVfeI.ACTION_BILL_POST);
            if (_online) {
                _requestMap.put("EXT_CREDIT_INTFCE_TYPE", "Online");
                sendRequestToIN(PostVfeI.ACTION_BILL_POST);
            }
            if (!_online) {
                _requestMap.put("EXT_CREDIT_INTFCE_TYPE", "Offline");
                sendRequestToIN(PostVfeI.ACTION_BILL_POST_DB);
            }
            // throw new
            // BTSLBaseException(this,"credit",InterfaceErrorCodesI.AMBIGOUS);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            _log.error("credit", "BTSLBaseException" + be + ":online" + _online);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[credit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }
    }

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
    private void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR_CORBA");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("setInterfaceParameters", "Value of MULT_FACTOR_CORBA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR_CORBA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("MULT_FACTOR_CORBA", multFactor.trim());

            String currId = FileCache.getValue(_interfaceID, "CURRENCY_ID");
            if (InterfaceUtil.isNullString(currId)) {
                _log.error("setInterfaceParameters", "Value of CURRENCY_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CURRENCY_ID is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CURRECNY_ID", currId.trim());

            String paymentMode = FileCache.getValue(_interfaceID, "PAYMENT_MODE");
            if (InterfaceUtil.isNullString(paymentMode)) {
                _log.error("setInterfaceParameters", "Value of PAYMENT_MODE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "PAYMENT_MODE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("PAYMENT_MODE", paymentMode.trim());

            String syncModeFlag = FileCache.getValue(_interfaceID, "SYNCHRONOUS_MODE");
            if (InterfaceUtil.isNullString(syncModeFlag)) {
                _log.error("setInterfaceParameters", "Value of SYNCHRONOUS_MODE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYNCHRONOUS_MODE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYNCHRONOUS_MODE", syncModeFlag.trim());

            String txnCode = FileCache.getValue(_interfaceID, "TRANSX_CODE");
            if (InterfaceUtil.isNullString(txnCode)) {
                _log.error("setInterfaceParameters", "Value of TRANSX_CODE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TRANSX_CODE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("TRANSX_CODE", txnCode.trim());

            String remark = FileCache.getValue(_interfaceID, "RT_CAUSERNAME");
            if (InterfaceUtil.isNullString(remark)) {
                _log.error("setInterfaceParameters", "Value of RT_CAUSERNAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RT_CAUSERNAMEis not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RT_CAUSERNAME", remark.trim());

            String endpoint = FileCache.getValue(_interfaceID, "END_URL");
            if (InterfaceUtil.isNullString(endpoint)) {
                _log.error("setInterfaceParameters", "Value of END_URL is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "END_URL is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("END_URL", endpoint.trim());

            String userName = FileCache.getValue(_interfaceID, "USER_NAME");
            if (InterfaceUtil.isNullString(userName)) {
                _log.error("setInterfaceParameters", "Value of USER_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "USER_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("USER_NAME", userName.trim());

            String password = FileCache.getValue(_interfaceID, "PASSWORD");
            if (InterfaceUtil.isNullString(password)) {
                _log.error("setInterfaceParameters", "Value of PASSWORD is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("PASSWORD", password.trim());

            /**
             * String endurl=FileCache.getValue(_interfaceID, "END_POINT");
             * if(InterfaceUtil.isNullString(endurl))
             * {
             * _log.error("setInterfaceParameters",
             * "Value of END_POINT is not defined in the INFile");
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,
             * EventComponentI.INTERFACES, EventStatusI.RAISED,
             * EventLevelI.FATAL,
             * "PostVfeINHandler[setInterfaceParameters]",_referenceID
             * ,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String)
             * _requestMap.get("NETWORK_CODE"),
             * "END_POINT is not defined in the INFile.");
             * throw new BTSLBaseException(this,"setInterfaceParameters",
             * InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
             * }
             * _requestMap.put("END_POINT",endurl.trim());
             */
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
            if (InterfaceUtil.isNullString(mappingString)) {
                mappingString = "";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeINHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LANGUAGE_MAPPING is not defined in IN file,Hence setting the Default language");
            }
            langFromIN = (String) _responseMap.get("languageID");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString = " + mappingString + " langFromIN = " + langFromIN);

            if (!InterfaceUtil.isNullString(langFromIN)) {
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeINHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            }
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

    /**
     * This method is responsible to send the request to IN.
     * 
     * @param int p_action
     * @param Hashmap
     *            responseMap
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(int p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_stage =" + p_stage);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_REQ, "", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_stage);
        Connection connection = null;
        String responseStr = null;
        Object dbUtility = null;
        PostVfeConnector serviceConnection = null;
        CMSInvoke clientStub = null;
        CMSRequest cmsRequest = null;
        CMSResponse cmsResponse = null;
        String resultCode = null;
        try {
            if (p_stage == PostVfeI.ACTION_ACCOUNT_INFO) {
                try {
                    serviceConnection = new PostVfeConnector((String) _requestMap.get("END_URL"), (String) _requestMap.get("TIME_OUT"));
                    clientStub = serviceConnection.getPostVfeClient();
                    if (clientStub == null) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "Unable to get Client Object");
                        _log.error("sendRequestToIN", "Unable to get Client Object");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    }
                    try {
                        cmsRequest = _formatter.genReqObject(p_stage, _requestMap);
                        if (cmsRequest == null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        cmsResponse = clientStub.invoke(cmsRequest);
                        if (cmsResponse == null) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_stage, "Response object found null");
                            _log.error("sendRequestToIN", "No response received from the IN for Val Request.");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.FAIL);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        resultCode = cmsResponse.getStatusCode();
                    } catch (RemoteException re) {
                        re.printStackTrace();
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_stage, " RemoteException, Error Message=" + re.getMessage());
                        _log.error("sendRequestToIN", "RemoteException, Error Message=" + re.getMessage());
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.FAIL);
                        throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                    } catch (BTSLBaseException be) {
                        be.printStackTrace();
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_stage, " BTSLBaseException, Error Message=" + be.getMessage());
                        _log.error("sendRequestToIN", "BTSLBaseException, Error Message=" + be.getMessage());
                        throw be;
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action =" + p_stage, " Exception Error Message:" + e.getMessage());
                        _log.error("sendRequestToIN", "Exception Error Message :" + e.getMessage());
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    } finally {
                        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "Response=" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " action=" + p_stage);
                    }
                    // Construct the response map from the response object
                    // received.
                    _responseMap = _formatter.parseResponseObject(p_stage, cmsResponse);

                    String status = _responseMap.get("RESP_STATUS");

                    _requestMap.put("INTERFACE_STATUS", status);

                    if (!PostVfeI.RESULT_OK.equals(status)) {
                        // Check the status whether the subscriber's MSISDN
                        // defined in the IN
                        if (PostVfeI.SUBSCRIBER_NOT_FOUND_IN.equals(status)) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "MSISDN does not exist on IN");
                            _log.error("sendRequestToIN", "MSISDN does not exist on IN");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                        }
                        // Check the status whether the subscriber's MSISDN
                        // defined in the IN
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "Response Status fron IN is: " + status + ". So marking response as FAIL");
                        _log.error("sendRequestToIN", "Response Status fron IN is: " + status + ". So marking response as FAIL.");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
                    throw be;
                }// end of BTSLBaseException

                catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception=" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "System Exception=" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }

                finally {
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", " Exiting p_action=" + p_stage + " Response Status fron IN=" + _requestMap.get("INTERFACE_STATUS"));
                }
            }
            if (p_stage == PostVfeI.ACTION_ACCOUNT_INFO_DB) {

                dbUtility = PostVfeUtilityManager._utilityObjectMap.get(_interfaceID);
                connection = ((PostVfeDBUtility) dbUtility).getConnection();
                if (connection == null) {
                    _log.error("sendRequestToIN", "Database Connection NULL, Before creating pool for " + _interfaceID);
                    new PostVfeUtilityManager().initialize(_interfaceID);
                    connection = ((PostVfeDBUtility) dbUtility).getConnection();
                    _log.error("sendRequestToIN", "After creating  pool for " + _interfaceID);
                    if (connection == null) {
                        _log.error("sendRequestToIN", "connection=" + connection);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "DBConnection is NULL");
                        throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);// Confrim
                                                                                                                                  // for
                                                                                                                                  // the
                                                                                                                                  // new
                                                                                                                                  // key
                    }
                }

                sendRequestToDB(connection);
                responseStr = _requestMap.get("RESPONSE_STR").toString();
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "p_stage=" + p_stage);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr::" + responseStr);

                // Check if there is no response, handle the event showing Blank
                // response from Comverse stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_stage, "Blank response from IN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.FAIL);
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                }
                _responseMap = BTSLUtil.getStringToHash(responseStr, "&", "=");

                String errorCode = (String) _responseMap.get("errorCode");
                _requestMap.put("INTERFACE_STATUS", errorCode);

                if (!InterfaceUtil.isNullString(errorCode)) {
                    if (!(PostVfeI.SUCCESS_DB.equals(errorCode))) {
                        if (PostVfeI.SUBSCRIBER_NOT_FOUND_DB.equals(errorCode)) {
                            _log.error("sendRequestToIN", "Subscriber not found with MSISDN::" + _msisdn);
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PostVfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_stage, "Subscriber is not found at IN");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                        }// end of checking the subscriber existence.
                        else if (PostVfeI.SUBSCRIBER_INVALID_FOUND_DB.equals(errorCode)) {
                            _log.error("sendRequestToIN", "Subscriber not found with MSISDN::" + _msisdn);
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PostVfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_stage, "Subscriber is not found at IN");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                        }// end of checking the subscriber existence.
                        else {
                            _log.error("sendRequestToIN", "Error code received from IN ::" + errorCode);
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PostVfeINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_stage, "Error code received from IN " + errorCode);
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                        }
                    }
                }
            } else if (p_stage == PostVfeI.ACTION_BILL_POST) {
                try {
                    serviceConnection = new PostVfeConnector((String) _requestMap.get("END_URL"), (String) _requestMap.get("TIME_OUT"));
                    clientStub = serviceConnection.getPostVfeClient();
                    if (clientStub == null) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "Unable to get Client Object");
                        _log.error("sendRequestToIN", "Unable to get Client Object");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    }
                    try {
                        cmsRequest = _formatter.genReqObject(p_stage, _requestMap);
                        if (cmsRequest == null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        cmsResponse = clientStub.invoke(cmsRequest);
                        if (cmsResponse == null) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_stage, "Response object found null");
                            _log.error("sendRequestToIN", "No response received from the IN for Credit Request.");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        resultCode = cmsResponse.getStatusCode();

                    } catch (RemoteException re) {
                        re.printStackTrace();
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_stage, " RemoteException, Error Message=" + re.getMessage());
                        _log.error("sendRequestToIN", "RemoteException, Error Message=" + re.getMessage());
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                    } catch (BTSLBaseException be) {
                        be.printStackTrace();
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_stage, " BTSLBaseException, Error Message=" + be.getMessage());
                        _log.error("sendRequestToIN", "BTSLBaseException, Error Message=" + be.getMessage());
                        throw be;
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action =" + p_stage, " Exception Error Message:" + e.getMessage());
                        _log.error("sendRequestToIN", "Exception Error Message :" + e.getMessage());
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    } finally {
                        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "Response=" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " action=" + p_stage);
                    }
                    // Construct the response map from the response object
                    // received.
                    _responseMap = _formatter.parseResponseObject(p_stage, cmsResponse);

                    String status = _responseMap.get("RESP_STATUS");

                    _requestMap.put("INTERFACE_STATUS", status);

                    if (!PostVfeI.RESULT_OK.equals(status)) {
                        // Check the status whether the subscriber's MSISDN
                        // defined in the IN
                        _online = false;
                        // Check the status whether the subscriber's MSISDN
                        // defined in the IN
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "Response Status fron IN is: " + status + ". So marking response as FAIL");
                        _log.error("sendRequestToIN", "Response Status fron IN is: " + status);
                    }
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
                    throw be;
                }// end of BTSLBaseException

                catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception=" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "System Exception=" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }

                finally {
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", " Exiting p_action=" + p_stage + " Response Status fron IN=" + _requestMap.get("INTERFACE_STATUS"));
                }
            } else if (p_stage == PostVfeI.ACTION_BILL_POST_DB)// Generate CDR
                                                               // for the
                                                               // processed
                                                               // transaction
            {
                QueueTableHandler queueTableHandler = null;
                queueTableHandler = new QueueTableHandler();
                queueTableHandler.credit(_requestMap);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "While calling the stored proc get Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exited _requestMap =" + _requestMap);
        }

    }

    /**
     * Method to perform actual communication with database
     * 
     * @param HashMap
     *            p_requestMap
     * @param String
     *            p_stage
     * @throws BTSLBaseException
     */
    public void sendRequestToDB(Connection p_connection) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("sendAccountInfoRequest", "Entered");
        CallableStatement callStmt = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        double balance = 0;
        String filteredMsisdn = null;
        try {
            filteredMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            // call procedure getAccountInformation and concatenate all returned
            // parametes.
            // put the concatenated response string in response map as
            // RESPONSE_STR
            // String
            // procStr="call PB.bharti_tools.getAccountInformation(?,?,?,?,?,?,?,?,?,?)";
            String procStr = "call sysadm.VF_READ_TOPUP_ELIGIBLITY(?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);

            callStmt.setString(1, filteredMsisdn);
            callStmt.setString(2, (String) _requestMap.get("IN_TXN_ID"));
            callStmt.registerOutParameter(3, Types.INTEGER);
            callStmt.registerOutParameter(4, Types.VARCHAR);
            callStmt.registerOutParameter(5, Types.DOUBLE);
            callStmt.registerOutParameter(6, Types.INTEGER);
            callStmt.registerOutParameter(7, Types.INTEGER);
            callStmt.registerOutParameter(8, Types.VARCHAR);

            callStmt.execute();
            responseBuffer = new StringBuffer(1028);

            responseBuffer.append("customerID=" + callStmt.getInt(3));
            responseBuffer.append("&accountStatus=" + callStmt.getString(4));
            responseBuffer.append("&Balance=" + callStmt.getDouble(5));
            responseBuffer.append("&languageID=" + callStmt.getInt(6));
            responseBuffer.append("&errorCode=" + callStmt.getInt(7));
            responseBuffer.append("&errorDesc=" + callStmt.getString(8));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
            // _responseMap=BTSLUtil.getStringToHash(responseStr,"&","=");

        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendAccountInfoRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + PostVfeI.ACTION_ACCOUNT_INFO, "While validating the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendAccountInfoRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + PostVfeI.ACTION_ACCOUNT_INFO, "While validating the subscriber get Exception e:" + e.getMessage());
            // throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
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
                _log.debug("sendAccountInfoRequest", "exited");
        }

    }

    private String getINTransactionID(HashMap p_requestMap, int p_stage) {
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Entered");

        String transactionId = _referenceID;

        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Exited");
        return transactionId;
    }
}
