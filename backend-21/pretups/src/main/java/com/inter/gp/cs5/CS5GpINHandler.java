package com.inter.gp.cs5;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.inter.gp.cs5.cs5scheduler.NodeManager;
import com.inter.gp.cs5.cs5scheduler.NodeScheduler;
import com.inter.gp.cs5.cs5scheduler.NodeVO;
import com.inter.voms.VOMSHandler;

public class CS5GpINHandler implements InterfaceHandler{
	
	private static String CLASS_NAME="CS5GpINHandler";

    private static Log log = LogFactory.getLog(CS5GpINHandler.class.getName());
    private HashMap<String,String> _requestMap = null;//Contains the respose of the request as key and value pair.
    private HashMap<String,String> _responseMap_c = null;
    private HashMap<String,String> _responseMap = null;
	private String _interfaceID=null;//Contains the interfaceID
	private String _inTXNID=null;//UNodesed to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;
	private String  _userType=null;
	private String _stage=null;
   	private static CS5GpRequestFormatter _cs5gpRequestFormatter=null;
	
	
	private static  SimpleDateFormat _sdf = new SimpleDateFormat ("yyMMddHHmm");
	private static int _transactionIDCounter=0;
	private static int _prevMinut=0;
	private Object[] successList=CS5GpI.RESULT_OK.split(",");
	
	
	 
	static
	{
		
		LogFactory.printLog("Static", PretupsI.ENTERED, log);
	    
	    try
	    {
	    	_cs5gpRequestFormatter = new CS5GpRequestFormatter();
	    }
	    catch(Exception e)
	    {
	    	log.errorTrace("Exception",e);
	        log.error(CLASS_NAME+"[static]","While instantiation of CS5GpRequestFormatter get Exception e::"+e.getMessage());
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"[static]","","", "","While instantiation of CS5GpRequestFormatter get Exception e::"+e.getMessage());
	    }
	    finally
	    {
	       LogFactory.printLog("Static", PretupsI.EXITED, log);
	    }
	}
	
	/**
     * Implements the logic that validate the subscriber and get the subscriber information 
     * from the IN.
     * @param	HashMap  p_requestMap
     * @throws	BTSLBaseException, Exception 
     */
    public void validate(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception
    {
    	String METHOD_NAME="validate";
    	LogFactory.printLog(METHOD_NAME, "Entered p_requestMap:"+p_requestMap, log);
    	
         _requestMap = p_requestMap;
         try
		 {
        	 if(_requestMap.get("REQ_FROM_NSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)_requestMap.get("REQ_FROM_NSMS")))
        	 {
        		 _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        		 _requestMap.put("SERVICE_CLASS", PretupsI.ALL);
        		 _requestMap.put("ACCOUNT_STATUS","ACTIVE");
        	 }
        	 else
        	 {
        		 _interfaceID=(String)_requestMap.get("INTERFACE_ID");
        		 _msisdn=(String)_requestMap.get("MSISDN"); 	
        		 if(!InterfaceUtil.isNullString(_msisdn))
        		 {
        			 _msisdn =InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);                        
        		 }
        		 _requestMap.put("FILTER_MSISDN",_msisdn);
        		 //Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
        		 //If validation of subscriber is not required set the SUCCESS code into request map and return.
        		 String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
        		 //String validateRequired="Y";
        		 if("N".equals(validateRequired))
        		 {
        			 _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        			 return ;
        		 }
        		 _inTXNID=getINTransactionID(_requestMap);	
        		 _requestMap.put("IN_RECON_ID",_inTXNID);
        		 _requestMap.put("IN_TXN_ID",_inTXNID);
        		 _referenceID=(String)_requestMap.get("TRANSACTION_ID");
        		 String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
        		 if(log.isDebugEnabled()) log.debug("validate","multFactor: "+multFactor);
        		 if(InterfaceUtil.isNullString(multFactor))
        		 {
        			 log.error(METHOD_NAME,"MULT_FACTOR  is not defined in the INFile");
        			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
        			 throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
        		 }
        		 multFactor=multFactor.trim();
        		 //Set the interface parameters into requestMap
        		 setInterfaceParameters();
        		//String  _iNTraceEnabled=(String)_requestMap.get("UCIP_IN_DISABLED_FLAG");
        		//changed by rajeev
        		/*if("Y".equals(ucipFlag))
        		{
        			_reqRespAllowed=true;
        		}*/
        		 //key value of requestMap is formatted into XML string for the validate request.
        		 String inStr = _cs5gpRequestFormatter.generateRequest(CS5GpI.ACTION_ACCOUNT_INFO,_requestMap);
        		 
        		  //sending the AccountInfo request to IN along with validate action defined in CS5GpI interface
        		 sendRequestToIN(inStr,CS5GpI.ACTION_ACCOUNT_INFO);
        		 
        		  String interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");
        		  
        		  
        		 
        		  
        		  if(CS5GpI.ACCOUNT_NOT_ACTIVE.equals(interfaceStatus))
        		  {
        				if(log.isDebugEnabled()) log.debug(METHOD_NAME,"Entered interfaceStatus is   :"+interfaceStatus);
        			  String inStr1 = _cs5gpRequestFormatter.generateRequest(CS5GpI.ACTION_ACCOUNT_DETAILS,_requestMap);
                      sendRequestToIN(inStr1,CS5GpI.ACTION_ACCOUNT_DETAILS);
        		  }
        		  interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");

        		 //set TRANSACTION_STATUS as Success in request map
        		 _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        		 String amountStr ="0";
        		 amountStr=(String)_responseMap.get("accountValue1");
           		 
           		 //dedicatedAccountInformation and account value 
           		 String  da100=(String)_requestMap.get("DEDICATED_ACCOUNT_FOR_POSTPAID");
           		 String daAccInfoString=(String)_responseMap.get("dedicatedAccountInformation");
           		 String da100AccountValue =null;
           		 if(!InterfaceUtil.isNullString(daAccInfoString))
           		 {
           			// <member><name>dedicatedAccountID</name><value><i4>100</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>24372</string></value></member><member> 
           			int indexStart=0;
           			int tempIndex=0; 
           			int indexEnd=0;
           			 indexStart= daAccInfoString.indexOf("<member><name>dedicatedAccountID</name><value><i4>"+da100+"</i4></value></member><member><name>dedicatedAccountValue1</name><value>",indexEnd);
           			 tempIndex = daAccInfoString.indexOf("dedicatedAccountValue1",indexStart);
           			 if(tempIndex>0)
           			 {
           				da100AccountValue = daAccInfoString.substring("<string>".length()+daAccInfoString.indexOf("<string>",tempIndex),daAccInfoString.indexOf("</string>",tempIndex)).trim();
           		           
           				 indexEnd = daAccInfoString.indexOf("</member>",indexStart);
           			 }
           			 
           		 }
           		LogFactory.printLog(METHOD_NAME, "Entered pda100AccountValue :"+da100AccountValue, log);
           		 
           			
           		
     			if(Long.valueOf(_responseMap.get("serviceClassCurrent")) >Long.valueOf(_requestMap.get("PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE")))
     			{
     				amountStr=da100AccountValue;
          			 if(InterfaceUtil.isNullString(amountStr))
          				 amountStr="0";

     			}
           		 //get value of accountValue1 from response map (accountValue1 was set in response map in sendRequestToIN method.)
        		 
     		 	
        		 try
        		 {
        			 amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
        			 _requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
        			 //temporary to run CS5 idea as postpaid interface
        			 _requestMap.put("AvailableBalance",amountStr);
        			 _requestMap.put("BILL_AMOUNT_BAL","0");
        		 }
        		 catch(Exception e)
        		 {
        			 log.errorTrace("Exception",e);
        			 log.error(METHOD_NAME,"Exception e:"+e.getMessage());
        			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:"+e.getMessage());
        			 throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_RESPONSE);
        		 }
        		 Date supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("supervisionExpiryDate"), "yyyyMMdd");
        		 Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");


        		 Date currentDate = new Date();
        		 if("0".equals(_requestMap.get("CS5_BAR_FLAG")))
        		 {
        			 if("1".equals(_responseMap.get("temporaryBlockedFlag")))
        			 {
        				 _requestMap.put("ACCOUNT_STATUS","BARRED");
        			 }    
        		 }

        		 if(supervisionExpiryDate.after(currentDate))
        			 _requestMap.put("ACCOUNT_STATUS","ACTIVE");
        		 else if(currentDate.after(supervisionExpiryDate))
        			 _requestMap.put("ACCOUNT_STATUS","INACTIVE");
        		 else if(currentDate.after(serviceFeeExpiryDate))
        			 _requestMap.put("ACCOUNT_STATUS","DEACTIVE");

        		 _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
        		 //set OLD_EXPIRY_DATE in request map as returned from _responseMap.
        		 _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("supervisionExpiryDate")).trim(), "yyyyMMdd"));
        		 _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("serviceFeeExpiryDate")).trim(), "yyyyMMdd"));


        		 // Logic is written by Veer for validation On IN for EVD Service

        		 String  serviceType =(String)_requestMap.get("REQ_SERVICE");
        		 if(PretupsI.SERVICE_TYPE_EVD.equals(serviceType))
        			 new VOMSHandler().validate(_requestMap);
        	 }
		 }
         catch (BTSLBaseException be)
         {
        	 log.errorTrace("Exception",be);
        	log.error(METHOD_NAME,"BTSLBaseException be="+be.getMessage());
        	throw be; 	   	
         }
         catch(Exception e)
         {
        	 log.errorTrace("Exception",e);
            log.error(METHOD_NAME,"Exception e:"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
        	 LogFactory.printLog(METHOD_NAME, "Exiting with  _requestMap: "+_requestMap, log); 
        	
         }		 
    }//end of validate
    /**
     * Implements the logic that credit the subscriber account on IN.
     * @param	HashMap  p_requestMap
     * @throws	BTSLBaseException, Exception  
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
    	String METHOD_NAME="credit";
    	LogFactory.printLog(METHOD_NAME, "Entered p_requestMap:"+p_requestMap, log);
    	
    	double systemAmtDouble=0;
    	double multFactorDouble=0;
    	String amountStr=null;
        _requestMap = p_requestMap;
	
        try
         {
        	if(_requestMap.get("REQ_FROM_NSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)_requestMap.get("REQ_FROM_NSMS")))
          	 {
          		 _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
          	 }
        	else
        	{
        		_interfaceID=(String)_requestMap.get("INTERFACE_ID");		
        		_inTXNID=getINTransactionID(_requestMap);
        		_requestMap.put("IN_RECON_ID",_inTXNID);
        		_requestMap.put("IN_TXN_ID",_inTXNID);
        		_referenceID=(String)_requestMap.get("TRANSACTION_ID");
        		_msisdn=(String)_requestMap.get("MSISDN");
        		if(!InterfaceUtil.isNullString(_msisdn))
        			_msisdn =InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);                        

        		_requestMap.put("FILTER_MSISDN",_msisdn);
        		_userType=(String)_requestMap.get("USER_TYPE");

        		//Fetching the MULT_FACTOR from the INFile.
        		//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
        		String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
        		if(log.isDebugEnabled())log.debug("credit","multFactor:"+multFactor);
        		if(InterfaceUtil.isNullString(multFactor))
        		{
        			log.error("credit","MULT_FACTOR  is not defined in the INFile");
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
        			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
        		}
        		multFactor = multFactor.trim();
        		_requestMap.put("MULT_FACTOR",multFactor);

        		//Set the interface parameters into requestMap
        		setInterfaceParameters();
        		
        		//String  ucipFlag=(String)_requestMap.get("UCIP_IN_DISABLED_FLAG");
        		
        		/*if("Y".equalsIgnoreCase(ucipFlag))
        		{
        			_reqRespAllowed=true;
        		}*/
        		_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
        		try
        		{
        			multFactorDouble=Double.parseDouble(multFactor);
        			double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
        			systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
        			amountStr=String.valueOf(systemAmtDouble);
        			//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
        			String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
        			
        			LogFactory.printLog(METHOD_NAME, "From file cache roundFlag = "+roundFlag, log);
        			
        			//If the ROUND_FLAG is not defined in the INFile 
        			if(InterfaceUtil.isNullString(roundFlag))
        			{
        				roundFlag="Y";
        				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5GpINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
        			}
        			//If rounding of amount is allowed, round the amount value and put this value in request map.
        			if("Y".equals(roundFlag.trim()))
        			{
        				amountStr=String.valueOf(Math.round(systemAmtDouble));
        				_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
        			}
        		}
        		catch(Exception e)
        		{
        			log.errorTrace("Exception",e);
        			log.error("credit","Exception e:"+e.getMessage());
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
        			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        		}
        	
        		LogFactory.printLog(METHOD_NAME, "transfer_amount:"+amountStr, log);
        		//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
        		_requestMap.put("transfer_amount",amountStr);

