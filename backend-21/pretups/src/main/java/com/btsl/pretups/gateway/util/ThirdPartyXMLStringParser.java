package com.btsl.pretups.gateway.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import  org.apache.commons.lang3.StringUtils;

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
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
/**
 *  * @(#)ThirdPartyXMLStringParser.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * @author Mahindra Comviva 02 08 2016
 *
 */
/**
 * @author akanksha.gupta
 *
 */
public class ThirdPartyXMLStringParser {
	   public static final Log _log = LogFactory.getLog(ThirdPartyXMLStringParser.class.getName());
	   private static String CHNL_MESSAGE_SEP = null;
  	 private static OperatorUtilI _operatorUtil = null;

	/**
	 * ensures no instantiation
	 */
  	 private ThirdPartyXMLStringParser(){
  		 
  	 }
	    
  	 static {
	    	
	        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        }catch (InstantiationException e) {
	        	_log.errorTrace("ThirdPartyXMLStringParser", e);
	        } catch (ClassNotFoundException e) {
	        	_log.errorTrace("ThirdPartyXMLStringParser", e);
	        } 
	        catch (Exception e) {
	            _log.errorTrace("ThirdPartyXMLStringParser", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ThirdPartyXMLStringParser[initialize]", "", "", "",
	                "Exception while loading the class at the call:" + e.getMessage());
	        }

