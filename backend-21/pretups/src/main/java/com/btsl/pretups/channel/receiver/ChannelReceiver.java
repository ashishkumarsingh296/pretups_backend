package com.btsl.pretups.channel.receiver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.btsl.pretups.channel.logging.QueueLogger;
import com.btsl.pretups.channel.logging.TransactionPerSecLogger;
import com.btsl.pretups.channel.queue.DataQueue;
import com.btsl.pretups.channel.queue.RequestQueueVO;
import com.btsl.pretups.channel.queue.ThreadCountManager;
import com.btsl.pretups.channel.queue.ThreadPoolClient;
import com.btsl.pretups.channel.queue.ThreadPoolConstants;
import com.btsl.pretups.channel.queue.ThreadPoolForQueue;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.FixedInformationVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceInstancePriorityCache;
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
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;


public class ChannelReceiver extends HttpServlet {

	private static final Log log= LogFactory.getLog(ChannelReceiver.class.getName());
	
	private static String instanceCode = null;
	//private static long requestID = 0;
	private static AtomicInteger requestID=new AtomicInteger();
	private static ThreadPoolForQueue threadPoolForQueue;
	public static List<String> listOfRequestsSendToPool = null;
	private String queueLogicValue = null;
	private String queueForAll = null;
	public static DataQueue concurentQueue = null;
	private static ThreadCountManager threadCountManager = null;
    public static ThreadPoolExecutor executor = null;
	
	static{
		
		try {
            executor = (ThreadPoolExecutor)Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE")).intValue()));
        } catch (Exception e) {
            executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(30);
            //log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[thread pool initialize]", "", "", "", "Exception while initilizing the thread pool :" + e.getMessage());
        }
	
	}
	@Override
	public void init() throws ServletException {

		instanceCode = Constants.getProperty("INSTANCE_ID");
		if (log.isInfoEnabled()) {
			log.info("init", "_instanceCode:" + instanceCode);
		}
		queueLogicValue = BTSLUtil.NullToString(Constants.getProperty("IS_QUEUE_LOGIC_TO_BE_IMPLEMENTED"));
		queueForAll = BTSLUtil.NullToString(Constants.getProperty("IS_QUEUE_LOGIC_FOR_ALL_REQUESTS"));
		if ("Y".equalsIgnoreCase(queueLogicValue)) {
			if (threadPoolForQueue == null) {
				threadPoolForQueue = new ThreadPoolForQueue();
				threadPoolForQueue.setMinWorkerThreads(Integer.valueOf(Constants.getProperty("QUEUE_MIN_WORKER_THREAD")));
				threadPoolForQueue.setMaxWorkerThreads(Integer.valueOf(Constants.getProperty("QUEUE_MAX_WORKER_THREAD")));
				threadPoolForQueue.setPoolQueueSize(Integer.valueOf(Constants.getProperty("QUEUE_POOL_SIZE")));
				threadPoolForQueue.setThresholdPoolQueueSize(Integer.valueOf(Constants.getProperty("QUEUE_THRESHOLD_POOL_SIZE")));
			}
			listOfRequestsSendToPool = new ArrayList<String>();
			concurentQueue = new DataQueue();
			threadPoolForQueue.init(concurentQueue);
			threadCountManager = new ThreadCountManager(threadPoolForQueue, concurentQueue);
		}
		log.info("init", "_instanceCode:" + instanceCode);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//++requestID;
		//final String requestIDStr = String.valueOf(requestID);
		String requestIDStr=String.valueOf(requestID.incrementAndGet());
		if (log.isDebugEnabled()) {
			log.debug("doGet", requestIDStr, "Entered");
		}
		processRequest(requestIDStr, request, response, 1);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//++requestID;
		//final String requestIDStr = String.valueOf(requestID);
		String requestIDStr=String.valueOf(requestID.incrementAndGet()); 
		
		if (log.isDebugEnabled()) {
			log.debug("doPost", requestIDStr, "Entered");
		}
		processRequest(requestIDStr, request, response, 2);
	}

	@Override
	public void destroy() {
		threadPoolForQueue.destroy();
	}

	/**
	 * Method to validate the Service Type that is send by the user
	 * 1) Checks if it is not REG or ADM and user is not registered then
	 * deactivate the services
	 * 2) Validates the Service ID and version corresponding to the type
	 * requested
	 * 3) Check if service type is allowed to the user or not
	 * 
	 * @param prequestVO
	 * @param pserviceKeywordCacheVO
	 * @param psimProfileVO
	 * @throws BTSLBaseException
	 */
	private void validateServiceType(RequestVO prequestVO, ServiceKeywordCacheVO pserviceKeywordCacheVO, SimProfileVO psimProfileVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
		final String methodName = "validateServiceType";
		StringBuilder loggerValue= new StringBuilder(); 
		final String serviceType = prequestVO.getServiceType();
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" MSISDN=");
			loggerValue.append(prequestVO.getFilteredMSISDN());
			loggerValue.append(" Service Type=");
			loggerValue.append(serviceType);
							
