package com.btsl.pretups.gateway.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class ExtAPIStringParser {
    private static String type = null;
    public static final Log _log = LogFactory.getLog(ExtAPIStringParser.class.getName());
    private static final String CLASS_NAME = "ExtAPIStringParser";
    private static final String ENTRY_KEY = "Entered: p_requestVO=";
    private static final String EXIT_KEY = "Exiting: p_requestVO:";
    private static final String EXIT_KEY_RES = "Exiting: responseStr:";

    /**
   	 * ensures no instantiation
   	 */
    private ExtAPIStringParser(){
    	
    }
    
    public static void parseExtStringRequest(RequestVO p_requestVO) throws Exception {
        final String METHOD_NAME = "parseExtStringRequest";
        StringBuilder loggerValue= new StringBuilder();
		printEnterExitLogger(METHOD_NAME, loggerValue, ENTRY_KEY,  p_requestVO);
        try {

            String xmlString = p_requestVO.getRequestMessage();
        	String contentType=p_requestVO.getReqContentType();
			//Removing the extra space if this is coming into the request through USSD Phone.
        	if(!BTSLUtil.isNullString(xmlString)){
        		xmlString = xmlString.trim();
        	}
            String[] mandatoryRequestParamArr = null; // "TYPE,DATE,EXTNWCODE,CATCODE,EMPCODE,LOGINID,PASSWORD,EXTREFNUM,MSISDN,TRFTYPE,FROMDATE,TODATE,TRANSACTIONID".split(",");
            String[] msgFormatArr = null; // "TYPE DATE EXTNWCODE CATCODE EMPCODE".split(" ");
             HashMap requestMap = new HashMap();
            int index1, index2, index3, index4;
            String str1, str2, key, value;
            if(contentType!=null&&(contentType.indexOf("plain")!=-1 || contentType.indexOf("PLAIN")!=-1)){
       		 requestMap=BTSLUtil.getString2Hash(xmlString,"&","=");
       	}else{
            for (int ind = xmlString.indexOf("<COMMAND>") + 9; ind < xmlString.length() - 10; ind = index4 + 1) {
                index1 = xmlString.indexOf("<", ind);
                index2 = xmlString.indexOf(">", index1);
                key = xmlString.substring(index1 + 1, index2);
                if (!key.startsWith("/")){
                	index3 = xmlString.indexOf("</", index2);
                	index4 = xmlString.indexOf(">", index3);
                	str1 = xmlString.substring(index1, index2 + 1);
                	str2 = xmlString.substring(index3, index4 + 1);
                	value = xmlString.substring(index1 + str1.length(), xmlString.indexOf(str2, ind));
                	requestMap.put(key, value);
                	if (_log.isDebugEnabled()) {
                		_log.debug(METHOD_NAME, "Exiting key:value= " + key + ":" + value);
                	}
                }else{
                	index4=xmlString.length() - 10;
                }
            }
       	}

            HashMap seviceKeywordMap = new HashMap();
            type = (String) requestMap.get("TYPE");
           String date=(String) requestMap.get("DATE");
            seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
            final ServiceKeywordCacheVO serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type + "_" + p_requestVO.getModule() + "_" + p_requestVO
                .getRequestGatewayType() + "_" + p_requestVO.getServicePort());
            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ExtAPIStringParser[parseExtStringRequest]",
                    p_requestVO.getRequestIDStr(), "retailer msisnd", "", "Service keyword not found for the keyword=" + type + " For Gateway Type=" + p_requestVO
                        .getRequestGatewayType() + "Service Port=" + p_requestVO.getServicePort());
                throw new BTSLBaseException("ExtAPIStringParser", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ExtAPIStringParser[parseExtStringRequest]",
                    p_requestVO.getRequestIDStr(), "retailer msisnd", "", "Service keyword suspended for the keyword=" + type + " For Gateway Type=" + p_requestVO
                        .getRequestGatewayType() + "Service Port=" + p_requestVO.getServicePort());
                throw new BTSLBaseException("ExtAPIStringParser", METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
          
           
            final String messageFormat = serviceKeywordCacheVO.getMessageFormat();
            final String requestParam = serviceKeywordCacheVO.getRequestParam();
            HashMap ResponseMap = new HashMap(); 
            ResponseMap.put("RESPONSEPARAM", serviceKeywordCacheVO.getResponseParam());
            p_requestVO.setResponseMap(ResponseMap);
            if (BTSLUtil.isNullString(requestParam) || (BTSLUtil.isNullString(messageFormat))) {
                _log.error("parseExtStringRequest", "requestParam=" + requestParam + ",messageFormat=" + messageFormat);
                throw new BTSLBaseException("ExtAPIStringParser", METHOD_NAME, PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            } else {
                mandatoryRequestParamArr = requestParam.split(",");
                msgFormatArr = messageFormat.split(" ");
            } 
        	String reqType=(String) requestMap.get("TYPE");
            int mandatoryRequestParamArrs=mandatoryRequestParamArr.length;
            for (int i = 0; i < mandatoryRequestParamArrs; i++) {
                if (requestMap.containsKey(mandatoryRequestParamArr[i])) {
                	_log.debug(METHOD_NAME, "mandatoryRequestParamArr[i]="+mandatoryRequestParamArr[i]);
                    if (BTSLUtil.isNullString((String) requestMap.get(mandatoryRequestParamArr[i]))) {
                    
                    	if((reqType.equals("CGENQREQ") && mandatoryRequestParamArr[i].equals("MSISDN1")) || 
                    			(reqType.equals("VOMSSTCHGREQ") && mandatoryRequestParamArr[i].equals("FROM_SERIALNO")) ||
                    			(reqType.equals("VOMSSTCHGREQ") && mandatoryRequestParamArr[i].equals("TO_SERIALNO")))
                    	                              continue;
                    	if(reqType.equals("EXTSYSENQREQ") && mandatoryRequestParamArr[i].equals("SELECTOR"))
                    		continue;
                        throw new BTSLBaseException("ExtAPIStringParser", METHOD_NAME, PretupsErrorCodesI.MANDATORY_EMPTY);
                    }

                } /*else {
                    if (mandatoryRequestParamArr[i].equals("IMEI")) {
                        ;
                    } else {
                        throw new BTSLBaseException("ExtAPIStringParser", METHOD_NAME, PretupsErrorCodesI.MANDATORY_EMPTY);
                    }

                }*/
            }
            if (!BTSLUtil.isNullString(date))
            {
            	 BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInStringExtGw(date));
            
            }
			String CHNL_PLAIN_SMS_SEPARATOR=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			if(BTSLUtil.isNullString(CHNL_PLAIN_SMS_SEPARATOR)){
				CHNL_PLAIN_SMS_SEPARATOR=" ";
			}
			if(_log.isDebugEnabled()){
				_log.error(METHOD_NAME, "CHNL_PLAIN_SMS_SEPARATOR="+CHNL_PLAIN_SMS_SEPARATOR+",messageFormat="+messageFormat);
			}
            StringBuffer parsedRequestMessageStr = new StringBuffer();
            int msgFormatArry=msgFormatArr.length;
            for (int i = 0; i < msgFormatArry; i++) {
                if (requestMap.containsKey(msgFormatArr[i])) {
                	if(BTSLUtil.isNullString((String)requestMap.get(msgFormatArr[i]))){
                        parsedRequestMessageStr = parsedRequestMessageStr.append((String)null);
                	}else{
                		parsedRequestMessageStr = parsedRequestMessageStr.append(requestMap.get(msgFormatArr[i]));
                	}
					if(!(msgFormatArr.length==(i+1))){
						parsedRequestMessageStr.append(CHNL_PLAIN_SMS_SEPARATOR);
					}
                }
            }
			if(_log.isDebugEnabled()){
				_log.error(METHOD_NAME, "Finally parsedRequestMessageStr="+parsedRequestMessageStr);
			}
        	//Ended Here
            p_requestVO.setDecryptedMessage(parsedRequestMessageStr.toString());
            p_requestVO.setRequestMap(requestMap);

            final HashMap responseMap = new HashMap<String, String>();
            responseMap.put("RESPONSEPARAM", serviceKeywordCacheVO.getResponseParam());
            p_requestVO.setResponseMap(responseMap);

        } catch (BTSLBaseException be) {
            _log.error("parseChannelRequest", "Exception e: " + be);
            _log.errorTrace("METHOD_NAME", be);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            throw be;
        }
        catch (ParseException p)
        {
        	p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER);
            _log.error("parseChannelRequest", "Exception e: " + p);
            _log.errorTrace("METHOD_NAME", p);
            throw new BTSLBaseException("ExtAPIStringParser", "parseExtStringRequest", PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER);
        }
        catch (Exception e) {
        	p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
            _log.error("parseChannelRequest", "Exception e: " + e);
            _log.errorTrace("METHOD_NAME", e);
            throw new BTSLBaseException("ExtAPIStringParser", "parseExtStringRequest", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public static void populateResponseMap(RequestVO p_requestVO, String p_Str) throws Exception {
        final String methodName = "populateResponseMap";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString() + ",Success Code:" + p_Str);
        }

        final java.util.Date date = new java.util.Date();
        try {
            final HashMap responseMap = p_requestVO.getResponseMap();
            if (p_requestVO.isSuccessTxn()) {
                responseMap.put("TXNSTATUS", PretupsI.TXN_STATUS_SUCCESS);
            } else {
                String message = p_requestVO.getMessageCode();
                if (message.indexOf("_") != -1) {
                    message = message.substring(0, message.indexOf("_"));
                }
                responseMap.put("TXNSTATUS", message);
            }
            // responseMap.put("EXTREFNUM",p_requestVO.getExternalReferenceNum());
            if (!p_requestVO.isSuccessTxn()) {
                responseMap.put("MESSAGE", ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()));
            } else {
            	responseMap.put("MESSAGE", ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_Str, p_requestVO.getMessageArguments()));
            	
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIStringParser[populateResponseMap]",
                PretupsErrorCodesI.XML_ERROR_EXCEPTION, "", "", "generateExtStringResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p_requestVO.getResponseMap()" + p_requestVO.getResponseMap());
            }
        }
    }

    public static void generateExtStringResponse(RequestVO p_requestVO) throws Exception {
        final String methodName = "generateExtStringResponse";
        StringBuilder loggerValue= new StringBuilder();
		printEnterExitLogger(methodName, loggerValue, ENTRY_KEY,  p_requestVO);
        String responseStr = null;
        StringBuffer generateResStr=null;
		String contentType=p_requestVO.getReqContentType();
        final java.util.Date date = new java.util.Date();
        try {
            final HashMap responseMap = p_requestVO.getResponseMap();
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false);
             String[] mandatoryResponseParamArr = ((String) responseMap.get("RESPONSEPARAM")).split(","); // "TYPE,DATE,TXNSTATUS,EXTREFNUM,MESSAGE".split(",");
            if(contentType!=null&&(contentType.indexOf("plain")!=-1 || contentType.indexOf("PLAIN")!=-1)){
            	generateResStr = new StringBuffer();
            for (int i = 0; i < mandatoryResponseParamArr.length; i++) {
            	generateResStr =generateResStr.append(mandatoryResponseParamArr[i]+"=");
				if("DATE".equals((String)mandatoryResponseParamArr[i]))
					generateResStr =generateResStr.append(sdf.format(date));
				else
					generateResStr =generateResStr.append(responseMap.get(mandatoryResponseParamArr[i]));
				    generateResStr =generateResStr.append("&");
			}
        	responseStr=generateResStr.toString().substring(0,generateResStr.toString().lastIndexOf("&"));
        	}else{
        		
        	 generateResStr = new StringBuffer("<?xml version=\"1.0\"?><COMMAND>");
        	for(int i =0;i<mandatoryResponseParamArr.length;i++){
            
                generateResStr = generateResStr.append("<" + mandatoryResponseParamArr[i] + ">");
                if ("DATE".equals((String) mandatoryResponseParamArr[i])) {
                    generateResStr = generateResStr.append(sdf.format(date));
                } else {
					if(!BTSLUtil.isNullString((String)responseMap.get(mandatoryResponseParamArr[i]))){
                    	generateResStr = generateResStr.append(responseMap.get(mandatoryResponseParamArr[i]));
                	}
				}
                generateResStr = generateResStr.append("</" + mandatoryResponseParamArr[i] + ">");
            }
            generateResStr.append("</COMMAND>");
            responseStr = generateResStr.toString();	
        	}   
            p_requestVO.setSenderReturnMessage(responseStr);
    }
    catch(Exception e)
	{
		_log.errorTrace(methodName, e);
		_log.error(methodName,"Exception e: "+e);
		p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ExtAPIStringParser[generateExtStringResponse]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateExtStringResponse:"+e.getMessage());
	}
	finally
	{
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Exiting responseStr: "+responseStr);
		}
	}
}
    

