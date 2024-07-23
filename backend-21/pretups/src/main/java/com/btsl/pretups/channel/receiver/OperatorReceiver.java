package com.btsl.pretups.channel.receiver;

/*
 * @(#)OperatorReceiver.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ashish Kumar Todia 17/05/2012 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2012 Comviva technologies Ltd.
 */

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
import com.btsl.common.BTSLMessages;
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
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.services.businesslogic.SimProfileCache;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.util.SimUtil;
import com.btsl.pretups.channel.logging.ChannelGatewayRequestLog;
import com.btsl.pretups.channel.logging.TransactionPerSecLogger;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
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
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class OperatorReceiver extends HttpServlet {

	private static final Log _log = LogFactory.getLog(OperatorReceiver.class.getName());
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
    public void init() throws ServletException {
        // instanceCode paramter should be passed from the web.xml,
        // value should be same as the value in the the should be defined in
        // _instanceCode=getInitParameter("instanceCode");
        _instanceCode = Constants.getProperty("INSTANCE_ID");
        _log.info("init", "_instanceCode:" + _instanceCode);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ++_requestID;
        final String requestIDStr = String.valueOf(_requestID);
        if (_log.isDebugEnabled()) {
            _log.debug("doGet", requestIDStr, "Entered");
        }
        processRequest(requestIDStr, request, response, 1);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ++_requestID;
        final String requestIDStr = String.valueOf(_requestID);
        if (_log.isDebugEnabled()) {
            _log.debug("doPost", requestIDStr, "Entered");
        }
        processRequest(requestIDStr, request, response, 2);
    }

    /**
     * Method to validate the Service Type that is send by the user
     * 1) Checks if it is not REG or ADM and user is not registered then
     * deactivate the services
     * 2) Validates the Service ID and version corresponding to the type
     * requested
     * 3) Check if service type is allowed to the user or not
     * 
     * @param p_requestVO
     * @param p_serviceKeywordCacheVO
     * @param p_simProfileVO
     * @throws BTSLBaseException
     */
    private void validateServiceType(RequestVO p_requestVO, ServiceKeywordCacheVO p_serviceKeywordCacheVO, SimProfileVO p_simProfileVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateServiceType";
        final String serviceType = p_requestVO.getServiceType();
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(p_requestVO.getRequestIDStr());
        	loggerValue.append(" MSISDN=");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
        	loggerValue.append(" Service Type=");
        	loggerValue.append(serviceType);
            _log.debug("validateServiceType", loggerValue);
        }
        String c2sUserRegistrationRequired = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_USER_REGISTRATION_REQUIRED);
        
        try {
            // Do only in case of STK only as byte code is only for Applet
            if (p_requestVO.getSourceType().equals(PretupsI.REQUEST_SOURCE_TYPE_STK)) {
                if (!(PretupsI.KEYWORD_TYPE_REGISTRATION.equalsIgnoreCase(serviceType) || PretupsI.KEYWORD_TYPE_ADMIN.equalsIgnoreCase(serviceType))) {
                    // Get from Preferences USER_REGISTRATION_REQUIRED
                    if (!(channelUserVO.getUserPhoneVO()).isRegistered() && PretupsI.YES.equals(c2sUserRegistrationRequired)) {
                        // send is disabled if STK registration is not required
                        // this means STK is not supporting full OTA
                        if ("Y".equalsIgnoreCase(Constants.getProperty("STK_REGISTRATION_REQUIRED"))) {
                            SimUtil.deactivateAllServices(p_requestVO.getFilteredMSISDN(), (channelUserVO.getUserPhoneVO()).getEncryptDecryptKey(), p_simProfileVO);
                        }
                        loggerValue .setLength(0);
                        loggerValue.append( " MSISDN=");
                        loggerValue.append(p_requestVO.getFilteredMSISDN());
                        loggerValue.append(" User sim not registered but able to send request");	
                        _log.error("validateServiceType", p_requestVO.getRequestIDStr(),loggerValue);
                        loggerValue .setLength(0);
                        loggerValue.append( " User sim not registered for number :");
                        loggerValue.append( p_requestVO.getFilteredMSISDN());
                        loggerValue.append (" but able to send request");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[parseBinaryMessage]", p_requestVO
                            .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",loggerValue.toString() );
                        throw new BTSLBaseException("OperatorReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_NOTREG_BUTSENDREQ);
                    }// end if
                }// end if
            }// end if
            if (channelUserVO.isUsingNewSTK()) {
                PretupsBL.validateServiceTypeIdandVersion(p_requestVO, p_serviceKeywordCacheVO);
            }
            // check whether the service type is allowed to the user or not,
            // check only for external interface services
            // if(PretupsI.YES.equals(p_serviceKeywordCacheVO.getExternalInterface()))
            // // As discussed with Shishupal.
            if (PretupsI.OPT_MODULE.equalsIgnoreCase(p_requestVO.getModule()) && PretupsI.NO.equals(p_serviceKeywordCacheVO.getExternalInterface())) {
                final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType, channelUserVO.getAssociatedServiceTypeList());
                if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                	loggerValue.setLength(0);
                	loggerValue.append(" MSISDN=");
                	loggerValue.append(p_requestVO.getFilteredMSISDN());
                	loggerValue.append(" Service Type not found in allowed List");
                    _log.error("validateServiceType", p_requestVO.getRequestIDStr(),  loggerValue );
                    throw new BTSLBaseException("OperatorReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                } else if (!PretupsI.YES.equals(listValueVO.getLabel())) {
                	loggerValue.setLength(0);
                	loggerValue.append(" MSISDN=");
                	loggerValue.append(p_requestVO.getFilteredMSISDN());
                	loggerValue.append(" Service Type is suspended in allowed List");
                					
                    _log.error("validateServiceType", p_requestVO.getRequestIDStr(),loggerValue);
                    throw new BTSLBaseException("OperatorReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED);
                }// end if
            }// end if
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            loggerValue.setLength(0);
        	loggerValue.append(" MSISDN=");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
        	loggerValue.append(" Base Exception :");
        	loggerValue.append(be.getMessage());
            _log.error("validateServiceType", p_requestVO.getRequestIDStr(),  loggerValue );
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append(" MSISDN=");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
        	loggerValue.append(" Exception :");
        	loggerValue.append(e.getMessage());
            _log.error("validateServiceType", p_requestVO.getRequestIDStr(), loggerValue);
            loggerValue.setLength(0);
        	loggerValue.append("Not able to check validate Service Type is in allowed List for Request ID:");
        	loggerValue.append(p_requestVO.getRequestID());
        	loggerValue.append(" and MSISDN:");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
        	loggerValue.append(" ,getting Exception=" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkServiceTypeAllowed]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", loggerValue.toString() );
            throw new BTSLBaseException("OperatorReceiver", "validateServiceType", PretupsErrorCodesI.C2S_SERVICE_TYPE_VALIDATION_ERROR);
        }
    }

    /**
     * Method that will populate the language to be used while sending the
     * response
     * 
     * @param p_requestVO
     */
    private void populateLanguageSettings(RequestVO p_requestVO, ChannelUserVO channelUserVO) {
        final FixedInformationVO fixedInformationVO = (FixedInformationVO) p_requestVO.getFixedInformationVO();
        if (p_requestVO.getLocale() == null) {
            p_requestVO.setLocale(new Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (channelUserVO.getUserPhoneVO()).getCountry()));
        }
        if (fixedInformationVO != null) {
            PretupsBL.getCurrentLocale(p_requestVO, channelUserVO.getUserPhoneVO());
        }
    }

    /**
     * Method that will update the SIM parameters for the sender
     * 
     * @param p_requestVO
     * @param p_userPhoneVO
     * @param p_networkCode
     */
    private void updateSimParameters(RequestVO p_requestVO, UserPhoneVO p_userPhoneVO, String p_networkCode) {
        final String METHOD_NAME = "updateSimParameters";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Request ID=");
        	loggerValue.append( p_requestVO.getRequestID());
        	loggerValue.append(" Update Msisdn =");
        	loggerValue.append( p_userPhoneVO.getMsisdn());
        	loggerValue.append(" in case of New SIM and if parameters are not matching");
            _log.debug("updateSimParameters",loggerValue );
        }
        final SimUtil simUtil = new SimUtil();
        try {
            simUtil.updateParametersAndSendSMS(p_networkCode, p_userPhoneVO.getMsisdn(), ByteCodeGeneratorI.UPDATE_PARAMETER_PIN, p_userPhoneVO.getPinRequired(),
                p_userPhoneVO.getEncryptDecryptKey(), PretupsI.REQUEST_SOURCE_TYPE_SMS);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append("Not able to send Adm messages for Request ID:");
            loggerValue.append(p_requestVO.getRequestID());
            loggerValue.append(" and MSISDN:");
            loggerValue.append(p_requestVO.getFilteredMSISDN());
            loggerValue.append(" ,getting Exception=");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "OperatorReceiver[updateSimParameters]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",loggerValue.toString());
            _log.error("updateSimParameters", "Request ID=" + p_requestVO.getRequestID() + " Exception in sending adm messages::" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Request ID=");
        	loggerValue.append(p_requestVO.getRequestID());
        	loggerValue.append( " Exiting ");
            _log.debug("updateSimParameters",  loggerValue);
        }
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
        final String METHOD_NAME = "parseRequest";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered for p_requestID=");
        	loggerValue.append(p_requestID);
        	loggerValue.append(" p_request.Header:Authorization=");
        	loggerValue.append(p_request.getHeader("Authorization"));
            _log.debug("parseRequest",  loggerValue );
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
        String param1 = null;
        if (BTSLUtil.isNullString(p_request.getHeader("Authorization"))) {
            requestGatewayCode = p_request.getParameter("REQUEST_GATEWAY_CODE");
            requestGatewayType = p_request.getParameter("REQUEST_GATEWAY_TYPE");

            servicePort = p_request.getParameter("SERVICE_PORT");
            login = p_request.getParameter("LOGIN");
            password = p_request.getParameter("PASSWORD");
            udh = p_request.getParameter("UDH");
            sourceType = p_request.getParameter("SOURCE_TYPE");
            // iccid will be in request for DP6
            param1 = p_request.getParameter("ICD");
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
                throw new BTSLBaseException("OperatorReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_INVALID_AUTH_PARAMETER);
            }
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" p_requestFromrequestGatewayCode:");
        	loggerValue.append(requestGatewayCode);
        	loggerValue.append(", requestGatewayType:");
        	loggerValue.append(requestGatewayType);
        	loggerValue.append(", servicePort:");
        	loggerValue.append(servicePort);
        	loggerValue.append(udh);
        	loggerValue.append( sourceType );
        	loggerValue.append(BTSLUtil.maskParam(login) );
        	loggerValue.append(", password:" );
        	loggerValue.append(BTSLUtil.maskParam(password));
        	loggerValue.append(", param1:");
        	loggerValue.append(param1);
            _log.debug("parseRequest", p_requestID,loggerValue);
        }

        if (BTSLUtil.isNullString(requestGatewayCode)) {
            throw new BTSLBaseException("OperatorReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
        } else {
            requestGatewayCode = requestGatewayCode.trim();
        }
        p_requestVO.setRequestGatewayCode(requestGatewayCode);
        if (BTSLUtil.isNullString(requestGatewayType)) {
            throw new BTSLBaseException("OperatorReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
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
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting for p_requestID=");
        	loggerValue.append(p_requestID);
            _log.debug("parseRequest",  loggerValue);
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
     * 9. Load the operator User Details
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
        StringBuilder loggerValue= new StringBuilder(); 
        PrintWriter out = null;
        final RequestVO requestVO = new RequestVO();
        String message = null;
        Connection con = null;MComConnectionI mcomCon = null;
        NetworkPrefixVO networkPrefixVO = null;
        ChannelUserVO channelUserVO = null;
        final Date currentDate = new Date();
        SimProfileVO simProfileVO = null;
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
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

        try {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("************Start Time***********=");
            	loggerValue.append(requestStartTime);
                _log.debug("processRequest", requestIDStr,  loggerValue );
            }
            out = response.getWriter();
            requestIDMethod = Long.parseLong(requestIDStr);
            requestVO.setReqContentType(request.getContentType());
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "Content Type: ");
            	loggerValue.append( request.getContentType());
            	
                _log.debug("processRequest", loggerValue);
            }
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.OPT_MODULE);
            requestVO.setInstanceID(_instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setRequestStartTime(requestStartTime);
			if(!PretupsI.NO.equalsIgnoreCase(Constants.getProperty("IS_TPS_LOGS_REQUIRED"))){
					transactionPerSecLogging(requestStartTime,requestVO);
			}
            // parse Request method is used to parse the request that is sent to
            // it
            // here information about the message gateway , source type ,login
            // id ,password and udh is fetched from the request.
            parseRequest(requestIDStr, request, requestVO);

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

            ChannelGatewayRequestLog.inLog(requestVO);

            gatewayParsersObj.parseOperatorRequestMessage(requestVO);

            // validate Basic Validation on Input

            gatewayParsersObj.validateUserIdentification(requestVO);

            filteredMSISDN = requestVO.getFilteredMSISDN();

            // This block will check the status of message gateway
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
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

            // this code not used for unbarred user automatically (chage by
            // santanu as for mali implementation)
            // step1 :load user details to find category wise
            // Step 2:call new method unBarredUserAutomaic from PretupsBL.

            // Validate and load Sender Details
            channelUserVO = gatewayParsersObj.loadValidateUserDetails(con, requestVO);

            // 8. automatted unbarred pin type after expiry period of the sender
            // in barred list
            if (requestVO.getMessageGatewayVO().getAccessFrom() == null || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                // used for unbarred user automatically after the expiry period.
                PretupsBL.unBarredUserAutomaic(con, channelUserVO.getUserPhoneVO().getMsisdn(), networkPrefixVO.getNetworkCode(), PretupsI.C2S_MODULE,
                    PretupsI.USER_TYPE_SENDER, channelUserVO);
            }

            channelUserVO.setModifiedOn(currentDate);
            final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
            userPhoneVO.setModifiedBy(channelUserVO.getUserID());
            userPhoneVO.setModifiedOn(currentDate);

            /*
             * access on flag is used to get that first time user is accessing
             * the system or not.
             * if the user first time accessing the system then its value is
             * true , otherwise false.
             * Last access on field is used to set the date and time
             * information, when user is accessin the system through STK.
             */

            if (userPhoneVO.getLastAccessOn() == null) {
                // first time user is
                // accessing the system.
                userPhoneVO.setAccessOn(true);
            }
            if (requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                userPhoneVO.setLastAccessOn(currentDate);
                // Get the activated Ones only else show respective error if
                // suspended
            }

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Sender Locale in Request if any");
            	loggerValue.append(requestVO.getSenderLocale());
                _log.debug("processRequest", requestIDStr,  loggerValue );
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            } else {
                requestVO.setSenderLocale(requestVO.getLocale());
            }

            // check for previous request under process for channel User
            try {
                gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, true, channelUserVO);
                isMarkedUnderprocess = true;
            } catch (BTSLBaseException e1) {
                _log.errorTrace(METHOD_NAME, e1);
                throw e1;
            } finally {
                con.commit();
            }
            if (!requestVO.isMessageAlreadyParsed()) {
                PretupsBL.isPlainMessageAndAllowed(requestVO);

                // Check whether the message is Plain or in Binary, and if plain
                // then also allowed
                if (!requestVO.isPlainMessage()) {
                    // Check the encryption level to be used and get the key
                    // appropriately

                    // Load the pos key of the msisdn and check whether
                    // User is not registered on the server but able to send sms
                    // request from STK, then deactivating all have to be done
                    // After decrypting the message
                    // Required SIM Profile VO that will be get from the Cache
                    PretupsBL.getEncryptionKeyForUser(con, requestVO);
                    // Get from Cache the Profile Info based on the Profile ID
                    simProfileVO = (SimProfileVO) SimProfileCache.getSimProfileDetails(userPhoneVO.getSimProfileID());
                    PretupsBL.parseBinaryMessage(requestVO, simProfileVO);
                    // SIM Profile ID is not set
                }
            }
            // Close the connection as it is not required further
            if(mcomCon != null){mcomCon.close("OperatorReceiver#processRequest");mcomCon=null;}
            MessageFormater.handleChannelMessageFormat(requestVO, channelUserVO);

            if (channelUserVO.isUpdateSimRequired() && requestVO.getSourceType().equals(PretupsI.REQUEST_SOURCE_TYPE_STK)) {
            	loggerValue.setLength(0);
            	loggerValue.append("Update the Sim parameters for the MSISDN=");
            	loggerValue.append(requestVO.getFilteredMSISDN());
            	loggerValue.append(" as STK and Server Parameters like PIN required not matching");
                _log.error("processRequest", requestIDStr,loggerValue );
                updateSimParameters(requestVO, channelUserVO.getUserPhoneVO(), networkPrefixVO.getNetworkCode());
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_UPDATE_SIM_PARAMS_REQD);
            }

            // 19. The Temp Transaction ID checks will be done so that same one
            // is not repeated
            PretupsBL.validateTempTransactionID(requestVO, channelUserVO);

            PretupsBL.processFixedInfo(requestVO, channelUserVO);

            populateLanguageSettings(requestVO, channelUserVO);

            if (!(channelUserVO.getUserPhoneVO()).getPhoneLanguage().equalsIgnoreCase(requestVO.getLocale().getLanguage())) {
            	loggerValue.setLength(0);
            	loggerValue.append(" Changing the Language code for MSISDN=");
            	loggerValue.append(requestVO.getFilteredMSISDN());
            	loggerValue.append(	" From Language=");	
            	loggerValue.append((channelUserVO.getUserPhoneVO()).getPhoneLanguage());
            	loggerValue.append(" to language=" );
            	loggerValue.append(requestVO.getLocale().getLanguage());
                _log.error("processRequest",requestVO.getRequestIDStr(),loggerValue);
                (channelUserVO.getUserPhoneVO()).setPhoneLanguage(requestVO.getLocale().getLanguage());
                (channelUserVO.getUserPhoneVO()).setCountry(requestVO.getLocale().getCountry());
            }

            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "OperatorReceiver[processRequest]", requestIDStr,
                    filteredMSISDN, "", "Service keyword not found for the keyword=" + requestVO.getRequestMessageArray()[0] + " For Gateway Type=" + requestVO
                        .getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException("OperatorReceiver", "processRequest", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "OperatorReceiver[processRequest]", requestIDStr,
                    filteredMSISDN, "", "Service keyword suspended for the keyword=" + requestVO.getRequestMessageArray()[0] + " For Gateway Type=" + requestVO
                        .getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException("OperatorReceiver", "processRequest", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());

            // added to store request type in channelUserVO for checking while
            // validating pin
            channelUserVO.setServiceTypes(requestVO.getServiceType());
            // 18. Check the service is applicable to the user from
            // user_services table
            validateServiceType(requestVO, serviceKeywordCacheVO, simProfileVO, channelUserVO);
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            requestVO.setSuccessTxn(false);
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("BTSLBaseException be:" );
            	loggerValue.append(be.getMessage());
                _log.debug("processRequest", requestIDStr, loggerValue );
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
            loggerValue.setLength(0);
            loggerValue.append("Exception e:");
            loggerValue.append(e.getMessage());
            _log.error("processRequest", requestIDStr,  loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            loggerValue.setLength(0);
            loggerValue.append("Exception in OperatorReceiver:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorReceiver[processRequest]", requestIDStr, "",
                "",  loggerValue.toString() );
        } finally {

            // set the sender return message if it is not null
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }

            try {
                if (con == null && (channelUserVO != null || "Y".equals(Constants.getProperty("LOAD_TEST")))) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                loggerValue.setLength(0);
                loggerValue.append("Exception in OperatorReceiver while getting connection :");
                loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorReceiver[processrequest]", requestIDStr,
                    "", "",  loggerValue.toString());
            }

            // Forward to handler class to get the request message
            if (gatewayParsersObj == null) {
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    loggerValue.setLength(0);
                    loggerValue.append("Exception in OperatorReceiver while getting gatewayParsersObj :");
                    loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorReceiver[processrequest]",
                        requestIDStr, "", "",  loggerValue.toString() );
                }
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("gatewayParsersObj=");
            	loggerValue.append( gatewayParsersObj);
                _log.debug(this, requestIDStr, loggerValue);
            }

            if (channelUserVO != null && isMarkedUnderprocess && con != null) {
                try {
                    // If need to bar the user for PIN Change
                    if (channelUserVO.getUserPhoneVO().isBarUserForInvalidPin()) {
                        ChannelUserBL.barSenderMSISDN(con, channelUserVO, PretupsI.BARRED_TYPE_PIN_INVALID, currentDate, PretupsI.C2S_MODULE);
                        con.commit();
						Locale locale = new Locale(defaultLanguage, defaultCountry);
						PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale,channelUserVO.getNetworkCode());
						pushMessage.push();
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    loggerValue.setLength(0);
                	loggerValue.append("BTSLBaseException be:");
                	loggerValue.append(be.getMessage());
                    _log.error("processRequest", requestIDStr, loggerValue );
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
                    loggerValue.setLength(0);
                    loggerValue.append("Exception in OperatorReceiver while barring user becuase of invalid PIN counts:");
                    loggerValue.append(e.getMessage());
                    
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorReceiver[processrequest]",
                        requestIDStr, "", "",  loggerValue.toString() );
                }
                try {
                    // if request is under process the unmark the last
                    // transaction status as completed(C)
                    gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, false, channelUserVO);
					if(!con.isClosed())
                    con.commit();
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    loggerValue.setLength(0);
                    loggerValue.append("BTSLBaseException be:");
                    loggerValue.append(be.getMessage());
                    _log.error("processRequest", requestIDStr,  loggerValue );
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    try {
    					if(!con.isClosed())
                        con.rollback();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                    loggerValue.setLength(0);
                    loggerValue.append("Exception in OperatorReceiver while updating last status:");
                    loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorReceiver[processrequest]",
                        requestIDStr, "", "",  loggerValue.toString());
                }
            }
            if (con != null) {
                if (Constants.getProperty("LOAD_TEST") != null && "Y".equals(Constants.getProperty("LOAD_TEST"))) {
                    try {
                        // Done so that whatever the above transaction has done
                        // will be closed by the above code
                        // or else if above some exception is there it will be
                        // rollbacked
                        con.rollback();
                        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

                        if (!BTSLUtil.isNullString(requestVO.getIntMsisdnNotFound())) {
                            requestVO.setMessageCode(requestVO.getIntMsisdnNotFound());
                        }

                        channelTransferDAO.addC2SReceiverRequests(con, requestVO);
                        con.commit();
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                    }
                }

                if(mcomCon != null){mcomCon.close("OperatorReceiver#processRequest");mcomCon=null;}
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
            	loggerValue.setLength(0);
            	loggerValue.append( "requestEndTime=" );
            	loggerValue.append(requestEndTime);
            	loggerValue.append(" requestStartTime=");
            	loggerValue.append(requestStartTime);
            	loggerValue.append(" Message Code=" );
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append(" Args=");
            	loggerValue.append(requestVO.getMessageArguments());
            	loggerValue.append(" Message If any=");
            	loggerValue.append(message);
            	loggerValue.append(" Locale=");
            	loggerValue.append(	requestVO.getLocale());					
                _log.debug(this, requestIDStr,loggerValue);
            }

            if (requestVO.getMessageGatewayVO() != null) {
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Gateway Time out=");
                	loggerValue.append(requestVO.getMessageGatewayVO().getTimeoutValue());
                    _log.debug("processRequest", requestIDStr, loggerValue);
                }
            }

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
                        	loggerValue.setLength(0);
                        	loggerValue.append("Requested GW was:");
                        	loggerValue.append(requestVO.getRequestGatewayCode());
                            _log.debug("processRequest: Sender Message push through alternate GW", reqruestGW,  loggerValue );
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
                    } else // Message Encoding need not be requied only for WEB
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
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "Locale=" );
            	loggerValue.append(requestVO.getLocale());
            	loggerValue.append(" requestEndTime=");
            	loggerValue.append(requestEndTime);
            	loggerValue.append(" requestStartTime=" );
            	loggerValue.append(requestStartTime);
            	loggerValue.append(" Message Code=");
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append(" Args=" );
            	loggerValue.append(requestVO.getMessageArguments());
            	loggerValue.append(" Message If any=" );
            	loggerValue.append(message);
                _log.debug("processRequest", requestIDStr,loggerValue );
            }
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
                .getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
                out.println(message);
                // if the sender return message is required then send sms to
                // sender
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && requestVO.isSuccessTxn() && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO
                    .getReqContentType().indexOf("XML") != -1 || requestVO.getReqContentType().indexOf("plain") != -1 || requestVO.getReqContentType().indexOf("PLAIN") != -1) && requestVO
                    .isSenderMessageRequired()) {
                    if (!PretupsI.YES.equals(externalInterfaceAllowed)) {
                        final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
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
                _log.debug("processRequest", requestIDStr, "Exiting");
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
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered calledMethodType: ");
        	loggerValue.append(calledMethodType);
            _log.debug("parseRequestForMessage", requestID,  loggerValue);
        }
        String msisdn = request.getParameter("MSISDN");
        final String requestMessage = request.getParameter("MESSAGE");
        final String activeUserId = request.getParameter("ACTIVE_USER_ID");
        // request comes from the doGET
        if (calledMethodType == 1) {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "msisdn:");
            	loggerValue.append(msisdn);
            	loggerValue.append(" requestMessage:");
            	loggerValue.append(requestMessage);
                _log.debug("parseRequestForMessage", requestID,loggerValue);
            }
            p_requestVO.setRequestMessage(requestMessage);
            String loginId = null;
            if (PretupsI.GATEWAY_TYPE_SMS_POS.equals(p_requestVO.getRequestGatewayType())) {
                if (BTSLUtil.isNullString(requestMessage)) {
                    throw new BTSLBaseException("OperatorReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                }
                String chnlPlainSmsSeptLoginId = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID);
                final String[] array = requestMessage.split(chnlPlainSmsSeptLoginId);

                if (array.length == 2) {
                    loginId = array[1];
                }
            }

            if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(loginId)) {
                throw new BTSLBaseException("OperatorReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_MSISDN);
            }

            if (!BTSLUtil.isNullString(loginId)) {
                p_requestVO.setSenderLoginID(loginId);
            } else if (!BTSLUtil.isNullString(msisdn)) {
                msisdn = msisdn.trim();
                p_requestVO.setRequestMSISDN(msisdn);
            }
            if (!BTSLUtil.isNullString(msisdn)) {
                msisdn = msisdn.trim();
                p_requestVO.setPosMSISDN(msisdn);
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
                        throw new BTSLBaseException("OperatorReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                    }
                    p_requestVO.setRequestMessage(msg);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    throw be;
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("OperatorReceiver", "parseRequestForMessage", PretupsErrorCodesI.REQ_NOT_PROCESS);
                }

            }
            if (!BTSLUtil.isNullString(msisdn)) {
                p_requestVO.setRequestMSISDN(msisdn);
            }

            if (!BTSLUtil.isNullString(requestMessage)) {
                p_requestVO.setRequestMessage(requestMessage);
            }

            if (BTSLUtil.isNullString(p_requestVO.getRequestMessage())) {
                throw new BTSLBaseException("OperatorReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
            }
        }
        if (!BTSLUtil.isNullString(activeUserId)) {
            p_requestVO.setActiverUserId(activeUserId);
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting with Message=");
        	loggerValue.append(p_requestVO.getRequestMessage());
            _log.debug("parseRequestForMessage", requestID, loggerValue );
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorReceiver[prepareXMLResponse]", p_requestVO
                .getRequestIDStr(), "", "", "Exception while generating XML response:" + e.getMessage());
            try {
                ParserUtility.generateFailureResponse(p_requestVO);
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorReceiver[prepareXMLResponse]", p_requestVO
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
	public void transactionPerSecLogging(final long pMillisecondsTime , final RequestVO pRequestVO)
	{
	
	executor.execute(new TransactionPerSecLogger(pMillisecondsTime,pRequestVO));
	
			
	}
}
