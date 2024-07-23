package com.btsl.pretups.p2p.receiver.requesthandler;

/*
 * @(#)P2PReceiver.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * Ankit Zindal 20/11/2006 ChangeID=LOCALEMASTER
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.logging.P2PGatewayRequestLog;
import com.btsl.pretups.p2p.query.businesslogic.SubscriberTransferDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author abhijit.chauhan
 * 
 */
public class P2PReceiver extends HttpServlet {

    private final Log _log = LogFactory.getLog(this.getClass().getName());
    private static String _instanceCode = null;
    private static long _requestID = 0;
    private static OperatorUtilI calculatorI = null;

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    @Override
	public void init() throws ServletException {
        // Put your code here
        final String METHOD_NAME = "init";
        _instanceCode = getInitParameter("instanceCode");
        _log.info("init", "instanceCode:" + _instanceCode);
        final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PReceiver[init]", "", "", "",
                "Exception while loading the operator util class at the init:" + e.getMessage());
        }
        _log.info("init", "calculatorI:" + calculatorI);
    }

    /**
     * Constructor of the object.
     */
    public P2PReceiver() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    @Override
	public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ++_requestID;
        final String requestIDStr = String.valueOf(_requestID);
        if (_log.isDebugEnabled()) {
            _log.debug("doGet", requestIDStr, "Entered");
        }
        processRequest(requestIDStr, request, response, 1);
    }

    /**
     * The doPost method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to
     * post.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
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
     * Parse parse Request For Message and retreives details
     * 
     * @param requestID
     * @param request
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    public void parseRequestForMessage(String requestID, HttpServletRequest request, RequestVO p_requestVO, int calledMethodType) throws BTSLBaseException {
        final String METHOD_NAME = "parseRequestForMessage";
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequestForMessage", requestID, "Entered calledMethodType: " + calledMethodType);
        }
        if (calledMethodType == 1) {
            String msisdn = request.getParameter("MSISDN");
            final String requestMessage = request.getParameter("MESSAGE");

            if (_log.isDebugEnabled()) {
                _log.debug("parseRequestForMessage", requestID, "msisdn:" + msisdn + " requestMessage:" + requestMessage);
            }
            if (BTSLUtil.isNullString(msisdn)) {
                throw new BTSLBaseException("P2PReceiver", "parseRequestForMessage", PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);
            } else {
                msisdn = msisdn.trim();
            }
            p_requestVO.setRequestMSISDN(msisdn);

            if (BTSLUtil.isNullString(requestMessage)) {
				if(PretupsI.GATEWAY_TYPE_USSD.equalsIgnoreCase(p_requestVO.getRequestGatewayType())){
					String p2pThroughPlainUSSDAllowded = "N";
					p2pThroughPlainUSSDAllowded= Constants.getProperty("P2P_USSD_PLAIN_ALLOWDED");
					if(PretupsI.YES.equalsIgnoreCase(p2pThroughPlainUSSDAllowded)){
						//check if request interface valid
						PretupsBL.validateRequestMessageGateway(p_requestVO);
						if(_log.isDebugEnabled())_log.debug("parseRequestForMessage",requestID,"Finally p_requestVO:getRequestMessage()="+p_requestVO.getMessageGatewayVO().getHandlerClass());
						GatewayParsersI gatewayParsersObj=null;
						//Forward to handler class to get the request message
						gatewayParsersObj=(GatewayParsersI)PretupsBL.getGatewayHandlerObj(p_requestVO.getMessageGatewayVO().getHandlerClass());
						gatewayParsersObj.parseRequestMessage(p_requestVO,request);
						p_requestVO.setRequestMessage(p_requestVO.getDecryptedMessage());
						if(_log.isDebugEnabled())_log.debug("parseRequestForMessage",requestID,"Finally p_requestVO:getRequestMessage()="+p_requestVO.getRequestMessage()+", getDecryptedMessage()="+p_requestVO.getDecryptedMessage());
					}
					
				} else {
					throw new BTSLBaseException("P2PReceiver", "parseRequestForMessage", PretupsErrorCodesI.P2P_ERROR_BLANK_REQUESTMESSAGE);
				}
			} else { 
				p_requestVO.setRequestMessage(requestMessage);
			}
        } else {
            final String msisdn = request.getParameter("MSISDN");
            final String requestMessage = request.getParameter("MESSAGE");
            String msg = "";
            StringBuilder sb = new StringBuilder(1024);
            if (BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage)) {
                if (PretupsI.FLARES_CONTENT_TYPE.equals(request.getContentType())) {
                    final StringBuffer requestBuffer = new StringBuffer();
                    String tempElement = null;
                    final Enumeration requestEnum = request.getParameterNames();
                    while (requestEnum.hasMoreElements()) {
                        tempElement = (String) requestEnum.nextElement();
                        requestBuffer.append(tempElement + "=" + request.getParameter(tempElement) + "&");
                        if ("TYPE".equals(tempElement)) {
                            p_requestVO.setServiceKeyword(request.getParameter(tempElement));
                        }
                    }
                    p_requestVO.setRequestMessage(requestBuffer.toString());
                    p_requestVO.setReqContentType("plain");

                } else {
                    try {
                        final ServletInputStream in = request.getInputStream();
                        int c = 0;

                        while ((c = in.read()) != -1) {
                            // Process line...
                            msg += (char) c;
                        }
                        if (BTSLUtil.isNullString(msg)) {
                            throw new BTSLBaseException("P2PReceiver", "parseRequestForMessage", PretupsErrorCodesI.P2P_ERROR_BLANK_REQUESTMESSAGE);
                        }
                        p_requestVO.setRequestMessage(msg);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException("P2PReceiver", "parseRequestForMessage", PretupsErrorCodesI.REQ_NOT_PROCESS);
                    }
                }
            }
            if (!BTSLUtil.isNullString(msisdn)) {
                p_requestVO.setRequestMSISDN(msisdn);
            }

            if (!BTSLUtil.isNullString(requestMessage)) {
                p_requestVO.setRequestMessage(requestMessage);
            }

            if (BTSLUtil.isNullString(p_requestVO.getRequestMessage())) {
                throw new BTSLBaseException("P2PReceiver", "parseRequestForMessage", PretupsErrorCodesI.P2P_ERROR_BLANK_REQUESTMESSAGE);
            }
        }

        if (_log.isDebugEnabled()) {
            _log.debug("parseRequestForMessage", requestID, "Exiting with Message=" + p_requestVO.getRequestMessage());
        }
    }

    /**
     * Parse Request and retreives details
     * 
     * @param requestID
     * @param request
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    public void parseRequest(String requestID, HttpServletRequest p_request, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered for p_requestID=" + " p_request.Header:Authorization=" + p_request.getHeader("Authorization"));
        }

        if (BTSLUtil.isNullString(p_requestVO.getReqContentType())) {
            p_requestVO.setReqContentType("CONTENT_TYPE");
        }

        String requestGatewayCode = null;
        String requestGatewayType = null;
        String servicePort = null;
        String login = null;
        String password = null;
        String udh = null;
        String sourceType = null;
        String headerAuthorization=p_request.getHeader("Authorization");
		if("CONTENT_TYPE".equalsIgnoreCase(p_requestVO.getReqContentType())){
			headerAuthorization="";
			p_requestVO.setReqContentType("PLAIN");
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Manual Authorization Done. prequest.Header:Authorization="+headerAuthorization );
			}
		}
        if (BTSLUtil.isNullString(headerAuthorization)) {
            requestGatewayCode = p_request.getParameter("REQUEST_GATEWAY_CODE");
            requestGatewayType = p_request.getParameter("REQUEST_GATEWAY_TYPE");
            servicePort = p_request.getParameter("SERVICE_PORT");
            login = p_request.getParameter("LOGIN");
            password = p_request.getParameter("PASSWORD");
            udh = p_request.getParameter("UDH");
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
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("P2PReceiver", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALID_AUTH_PARAMETER);
            }
        }

        if (BTSLUtil.isNullString(requestGatewayCode)) {
            throw new BTSLBaseException("P2PReceiver", METHOD_NAME, PretupsErrorCodesI.P2P_ERROR_BLANK_REQUESTINTID);
        } else {
            requestGatewayCode = requestGatewayCode.trim();
        }
        p_requestVO.setRequestGatewayCode(requestGatewayCode);

        if (BTSLUtil.isNullString(requestGatewayType)) {
            throw new BTSLBaseException("P2PReceiver", METHOD_NAME, PretupsErrorCodesI.P2P_ERROR_BLANK_REQUESTINTTYPE);
        } else {
            requestGatewayType = requestGatewayType.trim();
        }
        p_requestVO.setRequestGatewayType(requestGatewayType);
        p_requestVO.setUDH(udh);
        p_requestVO.setLogin(login);
        p_requestVO.setPassword(password);
        p_requestVO.setServicePort(servicePort);
        p_requestVO.setRemoteIP(p_request.getRemoteAddr());
        p_requestVO.setSourceType(BTSLUtil.NullToString(sourceType));

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting ");
        }

    }

    /**
     * Method to process the common request from different Request Type i.e. GET
     * or POST
     * 
     * @param requestIDStr
     * @param request
     * @param response
     * @param p_requestFrom
     */
    private void processRequest(String requestIDStr, HttpServletRequest request, HttpServletResponse response, int p_requestFrom) {
        final String METHOD_NAME = "processRequest";
        response.setContentType("text/html");
        PrintWriter out = null;
        final BTSLMessages messages = null;
        String message = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        SenderVO senderVO = null;
        final Date currentDate = new Date();
        NetworkPrefixVO networkPrefixVO = null;
        final RequestVO requestVO = new RequestVO();
        final long requestStartTime = System.currentTimeMillis();
        long requestEndTime = 0;
        boolean isMarkedUnderprocess = false;
        String filteredMSISDN = null;
        GatewayParsersI gatewayParsersObj = null;
        long requestIDMethod = 0;
        String requestType = null;
        String networkID = null;
        String serviceType = null;
        String externalInterfaceAllowed = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", requestIDStr, "************Start Time***********=" + requestStartTime);
            }
            out = response.getWriter();
            requestIDMethod = Long.parseLong(requestIDStr);
            requestVO.setReqContentType(request.getContentType());
            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", "Content Type: " + request.getContentType());
            }
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.P2P_MODULE);
            requestVO.setInstanceID(_instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setPlainMessage(true);
            requestVO.setRequestStartTime(requestStartTime);
            parseRequest(requestIDStr, request, requestVO);

            parseRequestForMessage(requestIDStr, request, requestVO, p_requestFrom);
            // check if request interface valid
            PretupsBL.validateRequestMessageGateway(requestVO);
            requestType = requestVO.getMessageGatewayVO().getGatewayType();

            // Forward to handler class to get the request message
            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());

            gatewayParsersObj.parseRequestMessage(requestVO);

            // filteredMSISDN=PretupsBL.getFilteredMSISDN(requestVO.getRequestMSISDN());
            filteredMSISDN = calculatorI.getSystemFilteredMSISDN(requestVO.getRequestMSISDN());
            requestVO.setFilteredMSISDN(filteredMSISDN);
            if (!BTSLUtil.isValidMSISDN(filteredMSISDN)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PReceiver[processRequest]", requestIDStr,
                    filteredMSISDN, "", "Sender MSISDN Not valid");
                requestVO.setSenderMessageRequired(false);
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN);
            }
            // Done on 25/06/07 by Ankit Zindal as discussed with Sanjay/Gurjeet
            // This block will check the status of message gateway
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
            }

            LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
            requestVO.setDecreaseLoadCounters(true);

            if (requestVO.getReceiverLocale() == null) {
                requestVO.setReceiverLocale(requestVO.getLocale());
            }

            // load network details
            //changed by Ashish for VIL
            String tempfilteredMSISDN = filteredMSISDN;
