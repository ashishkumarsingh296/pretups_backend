
package com.inter.cs3obfaso;

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


public class CS3OBFasoRequestFormatter 
{
    public static Log _log = LogFactory.getLog(CS3OBFasoRequestFormatter.class);
    private SimpleDateFormat _sdf = null;
    private TimeZone _timeZone = null;
    private String _transDateFormat="yyyyMMdd'T'HH:mm:ss";//Defines the Date and time format of CS3NigeriaIN.
    private DecimalFormat _twoDigits = null;
    private int _offset;
    private String _sign="+";
	private int _hours;
	private int _minutes;
	private static int _counter = 0;
	private static long _prevReqTime=0;
	
    public CS3OBFasoRequestFormatter() throws Exception
    {
    	final String methodName="CS3OBFasoRequestFormatter[constructor]";
        if(_log.isDebugEnabled()) _log.debug(methodName,"Entered");
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
             _log.errorTrace(methodName,e);
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug(methodName,"Exited");
        }//end of finally
    }//end of CS3OBFasoRequestFormatter[constructor]
     

 
    /**
     * This method is used to parse the response string based on the type of Action.
     * @param	int p_action
     * @param	HashMap p_map
     * @return	String.
     * @throws	Exception
     */
	protected String generateRequest(int p_action, HashMap p_map) throws Exception 
	{
		final String methodName="generateRequest";
       if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_action::"+p_action+" map::"+p_map);
		String str=null;
		p_map.put("action",String.valueOf(p_action));
		try
		{
			switch(p_action)
			{
				case CS3OBFaso.ACTION_ACCOUNT_INFO: 
				{
					str=generateGetAccountInfoRequest(p_map);
					break;	
				}
				case CS3OBFaso.ACTION_ACCOUNT_DETAILS: 
				{
					str=generateGetAccountDetailRequest(p_map);
					break;	
				}
				case CS3OBFaso.ACTION_RECHARGE_CREDIT: 
				{
				    str=generateRechargeCreditRequest(p_map);
					break;	
				}
				case CS3OBFaso.ACTION_IMMEDIATE_DEBIT: 
				{
					str=generateImmediateDebitRequest(p_map);
					break;	
				}
				case CS3OBFaso.ACTION_GET_OFFERS: 
				{
					str=generateGetOffersRequest(p_map);
					break;	
				}
				
			}//end of switch block
		}//end of try block
		catch(Exception e)
		{
			_log.error(methodName,"Exception e ::"+e.getMessage());
			throw e;
		}//end of catch-Exception 
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exited Request String: str::"+str);
		}//end of finally
		return str;
	}//end of generateRequest
    /**
     * This method is used to parse the response.
     * @param	int p_action
     * @param	String p_responseStr
     * @return	HashMap
     * @throws	Exception
     */
    public HashMap parseResponse(int p_action,String p_responseStr,HashMap p_responseMap) throws Exception
    {
    	final String methodName="parseResponse";
	    if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_action::"+p_action+" p_responseStr:: "+p_responseStr);
		HashMap map=null;
		try
		{
			switch(p_action)
			{
				case CS3OBFaso.ACTION_ACCOUNT_INFO: 
				{
					map=parseGetAccountInfoResponse(p_responseStr);
					break;	
				}
				case CS3OBFaso.ACTION_ACCOUNT_DETAILS: 
				{
					map=parseGetAccountDetailResponse(p_responseStr);
					map.putAll(p_responseMap);
					break;	
				}
				case CS3OBFaso.ACTION_RECHARGE_CREDIT: 
				{
					map=parseRechargeCreditResponse(p_responseStr);
					break;	
				}
				case CS3OBFaso.ACTION_IMMEDIATE_DEBIT: 
				{
					map=parseImmediateDebitResponse(p_responseStr);
					break;	
				}
				case CS3OBFaso.ACTION_GET_OFFERS: 
				{
					map=parseGetOffersResponse(p_responseStr);
					break;	
				}
				
			}//end of switch block
		}//end of try block
		catch(Exception e)
		{
			_log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting map::"+map);
		}//end of finally
		return map;	
	}//end of parseResponse

	/**
	 * This method is used to generate the request for getting account information.
	 * @param	HashMap	p_requestMap
	 * @return	String
	 * @throws	Exception
	 */
    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception
	{
    	final String methodName="generateGetAccountInfoRequest";
	    if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		String inTransactionID=null;
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getCS3TransDateTime()+"</dateTime.iso8601></value>");
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
		}//end of try-block
		catch(Exception e)
		{
			_log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting Request String:requestStr::"+requestStr);
		}//end of finally
		return requestStr;
	}//end of generateGetAccountInfoRequest
    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
    {
    	final String methodName="generateRechargeCreditRequest";
	    if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		String inTransactionID=null;
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getCS3TransDateTime()+"</dateTime.iso8601></value>");
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
	        	stringBuffer.append("<value><string>"+p_requestMap.get("ExternalData1")+"</string></value>");
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
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>requestRefillAccountAfterFlag</name>");
	        stringBuffer.append("<value><boolean>"+p_requestMap.get("REFILL_ACNT_AFTER_FLAG")+"</boolean></value>");
	        stringBuffer.append("</member>");
	        
	      /*  stringBuffer.append("<member>");
	        stringBuffer.append("<name>requestRefillAccountBeforeFlag</name>");
	        stringBuffer.append("<value><boolean>"+p_requestMap.get("REFILL_ACNT_B4_FLAG")+"</boolean></value>");
	        stringBuffer.append("</member>");*/
	        
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
	        stringBuffer.append("<value><string>"+p_requestMap.get("CARD_GROUP")+"</string></value>");
	        stringBuffer.append("</member>");
	        stringBuffer.append(endRequestTag());
	        stringBuffer.append("</methodCall>");
	        requestStr = stringBuffer.toString();       
	        
		}//end of try-block
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e);
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting Request requestStr::"+requestStr);
		}//end of finally
		return requestStr;
    }//end of generateRechargeCreditRequest

 
    /**
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception
    {
    	final String methodName="generateImmediateDebitRequest";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		String inTransactionID=null;
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getCS3TransDateTime()+"</dateTime.iso8601></value>");
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
	        
	        String useDedicatedAccountFlag = (String)p_requestMap.get("USE_DEDICATED_ACCOUNT_FLAG");
	        if(InterfaceUtil.isNullString(useDedicatedAccountFlag) || PretupsI.NO.equalsIgnoreCase(useDedicatedAccountFlag)) {
		        
	        	if(_log.isDebugEnabled()) {
	        		_log.debug(methodName, "Use main account for debit");
	        	}
	        	
	        	stringBuffer.append("<member>");
		        stringBuffer.append("<name>adjustmentAmountRelative</name>");
		        stringBuffer.append("<value><string>"+p_requestMap.get("transfer_amount")+"</string></value>");
		        stringBuffer.append("</member>");
	        }

	        
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
	        	stringBuffer.append("<value><string>"+(String)p_requestMap.get("ExternalData1")+"</string></value>");
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
                        stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_CODE_CR")).append("</string></value>").toString());
                        stringBuffer.append("</member>");
            }
            if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_TYPE_CR")))
            {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>transactionType</name>");
                stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_TYPE_CR")).append("</string></value>").toString());
                stringBuffer.append("</member>");
            }
            if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_CODE_DR")))
            {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>transactionCode</name>");
                stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_CODE_DR")).append("</string></value>").toString());
                stringBuffer.append("</member>");
            }
            if(!InterfaceUtil.isNullString((String)p_requestMap.get("TRANSACTION_TYPE_DR")))
            {
                stringBuffer.append("<member>");
                stringBuffer.append("<name>transactionType</name>");
                stringBuffer.append((new StringBuilder()).append("<value><string>").append((String)p_requestMap.get("TRANSACTION_TYPE_DR")).append("</string></value>").toString());
                stringBuffer.append("</member>");
            }		
	
            
            // append dedicated account info
            if(!InterfaceUtil.isNullString(useDedicatedAccountFlag) && PretupsI.YES.equalsIgnoreCase(useDedicatedAccountFlag)) {
                
	        	if(_log.isDebugEnabled()) {
	        		_log.debug(methodName, "Use dedicated account for debit");
	        	}
            	
            	//debit for processing fee if main balance has enough balance
            	String debitProcessingFeeFromMainAcc = (String)p_requestMap.get("processingFee_from_mainAccount");
            	if(!InterfaceUtil.isNullString(debitProcessingFeeFromMainAcc) && PretupsI.YES.equalsIgnoreCase(debitProcessingFeeFromMainAcc)){
    	        	
    	        	if(_log.isDebugEnabled()) {
    	        		_log.debug(methodName, "Use main account to debit processing fee");
    	        	}
            		
            		stringBuffer.append("<member>");
    		        stringBuffer.append("<name>adjustmentAmountRelative</name>");
    		        stringBuffer.append("<value><string>"+p_requestMap.get("dedicated_processing_fee")+"</string></value>");
    		        stringBuffer.append("</member>");
            	}
            	
            	
            	
            	stringBuffer.append("<member>");
                stringBuffer.append("<name>dedicatedAccountUpdateInformation</name>");
                stringBuffer.append("<value><array><data><value>");
                stringBuffer.append("<struct>");
                
                stringBuffer.append("<member>");
                stringBuffer.append("<name>dedicatedAccountID</name>");
                stringBuffer.append("<value><int>" + (String)p_requestMap.get("DEDICATED_ACCOUNT_ID")+"</int></value>");
                stringBuffer.append("</member>");
                
                stringBuffer.append("<member>");
                stringBuffer.append("<name>adjustmentAmountRelative</name>");
                stringBuffer.append("<value><string>" + (String)p_requestMap.get("dedicated_transfer_amount")+"</string></value>");
                stringBuffer.append("</member>");
                
                //need to take care of it
                stringBuffer.append("<member>");
                stringBuffer.append("<name>dedicatedAccountUnitType</name>");
                stringBuffer.append("<value><i4>" + (String)p_requestMap.get("DEDICATED_ACCOUNT_UNITTYPE")+"</i4></value>");
                stringBuffer.append("</member>");
                
                stringBuffer.append("</struct>");
                stringBuffer.append("</value></data></array></value>");
                stringBuffer.append("</member>");
            }
            
            
            
 
	        stringBuffer.append(endRequestTag());
	        stringBuffer.append("</methodCall>");
	        requestStr = stringBuffer.toString();	       
		}//end of try-Block
		catch(Exception e)
		{
			_log.error(methodName,"Exception e: "+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting  requestStr::"+requestStr);
		}//end of finally
        return requestStr;
    }//end of generateImmediateDebitRequest
    /**
     * This method is used to parse the response of GetAccountinformation.
     * @param	String p_responseStr
     * @return	HashMap
     */
    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception
    {
    	final String methodName="parseGetAccountInfoResponse";
    	if(_log.isDebugEnabled()) _log.debug(methodName,"Entered: ");
        HashMap responseMap = null;
        int indexStart=0;
        int indexEnd=0;
        int tempIndex=0;
        String responseCode = null;
        try
        {
            responseMap = new HashMap();
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
	            Object[] successList=CS3OBFaso.RESULT_OK.split(",");
	            //if(!CS3NigeriaI.RESULT_OK.equals(responseCode))
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
	            responseMap.put("temporaryBlockedFlag",temporaryBlockedFlag);
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        int startIdx = p_responseStr.indexOf("<member><name>dedicatedAccountInformation");
	        int endIdx =  p_responseStr.indexOf("</array>", startIdx);
	        if(_log.isDebugEnabled()) _log.debug(methodName,"parsing info for dedicated account");
	        if(_log.isDebugEnabled()) _log.debug(methodName,"startidx: " + startIdx);
	        if(_log.isDebugEnabled()) _log.debug(methodName,"endidx: " + endIdx);

	        
	        HashMap<String, String> dedicatedAccountMap = new HashMap();
	        HashMap<String, String> dedicatedAccUnitTypeMap = new HashMap();
	        

	        
	        for(int i=startIdx; i<endIdx; i++) {
	        	String dedicatedAccountId = "";
	        	String dedicatedAccountBal = "";
	        	String dedicatedAccountUnitType = "";
	        	int idx = p_responseStr.indexOf("<member><name>dedicatedAccountID",i);
	        	if(_log.isDebugEnabled()) _log.debug(methodName,"loopCounter i=" + i);
	        	
	        	
	        	if(idx<0) {
	        		if(_log.isDebugEnabled()) _log.debug(methodName,"breaking loop");
	        		
	        		break;
	        	}
	        	int tempIdx = p_responseStr.indexOf("dedicatedAccountID", idx);
	        	if(tempIdx>0) {
	        		dedicatedAccountId = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIdx),p_responseStr.indexOf("</i4>",tempIdx)).trim();
	        		
	        		
	        		idx = p_responseStr.indexOf("</member>",idx);
	        	}
	        	int tmp = p_responseStr.indexOf("<member><name>dedicatedAccountUnitType",idx);
	        	if(tmp>0){
	        		idx = p_responseStr.indexOf("<member><name>dedicatedAccountUnitType",idx);
	        	}
	        	
	        	tempIdx = p_responseStr.indexOf("dedicatedAccountUnitType", idx);
	        	if(tempIdx>0) {
	        		dedicatedAccountUnitType = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIdx),p_responseStr.indexOf("</i4>",tempIdx)).trim();
	        		
	        		idx = p_responseStr.indexOf("</member>",idx);
	        	}
	        	
	        	idx = p_responseStr.indexOf("<member><name>dedicatedAccountValue1",idx);
	        	tempIdx = p_responseStr.indexOf("dedicatedAccountValue1", idx);
	        	if(tempIdx>0) {
	        		dedicatedAccountBal = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIdx),p_responseStr.indexOf("</string>",tempIdx)).trim();
	        		
	        		idx = p_responseStr.indexOf("</member>",idx);
	        	}
	        	

	        	
	        	dedicatedAccountMap.put(dedicatedAccountId, dedicatedAccountBal);
	        	dedicatedAccUnitTypeMap.put(dedicatedAccountId, dedicatedAccountUnitType);
	        	

	        	i = idx;
	        }
	        
	        String dedicatedAccountStr = BTSLUtil.getStringFromHash(dedicatedAccountMap);
	        String dedicatedAccUnitTypeStr = BTSLUtil.getStringFromHash(dedicatedAccUnitTypeMap);
	        
	        if(_log.isDebugEnabled()) {
	        	_log.debug(methodName, "dedicatedAccountStr: " + dedicatedAccountStr);
	        	_log.debug(methodName, "dedicatedAccUnitTypeStr: " + dedicatedAccUnitTypeStr);
	        }
	        
	        responseMap.put("DEDICATED_ACCOUNTS_STR", dedicatedAccountStr);
	        responseMap.put("DEDICATED_ACC_UNIT_TYPE_STR", dedicatedAccUnitTypeStr);
	        
	        
	        
	        
        }
        catch(Exception e)
        {
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }//end catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug(methodName,"Exited responseMap::"+responseMap);
        }//end of finally
		return responseMap;
    }//end of parseGetAccountInfoResponse
    /**
     * This method is used to parse the credit response. 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception
    {
    	final String methodName="parseRechargeCreditResponse";
        if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        int indexStart=0;
        int indexEnd=0;
        int tempIndex=0;
        String responseCode = null;
        try
        {
            responseMap = new HashMap();
 	        
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
	            Object[] successList=CS3OBFaso.RESULT_OK.split(",");
	            //if(!CS3NigeriaI.RESULT_OK.equals(responseCode))
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
         }
        catch(Exception e)
        {
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug(methodName,"Exited responseMap::"+responseMap);
        }//end of finally
		return responseMap;
    }//end of parseRechargeCreditResponse

    /**
     * This method is used to parse the credit response. 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception
    {
    	final String methodName="parseImmediateDebitResponse";
        if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        int indexStart=0;
        int indexEnd=0;
        int tempIndex=0;
        String responseCode = null;
        try
        {
        	responseMap = new HashMap();
  	       
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
	            Object[] successList=CS3OBFaso.RESULT_OK.split(",");
	            //if(!CS3NigeriaI.RESULT_OK.equals(responseCode))
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
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug(methodName,"Exited responseMap::"+responseMap);
        }
		return responseMap;
    }//end of parseImmediateDebitResponse
    /**
     * Method to get the Transaction date and time with specified format.
     * @return String
     * @throws Exception
     */
    private String getCS3TransDateTime() throws Exception
    {
    	final String methodName="getCS3TransDateTime";
        if(_log.isDebugEnabled()) _log.debug(methodName,"Entered");
        String transDateTime =null;
	    try
	    {
			Date now = new Date();
			transDateTime = _sdf.format(now)+_sign+_twoDigits.format(_hours)+ _twoDigits.format(_minutes);
	    }//end of try block
	    catch(Exception e)
	    {
	        _log.error(methodName,"Exception e = "+e.getMessage());
	        throw e;
	    }//end of catch-Exception
	    finally
	    {
	        if(_log.isDebugEnabled()) _log.debug(methodName,"Exited transDateTime: "+transDateTime);
	    }//end of finally
	    return transDateTime;
    }//end of getCS3TransDateTime
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
    	final String methodName="getDateString";
        if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_dateStr::"+p_dateStr);
        String dateStr="";
        try
        {
            dateStr = p_dateStr.substring(0,p_dateStr.indexOf("T")).trim();
        }
        catch(Exception e)
        {
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug(methodName,"Exited dateStr::"+dateStr);
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
    	final String methodName="generateGetAccountDetailRequest";
	    if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		String inTransactionID=null;
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getCS3TransDateTime()+"</dateTime.iso8601></value>");
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
		}//end of try-block
		catch(Exception e)
		{
			_log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting Request String:requestStr::"+requestStr);
		}//end of finally
		return requestStr;
	}//end of generateGetAccountDetailRequest
    
    /**
     * This method is used to parse the response of GetAccountinDetails.
     * @param	String p_responseStr
     * @return	HashMap
     */
    private HashMap parseGetAccountDetailResponse(String p_responseStr) throws Exception
    {
    	final String methodName="parseGetAccountDetailResponse";
       if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        int indexStart=0;
        int indexEnd=0;
        int tempIndex=0;
        String responseCode = null;
        try
        {
            responseMap = new HashMap();
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
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }//end catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug(methodName,"Exited responseMap::"+responseMap);
        }//end of finally
		return responseMap;
    }//end of parseGetAccountDetailsResponse
    
    
    
	/**
	 * This method is used to generate the request for getting account information.
	 * @param	HashMap	p_requestMap
	 * @return	String
	 * @throws	Exception
	 */
    private String generateGetOffersRequest(HashMap p_requestMap) throws Exception
	{
    	final String methodName="generateGetOffersRequest";
	    if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		
		try
		{
		    stringBuffer=new StringBuffer(1028);
		    

	        stringBuffer.append("<?xml version='1.0' encoding='ISO-8859-1' standalone='no'?>");
	        stringBuffer.append("<methodCall>");
	        stringBuffer.append("<methodName>GetOffers</methodName>");
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getCS3TransDateTime()+"</dateTime.iso8601></value>");
	        stringBuffer.append("</member>");
	        
	        //Set the subscriberNumber after adding or removing the prefix defined in the INFile.
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>subscriberNumber</name>");
	        stringBuffer.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"))+"</string></value>");
	        stringBuffer.append("</member>");
	        stringBuffer.append(endRequestTag());
	        stringBuffer.append("</methodCall>");
	        requestStr = stringBuffer.toString();
		}//end of try-block
		catch(Exception e)
		{
			_log.error(methodName,"Exception e::"+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting Request String:requestStr::"+requestStr);
		}//end of finally
		return requestStr;
	}//end of generateGetOffersRequest
    
    
    private HashMap parseGetOffersResponse(String p_responseStr) throws Exception{
    	final String methodName="parseGetOffersResponse";
    	if(_log.isDebugEnabled()) _log.debug(methodName,"Entered: ");
        HashMap responseMap = null;
        HashMap offersMap = null;
        int startIdx = 0;
        int endIdx = 0;
        int tempIdx = 0;
        String responseCode = null;
        try {
        	responseMap = new HashMap();
        	offersMap = new HashMap();
        	
	        startIdx = p_responseStr.indexOf("<member><name>responseCode");
	        tempIdx = p_responseStr.indexOf("responseCode",startIdx);
	        if(tempIdx>0){
	            responseCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIdx),p_responseStr.indexOf("</i4>",tempIdx)).trim();
	            responseMap.put("responseCode",responseCode.trim());
	            Object[] successList=CS3OBFaso.RESULT_OK.split(",");
	            //if(!CS3NigeriaI.RESULT_OK.equals(responseCode))
	            if(!Arrays.asList(successList).contains(responseCode))
	                return responseMap;
	        }
        	
        	
        	
        	
        	startIdx = p_responseStr.indexOf("<member><name>offerInformation");
	        endIdx =  p_responseStr.indexOf("</methodResponse>");
	        
	        int offerIdCounter = 0;
	        for(int i=startIdx; i<endIdx; i++) {
	        	String offerId = "";
	        	int idx = p_responseStr.indexOf("<member><name>offerID", i);
	        	if(_log.isDebugEnabled()) _log.debug(methodName,"loopCounter i=" + i);
	        	
	        	if(idx<0) {
	        		if(_log.isDebugEnabled()) _log.debug(methodName,"breaking loop");
	        		
	        		break;
	        	}
	        	
	        	tempIdx = p_responseStr.indexOf("offerID", idx);
	        	if(tempIdx>0) {
	        		offerId = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIdx),p_responseStr.indexOf("</i4>",tempIdx)).trim();
	        		offerIdCounter++;
	        		
	        		idx = p_responseStr.indexOf("</member>",idx);
	        	}
	        	
	        	if(_log.isDebugEnabled()) {
	        		_log.debug(methodName, "OfferId=" + offerId + " Counter=" + offerIdCounter);
	        	}
	        	
	        	offersMap.put(offerId, String.valueOf(offerIdCounter));
	        	
	        	i=idx;
	        }
	        
	        String offersStr = BTSLUtil.getStringFromHash(offersMap);
	        responseMap.put("OFFER_IDS", offersStr);
	        
        	

	        
	        startIdx= p_responseStr.indexOf("<member><name>originTransactionID");
	        tempIdx = p_responseStr.indexOf("originTransactionID",startIdx);
	        if(tempIdx>0)
	        {
	            String originTransactionID = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIdx),p_responseStr.indexOf("</string>",tempIdx)).trim();
	            responseMap.put("originTransactionID",originTransactionID.trim());
	            
	        }
        	
        }
        catch(Exception e){
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }//end catch-Exception
        finally{
            if(_log.isDebugEnabled()) _log.debug(methodName,"Exited responseMap::"+responseMap);
        }//end of finally
        
        return responseMap;
    }
    
}
