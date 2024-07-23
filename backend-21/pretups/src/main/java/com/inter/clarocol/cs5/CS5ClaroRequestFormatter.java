
package com.inter.clarocol.cs5;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;



public class CS5ClaroRequestFormatter 
{
    public static Log _log = LogFactory.getLog(CS5ClaroRequestFormatter.class);
	static String promobal="";
    public CS5ClaroRequestFormatter() throws Exception
    {
       
    }//end of CS5ClaroRequestFormatter[constructor]
     

 
    /**
     * This method is used to parse the response string based on the type of Action.
     * @param	int p_action
     * @param	HashMap p_map
     * @return	String.
     * @throws	Exception
     */
	protected String generateRequest(int p_action, HashMap p_map) throws Exception 
	{
       if(_log.isDebugEnabled())_log.debug("generateRequest","Entered p_action::"+p_action+" map::"+p_map);
		String str=null;
		p_map.put("action",String.valueOf(p_action));
		try
		{
			switch(p_action)
			{
				case CS5ClaroI.ACTION_ACCOUNT_INFO: 
				{
					str=generateGetAccountInfoRequest(p_map);
					break;	
				}
				case CS5ClaroI.ACTION_ACCOUNT_DETAILS:
				{
                         str=generateGetAccountDetailRequest(p_map);
                         break;
				}
				case CS5ClaroI.ACTION_RECHARGE_CREDIT: 
				{
				    str=generateRechargeCreditRequest(p_map);
					break;	
				}
				case CS5ClaroI.ACTION_IMMEDIATE_DEBIT: 
				{
					str=generateImmediateDebitRequest(p_map);
					break;	
				}
			}//end of switch block
		}//end of try block
		catch(Exception e)
		{
			_log.error("generateRequest","Exception e ::"+e.getMessage());
			throw e;
		}//end of catch-Exception 
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRequest","Exited Request String: str::"+str);
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
	    if(_log.isDebugEnabled())_log.debug("parseResponse","Entered p_action::"+p_action+" p_responseStr:: "+p_responseStr);
		HashMap map=null;
		try
		{
			switch(p_action)
			{
				case CS5ClaroI.ACTION_ACCOUNT_INFO: 
				{
					map=parseGetAccountInfoResponse(p_responseStr);
					break;	
				}
			    case CS5ClaroI.ACTION_ACCOUNT_DETAILS:
                {
                        map=parseGetAccountDetailResponse(p_responseStr);
                        map.putAll(p_responseMap);
                        break;
                }
				case CS5ClaroI.ACTION_RECHARGE_CREDIT: 
				{
					map=parseRechargeCreditResponse(p_responseStr);
					break;	
				}
				case CS5ClaroI.ACTION_IMMEDIATE_DEBIT: 
				{
					map=parseImmediateDebitResponse(p_responseStr);
					break;	
				}
			}//end of switch block
		}//end of try block
		catch(Exception e)
		{
			_log.error("parseResponse","Exception e::"+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug("parseResponse","Exiting map::"+map);
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
	    if(_log.isDebugEnabled()) _log.debug("generateGetAccountInfoRequest","Entered p_requestMap::"+p_requestMap);
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
	        stringBuffer.append("</member>");
	        //Set the optional parameter subscriberNumberNAI if present.
	        if(!InterfaceUtil.isNullString((String)p_requestMap.get("SubscriberNumberNAI")))
	        {
		        stringBuffer.append("<member>");
		        stringBuffer.append("<name>subscriberNumberNAI</name>");
		        stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
		        stringBuffer.append("</member>");
	        }
	        
	        //message cabablity flag 
	        if(!InterfaceUtil.isNullString((String)p_requestMap.get("MESSAGE_CAPABILITY_FLAG")))
	        {
	        	stringBuffer.append("<member><name>messageCapabilityFlag</name><value><struct>");
	        	
	        	if(!InterfaceUtil.isNullString((String)p_requestMap.get("PROMOTION_NOTIFICATION_FLAG")))
	        	 {
	        		 stringBuffer.append("<member><name>promotionNotificationFlag</name>");
	        		 stringBuffer.append("<value><boolean>"+(String)p_requestMap.get("PROMOTION_NOTIFICATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)p_requestMap.get("FIRST_IVR_CALL_SET_FLAG")))
	        	 {
	        		 stringBuffer.append("<member><name>firstIVRCallSetFlag</name><value><boolean>"+(String)p_requestMap.get("FIRST_IVR_CALL_SET_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)p_requestMap.get("ACCOUNT_ACTIVATION_FLAG")))
	        	 {
	        		 stringBuffer.append("<member><name>accountActivationFlag</name><value><boolean>"+(String)p_requestMap.get("ACCOUNT_ACTIVATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	stringBuffer.append("</struct></value></member>");
	        }
	        
	        //dedicated account selection on manual selection 
	        /*
	        if("Y".equalsIgnoreCase((String)p_requestMap.get("DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL")))
	        {
	        	 stringBuffer.append("<member>");
	        	 stringBuffer.append("<name>dedicatedAccountSelection</name>");
	        	 stringBuffer.append("<value><array><data>");
	        	 //dedicated account selectionList 
	        	String dedicatedAccount= (String)p_requestMap.get("DEDICATED_ACCOUNT_SELECTION_ACCOUNTID");
	        	
	        	String dAccount[]= dedicatedAccount.split(":");
	        	
	        	for (int i=0;i<dAccount.length;i++)
	        	{
	        		stringBuffer.append("<value>");
	        		stringBuffer.append("<struct>");
	        		stringBuffer.append("<member><name>dedicatedAccountIDFirst</name><value><i4>"+dAccount[i]+"</i4></value></member>");
	    	        	// stringBuffer.append("<member><name>dedicatedAccountIDLast</name><value><i4>1</i4></value></member>");
	        		stringBuffer.append("</struct>");
	        		stringBuffer.append("</value>");
	    	        
	        	}
	        	 stringBuffer.append("</data></array></value></member>");
	        
	        }
	        else if("Y".equalsIgnoreCase((String)p_requestMap.get("DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE")))
	        {
	        	stringBuffer.append("<member>");
	        	stringBuffer.append("<name>dedicatedAccountSelection</name>");
	        	stringBuffer.append("<value><array><data>");
	        	String dedicatedAccountIDFirst= (String)p_requestMap.get("DEDICATED_ACCOUNT_ID_FIRST_RANGE");
	        	String dedicatedAccountIDLast= (String)p_requestMap.get("DEDICATED_ACCOUNT_ID_LAST_RANGE");
	        	
	       
	        	String dedicatedDefAccountIDFirst= (String)p_requestMap.get("DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE");
	        	String dedicatedDefAccountIDLast= (String)p_requestMap.get("DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE");
	  	        
	         	int daFirstRange= Integer.parseInt(dedicatedAccountIDFirst);
        		int daLastRange=Integer.parseInt(dedicatedAccountIDLast);
        		int daDefFirstRange=Integer.parseInt(dedicatedDefAccountIDFirst);
        		int daDefLastRange=Integer.parseInt(dedicatedDefAccountIDLast);
        			
        			//DefaultDAFirst=61 ( logic will be any value smaller compare with DAselectionFirst)
        			//DefaultDALast=105 (logic will be any value greater compare with DAselectionLast)
        		
        	
        		daFirstRange=	Math.min(daFirstRange, daDefFirstRange);
        		daLastRange=	Math.max(daLastRange, daDefLastRange);
	        	stringBuffer.append("<value>");
	        	stringBuffer.append("<struct>");
	        	stringBuffer.append("<member><name>dedicatedAccountIDFirst</name><value><i4>"+daFirstRange+"</i4></value></member>");
	        	stringBuffer.append("<member><name>dedicatedAccountIDLast</name><value><i4>"+daLastRange+"</i4></value></member>");
	        	stringBuffer.append("</struct>");
	        	stringBuffer.append("</value>");
	  	    	        
	        	stringBuffer.append("</data></array></value></member>");
	  	        
	        }
	        */
	        
	        //Set the subscriberNumber after adding or removing the prefix defined in the INFile.
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>subscriberNumber</name>");
	        stringBuffer.append("<value><string>"+p_requestMap.get("FILTER_MSISDN")+"</string></value>");
	        stringBuffer.append("</member>");
	        stringBuffer.append(endRequestTag());
	        stringBuffer.append("</methodCall>");
	        requestStr = stringBuffer.toString();
		}//end of try-block
		catch(Exception e)
		{
			_log.error("generateGetAccountInfoRequest","Exception e::"+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequest","Exiting Request String:requestStr::"+requestStr);
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
	    if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
	        stringBuffer.append("</member>");
	        //Check the optional value of SubscriberNumberNAI if it is not null set it.	 
	        if(!InterfaceUtil.isNullString((String)p_requestMap.get("SubscriberNumberNAI")))
	        {
	        	stringBuffer.append("<member>");
	        	stringBuffer.append("<name>subscriberNumberNAI</name>");
	        	stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
	        	stringBuffer.append("</member>");	
	        }
	        //Set subscriberNumber and add or remove the msisdn if required.
        	
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>subscriberNumber</name>");
	        stringBuffer.append("<value><string>"+p_requestMap.get("FILTER_MSISDN")+"</string></value>");
	        stringBuffer.append("</member>");
	        
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
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>externalData2</name>");
	        stringBuffer.append("<value><string>"+p_requestMap.get("IN_TXN_ID")+"</string></value>");
	        stringBuffer.append("</member>");
	        
		      
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>externalData3</name>");
	        stringBuffer.append("<value><string>"+p_requestMap.get("EXTERNALDATA3")+"</string></value>");
	        stringBuffer.append("</member>");
	        
	        //Set the card group for paymentProfileID
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>refillProfileID</name>");
	        stringBuffer.append("<value><string>"+p_requestMap.get("CARD_GROUP")+"</string></value>");
	        stringBuffer.append("</member>");
	       
/*
	      // value of external data 1 tag 
	        if (p_requestMap.get("GEO_DOMAIN_CODE") != null) 
	        {
	        	stringBuffer.append("<member>");
	        	stringBuffer.append("<name>externalData1</name>");
	        	stringBuffer.append("<value><string>"+p_requestMap.get("GEO_DOMAIN_CODE")+"</string></value>");
	        	stringBuffer.append("</member>");
	        }
	   
	      //Set the  value of ExternalData2, .
	        if (p_requestMap.get("SENDER_MSISDN") != null) 
	        {
	        	stringBuffer.append("<member>" );
	        	stringBuffer.append("<name>externalData2</name>" );
	        	stringBuffer.append("<value><string>" + p_requestMap.get("SENDER_MSISDN")+"</string></value>" );
	        	stringBuffer.append("</member>" );
	        }
	        else
	        {
	        	stringBuffer.append("<member>" );
	        	stringBuffer.append("<name>externalData2</name>" );
	        	stringBuffer.append("<value><string>0</string></value>" );
	        	stringBuffer.append("</member>" );
	        }
					
	     
	        if( !( InterfaceUtil.isNullString( (String)p_requestMap.get("BONUS_BUNDLE_CODES") )&& InterfaceUtil.isNullString( (String)p_requestMap.get("BONUS_BUNDLE_VALUES") ) ) )
	        {
	        	stringBuffer.append("<member>");
	        	stringBuffer.append("<name>externalData3</name>");
	        	String bundlename=(String)p_requestMap.get("BONUS_BUNDLE_CODES");
	        	String bundlevalue=(String)p_requestMap.get("BONUS_BUNDLE_VALUES");
	        	 
	        	String finalextd3="";
	        	if(bundlename.contains("|"))
	        	{
	        		String bundlenamearr[]=bundlename.split("\\|");
	        		String bundlevaluearr[]=bundlevalue.split("\\|");
	        		int len=bundlenamearr.length;
	        		
	        		len--;
	        		for(int i=0;i<=len;i++)
	        		{
	        			finalextd3 +=bundlenamearr[i]+"="+bundlevaluearr[i];
	        			if(i==len)
	        				break;
	        			finalextd3+="|";
	        		}
	        	}
	        	else
	        	{
	        		finalextd3 =bundlename+"="+bundlevalue;
	        	}
	        	
	        	stringBuffer.append("<value><string>"+finalextd3+"</string></value>");
	        	stringBuffer.append("</member>");
	        }
	     
	        	
	        if(!InterfaceUtil.isNullString((String)p_requestMap.get("GET_INFO_10")))
	        {
	        	stringBuffer.append("<member>");
	        	stringBuffer.append("<name>externalData4</name>");
	        	stringBuffer.append("<value><string>"+p_requestMap.get("GET_INFO_10")+"</string></value>");
	        	stringBuffer.append("</member>");
	        }
*/	  
	        String accountsflag=(String)p_requestMap.get("REFILL_ACNT_AFTER_FLAG");
	        if(("1").equalsIgnoreCase(accountsflag)){
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>requestRefillAccountAfterFlag</name>");
	        stringBuffer.append("<value><boolean>"+accountsflag+"</boolean></value>");
	        stringBuffer.append("</member>");
	        }
	       
	        
	        stringBuffer.append(endRequestTag());
	        stringBuffer.append("</methodCall>");   
	        requestStr = stringBuffer.toString();       
	        
		}//end of try-block
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request requestStr::"+requestStr);
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
		if(_log.isDebugEnabled())_log.debug("generateImmediateDebitRequest","Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
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
	        stringBuffer.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
	        stringBuffer.append("</member>");
	        //Check the optional value of SubscriberNumberNAI if it is not null set it.	        
        	stringBuffer.append("<member>");
        	stringBuffer.append("<name>subscriberNumberNAI</name>");
        	stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
        	stringBuffer.append("</member>");	     
	        //Set subscriberNumber and add or remove the msisdn if required.
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>subscriberNumber</name>");
	        stringBuffer.append("<value><string>"+p_requestMap.get("FILTER_MSISDN")+"</string></value>");
	        stringBuffer.append("</member>");
	        //Set the transaction_currency defined into INFile for transactionCurrency.
	        stringBuffer.append("<member>");
	        stringBuffer.append("<name>transactionCurrency</name>");
	        stringBuffer.append("<value><string>"+p_requestMap.get("CURRENCY")+"</string></value>");
	        stringBuffer.append("</member>");
	        
	        String dedicatedAccount100=(String)p_requestMap.get("DEDICATED_ACCOUNT_FOR_POSTPAID"); 
	        if("POST".equals((String)p_requestMap.get("SUBSCRIBER_TYPE")))
	        {
		        stringBuffer.append("<member><name>dedicatedAccountUpdateInformation</name><value><array><data><value><struct>");
		        stringBuffer.append("<member><name>dedicatedAccountID</name><value><i4>"+dedicatedAccount100+"</i4></value></member>");
		        stringBuffer.append("<member><name>adjustmentAmountRelative</name><value><string>"+p_requestMap.get("transfer_amount")+"</string></value></member>");
		        stringBuffer.append(" </struct></value></data></array></value></member>");
	        }
	        else
	        {
	        //Set the transfer_amount to transactionAmount
	        	stringBuffer.append("<member>");
	        	stringBuffer.append("<name>adjustmentAmountRelative</name>");
	        	stringBuffer.append("<value><string>"+p_requestMap.get("transfer_amount")+"</string></value>");
	        	stringBuffer.append("</member>");
	        }        
	        stringBuffer.append(endRequestTag());
	        stringBuffer.append("</methodCall>");
	        requestStr = stringBuffer.toString();	       
		}//end of try-Block
		catch(Exception e)
		{
			_log.error("generateImmediateDebitRequest","Exception e: "+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateImmediateDebitRequest","Exiting  requestStr::"+requestStr);
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
       // if(_log.isDebugEnabled()) _log.debug("parseGetAccountInfoResponse","Entered p_responseStr::"+p_responseStr);
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
	            Object[] successList=CS5ClaroI.RESULT_OK.split(",");
	            //if(!CS5IdeaI.RESULT_OK.equals(responseCode))
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
	        
	        indexStart= p_responseStr.indexOf("<member><name>aggregatedBalance1",indexEnd);
	        tempIndex = p_responseStr.indexOf("aggregatedBalance1",indexStart);
	        if(tempIndex>0)
	        {
	            String aggregatedBalance1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("aggregatedBalance1",aggregatedBalance1.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        
	        indexStart= p_responseStr.indexOf("<member><name>creditClearanceDate",indexEnd);
	        tempIndex = p_responseStr.indexOf("creditClearanceDate",indexStart);
	        if(tempIndex>0)
	        {
	            String creditClearanceDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("creditClearanceDate",creditClearanceDate.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        indexStart= p_responseStr.indexOf("<member><name>currency1",indexEnd);
	        tempIndex = p_responseStr.indexOf("currency1",indexStart);
	        if(tempIndex>0)
	        {
	            String currency1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("currency1",currency1.trim());
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
	        
	        indexStart= p_responseStr.indexOf("<member><name>originTransactionID",indexEnd);
	        tempIndex = p_responseStr.indexOf("originTransactionID",indexStart);
	        if(tempIndex>0)
	        {
	            String originTransactionID = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("originTransactionID",originTransactionID.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        
	        
	        
	        
	        
	       /* indexStart= p_responseStr.indexOf("<member><name>dedicatedAccountInformation",indexEnd);
	        
	        tempIndex = p_responseStr.indexOf("dedicatedAccountInformation",indexStart);
	        
	        if(tempIndex>0)
	        {
	            String daXMLString = p_responseStr.substring("<array>".length()+p_responseStr.indexOf("<array>",tempIndex),p_responseStr.indexOf("</array>",tempIndex)).trim();
	            responseMap.put("dedicatedAccountInformation",daXMLString.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }*/
	        
	        /*if(responseMap.get("dedicatedAccountInformation")!= null){
	        	
	        	String daXML=(String)responseMap.get("dedicatedAccountId");
	        	indexStart= p_responseStr.indexOf("<member><name>dedicatedAccountInformation",indexEnd);
		        	
		        tempIndex = daXML.indexOf("dedicatedAccountID</name><value><i4>100",indexStart);
		        
		        if(tempIndex>0)
		        {
		            String daXMLString = daXML.substring("dedicatedAccountValue1</name><value><string>".length()+p_responseStr.indexOf("dedicatedAccountValue1</name><value><string>",tempIndex),p_responseStr.indexOf("</string>",p_responseStr.indexOf("dedicatedAccountValue1</name><value><string>"))).trim();
		            responseMap.put("DA100",daXMLString.trim());
		            indexEnd = p_responseStr.indexOf("</member>",indexStart);
		        }
	        }*/
	        
	        indexStart= p_responseStr.indexOf("<member><name>serviceClassCurrent",indexEnd);
	        tempIndex = p_responseStr.indexOf("serviceClassCurrent",indexStart);
	        if(tempIndex>0)
	        {
	            String serviceClassCurrent = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("serviceClassCurrent",serviceClassCurrent.trim());
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
	        
	        indexStart= p_responseStr.indexOf("<member><name>serviceRemovalDate",indexEnd);
	        tempIndex = p_responseStr.indexOf("serviceRemovalDate",indexStart);
	        if(tempIndex>0)
	        {
	            String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("serviceRemovalDate",getDateString(serviceRemovalDate.trim()));
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
	        
	        
	        
	        indexStart= p_responseStr.indexOf("<member><name>temporaryBlockedFlag",indexEnd);
	        tempIndex = p_responseStr.indexOf("temporaryBlockedFlag",indexStart);
	        if(tempIndex>0)
	        {
	            String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length()+p_responseStr.indexOf("<boolean>",tempIndex),p_responseStr.indexOf("</boolean>",tempIndex)).trim();
	            responseMap.put("temporaryBlockedFlag",temporaryBlockedFlag);
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
        }
        catch(Exception e)
        {
            _log.error("parseGetAccountInfoResponse","Exception e::"+e.getMessage());
            throw e;
        }//end catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("parseGetAccountInfoResponse","Exited responseMap::"+responseMap);
        }//end of finally
		return responseMap;
    }
    //end of parseGetAccountInfoResponse
    /**
     * This method is used to parse the credit response. 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception
    {
        if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Entered p_responseStr::"+p_responseStr);
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
	        
	        
	        
	        indexStart= p_responseStr.indexOf("<member><name>currency1",indexEnd);
	        tempIndex = p_responseStr.indexOf("currency1",indexStart);
	        if(tempIndex>0)
	        {
	            String currency1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("currency1",currency1.trim());
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
 	       
	        indexStart= p_responseStr.indexOf("<member><name>masterAccountNumber",indexEnd);
	        tempIndex = p_responseStr.indexOf("masterAccountNumber",indexStart);
	        if(tempIndex>0)
	        {
	            String masterAccountNumber = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("masterAccountNumber",masterAccountNumber.trim());
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
 	        
 	      /* indexStart= p_responseStr.indexOf("<member><name>dedicatedAccountInformation",indexEnd);
	        
	        tempIndex = p_responseStr.indexOf("dedicatedAccountInformation",indexStart);
	        
	        if(tempIndex>0)
	        {
	            String daXMLString = p_responseStr.substring("<array>".length()+p_responseStr.indexOf("<array>",tempIndex),p_responseStr.indexOf("</array>",tempIndex)).trim();
	            responseMap.put("dedicatedAccountInformation",daXMLString.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }*/
 	        
 	       indexStart = p_responseStr.indexOf("<member><name>refillType");
	        tempIndex = p_responseStr.indexOf("refillType",indexStart);
	        if(tempIndex>0)
	        {
	        	String refillType = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("refillType",refillType.trim());
	            
	        }
 	        
	        indexStart= p_responseStr.indexOf("<member><name>segmentationID",indexEnd);
 	        tempIndex = p_responseStr.indexOf("segmentationID",indexStart);
 	        if(tempIndex>0)
 	        {
 	            String segmentationID = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
 	            responseMap.put("segmentationID",segmentationID.trim());
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
 	       	 
 	       indexStart= p_responseStr.indexOf("<member><name>transactionCurrency",indexEnd);
	        tempIndex = p_responseStr.indexOf("transactionCurrency",indexStart);
	        if(tempIndex>0)
	        {
	            String transactionCurrency = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("transactionCurrency",transactionCurrency.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
 	        //earlier commented from here
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
 	        //commented till here
 	        
 	        indexStart= p_responseStr.indexOf("<member><name>accountValue1",indexEnd);
 	        tempIndex = p_responseStr.indexOf("accountValue1",indexStart);
 	        if(tempIndex>0)
 	        {
 	            String accountValue1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
 	            responseMap.put("accountValue1",accountValue1.trim());
 	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
 	        }
 	       indexStart= p_responseStr.indexOf("<member><name>refillValuePromotion",indexEnd);
	        tempIndex = p_responseStr.indexOf("refillAmount1",indexStart);
	        if(tempIndex>0)
	        {
	            String refillAmount1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            //System.out.println("refillAmount1"+refillAmount1);
	            responseMap.put("refillAmount1",refillAmount1.trim());
	           // promobal=refillAmount1.trim();
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        
 	       indexStart = p_responseStr.indexOf("<member><name>responseCode");
	        tempIndex = p_responseStr.indexOf("responseCode",indexStart);
	        if(tempIndex>0)
	        {
	            responseCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("responseCode",responseCode.trim());
	            
	        }
         }
        catch(Exception e)
        {
            _log.error("parseGetAccountInfoResponse","Exception e::"+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Exited responseMap::"+responseMap);
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
        if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Entered p_responseStr::"+p_responseStr);
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
	            Object[] successList=CS5ClaroI.RESULT_OK.split(",");
	            //if(!CS5IdeaI.RESULT_OK.equals(responseCode))
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
            _log.error("parseImmediateDebitResponse","Exception e::"+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Exited responseMap::"+responseMap);
        }
		return responseMap;
    }//end of parseImmediateDebitResponse
    /**
     * Method to get the Transaction date and time with specified format.
     * @return String
     * @throws Exception
     */
    /*private String getCS5TransDateTime() throws Exception
    {
        if(_log.isDebugEnabled()) _log.debug("getCS5TransDateTime","Entered");
        String transDateTime =null;
	    try
	    {
			Date now = new Date();
			transDateTime = _sdf.format(now)+_sign+_twoDigits.format(_hours)+ _twoDigits.format(_minutes);
	    }//end of try block
	    catch(Exception e)
	    {
	        _log.error("getCS5TransDateTime","Exception e = "+e.getMessage());
	        throw e;
	    }//end of catch-Exception
	    finally
	    {
	        if(_log.isDebugEnabled()) _log.debug("getCS5TransDateTime","Exited transDateTime: "+transDateTime);
	    }//end of finally
	    return transDateTime;
    }//end of getCS5TransDateTime
    */
    private String getISO8601DateTime() {
		SimpleDateFormat ISO8601Local = new SimpleDateFormat(
				"yyyyMMdd'T'HH:mm:ss");
		TimeZone timeZone = TimeZone.getDefault();
		ISO8601Local.setTimeZone(timeZone);
		DecimalFormat twoDigits = new DecimalFormat("00");

		Date now = new Date();
		int offset = ISO8601Local.getTimeZone().getOffset(now.getTime());
	//	System.out.println(offset);
		String sign = "+";
		if (offset < 0) {
			offset = -offset;
			sign = "-";
		}
		int hours = offset / 3600000;
		int minutes = (offset - hours * 3600000) / 60000;
		String ISO8601Now = ISO8601Local.format(now) + sign
				+ twoDigits.format(hours) + twoDigits.format(minutes);
		return ISO8601Now;
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
        if(_log.isDebugEnabled()) _log.debug("getDateString","Entered p_dateStr::"+p_dateStr);
        String dateStr="";
        try
        {
            dateStr = p_dateStr.substring(0,p_dateStr.indexOf("T")).trim();
        }
        catch(Exception e)
        {
            _log.error("getDateString","Exception e::"+e.getMessage());
            throw e;
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("getDateString","Exited dateStr::"+dateStr);
        }
        return dateStr;
    }
    
    /**
     * This method is used to generate the request for getting account Details.
     * @param       HashMap p_requestMap
     * @return      String
     * @throws      Exception
     */
private String generateGetAccountDetailRequest(HashMap p_requestMap) throws Exception
    {
        if(_log.isDebugEnabled()) _log.debug("generateGetAccountDetailRequest","Entered p_requestMap::"+p_requestMap);
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
            stringBuffer.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
            stringBuffer.append("</member>");
            //Set the optional parameter subscriberNumberNAI if present.
            stringBuffer.append("<member>");
            stringBuffer.append("<name>subscriberNumberNAI</name>");
            stringBuffer.append("<value><i4>"+p_requestMap.get("SubscriberNumberNAI")+"</i4></value>");
            stringBuffer.append("</member>");
            //message cabablity flag 
	        if(!InterfaceUtil.isNullString((String)p_requestMap.get("MESSAGE_CAPABILITY_FLAG")))
	        {
	        	stringBuffer.append("<member><name>messageCapabilityFlag</name><value><struct>");
	        	
	        	if(!InterfaceUtil.isNullString((String)p_requestMap.get("PROMOTION_NOTIFICATION_FLAG")))
	        	 {
	        		 stringBuffer.append("<member><name>promotionNotificationFlag</name>");
	        		 stringBuffer.append("<value><boolean>"+(String)p_requestMap.get("PROMOTION_NOTIFICATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)p_requestMap.get("FIRST_IVR_CALL_SET_FLAG")))
	        	 {
	        		 stringBuffer.append("<member><name>firstIVRCallSetFlag</name><value><boolean>"+(String)p_requestMap.get("FIRST_IVR_CALL_SET_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)p_requestMap.get("ACCOUNT_ACTIVATION_FLAG")))
	        	 {
	        		 stringBuffer.append("<member><name>accountActivationFlag</name><value><boolean>"+(String)p_requestMap.get("ACCOUNT_ACTIVATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	stringBuffer.append("</struct></value></member>");
	        }
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
                    _log.error("generateGetAccountDetailRequest","Exception e::"+e.getMessage());
                    throw e;
            }//end of catch-Exception
            finally
            {
                    if(_log.isDebugEnabled())_log.debug("generateGetAccountDetailRequest","Exiting Request String:requestStr::"+requestStr);
            }//end of finally
            return requestStr;
    }//end of generateGetAccountDetailRequest

/**
 * This method is used to parse the response of GetAccountinDetails.
 * @param   String p_responseStr
 * @return  HashMap
 */
private HashMap parseGetAccountDetailResponse(String p_responseStr) throws Exception
{
   if(_log.isDebugEnabled()) _log.debug("parseGetAccountDetailResponse","Entered p_responseStr::"+p_responseStr);
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
                if(!InterfaceUtil.isNullString(serviceOfferingActiveFlag))
                    responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag.trim());
                else
                      responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag);
                indexEnd = p_responseStr.indexOf("</member>",indexStart);
            }
            
            indexStart = p_responseStr.indexOf("<member><name>responseCode");
	        tempIndex = p_responseStr.indexOf("responseCode",indexStart);
	        if(tempIndex>0)
	        {
	            responseCode = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("responseCode",responseCode.trim());
	            Object[] successList=CS5ClaroI.RESULT_OK.split(",");
	            //if(!CS5IdeaI.RESULT_OK.equals(responseCode))
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
	        else 
	        	  responseMap.put("accountValue1","0");

	        
	        indexStart= p_responseStr.indexOf("<member><name>aggregatedBalance1",indexEnd);
	        tempIndex = p_responseStr.indexOf("aggregatedBalance1",indexStart);
	        if(tempIndex>0)
	        {
	            String aggregatedBalance1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("aggregatedBalance1",aggregatedBalance1.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        
	        indexStart= p_responseStr.indexOf("<member><name>creditClearanceDate",indexEnd);
	        tempIndex = p_responseStr.indexOf("creditClearanceDate",indexStart);
	        if(tempIndex>0)
	        {
	            String creditClearanceDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("creditClearanceDate",creditClearanceDate.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        indexStart= p_responseStr.indexOf("<member><name>currency1",indexEnd);
	        tempIndex = p_responseStr.indexOf("currency1",indexStart);
	        if(tempIndex>0)
	        {
	            String currency1 = p_responseStr.substring("<string>".length()+p_responseStr.indexOf("<string>",tempIndex),p_responseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("currency1",currency1.trim());
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
	        
	        indexStart= p_responseStr.indexOf("<member><name>dedicatedAccountInformation",indexEnd);
	        tempIndex = p_responseStr.indexOf("dedicatedAccountInformation",indexStart);
	        
	        if(tempIndex>0)
	        {
	            String daXMLString = p_responseStr.substring("<array>".length()+p_responseStr.indexOf("<array>",tempIndex),p_responseStr.indexOf("</array>",tempIndex)).trim();
	            responseMap.put("dedicatedAccountInformation",daXMLString.trim());
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
	        else 
	        	 responseMap.put("supervisionExpiryDate",addDaysInUtilDate());
	        	
	        indexStart= p_responseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
	        tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate",indexStart);
	        if(tempIndex>0)
	        {
	            String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length()+p_responseStr.indexOf("<dateTime.iso8601>",tempIndex),p_responseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	        responseMap.put("serviceFeeExpiryDate", addDaysInUtilDate());
	       
	        /*indexStart= p_responseStr.indexOf("<member><name>languageIDCurrent",indexEnd);
	        tempIndex = p_responseStr.indexOf("languageIDCurrent",indexStart);
	        if(tempIndex>0)
	        {
	            String languageIDCurrent = p_responseStr.substring("<i4>".length()+p_responseStr.indexOf("<i4>",tempIndex),p_responseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("languageIDCurrent",languageIDCurrent.trim());
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }*/
	        
	        indexStart= p_responseStr.indexOf("<member><name>temporaryBlockedFlag",indexEnd);
	        tempIndex = p_responseStr.indexOf("temporaryBlockedFlag",indexStart);
	        if(tempIndex>0)
	        {
	            String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length()+p_responseStr.indexOf("<boolean>",tempIndex),p_responseStr.indexOf("</boolean>",tempIndex)).trim();
	            responseMap.put("temporaryBlockedFlag",temporaryBlockedFlag);
	            indexEnd = p_responseStr.indexOf("</member>",indexStart);
	        }
	        
	 
    }
    catch(Exception e)
    {
        _log.error("parseGetAccountDetailResponse","Exception e::"+e.getMessage());
        throw e;
    }//end catch-Exception
    finally
    {
        if(_log.isDebugEnabled()) _log.debug("parseGetAccountDetailResponse","Exited responseMap::"+responseMap);
    }//end of finally
            return responseMap;
}//end of parseGetAccountDetailsResponse


	public static String  addDaysInUtilDate()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE,CS5ClaroI.ADD_DAYS_INSTALLED);
		
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMdd");
		sdf.setLenient(false); // this is required else it will convert
		return sdf.format(cal.getTime());
		
	
	}
	
	//public boolean comparetags(String stringoffile, String strMsisdn, String strPtRefId, String strRetMsisdn, String stage) {
	public boolean comparetags(String stringoffile, HashMap<String,String> _responseMap) {
			
		//if (_debugEnabled)
                //_logger.debug("Entered in comparetags witrh data : ");

        java.util.ArrayList<String> strarray = new java.util.ArrayList<String>();
        String temp = "";
        String _key_value = "";
        String _name = "";
        String _value = "";
        int i = 0;
        int j = 0;
        boolean flag = false;
        try {
                String _firstline = "<?xml version=\"1.0\" encoding=\"utf-8\"";

                if (stringoffile.startsWith(_firstline)) {
                        int len = stringoffile.indexOf('>');
                        stringoffile = stringoffile.substring(38);
                }
                
                //////////////////////////////////////////////////////////
                
		                    String dedicatedStrStart="<member><name>dedicatedAccountInformation</name><value><array>";
		                    String dedicatedStrEnd="</array></value></member>";
		                    String dedicatedAccInfo="";
		                    try{
				                    int dedicatedStrStartIndex=stringoffile.indexOf(dedicatedStrStart);
				                    if(dedicatedStrStartIndex !=-1){
				                    	int dedicatedStrEndIndex=stringoffile.indexOf(dedicatedStrEnd,dedicatedStrStartIndex);
				                    
				                    	if(dedicatedStrEndIndex !=-1){
				                    		dedicatedStrEndIndex=dedicatedStrEndIndex+dedicatedStrEnd.length();
				                    		dedicatedAccInfo=stringoffile.substring(dedicatedStrStartIndex,dedicatedStrEndIndex);
				                    		stringoffile=stringoffile.substring(0,dedicatedStrStartIndex)+stringoffile.substring(dedicatedStrEndIndex);
				        //            		if(_iNTraceEnabled!=null && _iNTraceEnabled.equalsIgnoreCase("Yes")){
			              //  					TransactionLogger.logMessage("[Class] : CS4Client ::: [Method] : compareTags ::: [Recharge No] : "+strPtRefId+" ::: [MSISDN] : "+strMsisdn+" ::: [RETAILER MSISDN] : "+strRetMsisdn+" ::: [STAGE] : "+stage+" ::: [Comment] : Modified XML="+stringoffile+" dedicatedAccInfo="+dedicatedAccInfo);
			                //				}
				                    	}
				                    	
				                    }
		                    }catch(Exception ex){
		                    //	 TransactionLogger.logMessage("[Class] : CS4Client ::: [Method] : compareTags ::: [Recharge No] : "+strPtRefId+" ::: [MSISDN] : "+strMsisdn+" ::: [RETAILER MSISDN] : "+strRetMsisdn+" ::: [STAGE] : "+stage+" ::: [Comment] : Exception in extracting Dedicated Account information" + " ::: IN Response XML="+stringoffile);
		                    }
		                    
		                    String serviceOfferingsStart="<member><name>serviceOfferings</name><value><array>";
		                    String serviceOfferingsEnd="</array></value></member>";
		                    String serviceOfferingsInfo="";
		                    try{
				                    int serviceOfferingsStartIndex=stringoffile.indexOf(serviceOfferingsStart);
				                    if(serviceOfferingsStartIndex !=-1){
				                    	int serviceOfferingsEndIndex=stringoffile.indexOf(serviceOfferingsEnd,serviceOfferingsStartIndex);
				                    
				                    	if(serviceOfferingsEndIndex !=-1){
				                    		serviceOfferingsEndIndex=serviceOfferingsEndIndex+serviceOfferingsEnd.length();
				                    		serviceOfferingsInfo=stringoffile.substring(serviceOfferingsStartIndex,serviceOfferingsEndIndex);
				                    		stringoffile=stringoffile.substring(0,serviceOfferingsStartIndex)+stringoffile.substring(serviceOfferingsEndIndex);
				  //                  		if(_iNTraceEnabled!=null && _iNTraceEnabled.equalsIgnoreCase("Yes")){
			        //        					TransactionLogger.logMessage("[Class] : CS4Client ::: [Method] : compareTags ::: [Recharge No] : "+strPtRefId+" ::: [MSISDN] : "+strMsisdn+" ::: [RETAILER MSISDN] : "+strRetMsisdn+" ::: [STAGE] : "+stage+" ::: [Comment] : Modified XML="+stringoffile+" serviceOfferingsInfo="+serviceOfferingsInfo);
			          //      				}
				                    		
				                    	}
				                    	
				                    }
				             }catch(Exception ex){
				//            	 TransactionLogger.logMessage("[Class] : CS4Client ::: [Method] : compareTags ::: [Recharge No] : "+strPtRefId+" ::: [MSISDN] : "+strMsisdn+" ::: [RETAILER MSISDN] : "+strRetMsisdn+" ::: [STAGE] : "+stage+" ::: [Comment] : Exception in extracting Service Offering information" + " ::: IN Response XML="+stringoffile);
				             }
                /////////////////////////////////////////////////////////

                while (stringoffile.length() != 0) {
                        i = stringoffile.indexOf('<');
                        if (i == 0) {
                                j = stringoffile.indexOf('>');
                                if (stringoffile.charAt(1) == '/') {
                                        _key_value = stringoffile.substring(2, j);
                                        int _strarraysize = strarray.size();
                                        //temp = (String) strarray.get(_strarraysize - 1);
                                        temp =  strarray.get(_strarraysize - 1);

                                        if (temp.equals(_key_value))
                                                strarray.remove(_strarraysize - 1);
                                        stringoffile = stringoffile.substring(j + 1);
                                } else {
                                        _key_value = stringoffile.substring(1, j);
                                        stringoffile = stringoffile.substring(j + 1);
                                        strarray.add(_key_value);
                                }
                        } else {
                                j = stringoffile.indexOf('<');

                                _key_value = stringoffile.substring(0, j);
                                stringoffile = stringoffile.substring(j);
                                int _strarraysize = strarray.size();
                                //temp = (String) strarray.get(_strarraysize - 1);
                                temp =  strarray.get(_strarraysize - 1);
                                if (temp.equals("name")) {
                                        _name = _key_value;
                                } else {
                                        _value = _key_value;
                                }
                                _responseMap.put(_name, _value);
                        }
                }

                if (strarray.size() == 0)
                        flag = true;
        } catch (Exception e) {
        	//TransactionLogger.logMessage("[Class] : CS4Client ::: [Method] : compareTags ::: [Recharge No] : "+strPtRefId+" ::: [MSISDN] : "+strMsisdn+" ::: [RETAILER MSISDN] : "+strRetMsisdn+" ::: [STAGE] : "+stage+" ::: [Comment] : Exception in Parsing" + " ::: IN Response XML="+stringoffile);
        }
        return flag;
}
	public static void main(String [] args){
		try{
		HashMap<String,String> responseMap=new HashMap<String,String>();
		//String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountValue1</name><value><string>437257</string></value></member><member><name>aggregatedBalance1</name><value><string>437257</string></value></member><member><name>creditClearanceDate</name><value><dateTime.iso8601>20180823T12:00:00+0000</dateTime.iso8601></value></member><member><name>currency1</name><value><string>BDT</string></value></member><member><name>languageIDCurrent</name><value><i4>4</i4></value></member><member><name>originTransactionID</name><value><string>716092513174100012</string></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>53</i4></value></member><member><name>serviceFeeExpiryDate</name><value><dateTime.iso8601>20180822T12:00:00+0000</dateTime.iso8601></value></member><member><name>serviceRemovalDate</name><value><dateTime.iso8601>20180823T12:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionExpiryDate</name><value><dateTime.iso8601>20170822T12:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountValue1</name><value><string>-818</string></value></member><member><name>creditClearanceDate</name><value><dateTime.iso8601>20110301T12:00:00+0000</dateTime.iso8601></value></member><member><name>currency1</name><value><string>BDT</string></value></member><member><name>dedicatedAccountInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>1</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>2</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>3</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>4</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>5</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>6</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>7</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>8</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>9</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>10</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value></data></array></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>originTransactionID</name><value><string>20100818095958</string></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>93</i4></value></member><member><name>serviceFeeExpiryDate</name><value><dateTime.iso8601>20100902T12:00:00+0000</dateTime.iso8601></value></member><member><name>serviceRemovalDate</name><value><dateTime.iso8601>20110301T12:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionExpiryDate</name><value><dateTime.iso8601>20100902T12:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";
		boolean flag=new CS5ClaroRequestFormatter().comparetags(xml, responseMap);
		System.out.println("Flag="+flag);
		System.out.println("responseMap="+responseMap);
		}catch(Exception e){e.printStackTrace();}
	}

}