//            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS))).booleanValue()){
//            	filteredMSISDN=requestVO.getImsi();
//            }
            //networkPrefixVO = PretupsBL.getNetworkDetails(filteredMSISDN, PretupsI.USER_TYPE_SENDER);
            networkPrefixVO = calculatorI.getNetworkDetails(filteredMSISDN, PretupsI.USER_TYPE_SENDER);
            filteredMSISDN = tempfilteredMSISDN;

            requestVO.setRequestNetworkCode(networkPrefixVO.getNetworkCode());

            // Check Network Load : If true then pass the request else refuse
            // the request
            LoadController.checkNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);
            requestVO.setDecreaseNetworkLoadCounters(true);

            networkID = networkPrefixVO.getNetworkCode();

            // check network status
            if (!PretupsI.YES.equals(networkPrefixVO.getStatus())) {
                // ChangeID=LOCALEMASTER
                // Set the message based on locale master value for the
                // requested locale
                final LocaleMasterVO localeVO = LocaleMasterCache
                    .getLocaleDetailsFromlocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    message = networkPrefixVO.getLanguage1Message();
                } else {
                    message = networkPrefixVO.getLanguage2Message();
                }
                requestVO.setSenderReturnMessage(message);
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.P2P_NETWORK_NOT_ACTIVE);
            }

            // check network load
            String requestHandlerClass;
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // check black list status of the sender

            // this code kill for un barreduser after barred Expiry period.
            /*
				check MSISDN barred

             */

            // check MSISDN barred and un barreduser after barred Expiry period.
            // modify for mali.Check and automated un barred pin type in the
            // system
            if (requestVO.getMessageGatewayVO().getAccessFrom() == null || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                // chenel vo passed as null as p2p has no categoty.
                PretupsBL.unBarredUserAutomaic(con, filteredMSISDN, networkPrefixVO.getNetworkCode(), PretupsI.P2P_MODULE, PretupsI.USER_TYPE_SENDER, null);
            }

            // load subscriber details
            senderVO = SubscriberBL.validateSubscriberDetails(con, filteredMSISDN);
            if (senderVO != null) {
                // senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                if(!BTSLUtil.isNullString(requestVO.getImsi())){
                	senderVO.setImsi(requestVO.getImsi());
                }

                senderVO.setModifiedBy(senderVO.getUserID());
                // senderVO.setModifiedOn(currentDate);
                senderVO.setModule(PretupsI.P2P_MODULE);
                requestVO.setSenderVO(senderVO);
                requestVO.setLocale(senderVO.getLocale());
                final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMSISDN);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, senderVO.getSubscriberType());

                // This condition is added to update the prefixID of Subscriber
                // in P2PSubscriber table Date 25/01/08
                if (senderVO.getPrefixID() != networkPrefixVO.getPrefixID()) {
                    SubscriberBL.updateSubscriberPrefixID(con, senderVO, networkPrefixVO.getPrefixID());
                }
                // end of prefixID updation

                // check for previous request under process
                SubscriberBL.checkRequestUnderProcess(con, requestIDStr, senderVO, true);
                isMarkedUnderprocess = true;
                con.commit();

            }

			if (mcomCon != null) {
				mcomCon.close("P2PReceiver#processRequest");
				mcomCon = null;
			}
            con = null;

            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", requestIDStr, "Sender Locale in Request if any" + requestVO.getSenderLocale());
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            } else {
                requestVO.setSenderLocale(requestVO.getLocale());
            }

            // message encryption check
            // parse message
            final String[] messageArray = PretupsBL.parsePlainMessage(requestVO.getDecryptedMessage());
            requestVO.setRequestMessageArray(messageArray);
            // load service details, CR 000009 Sub Keyword Based Service Type
            // identification
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
                // return with error message
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PReceiver[processRequest]", requestIDStr,
                    filteredMSISDN, "",
                    "Service keyword not found for the keyword=" + messageArray[0] + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                        .getServicePort());
                throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PReceiver[processRequest]", requestIDStr,
                    filteredMSISDN, "",
                    "Service keyword suspended for the keyword=" + messageArray[0] + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                        .getServicePort());
                throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            if (senderVO == null) {
                // if service type is not reqisteration
                if (serviceKeywordCacheVO.getUnregisteredAccessAllowed().equals(PretupsI.NO)) {
                    throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.ERROR_NOTFOUND_SUBSCRIBER);
                }
            } else if ((serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PSUSPEND)) && senderVO.getStatus()
                .equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.P2P_USER_STATUS_ALREADY_SUSPENDED);
            } else if ((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_RESUMESERVICE)) && senderVO.getStatus().equalsIgnoreCase(
                PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_SENDER_SUSPEND);
            } else if ((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.MULT_CRE_TRA_DED_ACC)) && (!serviceKeywordCacheVO.getServiceType().equals(
                PretupsI.SERVICE_TYPE_P2PRECHARGE)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE)) && (!serviceKeywordCacheVO
                .getServiceType().equals(PretupsI.SERVICE_TYPE_ACCOUNTINFO)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PCHANGEPIN)) && (!serviceKeywordCacheVO
                .getServiceType().equals(PretupsI.SERVICE_TYPE_REGISTERATION)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PRECHARGEWITHVALEXT)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_DATA_CP2P)) &&  senderVO
                .getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW)) {
                throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_SENDER_STATUS_NEW);
            }
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());
            // If group type counters are allowed to check for controlling for
            // the request gateway then check them
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (requestVO.getSenderVO() != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)).indexOf(requestVO
                .getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(requestVO.getGroupType())) {
                // load the user running and profile counters
                // Check the counters
                // update the counters
                final GroupTypeProfileVO groupTypeProfileVO = PretupsBL.loadAndCheckP2PGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CONTROLLING);
                // If counters reach the profile limit them throw exception
                if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                    requestVO.setDecreaseGroupTypeCounter(false);
                    final String arr[] = { String.valueOf(groupTypeProfileVO.getThresholdValue()) };
                    if (PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.getFrequency())) {
                        throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D, arr);
                    }
                    throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M, arr);
                }
            }
            // call process of controller
            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            requestVO.setSuccessTxn(false);
			requestVO.setDecreaseGroupTypeCounter(false);
            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", requestIDStr, "BTSLBaseException be:" + be.getMessage());
            }
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                // construct Message
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
			requestVO.setDecreaseGroupTypeCounter(false);
            _log.error("processRequest", requestIDStr, "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PReceiver[processRequest]", requestIDStr, "", "",
                "Exception in P2PReceiver:" + e.getMessage());
        } finally {
            try {
                // if(con==null) killed by avinash. to avoid an extra connection
                // in case of request refusal from instance and n/w
                if (mcomCon == null && (senderVO != null || "Y".equals(Constants.getProperty("LOAD_TEST")))) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PReceiver[processrequest]", requestIDStr, "",
                    "", "Exception in P2PReceiver while getting connection :" + e.getMessage());
            }

            if (senderVO != null && isMarkedUnderprocess && requestVO.isUnmarkSenderRequired() && con != null) {
                try {
                    // If need to bar the user for PIN Change
                    if (_log.isDebugEnabled()) {
                        _log.debug("processRequest", requestIDStr, "User Barring required because of PIN change......" + senderVO.isBarUserForInvalidPin());
                    }
                    if (senderVO.isBarUserForInvalidPin()) {
                        SubscriberBL.barSenderMSISDN(con, senderVO, PretupsI.BARRED_TYPE_PIN_INVALID, currentDate);
                        con.commit();
						Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
						PushMessage pushMessage = new PushMessage(senderVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale,senderVO.getNetworkCode());
						pushMessage.push();
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    _log.error("processRequest", requestIDStr, "BTSLBaseException be: " + be.getMessage());
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    try {
                        con.rollback();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PReceiver[processRequest]", requestIDStr,
                        "", "", "Exception in P2PReceiver:" + e.getMessage());
                }
                try {
                    SubscriberBL.checkRequestUnderProcess(con, requestIDStr, senderVO, false);
                    con.commit();
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    _log.error("processRequest", requestIDStr, "BTSLBaseException be:" + be.getMessage());
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    try {
                        con.rollback();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PReceiver[processRequest]", requestIDStr,
                        "", "", "Exception in P2PReceiver:" + e.getMessage());
                }
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (requestVO.getSenderVO() != null && !requestVO.isSuccessTxn() && requestVO.isDecreaseGroupTypeCounter() && ((SenderVO) requestVO.getSenderVO())
                .getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((SenderVO) requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }
            if (con != null) {
                if (Constants.getProperty("LOAD_TEST") != null && "Y".equals(Constants.getProperty("LOAD_TEST"))) {
                    try {
                        // Done so that whatever the above transaction has done
                        // will be closed by the above code
                        // or else if above some exception is there it will be
                        // rollbacked
                    	mcomCon.finalRollback();
                        final SubscriberTransferDAO subscriberTransferDAO = new SubscriberTransferDAO();
                        subscriberTransferDAO.addP2PReceiverRequests(con, requestVO);
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

				if (mcomCon != null) {
					mcomCon.close("P2PReceiver#processRequest");
					mcomCon = null;
				}
                con = null;
            }

            // Decrease the counters only when it is required
            if (requestVO.isDecreaseLoadCounters()) {
                if (requestVO.isDecreaseNetworkLoadCounters()) {
                    LoadController.decreaseCurrentNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                }
                LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
            // return message to sender
            // pass default locale also
            requestEndTime = System.currentTimeMillis();

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseIntermediateCounters(_instanceCode, requestType, networkID, serviceType, requestIDStr,
                LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());

            if (requestVO.getMessageGatewayVO() != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(this, requestIDStr, "requestVO.getMessageGatewayVO()=" + requestVO.getMessageGatewayVO().getTimeoutValue());
                }
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
            if (gatewayParsersObj != null) {
                gatewayParsersObj.generateResponseMessage(requestVO);
            } else {
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf(
                    "XML") != -1)) {
                    prepareXMLResponse(requestVO);
                } else {
                    if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                        message = requestVO.getSenderReturnMessage();
                    } else {
                        message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                    }
                    requestVO.setSenderReturnMessage(message);
                }
            }
            // Added by Rajdeep for getting the response for CP2P web recharge
            try {
                String reqruestGW = requestVO.getRequestGatewayCode();
                final String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
                if (!BTSLUtil.isNullString(altrnetGW) && (altrnetGW.split(":")).length >= 2) {
                    if (reqruestGW.equalsIgnoreCase(altrnetGW.split(":")[0])) {
                        reqruestGW = (altrnetGW.split(":")[1]).trim();
                        if (_log.isDebugEnabled()) {
                            _log.debug("processRequest: Sender Message push through alternate GW", reqruestGW, "Requested GW was:" + requestVO.getRequestGatewayCode());
                        }
                    }
                }
                int messageLength = 0;
                final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                if (!BTSLUtil.isNullString(messLength)) {
                    messageLength = (new Integer(messLength)).intValue();
                }

                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf(
                    "XML") != -1)) && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType()))// @@
                {
                    String txn_status = null;
                    if (requestVO.isSuccessTxn()) {
                        txn_status = PretupsI.TXN_STATUS_SUCCESS;
                    } else {
                        txn_status = requestVO.getMessageCode();
                    }
                    if (!reqruestGW.equalsIgnoreCase(requestVO.getRequestGatewayCode()) || !("WEB".equalsIgnoreCase(reqruestGW))) {
                        message = requestVO.getSenderReturnMessage();
                        String message1 = null;
                        if ((messageLength > 0) && (message.length() > messageLength)) {
                            message1 = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.REQUEST_IN_QUEUE_UB, requestVO.getMessageArguments());
                            final PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1, requestVO.getRequestIDStr(), requestVO
                                .getRequestGatewayCode(), requestVO.getLocale());
                            pushMessage1.push();
                            requestVO.setRequestGatewayCode(reqruestGW);
                        }
                    } else {
                        message = "MESSAGE=" + URLEncoder.encode(requestVO.getSenderReturnMessage(), "UTF16") + "&TXN_ID=" + BTSLUtil.NullToString(requestVO
                            .getTransactionID()) + "&TXN_STATUS=" + BTSLUtil.NullToString(txn_status);
                    }
                } else {
                    message = requestVO.getSenderReturnMessage();
                   
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            // End of Rajdeep's code

            if (_log.isDebugEnabled()) {
                _log.debug(this, requestIDStr,
                    "Locale=" + requestVO.getLocale() + " requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO
                        .getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message);
            }
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
                .getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
                out.println(message);

                if (!BTSLUtil.isNullString(requestVO.getReqContentType())  && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO
                    .getReqContentType().indexOf("XML") != -1 || requestVO.getReqContentType().indexOf("plain") != -1 || requestVO.getReqContentType().indexOf("PLAIN") != -1) && requestVO.isSenderMessageRequired()) {
                    if (requestVO.isPushMessage() && !PretupsI.YES.equals(externalInterfaceAllowed)) {
                        final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());

                        PushMessage pushMessage = null;
                        pushMessage = new PushMessage(requestVO.getFilteredMSISDN(), senderMessage, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO
                            .getLocale());
                        // If changing is enable in system and external
                        // interface allowed is N then check the charging counts
                        // This change has been done by ankit on date 14/07/06
                        // for SMS charging
                        if (requestVO.getSenderVO() != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)).indexOf(requestVO
                            .getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(requestVO.getGroupType())) {
                            GroupTypeProfileVO groupTypeProfileVO = null;
                            // load the user running and profile counters
                            // Check the counters
                            // update the counters
                            groupTypeProfileVO = PretupsBL.loadAndCheckP2PGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
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
                                SMSChargingLog.log(((SenderVO) requestVO.getSenderVO()).getUserID(), (((SenderVO) requestVO.getSenderVO()).getUserChargeGrouptypeCounters())
                                    .getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(), groupTypeProfileVO.getResGatewayType(),
                                    groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(), requestVO.getModule());
                            } else {
								if(requestVO.getMessageCode() != null){
									pushMessage.push();
								}
                            }
                        } else {
							if(requestVO.getMessageCode() != null){
								pushMessage.push();
							}
                        }
                    }
                }

                // return message to sender
            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                // Will be removed in the future: For testing

                if (requestVO.isSenderMessageRequired()) {
                    PushMessage pushMessage = null;
                    pushMessage = new PushMessage(requestVO.getFilteredMSISDN(), message, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO
                        .getLocale());
                    // This change has been done by ankit on date 14/07/06 for
                    // SMS charging
                    if (requestVO.getSenderVO() != null && !PretupsI.YES.equals(externalInterfaceAllowed) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED))
                        .indexOf(requestVO.getRequestGatewayType()) != -1 && requestVO.isSuccessTxn() && !PretupsI.NOT_APPLICABLE.equals(requestVO.getGroupType())) {
                        GroupTypeProfileVO groupTypeProfileVO = null;
                        // load the user running and profile counters
                        // Check the counters
                        // update the counters
                        groupTypeProfileVO = PretupsBL.loadAndCheckP2PGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
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
                            SMSChargingLog.log(((SenderVO) requestVO.getSenderVO()).getUserID(), (((SenderVO) requestVO.getSenderVO()).getUserChargeGrouptypeCounters())
                                .getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(), groupTypeProfileVO.getResGatewayType(),
                                groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(), requestVO.getModule());
                        } else {
                            pushMessage.push();
                        }
                    } else {
                        pushMessage.push();
                    }
                }

            }

            out.flush();
            out.close();
            // Log the request in Request Logger
            P2PGatewayRequestLog.log(requestVO);
            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", requestIDStr, "Exiting");
            }
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
            ParserUtility.actionParser(p_requestVO);
            ParserUtility.generateResponse(p_requestVO.getActionValue(), p_requestVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PReceiver[prepareXMLResponse]", p_requestVO
                .getRequestIDStr(), "", "", "Exception while generating XML response:" + e.getMessage());
            try {
                ParserUtility.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PReceiver[prepareXMLResponse]", p_requestVO
                    .getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("prepareXMLResponse", p_requestVO.getRequestIDStr(), "Exiting with message=" + p_requestVO.getSenderReturnMessage());
        }
    }
}
