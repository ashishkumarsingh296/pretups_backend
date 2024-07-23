package com.btsl.pretups.gateway.parsers;

/*
 * @(#)SMSCParsers.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gurjeet Singh Nov 04, 2005 Initital Creation
 * GAURAV PANDEY MARCH 22 2012 MODIFIED
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle SMSC requests
 */

import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SMSCParsers extends ParserUtility {
	  private static final Log LOG = LogFactory.getLog(ParserUtility.class.getName());
	  
    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
        String message = null;
        if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
            message = p_requestVO.getSenderReturnMessage();
        } else {
            message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
        }

        p_requestVO.setSenderReturnMessage(message);
    }
    
    private String getNodeValue(JsonNode node, String value) {
        if (node.get(value) != null) {
            return node.get(value).textValue();
        } else {
            return "";
        }
    }

    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
    	final String METHOD_NAME = "parseChannelRequestMessage";
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        // System.out.println(p_requestVO.getDecryptedMessage());
        // parseMessage(p_requestVO);
        String message = p_requestVO.getRequestMessage();
        if (message.contains(" ")) {
            if (message.substring(0, message.indexOf(" ")).equals("SUSRESUSR")) {
                suspendResumeChannelUserParse(p_requestVO);
            }
        }
        
        
      //for web gateway handling revamp 
        if(p_requestVO.getRequestGatewayType().equals(PretupsI.WEB_LOCALE)) {
	        String requestMessageOrigStr = p_requestVO.getRequestMessageOrigStr();
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode requestMessage = null;
	        String sourceType = null;
	        try {
	        	requestMessage = mapper.readTree(requestMessageOrigStr);
	        	sourceType = getNodeValue(requestMessage, "sourceType");
	        }catch(Exception e) {
	        	LOG.errorTrace(METHOD_NAME, e);
	        }
	        
	        if(!BTSLUtil.isNullString(sourceType) && "JSON".equals(sourceType)) {
	        	RestParser restParser = new RestParser();
	        	restParser.parseChannelRequestMessage(p_requestVO,pCon);
	        }else {
	        	//called originally in the method
	        	ChannelUserBL.updateUserInfo(pCon, p_requestVO);
	        }
        }
    }
    
    

    /*
     * In case Vodafone-Egypt there is a issue that
     * if a user logined in (WEB) using language ENGLISH
     * and user language is Arabic in USER_PHONES table.
     * The error message displayed to channel user is in language Arabic
     * whereas it should be in ENGLISH language.
     * So extract sender language from request message and set it in
     * SenderLocale.
     */
    public void generateChannelResponseMessage(RequestVO p_requestVO) {
    	final String METHOD_NAME = "generateChannelResponseMessage";
        String message = null;
        if (PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(p_requestVO.getRequestGatewayType()) && p_requestVO.getSenderLocale() == null) {
            message = setSenderLocaleWeb(p_requestVO);
        }
        if (BTSLUtil.isNullString(message)) {
            if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                message = p_requestVO.getSenderReturnMessage();
            } else {
                message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            }

        }
        p_requestVO.setSenderReturnMessage(message);
        
        //for web gateway handing revamp 
        String requestMessageOrigStr = p_requestVO.getRequestMessageOrigStr();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode requestMessage = null;
        if (!BTSLUtil.isNullString(requestMessageOrigStr)) {
			try {
				requestMessage = mapper.readTree(requestMessageOrigStr);
				String sourceType = getNodeValue(requestMessage, "sourceType");
	            if(!BTSLUtil.isNullString(sourceType) && "JSON".equals(sourceType)) {
	            	RestAPIStringParser.generateJsonResponse(p_requestVO);
	            } 
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
		}     
    }

    public String setSenderLocaleWeb(RequestVO p_requestVO) {
        String message = null;
        String msg = p_requestVO.getRequestMessage();
        String[] reqMessage = msg.split((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));
        // String[] reqMessage=msg.split(" ");
        if (reqMessage != null && (PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(reqMessage[0]) || PretupsI.SERVICE_TYPE_CHNL_BILLPAY.equals(reqMessage[0])) && reqMessage.length == 7) {
            if (BTSLUtil.isNullString(reqMessage[4])) {
                p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
            } else {
                p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(reqMessage[4]));
            }
            message = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
        }
        return message;
    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        updateUserInfo(p_con, p_requestVO);
    }

    /**
     * @author gaurav.pandey
     * @param p_requestVO
     * @return void
     *         added for suspend resume channel use (Road map 5.8)
     */

    private void suspendResumeChannelUserParse(RequestVO p_requestVO) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("suspendResumeChannelUserParse", "Entered p_requestVO: " + p_requestVO.getDecryptedMessage());
        }
        String req_message;
        String message = null;
        try {
            req_message = p_requestVO.getDecryptedMessage();
            HashMap requestHashMap = p_requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            String[] temp = req_message.trim().split(" ");
            requestHashMap.put("TYPE", temp[0]);
            requestHashMap.put("MSISDN", temp[1]);
            requestHashMap.put("ACTION", temp[2]);
            requestHashMap.put("PIN", temp[3]);
            String msisdn = temp[1];

            p_requestVO.setDecryptedMessage(req_message);
            p_requestVO.setRequestMap(requestHashMap);
            p_requestVO.setReceiverMsisdn(msisdn);

        }

        catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelUserSuspendResume", "Exception e: " + e);
            // throw new
            // BTSLBaseException("XMLStringParser","parseChannelUserSuspendResume",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("suspendResumeChannelUserParse", "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }

    }

    /**
     * Method to parse the Operator request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
		//p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
		p_requestVO.setDecryptedMessage(changedSTKSeperatorOtherThanDefault(p_requestVO.getRequestMessage()));
        if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
            String filteredMsisdn = PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN());
            p_requestVO.setFilteredMSISDN(filteredMsisdn);
            p_requestVO.setMessageSentMsisdn(filteredMsisdn);
        }
    }
    
    /**
   	 * Method to parse the Operator request 
   	 * @param p_requestVO
   	 * @throws BTSLBaseException
   	 */
   	private String changedSTKSeperatorOtherThanDefault(String p_RequestMessage) {
   		String METHOD_NAME="changedSTKSeperatorOtherThanDefault";
   		if(LOG.isDebugEnabled()){
   			LOG.debug(METHOD_NAME,"Entered for p_RequestMessage= "+p_RequestMessage);
   		}
   		String CHNL_MESSAGE_SEP=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
   		if(BTSLUtil.isNullString(CHNL_MESSAGE_SEP))
   			CHNL_MESSAGE_SEP=" ";
   		if(LOG.isDebugEnabled()){
   			LOG.debug(METHOD_NAME,"CHNL_MESSAGE_SEP ="+CHNL_MESSAGE_SEP+", p_RequestMessage= "+p_RequestMessage);
   		}
   		String stkRequestSeperator=Constants.getProperty("CHNL_PLAIN_STK_SEPT_OTHER_THAN_DEFAULT");
   		if(LOG.isDebugEnabled()){
   			LOG.debug(METHOD_NAME,"stkRequestSeperator ="+stkRequestSeperator);
   		}
   		if(!p_RequestMessage.contains(CHNL_MESSAGE_SEP) && !BTSLUtil.isNullString(stkRequestSeperator)) {
   			String []stkRequestSeperatorArray = null;
   				stkRequestSeperatorArray=(String[])BTSLUtil.split(stkRequestSeperator,",");
   				String stkRequestSeperatorIndex="";
   				String modifiedRequestMessage="";
   				for(int index=0;index<stkRequestSeperatorArray.length;index++){
   					stkRequestSeperatorIndex = stkRequestSeperatorArray[index]; 
   					if(stkRequestSeperatorIndex.equals("\\")){
   						p_RequestMessage=p_RequestMessage.replaceAll("\\\\", "\\\\\\\\");
   						stkRequestSeperatorIndex="\\";						
   					}
   					if(LOG.isDebugEnabled()){
   						LOG.debug(METHOD_NAME,"stkRequestSeperatorIndex ="+stkRequestSeperatorIndex+" , p_RequestMessage = "+p_RequestMessage);
   					}
   					if(p_RequestMessage.contains(stkRequestSeperatorIndex)){
   						if(stkRequestSeperatorIndex.equals("\\")){
   							modifiedRequestMessage=p_RequestMessage.replaceAll("\\\\", CHNL_MESSAGE_SEP);
   						} else {
   							modifiedRequestMessage=p_RequestMessage.replaceAll(stkRequestSeperatorIndex, CHNL_MESSAGE_SEP);
   						}
   						break;
   					}
   				}
   				if(BTSLUtil.isNullString(modifiedRequestMessage)){
   					modifiedRequestMessage=p_RequestMessage;
   				}
   				if(LOG.isDebugEnabled()){
   					LOG.debug(METHOD_NAME,"Exited for p_RequestMessage= "+p_RequestMessage+" , modifiedRequestMessage = "+modifiedRequestMessage);
   				}
   				return modifiedRequestMessage;
   			
   		} else {
   			if(LOG.isDebugEnabled()){
   				LOG.debug(METHOD_NAME,"Exited for p_RequestMessage= "+p_RequestMessage);
   			}
   			return p_RequestMessage;
   		}
   		
   	}
   	
   	
	public ChannelUserVO loadValidateUserDetails(Connection con, RequestVO requestVO) throws BTSLBaseException {
		final String METHOD_NAME = "loadValidateUserDetails";
		String requestMessageOrigStr = requestVO.getRequestMessageOrigStr();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode requestMessage = null;
		String sourceType = null;

		if (!BTSLUtil.isNullString(requestMessageOrigStr)) {
			try {
				requestMessage = mapper.readTree(requestMessageOrigStr);
				sourceType = getNodeValue(requestMessage, "sourceType");
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
			}
		}

		if (!BTSLUtil.isNullString(sourceType) && "JSON".equalsIgnoreCase(sourceType)) {
			RestParser restParser = new RestParser();
			return restParser.loadValidateUserDetails(con, requestVO);
		} else {
			return super.loadValidateUserDetails(con, requestVO);
		}
	}

}
