package com.btsl.pretups.channel.receiver;

import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
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
import com.btsl.ota.services.businesslogic.SimProfileCache;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.channel.logging.ChannelGatewayRequestLog;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserHierarchyRequestParentVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserHierarchyVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.logging.P2PGatewayRequestLog;
import com.btsl.pretups.p2p.query.businesslogic.SubscriberTransferDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.FixedInformationVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.MessageFormater;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.voms.vomscommon.VOMSI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;


import io.swagger.v3.oas.annotations.Parameter;


@Path("")
public class RestReceiver {

    @Context
    private HttpServletRequest httpServletRequest;
    private final Log log = LogFactory.getLog(this.getClass().getName());
    private static final String PROCESSREQUEST = "RestReceiver[processRequest]";
    private static final String RSTRECEIVER = "RestReceiver";
    private static final String TXNSTATUS = "TXNSTATUS";
    private static final String DATE = "DATE";
    private static final String MESSAGE = "MESSAGE";
    private static final String GATEWAYTYPE = " For Gateway Type=";
    private static final String MESSAGECODE = " Message Code=";
    private static final String ARGS = "Args=";
    private static final String SERVICEPORT = "Service Port=";
    private static final String BTSLBASEEXP ="BTSLBaseException be:";
    private static final String LOADTEST ="LOAD_TEST";
    private static final String REQSTART =" requestStartTime=";
    private static final String MSGANY =" Message If any=";
    private static long requestIdChannel = 0;
    private static long requestIdOperator = 0;
    private static long requestIdSystem = 0;
    

    /**
     * @param request
     * @return
     */

    @POST
    @Path("/voms-rest-receiver/{servicekeyword}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PretupsResponse<JsonNode> processVoucherReceiverRequest(JsonNode request,
            @PathParam("servicekeyword") String serviceKeyword) {
        PretupsResponse<JsonNode> response;
        ++requestIdChannel;
        final String requestIDStr = String.valueOf(requestIdChannel);
        response = processVoucherReceiverRequest(request, serviceKeyword.toUpperCase(),requestIDStr);
        return response;
    }  
    
    
    
