/**
 * @(#) IATINHandler.java
 *      Copyright(c) 2011, Comviva Technologies Ltd.
 *      All Rights Reserved
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Created By Created On History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Babu Kunwar 02-DEC-2011 Initial Creation
 *      ------------------------------------------------------------------------
 *      -------------------------
 */

package com.inter.iat;

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
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.IATInterfaceHandlerI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.logging.IATInterfaceLog;
import com.inter.iat.client.AuthHeader;
import com.inter.iat.client.AuthHeaderParam;
import com.inter.iat.client.CheckStatusParam;
import com.inter.iat.client.CheckStatusResult;
import com.inter.iat.client.IATONIStub;
import com.inter.iat.client.RechargeParam;
import com.inter.iat.client.RechargeResult;

public class IATINHandler implements IATInterfaceHandlerI {
    private static Log _log = LogFactory.getLog("IATINHandler".getClass().getName());
    private String _msisdn = null;
    private String _senderZebraTXNID = null;
    private String _interfaceID = null;
    private String _networkCode = null;
    private String _iatTAXID = null;
    private String _ipLocalHostName = null;
    private String _serviceTypeMapping = null;
    private String _failedAtMapping = null;
    private String _rechargeTypeMapping = null;
    private int _chkStatusCount = 0;
    private long _reqSleepTime = 0;
    private String _multiFact = null;
    private String _warnTimeStr = null;
    private String _isHttpsEnable = null;
    private String _sendingNWType = null;// Operator, banks ATM.
    private String _deviceID = null;
    private String _userName = null;
    private String _password = null;
    private boolean _isRequestParamSet = false;
    private String _sNwId = null; // Added by deepika aggarwal
    private HashMap<String, String> _requestMap = null;

    /**
     * validate Method is used for getting the account information.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(IATInterfaceVO p_IATReqResVO) {
        if (_log.isDebugEnabled())
            _log.debug("validate :: ", "Entered with p_IATReqResVO :: " + p_IATReqResVO.toString());

        p_IATReqResVO.setIatResponseCodeVal(IATI.VALIDATION_RESULT);
        p_IATReqResVO.setIatINTransactionStatus(IATI.VALIDATION_RESULT);

        if (_log.isDebugEnabled())
            _log.debug("validate :: ", "Exiting with p_IATReqResVO :: " + p_IATReqResVO.toString());

    }

    /**
     * This method is responsible for the credit of subscriber account.
     * Interface specific parameters are set and added to the request.
     * For sending request to IN this method internally calls private method
     * sendRequestToIN
     * Process the response.
     * 
     * @throws BTSLBaseException
     *             ,Exception
     */

