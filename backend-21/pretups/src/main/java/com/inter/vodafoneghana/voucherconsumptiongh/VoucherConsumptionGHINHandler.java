/*
 * Created on Jun 10, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodafoneghana.voucherconsumptiongh;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.inter.vodafoneghana.locationservice.LSWSINHandler;
import com.inter.voms.ConsumeVoucherINHandler;

/**
 * @author Vipan Kumar
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class VoucherConsumptionGHINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(VoucherConsumptionGHINHandler.class.getName());
    private VoucherConsumptionGHRequestFormatter _formatter = null;
    private VoucherConsumptionGHResponseParser _parser = null;
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    VoucherConsumptionGHError err = new VoucherConsumptionGHError();
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("ss");
    private static int _txnCounter = 1;
    private static int _prevSec = 0;
    public int IN_TRANSACTION_ID_PAD_LENGTH = 2;

    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }

    /**
	 * 
	 */
    public VoucherConsumptionGHINHandler() {
        _formatter = new VoucherConsumptionGHRequestFormatter();
        _parser = new VoucherConsumptionGHResponseParser();
    }

    /**
     * This method is used to validate the subscriber
     * 
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        final String methodName = "VoucherConsumptionGHINHandler[validate]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String amountStr = "";
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }
            // Fetch value of VALIDATION key from IN File. (this is used to
            // ensure that validation will be done on IN or not)
            // If validation of subscriber is not required set the SUCCESS code
            // into request map and return.
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));

            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            _requestMap.put("IN_TXN_ID", _referenceID);

            setInterfaceParameters(VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS);

            String multFactor = FileCache.getValue(_interfaceID, "RES_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error(methodName, "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Befor generating Request format validateRequired=" + validateRequired);
            Object object = _formatter.generateRequest(VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS, _requestMap);
            _requestMap.put("requestObject", object);
            // sending the AccountInfo request to IN along with validate action
            // defined in interface
            if ("Y".equals(validateRequired)) {
                sendRequestToIN(VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS);
            }
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            // get value of BALANCE from response map (BALANCE was set in
            // response map in sendRequestToIN method.)
            try {
                amountStr = (Double) _responseMap.get("RESP_BALANCE") + "";
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
            } catch (Exception e) {
                _log.error(methodName, "Exception e:" + e.getMessage() + " amountStr:" + amountStr);
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL, methodName,_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "Balance obtained from the IN is not numeric amountStr=:"+amountStr+", while parsing the Balance get Exception e:"+e.getMessage());
                _requestMap.put("INTERFACE_PREV_BALANCE", "00");
            }

            String expdate = "";
            try {
                expdate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("OLD_EXPIRY_DATE")).getTime(), "yyyyMMdd");
            } catch (Exception e) {
                _log.error(methodName, "Exception e:" + e.getMessage() + " Exp date:" + expdate);
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL, methodName,_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "Balance obtained from the IN is not numeric amountStr=:"+amountStr+", while parsing the date get Exception e:"+e.getMessage());
                expdate = BTSLUtil.getDateTimeStringFromDate(new Date(), "yyyyMMdd");
            }

            _requestMap.put("OLD_EXPIRY_DATE", expdate);

            if (_responseMap != null && _responseMap.get("ACCOUNT_STATUS") != null && !BTSLUtil.isNullString((String) _responseMap.get("ACCOUNT_STATUS")))
                _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("ACCOUNT_STATUS"));
            else
                _requestMap.put("ACCOUNT_STATUS", "Active");

            if (_responseMap != null && _responseMap.get("SERVICE_CLASS") != null && !BTSLUtil.isNullString((String) _responseMap.get("SERVICE_CLASS")))
                _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("SERVICE_CLASS"));
            else
                _requestMap.put("SERVICE_CLASS", "ALL");

        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be=" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validating the subscriber, get BTSLBaseException e:" + be.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATION_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error(methodName, "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.VALIDATION_ERROR);
        } finally {
            if (InterfaceUtil.NullToString((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS)) {
                try {
                    if ("Y".equalsIgnoreCase(_requestMap.get("SUBSCRIBER_CELL_SWITCH_ID_REQ").toString())) {
                        _requestMap.put("IN_START_TIME", "0");
                        _requestMap.put("IN_END_TIME", "0");

                        String inFileId = FileCache.getValue(_interfaceID, "GHANA_LS_IN_IDS");

                        if (InterfaceUtil.isNullString(inFileId)) {
                            _log.error(methodName, "GHANA_LS_IN_IDS  is not defined in the INFile");
                            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                            // EventComponentI.INTERFACES, EventStatusI.RAISED,
                            // EventLevelI.FATAL,
                            // methodName,_referenceID,_msisdn
                            // +" INTERFACE ID = "+_interfaceID, (String)
                            // _requestMap.get("NETWORK_CODE"),
                            // "GHANA_LS_IN_IDS  is not defined in the INFile");
                            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.AMBIGOUS);
                        }
                        _requestMap.put("INTERFACE_ID", inFileId);
                        new LSWSINHandler().locationServiceCredit(_requestMap);
                        _requestMap.put("LS_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                        _requestMap.put("INTERFACE_ID", _interfaceID);
                        _requestMap.put("IN_LS_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                    } else if (TransactionLog.getLogger().isDebugEnabled())
                        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), methodName, _referenceID, " Cell ID request will not go to IN.", "");
                } catch (BTSLBaseException be) {
                    _log.error(methodName, "BTSLBaseException be=" + be.getMessage());
                    p_requestMap.put("LS_AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While processing Cell ID, get the Base Exception be:" + be.getMessage());
                } catch (Exception e) {
                    _log.error(methodName, "BTSLBaseException be=" + e.getMessage());
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsumptionGHWSINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
                } finally {
                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Exit p_requestMap:" + p_requestMap);
                }

            }

            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), methodName, "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
    }

    /**
     * This method is used to credit the Subscriber
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        final String methodName = "VoucherConsumptionGHINHandler[credit]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        int retryCountCredit = 0;
        _requestMap = p_requestMap;

        String voucherCreditRequired = "";

        try {
            _requestMap.put("IN_START_TIME", "0");
            _requestMap.put("IN_END_TIME", "0");
            _requestMap.put("IN_RECHARGE_TIME", "0");
            _requestMap.put("IN_CREDIT_VAL_TIME", "0");

            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            // _requestMap.put("IN_TXN_ID",_referenceID);

            _requestMap.put("VMS_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            _msisdn = (String) _requestMap.get("MSISDN");

            if (!BTSLUtil.isNullString(_msisdn)) {
                _msisdn = InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }

            _requestMap.put("MSISDN", _msisdn);

            voucherCreditRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE").toString());

            try {
                if ("Y".equalsIgnoreCase(voucherCreditRequired)) {
                    if (!InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "VMS_VOUCHER_CONSUMPTION_REQ")) && "Y".equalsIgnoreCase(FileCache.getValue(_interfaceID, "VMS_VOUCHER_CONSUMPTION_REQ"))) {
                        _requestMap.put("IN_START_TIME", "0");
                        _requestMap.put("IN_END_TIME", "0");

                        String inFileId = FileCache.getValue(_interfaceID, "GHANA_VOMS_IN_IDS");

                        if (InterfaceUtil.isNullString(inFileId)) {
                            _log.error(methodName, "GHANA_VOMS_IN_IDS  is not defined in the INFile");
                            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                            // EventComponentI.INTERFACES, EventStatusI.RAISED,
                            // EventLevelI.FATAL,
                            // methodName,_referenceID,_msisdn
                            // +" INTERFACE ID = "+_interfaceID, (String)
                            // _requestMap.get("NETWORK_CODE"),
                            // "GHANA_VOMS_IN_IDS  is not defined in the INFile");
                            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.AMBIGOUS);
                        }
                        _requestMap.put("INTERFACE_ID", inFileId);
                        new ConsumeVoucherINHandler().credit(_requestMap);
                        _requestMap.put("VMS_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

                        _requestMap.put("INTERFACE_ID", _interfaceID);
                        _requestMap.put("IN_VOMS_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                    } else if (TransactionLog.getLogger().isDebugEnabled())
                        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), methodName, methodName, " VOms request will not go to IN.", "");

                }
            } catch (BTSLBaseException be) {
                _log.error(methodName, "BTSLBaseException be=" + be.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While Fetching Cell ID, get the Base Exception be:" + be.getMessage());
                _requestMap.put("VMS_TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
            } catch (Exception e) {
                _log.error(methodName, "BTSLBaseException be=" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsumptionGHWSINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While Fetching Cell ID get the Exception e:" + e.getMessage());
                _requestMap.put("VMS_TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
            }

            if (InterfaceUtil.NullToString((String) _requestMap.get("VMS_TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.FAIL)) {
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.VOMS_ERROR_UPDATION);
            } else {
                _requestMap.remove("TRANSACTION_STATUS");
                _requestMap.remove("INTERFACE_STATUS");
            }

            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error(methodName, "MULT_FACTOR  is not defined in the INFile");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL, methodName,_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);
            String resMultFactor = FileCache.getValue(_interfaceID, "RES_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "resMultFactor: " + resMultFactor);
            if (InterfaceUtil.isNullString(resMultFactor)) {
                _log.error(methodName, "RES_MULT_FACTOR  is not defined in the INFile");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL,
                // "VoucherConsumptionGHINHandler[validate]",_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "RES_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            resMultFactor = resMultFactor.trim();

            setInterfaceParameters(VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT);

            try {
                multFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = 0.0;

                if (BTSLUtil.isNullString((String) _requestMap.get("TOPUP"))) {
                    interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                    systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
                } else {
                    interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("TOPUP"));
                }

                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, methodName, _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error(methodName, "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            Object object = _formatter.generateRequest(VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT, _requestMap);
            _requestMap.put("requestObject", object);

            // sending the Re-charge request to IN along with re-charge action
            // defined in VoucherConsumptionGHTRI interface
            sendRequestToIN(VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT);

            String resamountStr = "";
            try {
                resamountStr = (String) _responseMap.get("POST_BALANCE") + "";
                resamountStr = InterfaceUtil.getSystemAmountFromINAmount(resamountStr, Double.parseDouble(resMultFactor));
                _requestMap.put("INTERFACE_POST_BALANCE", resamountStr);
            } catch (Exception e) {
                _log.error(methodName, "Exception e:" + e.getMessage() + " resamountStr:" + resamountStr);
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL,
                // "VoucherConsumptionGHINHandler[validate]",_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "Balance obtained from the IN is not numeric amountStr=:"+resamountStr+", while parsing the Balance get Exception e:"+e.getMessage());
                _requestMap.put("INTERFACE_POST_BALANCE", "00");
            }
            // set IN_RECHARGE_STATUS as Success in request map
            _requestMap.put("RECHARGE_ENQUIRY", "N");
            _requestMap.put("IN_RECHARGE_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", _responseMap.get("INTERFACE_STATUS"));
            _requestMap.put("INTERFACE_POST_BALANCE", _responseMap.get("POST_BALANCE"));
            String newExpdate = "";
            try {
                newExpdate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("NEW_EXPIRY_DATE")).getTime(), "yyyyMMdd");
            } catch (Exception e) {
                _log.error(methodName, "Exception e:" + e.getMessage() + " newExpdate:" + _responseMap.get("NEW_EXPIRY_DATE"));
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL, methodName,_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "expdate obtained from the IN is in format=:"+_responseMap.get("NEW_EXPIRY_DATE")+", while parsing the date get Exception e:"+e.getMessage());
                newExpdate = BTSLUtil.getDateTimeStringFromDate(new Date(), "yyyyMMdd");
            }
            _requestMap.put("NEW_EXPIRY_DATE", newExpdate);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");

        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be=" + be.getMessage());
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "CR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                _log.error(methodName, "Exception e:" + e.getMessage());
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.SYSTEM, EventStatusI.RAISED,
                // EventLevelI.FATAL, "VoucherConsumptionGH[credit]",
                // _referenceID,_msisdn, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

        } catch (Exception e) {
            _log.error(methodName, "BTSLBaseException be=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsumptionGHWSINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if ("Y".equalsIgnoreCase(voucherCreditRequired)) {
                if (InterfaceUtil.NullToString((String) _requestMap.get("VMS_TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS)) {
                    if (!InterfaceUtil.NullToString((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS)) {
                        try {
                            if (!InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "VMS_VOUCHER_ROLLBACK_REQ")) && "Y".equalsIgnoreCase(FileCache.getValue(_interfaceID, "VMS_VOUCHER_ROLLBACK_REQ"))) {
                                _requestMap.put("IN_START_TIME", "0");
                                _requestMap.put("IN_END_TIME", "0");

                                String inFileId = FileCache.getValue(_interfaceID, "GHANA_VOMS_IN_IDS");

                                if (InterfaceUtil.isNullString(inFileId)) {
                                    _log.error(methodName, "GHANA_VOMS_IN_IDS  is not defined in the INFile");
                                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "GHANA_VOMS_IN_IDS  is not defined in the INFile");
                                    throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.AMBIGOUS);
                                }
                                _requestMap.put("INTERFACE_ID", inFileId);
                                new ConsumeVoucherINHandler().debit(_requestMap);
                                _requestMap.put("VMS_ROLLBACK_STATUS", InterfaceErrorCodesI.SUCCESS);
                                _requestMap.put("INTERFACE_ID", _interfaceID);
                                _requestMap.put("IN_VOMS_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                            } else if (TransactionLog.getLogger().isDebugEnabled())
                                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), methodName, methodName, " Cell ID request will go to IN.", "");
                        } catch (BTSLBaseException be) {
                            _log.error(methodName, "BTSLException e:" + be.getMessage());
                            p_requestMap.put("VOMS_AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
                            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                            // EventComponentI.INTERFACES, EventStatusI.RAISED,
                            // EventLevelI.FATAL,
                            // methodName,_referenceID,_msisdn, (String)
                            // _requestMap.get("NETWORK_CODE"),
                            // "While VMS Rollback, get the Base Exception be:"+be.getMessage());
                        } catch (Exception e) {
                            _log.error(methodName, "Exception e:" + e.getMessage());
                            e.printStackTrace();
                            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                            // EventComponentI.INTERFACES, EventStatusI.RAISED,
                            // EventLevelI.FATAL,
                            // "VoucherConsumptionGHWSINHandler[credit]",_referenceID,_msisdn,
                            // (String) _requestMap.get("NETWORK_CODE"),
                            // "While VMS Rollback get the Exception e:"+e.getMessage());
                        } finally {
                        }

                    }
                    // For Rollback End
                }
            }
        }

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Exited _requestMap=" + _requestMap);
        if (TransactionLog.getLogger().isDebugEnabled())
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), methodName, "request complete.", " _requestMap string:" + _requestMap.toString(), "", "");

    }

    /**
	 * 
	 */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }

    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }

    /**
     * This method used to send the request to the IN
     * 
     * @param p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        final String methodName = "VoucherConsumptionGHINHandler[sendRequestToIN]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, " p_action=" + p_action + " __msisdn=" + _msisdn);

        String actionLevel = "";
        switch (p_action) {
        case VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS: {
            actionLevel = "ACTION_ACCOUNT_DETAILS";
            break;
        }
        case VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT: {
            actionLevel = "ACTION_RECHARGE_CREDIT";
            break;
        }
        case VoucherConsumptionGHI.ACTION_IMMEDIATE_DEBIT: {
            actionLevel = "ACTION_IMMEDIATE_DEBIT";
            break;
        }
        case VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT_BUNDLE: {
            actionLevel = "ACTION_RECHARGE_CREDIT_BUNDLE";
            break;
        }
        case VoucherConsumptionGHI.ACTION_BLOCK_SUBSCRIBER: {
            actionLevel = "ACTION_BLOCK_SUBSCRIBER";
            break;
        }

        }
        if (!BTSLUtil.isNullString(_msisdn)) {
            InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
        }

        if (_log.isDebugEnabled())
            _log.debug(methodName, " p_action=" + actionLevel + " __msisdn=" + _msisdn);

        long startTime = 0, endTime = 0, sleepTime, warnTime = 0;
        LDAPConnection clientStub = null;

        boolean alternateInterface = false;
        VoucherConsumptionGHPoolUtil serviceConnection = null;
        try {
            clientStub = serviceConnection.getConnection(_interfaceID);
            if (clientStub == null || !clientStub.isConnected()) {
                // EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String)
                // _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object for Interface Id"+_interfaceID);
                _log.error(methodName, "Unable to get Client Object for Interface Id" + _interfaceID);
                if (_requestMap.get("LDAP_ALTERNATE_INTERFACE_REQ").toString().equalsIgnoreCase("Y")) {
                    alternateInterface = true;
                    _log.debug(methodName, "Unable to get Client Object for Interface Id" + _interfaceID + " , Looking for client object in alternate interface" + _requestMap.get("LDAP_ALTERNATE_INTERFACE_ID"));
                    clientStub = serviceConnection.getConnection(_requestMap.get("LDAP_ALTERNATE_INTERFACE_ID").toString());
                    if (clientStub == null) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Unable to get Client Object for Alternate Interface Id" + _requestMap.get("LDAP_ALTERNATE_INTERFACE_ID").toString());
                        _log.error(methodName, "Unable to get Client Object for Interface Id" + _interfaceID);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    }
                } else {
                    _log.error(methodName, "Exception e:" + InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                }
            }
            try {
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                String requestStr = (String) _requestMap.get("requestObject");
                LDAPSearchResults ldSearchResult = clientStub.search(clientStub.getAuthenticationDN(), LDAPConnection.SCOPE_ONE, requestStr, null, false);
                _requestMap.put("RESPONSE_OBJECT", ldSearchResult);

            } catch (LDAPException re) {
                re.printStackTrace();
                _log.error(methodName, "Exception e:" + re.getMessage());
                String be = err.mapError(re.getLDAPResultCode());
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "LDAP Error Code=" + re.getLDAPErrorMessage());
                throw new BTSLBaseException(be, "index");
            } catch (Exception e) {
                _log.error(methodName, "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Exception Error Message:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } finally {
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                _log.error(methodName, "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);

            }
            _responseMap = _parser.parseResponse(p_action, _requestMap);
            // put value of response
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response Map: " + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + actionLevel);
            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            if (endTime - startTime >= warnTime) {
                _log.info(methodName, "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,methodName,"REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String)
                // _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel," IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
            }
            String status = (String) _responseMap.get("INTERFACE_STATUS");
            _requestMap.put("INTERFACE_STATUS", status);
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.error(methodName, "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            _requestMap.remove("RESPONSE_OBJECT");
            if (alternateInterface) {
                serviceConnection.retunConnection(_requestMap.get("LDAP_ALTERNATE_INTERFACE_ID").toString(), clientStub);
            } else {
                serviceConnection.retunConnection(_interfaceID, clientStub);
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting p_action=" + p_action);
        }// end of finally
    }

    public void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        final String methodName = "VoucherConsumptionGHINHandler[sendRequestToIN]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered Action =" + p_action);
        try {
            String subscriberSwitchId = FileCache.getValue(_interfaceID, "SUBSCRIBER_CELL_SWITCH_ID_REQ");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "SUBSCRIBER_CELL_SWITCH_ID_REQ:" + subscriberSwitchId);
            if (InterfaceUtil.isNullString(subscriberSwitchId)) {
                _log.error(methodName, "SUBSCRIBER_CELL_SWITCH_ID_REQ  is not defined in the INFile");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL, methodName,_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "SUBSCRIBER_CELL_SWITCH_ID_REQ  is not defined in the INFile");
                subscriberSwitchId = "N";
            }
            subscriberSwitchId = subscriberSwitchId.trim();
            _requestMap.put("SUBSCRIBER_CELL_SWITCH_ID_REQ", subscriberSwitchId);

            String alternateInterfaceId = FileCache.getValue(_interfaceID, "LDAP_ALTERNATE_INTERFACE_ID");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "LDAP_ALTERNATE_INTERFACE_ID:" + alternateInterfaceId);
            if (InterfaceUtil.isNullString(alternateInterfaceId)) {
                _log.error(methodName, "LDAP_ALTERNATE_INTERFACE_REQ  is not defined in the INFile");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL, methodName,_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "LDAP_ALTERNATE_INTERFACE_ID  is not defined in the INFile");
                alternateInterfaceId = _interfaceID;
            }
            alternateInterfaceId = alternateInterfaceId.trim();
            _requestMap.put("LDAP_ALTERNATE_INTERFACE_ID", alternateInterfaceId);

            String alternateInterfaceReq = FileCache.getValue(_interfaceID, "LDAP_ALTERNATE_INTERFACE_REQ");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "LDAP_ALTERNATE_INTERFACE_REQ:" + alternateInterfaceReq);
            if (InterfaceUtil.isNullString(alternateInterfaceReq)) {
                _log.error(methodName, "LDAP_ALTERNATE_INTERFACE_REQ  is not defined in the INFile");
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                // EventComponentI.INTERFACES, EventStatusI.RAISED,
                // EventLevelI.FATAL, methodName,_referenceID,_msisdn
                // +" INTERFACE ID = "+_interfaceID, (String)
                // _requestMap.get("NETWORK_CODE"),
                // "LDAP_ALTERNATE_INTERFACE_REQ  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            alternateInterfaceReq = alternateInterfaceReq.trim();
            _requestMap.put("LDAP_ALTERNATE_INTERFACE_REQ", alternateInterfaceReq);

            String creditsid = FileCache.getValue(_interfaceID, "creditSid");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditSid:" + creditsid);
            if (InterfaceUtil.isNullString(creditsid)) {
                _log.error(methodName, "creditsid  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditSid  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditsid = creditsid.trim();
            _requestMap.put("creditSid", creditsid);

            String creditACTION = FileCache.getValue(_interfaceID, "creditACTION");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditACTION:" + creditACTION);
            if (InterfaceUtil.isNullString(creditACTION)) {
                _log.error(methodName, "creditACTION  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditACTION  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditACTION = creditACTION.trim();
            _requestMap.put("creditACTION", creditACTION);

            String creditNeg_Credit = FileCache.getValue(_interfaceID, "creditNeg_Credit");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditNeg_Credit:" + creditNeg_Credit);
            if (InterfaceUtil.isNullString(creditNeg_Credit)) {
                _log.error(methodName, "creditNeg_Credit  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditNeg_Credit  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditNeg_Credit = creditNeg_Credit.trim();
            _requestMap.put("creditNeg_Credit", creditNeg_Credit);

            String creditUCL = FileCache.getValue(_interfaceID, "creditUCL");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditUCL:" + creditUCL);
            if (InterfaceUtil.isNullString(creditUCL)) {
                _log.error(methodName, "creditUCL  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditUCL  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditUCL = creditUCL.trim();
            _requestMap.put("creditUCL", creditUCL);

            String creditRecharge = FileCache.getValue(_interfaceID, "creditRecharge");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditRecharge:" + creditRecharge);
            if (InterfaceUtil.isNullString(creditRecharge)) {
                _log.error(methodName, "creditRecharge  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditRecharge  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditRecharge = creditRecharge.trim();
            _requestMap.put("creditRecharge", creditRecharge);

            String creditNo_LC = FileCache.getValue(_interfaceID, "creditNo_LC");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditNo_LC:" + creditNo_LC);
            if (InterfaceUtil.isNullString(creditNo_LC)) {
                _log.error(methodName, "creditNo_LC  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditNo_LC  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditNo_LC = creditNo_LC.trim();
            _requestMap.put("creditNo_LC", creditNo_LC);

            String creditBal = FileCache.getValue(_interfaceID, "creditBal");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditBal:" + creditBal);
            if (InterfaceUtil.isNullString(creditBal)) {
                _log.error(methodName, "creditBal  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditBal  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditBal = creditBal.trim();
            _requestMap.put("creditBal", creditBal);

            String creditAdj = FileCache.getValue(_interfaceID, "creditAdj");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "creditAdj:" + creditAdj);
            if (InterfaceUtil.isNullString(creditAdj)) {
                _log.error(methodName, "creditAdj  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "creditAdj  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditAdj = creditAdj.trim();
            _requestMap.put("creditAdj", creditAdj);

            String creditUserId = FileCache.getValue(_interfaceID, "ADJUSERID");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "ADJUSERID:" + creditUserId);
            if (InterfaceUtil.isNullString(creditUserId)) {
                _log.error(methodName, "ADJUSERID  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ADJUSERID  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            creditUserId = creditUserId.trim();
            _requestMap.put("ADJUSERID", creditUserId);

            String valSid = FileCache.getValue(_interfaceID, "valSid");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "valSid:" + valSid);
            if (InterfaceUtil.isNullString(valSid)) {
                _log.error(methodName, "valSid  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "valSid  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            valSid = valSid.trim();
            _requestMap.put("valSid", valSid);

            String valTbl_name = FileCache.getValue(_interfaceID, "valTbl_name");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "valTbl_name:" + valTbl_name);
            if (InterfaceUtil.isNullString(valTbl_name)) {
                _log.error(methodName, "valTbl_name  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "valTbl_name  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            valTbl_name = valTbl_name.trim();
            _requestMap.put("valTbl_name", valTbl_name);
            String valField_Name = FileCache.getValue(_interfaceID, "valField_Name");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "valField_Name:" + valField_Name);
            if (InterfaceUtil.isNullString(valField_Name)) {
                _log.error(methodName, "valField_Name  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "valField_Name  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            valField_Name = valField_Name.trim();
            _requestMap.put("valField_Name", valField_Name);

            String debitAdj = FileCache.getValue(_interfaceID, "debitAdj");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "debitAdj:" + debitAdj);
            if (InterfaceUtil.isNullString(debitAdj)) {
                _log.error(methodName, "debitAdj  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "debitAdj  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            debitAdj = debitAdj.trim();
            _requestMap.put("debitAdj", debitAdj);

            String bundleSid = FileCache.getValue(_interfaceID, "bundleSid");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "bundleSid:" + bundleSid);
            if (InterfaceUtil.isNullString(bundleSid)) {
                _log.error(methodName, "bundleSid  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "bundleSid  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            bundleSid = bundleSid.trim();
            _requestMap.put("bundleSid", bundleSid);

            String bundleACTION = FileCache.getValue(_interfaceID, "bundleACTION");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "bundleACTION:" + bundleACTION);
            if (InterfaceUtil.isNullString(bundleACTION)) {
                _log.error(methodName, "bundleACTION  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "bundleACTION  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            bundleACTION = bundleACTION.trim();
            _requestMap.put("bundleACTION", bundleACTION);
            String bundlePTP_ID = FileCache.getValue(_interfaceID, "bundlePTP_ID");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "bundlePTP_ID:" + bundlePTP_ID);
            if (InterfaceUtil.isNullString(bundlePTP_ID)) {
                _log.error(methodName, "bundlePTP_ID  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "bundlePTP_ID  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            bundlePTP_ID = bundlePTP_ID.trim();
            _requestMap.put("bundlePTP_ID", bundlePTP_ID);

            String blockerSid = FileCache.getValue(_interfaceID, "blockerSid");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "blockerSid:" + blockerSid);
            if (InterfaceUtil.isNullString(blockerSid)) {
                _log.error(methodName, "blockerSid  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "blockerSid  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            blockerSid = blockerSid.trim();
            _requestMap.put("blockerSid", blockerSid);
            String blockerTbl_name = FileCache.getValue(_interfaceID, "blockerTbl_name");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "blockerTbl_name:" + blockerTbl_name);
            if (InterfaceUtil.isNullString(blockerTbl_name)) {
                _log.error(methodName, "blockerTbl_name  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "blockerTbl_name  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            blockerTbl_name = blockerTbl_name.trim();
            _requestMap.put("blockerTbl_name", blockerTbl_name);

            String blockerError_Indicator = FileCache.getValue(_interfaceID, "blockerError_Indicator");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "blockerError_Indicator:" + blockerError_Indicator);
            if (InterfaceUtil.isNullString(blockerError_Indicator)) {
                _log.error(methodName, "blockerError_Indicator  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "blockerError_Indicator  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            blockerError_Indicator = blockerError_Indicator.trim();
            _requestMap.put("blockerError_Indicator", blockerError_Indicator);

        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited _requestMap:" + _requestMap);
        }// end of finally
    }

    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * 
     * @throws BTSLBaseException
     */
    // private void handleCancelTransaction() throws BTSLBaseException
    // {}

    public synchronized String getINReconID() throws BTSLBaseException {
        // This method will be used when we have transID based on database
        // sequence.
        String inTransactionID = "";
        try {
            String secToCompare = null;
            Date mydate = null;

            mydate = new Date();

            secToCompare = _sdfCompare.format(mydate);
            int currentSec = Integer.parseInt(secToCompare);

            if (currentSec != _prevSec) {
                _txnCounter = 1;
                _prevSec = currentSec;
            } else if (_txnCounter >= 99) {
                _txnCounter = 1;
            } else {
                _txnCounter++;
            }
            if (_txnCounter == 0)
                throw new BTSLBaseException("this", "getINReconID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);

            inTransactionID = BTSLUtil.padZeroesToLeft(String.valueOf(Constants.getProperty("INSTANCE_ID")), IN_TRANSACTION_ID_PAD_LENGTH) + currentTimeFormatStringTillSec(mydate) + BTSLUtil.padZeroesToLeft(String.valueOf(_txnCounter), IN_TRANSACTION_ID_PAD_LENGTH);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _requestMap.put("IN_RECON_ID", inTransactionID);
            _requestMap.put("IN_TXN_ID", inTransactionID);
            return inTransactionID;
        }
    }

    public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * 
     * @throws BTSLBaseException
     */
    private void handleCancelTransaction() throws BTSLBaseException {
        final String methodName = "VoucherConsumptionGHINHandler[handleCancelTransaction]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered.");
        String cancelTxnAllowed = null;
        String cancelTxnStatus = null;
        String reconciliationLogStr = null;
        String cancelCommandStatus = null;
        String cancelNA = null;
        String interfaceStatus = null;
        Log reconLog = null;
        String systemStatusMapping = null;

        try {
            _requestMap.put("REMARK1", FileCache.getValue(_interfaceID, "REMARK1"));
            _requestMap.put("REMARK2", FileCache.getValue(_interfaceID, "REMARK2"));
            // get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug(methodName, "reconLog." + reconLog);
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
                    throw new BTSLBaseException(this, methodName, cancelTxnStatus); // //Based
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
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error(methodName, "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited");
        }
    }
}