	        try {
	        	CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
	        	if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
	        		CHNL_MESSAGE_SEP = " ";
	        	}
	        }catch (Exception e) {
	                _log.errorTrace("static", e);
	            }
	    
	    }
	    
    /**
	     * ThirdPartyXMLStringParser.java
	     * @param p_requestVO
	     * @throws BTSLBaseException
	     * void
	     * akanksha.gupta
	     * 05-Aug-2016 4:00:27 pm
	     */
	    public static void parseChanenlCurrencyConversionReq(RequestVO p_requestVO) throws BTSLBaseException {
	        final String methodName = "parseChanenlCurrencyConversionReq";
	        final String className = "ThirdPartyXMLStringParser";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
	        }
	        String parsedRequestStr = null;
	        String extNwCode = null;
	        String date = null;
	        String extRefNumber = null;
	        String currencyRecord = null;
	        String record = null;
	        String sourceCurrency = null;
	        String targetCurrency = null;
	        String targetCountryCode = null;
	        String conversion = null;
	        String partialFormat ="";
	          
	        try {
	            final String requestStr = p_requestVO.getRequestMessage();
	            int index = requestStr.indexOf("<TYPE>");
	            final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));

	            try {
	                index = requestStr.indexOf("<EXTREFNUM>");
	                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
	                index = requestStr.indexOf("<EXTNWCODE>");
	                extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
	                index = requestStr.indexOf("<DATE>");
	                date = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
	                index = requestStr.indexOf("<CURRENCYRECORDS>");
	                currencyRecord = requestStr.substring(index + "<CURRENCYRECORDS>".length(), requestStr.indexOf("</CURRENCYRECORDS>", index));
	                p_requestVO.setExternalNetworkCode(extNwCode);
		            p_requestVO.setExternalReferenceNum(extRefNumber);
		            p_requestVO.setExternalTransactionDate(date);
		            int i =0;
	             XMLStringValidation.validateCurrencyConversionRequest(p_requestVO, extNwCode, extRefNumber, currencyRecord, date);
	             int count = StringUtils.countMatches(currencyRecord, "<RECORD>");
	             	index=0;
	              while(i<count)
	             {
	            	  int subindex =0;
		            	
	            	  subindex = currencyRecord.indexOf("<RECORD>",index);
	            	 record = currencyRecord.substring(subindex + "<RECORD>".length(), currencyRecord.indexOf("</RECORD>", subindex));
	            	 subindex = record.indexOf("<SOURCECURRENCY>");
	            	 sourceCurrency = record.substring(subindex + "<SOURCECURRENCY>".length(), record.indexOf("</SOURCECURRENCY>", subindex));

	            	 subindex = record.indexOf("<TARGETCURRENCY>");
	            	 targetCurrency = record.substring(subindex + "<TARGETCURRENCY>".length(), record.indexOf("</TARGETCURRENCY>", subindex));

	            	 subindex = record.indexOf("<TARGETCOUNTRYCODE>");
	            	 targetCountryCode = record.substring(subindex + "<TARGETCOUNTRYCODE>".length(), record.indexOf("</TARGETCOUNTRYCODE>", subindex));

	            	 subindex = record.indexOf("<CONVERSION>");
	            	 conversion = record.substring(subindex + "<CONVERSION>".length(), record.indexOf("</CONVERSION>", subindex));
	            	 index=index +record.length();
	            	  
	            	 if(BTSLUtil.isNullString(sourceCurrency)||BTSLUtil.isNullString(targetCurrency)||BTSLUtil.isNullString(targetCountryCode)||BTSLUtil.isNullString(conversion))
	            	 {
	            		 _log.error(methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            		 throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            	 }
	            		 
	            		 i++;
	            	 if(BTSLUtil.isNullString(partialFormat))
	            		 partialFormat= sourceCurrency + CHNL_MESSAGE_SEP + targetCurrency+CHNL_MESSAGE_SEP+targetCountryCode+CHNL_MESSAGE_SEP+conversion;
	            	 else
	            		 partialFormat=partialFormat+CHNL_MESSAGE_SEP + sourceCurrency + CHNL_MESSAGE_SEP +  targetCurrency+CHNL_MESSAGE_SEP+targetCountryCode+CHNL_MESSAGE_SEP+conversion;
	             }    
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	                _log.error(methodName, "Exception e: " + e.getMessage());
	                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            }

	            parsedRequestStr = PretupsI.CURRENCY_CONVERSION + CHNL_MESSAGE_SEP + partialFormat;
	            p_requestVO.setDecryptedMessage(parsedRequestStr);
	                 p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
	        } 
	        catch(BTSLBaseException be)
	        {
	        	  final ArrayList argumentList = new ArrayList();
		            final ArrayList argumentVOList = new ArrayList();
		            KeyArgumentVO argumentVO = new KeyArgumentVO();
	                
					  final String[] transferStatusArr = new String[2];
	                  argumentVO.setKey(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	                  argumentVO.setArguments(transferStatusArr);
	                  argumentVOList.add(argumentVO);
	      
	                  HashMap map = p_requestVO.getRequestMap();
		                if (map == null) {
		                    map = new HashMap();
		                }
		                p_requestVO.setResponseMap(map);
	            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            p_requestVO.getResponseMap().put("TYPE", "CURRENCYCONVERSIONRESP");
	            p_requestVO.getResponseMap().put("EXTREFNUM", p_requestVO.getExternalReferenceNum());
	            p_requestVO.getResponseMap().put("TXNSTATUS", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            p_requestVO.getResponseMap().put("MESSAGE", BTSLUtil.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),argumentVOList));
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
			    _log.error(methodName, "Exception e: " +be);
	            throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	       
	        	
	        	
	        }
	        catch (Exception e) {
	        	
	        	  HashMap map = p_requestVO.getRequestMap();
	                if (map == null) {
	                    map = new HashMap();
	                }
	                p_requestVO.setResponseMap(map);
	        	  final ArrayList argumentList = new ArrayList();
		            final ArrayList argumentVOList = new ArrayList();
		            KeyArgumentVO argumentVO = new KeyArgumentVO();
	                
					  final String[] transferStatusArr = new String[2];
	                  argumentVO.setKey(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	                  argumentVO.setArguments(transferStatusArr);
	                  argumentVOList.add(argumentVO);
	      
	            p_requestVO.getResponseMap().put("TYPE", "CURRENCYCONVERSIONRESP");
	            p_requestVO.getResponseMap().put("EXTREFNUM", p_requestVO.getExternalReferenceNum());
	            p_requestVO.getResponseMap().put("TXNSTATUS", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            p_requestVO.getResponseMap().put("MESSAGE", BTSLUtil.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),argumentVOList));
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	
	            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	            _log.error(methodName, "Exception e: " + e);
	            throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
	        }
	        
	        finally {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting p_requestVO: " + p_requestVO.toString());
	            }
	        }
	    }

	    

	    /**
	     * ThirdPartyXMLStringParser.java
	     * @param p_requestVO
	     * @throws Exception
	     * void
	     * akanksha.gupta
	     * 05-Aug-2016 3:59:56 pm
	     */
	    public static void generateChanenlCurrencyConversionResponse(RequestVO p_requestVO) throws Exception {
	        final String methodName = "generateChanenlCurrencyConversionResponse";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
	        }
	        String responseStr = null;
	        StringBuilder generateResStr=null;
	       final LocalDateTime date = LocalDateTime.now();
	        String[] mandatoryResponseParamArr;
	        try {
	            final HashMap responseMap = p_requestVO.getResponseMap();
	           final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
	           if(!BTSLUtil.isNullString((String) responseMap.get("RESPONSEPARAM")))
	           {
	        	   mandatoryResponseParamArr = ((String) responseMap.get("RESPONSEPARAM")).split(","); // "TYPE,DATE,TXNSTATUS,EXTREFNUM,MESSAGE".split(",");

	        	   generateResStr = new StringBuilder("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
	        	   for(int i =0;i<mandatoryResponseParamArr.length;i++){

	        		   generateResStr = generateResStr.append("<" + mandatoryResponseParamArr[i] + ">");
	        		   if ("DATE".equals((String) mandatoryResponseParamArr[i])) {
	        			   generateResStr = generateResStr.append(date.format(formatter));
	        		   } else {
	        			   generateResStr = generateResStr.append(responseMap.get(mandatoryResponseParamArr[i]));
	        		   }
	        		   generateResStr = generateResStr.append("</" + mandatoryResponseParamArr[i] + ">");
	        	   }


	        	   List<CurrencyConversionVO> list= (List)responseMap.get("CURRENCYUPDATEOUTPUT");
				   if(list != null && !list.isEmpty())
	        		   generateResStr.append("<CURRENCYRECORDS>");
	        	   for( CurrencyConversionVO currencyConversionVO :list )
	        	   {
	        		   generateResStr.append("<RECORD>");
	        		   generateResStr.append("<SOURCECURRENCY>").append(currencyConversionVO.getSourceCurrencyCode()).append("</SOURCECURRENCY>");
	        		   generateResStr.append("<TARGETCURRENCY>").append(currencyConversionVO.getTargetCurrencyCode()).append("</TARGETCURRENCY>");
	        		   generateResStr.append("<TARGETCOUNTRYCODE>").append(currencyConversionVO.getCountry()).append("</TARGETCOUNTRYCODE>");	
	        		   generateResStr.append("<CONVERSION>").append(currencyConversionVO.getConversion()).append("</CONVERSION>");	
	        		   generateResStr.append("<ERRORCODE>").append(currencyConversionVO.getErrorCode()).append("</ERRORCODE>");	
	        		   generateResStr.append("<ERRORMESSAGE>").append(currencyConversionVO.getErrorMsg()).append("</ERRORMESSAGE>");	
	        		   generateResStr.append("</RECORD>");

	        	   }
	        	   generateResStr.append("</CURRENCYRECORDS>");
	        	   generateResStr.append("</COMMAND>");
	        	   responseStr = generateResStr.toString();	
	        	   p_requestVO.setSenderReturnMessage(responseStr);
	           }
	           else
	           {
	        	  generateResStr = new StringBuilder("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
		              generateResStr.append("<TYPE>").append(p_requestVO.getResponseMap().get("TYPE")).append("</TYPE>");
	        		   generateResStr.append("<DATE>").append(p_requestVO.getExternalTransactionDate()+"<DATE>");
	        		   generateResStr.append("<EXTREFNUM>").append(p_requestVO.getExternalReferenceNum()).append("</EXTREFNUM>");	
	        		   generateResStr.append("<TXNSTATUS>").append(p_requestVO.getResponseMap().get("TXNSTATUS")).append("</TXNSTATUS>");	
	        		   generateResStr.append("<MESSAGE>").append(p_requestVO.getResponseMap().get("MESSAGE")).append("</MESSAGE>");	
	        		   generateResStr.append("</COMMAND>");
	        		   responseStr = generateResStr.toString();	
		        	   p_requestVO.setSenderReturnMessage(responseStr);
	           }
	    }
	    catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName,"Exception e: "+e);
			p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ThirdPartyXMLStringParser[generateChanenlCurrencyConversionResponse]",PretupsErrorCodesI.XML_ERROR_EXCEPTION,"","","generateChanenlCurrencyConversionResponse:"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Exiting responseStr: "+responseStr);
			}
		}
	}

}