public static void generateResponseCardGroupEnquiryRequest(RequestVO p_requestVO) throws Exception
	{
		final String methodName = "generateResponseCardGroupEnquiryRequest";
		StringBuilder loggerValue= new StringBuilder();
		printEnterExitLogger(methodName, loggerValue, ENTRY_KEY,  p_requestVO);
		String responseStr= null;
		StringBuffer sbf=null;
		String responseMessage=null;
		String [] responseMessageArray=null;
		try
		{
			if(p_requestVO.isSuccessTxn())
			{
				sbf=new StringBuffer(1024);
				sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
				sbf.append("<TYPE>CGENQRESP</TYPE>");
				sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+ "</TXNSTATUS>");
				sbf.append("<MESSAGE>"+ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+ "</MESSAGE>");
				sbf.append("<MSISDN2>"+p_requestVO.getReceiverMsisdn()+ "</MSISDN2>");
				if(!BTSLUtil.isNullString(p_requestVO.getReceiverServiceClassId()))
				sbf.append("<SERVICECLASS>"+p_requestVO.getReceiverServiceClassId()+ "</SERVICECLASS>");
				else
					sbf.append("<SERVICECLASS></SERVICECLASS>");
				sbf.append("<CGDETAILS>");
				responseMessage=p_requestVO.getSenderReturnMessage();
				responseMessageArray=responseMessage.split(",");
				for(int i=0;i<responseMessageArray.length;i++)
				{
					String responseSubMessage=responseMessageArray[i];
					String [] responseSubMessageArray =responseSubMessage.split(":");
					int selecAmountCount =(responseSubMessageArray.length -1)/2;

					sbf.append("<DETAIL>");
					for(int j=1,k=1;j<=selecAmountCount;j++)
					{
						sbf.append("<SLABAMT>"+responseSubMessageArray[k]+ "</SLABAMT>");
						sbf.append("<CGDESC>"+responseSubMessageArray[k+1]+ "</CGDESC>");
						sbf.append("<SUBSERVICE>"+responseSubMessageArray[k+2]+ "</SUBSERVICE>");
						k=k+2;
					}
					sbf.append("</DETAIL>");

				}
				sbf.append("</CGDETAILS>");
				sbf.append("</COMMAND>");
				responseStr = sbf.toString();
				p_requestVO.setSenderReturnMessage(responseStr);
			}
			else
			{	
				sbf=new StringBuffer(1024);
				sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
				sbf.append("<TYPE>CGENQRESP</TYPE>");
				sbf.append("<TXNSTATUS>"+p_requestVO.getMessageCode()+"</TXNSTATUS>");
				sbf.append("<MESSAGE>"+ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+ "</MESSAGE>");
				sbf.append("<MSISDN2>"+p_requestVO.getReceiverMsisdn()+ "</MSISDN2>");
				sbf.append("<SERVICECLASS>"+p_requestVO.getReceiverServiceClassId()+ "</SERVICECLASS>");
				sbf.append("<CGDETAILS>");
				sbf.append("<DETAIL>");
				sbf.append("<SLABAMT></SLABAMT>");
				sbf.append("<CGDESC></CGDESC>");
				sbf.append("<SUBSERVICE></SUBSERVICE>");
				sbf.append("</DETAIL>");
				sbf.append("</CGDETAILS>");
				sbf.append("</COMMAND>");
				responseStr = sbf.toString();
				p_requestVO.setSenderReturnMessage(responseStr);
			}
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateResponseP2PMCDListViewRequest]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateResponseP2PMCDListViewRequest:"+e.getMessage());
		}
		finally
		{
			printExitLogger(methodName, loggerValue, responseStr);
		}
	}	



