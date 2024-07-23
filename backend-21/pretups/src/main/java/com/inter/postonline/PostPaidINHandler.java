package com.inter.postonline;

import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.btsl.pretups.inter.postqueue.QueueTableHandler;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;
import com.enterprisedt.net.ftp.FTPException;
import com.inter.postonline.FTPPoolManager;

/**
 * @PostPaidINHandler.java
 *                         Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Ashish K Apr 3, 2007 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 *                         This class would be capable of interacting with the
 *                         IN either using stored procedure or via online
 *                         interface commands.
 *                         Modes of communication could be:
 *                         a. Invocation of External DB procedures
 *                         b. File Transfers vi FTP
 * 
 */
public class PostPaidINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(PostPaidINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the respose of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _userType = null;
    private String _interfaceClosureSupport = null;
    private static int _prevMinut = 0;
    private static long _counter = 0;
    private static SimpleDateFormat _sdf = new SimpleDateFormat("yyMMddHHmmss");
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");

    private static SimpleDateFormat _sdfTemp = new SimpleDateFormat("yyMMddHH:mm:ss:SSS");

    /**
     * This method would implements the logic to validate the post paid
     * subscriber.
     * Validation of subscriber would be done by following ways.
     * a. External DB system,would be decided based on the value of VAL_REQ_TYPE
     * (=1),defined in the INFile.
     * b. Internal system(QTable),would be decided based on the value of
     * VAL_REQ_TYPE(=2), defined in the INFile.
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        QueueTableHandler queueTableHandler = null;
        String multFactor = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // Fetch value of VALIDATION key from IN File. (this is used to
            // ensure that subscriber validation is required or not)
            // If validation of subscriber is not required set the SUCCESS code
            // into request map and return.
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            String validationReqType = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "VAL_REQ_TYPE");
            if (InterfaceUtil.isNullString(validationReqType)) {
                _log.error("validate", _requestMap.get("REQ_SERVICE") + "_" + "VAL_REQ_TYPE  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "VAL_REQ_TYPE  is not defined in the INFile");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            validationReqType = validationReqType.trim();
            setInterfaceParameters();
            if (PostPaidI.QUEUE_TABLE.equals(validationReqType)) {
                // Call the validate method of QTableHandler.
                queueTableHandler = new QueueTableHandler();
                queueTableHandler.validate(_requestMap);
                return;
            }
            _requestMap.put("VAL_REQ_TYPE", validationReqType);
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            if (PostPaidI.ONLINE_DB_SP.equals(validationReqType)) {
                multFactor = FileCache.getValue(_interfaceID, "DB_MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multFactor)) {
                    _log.error("validate", "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[validate]", "", p_requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) p_requestMap.get("NETWORK_CODE"), "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multFactor = multFactor.trim();
                // Validate the subscriber by using the stored proccedure.
                // Call the sendRequestToIN method by providing the validate
                // stage.
                sendRequestToIN(PostPaidI.ACTION_ACCOUNT_INFO);
                _requestMap.put("IN_RECON_ID", _responseMap.get("AccountId"));
                _requestMap.put("ACCOUNT_ID", _responseMap.get("AccountId"));
            } else {
                _log.error("validate", _requestMap.get("REQ_SERVICE") + "_" + "VAL_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "VAL_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_INVALID_REQ_TYPE);
            }
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            String balanceStr = (String) _responseMap.get("Balance");
            String creditLimit = (String) _responseMap.get("CreditLimit");
            if (balanceStr == null || balanceStr.equalsIgnoreCase("null"))
                balanceStr = creditLimit;
            try {
                double multFactorDouble = Double.parseDouble(multFactor.trim());
                balanceStr = InterfaceUtil.getSystemAmountFromINAmount(balanceStr, multFactorDouble);
                // _requestMap.put("INTERFACE_PREV_BALANCE",balanceStr);
                creditLimit = InterfaceUtil.getSystemAmountFromINAmount(creditLimit, multFactorDouble);
                _requestMap.put("INTERFACE_PREV_BALANCE", creditLimit);
                _requestMap.put("BILL_AMOUNT_BAL", balanceStr);
                _requestMap.put("CREDIT_LIMIT", creditLimit);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            _requestMap.put("SERVICE_CLASS", _responseMap.get("ServiceClass"));
            _requestMap.put("ACCOUNT_STATUS", _responseMap.get("AccountStatus"));
            setLanguageFromMapping();
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap:" + _requestMap);
        }
    }

    /**
     * This method would implements the logic to credit the post paid subscriber
     * account by following ways.
     * 1.Generating the CDR
     * 2.Online PIH file transfer using FTP.
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap:" + p_requestMap);
        TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "credit. Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        QueueTableHandler queueTableHandler = null;
        double multFactorDouble = 0;
        String multFactor = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            String validationReqType = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "CR_REQ_TYPE");
            if (InterfaceUtil.isNullString(validationReqType)) {
                _log.error("credit", _requestMap.get("REQ_SERVICE") + "_" + "CR_REQ_TYPE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "CR_REQ_TYPE  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            validationReqType = validationReqType.trim();
            if (PostPaidI.QUEUE_TABLE.equals(validationReqType)) {
                // Call the credit method of QTableHandler.
                queueTableHandler = new QueueTableHandler();
                queueTableHandler.credit(_requestMap);
                return;
            }
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("VAL_REQ_TYPE", validationReqType);
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");
            if (PostPaidI.ONLINE_DB_SP.equals(validationReqType)) {
                multFactor = FileCache.getValue(_interfaceID, "DB_MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multFactor)) {
                    _log.error("credit", "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[credit]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multFactor = multFactor.trim();
                multFactAmountsetIntoRequestMap(multFactor);
                // Call the sendRequestToIN method for credit the subscriber
                // account.
                sendRequestToIN(PostPaidI.ACTION_RECHARGE_CREDIT);
                String balanceStr = (String) _responseMap.get("Balance");
                try {
                    balanceStr = InterfaceUtil.getSystemAmountFromINAmount(balanceStr, multFactorDouble);
                    _requestMap.put("INTERFACE_POST_BALANCE", balanceStr);
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("credit", "While parsing the balance obtained from IN get Exception e:" + e.getMessage() + " set the POST_BALANCE_ENQ_SUCCESS as N into requestMap");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While parsing the balance obtained from IN get Exception e:" + e.getMessage() + " set the POST_BALANCE_ENQ_SUCCESS as N into requestMap");
                    // throw new
                    // BTSLBaseException(this,"credit",InterfaceErrorCodesI.ERROR_RESPONSE);
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
            } else if (PostPaidI.CDR_FILE_FTP.equals(validationReqType)) {
                TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "credit. Entered in CDR_FILE_FTP");
                multFactor = FileCache.getValue(_interfaceID, "PIH_MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multFactor)) {
                    _log.error("credit", "PIH_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[credit]", "", p_requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) p_requestMap.get("NETWORK_CODE"), "PIH_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multFactor = multFactor.trim();
                multFactAmountsetIntoRequestMap(multFactor);
                TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "credit. Just before calling method sendRequestViaFTP");
                // Call sendRequestViaFTP method for credit the subscriber
                // account.
                sendRequestViaFTP(PostPaidI.ACTION_RECHARGE_CREDIT);
                TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "credit. Just after calling method sendRequestViaFTP");
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            } else {
                _log.error("credit", _requestMap.get("REQ_SERVICE") + "_" + "CR_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "CR_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_INVALID_REQ_TYPE);
            }
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While credit the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap:" + _requestMap);
        }
    }

    /**
     * This method would implements the logic to Adjust the credit for the post
     * paid subscriber.
     * Credit Adjustment of Post paid subscriber would be done by following way.
     * 1. Generating the CDR
     * 2. Online PIH file transfer using FTP
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        double multFactorDouble = 0;
        String multFactor = null;
        QueueTableHandler queueTableHandler = null;
        try {
            _userType = (String) _requestMap.get("USER_TYPE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && "R".equals(_userType))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            String validationReqType = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "CRADJ_REQ_TYPE");
            if (InterfaceUtil.isNullString(validationReqType)) {
                _log.error("creditAdjust", _requestMap.get("REQ_SERVICE") + "_" + "CRADJ_REQ_TYPE  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "CRADJ_REQ_TYPE  is not defined in the INFile");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            validationReqType = validationReqType.trim();
            if (PostPaidI.QUEUE_TABLE.equals(validationReqType)) {
                // Call the credit method of QTableHandler.
                queueTableHandler = new QueueTableHandler();
                queueTableHandler.creditAdjust(_requestMap);
                return;
            }
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("VAL_REQ_TYPE", validationReqType);
            setInterfaceParameters();
            if ("R".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            else if ("S".equals(_userType))
                _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (PostPaidI.ONLINE_DB_SP.equals(validationReqType)) {
                multFactor = FileCache.getValue(_interfaceID, "DB_MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multFactor)) {
                    _log.error("creditAdjust", "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[creditAdjust]", "", p_requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) p_requestMap.get("NETWORK_CODE"), "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multFactor = multFactor.trim();
                multFactAmountsetIntoRequestMap(multFactor);
                // Call the sendRequestToIN method for credit the subscriber
                // account.
                sendRequestToIN(PostPaidI.ACTION_RECHARGE_CREDIT);
                String balanceStr = (String) _responseMap.get("Balance");
                try {
                    balanceStr = InterfaceUtil.getSystemAmountFromINAmount(balanceStr, multFactorDouble);
                    _requestMap.put("INTERFACE_POST_BALANCE", balanceStr);
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("creditAdjust", "While parsing the balance obtained from the IN get Exception e:" + e.getMessage() + " setting the POST_BALANCE_ENQ_SUCCESS as N");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "PostPaidINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                    // throw new
                    // BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.ERROR_RESPONSE);
                    _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
            } else if (PostPaidI.CDR_FILE_FTP.equals(validationReqType)) {
                multFactor = FileCache.getValue(_interfaceID, "PIH_MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multFactor)) {
                    _log.error("creditAdjust", "PIH_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[creditAdjust]", "", p_requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) p_requestMap.get("NETWORK_CODE"), "PIH_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multFactor = multFactor.trim();
                multFactAmountsetIntoRequestMap(multFactor);
                // Call sendRequestViaFTP method for credit the subscriber
                // account.
                sendRequestViaFTP(PostPaidI.ACTION_RECHARGE_CREDIT);
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            } else {
                _log.error("creditAdjust", _requestMap.get("REQ_SERVICE") + "_" + "CRADJ_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "CRADJ_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_REQ_TYPE);
            }
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[creditAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[creditAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While creditAdjust the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Exited _requestMap:" + _requestMap);
        }
    }

    /**
     * This method would implements the logic to debit the subscriber account
     * while using the POST 2 PRE services.
     * Debit Adjust of Post paid subscriber would be done by following way.
     * 1. Generating the CDR
     * 2. Online PIH file transfer using FTP
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        String multFactor = null;
        QueueTableHandler queueTableHandler = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            String validationReqType = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "DR_REQ_TYPE");
            if (InterfaceUtil.isNullString(validationReqType)) {
                _log.error("debitAdjust", _requestMap.get("REQ_SERVICE") + "_" + "DR_REQ_TYPE  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "DR_REQ_TYPE  is not defined in the INFile");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            validationReqType = validationReqType.trim();
            if (PostPaidI.QUEUE_TABLE.equals(validationReqType)) {
                queueTableHandler = new QueueTableHandler();
                queueTableHandler.debitAdjust(_requestMap);
                return;
            }
            _inTXNID = getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            setInterfaceParameters();
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (PostPaidI.ONLINE_DB_SP.equals(validationReqType)) {
                multFactor = FileCache.getValue(_interfaceID, "DB_MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multFactor)) {
                    _log.error("debitAdjust", "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[debitAdjust]", "", p_requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) p_requestMap.get("NETWORK_CODE"), "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multFactor = multFactor.trim();
                multFactAmountsetIntoRequestMap(multFactor);
                // Call the sendRequestToIN method for credit the subscriber
                // account.
                sendRequestToIN(PostPaidI.ACTION_IMMEDIATE_DEBIT);
                String balanceStr = (String) _responseMap.get("Balance");
                try {
                    double multFactorDouble = Double.parseDouble(multFactor);
                    balanceStr = InterfaceUtil.getSystemAmountFromINAmount(balanceStr, multFactorDouble);
                    _requestMap.put("INTERFACE_POST_BALANCE", balanceStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("debitAdjust", "Exception e:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:" + e.getMessage());
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_RESPONSE);
                }
            } else if (PostPaidI.CDR_FILE_FTP.equals(validationReqType)) {
                multFactor = FileCache.getValue(_interfaceID, "DB_MULTIPLICATION_FACTOR");
                if (InterfaceUtil.isNullString(multFactor)) {
                    _log.error("debitAdjust", "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[debitAdjust]", "", p_requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) p_requestMap.get("NETWORK_CODE"), "DB_MULTIPLICATION_FACTOR  is not defined in the INFile");
                    throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                multFactor = multFactor.trim();
                multFactAmountsetIntoRequestMap(multFactor);
                // Call sendRequestViaFTP method for credit the subscriber
                // account.
                sendRequestViaFTP(PostPaidI.ACTION_IMMEDIATE_DEBIT);
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            } else {
                _log.error("debitAdjust", _requestMap.get("REQ_SERVICE") + "_" + "DR_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), _requestMap.get("REQ_SERVICE") + "_" + "DR_REQ_TYPE defined in the INFile[" + validationReqType + "] is INVALID");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_REQ_TYPE);
            }
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust the subscriber Account,getting the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap:" + _requestMap);
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
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered  p_stage:" + p_stage);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_REQ, "Stored Proceedure is Called for stage:" + p_stage, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
        DBUtility dbUtility = null;
        Connection dbConnection = null;
        long startTime = 0;
        long endTime = 0, warnTime = 0;
        boolean dbConnClosed = false;
        InnerClassI innerClassI = null;
        try {
            // Fetch the DBUtility from DBPoolManager.
            dbUtility = (DBUtility) DBPoolManager._dbUtilityObjectMap.get(_interfaceID);
            if (dbUtility == null) {
                _log.error("sendRequestToIN", "dbUtility:" + dbUtility);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_OBJECT_POOL_INITIALIZATION);// Confirm
                                                                                                   // for
                                                                                                   // the
                                                                                                   // Error
                                                                                                   // Code.
            }
            // Confirmation for the Synchronization.
            dbConnection = dbUtility.getConnection();
            if (dbConnection == null) {
                _log.error("sendRequestToIN", "dbConnection=" + dbConnection);
                // Confirm for the Event handling.
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "DBConnection is NULL");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);// Confrim
                                                                                                                          // for
                                                                                                                          // the
                                                                                                                          // new
                                                                                                                          // key
            }
            // In creditAdjust (sender credit back )don't check interface
            // status, simply send the request to IN.
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                _isSameRequest = true;
                checkInterfaceB4SendingRequest();
            }
            startTime = System.currentTimeMillis();
            _log.error("sendRequestToIN", " startTime:" + startTime);
            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
            try {
                innerClassI = dbUtility.getInnerClass();
                switch (p_stage) {
                case PostPaidI.ACTION_ACCOUNT_INFO: {
                    innerClassI.validate(dbConnection, _requestMap);
                    break;
                }
                case PostPaidI.ACTION_RECHARGE_CREDIT: {
                    innerClassI.credit(dbConnection, _requestMap);
                    dbConnection.commit();
                    break;
                }
                case PostPaidI.ACTION_IMMEDIATE_DEBIT: {
                    innerClassI.debitAdjust(dbConnection, _requestMap);
                    dbConnection.commit();
                    break;
                }
                }
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "While calling the stored proc get Exception=" + e.getMessage());
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            } finally {
                try {
                    if (dbConnection != null)
                        dbConnection.close();
                    dbConnClosed = true;
                } catch (Exception ex) {
                }
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _log.error("sendRequestToIN", "endTime:" + endTime + " Total time taken by IN to process the request[for stage=" + p_stage + "]");
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            }
            warnTime = Long.parseLong((String) _requestMap.get("WARN_TIMEOUT"));
            if (endTime - startTime > warnTime) {
                _log.info("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
            }
            // Fetch the response
            String responseStr = (String) _requestMap.get("RESPONSE_STR");
            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
            if (InterfaceUtil.isNullString(responseStr)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "Response from the IN:" + responseStr);
                _log.error("sendRequestToIN", "Response from the IN is null");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // commented code may be used in future to support on line
                // cancel request
                /*
                 * if(PostPaidI.ACTION_TXN_CANCEL == p_stage)
                 * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                 * AMBIGOUS);
                 */
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            _responseMap = new HashMap();
            // Convert the responseString into hash map.
            InterfaceUtil.populateStringToHash(_responseMap, responseStr, "&", "=");
            String status = (String) _responseMap.get("Status");
            // Check for the success code, what its value would be?
            // Case1: What status code would be fetched other than success code?
            // Case2: What would be the status code incase of MSISDN is not
            // found?
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
             * if(PostPaidI.ACTION_TXN_CANCEL == p_stage)
             * {
             * _requestMap.put("CANCEL_RESP_STATUS",result);
             * cancelTxnStatus =
             * InterfaceUtil.getErrorCodeFromMapping(_requestMap
             * ,result,"SYSTEM_STATUS_MAPPING");
             * throw new BTSLBaseException(cancelTxnStatus);
             * }
             */

            if (!PostPaidI.SP_SUCCESS_OK.equals(status)) {
                if (PostPaidI.SUBSCRIBER_NOT_FOUND.equals(status)) {
                    _log.error("sendRequestToIN", "Error in response SUBSCRIBER_NOT_FOUND");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_stage, " SUBSCRIBER_NOT_FOUND AT IN");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);// MSISDN
                                                                                                                          // Not
                                                                                                                          // Found
                } else if (PostPaidI.NOT_POSTPAID_NO.equals(status))// Number is
                                                                    // not a
                                                                    // valid
                                                                    // Postpaid
                                                                    // number
                {
                    _log.error("sendRequestToIN", "Error in response NOT_POSTPAID_NO");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_stage, " NOT_POSTPAID_NO (Number is not a valid Postpaid number)AT IN");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INVALID_POSTPAID_NUMBER);// Number
                                                                                                                       // is
                                                                                                                       // not
                                                                                                                       // a
                                                                                                                       // valid
                                                                                                                       // Postpaid
                                                                                                                       // number
                }
                _log.error("sendRequestToIN", "Error in response with" + " status=" + status);
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // If not successfull throw exception
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e:" + e.getMessage());
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (Exception ex) {
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                if (!dbConnClosed && dbConnection != null)
                    dbConnection.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exited");
        }
    }

    /**
     * This method would implements the logic to send the CDR or PIH file to IN,
     * using FTP.
     * 
     * @param HashMap
     *            p_requestMap
     * @param String
     *            p_stage
     * @throws BTSLBaseException
     */
    private void sendRequestViaFTP(int p_stage) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestViaFTP", "Entered  p_stage:" + p_stage);
        // Transaction log.
        FtpClient ftpClient = null;
        boolean isC2S = false;
        Vector freeList = null;
        Vector busyList = null;
        String pihRecordString = null;
        FileOutputStream fo = null;
        boolean isFileLocallyStored = false;
        StringBuffer strBuff = null;
        long startTime = 0, endTime = 0;
        String dirSeprator = null;
        File fileLoc = null;
        long sleepTimeForNewConnection = 0;
        boolean movedToFailedLoc = false;
        boolean isFail = true;
        String fileName = "";
        int retryCount = 0;
        int retryCountConInvalid = 0;// Represent the Number of retries in case
                                     // of exception to get ftp connection
        long sleepTimeConInvalid = 10;
        try {
            String retryCountConInvalidStr = FileCache.getValue(_interfaceID, "RETRY_CON_INVAL");
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " Sleep time between retries,SLEEP_CON_INVAL=" + retryCountConInvalidStr);
            if (InterfaceUtil.isNullString(retryCountConInvalidStr) || !InterfaceUtil.isNumeric(retryCountConInvalidStr)) {
                _log.error("sendRequestToIN", "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestViaFTP]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "RETRY_CON_INVAL is either not defined in the INFile or it is not Numeric");
                throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            retryCountConInvalid = Integer.parseInt(retryCountConInvalidStr);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", " Number of retries, RETRY_CON_INVAL=" + retryCountConInvalid);

            // Generate the PIH Record String
            // dirSeprator=System.
            pihRecordString = generatePIHFileFormat();
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string:" + pihRecordString, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_stage);
            String localFileLoaction = (String) _requestMap.get("LOCAL_FILE");
            String destFileLocation = (String) _requestMap.get("DEST_FILE");
            // File Name would be based on the transaction id.
            // Agreed format of file name-PIH_YYMMDDHH24MISS_MSISDN.txt
            dirSeprator = File.separator;
            strBuff = new StringBuffer(1028);
            strBuff.append(_requestMap.get("PREFIX_FILE"));
            strBuff.append(_requestMap.get("IN_RECON_ID"));
            strBuff.append(_requestMap.get("SUFFIX_FILE"));
            fileName = strBuff.toString();
            String fullPihFileName = localFileLoaction + dirSeprator + fileName;
            try {
                TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just before creating FTP local file");
                fileLoc = new File(localFileLoaction);
                TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just after creating FTP local file");
                if (!fileLoc.exists()) {
                    TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "File loaction does not exist");
                    if (fileLoc.mkdirs()) {
                        _log.info("sendRequestViaFTP", "Location faileTxnDir:" + fileLoc + " is created successfully");
                        fo = new FileOutputStream(new File(fullPihFileName));
                        fo.write(pihRecordString.getBytes());
                        isFileLocallyStored = true;
                        _log.error("sendRequestViaFTP", "Location :" + fileLoc + " does not exist hence created successfully");
                    } else {
                        _log.info("sendRequestViaFTP", "Location faileTxnDir:" + fileLoc + " does not exist hence creating the directory:" + fileLoc);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[sendRequestViaFTP]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "PIH File could not be stored at location :" + fileLoc);
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    }
                } else {
                    TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just before creating FTP local file");
                    fo = new FileOutputStream(new File(fullPihFileName));
                    fo.write(pihRecordString.getBytes());
                    isFileLocallyStored = true;
                    TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just after creating FTP local file. [" + localFileLoaction + "] and file name is[" + fileName + "]");
                    _log.error("sendRequestViaFTP", "Record is stored in local file successfully at location [" + localFileLoaction + "] and file name is[" + fileName + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestViaFTP", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestViaFTP]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "While writing the PIH record Locally on location [" + localFileLoaction + "]get Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);// Confirm
                                                                                              // new
                                                                                              // Error
                                                                                              // code.
            } finally {
                try {
                    fo.close();
                    fo = null;
                    fileLoc = null;
                } catch (Exception e) {
                }
                if (!isFileLocallyStored)
                    _log.error("sendRequestViaFTP", "File is not creadted successfully");
            }
            // Get new object from the PoolManager (on the basis of module)
            // Making the reference of busy and freeList
            try {
                TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just before getting FTP cleInt object.");
                freeList = (Vector) FTPPoolManager._freeBucket.get(_interfaceID);
                busyList = (Vector) FTPPoolManager._busyBucket.get(_interfaceID);
                if (PostPaidI.C2S_MODULE.equals((String) _requestMap.get("MODULE")))
                    isC2S = true;
                ftpClient = FTPPoolManager.getClientObject(_interfaceID, isC2S);
                TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just AFTER getting FTP cleInt object.");
            } catch (BTSLBaseException be) {
                throw be;
            }

            while (retryCount++ <= retryCountConInvalid) {
                try {

                    // Upload the local file to the FTP server.
                    // boolean isUploaded=false;
                    startTime = System.currentTimeMillis();
                    _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                    try {
                        TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just BEFORE changing remote directory. retry count: " + retryCount + " , max attempts: " + retryCountConInvalid);
                        ftpClient.changeDirectoryTo(destFileLocation);
                        TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just after changing remote directory. and uploading file on server");
                        ftpClient.uploadFileToServer(localFileLoaction + dirSeprator + fileName, fileName);
                        TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just after uploading file on remote server directory.");
                        endTime = System.currentTimeMillis();
                        try {
                            // isFileUploaded(ftpClient,fileName);
                            TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just BEFORE changing mode of file.");
                            try {
                                ftpClient.chmod((String) _requestMap.get("FTP_PIH_FILE_PERMISSION"), fileName);
                            } catch (Exception e) {
                            }
                            TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just after changing mode of file. and b4 change directory");
                            ftpClient.changeDirectoryTo(ftpClient.getFirstDir());
                            TransactionLog.log("", _referenceID, _msisdn, "", String.valueOf(p_stage), "", "", "", "sendRequestViaFTP credit Just after changing directory.");
                            /*
                             * if(!isUploaded)
                             * {
                             * moveFailedTxnFile(pihRecordString,fileName);
                             * movedToFailedLoc=true;
                             * }
                             */} catch (BTSLBaseException beEx) {
                            ftpClient.changeDirectoryTo(ftpClient.getFirstDir());
                            moveFailedTxnFile(pihRecordString, fileName);
                            movedToFailedLoc = true;
                            throw beEx;
                        }
                    } catch (FTPException ftpEx) {
                        ftpEx.printStackTrace();
                        _log.error("sendRequestViaFTP", "While uploading the file to Server get Exception ftpEx:" + ftpEx.getMessage());
                        if (retryCount > retryCountConInvalid) {
                            if (isFileLocallyStored && !movedToFailedLoc) {
                                moveFailedTxnFile(pihRecordString, fileName);
                                movedToFailedLoc = true;
                            }
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_FILE_UPLOAD);
                        }
                        try {
                            try {
                                sleepTimeForNewConnection = Long.parseLong(FileCache.getValue(_interfaceID, "SLEEP_FTP_EXP"));
                            } catch (Exception e) {
                                sleepTimeForNewConnection = 500;
                            }
                            Thread.sleep(sleepTimeForNewConnection);
                            FtpClient tempFtpClient = FTPPoolManager.getNewClientObject(_interfaceID);
                            busyList.remove(ftpClient);
                            ftpClient = tempFtpClient;
                            busyList.add(ftpClient);
                            _log.info("sendRequestViaFTP", "New connection:[" + tempFtpClient + "] has been created successfully and replaces the older one in the pool");
                        } catch (BTSLBaseException beEx) {
                            throw beEx;
                        }
                        continue;
                    } catch (SocketException socketEx) {
                        socketEx.printStackTrace();
                        _log.error("sendRequestViaFTP", "While uploading the file to Server get Exception socketEx:" + socketEx.getMessage());
                        try {
                            if (retryCount > retryCountConInvalid) {
                                if (isFileLocallyStored && !movedToFailedLoc) {
                                    moveFailedTxnFile(pihRecordString, fileName);
                                    movedToFailedLoc = true;
                                }
                                // Destroying the older FtpClient and creating
                                // new Ftp connection.
                                _log.error("sendRequestViaFTP", "While uploading the file to server get SocketException socketEx:" + socketEx.getMessage() + " after this creating a new connection and replacing it with older one");
                                if (isFileLocallyStored) {
                                    moveFailedTxnFile(pihRecordString, fileName);
                                    movedToFailedLoc = true;
                                }
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_FILE_UPLOAD);
                            }

                            try {
                                sleepTimeForNewConnection = Long.parseLong(FileCache.getValue(_interfaceID, "SLEEP_FTP_EXP"));
                            } catch (Exception e) {
                                sleepTimeForNewConnection = 180000;
                            }
                            Thread.sleep(sleepTimeForNewConnection);
                            FtpClient tempFtpClient = FTPPoolManager.getNewClientObject(_interfaceID);
                            busyList.remove(ftpClient);
                            ftpClient = tempFtpClient;
                            busyList.add(ftpClient);
                            _log.info("sendRequestViaFTP", "New connection:[" + tempFtpClient + "] has been created successfully and replaces the older one in the pool");
                        } catch (BTSLBaseException be) {
                            throw be;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            _log.error("sendRequestViaFTP", "Exception while creating new connection ex:" + ex.getMessage());
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_FILE_UPLOAD);
                        }
                        continue;
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (retryCount > retryCountConInvalid) {
                            _log.error("sendRequestViaFTP", "While uploading the file to server get Exception e:" + e.getMessage());
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestViaFTP]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "While Uploading the PIH File to the FTP server, get Exception=" + e.getMessage());
                            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "While uploading the file to server get Exception", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage + "Current retry count is " + retryCount + ". Now retry count excedeed so throwing error. Free list size():" + freeList.size() + ", busyList size: " + busyList.size() + " step1");
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                        try {
                            sleepTimeForNewConnection = Long.parseLong(FileCache.getValue(_interfaceID, "SLEEP_FTP_EXP"));
                        } catch (Exception se) {
                            sleepTimeForNewConnection = 180000;
                        }
                        Thread.sleep(sleepTimeForNewConnection);
                        FtpClient tempFtpClient = FTPPoolManager.getNewClientObject(_interfaceID);
                        busyList.remove(ftpClient);
                        ftpClient = tempFtpClient;
                        busyList.add(ftpClient);
                        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "New connection has been created successfully and replaces the older one in the pool", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage + "Current retry count is " + retryCount + ". Now retry will be tried with new add connection. Free list size():" + freeList.size() + ", busyList size: " + busyList.size() + " step2");
                        _log.info("sendRequestViaFTP", "New connection:[" + tempFtpClient + "] has been created successfully and replaces the older one in the pool");
                        continue;
                    }
                    TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "Response string: File is uploaded successfully on FTP server", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage);
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    // Confirm the reattempt for uploading the file on FTPServer
                    // in case of ERROR.
                    e.printStackTrace();
                    if (retryCount > retryCountConInvalid) {
                        _log.error("sendRequestViaFTP", "While uploading the file to server get Exception e:" + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestViaFTP]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "While Uploading the PIH File to the FTP server, get Exception=" + e.getMessage());
                        // If any error occurs while uploading the file, one
                        // copy would be stored into location defined in the
                        // INFile under key FAILED_TXN_FILE_LOC
                        if (isFileLocallyStored) {
                            moveFailedTxnFile(pihRecordString, fileName);
                            movedToFailedLoc = true;
                        }
                        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "While uploading the file to server get Exception", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage + "Current retry count is " + retryCount + ". Now retry count excedeed so throwing error. Free list size():" + freeList.size() + ", busyList size: " + busyList.size() + " step3");
                        throw new BTSLBaseException(this, "sendRequestViaFTP", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

                    }
                    try {
                        sleepTimeForNewConnection = Long.parseLong(FileCache.getValue(_interfaceID, "SLEEP_FTP_EXP"));
                    } catch (Exception se) {
                        sleepTimeForNewConnection = 180000;
                    }
                    Thread.sleep(sleepTimeForNewConnection);
                    FtpClient tempFtpClient = FTPPoolManager.getNewClientObject(_interfaceID);
                    busyList.remove(ftpClient);
                    ftpClient = tempFtpClient;
                    busyList.add(ftpClient);
                    _log.info("sendRequestViaFTP", "New connection:[" + tempFtpClient + "] has been created successfully and replaces the older one in the pool");
                    TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "New connection has been created successfully and replaces the older one in the pool", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage + "Current retry count is " + retryCount + ". Now retry will be tried with new add connection. Free list size():" + freeList.size() + ", busyList size: " + busyList.size() + " step4");
                    continue;
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                }
                // What the status code would be set for the INTERFACE_STATUS in
                // the request map.
                isFail = false;
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_stage), PretupsI.TXN_LOG_REQTYPE_RES, "File upload ended successfully", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_stage + "retry count is " + retryCount + ". Free list size():" + freeList.size() + ", busyList size: " + busyList.size() + " step5");
                break;
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestViaFTP", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[sendRequestViaFTP]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_stage, "While generating the PIH File record formate get Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "sendRequestViaFTP", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (freeList != null && busyList != null) {
                busyList.remove(ftpClient);
                freeList.add(ftpClient);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " In last finally freeList.size():" + freeList.size() + " busyList.size():" + busyList.size());
            }
            if (isFileLocallyStored && !movedToFailedLoc && isFail)
                moveFailedTxnFile(pihRecordString, fileName);
            if (_log.isDebugEnabled())
                _log.debug("sendRequestViaFTP", "Exited isFileLocallyStored=" + isFileLocallyStored + " movedToFailedLoc=" + movedToFailedLoc + " isFail=" + isFail + " fileName=" + fileName);
        }
    }

    /**
     * Implements the logic to generate the PIH Record fromat.
     * 
     * @return String
     * @throws BTSLBaseException
     */
    private String generatePIHFileFormat() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generatePIHFileFormat", "Entered");
        String pihFileFormat = null;
        String[] recordKeys = null;
        // String pihFileString=null;
        // StringBuffer strBuffer=null;
        String endLineChar = "\r\n";
        try {
            pihFileFormat = (String) _requestMap.get("PIH_FILE_FORMAT");
            if (_log.isDebugEnabled())
                _log.debug("generatePIHFileFormat", "pihFileFormat:" + pihFileFormat);
            recordKeys = pihFileFormat.split((String) _requestMap.get("RECORD_SEP"));
            if (InterfaceUtil.isNullArray(recordKeys)) {
                _log.error("generatePIHFileFormat", "PIH_FILE_FORMAT defined in the INFile contains no keys, pihFileFormat: " + pihFileFormat);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[generatePIHFileFormat]", "", _msisdn, (String) _requestMap.get("NETWORK_CODE"), "PIH_FILE_FORMAT defined in the INFile contains no keys");
                throw new BTSLBaseException(this, "generatePIHFileFormat", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION); // Confirm
                                                                                                                              // for
                                                                                                                              // the
                                                                                                                              // Error
                                                                                                                              // code
                                                                                                                              // key.
            }
            // strBuffer=new StringBuffer(1028);
            for (int i = 0, size = recordKeys.length; i < size; i++) {
                // if(InterfaceUtil.isNullString((String)_requestMap.get(recordKeys[i])))
                if (InterfaceUtil.isNullString((String) _requestMap.get(recordKeys[i])) || recordKeys[i].contains("$")) {
                    pihFileFormat = pihFileFormat.replace(recordKeys[i], FileCache.getValue(_interfaceID, recordKeys[i]) != null ? FileCache.getValue(_interfaceID, recordKeys[i]).trim() : "");
                    // strBuffer.append(FileCache.getValue(_interfaceID,recordKeys[i])!=null?FileCache.getValue(_interfaceID,recordKeys[i]).trim():"");
                    // strBuffer.append(_requestMap.get("RECORD_SEP"));
                } else {
                    pihFileFormat = pihFileFormat.replace(recordKeys[i], (String) _requestMap.get(recordKeys[i]));
                    // strBuffer.append(_requestMap.get(recordKeys[i]));
                    // strBuffer.append(_requestMap.get("RECORD_SEP"));
                }
            }
            pihFileFormat = pihFileFormat + endLineChar;
            if (_log.isDebugEnabled())
                _log.debug("generatePIHFileFormat", "pihFileFormat::" + pihFileFormat);
            // pihFileString=strBuffer.toString();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("generatePIHFileFormat", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[generatePIHFileFormat]", "", _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While generating the PIH record format get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "generatePIHFileFormat", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generatePIHFileFormat", "Exited pihFileFormat:" + pihFileFormat);
        }
        return pihFileFormat;
    }

    /**
     * This method is used to check whether the file has been uploaded
     * successfully to the server or not.
     * 
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isFileUploaded(FtpClient ftpClient, String fileName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isFileUploaded", "Entered ftpClient" + ftpClient + " fileName:" + fileName);
        TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "isFileUploaded credit Entered isFileUploaded");
        boolean isUploaded = false;
        String[] fileList = null;
        try {
            fileList = ftpClient.getDir(".");
            for (int i = 0, size = fileList.length; i < size; i++) {
                if (fileList[i].equals(fileName)) {
                    TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "**********isFileUploaded credit iterations at remote server " + i);
                    isUploaded = true;
                    break;
                }
            }
            TransactionLog.log("", _referenceID, _msisdn, "", "", "", "", "", "isFileUploaded credit After chaeking isFileUploaded");
            if (!isUploaded)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_FILE_UPLOAD);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("isFileUploaded", " While cheking the file whether uploaded successfully or not,Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("isFileUploaded", "Exited [if true then success] isUploaded:" + isUploaded);
        }
        return isUploaded;
    }

    /**
     * This method is used to stored the failed transaction file into the
     * specific location provided by the system.
     * 
     * @param String
     *            p_pihRecordString
     * @param String
     *            p_fileName
     */
    private void moveFailedTxnFile(String p_pihRecordString, String p_fileName) {
        FileOutputStream foFailedTxn = null;
        String dirSeprator = File.separator;
        try {
            String faileTxnFileLocation = (String) _requestMap.get("FAILED_TXN_FILE_LOC");
            File faileTxnDir = new File(faileTxnFileLocation);
            if (!faileTxnDir.exists()) {
                if (faileTxnDir.mkdirs()) {
                    _log.info("moveFailedTxnFile", "Location faileTxnDir:" + faileTxnDir + " is created successfully");
                    foFailedTxn = new FileOutputStream(new File(faileTxnFileLocation + "/" + p_fileName));
                    foFailedTxn.write(p_pihRecordString.getBytes());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[moveFailedTxnFile]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "PIH File is successfully stored at location :" + faileTxnFileLocation);
                } else {
                    _log.info("moveFailedTxnFile", "Location faileTxnDir:" + faileTxnDir + " does not exist hence creating the directory:" + faileTxnDir);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[moveFailedTxnFile]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "PIH File could not be stored at location :" + faileTxnFileLocation);
                }
            } else {
                foFailedTxn = new FileOutputStream(new File(faileTxnFileLocation + dirSeprator + p_fileName));
                foFailedTxn.write(p_pihRecordString.getBytes());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[moveFailedTxnFile]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "PIH File is successfully stored at location :" + faileTxnFileLocation);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            _log.error("moveFailedTxnFile", "While writing the a copy of file to location" + "" + "Exception ex:" + ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[moveFailedTxnFile]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "While storing the file in case of Failed trnasaction get Exception ex:" + ex.getMessage());
        } finally {
            try {
                foFailedTxn.close();
                foFailedTxn = null;
            } catch (Exception exp) {
            }
        }
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
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            // Currency is common for each type of communication mod.

            String filteredString = InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"), (String) _requestMap.get("MSISDN"));
            _requestMap.put("FILT_MSISDN", filteredString);
            String currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                _log.error("setInterfaceParameters", "CURRENCY is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("CURRENCY", currency.trim());
            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "currency:" + currency);
            if (PostPaidI.CDR_FILE_FTP.equals(FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "CR_REQ_TYPE")) || PostPaidI.CDR_FILE_FTP.equals(FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "DR_REQ_TYPE"))) {
                String pihFileFormat = FileCache.getValue(_interfaceID, "PIH_FILE_FORMAT");
                if (InterfaceUtil.isNullString(pihFileFormat)) {
                    _log.error("setInterfaceParameters", "PIH_FILE_FORMAT  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PIH_FILE_FORMAT  is not defined in the INFile");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("PIH_FILE_FORMAT", pihFileFormat.trim());
                String dateFormat = FileCache.getValue(_interfaceID, "TXN_DATE_FORMAT");
                if (InterfaceUtil.isNullString(dateFormat)) {
                    _log.error("setInterfaceParameters", "TXN_DATE_FORMAT  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "TXN_DATE_FORMAT  is not defined in the INFile");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                // TXN_DATE_FORMAT set into requestMap so that it could be refer
                // if required.
                _requestMap.put("TXN_DATE_FORMAT", dateFormat.trim());
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                sdf.setLenient(false);
                String formatStr = sdf.format(new Date());
                _requestMap.put("PAYMENT_REF2", formatStr);
                _requestMap.put("PAYMENT_DATE", formatStr);
                String prefixForFileName = FileCache.getValue(_interfaceID, "PREFIX_FILE");
                if (InterfaceUtil.isNullString(prefixForFileName)) {
                    _log.error("setInterfaceParameters", "PREFIX_FILE  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PREFIX_FILE  is not defined in the INFile");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("PREFIX_FILE", prefixForFileName.trim());
                String suffixForFileName = FileCache.getValue(_interfaceID, "SUFFIX_FILE");
                if (InterfaceUtil.isNullString(suffixForFileName)) {
                    _log.error("setInterfaceParameters", "SUFFIX_FILE  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "SUFFIX_FILE  is not defined in the INFile");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("SUFFIX_FILE", suffixForFileName.trim());
                String localFileLocation = FileCache.getValue(_interfaceID, "LOCAL_FILE");
                if (InterfaceUtil.isNullString(localFileLocation)) {
                    _log.error("setInterfaceParameters", "LOCAL_FILE  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LOCAL_FILE  is not defined in the INFile");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("LOCAL_FILE", localFileLocation.trim());
                String destFileLocation = FileCache.getValue(_interfaceID, "DEST_FILE");
                if (InterfaceUtil.isNullString(destFileLocation)) {
                    _log.error("setInterfaceParameters", "DEST_FILE  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "DEST_FILE  is not defined in the INFile");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("DEST_FILE", destFileLocation.trim());
                // FAILED_TXN_FILE_LOC

                String failedTxnFileLocation = FileCache.getValue(_interfaceID, "FAILED_TXN_FILE_LOC");
                if (InterfaceUtil.isNullString(failedTxnFileLocation)) {
                    _log.error("setInterfaceParameters", "FAILED_TXN_FILE_LOC  is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "FAILED_TXN_FILE_LOC  is not defined in the INFile");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("FAILED_TXN_FILE_LOC", failedTxnFileLocation.trim());
                String recordSep = FileCache.getValue(_interfaceID, "RECORD_SEP");
                if (InterfaceUtil.isNullString(recordSep))
                    recordSep = ";";
                _requestMap.put("RECORD_SEP", recordSep.trim());
                if (_log.isDebugEnabled())
                    _log.debug("setInterfaceParameters", "prefixForFileName:" + prefixForFileName + ", suffixForFileName:" + suffixForFileName + ", localFileLocation:" + localFileLocation + ", destFileLocation:" + destFileLocation + ", recordSep:" + recordSep);

                // FTP_PIH_FILE_PERMISSION
                String pihFilePermission = FileCache.getValue(_interfaceID, "FTP_PIH_FILE_PERMISSION");
                if (InterfaceUtil.isNullString(pihFilePermission) || pihFilePermission.length() != 3) {
                    _log.error("setInterfaceParameters", "FTP_PIH_FILE_PERMISSION  is not defined in the INFile or it is not correct");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "PostPaidINHandler[setInterfaceParameters]", "", _requestMap.get("MSISDN") + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "FTP_PIH_FILE_PERMISSION  is not defined in the INFile or it is not correct");
                    pihFilePermission = "555";
                }
                _requestMap.put("FTP_PIH_FILE_PERMISSION", pihFilePermission.trim());
            }
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "While setting the Interface parameters get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exiting");
        }
    }// end of setInterfaceParameters

    /**
     * This method would be used to map language provided in IN File.
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
        String mappingString = null;
        try {
            // Get the mapping string from the FileCache and storing all the
            // mappings into array which are separated by ','.
            mappingString = FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
            if (InterfaceUtil.isNullString(mappingString))
                mappingString = "";
            langFromIN = (String) _responseMap.get("LanguageId");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString = " + mappingString + " langFromIN = " + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0, length = mappingArr.length; in < length; in++) {
                tempArr = mappingArr[in].split(":");
                if (tempArr[0].equals(langFromIN)) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[setLanguageFromMapping]", "", _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostPaidINHandler[setLanguageFromMapping]", "", _msisdn, (String) _requestMap.get("NETWORK_CODE"), "while setting the language mapping get Exception=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping
    }// end of setLanguageFromMapping

    /**
     * This method is used to validate the multiplication factor for each mod
     * type(Online DB, FTP for PIH etc)
     * 
     * @param p_multiplicationFactor
     * @throws BTSLBaseException
     */
    private void multFactAmountsetIntoRequestMap(String p_multiplicationFactor) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("multFactAmountsetIntoRequestMap", "Entered p_multiplicationFactor:" + p_multiplicationFactor);
        String amountStr = null;
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String multFactor = p_multiplicationFactor;
        try {
            multFactorDouble = Double.parseDouble(multFactor);
            double interfaceAmountDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmountDouble, multFactorDouble);
            amountStr = String.valueOf(systemAmtDouble);
            // Based on the INFiles ROUND_FLAG flag, we have to decide to round
            // the transfer amount or not.
            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("multFactAmountsetIntoRequestMap", "From file cache roundFlag = " + roundFlag);
            // If rounding of amount is allowed, round the amount value and put
            // this value in request map.
            if ("Y".equals(roundFlag.trim())) {
                amountStr = String.valueOf(Math.round(systemAmtDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            if (_log.isDebugEnabled())
                _log.debug("multFactAmountsetIntoRequestMap", "transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount", amountStr);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostPaidINHandler[multFactAmountsetIntoRequestMap]", "MSISDN = " + (String) _requestMap.get("MSIDN"), "INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not valid");
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);// Confirm
                                                                                                          // for
                                                                                                          // new
                                                                                                          // ErrorCode
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("multFactAmountsetIntoRequestMap", "Exited");
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
             * _formatter.generateRequest(PostPaidI.ACTION_TXN_CANCEL,
             * _requestMap);
             * _maxRetryCount=cancelRetryCount;
             * sendRequestToIN(inStr,PostPaidI.ACTION_TXN_CANCEL);
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
