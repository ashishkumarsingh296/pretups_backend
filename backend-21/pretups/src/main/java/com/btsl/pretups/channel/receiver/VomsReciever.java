package com.btsl.pretups.channel.receiver;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.ChannelGatewayRequestLog;
import com.btsl.pretups.channel.logging.TransactionPerSecLogger;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;


public class VomsReciever extends HttpServlet {
	private static final Log _log = LogFactory
			.getLog(VomsReciever.class.getName());
    private String   _instanceCode = null;
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
     * @param requestIDStr
     * @param request
     * @param response
     * @param p_requestFrom
     */
    private void processRequest(String requestIDStr, HttpServletRequest request, HttpServletResponse response, int p_requestFrom) {
        final String METHOD_NAME = "processRequest";
        long requestEndTime;
        final long requestStartTime = System.currentTimeMillis();
        final RequestVO requestVO = new RequestVO();
        long requestIDMethod = 0;
        final Date currentDate = new Date();
        String requestType = null;
        GatewayParsersI gatewayParsersObj = null;
        final HashMap responseMap = new HashMap();
        Connection con = null;MComConnectionI mcomCon = null;
        PrintWriter out = null;
        String serviceType = null;
        String externalInterfaceAllowed = null;

        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", requestIDStr, "************Start Time***********=" + requestStartTime);
            }
            requestIDMethod = Long.parseLong(requestIDStr);
            out = response.getWriter();
            requestVO.setReqContentType(request.getContentType());
            if (_log.isDebugEnabled()) {
                _log.debug("processRequest", "Content Type: " + request.getContentType());
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
            // parse Request method is used to parse the request that is sent to
            // it
            // here information about the message gateway , source type ,login
            // id ,password and udh is fetched from the request.
            parseRequest(requestIDStr, request, requestVO);
            // parseRequestForMessage is used to parse the sender msisdn and the
            // request message from the request.
            parseRequestForMessage(requestIDStr, request, requestVO, p_requestFrom);
            PretupsBL.validateRequestMessageGateway(requestVO);
            requestType = requestVO.getMessageGatewayVO().getGatewayType();
            // Forward to handler class to get the request message
            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());// @@
            ChannelGatewayRequestLog.inLog(requestVO);
            gatewayParsersObj.parseRequestMessage(requestVO);
            gatewayParsersObj.validateUserIdentification(requestVO);
            
            gatewayParsersObj.loadValidateUserDetails(con, requestVO);
            
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            // 2. Check the instance Load
            LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
            requestVO.setDecreaseLoadCounters(true);
            // load data and return the map

