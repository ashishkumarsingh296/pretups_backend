package com.inter.safaricompost;

/**
 * @(#)SafComPostRequestFormatter.java
 * Copyright(c) 2008, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 *Manisha Jain			09 june	2008	Initial creation
 * ------------------------------------------------------------------------------------------------
 * Formatter class for the interface Post Paid billing System
 */

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;



public class SafComPostRequestFormatter 
{

	public Log _log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Get IN Reconciliation Txn ID
	 * @param p_requestMap
	 * @return
	 */
	private String getINReconTxnID(HashMap p_requestMap)
	{
		String inReconID=null;
		String userType=(String)p_requestMap.get("USER_TYPE");
		if(userType!=null)
			inReconID= ((String)p_requestMap.get("TRANSACTION_ID")+"."+userType);
		else
			inReconID= ((String)p_requestMap.get("TRANSACTION_ID"));
		p_requestMap.put("IN_RECON_ID",inReconID);
		return inReconID;
	}
	 /**
     * this method construct the request in XML String from HashMap
     * @param action int
     * @param map java.util.HashMap
     * @return str java.lang.String
     */
	protected String generateRequest(int action, HashMap map) throws Exception 
	{
       if(_log.isDebugEnabled())_log.debug("generateRequest","Entered map: "+map);
		String str=null;
		map.put("action",String.valueOf(action));
		switch(action)
		{
			case SafaricomPostI.ACTION_ACCOUNT_INFO: 
			{
				str=generateGetAccountInfoRequest(map);
				break;	
			}
			case SafaricomPostI.ACTION_CREDIT: 
			{
				str=generateCreditRequest(map);
				break;	
			}
		}
		str="REQUEST_BODY="+str;
		if(_log.isDebugEnabled())_log.debug("generateRequest","Exited Request String:  "+str);
		return str;
	}
	/**
     * this method parse the response from XML String into HashMap
     * 
     * @param action int
     * @param responseStr java.lang.String
     * @return map java.util.HashMap
     */
	