    public void credit(IATInterfaceVO p_iatReqResVO) // throws
                                                     // BTSLBaseException,Exception
    {
        if (_log.isDebugEnabled())
            _log.debug("credit ::", "Entered p_IATReqResVO =" + p_iatReqResVO.toString());

        try {
            _interfaceID = (String) p_iatReqResVO.getIatInterfaceId();
            _senderZebraTXNID = (String) p_iatReqResVO.getIatSenderNWTRXID();// Transaction
                                                                             // ID
            _msisdn = p_iatReqResVO.getIatReceiverMSISDN();
            _networkCode = p_iatReqResVO.getIatSenderNWID();
            // Set the interface parameters to the p_iatReqResVO
            setInterfaceParameters(p_iatReqResVO);

            // Changed to handle multiplication factor as double
            long requestedAmtDouble = p_iatReqResVO.getIatInterfaceAmt();
            p_iatReqResVO.setIatAmountSentToIAT(IATUtil.getINAmountFromSystemAmountToIN(requestedAmtDouble, Double.parseDouble(_multiFact)));

            try {
                // send the credit request to IN
                sendRequestToIN(p_iatReqResVO, IATI.ACTION_CREDIT, IATI.REQUEST_SOURCE_INMODULE);
            } catch (Exception e) {
                if (_log.isDebugEnabled())
                    _log.debug("credit ::", "While sending Request To IN got Exception e::" + e);
                p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS);
            }
        } catch (BTSLBaseException be) {
            if (_log.isDebugEnabled())
                _log.debug("credit ::", "Before sending Request To IN got BTSLBaseException be::" + be);
            p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
        } catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.debug("credit ::", "Before sending Request To IN got Exception e::" + e);
            p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
        } finally {
            try {
                if (IATI.CREDIT_RESPONSE_CODE.equalsIgnoreCase(p_iatReqResVO.getIatResponseCodeCredit()) || IATI.IAT_INTERNAL_SERVER_ERROR_CODE.equalsIgnoreCase(p_iatReqResVO.getIatResponseCodeCredit()) || IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS.equalsIgnoreCase(p_iatReqResVO.getIatINTransactionStatus())) {
                    checkTxnStatus(p_iatReqResVO, IATI.REQUEST_SOURCE_INMODULE, _chkStatusCount, _reqSleepTime);
                }
            } catch (Exception e) {
                if (_log.isDebugEnabled())
                    _log.debug("credit ::", "while doing Check status got exception e::" + e);
                p_iatReqResVO.setIatResponseCodeCredit(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
            }
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited");
        }
    }

    public void checkTxnStatus(IATInterfaceVO p_iatReqResVO, String reqSource, int retryCount, long sleepTime) {
        if (_log.isDebugEnabled())
            _log.debug("checkTxnStatus", "Entered with p_iatReqResVO =" + p_iatReqResVO + "reqSource =" + reqSource + "retryCount =" + retryCount + "sleepTime =" + sleepTime);

        try {
            /*
             * Set the interface parameters if request is from web or from
             * process,
             * because from IN module we have already set this parameters in
             * credit.
             */
            if (!IATI.REQUEST_SOURCE_INMODULE.equalsIgnoreCase(reqSource)) {
                _interfaceID = (String) p_iatReqResVO.getIatInterfaceId();
                _senderZebraTXNID = (String) p_iatReqResVO.getIatSenderNWTRXID();// Transaction
                                                                                 // ID
                _msisdn = p_iatReqResVO.getIatReceiverMSISDN();
                _networkCode = p_iatReqResVO.getIatSenderNWID();
                _iatTAXID = p_iatReqResVO.getIatTRXID();

                // Set the interface parameters to p_iatReqResVO
                setInterfaceParameters(p_iatReqResVO);
            }
            _chkStatusCount = retryCount;
            _reqSleepTime = sleepTime;
            sendRequestToIN(p_iatReqResVO, IATI.ACTION_CHECK_STATUS, reqSource);
        } catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.debug("checkTxnStatus", "Exception while doing check Status::" + e);
            e.printStackTrace();
            p_iatReqResVO.setIatResponseCodeCredit(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
            p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkTxnStatus", "Exiting with p_iatReqResVO =" + p_iatReqResVO + "_iatTXNID=" + _iatTAXID);
        }
    }

    /**
     * This method is responsible to send the request to IN.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws Exception
     */
    public void sendRequestToIN(IATInterfaceVO p_iatReqResVO, String p_action, String p_reqSource) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_iatReqResVO + " p_action:" + p_action);

        int serviceType;
        int rechargeType;
        long startTime = 0;
        long endTime = 0;
        String failedAt = null;
        long warnTime = 0;
        // IATHUBG2R0_pretupsServiceSoapBindingStub iatHUB_pretups_Stub;
        IATONIStub iatServiceRequest = null;
        RechargeParam rechargeParam;
        RechargeResult rechargeResult = null;
        CheckStatusParam checkStatusParam = null;
        CheckStatusResult checkStatusResult = null;

        try {
            // iatHUB_pretups_Stub=(IATHUBG2R0_pretupsServiceSoapBindingStub)new
            // IATHUBG2R0_pretupsServiceLocator().getIATHUB_pretupsSoapPort();
            // iatHUB_pretups_Stub=(IAT_HUB_STUB_ONI)new
            // IATHUB_pretupsServiceClient().getIATHUB_pretupsSoapPort();
            if ("Y".equalsIgnoreCase(_isHttpsEnable))
                enablehttps();
            // iatHUB_pretups_Stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,_ipLocalHostName);
            /**
             * Standard property: This boolean property is used by a service
             * client to indicate whether or not
             * it wants to participate in a session with a service endpoint. If
             * this property is set to
             * true, the service client indicates that it wants the session to
             * be maintained.
             * If set to false, the session is not maintained. The default value
             * for this property is false.
             */
            // iatHUB_pretups_Stub._setProperty(javax.xml.rpc.Stub.SESSION_MAINTAIN_PROPERTY,true);
            if ("CREDIT".equalsIgnoreCase(p_action)) {
                IATInterfaceLog.log("Before sending " + p_action + " Request from " + p_reqSource, _senderZebraTXNID, "", _msisdn, _networkCode, p_iatReqResVO.getIatServiceType(), PretupsI.TXN_LOG_REQTYPE_REQ, "[INTERFACE_ID:" + _interfaceID + "] [Request string:" + p_iatReqResVO.toString() + "] ", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");
                try {
                    rechargeParam = new RechargeParam();
                    AuthHeader authHeader = new AuthHeader();
                    AuthHeaderParam authHeaderParam = new AuthHeaderParam();
                    authHeaderParam.setUserName(_userName);
                    authHeaderParam.setPassword(_password);
                    authHeader.setAuthHeaderParam(authHeaderParam);
                    try {
                        serviceType = Integer.parseInt(getMappingFromString(p_iatReqResVO.getIatServiceType(), _serviceTypeMapping));
                        rechargeType = Integer.parseInt(getMappingFromString(p_iatReqResVO.getIatModule(), _rechargeTypeMapping));
                        p_iatReqResVO.setIatSenderNWTYPE(_sendingNWType);
                        p_iatReqResVO.setIatDeviceID(_deviceID);

                        warnTime = Long.parseLong(_warnTimeStr);

                        rechargeParam.setServiceType(serviceType); // RoamingRecharge/International
                                                                   // recharge M
                        rechargeParam.setSNwTrxId(p_iatReqResVO.getIatSenderNWTRXID()); // Sending
                                                                                        // Network
                                                                                        // Transaction
                                                                                        // identifier
                                                                                        // M

                        /* Added by Deepika Aggarwal */
                        _sNwId = FileCache.getValue(_interfaceID, p_iatReqResVO.getIatSenderNWID() + "_NEW");
                        if (IATUtil.isNullString(_sNwId)) {
                            rechargeParam.setSNwId(p_iatReqResVO.getIatSenderNWID()); // Sending
                                                                                      // Network
                                                                                      // (operator
                                                                                      // or
                                                                                      // distribution
                                                                                      // network)
                                                                                      // identifier

                        } else {
                            rechargeParam.setSNwId(_sNwId); // Sending Network
                                                            // Identifier
                        }
                        /* End Added by Deepika Aggarwal */

                        // rechargeParam.setSNwId(p_iatReqResVO.getIatSenderNWID());
                        // //Sending Network (operator or distribution network)
                        // identifier
                        rechargeParam.setSNwType(p_iatReqResVO.getIatSenderNWTYPE()); // Sending
                                                                                      // Network
                                                                                      // Type
                                                                                      // (operator,
                                                                                      // bank’s
                                                                                      // ATM
                                                                                      // network,
                                                                                      // …),
                                                                                      // i.e.
                                                                                      // « PreTUPSRP2P
                        rechargeParam.setSCountryCode(p_iatReqResVO.getIatSenderCountryCode()); // Sender
                                                                                                // country
                                                                                                // code
                                                                                                // (intl.
                                                                                                // prefix)
                        rechargeParam.setRCountryCode(p_iatReqResVO.getIatReceiverCountryCode()); // Receiver
                                                                                                  // country
                                                                                                  // code
                                                                                                  // (intl.
                                                                                                  // prefix)
                        rechargeParam.setSendingBearer(p_iatReqResVO.getIatSourceType()); // Sending
                                                                                          // Bearer
                                                                                          // (USSD,
                                                                                          // STK,
                                                                                          // SMS,
                                                                                          // IVR)
                        rechargeParam.setRechargeType(rechargeType); // Recharge
                                                                     // Type
                                                                     // (R2P,
                                                                     // P2P, …)
                        rechargeParam.setMSISDN1(p_iatReqResVO.getIatRetailerMsisdn()); // Retailer
                                                                                        // MSISDN
                                                                                        // O
                        rechargeParam.setMSISDN2(p_iatReqResVO.getIatReceiverMSISDN()); // Receiver
                                                                                        // MSISDN
                                                                                        // M
                        rechargeParam.setMSISDN3(p_iatReqResVO.getIatNotifyMSISDN()); // Payer
                                                                                      // MSISDN
                                                                                      // O
                        rechargeParam.setRetailerId(p_iatReqResVO.getIatRetailerID()); // Retailer
                                                                                       // Identifier
                                                                                       // O
                        rechargeParam.setDeviceId(p_iatReqResVO.getIatDeviceID()); // Device
                                                                                   // (ATM,
                                                                                   // POS,
                                                                                   // …),
                                                                                   // i.e.
                                                                                   // PreTUPSSN
                                                                                   // for
                                                                                   // PreTUPS
                                                                                   // Sonatel
                                                                                   // Senegal
                                                                                   // O
                        // 2009-06-02T15:05:43.039Z
                        Calendar calender = IATUtil.getDateFromDateString(p_iatReqResVO.getIatSendingNWTimestamp());
                        rechargeParam.setSNwTimeStamp(calender);
                        rechargeParam.setAmount(p_iatReqResVO.getIatAmountSentToIAT()); // Amount
                                                                                        // expressed
                                                                                        // in
                                                                                        // the
                                                                                        // lowest
                                                                                        // currency
                                                                                        // of
                                                                                        // the
                                                                                        // sender
                                                                                        // country
                                                                                        // (ex.
                                                                                        // cents
                                                                                        // for
                                                                                        // Euros
                                                                                        // but
                                                                                        // fcfa
                                                                                        // for
                                                                                        // CFA
                                                                                        // francs
                                                                                        // )
                                                                                        // M
                        rechargeParam.setExt2(p_iatReqResVO.getOption1()); // EXT1,
                                                                           // EXT2,
                                                                           // EXT3
                                                                           // optional
                                                                           // extended
                                                                           // parameters
                                                                           // (20,20,160
                                                                           // chars
                                                                           // size)
                        rechargeParam.setExt2(p_iatReqResVO.getOption2()); // EXT1,
                                                                           // EXT2,
                                                                           // EXT3
                                                                           // optional
                                                                           // extended
                                                                           // parameters
                                                                           // (20,20,160
                                                                           // chars
                                                                           // size)
                        rechargeParam.setExt2(p_iatReqResVO.getOption3()); // EXT1,
                                                                           // EXT2,
                                                                           // EXT3
                                                                           // optional
                                                                           // extended
                                                                           // parameters
                                                                           // (20,20,160
                                                                           // chars
                                                                           // size)
                    } catch (NumberFormatException nfExc) {
                        _log.error("sendRequestToIN", "Got NumberFormatException while setting rechargeParam .....");
                        p_iatReqResVO.setIatResponseMsgCredit("error.general.processing");
                        p_iatReqResVO.setIatResponseCodeCredit(IATI.RECHARGE_CREDIT_RESPONSE_FAIL); // 0
                                                                                                    // :
                                                                                                    // accepted,
                                                                                                    // 1
                                                                                                    // :
                                                                                                    // invalid
                                                                                                    // retailer,
                                                                                                    // 2
                                                                                                    // :
                                                                                                    // retailer
                                                                                                    // monthly
                                                                                                    // amount
                                                                                                    // threshold
                                                                                                    // reached,
                                                                                                    // …
                        p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
                        nfExc.printStackTrace();
                        throw nfExc;
                    } catch (Exception e) {
                        _log.error("sendRequestToIN", "Got Exception while setting rechargeParam .....");
                        p_iatReqResVO.setIatResponseCodeCredit(IATI.RECHARGE_CREDIT_RESPONSE_FAIL); // 0
                                                                                                    // :
                                                                                                    // accepted,
                                                                                                    // 1
                                                                                                    // :
                                                                                                    // invalid
                                                                                                    // retailer,
                                                                                                    // 2
                                                                                                    // :
                                                                                                    // retailer
                                                                                                    // monthly
                                                                                                    // amount
                                                                                                    // threshold
                                                                                                    // reached,
                                                                                                    // …
                        p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
                        e.printStackTrace();
                        throw e;
                    }
                    startTime = System.currentTimeMillis();
                    // Now call the rechargeRequest() method using stub.
                    iatServiceRequest = new IATONIStub();
                    _isRequestParamSet = iatServiceRequest.setRequestParameters(_requestMap);
                    if (!_isRequestParamSet) {
                        _log.error("sendRequestToIN", "IP_LOCAL_HOSTNAME not set on IATONIStub");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[CREDIT]", "", "INTERFACE_ID:" + _interfaceID, "", "IP_LOCAL_HOSTNAME is notset at IATONIStub.java");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    }
                    rechargeResult = iatServiceRequest.rechargeRequest(rechargeParam, authHeader);
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "After bringing response form IN rechargeResult::=" + rechargeResult.toString());

                    endTime = System.currentTimeMillis();
                    /*
                     * If warn time in the IN file exist and the difference of
                     * start and end time of writing the request
                     * and reading the response is greater than the Warn time
                     * log the info and handle the event.
                     */
                    if (endTime - startTime > warnTime) {
                        _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "IATINHandler[sendRequestToIN]", _senderZebraTXNID, _msisdn, _networkCode + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "IAT HUB is taking more time than the warning threshold. Time: " + (endTime - startTime));
                    }
                    if (rechargeResult == null) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[sendRequestToIN]", _senderZebraTXNID, _msisdn, _networkCode + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from IAT HUB");
                        _log.error("sendRequestToIN", "NULL response from IAT interface in credit");
                        p_iatReqResVO.setIatResponseMsgCredit("");
                        p_iatReqResVO.setIatResponseCodeCredit(IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS); // 0
                                                                                                         // :
                                                                                                         // accepted,
                                                                                                         // 1
                                                                                                         // :
                                                                                                         // invalid
                                                                                                         // retailer,
                                                                                                         // 2
                                                                                                         // :
                                                                                                         // retailer
                                                                                                         // monthly
                                                                                                         // amount
                                                                                                         // threshold
                                                                                                         // reached,
                                                                                                         // …
                        p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS);
                    } else {
                        if (rechargeResult.getIatTimeStamp() != null)
                            p_iatReqResVO.setIatTimeStamp(rechargeResult.getIatTimeStamp().getTime());
                        p_iatReqResVO.setIatTRXID(rechargeResult.getIatTrxId()); // PreTUPS
                                                                                 // Transaction
                                                                                 // ID
                                                                                 // return
                                                                                 // by
                                                                                 // IAT
                                                                                 // as
                                                                                 // it
                                                                                 // is.
                                                                                 // In
                                                                                 // order
                                                                                 // to
                                                                                 // map
                                                                                 // request
                                                                                 // with
                                                                                 // response.)
                        p_iatReqResVO.setIatResponseMsgCredit(rechargeResult.getMessage());
                        p_iatReqResVO.setIatResponseCodeCredit(Integer.toString(rechargeResult.getStatus())); // 0
                                                                                                              // :
                                                                                                              // accepted,
                                                                                                              // 1
                                                                                                              // :
                                                                                                              // invalid
                                                                                                              // retailer,
                                                                                                              // 2
                                                                                                              // :
                                                                                                              // retailer
                                                                                                              // monthly
                                                                                                              // amount
                                                                                                              // threshold
                                                                                                              // reached,
                                                                                                              // …
                        if (IATUtil.isNullString(p_iatReqResVO.getIatResponseCodeCredit()))
                            p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS);
                        else if (IATI.CREDIT_RESPONSE_CODE.equalsIgnoreCase(p_iatReqResVO.getIatResponseCodeCredit()))
                            p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_SUCCESS);
                        else if (IATI.IAT_INTERNAL_SERVER_ERROR_CODE.equalsIgnoreCase(p_iatReqResVO.getIatResponseCodeCredit()))
                            p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS);
                        else if (IATI.RECHARGE_CREDIT_RESPONSE_FAIL.equalsIgnoreCase(p_iatReqResVO.getIatResponseCodeCredit()))
                            p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
                        else
                            p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
                    }
                } catch (Error error) {
                    _log.error("sendRequestToIN", "Got Exception in sending credit request .....");
                    error.printStackTrace();
                } catch (Exception excp) {
                    _log.error("sendRequestToIN", "Got Exception in sending credit request .....");
                    // Remote exception, and in this case :-
                    // Whether we need to retry or simply set
                    // IatResponseCodecredit = & IatResponseMessage = Internal
                    // Error
                    if (!IATI.RECHARGE_CREDIT_RESPONSE_FAIL.equalsIgnoreCase(p_iatReqResVO.getIatINTransactionStatus())) {
                        p_iatReqResVO.setIatResponseMsgCredit("error.general.processing");
                        p_iatReqResVO.setIatResponseCodeCredit(IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS); // 0
                                                                                                         // :
                                                                                                         // accepted,
                                                                                                         // 1
                                                                                                         // :
                                                                                                         // invalid
                                                                                                         // retailer,
                                                                                                         // 2
                                                                                                         // :
                                                                                                         // retailer
                                                                                                         // monthly
                                                                                                         // amount
                                                                                                         // threshold
                                                                                                         // reached,
                                                                                                         // …
                        p_iatReqResVO.setIatINTransactionStatus(IATI.RECHARGE_CREDIT_RESPONSE_AMBIGIOUS);
                    }
                    excp.printStackTrace();
                }
            } else if ("CHKSTATUS".equalsIgnoreCase(p_action)) {
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "INSIDE CHKSTATUS p_action::=" + p_action);

                checkStatusParam = new CheckStatusParam();
                AuthHeader authHeader = new AuthHeader();
                AuthHeaderParam authHeaderParam = new AuthHeaderParam();
                authHeaderParam.setUserName(_userName);
                authHeaderParam.setPassword(_password);
                authHeader.setAuthHeaderParam(authHeaderParam);
                checkStatusParam.setIatTrxId(p_iatReqResVO.getIatTRXID());
                checkStatusParam.setSNwTrxId(p_iatReqResVO.getIatSenderNWTRXID());

                /* Added by Deepika Aggarwal */
                _sNwId = FileCache.getValue(_interfaceID, p_iatReqResVO.getIatSenderNWID() + "_NEW");
                if (IATUtil.isNullString(_sNwId)) {
                    checkStatusParam.setSNwId(p_iatReqResVO.getIatSenderNWID()); // Sending
                                                                                 // Network
                                                                                 // (operator
                                                                                 // or
                                                                                 // distribution
                                                                                 // network)
                                                                                 // identifier

                } else {
                    checkStatusParam.setSNwId(_sNwId); // Sending Network
                                                       // Identifier
                }

                /* End Added by Deepika Aggarwal */

                // checkStatusParam.setSNwId(p_iatReqResVO.getIatSenderNWID());
                IATInterfaceLog.log("Before sending " + p_action + " Request first time from " + p_reqSource, _senderZebraTXNID, p_iatReqResVO.getIatTRXID(), _msisdn, _networkCode, p_iatReqResVO.getIatServiceType(), PretupsI.TXN_LOG_REQTYPE_REQ, "[INTERFACE_ID:" + _interfaceID + "] [Request string:" + p_iatReqResVO.toString() + "] ", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "[ACTION :" + p_action + "] [ChkStatus ReqSource:" + p_reqSource + "] [Total ChktatusCount :" + _chkStatusCount + "] [First reqSleep Time:" + _reqSleepTime + "]");
                int j = 1;
                for (int i = 1; i <= _chkStatusCount; i++) {
                    try {
                        // In case of CheckStatus from IN Module, catch the
                        // exception if any and then retry
                        Thread.sleep(_reqSleepTime);
                        startTime = System.currentTimeMillis();
                        // Now call the checkStatusRequest() method using stub.
                        iatServiceRequest = new IATONIStub();
                        _isRequestParamSet = iatServiceRequest.setRequestParameters(_requestMap);
                        if (!_isRequestParamSet) {
                            _log.error("sendRequestToIN", "IP_LOCAL_HOSTNAME not set on IATONIStub");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[CREDIT]", "", "INTERFACE_ID:" + _interfaceID, "", "IP_LOCAL_HOSTNAME is notset at IATONIStub.java");
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                        }
                        checkStatusResult = iatServiceRequest.checkStatusRequest(checkStatusParam, authHeader);
                        if (_log.isDebugEnabled())
                            _log.debug("sendRequestToIN", "After returning form IN by bringing response with checkStatusResult::=" + checkStatusResult.toString());

                        endTime = System.currentTimeMillis();
                        try {
                            /*
                             * If warn time in the IN file exist and the
                             * difference of start and end time of writing the
                             * request
                             * and reading the response is greater than the Warn
                             * time log the info and handle the event.
                             */
                            warnTime = Long.parseLong(_warnTimeStr);
                            if (endTime - startTime > warnTime) {
                                _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "IATINHandler[sendRequestToIN]", _senderZebraTXNID, _msisdn, _networkCode + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "IAT HUB is taking more time than the warning threshold. Time: " + (endTime - startTime));
                            }
                        } catch (NumberFormatException nfExc) {
                            _log.error("sendRequestToIN", "Got NumberFormatException for _warnTimeStr" + _warnTimeStr);
                            nfExc.printStackTrace();
                            p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                            throw nfExc;
                        }
                        if (checkStatusResult == null) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[sendRequestToIN]", _senderZebraTXNID, _msisdn, _networkCode + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank check Status response from IAT HUB");
                            _log.error("sendRequestToIN", "NULL check status response from IAT interface ......");
                            p_iatReqResVO.setIatResponseCodeChkStatus("Null response while doing checkStatus..... ");
                            p_iatReqResVO.setIatResponseCodeChkStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                            p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                        } else {
                            p_iatReqResVO.setIatExchangeRate(checkStatusResult.getExchangeRate());
                            p_iatReqResVO.setIatReasonCode(Integer.toString(checkStatusResult.getIatReasonCode()));
                            p_iatReqResVO.setIatReasonMessage(checkStatusResult.getIatReasonMessage());

                            failedAt = getMappingFromString(Integer.toString(checkStatusResult.getLevel()), _failedAtMapping);

                            p_iatReqResVO.setIatFailedAt(failedAt);
                            p_iatReqResVO.setIatProvRatio(checkStatusResult.getProvRatio());
                            p_iatReqResVO.setIatFees(IATUtil.getSystemAmountFromINAmount(checkStatusResult.getFees(), Double.parseDouble(_multiFact)));
                            p_iatReqResVO.setIatReceiverZebraBonus(IATUtil.getSystemAmountFromINAmount(checkStatusResult.getRBonus(), Double.parseDouble(_multiFact)));
                            p_iatReqResVO.setIatRcvrRcvdAmount(IATUtil.getSystemAmountFromINAmount(checkStatusResult.getRecipientReceivedAmount(), Double.parseDouble(_multiFact)));
                            p_iatReqResVO.setIatSentAmtByIAT(IATUtil.getSystemAmountFromINAmount(checkStatusResult.getRPfReceivedAmount(), Double.parseDouble(_multiFact)));
                            p_iatReqResVO.setReceiverNWReasonCode(checkStatusResult.getRNwReasonCode());
                            // p_iatReqResVO.setReceiverNWReasonCode(Integer.toString(checkStatusResult.getRNwReasonCode()));
                            p_iatReqResVO.setReceiverNWReasonMessage(checkStatusResult.getRNwReasonMessage());
                            p_iatReqResVO.setIatReceivedAmount(IATUtil.getSystemAmountFromINAmount(checkStatusResult.getIatReceivedAmount(), Double.parseDouble(_multiFact)));
                            p_iatReqResVO.setIatResponseCodeChkStatus(Integer.toString(checkStatusResult.getStatus()));

                            if (IATUtil.isNullString(p_iatReqResVO.getIatResponseCodeChkStatus()))// discussed
                                                                                                  // by
                                                                                                  // dhiraj
                            {
                                p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                            } else if (IATI.CHECKSTATUS_RESPONSE_CODE.equalsIgnoreCase(p_iatReqResVO.getIatResponseCodeChkStatus())) {
                                p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_SUCCESS);
                                break;
                            } else if (IATUtil.isStringIn(p_iatReqResVO.getIatResponseCodeChkStatus(), IATI.CHK_STATUS_RESPONSE_AMBIGIOUS_ERROR_CODE_LIST)) {
                                p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                            } else if (IATI.REC_ZEBRA_TXN_STATUS_AMBIGUOUS.equalsIgnoreCase(p_iatReqResVO.getReceiverNWReasonCode()) || IATI.REC_ZEBRA_TXN_STATUS_UNDER_PROCESS.equalsIgnoreCase(p_iatReqResVO.getReceiverNWReasonCode()))
                                p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                            else if (IATI.CHK_STATUS_RESPONSE_FAIL_ERROR_CODE_LIST.equalsIgnoreCase(p_iatReqResVO.getIatReasonCode()))
                                p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_FAIL);
                            else {
                                p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_FAIL);
                                break;
                            }
                        }
                    } catch (Exception rmtEx) {
                        _log.error("sendRequestToIN", "While doing iteration " + i + " got Exception :" + rmtEx);
                        rmtEx.printStackTrace();
                        if (i == _chkStatusCount)// throw exception only for
                                                 // last request for chkStatus.
                        {
                            p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                        }
                    }

                    IATInterfaceLog.log(i + " iteration response of " + p_action + " Request from " + p_reqSource, _senderZebraTXNID, p_iatReqResVO.getIatTRXID(), _msisdn, _networkCode, p_iatReqResVO.getIatServiceType(), PretupsI.TXN_LOG_REQTYPE_RES, "[INTERFACE_ID:" + _interfaceID + "] [Response :" + p_iatReqResVO.toString() + "] ", p_iatReqResVO.getIatINTransactionStatus(), "[ACTION :" + p_action + "] [ChkStatus ReqSource:" + p_reqSource + "] [Itration :" + i + "] [reqSleep Time:" + _reqSleepTime + "[TTIN:" + (endTime - startTime) + "][INSTRTIME:" + startTime + "][INENDTIME:" + endTime + "]");

                    /**
                     * Also in case of Web & Process, _chkStatusCount=1 &
                     * _reqSleepTime = 0,
                     * so here no use of _reqSleepTime for 1st, request
                     * _reqSleepTime is same
                     * as pass from the respective source.
                     * REQ_SLEEP_TIME_1 is for 1st retry and REQ_SLEEP_TIME_2
                     * for second retry.
                     */
                    try {
                        j++;
                        if (j <= i)
                            _reqSleepTime = Long.parseLong(FileCache.getValue(_interfaceID, "REQ_SLEEP_TIME_" + j));
                    } catch (NumberFormatException nfExc) {
                        _log.error("sendRequestToIN", "Got Exception for REQ_SLEEP_TIME_" + j + ":: exception:-" + nfExc);
                        nfExc.printStackTrace();
                        p_iatReqResVO.setIatResponseMsgCredit("");
                        p_iatReqResVO.setIatINTransactionStatus(IATI.CHECKSTATUS_RESPONSE_AMBIGIOUS);
                        _reqSleepTime = 1000;
                    }
                    if (IATUtil.isNullString(Long.toString(_reqSleepTime))) {
                        _log.error("sendRequestToIN", "_reqSleepTime:" + _reqSleepTime);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[credit]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "REQ_SLEEP_TIME_" + j + "factor is not defined in IN File");
                        _reqSleepTime = 1000;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e ::" + e);
            throw e;
        } finally {
            rechargeResult = null;
            checkStatusResult = null;
            _log.error("sendRequestToIN", "Existing with ");
            IATInterfaceLog.log("Exiting finally of " + p_action + " Request from " + p_reqSource, _senderZebraTXNID, p_iatReqResVO.getIatTRXID(), _msisdn, _networkCode, p_iatReqResVO.getIatServiceType(), PretupsI.TXN_LOG_REQTYPE_RES, "[INTERFACE_ID:" + _interfaceID + "] [Response :" + p_iatReqResVO.toString() + "]", p_iatReqResVO.getIatINTransactionStatus(), "[ACTION :" + p_action + "] [TTIN:" + (endTime - startTime) + "][INSTRTIME:" + startTime + "][INENDTIME:" + endTime + "]");
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
     * @throws BTSLBaseException
     *             , Exception
     */
    private void setInterfaceParameters(IATInterfaceVO p_iatReqResVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        _requestMap = new HashMap<String, String>();
        try {
            _ipLocalHostName = FileCache.getValue(_interfaceID, "IP_LOCAL_HOSTNAME");
            if (IATUtil.isNullString(_ipLocalHostName)) {
                _log.error("setInterfaceParameters", "IP LocalHost Name is NULL");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IP_LOCAL_HOSTNAME is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            _requestMap.put("IP_LOCAL_HOSTNAME", _ipLocalHostName);

            _isHttpsEnable = FileCache.getValue(_interfaceID, "IS_HTTPS_ENABLE");
            if (IATUtil.isNullString(_isHttpsEnable)) {
                _log.error("setInterfaceParameters", "_isHttpsEnable:" + _isHttpsEnable);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "IS_HTTPS_ENABLE is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            if ("Y".equalsIgnoreCase(_isHttpsEnable))
                enablehttps();

            // get service type mapping from IAT IN file,
            // 1:RoamingRecharge,2:International recharge
            _serviceTypeMapping = FileCache.getValue(_interfaceID, "SERVICE_TYPE_MAPPING");
            if (IATUtil.isNullString(_serviceTypeMapping)) {
                _log.error("setInterfaceParameters", "_serviceTypeMapping:" + _serviceTypeMapping);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "SERVICE_TYPE_MAPPING is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // get service type mapping from IAT IN file, Recharge Type
            // (1:R2P,2:P2P)
            _rechargeTypeMapping = FileCache.getValue(_interfaceID, "RECHARGE_TYPE_MAPPING");
            if (IATUtil.isNullString(_rechargeTypeMapping)) {
                _log.error("setInterfaceParameters", "_rechargeTypeMapping:" + _rechargeTypeMapping);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "RECHARGE_TYPE_MAPPING is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            _failedAtMapping = FileCache.getValue(_interfaceID, "FAILED_AT_LOCATION_MAPPING");
            if (IATUtil.isNullString(_failedAtMapping)) {
                _log.error("setInterfaceParameters", "_failedAtMapping:" + _failedAtMapping);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "FAILED_AT_LOCATION_MAPPING is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            _warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (IATUtil.isNullString(_warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "WARN_TIMEOUT is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // Fetch the interface id from the request map.
            // Get the multiplication factor from the FileCache with the help of
            // interface id.
            _multiFact = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (IATUtil.isNullString(_multiFact)) {
                _log.error("setInterfaceParameters", "_multiFact:" + _multiFact);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "MULTIPLICATION_FACTOR is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            _chkStatusCount = Integer.parseInt(FileCache.getValue(_interfaceID, "CHECK_STATUS_RETRY_COUNT"));
            if (IATUtil.isNullString(Integer.toString(_chkStatusCount))) {
                _log.error("setInterfaceParameters", "_chkStatusCount:" + _chkStatusCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "CHECK_STATUS_RETRY_COUNT is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            _reqSleepTime = Long.parseLong(FileCache.getValue(_interfaceID, "REQ_SLEEP_TIME_1"));
            if (IATUtil.isNullString(Long.toString(_reqSleepTime))) {
                _log.error("setInterfaceParameters", "_reqSleepTime:" + _reqSleepTime);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "REQ_SLEEP_TIME_1 factor is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            _sendingNWType = FileCache.getValue(_interfaceID, "SENDIND_NW_TYPE");
            if (IATUtil.isNullString(_sendingNWType)) {
                _log.error("setInterfaceParameters", "_sendingNWType:" + _sendingNWType);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "SENDIND_NW_TYPE is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            _deviceID = FileCache.getValue(_interfaceID, "DEVICE_ID");
            if (IATUtil.isNullString(_deviceID)) {
                _log.error("setInterfaceParameters", "_deviceID:" + _deviceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "DEVICE_ID is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            _userName = FileCache.getValue(_interfaceID, "USER_NAME");
            if (IATUtil.isNullString(_userName)) {
                _log.error("setInterfaceParameters", "_userName:" + _userName);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "USER_NAME is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("USER_NAME", _userName);

            _password = FileCache.getValue(_interfaceID, "USER_PASSWORD");
            if (IATUtil.isNullString(_password)) {
                _log.error("setInterfaceParameters", "_password:" + _password);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[setInterfaceParameters]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) p_iatReqResVO.getIatSenderNWTRXID(), "USER_PASSWORD is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("USER_PASSWORD", _password);
        } catch (BTSLBaseException be) {
            _log.error("setInterfaceParameters", "Exception be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited");
        }
    }

    /**
     * Method ENABLEHTTPS()
     * This method is used to enable HTTPS
     * to send request through the secured socket layer
     * 
     * @throws BTSLBaseException
     */
    public void enablehttps() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("enablehttps", "Entered");
        String keyStore = null;
        String keyStorePassword = null;
        String trustStore = null;
        String trustStorePassword = null;

        try {
            keyStore = FileCache.getValue(_interfaceID, "KEY_STORE");
            if (IATUtil.isNullString(keyStore)) {
                _log.error("enablehttps", "keyStore:" + keyStore);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "KEY_STORE is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            keyStorePassword = FileCache.getValue(_interfaceID, "KEY_STORE_PASSWORD");
            if (IATUtil.isNullString(keyStore)) {
                _log.error("enablehttps", "keyStorePassword:" + keyStorePassword);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "KEY_STORE_PASSWORD is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            trustStore = FileCache.getValue(_interfaceID, "TRUST_STORE");
            if (IATUtil.isNullString(keyStore)) {
                _log.error("enablehttps", "trustStore:" + trustStore);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "TRUST_STORE is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            trustStorePassword = FileCache.getValue(_interfaceID, "TRUST_STORE_PASSWORD");
            if (IATUtil.isNullString(keyStore)) {
                _log.error("enablehttps", "trustStorePassword:" + trustStorePassword);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[Constructor]", "", "INTERFACE_ID:" + _interfaceID, "", "TRUST_STORE_PASSWORD is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }

            System.setProperty("javax.net.ssl.keyStore", keyStore);
            System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        } catch (BTSLBaseException bte) {
            bte.printStackTrace();
            _log.error("enablehttps", "Exception e=" + bte);
            throw bte;
        }
    }

    public String getMappingFromString(String strToMap, String p_mapArray) {
        if (_log.isDebugEnabled())
            _log.debug("setLanguageFromMapping", "Entered strToMap:=" + strToMap + "with p_mapArray" + p_mapArray);

        String mappedLang = "";
        String[] mappingArr;
        String[] tempArr;
        boolean mappingNotFound = true;// Flag defines whether the mapping is
                                       // found or not.
        try {
            mappingArr = p_mapArray.split(",");
            for (int in = 0; in < mappingArr.length; in++) {
                tempArr = mappingArr[in].split(":");
                if (strToMap.equals(tempArr[0].trim())) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "IATINHandler[getMappingFromString]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, _networkCode, "Mapping for " + strToMap + " is not defined in IAT IN file");

        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getMappingFromString", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "IATINHandler[getMappingFromString]", _senderZebraTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, _networkCode, "Mapping for " + strToMap + " is not defined in IAT IN file" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getMappingFromString", "Exited with mapping =" + strToMap + "=" + mappedLang);
        }
        return mappedLang;
    }

    public void Login(IATInterfaceVO p_iatvo) // throws BTSLBaseException,
                                              // Exception
    {

    }

    public void Logout(IATInterfaceVO p_iatvo) // throws BTSLBaseException,
                                               // Exception
    {

    }
}
