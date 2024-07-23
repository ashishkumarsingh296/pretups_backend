/*
 * Created on Jun 10, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.comversetg;

import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.Calendar;
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
import com.btsl.pretups.inter.comversetg.comversetgstub.BalanceCreditAccount;
import com.btsl.pretups.inter.comversetg.comversetgstub.BalanceEntity;
import com.btsl.pretups.inter.comversetg.comversetgstub.ChangeCOSRequest;
import com.btsl.pretups.inter.comversetg.comversetgstub.ChangeCOSResponse;
import com.btsl.pretups.inter.comversetg.comversetgstub.ServiceSoap_PortType;
import com.btsl.pretups.inter.comversetg.comversetgstub.SubscriberModify;
import com.btsl.pretups.inter.comversetg.comversetgstub.SubscriberRetrieve;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.inter.comversetg.scheduler.NodeManager;
import com.inter.comversetg.scheduler.NodeScheduler;
import com.inter.comversetg.scheduler.NodeVO;

/**
 * @author shamit.jain
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ComverseTGINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(ComverseTGINHandler.class.getName());
    private ComverseTGRequestFormatter _formatter = null;
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inReconID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.

    public void validityAdjust(HashMap p_map) throws BTSLBaseException, Exception {
    }

    /**
	 * 
	 */
    public ComverseTGINHandler() {
        _formatter = new ComverseTGRequestFormatter();
    }

    /**
     * This method is used to validate the subscriber
     * 
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String amountStr = "";
        String promoAmountStr = "";
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
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();

            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTGI.ACTION_ACCOUNT_DETAILS);

            // sending the AccountInfo request to IN along with validate action
            // defined in interface
            sendRequestToIN(ComverseTGI.ACTION_ACCOUNT_DETAILS);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // get value of BALANCE from response map (BALANCE was set in
            // response map in sendRequestToIN method.)
            try {
                amountStr = (Double) _responseMap.get("RESP_BALANCE") + "";
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage() + " amountStr:" + amountStr);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric amountStr=:" + amountStr + ", while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            try {
                promoAmountStr = (Double) _responseMap.get("PROMO_RESP_BALANCE") + "";
                promoAmountStr = InterfaceUtil.getSystemAmountFromINAmount(promoAmountStr, Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PROMO_PREV_BALANCE", promoAmountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage() + " promoAmountStr" + promoAmountStr);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Promo Balance obtained from the IN is not numeric promoAmountStr:" + promoAmountStr + ", while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("IN_CURRENCY", (String) _responseMap.get("IN_CURRENCY"));
            String expdate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
            _requestMap.put("OLD_EXPIRY_DATE", expdate);
            _requestMap.put("CAL_OLD_EXPIRY_DATE", String.valueOf(((Calendar) _responseMap.get("CAL_OLD_EXPIRY_DATE")).getTimeInMillis()));
            expdate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("PROMO_OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
            _requestMap.put("PROMO_OLD_EXPIRY_DATE", expdate);
            _requestMap.put("PROMO_CAL_OLD_EXPIRY_DATE", String.valueOf(((Calendar) _responseMap.get("PROMO_CAL_OLD_EXPIRY_DATE")).getTimeInMillis()));
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("ACCOUNT_STATUS"));
            _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("SERVICE_CLASS"));
            // Shamit Needs to verify
            setLanguageFromMapping();
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATION_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ComverseTGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.VALIDATION_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[validate]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
    }

    /**
     * This method is used to credit the Subscriber
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double comverseMultFactorDouble = 0;
        String amountStr = null;
        int retryCountCredit = 0;
        _requestMap = p_requestMap;
        try {
            // For LOGS
            _requestMap.put("IN_START_TIME", "0");
            _requestMap.put("IN_END_TIME", "0");
            _requestMap.put("IN_RECHARGE_TIME", "0");
            _requestMap.put("IN_PROMO_TIME", "0");
            _requestMap.put("IN_COS_TIME", "0");
            _requestMap.put("IN_CREDIT_VAL_TIME", "0");
            _requestMap.put("IN_PROMO_VAL_TIME", "0");
            _requestMap.put("IN_COS_VAL_TIME", "0");
            _requestMap.put("IN_POSTCREDIT_VAL_TIME", "0");
            // --
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);
            _msisdn = (String) _requestMap.get("MSISDN");
            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTGI.ACTION_RECHARGE_CREDIT);
            try {
                comverseMultFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, comverseMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTGINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            sendRequestToIN(ComverseTGI.ACTION_RECHARGE_CREDIT);
            // set IN_RECHARGE_STATUS as Success in request map
            _requestMap.put("RECHARGE_ENQUIRY", "N");
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            _requestMap.put("IN_RECHARGE_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit, get the Base Exception be:" + be.getMessage());
            retryCountCredit = Integer.parseInt((String) _requestMap.get("RETRY_COUNT_CREDIT"));
            _requestMap.put("TRANSACTION_STATUS", be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                if (retryCountCredit > 0) {
                    _requestMap.put("RETRY_COUNT_CREDIT", Integer.valueOf(retryCountCredit--));

                    try {
                        if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                            enquiryForAmbigousTransaction(ComverseTGI.ACTION_RECHARGE_CREDIT);
                        }
                    } catch (BTSLBaseException bex) {
                        /*
                         * _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI
                         * .AMBIGOUS);
                         * throw new
                         * BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                         */// -commented by rahul on 04/04/12 to mark as fail or
                           // amb after enqfor amb
                        throw bex;
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception exc:" + exc.getMessage());
                        throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[credit]", "credit complete.", " _requestMap string:" + _requestMap.toString(), "", "");
        }

        if (InterfaceUtil.NullToString((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS)) {

            try {
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("PROMOTION_AMOUNT"));
                if (_log.isDebugEnabled())
                    _log.debug("credit", "PROMOTION_AMOUNT=" + interfaceAmtDouble + ", In case of 0 promotion credit request will not go to IN.");
                if (interfaceAmtDouble > 0) {
                    _requestMap.put("IN_START_TIME", "0");
                    _requestMap.put("IN_END_TIME", "0");
                    PromotionCredit(_requestMap);
                    _requestMap.put("IN_PROMO_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                } else if (TransactionLog.getLogger().isDebugEnabled())
                    TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[credit]", "PromotionCredit", "Request will not go to IN as PROMOTION_AMOUNT=" + interfaceAmtDouble, "", "");

            } catch (BTSLBaseException be) {
                _log.error("promotion", "BTSLBaseException be=" + be.getMessage());
                // throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("promotion", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[promotion]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While promotion update, get the Exception e:" + e.getMessage());
            } finally {
                // if(_log.isDebugEnabled())
                // _log.debug("promotion","Exiting with  _requestMap: "+_requestMap);
            }

            // call the COS
            if (_requestMap.get("COS_FLAG").equals("Y")) {
                try {
                    String newCos = BTSLUtil.NullToString((String) _requestMap.get("NEW_COS_SERVICE_CLASS"));
                    if (_log.isDebugEnabled())
                        _log.debug("credit", "NEW_COS_SERVICE_CLASS=" + newCos + ", newCos.length=" + newCos.length() + ", In case of 0 length COSUpdate request will not go to IN.");
                    if (newCos.length() > 0) {
                        COSUpdate(_requestMap);
                        _requestMap.put("IN_START_TIME", "0");
                        _requestMap.put("IN_END_TIME", "0");
                        _requestMap.put("IN_COS_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                    } else if (TransactionLog.getLogger().isDebugEnabled())
                        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[credit]", "COSUpdate", "Request will not go to IN as NEW_COS_SERVICE_CLASS=" + newCos, "", "");
                } catch (BTSLBaseException be) {
                    _log.error("COSUpdate", "BTSLBaseException be=" + be.getMessage());
                    // throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("COAUpdate", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[COSUpdate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While COS update, get the Exception e:" + e.getMessage());
                    // throw new
                    // BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                } finally {
                    // if(_log.isDebugEnabled())
                    // _log.debug("COSUpdate","Exiting with  _requestMap: "+_requestMap);
                }
            } else if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[credit]", "COSUpdate", "Request will not go to IN as COS_FLAG=" + _requestMap.get("COS_FLAG"), "", "");

            // post credit enquiry after promotion credit and cos update
            try {
                _requestMap.put("IN_START_TIME", "0");
                _requestMap.put("IN_END_TIME", "0");
                postCreditEnquiry(_requestMap);
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                // lohit
                _requestMap.put("IN_POSTCREDIT_VAL_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[postCreditEnquiry]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While postCreditEnquiry Exception e:" + e.getMessage());
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("credit", "Exiting _interfaceID" + _interfaceID + "_referenceID" + _referenceID + "_msisdn" + _msisdn + "requestMap=" + _requestMap.toString());
    }

    /**
     * 
     * @param p_action
     * @throws BTSLBaseException
     */
    private void enquiryForAmbigousTransaction(int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("enquiryForAmbigousTransaction", "Entered.");
        String actionLevel = "";
        try {
            switch (p_action) {
            case ComverseTGI.ACTION_RECHARGE_CREDIT: {
                actionLevel = "ACTION_RECHARGE_CREDIT";
                String balanceBeforeCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
                String expiryBeforeCredit = _requestMap.get("CAL_OLD_EXPIRY_DATE").toString();
                String oldExipryBeforeCredit = _requestMap.get("OLD_EXPIRY_DATE").toString();
                _requestMap.put("RECHARGE_ENQUIRY", "Y");
                _requestMap.put("IN_START_TIME", "0");
                _requestMap.put("IN_END_TIME", "0");
                validate(_requestMap);
                _requestMap.put("IN_CREDIT_VAL_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                String balanceAfterCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
                String expiryAfterCredit = _requestMap.get("CAL_OLD_EXPIRY_DATE").toString();
                String newExipryBeforeCredit = _requestMap.get("OLD_EXPIRY_DATE").toString();
                double transferAmt = Double.parseDouble((String) _requestMap.get("transfer_amount"));
                if ((transferAmt != 0.0) && Double.parseDouble(balanceAfterCredit) <= Double.parseDouble(balanceBeforeCredit)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                } else {
                    // success the transaction
                    _requestMap.put("INTERFACE_PREV_BALANCE", balanceBeforeCredit);
                    _requestMap.put("INTERFACE_POST_BALANCE", balanceAfterCredit);
                    _requestMap.put("CAL_PREV_OLD_EXPIRY_DATE", expiryBeforeCredit);
                    _requestMap.put("CAL_POST_OLD_EXPIRY_DATE", expiryAfterCredit);
                    _requestMap.put("PREV_OLD_EXPIRY_DATE", oldExipryBeforeCredit);
                    _requestMap.put("NEW_EXPIRY_DATE", newExipryBeforeCredit);
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                }
                break;
            }
            case ComverseTGI.ACTION_PROMOTION_CREDIT: {
                actionLevel = "ACTION_PROMOTION_CREDIT";
                String promotionBalanceBeforePromotionCredit = _requestMap.get("INTERFACE_PROMO_PREV_BALANCE").toString();
                String expiryBeforeCredit = _requestMap.get("PROMO_CAL_OLD_EXPIRY_DATE").toString();
                String oldExipryBeforeCredit = _requestMap.get("PROMO_OLD_EXPIRY_DATE").toString();
                _requestMap.put("PROMOTION_ENQUIRY", "Y");
                _requestMap.put("IN_START_TIME", "0");
                _requestMap.put("IN_END_TIME", "0");
                validate(_requestMap);
                _requestMap.put("IN_PROMO_VAL_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                String promotionBalanceAfterPromotionCredit = _requestMap.get("INTERFACE_PROMO_PREV_BALANCE").toString();
                String expiryAfterCredit = _requestMap.get("PROMO_CAL_OLD_EXPIRY_DATE").toString();
                String newExipryBeforeCredit = _requestMap.get("PROMO_OLD_EXPIRY_DATE").toString();
                if (Double.parseDouble(promotionBalanceAfterPromotionCredit) <= Double.parseDouble(promotionBalanceBeforePromotionCredit)) {
                    _requestMap.put("PROMOTION_STATUS", InterfaceErrorCodesI.FAIL);
                } else {
                    // success the transaction
                    _requestMap.put("INTERFACE_PROMO_PREV_BALANCE", promotionBalanceBeforePromotionCredit);
                    _requestMap.put("INTERFACE_PROMO_POST_BALANCE", promotionBalanceAfterPromotionCredit);
                    _requestMap.put("PROMO_CAL_PREV_OLD_EXPIRY_DATE", expiryBeforeCredit);
                    _requestMap.put("PROMO_CAL_POST_OLD_EXPIRY_DATE", expiryAfterCredit);
                    _requestMap.put("PROMO_PREV_OLD_EXPIRY_DATE", oldExipryBeforeCredit);
                    _requestMap.put("NEW_PROMO_EXPIRY_DATE", newExipryBeforeCredit);
                    _requestMap.put("PROMOTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                }
                break;
            }
            case ComverseTGI.ACTION_COS_UPDATE: {
                actionLevel = "ACTION_COS_UPDATE";
                String previousServiceClass = _requestMap.get("SERVICE_CLASS").toString();
                _requestMap.put("COS_ENQUIRY", "Y");
                _requestMap.put("IN_START_TIME", "0");
                _requestMap.put("IN_END_TIME", "0");
                validate(_requestMap);
                _requestMap.put("IN_COS_VAL_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
                String postServiceClass = _requestMap.get("SERVICE_CLASS").toString();

                if (previousServiceClass.equals(postServiceClass)) {
                    _requestMap.put("COS_STATUS", InterfaceErrorCodesI.FAIL);
                } else {
                    _requestMap.put("INTERFACE_PREV_COS", previousServiceClass);
                    _requestMap.put("INTERFACE_POST_COS", postServiceClass);
                    _requestMap.put("COS_STATUS", InterfaceErrorCodesI.SUCCESS);
                }
                break;
            }
            case ComverseTGI.ACTION_DEBIT_ADJUST: {
                actionLevel = "ACTION_DEBIT_ADJUST";
                String balanceBeforeCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
                _requestMap.put("DEBIT_ENQUIRY", "Y");
                validate(_requestMap);
                String balanceAfterCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
                double transferAmt = Double.parseDouble((String) _requestMap.get("transfer_amount"));
                if ((transferAmt != 0.0) && Double.parseDouble(balanceBeforeCredit) <= Double.parseDouble(balanceAfterCredit)) {
                    _requestMap.put("DEBIT_STATUS", InterfaceErrorCodesI.FAIL);
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                } else {
                    // success the transaction
                    _requestMap.put("INTERFACE_PREV_BALANCE", balanceBeforeCredit);
                    _requestMap.put("INTERFACE_POST_BALANCE", balanceAfterCredit);
                    _requestMap.put("DEBIT_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                }
                break;
            }
            case ComverseTGI.ACTION_CREDIT_ADJUST: {
                actionLevel = "ACTION_CREDIT_ADJUST";
                String balanceBeforeCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
                _requestMap.put("CREDIT_ENQUIRY", "Y");
                validate(_requestMap);
                String balanceAfterCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
                double transferAmt = Double.parseDouble((String) _requestMap.get("transfer_amount"));
                if ((transferAmt != 0.0) && Double.parseDouble(balanceAfterCredit) <= Double.parseDouble(balanceBeforeCredit)) {
                    _requestMap.put("CREDIT_STATUS", InterfaceErrorCodesI.FAIL);
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                } else {
                    // success the transaction
                    _requestMap.put("INTERFACE_PREV_BALANCE", balanceBeforeCredit);
                    _requestMap.put("INTERFACE_POST_BALANCE", balanceAfterCredit);
                    _requestMap.put("CREDIT_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                }
                break;
            }
            case ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION: {
                actionLevel = "ACTION_SUBSCRIBER_ACTIVATION";
                String accountBeforeStatus = _requestMap.get("ACCOUNT_STATUS").toString();
                _requestMap.put("ACTIVATION_ENQUIRY", "Y");
                validate(_requestMap);
                String accountAfterStatus = _requestMap.get("ACCOUNT_STATUS").toString();

                if (accountBeforeStatus.equals(accountAfterStatus)) {
                    _requestMap.put("ACTIVATION_STATUS", InterfaceErrorCodesI.FAIL);
                } else {
                    _requestMap.put("INTERFACE_PREV_ACCOUNT_STATE", accountBeforeStatus);
                    _requestMap.put("INTERFACE_POST_ACCOUNT_STATE", accountAfterStatus);
                    _requestMap.put("ACTIVATION_STATUS", InterfaceErrorCodesI.SUCCESS);
                }
                break;
            }
            }
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "enquiryForAmbigousTransaction _requestMap string:" + _requestMap.toString(), PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + actionLevel);
            // Lohit for sending actula status in case of ambigous transaction
            // request
            // throw new
            // BTSLBaseException((String)_requestMap.get("TRANSACTION_STATUS"));//commented
            // by rahul.d 03/04/12
        } catch (BTSLBaseException be) {
            /*
             * if(be.getMessage().equals(InterfaceErrorCodesI.FAIL))
             * throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
             * else if(be.getMessage().equals(InterfaceErrorCodesI.SUCCESS))
             * throw new BTSLBaseException(InterfaceErrorCodesI.SUCCESS);
             * else
             * throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
             */
            _log.error("enquiryForAmbigousTransaction", "BTSLBaseException be" + be);
            if (be.getMessage().equals(InterfaceErrorCodesI.FAIL))
                throw be;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);// marking
                                                                           // transaction
                                                                           // as
                                                                           // ambiguous
                                                                           // when
                                                                           // exception
                                                                           // occured
                                                                           // in
                                                                           // sending
                                                                           // validation
                                                                           // request
                                                                           // to
                                                                           // IN
                                                                           // possible
                                                                           // error(InterfaceErrorCodesI.VALIDATION_ERROR)
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("enquiryForAmbigousTransaction", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "enquiryForAmbigousTransaction", InterfaceErrorCodesI.AMBIGOUS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("enquiryForAmbigousTransaction", "Exited");
        }
    }

    /**
     * Implements the logic that update the service class of the subscriber
     * from the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    private void COSUpdate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("COSUpdate", "Entered p_requestMap: " + p_requestMap);
        int retryCountCOS = 0;
        _requestMap = p_requestMap;
        try {
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            sendRequestToIN(ComverseTGI.ACTION_COS_UPDATE);
            _requestMap.put("COS_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("COS_ENQUIRY", "N");
            _requestMap.put("INTERFACE_POST_COS", (String) _requestMap.get("NEW_COS_SERVICE_CLASS"));
        } catch (BTSLBaseException be) {
            _log.error("COSUpdate", "BTSLBaseException be:" + be.getMessage());
            retryCountCOS = Integer.parseInt((String) _requestMap.get("RETRY_COUNT_COS"));
            _requestMap.put("COS_STATUS", be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                if (retryCountCOS > 0) {
                    _requestMap.put("RETRY_COUNT_COS", Integer.valueOf(retryCountCOS--));

                    try {
                        if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                            enquiryForAmbigousTransaction(ComverseTGI.ACTION_COS_UPDATE);
                        }
                    } catch (BTSLBaseException bex) {
                        _requestMap.put("COS_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        _log.error("COSUpdate", "Exception exc:" + exc.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[COSUpdate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While COSUpdate get the Exception exc:" + exc.getMessage());
                        throw new BTSLBaseException(this, "COSUpdate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("COSUpdate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[COSUpdate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While COSUpdate get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "COSUpdate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("COSUpdate", "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[COSUpdate]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
    }

    /**
     * This method is used to activate the subscriber
     * 
     * @param p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void SubscriberActivation(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("SubscriberActivation", "Entered p_requestMap: " + p_requestMap);
        int retryCountActivation = 0;
        _requestMap = p_requestMap;
        try {
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            _requestMap.put("ACTIVATION_FLAG", "Y");
            sendRequestToIN(ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION);
            _requestMap.put("ACTIVATION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("ACTIVATION_ENQUIRY", "N");

        } catch (BTSLBaseException be) {

            _log.error("SubscriberActivation", "BTSLBaseException be:" + be.getMessage());
            retryCountActivation = Integer.parseInt((String) _requestMap.get("RETRY_COUNT_CREDIT"));
            _requestMap.put("ACTIVATION_STATUS", be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                if (retryCountActivation > 0) {
                    _requestMap.put("RETRY_COUNT_CREDIT", Integer.valueOf(retryCountActivation--));

                    try {
                        if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                            enquiryForAmbigousTransaction(ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION);
                        }
                    } catch (BTSLBaseException bex) {
                        _requestMap.put("ACTIVATION_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        _log.error("SubscriberActivation", "Exception exc:" + exc.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[SubscriberActivation]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While SubscriberActivation get the Exception exc:" + exc.getMessage());
                        throw new BTSLBaseException(this, "SubscriberActivation", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("SubscriberActivation", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[SubscriberActivation]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While SubscriberActivation get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "SubscriberActivation", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("SubscriberActivation", "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[SubscriberActivation]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
    }

    /**
     * This method used to update the Promotion of the Account
     * 
     * @param p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void PromotionCredit(HashMap p_requestMap) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("PromotionCredit", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double comMultFactorDouble = 0;
        String amountStr = null;
        int retryCountCreditAdjust = 0;
        _requestMap = p_requestMap;
        try {
            String multFactor = (String) _requestMap.get("MULT_FACTOR");
            try {
                comMultFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("PROMOTION_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, comMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseINHandler[PromotionCredit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_PROMO_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("PromotionCredit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[PromotionCredit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PROMOTION_AMOUNT is not Numeric");
                throw new BTSLBaseException(this, "PromotionCredit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set promotion transfer_amount in request map as amountStr (which
            // is round value of INTERFACE_AMOUNT)
            _requestMap.put("promotion_transfer_amount", amountStr);
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            try {
                int promo_validity_days = 0;
                try {
                    promo_validity_days = Integer.parseInt((String) _requestMap.get("CREDIT_BONUS_VAL"));
                } catch (Exception e) {
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, promo_validity_days);

                if (calendar.getTimeInMillis() > Long.parseLong(((String) _requestMap.get("PROMO_CAL_OLD_EXPIRY_DATE")))) {
                    _requestMap.put("PROMO_CAL_OLD_EXPIRY_DATE", String.valueOf(calendar.getTimeInMillis()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("PromotionCredit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[PromotionCredit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Unable to set the promotion expiry date");
                throw new BTSLBaseException(this, "PromotionCredit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            sendRequestToIN(ComverseTGI.ACTION_PROMOTION_CREDIT);
            _requestMap.put("PROMO_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("PROMO_ENQUIRY", "N");
        } catch (BTSLBaseException be) {
            _log.error("PromotionCredit", "BTSLBaseException be:" + be.getMessage());
            retryCountCreditAdjust = Integer.parseInt((String) _requestMap.get("RETRY_COUNT_PROMO"));
            _requestMap.put("PROMO_STATUS", be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                if (retryCountCreditAdjust > 0) {
                    _requestMap.put("RETRY_COUNT_PROMO", Integer.valueOf(retryCountCreditAdjust--));

                    try {
                        if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                            enquiryForAmbigousTransaction(ComverseTGI.ACTION_PROMOTION_CREDIT);
                        }
                    } catch (BTSLBaseException bex) {
                        _requestMap.put("PROMO_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        _log.error("PromotionCredit", "Exception exc:" + exc.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[PromotionCredit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While promotion credit, get the Exception exc:" + exc.getMessage());
                        throw new BTSLBaseException(this, "PromotionCredit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("PromotionCredit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[PromotionCredit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While promotion credit, get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "PromotionCredit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("PromotionCredit", "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[PromotionCredit]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
    }

    /**
	 * 
	 */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double comverseMultFactorDouble = 0;
        String amountStr = null;
        int retryCountCreditAdjust = 0;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);
            _msisdn = (String) _requestMap.get("MSISDN");
            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }
            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTGI.ACTION_CREDIT_ADJUST);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("creditAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);
            try {
                comverseMultFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, comverseMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTGINHandler[creditAdjust]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            try {
                int credit_validity_days = Integer.parseInt((String) _requestMap.get("VALIDITY_DAYS"));
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, credit_validity_days);

                if (calendar.getTimeInMillis() > Long.parseLong(((String) _requestMap.get("CAL_OLD_EXPIRY_DATE")))) {
                    _requestMap.put("CAL_OLD_EXPIRY_DATE", String.valueOf(calendar.getTimeInMillis()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[creditAdjust]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Unable to set the expiry date");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            sendRequestToIN(ComverseTGI.ACTION_CREDIT_ADJUST);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("CREDIT_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("CREDIT_ENQUIRY", "N");
        } catch (BTSLBaseException be) {

            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
            retryCountCreditAdjust = Integer.parseInt((String) _requestMap.get("RETRY_COUNT_CREDIT"));
            _requestMap.put("CREDIT_STATUS", be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                if (retryCountCreditAdjust > 0) {
                    _requestMap.put("RETRY_COUNT_CREDIT", Integer.valueOf(retryCountCreditAdjust--));

                    try {
                        if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                            enquiryForAmbigousTransaction(ComverseTGI.ACTION_CREDIT_ADJUST);
                        }
                    } catch (BTSLBaseException bex) {
                        /*
                         * _requestMap.put("CREDIT_STATUS",InterfaceErrorCodesI.
                         * AMBIGOUS);
                         * throw new
                         * BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                         */
                        throw bex;
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        _log.error("creditAdjust", "Exception exc:" + exc.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit adjust, get the Exception exc:" + exc.getMessage());
                        throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit adjust, get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[creditAdjust]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }

        if (InterfaceUtil.NullToString((String) _requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS)) {
            if (!BTSLUtil.NullToString((String) _requestMap.get("ACCOUNT_STATUS")).equals((String) FileCache.getValue(_interfaceID, "SUBSCRIBER_P2P_ACTIVE_STATE")))
                if (FileCache.getValue(_interfaceID, "P2PRECIVER_ACTIVATION_REQ_STATES").contains(BTSLUtil.NullToString(URLDecoder.decode((String) _requestMap.get("ACCOUNT_STATUS"))))) {
                    try {
                        SubscriberActivation(p_requestMap);
                    } catch (BTSLBaseException be) {
                        _log.error("creditAdjust", " SubscriberActivation BTSLBaseException be=" + be.getMessage());
                        // throw be;
                    }
                } else {
                    _requestMap.put("ACTIVATION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("ACTIVATION_FLAG", "N");
                }
        }
    }

    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double comverseMultFactorDouble = 0;
        String amountStr = null;
        int retryCountDebit = 0;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);
            _msisdn = (String) _requestMap.get("MSISDN");
            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("debitAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTGI.ACTION_CREDIT_ADJUST);

            try {
                comverseMultFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, comverseMultFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ComverseTGINHandler[debitAdjust]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Re-charge request to IN along with re-charge action
            // defined in ComverseTRI interface
            sendRequestToIN(ComverseTGI.ACTION_DEBIT_ADJUST);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("DEBIT_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("DEBIT_ENQUIRY", "N");
        } catch (BTSLBaseException be) {
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            retryCountDebit = Integer.parseInt((String) _requestMap.get("RETRY_COUNT_DEBIT"));
            _requestMap.put("DEBIT_STATUS", be.getMessage());
            if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                if (retryCountDebit > 0) {
                    _requestMap.put("RETRY_COUNT_DEBIT", Integer.valueOf(retryCountDebit--));
                    try {
                        if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                            enquiryForAmbigousTransaction(ComverseTGI.ACTION_DEBIT_ADJUST);
                        }
                    } catch (BTSLBaseException bex) {
                        /*
                         * _requestMap.put("DEBIT_STATUS",InterfaceErrorCodesI.
                         * AMBIGOUS);
                         * throw new
                         * BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                         */
                        throw bex;
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        _log.error("debitAdjust", "Exception exc:" + exc.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComversegINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debit adjust, get the Exception exc:" + exc.getMessage());
                        throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debit adjust, get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[debitAdjust]", "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
    }

    /**
     * This method used to send the request to the IN
     * 
     * @param p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " p_action=" + p_action + " __msisdn=" + _msisdn);
        String actionLevel = "";
        switch (p_action) {
        case ComverseTGI.ACTION_ACCOUNT_DETAILS: {
            actionLevel = "ACTION_ACCOUNT_DETAILS";
            break;
        }
        case ComverseTGI.ACTION_RECHARGE_CREDIT: {
            actionLevel = "ACTION_RECHARGE_CREDIT";
            break;
        }
        case ComverseTGI.ACTION_PROMOTION_CREDIT: {
            actionLevel = "ACTION_PROMOTION_CREDIT";
            break;
        }
        case ComverseTGI.ACTION_COS_UPDATE: {
            actionLevel = "ACTION_COS_UPDATE";
            break;
        }
        case ComverseTGI.ACTION_DEBIT_ADJUST: {
            actionLevel = "ACTION_DEBIT_ADJUST";
            break;
        }
        case ComverseTGI.ACTION_CREDIT_ADJUST: {
            actionLevel = "ACTION_CREDIT_ADJUST";
            break;
        }
        case ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION: {
            actionLevel = "ACTION_SUBSCRIBER_ACTIVATION";
            break;
        }
        }
        if (!BTSLUtil.isNullString(_msisdn)) {
            InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
        }
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " p_action=" + actionLevel + " __msisdn=" + _msisdn);
        long startTime = 0, endTime = 0, sleepTime, warnTime = 0;
        ServiceSoap_PortType clientStub = null;

        NodeScheduler comverseNodeScheduler = null;
        NodeVO comverseNodeVO = null;
        int retryNumber = 0;
        int readTimeOut = 0;
        ComverseTGConnector serviceConnection = null;
        try {
            // Get the start time when the request is send to IN.
            comverseNodeScheduler = NodeManager.getScheduler(_interfaceID);
            // Get the retry number from the object that is used to retry the
            // getNode in case connection is failed.
            retryNumber = comverseNodeScheduler.getRetryNum();
            // check if comverseNodeScheduler is null throw exception.Confirm
            // for Error code(INTERFACE_CONNECTION_NULL)if required-It should be
            // new code like ERROR_NODE_FOUND!
            if (comverseNodeScheduler == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_COMVERSE_WHILE_GETTING_SCHEDULER_OBJECT);
            for (int loop = 1; loop <= retryNumber; loop++) {
                try {
                    comverseNodeVO = comverseNodeScheduler.getNodeVO(_inReconID);
                    TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ComverseTGINHandler[sendRequestToIN]", PretupsI.TXN_LOG_REQTYPE_REQ, "Node information comverseNodeVO:" + comverseNodeVO, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + actionLevel);
                    _requestMap.put("IN_URL", comverseNodeVO.getUrl());
                    // Check if Node is foud or not.Confirm for Error
                    // code(INTERFACE_CONNECTION_NULL)if required-It should be
                    // new code like ERROR_NODE_FOUND!
                    if (comverseNodeVO == null)
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_COMVERSE_NODE_DETAIL_NOT_FOUND);
                    warnTime = comverseNodeVO.getWarnTime();
                    readTimeOut = comverseNodeVO.getReadTimeOut();
                    // Confirm for the service name servlet for the url
                    // consturction whether URL will be specified in INFile or
                    // IP,PORT and ServletName.
                    serviceConnection = new ComverseTGConnector(comverseNodeVO, _interfaceID);
                    // break the loop on getting the successfull connection for
                    // the node;
                    clientStub = serviceConnection.getService();
                    if (clientStub == null) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Unable to get Client Object");
                        _log.error("sendRequestToIN", "Unable to get Client Object");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    }
                    try {
                        try {
                            startTime = System.currentTimeMillis();
                            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                            switch (p_action) {
                            case ComverseTGI.ACTION_ACCOUNT_DETAILS: {
                                SubscriberRetrieve subscriberRetrieve = clientStub.retrieveSubscriberWithIdentityNoHistory(_msisdn, "", 1);
                                BalanceEntity be[] = subscriberRetrieve.getSubscriberData().getBalances();
                                _requestMap.put("ACCINFO_RESP_OBJ", subscriberRetrieve);
                                break;
                            }
                            case ComverseTGI.ACTION_RECHARGE_CREDIT: {

                                boolean rechargeStatus = clientStub.nonVoucherRecharge(_msisdn, "", Double.parseDouble((String) _requestMap.get("transfer_amount")), Integer.parseInt((String) _requestMap.get("VALIDITY_DAYS")), (String) _requestMap.get("RECH_COMMENT"));
                                if (!rechargeStatus) {
                                    _requestMap.put("RECHARGE_INTERFACE_STATUS", "ERR_RESP");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                                }
                                break;
                            }
                            case ComverseTGI.ACTION_PROMOTION_CREDIT: {
                                _formatter.setRequestObjectInMap(ComverseTGI.ACTION_PROMOTION_CREDIT, _requestMap);
                                boolean creditStatus = clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("PROMO_REQ_OBJ"), "", (String) _requestMap.get("RECH_COMMENT"), "");
                                if (!creditStatus) {
                                    _requestMap.put("PROMO_INTERFACE_STATUS", "ERR_RESP");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                                }
                                break;
                            }
                            case ComverseTGI.ACTION_COS_UPDATE: {
                                _formatter.setRequestObjectInMap(ComverseTGI.ACTION_COS_UPDATE, _requestMap);
                                ChangeCOSResponse cosresp = new ChangeCOSResponse(false);
                                cosresp = clientStub.changeCOS((ChangeCOSRequest) _requestMap.get("COS_REQ_OBJ"));

                                if (!cosresp.isStatus()) {
                                    _requestMap.put("COS_INTERFACE_STATUS", "ERR_RESP");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                                }
                                break;
                            }

                            case ComverseTGI.ACTION_CREDIT_ADJUST: {
                                _formatter.setRequestObjectInMap(ComverseTGI.ACTION_CREDIT_ADJUST, _requestMap);
                                boolean creditStatus = clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("CR_ADJ_REQ_OBJ"), "", (String) _requestMap.get("XFER_COMMENT"), "");
                                if (!creditStatus) {
                                    _requestMap.put("INTERFACE_STATUS", "ERR_RESP");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                                }
                                break;
                            }
                            case ComverseTGI.ACTION_DEBIT_ADJUST: {
                                _formatter.setRequestObjectInMap(ComverseTGI.ACTION_DEBIT_ADJUST, _requestMap);
                                boolean creditStatus = clientStub.creditAccount(_msisdn, "", (BalanceCreditAccount[]) _requestMap.get("DR_ADJ_REQ_OBJ"), "", (String) _requestMap.get("XFER_COMMENT"), "");
                                if (!creditStatus) {
                                    _requestMap.put("INTERFACE_STATUS", "ERR_RESP");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                                }
                                break;
                            }
                            case ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION: {
                                _formatter.setRequestObjectInMap(ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION, _requestMap);
                                boolean creditStatus = clientStub.modifySubscriber((SubscriberModify) _requestMap.get("AC_ADJ_REQ_OBJ"));
                                if (!creditStatus) {
                                    _requestMap.put("ACTIVATION_INTERFACE_STATUS", "ERR_RESP");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                                }
                                break;
                            }
                            }
                        } catch (java.rmi.RemoteException re) {
                            re.printStackTrace();
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RemoteException Error Message:" + re.getMessage());
                            String respCode = null;
                            // parse error code
                            String requestStr = re.getMessage();
                            int index = requestStr.indexOf("<ErrorCode>");
                            if (index == -1) {
                                if (re.getMessage().contains("java.net.ConnectException")) {
                                    // In case of connection failure
                                    // 1.Decrement the connection counter
                                    // 2.set the Node as blocked
                                    // 3.set the blocked time
                                    // 4.Handle the event with level INFO, show
                                    // the message that Node is blocked for some
                                    // time (expiry time).
                                    // Continue the retry loop till success;
                                    // Check if the max retry attempt is reached
                                    // raise exception with error code.
                                    _log.error("sendRequestToIN", "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for Comverse Soap Stub with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + comverseNodeVO.getNodeNumber() + "]");

                                    _log.info("sendRequestToIN", "Setting the Node [" + comverseNodeVO.getNodeNumber() + "] as blocked for duration ::" + comverseNodeVO.getExpiryDuration() + " miliseconds");
                                    comverseNodeVO.incrementBarredCount();
                                    // comverseNodeVO.setBlocked(true);
                                    // comverseNodeVO.setBlokedAt(System.currentTimeMillis());

                                    if (loop == retryNumber) {
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
                                        throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                                    }
                                    continue;
                                } else if (re.getMessage().contains("java.net.SocketTimeoutException")) {
                                    re.printStackTrace();
                                    if (re.getMessage().contains("connect")) {
                                        // In case of connection failure
                                        // 1.Decrement the connection counter
                                        // 2.set the Node as blocked
                                        // 3.set the blocked time
                                        // 4.Handle the event with level INFO,
                                        // show the message that Node is blocked
                                        // for some time (expiry time).
                                        // Continue the retry loop till success;
                                        // Check if the max retry attempt is
                                        // reached raise exception with error
                                        // code.
                                        _log.error("sendRequestToIN", "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for Comverse Soap Stub with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + comverseNodeVO.getNodeNumber() + "]");

                                        _log.info("sendRequestToIN", "Setting the Node [" + comverseNodeVO.getNodeNumber() + "] as blocked for duration ::" + comverseNodeVO.getExpiryDuration() + " miliseconds");
                                        comverseNodeVO.incrementBarredCount();
                                        // comverseNodeVO.setBlocked(true);
                                        // comverseNodeVO.setBlokedAt(System.currentTimeMillis());

                                        if (loop == retryNumber) {
                                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
                                            throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                                        }
                                        continue;
                                    }
                                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketTimeoutException Message:" + re.getMessage());
                                    _log.error("sendRequestToIN", "RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                } else if (re.getMessage().contains("java.net.SocketException")) {
                                    re.printStackTrace();
                                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketException Message:" + re.getMessage());
                                    _log.error("sendRequestToIN", "RMI java.net.SocketException Error Message :" + re.getMessage());
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                } else
                                    throw new Exception(re);
                            }
                            respCode = requestStr.substring(index + "<ErrorCode>".length(), requestStr.indexOf("</ErrorCode>", index));

                            index = requestStr.indexOf("<ErrorDescription>");
                            String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(), requestStr.indexOf("</ErrorDescription>", index));
                            _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);

                            if (p_action == ComverseTGI.ACTION_PROMOTION_CREDIT) {
                                _requestMap.put("PROMO_INTERFACE_STATUS", respCode);
                                _requestMap.put("PROMO_INTERFACE_DESC", respCodeDesc);
                            } else if (p_action == ComverseTGI.ACTION_COS_UPDATE) {
                                _requestMap.put("COS_INTERFACE_STATUS", respCode);
                                _requestMap.put("COS_INTERFACE_DESC", respCodeDesc);
                            } else if (p_action == ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION) {
                                _requestMap.put("ACTIVATION_INTERFACE_STATUS", respCode);
                                _requestMap.put("ACTIVATION_INTERFACE_DESC", respCodeDesc);
                            } else {
                                _requestMap.put("INTERFACE_STATUS", respCode);
                                _requestMap.put("INTERFACE_DESC", respCodeDesc);
                            }

                            if (respCode.equals(ComverseTGI.MSISDN_NOT_FOUND)) {
                                _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                            } else {
                                _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                            }
                        } catch (SocketTimeoutException se) {
                            se.printStackTrace();
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "SocketTimeoutException Error Message:" + se.getMessage());
                            _log.error("sendRequestToIN", "SocketTimeoutException Error Message :" + se.getMessage());
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        } catch (Exception e) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Exception Error Message:" + e.getMessage());
                            _log.error("sendRequestToIN", "Exception Error Message :" + e.getMessage());
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        } finally {
                            _requestMap.remove("CR_ADJ_REQ_OBJ");
                            _requestMap.remove("DR_ADJ_REQ_OBJ");
                            _requestMap.remove("AC_ADJ_REQ_OBJ");
                            _requestMap.remove("PROMO_REQ_OBJ");
                            _requestMap.remove("COS_REQ_OBJ");

                        }
                        endTime = System.currentTimeMillis();
                        comverseNodeVO.resetBarredCount();
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Error Message:" + e.getMessage());
                        _log.error("sendRequestToIN", "Error Message :" + e.getMessage());
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    } finally {
                        if (endTime == 0)
                            endTime = System.currentTimeMillis();
                        _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                        _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
                    }

                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + comverseNodeVO.getNodeNumber() + "] created after the attempt number(loop)::" + loop);
                    break;
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
                    throw be;// Confirm should we come out of loop or do another
                             // retry
                }// end of catch-BTSLBaseException
                catch (Exception e) {
                    _log.error("sendRequestToIN", "Exception be::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }// end of catch-Exception
            }
            _responseMap = _formatter.parseResponseObject(p_action, _requestMap);
            // put value of response
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response Map: " + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + actionLevel);
            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            if (endTime - startTime >= warnTime) {
                _log.info("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ComverseTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "CCWS IP= " + comverseNodeVO.getUrl(), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Comverse IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
            }
            String status = (String) _responseMap.get("RESP_STATUS");

            if (p_action == ComverseTGI.ACTION_PROMOTION_CREDIT)
                _requestMap.put("PROMO_INTERFACE_STATUS", status);
            else if (p_action == ComverseTGI.ACTION_COS_UPDATE)
                _requestMap.put("COS_INTERFACE_STATUS", status);
            else if (p_action == ComverseTGI.ACTION_SUBSCRIBER_ACTIVATION)
                _requestMap.put("ACTIVATION_INTERFACE_STATUS", status);
            else
                _requestMap.put("INTERFACE_STATUS", status);

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
            clientStub = null;
            serviceConnection = null;
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action=" + p_action);
        }// end of finally
    }

    public void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            String subscriberState = FileCache.getValue(_interfaceID, "SUBSCRIBER_P2P_ACTIVE_STATE");
            if (InterfaceUtil.isNullString(subscriberState)) {
                _log.error("setInterfaceParameters", "Value of SUBSCRIBER_P2P_ACTIVE_STATE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SUBSCRIBER_P2P_ACTIVE_STATE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SUBSCRIBER_P2P_ACTIVE_STATE", subscriberState.trim());

            String promotionBalName = FileCache.getValue(_interfaceID, "PROMO_BAL_NAME");
            if (InterfaceUtil.isNullString(promotionBalName)) {
                _log.error("setInterfaceParameters", "Value of PROMO_BAL_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "PROMO_BAL_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("PROMO_BAL_NAME", promotionBalName.trim());

            String rechComment = FileCache.getValue(_interfaceID, "RECH_COMMENT");
            rechComment = rechComment.trim() + "-" + (String) _requestMap.get("SENDER_ID") + "-" + (String) _requestMap.get("SENDER_MSISDN") + "-" + _inReconID + "-" + (String) _requestMap.get("RC_COMMENT");
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "generated Recharge Comment::" + rechComment);
            if (InterfaceUtil.isNullString(rechComment)) {
                _log.error("setInterfaceParameters", "Value of RECH_COMMENT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RECH_COMMENT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RECH_COMMENT", rechComment);

            String xferComment = FileCache.getValue(_interfaceID, "XFER_COMMENT");
            xferComment = xferComment.trim() + "-" + (String) _requestMap.get("SENDER_ID") + "-" + (String) _requestMap.get("SENDER_MSISDN") + "-" + _inReconID;
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "generated P2P Recharge Comment::" + xferComment);
            if (InterfaceUtil.isNullString(xferComment)) {
                _log.error("setInterfaceParameters", "Value of XFER_COMMENT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "XFER_COMMENT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("XFER_COMMENT", xferComment);

            String coreBalName = FileCache.getValue(_interfaceID, "CORE_BAL_NAME");
            if (InterfaceUtil.isNullString(coreBalName)) {
                _log.error("setInterfaceParameters", "Value of CORE_BAL_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CORE_BAL_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CORE_BAL_NAME", coreBalName.trim());

            String retryCountCreditStr = (String) FileCache.getValue(_interfaceID, "RETRY_COUNT_CREDIT");
            if (InterfaceUtil.isNullString(retryCountCreditStr) || !InterfaceUtil.isNumeric(retryCountCreditStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "RETRY_COUNT_CREDIT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RETRY_COUNT_CREDIT", retryCountCreditStr.trim());

            String retryCountPromoStr = (String) FileCache.getValue(_interfaceID, "RETRY_COUNT_PROMO");
            if (InterfaceUtil.isNullString(retryCountCreditStr) || !InterfaceUtil.isNumeric(retryCountPromoStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "RETRY_COUNT_PROMO is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RETRY_COUNT_PROMO", retryCountPromoStr.trim());

            String retryCountCOSStr = (String) FileCache.getValue(_interfaceID, "RETRY_COUNT_COS");
            if (InterfaceUtil.isNullString(retryCountCreditStr) || !InterfaceUtil.isNumeric(retryCountCOSStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "RETRY_COUNT_COS is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RETRY_COUNT_COS", retryCountCOSStr.trim());

            String retryCountDebitStr = (String) FileCache.getValue(_interfaceID, "RETRY_COUNT_DEBIT");
            if (InterfaceUtil.isNullString(retryCountDebitStr) || !InterfaceUtil.isNumeric(retryCountDebitStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "RETRY_COUNT_DEBIT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("RETRY_COUNT_DEBIT", retryCountDebitStr.trim());

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
    }

    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * 
     * @throws BTSLBaseException
     */
    // private void handleCancelTransaction() throws BTSLBaseException
    // {}

    private String getINReconID() {
        String inReconId = null;
        String userType = (String) _requestMap.get("USER_TYPE");
        inReconId = _referenceID + userType;
        _requestMap.put("IN_RECON_ID", inReconId);
        _requestMap.put("IN_TXN_ID", inReconId);
        return inReconId;
    }

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
            // langFromIN = (String)_responseMap.get("LanguageName");
            langFromIN = (String) _responseMap.get("IN_LANG");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString::" + mappingString + " langFromIN::" + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0; in < mappingArr.length; in++) {
                tempArr = mappingArr[in].split(":");
                if (langFromIN.equalsIgnoreCase(tempArr[0].trim())) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ComverseINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e::" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang::" + mappedLang);
        }
    }// end of setLanguageFromMapping

    /**
     * Method : postCreditEnquiry
     * Function : to check the user balance after query
     * 
     * @param p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void postCreditEnquiry(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("postCreditEnquiry", "Entered p_requestMap:" + p_requestMap);

        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _referenceID);

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("postCreditEnquiry", "multFactor: " + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("postCreditEnquiry", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[postCreditEnquiry]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();

            // Set the interface parameters into requestMap
            setInterfaceParameters(ComverseTGI.ACTION_ACCOUNT_DETAILS);
            // sending the AccountInfo request to IN along with validate action
            // defined in interface
            sendRequestToIN(ComverseTGI.ACTION_ACCOUNT_DETAILS);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("POSTCRE_TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // get value of BALANCE from response map (BALANCE was set in
            // response map in sendRequestToIN method.)
            try {
                String amountStr = (Double) _responseMap.get("RESP_BALANCE") + "";
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
                _requestMap.put("POSTCRE_INTERFACE_POST_BALANCE", amountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[postCreditEnquiry]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            try {
                String promoAmountStr = (Double) _responseMap.get("PROMO_RESP_BALANCE") + "";
                promoAmountStr = InterfaceUtil.getSystemAmountFromINAmount(promoAmountStr, Double.parseDouble(multFactor));
                _requestMap.put("POSTCRE_INTERFACE_PROMO_POST_BALANCE", promoAmountStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[postCreditEnquiry]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Promo Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            String expdate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
            _requestMap.put("POSTCRE_NEW_EXPIRY_DATE", expdate);
            expdate = BTSLUtil.getDateTimeStringFromDate(((Calendar) _responseMap.get("PROMO_OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
            _requestMap.put("POSTCRE_NEW_PROMO_EXPIRY_DATE", expdate);
            _requestMap.put("INTERFACE_POST_COS", (String) _responseMap.get("SERVICE_CLASS"));
        } catch (BTSLBaseException be) {
            _log.error("postCreditEnquiry", "BTSLBaseException be=" + be.getMessage());
            _requestMap.put("POSTCRE_TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("postCreditEnquiry", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[postCreditEnquiry]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "postCreditEnquiry", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("postCreditEnquiry", "Exiting with  _requestMap: " + _requestMap);
        }
    }
}
