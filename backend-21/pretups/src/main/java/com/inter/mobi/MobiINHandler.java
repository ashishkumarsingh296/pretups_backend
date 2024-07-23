/*
 * Created on Jul 24, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.mobi;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
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
import com.btsl.pretups.inter.module.HandlerCommonUtility;
import com.btsl.pretups.inter.module.HandlerUtilityManager;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class MobiINHandler extends HandlerCommonUtility implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(MobiINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the respose of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _receiverMsisdn = null;
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;

    /**
     * This method is used to validate the subscriber number on interface.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);

        _requestMap = p_requestMap;
        String responseStr = null;
        String subscriberType = null;

        try {
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // Check interface status before sending request to IN.
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            // get IN reconciliation Id to be sent to IN.
            _inTXNID = getINReconTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // send request to IN to get Subscriber type.
            sendRequestToIN(MobiI.ACTION_SUBSCRIBER_TYPE);
            subscriberType = (String) _responseMap.get("SubscriberType");
            _requestMap.put("INT_SUBS_TYPE", subscriberType);
            // If subscriber type is found PRE or UNKNOWN, throw exception
            // INTERFACE_MSISDN_NOT_FOUND.
            if ((MobiI.PRE.equals(subscriberType) || MobiI.UNKNOWN.equals(subscriberType))) {
                _log.error("validate", "msisdn not found on interface" + _interfaceID);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            }
            // ***************************************************
            // this changes is done for only moldova requirment, where both
            // recharge and post paid bill payment service will perform through
            // common recharge api.
            // if service is CRC and we get the subscriber type then return ,and
            // call corresponding controller from common recharge controller.

            if (PretupsI.SERVICE_TYPE_CHNL_COMMON_RECHARGE.equals((String) _requestMap.get("REQ_SERVICE"))) {
                return;
            }

            // ************************************************

            else if (MobiI.POST.equals(subscriberType)) {
                // If subscriber type is POST, then process as normal.
                // Fetch value of VALIDATION key from IN File. (this is used to
                // ensure that validation will be done on IN or not)
                // If validation of subscriber is not required set the SUCCESS
                // code into request map and return.
                String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
                // String validateRequired="Y";
                if ("N".equals(validateRequired)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    return;
                }

                // Check interface status before sending request to IN.
                checkInterfaceB4SendingRequest();

                // get value of multiplication factor IN file. If it is not
                // defined, then handle event.
                String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multiplicationFactor)) {
                    _log.error("validate", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multiplicationFactor = multiplicationFactor.trim();

                // set the interface specific parameters into _requestMap
                setInterfaceParameters();

                // get IN reconciliation Id to be sent to IN.
                _inTXNID = getINReconTxnID();
                _requestMap.put("IN_TXN_ID", _inTXNID);

                // sending the AccountInfo request to IN
                sendRequestToIN(MobiI.ACTION_ACCOUNT_INFO);
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

                // get response string (set in request map in sendRequestToIN)
                // from request map
                responseStr = (String) _requestMap.get("RESPONSE_STR");

                // get user balance from response map. Multiply it with
                // multiplication factor.
                String amountStr = (String) _responseMap.get("BALANCE");
                String amountCrLimitStr = (String) _responseMap.get("CreditLimit");

                try {
                    amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multiplicationFactor));
                    _requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
                    amountCrLimitStr = InterfaceUtil.getSystemAmountFromINAmount(amountCrLimitStr, Double.parseDouble(multiplicationFactor));
                    _requestMap.put("INTERFACE_CREDIT_LIMIT", amountCrLimitStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("validate", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance or Credit Limit obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
                }

                /*
                 * Object[]
                 * negativeBalAllowedList=InterfaceUtil.NullToString(FileCache
                 * .getValue
                 * (_interfaceID,"NEGATIVE_BAL_ALLOWED_PROFILES")).split(",");
                 * if(!Arrays.asList(negativeBalAllowedList).contains(_responseMap
                 * .get("ServiceClass")) && Double.parseDouble(amountStr)<0)
                 * {
                 * _log.error("validate",
                 * "Transaction not allowed due to negative balance of a customer in profile"
                 * +_responseMap.get("ServiceClass"));
                 * EventHandler.handle(EventIDI.SYSTEM_ERROR,
                 * EventComponentI.INTERFACES, EventStatusI.RAISED,
                 * EventLevelI.FATAL,
                 * "MobiINHandler[validate]",_referenceID,_msisdn
                 * +" INTERFACE ID = "+_interfaceID, (String)
                 * _requestMap.get("NETWORK_CODE"),
                 * "Transaction not allowed due to negative balance of a customer in profile"
                 * +_responseMap.get("ServiceClass"));
                 * throw new
                 * BTSLBaseException(this,"validate",InterfaceErrorCodesI
                 * .INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
                 * }
                 */

                _requestMap.put("SERVICE_CLASS", _responseMap.get("ServiceClass"));
                _requestMap.put("ACCOUNT_STATUS", _responseMap.get("ACCOUNTStatus"));

                // set the mapping language of our system from FileCache mapping
                // based on the responsed language.
                setLanguageFromMapping();
            }
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            // If response is Ambiguous update interace closure related
            // parameters
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[validate]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_ACCOUNT_INFO, "While validating the subscriber get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited responseStr" + responseStr + " p_requestMap:" + p_requestMap);
        }
    }

    /**
     * This method would be used for Credit the user on the interface.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String responseStr = null;
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // Check Interface status b4 sending request to IN
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            // get IN reconciliation Id to be sent to IN.
            _inTXNID = getINReconTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // get transaction id for the current transaction
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // get value of multiplication factor IN file. If it is not defined,
            // then handle event.
            String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multiplicationFactor:" + multiplicationFactor);
            if (InterfaceUtil.isNullString(multiplicationFactor)) {
                _log.error("credit", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multiplicationFactor = multiplicationFactor.trim();
            _requestMap.put("MULTIPLICATION_FACTOR", multiplicationFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters();
            // set value of SYSTEM_STATUS_MAPPING in request map.
            // This value is used as key while picking mapped error code (used
            // in Reconciliation file generated for ambiguous cases)
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            try {
                // get amount to be sent to IN (using multiplication factor)
                multFactorDouble = Double.parseDouble(multiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");

                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "MobiINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Recharge request to IN along with recharge action
            // defined in MobiI interface
            sendRequestToIN(MobiI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            responseStr = (String) _requestMap.get("RESPONSE_STR");
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            try {
                // get user new balance from response map. Multiply it with
                // multiplication factor.
                String postBalanceStr = (String) _responseMap.get("BALANCE_Dest");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "BALANCE_Dest  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // indicates that final amount would be sent in message.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            // This time is logged in Reconciliation file when an ambiguous case
            // occurs.
            // Otherwise it is not used
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                // If response is Ambiguous update interace closure related
                // parameters
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                // This indicates type of transaction (CR==>credit)
                _requestMap.put("TRANSACTION_TYPE", "CR");
                // Used to generate reconciliation file entry for ambiguous case
                // happened.
                // This also supports, logic for cancel transaction (commented).
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_RECHARGE_CREDIT, "While credit the subscriber's account, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited responseStr:" + responseStr + " p_requestMap:" + p_requestMap);
        }
    }

    /**
     * This method would be used for Credit the user on the interface.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String responseStr = null;
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        String isOneStepCreditDebit = null;

        try {
            // get user type from request( R-receiver/ S-sender)
            _userType = (String) _requestMap.get("USER_TYPE");
            // get if request is for single step credit and debit
            isOneStepCreditDebit = (String) _requestMap.get("SINGLE_STEP_CREDIT_DEBIT");
            // get IN reconciliation Id to be sent to IN.
            _inTXNID = getINReconTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            // If request is for one step credit-debit then call
            // singleStepCreditDebit method and return
            // else process as normal
            if ("Y".equals(isOneStepCreditDebit)) {
                singleStepCreditDebit(p_requestMap);
                return;
            }
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            // if user type is receiver then only check interface status. (This
            // is applicable for vreditAdjust only)
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // get value of multiplication factor IN file. If it is not defined,
            // then handle event.
            String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "multiplicationFactor:" + multiplicationFactor);
            if (InterfaceUtil.isNullString(multiplicationFactor)) {
                _log.error("credit", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multiplicationFactor = multiplicationFactor.trim();
            _requestMap.put("MULTIPLICATION_FACTOR", multiplicationFactor);
            // Set the interface parameters into requestMap
            setInterfaceParameters();
            // set value of SYSTEM_STATUS_MAPPING in request map.
            // This value is used as key while picking mapped error code (used
            // in Reconciliation file generated for ambiguous cases)
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            try {
                multFactorDouble = Double.parseDouble(multiplicationFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("creditAdjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "MobiINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
            // sending the Recharge request to IN along with recharge action
            // defined in MobiI interface
            sendRequestToIN(MobiI.ACTION_IMMEDIATE_DEBIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            responseStr = (String) _requestMap.get("RESPONSE_STR");
            // get user new balance from response map. Multiply it with
            // multiplication factor.
            // truncate all digits after 2 (configured) points of decimal
            // Set the new balance INTERFACE_POST_BALANCE, in request map after
            // truncate.
            try {
                String postBalanceStr = (String) _responseMap.get("BALANCE_Dest");
                // postBalanceStr =
                // InterfaceUtil.getTrunncatedSystemAmountFromINAmount(postBalanceStr,multFactorDouble,Integer.parseInt(truncateAfterDecimalStr));
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            // If exception is not AMBIGUOUS then throw exception else update
            // countes and make entry in reconciliation file
            // extra condition added to check if the inteterface closer related
            // counters already updated in case of single step debit credit
            if ("Y".equals(isOneStepCreditDebit) || !(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;

            // This time is logged in Reconciliation file when an ambiguous case
            // occurs.
            // Otherwise it is not used
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust", "BTSLBaseException be:" + be.getMessage());
            try {
                // If response is Ambiguous and it is not credit back request,
                // update interace closure related parameters
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                // This indicates type of transaction (CR==>credit)
                _requestMap.put("TRANSACTION_TYPE", "CR");
                // Used to generate reconciliation file entry for ambiguous case
                // happened.
                // This also supports, logic for cancel transaction (commented).
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[creditAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_RECHARGE_CREDIT, "While creditAdjust the subscriber's account, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited responseStr:" + responseStr + " p_requestMap:" + p_requestMap);
        }
    }

    /**
     * This method would be used for Debit the user on the interface
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String responseStr = null;
        String isOneStepCreditDebit = null;
        _requestMap = p_requestMap;
        double systemAmtDouble = 0;
        double interfaceAmtDouble = 0;
        String amountStr = null;
        try {
            // If request is for one step credit-debit then return
            // else process as normal
            isOneStepCreditDebit = (String) _requestMap.get("SINGLE_STEP_CREDIT_DEBIT");
            if ("Y".equals(isOneStepCreditDebit)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            // process as normal
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            // Check Interface status b4 sending request to IN
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

            // get IN reconciliation Id to be sent to IN.
            _inTXNID = getINReconTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");// get msisdn from
                                                         // request map
            _receiverMsisdn = (String) _requestMap.get("RECEIVER_MSISDN");// get
                                                                          // msisdn
                                                                          // from
                                                                          // request
                                                                          // map
            // get value of multiplication factor IN file. If it is not defined,
            // then handle event.
            String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "multiplicationFactor:" + multiplicationFactor);
            if (InterfaceUtil.isNullString(multiplicationFactor)) {
                _log.error("debitAdjust", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "debitAadjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multiplicationFactor = multiplicationFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();
            // set value of SYSTEM_STATUS_MAPPING in request map.
            // This value is used as key while picking mapped error code (used
            // in Reconciliation file generated for ambiguous cases)
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                double multFactorDouble = Double.parseDouble(multiplicationFactor);
                interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("debitAadjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "MobiINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }

                // If rounding of amount is allowed, truncate the amount value
                // string and put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // String transferDebitAmount = "-"+amountStr;
            String transferDebitAmount = amountStr;
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "transfer_amount:" + transferDebitAmount + " multiplicationFactor:" + multiplicationFactor);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", transferDebitAmount);

            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("Y".equals(validateRequired)) {
                String prevBalanceAmountStr = (String) p_requestMap.get("INTERFACE_PREV_BALANCE");
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "prevBalanceAmountStr:" + prevBalanceAmountStr);
                if (InterfaceUtil.isNullString(prevBalanceAmountStr) || !InterfaceUtil.isNumeric(prevBalanceAmountStr)) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE present in the requestMap is either null or not numeric");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT present in the requestMap is either null or not numeric");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                double prevBalanceAmount = Double.parseDouble(prevBalanceAmountStr.trim());

                Object[] negativeBalAllowedList = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "NEGATIVE_BAL_ALLOWED_PROFILES")).split(",");
                // Checking the sufficient previous balance, if previous balance
                // is less than that of the debit amount, throw the exception.
                if (!Arrays.asList(negativeBalAllowedList).contains(_requestMap.get("SERVICE_CLASS")) && (prevBalanceAmount < interfaceAmtDouble)) {
                    _log.error("debitAdjust", "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_PREV_BALANCE[" + prevBalanceAmountStr + "]is less than INTERFACE_AMOUNT[" + interfaceAmtDouble + "]");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
                }
            }

            sendRequestToIN(MobiI.ACTION_IMMEDIATE_DEBIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            responseStr = (String) _requestMap.get("RESPONSE_STR");
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            try {
                // get user new balance from response map. Multiply it with
                // multiplication factor.
                String postBalanceStr = (String) _responseMap.get("BALANCE_Source");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, Double.parseDouble(multiplicationFactor));
                _requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from response is not NUMERIC while parsing the balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // set POST_BALANCE_ENQ_SUCCESS as N in request map. why...????
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            // This time is logged in Reconciliation file when an ambiguous case
            // occurs.
            // Otherwise it is not used
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            // If exception is not AMBIGUOUS then throw exception else update
            // countes and make entry in reconciliation file
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                // If response is Ambiguous, update interace closure related
                // parameters
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                // This indicates type of transaction (DR==>debit)
                _requestMap.put("TRANSACTION_TYPE", "DR");
                // Used to generate reconciliation file entry for ambiguous case
                // happened.
                // This also supports, logic for cancel transaction (commented).
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[debitAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_IMMEDIATE_DEBIT, "While debitAdjsut the the subscriber's account, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited responseStr:" + responseStr + " p_requestMap:" + p_requestMap);
        }
    }

    /**
     * This method would be used for Debit sender and
     * credit receiver in single request to IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     */
    public void singleStepCreditDebit(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("singleStepCreditDebit", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String responseStr = null;
        double senderSystemAmtDouble = 0;
        double receiverSystemAmtDouble = 0;
        double senderInterfaceAmtDouble = 0;
        double receiverInterfaceAmtDouble = 0;
        String senderAmountStr = null;
        String receiverAmountStr = null;

        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            // Check Interface status b4 sending request to IN
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

            // get IN reconciliation Id to be sent to IN.
            _inTXNID = getINReconTxnID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("SENDER_MSISDN");// get msisdn
                                                                // from request
                                                                // map
            _receiverMsisdn = (String) _requestMap.get("RECEIVER_MSISDN");
            // get value of multiplication factor IN file. If it is not defined,
            // then handle event.
            String multiplicationFactor = FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("singleStepCreditDebit", "MultiplicationFactor:" + multiplicationFactor);
            if (InterfaceUtil.isNullString(multiplicationFactor)) {
                _log.error("debitAdjust", "MULTIPLICATION_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULTIPLICATION_FACTOR  is  not defined in the INFile");
                throw new BTSLBaseException(this, "singleStepCreditDebit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multiplicationFactor = multiplicationFactor.trim();
            // Set the interface parameters into requestMap
            setInterfaceParameters();
            // set value of SYSTEM_STATUS_MAPPING in request map.
            // This value is used as key while picking mapped error code (used
            // in Reconciliation file generated for ambiguous cases)
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try {
                double multFactorDouble = Double.parseDouble(multiplicationFactor);
                senderInterfaceAmtDouble = Double.parseDouble((String) _requestMap.get("SENDER_INTERFACE_AMOUNT"));
                receiverInterfaceAmtDouble = Double.parseDouble((String) _requestMap.get("RECEIVER_INTERFACE_AMOUNT"));

                senderSystemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(senderInterfaceAmtDouble, multFactorDouble);
                receiverSystemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(receiverInterfaceAmtDouble, multFactorDouble);

                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("debitAadjust", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "MobiINHandler[debitAdjust]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }

                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    senderAmountStr = String.valueOf(Math.round(senderSystemAmtDouble));
                    _requestMap.put("SENDER_INTERFACE_ROUND_AMOUNT", senderAmountStr);
                    receiverAmountStr = String.valueOf(Math.round(receiverSystemAmtDouble));
                    _requestMap.put("RECEIVER_INTERFACE_ROUND_AMOUNT", receiverAmountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("singleStepCreditDebit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SENDER_INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "singleStepCreditDebit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // set transfer_amount in request map as amountStr (which is round
            // value of SENDER_INTERFACE_AMOUNT)
            // _requestMap.put("transfer_amount",transferCreditDebitAmount);
            _requestMap.put("sender_transfer_amount", senderAmountStr);

            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("Y".equals(validateRequired)) {
                String senderPrevBalanceAmountStr = (String) p_requestMap.get("SENDER_INTERFACE_PREV_BALANCE");
                // String receiverPrevBalanceAmountStr =
                // (String)p_requestMap.get("RECEIVER_INTERFACE_PREV_BALANCE");
                if (_log.isDebugEnabled())
                    _log.debug("singleStepCreditDebit", "prevBalanceAmountStr:" + senderPrevBalanceAmountStr);
                if (InterfaceUtil.isNullString(senderPrevBalanceAmountStr) || !InterfaceUtil.isNumeric(senderPrevBalanceAmountStr)) {
                    _log.error("debitAdjust", "SENDER_INTERFACE_PREV_BALANCE present in the requestMap is either null or not numeric");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SENDER_INTERFACE_PREV_BALANCE present in the requestMap is either null or not numeric");
                    throw new BTSLBaseException(this, "singleStepCreditDebit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                double senderPrevBalanceAmount = Double.parseDouble(senderPrevBalanceAmountStr.trim());
                Object[] negativeBalAllowedList = InterfaceUtil.NullToString(FileCache.getValue(_interfaceID, "NEGATIVE_BAL_ALLOWED_PROFILES")).split(",");
                // Checking the sufficient previous balance, if previous balance
                // is less than that of the debit amount, throw the exception.
                if (!Arrays.asList(negativeBalAllowedList).contains(_requestMap.get("SENDER_SERVICE_CLASS")) && (senderPrevBalanceAmount < senderInterfaceAmtDouble)) {
                    _log.error("debitAdjust", "SENDER_INTERFACE_PREV_BALANCE[" + senderPrevBalanceAmountStr + "]is less than SENDER_INTERFACE_AMOUNT[" + senderInterfaceAmtDouble + "]");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SENDER_INTERFACE_PREV_BALANCE[" + senderPrevBalanceAmountStr + "]is less than SENDER_INTERFACE_AMOUNT[" + senderInterfaceAmtDouble + "]");
                    throw new BTSLBaseException(this, "singleStepCreditDebit", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
                }
            }
            sendRequestToIN(MobiI.ACTION_SINGLE_STEP_DEBIT_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            responseStr = (String) _requestMap.get("RESPONSE_STR");

            try {
                // get sender and receiver new balance from response map.
                // Multiply it with multiplication factor.
                // Set the new balance as SENDER_INTERFACE_POST_BALANCE and
                // RECEIVER_INTERFACE_POST_BALANCE, in request map after
                // truncate.
                String senderPostBalanceStr = (String) _responseMap.get("BALANCE_Source");
                // senderPostBalanceStr=InterfaceUtil.getTrunncatedSystemAmountFromINAmount(senderPostBalanceStr,Double.parseDouble(multiplicationFactor),Integer.parseInt(truncateAfterDecimalStr));
                senderPostBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(senderPostBalanceStr, Double.parseDouble(multiplicationFactor));
                _requestMap.put("SENDER_INTERFACE_POST_BALANCE", senderPostBalanceStr);

                String receiverPostBalanceStr = (String) _responseMap.get("BALANCE_Dest");
                receiverPostBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(receiverPostBalanceStr, Double.parseDouble(multiplicationFactor));
                _requestMap.put("RECEIVER_INTERFACE_POST_BALANCE", receiverPostBalanceStr);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("singleStepCreditDebit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from response is not NUMERIC while parsing the balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "singleStepCreditDebit", InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            _requestMap.put("SENDER_POST_BALANCE_ENQ_SUCCESS", "Y");
            _requestMap.put("RECEIVER_POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {

            // This time is logged in Reconciliation file when an ambiguous case
            // occurs.
            // Otherwise it is not used
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("singleStepCreditDebit", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                // If response is Ambiguous, update interace closure related
                // parameters
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                // This indicates type of transaction (CRDR==>credit,debit in
                // single step)
                _requestMap.put("TRANSACTION_TYPE", "CRDR");
                // Used to generate reconciliation file entry for ambiguous case
                // happened.
                // This also supports, logic for cancel transaction (commented).
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("singleStepCreditDebit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[singleStepCreditDebit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in singleStepCreditDebit");
                throw new BTSLBaseException(this, "singleStepCreditDebit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("singleStepCreditDebit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[debitAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_IMMEDIATE_DEBIT, "While debitAdjsut the the subscriber's account, get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
            _requestMap.put("MSISDN", _receiverMsisdn);
            if (_log.isDebugEnabled())
                _log.debug("singleStepCreditDebit", "Exited responseStr:" + responseStr + " p_requestMap:" + p_requestMap);
        }
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
     * This method would be used to set all interface related configurable
     * parameters.
     * 
     * @throws BTSLBaseException
     *             ,Exception
     */
    private void setInterfaceParameters() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered _interfaceID=" + _interfaceID);

        try {
            // All following parameters loaded from File Cache are interface
            // closure related parameters
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap" + _requestMap);
        }// end of finally
    }

    /**
     * This method would be used to communicate with IN.
     * Request would be sent and response received from IN.
     * Received response is parsed and if status is not 0, exception is thrown
     * 
     * @param int p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN entered ", "p_action " + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Stored Proceedure is Called for stage:" + p_action, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
        Object dbUtility = null;
        Connection dbConnection = null;
        String inReconID = null;
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        try {
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            // In creditAdjust and subscriber type query, don't check interface
            // status, simply send the request to IN.
            // if((!("Y".equals(_requestMap.get("ADJUST")) &&
            // "C".equals(_requestMap.get("INTERFACE_ACTION"))) &&
            // "S".equals(_userType)) || p_action!=MobiI.ACTION_SUBSCRIBER_TYPE)
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && (!("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }

            // If this method (sendRequestToIN) is called for
            // ACTION_SUBSCRIBER_TYPE action from get the utility object from
            // from Common Utility manager (used by both mobi and cboss In
            // handlers).
            // else get utility object from mobi utility manager

            if (MobiI.ACTION_SUBSCRIBER_TYPE == p_action) {
                dbUtility = HandlerUtilityManager._utilityObjectMap.get(_interfaceID);
                if (dbUtility == null) {
                    _log.error("sendRequestToIN", "dbUtility:" + dbUtility);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);// Confirm
                                                                                                       // for
                                                                                                       // the
                                                                                                       // Error
                                                                                                       // Code.
                }
                dbConnection = getConnection();
            } else {
                dbUtility = MobiDBPoolManager._dbUtilityObjectMap.get(_interfaceID);
                if (dbUtility == null) {
                    _log.error("sendRequestToIN", "dbUtility:" + dbUtility);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);// Confirm
                                                                                                       // for
                                                                                                       // the
                                                                                                       // Error
                                                                                                       // Code.
                }
                dbConnection = ((MobiDBUtility) dbUtility).getConnection();
            }

            if (dbConnection == null) {
                _log.error("sendRequestToIN", "dbConnection=" + dbConnection);
                // Confirm for the Event handling.
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "MobiINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "DBConnection is NULL");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);// Confrim
                                                                                                                          // for
                                                                                                                          // the
                                                                                                                          // new
                                                                                                                          // key
            }
            startTime = System.currentTimeMillis();
            _requestMap.put("IN_START_TIME", String.valueOf(startTime));

            // based on action call different methods which will inturn
            // communicate with IN
            switch (p_action) {
            case MobiI.ACTION_ACCOUNT_INFO: {
                sendAccountInfoRequest(dbConnection);
                break;
            }
            case MobiI.ACTION_RECHARGE_CREDIT: {
                sendCreditRechargeRequest(dbConnection);
                break;
            }
            case MobiI.ACTION_IMMEDIATE_DEBIT: {
                if ("D".equals(_requestMap.get("INTERFACE_ACTION")))
                    sendDebitAdjustRequest(dbConnection);
                else
                    sendCreditAdjustRequest(dbConnection);
                break;
            }
            case MobiI.ACTION_SUBSCRIBER_TYPE: {
                // sendSubscriberIdentificationRequest(dbConnection,_requestMap);
                sendSubscriberIdentificationRequest(dbConnection);
                break;
            }
            case MobiI.ACTION_SINGLE_STEP_DEBIT_CREDIT: {
                sendSingleStepDebitCredit(dbConnection);
                break;
            }
            }
            dbConnection.commit();
            endTime = System.currentTimeMillis();
        } catch (BTSLBaseException be) {
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "While calling the stored proc get Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                if (dbConnection != null)
                    dbConnection.close();
            } catch (Exception ex) {
            }
            if (endTime == 0)
                endTime = System.currentTimeMillis();
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            _log.error("sendRequestToIN", "Request sent to IN at = " + startTime + ". Response received at = " + endTime);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "exited");
        }
        String warnTimeStr = FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
        if (!InterfaceUtil.isNullString(warnTimeStr))
            warnTime = Long.parseLong(warnTimeStr);
        if (endTime - startTime > warnTime) {
            _log.error("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobiINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
        }
        // Fetch the response
        String responseStr = (String) _requestMap.get("RESPONSE_STR");
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Request String :" + "" + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
        if (InterfaceUtil.isNullString(responseStr)) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Response from the IN:" + responseStr);
            _log.error("sendRequestToIN", "Response from the IN is null");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
        }
        _responseMap = new HashMap();
        // Convert the responseString into hash map.
        InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");

        String status = (String) _responseMap.get("Status");

        // UPDATE interface related parameters on successful response (if
        // current method is not for subscriber type query and not for sender
        // credit back)
        // if((!("Y".equals(_requestMap.get("ADJUST")) &&
        // "C".equals(_requestMap.get("INTERFACE_ACTION"))) &&
        // "S".equals(_userType)) || p_action!=MobiI.ACTION_SUBSCRIBER_TYPE)
        if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && (!("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))) && "S".equals(_userType))) {
            if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
            _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
        }
        String mobiTxnId = (String) _responseMap.get("transactionNumber");

        _requestMap.put("INTERFACE_STATUS", status);
        if (status.charAt(0) == '-') {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Response from the IN:" + responseStr);
            _log.error("sendRequestToIN", "Error in response with" + " status=" + status);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
        }
        if (status.charAt(0) == '+')
            status = status.substring(1);

        if (!MobiI.RESULT_OK.equals(status)) {
            if (p_action == MobiI.ACTION_SUBSCRIBER_TYPE && MobiI.SUBTYPE_SUBSCRIBER_NOT_FOUND.equals(status)) {
                _log.error("sendRequestToIN", " Error in response SUBTYPE_SUBSCRIBER_NOT_FOUND");
                EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " SUBTYPE_SUBSCRIBER_NOT_FOUND AT IN");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            } else if (p_action == MobiI.ACTION_ACCOUNT_INFO && (MobiI.ACNTINFO_PREPAID.equals(status) || MobiI.SUBSCRIBER_NOT_FOUND.equals(status))) {
                _log.error("sendRequestToIN", " Error in response. Either subscriber is prepaid or subscriber not found during Account Information");
                EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " Either subscriber is prepaid or subscriber not found during Account Information");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            }
            _log.error("sendRequestToIN", "Error in response with" + " status=" + status);
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, " Error Response Code =" + status);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
        }

        // If reconciliation id sent to IN and received from IN, are not equal
        // throw exception
        if (!(_inTXNID.equals(mobiTxnId))) {
            _log.error("sendRequestToIN", "Transaction id set in the request [" + inReconID + "] does not matched with the transaction id fetched from response[" + mobiTxnId + "]");
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " p_action = " + p_action, "Transaction id set in the request [" + _inTXNID + "] does not match with the transaction id fetched from response[" + mobiTxnId + "],Hence marking the transaction as AMBIGUOUS");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.AMBIGOUS);
        }

    }

    /**
     * This method would be used to extract account information from IN.
     * 
     * @param Connection
     *            p_connection
     * @throws BTSLBaseException
     */

    /*
     * private void sendAccountInfoRequest(Connection p_connection) throws
     * BTSLBaseException
     * {
     * if(_log.isDebugEnabled())_log.debug("sendAccountInfoRequest","Entered");
     * CallableStatement callStmt = null;
     * StringBuffer responseBuffer=null;
     * String responseStr=null;
     * try
     * {
     * //call procedure getAccountInformation and concatenate all returned
     * parametes.
     * //put the concatenated response string in response map as RESPONSE_STR
     * String
     * procStr="call PB.bharti_tools.getAccountInformation(?,?,?,?,?,?,?,?,?,?,?)"
     * ;
     * callStmt=p_connection.prepareCall(procStr);
     * callStmt.setString(1,InterfaceUtil.getFilterMSISDN((String)_requestMap.get
     * ("INTERFACE_ID"),(String)_requestMap.get("MSISDN")));
     * callStmt.setString(2,(String)_requestMap.get("IN_RECON_ID"));
     * callStmt.registerOutParameter(3,Types.VARCHAR);
     * callStmt.registerOutParameter(4,Types.VARCHAR);
     * callStmt.registerOutParameter(5,Types.INTEGER);
     * callStmt.registerOutParameter(6,Types.VARCHAR);
     * callStmt.registerOutParameter(7,Types.VARCHAR);
     * callStmt.registerOutParameter(8,Types.DOUBLE);
     * callStmt.registerOutParameter(9,Types.VARCHAR);
     * callStmt.registerOutParameter(10,Types.VARCHAR);
     * callStmt.registerOutParameter(11,Types.DOUBLE);
     * callStmt.execute();
     * responseBuffer=new StringBuffer(1028);
     * 
     * responseBuffer.append("Status="+callStmt.getString(3));
     * responseBuffer.append("&transactionNumber="+callStmt.getString(4));
     * responseBuffer.append("&ServiceClass="+String.valueOf(callStmt.getInt(5)))
     * ;
     * responseBuffer.append("&AccountID="+String.valueOf(callStmt.getString(6)))
     * ;
     * responseBuffer.append("&ACCOUNTStatus="+callStmt.getString(7));
     * responseBuffer.append("&CreditLimit="+callStmt.getDouble(8));
     * responseBuffer.append("&LanguageID="+callStmt.getString(9));
     * responseBuffer.append("&IMSI="+callStmt.getString(10));
     * responseBuffer.append("&BALANCE="+callStmt.getDouble(11));
     * responseStr=responseBuffer.toString();
     * _requestMap.put("RESPONSE_STR",responseStr);
     * 
     * }
     * catch(SQLException sqe)
     * {
     * sqe.printStackTrace();
     * _log.error("sendAccountInfoRequest","SQLException sqe:"+sqe.getMessage());
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,
     * EventStatusI
     * .RAISED,EventLevelI.FATAL,"MobiINHandler[sendAccountInfoRequest]"
     * ,"REFERENCE ID = "
     * +(String)_requestMap.get("IN_TXN_ID")+"MSISDN = "+(String
     * )_requestMap.get(
     * "MSISDN"),"INTERFACE ID = "+(String)_requestMap.get("INTERFACE_ID"
     * ),"Network code = "+(String)
     * _requestMap.get("NETWORK_CODE")+" Action = "+MobiI.ACTION_ACCOUNT_INFO,
     * "While validating the subscriber get SQLException sqlEx:"
     * +sqe.getMessage());
     * throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
     * //throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("sendAccountInfoRequest","Exception e:"+e.getMessage());
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,
     * EventStatusI
     * .RAISED,EventLevelI.FATAL,"MobiINHandler[sendAccountInfoRequest]"
     * ,"REFERENCE ID = "
     * +(String)_requestMap.get("IN_TXN_ID")+"MSISDN = "+(String
     * )_requestMap.get(
     * "MSISDN"),"INTERFACE ID = "+(String)_requestMap.get("INTERFACE_ID"
     * ),"Network code = "+(String)
     * _requestMap.get("NETWORK_CODE")+" Action = "+
     * MobiI.ACTION_ACCOUNT_INFO,"While validating the subscriber get Exception e:"
     * +e.getMessage());
     * //throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
     * throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
     * }
     * finally
     * {
     * try{if(callStmt!=null)callStmt.clearParameters();}catch(Exception e){}
     * try{if(callStmt!=null)callStmt.close();}catch(Exception e){}
     * if(_log.isDebugEnabled())_log.debug("sendAccountInfoRequest","exited");
     * }
     * }
     */

    private void sendAccountInfoRequest(Connection p_connection) throws BTSLBaseException {
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
            String procStr = "call mobi.getAccountInformation(?,?,?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, filteredMsisdn);
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.registerOutParameter(3, Types.VARCHAR);
            callStmt.registerOutParameter(4, Types.VARCHAR);
            callStmt.registerOutParameter(5, Types.INTEGER);
            callStmt.registerOutParameter(6, Types.VARCHAR);
            callStmt.registerOutParameter(7, Types.VARCHAR);
            callStmt.registerOutParameter(8, Types.VARCHAR);
            callStmt.registerOutParameter(9, Types.VARCHAR);
            callStmt.registerOutParameter(10, Types.DOUBLE);
            callStmt.execute();
            responseBuffer = new StringBuffer(1028);

            responseBuffer.append("Status=" + callStmt.getString(3));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(4));
            responseBuffer.append("&ServiceClass=" + String.valueOf(callStmt.getInt(5)));
            responseBuffer.append("&AccountID=" + String.valueOf(callStmt.getString(6)));
            responseBuffer.append("&ACCOUNTStatus=" + callStmt.getString(7));
            responseBuffer.append("&LanguageID=" + callStmt.getString(8));
            responseBuffer.append("&IMSI=" + callStmt.getString(9));
            balance = callStmt.getDouble(10);
            responseBuffer.append("&BALANCE=" + balance);
            responseBuffer.append("&CreditLimit=" + balance);
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);

        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendAccountInfoRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_ACCOUNT_INFO, "While validating the subscriber get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendAccountInfoRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendAccountInfoRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_ACCOUNT_INFO, "While validating the subscriber get Exception e:" + e.getMessage());
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

    /**
     * This method would be used to recharge user's account on IN.
     * 
     * @param Connection
     *            p_connection
     * @throws BTSLBaseException
     */

    private void sendCreditRechargeRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendCreditRechargeRequest", "Entered");
        CallableStatement callStmt = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        String filteredMsisdn = null;
        try {
            filteredMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            // call procedure Adjustment_C2S_Post and concatenate all returned
            // parametes.
            // put the concatenated response string in response map as
            // RESPONSE_STR
            // String
            // procStr="call PB.bharti_tools.Adjustment_C2S_Post(?,?,?,?,?,?)";
            String procStr = "call mobi.Adjustment_C2S_Post(?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, filteredMsisdn);
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.setLong(3, Long.parseLong((String) _requestMap.get("transfer_amount")));
            callStmt.registerOutParameter(4, Types.VARCHAR);
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.DOUBLE);
            callStmt.execute();

            responseBuffer = new StringBuffer(1028);
            responseBuffer.append("Status=" + callStmt.getString(4));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(5));
            responseBuffer.append("&BALANCE_Dest=" + callStmt.getDouble(6));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendCreditRechargeRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendCreditRechargeRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_RECHARGE_CREDIT, "While credit get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendCreditRechargeRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendCreditRechargeRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_RECHARGE_CREDIT, "While credit get Exception e:" + e.getMessage());
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
                _log.debug("sendCreditRechargeRequest", "exited");
        }
    }

    /**
     * This method would be used for credit transfer in user's account on IN.
     * 
     * @param Connection
     *            p_connection
     * @throws BTSLBaseException
     */
    private void sendCreditAdjustRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendCreditAdjustRequest", "Entered");
        CallableStatement callStmt = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        String filteredMsisdn = null;
        try {
            filteredMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            // call procedure Adjustment_C2S_Post and concatenate all returned
            // parametes.
            // put the concatenated response string in response map as
            // RESPONSE_STR
            // String
            // procStr="call PB.bharti_tools.Adjustment_C2S_Post(?,?,?,?,?,?)";
            String procStr = "call mobi.Adjustment_C2S_Post(?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, filteredMsisdn);
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.setLong(3, Long.parseLong((String) _requestMap.get("transfer_amount")));
            callStmt.registerOutParameter(4, Types.VARCHAR);
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.DOUBLE);
            callStmt.execute();

            responseBuffer = new StringBuffer(1028);
            responseBuffer.append("Status=" + callStmt.getString(4));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(5));
            responseBuffer.append("&BALANCE_Dest=" + callStmt.getDouble(6));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendCreditAdjustRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendCreditAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_IMMEDIATE_DEBIT, "While credit Adjust get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendCreditAdjustRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendCreditAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_IMMEDIATE_DEBIT, "While credit Adjust get Exception e:" + e.getMessage());
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
                _log.debug("sendCreditAdjustRequest", "exited");
        }
    }

    /**
     * This method would be used for debit user's account on IN.
     * 
     * @param Connection
     *            p_connection
     * @throws BTSLBaseException
     */
    private void sendDebitAdjustRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendDebitAdjustRequest", "Entered");
        CallableStatement callStmt = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        String filteredSndrMsisdn = null;
        String filteredRcvrMsisdn = null;
        try {
            filteredSndrMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            filteredRcvrMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("RECEIVER_MSISDN"));
            // call procedure Adjustment_Post_Pre and concatenate all returned
            // parametes.
            // put the concatenated response string in response map as
            // RESPONSE_STR
            // String
            // procStr="call PB.bharti_tools.Adjustment_Post_Pre(?,?,?,?,?,?,?)";
            String procStr = "call mobi.Adjustment_Post_Pre(?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, filteredSndrMsisdn);
            callStmt.setString(2, filteredRcvrMsisdn);
            callStmt.setString(3, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.setLong(4, Long.parseLong((String) _requestMap.get("transfer_amount")));
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.VARCHAR);
            callStmt.registerOutParameter(7, Types.DOUBLE);
            callStmt.execute();

            responseBuffer = new StringBuffer(1028);
            responseBuffer.append("Status=" + callStmt.getString(5));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(6));
            responseBuffer.append("&BALANCE_Source=" + callStmt.getDouble(7));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);

        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendDebitAdjustRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_IMMEDIATE_DEBIT, "While debit Adjust  get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendDebitAdjustRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_IMMEDIATE_DEBIT, "While debit Adjust  get Exception e:" + e.getMessage());
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
                _log.debug("sendDebitAdjustRequest", "exited");
        }
    }

    /**
     * This method would be used for credit receiver's account
     * and debit sender er's account on IN in nsingle step.
     * 
     * @param Connection
     *            p_connection
     * @throws BTSLBaseException
     */
    private void sendSingleStepDebitCredit(Connection p_connection) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("sendSingleStepDebitCredit", "Entered _requestMap" + _requestMap);
        CallableStatement callStmt = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        String filteredSndrMsisdn = null;
        String filteredRcvrMsisdn = null;

        try {
            filteredSndrMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("SENDER_MSISDN"));
            filteredRcvrMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("RECEIVER_MSISDN"));
            // call procedure Adjustment_Post_Post and concatenate all returned
            // parametes.
            // put the concatenated response string in response map as
            // RESPONSE_STR
            // String
            // procStr="call PB.bharti_tools.Adjustment_Post_Post(?,?,?,?,?,?,?,?)";
            String procStr = "call mobi.Adjustment_Post_Post(?,?,?,?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, filteredSndrMsisdn);
            callStmt.setString(2, filteredRcvrMsisdn);
            callStmt.setString(3, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.setLong(4, Long.parseLong((String) _requestMap.get("sender_transfer_amount")));
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.registerOutParameter(6, Types.VARCHAR);
            callStmt.registerOutParameter(7, Types.DOUBLE);
            callStmt.registerOutParameter(8, Types.DOUBLE);
            callStmt.execute();

            responseBuffer = new StringBuffer(1028);
            responseBuffer.append("Status=" + callStmt.getString(5));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(6));
            responseBuffer.append("&BALANCE_Source=" + callStmt.getDouble(7));
            responseBuffer.append("&BALANCE_Dest=" + callStmt.getDouble(8));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendSingleStepDebitCredit", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_SINGLE_STEP_DEBIT_CREDIT, "While single step credit debit  get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            // throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendSingleStepDebitCredit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_SINGLE_STEP_DEBIT_CREDIT, "While single step credit debit  get Exception e:" + e.getMessage());
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
                _log.debug("sendSingleStepDebitCredit", "exited");
        }

    }

    /**
     * This method would be used to identify the subscriber's type
     * (POST/PRE/UNKNOWN)on IN.
     * 
     * @param Connection
     *            p_connection
     * @throws BTSLBaseException
     */

    private void sendSubscriberIdentificationRequest(Connection p_connection) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendSubscriberIdentificationRequest", "Entered");
        CallableStatement callStmt = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        String filteredMsisdn = null;
        try {
            filteredMsisdn = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            // call procedure getSubscriberIdentification and concatenate all
            // returned parametes.
            // put the concatenated response string in response map as
            // RESPONSE_STR
            // String
            // procStr="call PB.bharti_tools.getSubscriberIdentification(?,?,?,?,?)";
            String procStr = "call mobi.getSubscriberIdentification(?,?,?,?,?)";
            callStmt = p_connection.prepareCall(procStr);
            callStmt.setString(1, filteredMsisdn);
            callStmt.setString(2, (String) _requestMap.get("IN_RECON_ID"));
            callStmt.registerOutParameter(3, Types.VARCHAR);
            callStmt.registerOutParameter(4, Types.VARCHAR);
            callStmt.registerOutParameter(5, Types.VARCHAR);
            callStmt.execute();
            responseBuffer = new StringBuffer(1028);
            responseBuffer.append("Status=" + callStmt.getString(3));
            responseBuffer.append("&transactionNumber=" + callStmt.getString(4));
            responseBuffer.append("&SubscriberType=" + callStmt.getString(5));
            responseStr = responseBuffer.toString();
            _requestMap.put("RESPONSE_STR", responseStr);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            _log.error("sendSubscriberIdentificationRequest", "SQLException sqe:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_SUBSCRIBER_TYPE, "While subscriber type query  get SQLException sqlEx:" + sqe.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendSubscriberIdentificationRequest", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[sendDebitAdjustRequest]", "REFERENCE ID = " + (String) _requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) _requestMap.get("MSISDN"), "INTERFACE ID = " + (String) _requestMap.get("INTERFACE_ID"), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + MobiI.ACTION_SUBSCRIBER_TYPE, "While subscriber type query get Exception e:" + e.getMessage());
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
                _log.debug("sendSubscriberIdentificationRequest", "exited");
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
            langFromIN = (String) _responseMap.get("LanguageID");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString = " + mappingString + " langFromIN = " + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0; in < mappingArr.length; in++) {
                tempArr = mappingArr[in].split(":");
                if (langFromIN.equals(tempArr[0])) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobiINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobiINHandler[setLanguageFromMapping]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

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
             * _formatter.generateRequest(MobiI.ACTION_TXN_CANCEL, _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,MobiI.ACTION_TXN_CANCEL);
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

    /**
     * This method would be used for generation of reconciliation id
     * which would be sent to IN with each request.
     * 
     * @return String
     * @throws Exception
     */
    private String getINReconTxnID() throws Exception {
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
             */
            inReconID = ((String) _requestMap.get("TRANSACTION_ID"));

            _requestMap.put("IN_RECON_ID", inReconID);
            return inReconID;
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
}