public static void generateChannelCardGroupEnquiryResponse(RequestVO p_requestVO) throws Exception
	{
		final String methodName = "generateResponseCardGroupEnquiryRequest";
		StringBuilder loggerValue= new StringBuilder();
		printEnterExitLogger(methodName, loggerValue, ENTRY_KEY,  p_requestVO);
		String responseStr= null;
		StringBuffer sbf=null;
		String responseMessage=null;
		String [] responseMessageArray=null;
		try
		{
			if(p_requestVO.isSuccessTxn())
			{
				sbf=new StringBuffer(1024);
				sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
				sbf.append("<TYPE>CHCGENRESP</TYPE>");
				sbf.append("<MSISDN1>"+p_requestVO.getRequestMSISDN()+ "</MSISDN1>");
				sbf.append("<TXNSTATUS>"+PretupsI.TXN_STATUS_SUCCESS+ "</TXNSTATUS>");
				sbf.append("<SERVICE>"+p_requestVO.getEnquiryServiceType()+ "</SERVICE>");
				sbf.append("<MESSAGE>"+ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+ "</MESSAGE>");
				sbf.append("<CGDETAILS>");
				responseMessage=p_requestVO.getSenderReturnMessage();
				responseMessageArray=responseMessage.split(",");
				for(int i=0;i<responseMessageArray.length;i++)
				{
					String responseSubMessage=responseMessageArray[i];
					String [] responseSubMessageArray =responseSubMessage.split(":");
						sbf.append("<DETAIL>");
						sbf.append("<SELECTOR>"+responseSubMessageArray[5]+ "</SELECTOR>");
						sbf.append("<SELECTORNAME>"+responseSubMessageArray[4]+ "</SELECTORNAME>");
						sbf.append("<FROMSLAB>"+responseSubMessageArray[1]+ "</FROMSLAB>");
						sbf.append("<TOSLAB>"+responseSubMessageArray[2]+ "</TOSLAB>");
						sbf.append("<CGDESC>"+responseSubMessageArray[3]+ "</CGDESC>");
						sbf.append("<CGID>"+responseSubMessageArray[0]+ "</CGID>");
						sbf.append("<VALIDITY>"+responseSubMessageArray[6]+ "</VALIDITY>");
						sbf.append("<REVERSALALLOWED>"+responseSubMessageArray[7]+ "</REVERSALALLOWED>");
						sbf.append("</DETAIL>");

				}
				sbf.append("</CGDETAILS>");
				sbf.append("</COMMAND>");
				responseStr = sbf.toString();
				p_requestVO.setSenderReturnMessage(responseStr);
			}
			else
			{	
				sbf=new StringBuffer(1024);
				sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
				sbf.append("<TYPE>CHCGENRESP</TYPE>");
				sbf.append("<MSISDN1>"+p_requestVO.getRequestMSISDN()+ "</MSISDN1>");
				sbf.append("<TXNSTATUS>"+p_requestVO.getMessageCode()+"</TXNSTATUS>");
				sbf.append("<MESSAGE>"+ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments())+ "</MESSAGE>");
				sbf.append("<SERVICE>"+p_requestVO.getEnquiryServiceType()+ "</TXNSTATUS>");
				sbf.append("<CGDETAILS>");
				sbf.append("<DETAIL>");
				
				sbf.append("<SELECTOR></SELECTOR>");
				sbf.append("<SELECTORNAME></SELECTORNAME>");
				sbf.append("<SLABAMT></SLABAMT>");
				sbf.append("<CGDESC></CGDESC>");
				sbf.append("<VALIDITY></VALIDITY>");
				sbf.append("<REVERSALALLOWED></REVERSALALLOWED>");
				
				sbf.append("</DETAIL>");
				sbf.append("</CGDETAILS>");
				sbf.append("</COMMAND>");
				responseStr = sbf.toString();
				p_requestVO.setSenderReturnMessage(responseStr);
			}
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"XMLStringParser[generateResponseP2PMCDListViewRequest]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateResponseP2PMCDListViewRequest:"+e.getMessage());
		}
		finally
		{
			printExitLogger(methodName, loggerValue, responseStr);
		}
	}

	/**
	 * To write the Enter/Exit logger for parse request
	 * @param methodName
	 * @param loggerValue
	 * @param enterExitKey
	 * @param p_requestVO
	 */
	private static void printEnterExitLogger(String methodName, StringBuilder loggerValue, String enterExitKey, RequestVO p_requestVO){
		if (_log.isDebugEnabled()) {
	    	loggerValue.setLength(0);
	    	loggerValue.append(enterExitKey);
	    	loggerValue.append(p_requestVO.toString());
	    	_log.debug(methodName, loggerValue);
	    }
	}
	
	/**
	 * To write the response logger
	 * @param methodName
	 * @param loggerValue
	 * @param responseStr
	 */
	private static void printExitLogger(String methodName, StringBuilder loggerValue, String responseStr){
		if (_log.isDebugEnabled()) {
	     	loggerValue.setLength(0);
	     	loggerValue.append(EXIT_KEY_RES);
	     	loggerValue.append(responseStr.toString());
	     	_log.debug(methodName, loggerValue);
	     }
	}
}
