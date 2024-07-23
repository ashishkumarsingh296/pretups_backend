package com.selftopup.pretups.inter.citipg;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.inter.cache.FileCache;
import com.selftopup.pretups.inter.citipg.CitiBankI;
import com.selftopup.pretups.inter.citipg.CitiBankRequestFormatter;
import com.selftopup.pretups.inter.citipg.CitiBankResponseFormatter;
import com.selftopup.pretups.inter.citipg.CitiBankUrlConnection;
import com.selftopup.pretups.inter.module.InterfaceErrorCodesI;
import com.selftopup.pretups.inter.module.InterfaceHandler;
import com.selftopup.pretups.inter.module.InterfaceUtil;
import com.selftopup.pretups.inter.module.ReconcialiationLog;
import com.selftopup.pretups.logging.TransactionLog;

public class CitiBankPGHandler implements InterfaceHandler {

    private static Log _log = LogFactory.getLog(CitiBankPGHandler.class.getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private String _userType = null;
    private static CitiBankRequestFormatter _citiBankRequestFormatter = null;
    private static CitiBankResponseFormatter _citiBankResponseFormatter = null;
    static {
        if (_log.isDebugEnabled())
            _log.debug("CitiBankPGHandler[static]", "Entered");
        try {
            _citiBankRequestFormatter = new CitiBankRequestFormatter();
            _citiBankResponseFormatter = new CitiBankResponseFormatter();
        } catch (Exception e) {
            _log.error("CitiBankPGHandler[static]", "While instantiation of CitiBankRequestFormatter get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankPGHandler[static]", "", "", "", "While instantiation of CitiBankRequestFormatter get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("CitiBankPGHandler[static]", "Exited");
        }
    }

    /**
     * Implements the logic that debit the subscriber account on IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", " Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        double systemAmtDouble = 0;
        String amountStr = null;
        int validityDays = 0;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _inTXNID = getPGTransactionID(_requestMap);
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // Get the Multiplication factor from the FileCache with the help of
            // interface id.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("debitAdjust", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankPGHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                double multFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CitiBankPGHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankPGHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "transfer_amount:" + amountStr + " multFactor:" + multFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);

            // Check for the validity Adjustment-VALIDITY_DAYS
            if (!InterfaceUtil.isNullString((String) _requestMap.get("VALIDITY_DAYS"))) {
                try {
                    validityDays = Integer.parseInt(((String) _requestMap.get("VALIDITY_DAYS")).trim());
                    if (_log.isDebugEnabled())
                        _log.debug("debitAdjust", "validityDays::" + validityDays);

                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("debitAdjust", "Exception e::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }

            String inStr = _citiBankRequestFormatter.generateRequest(CitiBankI.ACTION_IMMEDIATE_DEBIT, _requestMap);
            // sending the CreditAdjust request to IN along with
            // ACTION_IMMEDIATE_DEBIT action defined in Comverse interface
            sendRequestToPG(inStr, CitiBankI.ACTION_IMMEDIATE_DEBIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;

            // to fire VAL request in case no response in TOP and compare the
            // balance before and after
            // if bal is same then fail else make it success.
            try {
                if (be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    balanceEnquiryForAmbigousTransaction();
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                _log.error("sendRequestToPG", "AMBIGOUS Exception e::" + exc.getMessage());
                try {
                    _requestMap.put("TRANSACTION_TYPE", "CR");
                    handleCancelTransaction();
                } catch (BTSLBaseException bte) {
                    throw bte;
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("credit", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankPGHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
            }
            // end here for VAL request in case no response in TOP/P2P

        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankPGHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:" + e.getMessage());
            throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }

        // post credit enquiry after promotion credit and cos update
        try {
            _requestMap.put("IN_START_TIME", "0");
            _requestMap.put("IN_END_TIME", "0");
            // /
            // if(((String)_requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS))
            // / postCreditEnquiry(_requestMap);
            // lohit
            // /_requestMap.put("IN_POSTCREDIT_VAL_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
        } catch (Exception e) {
            e.printStackTrace();
            // / _log.error("postCreditEnquiry","Exception e:"+e.getMessage());
            throw e;
        }
    }// end of debitAdjust.

    protected static String getPGTransactionID(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getPGTransactionID", "Entered");
        String userType = (String) p_requestMap.get("USER_TYPE");
        String inTxnId = (String) p_requestMap.get("TRANSACTION_ID");

        if (!InterfaceUtil.isNullString(userType))
            inTxnId = inTxnId + userType;

        p_requestMap.put("IN_RECON_ID", inTxnId);
        p_requestMap.put("IN_TXN_ID", inTxnId);
        if (_log.isDebugEnabled())
            _log.debug("getPGTransactionID", "exited");
        return inTxnId;
    }

    /**
     * This method is used to set the interface parameters into requestMap,
     * these parameters are as bellow
     * 1.Origin node type.
     * 2.Origin host type.
     * 
     * @throws Exception
     */
    private void setInterfaceParameters() throws Exception, BTSLBaseException {
        System.out.println();
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CURRENCY", currency.trim());

            String stateAllowed = FileCache.getValue(_interfaceID, "Comv_Soap_Rchg_State");
            if (InterfaceUtil.isNullString(stateAllowed)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Comv_Soap_Rchg_State is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Comv_Soap_Rchg_State", stateAllowed.trim());

            String valAction = FileCache.getValue(_interfaceID, "Soap_Val_Action");
            if (InterfaceUtil.isNullString(valAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Soap_Val_Action is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Soap_Val_Action", valAction.trim());

            String topAction = FileCache.getValue(_interfaceID, "Soap_Top_Action");
            if (InterfaceUtil.isNullString(topAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Soap_Top_Action is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Soap_Top_Action", topAction.trim());

            String adjAction = FileCache.getValue(_interfaceID, "Soap_Adj_Action");
            if (InterfaceUtil.isNullString(adjAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Soap_Adj_Action is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("Soap_Adj_Action", adjAction.trim());

            String id = FileCache.getValue(_interfaceID, "COMV_INIT_ID");
            if (InterfaceUtil.isNullString(id)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_INIT_ID is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("COMV_INIT_ID", id.trim());

            String pwd = FileCache.getValue(_interfaceID, "COMV_INIT_PASSWORD");
            if (InterfaceUtil.isNullString(pwd)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_INIT_PASSWORD is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("COMV_INIT_PASSWORD", pwd.trim());

            String url = FileCache.getValue(_interfaceID, "COMV_SOAP_URL");
            if (InterfaceUtil.isNullString(url)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "COMV_SOAP_URL is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("COMV_SOAP_URL", url.trim());

            String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
            if (InterfaceUtil.isNullString(keepAlive)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "KEEP_ALIVE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("KEEP_ALIVE", keepAlive.trim());

            String valSoapAction = FileCache.getValue(_interfaceID, "SoapAction_VAL");
            if (InterfaceUtil.isNullString(valSoapAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "SoapAction_VAL is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SoapAction_VAL", valSoapAction.trim());

            String topSoapAction = FileCache.getValue(_interfaceID, "SoapAction_TOP");
            if (InterfaceUtil.isNullString(topSoapAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "SoapAction_TOP is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SoapAction_TOP", topSoapAction.trim());

            String adjSoapAction = FileCache.getValue(_interfaceID, "SoapAction_ADJ");
            if (InterfaceUtil.isNullString(adjSoapAction)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "SoapAction_ADJ is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SoapAction_ADJ", adjSoapAction.trim());

            String connectTimeout = FileCache.getValue(_interfaceID, "CONNECT_TIME_OUT");
            if (InterfaceUtil.isNullString(connectTimeout)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CONNECT_TIME_OUT is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CONNECT_TIME_OUT", connectTimeout.trim());

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
     * This method is used to send the request to IN and stored the response
     * after parsing.
     * This method also take care about to handle the errornious satuation to
     * send the alarm and set the error code.
     * 1.Invoke the getNodeVO method of NodeScheduler class and pass the
     * Transaction Id.
     * 2.If the VO is Null then mark the request as fail and throw exception(New
     * Error code that defines No connection for any Node is available).
     * 3.If the VO is not NULL then pass the Node detail to
     * CitiBankUrlConnection class and get connection.
     * 4.After the proccessing the request(may be successful or fail) decrement
     * the connection counter and pass the
     * transaction id that is removed from the transNodeList.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToPG(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToPG", "Entered p_inRequestStr::" + p_inRequestStr + " p_action::" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
        String responseStr = "";
        CitiBankUrlConnection CitiBankUrlConnection = null;
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
                    _log.debug("sendRequestToPG", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                try {
                    warnTime = Long.parseLong(_requestMap.get("WARN_TIMEOUT").toString());
                    url = _requestMap.get("COMV_SOAP_URL").toString();
                    String keepAlive = _requestMap.get("KEEP_ALIVE").toString();
                    String soapAction = "";
                    if (p_action == CitiBankI.ACTION_IMMEDIATE_DEBIT) {
                        soapAction = "NonVoucherRecharge";
                        readTimeOut = Integer.parseInt(FileCache.getValue(_interfaceID, "READ_TIMEOUT_DEBIT"));
                        retryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "RETRY_COUNT_DEBIT"));
                    }

                    connectTimeOut = Integer.parseInt(_requestMap.get("CONNECT_TIME_OUT").toString());
                    long length = p_inRequestStr.length();
                    int i = 0;
                    do {
                        try {
                            CitiBankUrlConnection = new CitiBankUrlConnection(url, connectTimeOut, readTimeOut, keepAlive, length, soapAction);
                        } catch (RuntimeException e) {
                            _log.errorTrace("sendRequestToPG", e);
                        }
                    } while (CitiBankUrlConnection != null && i++ < retryCount);
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToPG", "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + url + "] created");
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToPG", "BTSLBaseException be::" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                    _log.error("sendRequestToPG", "Exception while creating connection e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for Comverse IN with INTERFACE_ID=[" + _interfaceID + "]");
                    throw e;
                }

                long totalTimeNode = System.currentTimeMillis() - startTimeNode;

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToPG", "Total time to find the scheduled Node totalTimeNode: " + totalTimeNode);
            } catch (BTSLBaseException be) {
                be.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]" + " Exception ::" + be.getMessage());
                _log.error("sendRequestToPG", "Exception e::" + be.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]" + " Exception ::" + e.getMessage());
                _log.error("sendRequestToPG", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                PrintWriter out = CitiBankUrlConnection.getPrintWriter();
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToPG", "Exception e::" + e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to Comverse IN IN INTERFACE_ID=[" + _interfaceID + "] Exception::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
            }

            try {
                StringBuffer buffer = new StringBuffer();
                String response = "";
                try {
                    // Get the response from the IN
                    CitiBankUrlConnection.setBufferedReader();
                    BufferedReader in = CitiBankUrlConnection.getBufferedReader();
                    // Reading the response from buffered reader.
                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    if (warnTime <= (endTime - startTime)) {
                        if (_log.isInfoEnabled())
                            _log.info("sendRequestToPG", "WARN time reaches, startTime::" + startTime + " endTime::" + endTime + " From file cache warnTime::" + warnTime + " time taken (endTime-startTime)::" + (endTime - startTime));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CitiBankINHandler[sendRequestToPG]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Comverse IN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToPG", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the Comverse IN for INTERFACE_ID=[" + _interfaceID + "] " + "Exception=" + e.getMessage());
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    _log.error("sendRequestToPG", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + readTimeOut);
                }
                responseStr = buffer.toString();

                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToPG", "responseStr::" + responseStr);

                String httpStatus = CitiBankUrlConnection.getResponseCode();
                _requestMap.put("PROTOCOL_STATUS", httpStatus);
                if (!CitiBankI.HTTP_STATUS_200.equals(httpStatus))
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                // Check if there is no response, handle the event showing Blank
                // response from Comverse stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from Comverse IN");
                    _log.error("sendRequestToPG", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                _responseMap = _citiBankResponseFormatter.parseResponse(p_action, responseStr);

                String faultCode = (String) _responseMap.get("faultCode");
                if (!InterfaceUtil.isNullString(faultCode)) {
                    // Log the value of executionStatus for corresponding
                    // msisdn,recieved from the response.
                    if (_log.isInfoEnabled())
                        _log.info("sendRequestToPG", "faultCode::" + faultCode + "_inTXNID::" + _inTXNID + " _msisdn::" + _msisdn);
                    _requestMap.put("INTERFACE_STATUS", faultCode);// Put the
                                                                   // interface_status
                                                                   // in
                                                                   // requestMap
                    _log.error("sendRequestToPG", "faultCode=" + _responseMap.get("faultCode") + "faultString = " + _responseMap.get("faultString"));
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, (String) _responseMap.get("faultString"));
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }

                String errorCode = (String) _responseMap.get("ErrorCode");
                if (InterfaceUtil.isNullString(errorCode) || !errorCode.equals("0")) {
                    _requestMap.put("INTERFACE_STATUS", errorCode);// Put the
                                                                   // interface_status
                                                                   // in
                                                                   // requestMap
                    if (CitiBankI.CARD_DETAILS_NOT_FOUND.equals(errorCode)) {
                        _log.error("sendRequestToPG", "Subscriber not found with MSISDN::" + _msisdn);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber is not found at IN");
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }// end of checking the subscriber existance.
                    else {
                        _log.error("sendRequestToPG", "Error code received from IN ::" + errorCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + errorCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                }

                String responseCode = null;
                Object[] successList = null;

                if (p_action == CitiBankI.ACTION_IMMEDIATE_DEBIT) {
                    responseCode = _responseMap.get("NonVoucherRechargeResult").toString();
                    // check again sub tags.

                    // /
                    successList = CitiBankI.RESULT_OK.split(",");
                    if (!Arrays.asList(successList).contains(responseCode)) {
                        _log.error("sendRequestToPG", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
                }

                // _requestMap.put("INTERFACE_STATUS",responseCode);
                _requestMap.put("INTERFACE_STATUS", errorCode);
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToPG", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToPG", "BTSLBaseException be::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToPG", "Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                // Closing the HttpUrl connection
                if (CitiBankUrlConnection != null)
                    CitiBankUrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToPG", "While closing Comverse IN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CitiBankINHandler[sendRequestToPG]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and IP=[" + url + "]");
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToPG", "Exiting  _interfaceID::" + _interfaceID + " Stage::" + p_action + " responseStr::" + responseStr);
        }
    }// end of sendRequestToPG

    private void balanceEnquiryForAmbigousTransaction() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("balanceEnquiryForAmbigousTransaction", "Entered.");
        try {
            String balanceBeforeCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
            String expiryBeforeCredit = _requestMap.get("OLD_EXPIRY_DATE").toString();

            validate(_requestMap);

            String balanceAfterCredit = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
            String expiryAfterCredit = _requestMap.get("OLD_EXPIRY_DATE").toString();
            if (balanceBeforeCredit.equals(balanceAfterCredit)) {
                // fail the transaction
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
            } else {
                // success the transaction
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                _requestMap.put("INTERFACE_PREV_BALANCE", balanceBeforeCredit);
                _requestMap.put("OLD_EXPIRY_DATE", expiryBeforeCredit);

                _requestMap.put("INTERFACE_POST_BALANCE", balanceAfterCredit);
                _requestMap.put("NEW_EXPIRY_DATE", expiryAfterCredit);
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            }
        } catch (BTSLBaseException be) {
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("balanceEnquiryForAmbigousTransaction", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "balanceEnquiryForAmbigousTransaction", InterfaceErrorCodesI.AMBIGOUS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("balanceEnquiryForAmbigousTransaction", "Exited");
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
                                                                                                   // system.
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                // added to discard amount field from the message.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }
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

    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }// end of validate

    public void credit(HashMap<String, String> p_map) throws BTSLBaseException, Exception {

    }

    public void creditAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {

    }

    public void validityAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {

    }
}
