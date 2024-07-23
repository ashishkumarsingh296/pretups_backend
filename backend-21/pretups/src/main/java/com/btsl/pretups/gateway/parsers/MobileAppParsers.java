package com.btsl.pretups.gateway.parsers;

import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.MessagesCache;
import com.btsl.util.MessagesCaches;
import com.btsl.util.XMLTagValueValidation;
import com.btsl.voms.vomscommon.VOMSI;
import com.google.gson.Gson;

public class MobileAppParsers extends ParserUtility {
    //private static String type = null;
    public static final Log LOG = LogFactory.getLog(MobileAppParsers.class.getName());
    public static String C2S_MESSAGE_SEP = null;
    static {
        try {
            C2S_MESSAGE_SEP = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
            if (BTSLUtil.isNullString(C2S_MESSAGE_SEP)) {
                C2S_MESSAGE_SEP = " ";
            }
        } catch (Exception e) {
            LOG.errorTrace("static", e);
        }
    }

    @Override
    public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateUserIdentification", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        // Validate user on the basis of values provided.
        // If MSISDN is there then validate the same.
        if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
            validateMSISDN(p_requestVO);
        }
    }

    @Override
    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {

            p_requestVO.setReqContentType(contentType);
            parseChannelRequest(p_requestVO);

            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());

            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseChannelRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error("parseChannelRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobileAppParsers[parseChannelRequestMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("MobileAppParsers", "parseChannelRequestMessage", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
    }

    public static void parseChannelRequest(RequestVO p_requestVO) throws Exception {
        final String METHOD_NAME = "parseChannelRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuilder parsedRequestMessageStr = new StringBuilder();
        HashMap requestMap = null;
        String type = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            String requestStr = p_requestVO.getRequestMessage();
            String language1 = null;
            String language2 = null;
            String imei = null;
            String message = null;
           
            int i1 = -1;
            
            i1 = requestStr.indexOf("Message=");
            if (i1 > -1) {
                String msg2 = requestStr.substring(i1 + "Message=".length(), requestStr.length());
                String msg1 = requestStr.substring(0, i1);
                requestMap = BTSLUtil.getStringToHash(msg1, "&", "=");
                requestMap.put("Message", msg2);
            } else {
                requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            }

            HashMap seviceKeywordMap = new HashMap();
            type = (String) requestMap.get("TYPE");
           
            String msisdn = (String) requestMap.get("MSISDN");
            if("SELFREG".equals(type))
            {
            	if(BTSLUtil.isNullString(msisdn))
            			{
            		throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_MSISDN_BLANK);
            			}
            }
            if ((BTSLUtil.isNullString(msisdn) && !type.equals(PretupsI.SYSTEM_LANGUAGE)) && (BTSLUtil.isNullString(msisdn) && !type.equals(PretupsI.PRODUCT_GATEWAY_SERVICES) && !type
                .equals(PretupsI.VAS_SERVICES) && !type.equals(PretupsI.VAS_ENQUIRY) && !type.equals(PretupsI.MAPP_VERSION_REQ) )) {
                throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            
            }
            seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
            ServiceKeywordCacheVO serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type + "_" + p_requestVO.getModule() + "_" + p_requestVO
                .getRequestGatewayType() + "_" + p_requestVO.getServicePort());
            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobileAppParsers[parseChannelRequest]", p_requestVO
                    .getRequestIDStr(), msisdn, "",
                    "Service keyword not found for the keyword=" + type + " For Gateway Type=" + p_requestVO.getRequestGatewayType() + "Service Port=" + p_requestVO
                        .getServicePort());
                throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobileAppParsers[parseChannelRequest]", p_requestVO
                    .getRequestIDStr(), msisdn, "",
                    "Service keyword suspended for the keyword=" + type + " For Gateway Type=" + p_requestVO.getRequestGatewayType() + "Service Port=" + p_requestVO
                        .getServicePort());
                throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }

		    // Language parameters handling start
            if (requestMap.containsKey("LANGUAGE1")) {
                language1 = (String) requestMap.get("LANGUAGE1");
                if (LocaleMasterCache.getLocaleFromCodeDetails(language1) != null) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                } else {
                    LOG.error("parseChannelRequest", "LANGUAGE1 is not numeric");
                    throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                }
            }
            if (requestMap.containsKey("LANGUAGE2")) {
                language2 = (String) requestMap.get("LANGUAGE2");
                if (LocaleMasterCache.getLocaleFromCodeDetails(language2) != null) {
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
                } else {
                    LOG.error("parseChannelRequest", "LANGUAGE2 is not numeric");
                    throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                }
            }
            
            p_requestVO.setRequestMSISDN(msisdn);
           ChannelUserVO userVO = null;
           if(!("SELFREG".equals(type)))
           {
            try{
            	mcomCon = new MComConnection();
            	con=mcomCon.getConnection();
            	ChannelUserBL.updateUserInfo(con, p_requestVO);
            	if(!BTSLUtil.isNullString(p_requestVO.getActiverUserId()) && p_requestVO.getIsStaffUser()!=null && p_requestVO.getIsStaffUser()) {
            		userVO = new ChannelUserDAO().loadActiveUserId(con,	p_requestVO.getActiverUserId(), "USERID");
            	}else	userVO = new ChannelUserDAO().loadActiveUserId(con, p_requestVO.getRequestMSISDN(), "MSISDN");
            	if (userVO != null) {
					p_requestVO.setActiverUserId(userVO.getUserID());
					p_requestVO.setEncryptionKey(userVO.getInfo1());
					p_requestVO.setImei(userVO.getInfo2());
					p_requestVO.setEmailId(userVO.getEmail());
					p_requestVO.setmHash(userVO.getInfo3());
					p_requestVO.setToken(userVO.getInfo4());
					p_requestVO.setTokenLastUsedDate(userVO.getTokenLastUsedDate());
				} else {
					throw new BTSLBaseException("ChannelUserBL", METHOD_NAME, PretupsErrorCodesI.NO_USER_EXIST);
				}
				ChannelUserBL.loadAllowedTransferForCategory(con,userVO.getCategoryCode(), p_requestVO);
            }finally{
				if (mcomCon != null) {
					mcomCon.close("MobileAppParsers#parseChannelRequest");
					mcomCon = null;
				}
            }
            }
            String messageFormat = serviceKeywordCacheVO.getMessageFormat();
            String requestParam = serviceKeywordCacheVO.getRequestParam();
            String[] mandatoryRequestParamArr;
            String[] msgFormatArr;
            if (BTSLUtil.isNullString(requestParam) || (BTSLUtil.isNullString(messageFormat))) {
                LOG.error("parseChannelRequest", "requestParam=" + requestParam + ",messageFormat=" + messageFormat);
                throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            } else {
                mandatoryRequestParamArr = requestParam.split(",");
                msgFormatArr = messageFormat.split(" ");
            }
            if (requestMap.containsKey("Message")) {
                // msisdn=(String)requestMap.get("MSISDN");
                message = (String) requestMap.get("Message");
                message = message.replaceAll("\\s", "+");
                message = BTSLUtil.decryptAESNew(message, p_requestVO.getEncryptionKey());
                requestMap = BTSLUtil.getStringToHash(message, "&", "=");
                // requestMap.put("TYPE",type);
                // requestMap.put("MSISDN",msisdn);
                for (int i = 0; i < mandatoryRequestParamArr.length; i++) {
                    LOG.error("parseChannelRequest", "mandatoryRequestParamArr[i]=" + mandatoryRequestParamArr[i]);
                    if (requestMap.containsKey(mandatoryRequestParamArr[i])) {
                        if (BTSLUtil.isNullString((String) requestMap.get(mandatoryRequestParamArr[i]))) {
                            throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 1", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
                        }
                    }else {
                        throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 2", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
                    }
                }
                for (int i = 0; i < msgFormatArr.length; i++) {
                    if (requestMap.containsKey(msgFormatArr[i])) {
                        parsedRequestMessageStr = parsedRequestMessageStr.append(requestMap.get(msgFormatArr[i]));
                        parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    }
                    if("PIN".equals(msgFormatArr[i])){ 
                    	if(!requestStr.contains("&PIN=") && !BTSLUtil.isNullString(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS")) && Arrays.asList(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS").split(",")).contains(type)){
                    		parsedRequestMessageStr = parsedRequestMessageStr.append(BTSLUtil.decryptText(userVO.getSmsPin()));
                    		parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    	}else if(requestStr.contains("&PIN=") && null == requestMap.get("PIN")){
                    		LOG.error("parseChannelRequest", "PIN is required.");
            				throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 3", PretupsErrorCodesI.PIN_REQUIRED);
                    	}else if(!(requestMap.get("PIN")).toString().equals(BTSLUtil.decryptText(userVO.getSmsPin()))){
                    		LOG.error("parseChannelRequest", "Invalid PIN.");
 	                        throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 3" + "Request", PretupsErrorCodesI.INVALID_PIN);
                    	}
                    }
                }
            } else {
                for (int i = 0; i < mandatoryRequestParamArr.length; i++) {
                	String msgargs[] ={requestParam};
                    if (requestMap.containsKey(mandatoryRequestParamArr[i])) {
                        if (BTSLUtil.isNullString((String) requestMap.get(mandatoryRequestParamArr[i]))) {
                            throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 3", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,msgargs);
                        }
                    }else {
                        throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 4", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,msgargs);
                    }
                }
                for (int i = 0; i < msgFormatArr.length; i++) {
                	if(msgFormatArr[i].equalsIgnoreCase("PRODUCTS"))
                	{
                		String[] parsedProducts;
                		String x = (String) requestMap.get(msgFormatArr[i]);
                		parsedProducts = x.split(",");
                		 for (int y = 0; y < parsedProducts.length; y++) {
                			 parsedRequestMessageStr.append(parsedProducts[y]);
                			 parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                		 }
                		 
                	}
                	else if (requestMap.containsKey(msgFormatArr[i])) {
                        parsedRequestMessageStr = parsedRequestMessageStr.append(requestMap.get(msgFormatArr[i]));
                        parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    }
                    if("PIN".equals(msgFormatArr[i])){ 
                    	if(!requestStr.contains("&PIN=") && !BTSLUtil.isNullString(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS")) && Arrays.asList(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS").split(",")).contains(type)){
                    		parsedRequestMessageStr = parsedRequestMessageStr.append(BTSLUtil.decryptText(userVO.getSmsPin()));
                    		parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    	}else if(requestStr.contains("&PIN=") && null == requestMap.get("PIN")){
                    		LOG.error("parseChannelRequest", "PIN is required.");
            				throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 3", PretupsErrorCodesI.PIN_REQUIRED);
                    	}else if(!(requestMap.get("PIN")).toString().equals(BTSLUtil.decryptText(userVO.getSmsPin()))){
                    		LOG.error("parseChannelRequest", "Invalid PIN.");
 	                        throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest 3" + "Request", PretupsErrorCodesI.INVALID_PIN);
                    	}
                    }
                }
            }
            // Length check of IMEI
            if(!("SELFREG".equals(type)))
            {if (requestMap.containsKey("IMEI")) {
            	
            	if(PreferenceCache.getSystemPreferenceValue(PreferenceI.IMEI_OPTIONAL)!=null && (boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.IMEI_OPTIONAL)==false)
            	{
                    imei = (String) requestMap.get("IMEI");
                    String Mhash=(String) requestMap.get("MHASH");
                    String Token=(String)requestMap.get("TOKEN");
                    if ("USRREGREQ".equals(type) || "USRAUTH".equals(type) || "LANG".equals(type) || "PRGWC2S2".equals(type) || "VASSERVICE".equals(type) || "VASENQUIRY"
                        .equals(type) || PretupsI.SEND_OTP_FOR_FORGOT_PIN.equals(type) || PretupsI.OTP_VALID_PIN_RST.equals(type)) {
    	                    if (BTSLUtil.isNullString(imei)) {
    	                        LOG.error("parseChannelRequest", "imei is null");
    	                        throw new BTSLBaseException("MobileAppParsers", "parseChanne" + "lRequest", PretupsErrorCodesI.ERROR_INVALID_IMEI);
    	                    }
    	                    
    	                    if ( BTSLUtil.isNullString(Mhash)) {
    	                        LOG.error("parseChannelRequest", "MHash is null");
    	                        throw new BTSLBaseException("MobileAppParsers", "parseChanne" + "lRequest", PretupsErrorCodesI.MAPP_INVALID_MHASH);
    	                    }
    		        } else{  
    		           
    		                 if (BTSLUtil.isNullString(imei) || !imei.equals(p_requestVO.getImei()) ) {
    		                      LOG.error("parseChannelRequest", "IMEI is not of length 15 or is not numeric or null or not valid");
    		                      throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.ERROR_INVALID_IMEI,0,null);
    		                 }
    		                 
    		                 if (BTSLUtil.isNullString(Mhash) ||!Mhash.equals(p_requestVO.getmHash())) {
    		                        LOG.error("parseChannelRequest", "Mhash is not of length 15 or is not numeric or null or not valid");
    		                        throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.MAPP_INVALID_MHASH,0,null);
    		                 }
    		               
    			             if (BTSLUtil.isNullString(Token) ||  !Token.equals(p_requestVO.getToken() )) {
    			                        LOG.error("parseChannelRequest", "Token is invalid");
    			                        throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.MAPP_INVALID_TOKEN,0,null);
    			             }
    			                	
    			             long tokenExpiryInMin=((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TOKEN_EXPIRY_IN_MINTS))).longValue();
    			             Date currentdate= new Date();//Token expiry need to validate   	
    			             if( BTSLUtil.isTimeExpired(p_requestVO.getTokenLastUsedDate(), tokenExpiryInMin)){
    			                 LOG.error("parseChannelRequest", "Token is Expired");
    			                 throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.MAPP_TOKEN_EXPIRED,0,null);
    			                    
    			             }
    		              }
            	}

            } 
            else if (PretupsI.MAPP_VERSION_REQ.equals(requestMap.get("TYPE"))) {
            	
            }
            else {
                throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.ERROR_INVALID_IMEI,0,null);
            }
            
            
            String dateTime = null;
            String extRefNum = null;
            
            boolean isExtRefNumMandatory = false;
            boolean isDateTimeMandatory = false;
            for(int i=0; i<mandatoryRequestParamArr.length; i++) {
            	if("EXTREFNUM".equals(mandatoryRequestParamArr[i])) isExtRefNumMandatory = true;
            	if("DATETIME".equals(mandatoryRequestParamArr[i])) isDateTimeMandatory = true;
            }
            
            if(requestMap.containsKey("DATETIME")) {
            	dateTime = (String) requestMap.get("DATETIME");
            	XMLTagValueValidation.validateTxnDate(dateTime, isDateTimeMandatory);
            	p_requestVO.setTxnDate(dateTime);
            }
            
            if(requestMap.containsKey("EXTREFNUM")) {
            	extRefNum = (String) requestMap.get("EXTREFNUM");
            	XMLTagValueValidation.validateExtRefNum(extRefNum, isExtRefNumMandatory);
            	p_requestVO.setExternalReferenceNum(extRefNum);
            }
            
            

            // Language parameters handling start
            if (requestMap.containsKey("LANGUAGE1")) {
                language1 = (String) requestMap.get("LANGUAGE1");
                if (LocaleMasterCache.getLocaleFromCodeDetails(language1) != null) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                } else {
                    LOG.error("parseChannelRequest", "LANGUAGE1 is not numeric");
                    throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                }
            }
            if (requestMap.containsKey("LANGUAGE2")) {
                language2 = (String) requestMap.get("LANGUAGE2");
                if (LocaleMasterCache.getLocaleFromCodeDetails(language2) != null) {
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
                } else {
                    LOG.error("parseChannelRequest", "LANGUAGE2 is not numeric");
                    throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                }
            }
            }
            /*
             * if(!BTSLUtil.isNumeric(language1)||
             * !BTSLUtil.isNumeric(language2)){
             * _log.error("parseChannelRequest",
             * "LANGUAGE1 or LANGUAGE2 is not numeric");
             * throw new
             * BTSLBaseException("MobileAppParsers","parseChannelRequest"
             * ,PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
             * }
             */
            p_requestVO.setDecryptedMessage(parsedRequestMessageStr.toString());
            if("SELFREG".equals(type))
            {
            	p_requestVO.setRequestMessageArray(p_requestVO.getDecryptedMessage().split(" "));
            	p_requestVO.setExternalNetworkCode(Constants.getProperty("EXTERNALNWCODE"));
            	
            }
            p_requestVO.setRequestMap(requestMap);
            // p_requestVO.setRequestMSISDN(msisdn);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseChannelRequest", "Exception e: " + be);
            p_requestVO.setRequestMap(requestMap);
            throw be;
        } catch (Exception e) {
        	p_requestVO.setRequestMap(requestMap);
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelRequest", "Exception e: " + e);
            throw new BTSLBaseException("MobileAppParsers", "parseChannelRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    @Override
    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
            if (contentType != null && (p_requestVO.getReqContentType().indexOf("plain") != -1 || p_requestVO.getReqContentType().indexOf("PLAIN") != -1) || p_requestVO
                .getReqContentType().indexOf("text") != -1 || p_requestVO.getReqContentType().indexOf("TEXT") != -1) {
                generateChannelResponse(p_requestVO);
            } else {
                String message = null;
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                    message = p_requestVO.getSenderReturnMessage();
                } else {
                    message = this.getMessageForApp(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                }
                if(PretupsI.SERVICE_TYPE_EVD.equals(p_requestVO.getServiceType())) {//priyank
                	StringBuffer sendingResponse = new StringBuffer();
                    sendingResponse.append("TYPE="+p_requestVO.getServiceType()+"RES"+"&");
                    sendingResponse.append("TXNSTATUS="+p_requestVO.getStatus()+"&");
                    sendingResponse.append("TXNID="+p_requestVO.getTransactionID()+"&");
                    if(message!=null)
                    	message = message.split(":").length==3?message.split(":")[2]:message;
                    sendingResponse.append("MESSAGE="+message);
                    p_requestVO.setSenderReturnMessage(sendingResponse.toString());
                }else p_requestVO.setSenderReturnMessage(message);
                
            }
        } catch (Exception e) {
            LOG.error("generateResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobileAppParsers[generateResponseMessage]",
                p_requestVO.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                generateChannelResponse(p_requestVO);
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobileAppParsers[generateResponseMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
    }

    public static void generateChannelResponse(RequestVO p_requestVO) throws Exception {
        final String METHOD_NAME = "generateChannelResponse";
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelResponse", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null; 
        StringBuffer sbf = null;
		ChannelUserVO channelUserVO=(ChannelUserVO)(p_requestVO.getSenderVO());
		HashMap requestMap=p_requestVO.getRequestMap();
        try {
        	if(PretupsI.SERVICE_TYPE_C2CBUYENQ.equals(requestMap.get("TYPE")) || PretupsI.SERVICE_TYPE_C2STRFSVCNT.equals(requestMap.get("TYPE")) || PretupsI.C2S_TOTAL_NO_OF_TRANSACTION.equals(requestMap.get("TYPE")) ||  PretupsI.SERVICE_TYPE_C2S_PROD_TXN.equals(requestMap.get("TYPE"))||PretupsI.SERVICE_TYPE_PASSBOOKVIEW.equals(requestMap.get("TYPE"))||PretupsI.SERVICE_TYPE_TAX_COMMISSION_CALCULATION.equals(requestMap.get("TYPE"))
        			|| PretupsI.SEND_OTP_FOR_FORGOT_PIN.equals(requestMap.get("TYPE")) || PretupsI.USER_INCOME_DETAILS_VIEW.equals(requestMap.get("TYPE")) || PretupsI.SERVICE_TYPE_C2S_N_PROD_TXN.equals(requestMap.get("TYPE")) || PretupsI.TRANSACTION_DETAILED_VIEW.equals(requestMap.get("TYPE")) 
        			|| PretupsI.AUTO_COMPLETE_USERS_DETAILS.equals(requestMap.get("TYPE"))|| PretupsI.GET_DOMAIN_CATEGORY_CONTROLLER.equals(requestMap.get("TYPE")) || PretupsI.O2C_DIRECT_APPRVL.equals(requestMap.get("TYPE")) || PretupsI.PAYABLE_AMOUNT_CALCULATION.equals(requestMap.get("TYPE"))|| PretupsI.O2C_TRANSFER_INITIATE.equals(requestMap.get("TYPE"))){
        		if (p_requestVO.isSuccessTxn()){
        			String response = p_requestVO.getResponseMap().get("RESPONSE").toString();
        			p_requestVO.setSenderReturnMessage(response);
        		}
        		else{
        			String resType = null;
        			StringBuffer responseSt = new StringBuffer("");
					resType = requestMap.get("TYPE") + "RES";
					responseSt.append("{ \"type\": \"" + resType + "\" ,");
					responseSt.append(" \"txnStatus\": \"" + p_requestVO.getMessageCode() + "\" ,");
					responseSt.append(" \"date\": \"" +new SimpleDateFormat("dd/MM/YY").format(p_requestVO.getCreatedOn()) + "\" ,");
					responseSt.append(" \"message\": \"" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "\"}");
					p_requestVO.setSenderReturnMessage(responseSt.toString());
        		}
                return;
        	}
        	if(PretupsI.OTP_VALID_PIN_RST.equals(requestMap.get("TYPE"))){
        		String resType = null;
    			StringBuffer responseSt = new StringBuffer("");
				resType = requestMap.get("TYPE") + "RES";
				responseSt.append("{ \"type\": \"" + resType + "\" ,");
				responseSt.append(" \"txnStatus\": \"" + p_requestVO.getMessageCode() + "\" ,");
				responseSt.append(" \"message\": \"" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "\"}");
				p_requestVO.setSenderReturnMessage(responseSt.toString());
				return;
        	}
        	if("SELFREG".equals(requestMap.get("TYPE"))){
        		String resType = null;
    			StringBuffer responseSt = new StringBuffer("");
				resType = requestMap.get("TYPE") + "RES";
				responseSt.append("{ \"type\": \"" + resType + "\" ,");
				responseSt.append(" \"txnStatus\": \"" + p_requestVO.getMessageCode() + "\" ,");
				if(p_requestVO.getVomsMessage()!=null)
				responseSt.append(" \"message\": \"" + p_requestVO.getVomsMessage() + "\"}");
				else
					responseSt.append(" \"message\": \"" + RestAPIStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "\"}");	
				p_requestVO.setSenderReturnMessage(responseSt.toString());
				return;
        	}
        	if("COMINCOME".equals(requestMap.get("TYPE")) || PretupsI.SERVICE_TYPE_VOUCHER_INFO.equals(requestMap.get("TYPE"))){
        		if (p_requestVO.isSuccessTxn()){
        		String response = p_requestVO.getResponseMap().get("RESPONSE").toString();
                p_requestVO.setSenderReturnMessage(response);
        		}
        		else{
        			String resType = null;
        			StringBuffer responseSt = new StringBuffer("");
					resType = requestMap.get("TYPE") + "RES";
					responseSt.append("{ \"type\": \"" + resType + "\" ,");
					responseSt.append(" \"txnStatus\": \"" + p_requestVO.getMessageCode() + "\" ,");
					responseSt.append(" \"message\": \"" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "\"}");
					p_requestVO.setSenderReturnMessage(responseSt.toString());
        		}
                return;
        	}
            if("OTHERBALAN".equals(p_requestVO.getServiceType())){
            	p_requestVO.setSenderMessageRequired(false);
            }
            sbf = new StringBuffer(1024);
            sbf.append("TYPE=" + requestMap.get("TYPE") + "RES");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&TXNSTATUS=" + PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("&TXNSTATUS=" + p_requestVO.getMessageCode());
            }

            if (PretupsI.SERVICE_TYPE_VOUCHER_TYPE.equals(requestMap.get("TYPE")) ||
                	PretupsI.SERVICE_TYPE_VOUCHER_SEGMENT.equals(requestMap.get("TYPE")) ||
                	PretupsI.SERVICE_TYPE_VOUCHER_DENOMINATION.equals(requestMap.get("TYPE"))) {
            	String response = null;
            	    if(p_requestVO.getResponseMap()!=null)
    				response = p_requestVO.getResponseMap().get("RESPONSE").toString();

                	
                	if(p_requestVO.isSuccessTxn())
                	{
                		sbf.append("&");
                		sbf.append(response);
                	}
                	else
                	{
                		sbf.append("No data found");
                	}
                }
           
            if (PretupsI.SERVICE_TYPE_CHNN_USER_REGISTRATION.equals(requestMap.get("TYPE"))) {

                sbf.append("&DECRYPTION_KEY=" + p_requestVO.getEncryptionKey());
                if(channelUserVO!=null)
				sbf.append("&CATEGORY_CODE="+channelUserVO.getCategoryCode());
                else
                	sbf.append("&CATEGORY_CODE= ");	
                
                sbf.append("&SESSIONEXPIRY="+((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAPP_SESSION_EXPIRY_SEC))).intValue());
            
            }else if(PretupsI.SERVICE_TYPE_UPUSRHRCHY.equals(requestMap.get("TYPE"))) {
            	if (p_requestVO.getResponseMap() != null && p_requestVO.getResponseMap().get("RESPONSE") != null) {
    				String response = p_requestVO.getResponseMap().get("RESPONSE").toString();
    				sbf.append("&UPWARDHIERARCHY=");
    				sbf.append(response);
    			
    			} else {
    				sbf.append("&UPWARDHIERARCHY=");
    				sbf.append("No data found");
    			}
                
            }
            else if(PretupsI.SERVICE_TYPE_C2CBUYENQ.equals(requestMap.get("TYPE"))) {
            	if (p_requestVO.getResponseMap() != null && p_requestVO.getResponseMap().get("RESPONSE") != null) {
    				String response = p_requestVO.getResponseMap().get("RESPONSE").toString();
    				sbf.append("&C2CRECENTBUYENQ=");
    				sbf.append(response);
    			
    			} else {
    				sbf.append("&C2CRECENTBUYENQ=");
    				sbf.append("No data found");
    			}
                
            }
            else if (PretupsI.MAPP_LOGIN_REQ.equals(requestMap.get("TYPE"))) {
                if (p_requestVO.isOTP()) {
                    sbf.append("&OTP=" + PretupsI.YES);
                } else {
                    sbf.append("&OTP=" + PretupsI.NO);
                }
                
                if(channelUserVO!=null)
                {
    				sbf.append("&USER_NAME="+channelUserVO.getUserName());
    				sbf.append("&CATEGORY_CODE="+channelUserVO.getCategoryCode());
    				sbf.append("&CATEGORY_NAME="+channelUserVO.getCategoryVO().getCategoryName().toString().trim());
                		if(channelUserVO.getAssociatedServiceTypeList().size()>0&&!BTSLUtil.isNullString(p_requestVO.getInfo1()))
    				sbf.append("&SERVICE_ALLOW="+getServiceList(channelUserVO.getAssociatedServiceTypeList())+","+p_requestVO.getInfo1());
    				else if(channelUserVO.getAssociatedServiceTypeList().size()>0)
    				sbf.append("&SERVICE_ALLOW="+getServiceList(channelUserVO.getAssociatedServiceTypeList())+","+"O2CINICU");
    				else if(!BTSLUtil.isNullString(p_requestVO.getInfo1()))
    				sbf.append("&SERVICE_ALLOW="+p_requestVO.getInfo1()); 
    				else
    				sbf.append("&SERVICE_ALLOW= ");	
                    sbf.append("&PINRESETREQD="+channelUserVO.getPinReset());	
                }
                    else
                    {
			sbf.append("&USER_NAME= ");
                    	sbf.append("&CATEGORY_CODE= ");	
                    	sbf.append("&CATEGORY_NAME= ");
        				sbf.append("&SERVICE_ALLOW= ");
                    }
                sbf.append("&SESSIONEXPIRY="+((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAPP_SESSION_EXPIRY_SEC))).intValue());
				sbf.append("&TOKEN="+p_requestVO.getToken());
				sbf.append("&JWTTOKEN="+p_requestVO.getJwtToken());
				sbf.append("&REFRESHTOKEN="+p_requestVO.getRefreshToken());
            }
            else if (PretupsI.MAPP_VERSION_REQ.equals(requestMap.get("TYPE"))) {
            	sbf.append("&APPTYPE="+p_requestVO.getInfo1()); 
            	sbf.append("&VERSION="+p_requestVO.getInfo2()); 
            	sbf.append("&PLATFORM="+p_requestVO.getInfo3()); 
            	sbf.append("&UPDTYPE="+p_requestVO.getInfo4()); 
            	sbf.append("&UPDURL="+p_requestVO.getInfo5());
            }
              else  if (PretupsI.VAS_SERVICES.equals(requestMap.get("TYPE"))) {
					sbf.append("&GROUPING=" + p_requestVO.getInfo1());
            } else if(PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY.equals(requestMap.get("TYPE")))
            {     
            	if(p_requestVO.getMessageArguments() != null && p_requestVO.getMessageArguments().length > 0 
            			&& p_requestVO.getMessageArguments()[0] != null) {
            		String[] args=p_requestVO.getMessageArguments()[0].split(",");
            		String temp = PretupsI.EMPTY;
            		for(int i=0; i<args.length; i++) {
            			if(args[i] != null) {
            				if(i==0) {
            					temp = temp + args[i].substring(args[i].indexOf(":") + 1, args[i].length()).trim();
                			} else {
                				temp = temp + "|" + args[i].substring(args[i].indexOf(":") + 1, args[i].length()).trim();
                			}
            			}
            		}
            		sbf.append("&BALANCE="+temp);
            	}
            }else if("OTHERBALAN".equals(requestMap.get("TYPE")))
            {     
            	if(p_requestVO.getMessageArguments() != null && p_requestVO.getMessageArguments().length > 1 
            			&& p_requestVO.getMessageArguments()[1] != null) {
            		String[] args=p_requestVO.getMessageArguments()[1].split(",");
            		String temp = PretupsI.EMPTY;
            		for(int i=0; i<args.length; i++) {
            			if(args[i] != null && !BTSLUtil.isNullString(args[i])) {
            				if(i==0) {
            					temp = temp + args[i].substring(args[i].indexOf(":") + 1, args[i].length()).trim();
                			} else {
                				temp = temp + "|" + args[i].trim();
                			}
            			}
            		}
            		sbf.append("&BALANCE="+temp);
            	}
            }
            
            //add extra response tags for OTHERBALAN service
            if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("OTHERBALAN_EXTRA_RESTAGS")) && (PretupsI.SERVICE_TYPE_OTHERBALAN.equalsIgnoreCase(p_requestVO.getServiceType()) || PretupsI.SERVICE_TYPE_CHILDPINRESET.equalsIgnoreCase(p_requestVO.getServiceType()))) {
                
            	Date currentDate = new Date();
            	String dateStr = null;
            	//currentDate = BTSLUtil.getTimestampFromUtilDate(currentDate);
            	dateStr = BTSLUtil.getDateTimeStringFromDate(currentDate);
            	sbf.append("&DATETIME=" + dateStr);
                
            	if(!BTSLUtil.isNullString(p_requestVO.getExternalReferenceNum())) {
            		sbf.append("&EXTREFNUM=" + p_requestVO.getExternalReferenceNum());
            	}
                            	
            }
            
            		
            		
            
            if ((PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(p_requestVO.getServiceType()) || PretupsI.SERVICE_TYPE_CHNL_BILLPAY.equals(p_requestVO.getServiceType()) 
            		|| PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE.equals(p_requestVO.getServiceType()) || PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(p_requestVO.getServiceType())) 
            		&& p_requestVO.isSuccessTxn()) {
            	sbf.append("&TXNID="+p_requestVO.getTransactionID());
            	sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), "210", p_requestVO.getMessageArguments()));
            }
            else if (PretupsI.SERVICE_TYPE_VAS_RECHARGE.equals(p_requestVO.getServiceType()) && p_requestVO.isSuccessTxn()) {
            	sbf.append("&TXNID="+p_requestVO.getTransactionID());
            	 sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.VAST_SENDER_SUCCESS, p_requestVO.getMessageArguments())); 	
            }
            else if ("DTH".equals(p_requestVO.getServiceType()) && p_requestVO.isSuccessTxn()) {
             sbf.append("&TXNID="+p_requestVO.getTransactionID());
           	 sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), PretupsErrorCodesI.DTH_SENDER_SUCCESS, p_requestVO.getMessageArguments())); 	
           }
            else if (PretupsI.SERVICE_TYPE_EVD.equals(p_requestVO.getServiceType()) && p_requestVO.isSuccessTxn()) {
                String[] arg = { p_requestVO.getTransactionID() };
                sbf.append("&TXNID="+p_requestVO.getTransactionID());
                sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), "125267", arg));
            }
            else if ("C2CVWTRFVC".equals(p_requestVO.getServiceType()) && p_requestVO.isSuccessTxn()) {
                String[] arg = { p_requestVO.getTransactionID() };
                sbf.append("&TRANSFER=" +  p_requestVO.getChannelTransferVO());
               
            }  else if ("C2CVTAPLST".equals(p_requestVO.getServiceType()) && p_requestVO.isSuccessTxn()) {
            	 Gson gson = new Gson();
                 // convert your list to json
                 String transferList = gson.toJson(p_requestVO.getChannelTransfersList());
                sbf.append("&TXNAPPLIST=" + transferList);
                sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            } else if((PretupsI.SERVICE_TYPE_C2C_VOMS.equals(p_requestVO.getServiceType()) || PretupsI.SERVICE_TYPE_C2C_VOMS_INI.equals(p_requestVO.getServiceType())
            		|| PretupsI.SERVICE_TYPE_C2C_VOMS_TRANSFERS.equals(requestMap.get("TYPE")) || PretupsI.SERVICE_TYPE_C2C_VOUCHER_APPROVAL.equals(requestMap.get("TYPE"))
            		|| PretupsI.SERVICE_TYPE_C2C_VOMS_INITIIATE.equals(requestMap.get("TYPE")) || PretupsI.SERVICE_TYPE_CHNL_TRANSFER.equals(p_requestVO.getServiceType())
            		|| PretupsI.SERVICE_TYPE_CHNL_RETURN.equals(requestMap.get("TYPE")) ||PretupsI.SERVICE_TYPE_CHNL_WITHDRAW.equals(requestMap.get("TYPE"))
            		|| PretupsI.SERVICE_TYPE_C2C_INITIATE.equals(requestMap.get("TYPE")) || PretupsRestI.C2C_TRF_APPR.equals(requestMap.get("TYPE"))) && p_requestVO.isSuccessTxn()){
            	sbf.append("&TXNID=" +  p_requestVO.getTransactionID());
            	 String[] arg = { p_requestVO.getTransactionID() };
            	 sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), arg));
            }else if("USRDETAILS".equals(p_requestVO.getServiceType()) && p_requestVO.isSuccessTxn()){
            	sbf.append("&USERNAMEPREFIX=" + requestMap.get("USERNAMEPREFIX"));
            	sbf.append("&FIRSTNAME=" +  requestMap.get("FIRSTNAME"));
            	if(requestMap.get("LASTNAME") == null)
            	{
            		sbf.append("&LASTNAME=" + "null");
            	}
            	else
            	{
            		sbf.append("&LASTNAME=" + requestMap.get("LASTNAME"));
            	}
             	sbf.append("&CATEGORYNAME=" + requestMap.get("CATEGORYNAME"));
             	sbf.append("&CATEGORYCODE=" + requestMap.get("CATEGORYCODE"));
           	 //sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), arg));
           }else if("C2STOTTRNS".equals(p_requestVO.getServiceType()) && p_requestVO.isSuccessTxn()){
           	sbf.append("&TOTALTNXCOUNT=" + p_requestVO.getC2sTotaltxnCount());
           	
          	 //sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), arg));
          }
            else {
                sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            }
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("generateChannelResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobileAppParsers[generateChannelResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateChannelResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateChannelResponse", "Exiting responseStr: " + responseStr);
            }
        }
    }

    @Override
    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
    }

    public static String getMessage(Locale locale, String key, String[] p_args1) {
        final String methodName = "getMessage";
        String[] p_args = null;
        if (p_args1 == null || p_args1.length == 0) {
            p_args = p_args1;
        } else {
            p_args = new String[p_args1.length];
            for (int i = 0; i < p_args.length; i++) {
                p_args[i] = p_args1[i];
            }
        }
        // System.out.println("p_args"+p_args+",p_args1"+p_args1);
        String message = getMessageForApp(locale, key, p_args);
        try {
            LocaleMasterVO localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            if (message.indexOf("mclass^") == 0) {
                int colonIndex = message.indexOf(":");
                message = message.substring(colonIndex + 1);
                int endIndexForMessageCode;
                // String messageCode=null;
                if ("ar".equals(localeMasterVO.getLanguage())) {
                    endIndexForMessageCode = message.indexOf("%00%3A");
                    if (endIndexForMessageCode != -1) {
                        // messageCode=URLDecoder.decode(message.substring(0,endIndexForMessageCode),"UTF16");
                        // message=message.substring(endIndexForMessageCode+1);
                        message = message.substring(endIndexForMessageCode + "%00%3A".length());
                        if (message.startsWith("%00%20")) {
                            message = message.substring("%00%20".length());
                        }
                    }
                } else {
                    endIndexForMessageCode = message.indexOf(":");
                    if (endIndexForMessageCode != -1) {
                        // messageCode=message.substring(0,endIndexForMessageCode);
                        message = message.substring(endIndexForMessageCode + 1);
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            LOG.error(methodName, "Exception e: " + e);
        }
        return message;
    }

    /**
     * method is added to parse if request is XML type.
     */

    @Override
    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {

        final String methodName = "parseRequestMessage";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
        }
        StringBuilder parsedRequestMessageStr = new StringBuilder();
        HashMap requestMap = null;
        String type = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            String[] mandatoryRequestParamArr = null; 
            String[] msgFormatArr = null; 
            String language1 = null;
            String language2 = null;
            String imei = null;
            String message = null;
            String requestStr = p_requestVO.getRequestMessage();
            int i1 = -1;
            i1 = requestStr.indexOf("Message=");
            if (i1 > -1) {
                String msg2 = requestStr.substring(i1 + "Message=".length(), requestStr.length());
                String msg1 = requestStr.substring(0, i1);
                requestMap = BTSLUtil.getStringToHash(msg1, "&", "=");
                requestMap.put("Message", msg2);
            } else {
                requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            }
            
            HashMap seviceKeywordMap = new HashMap();
            type = (String) requestMap.get("TYPE");
            String msisdn = (String) requestMap.get("MSISDN");
            if ((BTSLUtil.isNullString(msisdn) && !type.equals(PretupsI.SYSTEM_LANGUAGE)) && (BTSLUtil.isNullString(msisdn) && !type.equals(PretupsI.PRODUCT_GATEWAY_SERVICES) && !type
                .equals(PretupsI.VAS_SERVICES) && !type.equals(PretupsI.VAS_ENQUIRY) && !type.equals(PretupsI.MAPP_VERSION_REQ) )) {
                throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            }
            p_requestVO.setRequestMap(requestMap);
            seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
            ServiceKeywordCacheVO serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type + "_" + p_requestVO.getModule() + "_" + p_requestVO
                .getRequestGatewayType() + "_" + p_requestVO.getServicePort());
            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobileAppParsers[parseRequestMessage]",
                    p_requestVO.getRequestIDStr(), "retailer msisnd", "", "Service keyword not found for the keyword=" + type + " For Gateway Type=" + p_requestVO
                        .getRequestGatewayType() + "Service Port=" + p_requestVO.getServicePort());
                throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobileAppParsers[parseRequestMessage]",
                    p_requestVO.getRequestIDStr(), msisdn, "", "Service keyword suspended for the keyword=" + type + " For Gateway Type=" + p_requestVO
                        .getRequestGatewayType() + "Service Port=" + p_requestVO.getServicePort());
                throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            
         // Language parameters handling start
            if (requestMap.containsKey("LANGUAGE1")) {
                language1 = (String) requestMap.get("LANGUAGE1");
                if (LocaleMasterCache.getLocaleFromCodeDetails(language1) != null) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
                } else {
                    LOG.error(methodName, "LANGUAGE1 is not numeric");
                    throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                }
            }
            if (requestMap.containsKey("LANGUAGE2")) {
                language2 = (String) requestMap.get("LANGUAGE2");
                if (LocaleMasterCache.getLocaleFromCodeDetails(language2) != null) {
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
                } else {
                    LOG.error(methodName, "LANGUAGE2 is not numeric");
                    throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                }
            }
            p_requestVO.setRequestMSISDN(msisdn);
            p_requestVO.setSerialNo((String) requestMap.get("SERIALNO"));
            p_requestVO.setVoucherCode((String) requestMap.get("VOUCHER_CODE"));
            ChannelUserVO userVO = null;
            try{
            	mcomCon = new MComConnection();
            	con=mcomCon.getConnection();
            	ChannelUserBL.updateUserInfo(con, p_requestVO);
            	userVO = new ChannelUserDAO().loadActiveUserId(con, p_requestVO.getRequestMSISDN(), "MSISDN");
            	if (userVO != null) {
					p_requestVO.setActiverUserId(userVO.getUserID());
					p_requestVO.setEncryptionKey(userVO.getInfo1());
					p_requestVO.setImei(userVO.getInfo2());
					p_requestVO.setEmailId(userVO.getEmail());
					p_requestVO.setmHash(userVO.getInfo3());
					p_requestVO.setToken(userVO.getInfo4());
					p_requestVO.setTokenLastUsedDate(userVO.getTokenLastUsedDate());
					p_requestVO.setExternalNetworkCode(userVO.getNetworkCode());
				} else {
					throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.NO_USER_EXIST);
				}
				ChannelUserBL.loadAllowedTransferForCategory(con,userVO.getCategoryCode(), p_requestVO);
            }finally{
				if (mcomCon != null) {
					mcomCon.close("MobileAppParsers#parseRequestMessage");
					mcomCon = null;
				}
            }


            
            String messageFormat = serviceKeywordCacheVO.getMessageFormat();
            String requestParam = serviceKeywordCacheVO.getRequestParam();
            if (BTSLUtil.isNullString(requestParam) || (BTSLUtil.isNullString(messageFormat))) {
                LOG.error("MobileAppParsers", "requestParam=" + requestParam + ",messageFormat=" + messageFormat);
                throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            } else {
                mandatoryRequestParamArr = requestParam.split(",");
                msgFormatArr = messageFormat.split(" ");
            }
            if (requestMap.containsKey("Message")) {
                message = (String) requestMap.get("Message");
                message = message.replaceAll("\\s", "+");
                message = BTSLUtil.decryptAESNew(message, p_requestVO.getEncryptionKey());
                requestMap = BTSLUtil.getStringToHash(message, "&", "=");
                for (int i = 0; i < mandatoryRequestParamArr.length; i++) {
                    LOG.error(methodName, "mandatoryRequestParamArr[i]=" + mandatoryRequestParamArr[i]);
                    if (requestMap.containsKey(mandatoryRequestParamArr[i])) {
                        if (BTSLUtil.isNullString((String) requestMap.get(mandatoryRequestParamArr[i]))) {
                            throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
                        }
                    }else {
                        throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
                    }
                }
                for (int i = 0; i < msgFormatArr.length; i++) {
                    if (requestMap.containsKey(msgFormatArr[i])) {
                        parsedRequestMessageStr = parsedRequestMessageStr.append(requestMap.get(msgFormatArr[i]));
                        parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    }
                    if("PIN".equals(msgFormatArr[i])){ 
                    	if(!requestStr.contains("&PIN=") && !BTSLUtil.isNullString(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS")) && Arrays.asList(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS").split(",")).contains(type)){
                    		parsedRequestMessageStr = parsedRequestMessageStr.append(BTSLUtil.decryptText(userVO.getSmsPin()));
                    		parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    	}else if(requestStr.contains("&PIN=") && null == requestMap.get("PIN")){
                    		LOG.error(methodName, "PIN is required.");
            				throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.PIN_REQUIRED);
                    	}else if(!(requestMap.get("PIN")).toString().equals(BTSLUtil.decryptText(userVO.getSmsPin()))){
                    		LOG.error(methodName, "Invalid PIN.");
 	                        throw new BTSLBaseException("MobileAppParsers", methodName + "Request", PretupsErrorCodesI.INVALID_PIN);
                    	}
                    }
                }
            } else {
                for (int i = 0; i < mandatoryRequestParamArr.length; i++) {
                    if (requestMap.containsKey(mandatoryRequestParamArr[i])) {
                        if (BTSLUtil.isNullString((String) requestMap.get(mandatoryRequestParamArr[i]))) {
                            throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
                        }
                    }else {
                        throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
                    }
                }
                for (int i = 0; i < msgFormatArr.length; i++) {
                    if (requestMap.containsKey(msgFormatArr[i])) {
                        parsedRequestMessageStr = parsedRequestMessageStr.append(requestMap.get(msgFormatArr[i]));
                        parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    }
                    if("PIN".equals(msgFormatArr[i])){ 
                    	if(!requestStr.contains("&PIN=") && !BTSLUtil.isNullString(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS")) && Arrays.asList(Constants.getProperty("NON_FINANCIAL_SERVICES_PIN_BYPASS").split(",")).contains(type)){
                    		parsedRequestMessageStr = parsedRequestMessageStr.append(BTSLUtil.decryptText(userVO.getSmsPin()));
                    		parsedRequestMessageStr.append(C2S_MESSAGE_SEP);
                    	}else if(requestStr.contains("&PIN=") && null == requestMap.get("PIN")){
                    		LOG.error(methodName, "PIN is required.");
            				throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.PIN_REQUIRED);
                    	}else if(!(requestMap.get("PIN")).toString().equals(BTSLUtil.decryptText(userVO.getSmsPin()))){
                    		LOG.error(methodName, "Invalid PIN.");
 	                        throw new BTSLBaseException("MobileAppParsers", methodName + "Request", PretupsErrorCodesI.INVALID_PIN);
                    	}
                    }
                }
              }
            
         // Length check of IMEI
           
            if (requestMap.containsKey("IMEI")) {
                imei = (String) requestMap.get("IMEI");
                String Mhash=(String) requestMap.get("MHASH");
                String Token=(String)requestMap.get("TOKEN");
                if ("USRREGREQ".equals(type) || "USRAUTH".equals(type) || "LANG".equals(type) || "PRGWC2S2".equals(type) || "VASSERVICE".equals(type) || "VASENQUIRY"
                    .equals(type)) {
	                    if (BTSLUtil.isNullString(imei)) {
	                        LOG.error(methodName, "imei is null");
	                        throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.ERROR_INVALID_IMEI);
	                    }
	                    
	                    if ( BTSLUtil.isNullString(Mhash)) {
	                        LOG.error(methodName, "MHash is null");
	                        throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.MAPP_INVALID_MHASH);
	                    }
	                    
	                    
		        } else{  
		           
		                 if (BTSLUtil.isNullString(imei) || !imei.equals(p_requestVO.getImei()) ) {
		                      LOG.error(methodName, "IMEI is not of length 15 or is not numeric or null or not valid");
		                      throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.ERROR_INVALID_IMEI,0,null);
		                 }
		                 
		                 if (BTSLUtil.isNullString(Mhash) ||!Mhash.equals(p_requestVO.getmHash())) {
		                        LOG.error(methodName, "Mhash is not of length 15 or is not numeric or null or not valid");
		                        throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.MAPP_INVALID_MHASH,0,null);
		                 }
		               
			             if (BTSLUtil.isNullString(Token) ||  !Token.equals(p_requestVO.getToken() )) {
			                        LOG.error(methodName, "Token is invalid");
			                        throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.MAPP_INVALID_TOKEN,0,null);
			             }
			                	
			             long tokenExpiryInMin=((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TOKEN_EXPIRY_IN_MINTS))).longValue();
			             Date currentdate= new Date();//Token expiry need to validate   	
			             if( BTSLUtil.isTimeExpired(p_requestVO.getTokenLastUsedDate(), tokenExpiryInMin)){
			                 LOG.error(methodName, "Token is Expired");
			                 throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.MAPP_TOKEN_EXPIRED,0,null);
			                    
			             }
			           
		              }
            } 
            else {
                throw new BTSLBaseException("MobileAppParsers",methodName, PretupsErrorCodesI.ERROR_INVALID_IMEI,0,null);
            }
            
            p_requestVO.setDecryptedMessage(parsedRequestMessageStr.toString());
            p_requestVO.setRequestMap(requestMap);
            p_requestVO.setRequestMessageArray(parsedRequestMessageStr.toString().split(" "));
            HashMap responseMap = new HashMap<String, String>();
            responseMap.put("RESPONSEPARAM", serviceKeywordCacheVO.getResponseParam());
            p_requestVO.setResponseMap(responseMap);
        } catch (BTSLBaseException be) {
            LOG.error(methodName, "Exception e: " + be);
            LOG.errorTrace(methodName, be);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            throw be;
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            LOG.error(methodName, "Exception e: " + e);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("MobileAppParsers", methodName, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }

    }

    /**
     * generateResponseMessage is used to generate response for mobile app
     * request.
     */

    @Override
    public void generateResponseMessage(RequestVO p_requestVO) {

        final String METHOD_NAME = "generateResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        }
        try {
                if (contentType != null && (p_requestVO.getReqContentType().indexOf("plain") != -1 || p_requestVO.getReqContentType().indexOf("PLAIN") != -1) || p_requestVO
                    .getReqContentType().indexOf("text") != -1 || p_requestVO.getReqContentType().indexOf("TEXT") != -1) {
                	generateResponse(p_requestVO);
            } else {
                String message = null;
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                    message = p_requestVO.getSenderReturnMessage();
                } else {
                    message = getMessageForApp(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                }
                p_requestVO.setSenderReturnMessage(message);
            }
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "  Exception while generating Response Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobileAppParsers[generateResponseMessage]", p_requestVO
                .getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
            	generateResponse(p_requestVO);
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobileAppParsers[generateResponseMessage]",
                    p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO
                    .setSenderReturnMessage(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            }
        }
    }
    
    /**
     * Get Message from Messages.props on the basis of locale
     * 
     * @param locale
     * @param key
     * @param args
     * @return
     */
    public static String getMessageForApp(Locale locale, String key, String[] p_args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getMessageForApp", "Entered");
        }
        String message = null;
        final String METHOD_NAME = "getMessageForApp";
        try {
            if (locale == null) {
                LOG.error("getMessage",
                    "Locale not defined considering default locale " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)) + " " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)) + "    key: " + key);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BTSLUtil[getMessage]", "", "", " ",
                    "Locale not defined considering default locale " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)) + " " + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)) + "    key: " + key);
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("getMessage", "entered locale " + locale.getDisplayName() + " key: " + key + " args: " + p_args);
            }
            MessagesCache messagesCache = MessagesCaches.get(locale);
            if (messagesCache == null) {
                LOG.error("getMessage", "Messages cache not available for locale: " + locale.getDisplayName() + "    key: " + key);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", " ",
                    "Messages cache not available for locale " + locale.getDisplayName() + "    key: " + key);
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                messagesCache = MessagesCaches.get(locale);
                if (messagesCache == null) {
                    return null;
                }
            }
            message = messagesCache.getProperty(key);
            // ChangeID=LOCALEMASTER
            // populate localemasterVO for the requested locale
            final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            

            if (("ru".equals(locale.getLanguage()) && message != null) || ("ar".equals(locale.getLanguage()) && message != null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "encoding msg for russian/arabic locale: " + locale.getDisplayName() + " key: " + key + " message: " + message);
                }
                localeVO.setEncoding("UTF-8");
                final int indexOf = message.indexOf("mclass^");
                String tempMessage = null;
                String messageWithID = null;
                String messageID = null;
                String messageToEncode = null;
                if (indexOf != -1) {
                    tempMessage = message.substring(0, message.indexOf(":", indexOf) + 1);
                    messageWithID = message.substring(message.indexOf(":", indexOf) + 1);
                    // _log.error("getMessage","Before encoding tempMessage="+tempMessage+" messageWithID="+messageWithID);
                } else {
                    messageWithID = message;
                }
                if (messageWithID.lastIndexOf("[") != -1) {
                    if ("ar".equals(locale.getLanguage())) {
                        messageToEncode = messageWithID.substring(0, messageWithID.lastIndexOf("["));
                    } else {
                        messageToEncode = messageWithID.substring(messageWithID.lastIndexOf("]") + 1);
                    }
                    LOG.debug("getMessage", "Message: " + messageToEncode);
                    messageID = messageWithID.substring(messageWithID.lastIndexOf("[") + 1, messageWithID.lastIndexOf("]"));
                    // _log.error("getMessage","*************messageID="+messageID);
                    LOG.debug("getMessage", "MessageID: " + messageID);
                } else {
                    messageToEncode = messageWithID;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "messageToEncode: " + messageToEncode + " messageWithID: " + messageWithID);
                }
                // ChangeID=LOCALEMASTER
                // pass the encoding scheme from the locale master VO
                messageToEncode = MessageFormat.format(BTSLUtil.escape(messageToEncode), p_args);
                final String text = BTSLUtil.encodeSpecial(messageToEncode, false, localeVO.getEncoding());

                LOG.debug("getMessage", "message after encoding: " + text);
                String str = null;
                if (messageID != null) {
                    // ChangeID=LOCALEMASTER
                    // pass the encoding scheme from the locale master VO
                    final String unicodeMessageID = BTSLUtil.encodeSpecial(messageID, true, localeVO.getEncoding()) + "%00%3A";
                    str = unicodeMessageID + text;
                } else {
                    str = text;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "message after encode but before replacing args str: " + str);
                }
                if (!BTSLUtil.isNullString(tempMessage)) {
                    str = tempMessage + str;
                }
                if (p_args != null && p_args.length > 0) {
                    final int argslen = p_args.length;
                    for (int index = 0; index < argslen; index++) {
                        // Change done by Ankit Zindal
                        // Date 17/08/06 Change ID=ENCODESPCL
                        // Reason of change is that, in some cases array size is
                        // more but elements are null in it
                        // So it gives null pointer exception when we are going
                        // to encode a null element.
                        // Now encode special method is called only when element
                        // of array is not null.

                        // ChangeID=LOCALEMASTER
                        // pass the encoding scheme from the locale master VO
                        if (p_args[index] != null) {
                            p_args[index] = BTSLUtil.encodeSpecial(p_args[index], true, localeVO.getEncoding());
                        }
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "message  after encoding and before formatting: " + message);
                }
                if (str != null) {
                    message = str;
                }
            }
            /*
             * The change below is done by ankit Z on date 4/8/06 for following
             * problem
             * 1) In case when message is not find in message.props then message
             * formatter should not be called
             * 2) If message is not defined for key which are combination of two
             * parts i.e. have under score in it then no event is not raised
             * This alarm is not raised because we will try other key as its
             * alternative
             * 3) If key not contains under score then alarm will be raised.
             */
            if (!BTSLUtil.isNullString(message) && !"ar".equals(locale.getLanguage())) {
                message = MessageFormat.format(BTSLUtil.escape(message), p_args);
            } else if (!BTSLUtil.isNullString(key) && key.indexOf("_") == -1) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "",
                    " Exception: Message not defined for key" + key);
            }
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e
                .getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getMessage", "Exiting message: " + message);
            }
        }
        return message;
    }

    public static String getServiceList(List p_ServiceList) {
        String methodName = "getServiceList";
		StringBuilder sb = new StringBuilder(1024);
		if (LOG.isDebugEnabled()) {
            LOG.debug("getServiceList", "Entered ");
        }
        StringBuffer str = new StringBuffer();
        if (p_ServiceList != null) {
            ListValueVO listValueVO = null;
            for (int i = 0, j = p_ServiceList.size(); i < j; i++) {
                listValueVO = (ListValueVO) p_ServiceList.get(i);
                if (PretupsI.YES.equalsIgnoreCase(listValueVO.getLabel())) {
                	sb.setLength(0);
                	LogFactory.printLog(methodName, sb.append("listValueVO.getLabel()= ").append(listValueVO.getLabel()).append(", listValueVO.getValue()=").append(listValueVO.getValue()), LOG);
                	if(i==0)
                	str.append(listValueVO.getValue());
                	else
                	str.append(","+listValueVO.getValue());
                }
            }

        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getServiceList", "Exit ");
        }
        return str.toString();
    }
    
    
    
    public static void generateResponse(RequestVO p_requestVO) throws Exception {
        final String METHOD_NAME = "generateResponse";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_requestVO: " + p_requestVO.toString());
        }
        String responseStr = null;
        StringBuilder sbf = null;
		HashMap requestMap=p_requestVO.getRequestMap();
		HashMap responseMap=p_requestVO.getResponseMap();
        try {
            sbf = new  StringBuilder();
            sbf.append("TYPE=" + requestMap.get("TYPE") + "RES");

            if (p_requestVO.isSuccessTxn()) {
                sbf.append("&TXNSTATUS=" + PretupsI.TXN_STATUS_SUCCESS);
            } else {
                sbf.append("&TXNSTATUS=" + p_requestVO.getMessageCode());
            }
            sbf.append("&MESSAGE=" + MobileAppParsers.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            if (responseMap != null && VOMSI.SERVICE_TYPE_VOUCHER_QRY.equals(requestMap.get("TYPE")) && 
            		p_requestVO.isSuccessTxn()) {
            	sbf.append("&TOPUP=" + responseMap.get(VOMSI.TOPUP));
            	sbf.append("&STATUS=" + responseMap.get(VOMSI.VOMS_STATUS));
            	sbf.append("&VOUCHER_EXPIRY_DATE=" + responseMap.get(VOMSI.EXPIRY_DATE));
            	sbf.append("&VOUCHERPROFILENAME=" + responseMap.get(VOMSI.PRODUCT_NAME));
            	sbf.append("&VALIDITY=" + responseMap.get(VOMSI.VOMS_VALIDITY));
            	
            }
            
            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error(METHOD_NAME, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobileAppParsers[generateResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting responseStr: " + responseStr);
            }
        }
    }

}