	protected HashMap parseResponse(int action, String responseStr) throws Exception 
	{
	    if(_log.isDebugEnabled())_log.debug("parseResponse","Entered Response String:  "+responseStr);
		HashMap map=null;
		switch(action)
		{
			case SafaricomPostI.ACTION_ACCOUNT_INFO: 
			{
				map=parseGetAccountInfoResponse(responseStr);
				break;	
			}
			case SafaricomPostI.ACTION_CREDIT: 
			{
				map=parseCreditResponse(responseStr);
				break;	
			}
		}
		if(_log.isDebugEnabled())_log.debug("parseResponse","Exiting map: "+map);
		return map;	
	}
 	 /**
	 * This Method generate account information request
	 * @param map HashMap
	 * @throws Exception
	 * @return requestStr
	 */
	private String generateGetAccountInfoRequest(HashMap map) throws Exception
    {
		if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequest","Entered map="+map);
		String requestStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1028);
		    sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns0:COMMAND xmlns:ns0=\"http://www.tibco.com/schemas/pinless/PINLESS.Core/Schema/AccountInfoRequestSchema.xsd\">");
		    sbf.append("<ns0:ACTION>"+map.get("action")+"</ns0:ACTION>");
		 	sbf.append("<ns0:RETMSISDN>"+map.get("SENDER_MSISDN")+"</ns0:RETMSISDN>");
		 	sbf.append("<ns0:MSISDN>"+InterfaceUtil.getFilterMSISDN((String)map.get("INTERFACE_ID"),(String)map.get("MSISDN"))+"</ns0:MSISDN>");
		 	sbf.append("<ns0:TXNID>"+map.get("IN_TXN_ID")+"</ns0:TXNID>");
		 	sbf.append("<ns0:SOURCE>"+map.get("SOURCE")+"</ns0:SOURCE>");
		 	sbf.append("</ns0:COMMAND>");
		 	requestStr=sbf.toString();
		   	if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequest","Got the XML String as "+requestStr);
		 	return requestStr;		
		}
		catch(Exception e)
		{
			_log.error("generateGetAccountInfoRequest","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequest","Exiting requestStr: "+requestStr);
		}
    }
	
	/**
	 * This Method parse  GetAccount Info Response
	 * @param responseStr String
	 * @throws Exception
	 * @return map
	 */
	public HashMap parseGetAccountInfoResponse(String responseStr) throws Exception{
	    if(_log.isDebugEnabled())_log.debug("parseGetAccountInfoResponse","Entered responseStr: "+responseStr);
		HashMap map=null;
		try
		{  
		    map=new HashMap();
		    
		    
		    int index =responseStr.indexOf("<ns0:STATUS>");
	        String status=responseStr.substring(index+"<ns0:STATUS>".length(),responseStr.indexOf("</ns0:STATUS>",index));
	        map.put("STATUS",status);
		    if(SafaricomPostI.RESULT_OK.equals(status))
	        {
		        index =responseStr.indexOf("<ns0:TXNID>");
		        String res_txn_id=responseStr.substring(index+"<ns0:TXNID>".length(),responseStr.indexOf("</ns0:TXNID",index));
		        map.put("TXNID",res_txn_id);
		        
		        index= responseStr.indexOf("<ns0:SERVICECLASS>");
		        String res_service_class= responseStr.substring(index+"<ns0:SERVICECLASS>".length(),responseStr.indexOf("</ns0:SERVICECLASS>",index));
		        map.put("SERVICECLASS",res_service_class);
		        
		        index= responseStr.indexOf("<ns0:ACCOUNTNO>");
		        String res_acc_no= responseStr.substring(index+"<ns0:ACCOUNTNO>".length(),responseStr.indexOf("</ns0:ACCOUNTNO>",index));
		        map.put("ACCOUNTNO",res_acc_no);
		        
		        index =responseStr.indexOf("<ns0:ACCOUNTSTATUS>");
		        String res_acc_status=responseStr.substring(index+"<ns0:ACCOUNTSTATUS>".length(),responseStr.indexOf("</ns0:ACCOUNTSTATUS",index));
		        map.put("ACCOUNTSTATUS",res_acc_status);
		        
	        	index= responseStr.indexOf("<ns0:MESSAGE>");
		        String res_message= responseStr.substring(index+"<ns0:MESSAGE>".length(),responseStr.indexOf("</ns0:MESSAGE>",index));
		        map.put("MESSAGE",res_message);
		        
			}	        
		}
		catch(Exception e)
		{
			_log.error("parseGetAccountInfoResponse","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("parseGetAccountInfoResponse","Exiting map: "+map);
		}
		 return map;
	}
	
/**
 * @param map
 * @return requestStr java.lang.String
 * @throws Exception
 */	
	private String generateCreditRequest(HashMap map) throws Exception
    {
		if(_log.isDebugEnabled())_log.debug("generateCreditRequest","Entered MSISDN="+map.get("MSISDN")+"transactionId: "+map.get("IN_TXN_ID")+" source: "+map.get("application")+" action: "+map.get("action")+" transaction price: "+map.get("INTERFACE_AMOUNT"));
		String requestStr= null;
		StringBuffer sbf=null;
		String multFactor=null;
		try
		{
			multFactor=(String)map.get("MULT_FACTOR");
			sbf=new StringBuffer(1024);
    		sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <ns0:COMMAND xmlns:ns0=\"http://www.tibco.com/schemas/pinless/PINLESS.Core/Schema/BillPaymentReqSchema.xsd\">");
    		sbf.append("<ns0:ACTION>"+map.get("action")+"</ns0:ACTION>");
    		sbf.append("<ns0:RETMSISDN>"+map.get("SENDER_MSISDN")+"</ns0:RETMSISDN>");
    		sbf.append("<ns0:TXNID>"+map.get("IN_TXN_ID")+"</ns0:TXNID>");
    		sbf.append("<ns0:MSISDN>"+InterfaceUtil.getFilterMSISDN((String)map.get("INTERFACE_ID"),(String)map.get("MSISDN"))+"</ns0:MSISDN>");
    		sbf.append("<ns0:ACCOUNTNO>"+map.get("ACCOUNT_ID")+"</ns0:ACCOUNTNO>");
    		sbf.append("<ns0:AMOUNT>"+InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble((String)map.get("INTERFACE_AMOUNT")),Double.parseDouble(multFactor))+"</ns0:AMOUNT>");
    		sbf.append("<ns0:SERVICETYPE>"+map.get("SERVICE_TYPE")+"</ns0:SERVICETYPE>");
    		sbf.append("<ns0:SOURCE>"+map.get("SOURCE")+"</ns0:SOURCE>");	
    		sbf.append("</ns0:COMMAND>");
            requestStr=sbf.toString();
            if(_log.isDebugEnabled())_log.debug("generateCreditRequest","Got the XML String as "+requestStr);
			return requestStr;
		}
		catch(Exception e)
		{
			_log.error("generateCreditRequest","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally{
			if(_log.isDebugEnabled())_log.debug("generateCreditRequest","Exited requestStr: "+requestStr);
		}
    }

	/**
	 * This Method parse recharge credit response
	 * @param responseStr String
	 * @throws Exception
	 * @return map
	 */
	 private HashMap parseCreditResponse(String responseStr) throws Exception{
	    if(_log.isDebugEnabled())_log.debug("parseCreditResponse","Entered responseStr: "+responseStr);
		HashMap map=null;
		try
		{  
		    map= new HashMap();
		    
			int index=responseStr.indexOf("<ns0:STATUS>");
		    String status=responseStr.substring(index+"<ns0:STATUS>".length(),responseStr.indexOf("</ns0:STATUS>",index));
			map.put("STATUS",status);
			if(SafaricomPostI.RESULT_OK.equals((status)))
	        {
	        	index=responseStr.indexOf("<ns0:TXNID>");
			    String res_txn_id=responseStr.substring(index+"<ns0:TXNID>".length(),responseStr.indexOf("</ns0:TXNID>",index));
	            map.put("TXNID",res_txn_id);
	            
	            index=responseStr.indexOf("<ns0:MESSAGE>");
	            String res_message=responseStr.substring(index+"<ns0:MESSAGE>".length(),responseStr.indexOf("</ns0:MESSAGE>",index));
	           	map.put("MESSAGE",res_message);
		     } 
			
		}
		catch(Exception e)
		{
			_log.error("parseCreditResponse","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("parseCreditResponse","Exiting map: "+map);
		}
		return map;
	}
}
