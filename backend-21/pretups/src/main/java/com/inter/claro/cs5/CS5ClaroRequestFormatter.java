
package com.inter.claro.cs5;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceUtil;



public class CS5ClaroRequestFormatter 
{
	private static final Log log = LogFactory.getLog(CS5ClaroRequestFormatter.class);
	
	static String promobal="";
    public CS5ClaroRequestFormatter() throws Exception
    {
    	/*
    	 * 
    	 * */
       
    }//end of CS5ClaroRequestFormatter[constructor]
     

 
    /**
     * This method is used to parse the response string based on the type of Action.
     * @param	int pAction
     * @param	HashMap pMap
     * @return	String.
     * @throws	Exception
     */
	protected String generateRequest(int pAction, Map pMap) throws Exception 
	{
       if(log.isDebugEnabled())
    	   log.debug("generateRequest",PretupsI.ENTERED+" pAction::"+pAction+" map::"+pMap);
		String str=null;
		pMap.put("action",String.valueOf(pAction));
		try
		{
			switch(pAction)
			{
				case CS5ClaroI.ACTION_ACCOUNT_INFO: 
				{
					str=generateGetAccountInfoRequest(pMap);
					break;	
				}
				case CS5ClaroI.ACTION_ACCOUNT_DETAILS:
				{
                         str=generateGetAccountDetailRequest(pMap);
                         break;
				}
				case CS5ClaroI.ACTION_RECHARGE_CREDIT: 
				{
				    str=generateRechargeCreditRequest(pMap);
					break;	
				}
				case CS5ClaroI.ACTION_IMMEDIATE_DEBIT: 
				{
					str=generateImmediateDebitRequest(pMap);
					break;	
				}
			}//end of switch block
		}//end of try block
		catch(Exception e)
		{
			log.error("generateRequest",PretupsI.EXCEPTION+e.getMessage());
			throw e;
		}//end of catch-Exception 
		finally
		{
			if(log.isDebugEnabled())
				log.debug("generateRequest","Exited Request String: str::"+str);
		}//end of finally
		return str;
	}//end of generateRequest
    /**
     * This method is used to parse the response.
     * @param	int pAction
     * @param	String pResponseStr
     * @return	HashMap
     * @throws	Exception
     */
    public Map parseResponse(int pAction,String pResponseStr,Map pResponseMap) throws Exception
    {
	    if(log.isDebugEnabled())
	    	log.debug("parseResponse",PretupsI.ENTERED+" pAction::"+pAction+" pResponseStr:: "+pResponseStr);
		Map map=null;
		try
		{
			switch(pAction)
			{
				case CS5ClaroI.ACTION_ACCOUNT_INFO: 
				{
					map=parseGetAccountInfoResponse(pResponseStr);
					break;	
				}
			    case CS5ClaroI.ACTION_ACCOUNT_DETAILS:
                {
                        map=parseGetAccountDetailResponse(pResponseStr);
                        map.putAll(pResponseMap);
                        break;
                }
				case CS5ClaroI.ACTION_RECHARGE_CREDIT: 
				{
					map=parseRechargeCreditResponse(pResponseStr);
					break;	
				}
				case CS5ClaroI.ACTION_IMMEDIATE_DEBIT: 
				{
					map=parseImmediateDebitResponse(pResponseStr);
					break;	
				}
			}//end of switch block
		}//end of try block
		catch(Exception e)
		{
			log.error("parseResponse",PretupsI.EXCEPTION+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(log.isDebugEnabled())
				log.debug("parseResponse","Exiting map::"+map);
		}//end of finally
		return map;	
	}//end of parseResponse

	/**
	 * This method is used to generate the request for getting account information.
	 * @param	HashMap	pRequestMap
	 * @return	String
	 * @throws	Exception
	 */
    private String generateGetAccountInfoRequest(Map pRequestMap) throws Exception
	{
	    if(log.isDebugEnabled())
	    	log.debug("generateGetAccountInfoRequest",PretupsI.ENTERED+" pRequestMap::"+pRequestMap);
		String requestStr= null;
		StringBuilder stringBuilder = null;
		try
		{
		    stringBuilder=new StringBuilder(1028);
	        stringBuilder.append("<?xml version='1.0'?>");
	        stringBuilder.append("<methodCall>");
	        stringBuilder.append("<methodName>GetBalanceAndDate</methodName>");
	        stringBuilder.append(startRequestTag());
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originNodeType</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("NODE_TYPE")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the originHostName
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originHostName</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("HOST_NAME")+"</string></value>");
	        stringBuilder.append("</member>");
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originTransactionID</name>");
	        stringBuilder.append("<value><string>"+(String)pRequestMap.get("IN_RECON_ID")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the originTimeStamp
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originTimeStamp</name>");
	        stringBuilder.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
	        stringBuilder.append("</member>");
	        //Set the optional parameter subscriberNumberNAI if present.
	        if(!InterfaceUtil.isNullString((String)pRequestMap.get("SubscriberNumberNAI")))
	        {
		        stringBuilder.append("<member>");
		        stringBuilder.append("<name>subscriberNumberNAI</name>");
		        stringBuilder.append("<value><i4>"+pRequestMap.get("SubscriberNumberNAI")+"</i4></value>");
		        stringBuilder.append("</member>");
	        }
	        
	        //message cabablity flag 
	        if(!InterfaceUtil.isNullString((String)pRequestMap.get("MESSAGE_CAPABILITY_FLAG")))
	        {
	        	stringBuilder.append("<member><name>messageCapabilityFlag</name><value><struct>");
	        	
	        	if(!InterfaceUtil.isNullString((String)pRequestMap.get("PROMOTION_NOTIFICATION_FLAG")))
	        	 {
	        		 stringBuilder.append("<member><name>promotionNotificationFlag</name>");
	        		 stringBuilder.append("<value><boolean>"+(String)pRequestMap.get("PROMOTION_NOTIFICATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)pRequestMap.get("FIRST_IVR_CALL_SET_FLAG")))
	        	 {
	        		 stringBuilder.append("<member><name>firstIVRCallSetFlag</name><value><boolean>"+(String)pRequestMap.get("FIRST_IVR_CALL_SET_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)pRequestMap.get("ACCOUNT_ACTIVATION_FLAG")))
	        	 {
	        		 stringBuilder.append("<member><name>accountActivationFlag</name><value><boolean>"+(String)pRequestMap.get("ACCOUNT_ACTIVATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	stringBuilder.append("</struct></value></member>");
	        }
	        //dedicated account selection on manual selection 
	        if("Y".equalsIgnoreCase((String)pRequestMap.get("DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL")))
	        {
	        	 stringBuilder.append("<member>");
	        	 stringBuilder.append("<name>dedicatedAccountSelection</name>");
	        	 stringBuilder.append("<value><array><data>");
	        	 //dedicated account selectionList 
	        	String dedicatedAccount= (String)pRequestMap.get("DEDICATED_ACCOUNT_SELECTION_ACCOUNTID");
	        	
	        	String[] dAccount= dedicatedAccount.split(":");
	        	
	        	for (int i=0;i<dAccount.length;i++)
	        	{
	        		stringBuilder.append("<value>");
	        		stringBuilder.append("<struct>");
	        		stringBuilder.append("<member><name>dedicatedAccountIDFirst</name><value><i4>"+dAccount[i]+"</i4></value></member>");
	        		stringBuilder.append("</struct>");
	        		stringBuilder.append("</value>");
	    	        
	        	}
	        	 stringBuilder.append("</data></array></value></member>");
	        
	        }
	        else if("Y".equalsIgnoreCase((String)pRequestMap.get("DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE")))
	        {
	        	stringBuilder.append("<member>");
	        	stringBuilder.append("<name>dedicatedAccountSelection</name>");
	        	stringBuilder.append("<value><array><data>");
	        	String dedicatedAccountIDFirst= (String)pRequestMap.get("DEDICATED_ACCOUNT_ID_FIRST_RANGE");
	        	String dedicatedAccountIDLast= (String)pRequestMap.get("DEDICATED_ACCOUNT_ID_LAST_RANGE");
	        	
	       
	        	String dedicatedDefAccountIDFirst= (String)pRequestMap.get("DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE");
	        	String dedicatedDefAccountIDLast= (String)pRequestMap.get("DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE");
	  	        
	         	int daFirstRange= Integer.parseInt(dedicatedAccountIDFirst);
        		int daLastRange=Integer.parseInt(dedicatedAccountIDLast);
        		int daDefFirstRange=Integer.parseInt(dedicatedDefAccountIDFirst);
        		int daDefLastRange=Integer.parseInt(dedicatedDefAccountIDLast);
        			
        			//DefaultDAFirst=61 ( logic will be any value smaller compare with DAselectionFirst)
        			//DefaultDALast=105 (logic will be any value greater compare with DAselectionLast)
        		
        	
        		daFirstRange=	Math.min(daFirstRange, daDefFirstRange);
        		daLastRange=	Math.max(daLastRange, daDefLastRange);
	        	stringBuilder.append("<value>");
	        	stringBuilder.append("<struct>");
	        	stringBuilder.append("<member><name>dedicatedAccountIDFirst</name><value><i4>"+daFirstRange+"</i4></value></member>");
	        	stringBuilder.append("<member><name>dedicatedAccountIDLast</name><value><i4>"+daLastRange+"</i4></value></member>");
	        	stringBuilder.append("</struct>");
	        	stringBuilder.append("</value>");
	  	    	        
	        	stringBuilder.append("</data></array></value></member>");
	  	        
	        }
	        //Set the subscriberNumber after adding or removing the prefix defined in the INFile.
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>subscriberNumber</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("FILTER_MSISDN")+"</string></value>");
	        stringBuilder.append("</member>");
	        stringBuilder.append(endRequestTag());
	        stringBuilder.append("</methodCall>");
	        requestStr = stringBuilder.toString();
		}//end of try-block
		catch(Exception e)
		{
			log.error("generateGetAccountInfoRequest",PretupsI.EXCEPTION+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(log.isDebugEnabled())
				log.debug("generateGetAccountInfoRequest","Exiting Request String:requestStr::"+requestStr);
		}//end of finally
		return requestStr;
	}//end of generateGetAccountInfoRequest
    /**
     * 
     * @param pMap
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequest(Map pRequestMap) throws Exception
    {
	    if(log.isDebugEnabled())
	    	log.debug("generateRechargeCreditRequest",PretupsI.ENTERED+" pRequestMap::"+pRequestMap);
		String requestStr= null;
		StringBuilder stringBuilder = null;
		try
		{
			stringBuilder = new StringBuilder(1028);
	        stringBuilder.append("<?xml version='1.0'?>");
	        stringBuilder.append("<methodCall>");
	        stringBuilder.append("<methodName>Refill</methodName>");
	        stringBuilder.append(startRequestTag());
	        //Set the origin originNodeType
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originNodeType</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("NODE_TYPE")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the originHostName 
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originHostName</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("HOST_NAME")+"</string></value>");
	        stringBuilder.append("</member>");
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originTransactionID</name>");
	        stringBuilder.append("<value><string>"+(String)pRequestMap.get("IN_RECON_ID")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the originTimeStamp
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originTimeStamp</name>");
	        stringBuilder.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
	        stringBuilder.append("</member>");
	        //Check the optional value of SubscriberNumberNAI if it is not null set it.	 
	        if(!InterfaceUtil.isNullString((String)pRequestMap.get("SubscriberNumberNAI")))
	        {
	        	stringBuilder.append("<member>");
	        	stringBuilder.append("<name>subscriberNumberNAI</name>");
	        	stringBuilder.append("<value><i4>"+pRequestMap.get("SubscriberNumberNAI")+"</i4></value>");
	        	stringBuilder.append("</member>");	
	        }
	        //Set subscriberNumber and add or remove the msisdn if required.
        	
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>subscriberNumber</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("FILTER_MSISDN")+"</string></value>");
	        stringBuilder.append("</member>");
	        
	        //Set the transfer_amount to transactionAmount
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>transactionAmount</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("transfer_amount")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the transaction_currency defined into INFile for transactionCurrency.
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>transactionCurrency</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("CURRENCY")+"</string></value>");
	        stringBuilder.append("</member>");
		
		
	        //Set the card group for paymentProfileID
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>refillProfileID</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("CARD_GROUP")+"</string></value>");
	        stringBuilder.append("</member>");
	       

	      // value of external data 1 tag 
	        if (pRequestMap.get("GEO_DOMAIN_CODE") != null) 
	        {
	        	stringBuilder.append("<member>");
	        	stringBuilder.append("<name>externalData1</name>");
	        	stringBuilder.append("<value><string>"+pRequestMap.get("GEO_DOMAIN_CODE")+"</string></value>");
	        	stringBuilder.append("</member>");
	        }
	   
	      //Set the  value of ExternalData2, .
	        if (pRequestMap.get("SENDER_MSISDN") != null) 
	        {
	        	stringBuilder.append("<member>" );
	        	stringBuilder.append("<name>externalData2</name>" );
	        	stringBuilder.append("<value><string>" + pRequestMap.get("SENDER_MSISDN")+"</string></value>" );
	        	stringBuilder.append("</member>" );
	        }
	        else
	        {
	        	stringBuilder.append("<member>" );
	        	stringBuilder.append("<name>externalData2</name>" );
	        	stringBuilder.append("<value><string>0</string></value>" );
	        	stringBuilder.append("</member>" );
	        }
					
	     
	        if( !( InterfaceUtil.isNullString( (String)pRequestMap.get("BONUS_BUNDLE_CODES") )&& InterfaceUtil.isNullString( (String)pRequestMap.get("BONUS_BUNDLE_VALUES") ) ) )
	        {
	        	stringBuilder.append("<member>");
	        	stringBuilder.append("<name>externalData3</name>");
	        	String bundlename=(String)pRequestMap.get("BONUS_BUNDLE_CODES");
	        	String bundlevalue=(String)pRequestMap.get("BONUS_BUNDLE_VALUES");
	        	 
	        	String finalextd3="";
	        	if(bundlename.contains("|"))
	        	{
	        		String[] bundlenamearr=bundlename.split("\\|");
	        		String[] bundlevaluearr=bundlevalue.split("\\|");
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
	        	
	        	stringBuilder.append("<value><string>"+finalextd3+"</string></value>");
	        	stringBuilder.append("</member>");
	        }
	     
	        	
	        if(!InterfaceUtil.isNullString((String)pRequestMap.get("GET_INFO_10")))
	        {
	        	stringBuilder.append("<member>");
	        	stringBuilder.append("<name>externalData4</name>");
	        	stringBuilder.append("<value><string>"+pRequestMap.get("GET_INFO_10")+"</string></value>");
	        	stringBuilder.append("</member>");
	        }
	        String accountsflag=(String)pRequestMap.get("REFILL_ACNT_AFTER_FLAG");
	        if(("1").equalsIgnoreCase(accountsflag)){
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>requestRefillAccountAfterFlag</name>");
	        stringBuilder.append("<value><boolean>"+accountsflag+"</boolean></value>");
	        stringBuilder.append("</member>");
	        }
	       
	        
	        stringBuilder.append(endRequestTag());
	        stringBuilder.append("</methodCall>");   
	        requestStr = stringBuilder.toString();       
	        
		}//end of try-block
		catch(Exception e)
		{
			log.error("generateRechargeCreditRequest",PretupsI.EXCEPTION+e);
			throw e;
		}//end of catch-Exception
		finally
		{
			if(log.isDebugEnabled())
				log.debug("generateRechargeCreditRequest","Exiting Request requestStr::"+requestStr);
		}//end of finally
		return requestStr;
    }//end of generateRechargeCreditRequest

 
    /**
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String generateImmediateDebitRequest(Map pRequestMap) throws Exception
    {
		if(log.isDebugEnabled())
			log.debug("generateImmediateDebitRequest",PretupsI.ENTERED+" pRequestMap::"+pRequestMap);
		String requestStr= null;
		StringBuilder stringBuilder = null;
		try
		{
			stringBuilder = new StringBuilder(1028);
	        stringBuilder.append("<?xml version='1.0'?>");
	        stringBuilder.append("<methodCall>");
	        stringBuilder.append("<methodName>UpdateBalanceAndDate</methodName>");
	        stringBuilder.append(startRequestTag());
	        //Set the origin originNodeType
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originNodeType</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("NODE_TYPE")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the originHostName 
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originHostName</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("HOST_NAME")+"</string></value>");
	        stringBuilder.append("</member>");
	        stringBuilder.append("<member>");
	        //Set the originTransactionID
	        stringBuilder.append("<name>originTransactionID</name>");
	        stringBuilder.append("<value><string>"+(String)pRequestMap.get("IN_RECON_ID")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the originTimeStamp
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>originTimeStamp</name>");
	        stringBuilder.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
	        stringBuilder.append("</member>");
	        //Check the optional value of SubscriberNumberNAI if it is not null set it.	        
        	stringBuilder.append("<member>");
        	stringBuilder.append("<name>subscriberNumberNAI</name>");
        	stringBuilder.append("<value><i4>"+pRequestMap.get("SubscriberNumberNAI")+"</i4></value>");
        	stringBuilder.append("</member>");	     
	        //Set subscriberNumber and add or remove the msisdn if required.
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>subscriberNumber</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("FILTER_MSISDN")+"</string></value>");
	        stringBuilder.append("</member>");
	        //Set the transaction_currency defined into INFile for transactionCurrency.
	        stringBuilder.append("<member>");
	        stringBuilder.append("<name>transactionCurrency</name>");
	        stringBuilder.append("<value><string>"+pRequestMap.get("CURRENCY")+"</string></value>");
	        stringBuilder.append("</member>");
	        
	        String dedicatedAccount100=(String)pRequestMap.get("DEDICATED_ACCOUNT_FOR_POSTPAID"); 
	        if("POST".equals((String)pRequestMap.get("SUBSCRIBER_TYPE")))
	        {
		        stringBuilder.append("<member><name>dedicatedAccountUpdateInformation</name><value><array><data><value><struct>");
		        stringBuilder.append("<member><name>dedicatedAccountID</name><value><i4>"+dedicatedAccount100+"</i4></value></member>");
		        stringBuilder.append("<member><name>adjustmentAmountRelative</name><value><string>"+pRequestMap.get("transfer_amount")+"</string></value></member>");
		        stringBuilder.append(" </struct></value></data></array></value></member>");
	        }
	        else
	        {
	        //Set the transfer_amount to transactionAmount
	        	stringBuilder.append("<member>");
	        	stringBuilder.append("<name>adjustmentAmountRelative</name>");
	        	stringBuilder.append("<value><string>"+pRequestMap.get("transfer_amount")+"</string></value>");
	        	stringBuilder.append("</member>");
	        }        
	        stringBuilder.append(endRequestTag());
	        stringBuilder.append("</methodCall>");
	        requestStr = stringBuilder.toString();	       
		}//end of try-Block
		catch(Exception e)
		{
			log.error("generateImmediateDebitRequest",PretupsI.EXCEPTION+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(log.isDebugEnabled())
				log.debug("generateImmediateDebitRequest","Exiting  requestStr::"+requestStr);
		}//end of finally
        return requestStr;
    }//end of generateImmediateDebitRequest
    /**
     * This method is used to parse the response of GetAccountinformation.
     * @param	String pResponseStr
     * @return	HashMap
     */
    private HashMap parseGetAccountInfoResponse(String pResponseStr) throws Exception
    {
        HashMap responseMap = null;
        int indexStart=0;
        int indexEnd=0;
        int tempIndex=0;
        String responseCode = null;
        try
        {
            responseMap = new HashMap();
            indexStart = pResponseStr.indexOf("<fault>");
	        if(indexStart>0)
	        {
	            tempIndex = pResponseStr.indexOf("faultCode",indexStart);
	            String faultCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</4>",tempIndex));
	            responseMap.put("faultCode",faultCode.trim());
	            tempIndex = pResponseStr.indexOf("faultString",tempIndex);
	            String faultString = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex));
	            responseMap.put("faultString",faultString.trim());	            
	            return responseMap;
	        }
	        
	        indexStart = pResponseStr.indexOf("<member><name>responseCode");
	        tempIndex = pResponseStr.indexOf("responseCode",indexStart);
	        if(tempIndex>0)
	        {
	            responseCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("responseCode",responseCode.trim());
	            Object[] successList=CS5ClaroI.RESULT_OK.split(",");
	            if(!Arrays.asList(successList).contains(responseCode))
	                return responseMap;
	        }

	        indexStart= pResponseStr.indexOf("<member><name>accountValue1",indexEnd);
	        tempIndex = pResponseStr.indexOf("accountValue1",indexStart);
	        if(tempIndex>0)
	        {
	            String accountValue1 = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("accountValue1",accountValue1.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }

	        tempIndex = pResponseStr.indexOf("dedicatedAccountInformation",indexStart);
	        
	        if(tempIndex>0)
	        {
	            String daXMLString = pResponseStr.substring("<array>".length()+pResponseStr.indexOf("<array>",tempIndex),pResponseStr.indexOf("</array>",tempIndex)).trim();
	            responseMap.put("dedicatedAccountInformation",daXMLString.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        indexStart= pResponseStr.indexOf("<member><name>serviceClassCurrent",indexEnd);
	        tempIndex = pResponseStr.indexOf("serviceClassCurrent",indexStart);
	        if(tempIndex>0)
	        {
	            String serviceClassCurrent = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("serviceClassCurrent",serviceClassCurrent.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        
	        indexStart= pResponseStr.indexOf("<member><name>supervisionExpiryDate",indexEnd);
	        tempIndex = pResponseStr.indexOf("supervisionExpiryDate",indexStart);
	        if(tempIndex>0)
	        {
	            String supervisionExpiryDate = pResponseStr.substring("<dateTime.iso8601>".length()+pResponseStr.indexOf("<dateTime.iso8601>",tempIndex),pResponseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("supervisionExpiryDate",getDateString(supervisionExpiryDate.trim()));
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        indexStart= pResponseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
	        tempIndex = pResponseStr.indexOf("serviceFeeExpiryDate",indexStart);
	        if(tempIndex>0)
	        {
	            String serviceFeeExpiryDate = pResponseStr.substring("<dateTime.iso8601>".length()+pResponseStr.indexOf("<dateTime.iso8601>",tempIndex),pResponseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        
	        indexStart= pResponseStr.indexOf("<member><name>temporaryBlockedFlag",indexEnd);
	        tempIndex = pResponseStr.indexOf("temporaryBlockedFlag",indexStart);
	        if(tempIndex>0)
	        {
	            String temporaryBlockedFlag = pResponseStr.substring("<boolean>".length()+pResponseStr.indexOf("<boolean>",tempIndex),pResponseStr.indexOf("</boolean>",tempIndex)).trim();
	            responseMap.put("temporaryBlockedFlag",temporaryBlockedFlag);
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
        }
        catch(Exception e)
        {
            log.error("parseGetAccountInfoResponse",PretupsI.EXCEPTION+e.getMessage());
            throw e;
        }//end catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug("parseGetAccountInfoResponse",PretupsI.EXITED+" responseMap::"+responseMap);
        }//end of finally
		return responseMap;
    }
    //end of parseGetAccountInfoResponse
    /**
     * This method is used to parse the credit response. 
     * @param pResponseStr
     * @return HashMap
     * @throws Exception
     */
    public Map parseRechargeCreditResponse(String pResponseStr) throws Exception
    {
        if(log.isDebugEnabled())
        	log.debug("parseRechargeCreditResponse",PretupsI.ENTERED+" pResponseStr::"+pResponseStr);
        HashMap responseMap = null;
        int indexStart=0;
        int indexEnd=0;
        int tempIndex=0;
        String responseCode = null;
        try
        {
            responseMap = new HashMap();
 	        
            indexStart = pResponseStr.indexOf("<fault>");
	        if(indexStart>0)
	        {
	            tempIndex = pResponseStr.indexOf("faultCode",indexStart);
	            String faultCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</4>",tempIndex));
	            responseMap.put("faultCode",faultCode.trim());
	            tempIndex = pResponseStr.indexOf("faultString",tempIndex);
	            String faultString = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex));
	            responseMap.put("faultString",faultString.trim());	            
	            return responseMap;
	        }
	        
 	        
 	       
         	indexStart= pResponseStr.indexOf("<member><name>originTransactionID",indexEnd);
 	        tempIndex = pResponseStr.indexOf("originTransactionID",indexStart);
 	        if(tempIndex>0)
 	        {
 	            String originTransactionID = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
 	            responseMap.put("originTransactionID",originTransactionID.trim());
 	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
 	        }
 	        
 	        indexStart= pResponseStr.indexOf("<member><name>transactionAmount",indexEnd);
 	        tempIndex = pResponseStr.indexOf("transactionAmount",indexStart);
 	        if(tempIndex>0)
 	        {
 	            String transactionAmount = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
 	            responseMap.put("transactionAmount",transactionAmount.trim());
 	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
 	        }
 	       	   
 	        //earlier commented from here
 	        indexStart= pResponseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
 	        tempIndex = pResponseStr.indexOf("serviceFeeExpiryDate",indexStart);
 	        if(tempIndex>0)
 	        {
 	            String serviceFeeExpiryDate = pResponseStr.substring("<dateTime.iso8601>".length()+pResponseStr.indexOf("<dateTime.iso8601>",tempIndex),pResponseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
 	            responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
 	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
 	        }
 	        
 	        indexStart= pResponseStr.indexOf("<member><name>supervisionExpiryDate",indexEnd);
 	        tempIndex = pResponseStr.indexOf("supervisionExpiryDate",indexStart);
 	        if(tempIndex>0)
 	        {
 	            String supervisionExpiryDate = pResponseStr.substring("<dateTime.iso8601>".length()+pResponseStr.indexOf("<dateTime.iso8601>",tempIndex),pResponseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
 	            responseMap.put("supervisionExpiryDate",getDateString(supervisionExpiryDate.trim()));
 	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
 	        }
 	        //commented till here
 	        
 	        indexStart= pResponseStr.indexOf("<member><name>accountValue1",indexEnd);
 	        tempIndex = pResponseStr.indexOf("accountValue1",indexStart);
 	        if(tempIndex>0)
 	        {
 	            String accountValue1 = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
 	            responseMap.put("accountValue1",accountValue1.trim());
 	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
 	        }
 	       indexStart= pResponseStr.indexOf("<member><name>refillValuePromotion",indexEnd);
	        tempIndex = pResponseStr.indexOf("refillAmount1",indexStart);
	        if(tempIndex>0)
	        {
	            String refillAmount1 = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("refillAmount1",refillAmount1.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        
 	       indexStart = pResponseStr.indexOf("<member><name>responseCode");
	        tempIndex = pResponseStr.indexOf("responseCode",indexStart);
	        if(tempIndex>0)
	        {
	            responseCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("responseCode",responseCode.trim());
	            
	        }
         }
        catch(Exception e)
        {
            log.error("parseGetAccountInfoResponse",PretupsI.EXCEPTION+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug("parseRechargeCreditResponse",PretupsI.EXITED+" responseMap::"+responseMap);
        }//end of finally
		return responseMap;
    }//end of parseRechargeCreditResponse

    /**
     * This method is used to parse the credit response. 
     * @param pResponseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseImmediateDebitResponse(String pResponseStr) throws Exception
    {
        if(log.isDebugEnabled())
        	log.debug("parseImmediateDebitResponse",PretupsI.ENTERED+" pResponseStr::"+pResponseStr);
        HashMap responseMap = null;
        int indexStart=0;
        int indexEnd=0;
        int tempIndex=0;
        String responseCode = null;
        try
        {
        	responseMap = new HashMap();
  	       
        	indexStart = pResponseStr.indexOf("<fault>");
	        if(indexStart>0)
	        {
	            tempIndex = pResponseStr.indexOf("faultCode",indexStart);
	            String faultCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</4>",tempIndex));
	            responseMap.put("faultCode",faultCode.trim());
	            tempIndex = pResponseStr.indexOf("faultString",tempIndex);
	            String faultString = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex));
	            responseMap.put("faultString",faultString.trim());	            
	            return responseMap;
	        }
	        
  	        indexStart = pResponseStr.indexOf("<member><name>responseCode");
  	        tempIndex = pResponseStr.indexOf("responseCode",indexStart);
  	        if(tempIndex>0)
  	        {
  	            responseCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</i4>",tempIndex)).trim();
  	            responseMap.put("responseCode",responseCode.trim());
	            Object[] successList=CS5ClaroI.RESULT_OK.split(",");
	            if(!Arrays.asList(successList).contains(responseCode))
  	                return responseMap;
  	        }
  	       
          	indexStart= pResponseStr.indexOf("<member><name>originTransactionID",indexEnd);
  	        tempIndex = pResponseStr.indexOf("originTransactionID",indexStart);
  	        if(tempIndex>0)
  	        {
  	            String originTransactionID = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
  	            responseMap.put("originTransactionID",originTransactionID.trim());
  	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
  	        }
  	      indexStart= pResponseStr.indexOf("<member><name>accountValue1",indexEnd);
	        tempIndex = pResponseStr.indexOf("accountValue1",indexStart);
	        if(tempIndex>0)
	        {
	            String accountValue1 = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("accountValue1",accountValue1.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
        }
        catch(Exception e)
        {
            log.error("parseImmediateDebitResponse",PretupsI.EXCEPTION+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled())
            	log.debug("parseImmediateDebitResponse",PretupsI.EXITED+" responseMap::"+responseMap);
        }
		return responseMap;
    }//end of parseImmediateDebitResponse
    /**
     * Method to get the Transaction date and time with specified format.
     * @return String
     * @throws Exception
     */
    
    private String getISO8601DateTime() {
		SimpleDateFormat ISO8601Local = new SimpleDateFormat(
				"yyyyMMdd'T'HH:mm:ss");
		TimeZone timeZone = TimeZone.getDefault();
		ISO8601Local.setTimeZone(timeZone);
		DecimalFormat twoDigits = new DecimalFormat("00");

		Date now = new Date();
		int offset = ISO8601Local.getTimeZone().getOffset(now.getTime());
		String sign = "+";
		if (offset < 0) {
			offset = -offset;
			sign = "-";
		}
		int hours = offset / 3600000;
		int minutes = (offset - hours * 3600000) / 60000;
		return ISO8601Local.format(now) + sign + twoDigits.format(hours) + twoDigits.format(minutes);
	}
    
    
    /**
     * This method is used to construct the string that contains the start elements of request xml.
     * @return String
     */
	private String startRequestTag()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<params>");
        stringBuilder.append("<param>");
        stringBuilder.append("<value>");
        stringBuilder.append("<struct>");
        return stringBuilder.toString();
    }
    /**
     * This method is used to construct the string that contains the end elements of request xml.
     * @return String
     */
	private String endRequestTag()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("</struct>");
        stringBuilder.append("</value>");
        stringBuilder.append("</param>");
        stringBuilder.append("</params>");
        return stringBuilder.toString();
    }
	/**
     * This method is used to convert the date string into yyyyMMdd from yyyyMMdd'T'HH:mm:ss
     * @param	String pDateStr
     * @return	String
     */
    public String getDateString(String pDateStr) throws Exception
    {
        if(log.isDebugEnabled())
        	log.debug("getDateString",PretupsI.ENTERED+" pDateStr::"+pDateStr);
        String dateStr="";
        try
        {
            dateStr = pDateStr.substring(0,pDateStr.indexOf('T')).trim();
        }
        catch(Exception e)
        {
            log.error("getDateString",PretupsI.EXCEPTION+e.getMessage());
            throw e;
        }
        finally
        {
            if(log.isDebugEnabled())
            	log.debug("getDateString",PretupsI.EXITED+" dateStr::"+dateStr);
        }
        return dateStr;
    }
    
    /**
     * This method is used to generate the request for getting account Details.
     * @param       HashMap pRequestMap
     * @return      String
     * @throws      Exception
     */
private String generateGetAccountDetailRequest(Map pRequestMap) throws Exception
    {
        if(log.isDebugEnabled())
        	log.debug("generateGetAccountDetailRequest",PretupsI.ENTERED+" pRequestMap::"+pRequestMap);
            String requestStr= null;
            StringBuilder stringBuilder = null;
            
            try
            {
                stringBuilder=new StringBuilder(1028);
            stringBuilder.append("<?xml version='1.0'?>");
            stringBuilder.append("<methodCall>");
            stringBuilder.append("<methodName>GetAccountDetails</methodName>");
            stringBuilder.append(startRequestTag());
            stringBuilder.append("<member>");
            stringBuilder.append("<name>originNodeType</name>");
            stringBuilder.append("<value><string>"+pRequestMap.get("NODE_TYPE")+"</string></value>");
            stringBuilder.append("</member>");
            //Set the originHostName
            stringBuilder.append("<member>");
            stringBuilder.append("<name>originHostName</name>");
            stringBuilder.append("<value><string>"+pRequestMap.get("HOST_NAME")+"</string></value>");
            stringBuilder.append("</member>");
            stringBuilder.append("<member>");
            stringBuilder.append("<name>originTransactionID</name>");
            stringBuilder.append("<value><string>"+(String)pRequestMap.get("IN_RECON_ID")+"</string></value>");
            stringBuilder.append("</member>");
            //Set the originTimeStamp
            stringBuilder.append("<member>");
            stringBuilder.append("<name>originTimeStamp</name>");
            stringBuilder.append("<value><dateTime.iso8601>"+getISO8601DateTime()+"</dateTime.iso8601></value>");
            stringBuilder.append("</member>");
            //Set the optional parameter subscriberNumberNAI if present.
            stringBuilder.append("<member>");
            stringBuilder.append("<name>subscriberNumberNAI</name>");
            stringBuilder.append("<value><i4>"+pRequestMap.get("SubscriberNumberNAI")+"</i4></value>");
            stringBuilder.append("</member>");
            //message cabablity flag 
	        if(!InterfaceUtil.isNullString((String)pRequestMap.get("MESSAGE_CAPABILITY_FLAG")))
	        {
	        	stringBuilder.append("<member><name>messageCapabilityFlag</name><value><struct>");
	        	
	        	if(!InterfaceUtil.isNullString((String)pRequestMap.get("PROMOTION_NOTIFICATION_FLAG")))
	        	 {
	        		 stringBuilder.append("<member><name>promotionNotificationFlag</name>");
	        		 stringBuilder.append("<value><boolean>"+(String)pRequestMap.get("PROMOTION_NOTIFICATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)pRequestMap.get("FIRST_IVR_CALL_SET_FLAG")))
	        	 {
	        		 stringBuilder.append("<member><name>firstIVRCallSetFlag</name><value><boolean>"+(String)pRequestMap.get("FIRST_IVR_CALL_SET_FLAG")+"</boolean></value></member>");
	        	 }
	        	 if(!InterfaceUtil.isNullString((String)pRequestMap.get("ACCOUNT_ACTIVATION_FLAG")))
	        	 {
	        		 stringBuilder.append("<member><name>accountActivationFlag</name><value><boolean>"+(String)pRequestMap.get("ACCOUNT_ACTIVATION_FLAG")+"</boolean></value></member>");
	        	 }
	        	stringBuilder.append("</struct></value></member>");
	        }
            //Set the subscriberNumber after adding or removing the prefix defined in the INFile.
            stringBuilder.append("<member>");
            stringBuilder.append("<name>subscriberNumber</name>");
            stringBuilder.append("<value><string>"+InterfaceUtil.getFilterMSISDN((String) pRequestMap.get("INTERFACE_ID"),(String)pRequestMap.get("MSISDN"))+"</string></value>");
            stringBuilder.append("</member>");
            stringBuilder.append(endRequestTag());
            stringBuilder.append("</methodCall>");
            requestStr = stringBuilder.toString();
            }//end of try-block
            catch(Exception e)
            {
                    log.error("generateGetAccountDetailRequest",PretupsI.EXCEPTION+e.getMessage());
                    throw e;
            }//end of catch-Exception
            finally
            {
                    if(log.isDebugEnabled())
                    	log.debug("generateGetAccountDetailRequest","Exiting Request String:requestStr::"+requestStr);
            }//end of finally
            return requestStr;
    }//end of generateGetAccountDetailRequest

/**
 * This method is used to parse the response of GetAccountinDetails.
 * @param   String pResponseStr
 * @return  HashMap
 */
private HashMap parseGetAccountDetailResponse(String pResponseStr) throws Exception
{
   if(log.isDebugEnabled())
	   log.debug("parseGetAccountDetailResponse",PretupsI.ENTERED+" pResponseStr::"+pResponseStr);
    HashMap responseMap = null;
    int indexStart=0;
    int indexEnd=0;
    int tempIndex=0;
    String responseCode = null;
    try
    {
        responseMap = new HashMap();
        indexStart = pResponseStr.indexOf("<fault>");
            if(indexStart>0)
            {
                tempIndex = pResponseStr.indexOf("faultCode",indexStart);
                String faultCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</4>",tempIndex));
                responseMap.put("faultCode",faultCode.trim());
                tempIndex = pResponseStr.indexOf("faultString",tempIndex);
                String faultString = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex));
                responseMap.put("faultString",faultString.trim());
                return responseMap;
            }
            indexStart= pResponseStr.indexOf("<member><name>serviceOfferingID</name><value><i4>14</i4></value></member>",indexEnd);
            tempIndex = pResponseStr.indexOf("serviceOfferingActiveFlag",indexStart);
            if(tempIndex>0)
            {
                String serviceOfferingActiveFlag = pResponseStr.substring("<boolean>".length()+pResponseStr.indexOf("<boolean>",tempIndex),pResponseStr.indexOf("</boolean>",tempIndex)).trim();
                if(!InterfaceUtil.isNullString(serviceOfferingActiveFlag))
                    responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag.trim());
                else
                      responseMap.put("serviceOfferingActiveFlag",serviceOfferingActiveFlag);
                indexEnd = pResponseStr.indexOf("</member>",indexStart);
            }
            
            indexStart = pResponseStr.indexOf("<member><name>responseCode");
	        tempIndex = pResponseStr.indexOf("responseCode",indexStart);
	        if(tempIndex>0)
	        {
	            responseCode = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("responseCode",responseCode.trim());
	            Object[] successList=CS5ClaroI.RESULT_OK.split(",");
	            if(!Arrays.asList(successList).contains(responseCode))
	                return responseMap;
	        }

	        indexStart= pResponseStr.indexOf("<member><name>accountValue1",indexEnd);
	        tempIndex = pResponseStr.indexOf("accountValue1",indexStart);
	        if(tempIndex>0)
	        {
	            String accountValue1 = pResponseStr.substring("<string>".length()+pResponseStr.indexOf("<string>",tempIndex),pResponseStr.indexOf("</string>",tempIndex)).trim();
	            responseMap.put("accountValue1",accountValue1.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        else 
	        	  responseMap.put("accountValue1","0");

	        indexStart= pResponseStr.indexOf("<member><name>dedicatedAccountInformation",indexEnd);
	        tempIndex = pResponseStr.indexOf("dedicatedAccountInformation",indexStart);
	        
	        if(tempIndex>0)
	        {
	            String daXMLString = pResponseStr.substring("<array>".length()+pResponseStr.indexOf("<array>",tempIndex),pResponseStr.indexOf("</array>",tempIndex)).trim();
	            responseMap.put("dedicatedAccountInformation",daXMLString.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        indexStart= pResponseStr.indexOf("<member><name>serviceClassCurrent",indexEnd);
	        tempIndex = pResponseStr.indexOf("serviceClassCurrent",indexStart);
	        if(tempIndex>0)
	        {
	            String serviceClassCurrent = pResponseStr.substring("<i4>".length()+pResponseStr.indexOf("<i4>",tempIndex),pResponseStr.indexOf("</i4>",tempIndex)).trim();
	            responseMap.put("serviceClassCurrent",serviceClassCurrent.trim());
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        
	        indexStart= pResponseStr.indexOf("<member><name>supervisionExpiryDate",indexEnd);
	        tempIndex = pResponseStr.indexOf("supervisionExpiryDate",indexStart);
	        if(tempIndex>0)
	        {
	            String supervisionExpiryDate = pResponseStr.substring("<dateTime.iso8601>".length()+pResponseStr.indexOf("<dateTime.iso8601>",tempIndex),pResponseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("supervisionExpiryDate",getDateString(supervisionExpiryDate.trim()));
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        else 
	        	 responseMap.put("supervisionExpiryDate",addDaysInUtilDate());
	        	
	        indexStart= pResponseStr.indexOf("<member><name>serviceFeeExpiryDate",indexEnd);
	        tempIndex = pResponseStr.indexOf("serviceFeeExpiryDate",indexStart);
	        if(tempIndex>0)
	        {
	            String serviceFeeExpiryDate = pResponseStr.substring("<dateTime.iso8601>".length()+pResponseStr.indexOf("<dateTime.iso8601>",tempIndex),pResponseStr.indexOf("</dateTime.iso8601>",tempIndex)).trim();
	            responseMap.put("serviceFeeExpiryDate",getDateString(serviceFeeExpiryDate.trim()));
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	        responseMap.put("serviceFeeExpiryDate", addDaysInUtilDate());
	       
	        indexStart= pResponseStr.indexOf("<member><name>temporaryBlockedFlag",indexEnd);
	        tempIndex = pResponseStr.indexOf("temporaryBlockedFlag",indexStart);
	        if(tempIndex>0)
	        {
	            String temporaryBlockedFlag = pResponseStr.substring("<boolean>".length()+pResponseStr.indexOf("<boolean>",tempIndex),pResponseStr.indexOf("</boolean>",tempIndex)).trim();
	            responseMap.put("temporaryBlockedFlag",temporaryBlockedFlag);
	            indexEnd = pResponseStr.indexOf("</member>",indexStart);
	        }
	        
	 
    }
    catch(Exception e)
    {
        log.error("parseGetAccountDetailResponse",PretupsI.EXCEPTION+e.getMessage());
        throw e;
    }//end catch-Exception
    finally
    {
        if(log.isDebugEnabled())
        	log.debug("parseGetAccountDetailResponse",PretupsI.EXITED+" responseMap::"+responseMap);
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
}


