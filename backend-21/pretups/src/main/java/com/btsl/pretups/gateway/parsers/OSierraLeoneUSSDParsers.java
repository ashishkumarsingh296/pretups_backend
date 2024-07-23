package com.btsl.pretups.gateway.parsers;

/* @(#)OSierraLeoneUSSDParsers.java
* Copyright(c) 2013,Mahindra Comviva technologies Ltd.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* Author                        Date            History
*-------------------------------------------------------------------------------------------------
* Vikas Kumar					02/09/13		Initial creation
* Diwakar						08/07/17	    Modification done for version upgrade to 733
*-------------------------------------------------------------------------------------------------
*
*Parser class to handle USSD plain requests
*/

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.USSDStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

	public class OSierraLeoneUSSDParsers extends ParserUtility
	{
	public static String CHNL_MESSAGE_SEP=null;
	public static String P2P_MESSAGE_SEP=null;
	private static String USSD_RESP_SEP=null;
	private static String P2P_USSD_RESP_SEP=null;
	public static Log _log = LogFactory.getLog(OSierraLeoneUSSDParsers.class.getName());
	static
	{
		final String METHOD_NAME="static block";
		try
		{
			CHNL_MESSAGE_SEP=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			if(BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
				CHNL_MESSAGE_SEP=" ";
			}
	        P2P_MESSAGE_SEP=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
			if(BTSLUtil.isNullString(P2P_MESSAGE_SEP)) {
				P2P_MESSAGE_SEP=" ";
			}
			USSD_RESP_SEP=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_RESP_SEPARATOR));
		    if(BTSLUtil.isNullString(USSD_RESP_SEP)) {
			  USSD_RESP_SEP="&";
		    }
		   P2P_USSD_RESP_SEP=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_RESP_SEPARATOR));
	       if(BTSLUtil.isNullString(P2P_USSD_RESP_SEP)) {
	    	   P2P_USSD_RESP_SEP="&";
	       }
		}
		catch(Exception e)
		{
			 _log.errorTrace(METHOD_NAME,e);
		}
	}
	
	/**
	 * Method to parse the channel request based on action (Keyword)
	 * 
	 * @param p_requestVO
	 * @throws BTSLBaseException
	 */
	public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException
	{
		String contentType=p_requestVO.getReqContentType();
		final String METHOD_NAME = "parseRequestMessage";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Transfer ID="+p_requestVO.getRequestID()+" contentType: "+contentType);
		try
		{
			if(contentType!=null&&(contentType.indexOf("plain")!=-1 || contentType.indexOf("PLAIN")!=-1))
			{
				//Forward to XML parsing
				//Set Filtered MSISDN, set _requestMSISDN
				//Set message Format , set in decrypted message 
				HttpServletRequest p_request=(HttpServletRequest) p_requestVO.getRequestMap().get("HTTPREQUEST");
				p_requestVO.setServiceKeyword(p_request.getParameter("TYPE"));
				p_requestVO.setReqContentType(contentType);
				int action=actionParser(p_requestVO);
				parsePlainRequest(action,p_requestVO);
			}
			else
			{
			    //Forward to plain message parsing
				//Set message Format , set in decrypted message
				p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
			}
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Message ="+p_requestVO.getDecryptedMessage()+" MSISDN="+p_requestVO.getRequestMSISDN());
		}
		catch(BTSLBaseException be)
		{
			_log.error(METHOD_NAME," BTSL Exception while parsing Request Message :"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"  Exception while parsing Request Message :"+e.getMessage());
			_log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDParsers[parseRequestMessage]",p_requestVO.getTransactionID(),"","","Exception :"+e.getMessage());
			throw new BTSLBaseException("USSDParsers",METHOD_NAME,PretupsErrorCodesI.ERROR_EXCEPTION);
		}		
	}
	
	/* (non-Javadoc)
	* @see com.btsl.pretups.gateway.util.ParserUtility#parseChannelRequestMessage(com.btsl.pretups.receiver.RequestVO, jakarta.servlet.http.HttpServletRequest)
	*/
	public void parseChannelRequestMessage(RequestVO p_requestVO) throws BTSLBaseException
	{
		HttpServletRequest p_request=(HttpServletRequest) p_requestVO.getRequestMap().get("HTTPREQUEST");
		HttpServletResponse	p_response=	(HttpServletResponse) p_requestVO.getRequestMap().get("HTTPRESPONSE");
		String requestType = p_request.getParameter("TYPE"); 
	    String ussdContentType=null;
	    final String METHOD_NAME="parseChannelRequestMessage";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered TYPE="+requestType);
		// add response header information according to request header information
	
	
		    ussdContentType=Constants.getProperty("USSD_CONTENT_TYPE");
	        if(!BTSLUtil.isNullString(ussdContentType))
			 p_requestVO.setReqContentType("text/plain");
	  
	
			if(!BTSLUtil.isNullString(p_request.getContentType()))
			p_response.setContentType(p_request.getContentType());
				
			
		p_response.setContentLength(p_request.getContentLength());
		p_response.setLocale(p_request.getLocale());
		
		String parsedRequestStr=null;
		String msisdn1=null;
		String pin=null;
		String msisdn2=null;
		String selector=null;
		String amount=null;
		String qty=null;
		String username=null;
		String cellId=null;
		String switchId=null;
		try
		{
			if(!BTSLUtil.isNullString(requestType))
				p_requestVO.setRequestMessage("TYPE="+requestType+"&");
			else
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
			}
			//1
			if("ADUSRREQ".equals(requestType))
			{
	//			TYPE=ADDCHUSRREQ&MSISDN=Retailer_MSISDN&PIN=Retailer_PIN&MSISDN2=User_MSISDN&USERNAME=User_Name
				msisdn1 = p_request.getParameter("MSISDN");
				msisdn2 = p_request.getParameter("MSISDN2");
				pin= p_request.getParameter("PIN");
				username = p_request.getParameter("USERNAME");
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(msisdn2)||BTSLUtil.isNullString(username))
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				
				if(!BTSLUtil.isNullString(username))
				{
					if ((username.length()>80)|| !BTSLUtil.isAlphaNumeric(username))
					{
						
						p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
						 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					}
					
				}
				parsedRequestStr=PretupsI.SERVICE_TYPE_USER_CREATION+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+username+CHNL_MESSAGE_SEP+pin;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setType(PretupsI.SERVICE_TYPE_USER_CREATION);
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
			}
			//2
			else if("TRFREQ".equals(requestType))
			{
				msisdn1 = p_request.getParameter("MSISDN");
				msisdn2 = p_request.getParameter("MSISDN2");
				amount = p_request.getParameter("TOPUPVALUE");
				String productCode = p_request.getParameter("PRODUCTCODE");
				pin= p_request.getParameter("PIN");
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(msisdn2)||BTSLUtil.isNullString(pin)||BTSLUtil.isNullString(amount))
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				if(BTSLUtil.isNullString(productCode))
					productCode="101";
				parsedRequestStr=PretupsI.SERVICE_TYPE_CHNL_TRANSFER+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+productCode+CHNL_MESSAGE_SEP+pin;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
			}
			//3
			else if("RCTRFREQ".equals(requestType))
			{
					msisdn1 = p_request.getParameter("MSISDN");
					pin= p_request.getParameter("PIN");
					msisdn2 = p_request.getParameter("MSISDN2");
					amount = p_request.getParameter("AMOUNT");
					selector =  p_request.getParameter("SELECTOR");
					cellId =  p_request.getParameter("CELLID");
					switchId =  p_request.getParameter("SWITCHID");
					if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(msisdn2)||BTSLUtil.isNullString(amount)||BTSLUtil.isNullString(selector))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
						 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					}
					parsedRequestStr=PretupsI.SERVICE_TYPE_CHNL_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+pin;
					p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
					p_requestVO.setReqSelector(selector);
					p_requestVO.setDecryptedMessage(parsedRequestStr);
				    p_requestVO.setRequestMSISDN(msisdn1);
				    p_requestVO.setCellId(cellId);
		            p_requestVO.setSwitchId(switchId);
				    
				}
			//4
			else if("MVDREQ".equals(requestType))
			{
				//TYPE=MVDREQ&MSISDN=Retailer_MSISDN&PIN=Retailer_PIN&MSISDN2=Payee_MSISDN&AMOUNT=Amount&QTY=Quantity&SELECTOR=0
				msisdn1 = p_request.getParameter("MSISDN");
				msisdn2 = p_request.getParameter("MSISDN2");
				pin= p_request.getParameter("PIN");
				amount = p_request.getParameter("AMOUNT");
				selector =  p_request.getParameter("SELECTOR");
				qty =  p_request.getParameter("QTY");
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(msisdn2)||BTSLUtil.isNullString(amount)||BTSLUtil.isNullString(selector)||BTSLUtil.isNullString(qty))
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				if(BTSLUtil.isNullString(pin))
					pin=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
				
	            parsedRequestStr=PretupsI.SERVICE_TYPE_MVD+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+qty+CHNL_MESSAGE_SEP+pin;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setType(PretupsI.SERVICE_TYPE_MVD);
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
				//p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
				p_requestVO.setReqSelector(selector);
			}
			//5
			else if("BALREQ".equals(requestType))
			{
				 msisdn1 = p_request.getParameter("MSISDN");
				msisdn2 = p_request.getParameter("MSISDN2");
				pin= p_request.getParameter("PIN");
				if(!BTSLUtil.isNullString(msisdn2))
			        parsedRequestStr=PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+pin;
				else
				    parsedRequestStr=PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY+CHNL_MESSAGE_SEP+pin;
				
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				//p_requestVO.setReqSelector(selector);
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
			}
			//6
			else if("DSRREQ".equals(requestType))
			{
				 msisdn1 = p_request.getParameter("MSISDN");
				pin= p_request.getParameter("PIN");
				
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(pin))
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				parsedRequestStr=PretupsI.SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT+CHNL_MESSAGE_SEP+pin;
				
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
			}
			//7
			else if("LTSREQ".equals(requestType))
			{
				 msisdn1 = p_request.getParameter("MSISDN");
				pin= p_request.getParameter("PIN");
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(pin))
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				parsedRequestStr=PretupsI.SERVICE_TYPE_LAST_TRANSFER_STATUS+CHNL_MESSAGE_SEP+pin;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
			}
			//8
			else if("RCPNREQ".equals(requestType))
			{
				 msisdn1 = p_request.getParameter("MSISDN");
				 pin= p_request.getParameter("PIN");
				String newPin = p_request.getParameter("NEWPIN");
				String confirmPin = p_request.getParameter("CONFIRMPIN");
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(pin)||BTSLUtil.isNullString(newPin)||BTSLUtil.isNullString(confirmPin))
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				parsedRequestStr=PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN+CHNL_MESSAGE_SEP+pin+CHNL_MESSAGE_SEP+newPin+CHNL_MESSAGE_SEP+confirmPin;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
			}
			//9
			else if("TBRCTRFREQ".equals(requestType))
			{
				//TYPE=MVDREQ&MSISDN=Retailer_MSISDN&PIN=Retailer_PIN&MSISDN2=Payee_MSISDN&AMOUNT=Amount&QTY=Quantity&SELECTOR=0
				msisdn1 = p_request.getParameter("MSISDN");
				msisdn2 = p_request.getParameter("MSISDN2");
				pin= p_request.getParameter("PIN");
				amount = p_request.getParameter("AMOUNT");
				selector =  p_request.getParameter("SELECTOR");
				//qty =  p_request.getParameter("QTY");
				if(BTSLUtil.isNullString(msisdn1)||BTSLUtil.isNullString(msisdn2)||BTSLUtil.isNullString(amount)||BTSLUtil.isNullString(selector))
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
					 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				if(BTSLUtil.isNullString(pin))
					pin=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
				
	            parsedRequestStr=PretupsI.SERVICE_TYPE_PRODUCT_RECHARGE+CHNL_MESSAGE_SEP+msisdn2+CHNL_MESSAGE_SEP+amount+CHNL_MESSAGE_SEP+selector+CHNL_MESSAGE_SEP+pin;
				p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))));
				p_requestVO.setType(PretupsI.SERVICE_TYPE_PRODUCT_RECHARGE);
				p_requestVO.setDecryptedMessage(parsedRequestStr);
			    p_requestVO.setRequestMSISDN(msisdn1);
				//p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
				p_requestVO.setReqSelector(selector);
			}
			//10
			else if("REGSID".equals(requestType))
			{
				String msisdn=p_request.getParameter("MSISDN");
				String sid=p_request.getParameter("SID");
				String newsid=p_request.getParameter("NEWSID");
				if((BTSLUtil.isNullString(sid))&&(BTSLUtil.isNullString(newsid))) {
					parsedRequestStr=PretupsI.SERVICE_TYPE_SIDREG;
				} else if((!BTSLUtil.isNullString(sid))&&(BTSLUtil.isNullString(newsid))) {
					parsedRequestStr=PretupsI.SERVICE_TYPE_SIDREG+CHNL_MESSAGE_SEP+sid;
				} else if((!BTSLUtil.isNullString(sid))&&(!BTSLUtil.isNullString(newsid))) {
					parsedRequestStr=PretupsI.SERVICE_TYPE_SIDREG+CHNL_MESSAGE_SEP+sid+CHNL_MESSAGE_SEP+newsid;
				} else {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseChannelRegistrationRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				if(BTSLUtil.isNullString(msisdn)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseChannelRegistrationRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
	
				String language1=p_request.getParameter("LANGUAGE1"); 
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
			    
			}
			//11
			else if("DELSID".equals(requestType)){
				
				String msisdn=p_request.getParameter("MSISDN");
				String sid=p_request.getParameter("SID");
				parsedRequestStr=PretupsI.SERVICE_TYPE_SID_DELETE+CHNL_MESSAGE_SEP+msisdn+CHNL_MESSAGE_SEP+sid;
				if(BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(sid)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseDeleteSIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
			}
			//12
			else if("ENQSID".equals(requestType)){
				String msisdn=p_request.getParameter("MSISDN");
				parsedRequestStr=PretupsI.SERVICE_TYPE_SID_ENQUIRY+CHNL_MESSAGE_SEP+msisdn;
				if(BTSLUtil.isNullString(msisdn)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
			}
			//13
			else if("LMSPTRED".equals(requestType)){
				String msisdn=p_request.getParameter("MSISDN");
				String points=p_request.getParameter("POINTS");
				pin=p_request.getParameter("PIN");
				String productCode=p_request.getParameter("PRODUCTCODE");
				String language1=p_request.getParameter("LANGUAGE1");			
				if(!BTSLUtil.isNullString(productCode))
					parsedRequestStr=PretupsI.SERVICE_TYPE_LMS_POINTS_REDEMPTION+CHNL_MESSAGE_SEP+points+CHNL_MESSAGE_SEP+pin+CHNL_MESSAGE_SEP+productCode;
				else 
					parsedRequestStr=PretupsI.SERVICE_TYPE_LMS_POINTS_REDEMPTION+CHNL_MESSAGE_SEP+points+CHNL_MESSAGE_SEP+pin;
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(points)||BTSLUtil.isNullString(pin)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
			}
			//14
			else if("LMSPTENQ".equals(requestType)){
				String msisdn=p_request.getParameter("MSISDN");
				pin=p_request.getParameter("PIN");
				String productCode=p_request.getParameter("PRODUCTCODE");
				String language1=p_request.getParameter("LANGUAGE1");			
				if(!BTSLUtil.isNullString(productCode))
					parsedRequestStr=PretupsI.SERVICE_TYPE_LMS_POINTS_ENQUIRY+CHNL_MESSAGE_SEP+pin+CHNL_MESSAGE_SEP+productCode;
				else 
					parsedRequestStr=PretupsI.SERVICE_TYPE_LMS_POINTS_ENQUIRY+CHNL_MESSAGE_SEP+pin;
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(pin)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
			}
			//15
			else if ("INPRESET".equals(requestType)) {
				String msisdn=p_request.getParameter("MSISDN");
				pin=p_request.getParameter("PIN");
				String language1=p_request.getParameter("LANGUAGE1");			
				parsedRequestStr=PretupsI.SERVICE_TYPE_INIT_SELF_TPIN_RESET+CHNL_MESSAGE_SEP+pin;
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(pin)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
            }
			//16
			else if ("PRESET".equals(requestType)) {
            	String msisdn=p_request.getParameter("MSISDN");
				//pin=p_request.getParameter("PIN");
				String otp=p_request.getParameter("OTP");
				String answer=p_request.getParameter("ANSWER");
				String newPIN=p_request.getParameter("NEWPIN");
				String confirmNewPin=p_request.getParameter("CONFIRMNEWPIN");
				String language1=p_request.getParameter("LANGUAGE1");			
				parsedRequestStr=PretupsI.SERVICE_TYPE_SELF_TPIN_RESET+CHNL_MESSAGE_SEP+msisdn+CHNL_MESSAGE_SEP+otp+CHNL_MESSAGE_SEP+answer+CHNL_MESSAGE_SEP+newPIN+CHNL_MESSAGE_SEP+confirmNewPin;
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(newPIN)||BTSLUtil.isNullString(confirmNewPin)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				} else if (BTSLUtil.isNullString(otp) && BTSLUtil.isNullString(answer)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
            }
			//17
			else if ("DUPDATE".equals(requestType)) {
            	String msisdn=p_request.getParameter("MSISDN");
				pin=p_request.getParameter("PIN");
				String shortName=p_request.getParameter("SHORTNAME");
				String contactPerson=p_request.getParameter("CONTACTPERSON");
				String subscriberCode=p_request.getParameter("SUBSCRIBERCODE");
				String appointmentDate=p_request.getParameter("APPOINTMENTDATE");
				String ssn=p_request.getParameter("SSN");			
				String language1=p_request.getParameter("LANGUAGE1");	
				parsedRequestStr=PretupsI.SERVICE_TYPE_SELF_TPIN_DATA_UPDATE+CHNL_MESSAGE_SEP+msisdn+CHNL_MESSAGE_SEP+pin+CHNL_MESSAGE_SEP+shortName+CHNL_MESSAGE_SEP+contactPerson+CHNL_MESSAGE_SEP+subscriberCode+CHNL_MESSAGE_SEP+appointmentDate+CHNL_MESSAGE_SEP+ssn;
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(pin)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				} else if (BTSLUtil.isNullString(shortName) && BTSLUtil.isNullString(contactPerson) && BTSLUtil.isNullString(subscriberCode) && BTSLUtil.isNullString(appointmentDate) && BTSLUtil.isNullString(ssn) ) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
				if(p_requestVO.getRequestMap()== null) {
					HashMap requestHashMap = new HashMap();
					requestHashMap.put("MSISDN",msisdn);
					requestHashMap.put("PIN",pin);
					requestHashMap.put("SHORTNAME",shortName);
					requestHashMap.put("CONTACTPERSON",contactPerson);
					requestHashMap.put("SUBSCRIBERCODE",subscriberCode);
					requestHashMap.put("APPOINTMENTDATE",appointmentDate);
					requestHashMap.put("SSN",ssn);
					p_requestVO.setRequestMap(requestHashMap);
				}
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
            }
			//18
			else if ("SOSTRF".equals(requestType)) {
				String productCode=p_request.getParameter("PRODUCTCODE");
				String msisdn=p_request.getParameter("MSISDN");
				pin=p_request.getParameter("PIN");
				String language1=p_request.getParameter("LANGUAGE1");
				if (BTSLUtil.isNullString(productCode)) {
					//setting default product
					productCode="101";
				}
				parsedRequestStr=PretupsI.SERVICE_TYPE_SOS_TRANSFER+CHNL_MESSAGE_SEP+productCode+CHNL_MESSAGE_SEP+msisdn+CHNL_MESSAGE_SEP+pin;
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(pin)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				} 
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
				if(p_requestVO.getRequestMap()== null) {
					HashMap requestHashMap = new HashMap();
					requestHashMap.put("MSISDN",msisdn);
					requestHashMap.put("PIN",pin);
					requestHashMap.put("PRODUCTCODE",productCode);
					p_requestVO.setRequestMap(requestHashMap);
				}
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
            }
			//19
			else if ("SOSSTL".equals(requestType)) {
            	String msisdn=p_request.getParameter("MSISDN");
				pin=p_request.getParameter("PIN");
				String language1=p_request.getParameter("LANGUAGE1");	
				parsedRequestStr=PretupsI.SERVICE_TYPE_SOS_MANUAL_SETELMENT+CHNL_MESSAGE_SEP+msisdn+CHNL_MESSAGE_SEP+pin;
				if(BTSLUtil.isNullString(msisdn)||BTSLUtil.isNullString(pin)) {
					throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseEnquirySIDRequest",PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				}
				p_requestVO.setDecryptedMessage(parsedRequestStr);
				p_requestVO.setRequestMSISDN(msisdn);
				if(p_requestVO.getRequestMap()== null) {
					HashMap requestHashMap = new HashMap();
					requestHashMap.put("MSISDN",msisdn);
					requestHashMap.put("PIN",pin);
					p_requestVO.setRequestMap(requestHashMap);
				}
				if(BTSLUtil.isNullString(language1)) {
					language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
				}
				
				if(BTSLUtil.isNullString(language1)) {
					language1="1";
				}
				
				if(!BTSLUtil.isNullString(language1)){
					   if(!language1.equalsIgnoreCase("0") || !language1.equalsIgnoreCase("1")){
						   if(BTSLUtil.isNullString(language1)) {
							language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
						}
							
						if(BTSLUtil.isNullString(language1)) {
							language1="1";
						}
				   }
				   p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
				}
            }
			else
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}
			
			try
			{
				Connection con=null;
				con=OracleUtil.getConnection();
			    ChannelUserBL.updateUserInfo(con, 	p_requestVO);
			    con.close();
			}
			catch(BTSLBaseException e ){
				throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,e.getMessageKey());
			} catch (SQLException e) {
				_log.errorTrace(METHOD_NAME,e);
			}
			
		}
		catch(BTSLBaseException e)
		{
		     _log.errorTrace(METHOD_NAME,e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		    _log.error(METHOD_NAME,"Exception e: "+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OSierraLeoneUSSDParsers["+METHOD_NAME+"]",p_requestVO.getTransactionID(),"","","Exception :"+e.getMessage());
		    throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,e.getMessageKey());
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Exiting p_requestVO: "+p_requestVO.toString());
		}
			
	}

	/**
	 * Method to generate the channel response based on action
	 * 
	 * @param p_requestVO
	 */
	public void generateChannelResponseMessage(RequestVO p_requestVO) 
	{
		String contentType=p_requestVO.getReqContentType();
		final String METHOD_NAME="generateChannelResponseMessage";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Transfer ID="+p_requestVO.getRequestID()+" contentType: "+contentType+" (p_requestVO.getActionValue(): "+p_requestVO.getActionValue());
		try
		{
			if(contentType!=null&&(p_requestVO.getReqContentType().indexOf("text")!=-1 || p_requestVO.getReqContentType().indexOf("TEXT")!=-1||p_requestVO.getReqContentType().indexOf("plain")!=-1||p_requestVO.getReqContentType().indexOf("PLAIN")!=-1))
			{
				//Set the Sender Return Message
				if(p_requestVO.getActionValue()==-1)
					actionChannelParser(p_requestVO);
				
				generateChannelResponse(p_requestVO.getActionValue(),p_requestVO);
			}
			else
			{ 
				String message=null;
	
				LocaleMasterVO localeMasterVO=LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
				if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()))
				{
	                message=p_requestVO.getSenderReturnMessage();
	                if("fr".equals(localeMasterVO.getLanguage()))
	                {
	                        if(message.indexOf("mclass^")==0)
	                        {
	                                int colonIndex=message.indexOf(":");
	
	                                message=message.substring(colonIndex+1);
	                                if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"message"+message);
	                        }
	                }
	                else
	                        message=p_requestVO.getSenderReturnMessage();
	                                //p_requestVO.setSenderReturnMessage(message);
				}
				else
					message=BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments());
	
				p_requestVO.setSenderReturnMessage(message);
			}
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"  Exception while generating Response Message :"+e.getMessage());
			 _log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OSierraLeoneUSSDParsers[generateChannelResponseMessage]",p_requestVO.getTransactionID(),"","","Exception getting message :"+e.getMessage());
			try
			{
			    USSDStringParser.generateFailureResponse(p_requestVO);
			}
			catch(Exception ex)
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OSierraLeoneUSSDParsers[generateChannelResponseMessage]",p_requestVO.getTransactionID(),"","","Exception getting default message :"+ex.getMessage());
				p_requestVO.setSenderReturnMessage("TYPE=&TXNSTATUS="+PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			}
		}
	}


	/**
	* Method to parse the channel resquest based on action (Keyword)
	* @param action
	* @param p_requestVO
	* @throws Exception
	*/
	public void parseChannelRequest(int action,RequestVO p_requestVO) throws Exception
	{
		
	
	}
	
	public static void generateChannelResponse(int action,RequestVO p_requestVO) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateChannelResponse","Entered Request ID="+p_requestVO.getRequestID()+" action="+action);
		
		String messageCode = p_requestVO.getMessageCode();
		if((!BTSLUtil.isNullString(messageCode))&&(!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1)
		{
			messageCode = messageCode.substring(0,messageCode.indexOf("_"));
			p_requestVO.setMessageCode(messageCode);
		}
		switch(action)
		{
			case ACTION_CHNL_CREDIT_TRANSFER: 
			{
			    USSDStringParser.generateChannelCreditTransferResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_CHANGE_PIN: 
			{
			    USSDStringParser.generateChannelChangePinResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_NOTIFICATION_LANGUAGE: 
			{
			    USSDStringParser.generateChannelNotificationLanguageResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_TRANSFER_MESSAGE: 
			{
			    USSDStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_TRANSFER_MESSAGE);
				break;	
			}
			case ACTION_CHNL_WITHDRAW_MESSAGE: 
			{
			    USSDStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_WITHDRAW_MESSAGE);
				break;	
			}
			case ACTION_CHNL_RETURN_MESSAGE: 
			{
			    USSDStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_RETURN_MESSAGE);
				break;	
			}
			case ACTION_CHNL_BALANCE_ENQUIRY:
			{
			    USSDStringParser.generateChannelBalanceEnquiryResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_DAILY_STATUS_REPORT:
			{
			    USSDStringParser.generateChannelDailyStatusReportResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_LAST_TRANSFER_STATUS:
			{
			    USSDStringParser.generateChannelLastTransferStatusResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_C2S_LAST_XTRANSFER: 	//last X C2S transfer
			{
			    USSDStringParser.generateChannelLastXTransferStatusResponse(p_requestVO);
			    //manisha
			    break;
			}
			case ACTION_CHNL_CUST_LAST_XTRANSFER: 	//last X C2S transfer
			{
			    USSDStringParser.generateChannelXEnquiryStatusResponse(p_requestVO);
			    //manisha
			    break;
			}
			case  ACTION_MULTIPLE_VOUCHER_DISTRIBUTION:
			{
				USSDStringParser.generateMultipleVoucherDistributionResponse(p_requestVO);
				break;
			}
			case  ACTION_CHNL_ADD_CHNL_USER:
			{
				USSDStringParser.generateAddChannelUserResponse(p_requestVO);
				break;
			}
			case ACTION_PRODUCT_RECHARGE_REQ:
			{
				USSDStringParser.generateProductRechargeResponse(p_requestVO);
				break;
			}
			case ACTION_REGISTER_SID:
			{
				USSDStringParser.generateChannelRegistrationResponse(p_requestVO);
				break;	
			}
			case ACTION_DELETE_SID_REQ:
			{
				USSDStringParser.generateDeleteSIDResponse(p_requestVO);
				break;	
			}
			case ACTION_ENQUIRY_SID_REQ:
			{
				USSDStringParser.generateEnquirySIDResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_LMS_POINTS_REDEMPTION:
			{
				USSDStringParser.generateLmsPointRedemptionResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_LMS_POINTS_ENQUIRY:
			{
				USSDStringParser.generateLmsPointEnquiryResponse(p_requestVO);
				break;	
			}
			case ACTION_INITIATE_PIN_RESET:
			{
				USSDStringParser.generateInitiateSelfTPinResetResponse(p_requestVO);
				break;	
			}
			case ACTION_PIN_RESET:
			{
				USSDStringParser.generateSelfTPinResetResponse(p_requestVO);
				break;	
			}
			case ACTION_DATA_UPDATE:
			{
				USSDStringParser.generateSelfTPinDataUpdateResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_SOS_REQUEST:
			{
				USSDStringParser.generateSOSResponse(p_requestVO);
				break;	
			}
			case ACTION_CHNL_SOS_SETTLEMENT_REQUEST:
			{
				USSDStringParser.generateSOSSettlementResponse(p_requestVO);
				break;	
			}
		}
	}
		
	/**
	* Method to mark and unmark the request for subscriber
	* @param p_con
	* @param p_requestVO
	* @param p_module
	* @param p_mark
	* @throws BTSLBaseException
	*/
	public void checkRequestUnderProcess(Connection p_con,RequestVO p_requestVO,String p_module,boolean p_mark, ChannelUserVO channeluserVO) throws BTSLBaseException 
	{
		final String METHOD_NAME="checkRequestUnderProcess";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered Request ID="+p_requestVO.getRequestID()+" p_module="+p_module+" p_mark="+p_mark+" Check Required="+p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
		try
		{
			if(TypesI.YES.equals(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd()))
			{
				if(PretupsI.C2S_MODULE.equals(p_module))
				{
			            ChannelUserBL.checkRequestUnderProcessPOS(p_con,p_requestVO.getRequestIDStr(),channeluserVO.getUserPhoneVO(),p_mark);
				}
					
			}
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			 _log.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.ERROR_EXCEPTION);				
		}
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Exiting For Request ID="+p_requestVO.getRequestID());
	
	}
	
		public void parseChannelRequestMessage(RequestVO p_requestVO, Connection p_con) throws BTSLBaseException
		{
			String contentType=p_requestVO.getReqContentType();
			final String METHOD_NAME="parseChannelRequestMessage";
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Transfer ID="+p_requestVO.getRequestID()+" contentType: "+contentType);
			try
			{
				if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1))
				{
					int action=actionChannelParser(p_requestVO);
				    parseChannelRequest(action,p_requestVO);
				    ChannelUserBL.updateUserInfo(p_con, p_requestVO);
				}
				else if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1))
				{
				    //Forward to plain message parsing
					//Set message Format , set in decrypted message
					parseChannelRequestMessage(p_requestVO);
					ChannelUserBL.updateUserInfo(p_con, p_requestVO);
					//p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
				} else {
					// Forward to plain message parsing
					// Set message Format , set in decrypted message
					p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
						
				}
				if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Message ="+p_requestVO.getDecryptedMessage()+" MSISDN="+p_requestVO.getRequestMSISDN());
			}
			catch(BTSLBaseException be)
			{
				_log.error(METHOD_NAME," BTSL Exception while parsing Request Message :"+be.getMessage());
				throw be;
			}
			catch(Exception e)
			{
				_log.error(METHOD_NAME,"  Exception while parsing Request Message :"+e.getMessage());
				 _log.errorTrace(METHOD_NAME,e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OSierraLeoneUSSDParsers["+METHOD_NAME+"]",p_requestVO.getTransactionID(),"","","Exception :"+e.getMessage());
				throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.ERROR_EXCEPTION);
			}		
		}

	/**
	 * @param p_requestVO
	 * @param p_request
	 * @param p_response
	 * @throws BTSLBaseException
	 * for Cp2p Request parsing
	 */
	public void generateResponseMessage(RequestVO p_requestVO) 
	{
		String contentType=p_requestVO.getReqContentType();
		final String METHOD_NAME="generateResponseMessage";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Transfer ID="+p_requestVO.getRequestID()+" contentType: "+contentType);
		try
		{
			if(contentType!=null&&(p_requestVO.getReqContentType().indexOf("text")!=-1 || p_requestVO.getReqContentType().indexOf("TEXT")!=-1||p_requestVO.getReqContentType().indexOf("plain")!=-1||p_requestVO.getReqContentType().indexOf("PLAIN")!=-1))
			{
				//Set the Sender Return Message
				if(p_requestVO.getActionValue()==-1)
					actionParser(p_requestVO);
				
				generateResponse(p_requestVO.getActionValue(),p_requestVO);
			}
			else
			{ 
				String message=null;
	
				LocaleMasterVO localeMasterVO=LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
				if(!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()))
				{
	                message=p_requestVO.getSenderReturnMessage();
	                if("fr".equals(localeMasterVO.getLanguage()))
	                {
	                        if(message.indexOf("mclass^")==0)
	                        {
	                                int colonIndex=message.indexOf(":");
	
	                                message=message.substring(colonIndex+1);
	                                if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"message"+message);
	                        }
	                }
	                else
	                        message=p_requestVO.getSenderReturnMessage();
	                                //p_requestVO.setSenderReturnMessage(message);
				}
				else
					message=BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments());
	
				p_requestVO.setSenderReturnMessage(message);
			}
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"  Exception while generating Response Message :"+e.getMessage());
			 _log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OSierraLeoneUSSDParsers[generateResponseMessage]",p_requestVO.getTransactionID(),"","","Exception getting message :"+e.getMessage());
			try
			{
			    USSDStringParser.generateFailureResponse(p_requestVO);
			}
			catch(Exception ex)
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OSierraLeoneUSSDParsers[generateResponseMessage]",p_requestVO.getTransactionID(),"","","Exception getting default message :"+ex.getMessage());
				p_requestVO.setSenderReturnMessage("TYPE=&TXNSTATUS="+PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
			}
		}
	}


	/**
	 * @author diwakar
	 * @Description generate response for P2P Services only
	 * @param action
	 * @param p_requestVO
	 * @throws Exception
	 */
	public static void generateResponse(int action,RequestVO p_requestVO) throws Exception
	{
		final String METHOD_NAME="generateResponse";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered Request ID="+p_requestVO.getRequestID()+" action="+action);
		
		String messageCode = p_requestVO.getMessageCode();
		if((!BTSLUtil.isNullString(messageCode))&&(!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1)
		{
			messageCode = messageCode.substring(0,messageCode.indexOf("_"));
			p_requestVO.setMessageCode(messageCode);
		}
		switch(action)
		{
			case ACTION_ACCOUNT_INFO: 
			{
			    USSDStringParser.generateGetAccountInfoResponse(p_requestVO);
				break;	
			}
			case CREDIT_TRANSFER: 
			{
			    USSDStringParser.generateCreditTransferResponse(p_requestVO);
				break;	
			}
			case CHANGE_PIN: 
			{
			    USSDStringParser.generateChangePinResponse(p_requestVO);
				break;	
			}
			case NOTIFICATION_LANGUAGE: 
			{
			    USSDStringParser.generateNotificationLanguageResponse(p_requestVO);
				break;	
			}
			case HISTORY_MESSAGE: 
			{
			    USSDStringParser.generateHistoryMessageResponse(p_requestVO);
				break;	
			}
			case CREDIT_RECHARGE: 
			{
			    USSDStringParser.generateCreditRechargeResponse(p_requestVO);
				break;	
			}
			case SUBSCRIBER_REGISTRATION:
			{
			    USSDStringParser.generateSubscriberRegistrationResponse(p_requestVO);
				break;	
			}
			case SUBSCRIBER_DEREGISTRATION:
			{
			    USSDStringParser.generateSubscriberDeRegistrationResponse(p_requestVO);
				break;	
			}
			case P2P_SERVICE_SUSPEND:
			{
			    USSDStringParser.generateP2PServiceSuspendResponse(p_requestVO);
				break;	
			}
			case P2P_SERVICE_RESUME: 	//last X C2S transfer
			{
			    USSDStringParser.generateP2PServiceResumeResponse(p_requestVO);
			    //manisha
			    break;
			}
			case LAST_TRANSFER_STATUS: 	//last X C2S transfer
			{
			    USSDStringParser.generateLastTransferStatus(p_requestVO);
			    //manisha
			    break;
			}
			case  ADD_BUDDY:
			{
				USSDStringParser.generateAddBuddyResponse(p_requestVO);
				break;
			}
			case  DELETE_BUDDY:
			{
				USSDStringParser.generateDeleteBuddyResponse(p_requestVO);
				break;
			}
			case LIST_BUDDY:
			{
				USSDStringParser.generateListBuddyResponse(p_requestVO);
				break;	
			}
			case SELF_BAR:
			{
				USSDStringParser.generateSelfBarResponse(p_requestVO);
				break;	
			}
			case ACTION_GMB:
			{
				USSDStringParser.generateGMBResponse(p_requestVO);
				break;	
			}
			
		}
	}


	public void parseRequestMessage(RequestVO p_requestVO,
			HttpServletRequest p_request) throws BTSLBaseException {
	
		String requestType = p_request.getParameter("TYPE"); 
		final String METHOD_NAME="parseRequestMessage";	
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered TYPE="+requestType);
		String parsedRequestStr=null;
		String msisdn1=null;
		String pin=null;
		String newPin=null;
		String confirmPin=null;
		String msisdn2=null;
		String selector=null;
		String amount=null;
		String language1=null;
		String language2=null;
		try
		{
			if(!BTSLUtil.isNullString(requestType))
				p_requestVO.setRequestMessage("TYPE="+requestType+"&");
			else
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				 throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
			}
	
			 if("CCTRFREQ".equals(requestType))
				{
					
				msisdn1 = p_request.getParameter("MSISDN");
					msisdn2 = p_request.getParameter("MSISDN2");
					pin= p_request.getParameter("PIN");//optional
					amount = p_request.getParameter("AMOUNT");
					selector = p_request.getParameter("SELECTOR");
					language1=p_request.getParameter("LANGUAGE1");//optional
					language2=p_request.getParameter("LANGUAGE2");//optional
	
					if(BTSLUtil.isNullString(msisdn1))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);
						_log.error("parseCreditTransferRequest","Sender Msisdn is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);	
					}
					if(BTSLUtil.isNullString(msisdn2))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);
						_log.error("parseCreditTransferRequest","Receiver Msisdn is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);	
					}
					//amount field should be mandatory
					if(BTSLUtil.isNullString(amount))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
						_log.error("parseCreditTransferRequest","Amount field is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseCreditTransferRequest",PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);	
					}
					if(BTSLUtil.isNullString(pin))
						pin=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
	
					if(BTSLUtil.isNullString(selector))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
						_log.error("parseCreditTransferRequest","Amount field is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers","parseCreditTransferRequest",PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);	
					}
	
					if(BTSLUtil.isNullString(language1))
						language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
					if(BTSLUtil.isNullString(language1))
						language1="0";
	
					if(BTSLUtil.isNullString(language2))
						language2=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
	
					if(BTSLUtil.isNullString(language2))
						language2="0";
	
					parsedRequestStr=PretupsI.SERVICE_TYPE_P2PRECHARGE+P2P_MESSAGE_SEP+msisdn2+P2P_MESSAGE_SEP+amount+P2P_MESSAGE_SEP+selector+P2P_MESSAGE_SEP+language2+P2P_MESSAGE_SEP+pin;
					p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
					p_requestVO.setReqSelector(selector);
					p_requestVO.setDecryptedMessage(parsedRequestStr);
					p_requestVO.setRequestMSISDN(msisdn1);
		}
			 
				else if("CCPNREQ".equals(requestType)){
					msisdn1 = p_request.getParameter("MSISDN");
					pin= p_request.getParameter("PIN"); //optional
					newPin=p_request.getParameter("NEWPIN");
					confirmPin=p_request.getParameter("CONFIRMPIN");
					language1=p_request.getParameter("LANGUAGE1");// optional
					if(BTSLUtil.isNullString(pin))
						pin=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
					if(BTSLUtil.isNullString(msisdn1))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);
						_log.error(METHOD_NAME,"Amount field is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);	
					}
	
					if(BTSLUtil.isNullString(newPin))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PIN);
						_log.error(METHOD_NAME,"New Pin field is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);	
					}
					if(BTSLUtil.isNullString(confirmPin))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PIN);
						_log.error(METHOD_NAME,"Confirm Pin field is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);	
					}
	
					if(BTSLUtil.isNullString(language1))
						language1=Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
					if(BTSLUtil.isNullString(language1))
						language1="0";
	
					parsedRequestStr=PretupsI.SERVICE_TYPE_P2PCHANGEPIN+P2P_MESSAGE_SEP+pin+P2P_MESSAGE_SEP+newPin+P2P_MESSAGE_SEP+confirmPin;
					p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
					p_requestVO.setDecryptedMessage(parsedRequestStr);
					p_requestVO.setRequestMSISDN(msisdn1);
					}
				else if("CGMBALREQ".equals(requestType))
				{
					msisdn1 = p_request.getParameter("MSISDN");//mandatory
					msisdn2 = p_request.getParameter("MSISDN2");//mandatory
					amount = p_request.getParameter("AMOUNT");//mandatory
					language1 =p_request.getParameter("LANGUAGE1");//optional 
					language2 =p_request.getParameter("LANGUAGE2");//optional
					pin=p_request.getParameter("PIN");//optional
	                         
					if(BTSLUtil.isNullString(pin))
				    	 pin=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
					
					if(BTSLUtil.isNullString(msisdn1))
				    {
					    	 	p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);
							    _log.error(METHOD_NAME,"MSISDN1 field is null");
							    throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);	
				    }
					  
					if(BTSLUtil.isNullString(msisdn2))
				    {
						p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);
						_log.error(METHOD_NAME,"MSIDSN2  field is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);	
				    }
					//amount field should be mandatory
					if(BTSLUtil.isNullString(amount))
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);
						_log.error("parseCreditTransferRequest","Amount field is null");
						throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_BLANK_AMOUNT);	
					} 
					  
					HashMap  requestMap=new HashMap();
					requestMap.put("MSISDN1", msisdn1);
					requestMap.put("MSISDN2", msisdn2);
					
					requestMap.put("AMOUNT", amount);
					requestMap.put("LANGUAGE1", language1);
					requestMap.put("LANGUAGE2", language2);
					requestMap.put("PIN", pin);
					
					parsedRequestStr=PretupsI.SERVICE_TYPE_GMB+P2P_MESSAGE_SEP+msisdn2+P2P_MESSAGE_SEP+amount+(P2P_MESSAGE_SEP+language2)+(P2P_MESSAGE_SEP+language2)+P2P_MESSAGE_SEP+pin;
					p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
					p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));
					
					p_requestVO.setDecryptedMessage(parsedRequestStr);
				    p_requestVO.setRequestMSISDN(msisdn1);
				    p_requestVO.setRequestMap(requestMap);
				    
					
				}
				
		
		}
		catch(Exception e)
		{
		     _log.errorTrace(METHOD_NAME,e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
		    _log.error(METHOD_NAME,"Exception e: "+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ABUSSDPlainStringParser["+METHOD_NAME+"]",p_requestVO.getTransactionID(),"","","Exception :"+e.getMessage());
		    throw new BTSLBaseException("OSierraLeoneUSSDParsers",METHOD_NAME,PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Exiting p_requestVO: "+p_requestVO.toString());
		}
			
	
	}


	//@Override
	public void parseOperatorRequestMessage(RequestVO p_requestvo)
			throws BTSLBaseException {
		// TODO Auto-generated method stub
		
	}

	public void parsePlainRequest(int action, RequestVO p_requestVO) throws Exception
	{
		final String METHOD_NAME="parseRequest";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered Request ID="+p_requestVO.getRequestID()+" action="+action);
	
		switch(action)
		{
		case CREDIT_TRANSFER: 
		{
			USSDStringParser.parseCreditTransferRequest(p_requestVO);
			break;	
		}
		case P2P_GIVE_ME_BALANCE:
		{
			USSDStringParser.parseP2PGiveMeBalanceRequest(p_requestVO);
			break;	
		}
		
		case ACTION_REGISTER_SID:
		{
			USSDStringParser.parseChannelRegistrationRequest(p_requestVO);
			break;	
		}
		case ACTION_DELETE_SID_REQ:
		{
			USSDStringParser.parseDeleteSIDRequest(p_requestVO);
			break;	
		}
		case ACTION_ENQUIRY_SID_REQ:
		{
			USSDStringParser.parseEnquirySIDRequest(p_requestVO);
			break;	
		}
		
	
		}
	}

}