    @POST
    @Path("/c2s-rest-receiver/userhierarchy")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@ApiOperation(value = "User Hierarchy Request", response = PretupsResponse.class)
    @io.swagger.v3.oas.annotations.Operation(summary = "${c2s-rest-receiver_userhierarchy.summary}", description="${c2s-rest-receiver_userhierarchy.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
                            )
                    }

                    ),


                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    })
            }
    )

    public PretupsResponse<JsonNode> userHierarchy( @Parameter(description = SwaggerAPIDescriptionI.USER_HIERARCHY_REQUEST ) UserHierarchyRequestParentVO request ) {
    	
    	String serviceKeyword  = "UPUSRHRCHY";
    	PretupsResponse<JsonNode> response;
        JsonNode jsonReq = null;
		
        
        try {
			jsonReq = (JsonNode) PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(request), new TypeReference<JsonNode>() {});
		}catch (Exception e) {
			log.error("userHierarchy", "Exception while parsig request"+e);
		}
        
       
        response = processRequestChannel(jsonReq, serviceKeyword.toUpperCase(),"");
        return response;
    }
    
    @POST
    @Path("/c2s-rest-receiver/{servicekeyword}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PretupsResponse<JsonNode> processChannelUserRequest(JsonNode request,
            @PathParam("servicekeyword") String serviceKeyword) {
        PretupsResponse<JsonNode> response;
        ++requestIdChannel;
        final String requestIDStr = String.valueOf(requestIdChannel);
        response = processRequestChannel(request, serviceKeyword.toUpperCase(),requestIDStr);
        return response;
    }
    
    @POST
    @Path("/opt-rest-receiver/{servicekeyword}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public  PretupsResponse<JsonNode> processOperatorUserRequest(JsonNode request,
            @PathParam("servicekeyword") String serviceKeyword) {
        PretupsResponse<JsonNode> response;
        ++requestIdOperator;
        final String requestIDStr = String.valueOf(requestIdOperator);
        response = processRequestOperator(request, serviceKeyword.toUpperCase(),requestIDStr);
        return response;
    }
	
    @POST
    @Path("/p2p-rest-receiver/{servicekeyword}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public  PretupsResponse<JsonNode> processCP2PUserRequest(JsonNode request,
            @PathParam("servicekeyword") String serviceKeyword) {
        PretupsResponse<JsonNode> response;
        ++requestIdOperator;
        final String requestIDStr = String.valueOf(requestIdOperator);
        response = processCP2PRequestOperator(request, serviceKeyword.toUpperCase(),requestIDStr);
        return response;
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
    private PretupsResponse<JsonNode> processCP2PRequestOperator(JsonNode request, String serviceKeyword, String requestIdOperator ) {
        final String methodName = "processCP2PRequestOperator";
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        String message = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        SenderVO senderVO = null;
        final Date currentDate = new Date();
        NetworkPrefixVO networkPrefixVO = null;
        final RequestVO requestVO = new RequestVO();
        StringBuilder loggerValue= new StringBuilder(); 
        OperatorUtilI calculatorI = null;
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
        String instanceCode=null;
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        try {
        	
        	loggerValue.setLength(0);
        	loggerValue.append("************Start Time***********=");
        	loggerValue.append(requestStartTime);
            LogFactory.printLog(methodName, loggerValue.toString() , log);
            
            
            LogFactory.printLog(methodName, loggerValue.toString() , log);
           
            
            requestIDMethod = Long.parseLong(requestIdOperator);
        
            instanceCode = Constants.getProperty("INSTANCE_ID");
            
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.P2P_MODULE);
            requestVO.setInstanceID(instanceCode);
            
            requestVO.setCreatedOn(currentDate);
            requestVO.setServiceKeyword(serviceKeyword);
            requestVO.setLocale(new Locale(lang,country));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setPlainMessage(true);
            requestVO.setRequestStartTime(requestStartTime);
            requestVO.setActionValue(PretupsI.P2P_RECEIVER_ACTION);
            
            response = parseRequestfromJson(request, requestVO);

            if (!response.getStatus()) {
                return response;
            }
            PretupsBL.validateRequestMessageGateway(requestVO);
            requestType = requestVO.getMessageGatewayVO().getGatewayType();
            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());

            parseRequestForMessage( request, requestVO);

            gatewayParsersObj.parseOperatorRequestMessage(requestVO);
            
            String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            
            try {
                calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
            } catch (Exception e) {
            	loggerValue.append(e.getMessage());
                LogFactory.printLog(methodName, loggerValue.toString() , log);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestReceiver[processCP2PRequestOperator]", "", "", "","Exception while loading the operator util class at the init:" + e.getMessage());
            }
            
            filteredMSISDN = calculatorI.getSystemFilteredMSISDN(requestVO.getRequestMSISDN());
            requestVO.setFilteredMSISDN(filteredMSISDN);
            if (!BTSLUtil.isValidMSISDN(filteredMSISDN)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "RestReceiver[processCP2PRequestOperator]", Long.toString(requestIDMethod),
                    filteredMSISDN, "", "Sender MSISDN Not valid");
                requestVO.setSenderMessageRequired(false);
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN);
            }
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

            networkPrefixVO = PretupsBL.getNetworkDetails(filteredMSISDN, PretupsI.USER_TYPE_SENDER);

            requestVO.setRequestNetworkCode(networkPrefixVO.getNetworkCode());

            LoadController.checkNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);
            
            requestVO.setDecreaseNetworkLoadCounters(true);

            networkID = networkPrefixVO.getNetworkCode();

            if (!PretupsI.YES.equals(networkPrefixVO.getStatus())) {
                final LocaleMasterVO localeVO = LocaleMasterCache
                    .getLocaleDetailsFromlocale(new Locale(lang,country));
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    message = networkPrefixVO.getLanguage1Message();
                } else {
                    message = networkPrefixVO.getLanguage2Message();
                }
                requestVO.setSenderReturnMessage(message);
                throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.P2P_NETWORK_NOT_ACTIVE);
            }

            String requestHandlerClass;
            
            mcomCon = new MComConnection();
            
            con=mcomCon.getConnection();
            if (requestVO.getMessageGatewayVO().getAccessFrom() == null || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_LOGIN)) {
                PretupsBL.unBarredUserAutomaic(con, filteredMSISDN, networkPrefixVO.getNetworkCode(), PretupsI.P2P_MODULE, PretupsI.USER_TYPE_SENDER, null);
            }

            senderVO = SubscriberBL.validateSubscriberDetails(con, filteredMSISDN);
            if (senderVO != null) {
                senderVO.setModifiedBy(senderVO.getUserID());
                senderVO.setModule(PretupsI.P2P_MODULE);
                requestVO.setSenderVO(senderVO);
                requestVO.setLocale(senderVO.getLocale());
                final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMSISDN);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, senderVO.getSubscriberType());
                if (senderVO.getPrefixID() != networkPrefixVO.getPrefixID()) {
                    SubscriberBL.updateSubscriberPrefixID(con, senderVO, networkPrefixVO.getPrefixID());
                }
                SubscriberBL.checkRequestUnderProcess(con, requestIdOperator, senderVO, true);
                isMarkedUnderprocess = true;
                con.commit();

            }

			if (mcomCon != null) {
				mcomCon.close("RestReceiver#processRequest");
				mcomCon = null;
			}
            con = null;

            if (log.isDebugEnabled()) {
                log.debug("processRequest", requestIDMethod, "Sender Locale in Request if any" + requestVO.getSenderLocale());
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            } else {
                requestVO.setSenderLocale(requestVO.getLocale());
            }

            final String[] messageArray = PretupsBL.parsePlainMessage(requestVO.getDecryptedMessage());
            requestVO.setRequestMessageArray(messageArray);
    
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);


            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RestReceiver[processCP2PRequestOperator]", requestIdOperator,
                    filteredMSISDN, "",
                    "Service keyword not found for the keyword=" + messageArray[0] + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                        .getServicePort());
                throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RestReceiver[processCP2PRequestOperator]", requestIdOperator,
                    filteredMSISDN, "",
                    "Service keyword suspended for the keyword=" + messageArray[0] + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                        .getServicePort());
                throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            if (senderVO == null) {
                if (serviceKeywordCacheVO.getUnregisteredAccessAllowed().equals(PretupsI.NO)) {
                    throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.ERROR_NOTFOUND_SUBSCRIBER);
                }
            } else if ((serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PSUSPEND)) && senderVO.getStatus()
                .equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.P2P_USER_STATUS_ALREADY_SUSPENDED);
            } else if ((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_RESUMESERVICE)) && senderVO.getStatus().equalsIgnoreCase(
                PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_SENDER_SUSPEND);
            } else if ((!serviceKeywordCacheVO.getServiceType().equals(PretupsI.MULT_CRE_TRA_DED_ACC)) && (!serviceKeywordCacheVO.getServiceType().equals(
                PretupsI.SERVICE_TYPE_P2PRECHARGE)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE)) && (!serviceKeywordCacheVO
                .getServiceType().equals(PretupsI.SERVICE_TYPE_ACCOUNTINFO)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PCHANGEPIN)) && (!serviceKeywordCacheVO
                .getServiceType().equals(PretupsI.SERVICE_TYPE_REGISTERATION)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_P2PRECHARGEWITHVALEXT)) && (!serviceKeywordCacheVO.getServiceType().equals(PretupsI.SERVICE_TYPE_DATA_CP2P))  && (!serviceKeywordCacheVO.getServiceType().equals((PretupsI.SUBSCRIBER_VOUCHER_ENQ))) && senderVO
                .getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_NEW) ) {
                throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_SENDER_STATUS_NEW);
            }
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());
            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
 
        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            requestVO.setSuccessTxn(false);
			requestVO.setDecreaseGroupTypeCounter(false);
            if (log.isDebugEnabled()) {
                log.debug("processRequest", requestIdOperator, "BTSLBaseException be:" + be.getMessage());
            }
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
			requestVO.setDecreaseGroupTypeCounter(false);
            log.error("processRequest", requestIdOperator, "Exception e:" + e.getMessage());
            log.errorTrace(methodName, e);
            requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestReceiver[processCP2PRequestOperator]", requestIdOperator, "", "",
                "Exception in RestReceiver:" + e.getMessage());
        } finally {
            try {
                if (mcomCon == null && (senderVO != null || "Y".equals(Constants.getProperty("LOAD_TEST")))) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestReceiver[processCP2PRequestOperator]", requestIdOperator, "",
                    "", "Exception in RestReceiver while getting connection :" + e.getMessage());
            }

            if (senderVO != null && isMarkedUnderprocess && requestVO.isUnmarkSenderRequired() && con != null) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("processRequest", requestIdOperator, "User Barring required because of PIN change......" + senderVO.isBarUserForInvalidPin());
                    }
                    if (senderVO.isBarUserForInvalidPin()) {
                        SubscriberBL.barSenderMSISDN(con, senderVO, PretupsI.BARRED_TYPE_PIN_INVALID, currentDate);
                        con.commit();
						Locale locale = new Locale(lang,country);
						PushMessage pushMessage = new PushMessage(senderVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale,senderVO.getNetworkCode());
						pushMessage.push();
                    }
                } catch (BTSLBaseException be) {
                    log.errorTrace(methodName, be);
                    log.error("processRequest", requestIdOperator, "BTSLBaseException be: " + be.getMessage());
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    try {
                        con.rollback();
                    } catch (Exception ex) {
                        log.errorTrace(methodName, ex);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestReceiver[processCP2PRequestOperator]", requestIdOperator,
                        "", "", "Exception in RestReceiver:" + e.getMessage());
                }
                try {
                    SubscriberBL.checkRequestUnderProcess(con, requestIdOperator, senderVO, false);
                    con.commit();
                } catch (BTSLBaseException be) {
                    log.errorTrace(methodName, be);
                    log.error("processRequest", requestIdOperator, "BTSLBaseException be:" + be.getMessage());
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    try {
                        con.rollback();
                    } catch (Exception ex) {
                        log.errorTrace(methodName, ex);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestReceiver[processCP2PRequestOperator]", requestIdOperator,
                        "", "", "Exception in RestReceiver:" + e.getMessage());
                }
            }
            if (requestVO.getSenderVO() != null && !requestVO.isSuccessTxn() && requestVO.isDecreaseGroupTypeCounter() && ((SenderVO) requestVO.getSenderVO())
                .getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((SenderVO) requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }
            if (con != null) {
                if (Constants.getProperty(LOADTEST) != null && "Y".equals(Constants.getProperty(LOADTEST))) {
                    try {
                    	mcomCon.finalRollback();
                        final SubscriberTransferDAO subscriberTransferDAO = new SubscriberTransferDAO();
                        subscriberTransferDAO.addP2PReceiverRequests(con, requestVO);
                        mcomCon.finalCommit();
                    } catch (Exception ex) {
                        log.errorTrace(methodName, ex);
                        try {
                        	mcomCon.finalRollback();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                    }
                }

				if (mcomCon != null) {
					mcomCon.close("RestReceiver#processRequest");
					mcomCon = null;
				}
                con = null;
            }

            if (requestVO.isDecreaseLoadCounters()) {
                if (requestVO.isDecreaseNetworkLoadCounters()) {
                    LoadController.decreaseCurrentNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                }
                LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
            requestEndTime = System.currentTimeMillis();

            ReqNetworkServiceLoadController.increaseIntermediateCounters(instanceCode, requestType, networkID, serviceType, requestIdOperator,
                LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());

            if (requestVO.getMessageGatewayVO() != null) {
                if (log.isDebugEnabled()) {
                    log.debug(this, requestIdOperator, "requestVO.getMessageGatewayVO()=" + requestVO.getMessageGatewayVO().getTimeoutValue());
                }
            }

            if (gatewayParsersObj == null) {
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug(this, requestIdOperator, "gatewayParsersObj=" + gatewayParsersObj);
            }
            if (gatewayParsersObj != null) {
                gatewayParsersObj.generateChannelResponseMessage(requestVO);
            } else {
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().equals(PretupsI.JSON_CONTENT_TYPE))) {
                    prepareJsonResponse(requestVO);
                }
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
                final String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
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

                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()) && requestVO.getReqContentType().equals(PretupsI.JSON_CONTENT_TYPE)) && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType()))// @@
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
                            final PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1, requestIdOperator, requestVO
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
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(this, requestIdOperator,
                    "Locale=" + requestVO.getLocale() + " requestEndTime=" + requestEndTime + " requestStartTime=" + requestStartTime + " Message Code=" + requestVO
                        .getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message);
            }
            if (requestVO.getMessageGatewayVO() == null || requestVO.getMessageGatewayVO().getResponseType().equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE) || (requestEndTime - requestStartTime) / 1000 < requestVO
                .getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);

                if (!BTSLUtil.isNullString(requestVO.getReqContentType())  && (requestVO.getReqContentType().equals(PretupsI.JSON_CONTENT_TYPE) ) && requestVO.isSenderMessageRequired()) {
                    if (requestVO.isPushMessage() && !PretupsI.YES.equals(externalInterfaceAllowed)) {
                        final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());

                        PushMessage pushMessage = null;
                        pushMessage = new PushMessage(requestVO.getFilteredMSISDN(), senderMessage, requestIdOperator, requestVO.getRequestGatewayCode(), requestVO
                            .getLocale());
                     		if(requestVO.getMessageCode() != null){
								pushMessage.push();
					   }
                    }
                }
            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                if (requestVO.isSenderMessageRequired()) {
                    PushMessage pushMessage = null;
                    pushMessage = new PushMessage(requestVO.getFilteredMSISDN(), message, requestIdOperator, requestVO.getRequestGatewayCode(), requestVO
                        .getLocale());
                        pushMessage.push();
                   
                }

            }

            P2PGatewayRequestLog.log(requestVO);
            if (log.isDebugEnabled()) {
                log.debug("processRequest", requestIdOperator, "Exiting");
            }
        }
            response = requestVO.getJsonReponse();
            return response;
        }
    
    @POST
    @Path("/system-rest-receiver/{servicekeyword}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PretupsResponse<JsonNode> processSystemReceiverRequest(JsonNode request,
            @PathParam("servicekeyword") String serviceKeyword) {
        PretupsResponse<JsonNode> response;
        ++requestIdSystem;
        final String requestIDStr = String.valueOf(requestIdSystem);
        response = processRequestSystem(request, serviceKeyword.toUpperCase(),requestIDStr);
        return response;
    }

    private PretupsResponse<JsonNode> processRequestChannel(JsonNode request, String serviceKeyword, String requestIdChannel )  {

        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final String methodName = "processRequestChannel";
        StringBuilder loggerValue= new StringBuilder(); 
        StringBuilder eventhandler= new StringBuilder(); 
        final RequestVO requestVO = new RequestVO();
        String message = null;
        Connection con = null;MComConnectionI mcomCon = null;
        NetworkPrefixVO networkPrefixVO = null;
        ChannelUserVO channelUserVO = null;
        final Date currentDate = new Date();
        SimProfileVO simProfileVO = null;
        final long requestStartTime = System.currentTimeMillis();
        long requestEndTime = 0;
        String filteredMSISDN = null;
        GatewayParsersI gatewayParsersObj = null;
        String networkID = null;
        String externalInterfaceAllowed = null;
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        try {
            String instanceCode = Constants.getProperty("INSTANCE_ID");
            requestVO.setReqContentType(httpServletRequest.getContentType());
            requestVO.setModule(PretupsI.C2S_MODULE);
            requestVO.setInstanceID(instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setLocale(new Locale(lang,country));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setRequestStartTime(requestStartTime);
            requestVO.setServiceKeyword(serviceKeyword);
            requestVO.setActionValue(PretupsI.CHANNEL_RECEIVER_ACTION);
            requestVO.setRequestMessageOrigStr(request.toString());
            response = parseRequestfromJson(request, requestVO);
            if (!response.getStatus()) {
                return response;
            }
            
            PretupsBL.validateRequestMessageGateway(requestVO);

            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO()
                    .getHandlerClass());

            response = parseRequestForMessage(request, requestVO);
            if (!response.getStatus()) {
                return response;
            }

            ChannelGatewayRequestLog.inLog(requestVO);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            gatewayParsersObj.parseChannelRequestMessage(requestVO, con);

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

            if (requestVO.getMessageGatewayVO().getAccessFrom() == null
                    || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                PretupsBL
                        .unBarredUserAutomaic(con, channelUserVO.getUserPhoneVO().getMsisdn(),
                                networkPrefixVO.getNetworkCode(), PretupsI.C2S_MODULE, PretupsI.USER_TYPE_SENDER,
                                channelUserVO);
            }

            channelUserVO.setModifiedOn(currentDate);
//            final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
            final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO() != null ? channelUserVO.getUserPhoneVO() : new UserPhoneVO();

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
            LogFactory.printLog(methodName, "Sender Locale in Request if any" + requestVO.getSenderLocale(), log);
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
            try {
            	if(!channelUserVO.getUserType().equalsIgnoreCase(PretupsI.CATEGORY_USER_TYPE)) {
            		gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, true, channelUserVO);
            	}
			} catch (BTSLBaseException e1) {
				log.errorTrace(methodName, e1);
				throw e1;
			} finally {
				mcomCon.finalCommit();
			}
            if(mcomCon != null)
            {
            	mcomCon.close("RestReceiver#processRequestChannel");
            	mcomCon=null;
            }
            MessageFormater.handleChannelMessageFormat(requestVO, channelUserVO);

            populateLanguageSettings(requestVO, channelUserVO);

            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if (serviceKeywordCacheVO == null) {
            	eventhandler.setLength(0);
            	eventhandler.append("Service keyword not found for the keyword=");
            	eventhandler.append(requestVO.getRequestMessageArray()[0]);
            	eventhandler.append(GATEWAYTYPE);
            	eventhandler.append(requestVO.getRequestGatewayType());
            	eventhandler.append(SERVICEPORT);
            	eventhandler.append(requestVO.getServicePort());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
                        EventLevelI.INFO, PROCESSREQUEST, null, filteredMSISDN, "",eventhandler.toString());
                throw new BTSLBaseException(RSTRECEIVER, methodName,
                        PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
            	eventhandler.setLength(0);
            	eventhandler.append("Service keyword suspended for the keyword=");
            	eventhandler.append(requestVO.getRequestMessageArray()[0]);
            	eventhandler.append(GATEWAYTYPE);
            	eventhandler.append(requestVO.getRequestGatewayType());
            	eventhandler.append(SERVICEPORT);
            	eventhandler.append(requestVO.getServicePort());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
                        EventLevelI.INFO, PROCESSREQUEST, null, filteredMSISDN, "",eventhandler.toString());
                throw new BTSLBaseException(RSTRECEIVER, methodName,
                        PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());

            channelUserVO.setServiceTypes(requestVO.getServiceType());
            response = validateServiceType(requestVO, serviceKeywordCacheVO, channelUserVO);
            if (!response.getStatus()) {
                return response;
            }
            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL
                    .getServiceKeywordHandlerObj(requestHandlerClass);
            /* Added 23-11-2021*/
            if(Boolean.parseBoolean(getNodeValue(request, "webApiCall"))) {
            	requestVO.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
            	requestVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
            	requestVO.setSourceType(PretupsI.REQUEST_SOURCE_TYPE_WEB);
			}
            /* End 23-11-2021*/
            controllerObj.process(requestVO);

        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            requestVO.setSuccessTxn(false);
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(BTSLBASEEXP);
            	loggerValue.append(be.getMessage());
                log.debug(methodName,  loggerValue);
            }
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }
            if (be.isKey()) {
                response.setMessageCode(be.getMessageKey());
                response.setResponse(PretupsI.RESPONSE_SUCCESS, true,
                        PretupsRestUtil.getMessageString(be.getMessageKey(), be.getArgs()));
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            
            
			if ("UPUSRHRCHY".equalsIgnoreCase(requestVO.getServiceKeyword())||"C2CBUYUSENQ".equalsIgnoreCase(requestVO.getServiceKeyword())) {

				requestVO.setSuccessTxn(false);
				requestVO.setMessageCode(be.getMessage());
				
				requestVO.setMessageArguments(be.getArgs());

				try {
					ArrayList<UserHierarchyVO> userHirerachyRes = new ArrayList<UserHierarchyVO>();
					
					
					PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();
					;
					JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
							PretupsRestUtil.convertObjectToJSONString(userHirerachyRes), new TypeReference<JsonNode>() {
							});

					jsonReponse.setDataObject(dataObject);

					requestVO.setJsonReponse(jsonReponse);
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}
			}

        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            loggerValue.setLength(0);
            loggerValue.append("Exception e:");
            loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(requestVO.getMessageCode())) {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            eventhandler.setLength(0);
            eventhandler.append("Exception in ChannelReceiver:");
            eventhandler.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    PROCESSREQUEST, "", "", "",  eventhandler.toString());
            
            
            
            if ("UPUSRHRCHY".equalsIgnoreCase(requestVO.getServiceKeyword())) {

				requestVO.setSuccessTxn(false);
				requestVO.setMessageCode(PretupsErrorCodesI.USER_HIERRACHY_ERROR);
				
				String[] errorArg = {e.getMessage()};
				requestVO.setMessageArguments(errorArg);
				
				try {
					ArrayList<UserHierarchyVO> userHirerachyRes = new ArrayList<UserHierarchyVO>();
					
					
					PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();
					;
					JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
							PretupsRestUtil.convertObjectToJSONString(userHirerachyRes), new TypeReference<JsonNode>() {
							});

					jsonReponse.setDataObject(dataObject);

					requestVO.setJsonReponse(jsonReponse);
				} catch (Exception e2) {
					log.errorTrace(methodName, e2);
				}
			}
            
        } finally {

            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }

            if (gatewayParsersObj == null) {
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO
                            .getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("gatewayParsersObj=");
                	loggerValue.append(gatewayParsersObj);
                    log.debug(this,  loggerValue);
                }
            }

            if (requestVO.getMessageGatewayVO() != null) {
            	loggerValue.setLength(0);
            	loggerValue.append("Gateway Time out=");
            	loggerValue.append(requestVO.getMessageGatewayVO().getTimeoutValue());
                LogFactory.printLog(methodName,loggerValue.toString(), log);
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            }

            if (gatewayParsersObj != null) {
                gatewayParsersObj.generateChannelResponseMessage(requestVO);
            } else {
                if (!BTSLUtil.isNullString(requestVO.getReqContentType())
                        && PretupsI.JSON_CONTENT_TYPE.equals(requestVO.getReqContentType()) ) {
                    prepareJsonResponse(requestVO);
                } else {
                    if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                        message = requestVO.getSenderReturnMessage();
                    } else {
                        message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(),
                                requestVO.getMessageArguments());
                    }

                    requestVO.setSenderReturnMessage(message);
                }
            }
            response = requestVO.getJsonReponse();
            try {
                String reqruestGW = requestVO.getRequestGatewayCode();
                final String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                if ((!BTSLUtil.isNullString(altrnetGW) && (altrnetGW.split(":")).length >= 2)
                        && reqruestGW.equalsIgnoreCase(altrnetGW.split(":")[0])) {
                    reqruestGW = (altrnetGW.split(":")[1]).trim();
                    loggerValue.setLength(0);
                    loggerValue.append("processRequest: Sender Message push through alternate GW");
                    loggerValue.append(reqruestGW);
                    loggerValue.append("Requested GW was:");
                    loggerValue.append(requestVO.getRequestGatewayCode());
                    LogFactory.printLog(methodName, loggerValue.toString(), log);
                }
                int messageLength = 0;
                final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                if (!BTSLUtil.isNullString(messLength)) {
                    messageLength = (new Integer(messLength)).intValue();
                }

                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()))
                        && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType())) {
                 

                    message = requestVO.getSenderReturnMessage();
                    String message1 = null;
                    if ((messageLength > 0) && (message.length() > messageLength)) {
                        message1 = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.REQUEST_IN_QUEUE_UB,
                                requestVO.getMessageArguments());
                        final PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1,
                                requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());
                        pushMessage1.push();
                        requestVO.setRequestGatewayCode(reqruestGW);
                    }
                    message = requestVO.getSenderReturnMessage();

                } else {
                    message = requestVO.getSenderReturnMessage();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Locale=");
            	loggerValue.append(requestVO.getLocale());
            	loggerValue.append(" requestEndTime=");
            	loggerValue.append(requestEndTime);
            	loggerValue.append (REQSTART);
            	loggerValue.append(requestStartTime);
            	loggerValue.append(MESSAGECODE);
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append(ARGS);
            	loggerValue.append(requestVO.getMessageArguments());
                log.debug(methodName,  loggerValue );
            }
            if (requestVO.getMessageGatewayVO() == null
                    || requestVO.getMessageGatewayVO().getResponseType()
                            .equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE)
                    || (requestEndTime - requestStartTime) / 1000 < requestVO.getMessageGatewayVO().getTimeoutValue()) {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);

                if (requestVO.isSuccessTxn() && requestVO.isSenderMessageRequired()
                        && (PretupsI.JSON_CONTENT_TYPE.equalsIgnoreCase(requestVO.getReqContentType())||("c2cvomstrfini").equalsIgnoreCase(requestVO.getServiceKeyword())
                        		||("c2cvomstrf").equalsIgnoreCase(requestVO.getServiceKeyword())||("c2ctrfini").equalsIgnoreCase(requestVO.getServiceKeyword()))
                        && !PretupsI.YES.equals(externalInterfaceAllowed)) {
                    final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(),
                            requestVO.getMessageArguments());
                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage,
                            requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());

                    if (!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage)) {
                        pushMessage.push();
                    }
                }
            } else {
                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(requestVO.getServiceType())
                        && requestVO.isSenderMessageRequired()) {

                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(),
                            requestVO.getSenderReturnMessage(), requestVO.getRequestIDStr(),
                            requestVO.getRequestGatewayCode(), requestVO.getLocale());
                    pushMessage.push();

                }
            }

            ChannelGatewayRequestLog.outLog(requestVO);
            LogFactory.printLog(methodName, "Exiting", log);
            if(mcomCon != null){mcomCon.close("RestReceiver#processRequestChannel");mcomCon=null;}
        }
        
        //message handling for new web
        if(!BTSLUtil.isNullString(requestVO.getReqContentType())
        && PretupsI.JSON_CONTENT_TYPE.equals(requestVO.getReqContentType()) 
        && !BTSLUtil.isNullString(requestVO.getRequestGatewayType())
        && PretupsI.GATEWAY_TYPE_WEB.equalsIgnoreCase(requestVO.getRequestGatewayType())) {
        	if(response != null && 
        	   response.getDataObject()!= null && 
        	   response.getDataObject().get("errorcode")!= null &&
        	   !BTSLUtil.isNullString(response.getDataObject().get("errorcode").textValue())) {
        		String msgKey = response.getDataObject().get("errorcode").textValue();

				// to check if msg key is numeric or not
				if (BTSLUtil.isNumeric(msgKey)) {
					String resmsg = RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
									(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
							msgKey, null);
					ObjectNode objNode = (ObjectNode) response.getDataObject();
					objNode.put("errorcode", resmsg);
					response.setDataObject(objNode);
				}

        	}
        }
        
        return response;
    }
    
    private PretupsResponse<JsonNode> processRequestOperator(JsonNode request, String serviceKeyword, String requestIdOperator )  {
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final String methodName = "processRequestOperator";
        StringBuilder loggerValue= new StringBuilder(); 
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
        String instanceCode=null;
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        try {
        	loggerValue.setLength(0);
        	loggerValue.append("************Start Time***********=");
        	loggerValue.append(requestStartTime);
        	
            LogFactory.printLog(methodName, loggerValue.toString() , log);
            requestIDMethod = Long.parseLong(requestIdOperator);
            instanceCode = Constants.getProperty("INSTANCE_ID");
            requestVO.setReqContentType(httpServletRequest.getContentType());
            LogFactory.printLog(methodName, "Content Type: " + httpServletRequest.getContentType(), log);
            requestVO.setRequestID(requestIDMethod);
            requestVO.setModule(PretupsI.OPT_MODULE);
            requestVO.setInstanceID(instanceCode);
            requestVO.setCreatedOn(currentDate);
            requestVO.setServiceKeyword(serviceKeyword);
            requestVO.setLocale(new Locale(lang,country));
            requestVO.setDecreaseLoadCounters(false);
            requestVO.setRequestStartTime(requestStartTime);
            requestVO.setActionValue(PretupsI.OPERATOR_RECEIVER_ACTION);
            response = parseRequestfromJson(request, requestVO);
            if (!response.getStatus()) {
                return response;
            }
            PretupsBL.validateRequestMessageGateway(requestVO);
            requestType = requestVO.getMessageGatewayVO().getGatewayType();
            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
            parseRequestForMessage( request, requestVO);

            ChannelGatewayRequestLog.inLog(requestVO);
            gatewayParsersObj.parseOperatorRequestMessage(requestVO);

            gatewayParsersObj.validateUserIdentification(requestVO);

            filteredMSISDN = requestVO.getFilteredMSISDN();

            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
            }
            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
            }

            LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
            requestVO.setDecreaseLoadCounters(true);

            gatewayParsersObj.loadValidateNetworkDetails(requestVO);

            networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();

            networkID = networkPrefixVO.getNetworkCode();
            requestVO.setRequestNetworkCode(networkID);

            LoadController.checkNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);

            String requestHandlerClass;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            channelUserVO = gatewayParsersObj.loadValidateUserDetails(con, requestVO);
            if (requestVO.getMessageGatewayVO().getAccessFrom() == null || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                PretupsBL.unBarredUserAutomaic(con, channelUserVO.getUserPhoneVO().getMsisdn(), networkPrefixVO.getNetworkCode(), PretupsI.C2S_MODULE,
                    PretupsI.USER_TYPE_SENDER, channelUserVO);
            }

            channelUserVO.setModifiedOn(currentDate);
            final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
            userPhoneVO.setModifiedBy(channelUserVO.getUserID());
            userPhoneVO.setModifiedOn(currentDate);

            if (userPhoneVO.getLastAccessOn() == null) {
                userPhoneVO.setAccessOn(true);
            }
            if (requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
                userPhoneVO.setLastAccessOn(currentDate);
            }
            LogFactory.printLog(methodName, "Sender Locale in Request if any" + requestVO.getSenderLocale(), log);

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            } else {
                requestVO.setSenderLocale(requestVO.getLocale());
            }

            if (!requestVO.isMessageAlreadyParsed()) {
                PretupsBL.isPlainMessageAndAllowed(requestVO);
                if (!requestVO.isPlainMessage()) {
                    PretupsBL.getEncryptionKeyForUser(con, requestVO);
                    simProfileVO =  SimProfileCache.getSimProfileDetails(userPhoneVO.getSimProfileID());
                    PretupsBL.parseBinaryMessage(requestVO, simProfileVO);
                }
            }
            con.close();
            con = null;
            MessageFormater.handleChannelMessageFormat(requestVO, channelUserVO);
            PretupsBL.validateTempTransactionID(requestVO, channelUserVO);

            PretupsBL.processFixedInfo(requestVO, channelUserVO);

            populateLanguageSettings(requestVO, channelUserVO);

            if (!(channelUserVO.getUserPhoneVO()).getPhoneLanguage().equalsIgnoreCase(requestVO.getLocale().getLanguage())) {
                loggerValue.setLength(0);
                loggerValue.append(" Changing the Language code for MSISDN=");
                loggerValue.append(requestVO.getFilteredMSISDN());
                loggerValue.append( " From Language=");
                loggerValue.append((channelUserVO.getUserPhoneVO()).getPhoneLanguage());
                loggerValue.append(" to language=");
                loggerValue.append(requestVO.getLocale().getLanguage());
            	log.error(methodName, requestVO.getRequestIDStr(),loggerValue );
                (channelUserVO.getUserPhoneVO()).setPhoneLanguage(requestVO.getLocale().getLanguage());
                (channelUserVO.getUserPhoneVO()).setCountry(requestVO.getLocale().getCountry());
            }

            ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

            if(serviceKeywordCacheVO == null && requestVO.getModule().equals(PretupsI.OPT_MODULE)){
                String type = (String) requestVO.getRequestMap().get("TYPE");
                Map seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
                serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type.toUpperCase() + "_"
                        + PretupsI.C2S_MODULE + "_" + requestVO.getRequestGatewayType() + "_" + requestVO.getServicePort());
            }  

            if (serviceKeywordCacheVO == null) {
            	loggerValue.setLength(0);
            	loggerValue.append("Service keyword not found for the keyword=");
            	loggerValue.append(requestVO.getRequestMessageArray()[0]);
            	loggerValue.append(GATEWAYTYPE);
            	loggerValue.append(requestVO.getRequestGatewayType());
            	loggerValue.append(SERVICEPORT);
            	loggerValue.append(requestVO.getServicePort());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, PROCESSREQUEST, requestIdOperator,
                    filteredMSISDN, "", loggerValue.toString() );
                throw new BTSLBaseException("RestReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                serviceType = serviceKeywordCacheVO.getServiceType();
                loggerValue.setLength(0);
                loggerValue.append("Service keyword suspended for the keyword=");
                loggerValue.append( requestVO.getRequestMessageArray()[0]);
                loggerValue.append(GATEWAYTYPE);
                loggerValue.append(requestVO.getRequestGatewayType() );
                loggerValue.append(SERVICEPORT);
                loggerValue.append(requestVO.getServicePort());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, PROCESSREQUEST, requestIdOperator,
                    filteredMSISDN, "", loggerValue.toString());
                throw new BTSLBaseException("RestReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            serviceType = serviceKeywordCacheVO.getServiceType();
            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
            requestVO.setType(serviceKeywordCacheVO.getType());
            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
            channelUserVO.setServiceTypes(requestVO.getServiceType());
            response = validateServiceType(requestVO, serviceKeywordCacheVO, channelUserVO);
            if (!response.getStatus()) {
                return response;
            }
            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
            controllerObj.process(requestVO);
        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            requestVO.setSuccessTxn(false);
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(BTSLBASEEXP);
            	loggerValue.append(be.getMessage());
                log.debug(methodName, requestIdOperator,  loggerValue);
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
            log.error(methodName, requestIdOperator, loggerValue);
            log.errorTrace(methodName, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            loggerValue.setLength(0);
            loggerValue.append("Exception in RestReceiver:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESSREQUEST, requestIdOperator, "",
                "", loggerValue.toString());
        } finally {
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            }

            try {
                if (con == null && (channelUserVO != null || "Y".equals(Constants.getProperty(LOADTEST)))) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                loggerValue.setLength(0);
                loggerValue.append("Exception in RestReceiver while getting connection :");
                loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESSREQUEST, requestIdOperator,
                    "", "", loggerValue.toString());
            }

            if (gatewayParsersObj == null) {
                try {
                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
                    loggerValue.append("Exception in RestReceiver while getting gatewayParsersObj :");
                    loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESSREQUEST,
                        requestIdOperator, "", "", loggerValue.toString());
                }
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("gatewayParsersObj=");
            	loggerValue.append(gatewayParsersObj);
                log.debug(this, requestIdOperator,  loggerValue );
            }

            if (channelUserVO != null && isMarkedUnderprocess && con != null) {
                try {
                    if (channelUserVO.getUserPhoneVO().isBarUserForInvalidPin()) {
                        ChannelUserBL.barSenderMSISDN(con, channelUserVO, PretupsI.BARRED_TYPE_PIN_INVALID, currentDate, PretupsI.C2S_MODULE);
                        con.commit();
						Locale locale = new Locale(lang,country);
						PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale,channelUserVO.getNetworkCode());
						pushMessage.push();
                    }
                } catch (BTSLBaseException be) {
                    log.errorTrace(methodName, be);
                    loggerValue.setLength(0);
                    loggerValue.append(BTSLBASEEXP);
                    loggerValue.append(be.getMessage());
                    log.error(methodName, requestIdOperator,  loggerValue);
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }

                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    try {
                        con.rollback();
                    } catch (Exception ex) {
                        log.errorTrace(methodName, ex);
                    }
                    loggerValue.setLength(0);
                    loggerValue.append("Exception in RestReceiver while barring user becuase of invalid PIN counts:");
                    loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESSREQUEST,
                        requestIdOperator, "", "", loggerValue.toString() );
                }
            }
            if (con != null) {
                if (Constants.getProperty(LOADTEST) != null && "Y".equals(Constants.getProperty(LOADTEST))) {
                    try {
                        con.rollback();
                        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

                        if (!BTSLUtil.isNullString(requestVO.getIntMsisdnNotFound())) {
                            requestVO.setMessageCode(requestVO.getIntMsisdnNotFound());
                        }

                        channelTransferDAO.addC2SReceiverRequests(con, requestVO);
                        con.commit();
                    } catch (Exception ex) {
                        log.errorTrace(methodName, ex);
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                    }
                }

                try {
                    con.close();
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                con = null;
            }
            if (requestVO.isDecreaseLoadCounters()) {
                if (networkPrefixVO != null) {
                    LoadController.decreaseCurrentNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                }
                LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
            requestEndTime = System.currentTimeMillis();
            ReqNetworkServiceLoadController.increaseIntermediateCounters(instanceCode, requestType, networkID, serviceType, requestIdOperator,
                LoadControllerI.COUNTER_NEW_REQUEST, requestStartTime, requestEndTime, requestVO.isSuccessTxn(), requestVO.isDecreaseLoadCounters());

            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("requestEndTime=");
            	loggerValue.append(requestEndTime);
            	loggerValue.append(REQSTART);
            	loggerValue.append(requestStartTime);
            	loggerValue.append(MESSAGECODE);
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append(" Args=");
            	loggerValue.append(requestVO.getMessageArguments());
            	loggerValue.append(MSGANY);
            	loggerValue.append(message);
            	loggerValue.append(" Locale=");
            	loggerValue.append(requestVO.getLocale());
                log.debug(this, requestIdOperator,loggerValue );
            }

            if (requestVO.getMessageGatewayVO() != null) {
            	loggerValue.setLength(0);
            	loggerValue.append("Gateway Time out=" );
            	loggerValue.append(requestVO.getMessageGatewayVO().getTimeoutValue());
            	
                LogFactory.printLog(methodName, loggerValue.toString(), log);
            }

            if (requestVO.getSenderLocale() != null) {
                requestVO.setLocale(requestVO.getSenderLocale());
            }
            if (gatewayParsersObj != null) {
                gatewayParsersObj.generateChannelResponseMessage(requestVO);
            } else {
                if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().equals(PretupsI.JSON_CONTENT_TYPE))) {
                    prepareJsonResponse(requestVO);
                }
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
                        LogFactory.printLog(methodName, "Requested GW was:" + requestVO.getRequestGatewayCode(), log);
                    }
                }

                    message = requestVO.getSenderReturnMessage();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "Locale=");
            	loggerValue.append(requestVO.getLocale());
            	loggerValue.append(" requestEndTime=");
            	loggerValue.append(requestEndTime);
            	loggerValue.append(REQSTART);
            	loggerValue.append( requestStartTime);
            	loggerValue.append(MESSAGECODE);
            	loggerValue.append(requestVO.getMessageCode());
            	loggerValue.append(" Args=");
            	loggerValue.append( requestVO.getMessageArguments());
            	loggerValue.append(MSGANY);
            	loggerValue.append(message);
                log.debug(methodName, requestIdOperator,loggerValue);
            }
            ChannelGatewayRequestLog.outLog(requestVO);
            LogFactory.printLog(methodName, "Exiting", log);
            if(mcomCon != null){mcomCon.close("RestReceiver#processRequestOperator");mcomCon=null;}
        }
        response = requestVO.getJsonReponse();
        return response;
        }

    public PretupsResponse<JsonNode> processRequestSystem(JsonNode request, String serviceKeyword, String requestIdSystem )  {
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final String methodName = "processRequestSystem";
        StringBuilder loggerValue= new StringBuilder(); 
        final RequestVO requestVO = new RequestVO();
        String message = null;
        NetworkPrefixVO networkPrefixVO = null;
        final Date currentDate = new Date();
        final long requestStartTime = System.currentTimeMillis();
        long requestEndTime = 0;	
        GatewayParsersI gatewayParsersObj = null;
        String networkID = null;
        long requestIDMethod = 0;
        String instanceCode=null;
        Connection con = null;MComConnectionI mcomCon = null;
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        try {
        	instanceCode = Constants.getProperty("INSTANCE_ID");
        	requestIDMethod = Long.parseLong(requestIdSystem);
        	requestVO.setReqContentType(httpServletRequest.getContentType());
        	requestVO.setModule(PretupsI.OPT_MODULE);
        	requestVO.setInstanceID(instanceCode);
        	requestVO.setCreatedOn(currentDate);
        	requestVO.setLocale(new Locale(lang,country));
        	requestVO.setDecreaseLoadCounters(false);
        	requestVO.setRequestStartTime(requestStartTime);
        	requestVO.setServiceKeyword(serviceKeyword);
        	requestVO.setActionValue(PretupsI.SYSTEM_RECEIVER_ACTION);
        	response = parseRequestfromJson(request, requestVO);
        	if (!response.getStatus()) {
        		return response;
        	}

        	PretupsBL.validateRequestMessageGateway(requestVO);

        	gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
        	response = parseRequestForMessage(request, requestVO);
        	if (!response.getStatus()) {
        		return response;
        	}

        	ChannelGatewayRequestLog.inLog(requestVO);
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
        	gatewayParsersObj.parseChannelRequestMessage(requestVO, con);

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

        	LogFactory.printLog(methodName, "Sender Locale in Request if any" + requestVO.getSenderLocale(), log);

        	if (requestVO.getSenderLocale() != null) {
        		requestVO.setLocale(requestVO.getSenderLocale());
        	} else {
        		requestVO.setSenderLocale(requestVO.getLocale());
        	}

        	LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
        	requestVO.setDecreaseLoadCounters(true);
        	LoadController.checkNetworkLoad(requestIDMethod, networkPrefixVO.getNetworkCode(), LoadControllerI.NETWORK_NEW_REQUEST);
        	requestVO.setToBeProcessedFromQueue(false);

        	MessageFormater.handleExtChannelMessageFormat(requestVO);


        	final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

        	if (serviceKeywordCacheVO == null) {
        		loggerValue.setLength(0);
        		loggerValue.append("Service keyword not found for the keyword=");
        		loggerValue.append(requestVO.getRequestMessageArray()[0]);
        		loggerValue.append(" For Gateway Type=");
        		loggerValue.append(requestVO.getRequestGatewayType());
        		loggerValue.append("Service Port=" );
        		loggerValue.append(requestVO.getServicePort());
        								
        		
        		EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, PROCESSREQUEST, requestIdSystem,
        				"", "",  loggerValue.toString());
        		throw new BTSLBaseException("SystemReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
        	} else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
        		loggerValue.setLength(0);
        		loggerValue.append("Service keyword suspended for the keyword=");
        		loggerValue.append(requestVO.getRequestMessageArray()[0]);
        		loggerValue.append(" For Gateway Type=");
        		loggerValue.append(requestVO.getRequestGatewayType());
        		loggerValue.append("Service Port=");
        		loggerValue.append(requestVO.getServicePort());
        		EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, PROCESSREQUEST, null,
        				"", "",  loggerValue.toString());
        		throw new BTSLBaseException("SystemReceiver", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
        	}
        	requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
        	requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
        	requestVO.setType(serviceKeywordCacheVO.getType());
        	requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
        	requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());


        	final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
        	controllerObj.process(requestVO);
        	HashMap responseMap = new HashMap();
        	if(requestVO.getResponseMap() == null){
        		requestVO.setResponseMap(responseMap);
        	}
        	requestVO.getResponseMap().put("RESPONSEPARAM", serviceKeywordCacheVO.getResponseParam());

        } catch (BTSLBaseException be) {
        	log.errorTrace(methodName, be);
        	requestVO.setSuccessTxn(false);
            loggerValue.setLength(0);
            loggerValue.append(BTSLBASEEXP);
            loggerValue.append(be.getMessage());
        	LogFactory.printLog(methodName, loggerValue.toString() , log);

        	if (be.isKey()) {
        		response.setMessageCode(be.getMessageKey());
        		response.setResponse(PretupsI.RESPONSE_SUCCESS, true,
        				PretupsRestUtil.getMessageString(be.getMessageKey(), be.getArgs()));
        		requestVO.setMessageCode(be.getMessageKey());
        		requestVO.setMessageArguments(be.getArgs());
        	} else {
        		requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        	}
        } catch (Exception e) {
        	requestVO.setSuccessTxn(false);
        	loggerValue.setLength(0);
        	loggerValue.append("Exception e:" );
        	loggerValue.append(e.getMessage());
        	log.error(methodName,loggerValue );
        	log.errorTrace(methodName, e);
        	if (BTSLUtil.isNullString(requestVO.getMessageCode())) {
        		requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        	}
        	loggerValue.setLength(0);
        	loggerValue.append("Exception in SystemReceiver:" );
        	loggerValue.append(e.getMessage());
        	EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SystemReceiver[processRequest]", "", "",
        			"", loggerValue.toString() );
        } finally {
        	if(mcomCon != null){mcomCon.close("RestReceiver#processRequestSystem");mcomCon=null;}
        	if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
        		message = requestVO.getSenderReturnMessage();
        	}

        	if (gatewayParsersObj == null) {
        		try {
        			gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
        		} catch (Exception e) {
        			log.errorTrace(methodName, e);
        		}
                loggerValue.setLength(0);
                loggerValue.append("gatewayParsersObj=");
                loggerValue.append(gatewayParsersObj);
        		LogFactory.printLog(methodName,  loggerValue.toString() , log);
        	}

        	if (requestVO.getMessageGatewayVO() != null) {
        		loggerValue.setLength(0);
        		loggerValue.append("Gateway Time out=");
        		loggerValue.append(requestVO.getMessageGatewayVO().getTimeoutValue());
        		LogFactory.printLog(methodName, loggerValue.toString() , log);
        	}



        	if (gatewayParsersObj != null) {
        		gatewayParsersObj.generateChannelResponseMessage(requestVO);
        	} else {
        		if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().equals(PretupsI.JSON_CONTENT_TYPE))) {
        			prepareJsonResponse(requestVO);
        		} else {
        			if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
        				message = requestVO.getSenderReturnMessage();
        			} else {
        				message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
        			}

        			requestVO.setSenderReturnMessage(message);
        		}
        	}

        	response = requestVO.getJsonReponse();

        	try {
        		String requestGW = requestVO.getRequestGatewayCode();
        		final String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
        		if (!BTSLUtil.isNullString(altrnetGW) && (altrnetGW.split(":")).length >= 2 && requestGW.equalsIgnoreCase(altrnetGW.split(":")[0])) {
        			requestGW = (altrnetGW.split(":")[1]).trim();
        			LogFactory.printLog(methodName, "Requested GW was:" + requestVO.getRequestGatewayCode(), log);
        		}

        		message = requestVO.getSenderReturnMessage();
        	} catch (Exception e) {
        		log.errorTrace(methodName, e);
        	}

        	LogFactory.printLog(methodName, "Locale=" + requestVO.getLocale() + " requestEndTime=" + requestEndTime +
        			" requestStartTime=" + requestStartTime + " Message Code=" + requestVO
        			.getMessageCode() + " Args=" + requestVO.getMessageArguments() + " Message If any=" + message, log);

        	requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);

        	ChannelGatewayRequestLog.outLog(requestVO);
        	LogFactory.printLog(methodName, "Exiting", log);
        }
        return response;
       }
        
        

        /**
         * @param prequestVO
         * @param pserviceKeywordCacheVO
         * @param channelUserVO
         * @throws BTSLBaseException
         */
        private PretupsResponse<JsonNode> validateServiceType(RequestVO prequestVO,
                ServiceKeywordCacheVO pserviceKeywordCacheVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
            PretupsResponse<JsonNode> response = new PretupsResponse<>();
            final String methodName = "validateServiceType";
            final String serviceType = prequestVO.getServiceType();
            if (PretupsI.C2S_MODULE.equalsIgnoreCase(prequestVO.getModule())&&(PretupsI.YES.equals(pserviceKeywordCacheVO.getExternalInterface())
                    || PretupsI.AUTO_ASSIGN_SERVICES.equals(pserviceKeywordCacheVO.getExternalInterface()))) {
                final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType,
                        channelUserVO.getAssociatedServiceTypeList());
                if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                    LogFactory.printError(methodName, " MSISDN=" + prequestVO.getFilteredMSISDN()
                            + " Service Type not found in allowed List", log);
                    response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                    response.setResponse(PretupsI.RESPONSE_FAIL, false,
                            PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED));
                    return response;
                } else if (PretupsI.C2S_MODULE.equalsIgnoreCase(prequestVO.getModule())&&!PretupsI.YES.equals(listValueVO.getLabel())) {
                    LogFactory.printError(methodName, " MSISDN=" + prequestVO.getFilteredMSISDN()
                            + " Service Type is suspended in allowed List", log);
                    response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED);
                    response.setResponse(PretupsI.RESPONSE_FAIL, false,
                            PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED));
                    return response;
                }

            }
            if (PretupsI.OPT_MODULE.equalsIgnoreCase(prequestVO.getModule()) && PretupsI.YES.equals(pserviceKeywordCacheVO.getExternalInterface())) {
                final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType, channelUserVO.getAssociatedServiceTypeList());
                if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                    log.error("validateServiceType", prequestVO.getRequestIDStr(), " MSISDN=" + prequestVO.getFilteredMSISDN() + " Service Type not found in allowed List");
                    response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                    response.setResponse(PretupsI.RESPONSE_FAIL, false,
                            PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED));
                    return response;
                } else if (!PretupsI.YES.equals(listValueVO.getLabel())) {
                    log.error("validateServiceType", prequestVO.getRequestIDStr(),
                        " MSISDN=" + prequestVO.getFilteredMSISDN() + " Service Type is suspended in allowed List");
                    response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                    response.setResponse(PretupsI.RESPONSE_FAIL, false,
                            PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED));
                    return response;
                }// end if
            }
            response.setStatus(true);
            return response;
        }

        /**
         * @param request
         * @param requestVO
         * @throws BTSLBaseException
         */
        public PretupsResponse<JsonNode> parseRequestfromJson(JsonNode request, RequestVO requestVO)
                throws BTSLBaseException {
            PretupsResponse<JsonNode> response = new PretupsResponse<>();
            final String methodName = "parseRequestfromJson";
            if (BTSLUtil.isNullString(requestVO.getReqContentType())) {
                requestVO.setReqContentType("CONTENT_TYPE");
            }
            String reqGatewayCode = getNodeValue(request, "reqGatewayCode");
            String reqGatewayType = getNodeValue(request, "reqGatewayType");
            LogFactory.printLog(methodName, " reqGatewayCode " + reqGatewayCode + "reqGatewayType " + reqGatewayType, log);

            if (BTSLUtil.isNullString(reqGatewayCode)) {
                response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
                response.setResponse(PretupsI.RESPONSE_FAIL, false,
                        PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID));
                return response;
            } else {
                requestVO.setRequestGatewayCode(reqGatewayCode.trim());
            }
            if (BTSLUtil.isNullString(reqGatewayType)) {
                response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
                response.setResponse(PretupsI.RESPONSE_FAIL, false,
                        PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE));
                return response;
            } else {
                requestVO.setRequestGatewayType(reqGatewayType.trim());
            }

            requestVO.setLogin(getNodeValue(request, "reqGatewayLoginId"));
            requestVO.setPassword(getNodeValue(request, "reqGatewayPassword"));
            requestVO.setServicePort(getNodeValue(request, "servicePort"));
            requestVO.setRemoteIP(httpServletRequest.getRemoteAddr());
            requestVO.setSourceType(getNodeValue(request, "sourceType"));

            response.setStatus(true);
            return response;

        }

        /**
         * @param request
         * @param prequestVO
         * @throws BTSLBaseException
         */
        public PretupsResponse<JsonNode> parseRequestForMessage(JsonNode request, RequestVO prequestVO)
                throws BTSLBaseException {
            final String methodName = "parseRequestForMessage";
            PretupsResponse<JsonNode> response = new PretupsResponse<>();
            String msisdn;
            String requestMessage;
            String loginID;
            JsonNode data;
            if (request != null) {
                data = request.get("data");
                requestMessage = data.toString();
            } else {
                response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
                response.setResponse(PretupsI.RESPONSE_FAIL, false,
                        PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE));
                return response;
            }
            msisdn = getNodeValue(request, "msisdn");
            if ((BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage))
                    && PretupsI.JSON_CONTENT_TYPE.equals(httpServletRequest.getContentType())) {
                prequestVO.setRequestMessage(requestMessage);
                prequestVO.setReqContentType(httpServletRequest.getContentType());

            }
            if (!BTSLUtil.isNullString(msisdn)) {
                prequestVO.setRequestMSISDN(msisdn);
            }
            if(PretupsI.SERVICE_TYPE_VMSPINEXT.equalsIgnoreCase(prequestVO.getServiceKeyword()))
            {
            	loginID=getNodeValue(data, "loginid");
            	if (BTSLUtil.isNullString(loginID)) {
            		prequestVO.setSuccessTxn(false);
            		prequestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_SENDER_INVALID);
            		 throw new BTSLBaseException("RestReceiver", "parseRequestForMessage", PretupsErrorCodesI.EXTSYS_REQ_SENDER_INVALID);
                }
            }
            
            LogFactory.printLog(methodName, "requestMessage " + requestMessage, log);
            prequestVO.setRequestMessage(requestMessage);
            response.setStatus(true);
            return response;

        }

        /**
         * @param prequestVO
         * @param channelUserVO
         */
        private void populateLanguageSettings(RequestVO prequestVO, ChannelUserVO channelUserVO) {
            final FixedInformationVO fixedInformationVO = (FixedInformationVO) prequestVO.getFixedInformationVO();
            if (prequestVO.getLocale() == null) {
                prequestVO.setLocale(new Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (channelUserVO
                        .getUserPhoneVO()).getCountry()));
            }
            if (fixedInformationVO != null) {
                PretupsBL.getCurrentLocale(prequestVO, channelUserVO.getUserPhoneVO());
            }
        }

        /**
         * @param prequestVO
         */
        protected void prepareJsonResponse(RequestVO prequestVO) {
            final String methodName = "prepareJsonResponse";
            final java.util.Date date = new java.util.Date();
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
            String responseStr;
            LogFactory.printLog(methodName, "Entered", log);
            try {
                PretupsResponse<JsonNode> response = new PretupsResponse<>();
                JsonObject json = new JsonObject();
                if (!BTSLUtil.isNullString(prequestVO.getMessageCode())) {
                    if (prequestVO.isSuccessTxn()) {
                        json.addProperty(TXNSTATUS, PretupsI.TXN_STATUS_SUCCESS);

                    } else {
                        String message = prequestVO.getMessageCode();
                        if (message.indexOf('_') != -1) {
                            message = message.substring(0, message.indexOf('_'));
                        }
                        json.addProperty(TXNSTATUS, message);
                    }

                    sdf.setLenient(false);
                    json.addProperty(DATE.toUpperCase(), sdf.format(date));
                    json.addProperty(MESSAGE,
                            PretupsRestUtil.getMessageString(prequestVO.getMessageCode(), prequestVO.getMessageArguments()));
                }
                responseStr = json.toString();
                response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
                        (JsonNode) PretupsRestUtil.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
                        }));
                prequestVO.setJsonReponse(response);
                prequestVO.setSenderReturnMessage(responseStr);

            } catch (Exception e) {
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ChannelReceiver[prepareJsonResponse]", prequestVO.getRequestIDStr(), "", "",
                        "Exception while generating XML response:" + e.getMessage());
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, prequestVO.getRequestIDStr(),
                        "Exiting with message=" + prequestVO.getSenderReturnMessage());
            }

        }

        /**
         * @param node
         * @param value
         * @return
         */
        private String getNodeValue(JsonNode node, String value) {
            if (node.get(value) != null) {
                return node.get(value).textValue();
            } else {
                return "";
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
        private PretupsResponse<JsonNode> processVoucherReceiverRequest(JsonNode request, String serviceKeyword, String requestIdOperator ) {
            final String methodName = "processVoucherReceiverRequest";
            PretupsResponse<JsonNode> response = new PretupsResponse<>();
            final long requestStartTime = System.currentTimeMillis();
            final RequestVO requestVO = new RequestVO();
            long requestIDMethod = 0;
            final Date currentDate = new Date();
            GatewayParsersI gatewayParsersObj = null;
            final HashMap responseMap = new HashMap();
            Connection con = null;MComConnectionI mcomCon = null;
            
            NetworkPrefixVO networkPrefixVO = null;
            String instanceCode=null;
            String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            
            try {
            	mcomCon = new MComConnection();con=mcomCon.getConnection();
                if (log.isDebugEnabled()) {
                    log.debug("processRequest", requestIdOperator, "************Start Time***********=" + requestStartTime);
                }
                requestIDMethod = Long.parseLong(requestIdOperator);
                
                instanceCode = Constants.getProperty("INSTANCE_ID");
                requestVO.setRequestID(requestIDMethod);
                requestVO.setServiceKeyword(serviceKeyword);
                requestVO.setModule(PretupsI.C2S_MODULE);
                requestVO.setInstanceID(instanceCode);
                requestVO.setCreatedOn(currentDate);
                requestVO.setLocale(new Locale(lang,country));
                requestVO.setDecreaseLoadCounters(false);
                requestVO.setRequestStartTime(requestStartTime);
    			
                if(PretupsI.SERVICE_TYPE_VMSPINEXT.equalsIgnoreCase(requestVO.getServiceKeyword()))
                {
                	requestVO.setActionValue(PretupsI.OPERATOR_RECEIVER_ACTION);
                }
                else
                	requestVO.setActionValue(PretupsI.CHANNEL_RECEIVER_ACTION);
                
                response = parseRequestfromJson(request, requestVO);

                if (!response.getStatus()) {
                    return response;
                }
                
                PretupsBL.validateRequestMessageGateway(requestVO);
                gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                
                parseRequestForMessage( request, requestVO);
                
                
                ChannelGatewayRequestLog.inLog(requestVO);
                
                gatewayParsersObj.parseOperatorRequestMessage(requestVO);
                
                gatewayParsersObj.validateUserIdentification(requestVO);
                

                gatewayParsersObj.loadValidateNetworkDetails(requestVO);

                networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();

                String networkID = networkPrefixVO.getNetworkCode();
                requestVO.setRequestNetworkCode(networkID);

                
                gatewayParsersObj.loadValidateUserDetails(con, requestVO);
                
                if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
                    throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
                }
                if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
                    throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
                }
                
                final String[] messageArray = PretupsBL.parsePlainMessage(requestVO.getDecryptedMessage());
                requestVO.setRequestMessageArray(messageArray);
                LoadController.checkInstanceLoad(requestIDMethod, LoadControllerI.INSTANCE_NEW_REQUEST);
                requestVO.setDecreaseLoadCounters(true);
                String requestHandlerClass;
                final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

                if (serviceKeywordCacheVO == null) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RestReceiver[processRequest]", requestIdOperator, "", "",
                        "Service keyword not found for the keyword=" + "messageArray[0]" + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                            .getServicePort());
                    throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.ERROR_NOTFOUND_SERVICEKEYWORD);
                } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RestReceiver[processRequest]", requestIdOperator, "", "",
                        "Service keyword suspended for the keyword=" + "messageArray[0]" + " For Gateway Type=" + requestVO.getRequestGatewayType() + "Service Port=" + requestVO
                            .getServicePort());
                    throw new BTSLBaseException("RestReceiver", "processRequest", PretupsErrorCodesI.P2P_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
                }
                requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
                requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
                requestVO.setType(serviceKeywordCacheVO.getType());
                requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
                requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());
                requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

                final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(requestHandlerClass);
                controllerObj.process(requestVO);
                
                if(requestVO.getResponseMap()!=null){           	
                	requestVO.getResponseMap().put("RESPONSEPARAM", serviceKeywordCacheVO.getResponseParam());
                }
            } catch (BTSLBaseException be) {
            	if (be.isKey()) {
                    requestVO.setVomsError(BTSLUtil.getMessage(new Locale(lang,country), be.getMessageKey(), be.getArgs()));
                    requestVO.setVomsMessage(be.getMessageKey());
                    if(requestVO.getResponseMap()!=null){ 
    	                requestVO.getResponseMap().put(VOMSI.TXNSTATUS_TAG,be.getMessageKey());
    	            	requestVO.getResponseMap().put(VOMSI.MESSAGE_TAG,"Not able to Change Voucher Status");
    	            	requestVO.getResponseMap().put(VOMSI.ERROR_TAG,BTSLUtil.getMessage(new Locale(lang,country), be.getMessageKey(), be.getArgs()));
    	               }
                    else{
                    	responseMap.put(VOMSI.TXNSTATUS_TAG,be.getMessageKey());
                    	responseMap.put(VOMSI.MESSAGE_TAG,"Not able to Change Voucher Status");
        				responseMap.put(VOMSI.ERROR_TAG,BTSLUtil.getMessage(new Locale(lang,country), be.getMessageKey(), be.getArgs()));
        				requestVO.setResponseMap(responseMap);
                    }
                } else
                {
                	requestVO.setVomsError(VOMSI.ERROR_EXCEPTION);
                	requestVO.setVomsMessage(VOMSI.ERROR_EXCEPTION);
                }
            	
    	        
    				
                  log.debug("processRequest", "BTSLBaseException"+be);
            } catch (Exception e) {
                log.debug(methodName, e);
                
            } finally {
                if (gatewayParsersObj == null) {
                    try {
                        gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO().getHandlerClass());
                    } catch (Exception e) {
                        log.debug(methodName, e);
                    }
                }

                try {
                	
                	   if (gatewayParsersObj != null) {
                           gatewayParsersObj.generateChannelResponseMessage(requestVO);
                       } else {
                           if (!BTSLUtil.isNullString(requestVO.getReqContentType()) && (requestVO.getReqContentType().equals(PretupsI.JSON_CONTENT_TYPE))) {
                               prepareJsonResponse(requestVO);
                           }
                       
                       }
                	   
                   
                    if (requestVO.isDecreaseLoadCounters()) {
                        LoadController.decreaseCurrentInstanceLoad(requestIDMethod, LoadControllerI.DEC_LAST_TRANS_COUNT);
                    }
                    ChannelGatewayRequestLog.outLog(requestVO);

                } catch (Exception e) {
                    log.debug(methodName, e);
                    log.debug("processRequest", "Exception e" + e);
                }
                if(mcomCon != null){mcomCon.close("VomsReciever#processRequest");mcomCon=null;}
            }
            response = requestVO.getJsonReponse();
                return response;
            }

        public static void updateRequestIdChannel() {
        	++requestIdChannel;
        }
        
        public static long getRequestIdChannel() {
        	return requestIdChannel;
        }
        
        public PretupsResponse<JsonNode> processCP2PRequestOperator (HttpServletRequest req, JsonNode request, String serviceKeyword, String requestIdChannel )  {
            httpServletRequest = req;
            return processRequestChannel(request, serviceKeyword, requestIdChannel );
            
         }
        
        public PretupsResponse<JsonNode> processCommsission (HttpServletRequest req, JsonNode request, String serviceKeyword, String requestIdChannel )  {
            httpServletRequest = req;
            return processRequestChannel(request, serviceKeyword, requestIdChannel );
            
         }
        
        public PretupsResponse<JsonNode> processPassbookView (HttpServletRequest req, JsonNode request, String serviceKeyword, String requestIdChannel )  {
            httpServletRequest = req;
            return processRequestChannel(request, serviceKeyword, requestIdChannel );
            
         }
        public PretupsResponse<JsonNode> processTransferDetailView (HttpServletRequest req, JsonNode request, String serviceKeyword, String requestIdChannel )  {
            httpServletRequest = req;
            return processRequestChannel(request, serviceKeyword, requestIdChannel );
            
         }
        public PretupsResponse<JsonNode> processRequestChannel(HttpServletRequest req, JsonNode request, String serviceKeyword, String requestIdChannel )  {
        	httpServletRequest = req;
        	return processRequestChannel(request, serviceKeyword, requestIdChannel);
        }
        
        public PretupsResponse<JsonNode> processTotalIncomeDetailsView (HttpServletRequest req, JsonNode request, String serviceKeyword, String requestIdChannel )  {
            httpServletRequest = req;
            return processRequestChannel(request, serviceKeyword, requestIdChannel );
            
         }
        public PretupsResponse<JsonNode> processP2PService (HttpServletRequest req, JsonNode request, String serviceKeyword, String requestIdChannel )  {
            httpServletRequest = req;
            return processCP2PRequestOperator(request, serviceKeyword, requestIdChannel );
            
         }
}
