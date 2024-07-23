package com.selftopup.pretups.p2p.receiver.requesthandler;

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

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.loadcontroller.LoadController;
import com.selftopup.loadcontroller.LoadControllerI;
import com.selftopup.loadcontroller.ReqNetworkServiceLoadController;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.gateway.util.GatewayParsersI;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterVO;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.logging.P2PGatewayRequestLog;
import com.selftopup.pretups.p2p.query.businesslogic.SubscriberTransferDAO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.selftopup.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

/**
 * @author gaurav.pandey
 * 
 */

public class SelfTopUpReceiver extends HttpServlet {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static String _instanceCode = null;
    private static long _requestID = 0;
    public static OperatorUtilI calculatorI = null;

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    public void init() throws ServletException {
        // Put your code here
        _instanceCode = getInitParameter("instanceCode");
        _log.info("init", "instanceCode:" + _instanceCode);
        String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpReceiver[init]", "", "", "", "Exception while loading the operator util class at the init:" + e.getMessage());
        }
        _log.info("init", "calculatorI:" + calculatorI);
    }

    /**
     * Constructor of the object.
     */
    public SelfTopUpReceiver() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ++_requestID;
        String requestIDStr = String.valueOf(_requestID);
        if (_log.isDebugEnabled())
            _log.debug("doGet", requestIDStr, "Entered");
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ++_requestID;
        String requestIDStr = String.valueOf(_requestID);
        if (_log.isDebugEnabled())
            _log.debug("doPost", requestIDStr, "Entered");
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
        if (_log.isDebugEnabled())
            _log.debug("parseRequestForMessage", requestID, "Entered calledMethodType: " + calledMethodType);
        String msg = "";
        if (calledMethodType == 1) {
            String msisdn = request.getParameter("MSISDN");
            String imei = request.getParameter("IMEI");
            String emailId = request.getParameter("EMAILID");
            String userLoginid = request.getParameter("USERLOGINID");
            String requestMessage = request.getParameter("MESSAGE");

            if (_log.isDebugEnabled())
                _log.debug("parseRequestForMessage", requestID, "msisdn:" + msisdn + " requestMessage:" + requestMessage);
            if (BTSLUtil.isNullString(msisdn)) {
                throw new BTSLBaseException("SelfTopUpReceiver", "parseRequestForMessage", SelfTopUpErrorCodesI.P2P_ERROR_BLANK_MSISDN);
            } else
                msisdn = msisdn.trim();

            if (BTSLUtil.isNullString(imei)) {
                p_requestVO.setImei(PretupsI.DEFAULT_P2P_WEB_IMEI);
            } else {
                p_requestVO.setImei(imei);
            }
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setEmailId(emailId);
            p_requestVO.setUserLoginId(userLoginid);

            if (BTSLUtil.isNullString(requestMessage)) {
                throw new BTSLBaseException("SelfTopUpReceiver", "parseRequestForMessage", SelfTopUpErrorCodesI.P2P_ERROR_BLANK_REQUESTMESSAGE);
            }
            p_requestVO.setRequestMessage(requestMessage);
        } else {
            String msisdn = request.getParameter("MSISDN");
            String requestMessage = request.getParameter("MESSAGE");

            if (BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage)) {
                if (PretupsI.FLARES_CONTENT_TYPE.equals(request.getContentType())) {
                    StringBuffer requestBuffer = new StringBuffer();
                    String tempElement = null;
                    Enumeration requestEnum = request.getParameterNames();
                    while (requestEnum.hasMoreElements()) {
                        tempElement = (String) requestEnum.nextElement();
                        requestBuffer.append(tempElement + "=" + request.getParameter(tempElement) + "&");
                        if (tempElement.equals("TYPE"))
                            p_requestVO.setServiceKeyword(request.getParameter(tempElement));
                    }
                    p_requestVO.setRequestMessage(requestBuffer.toString());
                    p_requestVO.setReqContentType("plain");

                } else {
                    try {
                        ServletInputStream in = request.getInputStream();
                        int c = 0;

                        while ((c = in.read()) != -1) {
                            // Process line...
                            msg += (char) c;
                        }
                        if (BTSLUtil.isNullString(msg))
                            throw new BTSLBaseException("SelfTopUpReceiver", "parseRequestForMessage", SelfTopUpErrorCodesI.P2P_ERROR_BLANK_REQUESTMESSAGE);
                        p_requestVO.setRequestMessage(msg);
                    } catch (Exception e) {
                        throw new BTSLBaseException("SelfTopUpReceiver", "parseRequestForMessage", SelfTopUpErrorCodesI.REQ_NOT_PROCESS);
                    }
                }
            }
            if (!BTSLUtil.isNullString(msisdn))
                p_requestVO.setRequestMSISDN(msisdn);

            if (!BTSLUtil.isNullString(requestMessage))
                p_requestVO.setRequestMessage(requestMessage);

            if (BTSLUtil.isNullString(p_requestVO.getRequestMessage()))
                throw new BTSLBaseException("SelfTopUpReceiver", "parseRequestForMessage", SelfTopUpErrorCodesI.P2P_ERROR_BLANK_REQUESTMESSAGE);
        }
        int index = msg.indexOf("CARDNO=");
        if (index != -1 && _log.isDebugEnabled()) {
            _log.debug("parseRequestForMessage", requestID, "Exiting with Message=" + msg.substring(0, index + "CARDNO=".length()) + "************" + msg.substring(index + "CARDNO=".length() + 12));
        } else if (_log.isDebugEnabled()) {
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
    public void parseRequest(String requestID, HttpServletRequest request, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("parseRequest", requestID, "Entered");

        String requestGatewayCode = request.getParameter("REQUEST_GATEWAY_CODE");
        String requestGatewayType = request.getParameter("REQUEST_GATEWAY_TYPE");
        String servicePort = request.getParameter("SERVICE_PORT");
        String login = request.getParameter("LOGIN");
        String password = request.getParameter("PASSWORD");
        String sourceType = request.getParameter("SOURCE_TYPE");

        if (_log.isDebugEnabled())
            _log.debug("parseRequest", requestID, "requestGatewayCode: " + requestGatewayCode + " requestGatewayType: " + requestGatewayType + " servicePort: " + servicePort + " login: " + login + " password: " + password + " sourceType: " + sourceType);
        if (BTSLUtil.isNullString(requestGatewayCode)) {
            throw new BTSLBaseException("SelfTopUpReceiver", "parseRequest", SelfTopUpErrorCodesI.P2P_ERROR_BLANK_REQUESTINTID);
        } else
            requestGatewayCode = requestGatewayCode.trim();
        p_requestVO.setRequestGatewayCode(requestGatewayCode);

        if (BTSLUtil.isNullString(requestGatewayType)) {
            throw new BTSLBaseException("SelfTopUpReceiver", "parseRequest", SelfTopUpErrorCodesI.P2P_ERROR_BLANK_REQUESTINTTYPE);
        } else
            requestGatewayType = requestGatewayType.trim();
        p_requestVO.setRequestGatewayType(requestGatewayType);

        p_requestVO.setLogin(login);
        p_requestVO.setPassword(password);
        p_requestVO.setServicePort(servicePort);
        p_requestVO.setRemoteIP(request.getRemoteAddr());
        p_requestVO.setSourceType(sourceType);

        if (_log.isDebugEnabled())
            _log.debug("parseRequest", requestID, "Exiting");
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
        response.setContentType("text/html");
        PrintWriter out = null;
        BTSLMessages messages = null;
        String message = null;
        Connection con = null;
        SenderVO senderVO = null;
        Date currentDate = new Date();
        NetworkPrefixVO networkPrefixVO = null;
        RequestVO requestVO = new RequestVO();
        long requestStartTime = System.currentTimeMillis();
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
            if (_log.isDebugEnabled())
                _log.debug("processRequest", requestIDStr, "************Start Time***********=" + requestStartTime);
            out = response.getWriter();
            requestIDMethod = Long.parseLong(requestIDStr);
            requestVO.setReqContentType(request.getContentType());
            if (_log.isDebugEnabled())
                _log.debug("processRequest", "Content Type: " + request.getContentType());
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.P2P_MODULE);
            requestVO.setInstanceID(_instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SelfTopUpReceiver[processRequest]", requestIDStr, filteredMSISDN, "", "Sender MSISDN Not valid");
                requestVO.setSenderMessageRequired(false);
                throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN);
            }

            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus()))
                throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus()))
                throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);

            LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
            requestVO.setDecreaseLoadCounters(true);

            if (requestVO.getReceiverLocale() == null)
                requestVO.setReceiverLocale(requestVO.getLocale());

            // load network details
            networkPrefixVO = PretupsBL.getNetworkDetails(filteredMSISDN, PretupsI.USER_TYPE_SENDER);

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
                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
                    message = networkPrefixVO.getLanguage1Message();
                else
                    message = networkPrefixVO.getLanguage2Message();
                requestVO.setSenderReturnMessage(message);
                throw new BTSLBaseException(this, "processRequest", SelfTopUpErrorCodesI.P2P_NETWORK_NOT_ACTIVE);
            }

            // check network load
            String requestHandlerClass;
            con = OracleUtil.getConnection();
            // check black list status of the sender
            /*
             * if(SystemPreferences.CHK_BLK_LST_STAT)
             * RestrictedSubscriberBL.isSubscriberBlacklisted(con,requestVO.
             * getFilteredMSISDN());
             */
            // this code kill for un barreduser after barred Expiry period.
            /*
             * //check MSISDN barred
             * if(requestVO.getMessageGatewayVO().getAccessFrom()==null ||
             * requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.
             * ACCESS_FROM_PHONE))
             * PretupsBL.checkMSISDNBarred(con,filteredMSISDN,networkPrefixVO.
             * getNetworkCode(),PretupsI.P2P_MODULE,PretupsI.USER_TYPE_SENDER);
             */

            // check MSISDN barred and un barreduser after barred Expiry period.
            // modify for mali.Check and automated un barred pin type in the
            // system
            if (requestVO.getMessageGatewayVO().getAccessFrom() == null || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE))
                // chenel vo passed as null as p2p has no categoty.
                PretupsBL.unBarredUserAutomaic(con, filteredMSISDN, networkPrefixVO.getNetworkCode(), PretupsI.P2P_MODULE, PretupsI.USER_TYPE_SENDER, null);

            // load subscriber details
            senderVO = SubscriberBL.validateSubscriberDetails(con, filteredMSISDN);
            if (senderVO != null) {
                // senderVO.setPrefixID(networkPrefixVO.getPrefixID());
                senderVO.setModifiedBy(senderVO.getUserID());
                // senderVO.setModifiedOn(currentDate);
                senderVO.setModule(PretupsI.P2P_MODULE);
                requestVO.setSenderVO(senderVO);
                requestVO.setLocale(senderVO.getLocale());
                String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMSISDN);
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
                /*
                 * try{con.close();}catch(Exception e){}
                 * con=null;
                 */
            }

            con.close();
            con = null;

            if (_log.isDebugEnabled())
                _log.debug("processRequest", requestIDStr, "Sender Locale in Request if any" + requestVO.getSenderLocale());

            if (requestVO.getSenderLocale() != null)
                requestVO.setLocale(requestVO.getSenderLocale());
            else
                requestVO.setSenderLocale(requestVO.getLocale());

            // message encryption check
            // parse message
            String[] messageArray = PretupsBL.parsePlainMessage(requestVO.getDecryptedMessage());
            requestVO.setRequestMessageArray(messageArray);
            // load service details, CR 000009 Sub Keyword Based Service Type
            // identification
            ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
                // return with error message
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SelfTopUpReceiver[processRequest]", requestIDStr, filteredMSISDN, "", "Service keyword not found for the keyword=" + messageArray[0] + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException("SelfTopUpReceiver", "processRequest", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SelfTopUpReceiver[processRequest]", requestIDStr, filteredMSISDN, "", "Service keyword suspended for the keyword=" + messageArray[0] + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException("SelfTopUpReceiver", "processRequest", SelfTopUpErrorCodesI.P2P_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            if (senderVO == null) {
                // if service type is not reqisteration
                // if(!
                // (serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_REGISTERATION)
                // ||
                // serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_BARRED)
                // ))
                if (serviceKeywordCacheVO.getUnregisteredAccessAllowed().equals(PretupsI.NO))
                    throw new BTSLBaseException("SelfTopUpReceiver", "processRequest", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SUBSCRIBER);
            } else if ((serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PSUSPEND)) && senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException("SelfTopUpReceiver", "processRequest", SelfTopUpErrorCodesI.P2P_USER_STATUS_ALREADY_SUSPENDED);
            } else if ((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_RESUMESERVICE)) && senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException("SelfTopUpReceiver", "processRequest", SelfTopUpErrorCodesI.P2P_ERROR_SENDER_SUSPEND);
            } else if ((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.MULT_CRE_TRA_DED_ACC)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PRECHARGE)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_ACCOUNTINFO)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PCHANGEPIN)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_REGISTERATION)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PRECHARGEWITHVALEXT)) && senderVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW)) {
                throw new BTSLBaseException("SelfTopUpReceiver", "processRequest", SelfTopUpErrorCodesI.P2P_ERROR_SENDER_STATUS_NEW);
            }
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();

            // call process of controller
            ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
        } catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);
            if (_log.isDebugEnabled())
                _log.debug("processRequest", requestIDStr, "BTSLBaseException be:" + be.getMessage());
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage()))
                message = requestVO.getSenderReturnMessage();
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else
                requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            // construct Message
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            _log.error("processRequest", requestIDStr, "Exception e:" + e.getMessage());
            e.printStackTrace();
            requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpReceiver[processRequest]", requestIDStr, "", "", "Exception in SelfTopUpReceiver:" + e.getMessage());
        } finally {
            try {

                if (con == null && (senderVO != null || "Y".equals(Constants.getProperty("LOAD_TEST"))))
                    con = OracleUtil.getConnection();
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpReceiver[processrequest]", requestIDStr, "", "", "Exception in SelfTopUpReceiver while getting connection :" + e.getMessage());
            }

            if (senderVO != null && isMarkedUnderprocess && requestVO.isUnmarkSenderRequired() && con != null) {
                try {
                    // If need to bar the user for PIN Change
                    if (_log.isDebugEnabled())
                        _log.debug("processRequest", requestIDStr, "User Barring required because of PIN change......" + senderVO.isBarUserForInvalidPin());
                    if (senderVO.isBarUserForInvalidPin()) {
                        SubscriberBL.barSenderMSISDN(con, senderVO, PretupsI.BARRED_TYPE_PIN_INVALID, currentDate);
                        con.commit();
                    }
                } catch (BTSLBaseException be) {
                    _log.error("processRequest", requestIDStr, "BTSLBaseException be: " + be.getMessage());
                    try {
                        con.rollback();
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (Exception ex) {
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpReceiver[processRequest]", requestIDStr, "", "", "Exception in SelfTopUpReceiver:" + e.getMessage());
                }
                try {
                    SubscriberBL.checkRequestUnderProcess(con, requestIDStr, senderVO, false);
                    con.commit();
                } catch (BTSLBaseException be) {
                    _log.error("processRequest", requestIDStr, "BTSLBaseException be:" + be.getMessage());
                    try {
                        con.rollback();
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (Exception ex) {
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpReceiver[processRequest]", requestIDStr, "", "", "Exception in SelfTopUpReceiver:" + e.getMessage());
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
                        SubscriberTransferDAO subscriberTransferDAO = new SubscriberTransferDAO();
                        subscriberTransferDAO.addP2PReceiverRequests(con, requestVO);
                        con.commit();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        try {
                            con.rollback();
                        } catch (Exception e) {
                        }
                    }
                }

                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                con = null;
            }

            // Decrease the counters only when it is required
            if (requestVO.isDecreaseLoadCounters()) {
                if (requestVO.isDecreaseNetworkLoadCounters())
                    LoadController.decreaseCurrentNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
            // return message to sender
            // pass default locale also
            requestEndTime = System.currentTimeMillis();

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseIntermediateCounters(_instanceCode, requestType, networkID, serviceType, requestIDStr, LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());

            if (requestVO.getMessageGatewayVO() != null)
                if (_log.isDebugEnabled())
                    _log.debug(this, requestIDStr, "requestVO.getMessageGatewayVO()=" + requestVO.getMessageGatewayVO().getTimeoutValue());

            // Forward to handler class to get the request message
            if (gatewayParsersObj == null) {
                // Will be changed after discussion with ABHIJIT
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {

                }
            }
            if (_log.isDebugEnabled())
                _log.debug(this, requestIDStr, "gatewayParsersObj=" + gatewayParsersObj);
            if (gatewayParsersObj != null)
                gatewayParsersObj.generateResponseMessage(requestVO);
            else {

                if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage()))
                    message = requestVO.getSenderReturnMessage();
                else
                    message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
                requestVO.setSenderReturnMessage(message);

            }

            try {
                String reqruestGW = requestVO.getRequestGatewayCode();
                String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
                if (!BTSLUtil.isNullString(altrnetGW) && (altrnetGW.split(":")).length >= 2) {
                    if (reqruestGW.equalsIgnoreCase(altrnetGW.split(":")[0])) {
                        reqruestGW = (altrnetGW.split(":")[1]).trim();
                        if (_log.isDebugEnabled())
                            _log.debug("processRequest: Sender Message push through alternate GW", reqruestGW, "Requested GW was:" + requestVO.getRequestGatewayCode());
                    }
                }
                int messageLength = 0;
                String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                if (!BTSLUtil.isNullString(messLength))
                    messageLength = (new Integer(messLength)).intValue();

                // if(!(!BTSLUtil.isNullString(requestVO.getReqContentType()) &&
                // (requestVO.getReqContentType().indexOf("xml")!=-1 ||
                // requestVO.getReqContentType().indexOf("XML")!=-1)))
                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf("XML") != -1)) && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType()))// @@
                {
                    String txn_status = null;
                    if (requestVO.isSuccessTxn())
                        txn_status = PretupsI.TXN_STATUS_SUCCESS;
                    else
                        txn_status = requestVO.getMessageCode();
                    if (!reqruestGW.equalsIgnoreCase(requestVO.getRequestGatewayCode()) || !reqruestGW.equalsIgnoreCase("WEB")) {
                        message = requestVO.getSenderReturnMessage();
                        String message1 = null;
                        if ((messageLength > 0) && (message.length() > messageLength)) {
                            message1 = BTSLUtil.getMessage(requestVO.getLocale(), SelfTopUpErrorCodesI.REQUEST_IN_QUEUE_UB, requestVO.getMessageArguments());
                            PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());
                            pushMessage1.push();
                            requestVO.setRequestGatewayCode(reqruestGW);
                        }
                    } else
                        message = "MESSAGE=" + URLEncoder.encode(requestVO.getSenderReturnMessage(), "UTF16") + "&TXN_ID=" + BTSLUtil.NullToString(requestVO.getTransactionID()) + "&TXN_STATUS=" + BTSLUtil.NullToString(txn_status);
                } else
                    message = requestVO.getSenderReturnMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // End of Rajdeep's code
            // message=requestVO.getSenderReturnMessage();

            if (_log.isDebugEnabled())
                _log.debug(this, requestIDStr, "Locale=" + requestVO.getLocale() + " requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO.getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message);
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO.getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
                out.println(message);

                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && requestVO.isSuccessTxn() && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf("XML") != -1) && requestVO.isSenderMessageRequired()) {
                    if (requestVO.isPushMessage() && !PretupsI.YES.equals(externalInterfaceAllowed)) {
                        String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());

                        PushMessage pushMessage = null;
                        pushMessage = new PushMessage(requestVO.getFilteredMSISDN(), senderMessage, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());
                        pushMessage.push();
                    }
                }
            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                if (requestVO.isSenderMessageRequired()) {
                    PushMessage pushMessage = null;
                    pushMessage = new PushMessage(requestVO.getFilteredMSISDN(), message, requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());
                    pushMessage.push();
                }
            }

            out.flush();
            out.close();
            // Log the request in Request Logger
            P2PGatewayRequestLog.log(requestVO);
            if (_log.isDebugEnabled())
                _log.debug("processRequest", requestIDStr, "Exiting");
        }
    }

}
