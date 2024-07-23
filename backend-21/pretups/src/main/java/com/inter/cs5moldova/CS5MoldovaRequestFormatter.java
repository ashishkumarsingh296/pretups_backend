package com.inter.cs5moldova;

/**
 * @(#)CS5MoldovaRequestFormatter.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Zeeshan Aleem        Jul 08, 2017		    Initial Creation
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

public class CS5MoldovaRequestFormatter 
{
	private static Log log = LogFactory.getLog(CS5MoldovaRequestFormatter.class);
	private SimpleDateFormat sdf = null;
	private TimeZone timeZone = null;
	private String transDateFormat="yyyyMMdd'T'HH:mm:ss";//Defines the Date and time format of CS5MoldovaIN.
	private DecimalFormat twoDigits = null;
	private int offset;
	private String sign="+";
	private int hours;
	private int minutes;
	private static final String METHOD_START = "Entered";

	public CS5MoldovaRequestFormatter() throws Exception
	{
		final String methodName = "CS5MoldovaRequestFormatter";
		if(log.isDebugEnabled()) log.debug(methodName,METHOD_START);
		try
		{
			sdf = new SimpleDateFormat(transDateFormat);
			timeZone = TimeZone.getDefault();
			sdf.setTimeZone(timeZone);
			twoDigits = new DecimalFormat("00");
			offset = sdf.getTimeZone().getOffset(new Date().getTime());
			if (offset < 0)
			{
				offset = -offset;
				sign = "-";
			}
			hours = offset / 3600000;
			minutes = (offset - hours * 3600000) / 60000;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName,"Exception :: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug(methodName,"Exited");
		}
	}
	/**
	 * This method is used to parse the response string based on the type of Action.
	 * @param	int p_action
	 * @param	HashMap p_map
	 * @return	String.
	 * @throws	Exception
	 */
	protected String generateRequest(int action, HashMap<String,String> map) throws Exception 
	{
		final String methodName = "generateRequest";
		if(log.isDebugEnabled())log.debug(methodName,"Entered p_action="+action+" map="+map);
		String str=null;
		map.put("action",String.valueOf(action));
		try
		{
			switch(action)
			{
			case CS5MoldovaI.ACTION_ACCOUNT_INFO:
			{
				str=generateGetAccountInfoRequest(map);
				break;	
			}
			case CS5MoldovaI.ACTION_ACCOUNT_DETAILS: 
			{
				str=generateGetAccountDetailRequest(map);
				break;	
			}
			case CS5MoldovaI.ACTION_RECHARGE_CREDIT: 
			{
				str=generateRechargeCreditRequest(map);
				break;	
			}
			case CS5MoldovaI.ACTION_IMMEDIATE_DEBIT: 
			{
				str=generateImmediateDebitRequest(map);
				break;	
			}
			case CS5MoldovaI.ACTION_DEDICATED_ACCOUNT_CD: 
			{
				str=generateDedicatedAccountCDRequest(map);
				break;	
			}
			default:
				break;
			}
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e ::"+e.getMessage());
			throw e;
		} 
		finally
		{
			if(log.isDebugEnabled())log.debug(methodName,"Exited Request String: str="+str);
		}
		return str;
	}

	/**
	 * This method is used to parse the response.
	 * @param	int p_action
	 * @param	String responseStr
	 * @return	HashMap
	 * @throws	Exception
	 */
	public HashMap<String,String> parseResponse(int action,String responseStr,HashMap<String,String> responseMap) throws Exception
	{
		final String methodName = "parseResponse";
		
		if(log.isDebugEnabled())
			log.debug(methodName,"Entered p_action="+action+" responseStr="+responseStr);
		HashMap<String,String> map=null;
		try
		{
			switch(action)
			{
			case CS5MoldovaI.ACTION_ACCOUNT_INFO: 
			{
				map=parseGetAccountInfoResponse(responseStr);
				break;	
			}
			case CS5MoldovaI.ACTION_ACCOUNT_DETAILS: 
			{
				map=parseGetAccountDetailResponse(responseStr);
				map.putAll(responseMap);
				break;	
			}
			case CS5MoldovaI.ACTION_RECHARGE_CREDIT: 
			{
				map=parseRechargeCreditResponse(responseStr);
				break;	
			}
			case CS5MoldovaI.ACTION_IMMEDIATE_DEBIT: 
			{
				map=parseImmediateDebitResponse(responseStr);
				break;	
			}
			case CS5MoldovaI.ACTION_DEDICATED_ACCOUNT_CD: 
			{
				map=parseDedicatedAccountCDRequest(responseStr);
				break;	
			}
			}
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception :"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled())log.debug(methodName,"Exiting map::"+map);
		}
		return map;	
	}

	/**
	 * This method is used to generate the request for getting account information.
	 * @param	HashMap	requestMap
	 * @return	String
	 * @throws	Exception
	 */
	private String generateGetAccountInfoRequest(HashMap<String,String> requestMap) throws Exception
	{
		final String methodName = "generateGetAccountInfoRequest";
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered requestMap::"+requestMap);
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
			stringBuffer.append("<value><string>"+requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Set the optional parameter subscriberNumberNAI if present.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");
			//Set the subscriberNumber after adding or removing the prefix defined in the INFile.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) requestMap.get("INTERFACE_ID"),(String)requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled())
				log.debug(methodName,"Exiting Request String:requestStr::"+requestStr);
		}
		return requestStr;
	}

	/**
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private String generateRechargeCreditRequest(HashMap requestMap) throws Exception
	{
		final String methodName = "generateRechargeCreditRequest";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered requestMap::"+requestMap);
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
			stringBuffer.append("<value><string>"+requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName 
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Check the optional value of SubscriberNumberNAI if it is not null set it.	        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");	        
			//Set subscriberNumber and add or remove the msisdn if required.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) requestMap.get("INTERFACE_ID"),(String)requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			if(!InterfaceUtil.isNullString((String)requestMap.get("ExternalData1")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData1</name>");
				stringBuffer.append("<value><string>"+requestMap.get("ExternalData1")+"</string></value>");
				stringBuffer.append("</member>");
			}
			//Set the optional value of ExternalData2, if required.
			if(!InterfaceUtil.isNullString((String)requestMap.get("ExternalData2")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData2</name>");
				stringBuffer.append("<value><string>"+requestMap.get("ExternalData2")+"</string></value>");
				stringBuffer.append("</member>");
			}
			//Set the transfer_amount to transactionAmount
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionAmount</name>");
			stringBuffer.append("<value><string>"+requestMap.get("transfer_amount")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transaction_currency defined into INFile for transactionCurrency.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionCurrency</name>");
			stringBuffer.append("<value><string>"+requestMap.get("CURRENCY")+"</string></value>");
			stringBuffer.append("</member>");
			if(!InterfaceUtil.isNullString((String)requestMap.get("TRANSACTION_CODE_RF")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionCode</name>");
				stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)requestMap.get("TRANSACTION_CODE_RF")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)requestMap.get("TRANSACTION_TYPE_RF")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionType</name>");
				stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)requestMap.get("TRANSACTION_TYPE_RF")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}		
			//Set the card group for paymentProfileID
			stringBuffer.append("<member>");
			stringBuffer.append("<name>refillProfileID</name>");
			stringBuffer.append("<value><string>"+requestMap.get("CARD_GROUP")+"</string></value>");
			stringBuffer.append("</member>");
			
			if(!InterfaceUtil.isNullString((String)requestMap.get("REQUEST_REFILL_ACCOUNT_BEFORE_FLAG")))
			{
			stringBuffer.append("<member>");
			stringBuffer.append("<name>requestRefillAccountBeforeFlag</name>");
			stringBuffer.append("<value><string>"+requestMap.get("REQUEST_REFILL_ACCOUNT_BEFORE_FLAG")+"</string></value>");
			stringBuffer.append("</member>");
			}
			
			if(!InterfaceUtil.isNullString((String)requestMap.get("REQUEST_REFILL_ACCOUNT_AFTER_FLAG")))
			{
			stringBuffer.append("<member>");
			stringBuffer.append("<name>requestRefillAccountAfterFlag</name>");
			stringBuffer.append("<value><string>"+requestMap.get("REQUEST_REFILL_ACCOUNT_AFTER_FLAG")+"</string></value>");
			stringBuffer.append("</member>");
			}
			
			if(!InterfaceUtil.isNullString((String)requestMap.get("REQUEST_REFILL_DETAILS_FLAG")))
			{
			stringBuffer.append("<member>");
			stringBuffer.append("<name>requestRefillDetailsFlag</name>");
			stringBuffer.append("<value><string>"+requestMap.get("REQUEST_REFILL_DETAILS_FLAG")+"</string></value>");
			stringBuffer.append("</member>");
			}		
			
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();       
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e: "+e);
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled())log.debug(methodName,"Exiting Request requestStr::"+requestStr);
		}
		return requestStr;
	}

	/**
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private String generateImmediateDebitRequest(HashMap<String,String> requestMap) throws Exception
	{
		final String methodName = "generateImmediateDebitRequest";
		if(log.isDebugEnabled())
			log.debug(methodName,"Entered requestMap::"+requestMap);
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
			stringBuffer.append("<value><string>"+requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName 
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			//Set the originTransactionID
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Check the optional value of SubscriberNumberNAI if it is not null set it.	        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");	        
			//Set subscriberNumber and add or remove the msisdn if required.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) requestMap.get("INTERFACE_ID"),(String)requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transfer_amount to transactionAmount
			stringBuffer.append("<member>");
			stringBuffer.append("<name>adjustmentAmountRelative</name>");
			stringBuffer.append("<value><string>"+requestMap.get("transfer_amount")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transaction_currency defined into INFile for transactionCurrency.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>transactionCurrency</name>");
			stringBuffer.append("<value><string>"+requestMap.get("CURRENCY")+"</string></value>");
			stringBuffer.append("</member>");
			String validityDays = (String)requestMap.get("VALIDITY_DAYS");
			if( (!InterfaceUtil.isNullString(validityDays)) && !"0".equals(validityDays))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>supervisionExpiryDateRelative</name>");
				stringBuffer.append("<value><i4>"+validityDays+"</i4></value>");
				stringBuffer.append("</member>");
			}
			String graceDays = (String)requestMap.get("GRACE_DAYS");
			if(!InterfaceUtil.isNullString(graceDays) && !"0".equals(graceDays))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>serviceFeeExpiryDateRelative</name>");
				stringBuffer.append("<value><i4>"+(String)requestMap.get("GRACE_DAYS")+"</i4></value>");
				stringBuffer.append("</member>");
			}        
			if(!InterfaceUtil.isNullString((String)requestMap.get("ExternalData1")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData1</name>");
				stringBuffer.append("<value><string>"+(String)requestMap.get("ExternalData1")+"</string></value>");
				stringBuffer.append("</member>");
			}
			//Set the optional value of ExternalData2, if required.
			if(!InterfaceUtil.isNullString((String)requestMap.get("ExternalData2")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>externalData2</name>");
				stringBuffer.append("<value><string>"+(String)requestMap.get("ExternalData2")+"</string></value>");
				stringBuffer.append("</member>");
			}
			//set the values for transaction code and transaction type 
			if(!InterfaceUtil.isNullString((String)requestMap.get("TRANSACTION_CODE_CR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionCode</name>");
				String reqService=(String)requestMap.get("REQ_SERVICE");
				System.out.println("REQ_SERVICE::"+reqService);
			/*	if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
					stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
				else
			*/
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)requestMap.get("TRANSACTION_CODE_CR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)requestMap.get("TRANSACTION_TYPE_CR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionType</name>");
				String reqService=(String)requestMap.get("REQ_SERVICE");
				
                                System.out.println("REQ_SERVICE::"+reqService);
                                /*if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
                                        stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
                                else
				*/
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)requestMap.get("TRANSACTION_TYPE_CR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)requestMap.get("TRANSACTION_CODE_DR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionCode</name>");
				String reqService=(String)requestMap.get("REQ_SERVICE");
				 /*if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
				 	stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
				else
				*/
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)requestMap.get("TRANSACTION_CODE_DR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			if(!InterfaceUtil.isNullString((String)requestMap.get("TRANSACTION_TYPE_DR")))
			{
				stringBuffer.append("<member>");
				stringBuffer.append("<name>transactionType</name>");
				String reqService=(String)requestMap.get("REQ_SERVICE");
				/*if(reqService.equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))
                                        stringBuffer.append((new StringBuilder()).append("<value><string>").append("SCD").append("</string></value>").toString());
                                else
				*/
					stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)requestMap.get("TRANSACTION_TYPE_DR")).append("</string></value>").toString());
				stringBuffer.append("</member>");
			}
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();	       
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled())
				log.debug(methodName,"Exiting  requestStr="+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method is used to parse the response of GetAccountinformation.
	 * @param	String responseStr
	 * @return	HashMap
	 */
	private HashMap<String,String> parseGetAccountInfoResponse(String responseStr) throws Exception
	{
		final String methodName = "parseGetAccountInfoResponse";
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered responseStr="+responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = responseStr.indexOf("faultCode",indexStart);
				String faultCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</i4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = responseStr.indexOf("faultString",tempIndex);
				String faultString = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}

			indexStart = responseStr.indexOf("<member><name>responseCode");
			tempIndex = responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5MoldovaI.RESULT_OK.split(",");
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= responseStr.indexOf("<member><name>accountValue1",indexEnd);
			tempIndex = responseStr.indexOf("accountValue1",indexStart);
			if(tempIndex>0)
			{
				String accountValue1 = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("accountValue1",accountValue1.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>serviceClassCurrent",indexEnd);
			tempIndex = responseStr.indexOf("serviceClassCurrent",indexStart);
			if(tempIndex>0)
			{
				String serviceClassCurrent = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("serviceClassCurrent",serviceClassCurrent.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>supervisionExpiryDate",indexEnd);
			tempIndex = responseStr.indexOf("supervisionExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String supervisionExpiryDate = responseStr.substring("<dateTime.iso8601>".length()+responseStr.indexOf("<dateTime.iso8601>",tempIndex),responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("supervisionExpiryDate",getDateString(supervisionExpiryDate.trim()));
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
			tempIndex = responseStr.indexOf("serviceFeeExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String serviceFeeExpiryDate = responseStr.substring("<dateTime.iso8601>".length()+responseStr.indexOf("<dateTime.iso8601>",tempIndex),responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>languageIDCurrent",indexEnd);
			tempIndex = responseStr.indexOf("languageIDCurrent",indexStart);
			if(tempIndex>0)
			{
				String languageIDCurrent = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("languageIDCurrent",languageIDCurrent.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			/*
			indexStart= responseStr.indexOf("<member><name>temporaryBlockedFlag",indexEnd);
			tempIndex = responseStr.indexOf("temporaryBlockedFlag",indexStart);
			if(tempIndex>0)
			{
				String temporaryBlockedFlag = responseStr.substring("<boolean>".length()+responseStr.indexOf("<boolean>",tempIndex),responseStr.indexOf("</boolean>",tempIndex)).trim();
				responseMap.put("temporaryBlockedFlag",getDateString(temporaryBlockedFlag));
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			*/

			try
			{
				parseDedicatedAccountInfo(responseMap, responseStr);
			}
			catch (Exception e) 
			{			
				log.errorTrace(methodName, e);
				log.error(methodName,"Exception e::"+e.getMessage());
			}
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited responseMap="+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap<String,String> parseRechargeCreditResponse(String responseStr) throws Exception
	{
		final String methodName = "parseRechargeCreditResponse";
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered responseStr="+responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = responseStr.indexOf("faultCode",indexStart);
				String faultCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = responseStr.indexOf("faultString",tempIndex);
				String faultString = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}
			indexStart = responseStr.indexOf("<member><name>responseCode");
			tempIndex = responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5MoldovaI.RESULT_OK.split(",");
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>transactionAmount",indexEnd);
			tempIndex = responseStr.indexOf("transactionAmount",indexStart);
			if(tempIndex>0)
			{
				String transactionAmount = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("transactionAmount",transactionAmount.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
			tempIndex = responseStr.indexOf("serviceFeeExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String serviceFeeExpiryDate = responseStr.substring("<dateTime.iso8601>".length()+responseStr.indexOf("<dateTime.iso8601>",tempIndex),responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>supervisionExpiryDate",indexEnd);
			tempIndex = responseStr.indexOf("supervisionExpiryDate",indexStart);
			if(tempIndex>0)
			{
				String supervisionExpiryDate = responseStr.substring("<dateTime.iso8601>".length()+responseStr.indexOf("<dateTime.iso8601>",tempIndex),responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
				responseMap.put("supervisionExpiryDate",getDateString(supervisionExpiryDate.trim()));
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
			indexStart= responseStr.indexOf("<member><name>accountValue1",indexEnd);
			tempIndex = responseStr.indexOf("accountValue1",indexStart);
			if(tempIndex>0)
			{
				String accountValue1 = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("accountValue1",accountValue1.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private HashMap<String,String> parseImmediateDebitResponse(String responseStr) throws Exception
	{
		final String methodName = "parseImmediateDebitResponse";
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered responseStr="+responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = responseStr.indexOf("faultCode",indexStart);
				String faultCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = responseStr.indexOf("faultString",tempIndex);
				String faultString = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}
			indexStart = responseStr.indexOf("<member><name>responseCode");
			tempIndex = responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5MoldovaI.RESULT_OK.split(",");
				//if(!CS5MoldovaI.RESULT_OK.equals(responseCode))
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited responseMap::"+responseMap);
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
		final String methodName = "getCS5TransDateTime";
		if(log.isDebugEnabled()) 
			log.debug(methodName,METHOD_START);
		String transDateTime =null;
		try
		{
			Date now = new Date();
			transDateTime = sdf.format(now)+sign+twoDigits.format(hours)+ twoDigits.format(minutes);
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e = "+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug(methodName,"Exited transDateTime: "+transDateTime);
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
	public String getDateString(String dateStrParam) throws Exception
	{
		final String methodName = "getDateString"; 
		
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered p_dateStr::"+dateStrParam);
		String dateStr="";
		try
		{
			dateStr = dateStrParam.substring(0,dateStrParam.indexOf("T")).trim();
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited dateStr::"+dateStr);
		}
		return dateStr;
	}

	/**
	 * This method is used to generate the request for getting account Details.
	 * @param	HashMap	requestMap
	 * @return	String
	 * @throws	Exception
	 */
	private String generateGetAccountDetailRequest(HashMap requestMap) throws Exception
	{
		final String methodName = "generateGetAccountDetailRequest";
		
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered requestMap::"+requestMap);
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
			stringBuffer.append("<value><string>"+requestMap.get("NODE_TYPE")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originHostName
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originHostName</name>");
			stringBuffer.append("<value><string>"+requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Set the optional parameter subscriberNumberNAI if present.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");
			//Set the subscriberNumber after adding or removing the prefix defined in the INFile.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) requestMap.get("INTERFACE_ID"),(String)requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled())
				log.debug(methodName,"Exiting Request String:requestStr::"+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method is used to parse the response of GetAccountinDetails.
	 * @param	String responseStr
	 * @return	HashMap
	 */
	private HashMap<String,String> parseGetAccountDetailResponse(String responseStr) throws Exception
	{
		final String methodName = "parseGetAccountDetailResponse";
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered responseStr::"+responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = responseStr.indexOf("<fault>");
			if(indexStart>0)
			{
				tempIndex = responseStr.indexOf("faultCode",indexStart);
				String faultCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</4>",tempIndex));
				responseMap.put("faultCode",faultCode.trim());
				tempIndex = responseStr.indexOf("faultString",tempIndex);
				String faultString = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex));
				responseMap.put("faultString",faultString.trim());	            
				return responseMap;
			}
			indexStart= responseStr.indexOf("<member><name>serviceOfferingID</name><value><i4>14</i4></value></member>",indexEnd);
			tempIndex = responseStr.indexOf("serviceOfferingActiveFlag",indexStart);
			if(tempIndex>0)
			{
				String serviceOfferingActiveFlag = responseStr.substring("<boolean>".length()+responseStr.indexOf("<boolean>",tempIndex),responseStr.indexOf("</boolean>",tempIndex)).trim();
				if(!BTSLUtil.isNullString(serviceOfferingActiveFlag))
					responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag.trim());
				else
					responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag);
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method will be used for generating the credit or debit request for dedicated account.
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private String generateDedicatedAccountCDRequest(HashMap<String,String> requestMap) throws Exception
	{
		final String methodName = "generateDedicatedAccountCDRequest";
		if(log.isDebugEnabled())
			log.debug(methodName,"Entered requestMap::"+requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		String interfaceAction;
		String transactionType="";
		String trnasactionCode="";
		try
		{
			stringBuffer = new StringBuffer(1028);
			stringBuffer.append("<?xml version="+"\""+"1.0"+"\""+"encoding="+"\""+"ISO-8859-1"+"\""+" standalone="+"\""+"no"+"\""+"?>");
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
			stringBuffer.append("<value><string>"+requestMap.get("HOST_NAME")+"</string></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("<member>");
			//Set the originTransactionID
			stringBuffer.append("<name>originTransactionID</name>");
			stringBuffer.append("<value><string>"+(String)requestMap.get("IN_RECON_ID")+"</string></value>");
			stringBuffer.append("</member>");
			//Check the optional value of SubscriberNumberNAI if it is not null set it.	        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumberNAI</name>");
			stringBuffer.append("<value><i4>"+(String)requestMap.get("SubscriberNumberNAI")+"</i4></value>");
			stringBuffer.append("</member>");	     
			//Set subscriberNumber and add or remove the MSISDN if required.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>subscriberNumber</name>");
			stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) requestMap.get("INTERFACE_ID"),(String)requestMap.get("MSISDN"))+"</string></value>");
			stringBuffer.append("</member>");
			//Set the originTimeStamp
			stringBuffer.append("<member>");
			stringBuffer.append("<name>originTimeStamp</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getCS5TransDateTime()+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			//Set the transaction type
			interfaceAction=(String)(String)requestMap.get("INTERFACE_ACTION");
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
			stringBuffer.append("<value><string>"+requestMap.get("CURRENCY")+"</string></value>");
			stringBuffer.append("</member>");
			//Set the transaction_currency defined into INFile for transactionCurrency.
			stringBuffer.append("<member>");
			stringBuffer.append("<name>dedicatedAccountUpdateInformation</name>");
			stringBuffer.append("<value><array><data><value><struct>");
			//Dedicated account information
			//Card group selector value will be dedicated account id in the request
			String dedicatedAccountID=(String)requestMap.get("CARD_GROUP_SELECTOR");
			//Set the dedicated account id
			stringBuffer.append("<member>");
			stringBuffer.append("<name>dedicatedAccountID</name>");
			stringBuffer.append("<value><int>"+dedicatedAccountID+"</int></value>");
			stringBuffer.append("</member>");
			//Set the transfer_amount to transactionAmount
			stringBuffer.append("<member>");
			stringBuffer.append("<name>adjustmentAmountRelative</name>");
			stringBuffer.append("<value><string>"+(String)requestMap.get("transfer_amount")+"</string></value>");
			stringBuffer.append("</member>");
			
			//set the mandatory value of dedicatedAccountUnitType.        
			stringBuffer.append("<member>");
			stringBuffer.append("<name>dedicatedAccountUnitType</name>");
			stringBuffer.append("<value><i4>1</i4></value>");
			stringBuffer.append("</member>");
			
			//Set the expiry date to transactionAmount
			stringBuffer.append("<member>");
			stringBuffer.append("<name>expiryDate</name>");
			stringBuffer.append("<value><dateTime.iso8601>"+getDedicatedAccExpiryDate(requestMap)+"</dateTime.iso8601></value>");
			stringBuffer.append("</member>");
			stringBuffer.append("</struct></value></data></array></value>");
			stringBuffer.append("</member>");
			stringBuffer.append(endRequestTag());
			stringBuffer.append("</methodCall>");
			requestStr = stringBuffer.toString();	       
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled())
				log.debug(methodName,"Exiting  requestStr="+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method is used to parse the dedicated account credit/debit response. 
	 * @param responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private HashMap<String,String> parseDedicatedAccountCDRequest(String responseStr) throws Exception
	{
		final String methodName = "parseDedicatedAccountCDRequest";
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered responseStr::"+responseStr);
		HashMap<String,String> responseMap = null;
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		String responseCode = null;
		try
		{
			responseMap = new HashMap<String,String>();
			indexStart = responseStr.indexOf("<member><name>responseCode");
			tempIndex = responseStr.indexOf("responseCode",indexStart);
			if(tempIndex>0)
			{
				responseCode = responseStr.substring("<i4>".length()+responseStr.indexOf("<i4>",tempIndex),responseStr.indexOf("</i4>",tempIndex)).trim();
				responseMap.put("responseCode",responseCode.trim());
				Object[] successList=CS5MoldovaI.RESULT_OK.split(",");
				//if(!CS5MoldovaI.RESULT_OK.equals(responseCode))
				if(!Arrays.asList(successList).contains(responseCode))
					return responseMap;
			}
			indexStart= responseStr.indexOf("<member><name>originTransactionID",indexEnd);
			tempIndex = responseStr.indexOf("originTransactionID",indexStart);
			if(tempIndex>0)
			{
				String originTransactionID = responseStr.substring("<string>".length()+responseStr.indexOf("<string>",tempIndex),responseStr.indexOf("</string>",tempIndex)).trim();
				responseMap.put("originTransactionID",originTransactionID.trim());
				indexEnd = responseStr.indexOf("</member>",indexStart);
			}
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the dedicated account credit/debit response. 
	 * @param responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private void parseDedicatedAccountInfo(HashMap<String,String> responseMap, String responseString) throws Exception
	{
		final String methodName = "parseDedicatedAccountInfo";
		if(log.isDebugEnabled()) 
			log.debug(methodName,METHOD_START);
		String dedicatedID="";
		String dedicatedValue="";
		String dedicatedExpiry="";
		int indexStart=0;
		int indexEnd=0;
		int tempIndex=0;
		try
		{
			indexStart = responseString.indexOf("<member><name>dedicatedAccountInformation</name>");
			if(indexStart>0)
			{
				tempIndex=("<value><array><data><value><struct>").length();
				String dedicatedAccString = responseString.substring(responseString.indexOf("<value><array><data><value><struct>")+tempIndex,responseString.indexOf("</struct></value></data></array></value></member>"));
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
				responseMap.put("DEDICATED_ACC_ID", dedicatedID);
				responseMap.put("DEDICATED_ACC_VALUE", dedicatedValue);
				responseMap.put("DEDICATED_ACC_EXPIRY", dedicatedExpiry);
			}
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited with dedicatedID="+dedicatedID+" dedicatedValue="+dedicatedValue+" dedicatedExpiry="+dedicatedExpiry);
		}
	}

	/**
	 * This method is used to parse the dedicated account credit/debit response. 
	 * @param responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private String getDedicatedAccExpiryDate(HashMap<String,String> requestMap) throws Exception
	{
		final String methodName = "getDedicatedAccExpiryDate";
		if(log.isDebugEnabled()) log.debug(methodName,METHOD_START);
		String dedicatedExpiry=null;
		String cardGrpSelector;
		String split="%7C";
		try
		{
			cardGrpSelector=(String)requestMap.get("CARD_GROUP_SELECTOR");
			if(!InterfaceUtil.isNullString((String)requestMap.get("DEDICATED_ACC_ID")) && "S".equals((String)requestMap.get("USER_TYPE")))
			{
				String[] dedAccIDArray=((String)requestMap.get("DEDICATED_ACC_ID")).split(split);
				String[] dedAccExpiryArray=((String)requestMap.get("DEDICATED_ACC_EXPIRY")).split(split);
				for(int i=0; i<dedAccIDArray.length; i++)
				{
					if(!cardGrpSelector.equals(dedAccIDArray[i]))
						continue;
					else
					{
						dedicatedExpiry=dedAccExpiryArray[i];
						dedicatedExpiry=dedicatedExpiry.replace("%3A",":");
						dedicatedExpiry=dedicatedExpiry.replace("%2B","+");
						requestMap.put("DEDI_EXPIRY_DATE_S", dedicatedExpiry);
						break;
					}
				}
			}
			else if("R".equals((String)requestMap.get("USER_TYPE")))
			{
				dedicatedExpiry=(String)requestMap.get("DEDI_EXPIRY_DATE_S");
				dedicatedExpiry=dedicatedExpiry.replace("%3A",":");
				dedicatedExpiry=dedicatedExpiry.replace("%2B","+");
			}
		}
		catch(Exception e)
		{
			log.errorTrace(methodName, e);
			throw e;
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited with dedicatedExpiry="+dedicatedExpiry);
		}
		return dedicatedExpiry;
	}
}

