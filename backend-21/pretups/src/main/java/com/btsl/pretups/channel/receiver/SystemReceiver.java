	
package com.btsl.pretups.channel.receiver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.transfer.businesslogic.MessageFormater;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


/**
 * @author akanksha.gupta
 * SystemReceiver.java
 * 04-Aug-2016 11:39:53 am
 * akanksha.gupta
 *
 */
public class SystemReceiver extends ChannelReceiver{
	 public static final Log _log = LogFactory.getLog(SystemReceiver.class.getName());
	    private static String _instanceCode = null;
	    private static OperatorUtilI _operatorUtil = null;

		static {
			final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try {
				_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				_log.errorTrace("static", e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SystemReceiver[initialize]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
		}
	  
	    private static long _requestID = 0;
	   
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


	private void processRequest(String requestIDStr, HttpServletRequest request, HttpServletResponse response, int p_requestFrom) {
        final String METHOD_NAME = "processRequest";
         // response.setContentType("text/html");
        PrintWriter out = null;
        final RequestVO requestVO = new RequestVO();
        requestVO.setResponseMap(new HashMap());
        String message = null;
        Connection con = null;MComConnectionI mcomCon = null;
       final Date currentDate = new Date();
        final long requestStartTime = System.currentTimeMillis();
        long requestEndTime = 0;	
        GatewayParsersI gatewayParsersObj = null;
        long requestIDMethod = 0;
        String requestType = null;
        String serviceType = null;
        String externalInterfaceAllowed = null;
        List<ServiceKeywordControllerI> serviceList = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr, "************Start Time***********=" + requestStartTime);
            }
            String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            // changes for Amharic Language UTF8 Characters//
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            out = response.getWriter();
            requestIDMethod = Long.parseLong(requestIDStr);
            requestVO.setReqContentType(request.getContentType());
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Content Type: " + request.getContentType());
            }
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.USERTYPE);
            requestVO.setInstanceID(_instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale(lang,country));
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

            PretupsBL.validateRequestMessageGateway(requestVO);
            requestType = requestVO.getMessageGatewayVO().getGatewayType();

            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
            parseRequestForMessage(requestIDStr, request, requestVO, p_requestFrom);

            ChannelGatewayRequestLog.inLog(requestVO);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            gatewayParsersObj.parseChannelRequestMessage(requestVO, con);

          
    
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
            }

            if(requestVO.getExternalNetworkCode() != null){
            	requestVO.setRequestNetworkCode(requestVO.getExternalNetworkCode());
            }else{
            	requestVO.setRequestNetworkCode(requestVO.getRequestMessageArray()[4]);
            }
            
            String requestHandlerClass;
          
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr, "Sender Locale in Request if any" + requestVO.getSenderLocale());
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            } else {
                requestVO.setSenderLocale(requestVO.getLocale());
            }

              LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
                requestVO.setDecreaseLoadCounters(true);
                LoadController.checkNetworkLoad(requestIDMethod, requestVO.getRequestNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);
                requestVO.setToBeProcessedFromQueue(false);
            
               MessageFormater.handleExtChannelMessageFormat(requestVO);

      
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SystemReceiver[processRequest]", requestIDStr,
                    "SystemReceiver", "", "Service keyword not found for the keyword=" + requestVO.getRequestMessageArray()[0] + " For Gateway Type=" + requestVO
                        .getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException("SystemReceiver", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SystemReceiver[processRequest]", requestIDStr,
                		"SystemReceiver", "", "Service keyword suspended for the keyword=" + requestVO.getRequestMessageArray()[0] + " For Gateway Type=" + requestVO
                        .getRequestGatewayType() + "Service Port=" + requestVO.getServicePort());
                throw new BTSLBaseException("SystemReceiver", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            
            
           requestVO.setInfo1(request.getScheme() + "://" +request.getServerName() + ":" + request.getServerPort() +request.getContextPath());
     
           final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
                controllerObj.process(requestVO);
                requestVO.getResponseMap().put("RESPONSEPARAM", serviceKeywordCacheVO.getResponseParam());
                
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            requestVO.setSuccessTxn(false);
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, requestIDStr, "BTSLBaseException be:" + be.getMessage());
            }
           
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            _log.error(METHOD_NAME, requestIDStr, "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SystemReceiver[processRequest]", requestIDStr, "",
                "", "Exception in SystemReceiver:" + e.getMessage());
        } finally {
        	if(mcomCon != null){mcomCon.close("SystemReceiver#processRequest");mcomCon=null;}
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }

            if (gatewayParsersObj == null) {
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(this, requestIDStr, "gatewayParsersObj=" + gatewayParsersObj);
                }
            }

            
               
                if (requestVO.isDecreaseLoadCounters()) {
                    if (requestVO.getRequestNetworkCode() != null) {
                        LoadController.decreaseCurrentNetworkLoad(requestIDMethod, requestVO.getRequestNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                    }
                    LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
                }

                requestEndTime = System.currentTimeMillis();

                if (BTSLUtil.isNullString(requestVO.getTransactionID())) {
                    ReqNetworkServiceLoadController.increaseIntermediateCounters(_instanceCode, requestType, requestVO.getRequestNetworkCode(), serviceType, requestIDStr,
                        LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());
                } else {
                    ReqNetworkServiceLoadController.increaseIntermediateCounters(_instanceCode, requestType, requestVO.getRequestNetworkCode(), serviceType, requestVO.getTransactionID(),
                        LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());
                }

                if (_log.isDebugEnabled()) {
                    _log.debug(this, requestIDStr, "requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO
                        .getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message + " Locale=" + requestVO.getLocale());
                }
            

            if (requestVO.getMessageGatewayVO() != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, requestIDStr, "Gateway Time out=" + requestVO.getMessageGatewayVO().getTimeoutValue());
                }
            }

          

            if (gatewayParsersObj != null) {
                gatewayParsersObj.generateChannelResponseMessage(requestVO);
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

                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf(
                    "XML") != -1)) && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType())) {
                    String txn_status = null;
                    if (requestVO.isSuccessTxn() && !requestVO.isToBeProcessedFromQueue()) {
                        txn_status = PretupsI.TXN_STATUS_SUCCESS;
                    } else {
                        txn_status = requestVO.getMessageCode();
                    }
                    // Message Encoding need not be requied only for WEB
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
                _log.debug(METHOD_NAME, requestIDStr,
                    "Locale=" + requestVO.getLocale() + " requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO
                        .getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message);
            }
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
                .getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
                out.println(message);

            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                // get the Locale from the Channel User VO and send message back

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
                _log.debug(METHOD_NAME, requestIDStr, "Exiting");
            }
        }
    }
	
	public void parseRequestForMessage(String requestID, HttpServletRequest request, RequestVO prequestVO, int calledMethodType) throws BTSLBaseException {
		final String methodName = "parseRequestForMessage";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered calledMethodType: ");
			loggerValue.append(calledMethodType);
			_log.debug(methodName, requestID,  loggerValue );
		}
		 String requestMessage = request.getParameter("MESSAGE");
		if (calledMethodType == 1) {
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("requestMessage:");
				loggerValue.append(requestMessage);
				_log.debug(methodName, requestID,  loggerValue );
			}
			if(prequestVO.getRequestMessage()!=null){
				requestMessage=prequestVO.getRequestMessage();
			}
			prequestVO.setRequestMessage(requestMessage);
			if (!BTSLUtil.isNullString(requestMessage)) 
				prequestVO.setRequestMessageArray(requestMessage.split(" "));
			if (PretupsI.GATEWAY_TYPE_SMS_POS.equals(prequestVO.getRequestGatewayType())) {
				if (BTSLUtil.isNullString(requestMessage)) {
					throw new BTSLBaseException("SystemReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
				}
			}
			
		} else {
			String msg = "";
			StringBuilder sb = new StringBuilder(1024);
			if (BTSLUtil.isNullString(requestMessage)) {
				if (PretupsI.FLARES_CONTENT_TYPE.equals(request.getContentType())) {

					final StringBuffer requestBuffer = new StringBuffer();
					String tempElement;
					final Enumeration requestEnum = request.getParameterNames();
					while (requestEnum.hasMoreElements()) {
						tempElement = (String) requestEnum.nextElement();
						requestBuffer.append(tempElement + "=" + request.getParameter(tempElement) + "&");
						if ("TYPE".equals(tempElement)) {
							prequestVO.setServiceKeyword(request.getParameter(tempElement));
						}
					}
					prequestVO.setReqContentType("plain");	
					String reqMessage=requestBuffer.toString();
					
					prequestVO=_operatorUtil.parsePaymentMessage(prequestVO,reqMessage);
			
					
				
						
				} else {
					try {
						final ServletInputStream in = request.getInputStream();
						int c;
						sb.setLength(0);
						while ((c = in.read()) != -1) {
							sb.append(String.valueOf(c));
	                    }
	                    msg = sb.toString();
						if (BTSLUtil.isNullString(msg)) {
							throw new BTSLBaseException("SystemReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
						}
						prequestVO.setRequestMessage(msg);
					} catch (BTSLBaseException be) {
						_log.errorTrace(methodName, be);
						throw be;
					} catch (Exception e) {
						_log.errorTrace(methodName, e);
						throw new BTSLBaseException("SystemReceiver", methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
					}
				}
			}
			if (!BTSLUtil.isNullString(requestMessage)) 
				prequestVO.setRequestMessage(requestMessage);
			if (!BTSLUtil.isNullString(requestMessage)) 
				prequestVO.setRequestMessageArray(requestMessage.split(" "));
			if (BTSLUtil.isNullString(prequestVO.getRequestMessage())) {
				throw new BTSLBaseException("SystemReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
			}
		}
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting with Message=");
			loggerValue.append(prequestVO.getRequestMessage());
			_log.debug(methodName, requestID,  loggerValue);
		}
	}

	protected void parseRequest(String prequestID, HttpServletRequest prequest, RequestVO prequestVO) throws BTSLBaseException {
		final String methodName = "parseRequest";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered for prequestID=" + prequestID + " prequest.Header:Authorization=" + prequest.getHeader("Authorization"));
		}

		if (BTSLUtil.isNullString(prequestVO.getReqContentType())) {
			prequestVO.setReqContentType("CONTENT_TYPE");
		}

		String requestGatewayCode;
		String requestGatewayType="";
		String servicePort="";
		String login="";
		String password="";
		String udh = null;
		String sourceType="";
		String param1 = null;
		final String hexUrlEncodedRequired = prequest.getParameter("MESSAGE_ENCODE");
		if (BTSLUtil.isNullString(prequest.getHeader("Authorization"))) {
			requestGatewayCode = prequest.getParameter("REQUEST_GATEWAY_CODE");
			
			
			if(requestGatewayCode.contains("|")){
				String[] gatewayURL=requestGatewayCode.split("\\|");
				requestGatewayCode=gatewayURL[0];
				for (String string : gatewayURL) {
					String[] stringEach=string.split("=");
					if(stringEach[0].equalsIgnoreCase("REQUEST_GATEWAY_TYPE"))
					{
						requestGatewayType = stringEach[1].toString();
					}
					if(stringEach[0].equalsIgnoreCase("SERVICE_PORT"))
					{
						servicePort =stringEach[1].toString();
					}if(stringEach[0].equalsIgnoreCase("LOGIN"))
					{
						login = stringEach[1].toString();
					}if(stringEach[0].equalsIgnoreCase("PASSWORD"))
					{
						password = stringEach[1].toString();	
					}if(stringEach[0].equalsIgnoreCase("SOURCE_TYPE"))
					{
						sourceType = stringEach[1].toString();	
					}if(stringEach[0].equalsIgnoreCase("MESSAGE"))
					{
						prequestVO.setRequestMessage( stringEach[1].toString());
					}
				}
			}else{
				
				
			requestGatewayType = prequest.getParameter("REQUEST_GATEWAY_TYPE");

			servicePort = prequest.getParameter("SERVICE_PORT");
			login = prequest.getParameter("LOGIN");
			password = prequest.getParameter("PASSWORD");
			udh = prequest.getParameter("UDH");
			sourceType = prequest.getParameter("SOURCE_TYPE");
			param1 = prequest.getParameter("ICD");
			}
		} else {
			final String msg = prequest.getHeader("Authorization");
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
			_log.debug(methodName, prequestID, " p_requestFromrequestGatewayCode:" + requestGatewayCode + ", requestGatewayType:" + requestGatewayType + ", servicePort:" + servicePort + ", udh:" + udh + ", sourceType:" + sourceType + ", login:" + BTSLUtil.maskParam(login) + ", password:" + BTSLUtil.maskParam(password) + ", param1:" + param1);
		}

		if (BTSLUtil.isNullString(requestGatewayCode)) {
			throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
		} else {
			requestGatewayCode = requestGatewayCode.trim();
		}
		prequestVO.setRequestGatewayCode(requestGatewayCode);
		if (BTSLUtil.isNullString(requestGatewayType)) {
			throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
		} else {
			requestGatewayType = requestGatewayType.trim();
		}
		prequestVO.setRequestGatewayType(requestGatewayType);
		prequestVO.setUDH(udh);
		prequestVO.setLogin(login);
		prequestVO.setPassword(password);
		prequestVO.setServicePort(servicePort);
		Boolean loadBalancerIPAllowed = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.LOAD_BAL_IP_ALLOWED);
		if(loadBalancerIPAllowed)
		{
			if(BTSLUtil.isNullString(prequest.getHeader("X-Forwarded-For"))){
				prequestVO.setRemoteIP(prequest.getRemoteAddr());
			}
			else
				prequestVO.setRemoteIP(prequest.getHeader("X-Forwarded-For"));
		}
		else
			prequestVO.setRemoteIP(prequest.getRemoteAddr());
		
		if(_log.isDebugEnabled())_log.debug("parseRequest","Source IP for p_requestID="+prequest.getRemoteAddr()+" LB X-Fowarded-For"+prequest.getHeader("X-Forwarded-For"));
		prequestVO.setSourceType(BTSLUtil.NullToString(sourceType));
		prequestVO.setParam1(param1);// for DP6
		if ("b".equalsIgnoreCase(hexUrlEncodedRequired)) {
			prequestVO.setHexUrlEncodedRequired(false);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting for prequestID=" + prequestID);
		}
	}
}