//      		key value of requestMap is formatted into XML string for the validate request.
        		String inStr = _cs5gpRequestFormatter.generateRequest(CS5GpI.ACTION_RECHARGE_CREDIT,_requestMap);

        		//sending the Re-charge request to IN along with re-charge action defined in CS5GpI interface
        		sendRequestToIN(inStr,CS5GpI.ACTION_RECHARGE_CREDIT);
        		//set TRANSACTION_STATUS as Success in request map

        		
        		String interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");
        		String prevStatus = "";
    		 if(Arrays.asList(successList).contains(interfaceStatus)){
  		          		_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
    		 }else if(("true".equalsIgnoreCase((String)_requestMap.get("RESPONSE_CODE_100")) && ((String)_requestMap.get("RESPONSE_CODE_100_HANDLING")).contains(interfaceStatus)) || BTSLUtil.NullToString(interfaceStatus).equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS))
	            {
    			 prevStatus = interfaceStatus;
    			 try {
    				 String previousbalance = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
 	                //setInterfaceParameters();
 	            	String inStr1 = _cs5gpRequestFormatter.generateRequest(CS5GpI.ACTION_ACCOUNT_INFO,_requestMap);
 	            	sendRequestToIN(inStr1,CS5GpI.ACTION_ACCOUNT_INFO);
 	            	interfaceStatus="";
 	            	interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");
 	            	String currentbalance=(String)_responseMap.get("accountValue1");
 	            	
 	            	
 	            	///////////////////////////////////////TRRC CHANGE - PhaseII///////////////////////////////////////////////
 	            	if(_requestMap.get("REQ_SERVICE").equalsIgnoreCase(PretupsI.SERVICE_TYPE_TRRC)){
		 	           		 //dedicatedAccountInformation and account value 
		 	           		 String  da100=(String)_requestMap.get("DEDICATED_ACCOUNT_FOR_POSTPAID");
		 	           		 String daAccInfoString=(String)_responseMap.get("dedicatedAccountInformation");
		 	           		 String da100AccountValue =null;
		 	           		 if(!InterfaceUtil.isNullString(daAccInfoString))
		 	           		 {
		 	           			// <member><name>dedicatedAccountID</name><value><i4>100</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>24372</string></value></member><member> 
		 	           			int indexStart=0;
		 	           			int tempIndex=0; 
		 	           			int indexEnd=0;
		 	           			 indexStart= daAccInfoString.indexOf("<member><name>dedicatedAccountID</name><value><i4>"+da100+"</i4></value></member><member><name>dedicatedAccountValue1</name><value>",indexEnd);
		 	           			 tempIndex = daAccInfoString.indexOf("dedicatedAccountValue1",indexStart);
		 	           			 if(tempIndex>0)
		 	           			 {
		 	           				da100AccountValue = daAccInfoString.substring("<string>".length()+daAccInfoString.indexOf("<string>",tempIndex),daAccInfoString.indexOf("</string>",tempIndex)).trim();
		 	           		           
		 	           				 indexEnd = daAccInfoString.indexOf("</member>",indexStart);
		 	           			 }
		 	           			 
		 	           		 }
		 	           		
		 	           		LogFactory.printLog(METHOD_NAME, "Entered pda100AccountValue :"+da100AccountValue, log);	
		 	           		
		 	     			if(Long.valueOf(_responseMap.get("serviceClassCurrent")) >Long.valueOf(_requestMap.get("PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE")))
		 	     			{
		 	     				amountStr=da100AccountValue;
		 	          			 if(InterfaceUtil.isNullString(amountStr))
		 	          				 amountStr="0";
		 	          			 
		 	          			currentbalance=amountStr;
		
		 	     			}
 	            	}		
 	     			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 	            	
 	            	
 	            	
 	            	
 	            	
 	            	
 	            	ResponseCode100Logger.logInfo("::: TID="+_referenceID+"::: MSISDN="+_msisdn+"::: Previous Balance ="+previousbalance+"::: Current Balance ="+currentbalance+" ::: Reference ID ="+_inTXNID+" ::: After Balance Enquiry for response ="+interfaceStatus+"::: URL="+_requestMap.get("URL_PICKED") );
 	            	
 	            	 multFactor=(String)_requestMap.get("MULT_FACTOR");
 	                try
 	    			{
 	                	currentbalance = InterfaceUtil.getSystemAmountFromINAmount(currentbalance,Double.parseDouble(multFactor));
 	    				
 	    			}
 	                catch(Exception e)
 	    			{
 	                	log.errorTrace("sendRequestToIN",e);
 	                	log.error("sendRequestToIN","Exception e:"+e.getMessage());
 	                	EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 not numeric Parsing Exception e:"+e.getMessage());
 	                	throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.ERROR_100_TOP_STAGE);
 	    			}
 	            
 	            	
 	             	if(Double.parseDouble(currentbalance)<=Double.parseDouble(previousbalance))  
 	             	{	
 	             		log.error("sendRequestToIN","After again calling validate Previous and Final Balance are same, No Top Up on IN, Old Balance="+previousbalance+" Final balance="+currentbalance);
 	             		ResponseCode100Logger.logInfo("::: TID="+_referenceID+"::: MSISDN="+_msisdn+"::: Previous Balance ="+previousbalance+"::: Current Balance ="+currentbalance+" ::: Reference ID ="+_inTXNID+" ::: Previous Balance >= Final Balance::: No Credit on AIR for response code="+interfaceStatus+"::: URL="+_requestMap.get("URL_PICKED") );
 	             		_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.FAIL);
 	             		if(!BTSLUtil.NullToString(prevStatus).equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
 	             			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_100_TOP_STAGE);
 	             		}else{
 	             			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_AMBIGUOUS_RETRY_BALANCE_SAME);
 	             		}
 	             	}
 	             	else 
 	             	{
 	             		  log.info("sendRequestToIN","After again calling validate Previous and Final Balance are not same, Top Up successful on IN, Old Balance="+previousbalance+" Final balance="+currentbalance);
 	             		 ResponseCode100Logger.logInfo("::: TID="+_referenceID+"::: MSISDN="+_msisdn+"::: Previous Balance ="+previousbalance+"::: Current Balance ="+currentbalance+" ::: Reference ID ="+_inTXNID+" ::: Final Balance > Previous Balance::: Credit on AIR for response code="+interfaceStatus+"::: URL="+_requestMap.get("URL_PICKED") );
     	             		
 	             		  _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
 	             		//whether to set reponse code as 0 here or not
 	             	}
				}catch (Exception e) {
					if(BTSLUtil.NullToString(prevStatus).equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS) && BTSLUtil.NullToString(e.getMessage()).equalsIgnoreCase(InterfaceErrorCodesI.ERROR_AMBIGUOUS_RETRY_BALANCE_SAME)){
		            	//EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+_stage,  "Exception in Ambiguous handling.  so throwing AMBIGOUS exception");
		            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":Same Balance after balance enquiry for Ambiguous Case",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]");
		            	//_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_AMBIGUOUS_RETRY_BALANCE_SAME);
		            	_requestMap.put("INTERFACE_STATUS","");
		            	
		            	_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_AMBIGUOUS_RETRY_BALANCE_SAME);
		            }else if(BTSLUtil.NullToString(prevStatus).equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
		            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+_stage,  "Exception in Ambiguous handling.  so throwing AMBIGOUS exception");
		            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Read Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]");
		            	//_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
		            	_requestMap.put("INTERFACE_STATUS","");	       
						throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
		           }else{
		            	throw e;
		            }
				}
	               
	            }else{
	            	_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
	            }
        		// set NEW_EXPIRY_DATE into request map


        		_requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap_c.get("supervisionExpiryDate"), "yyyyMMdd"));
        		_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap_c.get("serviceFeeExpiryDate"), "yyyyMMdd"));


        		//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
        		try
        		{
        			String postBalanceStr="";
        			if("100".equalsIgnoreCase((String)_responseMap_c.get("responseCode")))
        				postBalanceStr = (String) _responseMap.get("accountValue1");
        			else
        				postBalanceStr = (String) _responseMap_c.get("accountValue1");
        			postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
        			_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
        			//Added for Promobalance k 

        			String postBalancePromoStr1 = (String) _responseMap_c.get("refillAmount1");

        			if(!InterfaceUtil.isNullString(postBalancePromoStr1))
        				postBalancePromoStr1 = InterfaceUtil.getSystemAmountFromINAmount(postBalancePromoStr1,multFactorDouble);
        			else 
        				postBalancePromoStr1="0";
        			_requestMap.put("INTERFACE_PROMO_POST_BALANCE",postBalancePromoStr1);

        		
        			LogFactory.printLog(METHOD_NAME, "postBalancePromoStr="+postBalancePromoStr1, log);
        		}
        		catch(Exception e)
        		{
        			log.errorTrace("Exception",e);
        			log.error("credit","Exception e:"+e.getMessage());
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
        			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.ERROR_RESPONSE);
        		}
        		_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        	}
            
         } 
        catch (BTSLBaseException be)
        {
        	//p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
    		log.error("credit","BTSLBaseException be:"+be.getMessage());    		   		
    		throw be;
		}
         catch (Exception e)
         {
        	 log.errorTrace("credit",e);
        	log.error("credit", "Exception e:" + e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
             throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
        	 LogFactory.printLog(METHOD_NAME, "Exited p_requestMap:"+p_requestMap, log);
             
         }
    }//end of credit
    
    
    /**
     * This method is used to adjust the following for sender credit back
     */

   public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
   {
   		String METHOD_NAME="creditAdjust";
   		LogFactory.printLog(METHOD_NAME, "Entered p_requestMap:"+p_requestMap, log);
	  
       _requestMap = p_requestMap;//assign map passed from InterfaceModule to _requestMap(instance var) 
       double systemAmtDouble =0;
       String amountStr=null;
       
	
       double multFactorDouble=0;
       try
		{
       	if(_requestMap.get("REQ_FROM_NSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)_requestMap.get("REQ_FROM_NSMS")))
       	{
       		_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
       	}
       	else
       	{
       		_userType=(String)_requestMap.get("USER_TYPE");
       		_interfaceID=(String)_requestMap.get("INTERFACE_ID");// get interface id form request map

       		_inTXNID=getINTransactionID(_requestMap);//Generate the IN transaction id and set in _requestMap
       		_requestMap.put("IN_TXN_ID",_inTXNID);//get TRANSACTION_ID from request map (which has been passed by controller)
       		_requestMap.put("IN_RECON_ID",_inTXNID);

       		_referenceID=(String)_requestMap.get("TRANSACTION_ID");

       		_msisdn=(String)_requestMap.get("MSISDN");//get MSISDN from request map
       		//	add transaction code and transaction type in request map
       		if(!InterfaceUtil.isNullString(_msisdn))
       		{
       			_msisdn =InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);                        
       		}

       		_requestMap.put("FILTER_MSISDN",_msisdn);



       		//Get the Multiplication factor from the FileCache with the help of interface id.
       		String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
       		if(InterfaceUtil.isNullString(multFactor))
       		{
       			log.error(METHOD_NAME,"MULT_FACTOR  is not defined in the INFile");
       			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
       			throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
       		}
       		multFactor = multFactor.trim();
       		//Set the interface parameters into requestMap
       		setInterfaceParameters();
       		
       		/*String  ucipFlag=(String)_requestMap.get("UCIP_IN_DISABLED_FLAG");
       		
       		if("Y".equalsIgnoreCase(ucipFlag))
       		{
       			_reqRespAllowed=true;
       		}*/
       		_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
       		try
       		{
       			double MultFactorDouble1=Double.parseDouble(multFactor);
       			double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
       			systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,MultFactorDouble1);
       			amountStr=String.valueOf(systemAmtDouble);
       			//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
       			String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
       			LogFactory.printLog(METHOD_NAME, "From file cache roundFlag = "+roundFlag, log);
       			
       			//If the ROUND_FLAG is not defined in the INFile 
       			if(InterfaceUtil.isNullString(roundFlag))
       			{
       				roundFlag="Y";
       				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5GpINHandler[creditAdjust]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
       			}
       			//If rounding of amount is allowed, round the amount value and put this value in request map.
       			if("Y".equals(roundFlag.trim()))
       			{
       				amountStr=String.valueOf(Math.round(systemAmtDouble));
       				_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
       			}
       		}
       		catch(Exception e)
       		{
       			log.errorTrace("Exception",e);
       			log.error(METHOD_NAME,"Exception e:"+e.getMessage());
       			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
       			throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
       		}
       		LogFactory.printLog(METHOD_NAME, "transfer_amount:"+amountStr+" multFactor:"+multFactor, log);
       		
       		//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
       		_requestMap.put("transfer_amount",amountStr);


       		String inStr = _cs5gpRequestFormatter.generateRequest(CS5GpI.ACTION_IMMEDIATE_DEBIT,_requestMap);


       		//sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS5MobililI interface
       		sendRequestToIN(inStr,CS5GpI.ACTION_IMMEDIATE_DEBIT);
       		//set TRANSACTION_STATUS as Success in request map
       		_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
       		multFactorDouble=Double.parseDouble(multFactor);
       		try
       		{
       			String postBalanceStr = (String) _responseMap.get("accountValue1");
       			
       			if(InterfaceUtil.isNullString(postBalanceStr))
       			{
       				postBalanceStr="0";
       				try
       				{
       					String requestQuantity=(String)_requestMap.get("INTERFACE_AMOUNT");
       					String previousBalance=(String)_requestMap.get("INTERFACE_PREV_BALANCE");
        				
       					int postBalance =Integer.parseInt(previousBalance)+Integer.parseInt(requestQuantity);
        					postBalanceStr=String.valueOf(postBalance);
       				}
       				catch(Exception ex){
       					log.errorTrace("Exception",ex);
       					log.error("Exception", "Exception :" + ex.getMessage()); 
        					postBalanceStr="0";
       				}
       			}
       			
       				
       			postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
       			_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
       		}
       		catch(Exception e)
       		{
       			log.errorTrace("Exception",e);
       			log.error(METHOD_NAME,"Exception e:"+e.getMessage());
       			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
       			throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_RESPONSE);
       		}				
       		//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
       		_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N"); 
       	} 
		}
       catch (BTSLBaseException be)
       {
       	p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
   		log.error(METHOD_NAME,"BTSLBaseException be:"+be.getMessage());    		   		
   		if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
   			throw be;
   		try
			{    			
   			_requestMap.put("TRANSACTION_TYPE","CR");
   			handleCancelTransaction();
   		}
   		catch(BTSLBaseException bte)
			{
				throw bte;
			}
			catch(Exception e)
			{
				log.errorTrace("Exception",e);
				 log.error(METHOD_NAME,"Exception e:"+e.getMessage());
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[creditAdjust]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
				 throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
        catch (Exception e)
        {
        	log.errorTrace("Exception",e);
            log.error(METHOD_NAME, "Exception e:"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[creditAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:"+e.getMessage());
            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
        	LogFactory.printLog(METHOD_NAME, "Exited p_requestMap:"+p_requestMap, log);
           
        }
   }//end of creditAdjust.
    /**
     * Implements the logic that debit the subscriber account on IN.
     * @param	HashMap  p_requestMap
     * @throws	BTSLBaseException, Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
    {
    	String METHOD_NAME="debitAdjust";
    	LogFactory.printLog(METHOD_NAME, "Entered p_requestMap:"+p_requestMap, log);
        _requestMap = p_requestMap;//assign map passed from InterfaceModule to _requestMap(instance var) 
        double systemAmtDouble =0;
        String amountStr=null;
        
	
        double multFactorDouble=0;
        try
		{
        	if(_requestMap.get("REQ_FROM_NSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)_requestMap.get("REQ_FROM_NSMS")))
        	{
        		_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        	}
        	else
        	{
        		_userType=(String)_requestMap.get("USER_TYPE");
        		_interfaceID=(String)_requestMap.get("INTERFACE_ID");// get interface id form request map

        		_inTXNID=getINTransactionID(_requestMap);//Generate the IN transaction id and set in _requestMap
        		_requestMap.put("IN_TXN_ID",_inTXNID);//get TRANSACTION_ID from request map (which has been passed by controller)
        		_requestMap.put("IN_RECON_ID",_inTXNID);

        		_referenceID=(String)_requestMap.get("TRANSACTION_ID");

        		_msisdn=(String)_requestMap.get("MSISDN");//get MSISDN from request map
        		//	add transaction code and transaction type in request map
        		if(!InterfaceUtil.isNullString(_msisdn))
        		{
        			_msisdn =InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);                        
        		}

        		_requestMap.put("FILTER_MSISDN",_msisdn);



        		//Get the Multiplication factor from the FileCache with the help of interface id.
        		String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
        		if(InterfaceUtil.isNullString(multFactor))
        		{
        			log.error("creditAdjust","MULT_FACTOR  is not defined in the INFile");
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
        			throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
        		}
        		multFactor = multFactor.trim();
        		//Set the interface parameters into requestMap
        		setInterfaceParameters();
        		
        		/*String  ucipFlag=(String)_requestMap.get("UCIP_IN_DISABLED_FLAG");
        		
        		if("Y".equalsIgnoreCase(ucipFlag))
        		{
        			_reqRespAllowed=true;
        		}*/
        		_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
        		try
        		{
        			double MultFactorDouble1=Double.parseDouble(multFactor);
        			double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
        			systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,MultFactorDouble1);
        			amountStr=String.valueOf(systemAmtDouble);
        			//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
        			String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
        			LogFactory.printLog(METHOD_NAME, "From file cache roundFlag = "+roundFlag, log);
        			
        			//If the ROUND_FLAG is not defined in the INFile 
        			if(InterfaceUtil.isNullString(roundFlag))
        			{
        				roundFlag="Y";
        				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5GpINHandler[debitAdjust]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
        			}
        			//If rounding of amount is allowed, round the amount value and put this value in request map.
        			if("Y".equals(roundFlag.trim()))
        			{
        				amountStr=String.valueOf(Math.round(systemAmtDouble));
        				_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
        			}
        		}
        		catch(Exception e)
        		{
        			log.errorTrace("creditAdjust",e);
        			log.error("creditAdjust","Exception e:"+e.getMessage());
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
        			throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        		}
        		
        		LogFactory.printLog(METHOD_NAME, "transfer_amount:"+amountStr+" multFactor:"+multFactor, log);
        		//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
        		_requestMap.put("transfer_amount","-"+amountStr);


        		String inStr = _cs5gpRequestFormatter.generateRequest(CS5GpI.ACTION_IMMEDIATE_DEBIT,_requestMap);


        		//sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS5MobililI interface
        		sendRequestToIN(inStr,CS5GpI.ACTION_IMMEDIATE_DEBIT);
        		//set TRANSACTION_STATUS as Success in request map
        		_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        		multFactorDouble=Double.parseDouble(multFactor);
        		try
        		{
        			String postBalanceStr = (String) _responseMap.get("accountValue1");
        			if(InterfaceUtil.isNullString(postBalanceStr))
        			{
        				try
        				{
        					String requestQuantity=(String)_requestMap.get("INTERFACE_AMOUNT");
        					String previousBalance=(String)_requestMap.get("INTERFACE_PREV_BALANCE");
        				
        					int postBalance =Integer.parseInt(previousBalance)-Integer.parseInt(requestQuantity);
        					postBalanceStr=String.valueOf(postBalance);
        				}
        				catch(Exception ex){
        					log.errorTrace("Exception",ex);
        					log.error("Exception", "Exception :" + ex.getMessage()); 
        					postBalanceStr="0";
        				}
        			}
        				
        			postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
        			_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
        		}
        		catch(Exception e)
        		{
        			log.errorTrace("credit",e);
        			log.error("credit","Exception e:"+e.getMessage());
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
        			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.ERROR_RESPONSE);
        		}				
        		//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
        		_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N"); 
        	} 
		}
        catch (BTSLBaseException be)
        {
        	p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
    		log.error("debitAdjust","BTSLBaseException be:"+be.getMessage());    		   		
    		if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
    			throw be;
    		try
			{    			
    			_requestMap.put("TRANSACTION_TYPE","DR");
    			handleCancelTransaction();
    		}
    		catch(BTSLBaseException bte)
			{
				throw bte;
			}
			catch(Exception e)
			{
				log.errorTrace("debitAdjust",e);
				 log.error("debitAdjust","Exception e:"+e.getMessage());
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[debitAdjust]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
				 throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
         catch (Exception e)
         {
        	 log.errorTrace("debitAdjust",e);
             log.error("debitAdjust", "Exception e:"+e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:"+e.getMessage());
             throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
        	 LogFactory.printLog(METHOD_NAME, "Exited p_requestMap:"+p_requestMap, log);
            
         }
    }//end of debitAdjust.
    
    /**
     * This method is used to send the request to IN and stored the response after parsing.
     * This method also take care about to handle the errornious satuation to send the alarm and set the error code.
     * 1.Invoke the getNodeVO method of NodeScheduler class and pass the Transaction Id.
     * 2.If the VO is Null then mark the request as fail and throw exception(New Error code that defines No connection for any Node is available).
     * 3.If the VO is not NULL then pass the Node detail to cs5URLConnection class and get connection.
     * 4.After the proccessing the request(may be successful or fail) decrement the connection counter and pass the
     * transaction id that is removed from the transNodeList.
     *  
     * @param	String p_inRequestStr
     * @param	int p_action
     * @throws	BTSLBaseException
     */
    
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException
    {
    	String METHOD_NAME="sendRequestToIN";
    	LogFactory.printLog(METHOD_NAME, "Entered p_inRequestStr::"+p_inRequestStr+" p_action::"+p_action, log);
        
        
        String  _iNTraceEnabled=(String)_requestMap.get("UCIP_IN_DISABLED_FLAG");
		
		
        
        String responseStr = "";
        NodeVO cs5NodeVO=null;
        NodeScheduler cs5NodeScheduler=null;
        CS5GpUrlConnection cs5URLConnection = null;
        long startTime=0;
        long endTime=0;
        //int conRetryNumber=0;
        long warnTime=0;
        int readTimeOut=0;
        String inReconID=null;
       // long retrySleepTime=0;
        //String  previousIP=null;
        //String previousURL=null;
        //String exceptionString="";
        
        
      
        ArrayList<String> triedURLList = new ArrayList<String>();
        ArrayList<String> triedNodeNumberList = new ArrayList<String>();
        CS5Status cs5Status = CS5Status.getInstance();
        boolean retryRequired=true;
        String responseCode="";
		
        
        try
        {
        
        	if(p_action==CS5GpI.ACTION_RECHARGE_CREDIT){
        		_responseMap_c = new HashMap<String,String>();
        		_stage=PretupsI.TXN_LOG_TXNSTAGE_INTOP;
        	}
        	else{
        		_responseMap = new HashMap<String,String>();
        		_stage=PretupsI.TXN_LOG_TXNSTAGE_INVAL;
        	}

            inReconID=(String)_requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=_inTXNID;
		//	retrySleepTime = Long.parseLong(FileCache.getValue(_interfaceID,"RETRY_SLEEP_TIME"));
		    //Get the instance of NodeScheduler based on interfaceId.
            cs5NodeScheduler = NodeManager.getScheduler(_interfaceID);
          //check if cs5NodeScheduler is null throw exception.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
            if(cs5NodeScheduler==null){
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_WHILE_GETTING_SCHEDULER_OBJECT);
            }
            if(_iNTraceEnabled!=null && ("Y").equalsIgnoreCase(_iNTraceEnabled)){
            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,"[IN Request XML] : "+p_inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: [REQUEST_MAP] : "+_requestMap);
            }else{
            	//TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,"[IN Request XML] : Suppresed",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: [REQUEST_MAP] : "+_requestMap);
            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,"[IN Request XML] : Suppresed",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN"));
   		 	}

            //Get the retry number from the object that is used to retry the getNode in case connection is failed.
            //conRetryNumber = cs5NodeScheduler.getRetryNum();
            //Host name and userAgent may be set into the VO corresponding to each Node for authentication-CONFIRM, if it is not releted with the request xml.
            String hostName = cs5NodeScheduler.getHeaderHostName();
            String userAgent = cs5NodeScheduler.getUserAgent();
            
           // ArrayList iPUsedlist=new ArrayList();
                        
					            for(int loop=1;loop<=cs5NodeScheduler.getNodeTable().size();loop++)
					            {
						          	
							            long startTimeNode = System.currentTimeMillis();
							            if(log.isDebugEnabled())log.debug("sendRequestToIN","Start time to find the scheduled Node startTimeNode::"+startTimeNode+"miliseconds");
							            //If the connection for corresponding node is failed, retry to get the node with configured number of times.
							            //If connection eshtablished then break the loop.
							              try
								            {
									            cs5NodeVO = cs5NodeScheduler.getNodeVO(inReconID);
									           
									            //Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
									            if(cs5NodeVO==null){
									                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_DETAIL_NOT_FOUND );
									            }
									            
									            LogFactory.printLog(METHOD_NAME,"URL PICKED IS ......."+ cs5NodeVO.getUrl(), log);
									            if(triedNodeNumberList!= null && !triedNodeNumberList.isEmpty() && triedNodeNumberList.contains(""+cs5NodeVO.getNodeNumber())){
									            	cs5NodeVO.decrementConNumber(inReconID);
									        		for(int i=1;i<=cs5NodeScheduler.getNodeTable().size();i++){
														if(!triedNodeNumberList.contains(""+((NodeVO)(cs5NodeScheduler.getNodeTable().get(i))).getNodeNumber())){
															cs5NodeVO=(NodeVO)(cs5NodeScheduler.getNodeTable().get(i));
															cs5NodeVO.incrementConNumber(inReconID);
														}
													}
													//TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":RETRY COUNT"+loop,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Connect AIR Number=["+cs5NodeVO.getNodeNumber()+"] Connect AIR URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
					
												}
									            TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":RETRY COUNT"+loop,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Connect AIR Number=["+cs5NodeVO.getNodeNumber()+"] Connect AIR URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
									            
					
											
									            
									            warnTime=cs5NodeVO.getWarnTime();
									            //Get the read time out based on the action.
									            if(CS5GpI.ACTION_ACCOUNT_INFO==p_action || CS5GpI.ACTION_ACCOUNT_DETAILS==p_action)
									                readTimeOut=cs5NodeVO.getValReadTimeOut();
									            else
									                readTimeOut=cs5NodeVO.getTopReadTimeOut();
									            
									            
									            //Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
									            cs5URLConnection = new CS5GpUrlConnection(cs5NodeVO.getUrl(),cs5NodeVO.getUsername(),cs5NodeVO.getPassword(),cs5NodeVO.getConnectionTimeOut(),readTimeOut,cs5NodeVO.getKeepAlive(),p_inRequestStr.length(),hostName,userAgent);
									            
												
									          
									            
									            try
									            {
									            	//changed for -ve TopUp Time
									            	startTime=System.currentTimeMillis();
													if(_requestMap.get("IN_START_TIME") ==null){ 
									                	_requestMap.put("IN_START_TIME",String.valueOf(System.currentTimeMillis()));
									                }
									                
													PrintWriter out = cs5URLConnection.getPrintWriter();
										            out.flush();
										            //changed for -ve TopUp Time
													
									                out.println(p_inRequestStr);
									                out.flush();
									            }
										        catch(Exception e)
										        {
										        	retryRequired=false;
									                e.printStackTrace();
									                log.error("sendRequestToIN","Exception e::"+e);
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),"While sending request to cs5Gp IN INTERFACE_ID=["+_interfaceID +"]and Node URL=["+cs5NodeVO.getUrl()+"] Exception::"+e.getMessage());
									                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":IN Request Writing Failure",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"] Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
									                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);	       
													throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
										        }
									            finally
										        {
										        	//changed for -ve TopUp Time
									            	endTime=System.currentTimeMillis();
													_requestMap.put("IN_END_TIME",String.valueOf(System.currentTimeMillis()));
													
										        	log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Connected to IN at:"+endTime+" defined connect time out is:"+cs5NodeVO.getConnectionTimeOut());
										        }//end of finally
										        
									            
									            
									            
										        StringBuffer buffer = new StringBuffer();
									            String response = "";
										        try
										        {
										            //Get the response from the IN 
										            cs5URLConnection.setBufferedReader();
									                BufferedReader in = cs5URLConnection.getBufferedReader();
									                //Reading the response from buffered reader.
									                while ((response = in.readLine()) != null)
									                {
									                    buffer.append(response);
									                }
													endTime=System.currentTimeMillis();
													if(warnTime<=(endTime-startTime))
													{
													    log.info("sendRequestToIN", "WARN time reaches, startTime::"+startTime+" endTime::"+endTime+" From file cache warnTime::"+warnTime+ " time taken (endTime-startTime)::"+(endTime-startTime));
													    EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.MAJOR,"CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"CS5GpIN is taking more time than the warning threshold. Time: "+(endTime-startTime)+"INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] and Node URL=["+cs5NodeVO.getUrl()+"]");
													}
										        }
										        catch(SocketTimeoutException ste)
									            {
										        	log.errorTrace("Exception",ste);
										        	log.error("Exception", "Exception :" + ste.getMessage()); 
										        	retryRequired=false;
									                //log.error("sendRequestToIN","Read Timeout Exception ::"+ste.getMessage());
									                
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"AIR Read Time Out Exception INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":AIR Read Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
							
									                
										            if((p_action==CS5GpI.ACTION_ACCOUNT_INFO || p_action==CS5GpI.ACTION_ACCOUNT_DETAILS) && "V".equals(_requestMap.get("INTERFACE_ACTION")))
										            {
										            	//log.error("sendRequestToIN","Read time out- Fail exception");
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read timeout from IN.  so throwing Fail in validation exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
														_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
										            }else{
										            	//log.error("sendRequestToIN","Read time out occured.  so throwing AMBIGOUS exception");
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read timeout from IN.  so throwing AMBIGOUS exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
										            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Read Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										            	_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
										            }
										        }//end of catch-Exception
										        catch(Exception e)
										        {
										        	log.errorTrace("Exception",e);
										        	log.error("Exception", "Exception :" + e.getMessage()); 
										        	retryRequired=false;
										        	//log.error("sendRequestToIN","Read Timeout Exception ::"+ste.getMessage());
									                
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"AIR Read Exception INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":AIR Read Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
							
									                
										            if((p_action==CS5GpI.ACTION_ACCOUNT_INFO || p_action==CS5GpI.ACTION_ACCOUNT_DETAILS) && "V".equals(_requestMap.get("INTERFACE_ACTION")))
										            {
										            	//log.error("sendRequestToIN","Read time out- Fail exception");
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read Exception from IN.  so throwing Fail in validation exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
														_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
										            }else{
										          
										            	//log.error("sendRequestToIN","Read time out occured.  so throwing AMBIGOUS exception");
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read Exception from IN.  so throwing AMBIGOUS exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
										            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Read Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										            	_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
										            }
										          
										        }//end of catch-Exception
										        finally
										        {
										        	retryRequired=false;
										        	//changed for -ve TopUp Time
										        	endTime=System.currentTimeMillis();
													_requestMap.put("IN_END_TIME",String.valueOf(System.currentTimeMillis()));
									                
									                
										        	//changed for -ve TopUp Time
													//if(endTime==0) endTime=System.currentTimeMillis();
													//_requestMap.put("IN_END_TIME",String.valueOf(endTime));
													log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime+" defined read time out is:"+readTimeOut);
										        }//end of finally
										        
										        
										        responseStr = buffer.toString();
										           
												   if("Y".equalsIgnoreCase(_iNTraceEnabled))
													   TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":IN Response Received",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] [Previously attempted URL =:"+triedURLList+"] [IN Response=:"+responseStr+"]");
												   
									            		
												   LogFactory.printLog(METHOD_NAME, "responseStr::" + responseStr, log);
										          
										            //TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
										            String httpStatus = cs5URLConnection.getResponseCode();
										            _requestMap.put("PROTOCOL_STATUS", httpStatus);
										            if(!CS5GpI.HTTP_STATUS_200.equalsIgnoreCase(httpStatus))
										                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
										            
										        
										        
										            if (InterfaceUtil.isNullString(responseStr))
										            {
										                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Blank response from CS5GpIN  NODE URL=["+cs5NodeVO.getUrl()+"]");
										                if((p_action==CS5GpI.ACTION_ACCOUNT_INFO || p_action==CS5GpI.ACTION_ACCOUNT_DETAILS) && "V".equals(_requestMap.get("INTERFACE_ACTION")))
											            {
											            	//log.error("sendRequestToIN","Read time out- Fail exception");
											            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":AIR Blank Response Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										                	_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
															throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
											            }else{
											          
											            	//log.error("sendRequestToIN","Read time out occured.  so throwing AMBIGOUS exception");
											            	TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Blank Response Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
											            	_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
															throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
											            }
											            
											    		
										            }
										            cs5Status.reintializeCouter(cs5NodeVO.getUrl());
										    			
										            //Parse the response string and get the response Map.
										            if(p_action==CS5GpI.ACTION_RECHARGE_CREDIT){
										            	_responseMap_c=_cs5gpRequestFormatter.parseResponse(p_action,responseStr,_responseMap_c);
										            	if(!"Y".equalsIgnoreCase(_iNTraceEnabled))
															   TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":IN Response Received",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] [Previously attempted URL =:"+triedURLList+"] [IN Response Map =:"+_responseMap_c+"]");
													}
										            else{
										            	_responseMap=_cs5gpRequestFormatter.parseResponse(p_action,responseStr,_responseMap);
										            	if(_responseMap.get("dedicatedAccountInformation")!= null){
										    	        	int indexStart=0;
										    	        	int indexEnd=0;
										    	        	String daXML=(String)_responseMap.get("dedicatedAccountInformation");
										    	        	indexStart= daXML.indexOf("dedicatedAccountInformation",indexEnd);
										    		        	
										    		        int tempIndex = daXML.indexOf("dedicatedAccountID</name><value><i4>"+_requestMap.get("DEDICATED_ACCOUNT_FOR_POSTPAID"),indexStart);
										    		        
										    		        if(tempIndex>0)
										    		        {
										    		            String daXMLString = daXML.substring("dedicatedAccountValue1</name><value><string>".length()+daXML.indexOf("dedicatedAccountValue1</name><value><string>",tempIndex),daXML.indexOf("</string>",daXML.indexOf("dedicatedAccountValue1</name><value><string>"))).trim();
										    		            _responseMap.put("DA100",daXMLString.trim());
										    		            
										    		        }
										    	        }
										            	String dedicatedAccountXml="";
										            	if(_responseMap.containsKey("dedicatedAccountInformation")){
										            		dedicatedAccountXml=_responseMap.remove("dedicatedAccountInformation");
										            	}
										            	
										            	if(!"Y".equalsIgnoreCase(_iNTraceEnabled))
															   TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":IN Response Received",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] [Previously attempted URL =:"+triedURLList+"] [IN Response Map =:"+_responseMap+"]");
										            	if(dedicatedAccountXml.length()>0){
										            		_responseMap.put("dedicatedAccountInformation", dedicatedAccountXml);
										            	}
													}
										            	
										            
										            
										            String faultCode="";
										            if(p_action==CS5GpI.ACTION_RECHARGE_CREDIT)
										            	faultCode = (String)_responseMap_c.get("faultCode");
										            else
										             faultCode = (String)_responseMap.get("faultCode");
										            
										            if(!InterfaceUtil.isNullString(faultCode))
										            {
										                //Log the value of executionStatus for corresponding msisdn,recieved from the response.
										                log.info("sendRequestToIN", "faultCode::"+faultCode +"_inTXNID::"+_inTXNID+" _msisdn::"+_msisdn);
										                _requestMap.put("INTERFACE_STATUS", faultCode);//Put the interface_status in requestMap
										                if(p_action==CS5GpI.ACTION_RECHARGE_CREDIT)
										                	 log.error("sendRequestToIN","faultCode="+_responseMap_c.get("faultCode")+"faultString = "+_responseMap_c.get("faultString"));	
										                else
										                	log.error("sendRequestToIN","faultCode="+_responseMap.get("faultCode")+"faultString = "+_responseMap.get("faultString"));
										                
										                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,(String)_responseMap.get("faultString"));
										               	throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
										            }
										            
										            
										            
										            responseCode="";
										            if(p_action==CS5GpI.ACTION_RECHARGE_CREDIT)
										            	responseCode = (String)_responseMap_c.get("responseCode");
										            else
										            	responseCode = (String)_responseMap.get("responseCode");
										            _requestMap.put("INTERFACE_STATUS",responseCode);
										        
										        
										            if (p_action==CS5GpI.ACTION_ACCOUNT_INFO || p_action==CS5GpI.ACTION_ACCOUNT_DETAILS)
										            {
										            	String valRetryForResCode[];
										        		  boolean valRetryForResCodeFlag = false;
										        		  try {
										        			  valRetryForResCode=_requestMap.get("CS5_VAL_RESPONSE_CODE_FOR_RETRIES").split(",");
										            		  for(int i=0;i<valRetryForResCode.length;i++){
										            			  if(!BTSLUtil.isNullString(valRetryForResCode[i]) && valRetryForResCode[i].equalsIgnoreCase(responseCode)){
										            				  valRetryForResCodeFlag = true;
										            				  break;
										            			  }
										            		  }
														} catch (Exception e) {
															log.errorTrace("Exception",e);
															log.error("Exception", "Exception :" + e.getMessage()); 
															valRetryForResCodeFlag = false;
														}
														if(valRetryForResCodeFlag){

															
															if(triedNodeNumberList!= null && !triedNodeNumberList.isEmpty() &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
																retryRequired=false;
															}else{
																retryRequired=true;
															}
															/*if(triedNodeNumberList!= null && triedNodeNumberList.size()> 0 ){
												            	  	triedNodeNumberList.clear();												            	
															}
															triedNodeNumberList.add(""+cs5NodeVO.getNodeNumber());*/
															ResponseCode100Logger.logInfo("STAGE ::: "+_stage+", MSISDN="+_msisdn+"::: Reference ID ="+_inTXNID+" ::: ="+responseCode+":::URL ::: = "+cs5NodeVO.getUrl()+", retryRequired  = "+retryRequired);
											            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
											                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
											                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_100_VAL_STAGE);
														}
										            }
									            //////////////////////////////
									            
									            
									            //break the loop on getting the successfull connection for the node;
									            break;
									        }
								            catch(BTSLBaseException be)
								            {
								            	triedURLList.add(cs5NodeVO.getUrl());
								                triedNodeNumberList.add(""+cs5NodeVO.getNodeNumber());
								                cs5NodeVO.decrementConNumber(_inTXNID);
								                
								            	//log.error("sendRequestToIN","Exception while creating connection e::"+ste.getMessage());
								                if(retryRequired){
								                	EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"BTSLBaseException for CS5GpIN with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":BTSLBaseException ",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
						
									                
									                if (cs5Status.isFailCountReached(cs5NodeVO.getUrl(),_requestMap)) {
														cs5NodeVO.setBlocked(true);
														cs5NodeVO.setBlokedAt(System.currentTimeMillis());
										                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"AIR Barred with INTERFACE_ID=["+_interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl());
										                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":HTTP Connect Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: AIR Barred with INTERFACE_ID=["+_interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl()+"] AIR Blocked Duration ="+cs5NodeVO.getExpiryDuration() +"]");
						
									                }
									                
									                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
									                if(triedNodeNumberList!= null && !triedNodeNumberList.isEmpty() &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
									            		EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"All AIR IP Tried for CS5GpIN with INTERFACE_ID=["+_interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":ALL AIR IP Tried",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList+", BTSLBaseException = "+be.getMessage());
										                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										                throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
									            	}
									                continue;
								                }else{
								                	throw be;
								                }
								            }//end of catch-BTSLBaseException
								            catch(SocketTimeoutException ste)
								            {
								            	triedURLList.add(cs5NodeVO.getUrl());
								                triedNodeNumberList.add(""+cs5NodeVO.getNodeNumber());
								                cs5NodeVO.decrementConNumber(_inTXNID);
								                
								            	 if(retryRequired){
								            		//log.error("sendRequestToIN","Exception while creating connection e::"+ste.getMessage());
										                
										                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"Connect Time Out Exception for CS5GpIN with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
										                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":HTTP Connect Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
							
										                
										                if (cs5Status.isFailCountReached(cs5NodeVO.getUrl(),_requestMap)) {
															cs5NodeVO.setBlocked(true);
															cs5NodeVO.setBlokedAt(System.currentTimeMillis());
											                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"AIR Barred with INTERFACE_ID=["+_interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl());
											                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":HTTP Connect Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: AIR Barred with INTERFACE_ID=["+_interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl()+"] AIR Blocked Duration ="+cs5NodeVO.getExpiryDuration() +"]");
							
										                }
										                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
										                if(triedNodeNumberList!= null && !triedNodeNumberList.isEmpty() &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
										            		EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"All AIR IP Tried for CS5GpIN with INTERFACE_ID=["+_interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
											                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":ALL AIR IP Tried",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList+", SocketTimeoutException = "+ste.getMessage());
											                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
											                throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										            	}
										                continue;
								            	 }else{
									                	throw ste;
									                }
								            }//end of catch-Exception
								            catch(Exception e)
								            {
								            	triedURLList.add(cs5NodeVO.getUrl());
								                triedNodeNumberList.add(""+cs5NodeVO.getNodeNumber());
								                cs5NodeVO.decrementConNumber(_inTXNID);
								                
								            	if(retryRequired){
								            		//log.error("sendRequestToIN","Exception while creating connection e::"+e.getMessage());
									                
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"Exception for CS5GpIN with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
						
									                if (cs5Status.isFailCountReached(cs5NodeVO.getUrl(),_requestMap)) {
														cs5NodeVO.setBlocked(true);
														cs5NodeVO.setBlokedAt(System.currentTimeMillis());
										                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"AIR Barred with INTERFACE_ID=["+_interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl());
										                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: AIR Barred with INTERFACE_ID=["+_interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =["+cs5NodeVO.getUrl() +"] AIR Blocked Duration ="+cs5NodeVO.getExpiryDuration() +"]");
						
									                }
									                 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
									                if(triedNodeNumberList!= null && !triedNodeNumberList.isEmpty() &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
									            		EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"All AIR IP Tried for CS5GpIN with INTERFACE_ID=["+_interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										                TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":ALL AIR IP Tried",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+_requestMap.get("SENDER_MSISDN")+" ::: INTERFACE_ID=["+_interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList+", Exception = "+e.getMessage());
										                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										                throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
									            	}
													continue;
								            	}else{
								                	throw e;
								                }
								            }finally{
								            	
								            	if(cs5NodeVO.getUrl() != null){
								            	
									            	_requestMap.put("URL_PICKED", cs5NodeVO.getUrl());
										            if(p_action==CS5GpI.ACTION_RECHARGE_CREDIT) {
										            	_requestMap.put("IP_CREDIT",cs5NodeVO.getUrl().substring(7, cs5NodeVO.getUrl().lastIndexOf(":")));
										            } else {
										            	_requestMap.put("IP",cs5NodeVO.getUrl().substring(7, cs5NodeVO.getUrl().lastIndexOf(":")));
										            }
								            	}
								            }
								            
								            
								            
								            
								    
					            	} //end of for loop
						            
						          	
					        
				          	
						          	try
							        {
							            
		            	
		            
		            
		            
		            if(!Arrays.asList(successList).contains(responseCode))
		            {
		            	
			            if(CS5GpI.SUBSCRIBER_NOT_FOUND.equals(responseCode))//102
			            {
			                log.error("sendRequestToIN","Subscriber not found with MSISDN::"+_msisdn);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Subscriber is not found at IN");
			                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
			            }//end of checking the subscriber existance.
			            else if(CS5GpI.OLD_TRANSACTION_ID.equals(responseCode))//162
			            {
			            	
								if(log.isDebugEnabled())log.debug("sendRequestToIN","Transaction ID mismatch");
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String)_requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Transaction ID mismatch");
								throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// recharge request with old transaction id								
									            	
			            }
			            else if(CS5GpI.OTHER_IN_EXCEPTION.equals(responseCode))//999
			            {
			            		if(log.isDebugEnabled())log.debug("sendRequestToIN","Other IN Exception 999");
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS5GpINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String)_requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Other IN Exception 999");
								throw new BTSLBaseException(InterfaceErrorCodesI.OTHER_IN_EXCEPTION);// recharge request with old transaction id								
									            	
			            }
			           
			            else if (CS5GpI.ACCOUNT_BARRED_FROM_REFILL.equals(responseCode))//103
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_BARRED_FROM_REFILL);
			            }
			            else if (CS5GpI.ACCOUNT_TEMPORARY_BLOCKED.equals(responseCode))//104
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_TEMPORARY_BLOCKED);
			            }
			            else if (CS5GpI.INVALID_PAYMENT_PROFILE.equals(responseCode))//120
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_PAYMENT_PROFILE);
			            }
			            else if (CS5GpI.MAX_CREDIT_LIMIT.equals(responseCode))//123
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_MAX_CREDIT_LIMIT);
			            }
			            else if (CS5GpI.SYSTEM_UNAVAILABLE.equals(responseCode))//125
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_SYSTEM_UNAVAILABLE);
			            }
			            else if (CS5GpI.DEDICATED_ACCOUNT_NOT_ACTIVE.equals(responseCode))//139
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_NOT_ACTIVE);
			            }
			            else if (CS5GpI.DATE_ADJUSTMENT_ISSUE.equals(responseCode))//136
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DATE_ADJUSTMENT_ISSUE);
			            }
			           
			            else if (CS5GpI.ACC_MAX_CREDIT_LIMIT.equals(responseCode))//153
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACC_MAX_CREDIT_LIMIT);
			            }
			            else if (CS5GpI.VOUCHER_STATUS_PENDING.equals(responseCode))//113
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VOUCHER_STATUS_PENDING);
			            }
			            else if (CS5GpI.VOUCHER_GROUP_SERVICE_CLASS.equals(responseCode))//115
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VOUCHER_GROUP_SERVICE_CLASS);
			            }
			            else if (CS5GpI.BELOW_MIN_BAL.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BELOW_MIN_BAL);
			            }
			            else  if (CS5GpI.ACCOUNT_NOT_ACTIVE.equals(responseCode))//126
			            {
			            	
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			               if(p_action	== CS5GpI.ACTION_RECHARGE_CREDIT)
			            	   throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			            }
			            else if("true".equalsIgnoreCase((String)_requestMap.get("RESPONSE_CODE_100")) && ((String)_requestMap.get("RESPONSE_CODE_100_HANDLING")).contains(responseCode)&& p_action==CS5GpI.ACTION_RECHARGE_CREDIT)
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                String previousbalance = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
			                ResponseCode100Logger.logInfo("STAGE ::: "+_stage+" ::: MSISDN="+_msisdn+"::: Previous Balance ="+previousbalance+" ::: Reference ID ="+_inTXNID+" :::  Before sending balance Enquiry after getting response code ="+responseCode+"::: URL ::: "+cs5NodeVO.getUrl() );
			            }
			            
			            else if (CS5GpI.DEDICATED_ACCOUNT_NOT_ALLOWED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.DEDICATED_ACCOUNT_NOT_ALLOWED);
			            }
			            else if (CS5GpI.DEDICATED_ACCOUNT_NEGATIVE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.DEDICATED_ACCOUNT_NEGATIVE);
			            }
			            else if (CS5GpI.VOUCHER_STATUS_USED_BY_SAME.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.VOUCHER_STATUS_USED_BY_SAME);
			            }
			            else if (CS5GpI.VOUCHER_STATUS_USED_BY_DIFFERENT.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.VOUCHER_STATUS_USED_BY_DIFFERENT);
			            }
			            else if (CS5GpI.VOUCHER_STATUS_UNAVAILABLE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.VOUCHER_STATUS_UNAVAILABLE);
			            }
			            else if (CS5GpI.VOUCHER_STATUS_EXPIRED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.VOUCHER_STATUS_EXPIRED);
			            }
			            else if (CS5GpI.VOUCHER_STATUS_STOLEN_OR_MISSING.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.VOUCHER_STATUS_STOLEN_OR_MISSING);
			            }
			            else if (CS5GpI.VOUCHER_STATUS_DAMAGED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.VOUCHER_STATUS_DAMAGED);
			            }
			            else if (CS5GpI.VOUCHER_TYPE_NOT_ACCEPTED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.VOUCHER_TYPE_NOT_ACCEPTED);
			            }
			            else if (CS5GpI.SERVICE_CLASS_CHANGE_NOT_ALLOWED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.SERVICE_CLASS_CHANGE_NOT_ALLOWED);
			            }
			            else if (CS5GpI.INVALID_VOUCHER_ACTIVATION_CODE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_VOUCHER_ACTIVATION_CODE);
			            }
			            else if (CS5GpI.SUPERVISION_PERIOD_TOO_LONG.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.SUPERVISION_PERIOD_TOO_LONG);
			            }
			            else if (CS5GpI.SERVICE_FEE_PERIOD_TOO_LONG.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.SERVICE_FEE_PERIOD_TOO_LONG);
			            }
			            else if (CS5GpI.ACCUMULATOR_NOT_AVAILABLE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ACCUMULATOR_NOT_AVAILABLE);
			            }
			            else if (CS5GpI.GET_BALANCE_AND_DATE_NOT_ALLOWED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.GET_BALANCE_AND_DATE_NOT_ALLOWED);
			            }
			            else if (CS5GpI.OPERATION_NOT_ALLOWED_FROM_CURRENT_LOCATION.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.OPERATION_NOT_ALLOWED_FROM_CURRENT_LOCATION);
			            }
			            else if (CS5GpI.FAILED_TO_GET_LOCATION_INFORMATION.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.FAILED_TO_GET_LOCATION_INFORMATION);
			            }
			            else if (CS5GpI.INVALID_DEDICATED_ACCOUNT_PERIOD.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_DEDICATED_ACCOUNT_PERIOD);
			            }
			            else if (CS5GpI.INVALID_DEDICATED_ACCOUNT_START_DATE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_DEDICATED_ACCOUNT_START_DATE);
			            }
			            else if (CS5GpI.OFFER_NOT_FOUND.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.OFFER_NOT_FOUND);
			            }
			            else if (CS5GpI.INVALID_UNIT_TYPE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_UNIT_TYPE);
			            }
			            else if (CS5GpI.REFILL_DENIED_FIRST_IVR_CALL_NOT_MADE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.REFILL_DENIED_FIRST_IVR_CALL_NOT_MADE);
			            }
			            else if (CS5GpI.REFILL_DENIED_ACCOUNT_NOT_ACTIVE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.REFILL_DENIED_ACCOUNT_NOT_ACTIVE);
			            }
			            else if (CS5GpI.REFILL_DENIED_SERVICE_FEE_PERIOD_EXPIRED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.REFILL_DENIED_SERVICE_FEE_PERIOD_EXPIRED);
			            }
			            else if (CS5GpI.REFILL_DENIED_SUPERVISION_PERIOD_EXPIRED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.REFILL_DENIED_SUPERVISION_PERIOD_EXPIRED);
			            }
			            else if (CS5GpI.PERIODIC_ACCOUNT_MANAGEMENT_EVALUATION_FAILED.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.PERIODIC_ACCOUNT_MANAGEMENT_EVALUATION_FAILED);
			            }
			            else if (CS5GpI.OFFER_START_DATE_NOT_CHANGED_AS_OFFER_ALREADY_ACTIVE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.OFFER_START_DATE_NOT_CHANGED_AS_OFFER_ALREADY_ACTIVE);
			            }
			            else if (CS5GpI.SHARED_ACCOUNT_OFFER_NOT_ALLOWED_SUBORDINATE_SUBSCRIBER.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.SHARED_ACCOUNT_OFFER_NOT_ALLOWED_SUBORDINATE_SUBSCRIBER);
			            }
			            else if (CS5GpI.ATTRIBUTE_NAME_NOT_EXIST.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ATTRIBUTE_NAME_NOT_EXIST);
			            }
			            else if (CS5GpI.CAPABILITY_NOT_AVAILABLE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.CAPABILITY_NOT_AVAILABLE);
			            }
			            else if (CS5GpI.ATTRIBUTE_UPDATE_NOT_ALLOWED_FOR_ATTRIBUTE.equals(responseCode))//124
			            {
			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ATTRIBUTE_UPDATE_NOT_ALLOWED_FOR_ATTRIBUTE);
			            }else{

			            	log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);			            	
			            }
			            
		            }else{
		            	
		            	if ((!BTSLUtil.NullToString(_requestMap.get("GATEWAY_TYPE")).equalsIgnoreCase(PretupsI.GATEWAY_TYPE_EXTGW))&& (p_action==CS5GpI.ACTION_ACCOUNT_INFO ||p_action==CS5GpI.ACTION_ACCOUNT_DETAILS)){
		            		if(_requestMap.get("CS5_SERVIC_TYPE_FOR_PBP_SRV_CLASS_CHECK").contains(_requestMap.get("REQ_SERVICE"))){
			            		if(Long.valueOf(_responseMap.get("serviceClassCurrent")) >= Long.valueOf(_requestMap.get("PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE"))){
			               		 _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
			            			TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":service class threshold reached  : ",PretupsI.TXN_LOG_STATUS_FAIL," Service Class : "+_responseMap.get("serviceClassCurrent"));
					                log.error("sendRequestToIN","service class threshold reached "+_responseMap.get("serviceClassCurrent"));
					                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"service class threshold reached "+_responseMap.get("serviceClassCurrent"));
					                _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.FAIL);
					                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_PREPAID_SERVICE_CLASS);
			            		}
		            		}
		            		
		            	}
		            	
		            	if ( (p_action==CS5GpI.ACTION_ACCOUNT_INFO ||p_action==CS5GpI.ACTION_ACCOUNT_DETAILS) && (_requestMap.get("REQ_SERVICE").equalsIgnoreCase(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PROMO))){
		            			if(Long.valueOf(_responseMap.get("serviceClassCurrent")) >= Long.valueOf(_requestMap.get("PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE"))){
		                   		 _requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
			            			TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),PretupsI.TXN_LOG_REQTYPE_REQ,_stage,":service class threshold reached  : ",PretupsI.TXN_LOG_STATUS_FAIL," Service Class : "+_responseMap.get("serviceClassCurrent"));
					                log.error("sendRequestToIN","service class threshold reached "+_responseMap.get("serviceClassCurrent"));
					                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5GpINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"service class threshold reached "+_responseMap.get("serviceClassCurrent"));
					                _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.FAIL);
					                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_PREPAID_SERVICE_CLASS);
			            		}
		            		}
		            		
		            	}
		            	
		            
		        
		        }
		        catch(BTSLBaseException be)
		        {
		        	if(!((p_action==CS5GpI.ACTION_RECHARGE_CREDIT) && (_requestMap.get("INTERFACE_STATUS").equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS))))
		               	throw be;
		        }//end of catch-BTSLBaseException
		        catch(Exception e)
		        {
		        	log.errorTrace("sendRequestToIN",e);
		        	log.error("sendRequestToIN","Exception e::"+e.getMessage());
		            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		        }//end of catch-Exception
            
		      
        }
        catch(BTSLBaseException be)
        {
        	
            log.error("sendRequestToIN","BTSLBaseException be::"+be.getMessage());
            if(!((p_action==CS5GpI.ACTION_RECHARGE_CREDIT) && (_requestMap.get("INTERFACE_STATUS").equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS))))
            	throw be;
        }//end of catch-BTSLBaseException
        catch(Exception e)
        {
        	log.errorTrace("Exception",e);
            log.error("sendRequestToIN","Exception e::"+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }//end of catch-Exception
        finally
        {
        	_requestMap.put("IN_END_TIME",String.valueOf(System.currentTimeMillis()));
			
            try
            {
            	//Closing the HttpUrl connection
                if (cs5URLConnection != null) cs5URLConnection.close();
                if(cs5NodeVO!=null)
                {
                    log.info("sendRequestToIN","Connection of Node ["+cs5NodeVO.getNodeNumber()+"] for INTERFACE_ID="+_interfaceID+" is closed");
                    //Decrement the connection number for the current Node.
                    cs5NodeVO.decrementConNumber(inReconID);
                    log.info("sendRequestToIN","After closing the connection for Node ["+cs5NodeVO.getNodeNumber()+"] USED connections are ::["+cs5NodeVO.getConNumber()+"]");
                }
            }
            catch (Exception e)
            {
            	log.errorTrace("Exception",e);
                log.error("sendRequestToIN", "While closing CS5GpIN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action, "Not able to close connection:" + e.getMessage()+"INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
            }
           
            LogFactory.printLog(METHOD_NAME, "Exiting  _interfaceID::"+_interfaceID+" Stage::"+p_action + " responseStr::" + responseStr, log);
            
        }//end of finally
    
    }//end of sendRequestToIN
    
    /**
    	 * This method would be used to adjust the validity of the subscriber account at the IN.
    	 * @param	HashMap p_requestMap
    	 * @throws	BTSLBaseException, Exception
    	 */   
    	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
    	{}//end of validityAdjust

    	/**
         * This method is used to set the interface parameters into requestMap, these parameters are as bellow
         * 1.Origin node type.
         * 2.Origin host type.
         * @throws Exception
         */
    	private void setInterfaceParameters() throws BTSLBaseException
	    {
	       
    		final String METHOD_NAME="setInterfaceParameters";
    		LogFactory.printLog(METHOD_NAME, "Entered ", log);
    		
	        try
	        {        	
		        String cancelTxnAllowed = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"CANCEL_TXN_ALLOWED");
		    	if(InterfaceUtil.isNullString(cancelTxnAllowed))
		    	{
		    	    log.error("setInterfaceParameters","Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
		    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());
		    	
		    	String systemStatusMappingCr = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"SYSTEM_STATUS_MAPPING_CREDIT");
		    	if(InterfaceUtil.isNullString(systemStatusMappingCr))
		    	{
		    	    log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
		    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());
		    	
		    	String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
		    	if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
		    	{
		    	    log.error(METHOD_NAME,"Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
		    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());
		    	
		    	String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
		    	if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
		    	{
		    	    log.error(METHOD_NAME,"Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
		    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());
		    	
		    	String systemStatusMappingCrBck = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
		    	if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
		    	{
		    	    log.error(METHOD_NAME,"Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
		    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());
		    	
		    	String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"CANCEL_COMMAND_STATUS_MAPPING");
		    	if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
		    	{
		    	    log.error(METHOD_NAME,"Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
		    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());
		    	
		    	
		    	String cancelNA = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"CANCEL_NA");
		    	if(InterfaceUtil.isNullString(cancelNA))
		    	{
		    	    log.error(METHOD_NAME,"Value of CANCEL_NA is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
		    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("CANCEL_NA",cancelNA.trim());
		    	
		    	String warnTimeStr=(String)FileCache.getValue(_interfaceID, _requestMap.get("NETWORK_CODE")+"_"+"WARN_TIMEOUT");
				if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());
				
				
				//required
				String nodeType=FileCache.getValue(_interfaceID, _requestMap.get("NETWORK_CODE")+"_"+"NODE_TYPE");
				if(InterfaceUtil.isNullString(nodeType))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "NODE_TYPE is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("NODE_TYPE",nodeType.trim());
				
				String hostName=FileCache.getValue(_interfaceID,"HOST_NAME");
				if(InterfaceUtil.isNullString(hostName))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "HOST_NAME is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("HOST_NAME",hostName.trim());
				
				
				
				String currency=FileCache.getValue(_interfaceID, _requestMap.get("NETWORK_CODE")+"_"+"CURRENCY");
				if(InterfaceUtil.isNullString(currency))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CURRENCY is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("CURRENCY",currency.trim());
				
				String RefillAccountAfterFlag="";
				try {
					//dedicateAccountValueFlag = Constants.getProperty("CS4_DEDICATED_ACCOUNT_VALUE_FLAG").toString();
					RefillAccountAfterFlag=FileCache.getValue(_interfaceID, _requestMap.get("NETWORK_CODE")+"_"+"REFILL_ACNT_AFTER_FLAG");
				} catch (Exception e) {
					log.errorTrace("Exception",e);
					log.error("Exception", "Exception :" + e.getMessage()); 
					RefillAccountAfterFlag = "0";
				}
				_requestMap.put("REFILL_ACNT_AFTER_FLAG",RefillAccountAfterFlag.trim());
				
				String originOperatorID=FileCache.getValue(_interfaceID, _requestMap.get("NETWORK_CODE")+"_"+"ORIGIN_OPERATOR_ID");
				if(InterfaceUtil.isNullString(originOperatorID))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "ORIGIN_OPERATOR_ID is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("ORIGIN_OPERATOR_ID",originOperatorID.trim());
				
				
				String intbarFlag=FileCache.getValue(_interfaceID,"CS5_BAR_FLAG");
				if(InterfaceUtil.isNullString(intbarFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CS5_BAR_FLAG is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("CS5_BAR_FLAG",intbarFlag.trim());
				
				String subsNoNAI=FileCache.getValue(_interfaceID, _requestMap.get("NETWORK_CODE")+"_"+"NAI");
				if(InterfaceUtil.isNullString(subsNoNAI)||!InterfaceUtil.isNumeric(subsNoNAI))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "SUBSCRIBER_NO_NAI is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("SubscriberNumberNAI",subsNoNAI.trim());
				
				String reponsecodeflag=FileCache.getValue(_interfaceID, "RESPONSE_CODE_100");
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "RESPONSE_CODE_100 flag is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("RESPONSE_CODE_100",reponsecodeflag.trim());
				
				String messageCapabilityFlag=FileCache.getValue(_interfaceID, "MESSAGE_CAPABILITY_FLAG");
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "MESSAGE_CAPABILITY_FLAG flag is not defined in IN File ");
				    //throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("MESSAGE_CAPABILITY_FLAG",messageCapabilityFlag.trim());
				
				String promotionNotificationFlag=FileCache.getValue(_interfaceID, "PROMOTION_NOTIFICATION_FLAG");
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "PROMOTION_NOTIFICATION_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("PROMOTION_NOTIFICATION_FLAG",promotionNotificationFlag.trim());
				
				String firstIVRCallSetFlag=FileCache.getValue(_interfaceID, "FIRST_IVR_CALL_SET_FLAG");
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "FIRST_IVR_CALL_SET_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("FIRST_IVR_CALL_SET_FLAG",firstIVRCallSetFlag.trim());
				
				String responseCode100handled=FileCache.getValue(_interfaceID, "RESPONSE_CODE_100_HANDLING");
				if(InterfaceUtil.isNullString(responseCode100handled))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "RESPONSE_CODE_100_HANDLING String is not defined in IN File ");
				    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("RESPONSE_CODE_100_HANDLING",responseCode100handled.trim());
				
				String accountActivationFlag=FileCache.getValue(_interfaceID, "ACCOUNT_ACTIVATION_FLAG");
				if(InterfaceUtil.isNullString(accountActivationFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "ACCOUNT_ACTIVATION_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("ACCOUNT_ACTIVATION_FLAG",accountActivationFlag.trim());
				
				String daSelectionFlagManual=FileCache.getValue(_interfaceID, "DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL");
				if(InterfaceUtil.isNullString(daSelectionFlagManual))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL flag is not defined in IN File ");
				    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL",daSelectionFlagManual.trim());
				
				String daSelectedAccID=FileCache.getValue(_interfaceID, "DEDICATED_ACCOUNT_SELECTION_ACCOUNTID");
				if(InterfaceUtil.isNullString(accountActivationFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_ACCOUNT_SELECTION_ACCOUNTID flag is not defined in IN File ");
				    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_ACCOUNT_SELECTION_ACCOUNTID",daSelectedAccID.trim());
				
				String daFirstSelectionFlag=FileCache.getValue(_interfaceID, "DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE");
				if(InterfaceUtil.isNullString(daFirstSelectionFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE flag is not defined in IN File ");
				    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE",daFirstSelectionFlag.trim());
				
				String daDefaultFirstRange=FileCache.getValue(_interfaceID, "DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE");
				if(InterfaceUtil.isNullString(daDefaultFirstRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE flag is not defined in IN File ");
				    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE",daDefaultFirstRange.trim());
				String daDefaultLastRange=FileCache.getValue(_interfaceID, "DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE");
				if(InterfaceUtil.isNullString(daDefaultLastRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE flag is not defined in IN File ");
				   throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE",daDefaultLastRange.trim());
				String daFirstRange=FileCache.getValue(_interfaceID, "DEDICATED_ACCOUNT_ID_FIRST_RANGE");
				if(InterfaceUtil.isNullString(daFirstRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_ACCOUNT_ID_FIRST_RANGE flag is not defined in IN File ");
				   throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_ACCOUNT_ID_FIRST_RANGE",daFirstRange.trim());
				String daLastRange=FileCache.getValue(_interfaceID, "DEDICATED_ACCOUNT_ID_LAST_RANGE");
				if(InterfaceUtil.isNullString(daLastRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_ACCOUNT_ID_LAST_RANGE flag is not defined in IN File ");
				   throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_ACCOUNT_ID_LAST_RANGE",daLastRange);
				
				String ucipINdisabledFlag=FileCache.getValue(_interfaceID, "IN_RESPONSE_PRINT_FLAG");
				if(InterfaceUtil.isNullString(ucipINdisabledFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "IN_RESPONSE_PRINT_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("UCIP_IN_DISABLED_FLAG",ucipINdisabledFlag.trim());
				
				String daPostpaid=FileCache.getValue(_interfaceID, "DEDICATED_ACCOUNT_FOR_POSTPAID");
				if(InterfaceUtil.isNullString(daPostpaid))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "DEDICATED_ACCOUNT_FOR_POSTPAID flag is not defined in IN File ");
				    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				_requestMap.put("DEDICATED_ACCOUNT_FOR_POSTPAID",daPostpaid.trim());
				
				
				 //String cs5_max_fail_air_count = FileCache.getValue(_interfaceID,_requestMap.get("CS5_MAX_Fail_AIR_COUNT"));
				 String cs5_max_fail_air_count = FileCache.getValue(_interfaceID,"CS5_MAX_Fail_AIR_COUNT");

			    	if(InterfaceUtil.isNullString(cs5_max_fail_air_count))
			    	{
			    	    log.error("setInterfaceParameters","Value of CS5_MAX_Fail_AIR_COUNT is not defined in the INFile");
			    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CS5_MAX_Fail_AIR_COUNT is not defined in the INFile.");
			    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			    	}
			    	_requestMap.put("CS5_MAX_Fail_AIR_COUNT",cs5_max_fail_air_count.trim());
			    	
			    	//String cs5_val_response_code_for_retries = FileCache.getValue(_interfaceID,_requestMap.get("CS5_VAL_RESPONSE_CODE_FOR_RETRIES"));
			    	String cs5_val_response_code_for_retries = FileCache.getValue(_interfaceID,"CS5_VAL_RESPONSE_CODE_FOR_RETRIES");		
			    	if(InterfaceUtil.isNullString(cs5_val_response_code_for_retries))
			    	{
			    	    log.error("setInterfaceParameters","Value of CS5_VAL_RESPONSE_CODE_FOR_RETRIES is not defined in the INFile");
			    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CS5_VAL_RESPONSE_CODE_FOR_RETRIES is not defined in the INFile.");
			    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			    	}
			    	_requestMap.put("CS5_VAL_RESPONSE_CODE_FOR_RETRIES",cs5_val_response_code_for_retries.trim());
			    	
			    	String cs5_servic_type_for_pbp_srv_class_check = FileCache.getValue(_interfaceID,"CS5_SERVIC_TYPE_FOR_PBP_SRV_CLASS_CHECK");		
			    	if(InterfaceUtil.isNullString(cs5_servic_type_for_pbp_srv_class_check))
			    	{
			    	    log.error("setInterfaceParameters","Value of CS5_SERVIC_TYPE_FOR_PBP_SRV_CLASS_CHECK is not defined in the INFile");
			    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CS5_SERVIC_TYPE_FOR_PBP_SRV_CLASS_CHECK is not defined in the INFile.");
			    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			    	}
			    	_requestMap.put("CS5_SERVIC_TYPE_FOR_PBP_SRV_CLASS_CHECK",cs5_servic_type_for_pbp_srv_class_check.trim());
			    	
			    	
			    	//String prepaid_postpaid_service_class_threshold_value = FileCache.getValue(_interfaceID,"PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE");		
			    	String prepaid_postpaid_service_class_threshold_value = ""+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.POSTPAID_SUBS_SERVICECLASS));
			    	if(InterfaceUtil.isNullString(prepaid_postpaid_service_class_threshold_value))
			    	{
			    	    log.error("setInterfaceParameters","Value of PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE is not defined in the INFile");
			    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE is not defined in the INFile.");
			    		throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			    	}
			    	_requestMap.put("PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE",prepaid_postpaid_service_class_threshold_value.trim());
			    	
				
	        }//end of try block
	        catch(BTSLBaseException be)
			{
	        	throw be;
			}
	        catch(Exception e)
	        {
	            log.error(METHOD_NAME,"Exception e="+e.getMessage());
	            throw e;
	        }//end of catch-Exception
	        finally
	        {
	        	LogFactory.printLog(METHOD_NAME, "Exited _requestMap:" + _requestMap, log);
	           
	        }//end of finally
	    }//end of setInterfaceParameters
    	
    	
      
        
    	/**
    	 * Method to send cancel request to IN for any ambiguous transaction.
    	 * This method also makes reconciliation log entry. 	
    	 * @throws	BTSLBaseException 
    	 */
        private void handleCancelTransaction() throws BTSLBaseException
        {
        	String METHOD_NAME="handleCancelTransaction";
        	LogFactory.printLog(METHOD_NAME, "Entered", log);
        	
    		String cancelTxnAllowed = null;
    		String cancelTxnStatus = null;
    		String reconciliationLogStr = null;
    		String cancelCommandStatus=null;
    		String cancelNA=null;
    		String interfaceStatus=null;
    		Log reconLog = null;
    		String systemStatusMapping=null;
    		
    		try
    		{
    			_requestMap.put("REMARK1",FileCache.getValue(_interfaceID,"REMARK1"));
    			_requestMap.put("REMARK2",FileCache.getValue(_interfaceID,"REMARK2"));
    			//get reconciliation log object associated with interface
    		    reconLog = ReconcialiationLog.getLogObject(_interfaceID);		    
    		    if (log.isDebugEnabled())log.debug("handleCancelTransaction", "reconLog."+reconLog);
    		    cancelTxnAllowed=(String)_requestMap.get("CANCEL_TXN_ALLOWED");
    		    //if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
    		    //into recon log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
    			if("N".equals(cancelTxnAllowed))
    			{
    				cancelNA=(String)_requestMap.get("CANCEL_NA");//Cancel command status as NA.
    				cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap,cancelNA,"CANCEL_COMMAND_STATUS_MAPPING");
    				_requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);				
    				interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");
    				systemStatusMapping=(String)_requestMap.get("SYSTEM_STATUS_MAPPING");				
    				cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING
    				_requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
    				reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
    				reconLog.info("",reconciliationLogStr);
    				if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
    					throw new BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.	??????)    			
    				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);	
    				//added to discard amount field from the message.
    				_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
    			}  			
    		}
    		catch(BTSLBaseException be)
    		{
    			throw be;
    		}
    		catch(Exception e)
    		{
    			log.errorTrace("handleCancelTransaction",e);
    		    log.error("handleCancelTransaction","Exception e:"+e.getMessage());
    			throw new BTSLBaseException(this,"handleCancelTransaction",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    		}
    		finally
    		{
    			LogFactory.printLog(METHOD_NAME, "Exited", log);
    			 
    		}
        }
        
        
        protected static synchronized String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException
    	{
    		String METHOD_NAME="getINTransactionID";
        	String instanceID=null;
    		int MAX_COUNTER=9999;
    		int inTxnLength=4;
    		String serviceType=null;
    		String userType=null;
    		Date mydate =null;
    		String minut2Compare=null;
    		String dateStr=null;
    		String transactionId=null;
    		try
    		{
    			if("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType) || "C2SENQ".equals(serviceType) || PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER.equals(serviceType))
    				serviceType="6";
    			else 
    				serviceType="7";
    				
    			userType = (String)p_requestMap.get("USER_TYPE");
    			if("S".equals(userType))
    				userType="3";
    			else if("R".equals(userType))	
    				userType="2";
				else 
    				userType="1";
    			instanceID = FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"INSTANCE_ID");
    			if(InterfaceUtil.isNullString(instanceID))
    			{
    				log.error(METHOD_NAME,"Parameter INSTANCE_ID is not defined in the INFile");
    				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5GpINHandler[validate]","","" , (String) p_requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
    				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    			}
    			mydate = new Date();	
    			dateStr = _sdf.format(mydate);
    			minut2Compare = dateStr.substring(8,10);
    			int currentMinut=Integer.parseInt(minut2Compare);  
    			if(currentMinut !=_prevMinut)
    			{
    				_transactionIDCounter=1;
    				_prevMinut=currentMinut;
    			}
    			else if(_transactionIDCounter > MAX_COUNTER)
    				_transactionIDCounter=1;
    			else
    				_transactionIDCounter++;
    			String txnid =String.valueOf(_transactionIDCounter);
    			int length = txnid.length();
    			int tmpLength=inTxnLength-length;
    			if(length<inTxnLength)
    			{
    				for(int i=0;i<tmpLength;i++)
    					txnid = "0"+txnid;
    			}
    			transactionId = serviceType+ dateStr+instanceID+txnid+userType;		  	
    		}
    		catch(Exception e)
    		{
    			log.errorTrace("Exception",e);
    			log.error("Exception", "Exception :" + e.getMessage()); 
    		}
    		return transactionId;		
    	}
        

}
