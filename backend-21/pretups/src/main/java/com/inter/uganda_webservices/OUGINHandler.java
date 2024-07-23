package com.inter.uganda_webservices;

/**
 * OUGINHandler.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Shashank Shukla September 26, 2011 Initial Creation
 * ----------------------------------------------------------------------------
 * --------------------
 * This class is the Handler class for the UGANDA Web-Service interface.
 */
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
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.DCPServiceSoapBindingStub;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogoffRequest;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogoffResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonRequest;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCAddSubscribedPackageReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCAddSubscribedPackageResponse;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.inter.uganda_webservices.OUGReqResFormatter;

public class OUGINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(OUGINHandler.class.getName());
    private OUGReqResFormatter _formatter = null;
    private HashMap<String, String> _requestMap = null;// Contains the request
                                                       // parameter as key and
                                                       // value pair.
    private HashMap<String, String> _responseMap = null;// Contains the response
                                                        // of the request as key
                                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inReconID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private String _userType = null;
    private String _inTXNID = null;// Used to represent the Transaction ID

    /**
     * Default constructor to create formatter object.
     */
    public OUGINHandler() {
        _formatter = new OUGReqResFormatter();
    }

    /**
     * This method would be used to validate the subscriber's account at the IN.
     * 
     * @param HashMap
     *            <String,String> p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _userType = (String) _requestMap.get("USER_TYPE");
            _msisdn = (String) _requestMap.get("MSISDN");

            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("validate", "ugandawebserviceMultiplicationFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, " OUGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            try {
                sendRequestToIN(OUGVoluBillI.ACTION_LOGON);
                _requestMap.put("LOGON_STATUS", InterfaceErrorCodesI.SUCCESS);
                _requestMap.put("SESSION_ID", _responseMap.get("SESSION_ID"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception=" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATE_LOGIN_FAILED);
            }
            try {
                // Set the interface parameters into requestMap
                setInterfaceParameters(OUGVoluBillI.ACTION_ACCOUNT_INFO);
                sendRequestToIN(OUGVoluBillI.ACTION_ACCOUNT_INFO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // sending the AccountInfo request to IN along with validate action
            // defined in HuaweiOMTI interface
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            String accountBalance = _responseMap.get("ACCOUNT_BALANCE");
            try {
                accountBalance = InterfaceUtil.getSystemAmountFromINAmount(accountBalance, Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE", accountBalance);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "AccountBalance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // set service class
            _requestMap.put("SERVICE_CLASS", "ALL");
            // set account status
            _requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("ACCOUNTSTATE"));

            // set OLD_EXPIRY_DATE in request map as returned from _responseMap.
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("ACTIVESTOP"), "yyyyMMdd"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("SUSPENDSTOP"), "yyyyMMdd"));

            // set the mapping language of our system from FileCache mapping
            // based on the response language.
            setLanguageFromMapping();
            sendRequestToIN(OUGVoluBillI.ACTION_LOGOFF);
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with _requestMap=" + _requestMap);
        }
    }

    /**
     * This method would be used to credit the subscriber's account at the IN.
     * 
     * @param HashMap
     *            <String,String> p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void credit(HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);

        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        _requestMap = p_requestMap;
        try {
            _interfaceID = _requestMap.get("INTERFACE_ID");
            _referenceID = _requestMap.get("TRANSACTION_ID");
            _userType = _requestMap.get("USER_TYPE");
            _inReconID = getINReconID();
            _requestMap.put("IN_TXN_ID", _inReconID);
            _msisdn = (String) _requestMap.get("MSISDN");

            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and received balance would be divided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile.");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters(OUGVoluBillI.ACTION_INTERNET_RECHARGE);
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");

            try {
                multFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
                // Check whether bonus is allowed in separately or not.
                String sepBonusAllowed = FileCache.getValue(_interfaceID, "SEPERATE_BONUS_ALLOWED");
                if (!InterfaceUtil.isNullString(sepBonusAllowed) && "Y".equals(sepBonusAllowed)) {
                    double bonusAmt = Double.parseDouble(_requestMap.get("BONUS_AMOUNT"));
                    bonusAmt = InterfaceUtil.getINAmountFromSystemAmountToIN(bonusAmt, multFactorDouble);
                    systemAmtDouble = systemAmtDouble - bonusAmt;
                }
                // Convert the transfer amount from double to string.
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "OUGINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("TRANSFER_AMOUNT", amountStr);

            // Read the requested package from the IN File
            String packageKey = p_requestMap.get("TRANSFER_AMOUNT") + "_" + p_requestMap.get("CARD_GROUP_SELECTOR");
            String packageName = FileCache.getValue(_interfaceID, packageKey);
            // If package not defined in the IN File, then throw the exception
            if (InterfaceUtil.isNullString(packageName)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Package not defined in the IN File for the combination packageKey=" + packageKey);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PACKAGE_NOT_FOUND);
            }
            _requestMap.put("REQ_PACKAGE", packageName);

            try {
                sendRequestToIN(OUGVoluBillI.ACTION_LOGON);
                _requestMap.put("LOGON_STATUS", InterfaceErrorCodesI.SUCCESS);
                _requestMap.put("SESSION_ID", _responseMap.get("SESSION_ID"));
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception=" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.CREDIT_LOGIN_FAILED);
            }
            // Send the Re-charge request to IN along with re-charge action
            // defined in VOLUBILL OUG interface
            sendRequestToIN(OUGVoluBillI.ACTION_INTERNET_RECHARGE);

            _requestMap.put("IN_RECHARGE_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            // Send the log-off response
            try {
                sendRequestToIN(OUGVoluBillI.ACTION_LOGOFF);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            }
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            _requestMap.put("IN_CREDIT_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited with _requestMap=" + _requestMap);
        }
    }

    /**
     * This method would be used to set the interface parameters from IN file to
     * request map..
     * 
     * @param int p_action
     * @throws BTSLBaseException
     *             , Exception
     */
    public void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", " Entered");
        try {
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

            String wssdLocation = (String) FileCache.getValue(_interfaceID, "WSDD_LOCATION");
            if (InterfaceUtil.isNullString(wssdLocation)) {
                _log.error("setInterfaceParameters: ", "Value of WSDD_LOCATION is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "WSDD_LOCATION is not defined in the INFile.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WSDD_LOCATION", wssdLocation.trim());

            String endURL = FileCache.getValue(_interfaceID, "END_URL");
            if (InterfaceUtil.isNullString(endURL)) {
                _log.error("setInterfaceParameters: ", "Value of END_URL is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "END_URL is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("END_URL", endURL.trim());

            String readTimeout = FileCache.getValue(_interfaceID, "READ_TIME_OUT");
            if (InterfaceUtil.isNullString(readTimeout)) {
                _log.error("setInterfaceParameters: ", "Value of READ_TIME_OUT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "READ_TIME_OUT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("READ_TIME_OUT", readTimeout.trim());

            String userName = FileCache.getValue(_interfaceID, "USER_NAME");
            if (InterfaceUtil.isNullString(userName)) {
                _log.error("setInterfaceParameters: ", "Value of USER_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "USER_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("USER_NAME", userName.trim());

            String soapActionURI = FileCache.getValue(_interfaceID, "SOAP_ACTION_URI");
            if (InterfaceUtil.isNullString(soapActionURI)) {
                _log.error("setInterfaceParameters: ", "Value of SOAP_ACTION_URI is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SOAP_ACTION_URI is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SOAP_ACTION_URI", soapActionURI.trim());
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
     * This method will be used to send the request to IN based on the action
     * parameters.
     * 
     * @param int p_action
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " p_action=" + p_action);
        long startTime = 0, endTime = 0, warnTime = 0;
        DCPServiceSoapBindingStub _service = null;
        OUGVoluBillConnector serviceConnection = null;
        String reply = null;
        LogonRequest logonRequest = null;
        LogonResponse logonResponse = null;
        LogoffRequest logoffRequest = null;
        LogoffResponse logoffResponse = null;
        GetAccountReq getAccountReq = null;
        GetAccountResponse getAccountResponse = null;
        MDCAddSubscribedPackageReq addSubscribedPackageRequest = null;
        MDCAddSubscribedPackageResponse addPackageResponse = null;

        // Print the requested package in the transaction log for credit
        // request.
        if (p_action == OUGVoluBillI.ACTION_INTERNET_RECHARGE) {
            String reqPackage = _requestMap.get("REQ_PACKAGE");
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "_requestMap: " + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " action=" + p_action + " reqPackage=" + reqPackage);
        } else
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "_requestMap: " + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " action=" + p_action);

        try {
            _requestMap.put("ACTION", String.valueOf(p_action));
            // Confirm for the service name SERVLET for the URL construction
            // whether URL will be specified in INFile or IP,PORT and
            // ServletName.
            serviceConnection = new OUGVoluBillConnector(_interfaceID);

            _service = (DCPServiceSoapBindingStub) serviceConnection.getService();

            if (_service == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_action, "Unable to get Client Object");
                _log.error("sendRequestToIN", "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            try {
                try {
                    startTime = System.currentTimeMillis();
                    _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                    switch (p_action) {
                    case OUGVoluBillI.ACTION_LOGON: {
                        logonRequest = _formatter.getLogonRequestObject(_requestMap);
                        if (logonRequest == null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

                        logonResponse = _service.logon(logonRequest);
                        if (logonResponse == null) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_action, "Response object found null");
                            _log.error("sendRequestToIN", "No response received from the IN for Login Request.");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        reply = logonResponse.getReply();
                        break;
                    }
                    case OUGVoluBillI.ACTION_LOGOFF: {
                        logoffRequest = _formatter.getLogoffRequestObject(_requestMap, _service);
                        if (logoffRequest == null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

                        logoffResponse = _service.logoff(logoffRequest);
                        break;
                    }
                    case OUGVoluBillI.ACTION_ACCOUNT_INFO: {
                        getAccountReq = _formatter.getAccountRequestObject(_requestMap, _service);
                        if (getAccountReq == null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        getAccountResponse = _service.getAccount(getAccountReq);
                        if (getAccountResponse == null) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_action, "Response object found null");
                            _log.error("sendRequestToIN", "No response received from the IN for Validate Request.");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        reply = getAccountResponse.getReply();
                        break;
                    }
                    case OUGVoluBillI.ACTION_INTERNET_RECHARGE: {
                        addSubscribedPackageRequest = _formatter.addSubscribedPackageReqObject(_requestMap, _service);
                        if (addSubscribedPackageRequest == null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        addPackageResponse = _service.mdcAddSubscribedPackage(addSubscribedPackageRequest);
                        if (addPackageResponse == null) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_action, "Response object found null");
                            _log.error("sendRequestToIN", "No response received from the IN for Credit Request.");
                            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        reply = addPackageResponse.getReply();
                        break;
                    }
                    }
                } catch (BTSLBaseException be) {
                    be.printStackTrace();
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_action, " BTSLBaseException, Error Message=" + be.getMessage());
                    _log.error("sendRequestToIN", "BTSLBaseException, Error Message=" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action =" + p_action, " Exception Error Message:" + e.getMessage());
                    _log.error("sendRequestToIN", "Exception Error Message :" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            } catch (Exception e) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action=" + p_action, "Error Message:" + e.getMessage());
                _log.error("sendRequestToIN", "Error Message=" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } finally {
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " Time taken by the IN is=" + (endTime - startTime));
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response status from the IN, reply=" + reply, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " action=" + p_action);
            }
            // Construct the response map from the response object received.
            _responseMap = _formatter.parseResponseObject(p_action, logonResponse, logoffResponse, getAccountResponse, addPackageResponse, _service);

            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            if (endTime - startTime >= warnTime)
                _log.info("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));

            if (p_action != OUGVoluBillI.ACTION_LOGOFF) {
                String status = _responseMap.get("RESP_STATUS");
                _requestMap.put("INTERFACE_STATUS", status);

                if (!OUGVoluBillI.RESULT_OK.equals(status)) {
                    String errMessage = _responseMap.get("RESP_ERR_MSG");
                    if (p_action == OUGVoluBillI.ACTION_LOGON)
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_LOGIN_FAILED);

                    // Check the status whether the subscriber's MSISDN defined
                    // in the IN
                    if (errMessage == OUGVoluBillI.SUBSCRIBER_NOT_FOUND) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "MSISDN does not exist on IN");
                        _log.error("sendRequestToIN", "MSISDN does not exist on IN");
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }
                    // Check the status whether the subscriber's MSISDN defined
                    // in the IN
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Response Status fron IN is: " + status + ". So marking response as FAIL");
                    _log.error("sendRequestToIN", "Response Status fron IN is: " + errMessage + ". So marking response as FAIL.");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            _service = null;
            if (p_action == OUGVoluBillI.ACTION_ACCOUNT_INFO || p_action == OUGVoluBillI.ACTION_INTERNET_RECHARGE)
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response Map in finally block of sendRequestToIN, _responseMap=" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " action=" + p_action);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " Exiting p_action=" + p_action + " Response Status fron IN=" + _requestMap.get("INTERFACE_STATUS"));
        }
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

            langFromIN = (String) _responseMap.get("IN_LANG");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString::" + mappingString + " langFromIN::" + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            if (!InterfaceUtil.isNullString(langFromIN)) {
                for (int in = 0; in < mappingArr.length; in++) {
                    tempArr = mappingArr[in].split(":");
                    if (langFromIN.equalsIgnoreCase(tempArr[0].trim())) {
                        mappedLang = tempArr[1];
                        mappingNotFound = false;
                        break;
                    }
                }// end of for loop
            } else
                mappedLang = (String) FileCache.getValue(_interfaceID, "DEFAULT_LANG");

            // if the mapping of IN language with our system is not found,handle
            // the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "OUGINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e::" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OUGINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang::" + mappedLang);
        }
    }// end of setLanguageFromMapping

    private String getINReconID() {
        String inReconId = null;
        inReconId = _referenceID + _userType;
        _requestMap.put("IN_RECON_ID", inReconId);
        _requestMap.put("IN_TXN_ID", inReconId);
        return inReconId;
    }

    /**
     * Method validityAdjust.
     * 
     * @param HashMap
     *            <String,String> p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validityAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {
    }

    /**
     * Method creditAdjust.
     * 
     * @param HashMap
     *            <String,String> p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void creditAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {
    }

    /**
     * Method debitAdjust.
     * 
     * @param HashMap
     *            <String,String> p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void debitAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {
    }
}
