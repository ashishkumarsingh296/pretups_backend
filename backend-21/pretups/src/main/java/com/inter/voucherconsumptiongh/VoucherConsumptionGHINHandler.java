/*
 * Created on Jun 10, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.voucherconsumptiongh;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
import com.inter.comverse.ComverseI;
import com.inter.vodafoneghana.voucherconsumptiongh.VoucherConsumptionGHI;
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
    private String _inReconID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private String _inTXNID = null;

    public void validityAdjust(HashMap p_map) throws BTSLBaseException, Exception {
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
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }

            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));

            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            _inReconID = getINReconID();

            _requestMap.put("IN_TXN_ID", _referenceID);

            if ("N".equals(validateRequired)) {
                // Setting default Response;
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }

            setInterfaceParameters(VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsumptionGHINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();

            // sending the AccountInfo request to IN along with validate action
            // defined in interface
            // sendRequestToIN(VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS);
            // set TRANSACTION_STATUS as Success in request map

            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATION_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherConsumptionGHINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.VALIDATION_ERROR);
        } finally {
            if (InterfaceUtil.NullToString((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS)) {
                try {
                    if (!InterfaceUtil.isNullString((String) p_requestMap.get("SUBSCRIBER_CELL_SWITCH_ID_REQ")) && "Y".equalsIgnoreCase((String) p_requestMap.get("SUBSCRIBER_CELL_SWITCH_ID_REQ"))) {
                        _requestMap.put("IN_START_TIME", "0");
                        _requestMap.put("IN_END_TIME", "0");

                        String inFileId = FileCache.getValue(_interfaceID, (String) _requestMap.get("GHANA_CELLID_IN_IDS"));

                        if (InterfaceUtil.isNullString(inFileId)) {
                            _log.error("credit", "GHANA_CELLID_IN_IDS  is not defined in the INFile");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsumptionGHINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "GHANA_CELLID_IN_IDS  is not defined in the INFile");
                            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.AMBIGOUS);
                        }
                        _requestMap.put("INTERFACE_ID", inFileId);
                        // _requestMap=new
                        // ClaroPromoWSINHandler().PromoCredit(_requestMap);
                        _requestMap.put("INTERFACE_ID", _interfaceID);
                        _requestMap.put("IN_CELLID_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                    } else if (TransactionLog.getLogger().isDebugEnabled())
                        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "VoucherConsumptionGHINHandler[validation]", "Credit", " Cell ID request will go to IN.", "");
                } catch (BTSLBaseException be) {
                    p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsumptionGHINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While Fetching Cell ID, get the Base Exception be:" + be.getMessage());
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While Fetching Cell ID get the Exception e:" + e.getMessage());
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                } finally {
                }

                if (_log.isDebugEnabled())
                    _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
                if (TransactionLog.getLogger().isDebugEnabled())
                    TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "VoucherConsumptionGHINHandler[validate]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");

            }
        }
    }

    /**
     * This method is used to credit the Subscriber
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        int retryCountCredit = 0;
        _requestMap = p_requestMap;
        try {

            final String methodName = "VoucherConsumptionGHINHandler[credit]";
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Entered p_requestMap: " + p_requestMap);

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
                    _requestMap.put("IN_START_TIME", "0");
                    _requestMap.put("IN_END_TIME", "0");

                    String inFileId = FileCache.getValue(_interfaceID, "GHANA_VOMS_IN_IDS");

                    if (InterfaceUtil.isNullString(inFileId)) {
                        _log.error(methodName, "GHANA_VOMS_IN_IDS  is not defined in the INFile");
                        // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                        // EventComponentI.INTERFACES, EventStatusI.RAISED,
                        // EventLevelI.FATAL, methodName,_referenceID,_msisdn
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
                // While sending the amount to IN, it would be multiplied by
                // this factor, and recieved balance would be devided by this
                // factor.
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
                        systemAmtDouble = Double.parseDouble((String) _requestMap.get("TOPUP"));
                    }

                    amountStr = String.valueOf(systemAmtDouble);
                    // Based on the INFiles ROUND_FLAG flag, we have to decide
                    // to round the transfer amount or not.
                    String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                    // If the ROUND_FLAG is not defined in the INFile
                    if (InterfaceUtil.isNullString(roundFlag)) {
                        roundFlag = "Y";
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, methodName, _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                    }
                    // If rounding of amount is allowed, round the amount value
                    // and put this value in request map.
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
                // set transfer_amount in request map as amountStr (which is
                // round value of INTERFACE_AMOUNT)
                _requestMap.put("transfer_amount", amountStr);

                _requestMap.put("VALIDITY_DAYS", (String) FileCache.getValue(_interfaceID, "VALIDITY_DAYS"));
                _requestMap.put("IN_RECON_ID", (String) FileCache.getValue(_interfaceID, "IN_RECON_ID"));
                String inStr = _formatter.generateRequest(VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT, _requestMap);

                // sending the Re-charge request to IN along with re-charge
                // action defined in VoucherConsumptionGHTRI interface
                sendRequestToIN(inStr, VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT);

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
                                // EventComponentI.INTERFACES,
                                // EventStatusI.RAISED, EventLevelI.FATAL,
                                // methodName,_referenceID,_msisdn, (String)
                                // _requestMap.get("NETWORK_CODE"),
                                // "While VMS Rollback, get the Base Exception be:"+be.getMessage());
                            } catch (Exception e) {
                                _log.error(methodName, "Exception e:" + e.getMessage());
                                e.printStackTrace();
                                // EventHandler.handle(EventIDI.SYSTEM_ERROR,
                                // EventComponentI.INTERFACES,
                                // EventStatusI.RAISED, EventLevelI.FATAL,
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

        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsumptionGHINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit, get the Base Exception be:" + be.getMessage());
            _requestMap.put("TRANSACTION_STATUS", be.getMessage());
            throw new BTSLBaseException(this, "credit", be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "VoucherConsumptionGHINHandler[credit]", "credit complete.", " _requestMap string:" + _requestMap.toString(), "", "");
        }

        if (_log.isDebugEnabled())
            _log.debug("credit", "Exiting _interfaceID" + _interfaceID + "_referenceID" + _referenceID + "_msisdn" + _msisdn + "requestMap=" + _requestMap.toString());
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

    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr::" + p_inRequestStr + " p_action::" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
        String responseStr = "";
        UrlConnection comverseURLConnection = null;
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        int connectTimeOut = 0;
        int readTimeOut = 0;
        String url = "";
        String inReconID = null;
        int retryCount = 0;
        try {
            _responseMap = new HashMap();
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            try {
                long startTimeNode = System.currentTimeMillis();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                try {
                    warnTime = Long.parseLong(_requestMap.get("WARN_TIMEOUT").toString());
                    url = _requestMap.get("COMV_SOAP_URL").toString();
                    String keepAlive = _requestMap.get("KEEP_ALIVE").toString();
                    String soapAction = "";
                    if (p_action == VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT) {
                        soapAction = "NonVoucherRecharge";
                        readTimeOut = Integer.parseInt(FileCache.getValue(_interfaceID, "READ_TIMEOUT_CREDIT"));
                        retryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "RETRY_COUNT_CREDIT"));
                    }
                    connectTimeOut = Integer.parseInt(_requestMap.get("CONNECT_TIME_OUT").toString());
                    long length = p_inRequestStr.length();

                    int i = 0;
                    while (i++ < retryCount) {
                        comverseURLConnection = new UrlConnection(url, connectTimeOut, readTimeOut, keepAlive, length, soapAction);
                    }
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + url + "] created");
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                    _log.error("sendRequestToIN", "Exception while creating connection e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for Comverse IN with INTERFACE_ID=[" + _interfaceID + "]");
                    throw e;
                }

                long totalTimeNode = System.currentTimeMillis() - startTimeNode;

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Total time to find the scheduled Node totalTimeNode: " + totalTimeNode);
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]" + " Exception ::" + e.getMessage());
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                PrintWriter out = comverseURLConnection.getPrintWriter();
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e::" + e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to Comverse IN IN INTERFACE_ID=[" + _interfaceID + "] Exception::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
            }

            try {
                StringBuffer buffer = new StringBuffer();
                String response = "";
                try {
                    // Get the response from the IN
                    comverseURLConnection.setBufferedReader();
                    BufferedReader in = comverseURLConnection.getBufferedReader();
                    // Reading the response from buffered reader.
                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    if (warnTime <= (endTime - startTime)) {
                        if (_log.isInfoEnabled())
                            _log.info("sendRequestToIN", "WARN time reaches, startTime::" + startTime + " endTime::" + endTime + " From file cache warnTime::" + warnTime + " time taken (endTime-startTime)::" + (endTime - startTime));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ComverseINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Comverse IN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the Comverse IN for INTERFACE_ID=[" + _interfaceID + "] " + "Exception=" + e.getMessage());
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + readTimeOut);
                }
                responseStr = buffer.toString();

                // for testing of AMBIGOUS cases only
                // if(p_action != ComverseI.ACTION_ACCOUNT_DETAILS)
                // responseStr=null;

                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr::" + responseStr);

                String httpStatus = comverseURLConnection.getResponseCode();
                _requestMap.put("PROTOCOL_STATUS", httpStatus);
                if (!ComverseI.HTTP_STATUS_200.equals(httpStatus))
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                // Check if there is no response, handle the event showing Blank
                // response from Comverse stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from Comverse IN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                _responseMap = _parser.parseResponse(p_action, responseStr);

                String faultCode = (String) _responseMap.get("faultCode");
                if (!InterfaceUtil.isNullString(faultCode)) {
                    // Log the value of executionStatus for corresponding
                    // msisdn,recieved from the response.
                    if (_log.isInfoEnabled())
                        _log.info("sendRequestToIN", "faultCode::" + faultCode + "_inTXNID::" + _inTXNID + " _msisdn::" + _msisdn);
                    _requestMap.put("INTERFACE_STATUS", faultCode);// Put the
                                                                   // interface_status
                                                                   // in
                                                                   // requestMap
                    _log.error("sendRequestToIN", "faultCode=" + _responseMap.get("faultCode") + "faultString = " + _responseMap.get("faultString"));
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, (String) _responseMap.get("faultString"));
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }

                String errorCode = (String) _responseMap.get("ErrorCode");
                if (InterfaceUtil.isNullString(errorCode) || !errorCode.equals("0")) {
                    _requestMap.put("INTERFACE_STATUS", errorCode);// Put the
                                                                   // interface_status
                                                                   // in
                                                                   // requestMap
                    if (ComverseI.SUBSCRIBER_NOT_FOUND.equals(errorCode)) {
                        _log.error("sendRequestToIN", "Subscriber not found with MSISDN::" + _msisdn);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber is not found at IN");
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }// end of checking the subscriber existance.
                    else {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + errorCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + errorCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                }
                String responseCode = null;
                Object[] successList = null;
                if (p_action == ComverseI.ACTION_ACCOUNT_DETAILS) {
                    responseCode = (String) _responseMap.get("State");
                    successList = _requestMap.get("Comv_Soap_Rchg_State").toString().split(",");
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        _log.error("sendRequestToIN", "Subscriber barred at IN::" + _msisdn);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber BARRED at IN");
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);
                    }
                } else if (p_action == ComverseI.ACTION_RECHARGE_CREDIT) {
                    responseCode = _responseMap.get("NonVoucherRechargeResult").toString();
                    successList = ComverseI.RESULT_OK.split(",");
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                } else if (p_action == ComverseI.ACTION_IMMEDIATE_DEBIT) {
                    responseCode = _responseMap.get("NonVoucherRechargeResult").toString();
                    successList = ComverseI.RESULT_OK.split(",");
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                } else if (p_action == ComverseI.ACTION_IMMEDIATE_CREDIT) {
                    responseCode = _responseMap.get("NonVoucherRechargeResult").toString();
                    successList = ComverseI.RESULT_OK.split(",");
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                } else if (p_action == ComverseI.ACTION_LANGUAGE_CODE) {
                    responseCode = (String) _responseMap.get("State");
                    successList = _requestMap.get("Comv_Soap_Rchg_State").toString().split(",");
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        _log.error("sendRequestToIN", "Subscriber barred at IN::" + _msisdn);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber BARRED at IN");
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);
                    }
                }
                // _requestMap.put("INTERFACE_STATUS",responseCode);
                _requestMap.put("INTERFACE_STATUS", errorCode);
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                // Closing the HttpUrl connection
                if (comverseURLConnection != null)
                    comverseURLConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "While closing Comverse IN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and IP=[" + url + "]");
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting  _interfaceID::" + _interfaceID + " Stage::" + p_action + " responseStr::" + responseStr);
        }

    }

    public void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered Action =" + p_action);
        try {

            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CURRENCY", currency.trim());

            String stateAllowed = FileCache.getValue(_interfaceID, "Comv_Soap_Rchg_State");
            if (InterfaceUtil.isNullString(stateAllowed)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Comv_Soap_Rchg_State is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Comv_Soap_Rchg_State", stateAllowed.trim());

            String valAction = FileCache.getValue(_interfaceID, "Soap_Val_Action");
            if (InterfaceUtil.isNullString(valAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Soap_Val_Action is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Soap_Val_Action", valAction.trim());

            String topAction = FileCache.getValue(_interfaceID, "Soap_Top_Action");
            if (InterfaceUtil.isNullString(topAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Soap_Top_Action is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Soap_Top_Action", topAction.trim());

            String adjAction = FileCache.getValue(_interfaceID, "Soap_Adj_Action");
            if (InterfaceUtil.isNullString(adjAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Soap_Adj_Action is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Soap_Adj_Action", adjAction.trim());

            String id = FileCache.getValue(_interfaceID, "COMV_INIT_ID");
            if (InterfaceUtil.isNullString(id)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_INIT_ID is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("COMV_INIT_ID", id.trim());

            String pwd = FileCache.getValue(_interfaceID, "COMV_INIT_PASSWORD");
            if (InterfaceUtil.isNullString(pwd)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_INIT_PASSWORD is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("COMV_INIT_PASSWORD", pwd.trim());

            String url = FileCache.getValue(_interfaceID, "COMV_SOAP_URL");
            if (InterfaceUtil.isNullString(url)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_SOAP_URL is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("COMV_SOAP_URL", url.trim());

            String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
            if (InterfaceUtil.isNullString(keepAlive)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "KEEP_ALIVE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("KEEP_ALIVE", keepAlive.trim());

            String valSoapAction = FileCache.getValue(_interfaceID, "SoapAction_VAL");
            if (InterfaceUtil.isNullString(valSoapAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "SoapAction_VAL is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SoapAction_VAL", valSoapAction.trim());

            String topSoapAction = FileCache.getValue(_interfaceID, "SoapAction_TOP");
            if (InterfaceUtil.isNullString(topSoapAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "SoapAction_TOP is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SoapAction_TOP", topSoapAction.trim());

            String adjSoapAction = FileCache.getValue(_interfaceID, "SoapAction_ADJ");
            if (InterfaceUtil.isNullString(adjSoapAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "SoapAction_ADJ is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SoapAction_ADJ", adjSoapAction.trim());

            String connectTimeout = FileCache.getValue(_interfaceID, "CONNECT_TIME_OUT");
            if (InterfaceUtil.isNullString(connectTimeout)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CONNECT_TIME_OUT is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CONNECT_TIME_OUT", connectTimeout.trim());

            /*
             * String readTimeout=FileCache.getValue(_interfaceID,
             * "READ_TIME_OUT");
             * if(InterfaceUtil.isNullString(readTimeout))
             * {
             * EventHandler.handle(EventIDI.SYSTEM_INFO,
             * EventComponentI.INTERFACES, EventStatusI.RAISED,
             * EventLevelI.INFO,
             * "ComverseINHandler[setInterfaceParameters]","REFERENCE ID = "
             * +_referenceID
             * +"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID,
             * "Network code "+(String) _requestMap.get("NETWORK_CODE") ,
             * "READ_TIME_OUT is not defined in IN File ");
             * throw new
             * BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI
             * .ERROR_CONFIG_PROBLEM);
             * }
             * _requestMap.put("READ_TIME_OUT",readTimeout.trim());
             */

        }// end of try block
         // catch(BTSLBaseException be)
        // {
        // throw be;
        // }
        catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
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

    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("ss");
    private static int _txnCounter = 1;
    private static int _prevSec = 0;
    public int IN_TRANSACTION_ID_PAD_LENGTH = 2;

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