			log.debug("validateServiceType", prequestVO.getRequestIDStr(),  loggerValue );
		}
		String c2sUserRegistrationRequired = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_USER_REGISTRATION_REQUIRED);
		try {
			if (prequestVO.getSourceType().equals(PretupsI.REQUEST_SOURCE_TYPE_STK)) {
				if (!(PretupsI.KEYWORD_TYPE_REGISTRATION.equalsIgnoreCase(serviceType) || PretupsI.KEYWORD_TYPE_ADMIN.equalsIgnoreCase(serviceType))) {
					if (!(channelUserVO.getUserPhoneVO()).isRegistered() && PretupsI.YES.equals(c2sUserRegistrationRequired)) {
						if ("Y".equalsIgnoreCase(Constants.getProperty("STK_REGISTRATION_REQUIRED"))) {
							SimUtil.deactivateAllServices(prequestVO.getFilteredMSISDN(), (channelUserVO.getUserPhoneVO()).getEncryptDecryptKey(), psimProfileVO);
						}

						log.error("validateServiceType", prequestVO.getRequestIDStr()," MSISDN=" + prequestVO.getFilteredMSISDN() + " User sim not registered but able to send request");
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[parseBinaryMessage]", prequestVO.getRequestIDStr(), prequestVO.getFilteredMSISDN(), ""," User sim not registered for number :" + prequestVO.getFilteredMSISDN() + " but able to send request");
						throw new BTSLBaseException("ChannelReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_NOTREG_BUTSENDREQ);
					}
				}
			}
			if (channelUserVO.isUsingNewSTK()) {
				PretupsBL.validateServiceTypeIdandVersion(prequestVO, pserviceKeywordCacheVO);
			}
			if (PretupsI.YES.equals(pserviceKeywordCacheVO.getExternalInterface()) || PretupsI.AUTO_ASSIGN_SERVICES.equals(pserviceKeywordCacheVO.getExternalInterface())) {
				final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType, channelUserVO.getAssociatedServiceTypeList());
				if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
					loggerValue.setLength(0);
					loggerValue.append("MSISDN=");
					loggerValue.append( prequestVO.getFilteredMSISDN());
					loggerValue.append(" Service Type not found in allowed List");
				
					log.error("validateServiceType", prequestVO.getRequestIDStr(),  loggerValue );
					throw new BTSLBaseException("ChannelReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
				} else if (!PretupsI.YES.equals(listValueVO.getLabel())) {
					loggerValue.setLength(0);
					loggerValue.append("MSISDN=");
					loggerValue.append(prequestVO.getFilteredMSISDN());
					loggerValue.append(" Service Type is suspended in allowed List");
					log.error("validateServiceType", prequestVO.getRequestIDStr(), loggerValue);
					throw new BTSLBaseException("ChannelReceiver", "validateServiceType", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED);
				}
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			loggerValue.setLength(0);
			loggerValue.append("MSISDN=");
			loggerValue.append(" Base Exception :");
			loggerValue.append(be.getMessage());
			log.error("validateServiceType", prequestVO.getRequestIDStr(),  loggerValue);
			throw be;
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("MSISDN=");
			loggerValue.append(prequestVO.getFilteredMSISDN());
			loggerValue.append(" Exception :");
			loggerValue.append(e.getMessage());
		
			log.error("validateServiceType", prequestVO.getRequestIDStr(),  loggerValue);
			
			loggerValue.setLength(0);
			loggerValue.append("Not able to check validate Service Type is in allowed List for Request ID:");
			loggerValue.append(prequestVO.getRequestID());
			loggerValue.append(" and MSISDN:");
			loggerValue.append(prequestVO.getFilteredMSISDN());
			loggerValue.append(" ,getting Exception=");
			loggerValue.append(e.getMessage());
			
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkServiceTypeAllowed]", prequestVO
					.getRequestIDStr(), prequestVO.getFilteredMSISDN(), "", loggerValue.toString() );
			throw new BTSLBaseException("ChannelReciever", "validateServiceType", PretupsErrorCodesI.C2S_SERVICE_TYPE_VALIDATION_ERROR);
		}
	}

	private void populateLanguageSettings(RequestVO prequestVO, ChannelUserVO channelUserVO) {
		final FixedInformationVO fixedInformationVO = (FixedInformationVO) prequestVO.getFixedInformationVO();
		if (prequestVO.getLocale() == null) {
			prequestVO.setLocale(new Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (channelUserVO.getUserPhoneVO()).getCountry()));
		}
		if (fixedInformationVO != null) {
			PretupsBL.getCurrentLocale(prequestVO, channelUserVO.getUserPhoneVO());
		}
	}

	private void updateSimParameters(RequestVO prequestVO, UserPhoneVO puserPhoneVO, String pnetworkCode) {
		final String methodName = "updateSimParameters";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Request ID=");
			loggerValue.append(prequestVO.getRequestID());
			loggerValue.append(" Update Msisdn =");
			loggerValue.append(puserPhoneVO.getMsisdn());
			loggerValue.append(" in case of New SIM and if parameters are not matching");
			
			log.debug(methodName, loggerValue);
		}
		final SimUtil simUtil = new SimUtil();
		try {
			simUtil.updateParametersAndSendSMS(pnetworkCode, puserPhoneVO.getMsisdn(), ByteCodeGeneratorI.UPDATE_PARAMETER_PIN, puserPhoneVO.getPinRequired(),
					puserPhoneVO.getEncryptDecryptKey(), PretupsI.REQUEST_SOURCE_TYPE_SMS);
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Not able to send Adm messages for Request ID:");
			loggerValue.append(prequestVO.getRequestID());
			loggerValue.append(" and MSISDN:" );
			loggerValue.append(prequestVO.getFilteredMSISDN());
			loggerValue.append(" ,getting Exception=");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelReciever[updateSimParameters]", prequestVO
					          .getRequestIDStr(), prequestVO.getFilteredMSISDN(), "",loggerValue.toString() );
			loggerValue.setLength(0);
			loggerValue.append("Request ID=");
			loggerValue.append(prequestVO.getRequestID());
			loggerValue.append(" Exception in sending adm messages::");
			loggerValue.append(e.getMessage());
			log.error(methodName,  loggerValue);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Request ID=");
			loggerValue.append(prequestVO.getRequestID());
			loggerValue.append(" Exiting ");
			
			log.debug(methodName, loggerValue );
		}
	}

	protected void parseRequest(String prequestID, HttpServletRequest prequest, RequestVO prequestVO) throws BTSLBaseException {
		final String methodName = "parseRequest";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered for prequestID=" + prequestID + " prequest.Header:Authorization=" + prequest.getHeader("Authorization"));
		}
		ChannelGatewayRequestLog.inLogIntermediate(prequestVO,"Entered ParseRequest");
		if (BTSLUtil.isNullString(prequestVO.getReqContentType())) {
			prequestVO.setReqContentType("CONTENT_TYPE");
		}

		String requestGatewayCode;
		String requestGatewayType;
		String servicePort;
		String login;
		String password;
		String udh = null;
		String sourceType;
		String param1 = null;
		final String hexUrlEncodedRequired = prequest.getParameter("MESSAGE_ENCODE");
		String headerAuthorization=prequest.getHeader("Authorization");
		if("CONTENT_TYPE".equalsIgnoreCase(prequestVO.getReqContentType())){
			headerAuthorization="";
			prequestVO.setReqContentType("CONTENT_TYPE");
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Manual Authorization Done. prequest.Header:Authorization="+headerAuthorization );
			}
		}
		if (BTSLUtil.isNullString(headerAuthorization)) {
			requestGatewayCode = prequest.getParameter("REQUEST_GATEWAY_CODE");
			requestGatewayType = prequest.getParameter("REQUEST_GATEWAY_TYPE");

			servicePort = prequest.getParameter("SERVICE_PORT");
			login = prequest.getParameter("LOGIN");
			password = prequest.getParameter("PASSWORD");
			udh = prequest.getParameter("UDH");
			sourceType = prequest.getParameter("SOURCE_TYPE");
			param1 = prequest.getParameter("ICD");
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
				log.errorTrace(methodName, e);
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_INVALID_AUTH_PARAMETER);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, prequestID, " p_requestFromrequestGatewayCode:" + requestGatewayCode + ", requestGatewayType:" + requestGatewayType + ", servicePort:" + servicePort + ", udh:" + udh + ", sourceType:" + sourceType + ", login:" + login + ", password:" + BTSLUtil.maskParam(password) + ", param1:" + param1);
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
		Boolean loadBalanceIpAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOAD_BAL_IP_ALLOWED);
		if(loadBalanceIpAllowed)
		{
			if(BTSLUtil.isNullString(prequest.getHeader("X-Forwarded-For"))){
				prequestVO.setRemoteIP(prequest.getRemoteAddr());
			}
			else
				prequestVO.setRemoteIP(prequest.getHeader("X-Forwarded-For"));
		}
		else
			prequestVO.setRemoteIP(prequest.getRemoteAddr());
		
		if(log.isDebugEnabled())log.debug("parseRequest","Source IP for p_requestID="+prequest.getRemoteAddr()+" LB X-Fowarded-For"+prequest.getHeader("X-Forwarded-For"));
		prequestVO.setSourceType(BTSLUtil.NullToString(sourceType));
		prequestVO.setParam1(param1);// for DP6
		if ("b".equalsIgnoreCase(hexUrlEncodedRequired)) {
			prequestVO.setHexUrlEncodedRequired(false);
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting for prequestID=" + prequestID);
		}
	}

	private void processRequest(String requestIDStr, HttpServletRequest request, HttpServletResponse response, int prequestFrom) {
		final String methodName = "processRequest";
		StringBuilder loggerValue= new StringBuilder(); 
		PrintWriter out = null;
		final RequestVO requestVO = new RequestVO();
		String message = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
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
		requestIDMethod = Long.parseLong(requestIDStr);
		requestVO.setRequestID(requestIDMethod);
		requestVO.setRequestStartTime(requestStartTime);
		requestVO.setCreatedOn(currentDate);
		ChannelGatewayRequestLog.inLogIntermediate(requestVO,"Entering processRequest");
		final RequestQueueVO requestQueueVO = new RequestQueueVO();
		ChannelGatewayRequestLog.inLogIntermediate(requestVO,"Creating ThreadPoolClient");
		final ThreadPoolClient threadPoolClient = new ThreadPoolClient();
		List<ServiceKeywordControllerI> serviceList = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		String gatCodeForBarredUsers = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GAT_CODE_FOR_BARRED_USERS);
		String grptCtrlAllowed = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED);
		String grptChrgAllowed = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED));
		try {
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append( "************Start Time***********=");
				loggerValue.append(requestStartTime);
				log.debug(methodName, requestIDStr, loggerValue );
			}
			//BTSLUtil.validateConnection();

			// changes for Amharic Language UTF8 Characters//
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Type", "text/html; charset=UTF-8");
			//out = response.getWriter();

			requestVO.setReqContentType(request.getContentType());
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Content Type: ");
				loggerValue.append(request.getContentType());
				log.debug(methodName,  loggerValue);
			}
			
			requestVO.setModule(PretupsI.C2S_MODULE);
			requestVO.setInstanceID(instanceCode);
			requestVO.setCreatedOn(currentDate);
			requestVO.setLocale(new Locale(defaultLanguage, defaultCountry));
			requestVO.setDecreaseLoadCounters(false);
			requestVO.setRequestStartTime(requestStartTime);
			HashMap<String, Object> mm = new HashMap();
			mm.put("HTTPREQUEST",request);
			mm.put("HTTPRESPONSE",response);
			requestVO.setRequestMap(mm);
			/*HashMap<String, Object> mm = new HashMap();
			mm.put("HTTPREQUEST",request);
			mm.put("HTTPRESPONSE",response);
			requestVO.setRequestMap(mm);
			requestVO.setHttpRequest(request);
			requestVO.setHttpResponse(response);
*/
		    if(!PretupsI.NO.equalsIgnoreCase(Constants.getProperty("IS_TPS_LOGS_REQUIRED"))){
					 ChannelGatewayRequestLog.inLogIntermediate(requestVO,"Entering transactionPerSecLogging ");
					transactionPerSecLogging(requestStartTime,requestVO);
					ChannelGatewayRequestLog.inLogIntermediate(requestVO,"Exited transactionPerSecLogging");
			}
			
			// parse Request method is used to parse the request that is sent to
			// it
			// here information about the message gateway , source type ,login
			// id ,password and udh is fetched from the request.
			ChannelGatewayRequestLog.inLogIntermediate(requestVO,"Entering parseRequest");
			parseRequest(requestIDStr, request, requestVO);
			ChannelGatewayRequestLog.inLogIntermediate(requestVO,"Exited parseRequest");

			PretupsBL.validateRequestMessageGateway(requestVO);
			requestType = requestVO.getMessageGatewayVO().getGatewayType();

			gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
			parseRequestForMessage(requestIDStr, request, requestVO, prequestFrom);

			ChannelGatewayRequestLog.inLog(requestVO);
			//con = OracleUtil.getConnection();
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			gatewayParsersObj.parseChannelRequestMessage(requestVO, con);
			//out = ((HttpServletResponse)requestVO.getRequestMap().get("HTTPRESPONSE")).getWriter();
			
			if(requestVO.getRequestMessage() != null && requestVO.getRequestMessage().contains("PSBKRPT")) {
			}else {
				//out = requestVO.getHttpResponse().getWriter();
				out = response.getWriter();
			}

			
			gatewayParsersObj.validateUserIdentification(requestVO); 

			filteredMSISDN = requestVO.getFilteredMSISDN();

			if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
			}
			if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
			}

			gatewayParsersObj.loadValidateNetworkDetails(requestVO);

			networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();

			networkID = networkPrefixVO.getNetworkCode();
			requestVO.setRequestNetworkCode(networkID);

			String requestHandlerClass;

			channelUserVO = gatewayParsersObj.loadValidateUserDetails(con, requestVO);
			if(log.isDebugEnabled())log.debug("processRequest","Requested Gateway Code="+ requestVO.getRequestGatewayCode()+", gatCodeForBarredUsers="+gatCodeForBarredUsers);
			if(!ParserUtility.SELF_CU_UNBAR.equals(requestVO.getServiceKeyword())){
				if(requestVO.getMessageGatewayVO().getAccessFrom()==null || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE))		 
					PretupsBL.unBarredUserAutomaic(con,channelUserVO.getUserPhoneVO().getMsisdn(),networkPrefixVO.getNetworkCode(),PretupsI.C2S_MODULE,PretupsI.USER_TYPE_SENDER,channelUserVO);
				else if(!BTSLUtil.isNullString(gatCodeForBarredUsers)&& gatCodeForBarredUsers.contains(requestVO.getRequestGatewayCode())){
					PretupsBL.unBarredUserAutomaic(con,channelUserVO.getUserPhoneVO().getMsisdn(),networkPrefixVO.getNetworkCode(),PretupsI.C2S_MODULE,PretupsI.USER_TYPE_SENDER,channelUserVO);
				}
			}
			channelUserVO.setModifiedOn(currentDate);
			final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
			userPhoneVO.setModifiedBy(channelUserVO.getUserID());
			userPhoneVO.setModifiedOn(currentDate);
			if (channelUserVO.isStaffUser() && PretupsI.NOT_AVAILABLE.equals(userPhoneVO.getSmsPin())) {
				final ChannelUserVO parentVO = (ChannelUserVO) requestVO.getSenderVO();
				parentVO.setModifiedOn(currentDate);
				final UserPhoneVO parentPhoneVO = parentVO.getUserPhoneVO();
				parentPhoneVO.setModifiedBy(channelUserVO.getUserID());
				parentPhoneVO.setModifiedOn(currentDate);
			}

			if (userPhoneVO.getLastAccessOn() == null) {
				userPhoneVO.setAccessOn(true);
			}
			if (requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
				userPhoneVO.setLastAccessOn(currentDate);
			}

			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Sender Locale in Request if any");
				loggerValue.append(requestVO.getSenderLocale());
				log.debug(methodName, requestIDStr,  loggerValue);
			}

			if (requestVO.getSenderLocale() != null) {
				requestVO.setLocale(requestVO.getSenderLocale());
			} else {
				requestVO.setSenderLocale(requestVO.getLocale());
			}

			if (!requestVO.isMessageAlreadyParsed()) {
				PretupsBL.isPlainMessageAndAllowed(requestVO);

				if (!requestVO.isPlainMessage()) {
					PretupsBL.getEncryptionKeyForUser(con, requestVO);
					simProfileVO = SimProfileCache.getSimProfileDetails(userPhoneVO.getSimProfileID());
					PretupsBL.parseBinaryMessage(requestVO, simProfileVO);
				}
			}
			if ("Y".equals(queueLogicValue)) {
				final boolean acceptedFromLoad = LoadController.checkSystemLoad(requestIDMethod, LoadControllerI.ENTRY_IN_QUEUE, networkPrefixVO.getNetworkCode(),
						queueForAll);
				if (acceptedFromLoad) {
					requestVO.setDecreaseLoadCounters(true);
					requestVO.setToBeProcessedFromQueue(false);
				} else {
					requestVO.setToBeProcessedFromQueue(true);
				}
			} else {
				LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
				requestVO.setDecreaseLoadCounters(true);
				LoadController.checkNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);
				requestVO.setToBeProcessedFromQueue(false);
			}
			
			if(requestVO.getRequestMessage() != null && requestVO.getRequestMessage().contains("PSBKRPT")) {
				
				
			}
			else if (!"Y".equals(queueLogicValue) || !requestVO.isToBeProcessedFromQueue()) {
				try {
					gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, true, channelUserVO);
					isMarkedUnderprocess = true;
				} catch (BTSLBaseException e1) {
					log.errorTrace(methodName, e1);
					throw e1;
				} finally {
					//DBUtil.finalCommit(con);
					mcomCon.finalCommit();
				}
			}
			
			
			//DBUtil.closeAfterUpdate(con);
			if(mcomCon != null )mcomCon.close("ChannelReceiver#process");
			mcomCon = null;
			con = null;
			MessageFormater.handleChannelMessageFormat(requestVO, channelUserVO);

			if (channelUserVO.isUpdateSimRequired() && requestVO.getSourceType().equals(PretupsI.REQUEST_SOURCE_TYPE_STK)) {
				loggerValue.setLength(0);
				loggerValue.append("Update the Sim parameters for the MSISDN=");
				loggerValue.append(requestVO.getFilteredMSISDN());
			    loggerValue.append(" as STK and Server Parameters like PIN required not matching");	
				log.error(methodName, requestIDStr, loggerValue);
				updateSimParameters(requestVO, channelUserVO.getUserPhoneVO(), networkPrefixVO.getNetworkCode());
				throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_UPDATE_SIM_PARAMS_REQD);
			}

			PretupsBL.validateTempTransactionID(requestVO, channelUserVO);

			PretupsBL.processFixedInfo(requestVO, channelUserVO);

			populateLanguageSettings(requestVO, channelUserVO);

			if (!(channelUserVO.getUserPhoneVO()).getPhoneLanguage().equalsIgnoreCase(requestVO.getLocale().getLanguage())) {
				loggerValue.setLength(0);
				loggerValue.append(" Changing the Language code for MSISDN=");
				loggerValue.append(requestVO.getFilteredMSISDN());
				loggerValue.append(" From Language=");
				loggerValue.append((channelUserVO.getUserPhoneVO()).getPhoneLanguage() );
				loggerValue.append(" to language=");
				loggerValue.append (requestVO.getLocale().getLanguage());
				log.error(methodName,requestVO.getRequestIDStr(), loggerValue );
				(channelUserVO.getUserPhoneVO()).setPhoneLanguage(requestVO.getLocale().getLanguage());
				(channelUserVO.getUserPhoneVO()).setCountry(requestVO.getLocale().getCountry());
			}

			final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);
			StringBuilder eventhandle= new StringBuilder(); 
			if (serviceKeywordCacheVO == null) {
				eventhandle.setLength(0);
				eventhandle.append("Service keyword not found for the keyword=");
				eventhandle.append(requestVO.getRequestMessageArray()[0]);
				eventhandle.append(" For Gateway Type=");
				eventhandle.append(requestVO.getRequestGatewayType());
				eventhandle.append("Service Port=");
				eventhandle.append(requestVO.getServicePort());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelReceiver[processRequest]", requestIDStr,
						filteredMSISDN, "",  eventhandle.toString());
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
			} else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
				serviceType = serviceKeywordCacheVO.getServiceType();
				eventhandle.setLength(0);
				eventhandle.append("Service keyword not found for the keyword=");
				eventhandle.append(requestVO.getRequestMessageArray()[0]);
				eventhandle.append(" For Gateway Type=");
				eventhandle.append(requestVO.getRequestGatewayType());
				eventhandle.append("Service Port=");
				eventhandle.append(requestVO.getServicePort());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelReceiver[processRequest]", requestIDStr,
						filteredMSISDN, "",  eventhandle.toString());
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
			}
			serviceType = serviceKeywordCacheVO.getServiceType();
			requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
			requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
			requestVO.setType(serviceKeywordCacheVO.getType());
			requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
			requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());

			channelUserVO.setServiceTypes(requestVO.getServiceType());
			validateServiceType(requestVO, serviceKeywordCacheVO, simProfileVO, channelUserVO);
			externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
			requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());
			if (!"Y".equals(queueLogicValue) || !requestVO.isToBeProcessedFromQueue()) {
				if (grptCtrlAllowed != null && grptCtrlAllowed.indexOf(requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
						.equals(requestVO.getGroupType())) {
					final GroupTypeProfileVO groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CONTROLLING);
					if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
						requestVO.setDecreaseGroupTypeCounter(false);
						final String arr[] = { String.valueOf(groupTypeProfileVO.getThresholdValue()) };
						if (PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.getFrequency())) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_D, arr);
						}
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_M, arr);
					}
				}
			}

			if ("Y".equals(queueLogicValue) && requestVO.isToBeProcessedFromQueue()) {
				requestQueueVO.setSenderMsisdn(filteredMSISDN);
				final String[] requestArr = requestVO.getRequestMessageArray();
				requestQueueVO.setReceiverMsisdn(requestArr[1]);
				requestQueueVO.setServiceType(requestVO.getServiceType());
				requestQueueVO.setRequestVO(requestVO);
				requestQueueVO.setRequestIDMethod(requestIDMethod);
				requestQueueVO.setExternalInterfaceAllowed(externalInterfaceAllowed);
				requestQueueVO.setResponse(response);
				requestQueueVO.setQueueForAll(queueForAll);
				threadPoolClient.setRequestQueueVO(requestQueueVO);
				serviceList = new ArrayList<ServiceKeywordControllerI>();
				serviceList.add((ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass));
				final String staticListEntry = requestQueueVO.getSenderMsisdn() + "_" + requestQueueVO.getReceiverMsisdn() + "_" + requestQueueVO.getServiceType();
				addRequestToQueue(threadPoolClient, serviceList, staticListEntry);

			} else {
				final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
				controllerObj.process(requestVO);
			}
		} catch (BTSLBaseException be) {
			requestVO.setSuccessTxn(false);
			log.errorTrace(methodName, be);
			loggerValue.setLength(0);
			loggerValue.append("BTSLBaseException be:");
			loggerValue.append(be.getMessage());
			log.error(methodName, requestIDStr,  loggerValue );
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
			loggerValue.append (e.getMessage());
			log.error(methodName, requestIDStr,  loggerValue );
			log.errorTrace(methodName, e);
			if (BTSLUtil.isNullString(requestVO.getMessageCode()))
			{
				requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			loggerValue.setLength(0);
			loggerValue.append("Exception in ChannelReceiver:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processRequest]", requestIDStr, "",
					"",  loggerValue.toString());
		} finally {

			
			if(requestVO.getServiceType() != null && requestVO.getServiceType().equalsIgnoreCase("PSBKRPT")) {
				try {
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment; filename=passbook.pdf");
				OutputStream outp = response.getOutputStream();
				FileInputStream fileInputStream = new FileInputStream(requestVO.getResponseMultiPartpath());  
	            
				int i;   
				while ((i=fileInputStream.read()) != -1) {  
					outp.write(i);   
				}   
				fileInputStream.close();   
				outp.close();  
				 
				}catch(Exception e) {
					log.debug("dPost", requestIDStr, "PSBKRPT  - Exception occured while download "+e);
				}


				//return;
			}
			
			
			if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
				message = requestVO.getSenderReturnMessage();
			}

			/*if (gatewayParsersObj == null) {
				gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
			}*/

			if (!"Y".equals(queueLogicValue) || !requestVO.isToBeProcessedFromQueue()) {
				try {
					if (mcomCon == null && (channelUserVO != null || "Y".equals(Constants.getProperty("LOAD_TEST")))) {
						//con = OracleUtil.getConnection();
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
					}

					if (channelUserVO != null && isMarkedUnderprocess && con != null) {
						try {
							if (channelUserVO.getUserPhoneVO().isBarUserForInvalidPin()) {
								ChannelUserBL.barSenderMSISDN(con, channelUserVO, PretupsI.BARRED_TYPE_PIN_INVALID, currentDate, PretupsI.C2S_MODULE);
								//DBUtil.partialCommit(con);
								mcomCon.partialCommit();
								Locale locale = new Locale(defaultLanguage, defaultCountry);
								PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale,channelUserVO.getNetworkCode());
								pushMessage.push();
							}
						} catch (BTSLBaseException be) {
							log.errorTrace(methodName, be);
							loggerValue.setLength(0);
							loggerValue.append("BTSLBaseException be:");
							loggerValue.append (be.getMessage());
							
							
							log.error(methodName, requestIDStr, loggerValue);
							//try {DBUtil.partialRollback(con);} catch (Exception e) { log.errorTrace(methodName, e);}
							try {mcomCon.partialRollback();} catch (SQLException esql) { log.errorTrace(methodName, esql);}
						} catch (Exception e) {
							log.errorTrace(methodName, e);
								/*try {
									DBUtil.partialRollback(con);
							} catch (Exception ex) {
								log.errorTrace(methodName, ex);
							}*/
							try {mcomCon.partialRollback();} catch (SQLException esql) { log.errorTrace(methodName, esql);}
							loggerValue.setLength(0);
							loggerValue.append("Exception in Channelreceiver while barring user becuase of invalid PIN counts:" );
							loggerValue.append(e.getMessage());
							EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processrequest]",requestIDStr, "", "", loggerValue.toString() );
						}
						try {
							gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, false, channelUserVO);
							//DBUtil.partialCommit(con);
							mcomCon.partialCommit();
						} catch (BTSLBaseException be) {
							log.errorTrace(methodName, be);
							loggerValue.setLength(0);
							loggerValue.append("BTSLBaseException be:" );
							loggerValue.append(be.getMessage());
							log.error(methodName, requestIDStr, loggerValue);
							/*try {
								DBUtil.partialRollback(con);
							} catch (Exception e) {
								log.errorTrace(methodName, e);
							}*/
							try {mcomCon.partialRollback();} catch (SQLException esql) { log.errorTrace(methodName, esql);}
						} catch (Exception e) {
							log.errorTrace(methodName, e);
							/*try {
								DBUtil.partialRollback(con);
							} catch (Exception ex) {
								log.errorTrace(methodName, ex);
							}*/
							try {mcomCon.partialRollback();} catch (SQLException esql) { log.errorTrace(methodName, esql);}
							loggerValue.setLength(0);
							loggerValue.append("Exception in Channelreceiver while updating last status:");
							loggerValue.append(e.getMessage());
							EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processrequest]",requestIDStr, "", "",  loggerValue.toString());
						}
					}
					try {
						if (!requestVO.isSuccessTxn() && requestVO.isDecreaseGroupTypeCounter() && channelUserVO != null && (requestVO.getSenderVO() != null) && ((ChannelUserVO) requestVO
								.getSenderVO()).getUserControlGrouptypeCounters() != null) {
							PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) requestVO.getSenderVO()).getUserControlGrouptypeCounters());
						}
					} catch (Exception e) {
						log.errorTrace(methodName, e);
						loggerValue.setLength(0);
						loggerValue.append("Exception :");
						loggerValue.append(e.getMessage());
						log.error(methodName, requestIDStr,  loggerValue);
					}

					if (con != null) {
						if (Constants.getProperty("LOAD_TEST") != null && "Y".equals(Constants.getProperty("LOAD_TEST"))) {
							try {
								final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

								if (!BTSLUtil.isNullString(requestVO.getIntMsisdnNotFound())) {
									requestVO.setMessageCode(requestVO.getIntMsisdnNotFound());
								}

								channelTransferDAO.addC2SReceiverRequests(con, requestVO);
								//DBUtil.finalCommit(con);
								mcomCon.finalCommit();
							} catch (Exception ex) {
								log.errorTrace(methodName, ex);
								/*try {
									DBUtil.finalRollback(con);
								} catch (Exception e) {
									log.errorTrace(methodName, e);
								}*/
								try {mcomCon.finalRollback();} catch (SQLException esql) { log.errorTrace(methodName, esql);}
							}
						}
						//DBUtil.closeAfterUpdate(con);
						if(mcomCon != null )mcomCon.close("ChannelReceiver#process");
						mcomCon = null;
						con = null;
					}
				}catch (Exception e) {
					log.errorTrace(methodName, e);
					loggerValue.setLength(0);
					loggerValue.append("Exception in Channelreceiver while getting connection :");
					loggerValue.append(e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[processrequest]",
							requestIDStr, "", "",  loggerValue.toString() );
				}finally{
					//DBUtil.closeAfterUpdate(con);
					if(mcomCon != null )mcomCon.close("ChannelReceiver#process");
					mcomCon = null;
				}

				if (requestVO.isDecreaseLoadCounters()) {
					if (networkPrefixVO != null) {
						LoadController.decreaseCurrentNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
					}
					LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
				}

				requestEndTime = System.currentTimeMillis();

				if (BTSLUtil.isNullString(requestVO.getTransactionID())) {
					ReqNetworkServiceLoadController.increaseIntermediateCounters(instanceCode, requestType, networkID, serviceType, requestIDStr,
							LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());
				} else {
					ReqNetworkServiceLoadController.increaseIntermediateCounters(instanceCode, requestType, networkID, serviceType, requestVO.getTransactionID(),
							LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());
				}

				if (log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("requestEndTime=" );
					loggerValue.append(requestEndTime);
					loggerValue.append(" requestStartTime=");
					loggerValue.append(requestStartTime);
					loggerValue.append(" Message Code=");
					loggerValue.append(requestVO.getMessageCode() );
					loggerValue.append(" Args=");
					loggerValue.append(requestVO.getMessageArguments());
					loggerValue.append(" Message If any=");
					loggerValue.append(message);
					loggerValue.append(" Locale=");
					loggerValue.append(requestVO.getLocale());
					
					log.debug(this, requestIDStr, loggerValue );
				}
			}

			if (requestVO.getMessageGatewayVO() != null) {
				if (log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Gateway Time out=");
					loggerValue.append (requestVO.getMessageGatewayVO().getTimeoutValue());

					log.debug(methodName, requestIDStr,  loggerValue);
				}
			}

			if (requestVO.getSenderLocale() != null) {
				requestVO.setLocale(requestVO.getSenderLocale());
			}

			if (gatewayParsersObj != null) {
				gatewayParsersObj.generateChannelResponseMessage(requestVO);
			} else {
				if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO.getReqContentType().indexOf("XML") != -1)) {
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
						if (log.isDebugEnabled()) {
							log.debug("processRequest: Sender Message push through alternate GW", reqruestGW, "Requested GW was:" + requestVO.getRequestGatewayCode());
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
					String txnstatus = null;
					if (requestVO.isSuccessTxn() && !requestVO.isToBeProcessedFromQueue()) {
						txnstatus = PretupsI.TXN_STATUS_SUCCESS;
					} else {
						txnstatus = requestVO.getMessageCode();
					}

					message = requestVO.getSenderReturnMessage();
					String message1 = null;
					if ((messageLength > 0) && !BTSLUtil.isNullString(message) && (message.length() > messageLength)) {
						message1 = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.REQUEST_IN_QUEUE_UB, requestVO.getMessageArguments());
						final PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1, requestVO.getRequestIDStr(), requestVO
								.getRequestGatewayCode(), requestVO.getLocale());
						pushMessage1.push();
						requestVO.setRequestGatewayCode(reqruestGW);
					}
					if ((requestVO.getRequestGatewayType()).equalsIgnoreCase(PretupsI.GATEWAY_TYPE_WEB)) {
						if(requestVO.isSuccessTxn() && PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(requestVO.getServiceType()) && PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST.equalsIgnoreCase(requestVO.getMessageGatewayVO().getFlowType())){

							message = "MESSAGE=" + URLEncoder.encode(BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.C2S_SENDER_SUCCESS, requestVO.getMessageArguments()), "UTF16") + "&TXN_ID=" + BTSLUtil.NullToString(requestVO
									.getTransactionID()) + "&TXN_STATUS=" + BTSLUtil.NullToString(txnstatus);
						}else{
							message = "MESSAGE=" + URLEncoder.encode(requestVO.getSenderReturnMessage(), "UTF16") + "&TXN_ID=" + BTSLUtil.NullToString(requestVO
									.getTransactionID()) + "&TXN_STATUS=" + BTSLUtil.NullToString(txnstatus);
						}

					} else {
						message = requestVO.getSenderReturnMessage();
					}
				} else {
					message = requestVO.getSenderReturnMessage();
				}
				if(PretupsI.SERVICE_TYPE_MVD.equalsIgnoreCase(requestVO.getServiceType()) || PretupsI.SERVICE_TYPE_NMVD.equalsIgnoreCase(requestVO.getServiceType()))
				{
                    if(((ArrayList)requestVO.getValueObject()).size()>0)
                    {
                        VomsVoucherVO voucherVO=null;
                        voucherVO=(VomsVoucherVO)((ArrayList)requestVO.getValueObject()).get(0);
                        if(!requestVO.getRequestGatewayCode().equals(PretupsI.REQUEST_SOURCE_TYPE_EXTGW))
                        	{
                        		message=message+"&SALE_BATCH_NO="+voucherVO.getSaleBatchNo();
                        	}
                    }
                }
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Locale=" );
				loggerValue.append(requestVO.getLocale() );
				loggerValue.append(" requestEndTime=");
				loggerValue.append(requestEndTime );
				loggerValue.append(" requestStartTime=");
				loggerValue.append(requestStartTime);
				loggerValue.append(" Message Code=");
				loggerValue.append(requestVO.getMessageCode() );
				loggerValue.append(" Args=");
				loggerValue.append(requestVO.getMessageArguments());
				loggerValue.append(" Message If any=");
				loggerValue.append (message);
				log.debug(methodName, requestIDStr,
						loggerValue );
			}
			if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
					.getMessageGatewayVO().getTimeoutValue()) {
				requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);
				
				if(out == null) {
					try{ 
						out = response.getWriter();
					}catch(Exception e) {
						log.error(methodName, "Exception occured while initializing PrintWriter "+e);
					}
				}
				out.println(message);
				if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && requestVO.isSuccessTxn() && requestVO
						.isSenderMessageRequired())
				{

					if (requestVO.getReqContentType().indexOf("xml") != -1 || requestVO
							.getReqContentType().indexOf("XML") != -1 || requestVO.getReqContentType().indexOf("plain") != -1 || requestVO.getReqContentType().indexOf("PLAIN") != -1)  {

						if (!PretupsI.YES.equals(externalInterfaceAllowed)) {
							final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
							final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage, requestVO.getRequestIDStr(), requestVO
									.getRequestGatewayCode(), requestVO.getLocale());
							if (!"Y".equals(queueLogicValue) || !requestVO.isToBeProcessedFromQueue()) {
								if (grptChrgAllowed != null && grptChrgAllowed.indexOf(requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
										.equals(requestVO.getGroupType())) {
									GroupTypeProfileVO groupTypeProfileVO = null;
									groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
									if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
										pushMessage.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());// new
										SMSChargingLog.log(((ChannelUserVO) requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) requestVO.getSenderVO())
												.getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
												groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(),
												requestVO.getModule());
									} else {
										if(!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage)){
											pushMessage.push();
										}
									}
								} else {
									if(!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage)){
										pushMessage.push();
									}
								}
							} else {
								if(!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage)){
									pushMessage.push();
								}
							}
						}
					}
				}
			} else {
				requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
				if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(requestVO.getServiceType()) && requestVO.isSenderMessageRequired()) {

					final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), requestVO.getSenderReturnMessage(), requestVO.getRequestIDStr(),
							requestVO.getRequestGatewayCode(), requestVO.getLocale());
					if (!"Y".equals(queueLogicValue) || !requestVO.isToBeProcessedFromQueue()) {
						if (!PretupsI.YES.equals(externalInterfaceAllowed) && grptChrgAllowed != null && grptChrgAllowed
								.indexOf(requestVO.getRequestGatewayType()) != -1 && requestVO.isSuccessTxn() && !PretupsI.NOT_APPLICABLE.equals(requestVO.getGroupType())) {
							GroupTypeProfileVO groupTypeProfileVO = null;
							groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
							if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
								pushMessage.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());
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
					} else {
						pushMessage.push();
					}
				}
			}
			
			
			if(out == null) {
				try{ 
					out = response.getWriter();
				}catch(Exception e) {
					log.error(methodName, "Exception occured while initializing PrintWriter "+e);
				}
			}
			out.flush();
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			ChannelGatewayRequestLog.outLog(requestVO);
			if (log.isDebugEnabled()) {
				log.debug(methodName, requestIDStr, "Exiting");
			}
			if(mcomCon != null )mcomCon.close("ChannelReceiver#process");
			mcomCon = null;
			con = null;
			
		}
	}

	public void parseRequestForMessage(String requestID, HttpServletRequest request, RequestVO prequestVO, int calledMethodType) throws BTSLBaseException {
		final String methodName = "parseRequestForMessage";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered calledMethodType: ");
			loggerValue.append(calledMethodType);
			log.debug(methodName, requestID,  loggerValue );
		}
		String msisdn = request.getParameter("MSISDN");
		final String requestMessage = request.getParameter("MESSAGE");
		final String activeUserId = request.getParameter("ACTIVE_USER_ID");
		// request comes from the doGET
		if (calledMethodType == 1) {
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("msisdn:");
				loggerValue.append(msisdn);
				loggerValue.append("requestMessage:");
				loggerValue.append(requestMessage);
				log.debug(methodName, requestID,  loggerValue );
			}

			prequestVO.setRequestMessage(requestMessage);
			String loginId = null;
			if (PretupsI.GATEWAY_TYPE_SMS_POS.equals(prequestVO.getRequestGatewayType())) {
				if (BTSLUtil.isNullString(requestMessage)) {
					throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
				}
				String chnlPlainSmsSeptLoginId = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID);
				final String[] array = requestMessage.split(chnlPlainSmsSeptLoginId);

				if (array.length == 2) {
					loginId = array[1];
				}
			}
			
			if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(loginId)) {
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_MSISDN);
			}

			if (!BTSLUtil.isNullString(loginId)) {
				prequestVO.setSenderLoginID(loginId);
			} else if (!BTSLUtil.isNullString(msisdn)) {
				msisdn = msisdn.trim();
				prequestVO.setRequestMSISDN(msisdn);
			}
			if (!BTSLUtil.isNullString(msisdn)) {
				msisdn = msisdn.trim();
				prequestVO.setPosMSISDN(msisdn);
			}
		} else {
			String msg = "";
			StringBuilder sb = new StringBuilder(1024);
			if (BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage)) {
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
					prequestVO.setRequestMessage(requestBuffer.toString());
					prequestVO.setReqContentType("plain");
				} else {
					try {/*
						final ServletInputStream in = request.getInputStream();
						int c;
						sb.setLength(0);
						while ((c = in.read()) != -1) {
							// Process line...
							sb.append(String.valueOf(c));
						}
	                    msg = sb.toString();
						if (BTSLUtil.isNullString(msg)) {
							throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
						}
						prequestVO.setRequestMessage(msg);
					*/
						
					

						final ServletInputStream in = request.getInputStream();
						int c;

						while ((c = in.read()) != -1) {
							// Process line...
							msg += (char) c;
						}
						if (BTSLUtil.isNullString(msg)) {
							throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
						}
						prequestVO.setRequestMessage(msg);
					
					} catch (BTSLBaseException be) {
						log.errorTrace(methodName, be);
						throw be;
					} catch (Exception e) {
						log.errorTrace(methodName, e);
						throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
					}
				}

			}
			if (!BTSLUtil.isNullString(msisdn)) {
				prequestVO.setRequestMSISDN(msisdn);
			} 
			if (!BTSLUtil.isNullString(requestMessage)) {
				prequestVO.setRequestMessage(requestMessage);
			}

			if (BTSLUtil.isNullString(prequestVO.getRequestMessage())) {
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
			}
		}
		if (!BTSLUtil.isNullString(activeUserId)) {
			prequestVO.setActiverUserId(activeUserId);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting with Message=");
			loggerValue.append(prequestVO.getRequestMessage());
			log.debug(methodName, requestID,  loggerValue);
		}
	}

	protected void prepareXMLResponse(RequestVO prequestVO) {
		final String methodName = "prepareXMLResponse";
		if (log.isDebugEnabled()) {
			log.debug(methodName, prequestVO.getRequestIDStr(), "Entered");
		}
		try {
			ParserUtility.actionChannelParser(prequestVO);
			ParserUtility.generateChannelResponse(prequestVO.getActionValue(), prequestVO);
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[prepareXMLResponse]", prequestVO
					.getRequestIDStr(), "", "", "Exception while generating XML response:" + e.getMessage());
			try {
				ParserUtility.generateFailureResponse(prequestVO);
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelReceiver[prepareXMLResponse]", prequestVO
						.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
				prequestVO
				.setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, prequestVO.getRequestIDStr(), "Exiting with message=" + prequestVO.getSenderReturnMessage());
		}
	}

	private void addRequestToQueue(ThreadPoolClient appThreadPoolClient, List<ServiceKeywordControllerI> serviceList, String listEntry) throws BTSLBaseException {
		final String methodName = "addRequestToQueue";
		if (log.isDebugEnabled()) {
			log.debug("ChannelReceiver", methodName, "Entered: ");
		}
		try {
			if (listOfRequestsSendToPool != null && listOfRequestsSendToPool.contains(listEntry)) {
				LoadController.checkSystemLoad(appThreadPoolClient.getRequestQueueVO().getRequestIDMethod(), LoadControllerI.FAIL_BEF_PROCESSED_QUEUE, appThreadPoolClient
						.getRequestQueueVO().getRequestVO().getRequestNetworkCode(), queueForAll);
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_DUPLICATE_REQUEST);
			}
			if (!concurentQueue.isFull()) {
				listOfRequestsSendToPool.add(listEntry);
				appThreadPoolClient.getRequestQueueVO().setQueueAdditionTime(System.currentTimeMillis());
				appThreadPoolClient.setServiceList(serviceList);
				final String[] messageArgArray = { appThreadPoolClient.getRequestQueueVO().getReceiverMsisdn(), String.valueOf(Long.valueOf(Constants
						.getProperty("QUEUE_REQUEST_TIMEOUT")) * 0.001) };
				appThreadPoolClient.getRequestQueueVO().getRequestVO().setSenderReturnMessage(
						BTSLUtil.getMessage(appThreadPoolClient.getRequestQueueVO().getRequestVO().getSenderLocale(), PretupsErrorCodesI.TXN_STATUS_IN_QUEUE, messageArgArray));
				String priorityRequestTimeout;
				int priority = 0;
				Long requestTimeout = 0L;

				priorityRequestTimeout = ServiceInstancePriorityCache
						.getServiceInstancePriority(appThreadPoolClient.getRequestQueueVO().getServiceType() + "_" + instanceCode);
				if (priorityRequestTimeout != null) {
					final String[] indexOfSplit = priorityRequestTimeout.split("_");
					priority = Integer.parseInt(indexOfSplit[0]);
					requestTimeout = Long.parseLong(indexOfSplit[1]);
				}

				if (priority == 0) {
					appThreadPoolClient.getRequestQueueVO().setPriority(ThreadPoolConstants.DEFAULT_PRIORITY);
				} else {
					appThreadPoolClient.getRequestQueueVO().setPriority(priority);
				}

				if (requestTimeout == 0L) {
					try {
						if (Long.valueOf(Constants.getProperty("QUEUE_REQUEST_TIMEOUT")) > 0) {
							requestTimeout = Long.valueOf(Constants.getProperty("QUEUE_REQUEST_TIMEOUT"));
						}
					} catch (Exception e) {
						log.errorTrace(methodName, e);
						requestTimeout = 3000L;
					}
					appThreadPoolClient.getRequestQueueVO().setRequestTimeout(requestTimeout);
				} else {
					appThreadPoolClient.getRequestQueueVO().setRequestTimeout(requestTimeout);
				}

				concurentQueue.enqueue(appThreadPoolClient);
				QueueLogger.INlog(appThreadPoolClient.getRequestQueueVO());
				if ("NEW".equals(threadCountManager.getState().toString())) {
					threadCountManager.start();
				} else if ("TERMINATED".equals(threadCountManager.getState().toString())) {
					threadCountManager = new ThreadCountManager(threadPoolForQueue, concurentQueue);
					threadCountManager.start();
				}

			}

			else {
				LoadController.checkSystemLoad(appThreadPoolClient.getRequestQueueVO().getRequestIDMethod(), LoadControllerI.FAIL_BEF_PROCESSED_QUEUE, appThreadPoolClient
						.getRequestQueueVO().getRequestVO().getRequestNetworkCode(), queueForAll);
				throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE);
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			LoadController.checkSystemLoad(appThreadPoolClient.getRequestQueueVO().getRequestIDMethod(), LoadControllerI.REFUSED_FROM_QUEUE, appThreadPoolClient
					.getRequestQueueVO().getRequestVO().getRequestNetworkCode(), queueForAll);
			final String arr[] = { appThreadPoolClient.getRequestQueueVO().getReceiverMsisdn() };
			throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_DUPLICATE_REQUEST, arr);
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			LoadController.checkSystemLoad(appThreadPoolClient.getRequestQueueVO().getRequestIDMethod(), LoadControllerI.REFUSED_FROM_QUEUE, appThreadPoolClient
					.getRequestQueueVO().getRequestVO().getRequestNetworkCode(), queueForAll);
			throw new BTSLBaseException("ChannelReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTADDED_IN_QUEUE);
		}

		if (log.isDebugEnabled()) {
			log.debug("ChannelReceiver", methodName, "Exiting: ");
		}
	}
	
	public void transactionPerSecLogging(final long pMillisecondsTime , final RequestVO pRequestVO)
	{
	
			executor.execute(new TransactionPerSecLogger(pMillisecondsTime,pRequestVO));
	
	}
}
