package com.inter.blin.blpost;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;

/**
 * @PostPaidINHandler.java
 *                         Copyright(c) 2016, BMahindra Comviva.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Sanjeew K Aug 22, 2016 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 *                         This class would be capable of interacting with the
 *                         Application DB
 *                         interface commands.
 *                         a) Validate post paid subscriber- Validate the subscriber from POSTPAID_CUST_MASTER table
 *                         b) Bill payment-Insert the credit value in POSTPAID_CUST_PAY_MASTER table
 * 
 */
public class PostPaidINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(PostPaidINHandler.class.getName());
    private HashMap <String,String> _requestMap = null;// Contains the request parameter as key
    // and value pair.
    private HashMap <String,String> _responseMap = null;// Contains the response of the request
    // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
    // transaction id.
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private String _interfaceClosureSupport = null;
    private static int _prevMinut = 0;
    private static long _counter = 0;
    private static SimpleDateFormat _sdf = new SimpleDateFormat("yyMMddHHmmss");
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    /**
     * This method would implements the logic to validate the post paid
     * subscriber.
     * Validation of subscriber would be done by following ways.
     * a. Validate the subscriber from POSTPAID_CUST_MASTER table if subscriber exist in this table then 
     *      that subscriber is valid post paid subscriber
     * 
     */
    public void validate(HashMap <String,String> p_requestMap) throws BTSLBaseException, Exception {
        String METHODE_NAME="PostPaidINHandler[validate()]";
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
       try {
            _interfaceID = _requestMap.get("INTERFACE_ID");
            _referenceID = _requestMap.get("TRANSACTION_ID");
            _msisdn = _requestMap.get("MSISDN");
            // ensure that subscriber validation is required or not)
            // If validation of subscriber is not required set the SUCCESS code
            // into request map and return.
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            setInterfaceParameters();
            sendRequestToIN(PostPaidI.ACTION_ACCOUNT_INFO);
            _requestMap.put("ACCOUNT_ID", _responseMap.get("Custcode"));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHODE_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHODE_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID,_requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "Exited _requestMap:" + _requestMap);
        }
    }

    /**
     * This method would implements the logic to credit the post paid subscriber
     * account by following ways.
     * 1.Generating the CDR
     * 2.Online PIH file transfer using FTP.
     */
    public void credit(HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception {
        String METHODE_NAME="PostPaidINHandler[cirdit()]]";
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Entered p_requestMap:" + p_requestMap);
        TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "credit. Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        try {
            _interfaceID = _requestMap.get("INTERFACE_ID");
            _referenceID =_requestMap.get("TRANSACTION_ID");
            _msisdn = _requestMap.get("MSISDN");
            multFactAmountsetIntoRequestMap(FileCache.getValue(_interfaceID, "MULT_FACTOR"));
            setInterfaceParameters();
            sendRequestToIN(PostPaidI.ACTION_RECHARGE_CREDIT);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.errorTrace(METHODE_NAME, be);
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
        } catch (Exception e) {
            _log.errorTrace(METHODE_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID,_requestMap.get("NETWORK_CODE"), "While credit the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "Exited _requestMap:" + _requestMap);
        }
    }

    /**
     * This method would implements the logic to Adjust the credit for the post
     * paid subscriber.
     * Credit Adjustment of Post paid subscriber would be done by following way.
     * 1. Generating the CDR
     * 2. Online PIH file transfer using FTP
     */
    public void creditAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception {
    }

    /**
     * This method would implements the logic to debit the subscriber account
     * while using the POST 2 PRE services.
     * Debit Adjust of Post paid subscriber would be done by following way.
     * 1. Generating the CDR
     * 2. Online PIH file transfer using FTP
     */
    public void debitAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception {
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
    public void validityAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception {
    }// end of validityAdjust

    /**
     * This method would implements the logic for subscriber validation, credit
     * or debit the subscriber to the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @param String
     *            p_stage
     * @throws BTSLBaseException
     *             ,Exception
     */
    private void sendRequestToIN(int p_stage) throws BTSLBaseException {
        String METHODE_NAME="PostPaidINHandler[sendRequestToIN()]";
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Entered  p_stage:" + p_stage);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_REQ, "Prepared statment is Called for stage:" + p_stage, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
        Connection con = null;
        MComConnectionI mcomCon = null;
        long startTime = 0;
        long endTime = 0, warnTime = 0;
        PostPaidReqResFormatter postPaidReqResFor=null;
        try {
            postPaidReqResFor=new PostPaidReqResFormatter();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if (con == null) 
            {
                _log.error(METHODE_NAME, "dbConnection=" + con);
                // Confirm for the Event handling.
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, METHODE_NAME, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " +_requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "DBConnection is NULL");
                throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }
            startTime = System.currentTimeMillis();
            _log.error(METHODE_NAME, " startTime:" + startTime);
            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
            switch (p_stage) {
            case PostPaidI.ACTION_ACCOUNT_INFO: {
                postPaidReqResFor.validate(con, _requestMap);
                break;
            }
            case PostPaidI.ACTION_RECHARGE_CREDIT: {
                postPaidReqResFor.credit(con, _requestMap);
                mcomCon.finalCommit();
                break;
            }
            }
            endTime = System.currentTimeMillis();
        } catch (BTSLBaseException be) {
            try {
                if (con != null)
                    mcomCon.finalRollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                if (con != null)
                    mcomCon.finalRollback();
            } catch (Exception ex) {
            }
            _log.errorTrace(METHODE_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "While calling the stored proc get Exception=" + e.getMessage());
            throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("PostPaidINHandler#sendRequestToIN");
				mcomCon = null;
			}
            if (endTime == 0)
                endTime = System.currentTimeMillis();
            _log.error(METHODE_NAME, "endTime:" + endTime + " Total time taken by IN to process the request[for stage=" + p_stage + "]");
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
        }
        warnTime = Long.parseLong(_requestMap.get("WARN_TIMEOUT"));
        if (endTime - startTime > warnTime) {
            _log.info(METHODE_NAME, "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, METHODE_NAME, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " +_requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
        }
        // Fetch the response
        String responseStr = _requestMap.get("RESPONSE_STR");
        TransactionLog.log(_inTXNID, _referenceID, _msisdn,_requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
        if (InterfaceUtil.isNullString(responseStr)) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " +_requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "Response from the IN:" + responseStr);
            _log.error(METHODE_NAME, "Response from the IN is null");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
        }
        _responseMap = new HashMap<String,String>();
        // Convert the responseString into hash map.
        InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");
        String status = _responseMap.get("Status");
        // Check for the success code, what its value would be?
        // Case1: What status code would be fetched other than success code?
        // Case2: What would be the status code incase of MSISDN is not
        // found?
        _requestMap.put("INTERFACE_STATUS", status);

        if (!PostPaidI.SP_SUCCESS_OK.equals(status)) {
            if (PostPaidI.SUBSCRIBER_NOT_FOUND.equals(status)) {
                _log.error(METHODE_NAME, "Error in response SUBSCRIBER_NOT_FOUND");
                EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,METHODE_NAME, _referenceID, _msisdn, _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_stage, " SUBSCRIBER_NOT_FOUND AT IN");
                throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);// MSISDN
            } else if (PostPaidI.NOT_POSTPAID_NO.equals(status))// Number is
            {
                _log.error(METHODE_NAME, "Error in response NOT_POSTPAID_NO");
                EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, METHODE_NAME, _referenceID, _msisdn, _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_stage, " NOT_POSTPAID_NO (Number is not a valid Postpaid number)AT IN");
                throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INVALID_POSTPAID_NUMBER);// Number
            }
            _log.error(METHODE_NAME, "Error in response with" + " status=" + status);
            throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.ERROR_RESPONSE);
        }
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Exited");
    }

    /**
     * This method is used to get interface specific values from FileCache,
     * based on
     * interface id and set to the requested map.These parameters are
     * 1.PIH FILE FORMAT
     * 2.CUURENCY
     * 
     * @throws BTSLBaseException
     */
    private void setInterfaceParameters() throws BTSLBaseException {
        String METHODE_NAME="PostPaidINHandler[setInterfaceParameters()]";
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Entered");
            try {
                // Currency is common for each type of communication mod.
    
                String filteredString = InterfaceUtil.getFilterMSISDN(_requestMap.get("INTERFACE_ID"),_requestMap.get("MSISDN"));
                _requestMap.put("FILT_REC_MSISDN", filteredString);
                try {
                    filteredString = InterfaceUtil.getFilterMSISDN(_requestMap.get("INTERFACE_ID"),_requestMap.get("SENDER_MSISDN"));
                    _requestMap.put("FILT_SEND_MSISDN", filteredString);
                }catch(Exception e){_log.errorTrace(METHODE_NAME, e);}
                
                String currency = FileCache.getValue(_interfaceID, "CURRENCY");
                if (InterfaceUtil.isNullString(currency)) {
                    _log.error(METHODE_NAME, "CURRENCY is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " +_requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("CURRENCY", currency.trim());
                String warnTimeStr = FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());
                if (_log.isDebugEnabled())
                    _log.debug(METHODE_NAME, "currency:" + currency);
                String dateFormat = FileCache.getValue(_interfaceID, "TXN_DATE_FORMAT");
                if (InterfaceUtil.isNullString(dateFormat)) {
                    _log.error(METHODE_NAME, "TXN_DATE_FORMAT  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID,_requestMap.get("NETWORK_CODE"), "TXN_DATE_FORMAT  is not defined in the INFile");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("TXN_DATE_FORMAT", dateFormat.trim());
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                sdf.setLenient(false);
                String formatStr = sdf.format(new Date());
                _requestMap.put("PAYMENT_REF2", formatStr);
                _requestMap.put("PAYMENT_DATE", formatStr);
    
                String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
                if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                    _log.error(METHODE_NAME, "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn,_requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());
    
                String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
                if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                    _log.error(METHODE_NAME, "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn,_requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());
    
                String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
                if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                    _log.error(METHODE_NAME, "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn,_requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());
    
                String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
                if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                    _log.error(METHODE_NAME, "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn,_requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());
    
                String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
                if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                    _log.error(METHODE_NAME, "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn,_requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                    throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
                _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());
           }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHODE_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " +_requestMap.get("NETWORK_CODE"), "While setting the Interface parameters get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "Exiting");
        }
    }// end of setInterfaceParameters
    /**
     * This method is used to validate the multiplication factor for each mod
     * type(Online DB, FTP for PIH etc)
     * 
     * @param p_multiplicationFactor
     * @throws BTSLBaseException
     */
    private void multFactAmountsetIntoRequestMap(String p_multiplicationFactor) throws BTSLBaseException {
        String METHODE_NAME="PostPaidINHandler[multFactAmountsetIntoRequestMap()]";
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Entered p_multiplicationFactor:" + p_multiplicationFactor);
        String amountStr = null;
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String multFactor = p_multiplicationFactor;
        try {
            multFactorDouble = Double.parseDouble(multFactor);
            double interfaceAmountDouble = Double.parseDouble(_requestMap.get("INTERFACE_AMOUNT"));
            systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmountDouble, multFactorDouble);
            amountStr = String.valueOf(systemAmtDouble);
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "From file cache roundFlag = " + roundFlag);
            // If rounding of amount is allowed, round the amount value and put
            // this value in request map.
            if ("Y".equals(roundFlag.trim())) {
                amountStr = String.valueOf(Math.round(systemAmtDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
        } catch (Exception e) {
            _log.errorTrace(METHODE_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "MSISDN = " + _requestMap.get("MSIDN"), "INTERFACE ID = " + _interfaceID, _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not valid");
            throw new BTSLBaseException(this, METHODE_NAME, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "Exited");
        }
    }

    public static synchronized String getINTransactionID() {
        String txnIdPrefix = null;
        String minut2Compare = null;
        Date mydate = null;
        int inTxnLength = 5;

        mydate = new Date();
        txnIdPrefix = _sdf.format(mydate);
        minut2Compare = _sdfCompare.format(mydate);
        int currentMinut = Integer.parseInt(minut2Compare);
        String counterStr = "";

        if (currentMinut != _prevMinut) {
            _counter = 1;
            _prevMinut = currentMinut;
        } else if (_counter >= 99999) {
            _counter = 1;
        } else
            _counter++;

        counterStr = String.valueOf(_counter);
        int counterLen = counterStr.length();
        int tmpLength = inTxnLength - counterLen;
        if (counterLen < inTxnLength) {
            for (int i = 0; i < tmpLength; i++)
                counterStr = "0" + counterStr;
        }
        return txnIdPrefix + counterStr;
    }

    /*
     * public static void main(String[] arg)
     * {
     * String x="\r\n";
     * System.out.println("dkldfjdfkjfd"+x);
     * System.out.println(x);
     * 
     * }
     */
}
