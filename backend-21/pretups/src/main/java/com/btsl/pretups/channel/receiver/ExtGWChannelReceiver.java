/*
 * @#ExtGWChannelReceiver.java
 * * This class is the receiver class of external user creation Module.
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Lohit August, 2011 Initial creation*
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2010 Comviva Ltd.
 */
package com.btsl.pretups.channel.receiver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.channel.logging.TransactionPerSecLogger;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.FixedInformationVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.transfer.businesslogic.MessageFormater;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ExtGWChannelReceiver extends HttpServlet {
	 public static final Log _log = LogFactory.getLog(ExtGWChannelReceiver.class.getName());
    private static String _instanceCode = null;
    private static long _requestID = 0;
	public static ThreadPoolExecutor executor = null;
	
	static{
		
		try {
            executor = (ThreadPoolExecutor)Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE")).intValue()));
        } catch (Exception e) {
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
            //log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[thread pool initialize]", "", "", "", "Exception while initilizing the thread pool :" + e.getMessage());
        }
	
	}
    @Override
    public void init() throws ServletException {
        _instanceCode = Constants.getProperty("INSTANCE_ID");
        // _instanceCode=getInitParameter("instanceCode");
        _log.info("init", "_instanceCode:" + _instanceCode);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ++_requestID;
        final String requestIDStr = String.valueOf(_requestID);
        if (_log.isDebugEnabled()) {
            _log.debug("doPost", requestIDStr, "Entered");
        }
        processRequest(requestIDStr, request, response, 2);
    }

    /**
     * @Description : process method
     * @param requestIDStr
     * @param request
     * @param response
     * @param p_requestFrom
     */
    private void processRequest(String requestIDStr, HttpServletRequest request, HttpServletResponse response, int p_requestFrom) {
        final String METHOD_NAME = "processRequest";
        StringBuilder loggerValue= new StringBuilder(); 
        long requestStartTime = 0L;
        long requestIDMethod = 0L;
        RequestVO requestVO = null;
        PrintWriter out = null;
        Date currentDate = null;
        String requestType = null;
        GatewayParsersI gatewayParsersObj = null;
        NetworkPrefixVO networkPrefixVO = null;
        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = null;
        long requestEndTime = 0L;
        String serviceType = null;
        String externalInterfaceAllowed = null;
        String message = null;

        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

        try {
            requestVO = new RequestVO();
            currentDate = new Date();
            requestStartTime = System.currentTimeMillis();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Start Time=");
            	loggerValue.append(requestStartTime);
                _log.debug("processRequest", requestIDStr, loggerValue );
            }
            out = response.getWriter();
            requestIDMethod = Long.parseLong(requestIDStr);
            requestVO.setReqContentType(request.getContentType());
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Content Type: ");
            	loggerValue.append(request.getContentType());
                _log.debug("processRequest",  loggerValue );
            }
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.C2S_MODULE);
            requestVO.setInstanceID(_instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setRequestStartTime(requestStartTime);
			if(!PretupsI.NO.equalsIgnoreCase(Constants.getProperty("IS_TPS_LOGS_REQUIRED"))){
					transactionPerSecLogging(requestStartTime,requestVO);
			}
            parseRequest(requestIDStr, request, requestVO);
            parseRequestForMessage(requestIDStr, request, requestVO, p_requestFrom);
            PretupsBL.validateRequestMessageGateway(requestVO);
            requestType = requestVO.getMessageGatewayVO().getGatewayType();
            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());// @@
            ChannelGatewayRequestLog.inLog(requestVO);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            gatewayParsersObj.parseChannelRequestMessage(requestVO, con);
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
            requestVO.setDecreaseLoadCounters(true);
            gatewayParsersObj.loadValidateNetworkDetails(requestVO);
            networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();
            requestVO.setRequestNetworkCode(networkPrefixVO.getNetworkCode());
            // Check Network Load : If true then pass the request else refuse
            // the request
            LoadController.checkNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);
            String requestHandlerClass;
            
            channelUserVO = gatewayParsersObj.loadValidateUserDetails(con, requestVO);

            if (channelUserVO != null) {
                try {
                    if (con != null) {
                        con.commit();
                        con.close();
                        con = null;
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    loggerValue.setLength(0);
                	loggerValue.append("Exception in Channelreceiver while closing connection :");
                	loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processrequest]",
                        requestIDStr, "", "",  loggerValue.toString());
                }
                populateLanguageSettings(requestVO, channelUserVO);
                if (!(channelUserVO.getUserPhoneVO()).getPhoneLanguage().equalsIgnoreCase(requestVO.getLocale().getLanguage())) {
                	loggerValue.setLength(0);
                	loggerValue.append(" Changing the Language code for MSISDN=");
                	loggerValue.append(requestVO.getFilteredMSISDN());
                	loggerValue.append(" From Language=");
                	loggerValue.append((channelUserVO.getUserPhoneVO()).getPhoneLanguage());
                	loggerValue.append(" to language=");
                	loggerValue.append( requestVO.getLocale().getLanguage());
                	_log.error(
                        "processRequest",requestVO.getRequestIDStr(),loggerValue);
                    (channelUserVO.getUserPhoneVO()).setPhoneLanguage(requestVO.getLocale().getLanguage());
                    (channelUserVO.getUserPhoneVO()).setCountry(requestVO.getLocale().getCountry());
                }
            }
            MessageFormater.handleExtChannelMessageFormat(requestVO);
            StringBuilder eventHandler= new StringBuilder(); 
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);
            if (serviceKeywordCacheVO == null) {
            	eventHandler.setLength(0);
            	eventHandler.append("Service keyword not found for the keyword=");
            	eventHandler.append( requestVO.getRequestMessageArray()[0]);
            	eventHandler.append(" For Gateway Type=");
            	eventHandler.append( requestVO.getRequestGatewayType());
            	loggerValue.append("Service Port=");
            	loggerValue.append(requestVO.getServicePort());
            	
                EventHandler
                    .handle(
                        EventIDI.SYSTEM_INFO,
                        EventComponentI.SYSTEM,
                        EventStatusI.RAISED,
                        EventLevelI.INFO,
                        "ExtGWChannelReceiver[processRequest]",requestIDStr, "", "",loggerValue.toString());
                throw new BTSLBaseException("ExtGWChannelReceiver", "processRequest", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                eventHandler.setLength(0);
            	eventHandler.append("Service keyword suspended for the keyword=" );
            	eventHandler.append(requestVO.getRequestMessageArray()[0]);
            	eventHandler.append(" For Gateway Type=");
            	eventHandler.append(requestVO.getRequestGatewayType());
            	eventHandler.append("Service Port=");
            	eventHandler.append( requestVO.getServicePort());
                EventHandler
                    .handle(
                        EventIDI.SYSTEM_INFO,
                        EventComponentI.SYSTEM,
                        EventStatusI.RAISED,
                        EventLevelI.INFO,
                        "ExtGWChannelReceiver[processRequest]",
                        requestIDStr, "", "",eventHandler.toString());
                throw new BTSLBaseException("ExtGWChannelReceiver", "processRequest", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            if (channelUserVO != null) {
                // added to store request type in channelUserVO for checking
                // while validating pin
                channelUserVO.setServiceTypes(requestVO.getServiceType());
                if (channelUserVO.isStaffUser()) {
                    channelUserVO.getStaffUserDetails().setServiceTypes(requestVO.getServiceType());
                }
                // 18. Check the service is applicable to the user from
                // user_services table
                validateServiceType(requestVO, serviceKeywordCacheVO, channelUserVO);
            }
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());
            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            requestVO.setSuccessTxn(false);
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("BTSLBaseException be:");
            	loggerValue.append( be.getMessage());
                _log.debug("processRequest", requestIDStr,loggerValue);
            }
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            loggerValue.setLength(0);
            loggerValue.append("Exception e:");
            loggerValue.append(e.getMessage());
            _log.error("processRequest", requestIDStr,  loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            loggerValue.setLength(0);
            loggerValue.append("Exception in ExtGWChannelReceiver:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtGWChannelReceiver[processRequest]", requestIDStr,
                "", "",  loggerValue.toString());
        } finally {
        	if(mcomCon != null){mcomCon.close("ExtGWChannelReceiver#processRequest");mcomCon=null;}
            // set the sender return message if it is not null
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
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
            	loggerValue.setLength(0);
            	loggerValue.append("gatewayParsersObj=");
            	loggerValue.append(gatewayParsersObj);
                _log.debug(this, requestIDStr,  loggerValue );
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            try {
                if (!requestVO.isSuccessTxn() && requestVO.isDecreaseGroupTypeCounter() && channelUserVO != null && (requestVO.getSenderVO() != null) && ((ChannelUserVO) requestVO
                    .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                    PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) requestVO.getSenderVO()).getUserControlGrouptypeCounters());
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                loggerValue.setLength(0);
            	loggerValue.append("Exception :");
            	loggerValue.append( e.getMessage());
                _log.error("processRequest", requestIDStr,  loggerValue);
            }

            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                con = null;
            }
            // Decrease the counters only when it is required
            if (requestVO.isDecreaseLoadCounters()) {
                if (networkPrefixVO != null) {
                    LoadController.decreaseCurrentNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                }
                LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }

            // request end time is the end time of the process.
            requestEndTime = System.currentTimeMillis();
            if (_log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append( "_instanceCode=");
             	loggerValue.append( _instanceCode);
             	loggerValue.append( ",requestType=");
             	loggerValue.append( requestType);
             	loggerValue.append(",networkPrefixVO="); 
             	loggerValue.append( networkPrefixVO);
             	loggerValue.append(".serviceType=");
             	loggerValue.append( serviceType);
             	loggerValue.append( ",requestIDStr=");
             	loggerValue.append(requestIDStr);
             	loggerValue.append(",LoadControllerI.COUNTER_NEW_REQUEST=");
             	loggerValue.append(LoadControllerI.COUNTER_NEW_REQUEST);
             	loggerValue.append(".requestStartTime=");
             	loggerValue.append(requestStartTime);
             	loggerValue.append(",requestEndTime=");
             	loggerValue.append(requestEndTime);
             	loggerValue.append(",requestvo=");
             	loggerValue.append(requestVO);
                _log.debug("processRequest",loggerValue );
            }
            // For increaseing the counters in network and service type
            if (networkPrefixVO != null) {
                ReqNetworkServiceLoadController.increaseIntermediateCounters(_instanceCode, requestType, networkPrefixVO.getNetworkCode(), serviceType, requestIDStr,
                    LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("requestEndTime=");
            	loggerValue.append(requestEndTime);
            	loggerValue.append( " requestStartTime=");
            	loggerValue.append(requestStartTime);
            	loggerValue.append(" Message Code=" );
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append( " Args=");
            	loggerValue.append( requestVO.getMessageArguments());
            	loggerValue.append(" Message If any=" );
            	loggerValue.append(message);
            	loggerValue.append(" Locale=");
            	loggerValue.append(requestVO.getLocale());
                _log.debug(this, requestIDStr,loggerValue );
            }
            if (requestVO.getMessageGatewayVO() != null) {
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Gateway Time out=");
                	loggerValue.append(requestVO.getMessageGatewayVO().getTimeoutValue());
                    _log.debug("processRequest", requestIDStr,  loggerValue);
                }
            }
            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            }

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
                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf(
                    "XML") != -1)) && !(PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType()) || PretupsI.MOBILE_APP_GATEWAY.equals(requestVO
                    .getRequestGatewayType())))// @@
                {
                    String txn_status = null;
                    if (requestVO.isSuccessTxn()) {
                        txn_status = PretupsI.TXN_STATUS_SUCCESS;
                    } else {
                        txn_status = requestVO.getMessageCode();
                    }
                    message = "MESSAGE=" + URLEncoder.encode(requestVO.getSenderReturnMessage(), "UTF16") + "&TXN_ID=" + BTSLUtil.NullToString(requestVO.getTransactionID()) + "&TXN_STATUS=" + BTSLUtil
                        .NullToString(txn_status);
                } else {
                    message = requestVO.getSenderReturnMessage();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Locale=");
            	loggerValue.append(requestVO.getLocale());
            	loggerValue.append(" requestEndTime=" );
            	loggerValue.append( requestEndTime );
            	loggerValue.append(" requestStartTime=");
            	loggerValue.append(requestStartTime);
            	loggerValue.append(" Message Code=");
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append(" Args=");
            	loggerValue.append(requestVO.getMessageArguments());
            	loggerValue.append(" Message If any=");
            	loggerValue.append(message);
                _log.debug("processRequest", requestIDStr,loggerValue);
            }
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
                .getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
                out.println(message);
                // if the sender return message is required then send sms to
                // sender
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && requestVO.isSuccessTxn() && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO
                    .getReqContentType().indexOf("XML") != -1 || requestVO.getReqContentType().indexOf("plain") != -1 || requestVO.getReqContentType().indexOf("PLAIN") != -1) && requestVO
                    .isSenderMessageRequired())// @@
                {
                    if (!PretupsI.YES.equals(externalInterfaceAllowed)) {
                        final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                        final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO
                            .getRequestGatewayCode(), requestVO.getLocale());// @@
                        // If changing is enable in system and external
                        // interface allowed is N then check the charging counts
                        // This change has been done by ankit on date 14/07/06
                        // for SMS charging
                        if (!PretupsI.SERVICE_TYPE_USER_AUTH.equals(requestVO.getServiceType())) {
                            pushMessage.push();
                        }
                    }
                }
            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                // get the Locale from the Channel User VO and send message back
                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(requestVO.getServiceType()) && requestVO.isSenderMessageRequired()) {
                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), requestVO.getSenderReturnMessage(), requestVO.getRequestIDStr(),
                        requestVO.getRequestGatewayCode(), requestVO.getLocale());
                    pushMessage.push();
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
            ChannelGatewayRequestLog.outLog(requestVO);
            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", requestIDStr, "Exiting");
            }
        }
    }

    /**
     * @Description: parseRequest for parsing the request coming from HTTP URL.
     * @param p_requestID
     * @param p_request
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    private void parseRequest(String p_requestID, HttpServletRequest p_request, RequestVO p_requestVO) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("Entered for p_requestID");
    		loggerValue.append(p_requestID);
    		loggerValue.append(" p_request.Header:Authorization=");
    		loggerValue.append(p_request.getHeader("Authorization"));
            _log.debug("parseRequest",  loggerValue );
        }
        if (BTSLUtil.isNullString(p_requestVO.getReqContentType())) {
            p_requestVO.setReqContentType("CONTENT_TYPE");
        }

        /*
         * String requestGatewayCode =
         * p_request.getParameter("REQUEST_GATEWAY_CODE");
         * String requestGatewayType =
         * p_request.getParameter("REQUEST_GATEWAY_TYPE");
         * String servicePort = p_request.getParameter("SERVICE_PORT");
         * String login = p_request.getParameter("LOGIN");
         * String password = p_request.getParameter("PASSWORD");
         * String sourceType = p_request.getParameter("SOURCE_TYPE");
         */

        String requestGatewayCode = null;
        String requestGatewayType = null;
        String servicePort = null;
        String login = null;
        String password = null;
        String sourceType = null;

        if (BTSLUtil.isNullString(p_request.getHeader("Authorization"))) {
            requestGatewayCode = p_request.getParameter("REQUEST_GATEWAY_CODE");
            requestGatewayType = p_request.getParameter("REQUEST_GATEWAY_TYPE");
            servicePort = p_request.getParameter("SERVICE_PORT");
            login = p_request.getParameter("LOGIN");
            password = p_request.getParameter("PASSWORD");
            sourceType = p_request.getParameter("SOURCE_TYPE");

        } else {
            final String msg = p_request.getHeader("Authorization");
            int indx1 = 0;
            try {
                indx1 = msg.indexOf("REQUEST_GATEWAY_CODE");
                indx1 = msg.indexOf("=", indx1);
                int index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    requestGatewayCode = msg.substring(indx1 + 1, index2);
                } else {
                    requestGatewayCode = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("REQUEST_GATEWAY_TYPE");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    requestGatewayType = msg.substring(indx1 + 1, index2);
                } else {
                    requestGatewayType = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("LOGIN");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    login = msg.substring(indx1 + 1, index2);
                } else {
                    login = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("PASSWORD");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    password = msg.substring(indx1 + 1, index2);
                } else {
                    password = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("SOURCE_TYPE");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    sourceType = msg.substring(indx1 + 1, index2);
                } else {
                    sourceType = msg.substring(indx1 + 1);
                }

                indx1 = msg.indexOf("SERVICE_PORT");
                indx1 = msg.indexOf("=", indx1);
                index2 = msg.indexOf("&", indx1 + 1);
                if (index2 > 0) {
                    servicePort = msg.substring(indx1 + 1, index2);
                } else {
                    servicePort = msg.substring(indx1 + 1);
                }
            } catch (Exception e) {
                _log.errorTrace("parseRequest", e);
                throw new BTSLBaseException("ExtGWChannelReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_INVALID_AUTH_PARAMETER);
            }
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("requestGatewayCode:");
        	loggerValue.append(requestGatewayCode);
        	loggerValue.append(" requestGatewayType:");
        	loggerValue.append(requestGatewayType);
        	loggerValue.append(" servicePort:");
        	loggerValue.append(servicePort);
        	loggerValue.append(" sourceType:" );
        	loggerValue.append(sourceType);
        	loggerValue.append(" login:");
        	loggerValue.append(login);
            _log.debug("parseRequest", p_requestID,loggerValue );
        }
        if (BTSLUtil.isNullString(requestGatewayCode)) {
            throw new BTSLBaseException("ExtGWChannelReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
        }
        if (BTSLUtil.isNullString(requestGatewayType)) {
            throw new BTSLBaseException("ExtGWChannelReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
        }
        p_requestVO.setRequestGatewayCode(requestGatewayCode.trim());
        p_requestVO.setRequestGatewayType(requestGatewayType.trim());
        p_requestVO.setLogin(login);
        p_requestVO.setPassword(password);
        p_requestVO.setServicePort(servicePort);
        p_requestVO.setSourceType(BTSLUtil.NullToString(sourceType));
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting for p_requestID=");
        	loggerValue.append(p_requestID);
            _log.debug("parseRequest",  loggerValue);
        }
    }

    /**
     * @Description: To parse the message coming in request
     * @param requestID
     * @param request
     * @param p_requestVO
     * @param calledMethodType
     * @throws BTSLBaseException
     */
    private void parseRequestForMessage(String requestID, HttpServletRequest request, RequestVO p_requestVO, int calledMethodType) throws BTSLBaseException {
        final String METHOD_NAME = "parseRequestForMessage";
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequestForMessage", requestID, "Entered calledMethodType: " + calledMethodType);
        }
        String msg = "";
        StringBuilder sb = new StringBuilder(1024);
        if (calledMethodType == 2) {
            try {
                final ServletInputStream in = request.getInputStream();
                int c = 0;
                sb.setLength(0);
                while ((c = in.read()) != -1) {
                    // Process line...
                	sb.append(String.valueOf(c));
                }
                msg = sb.toString();
                if (BTSLUtil.isNullString(msg)) {
                    throw new BTSLBaseException("ExtGWChannelReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                }
                p_requestVO.setRequestMessage(msg);
                if (BTSLUtil.isNullString(p_requestVO.getRequestMessage())) {
                    throw new BTSLBaseException("ExtGWChannelReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                throw be;
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("ExtGWChannelReceiver", "parseRequestForMessage", PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequestForMessage", requestID, "Exiting with Message=" + p_requestVO.getRequestMessage());
        }
    }

    /**
     * Method that will populate the language to be used while sending the
     * response
     * 
     * @param p_requestVO
     */
    private void populateLanguageSettings(RequestVO p_requestVO, ChannelUserVO channelUserVO)// @@
    {
        final FixedInformationVO fixedInformationVO = (FixedInformationVO) p_requestVO.getFixedInformationVO();
        if (p_requestVO.getLocale() == null) {
            p_requestVO.setLocale(new Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (channelUserVO.getUserPhoneVO()).getCountry()));
        }
        if (fixedInformationVO != null) {
            PretupsBL.getCurrentLocale(p_requestVO, channelUserVO.getUserPhoneVO());
        }
    }

    /**
     * @Description:validateServiceType
     * @param p_requestVO
     * @param p_serviceKeywordCacheVO
     * @param p_simProfileVO
     * @param channelUserVO
     * @throws BTSLBaseException
     */
    private void validateServiceType(RequestVO p_requestVO, ServiceKeywordCacheVO p_serviceKeywordCacheVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateServiceType";
        StringBuilder loggerValue= new StringBuilder(); 
        final String serviceType = p_requestVO.getServiceType();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" MSISDN=");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
        	loggerValue.append(" Service Type=" );
        	loggerValue.append(serviceType);
       
            _log.debug("validateServiceType", p_requestVO.getRequestIDStr(), loggerValue);
        }
        try {
            if (PretupsI.YES.equals(p_serviceKeywordCacheVO.getExternalInterface())) {
                final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType, channelUserVO.getAssociatedServiceTypeList());
                if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                	loggerValue.setLength(0);
                	loggerValue.append(" MSISDN=");
                	loggerValue.append( p_requestVO.getFilteredMSISDN());
                	loggerValue.append(" Service Type not found in allowed List");
                    _log.error("validateServiceType", p_requestVO.getRequestIDStr(),  loggerValue );
                    throw new BTSLBaseException("ExtGWChannelReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                } else if (!PretupsI.YES.equals(listValueVO.getLabel())) {
                	loggerValue.setLength(0);
                	loggerValue.append(" MSISDN=");
                	loggerValue.append( p_requestVO.getFilteredMSISDN());
                	loggerValue.append(" Service Type is suspended in allowed List");
                    _log.error("validateServiceType", p_requestVO.getRequestIDStr(),loggerValue);
                    throw new BTSLBaseException("ExtGWChannelReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED);
                }// end if
            }// end if
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            loggerValue.setLength(0);
            loggerValue.append( " MSISDN=");
            loggerValue.append( p_requestVO.getFilteredMSISDN());
            loggerValue.append(" Base Exception :");
            loggerValue.append( be.getMessage());
           
            _log.error("validateServiceType", p_requestVO.getRequestIDStr(), loggerValue);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append( " MSISDN=");
            loggerValue.append(p_requestVO.getFilteredMSISDN());
            loggerValue.append(" Exception :");
            loggerValue.append(e.getMessage());
            _log.error("validateServiceType", p_requestVO.getRequestIDStr(),loggerValue );
            loggerValue.setLength(0);
            loggerValue.append( "Not able to check validate Service Type is in allowed List for Request ID:");
            loggerValue.append(p_requestVO.getRequestID());
            loggerValue.append(" and MSISDN:");
            loggerValue.append( p_requestVO.getFilteredMSISDN());
            loggerValue.append(" ,getting Exception=" );
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkServiceTypeAllowed]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", loggerValue.toString() );
            throw new BTSLBaseException("ExtGWChannelReceiver", "validateServiceType", PretupsErrorCodesI.C2S_SERVICE_TYPE_VALIDATION_ERROR);
        }
    }

    /**
     * Method to generate XML response
     * 
     * @param p_requestVO
     */
    private void prepareXMLResponse(RequestVO p_requestVO) {
        final String METHOD_NAME = "prepareXMLResponse";
        if (_log.isDebugEnabled()) {
            _log.debug("prepareXMLResponse", p_requestVO.getRequestIDStr(), "Entered");
        }
        try {
            ParserUtility.actionChannelParser(p_requestVO);
            ParserUtility.generateChannelResponse(p_requestVO.getActionValue(), p_requestVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[prepareXMLResponse]", p_requestVO
                .getRequestIDStr(), "", "", "Exception while generating XML response:" + e.getMessage());
            try {
                ParserUtility.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[prepareXMLResponse]", p_requestVO
                    .getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("prepareXMLResponse", p_requestVO.getRequestIDStr(), "Exiting with message=" + p_requestVO.getSenderReturnMessage());
        }
    }
	
	/**
	 * @param pMillisecondsTime
	 * @param pRequestVO
	 */
	public void transactionPerSecLogging(final long pMillisecondsTime ,final RequestVO pRequestVO)
	{
	
			executor.execute(new TransactionPerSecLogger(pMillisecondsTime,pRequestVO));
		}
}
