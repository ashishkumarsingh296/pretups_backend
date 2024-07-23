package com.btsl.pretups.channel.receiver;

/*
 * @(#)C2SSubscriberReceiver.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ashish Todia 28/12/2010 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2010 Comviva Technologies Ltd.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class C2SSubscriberReceiver extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Log _log = LogFactory.getLog(C2SSubscriberReceiver.class.getName());
    private static String _instanceCode = null;
    private static long _requestID = 0;
    private static final String className="C2SSubscriberReceiver"; 
	private static ThreadPoolExecutor executor = null;
	
	static{
		
		try {
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE")).intValue()));
        } catch (Exception e) {
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
            //log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SSubscriberReceiver[thread pool initialize]", "", "", "", "Exception while initilizing the thread pool :" + e.getMessage());
        }
	}
	
    @Override
	public void init() throws ServletException {
        // instanceCode paramter should be passed from the web.xml,
        // value should be same as the value in the the should be defined in
        _instanceCode = Constants.getProperty("INSTANCE_ID");
        _log.info("init", "_instanceCode:" + _instanceCode);
    }

    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ++_requestID;
        final String requestIDStr = String.valueOf(_requestID);
        if (_log.isDebugEnabled()) {
            _log.debug("doGet", requestIDStr, "Entered");
        }
        processRequest(requestIDStr, request, response, 1);
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
     * This is used to prepare the request VO object from the parameters coming
     * in the request
     * 
     * @param p_requestID
     * @param p_request
     * @param p_requestVO
     * @throws BTSLBaseException
     */

    private void parseRequest(String p_requestID, HttpServletRequest p_request, RequestVO p_requestVO) throws BTSLBaseException {
    	final String METHOD_NAME="parseRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered for p_requestID" + p_requestID);
        }
        String requestGatewayCode = p_request.getParameter("REQUEST_GATEWAY_CODE");

        String requestGatewayType = p_request.getParameter("REQUEST_GATEWAY_TYPE");
        if (BTSLUtil.isNullString(p_requestVO.getReqContentType())) {
            p_requestVO.setReqContentType("PLAIN");
        }
        final String servicePort = p_request.getParameter("SERVICE_PORT");
        final String login = p_request.getParameter("LOGIN");
        final String password = p_request.getParameter("PASSWORD");
        final String udh = p_request.getParameter("UDH");
        final String sourceType = p_request.getParameter("SOURCE_TYPE");
        // iccid will be in request for DP6
        final String param1 = p_request.getParameter("ICD");

        if (_log.isDebugEnabled()) {
            _log.debug(
            		METHOD_NAME,
                p_requestID,
                "requestGatewayCode:" + requestGatewayCode + " requestGatewayType:" + requestGatewayType + " servicePort:" + servicePort + " udh:" + udh + " sourceType:" + sourceType + " login:" + login);
        }

        if (BTSLUtil.isNullString(requestGatewayCode)) {
            throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
        } else {
            requestGatewayCode = requestGatewayCode.trim();
        }
        p_requestVO.setRequestGatewayCode(requestGatewayCode);
        if (BTSLUtil.isNullString(requestGatewayType)) {
            throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
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
        p_requestVO.setParam1(param1);// for DP6
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting for p_requestID=" + p_requestID);
        }
    }

    /*
     * 1.Get the filtered MSISDN
     * 2 Check the Loads
     * a) Instance Load
     * 3. Parse the message and get the values from the request and store in the
     * requestVO
     * 4. Check for the request gateway , if not accepted then make entry in SMS
     * Excpetion Log (Unauthorised access)
     * 5. Get the Prefix ID of the sender MSISDN From the Cache
     * 6. Based on the Prefix ID get the location details
     * 7. Check for location status (Active or suspend)
     * b) Check the network Load allowed
     * 8. Check for the status of the sender whether that exist in the barred
     * list
     * 9. Load the channel User Details
     * Check the status of the user whether it is active or suspended
     * 10. Mark the request as Under process for that MSISDN, can we commit and
     * close the connection after this??
     * 11. Check For UDH_HEX (Plain or Binary SMS)
     * 12. Check if User Registration is Required or not
     * a) If the user is not registered but able to send request then Deactivate
     * all services on STK
     * 13. If udh == null || udh.length() < 1 then Plain SMS else Binary
     * 14. If Plain SMS then
     * a) Check if Plain SMS is allowed in the system or not
     * b) Parse the SMS
     * 15. If binary SMS then get the Encryption value to be used
     * a) Do we need to check for the blank Message
     * b) Get the SIM Profile INFO (To be stored In Cache to get the Also to be
     * used in Decrytion)
     * c) If User is there, then get the decrypt key from POS_KEYS table and
     * decrypt message
     * d) If Gloabl is there, then use the key stored in the Message gateway and
     * decrypt message
     * e) This has been done to check whether the Message has be decrypted
     * properly or not
     * only first 3 chars are checked in this case (Do we need to do this?)
     * f) Fixed Info related checks
     * 16. Message parsing to be done in separate method
     * a) Logic of default product
     * Cane we do PIN validation after message parsing (??)
     * 17. Get the service type from the keyword passed
     * 18. Check the service type is associated with the category who is sending
     * request (If there because user ID wise check is there)
     * 19. Check for the service ID and version supported for this service
     * 20. Check the service is applicable to the user from user_services table
     * 21. The Temp Transaction ID checks will be done so that same one is not
     * repeated, the same will then have
     * to be updated in User_phones table, also the language and Country will be
     * updated (Will be done
     * while unmarking the request)
     * 21. Pass control to process method of the class that handles it
     * 22. Update the last transaction status to complete of that msisdna and
     * update the language and the last transaction ID if there
     * 23. Push the message is service type is not ADM
     * 24. Registration Process handling and update of SMS Parameters, product
     * required etc
     * 
     * Where to fit STK setting does not match with the server setting for
     * number ????
     * Fixed INFO processing and new STK updation (Left)
     * First Keyword Check is not required
     * 
     * TO DO : Add Comments in Methods written
     */

    private void processRequest(String requestIDStr, HttpServletRequest request, HttpServletResponse response, int p_requestFrom) {
        final String METHOD_NAME = "processRequest";
        final String varName="C2SSubscriberReceiver[processRequest]";
        PrintWriter out = null;
        final RequestVO requestVO = new RequestVO();
        String message = null;
        Connection con = null;MComConnectionI mcomCon = null;
        NetworkPrefixVO networkPrefixVO = null;
        final Date currentDate = new Date();
        final long requestStartTime = System.currentTimeMillis();
        long requestEndTime = 0;
        String filteredMSISDN = null;
        GatewayParsersI gatewayParsersObj = null;
        long requestIDMethod = 0;
        String requestType = null;
        String networkID = null;
        String serviceType = null;
        final String externalInterfaceAllowed = null;

        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

        try {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr, "************Start Time***********=" + requestStartTime);
            }
            out = response.getWriter();
            requestIDMethod = Long.parseLong(requestIDStr);
            requestVO.setReqContentType(request.getContentType());
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Content Type: " + request.getContentType());
            }
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.C2S_MODULE);
            requestVO.setInstanceID(_instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setRequestStartTime(requestStartTime);
            HashMap mm = new HashMap();
			mm.put("HTTPREQUEST",request);
			mm.put("HTTPRESPONSE",response);
			requestVO.setRequestMap(mm);
			/*requestVO.setHttpRequest(request);
			requestVO.setHttpResponse(response);*/
			if(!PretupsI.NO.equalsIgnoreCase(Constants.getProperty("IS_TPS_LOGS_REQUIRED"))){
					transactionPerSecLogging(requestStartTime,requestVO);
			}
            // parse Request method is used to parse the request that is sent to
            // it
            // here information about the message gateway , source type ,login
            // id ,password and udh is fetched from the request.
            parseRequest(requestIDStr, request, requestVO);
            //Setting Default Content type as if not coming into the request for OBF
			if(BTSLUtil.isNullString(request.getContentType()) ||request.getContentType().equalsIgnoreCase("null")) {
				String requestedGatewayTypeCode=null;
				requestedGatewayTypeCode=Constants.getProperty("C2SSUBSCRIBER_DEFAULT_CONTENT_TYPE_GATEWAYTYPECODEWISE");
				if(_log.isDebugEnabled())_log.debug("processRequest","Set Default Content Type"+", requestedGatewayTypeCode = "+requestedGatewayTypeCode);
				if(BTSLUtil.isNullString(requestedGatewayTypeCode)) {
					requestedGatewayTypeCode="USSD:USSD:text/plain";
				}
				if(!BTSLUtil.isNullString(requestedGatewayTypeCode)) {
					String requestedGatewayType=requestedGatewayTypeCode.split(":")[0];
					String requestedGatewayCode=requestedGatewayTypeCode.split(":")[1];
					String defaultContentType=requestedGatewayTypeCode.split(":")[2];
					if(requestVO.getRequestGatewayType().equalsIgnoreCase(requestedGatewayType) && requestVO.getRequestGatewayCode().equalsIgnoreCase(requestedGatewayCode) ){
						requestVO.setReqContentType(defaultContentType);
					}
					if(_log.isDebugEnabled())_log.debug("processRequest","Default Content Type: "+requestVO.getReqContentType());					
				}				
			}
            // 3. Check for the request gateway
            PretupsBL.validateRequestMessageGateway(requestVO);
            requestType = requestVO.getMessageGatewayVO().getGatewayType();

            // uncomment the code in case gateway type are same for
            // SMSCPOSParser and SMSCParser
            // Forward to handler class to get the request message
            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
            // parseRequestForMessage is used to parse the sender msisdn and the
            // request message from the request.
            parseRequestForMessage(requestIDStr, request, requestVO, p_requestFrom);
            // Log the request in Request Logger
            ChannelGatewayRequestLog.inLog(requestVO);
            gatewayParsersObj.parseRequestMessage(requestVO);

            // validate Basic Validation on Input
            gatewayParsersObj.validateUserIdentification(requestVO);
            filteredMSISDN = PretupsBL.getFilteredMSISDN(requestVO.getRequestMSISDN());
            requestVO.setFilteredMSISDN(filteredMSISDN);

            // Done on 25/06/07 by Ankit Zindal as discussed with Sanjay/Gurjeet
            // This block will check the status of message gateway
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
            }

            // 2. Check the instance Load
            LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
            requestVO.setDecreaseLoadCounters(true);

            // Validate network details
            gatewayParsersObj.loadValidateNetworkDetails(requestVO);

            networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();

            networkID = networkPrefixVO.getNetworkCode();
            requestVO.setRequestNetworkCode(networkID);
            // Check Network Load : If true then pass the request else refuse
            // the request
            LoadController.checkNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);

            String requestHandlerClass;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr, "Sender Locale in Request if any" + requestVO.getSenderLocale());
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            } else {
                requestVO.setSenderLocale(requestVO.getLocale());
            }
            if (!requestVO.isMessageAlreadyParsed()) {
                PretupsBL.isPlainMessageAndAllowed(requestVO);
            }
            if(mcomCon != null){mcomCon.close("C2SSubscriberReceiver#processRequest");mcomCon=null;}
            handleChannelMessageFormat(requestVO);
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, varName,
                    requestIDStr, filteredMSISDN, "", "Service keyword not found for the keyword=" + requestVO.getRequestMessageArray()[0] + " For Gateway Type=" + requestVO
                        .getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, varName,
                    requestIDStr, filteredMSISDN, "", "Service keyword suspended for the keyword=" + requestVO.getRequestMessageArray()[0] + " For Gateway Type=" + requestVO
                        .getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            requestVO.setSuccessTxn(false);
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr, "BTSLBaseException be:" + be.getMessage());
            }
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                // construct Message
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            _log.error(METHOD_NAME, requestIDStr, "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, varName, requestIDStr,
                "", "", "Exception in C2SSubscriberReceiver:" + e.getMessage());
        } finally {

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
                _log.debug(this, requestIDStr, "gatewayParsersObj=" + gatewayParsersObj);
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

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseIntermediateCounters(_instanceCode, requestType, networkID, serviceType, requestIDStr,
                LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());

            if (_log.isDebugEnabled()) {
                _log.debug(this, requestIDStr,
                    "requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO.getMessageCode() + " Args=" + requestVO
                        .getMessageArguments() + " Message If any=" + message + " Locale=" + requestVO.getLocale());
            }

            if (requestVO.getMessageGatewayVO() != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, requestIDStr, "Gateway Time out=" + requestVO.getMessageGatewayVO().getTimeoutValue());
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
                            _log.debug("processRequest: Sender Message push through alternate GW", reqruestGW, "Requested GW was:" + requestVO.getRequestGatewayCode());
                        }
                    }
                }
                int messageLength = 0;
                final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                if (!BTSLUtil.isNullString(messLength)) {
                    messageLength = (new Integer(messLength)).intValue();
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("processRequest: requestVO.getReqContentType() =", requestVO.getReqContentType());
                }
                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf(
                    "XML") != -1)) && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType()) && !PretupsI.GATEWAY_TYPE_SMSC.equals(requestVO.getRequestGatewayType())) {
                    String txn_status = null;
                    if (requestVO.isSuccessTxn()) {
                        txn_status = PretupsI.TXN_STATUS_SUCCESS;
                    } else {
                        txn_status = requestVO.getMessageCode();
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("processRequest: txn_status =", txn_status);
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
                    } else {
                        message = "MESSAGE=" + URLEncoder.encode(requestVO.getSenderReturnMessage(), "UTF16") + "&TXN_ID=" + BTSLUtil.NullToString(requestVO
                            .getTransactionID()) + "&TXN_STATUS=" + BTSLUtil.NullToString(txn_status);
                    }
                } else {
                    message = requestVO.getSenderReturnMessage();
                    if((requestVO.getRequestGatewayType()).equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC)){
						if(_log.isDebugEnabled()) 
							_log.debug("processRequest: Logger put by message SMSC  for Message",message,"Requested GW was:"+requestVO.getRequestGatewayCode());
						
						if(!BTSLUtil.isNullString(message) && !requestVO.isSuccessTxn())
						{
							PushMessage pushMessage1=new PushMessage(requestVO.getFilteredMSISDN(),message,requestVO.getRequestIDStr(),requestVO.getRequestGatewayCode(),requestVO.getLocale());
							pushMessage1.push();
						 }
					}
                    /*
                     * if(PretupsI.SERVICE_TYPE_MVD.equalsIgnoreCase(requestVO.
                     * getServiceType()))
                     * {
                     * if(((ArrayList)requestVO.getValueObject()).size()>0)
                     * {
                     * VomsVoucherVO voucherVO=null;
                     * voucherVO=(VomsVoucherVO)((ArrayList)requestVO.getValueObject
                     * (
                     * )).get(0);
                     * message=message+"&SALE_BATCH_NO="+voucherVO.getSaleBatchNo
                     * ();
                     * }
                     * }
                     */
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr,
                    "Locale=" + requestVO.getLocale() + " requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO
                        .getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message+", requestVO.isSenderMessageRequired()="+requestVO.isSenderMessageRequired()+", requestVO.getMessageGatewayVO().getResponseType()="+requestVO.getMessageGatewayVO().getResponseType()+", requestVO.getReqContentType()="+requestVO.getReqContentType());
            }
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
                .getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
                if(_log.isDebugEnabled())_log.debug("processRequest","Before print call, out = "+out);
                out.println(message);
                out.flush();
				try{if(out !=null) out.close();}catch(Exception e){_log.errorTrace(METHOD_NAME,e);}
				if(_log.isDebugEnabled())_log.debug("processRequest","After print call, out = "+out);
				if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "externalInterfaceAllowed1 =",externalInterfaceAllowed);
                }
                if(requestVO.getLocale() == null) {
                	requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
                }
				// if the sender return message is required then send sms to
                // sender
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && requestVO.isSuccessTxn() && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO
                    .getReqContentType().indexOf("XML") != -1 || requestVO.getReqContentType().indexOf("plain") != -1 || requestVO.getReqContentType().indexOf("PLAIN") != -1) && requestVO
                    .isSenderMessageRequired()) {
                	if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "externalInterfaceAllowed Inside =",externalInterfaceAllowed);
                    }
                    if (!PretupsI.YES.equals(externalInterfaceAllowed)) {
                    	final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                        // PushMessage pushMessage=new
                        final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO
                            .getRequestGatewayCode(), requestVO.getLocale());
                        // If changing is enable in system and external
                        // interface allowed is N then check the charging counts
                        pushMessage.push();
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
            if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Before flus call, out = "+out);
            /*out.flush();
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }*/
            // Log the request out Request Logger
            ChannelGatewayRequestLog.outLog(requestVO);
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr, "Exiting");
            }
        }
    }

    /**
     * Parse parse Request For Message and retreives details
     * 
     * @param requestID
     * @param request
     * @param p_requestVO
     * @param calledMethodType
     * @return
     * @throws BTSLBaseException
     */
    public void parseRequestForMessage(String requestID, HttpServletRequest request, RequestVO p_requestVO, int calledMethodType) throws BTSLBaseException {
        final String METHOD_NAME = "parseRequestForMessage";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, requestID, "Entered calledMethodType: " + calledMethodType);
        }
        String msisdn = request.getParameter("MSISDN");
        final String requestMessage = request.getParameter("MESSAGE");
        final String activeUserId = request.getParameter("ACTIVE_USER_ID");
        // request comes from the doGET
        if (calledMethodType == 1) {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestID, "msisdn:" + msisdn + " requestMessage:" + requestMessage);
            }
            p_requestVO.setRequestMessage(requestMessage);
            String loginId = null;
            if (PretupsI.GATEWAY_TYPE_SMS_POS.equals(p_requestVO.getRequestGatewayType())) {
                if (BTSLUtil.isNullString(requestMessage)) {
                    throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                }
                String chnlPlainSmsSeptLoginId = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID);
                final String[] array = requestMessage.split(chnlPlainSmsSeptLoginId);

                if (array.length == 2) {
                    // Sir..(ashishT)
                    loginId = array[1];
                }
            }

            if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(loginId)) {
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_BLANK_MSISDN);
            }
            if (!BTSLUtil.isNullString(loginId)) {
                p_requestVO.setSenderLoginID(loginId);
            }
            if (!BTSLUtil.isNullString(msisdn)) {
                msisdn = msisdn.trim();
                p_requestVO.setPosMSISDN(msisdn);
                p_requestVO.setRequestMSISDN(msisdn);
            }
        }
        // request comes form doPost
        else {
            String msg = "";
            StringBuilder sb = new StringBuilder(1024);
            if (BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage)) {
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
                        throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                    }
                    p_requestVO.setRequestMessage(msg);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    throw be;
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.REQ_NOT_PROCESS);
                }

            }
            if (!BTSLUtil.isNullString(msisdn)) {
                p_requestVO.setRequestMSISDN(msisdn);
            }

            if (!BTSLUtil.isNullString(requestMessage)) {
                p_requestVO.setRequestMessage(requestMessage);
            }

            if (BTSLUtil.isNullString(p_requestVO.getRequestMessage())) {
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
            }
        }
        if (!BTSLUtil.isNullString(activeUserId)) {
            p_requestVO.setActiverUserId(activeUserId);
        }

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, requestID, "Exiting with Message=" + p_requestVO.getRequestMessage());
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
            _log.debug(METHOD_NAME, p_requestVO.getRequestIDStr(), "Entered");
        }
        try {
            ParserUtility.actionChannelParser(p_requestVO);
            ParserUtility.generateChannelResponse(p_requestVO.getActionValue(), p_requestVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SSubscriberReceiver[prepareXMLResponse]",
                p_requestVO.getRequestIDStr(), "", "", "Exception while generating XML response:" + e.getMessage());
            try {
                ParserUtility.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SSubscriberReceiver[prepareXMLResponse]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, p_requestVO.getRequestIDStr(), "Exiting with message=" + p_requestVO.getSenderReturnMessage());
        }
    }

    private void handleChannelMessageFormat(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "handleChannelMessageFormat";
        final String requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, requestID, "Entered MSISDN: " + p_requestVO.getFilteredMSISDN() + " With Message=" + p_requestVO.getDecryptedMessage());
        }
        String userMessage = p_requestVO.getDecryptedMessage();
        String tokenVal = null;
        final ArrayList aList = new ArrayList();
        String[] newMessageArray = null;
        final String mesg = "";
        String CHNL_MESSAGE_SEP = null;
        String chnlPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
        try {
            CHNL_MESSAGE_SEP = chnlPlainSmsSeparator;
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }

            userMessage = p_requestVO.getDecryptedMessage();
            if (!BTSLUtil.isNullString(userMessage)) {
                if (!p_requestVO.isPlainMessage()) {
                    final String[] messageArray = userMessage.split(" ");
                    final int messageArrayLength = messageArray.length;
                    for (int i = 0; i < messageArrayLength; i++) {
                        tokenVal = messageArray[i];

                        if (BTSLUtil.isNullString(tokenVal)) {
                            continue;
                        }
                        if (i == 0) {
                            aList.add(tokenVal);
                            if (_log.isDebugEnabled()) {
                                _log.debug(METHOD_NAME, requestID, " Position=" + i + " Token Value=" + tokenVal);
                            }
                            continue;
                        }// end if
                    }
                    newMessageArray = new String[aList.size()];
                    final int aListSize = aList.size();
                    for (int i = 0; i < aListSize; i++) {
                        newMessageArray[i] = (String) aList.get(i);
                    }
                } else {
                    final String[] messageArray = BTSLUtil.split(userMessage, CHNL_MESSAGE_SEP);
                    // Handle Plain SMS Parsing here
                    for (int i = 0; i < messageArray.length; i++) {
                        aList.add(messageArray[i]);
                    }
                    newMessageArray = new String[aList.size()];
                }
                final int aListSize = aList.size();
                for (int i = 0; i < aListSize; i++) {
                    newMessageArray[i] = (String) aList.get(i);
                }
                p_requestVO.setRequestMessageArray(newMessageArray);
                p_requestVO.setIncomingSmsStr(mesg);
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SSubscriberReceiver[handleTransferMessageFormat]",
                    requestID, p_requestVO.getFilteredMSISDN(), "", "Not able to translate the message request for Request ID:" + requestID + " and number:" + p_requestVO
                        .getFilteredMSISDN() + " ,getting Base Exception=" + PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE);
                throw new BTSLBaseException(className, "handleTransferMessageFormat", PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error(METHOD_NAME, requestID, "BTSLBaseException for Request ID:" + p_requestVO.getRequestID() + " Exception=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, requestID, " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SSubscriberReceiver[handleTransferMessageFormat]",
                requestID, p_requestVO.getFilteredMSISDN(), "", "Not able to translate the message request for Request ID:" + requestID + " and MSISDN:" + p_requestVO
                    .getFilteredMSISDN() + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(className, "handleTransferMessageFormat", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, requestID, "Exiting");
        }
    }
	
	/**
	 * @param pMillisecondsTime
	 * @param pRequestVO
	 */
	public void transactionPerSecLogging(final long pMillisecondsTime , final RequestVO pRequestVO)
	{
	
		executor.execute(new TransactionPerSecLogger(pMillisecondsTime,pRequestVO));
	
	}
}
