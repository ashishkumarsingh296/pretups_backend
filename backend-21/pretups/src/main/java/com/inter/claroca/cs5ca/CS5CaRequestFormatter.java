package com.inter.claroca.cs5ca;

/**
 * @(#)CS5CaRequestFormatter.java
 * Copyright(c) 2015, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 	History
 *-------------------------------------------------------------------------------------------------
 * 	 Zeeshan Aleem   		July 20, 2016		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 */
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class CS5CaRequestFormatter 
{
	public static Log _log = LogFactory.getLog(CS5CaRequestFormatter.class);
	private SimpleDateFormat _sdf = null;
	private TimeZone _timeZone = null;
	private String _transDateFormat="yyyyMMdd'T'HH:mm:ss";//Defines the Date and time format of CS5CaIN.
	private DecimalFormat _twoDigits = null;
	private int _offset;
	private String _sign="+";
	private int _hours;
	private int _minutes;

	public CS5CaRequestFormatter() throws Exception
	{
		String METHOD_NAME="CS5CaRequestFormatter[constructor]";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered");
		try
		{
			_sdf = new SimpleDateFormat(_transDateFormat);
			_timeZone = TimeZone.getDefault();
			_sdf.setTimeZone(_timeZone);
			_twoDigits = new DecimalFormat("00");
			_offset = _sdf.getTimeZone().getOffset(new Date().getTime());
			if (_offset < 0)
			{
				_offset = -_offset;
				_sign = "-";
			}
			_hours = _offset / 3600000;
			_minutes = (_offset - _hours * 3600000) / 60000;
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Exited");
		}
	}
	/**
	 * This method is used to parse the response string based on the type of Action.
	 * @param	int p_action
	 * @param	HashMap p_map
	 * @return	String.
	 * @throws	Exception
	 */
	protected String generateRequest(int p_action, HashMap<String,String> p_map) throws Exception 
	{
		String METHOD_NAME="generateRequest";
		if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Entered p_action="+p_action+" map="+p_map);
		String str=null;
		p_map.put("action",String.valueOf(p_action));
		try
		{
			switch(p_action)
			{
			case CS5CaI.ACTION_ACCOUNT_INFO: 
			{
				str=generateGetAccountInfoRequest(p_map);
				break;	
			}
			case CS5CaI.ACTION_ACCOUNT_DETAILS: 
			{
				str=generateGetAccountDetailRequest(p_map);
				break;	
			}
			case CS5CaI.ACTION_RECHARGE_CREDIT: 
			{
				str=generateRechargeCreditRequest(p_map);
				break;	
			}
			case CS5CaI.ACTION_IMMEDIATE_DEBIT: 
			{
				str=generateImmediateDebitRequest(p_map);
				break;	
			}
			case CS5CaI.ACTION_DEDICATED_ACCOUNT_CD: 
			{
				str=generateDedicatedAccountCDRequest(p_map);
				break;	
			}
			}
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e ::"+e.getMessage());
			throw e;
		} 
		finally
		{
			if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Exited Request String: str="+str);
		}
		return str;
	}

	/**
	 * This method is used to parse the response.
	 * @param	int p_action
	 * @param	String p_responseStr
	 * @return	HashMap
	 * @throws	Exception
	 */
	public HashMap<String,String> parseResponse(int p_action,String p_responseStr,HashMap<String,String> p_responseMap) throws Exception
	{
		String METHOD_NAME="parseResponse";
		if(_log.isDebugEnabled())		_log.debug(METHOD_NAME,"Entered p_action="+p_action+" p_responseStr="+p_responseStr);
		HashMap<String,String> map=null;
		try
		{
			switch(p_action)
			{
			case CS5CaI.ACTION_ACCOUNT_INFO: 
			{
				map=parseGetAccountInfoResponse(p_responseStr);
				break;	
			}
			case CS5CaI.ACTION_ACCOUNT_DETAILS: 
			{
				map=parseGetAccountDetailResponse(p_responseStr);
				map.putAll(p_responseMap);
				break;	
			}
			case CS5CaI.ACTION_RECHARGE_CREDIT: 
			{
				map=parseRechargeCreditResponse(p_responseStr);
				break;	
			}
			case CS5CaI.ACTION_IMMEDIATE_DEBIT: 
			{
				map=parseImmediateDebitResponse(p_responseStr);
				break;	
			}
			case CS5CaI.ACTION_DEDICATED_ACCOUNT_CD: 
			{
				map=parseDedicatedAccountCDRequest(p_responseStr);
				break;	
			}
			}
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Exiting map::"+map);
		}
		return map;	
	}

	/**
	 * This method is used to generate the request for getting account information.
	 * @param	HashMap	p_requestMap
	 * @return	String
	 * @throws	Exception
	 */
	private String generateGetAccountInfoRequest(HashMap<String,String> p_requestMap) throws Exception
	{
		String METHOD_NAME="generateGetAccountInfoRequest";
		if(_log.isDebugEnabled()) 
			_log.debug(METHOD_NAME,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		try
		{
			stringBuffer=new StringBuffer(1028);
			stringBuffer.append("<?xml version='1.0'?>");
			stringBuffer.append("<methodCall>");
			stringBuffer.append("<methodName>GetBalanceAndDate</methodName>");
			stringBuffer.append(startRequestTag());
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originNodeType</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)p_requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Set the optional parameter subscriberNumberNAI if present.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");
			//Set the subscriberNumber after adding or removing the prefix defined in the INFile.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(METHOD_NAME,"Exiting Request String:requestStr::"+requestStr);
		}
		return requestStr;
	}

	/**
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
	{
		String METHOD_NAME="generateRechargeCreditRequest";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		try
		{
			stringBuffer = new StringBuffer(1028);
			stringBuffer.append("<?xml version='1.0'?>");
			stringBuffer.append("<methodCall>");
			stringBuffer.append("<methodName>Refill</methodName>");
			stringBuffer.append(startRequestTag());
			//Set the origin originNodeType
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originNodeType</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName 
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)p_requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Check the optional value of SubscriberNumberNAI if it is not null set it.	        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");	        
			//Set subscriberNumber and add or remove the msisdn if required.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("ExternalData1")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData1</name>");
                                stringBuffer.append("<value><string>"+p_requestMap.get("OTT_CODE")+"</string></value>");
				stringBuffer.append("</member>");
			}
			//Set the optional value of ExternalData2, if required.
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("ExternalData2")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData2</name>");
				stringBuffer.append("<value><string>"+p_requestMap.get("ExternalData2")+"</string></value>");
				stringBuffer.append("</member>");
			}
                //      stringBuffer.append("<member>");
                //      stringBuffer.append("<name>requestRefillAccountAfterFlag</name>");
                //      stringBuffer.append("<value><boolean>"+p_requestMap.get("REFILL_ACNT_AFTER_FLAG")+"</boolean></value>");
                //      stringBuffer.append("</member>");
			//Set the transfer_amount to transactionAmount
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionAmount</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("transfer_amount")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transaction_currency defined into INFile for transactionCurrency.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionCurrency</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("CURRENCY")+"</string></value>");
			stringBuffer.append("</member>");
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_CODE_RF")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionCode</name>");
				stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_CODE_RF")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_TYPE_RF")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionType</name>");
				stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_TYPE_RF")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}		
			//Set the card group for paymentProfileID
			stringBuffer.append("<member>");
			stringBuffer.append("<name>refillProfileID</name>");
                        stringBuffer.append("<value><string>"+p_requestMap.get("REFILL_PROFILE_ID")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();       
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e: "+e);
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Exiting Request requestStr::"+requestStr);
		}
		return requestStr;
	}

	/**
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private String generateImmediateDebitRequest(HashMap<String,String> p_requestMap) throws Exception
	{
		String METHOD_NAME="generateImmediateDebitRequest";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
//		String inTransactionID=null;
		try
		{
			stringBuffer = new StringBuffer(1028);
			stringBuffer.append("<?xml version='1.0'?>");
			stringBuffer.append("<methodCall>");
			stringBuffer.append("<methodName>UpdateBalanceAndDate</methodName>");
			stringBuffer.append(startRequestTag());
			//Set the origin originNodeType
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originNodeType</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName 
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			//Set the originTransactionID
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)p_requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Check the optional value of SubscriberNumberNAI if it is not null set it.	        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");	        
			//Set subscriberNumber and add or remove the msisdn if required.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transfer_amount to transactionAmount
			stringBuffer.append("<member>");
			stringBuffer.append("<name>adjustmentAmountRelative</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("transfer_amount")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transaction_currency defined into INFile for transactionCurrency.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionCurrency</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("CURRENCY")+"</string></value>");
			stringBuffer.append("</member>");
			String validityDays = (String)p_requestMap.get("VALIDITY_DAYS");
			if( (!InterfaceUtil.isNullString(validityDays)) && !"0".equals(validityDays))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>supervisionExpiryDateRelative</name>");
				stringBuffer.append("<value><i4>"+validityDays+"</i4></value>");
				stringBuffer.append("</member>");
			}
			String graceDays = (String)p_requestMap.get("GRACE_DAYS");
			if(!InterfaceUtil.isNullString(graceDays) && !"0".equals(graceDays))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>serviceFeeExpiryDateRelative</name>");
				stringBuffer.append("<value><i4>"+(String)p_requestMap.get("GRACE_DAYS")+"</i4></value>");
				stringBuffer.append("</member>");
			}        
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("ExternalData1")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData1</name>");
                                stringBuffer.append("<value><string>"+(String)p_requestMap.get("OTT_CODE")+"</string></value>");
				stringBuffer.append("</member>");
			}
			//Set the optional value of ExternalData2, if required.
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("ExternalData2")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData2</name>");
				stringBuffer.append("<value><string>"+(String)p_requestMap.get("ExternalData2")+"</string></value>");
				stringBuffer.append("</member>");
			}
			//set the values for transaction code and transaction type 
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_CODE_CR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionCode</name>");
				String reqService=(String)p_requestMap.get("REQ_SERVICE");
				if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"REQ_SERVICE::"+reqService);
				if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
					stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
				else
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_CODE_CR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_TYPE_CR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionType</name>");
				String reqService=(String)p_requestMap.get("REQ_SERVICE");
				
                                if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"REQ_SERVICE::"+reqService);
                                if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
                                        stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
                                else
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_TYPE_CR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_CODE_DR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionCode</name>");
				String reqService=(String)p_requestMap.get("REQ_SERVICE");
				 if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
				 	stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
				else
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_CODE_DR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_TYPE_DR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionType</name>");
				String reqService=(String)p_requestMap.get("REQ_SERVICE");
				if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
                                        stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
                                else
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_TYPE_DR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();	       
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(METHOD_NAME,"Exiting  requestStr="+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method is used to parse the response of GetAccountinformation.
	 * @param	String p_responseStr
	 * @return	HashMap
	 */
	private HashMap<String,String> parseGetAccountInfoResponse(String p_responseStr) throws Exception
	{
		String METHOD_NAME="parseGetAccountInfoResponse";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_responseStr="+p_responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = p_responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = p_responseStr.indexOf("faultCode",indexStart);
				String faultCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = p_responseStr.indexOf("faultString",tempIndex);
				String faultString = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}

			indexStart = p_responseStr.indexOf("<member><name>responseCode");
			tempIndex = p_responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5CaI.RESULT_OK.split(",");
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= p_responseStr.indexOf("<member><name>accountValue1",indexEnd);
			tempIndex = p_responseStr.indexOf("accountValue1",indexStart);
			if(tempIndex>0)
			{
				String accountValue1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("accountValue1",accountValue1.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = p_responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>serviceClassCurrent",indexEnd);
			tempIndex = p_responseStr.indexOf("serviceClassCurrent",indexStart);
			if(tempIndex>0)
			{
				String serviceClassCurrent = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("serviceClassCurrent",serviceClassCurrent.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>supervisionExpiryDate",indexEnd);
			tempIndex = p_responseStr.indexOf("supervisionExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("supervisionExpiryDate",getDateString(supervisionExpiryDate.trim()));
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
			tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>languageIDCurrent",indexEnd);
			tempIndex = p_responseStr.indexOf("languageIDCurrent",indexStart);
			if(tempIndex>0)
			{
				String languageIDCurrent = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("languageIDCurrent",languageIDCurrent.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>temporaryBlockedFlag",indexEnd);
			tempIndex = p_responseStr.indexOf("temporaryBlockedFlag",indexStart);
			if(tempIndex>0)
			{
				String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length()+p_responseStr.indexOf("<boolean>",tempIndex),p_responseStr.indexOf("</boolean>",tempIndex)).trim();
				responseMap.put("temporaryBlockedFlag",getDateString(temporaryBlockedFlag));
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>serviceRemovalDate",indexEnd);
			tempIndex = p_responseStr.indexOf("serviceRemovalDate",indexStart);
			if(tempIndex>0)
			{
				String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("serviceRemovalDate",getDateString(serviceRemovalDate.trim()));
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			try
			{
				parseDedicatedAccountInfo(responseMap, p_responseStr);
			}
			catch (Exception e) 
			{
				_log.errorTrace(METHOD_NAME,e);
				_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			}
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME,"Exited responseMap="+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap<String,String> parseRechargeCreditResponse(String p_responseStr) throws Exception
	{
		String METHOD_NAME="parseRechargeCreditResponse";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_responseStr="+p_responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = p_responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = p_responseStr.indexOf("faultCode",indexStart);
				String faultCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = p_responseStr.indexOf("faultString",tempIndex);
				String faultString = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}
			indexStart = p_responseStr.indexOf("<member><name>responseCode");
			tempIndex = p_responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5CaI.RESULT_OK.split(",");
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= p_responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = p_responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>transactionAmount",indexEnd);
			tempIndex = p_responseStr.indexOf("transactionAmount",indexStart);
			if(tempIndex>0)
			{
				String transactionAmount = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("transactionAmount",transactionAmount.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
			tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>supervisionExpiryDate",indexEnd);
			tempIndex = p_responseStr.indexOf("supervisionExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("supervisionExpiryDate",getDateString(supervisionExpiryDate.trim()));
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			indexStart= p_responseStr.indexOf("<member><name>accountValue1",indexEnd);
			tempIndex = p_responseStr.indexOf("accountValue1",indexStart);
			if(tempIndex>0)
			{
				String accountValue1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("accountValue1",accountValue1.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			parseDedicatedAccountInfo(responseMap,p_responseStr);
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME,"Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private HashMap<String,String> parseImmediateDebitResponse(String p_responseStr) throws Exception
	{
		String METHOD_NAME="parseImmediateDebitResponse";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_responseStr="+p_responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = p_responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = p_responseStr.indexOf("faultCode",indexStart);
				String faultCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = p_responseStr.indexOf("faultString",tempIndex);
				String faultString = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}
			indexStart = p_responseStr.indexOf("<member><name>responseCode");
			tempIndex = p_responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5CaI.RESULT_OK.split(",");
				//if(!CS5CaI.RESULT_OK.equals(responseCode))
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= p_responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = p_responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME,"Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * Method to get the Transaction date and time with specified format.
	 * @return String
	 * @throws Exception
	 */
	private String getCS5TransDateTime() throws Exception
	{
		String METHOD_NAME="getCS5TransDateTime";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered");
		String transDateTime =null;
		try
		{
			Date now = new Date();
			transDateTime = _sdf.format(now)+_sign+_twoDigits.format(_hours)+ _twoDigits.format(_minutes);
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e = "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Exited transDateTime: "+transDateTime);
		}
		return transDateTime;
	}

	/**
	 * This method is used to construct the string that contains the start elements of request xml.
	 * @return String
	 */
	private String startRequestTag()
	{
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<params>");
		stringBuffer.append("<param>");
		stringBuffer.append("<value>");
		stringBuffer.append("<struct>");
		return stringBuffer.toString();
	}

	/**
	 * This method is used to construct the string that contains the end elements of request xml.
	 * @return String
	 */
	private String endRequestTag()
	{
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("</struct>");
		stringBuffer.append("</value>");
		stringBuffer.append("</param>");
		stringBuffer.append("</params>");
		return stringBuffer.toString();
	}

	/**
	 * This method is used to convert the date string into yyyyMMdd from yyyyMMdd'T'HH:mm:ss
	 * @param	String p_dateStr
	 * @return	String
	 */
	public String getDateString(String p_dateStr) throws Exception
	{
		String METHOD_NAME="getDateString";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_dateStr::"+p_dateStr);
		String dateStr="";
		try
		{
			dateStr = p_dateStr.substring(0,p_dateStr.indexOf("T")).trim();
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME,"Exited dateStr::"+dateStr);
		}
		return dateStr;
	}

	/**
	 * This method is used to generate the request for getting account Details.
	 * @param	HashMap	p_requestMap
	 * @return	String
	 * @throws	Exception
	 */
	private String generateGetAccountDetailRequest(HashMap p_requestMap) throws Exception
	{
		String METHOD_NAME="generateGetAccountDetailRequest";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		try
		{
			stringBuffer=new StringBuffer(1028);
			stringBuffer.append("<?xml version='1.0'?>");
			stringBuffer.append("<methodCall>");
			stringBuffer.append("<methodName>GetAccountDetails</methodName>");
			stringBuffer.append(startRequestTag());
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originNodeType</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)p_requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Set the optional parameter subscriberNumberNAI if present.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");
			//Set the subscriberNumber after adding or removing the prefix defined in the INFile.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(METHOD_NAME,"Exiting Request String:requestStr::"+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method is used to parse the response of GetAccountinDetails.
	 * @param	String p_responseStr
	 * @return	HashMap
	 */
	private HashMap<String,String> parseGetAccountDetailResponse(String p_responseStr) throws Exception
	{
		String METHOD_NAME="parseGetAccountDetailResponse";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_responseStr::"+p_responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = p_responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = p_responseStr.indexOf("faultCode",indexStart);
				String faultCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = p_responseStr.indexOf("faultString",tempIndex);
				String faultString = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}
			indexStart= p_responseStr.indexOf("<member><name>serviceOfferingID</name><value><i4>14</i4></value></member>",indexEnd);
			tempIndex = p_responseStr.indexOf("serviceOfferingActiveFlag",indexStart);
			if(tempIndex>0)
			{
				String serviceOfferingActiveFlag = p_responseStr.substring("<boolean>".length()+p_responseStr.indexOf("<boolean>",tempIndex),p_responseStr.indexOf("</boolean>",tempIndex)).trim();
				if(!BTSLUtil.isNullString(serviceOfferingActiveFlag))
					responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag.trim());
				else
					responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag);
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME,"Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method will be used for generating the credit or debit request for dedicated account.
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private String generateDedicatedAccountCDRequest(HashMap<String,String> p_requestMap) throws Exception
	{
		String METHOD_NAME="generateDedicatedAccountCDRequest";
		if(_log.isDebugEnabled())	_log.debug(METHOD_NAME,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		String interfaceAction;
		String transactionType="";
		String trnasactionCode="";
		try
		{
			stringBuffer = new StringBuffer(1028);
			//stringBuffer.append("<?xml version="+"\""+"1.0"+"\""+"encoding="+"\""+"ISO-8859-1"+"\""+" standalone="+"\""+"no"+"\""+"?>");
			stringBuffer.append("<?xml version='1.0'?>");
			stringBuffer.append("<methodCall>");
			stringBuffer.append("<methodName>UpdateBalanceAndDate</methodName>");
			stringBuffer.append(startRequestTag());
			//Set the origin originNodeType
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originNodeType</name>");
			stringBuffer.append("<value><string>etoptup</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName 
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			//Set the originTransactionID
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)p_requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Check the optional value of SubscriberNumberNAI if it is not null set it.	        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+(String)p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");	     
			//Set subscriberNumber and add or remove the MSISDN if required.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Set the transaction type
			interfaceAction=(String)(String)p_requestMap.get("INTERFACE_ACTION");
			{
				if(interfaceAction.equals(PretupsI.INTERFACE_CREDIT_ACTION))
				{
					transactionType="DistributedCreditDeposit";
					trnasactionCode="DistributedCreditDeposit";
				}
				else if(interfaceAction.equals(PretupsI.INTERFACE_DEBIT_ACTION))
				{
					transactionType="DistributedCreditDeduction";
					trnasactionCode="DistributedCreditDeduction";
				}
			}
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionType</name>");
			stringBuffer.append((new StringBuilder()).append("<value><string>").append(transactionType).append("</string></value>").toString());
			stringBuffer.append("</member>");
			//Set the transaction code
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionCode</name>");
			stringBuffer.append((new StringBuilder()).append("<value><string>").append(trnasactionCode).append("</string></value>").toString());
			stringBuffer.append("</member>");
			//Set the transaction_currency defined into INFile for transactionCurrency.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionCurrency</name>");
			stringBuffer.append("<value><string>"+p_requestMap.get("CURRENCY")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transaction_currency defined into INFile for transactionCurrency.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>dedicatedAccountUpdateInformation</name>");
			stringBuffer.append("<value><array><data><value><struct>");
			//Dedicated account information
			//Card group selector value will be dedicated account id in the request
			String dedicatedAccountID=(String)p_requestMap.get("DEDICATED_ACCOUNT_ID");
			//Set the dedicated account id
			stringBuffer.append("<member>");
			stringBuffer.append("<name>dedicatedAccountID</name>");
			stringBuffer.append("<value><int>"+dedicatedAccountID+"</int></value>");
			stringBuffer.append("</member>");
			//Set the transfer_amount to transactionAmount
			stringBuffer.append("<member>");
			stringBuffer.append("<name>adjustmentAmountRelative</name>");
			stringBuffer.append("<value><string>"+(String)p_requestMap.get("transfer_amount")+"</string></value>");
			stringBuffer.append("</member>");
			
			//set the mandatory value of dedicatedAccountUnitType.        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>dedicatedAccountUnitType</name>");
			stringBuffer.append("<value><i4>1</i4></value>");
			stringBuffer.append("</member>");
			
			//Set the expiry date to transactionAmount
			/*stringBuffer.append("<member>");
			stringBuffer.append("<name>expiryDate</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+p_requestMap.get("DEDI_EXPIRY_DATE_S")+"</dateTime.iso8601></value>");
			//stringBuffer.append("<value><dateTime.iso8601>"+getDedicatedAccExpiryDate(p_requestMap)+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");*/
			stringBuffer.append("</struct></value></data></array></value>");
			stringBuffer.append("</member>");
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();	       
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(METHOD_NAME,"Exiting  requestStr="+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method is used to parse the dedicated account credit/debit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private HashMap<String,String> parseDedicatedAccountCDRequest(String p_responseStr) throws Exception
	{
		String METHOD_NAME="parseDedicatedAccountCDRequest";
		if(_log.isDebugEnabled()) 
			_log.debug(METHOD_NAME,"Entered p_responseStr::"+p_responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = p_responseStr.indexOf("<member><name>responseCode");
			tempIndex = p_responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5CaI.RESULT_OK.split(",");
				//if(!CS5CaI.RESULT_OK.equals(responseCode))
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= p_responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = p_responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = p_responseStr.indexOf("</member>",indexStart);
			}
			parseDedicatedAccountInfo(responseMap,p_responseStr);
		}
		catch(Exception e)
		{
			_log.error(METHOD_NAME,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME,"Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the dedicated account credit/debit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private void parseDedicatedAccountInfo(HashMap<String,String> p_responseMap, String p_responseString) throws Exception
	{
		String METHOD_NAME="parseDedicatedAccountInfo";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered p_responseMap="+p_responseMap+" , p_responseString="+p_responseString);
		String dedicatedID="";
		String dedicatedValue="";
		String dedicatedExpiry="";
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		try
		{
			indexStart = p_responseString.indexOf("<member><name>dedicatedAccountInformation</name>");
			if(indexStart==-1)
			    indexStart = p_responseString.indexOf("<member><name>dedicatedAccountChangeInformation</name>");
			if(indexStart>0)
			{
                    		String tmp_responseString=p_responseString.substring(indexStart,p_responseString.length());
				tempIndex=("<value><array><data><value><struct>").length();
				//String dedicatedAccString = p_responseString.substring(p_responseString.indexOf("<value><array><data><value><struct>")+tempIndex,p_responseString.indexOf("</data></array></value></member>"));
				//String dedicatedAccString =p_responseString.substring(p_responseString.indexOf("<value><array><data><value><struct>")+tempIndex,p_responseString.indexOf("</struct></value></data></array></value>"));
                    		String dedicatedAccString =tmp_responseString.substring(tmp_responseString.indexOf("<value><array><data><value><struct>")+tempIndex,tmp_responseString.indexOf("</struct></value></data></array></value>"));
				String[] dedicatedAccInfo=dedicatedAccString.split("<struct>");
				for(int i=0; i<dedicatedAccInfo.length; i++)
				{
					indexStart= dedicatedAccInfo[i].indexOf("<member><name>dedicatedAccountID",indexEnd);
					tempIndex = dedicatedAccInfo[i].indexOf("dedicatedAccountID",indexStart);
					if(tempIndex>0)
					{
						String accountID = dedicatedAccInfo[i].substring("<i4>".length()+dedicatedAccInfo[i].indexOf("<i4>",tempIndex),dedicatedAccInfo[i].indexOf("</i4>",tempIndex)).trim();
						indexEnd = dedicatedAccInfo[i].indexOf("</member>",indexStart);
						if(InterfaceUtil.isNullString(dedicatedID))
							dedicatedID=accountID;
						else
							dedicatedID+="|"+accountID;
					}
					indexStart= dedicatedAccInfo[i].indexOf("<member><name>dedicatedAccountValue1",indexEnd);
					tempIndex = dedicatedAccInfo[i].indexOf("dedicatedAccountValue1",indexStart);
					if(tempIndex>0)
					{
						String accountValue = dedicatedAccInfo[i].substring("<string>".length()+dedicatedAccInfo[i].indexOf("<string>",tempIndex),dedicatedAccInfo[i].indexOf("</string>",tempIndex)).trim();
						indexEnd = dedicatedAccInfo[i].indexOf("</member>",indexStart);
						if(InterfaceUtil.isNullString(dedicatedValue))
							dedicatedValue=accountValue;
						else
							dedicatedValue+="|"+accountValue;
					}
					indexStart= dedicatedAccInfo[i].indexOf("<member><name>expiryDate",indexEnd);
					tempIndex = dedicatedAccInfo[i].indexOf("expiryDate",indexStart);
					if(tempIndex>0)
					{
						String expiryDate = dedicatedAccInfo[i].substring("<dateTime.iso8601>".length()+dedicatedAccInfo[i].indexOf("<dateTime.iso8601>",tempIndex),dedicatedAccInfo[i].indexOf("</dateTime.iso8601>",tempIndex)).trim();
						indexEnd = dedicatedAccInfo[i].indexOf("</member>",indexStart);
						if(InterfaceUtil.isNullString(dedicatedExpiry))
							dedicatedExpiry=expiryDate;
						else
							dedicatedExpiry+="|"+expiryDate;
					}
				}
				p_responseMap.put("DEDICATED_ACC_ID", dedicatedID);
				p_responseMap.put("DEDICATED_ACC_VALUE", dedicatedValue);
				p_responseMap.put("DEDICATED_ACC_EXPIRY", dedicatedExpiry);
			}
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME,"Exited with dedicatedID="+dedicatedID+" dedicatedValue="+dedicatedValue+" dedicatedExpiry="+dedicatedExpiry);
		}
	}

	/**
	 * This method is used to parse the dedicated account credit/debit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private String getDedicatedAccExpiryDate(HashMap<String,String> p_requestMap) throws Exception
	{
		String METHOD_NAME="getDedicatedAccExpiryDate";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Entered p_requestMap="+p_requestMap);
		String dedicatedExpiry=null;
		String cardGrpSelector;
		String split="%7C";
		try
		{
			cardGrpSelector=(String)p_requestMap.get("CARD_GROUP_SELECTOR");
			if(!InterfaceUtil.isNullString((String)p_requestMap.get("DEDICATED_ACC_ID")) && "S".equals((String)p_requestMap.get("USER_TYPE")))
			{
				String[] dedAccIDArray=((String)p_requestMap.get("DEDICATED_ACC_ID")).split(split);
				String[] dedAccExpiryArray=((String)p_requestMap.get("DEDICATED_ACC_EXPIRY")).split(split);
				for(int i=0; i<dedAccIDArray.length; i++)
				{
					if(!cardGrpSelector.equals(dedAccIDArray[i]))
						continue;
					else
					{
						dedicatedExpiry=dedAccExpiryArray[i];
						dedicatedExpiry=dedicatedExpiry.replace("%3A",":");
						dedicatedExpiry=dedicatedExpiry.replace("%2B","+");
						p_requestMap.put("DEDI_EXPIRY_DATE_S", dedicatedExpiry);
						break;
					}
				}
			}
			else if("R".equals((String)p_requestMap.get("USER_TYPE")))
			{
				dedicatedExpiry=(String)p_requestMap.get("DEDI_EXPIRY_DATE_S");
				dedicatedExpiry=dedicatedExpiry.replace("%3A",":");
				dedicatedExpiry=dedicatedExpiry.replace("%2B","+");
			}
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,"Exited with dedicatedExpiry="+dedicatedExpiry);
		}
		return dedicatedExpiry;
	}
}

