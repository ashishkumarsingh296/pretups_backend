package com.inter.zteogn.zteogndata;
/**
* @(#)CP2PDataReqResFormatter.java
* Copyright(c) 2011, Comviva Technologies Ltd.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* 	  Author				Date				 History
*-------------------------------------------------------------------------------------------------
* Vinay Kumar Singh      July 09, 2009		  Initial Creation
* ------------------------------------------------------------------------------------------------
* This class can be used as a parser class for both request(before sending the request to IN) and 
* response(after getting the response from the IN). 
*/
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class CP2PDataReqResFormatter 
{
	private static Log _log = LogFactory.getLog(CP2PDataReqResFormatter.class.getName());
	private static int _counter=0;
	private String _cardGrp=null;
	private boolean _explicitRecharge=false;
	private boolean _combinedRecharge=false;
	private boolean _implicitRecharge=false;
	private String _selectorBundleId=null;
	private String _interfaceId=null;
	private String _serviceType=null;
/**
 * Constructor
 */
	public CP2PDataReqResFormatter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected String generateRequest(int action, HashMap<String,String> p_requestMap) throws Exception 
	{
       if(_log.isDebugEnabled())
           _log.debug("generateRequest","Entered");
       
		String requestStr=null;
		p_requestMap.put("action",String.valueOf(action));
		_selectorBundleId=p_requestMap.get("SELECTOR_BUNDLE_ID");
		if(!InterfaceUtil.isNullString(_selectorBundleId))
	        _selectorBundleId=_selectorBundleId.trim();
		_interfaceId=(p_requestMap.get("INTERFACE_ID")).trim();
		_serviceType=p_requestMap.get("REQ_SERVICE");
		switch(action)
		{
			case CP2PDataI.ACTION_ACCOUNT_INFO: 
			{
				requestStr=generateGetAccountInfoRequest(p_requestMap);
				break;	
			}
			case CP2PDataI.ACTION_IMMEDIATE_DEBIT:
			{
				requestStr=generateImmediateDebitOrCreditAdjustRequest(p_requestMap);
				break;	
			}
			case CP2PDataI.ACTION_RECHARGE_CREDIT: 
			{
				/*if("PRC".equals(_serviceType))
					requestStr=generateCreditAdjustRequest(p_requestMap);
				else*/
					requestStr=generateRechargeCreditRequest(p_requestMap);
				break;
			}
			case CP2PDataI.ACTION_RECHARGE_CREDIT_ADJUST: 
			{
				requestStr=generateCreditAdjustRequest(p_requestMap);
				break;	
			}
			
		}
		if(_log.isDebugEnabled())
		    _log.debug("generateRequest","Exited=");
		return requestStr;
	}
/**
 * this method parse the response from XML String into HashMap
 * 
 * @param action int
 * @param responseStr String
 * @return map HashMap<String,String>
 */
	protected HashMap<String,String> parseResponse(int action, String responseStr) throws Exception 
	{
	    if(_log.isDebugEnabled())
	        _log.debug("parseResponse","Entered");
		HashMap<String,String> responseMap=null;
		switch(action)
		{
			case CP2PDataI.ACTION_ACCOUNT_INFO: 
			{
				responseMap=parseGetAccountInfoResponse(responseStr);
				break;	
			}
			case CP2PDataI.ACTION_RECHARGE_CREDIT: 
			{
				/*if("PRC".equals(_serviceType))
					responseMap=parseCreditAdjustResponse(responseStr);
				else*/
					responseMap=parseRechargeCreditResponse(responseStr);
				break;	
			}
			
			case CP2PDataI.ACTION_IMMEDIATE_DEBIT: 
			{
				responseMap=parseImmediateDebitResponse(responseStr);
				break;	
			}
			case CP2PDataI.ACTION_RECHARGE_CREDIT_ADJUST: 
			{
					responseMap=parseCreditAdjustResponse(responseStr);
				break;	
			}
		}
		if(_log.isDebugEnabled())
		    _log.debug("parseResponse","Exiting");
		return responseMap;	
	}
/**
 * This Method generate account information request
 * @param p_requestMap HashMap<String,String>
 * @throws Exception
 * @return requestStr
 */
	private String generateGetAccountInfoRequest(HashMap<String,String> p_requestMap) throws Exception
    {
		if(_log.isDebugEnabled())
		    _log.debug("generateGetAccountInfoRequest","Entered with p_requestMap="+p_requestMap);
		String requestStr= null;
		StringBuffer sbf=null;
		try
		{
			getINRequestID(p_requestMap);
		    sbf=new StringBuffer(1028);
		    sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		 	sbf.append("<zsmart>");
		 	sbf.append("<Data>");
		 	sbf.append("<header>");
		 	sbf.append("<ACTION_ID>QueryProfileAndBal4ETopup</ACTION_ID>");
		sbf.append("<CONTACT_CHANNLE_ID>"+p_requestMap.get("CHANNEL_ID")+"</CONTACT_CHANNLE_ID>");
    	 	sbf.append("<REQUEST_ID>"+p_requestMap.get("IN_REQ_ID")+"</REQUEST_ID>");
    	 	sbf.append("</header>");
    	 	sbf.append("<body>");
    	 	sbf.append("<MSISDN>"+InterfaceUtil.getFilterMSISDN( p_requestMap.get("INTERFACE_ID"),p_requestMap.get("MSISDN"))+"</MSISDN>");
    	 	sbf.append("<TransactionSN>"+p_requestMap.get("IN_RECON_ID")+"</TransactionSN>");
    	 	sbf.append("<UserPwd></UserPwd>");
    	 	sbf.append("</body>");
    	 	sbf.append("</Data>");
    	 	sbf.append("</zsmart>");
    	 	requestStr=sbf.toString();

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
			if(_log.isDebugEnabled())
			    _log.debug("generateGetAccountInfoRequest","Exiting with requestStr="+requestStr);
		}
    }
/**
 * This Method parse  GetAccount Info Response
 * @param p_responseStr String
 * @throws Exception
 * @return responseMap HashMap<String,String>
 */
	public HashMap<String,String> parseGetAccountInfoResponse(String p_responseStr) throws Exception
	{
	    if(_log.isDebugEnabled())
	        _log.debug("parseGetAccountInfoResponse","Entered responseStr="+p_responseStr);
		HashMap<String,String> responseMap=null;
		try
		{  
		    responseMap=new HashMap<String,String>();
		    String _str="0";
		    int index=p_responseStr.indexOf("<returnCode>");
		    String returnCode=p_responseStr.substring(index+"<returnCode>".length(),p_responseStr.indexOf("</returnCode>",index));
		    responseMap.put("resp_returnCode",returnCode);
		    if(_str.equals(returnCode))
	        {
		    	index=p_responseStr.indexOf("<ACTION_ID>");
		    	if(index!=-1)
		    	{
		    		String actionId=p_responseStr.substring(index+"<ACTION_ID>".length(),p_responseStr.indexOf("</ACTION_ID>",index));
		    		responseMap.put("resp_action_id",actionId);
		    	}
			    index=p_responseStr.indexOf("<REQUEST_ID>");
			    if(index!=-1)
		    	{
			    	String reqId=p_responseStr.substring(index+"<REQUEST_ID>".length(),p_responseStr.indexOf("</REQUEST_ID>",index));
			    	responseMap.put("resp_req_id",reqId);
		    	}
			    index=p_responseStr.indexOf("<MSISDN>");
			    if(index!=-1)
		    	{
			    	String msisdn=p_responseStr.substring(index+"<MSISDN>".length(),p_responseStr.indexOf("</MSISDN>",index));
			    	responseMap.put("resp_msisdn",msisdn);
		    	}
			    index=p_responseStr.indexOf("<DefLang>");
			    if(index!=-1)
		    	{	
			    	String defLang=p_responseStr.substring(index+"<DefLang>".length(),p_responseStr.indexOf("</DefLang>",index));
			    	responseMap.put("resp_defLang",defLang);
		    	}			    
	           	index=p_responseStr.indexOf("<State>");
	           	if(index!=-1)
		    	{
	           		String resp_state=p_responseStr.substring(index+"<State>".length(),p_responseStr.indexOf("</State>",index));
	           		responseMap.put("resp_state",resp_state);
		        }	           	
	           
	            index=p_responseStr.indexOf("<ProductCode>");
	            if(index!=-1)
	            {
	            	String resp_ProductCode=p_responseStr.substring(index+"<ProductCode>".length(),p_responseStr.indexOf("</ProductCode>",index));
	            	responseMap.put("resp_ProductCode",resp_ProductCode);
	            }
	            
	           	index=p_responseStr.indexOf("<ActiveStopDate>");
	           	if(index!=-1)
		    	{
	           		String resp_activeStopDate=p_responseStr.substring(index+"<ActiveStopDate>".length(),p_responseStr.indexOf("</ActiveStopDate>",index));
	           		responseMap.put("resp_activeStopDate",resp_activeStopDate);
		    	}
	          	index=p_responseStr.indexOf("<SuspendStopDate>");
	          	if(index!=-1)
		    	{
	          		String resp_suspendStopDate=p_responseStr.substring(index+"<SuspendStopDate>".length(),p_responseStr.indexOf("</SuspendStopDate>",index));
	          		responseMap.put("resp_suspendStopDate",resp_suspendStopDate);
		    	}
		        index=p_responseStr.indexOf("<DisableStopDate>");
		        if(index!=-1)
		    	{
		        	String resp_disableStopDate=p_responseStr.substring(index+"<DisableStopDate>".length(),p_responseStr.indexOf("</DisableStopDate>",index));
		        	responseMap.put("resp_disableStopDate",resp_disableStopDate);
		    	}
	          	index=p_responseStr.indexOf("<ServiceStopDate>");
	          	if(index!=-1)
		    	{
	          		String resp_serviceStopDate=p_responseStr.substring(index+"<ServiceStopDate>".length(),p_responseStr.indexOf("</ServiceStopDate>",index));
	          		responseMap.put("resp_serviceStopDate",resp_serviceStopDate);
		    	}
	          	index=p_responseStr.indexOf("<ServiceClass>");
	          	if(index!=-1)
		    	{
	          		String resp_serviceClass=p_responseStr.substring(index+"<ServiceClass>".length(),p_responseStr.indexOf("</ServiceClass>",index));
	          		responseMap.put("resp_serviceClass",resp_serviceClass);
		    	}
				index=p_responseStr.indexOf("<TransactionSN>");
				if(index!=-1)
		    	{
					String resp_transactionSN=p_responseStr.substring(index+"<TransactionSN>".length(),p_responseStr.indexOf("</TransactionSN>",index));
					responseMap.put("resp_transactionSN",resp_transactionSN);
		    	}
	            //Set the bundle info received from the response in to the response map.
				getBundlesInfoRcvdFromIN(p_responseStr,responseMap);
				
	        }
	        return responseMap;
		}
		catch(Exception e)
		{
			_log.error("parseGetAccountInfoResponse","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
			    _log.debug("parseGetAccountInfoResponse","Exiting with responseMap="+responseMap);
		}
	}
/**
 * This method is to generate re-charge Credit Request
 * @param p_requestMap HashMap
 * @return String
 * @throws Exception
 */	
	private String generateRechargeCreditRequest(HashMap<String,String> p_requestMap) throws Exception
    {
		if(_log.isDebugEnabled())
		    _log.debug("generateRechargeCreditRequest","Entered with p_requestMap="+p_requestMap);
		String requestStr= null;
		StringBuffer sbf=null;
		String graceDays="0";
		try
		{
		    String module = p_requestMap.get("MODULE");
			//Grace days
			graceDays=p_requestMap.get("GRACE_DAYS");
			if(InterfaceUtil.isNullString(graceDays))
				graceDays="0";
			_cardGrp=p_requestMap.get("CARD_GROUP");
			String combined=p_requestMap.get("COMBINED_RECHARGE");
			String implicit=p_requestMap.get("IMPLICIT_RECHARGE");
			if("N".equals(implicit) && "N".equals(combined))
				_explicitRecharge=true;
			else if("Y".equals(implicit) && "Y".equals(combined))
				_combinedRecharge=true;
			else if("Y".equals(implicit) && "N".equals(combined))
				_implicitRecharge=true;
			
			//Transfer amount of selector.
			double transAmtDbl=0;
  	 		if(!InterfaceUtil.isNullString(p_requestMap.get("transfer_amount")))
	 		{
				transAmtDbl=Double.parseDouble(p_requestMap.get("transfer_amount"));
				transAmtDbl = 0 - transAmtDbl;
			
	 		}
  	 		String receiverBundleID = "1";
  	 		if(!InterfaceUtil.isNullString(p_requestMap.get("receiver_bundle")))
	 		{
  	 			receiverBundleID=p_requestMap.get("receiver_bundle").toString();
			}
			//Validity days of selector.
			String validity=p_requestMap.get("VALIDITY_DAYS");
			//If bonus validity is separate from the main validity, then subtract it from the main validity.
			String addMainBnsVal=FileCache.getValue(_interfaceId,"ADD_MAIN_AND_BUNUS_VALIDITY").trim();
			String bonusValidity=p_requestMap.get("BONUS_VALIDITY_DAYS");
			if("N".equals(addMainBnsVal) && !InterfaceUtil.isNullString(bonusValidity.trim()) && !InterfaceUtil.isNullString(validity))
			{
				long mainVal = Long.parseLong(validity.trim());
				long bonusVal=Long.parseLong(bonusValidity);
				if(bonusVal>0)
					validity=String.valueOf(mainVal-bonusVal);
			}
			else if(InterfaceUtil.isNullString(validity))
				validity="0";
			
			getINRequestID(p_requestMap);
			sbf=new StringBuffer(1024);
			sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		 	sbf.append("<zsmart>");
		 	sbf.append("<Data>");
		 	sbf.append("<header>");
		 	sbf.append("<ACTION_ID>ModifyAllBalReturnAllBal</ACTION_ID>");
    	 	sbf.append("<REQUEST_ID>"+p_requestMap.get("IN_REQ_ID")+"</REQUEST_ID>");
    	 	sbf.append("</header>");
    	 	sbf.append("<body>");
            sbf.append("<MSISDN>"+InterfaceUtil.getFilterMSISDN(p_requestMap.get("INTERFACE_ID"),p_requestMap.get("MSISDN"))+"</MSISDN>");
            String txnDesc = FileCache.getValue( p_requestMap.get("INTERFACE_ID"),module+"_TXN_DESC");
    	 	if(!InterfaceUtil.isNullString(txnDesc))
    	 	    sbf.append("<TransactionDescription>"+txnDesc+"</TransactionDescription>");
    	 	sbf.append("<AccountCode></AccountCode>");
    	 	sbf.append("<TransactionSN>"+p_requestMap.get("IN_RECON_ID")+"</TransactionSN>");
    	 	if(_implicitRecharge || _combinedRecharge)
    	 		sbf.append("<RechargingID>"+_cardGrp+"</RechargingID>");
    	 	else
    	 		sbf.append("<RechargingID></RechargingID>");
    	 	sbf.append("<SuspendAddDays>"+graceDays+"</SuspendAddDays>");

    	 	if(_explicitRecharge || _combinedRecharge)
    	 	{  
    	 		String cardGrpSelector=p_requestMap.get("CARD_GROUP_SELECTOR");
    	 		//If combined re-charge, and selector value is other than 1
				if(_combinedRecharge && !"1".equals(cardGrpSelector) && !InterfaceUtil.isNullString(p_requestMap.get("BONUS_BUNDLE_IDS")))
				{
					Object[] ambList=(p_requestMap.get("BONUS_BUNDLE_IDS")).split("\\|");
					if(Arrays.asList(ambList).contains("1"))
						sbf.append(getBundleRequestString(p_requestMap));
				}
    	 		else
    	 		{
        	 		sbf.append("<BalInputDtoList>");
        	 		sbf.append("<AcctResCode>"+receiverBundleID+"</AcctResCode>");
        	 		sbf.append("<AcctResName></AcctResName>");
         			sbf.append("<Balance></Balance>");
         			//If transfer amount is not zero, then set it in to the request string.
        	 		if(transAmtDbl!=0)
        				sbf.append("<AddBalance>"+String.valueOf(Math.round(transAmtDbl))+"</AddBalance>");
        	 		else
        	 			sbf.append("<AddBalance></AddBalance>");
        	 		//If validity period is not null, then set it in to the request string.
        	 		if(!InterfaceUtil.isNullString(validity))
        	 			sbf.append("<AddDays>"+validity+"</AddDays>");
        	 		else
        	 			sbf.append("<AddDays></AddDays>");
        	 		sbf.append("<ExpDate></ExpDate>");
        	 		sbf.append("</BalInputDtoList>");
    	 		}
    	 	}
    	 	//If explicit re-charge, then set the other bundles in to the request string.
    	 	if(_explicitRecharge)
    	 		sbf.append(getBundleRequestString(p_requestMap));

    	 	sbf.append("</body>");
    	 	sbf.append("</Data>");
    	 	sbf.append("</zsmart>");
    		requestStr=sbf.toString();
            if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Got the XML String as "+requestStr);
			return requestStr;
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally{
			if(_log.isDebugEnabled())
			    _log.debug("generateRechargeCreditRequest","Exiting with requestStr="+requestStr);
		}
   }
/**
 * This Method parse re-charge credit response
 * @param responseStr String
 * @throws Exception
 * @return map
 */
	 private HashMap<String,String> parseRechargeCreditResponse(String responseStr) throws Exception
	 {
	    if(_log.isDebugEnabled())
	        _log.debug("parseRechargeCreditResponse","Entered with responseStr="+responseStr);
		HashMap<String,String> responseMap=null;
		try
		{  
		    responseMap= new HashMap<String,String>();
		    String _str="0";
		    int index=responseStr.indexOf("<returnCode>");
		    String returnCode=responseStr.substring(index+"<returnCode>".length(),responseStr.indexOf("</returnCode>",index));
		    responseMap.put("resp_returnCode",returnCode);
		    if(_str.equals((returnCode)))
	        {
		    	index=responseStr.indexOf("<ACTION_ID>");
				if(index!=-1)
		    	{
					String actionId=responseStr.substring(index+"<ACTION_ID>".length(),responseStr.indexOf("</ACTION_ID>",index));
	           		responseMap.put("resp_action_id",actionId);
		    	}
			    index=responseStr.indexOf("<REQUEST_ID>");
			    if(index!=-1)
		    	{
			    	String reqId=responseStr.substring(index+"<REQUEST_ID>".length(),responseStr.indexOf("</REQUEST_ID>",index));
	           		responseMap.put("resp_req_id",reqId);
		    	}
	          	index=responseStr.indexOf("<TransactionSN>");
	          	if(index!=-1)
		    	{
	          		String transactionSN=responseStr.substring(index+"<TransactionSN>".length(),responseStr.indexOf("</TransactionSN>",index));
	          		responseMap.put("resp_transactionSN",transactionSN);
		    	}
	          	
	          	   // for all bundles choose main or Data 
			String bdl_code="";
			String dataBundlesStr = FileCache.getValue(_interfaceId,"SENDER_DATA_BUNDLES");
			index=responseStr.indexOf("</BalDto>");
			if(index!=-1)
			{
				
				String[] bundleArray=responseStr.split("</BalDto>");
				int noOfBundles=bundleArray.length;
				for(int i=0; i<noOfBundles; i++)
				{
					//get the bdl_name if present in the bucket.
					int index1=0;
					String bdl_Balance="";
					index=bundleArray[i].indexOf("<AcctResCode>");
					if(index!=-1)
					{
						bdl_code=bundleArray[i].substring(index+"<AcctResCode>".length(),bundleArray[i].indexOf("</AcctResCode>",index));
		        	
						// FOR THE DATA BALANCE
							index1 = bundleArray[i].indexOf("<Balance>");
							bdl_Balance=bundleArray[i].substring(index1+"<Balance>".length(),bundleArray[i].indexOf("</Balance>",index1));
							
							
						
						
							index1 = bundleArray[i].indexOf("<AcctResName>");
							String accountResName=bundleArray[i].substring(index1+"<AcctResName>".length(),bundleArray[i].indexOf("</AcctResName>",index1));
						
							index1 = bundleArray[i].indexOf("<ExpDate>");
							String expDate=bundleArray[i].substring(index1+"<ExpDate>".length(),bundleArray[i].indexOf("</ExpDate>",index1));
							//bdl_BalanceLng=0-(bdl_BalanceLng); // in response balance is negative to make it +ve
							//bdl_Balance=String.valueOf(bdl_BalanceLng);
							//p_requestMap.put("resp_Balance",bdl_Balance);
							responseMap.put("resp_accountResCode",bdl_code);
							responseMap.put("resp_accountResName",accountResName);
							responseMap.put("resp_expDate",expDate);
							
							responseMap.put("resp_creditData_"+bdl_code,bdl_Balance+":"+accountResName+":"+expDate);
						if(_log.isDebugEnabled())
							_log.debug("parseRechargeCreditResponse","resp_creditData_"+bdl_code+ "    "+bdl_Balance+":"+accountResName+":"+expDate);
						
					}
				}
				
				
			}
	          	
	          	
	            //Set the bundle info received from the response in to the response map.	
//	          	getBundlesInfoRcvdFromIN4Credit(responseStr,responseMap);
	        }
			return responseMap;
		}
		catch(Exception e)
		{
			_log.error("parseRechargeCreditResponse","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
			    _log.debug("parseRechargeCreditResponse","Exiting with Response Map="+responseMap);
		}
	}
 /**
  * @param map
  * @return requestStr String
  * @throws Exception
  */	
 	private String generateCreditAdjustRequest(HashMap<String,String> p_requestMap) throws Exception
    {
		if(_log.isDebugEnabled())
			_log.debug("generateCreditAdjustRequest","Entered with p_requestMap="+p_requestMap);
		String requestStr= null;
		StringBuffer sbf=null;
		String validity="0";
		String graceDays="0";
		try
		{

		
			double transAmtDbl=0;
  	 		if(!InterfaceUtil.isNullString(p_requestMap.get("transfer_amount")))
	 		{
				transAmtDbl=Double.parseDouble(p_requestMap.get("transfer_amount"));
				transAmtDbl = 0 - transAmtDbl;
			}
  	 		//Get the IN Request ID
			getINRequestID(p_requestMap);
			//Prepare the request string.
			sbf=new StringBuffer(1024);
			sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		 	sbf.append("<zsmart>");
		 	sbf.append("<Data>");
		 	sbf.append("<header>");
		 	sbf.append("<ACTION_ID>ModifyAllBalReturnAllBal</ACTION_ID>");
    	 	sbf.append("<REQUEST_ID>"+p_requestMap.get("IN_REQ_ID")+"</REQUEST_ID>");
    	 	sbf.append("</header>");
    	 	sbf.append("<body>");
            sbf.append("<MSISDN>"+InterfaceUtil.getFilterMSISDN( p_requestMap.get("INTERFACE_ID"),p_requestMap.get("MSISDN"))+"</MSISDN>");
           
            sbf.append("<AccountCode></AccountCode>");
          String creditBkTranID = p_requestMap.get("IN_RECON_ID");
				creditBkTranID = creditBkTranID.replace("S", "B");
			for (String bdl_data : p_requestMap.get("budle_balance_deduction")
					.split("\\|")) {
				transAmtDbl = Double.parseDouble(bdl_data.split(":")[0]);
				transAmtDbl = 0 - transAmtDbl;
				


				 sbf.append("<BalInputDtoList>");
				sbf.append("<AcctResCode>" + bdl_data.split(":")[1]+ "</AcctResCode>");
				// sbf.append("<AcctResName></AcctResName>");
				 sbf.append("<Balance></Balance>");
				sbf.append("<AddBalance>"+ String.valueOf(Math.round(transAmtDbl))+ "</AddBalance>");
				sbf.append("<AddDays>0</AddDays>");
				// sbf.append("<ExpDate></ExpDate>");
			
			
				sbf.append("</BalInputDtoList>");
			}
    	 	
	sbf.append("<TransactionSN>" + creditBkTranID+ "</TransactionSN>");
    	 	sbf.append("</body>");
    	 	sbf.append("</Data>");
    	 	sbf.append("</zsmart>");
    		requestStr=sbf.toString();
            if(_log.isDebugEnabled())
            	_log.debug("generateCreditAdjustRequest","Got the XML String as "+requestStr);
			return requestStr;
		}
		catch(Exception e)
		{
			_log.error("generateCreditAdjustRequest","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally{
			if(_log.isDebugEnabled())
			    _log.debug("generateCreditAdjustRequest","Exiting with requestStr="+requestStr);
		}
    }
/**
 * This Method parse re-charge credit response
 * @param responseStr String
 * @throws Exception
 * @return map
 */
 	 private HashMap<String,String> parseCreditAdjustResponse(String responseStr) throws Exception
	 {
	    if(_log.isDebugEnabled())
	        _log.debug("parseCreditAdjustResponse","Entered with responseStr="+responseStr);
		HashMap<String,String> responseMap=null;
		try
		{  
		    responseMap= new HashMap<String,String>();
		    String _str="0";
		    int index=responseStr.indexOf("<returnCode>");
		    String returnCode=responseStr.substring(index+"<returnCode>".length(),responseStr.indexOf("</returnCode>",index));
		    responseMap.put("resp_returnCode",returnCode);
		    if(_str.equals((returnCode)))
	        {
		    	index=responseStr.indexOf("<ACTION_ID>");
				if(index!=-1)
		    	{
					String actionId=responseStr.substring(index+"<ACTION_ID>".length(),responseStr.indexOf("</ACTION_ID>",index));
	           		responseMap.put("resp_action_id",actionId);
		    	}
			    index=responseStr.indexOf("<REQUEST_ID>");
			    if(index!=-1)
		    	{
			    	String reqId=responseStr.substring(index+"<REQUEST_ID>".length(),responseStr.indexOf("</REQUEST_ID>",index));
	           		responseMap.put("resp_req_id",reqId);
		    	}			    
	           	index=responseStr.indexOf("<TransactionSN>");
	          	if(index!=-1)
		    	{
	          		String transactionSN=responseStr.substring(index+"<TransactionSN>".length(),responseStr.indexOf("</TransactionSN>",index));
	          		responseMap.put("resp_transactionSN",transactionSN);
		    	}
			String bdl_code="";
			String dataBundlesStr = FileCache.getValue(_interfaceId,"SENDER_DATA_BUNDLES");
			index=responseStr.indexOf("</BalDto>");
	          	if(index!=-1)
		    	{
				long totalDataBalance=0;
				String[] bundleArray=responseStr.split("</BalDto>");
				int noOfBundles=bundleArray.length;
				for(int i=0; i<noOfBundles; i++)
				{
					int index1=0;
					String bdl_Balance="";
					index=bundleArray[i].indexOf("<AcctResCode>");
	          	if(index!=-1)
		    	{
						bdl_code=bundleArray[i].substring(index+"<AcctResCode>".length(),bundleArray[i].indexOf("</AcctResCode>",index));
						if(isBundleBelongTo(bdl_code,dataBundlesStr))
						{
							index1 = bundleArray[i].indexOf("<Balance>");
							bdl_Balance=bundleArray[i].substring(index1+"<Balance>".length(),bundleArray[i].indexOf("</Balance>",index1));
							long bdl_BalanceLng=Long.parseLong(bdl_Balance);
							totalDataBalance+=bdl_BalanceLng;
						
							index1 = bundleArray[i].indexOf("<AcctResName>");
							String accountResName=bundleArray[i].substring(index1+"<AcctResName>".length(),bundleArray[i].indexOf("</AcctResName>",index1));
						
							index1 = bundleArray[i].indexOf("<ExpDate>");
							String expDate=bundleArray[i].substring(index1+"<ExpDate>".length(),bundleArray[i].indexOf("</ExpDate>",index1));
							responseMap.put("resp_accountResCode",bdl_code);
							responseMap.put("resp_accountResName",accountResName);
							responseMap.put("resp_expDate",expDate);
		    	}
						
					}
				}
				
				responseMap.put("resp_Balance",String.valueOf(totalDataBalance));
		    	}
	         // 	getBundlesInfoRcvdFromIN4Credit(responseStr,responseMap);
	        }
			return responseMap;
		}
		catch(Exception e)
		{
			_log.error("parseCreditAdjustResponse","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
			    _log.debug("parseCreditAdjustResponse","Exiting with responseMap="+responseMap);
		}
	}
 /**
 * This Method Generate immediate Debit Request
 * @param map HashMap
 * @throws Exception
 * @return requestStr
 */
	 private String generateImmediateDebitOrCreditAdjustRequest(HashMap<String,String> p_requestMap) throws Exception
	 {

		if(_log.isDebugEnabled())
		    _log.debug("generateImmediateDebitOrCreditAdjustRequest","Entered with p_requestMap=" + p_requestMap);
		String requestStr= null;
		StringBuffer sbf=null;
		double feeMultFact = 100;
		String senderDebitID=null;
		try
		{
		    String module = p_requestMap.get("MODULE");
			if(!InterfaceUtil.isNullString(p_requestMap.get("FEE_MULT_FACT")))
				feeMultFact = Double.parseDouble(p_requestMap.get("FEE_MULT_FACT"));
			
		    double transAmtDbl=0;
  	 		if(!InterfaceUtil.isNullString(p_requestMap.get("transfer_amount")))
	 		{
				transAmtDbl=Double.parseDouble(p_requestMap.get("transfer_amount"));
				transAmtDbl = transAmtDbl*feeMultFact;

			}
			senderDebitID="DEBIT_"+p_requestMap.get("CARD_GROUP_SELECTOR");
			getINRequestID(p_requestMap);
			sbf=new StringBuffer(1024);
			sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		 	sbf.append("<zsmart>");
		 	sbf.append("<Data>");
		 	sbf.append("<header>");
		 	sbf.append("<ACTION_ID>ModifyAllBalReturnAllBal</ACTION_ID>");
    	 	sbf.append("<REQUEST_ID>"+p_requestMap.get("IN_REQ_ID")+"</REQUEST_ID>");
    	 	sbf.append("</header>");
    	 	sbf.append("<body>");
            sbf.append("<MSISDN>"+InterfaceUtil.getFilterMSISDN( p_requestMap.get("INTERFACE_ID"),p_requestMap.get("MSISDN"))+"</MSISDN>");
            String txnDesc = FileCache.getValue( p_requestMap.get("INTERFACE_ID"),module+"_TXN_DESC");
    	 	if(!InterfaceUtil.isNullString(txnDesc))
    	 	    sbf.append("<TransactionDescription>"+txnDesc+"</TransactionDescription>");
            sbf.append("<AccountCode></AccountCode>");
    	 	sbf.append("<TransactionSN>"+p_requestMap.get("IN_RECON_ID")+"</TransactionSN>");
   	 		sbf.append("<RechargingID></RechargingID>");
   	 		sbf.append("<SuspendAddDays></SuspendAddDays>");
    	 	//Set the balance input list
    	 	//sbf.append("<BalInputDtoList>");
    	 	/*sbf.append("<AcctResCode>"+FileCache.getValue(_interfaceId,senderDebitID)+"</AcctResCode>");
	 		sbf.append("<AcctResName></AcctResName>");
 			sbf.append("<Balance></Balance>");
	 		sbf.append("<AddBalance>"+String.valueOf(Math.round(transAmtDbl))+"</AddBalance>");
	 		sbf.append("<ExpDate></ExpDate>");
		    sbf.append("<AddDays>0</AddDays>");*/
			sbf.append("<BalInputDtoList>");
			for(String bdl_data : p_requestMap.get("budle_balance_deduction").split("\\|")){
				transAmtDbl=Double.parseDouble(bdl_data.split(":")[0]);
				transAmtDbl=0-transAmtDbl;
				
//
				
				sbf.append("<AcctResCode>"+bdl_data.split(":")[1]+"</AcctResCode>");
//				sbf.append("<AcctResName></AcctResName>");
				sbf.append("<Balance></Balance>");
				sbf.append("<AddBalance>"+String.valueOf(Math.round(transAmtDbl))+"</AddBalance>");
//				sbf.append("<DeductBalance>"+String.valueOf(Math.round(transAmtDbl))+"</DeductBalance>");
				sbf.append("<ExpDate></ExpDate>");
				sbf.append("<AddDays>0</AddDays>");
//				sbf.append("</BalInputDtoList>");
    	 	}
			sbf.append("</BalInputDtoList>");
    	 	
			sbf.append("</body>");
    	 	sbf.append("</Data>");
    	 	sbf.append("</zsmart>");
    		requestStr=sbf.toString();
            if(_log.isDebugEnabled())
            	_log.debug("generateImmediateDebitOrCreditAdjustRequest","Got the XML String as "+requestStr);
			return requestStr;
		}
		catch(Exception e)
		{
			_log.error("generateImmediateDebitOrCreditAdjustRequest","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally{
			if(_log.isDebugEnabled())
			    _log.debug("generateImmediateDebitOrCreditAdjustRequest","Exiting with requestStr="+requestStr);
		}
	 } 
 /**
 * This Method parse Immediate Debit Response
 * @param p_responseStr String
 * @throws Exception
 * @return map
 */
	public HashMap<String,String> parseImmediateDebitResponse(String p_responseStr) throws Exception
	{

	    if(_log.isDebugEnabled())
	    	_log.debug("parseImmediateDebitResponse","Entered with p_responseStr="+p_responseStr);
		HashMap<String,String> responseMap=null;
		try
		{  
		    responseMap= new HashMap<String,String>();
		    String _str="0";
		    int index=p_responseStr.indexOf("<returnCode>");
		    String returnCode=p_responseStr.substring(index+"<returnCode>".length(),p_responseStr.indexOf("</returnCode>",index));
		    responseMap.put("resp_returnCode",returnCode);
		    if(_str.equals((returnCode)))
	        {
		    	//_requestMap or INTERFACE_ID not available here so AccountResCode is hard-coded.
		    	/*int end = p_responseStr.indexOf("<AccountResCode>1</AccountResCode>");//1 is for main account on live environment.
		    	end = p_responseStr.indexOf("</BalDtoList>", end);*/
		    	
				index=p_responseStr.indexOf("<ACTION_ID>");
				if(index!=-1)
		    	{
					String actionId=p_responseStr.substring(index+"<ACTION_ID>".length(),p_responseStr.indexOf("</ACTION_ID>",index));
	           		responseMap.put("resp_action_id",actionId);
		    	}
			    index=p_responseStr.indexOf("<REQUEST_ID>");
			    if(index!=-1)
		    	{
			    	String reqId=p_responseStr.substring(index+"<REQUEST_ID>".length(),p_responseStr.indexOf("</REQUEST_ID>",index));
	           		responseMap.put("resp_req_id",reqId);
		    	}
			    
	           
			    	   // for all bundles choose main or Data 
			String bdl_code="";
			String dataBundlesStr = FileCache.getValue(_interfaceId,"SENDER_DATA_BUNDLES");
			index=p_responseStr.indexOf("</BalDto>");
			if(index!=-1)
			{
				long totalDataBalance=0;
				String[] bundleArray=p_responseStr.split("</BalDto>");
				int noOfBundles=bundleArray.length;
				for(int i=0; i<noOfBundles; i++)
				{
					//get the bdl_name if present in the bucket.
					int index1=0;
					String bdl_Balance="";
					index=bundleArray[i].indexOf("<AcctResCode>");
					if(index!=-1)
					{
						bdl_code=bundleArray[i].substring(index+"<AcctResCode>".length(),bundleArray[i].indexOf("</AcctResCode>",index));
		        	
						// FOR THE DATA BALANCE
						if(isBundleBelongTo(bdl_code,dataBundlesStr))
						{
							index1 = bundleArray[i].indexOf("<Balance>");
							bdl_Balance=bundleArray[i].substring(index1+"<Balance>".length(),bundleArray[i].indexOf("</Balance>",index1));
							long bdl_BalanceLng=Long.parseLong(bdl_Balance);
							totalDataBalance+=bdl_BalanceLng;
							
						
							index1 = bundleArray[i].indexOf("<AcctResName>");
							String accountResName=bundleArray[i].substring(index1+"<AcctResName>".length(),bundleArray[i].indexOf("</AcctResName>",index1));
						
							index1 = bundleArray[i].indexOf("<ExpDate>");
							String expDate=bundleArray[i].substring(index1+"<ExpDate>".length(),bundleArray[i].indexOf("</ExpDate>",index1));
							//bdl_BalanceLng=0-(bdl_BalanceLng); // in response balance is negative to make it +ve
							//bdl_Balance=String.valueOf(bdl_BalanceLng);
							//p_requestMap.put("resp_Balance",bdl_Balance);
							responseMap.put("resp_accountResCode",bdl_code);
							responseMap.put("resp_accountResName",accountResName);
							responseMap.put("resp_expDate",expDate);
						}
						
					}
				}
				
				responseMap.put("resp_Balance",String.valueOf(totalDataBalance));
			}
	          	
			   
			   
		     /*   index=p_responseStr.lastIndexOf("<EffDate>", end);
		        if(index!=-1)
		    	{
		        	String effDate=p_responseStr.substring(index+"<EffDate>".length(),p_responseStr.indexOf("</EffDate>",index));
	           		responseMap.put("resp_effDate",effDate);
		    	}*/
		      
	           	/*index=p_responseStr.lastIndexOf("<BalanceID>", end);
	           	if(index!=-1)
		    	{
	           		String balanceID=p_responseStr.substring(index+"<BalanceID>".length(),p_responseStr.indexOf("</BalanceID>",index));
	           		responseMap.put("resp_balanceID",balanceID);
		    	}*/
	          	/*index=p_responseStr.lastIndexOf("<AccountResCode>");
	          	if(index!=-1)
		    	{
	          		String accountResCode=p_responseStr.substring(index+"<AccountResCode>".length(),p_responseStr.indexOf("</AccountResCode>",index));
	          		responseMap.put("resp_accountResCode",accountResCode);
		    	}*/
		      
	          	
	          	
	          	
	        }
			return responseMap;
		}
		catch(Exception e)
		{
			_log.error("parseImmediateDebitResponse","Exception e: "+e);
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
			    _log.debug("parseImmediateDebitResponse","Exiting with responseMap="+responseMap);
		}
	}
 /**
 * This Method will generate the request string for the bundles.
 * @param p_requestMap HashMap
 * @throws Exception
 * @return bundleReqStr String
 */	
	private String getBundleRequestString(HashMap<String,String> p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled())
		    _log.debug("getBundleRequestString","Entered");
		String bundleReqStr="";
		StringBuffer sbfBundle=null;
		String[] bundleCodes=null;
		String[] bundleIds=null;
		String[] bundleValidities=null;
		String[] bundleValues=null;
		String[] bundleTypes=null;
		int bonusBundleCount=0;
		
		try
		{
			if(!InterfaceUtil.isNullString(p_requestMap.get("BONUS_BUNDLE_IDS")))
			{
			    bundleCodes=(p_requestMap.get("BONUS_BUNDLE_CODES")).split("\\|");
				bundleIds=(p_requestMap.get("BONUS_BUNDLE_IDS")).split("\\|");
				bundleValidities=(p_requestMap.get("BONUS_BUNDLE_VALIDITIES")).split("\\|");
				bundleValues=(p_requestMap.get("BONUS_BUNDLE_VALUES")).split("\\|");
				bundleTypes=(p_requestMap.get("BONUS_BUNDLE_TYPES")).split("\\|");
			
			    String amtMultFactor=FileCache.getValue(_interfaceId,"AMT_MULT_FACTOR");
			    String unitMultFactor=FileCache.getValue(_interfaceId,"UNIT_MULT_FACTOR");
			    if(InterfaceUtil.isNullString(amtMultFactor) || InterfaceUtil.isNullString(unitMultFactor))
			    {
	                _log.error("getBundleRequestString","Value of AMT_MULT_FACTOR UNIT_MULT_FACTOR is not defined in the INFile");
	                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	            }
				bonusBundleCount=bundleIds.length;
				sbfBundle=new StringBuffer(1024);
				for(int i=0; i<bonusBundleCount;i++)
				{
				    String bundleCode=FileCache.getValue(_interfaceId,bundleCodes[i]);

				    if(InterfaceUtil.isNullString(bundleCode) || bundleCode.equals(p_requestMap.get("CARD_GROUP_SELECTOR")))
						continue;
					
					String bundleValue=bundleValues[i];
					String bundleValidity=bundleValidities[i];
					double bundleValueDbl=Double.parseDouble(bundleValue);
					double inbundleValueDbl=0;
					if(bundleValueDbl>0 || Long.parseLong(bundleValidity)>0)
					{
						//Calculate the amount by the help of multiplication factor.
						if("AMT".equals(bundleTypes[i]))
							inbundleValueDbl=InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl,Double.parseDouble(amtMultFactor));
						else if("UNIT".equals(bundleTypes[i]))
							inbundleValueDbl=InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl,Double.parseDouble(unitMultFactor));
						
						

						//set the bundle parameters in to the request string.
						sbfBundle.append("<BalInputDtoList>");
						sbfBundle.append("<AcctResCode>"+bundleCode+"</AcctResCode>");
						sbfBundle.append("<AcctResName></AcctResName>");
						sbfBundle.append("<Balance></Balance>");
						sbfBundle.append("<AddBalance>"+String.valueOf(Math.round(inbundleValueDbl))+"</AddBalance>");
						sbfBundle.append("<ExpDate></ExpDate>");
						sbfBundle.append("<AddDays>"+bundleValidity+"</AddDays>");
						sbfBundle.append("</BalInputDtoList>");
					}				
				}
				bundleReqStr=sbfBundle.toString();
			}
			return bundleReqStr;
		}
		catch(Exception e)
		{
            e.printStackTrace();
            _log.error("CP2PDataReqResFormatter[getBundleRequestString]","Exception e::"+e.getMessage());
             throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
			    _log.debug("getBundleRequestString","Exiting  with bundleReqStr="+bundleReqStr);
		}
	}
/**
 * This Method will parse  GetAccount Info Response for received bundles.
 * @param p_responseStr String
 * @param p_requestMap HashMap
 * @throws Exception
 * @return void
 */	
	private void getBundlesInfoRcvdFromIN(String p_responseStr, HashMap<String,String> p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled())
		    _log.debug("getBundlesInfoRcvdFromIN","Entered ");
		int index=0;
		int noOfBundles=0;
		String bdl_code=null;
		String bdlCodeStr=null;
		
		String bdl_BalanceStr = "";
		String bdl_ExpDateStr = "";
		String bdl_Balance = "";
		String bdl_ExpDate = "";
		String bdl_EffDate = null;
		double  dataMultFactor = Double.parseDouble(FileCache.getValue(_interfaceId,"DATA_MULT_FACTOR").trim());
		double  dataDivideFactor = Double.parseDouble(FileCache.getValue(_interfaceId,"DATA_DIVIDE_FACTOR").trim());
		
		int index1 =0;
		long totalBalance=0;
		HashMap<String, Boolean> dataBundles = new HashMap<>();
		String dataBundlesStr = FileCache.getValue(_interfaceId,"SENDER_DATA_BUNDLES");
		Date currentDate = new Date();
		currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
		if(_log.isDebugEnabled())
		    _log.debug("getBundlesInfoRcvdFromIN","dataBundlesStr :  "+dataBundlesStr);
		
		// taking only the bundles which in mentioned in IN File For DATA under SENDER_DATA_BUNDLES
		try{
		if(dataBundlesStr!=null){
			for(String dataBundle :dataBundlesStr.split(",") )
			{
				dataBundles.put(dataBundle.trim(), true);
			}
		}else
 			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			//throw new BTSLBaseException(InterfaceErrorCodesI.SENDER_DATA_BUNDLES_NOT_FOUND);
		}
		catch(BTSLBaseException be)
		{
		    _log.error("getBundlesInfoRcvdFromIN","BTSLBaseException be = "+be.getMessage());
		    throw be;
		}//end of BTSLBaseException
		try
		{
			//Check  whether bundle is present in string or not, if present then split it.
			index=p_responseStr.indexOf("</BalDto>");
			if(index!=-1)
			{
				String[] bundleArray=p_responseStr.split("</BalDto>");
				noOfBundles=bundleArray.length;
				for(int i=0; i<noOfBundles; i++)
				{
					//get the bdl_name if present in the bucket.
					index=bundleArray[i].indexOf("<AcctResCode>");
					if(index!=-1)
					{
						bdl_code=bundleArray[i].substring(index+"<AcctResCode>".length(),bundleArray[i].indexOf("</AcctResCode>",index));
						// FOR THE MAIN BALANCE NEED TO DEBIT PROCESSING FEE
							index1 = bundleArray[i].indexOf("<Balance>");
							bdl_Balance=bundleArray[i].substring(index1+"<Balance>".length(),bundleArray[i].indexOf("</Balance>",index1));
							//bdl_BalanceLng=0-(bdl_BalanceLng); // in response balance is negative to make it +ve
							
							
							index1 = bundleArray[i].indexOf("<AcctResName>");
							String accountResName=bundleArray[i].substring(index1+"<AcctResName>".length(),bundleArray[i].indexOf("</AcctResName>",index1));
						//For the DATA BUNDLES 

							// RECTIFYING BUNDLES AND TAKING ONLY THOSE BUNDLE WHICH MENTIONED IN INFile for DATA
		
								
								index1 = bundleArray[i].indexOf("<ExpDate>");
							String expDate=bundleArray[i].substring(index1+"<ExpDate>".length(),bundleArray[i].indexOf("</ExpDate>",index1));
							
								
							
								//bdl_BalanceLng=0-(bdl_BalanceLng);
								
							//bdl_Balance=String.valueOf(bdl_BalanceLng);
								
							p_requestMap.put("resp_accountResCode",bdl_code);
							p_requestMap.put("resp_accountResName",accountResName);
							p_requestMap.put("resp_expDate",expDate);
							
								
								
							p_requestMap.put("resp_valData_"+bdl_code,bdl_Balance+":"+accountResName+":"+expDate);
						if(_log.isDebugEnabled())
							_log.debug("parseRechargeCreditResponse","resp_creditData_"+bdl_code+ "    "+bdl_Balance+":"+accountResName+":"+expDate);

								

						 p_requestMap.put("SELECTOR_BUNDLE_ID","1");
					}
				}
			}

			
		}
		
		catch(Exception e)
		{
			_log.error("getBundlesInfoRcvdFromIN","Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
			    _log.debug("getBundlesInfoRcvdFromIN","Exiting Defined bundles at IN="+p_requestMap.get("RECEIVED_BUNDLES")+"SELECTOR_BUNDLE_ID"+p_requestMap.get("SELECTOR_BUNDLE_ID"));
		}
	}
/**
 * This Method will parse the Credit Response for received bundles.
 * @param p_responseStr String
 * @param p_requestMap HashMap
 * @throws Exception
 * @return void
 */		
	private void getBundlesInfoRcvdFromIN4Credit(String p_responseStr, HashMap<String,String> p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled())
		    _log.debug("getBundlesInfoRcvdFromIN4Credit","Entered ");
		int index=0;
		int startInd=0;
		int endInd=0;
		String subStr=null;
		int noOfBundles=0;
		String bdl_code=null;
		String bdlCodeStr=null;
		try
		{
			//Check  whether bundle is present in string or not, if present then split it.
			startInd=p_responseStr.indexOf("<BalDtoList>");
			endInd=p_responseStr.indexOf("</BalDtoList>");
			subStr=p_responseStr.substring(startInd, endInd);
			if(_log.isDebugEnabled())
			    _log.debug("getBundlesInfoRcvdFromIN4Credit","subStr="+subStr);
			index=p_responseStr.indexOf("</BalDto>");
			if(index!=-1)
			{
				String[] bundleArray=subStr.split("/BalDto>");
				noOfBundles=bundleArray.length;
				for(int i=0; i<noOfBundles; i++)
				{
					//get the bdl_name if present in the bucket.
					index=bundleArray[i].indexOf("<AcctResCode>");
					if(index!=-1)
					{
						bdl_code=bundleArray[i].substring(index+"<AcctResCode>".length(),bundleArray[i].indexOf("</AcctResCode>",index));
						if(bdl_code.equalsIgnoreCase("1"))
						{
							int index1 = bundleArray[i].indexOf("<Balance>");
							String bdl_Balance=bundleArray[i].substring(index1+"<Balance>".length(),bundleArray[i].indexOf("</Balance>",index1));
							long bdl_BalanceLng=Long.parseLong(bdl_Balance);
						//	bdl_BalanceLng=0-(bdl_BalanceLng);
							bdl_Balance=String.valueOf(bdl_BalanceLng);
							p_requestMap.put("resp_Balance",bdl_Balance);
							
							index1 = bundleArray[i].indexOf("<ExpDate>");
							String bdl_ExpDate=bundleArray[i].substring(index1+"<ExpDate>".length(),bundleArray[i].indexOf("</ExpDate>",index1));
							p_requestMap.put("resp_ExpDate",bdl_ExpDate);
							
							index1 = bundleArray[i].indexOf("<EffDate>");
							String bdl_EffDate=bundleArray[i].substring(index1+"<EffDate>".length(),bundleArray[i].indexOf("</EffDate>",index1));
							p_requestMap.put("resp_EffDate",bdl_EffDate);
						}
						if(!InterfaceUtil.isNullString(bdlCodeStr))
							bdlCodeStr=bdlCodeStr+","+bdl_code;
						else
							bdlCodeStr=bdl_code;
					}
				}
			}
			if(!InterfaceUtil.isNullString(bdlCodeStr))
				p_requestMap.put("received_bundles",bdlCodeStr.trim());	
			else
				p_requestMap.put("received_bundles",bdlCodeStr);	
		}
		catch(Exception e)
		{
			_log.error("getBundlesInfoRcvdFromIN4Credit","Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
			    _log.debug("getBundlesInfoRcvdFromIN4Credit","Exiting Defined bundles at IN="+p_requestMap.get("received_bundles"));
		}
	}
 /**
 * This Method will generate the IN Request ID(a unique sequence identify a Request, can not be repeated),
 * for each request. 
 * The format is: Channel_ID+yyyyMMddHHmmss+8 bit sequence no.
 * @param p_map HashMap
 * @throws Exception
 * @return dateStrReqTime String
 */	
	private String getINRequestID(HashMap<String,String> p_map) throws Exception
    {
          if(_log.isDebugEnabled())
              _log.debug("getINRequestID","Entered");
          String reqId="";
          String counter="";
          String dateStrReqId = null;
          String dateStrReqTime = null;
          String timeStrReqTime = null;
          SimpleDateFormat sdfReqId =null;
          SimpleDateFormat sdfReqTime =null;
          SimpleDateFormat sdfTimeReqTime =null;
          // 5 bit sequence number is required to generate the IN Request ID.
          int inTxnLength=5;
          try
          {
                Date mydate = new Date();
                sdfReqId = new SimpleDateFormat ("yyMMdd");
                sdfReqTime = new SimpleDateFormat ("yyyyMMddHHmmss");
                sdfTimeReqTime= new SimpleDateFormat ("HHmmss");
                dateStrReqId = sdfReqId.format(mydate);
                dateStrReqTime = sdfReqTime.format(mydate);
                timeStrReqTime = sdfTimeReqTime.format(mydate);
                counter = getIncrCounter();
                if(_log.isDebugEnabled())
                	_log.debug("getINRequestID","counter value is "+counter);
                
                int length = counter.length();
                int tmpLength=inTxnLength-length;
                if(length<inTxnLength)
                {
                    for(int i=0;i<tmpLength;i++)
                        counter = "0"+counter;
                }
                reqId =p_map.get("CHANNEL_ID")+dateStrReqId+timeStrReqTime+Constants.getProperty("INSTANCE_ID")+counter;
                p_map.put("IN_REQ_ID",reqId);
                p_map.put("IN_REQ_TIME",dateStrReqTime);
                if(_log.isDebugEnabled())_log.debug("getINRequestID","Exited  id: "+counter+", reqId="+reqId);
                return reqId;
          }
       catch(Exception e)
       {
             e.printStackTrace();
             _log.error("CP2PDataReqResFormatter[getINRequestID]","Exception e="+e.getMessage());
              throw e;
       }//end of catch-Exception
    }
 /**
 * This Method will generate the IN Transaction ID for each request.
 * @param p_map HashMap
 * @throws BTSLBaseException
 * @return _counter String
 */
	public static synchronized String getIncrCounter() throws BTSLBaseException
	{
	    if(_log.isDebugEnabled())_log.debug("getIncrCounter","Entered");
	    try
	    {
	        if(_counter==99999)
	            _counter=0;
	        _counter++;
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	        _log.error("getIncrCounter",e.getMessage());
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEKenyaRequestFormatter[getIncrCounter]","","", ""," Error occurs while getting IN request id Exception is "+e.getMessage());
	        throw new BTSLBaseException(e.getMessage());
	    }
	    finally
	    {
	        if(_log.isDebugEnabled()) 
	            _log.debug("getIncrCounter","Exiting counter = "+_counter);
	    }
	    return String.valueOf(_counter);
	}
/**
 * This Method is responsible to do the parsing of buckets to get bundle names defined at IN.
 * @param p_bundleCode String
 * @param p_requestMap HashMap
 * @throws Exception
 * @return void
 *//*
	private boolean isBundleAllowed(String p_bundleCode, HashMap<String,String> p_requestMap) throws Exception
	{
		
		if(_log.isDebugEnabled())
			_log.debug("isBundleAllowed","Entered for p_bundleID="+p_bundleCode);
		boolean isAllowed=false;
		try
		{
			//Remove the selector bonus from the bonus string.
			if(p_bundleCode.equals(p_requestMap.get("CARD_GROUP_SELECTOR")))
				return isAllowed;
			//Check whether bonus bundle code is received from the IN in validate request or not.
			String bundleFromIN=p_requestMap.get("IN_RESP_BUNDLE_CODES");
			if(!InterfaceUtil.isNullString(bundleFromIN) && bundleFromIN.contains(p_bundleCode))
				isAllowed=true;
			return isAllowed;
		}
		catch(Exception e)
		{
			_log.error("isBundleAllowed","Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug("isBundleAllowed","Exiting: Is "+p_bundleCode+" is allow on the IN="+ isAllowed);
		}
	}*/
	
	
	/**
	 * this method to check selector value from COnstant.props belonging
	 * @param p_selectorString
	 * @return
	 */
	private boolean isBundleBelongTo(String p_bundleCode,String p_bundlesString){
		if(_log.isDebugEnabled()) 
			_log.debug("isBundleBelongTo"," p_bundleCode="+p_bundleCode+" , p_selectorString : "+p_bundlesString);
		String[] bundleList = BTSLUtil.split(p_bundlesString , ",");
		for(String bundle : bundleList)
		{
			if(bundle.trim().equals(p_bundleCode))
				return true;
		}
		return false;
	}
}

