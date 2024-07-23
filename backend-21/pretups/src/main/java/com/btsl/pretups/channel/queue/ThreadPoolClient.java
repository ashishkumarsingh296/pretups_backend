package com.btsl.pretups.channel.queue;

/*
 * @(#)ThreadPoolClient.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Anu Garg 03/08/2013 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadController;
import com.btsl.loadcontroller.LoadControllerI;
import com.btsl.loadcontroller.ReqNetworkServiceLoadController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.ChannelGatewayRequestLog;
import com.btsl.pretups.channel.logging.QueueLogger;
import com.btsl.pretups.channel.receiver.ChannelReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class ThreadPoolClient implements Runnable {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    // private static final int SERVICE_EXEC_CONTINUE = 0;
    private RequestQueueVO requestQueueVO = new RequestQueueVO();
    private List<ServiceKeywordControllerI> serviceList = null;

    public ThreadPoolClient() {
    }

    public void run() {
        processAtClient();
    }

    /**
     * processAtClient() method
     * this method 1)increment transaction counters
     * 2) Check Network Load : If true then pass the request else refuse the
     * request
     * 3) mark request under process
     * 4) Check and update the group type counters
     * 5) calls the process method of specific controller for processing the
     * request
     */
    protected void processAtClient() {
        final String METHOD_NAME = "processAtClient";
        RequestVO requestVO = null;
        boolean isMarkedUnderprocess = false;
        HttpServletResponse response = null;
        GatewayParsersI gatewayParsersObj = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = null;
        Long requestIDMethod = 0L;
        String requestIDStr = null;
        final Date currentDate = new Date();
        String message = null;
        PrintWriter out = null;
        final long requestStartTime = System.currentTimeMillis();
        long requestEndTime = 0;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("processAtClient", String.valueOf(this.requestQueueVO.getRequestIDMethod()),
                    "************Start Time at processAtClient***********=" + requestStartTime);
            }
            if (serviceList == null && serviceList.isEmpty()) {
                return;
            }
            final int size = serviceList.size();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            for (int i = 0; i < size; i++) {
                // removing the request from list after processing
                ChannelReceiver.listOfRequestsSendToPool.remove(this.getRequestQueueVO().getSenderMsisdn() + "_" + this.getRequestQueueVO().getReceiverMsisdn() + "_" + this
                    .getRequestQueueVO().getServiceType());
                QueueLogger.log("Request removed from list is " + this.getRequestQueueVO().getSenderMsisdn());
                // QueueLogger.log("entered in Thread Pool Client for "+this.getRequestQueueVO().getSenderMsisdn());
                // requestVO = new RequestVO();
                requestVO = this.requestQueueVO.getRequestVO();
                requestVO.setRequestStartTime(requestStartTime);
                requestIDMethod = this.requestQueueVO.getRequestIDMethod();
                requestIDStr = String.valueOf(requestIDMethod);
                response = requestQueueVO.getResponse();
                out = response.getWriter();
                if (((ChannelUserVO) requestVO.getSenderVO()).isStaffUser()) {
                    channelUserVO = ((ChannelUserVO) requestVO.getSenderVO()).getStaffUserDetails();
                } else {
                    channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
                }
                // QueueLogger.log(this.requestQueueVO);
                // 1. increment transaction counters
                LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
                requestVO.setDecreaseLoadCounters(true);

                // 2. Check Network Load : If true then pass the request else
                // refuse the request
                LoadController.checkNetworkLoad(requestIDMethod, requestVO.getRequestNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);
                
                // 3. mark request under process
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                    gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, true, channelUserVO);
                    isMarkedUnderprocess = true;
                } catch (BTSLBaseException e1) {
                    throw e1;
                } finally {
                    con.commit();
                }
				String grptCtrlAllowed = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED);
                // 4. Check and update the group type counters
                if (grptCtrlAllowed != null && grptCtrlAllowed.indexOf(requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
                    .equals(requestVO.getGroupType())) {
                    // load the user running and profile counters
                    final GroupTypeProfileVO groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CONTROLLING);
                    // If counters reach the profile limit them throw exception
                    if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                        requestVO.setDecreaseGroupTypeCounter(false);
                        final String arr[] = { String.valueOf(groupTypeProfileVO.getThresholdValue()) };
                        if (PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.getFrequency())) {
                            throw new BTSLBaseException(this, "processAtClient", PretupsErrorCodesI.C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_D, arr);
                        }
                        throw new BTSLBaseException(this, "processAtClient", PretupsErrorCodesI.C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_M, arr);
                    }
                }
                // 5. calls the process method of specific controller for
                // processing the request
                serviceList.get(i).process(requestVO);
            }
            if (mcomCon != null) {
				mcomCon.close("ThreadPoolClient#processAtClient");
				mcomCon = null;
			}
            con = null;            
        } catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);
            if (_log.isDebugEnabled()) {
                _log.debug("processAtClient", requestIDStr, "BTSLBaseException be:" + be.getMessage());
            }
            // if(!BTSLUtil.isNullString(requestVO.getSenderReturnMessage()))
            // message=requestVO.getSenderReturnMessage();
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            // removing the request from list after processing
            // ChannelReceiver.listOfRequestsSendToPool.remove(this.getRequestQueueVO().getSenderMsisdn()+"_"+this.getRequestQueueVO().getReceiverMsisdn()+"_"+this.getRequestQueueVO().getServiceType());
            QueueLogger.log("Thread Pool Client BTSL catch block" + requestVO.getMessageCode() + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            if (_log.isDebugEnabled()) {
                _log.debug("processAtClient", requestIDStr, "Exception e:" + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processRequest]", requestIDStr, "",
                "", "Exception in ChannelReceiver:" + e.getMessage());
            // removing the request from list after processing
            // ChannelReceiver.listOfRequestsSendToPool.remove(this.getRequestQueueVO().getSenderMsisdn()+"_"+this.getRequestQueueVO().getReceiverMsisdn()+"_"+this.getRequestQueueVO().getServiceType());
            QueueLogger.log("Thread Pool Client general catch block" + requestVO.getMessageCode());
        } finally {
            // set the sender return message if it is not null
            message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
            requestVO.setSenderReturnMessage(message);
            // if(!BTSLUtil.isNullString(requestVO.getSenderReturnMessage()))
            // message=requestVO.getSenderReturnMessage();
            try {
                // if(con==null) killed by avinash. to avoid an extra connection
                // in case of request refusal from instance and n/w
                if (con == null && (channelUserVO != null || "Y".equals(Constants.getProperty("LOAD_TEST")))) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processrequest]", requestIDStr,
                    "", "", "Exception in Channelreceiver while getting connection :" + e.getMessage());
            }

            // Forward to handler class to get the request message
            if (gatewayParsersObj == null) {
                // Will be changed after discussion with ABHIJIT
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(this, requestIDStr, "gatewayParsersObj=" + gatewayParsersObj);
            }

            if (channelUserVO != null && isMarkedUnderprocess && con != null) {
                try {
                    // If need to bar the user for PIN Change
                    if (channelUserVO.getUserPhoneVO().isBarUserForInvalidPin()) {
                        ChannelUserBL.barSenderMSISDN(con, channelUserVO, PretupsI.BARRED_TYPE_PIN_INVALID, currentDate, PretupsI.C2S_MODULE);
                        mcomCon.partialCommit();
                        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                        Locale locale = new Locale(defaultLanguage, defaultCountry);
        				PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale,channelUserVO.getNetworkCode());
        				pushMessage.push();
                    }
                } catch (BTSLBaseException be) {
                    _log.error("processAtClient", requestIDStr, "BTSLBaseException be:" + be.getMessage());
                    _log.errorTrace(METHOD_NAME, be);
                    try {
                        mcomCon.partialRollback();
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }

                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    try {
                        mcomCon.partialRollback();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processrequest]",
                        requestIDStr, "", "", "Exception in Channelreceiver while barring user becuase of invalid PIN counts:" + e.getMessage());
                }
                try {
                    // if request is under process the unmark the last
                    // transaction status as completed(C)
                    gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, false, channelUserVO);
                    // ChannelUserBL.checkRequestUnderProcess(con,requestIDStr,channelUserVO.getUserPhoneVO(),false);
                    mcomCon.partialCommit();
                } catch (BTSLBaseException be) {
                    _log.error("processAtClient", requestIDStr, "BTSLBaseException be:" + be.getMessage());
                    _log.errorTrace(METHOD_NAME, be);
                    try {
                        mcomCon.partialRollback();
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    try {
                        mcomCon.partialRollback();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processrequest]",
                        requestIDStr, "", "", "Exception in Channelreceiver while updating last status:" + e.getMessage());
                }
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            try {
                if (!requestVO.isSuccessTxn() && requestVO.isDecreaseGroupTypeCounter() && channelUserVO != null && (requestVO.getSenderVO() != null) && ((ChannelUserVO) requestVO
                    .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                    PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) requestVO.getSenderVO()).getUserControlGrouptypeCounters());
                }
            } catch (Exception e) {
                _log.error("processAtClient", requestIDStr, "Exception :" + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
            }

            if (con != null) {
                if (Constants.getProperty("LOAD_TEST") != null && "Y".equals(Constants.getProperty("LOAD_TEST"))) {
                    try {
                        // Done so that whatever the above transaction has done
                        // will be closed by the above code
                        // or else if above some exception is there it will be
                        // rollbacked
                        mcomCon.partialRollback();
                        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

                        // Add for MSISDn not found In IN 31/01/08
                        if (!BTSLUtil.isNullString(requestVO.getIntMsisdnNotFound())) {
                            requestVO.setMessageCode(requestVO.getIntMsisdnNotFound());
                            // End of 31/01/08
                        }

                        channelTransferDAO.addC2SReceiverRequests(con, requestVO);
                        mcomCon.finalCommit();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                        try {
                            mcomCon.finalRollback();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                    }
                }

                if(mcomCon != null){mcomCon.close("ThreadPoolClient#processAtClient");mcomCon=null;}
                con = null;
            }

            // Decrease the counters only when it is required
            if (requestVO.isDecreaseLoadCounters()) // ANU
            {
                if (requestVO.getRequestNetworkCode() != null) {
                    LoadController.decreaseCurrentNetworkLoad(requestIDMethod, requestVO.getRequestNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                }
                LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }

            // request end time is the end time of the process.
            requestEndTime = System.currentTimeMillis();

            // For increasing the counters in network and service type
            ReqNetworkServiceLoadController.increaseIntermediateCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), requestVO
                .getRequestNetworkCode(), requestVO.getServiceType(), requestIDStr, LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO
                .isSuccessTxn(), requestVO.isDecreaseLoadCounters());

            if (_log.isDebugEnabled()) {
                _log.debug(this, requestIDStr,
                    "requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO.getMessageCode() + " Args=" + requestVO
                        .getMessageArguments() + " Message If any=" + message + " Locale=" + requestVO.getLocale());
            }

            if (requestVO.getMessageGatewayVO() != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("processAtClient", requestIDStr, "Gateway Time out=" + requestVO.getMessageGatewayVO().getTimeoutValue());
                }
            }

            // 30/06/06 Done so that response is sent in language specified in
            // request
            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            }

            // if gateway parser is not null then parse the response using that
            // parser
            if (gatewayParsersObj != null) {
                gatewayParsersObj.generateChannelResponseMessage(requestVO);
            } else {
                // if content type is xml then prapare XML Response
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf(
                    "XML") != -1)) {
                    prepareXMLResponse(requestVO);
                }
                // if gateway parser is null and also the content type is not
                // XML then construct the sender return message
                else {
                    if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                        message = requestVO.getSenderReturnMessage();
                    } else {
                        message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                    }
                    requestVO.setSenderReturnMessage(message);
                }
            }
            try {
                String reqruestGW = requestVO.getRequestGatewayCode();
                final String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                if (!BTSLUtil.isNullString(altrnetGW) && (altrnetGW.split(":")).length >= 2) {
                    if (reqruestGW.equalsIgnoreCase(altrnetGW.split(":")[0])) {
                        reqruestGW = (altrnetGW.split(":")[1]).trim();
                        if (_log.isDebugEnabled()) {
                            _log.debug("processAtClient: Sender Message push through alternate GW", reqruestGW, "Requested GW was:" + requestVO.getRequestGatewayCode());
                        }
                    }
                }
                int messageLength = 0;
                final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                if (!BTSLUtil.isNullString(messLength)) {
                    messageLength = (new Integer(messLength)).intValue();
                }

                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf(
                    "XML") != -1)) && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType())) {
                    String txn_status = null;
                    if (requestVO.isSuccessTxn()) {
                        txn_status = PretupsI.TXN_STATUS_SUCCESS;
                    } else {
                        txn_status = requestVO.getMessageCode();
                    }
                    if (!reqruestGW.equalsIgnoreCase(requestVO.getRequestGatewayCode())) {
                        message = requestVO.getSenderReturnMessage();
                        String message1 = null;
                        if ((messageLength > 0) && (message.length() > messageLength)) {
                            message1 = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.REQUEST_IN_QUEUE_UB, requestVO.getMessageArguments());
                            final PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1, requestVO.getRequestIDStr(), requestVO
                                .getRequestGatewayCode(), requestVO.getLocale());
                            pushMessage1.push();
                            requestVO.setRequestGatewayCode(reqruestGW);
                        }
                    } else // Message Encoding need not be required only for WEB
                           // interface.
                    if ((requestVO.getRequestGatewayType()).equalsIgnoreCase(PretupsI.GATEWAY_TYPE_WEB)) {
                        message = "MESSAGE=" + URLEncoder.encode(requestVO.getSenderReturnMessage(), "UTF16") + "&TXN_ID=" + BTSLUtil.NullToString(requestVO
                            .getTransactionID()) + "&TXN_STATUS=" + BTSLUtil.NullToString(txn_status);
                    } else {
                        message = requestVO.getSenderReturnMessage();
                    }
                } else {
                    message = requestVO.getSenderReturnMessage();
                }
                if (PretupsI.SERVICE_TYPE_MVD.equalsIgnoreCase(requestVO.getServiceType())) {
                    if (((ArrayList) requestVO.getValueObject())!= null && !((ArrayList) requestVO.getValueObject()).isEmpty()) {
                        VomsVoucherVO voucherVO = null;
                        voucherVO = (VomsVoucherVO) ((ArrayList) requestVO.getValueObject()).get(0);
                        message = message + "&SALE_BATCH_NO=" + voucherVO.getSaleBatchNo();
                    }
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("processAtClient", requestIDStr,
                    "Locale=" + requestVO.getLocale() + " requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO
                        .getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message);
            }
            String grptChrgAllowed = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED));
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
                .getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
                out.println(message);
                // if the sender return message is required then send sms to
                // sender
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && requestVO.isSuccessTxn() && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO
                    .getReqContentType().indexOf("XML") != -1 || requestVO.getReqContentType().indexOf("plain") != -1 || requestVO.getReqContentType().indexOf("PLAIN") != -1) && requestVO
                    .isSenderMessageRequired()) {
                    if (!PretupsI.YES.equals(requestQueueVO.getExternalInterfaceAllowed())) {
                        final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                        // PushMessage pushMessage=new
                        // PushMessage(requestVO.getFilteredMSISDN(),senderMessage,requestVO.getRequestIDStr(),requestVO.getRequestGatewayCode(),requestVO.getLocale());
                        final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO
                            .getRequestGatewayCode(), requestVO.getLocale());
                        // If changing is enable in system and external
                        // interface allowed is N then check the charging counts
                        // This change has been done by ankit on date 14/07/06
                        // for SMS charging
                        if (grptChrgAllowed != null && grptChrgAllowed.indexOf(requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
                            .equals(requestVO.getGroupType())) {
                            GroupTypeProfileVO groupTypeProfileVO = null;
                            // load the user running and profile counters
                            // Check the counters
                            // update the counters
                            groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
                            // If counts reach the profile limit then send
                            // message using gateway that is associated with
                            // group type profiles
                            if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                                pushMessage.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());// new
                                // method
                                // will
                                // be
                                // called
                                // here
                                SMSChargingLog.log(((ChannelUserVO) requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) requestVO.getSenderVO())
                                    .getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
                                    groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(),
                                    requestVO.getModule());
                            } else {
                                pushMessage.push();
                            }
                        } else {
                            pushMessage.push();
                        }
                    }
                }

                /*
                 * //return message to sender
                 * if(message!=null)
                 * out.println(message);
                 * else
                 * out.println(BTSLUtil.getMessage(requestVO.getLocale(),requestVO
                 * .getMessageCode(),requestVO.getMessageArguments()));
                 */
            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                // get the Locale from the Channel User VO and send message back
                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(requestVO.getServiceType()) && requestVO.isSenderMessageRequired()) {
                    // out.println(message);
                    /*
                     * //Will be removed in the future: For testing
                     * if(message!=null)
                     * out.println(message);
                     * else
                     * out.println(BTSLUtil.getMessage(requestVO.getLocale(),
                     * requestVO
                     * .getMessageCode(),requestVO.getMessageArguments()));
                     */
                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), requestVO.getSenderReturnMessage(), requestVO.getRequestIDStr(),
                        requestVO.getRequestGatewayCode(), requestVO.getLocale());
                    /*
                     * if(message!=null)
                     * pushMessage=new
                     * PushMessage(requestVO.getFilteredMSISDN(),
                     * message,requestVO
                     * .getRequestIDStr(),requestVO.getRequestGatewayCode
                     * (),requestVO.getLocale());
                     * else
                     * pushMessage=new
                     * PushMessage(requestVO.getFilteredMSISDN(),
                     * BTSLUtil.getMessage
                     * (requestVO.getLocale(),requestVO.getMessageCode
                     * (),requestVO
                     * .getMessageArguments()),requestVO.getRequestIDStr
                     * (),requestVO
                     * .getRequestGatewayCode(),requestVO.getLocale());
                     */
                    // This change has been done by ankit on date 14/07/06 for
                    // SMS charging
                    if (!PretupsI.YES.equals(requestQueueVO.getExternalInterfaceAllowed()) && grptChrgAllowed != null && grptChrgAllowed
                        .indexOf(requestVO.getRequestGatewayType()) != -1 && requestVO.isSuccessTxn() && !PretupsI.NOT_APPLICABLE.equals(requestVO.getGroupType())) {
                        GroupTypeProfileVO groupTypeProfileVO = null;
                        // load the user running and profile counters
                        // Check the counters
                        // update the counters
                        groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
                        // If counts reach the profile limit then send message
                        // using gateway that is associated with group type
                        // profiles
                        if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                            pushMessage.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());// new
                            // method
                            // will
                            // be
                            // called
                            // here
                            SMSChargingLog.log(((ChannelUserVO) requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) requestVO.getSenderVO())
                                .getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
                                groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(), requestVO
                                    .getModule());
                        } else {
                            pushMessage.push();
                        }
                    } else {
                        pushMessage.push();
                    }
                }
            }
            out.flush();
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            // Log the request in Request Logger
            ChannelGatewayRequestLog.outLog(requestVO);
            if (_log.isDebugEnabled()) {
                _log.debug("processAtClient", requestIDStr, "Exiting");
            }
        }
    }
    
    @Override
    public boolean equals(Object object) {
        final ThreadPoolClient element1 = (ThreadPoolClient) object;
        final RequestQueueVO element = element1.getRequestQueueVO();
        if (element.getReceiverMsisdn().equals(this.requestQueueVO.getReceiverMsisdn()) && element.getSenderMsisdn().equals(this.requestQueueVO.getSenderMsisdn()) && element
            .getServiceType().equals(this.requestQueueVO.getServiceType())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
    	int hash = 7;
        hash = 31 * hash + (this.requestQueueVO.getReceiverMsisdn() == null ? 0 : this.requestQueueVO.getReceiverMsisdn().hashCode());
        hash = 31 * hash + (this.requestQueueVO.getSenderMsisdn() == null ? 0 : this.requestQueueVO.getSenderMsisdn().hashCode());
        hash = 31 * hash + (this.requestQueueVO.getServiceType() == null ? 0 : this.requestQueueVO.getServiceType().hashCode());
        return hash;
    }

    public void setServiceList(List<ServiceKeywordControllerI> serviceList) {
        this.serviceList = serviceList;
    }

    public void setRequestQueueVO(RequestQueueVO requestQueueVO) {
        this.requestQueueVO = requestQueueVO;
    }

    public RequestQueueVO getRequestQueueVO() {
        return this.requestQueueVO;
    }

    /**
     * Method to generate XML response
     * 
     * @param p_requestVO
     */
    private void prepareXMLResponse(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareXMLResponse", p_requestVO.getRequestIDStr(), "Entered");
        }
        final String METHOD_NAME = "prepareXMLResponse";
        try {
            ParserUtility.actionChannelParser(p_requestVO);
            ParserUtility.generateChannelResponse(p_requestVO.getActionValue(), p_requestVO);
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[prepareXMLResponse]", p_requestVO
                .getRequestIDStr(), "", "", "Exception while generating XML response:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            try {
                ParserUtility.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[prepareXMLResponse]", p_requestVO
                    .getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
                _log.errorTrace(METHOD_NAME, ex);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("prepareXMLResponse", p_requestVO.getRequestIDStr(), "Exiting with message=" + p_requestVO.getSenderReturnMessage());
        }
    }
}
