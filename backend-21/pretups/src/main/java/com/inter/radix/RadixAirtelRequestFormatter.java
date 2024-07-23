/* 
 * #RadixAirtelRequestFormatter.java
 *
 *------------------------------------------------------------------------------------------------
 *  Name                  Version		 Date            	History
 *-------------------------------------------------------------------------------------------------
 *  Mahindra Comviva       1.0     		04/09/2014         	Initial Creation
 *-------------------------------------------------------------------------------------------------
 *
 * Copyright(c) 2005 Comviva Technologies Ltd.
 *
 */
package com.inter.radix;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;


public class RadixAirtelRequestFormatter {
	
	public static Log _log = LogFactory.getLog(RadixAirtelRequestFormatter.class);
	public RadixAirtelRequestFormatter() throws Exception
    {
       
    }  
 /**
     * This method is used to parse the response string based on the type of Action.
     * @param	int p_action
     * @param	HashMap p_map
     * @return	String.
     * @throws	Exception
     * @author abhilasha
     */
	protected String generateRequest(int p_action, HashMap p_map) throws Exception 
	{
		String methodName="generateRequest";
       if(_log.isDebugEnabled())_log.debug(methodName,"Entered p_action::"+p_action+" map::"+p_map);
		String str=null;
		p_map.put("action",String.valueOf(p_action));
		try
		{
			switch(p_action)
			{
				
				case RadixAirtelI.ACTION_SUBMIT_PROVISION: 
				{
				    str=generateDataBundleSubmitProvisionReq(p_map);
					break;	
				}
				case RadixAirtelI.ACTION_RETRIEVE_PROVISION: 
				{
				    str=generateDataBundleRetrieveProvisionReq(p_map);
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
     * This method generate the requests and calls the respective IN URLS respectively
     * @param p_map
     * @return
     * @throws Exception
     * @author abhilasha.dwivedi
     */
    private String generateDataBundleSubmitProvisionReq(HashMap p_requestMap) throws Exception
    {

    	String methodName="generateDataBundleSubmitProvisionReq";
    	if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+p_requestMap);
    	StringBuffer stringBuffer = null;

    	try
    	{

    		stringBuffer=new StringBuffer(1028);
    		String url=(String)p_requestMap.get("URL_SUBMITPROVISION");

    		String msisdn= InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"));
    		String packageId=(String)p_requestMap.get("CARD_GROUP");
    		String authKey=(String)p_requestMap.get("AUTH_KEY");
    		if(!InterfaceUtil.isNullString(authKey))
    			authKey=	new CryptoUtil().decrypt(authKey,Constants.KEY);
    		String requestId=(String)p_requestMap.get("IN_TXN_ID");
    		String  accountId=(String)p_requestMap.get("ACCOUNT_ID");
    		String externalData1=(String)p_requestMap.get("EXTERNAL_DATA1");
    		String externalData2Required = FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"EXTERNAL_DATA2_REQUIRED").trim();
    		String externalData2=null;
    		if (_log.isDebugEnabled())
				_log.debug(methodName,"externalData2Required"+externalData2Required);
    		
			if(!InterfaceUtil.isNullString(externalData2Required)&& "Y".equals(externalData2Required))
			{
				 externalData2=FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),packageId+"_"+"EXTERNAL_DATA2").trim()+"_"+(String)p_requestMap.get("SENDER_MSISDN");
			}
    		// generate http URL request string 
			if (_log.isDebugEnabled())
				_log.debug(methodName,"externalData2"+externalData2);
			
    		stringBuffer.append(url+"msisdn="+msisdn+"&packageId="+packageId+"&authKey="+authKey+"&requestId="+requestId+"&accountId="+accountId);
    		if (_log.isDebugEnabled())
				_log.debug(methodName,"externalData1Req"+(String)p_requestMap.get("EXTERNALDATA1REQ"));
    		if("Y".equalsIgnoreCase((String)p_requestMap.get("EXTERNALDATA1REQ")))
    		stringBuffer.append("&externalData1="+externalData1);
    		
    		if("Y".equalsIgnoreCase(externalData2Required))
        		stringBuffer.append("&externalData2="+externalData2);
    		

    	}//end of try-block
    	catch(Exception e)
    	{
    		_log.error(methodName,"Exception e::"+e.getMessage());
    		throw e;
    	}//end of catch-Exception
    	finally
    	{
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting Request String:gethttpURLString::"+stringBuffer.toString());
    	}//end of finally
    	return stringBuffer.toString();
    }
    
    /**
     * This method generate the requests and calls the respective IN URLS respectively
     * @param p_map
     * @return
     * @throws Exception
     * @author abhilasha.dwivedi
     */
    private String generateDataBundleRetrieveProvisionReq(HashMap p_requestMap) throws Exception
    {
    	String methodName="generateDataBundleRetrieveProvisionReq";
    	if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap::"+p_requestMap);
    	StringBuffer stringBuffer = null;

    	try
    	{
    		stringBuffer=new StringBuffer(1028);
    		String url=(String)p_requestMap.get("URL_RETRIEVEPROVISION");

    		String msisdn= InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)p_requestMap.get("MSISDN"));
    		String authKey=(String)p_requestMap.get("AUTH_KEY");
    		if(!InterfaceUtil.isNullString(authKey))
    			authKey=	new CryptoUtil().decrypt(authKey,Constants.KEY);
    		String requestId=(String)p_requestMap.get("REQUEST_ID");
    		String  accountId=(String)p_requestMap.get("ACCOUNT_ID");
    		String  transactionId=(String)p_requestMap.get("TRANSACTION_ID");
    		// generate http URL request string 

    		stringBuffer.append(url+"msisdn="+msisdn+"&authKey="+authKey+"&requestId="+requestId+"&accountId="+accountId+"&transactionId="+transactionId);

    	}//end of try-block
    	catch(Exception e)
    	{
    		_log.error(methodName,"Exception e::"+e.getMessage());
    		throw e;
    	}//end of catch-Exception
    	finally
    	{
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting Request String:gethttpURLString::"+stringBuffer.toString());
    	}//end of finally
    	return stringBuffer.toString();
    }
    
}