            // load service details, CR 000009 Sub Keyword Based Service Type
            // identification
            String requestHandlerClass;
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
                // return with error message
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PReceiver[processRequest]", requestIDStr, "", "",
                    "Service keyword not found for the keyword=" + "messageArray[0]" + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                        .getServicePort());
                throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PReceiver[processRequest]", requestIDStr, "", "",
                    "Service keyword suspended for the keyword=" + "messageArray[0]" + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                        .getServicePort());
                throw new BTSLBaseException("P2PReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

            // call process of controller
            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
            
            if(requestVO.getResponseMap()!=null){           	
            	requestVO.getResponseMap().put("RESPONSEPARAM", serviceKeywordCacheVO.getResponseParam());
            }
        } catch (BTSLBaseException be) {
        	if (be.isKey()) {
                requestVO.setVomsError(BTSLUtil.getMessage(new Locale(defaultLanguage, defaultCountry), be.getMessageKey(), be.getArgs()));
                requestVO.setVomsMessage(be.getMessageKey());
                if(requestVO.getResponseMap()!=null){ 
	                requestVO.getResponseMap().put(VOMSI.TXNSTATUS_TAG,be.getMessageKey());
	                if(!BTSLUtil.isNullString(be.getMessageKey())){
	                	requestVO.getResponseMap().put(VOMSI.MESSAGE_TAG,BTSLUtil.getMessage(new Locale(defaultLanguage, defaultCountry), be.getMessageKey(), be.getArgs()));
	                }else{
	                	requestVO.getResponseMap().put(VOMSI.MESSAGE_TAG,"Not able to Change Voucher Status");
	                }
	            	requestVO.getResponseMap().put(VOMSI.ERROR_TAG,BTSLUtil.getMessage(new Locale(defaultLanguage, defaultCountry), be.getMessageKey(), be.getArgs()));
	               }
                else{
                	responseMap.put(VOMSI.TXNSTATUS_TAG,be.getMessageKey());
                	if(!BTSLUtil.isNullString(be.getMessageKey())){
	                	requestVO.getResponseMap().put(VOMSI.MESSAGE_TAG,BTSLUtil.getMessage(new Locale(defaultLanguage, defaultCountry), be.getMessageKey(), be.getArgs()));
	                }else{
	                	requestVO.getResponseMap().put(VOMSI.MESSAGE_TAG,"Not able to Change Voucher Status");
	                }
    				responseMap.put(VOMSI.ERROR_TAG,BTSLUtil.getMessage(new Locale(defaultLanguage, defaultCountry), be.getMessageKey(), be.getArgs()));
    				requestVO.setResponseMap(responseMap);
                }
            } else
            {
            	requestVO.setVomsError(VOMSI.ERROR_EXCEPTION);
            	requestVO.setVomsMessage(VOMSI.ERROR_EXCEPTION);
            }
        	
        	requestVO.setSuccessTxn(false);
        	requestVO.setMessageCode(be.getMessageKey());
				
              _log.error("processRequest", "BTSLBaseException"+be);
//              String[] messageArgArray = null;
//              responseMap.put(VOMSI.SERIAL_NO,PretupsI.NOT_APPLICABLE);
//              responseMap.put(VOMSI.TOPUP,0);
//              responseMap.put(VOMSI.SUBSCRIBER_ID,PretupsI.NOT_APPLICABLE);
//              responseMap.put(VOMSI.REGION,PretupsI.NOT_APPLICABLE);
//              responseMap.put(VOMSI.VALID,PretupsI.NO);
//              responseMap.put(VOMSI.MESSAGE,be.getMessage());
//              responseMap.put(VOMSI.ERROR,BTSLUtil.getMessage(new Locale(defaultLanguage, defaultCountry), be.getMessage(), messageArgArray));
//              responseMap.put(VOMSI.CONSUMED,"N.A");
//              populateVOfromMap(responseMap, requestVO);
             
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            
              _log.error("processRequest", "Exception"+e);
//              String[] messageArgArray = null;
//              responseMap.put(VOMSI.SERIAL_NO,PretupsI.NOT_APPLICABLE);
//              responseMap.put(VOMSI.TOPUP,0);
//              responseMap.put(VOMSI.SUBSCRIBER_ID,PretupsI.NOT_APPLICABLE);
//              responseMap.put(VOMSI.REGION,PretupsI.NOT_APPLICABLE);
//              responseMap.put(VOMSI.VALID,PretupsI.NO);
//              responseMap.put(VOMSI.MESSAGE,e.getMessage());
//              responseMap.put(VOMSI.ERROR,BTSLUtil.getMessage(new Locale(defaultLanguage, defaultCountry), e.getMessage(), messageArgArray));
//              responseMap.put(VOMSI.CONSUMED,"N.A");
//              populateVOfromMap(responseMap, requestVO);
             
        } finally {
            if (gatewayParsersObj == null) {
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }

            try {
                gatewayParsersObj.generateResponseMessage(requestVO);
                if (out != null) {
                    out.println(requestVO.getSenderReturnMessage());
                }
                if (requestVO.isDecreaseLoadCounters()) {
                    LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
                }
                requestEndTime = System.currentTimeMillis();
                out.flush();
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                ChannelGatewayRequestLog.outLog(requestVO);

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("processRequest", "Exception e" + e);
            }
            if(mcomCon != null){mcomCon.close("VomsReciever#processRequest");mcomCon=null;}
        }
    }

    

    /**
     * @param p_requestID
     * @param p_request
     * @param p_requestVO
     * @throws BTSLBaseException
     * @author rahul.dutt
     *         This is used to prepare the request VO object from the parameters
     *         coming in the request
     */
    private void parseRequest(String p_requestID, HttpServletRequest p_request, RequestVO p_requestVO) throws BTSLBaseException {
    	final String methodName = "parseRequest";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered for prequestID=" + p_requestID + " prequest.Header:Authorization=" + p_request.getHeader("Authorization"));
		}

		if (BTSLUtil.isNullString(p_requestVO.getReqContentType())) {
			p_requestVO.setReqContentType("CONTENT_TYPE");
		}
		
		String requestGatewayCode;
		String requestGatewayType;
		String servicePort;
		String login;
		String password;
		String udh = null;
		String sourceType;
		String param1 = null;
		final String hexUrlEncodedRequired = p_request.getParameter("MESSAGE_ENCODE");
		if (BTSLUtil.isNullString(p_request.getHeader("Authorization"))) {
			requestGatewayCode = p_request.getParameter("REQUEST_GATEWAY_CODE");
			requestGatewayType = p_request.getParameter("REQUEST_GATEWAY_TYPE");

			servicePort = p_request.getParameter("SERVICE_PORT");
			login = p_request.getParameter("LOGIN");
			password = p_request.getParameter("PASSWORD");
			udh = p_request.getParameter("UDH");
			sourceType = p_request.getParameter("SOURCE_TYPE");
			param1 = p_request.getParameter("ICD");
		} else {
			final String msg = p_request.getHeader("Authorization");
			int indx1 = 0;
			try {
				indx1 = msg.indexOf("REQUEST_GATEWAY_CODE");
				indx1 = msg.indexOf('=', indx1);
				int index2 = msg.indexOf('&', indx1 + 1);
				if (index2 > 0) {
					requestGatewayCode = msg.substring(indx1 + 1, index2);
				} else {
					requestGatewayCode = msg.substring(indx1 + 1);
				}

				indx1 = msg.indexOf("REQUEST_GATEWAY_TYPE");
				indx1 = msg.indexOf('=', indx1);
				index2 = msg.indexOf('&', indx1 + 1);
				if (index2 > 0) {
					requestGatewayType = msg.substring(indx1 + 1, index2);
				} else {
					requestGatewayType = msg.substring(indx1 + 1);
				}

				indx1 = msg.indexOf("LOGIN");
				indx1 = msg.indexOf('=', indx1);
				index2 = msg.indexOf('&', indx1 + 1);
				if (index2 > 0) {
					login = msg.substring(indx1 + 1, index2);
				} else {
					login = msg.substring(indx1 + 1);
				}

				indx1 = msg.indexOf("PASSWORD");
				indx1 = msg.indexOf('=', indx1);
				index2 = msg.indexOf('&', indx1 + 1);
				if (index2 > 0) {
					password = msg.substring(indx1 + 1, index2);
				} else {
					password = msg.substring(indx1 + 1);
				}

				indx1 = msg.indexOf("SOURCE_TYPE");
				indx1 = msg.indexOf('=', indx1);
				index2 = msg.indexOf('&', indx1 + 1);
				if (index2 > 0) {
					sourceType = msg.substring(indx1 + 1, index2);
				} else {
					sourceType = msg.substring(indx1 + 1);
				}

				indx1 = msg.indexOf("SERVICE_PORT");
				indx1 = msg.indexOf('=', indx1);
				index2 = msg.indexOf('&', indx1 + 1);
				if (index2 > 0) {
					servicePort = msg.substring(indx1 + 1, index2);
				} else {
					servicePort = msg.substring(indx1 + 1);
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_INVALID_AUTH_PARAMETER);
			}
		}

		

        if (_log.isDebugEnabled()) {
            _log.debug(
                "parseRequest",
                p_requestID,
                "requestGatewayCode:" + requestGatewayCode + " requestGatewayType:" + requestGatewayType + " servicePort:" + servicePort + " udh:" + udh + " sourceType:" + sourceType + " login:" + login);
        }

        if (BTSLUtil.isNullString(requestGatewayCode)) {
            throw new BTSLBaseException("ChannelReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
        } else {
            requestGatewayCode = requestGatewayCode.trim();
        }
        p_requestVO.setRequestGatewayCode(requestGatewayCode);
        if (BTSLUtil.isNullString(requestGatewayType)) {
            throw new BTSLBaseException("ChannelReceiver", "parseRequest", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
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
            _log.debug("parseRequest", "Exiting for p_requestID=" + p_requestID);
        }
    }

    /**
     * @param requestID
     * @param request
     * @param p_requestVO
     * @param calledMethodType
     * @throws BTSLBaseException
     *             populates the message incoming in request
     */
    public void parseRequestForMessage(String requestID, HttpServletRequest request, RequestVO p_requestVO, int calledMethodType) throws BTSLBaseException {
        final String METHOD_NAME = "parseRequestForMessage";
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequestForMessage", requestID, "Entered calledMethodType: " + calledMethodType);
        }
        String msisdn = request.getParameter("MSISDN");
        final String requestMessage = request.getParameter("MESSAGE");
        // request comes from the doGET
        if (calledMethodType == 1) {
            if (_log.isDebugEnabled()) {
                _log.debug("parseRequestForMessage", requestID, "msisdn:" + msisdn + " requestMessage:" + requestMessage);
            }
            p_requestVO.setRequestMessage(requestMessage);
            if (BTSLUtil.isNullString(requestMessage)) {
                throw new BTSLBaseException("ChannelReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
            }

            if (!BTSLUtil.isNullString(msisdn)) {
                msisdn = msisdn.trim();
                p_requestVO.setRequestMSISDN(msisdn);
            }
        } else {
            String msg = "";
            StringBuilder sb = new StringBuilder(1024);
            if (BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage)) {
                try {
					final ServletInputStream in = request.getInputStream();
					int c;
					while ((c = in.read()) != -1) {
						// Process line...
						msg += (char) c;
					}
                    if (BTSLUtil.isNullString(msg)) {
                        throw new BTSLBaseException("ChannelReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                    }
                    p_requestVO.setRequestMessage(msg);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    throw be;
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("ChannelReceiver", "parseRequestForMessage", PretupsErrorCodesI.REQ_NOT_PROCESS);
                }
            }
            if (!BTSLUtil.isNullString(msisdn)) {
                p_requestVO.setRequestMSISDN(msisdn);
            }

            if (!BTSLUtil.isNullString(requestMessage)) {
                p_requestVO.setRequestMessage(requestMessage);
            }

            if (BTSLUtil.isNullString(p_requestVO.getRequestMessage())) {
                throw new BTSLBaseException("ChannelReceiver", "parseRequestForMessage", PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("parseRequestForMessage", requestID, "Exiting with Message=" + p_requestVO.getRequestMessage());
        }
    }

    private void populateVOfromMap(HashMap p_map, RequestVO p_requestVO) {
        // added for voucher query and rollback request
        final String methodName = "populateVOfromMap";

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_map" + p_map);
        }
        p_requestVO.setSerialNo((String) p_map.get(VOMSI.SERIAL_NO));
        try {
            p_requestVO.setVoucherAmount(Long.parseLong(String.valueOf(p_map.get(VOMSI.TOPUP))));
        } catch (Exception e) {
            _log.error(methodName, "Exception while parsing Voucher MRP: " + e.getMessage());
            _log.errorTrace(methodName, e);
        }// changed for voucher query and rollback request
        p_requestVO.setConsumed((String) p_map.get(VOMSI.CONSUMED));
        p_requestVO.setVomsMessage((String) p_map.get(VOMSI.MESSAGE));
        p_requestVO.setVomsError((String) p_map.get(VOMSI.ERROR));
        p_requestVO.setVomsRegion((String) p_map.get(VOMSI.REGION));
        p_requestVO.setReceiverMsisdn((String) p_map.get(VOMSI.SUBSCRIBER_ID));
        p_requestVO.setVomsValid((String) p_map.get(VOMSI.VALID));
        p_requestVO.setValidity((String) p_map.get(VOMSI.VOMS_VALIDITY));
        p_requestVO.setTalkTime(String.valueOf( p_map.get(VOMSI.VOMS_TALKTIME)));  
        p_requestVO.setTransactionID((String) p_map.get(VOMSI.VOMS_TXNID));        
        VomsVoucherVO voucherVO=new VomsVoucherVO();
        voucherVO.setProductID((String) p_map.get(VOMSI.PRODUCT_ID));
       
        p_requestVO.setVoucherStatus((String) p_map.get(VOMSI.VOMS_STATUS));
        p_requestVO.setPin((String) p_map.get(VOMSI.PIN));
        p_requestVO.setExpiryDate((Date) p_map.get(VOMSI.EXPIRY_DATE));
        voucherVO.setProductName((String) p_map.get(VOMSI.PRODUCT_NAME));
        voucherVO.setProductID((String) p_map.get(VOMSI.PRODUCT_ID));
        voucherVO.setConsumedOn((Date) p_map.get(VOMSI.FIRST_CONSUMED_ON));
        p_requestVO.setValidity((String) p_map.get(VOMSI.VOMS_VALIDITY));
        p_requestVO.setTalkTime(""+p_map.get(VOMSI.VOMS_TALKTIME));

        p_requestVO.setValueObject(voucherVO);
        // added for voucher query and rollback request
        if (VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(p_requestVO.getServiceType())) {
            p_requestVO.setVoucherStatus((String) p_map.get(VOMSI.VOMS_STATUS));
            p_requestVO.setPin((String) p_map.get(VOMSI.PIN));
            p_requestVO.setExpiryDate((Date) p_map.get(VOMSI.EXPIRY_DATE));
            voucherVO.setProductName((String) p_map.get(VOMSI.PRODUCT_NAME));
            voucherVO.setProductID((String) p_map.get(VOMSI.PRODUCT_ID));
            voucherVO.setConsumedOn((Date) p_map.get(VOMSI.FIRST_CONSUMED_ON));
            p_requestVO.setValidity((String) p_map.get(VOMSI.VOMS_VALIDITY));
            p_requestVO.setTalkTime((String) p_map.get(VOMSI.VOMS_TALKTIME));
            
            p_requestVO.setValueObject(voucherVO);
        }
        // added for Voucher retrieval RollBack Request
        if (VOMSI.SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK.equals(p_requestVO.getServiceType())) {
            p_requestVO.setExternalTransactionNum((String) p_map.get(VOMSI.VOMS_TXNID));
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
