package com.inter.claro.cs5;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.btsl.util.BTSLUtil;
import com.inter.claro.cs5.cs5scheduler.NodeManager;
import com.inter.claro.cs5.cs5scheduler.NodeScheduler;
import com.inter.claro.cs5.cs5scheduler.NodeVO;
import com.inter.voms.VOMSHandler;

public class CS5ClaroINHandler implements InterfaceHandler{
	
	static String className="CS5ClaroINHandler";
    private static final Log log = LogFactory.getLog(CS5ClaroINHandler.class);
    
    private Map<String,String> requestMap = null;//Contains the response of the request as key and value pair.
    private Map<String,String> responseMapC = null;
    private Map<String,String> responseMap = null;
	private String interfaceID=null;//Contains the interfaceID
	private String inTXNID=null;//UNodesed to represent the Transaction ID
	private String msisdn=null;//Used to store the MSISDN
	private String referenceID=null;
	private String userType=null;
	private String stage=null;
   	private static CS5ClaroRequestFormatter cs5claroRequestFormatter=null;
	boolean reqRespAllowed=false;
	private static  SimpleDateFormat sdf = new SimpleDateFormat ("yyMMddHHmm");
	private static int transactionIDCounter=0;
	private static int prevMinut=0;
	
	static final String SENDREQTOIN="sendRequestToIN";
	static final String TXNSTATUS="TRANSACTION_STATUS";
	static final String REFID="REFERENCE ID = ";
	static final String NETCODE="NETWORK_CODE";
	static final String MSISDNMSG="MSISDN = ";
	static final String SENDERMSISDNMSG="SENDER_MSISDN";
	static final String INTSTATUS="INTERFACE_STATUS";
	static final String USERTYPES="USER_TYPE";
	static final String INTERFACEAMOUNT="INTERFACE_AMOUNT";
	static final String INTERFACEIDS="INTERFACE_ID";
	static final String INTERFACEPOSTBALANCE="INTERFACE_POST_BALANCE";
	static final String INTERFACEACTION="INTERFACE_ACTION";
	static final String INTID="INTERFACE ID";
	static final String MULTFACTOR="MULT_FACTOR";
	static final String INTERFACEPREVBALANCE="INTERFACE_PREV_BALANCE";
	static final String INTERFACEROUNDAMOUNT="INTERFACE_ROUND_AMOUNT";
	static final String POSTBALANCEENQSUCCESS="POST_BALANCE_ENQ_SUCCESS";
	static final String FILTERMSISDN="FILTER_MSISDN";
	static final String CS5VALRESPONSECODEFORRETRIES="CS5_VAL_RESPONSE_CODE_FOR_RETRIES";
	static final String CS5BARFLAG="CS5_BAR_FLAG";
	static final String DEDICATEDACCOUNTFORPOSTPAID="DEDICATED_ACCOUNT_FOR_POSTPAID";
	static final String INRECONID="IN_RECON_ID";
	static final String PREPAIDPOSTPAIDSERVICECLASSTHRESHOLDVALUE="PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE";
	static final String INTXNIDS="IN_TXN_ID";
	static final String TRANSACTIONID="TRANSACTION_ID";
	static final String REQFROMNSMS="REQ_FROM_NSMS";
	static final String RESPONSECODE100="RESPONSE_CODE_100";
	static final String SYSTEMSTATUSMAPPINGDEBITADJ="SYSTEM_STATUS_MAPPING_DEBIT_ADJ";
	static final String SYSTEMSTATUSMAPPINGCREDIT="SYSTEM_STATUS_MAPPING_CREDIT";
	static final String SYSTEMSTATUSMAPPING="SYSTEM_STATUS_MAPPING";
	static final String ROUNDFLAG="ROUND_FLAG";
	static final String CANCELTXNALLOWED="CANCEL_TXN_ALLOWED";
	static final String CANCELCOMMANDSTATUSMAPPING="CANCEL_COMMAND_STATUS_MAPPING";
	static final String RESPONSECODE100HANDLING="RESPONSE_CODE_100_HANDLING";
	static final String ACCOUNTSTATUS="ACCOUNT_STATUS";
	static final String CANCELNA="CANCEL_NA";
	
	
	
	
	private Object[] successList=CS5ClaroI.RESULT_OK.split(",");
	private Object[] errorList=CS5ClaroI.RESULT_NOT_OK.split(",");
	
	 
	static
	{
	    if(log.isDebugEnabled())
	    	log.debug(className+"[static]",PretupsI.ENTERED);
	    try
	    {
	    	cs5claroRequestFormatter = new CS5ClaroRequestFormatter();
	    }
	    catch(Exception e)
	    {
            log.errorTrace("While instantiation of CS5ClaroRequestFormatter get Exception e::"+className,e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, className+"[static]","","", "","While instantiation of CS5ClaroRequestFormatter get Exception e::"+e.getMessage());
	    }
	    finally
	    {
	        if(log.isDebugEnabled())
	        	log.debug(className+"[static]",PretupsI.EXITED);
	    }
	}
	
	/**
     * Implements the logic that validate the subscriber and get the subscriber information 
     * from the IN.
     * @param	HashMap  pRequestMap
     * @throws	BTSLBaseException, Exception 
     */
	@Override
    public void validate(HashMap<String,String> pRequestMap) throws BTSLBaseException, Exception
    {
    	final String methodName="validate";
    	if(log.isDebugEnabled())
    		log.debug(methodName,PretupsI.ENTERED+" pRequestMap:"+pRequestMap);
         requestMap = pRequestMap;
         try
		 {
        	 if(requestMap.get("REQFROMNSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)requestMap.get(REQFROMNSMS)))
        	 {
        		 requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);
        		 requestMap.put("SERVICE_CLASS", PretupsI.ALL);
        		 requestMap.put(ACCOUNTSTATUS,"ACTIVE");
        	 }
        	 else
        	 {
        		 interfaceID=requestMap.get(INTERFACEIDS);
        		 msisdn=requestMap.get("MSISDN"); 	
        		 if(!InterfaceUtil.isNullString(msisdn))
        		 {
        			 msisdn =InterfaceUtil.getFilterMSISDN(interfaceID,msisdn);                        
        		 }
        		 requestMap.put(FILTERMSISDN,msisdn);
        		 //Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
        		 //If validation of subscriber is not required set the SUCCESS code into request map and return.
        		 String validateRequired = FileCache.getValue(interfaceID,requestMap.get("REQ_SERVICE")+"_"+requestMap.get(USERTYPES));
        		 if("N".equals(validateRequired))
        		 {
        			 requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);
        			 return ;
        		 }
        		 inTXNID=getINTransactionID(requestMap);	
        		 requestMap.put(INRECONID,inTXNID);
        		 requestMap.put(INTXNIDS,inTXNID);
        		 referenceID=requestMap.get(TRANSACTIONID);
        		 String multFactor = FileCache.getValue(interfaceID,MULTFACTOR);
        		 if(log.isDebugEnabled())
        			 log.debug("validate","multFactor: "+multFactor);
        		 if(InterfaceUtil.isNullString(multFactor))
        		 {
        			 log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
        			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[validate]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "MULT_FACTOR  is not defined in the INFile");
        			 throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
        		 }
        		 multFactor=multFactor.trim();
        		 //Set the interface parameters into requestMap
        		 setInterfaceParameters();
        		//changed by rajeev
        		 //key value of requestMap is formatted into XML string for the validate request.
        		 String inStr = cs5claroRequestFormatter.generateRequest(CS5ClaroI.ACTION_ACCOUNT_INFO,requestMap);
        		 
        		  //sending the AccountInfo request to IN along with validate action defined in CS5ClaroI interface
        		 sendRequestToIN(inStr,CS5ClaroI.ACTION_ACCOUNT_INFO);
        		 
        		  String interfaceStatus=requestMap.get(INTSTATUS);
        		  
        		  
        		 
        		  
        		  if(CS5ClaroI.ACCOUNT_NOT_ACTIVE.equals(interfaceStatus))
        		  {
        				if(log.isDebugEnabled())
        					log.debug(methodName,PretupsI.ENTERED+" interfaceStatus is   :"+interfaceStatus);
        				
        			  String inStr1 = cs5claroRequestFormatter.generateRequest(CS5ClaroI.ACTION_ACCOUNT_DETAILS,requestMap);
                      sendRequestToIN(inStr1,CS5ClaroI.ACTION_ACCOUNT_DETAILS);
        		  }
        		  interfaceStatus=requestMap.get(INTSTATUS);

        		 //set TRANSACTION_STATUS as Success in request map
        		 requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);
        		 String amountStr ="0";
        		 amountStr=responseMap.get("accountValue1");
           		 
           		 //dedicatedAccountInformation and account value 
           		 String  da100=requestMap.get(DEDICATEDACCOUNTFORPOSTPAID);
           		 String daAccInfoString=responseMap.get("dedicatedAccountInformation");
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
           		 if(log.isDebugEnabled())
           			 log.debug(methodName,PretupsI.ENTERED+" pda100AccountValue :"+da100AccountValue);
           			
           		 if(PretupsI.SUBSCRIBER_TYPE_POST.equals((String)requestMap.get("SUBSCRIBER_TYPE")))
           		 {
           			 if(InterfaceUtil.isNullString(amountStr))
           				 amountStr="0";
           			 else
           			 amountStr=da100AccountValue;
           			
           		 }
           		 //get value of accountValue1 from response map (accountValue1 was set in response map in sendRequestToIN method.)
        		 
     		 	
        		 try
        		 {
        			 amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
        			 requestMap.put(INTERFACEPREVBALANCE,amountStr);
        			 //Temporary to run CS5 Idea as postpaid interface
        			 requestMap.put("AvailableBalance",amountStr);
        			 requestMap.put("BILL_AMOUNT_BAL","0");
        		 }
        		 catch(Exception e)
        		 {
                     log.errorTrace(PretupsI.EXCEPTION+methodName,e);
        			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[validate]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:"+e.getMessage());
        			 throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
        		 }
        		 Date supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String)responseMap.get("supervisionExpiryDate"), "yyyyMMdd");
        		 Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String)responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");


        		 Date currentDate = new Date();
        		 if(("0".equals(requestMap.get(CS5BARFLAG))) && ("1".equals(responseMap.get("temporaryBlockedFlag"))))
        		 {
        			 requestMap.put(ACCOUNTSTATUS,"BARRED");    
        		 }

        		 if(supervisionExpiryDate.after(currentDate))
        			 requestMap.put(ACCOUNTSTATUS,"ACTIVE");
        		 else if(currentDate.after(supervisionExpiryDate))
        			 requestMap.put(ACCOUNTSTATUS,"INACTIVE");
        		 else if(currentDate.after(serviceFeeExpiryDate))
        			 requestMap.put(ACCOUNTSTATUS,"DEACTIVE");

        		 requestMap.put("SERVICE_CLASS", (String) responseMap.get("serviceClassCurrent"));
        		 //set OLD_EXPIRY_DATE in request map as returned from responseMap.
        		 requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((responseMap.get("supervisionExpiryDate")).trim(), "yyyyMMdd"));
        		 requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((responseMap.get("serviceFeeExpiryDate")).trim(), "yyyyMMdd"));


        		 // Logic is written by Veer for validation On IN for EVD Service

        		 String  serviceType =requestMap.get("REQ_SERVICE");
        	
        	 }
		 }
         catch (BTSLBaseException be)
         {
        	log.error(methodName,"BTSLBaseException be="+be.getMessage());
        	throw be; 	   	
         }
         catch(Exception e)
         {
            log.errorTrace(PretupsI.EXCEPTION+methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[validate]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String)requestMap.get(NETCODE), "While validation of the subscriber get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
        	if(log.isDebugEnabled())
        		log.debug(methodName,"Exiting with  requestMap: "+requestMap);
         }		 
    }//end of validate
    /**
     * Implements the logic that credit the subscriber account on IN.
     * @param	HashMap  pRequestMap
     * @throws	BTSLBaseException, Exception  
     */
    @Override
    public void credit(HashMap pRequestMap) throws BTSLBaseException, Exception 
	{
    	final String methodName = "credit";
    	if (log.isDebugEnabled())
    		log.debug(methodName,PretupsI.ENTERED+" pRequestMap: " + pRequestMap);
    	double systemAmtDouble=0;
    	double multFactorDouble=0;
    	String amountStr=null;
        requestMap = pRequestMap;
	
        try
         {
        	if(requestMap.get(REQFROMNSMS)!=null && PretupsI.YES.equalsIgnoreCase((String)requestMap.get(REQFROMNSMS)))
          	 {
          		 requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);
          	 }
        	else
        	{
        		interfaceID=requestMap.get(INTERFACEIDS);		
        		inTXNID=getINTransactionID(requestMap);
        		requestMap.put(INRECONID,inTXNID);
        		requestMap.put(INTXNIDS,inTXNID);
        		referenceID=requestMap.get(TRANSACTIONID);
        		msisdn=requestMap.get("MSISDN");
        		if(!InterfaceUtil.isNullString(msisdn))
        			msisdn =InterfaceUtil.getFilterMSISDN(interfaceID,msisdn);                        

        		requestMap.put(FILTERMSISDN,msisdn);
        		userType=requestMap.get(USERTYPES);

        		//Fetching the MULT_FACTOR from the INFile.
        		//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
        		String multFactor = FileCache.getValue(interfaceID,MULTFACTOR);
        		if(log.isDebugEnabled())
        			log.debug(methodName,"multFactor:"+multFactor);
        		if(InterfaceUtil.isNullString(multFactor))
        		{
        			log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[credit]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "MULT_FACTOR  is not defined in the INFile");
        			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
        		}
        		multFactor = multFactor.trim();
        		requestMap.put(MULTFACTOR,multFactor);

        		//Set the interface parameters into requestMap
        		setInterfaceParameters();
        		
        		requestMap.put(SYSTEMSTATUSMAPPING,SYSTEMSTATUSMAPPINGCREDIT);
        		try
        		{
        			multFactorDouble=Double.parseDouble(multFactor);
        			double interfaceAmtDouble = Double.parseDouble((String)requestMap.get(INTERFACEAMOUNT));
        			systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
        			amountStr=String.valueOf(systemAmtDouble);
        			//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
        			String roundFlag = FileCache.getValue(interfaceID,ROUNDFLAG);
        			if(log.isDebugEnabled())
        				log.debug(methodName,"From file cache roundFlag = "+roundFlag);
        			//If the ROUND_FLAG is not defined in the INFile 
        			if(InterfaceUtil.isNullString(roundFlag))
        			{
        				roundFlag="Y";
        				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5ClaroINHandler[credit]",referenceID+MSISDNMSG+msisdn ," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
        			}
        			//If rounding of amount is allowed, round the amount value and put this value in request map.
        			if("Y".equals(roundFlag.trim()))
        			{
        				amountStr=String.valueOf(Math.round(systemAmtDouble));
        				requestMap.put(INTERFACEROUNDAMOUNT,amountStr);
        			}
        		}
        		catch(Exception e)
        		{
                    log.errorTrace(PretupsI.EXCEPTION+methodName,e);
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[credit]",REFID+referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "INTERFACE_AMOUNT  is not Numeric");
        			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        		}
        		if(log.isDebugEnabled())
        			log.debug(methodName,"transfer_amount:"+amountStr);
        		//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
        		requestMap.put("transfer_amount",amountStr);

//      		key value of requestMap is formatted into XML string for the validate request.
        		String inStr = cs5claroRequestFormatter.generateRequest(CS5ClaroI.ACTION_RECHARGE_CREDIT,requestMap);

        		//sending the Re-charge request to IN along with re-charge action defined in CS5ClaroI interface
        		sendRequestToIN(inStr,CS5ClaroI.ACTION_RECHARGE_CREDIT);
        		//set TRANSACTION_STATUS as Success in request map

        		
        		String interfaceStatus=requestMap.get(INTSTATUS);
        		String prevStatus;
    		 if(Arrays.asList(successList).contains(interfaceStatus)){
  		          		requestMap.put(TXNSTATUS, InterfaceErrorCodesI.SUCCESS);
    		 }else if(("true".equalsIgnoreCase((String)requestMap.get(RESPONSECODE100)) && (requestMap.get(RESPONSECODE100HANDLING)).contains(interfaceStatus)) || BTSLUtil.NullToString(interfaceStatus).equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS))
	            {
    			 prevStatus = interfaceStatus;
    			 try {
    				 String previousbalance = requestMap.get(INTERFACEPREVBALANCE);
 	            	String inStr1 = cs5claroRequestFormatter.generateRequest(CS5ClaroI.ACTION_ACCOUNT_INFO,requestMap);
 	            	sendRequestToIN(inStr1,CS5ClaroI.ACTION_ACCOUNT_INFO);
 	            	interfaceStatus=requestMap.get(INTSTATUS);
 	            	String currentbalance=responseMap.get("accountValue1");
 	            	
 	            	multFactor=requestMap.get(MULTFACTOR);
 	                try
 	    			{
 	                	currentbalance = InterfaceUtil.getSystemAmountFromINAmount(currentbalance,Double.parseDouble(multFactor));
 	    				
 	    			}
 	                catch(Exception e)
 	    			{
 	                    log.errorTrace(PretupsI.EXCEPTION+methodName,e);
 	                	EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[validate]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "accountValue1 not numeric Parsing Exception e:"+e.getMessage());
 	                	throw new BTSLBaseException(this,SENDREQTOIN,InterfaceErrorCodesI.ERROR_100_TOP_STAGE);
 	    			}
 	            
 	            	
 	             	if(Double.parseDouble(currentbalance)<=Double.parseDouble(previousbalance))  
 	             	{	
 	             		log.error(SENDREQTOIN,"After again calling validate Previous and Final Balance are same, No Top Up on IN, Old Balance="+previousbalance+" Final balance="+currentbalance);
 	             		requestMap.put(TXNSTATUS,InterfaceErrorCodesI.FAIL);
 	             		throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_100_TOP_STAGE);
 	             	}
 	             	else 
 	             	{
 	             		  log.info(SENDREQTOIN,"After again calling validate Previous and Final Balance are not same, Top Up successful on IN, Old Balance="+previousbalance+" Final balance="+currentbalance);
     	             		
 	             		  requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);
 	             		//whether to set reponse code as 0 here or not
 	             	}
				} catch (Exception e) {
					if(BTSLUtil.NullToString(prevStatus).equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
		            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+stage,  "Exception in Ambiguous handling.  so throwing AMBIGOUS exception");
		            	TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Read Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]");
		            	requestMap.put(INTSTATUS,InterfaceErrorCodesI.AMBIGOUS);	       
						throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
		            }else{
		            	throw e;
		            }
				}
	               
	            }else{
	            	requestMap.put(TXNSTATUS, InterfaceErrorCodesI.FAIL);
	            }
        		// set NEW_EXPIRY_DATE into request map


        		requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) responseMapC.get("supervisionExpiryDate"), "yyyyMMdd"));
        		requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) responseMapC.get("serviceFeeExpiryDate"), "yyyyMMdd"));


        		//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
        		try
        		{
        			String postBalanceStr;
        			if("100".equalsIgnoreCase((String)responseMapC.get("responseCode")))
        				postBalanceStr =  responseMap.get("accountValue1");
        			else
        				postBalanceStr = responseMapC.get("accountValue1");
        			postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
        			requestMap.put(INTERFACEPOSTBALANCE,postBalanceStr);
        			//Added for Promobalance k 

        			String postBalancePromoStr1 = responseMapC.get("refillAmount1");

        			if(!InterfaceUtil.isNullString(postBalancePromoStr1))
        				postBalancePromoStr1 = InterfaceUtil.getSystemAmountFromINAmount(postBalancePromoStr1,multFactorDouble);
        			else 
        				postBalancePromoStr1="0";
        			requestMap.put("INTERFACE_PROMO_POST_BALANCE",postBalancePromoStr1);

        			if(log.isDebugEnabled())
        				log.debug("postBalancePromoStr=",postBalancePromoStr1);

        		}
        		catch(Exception e)
        		{
                    log.errorTrace(PretupsI.EXCEPTION+methodName,e);
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[credit]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "NEWBALANCE  is not Numeric");
        			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
        		}
        		requestMap.put(POSTBALANCEENQSUCCESS, "Y");
        	}
            
         } 
        catch (BTSLBaseException be)
        {
    		log.error(methodName,"BTSLBaseException be:"+be.getMessage());    		   		
    		throw be;
		}
         catch (Exception e)
         {
             log.errorTrace(PretupsI.EXCEPTION+methodName,e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[credit]",referenceID,msisdn, (String) requestMap.get(NETCODE), "While credit get the Exception e:"+e.getMessage());
             throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (log.isDebugEnabled())
            	 log.debug(methodName,PretupsI.EXITED+" requestMap=" + requestMap);
         }
    }//end of credit
    
    
    /**
     * This method is used to adjust the following for sender credit back
     */

   @Override
   public void creditAdjust(HashMap pRequestMap) throws BTSLBaseException, Exception
   {
   		String methodName="creditAdjust";
	   if (log.isDebugEnabled())
		   log.debug(methodName,PretupsI.ENTERED+" pRequestMap:" + pRequestMap);
       requestMap = pRequestMap;//assign map passed from InterfaceModule to requestMap(instance var) 
       double systemAmtDouble =0;
       String amountStr=null;
       
	
       double multFactorDouble=0;
       try
		{
       	if(requestMap.get(REQFROMNSMS)!=null && PretupsI.YES.equalsIgnoreCase((String)requestMap.get(REQFROMNSMS)))
       	{
       		requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);
       	}
       	else
       	{
       		userType=requestMap.get(USERTYPES);
       		interfaceID=requestMap.get(INTERFACEIDS);// get interface id form request map

       		inTXNID=getINTransactionID(requestMap);//Generate the IN transaction id and set in requestMap
       		requestMap.put(INTXNIDS,inTXNID);//get TRANSACTION_ID from request map (which has been passed by controller)
       		requestMap.put(INRECONID,inTXNID);

       		referenceID=requestMap.get(TRANSACTIONID);

       		msisdn=requestMap.get("MSISDN");//get MSISDN from request map
       		//	add transaction code and transaction type in request map
       		if(!InterfaceUtil.isNullString(msisdn))
       		{
       			msisdn =InterfaceUtil.getFilterMSISDN(interfaceID,msisdn);                        
       		}

       		requestMap.put(FILTERMSISDN,msisdn);



       		//Get the Multiplication factor from the FileCache with the help of interface id.
       		String multFactor = FileCache.getValue(interfaceID,MULTFACTOR);
       		if(InterfaceUtil.isNullString(multFactor))
       		{
       			log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
       			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[creditAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "MULT_FACTOR  is  not defined in the INFile");
       			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
       		}
       		multFactor = multFactor.trim();
       		//Set the interface parameters into requestMap
       		setInterfaceParameters();
       		
       		requestMap.put(SYSTEMSTATUSMAPPING,SYSTEMSTATUSMAPPINGDEBITADJ);
       		try
       		{
       			double multFactorDouble1=Double.parseDouble(multFactor);
       			double interfaceAmtDouble = Double.parseDouble((String)requestMap.get(INTERFACEAMOUNT));
       			systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble1);
       			amountStr=String.valueOf(systemAmtDouble);
       			//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
       			String roundFlag = FileCache.getValue(interfaceID,ROUNDFLAG);
       			if(log.isDebugEnabled())
       				log.debug(methodName,"From file cache roundFlag = "+roundFlag);
       			//If the ROUND_FLAG is not defined in the INFile 
       			if(InterfaceUtil.isNullString(roundFlag))
       			{
       				roundFlag="Y";
       				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5ClaroINHandler[creditAdjust]",REFID+referenceID+MSISDNMSG+msisdn ," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
       			}
       			//If rounding of amount is allowed, round the amount value and put this value in request map.
       			if("Y".equals(roundFlag.trim()))
       			{
       				amountStr=String.valueOf(Math.round(systemAmtDouble));
       				requestMap.put(INTERFACEROUNDAMOUNT,amountStr);
       			}
       		}
       		catch(Exception e)
       		{
                log.errorTrace(PretupsI.EXCEPTION+methodName,e);
       			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[debitAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "INTERFACE_AMOUNT  is not Numeric");
       			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
       		}
       		if(log.isDebugEnabled())
       			log.debug(methodName,"transfer_amount:"+amountStr+" multFactor:"+multFactor);
       		//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
       		requestMap.put("transfer_amount",amountStr);


       		String inStr = cs5claroRequestFormatter.generateRequest(CS5ClaroI.ACTION_IMMEDIATE_DEBIT,requestMap);


       		//sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS5MobililI interface
       		sendRequestToIN(inStr,CS5ClaroI.ACTION_IMMEDIATE_DEBIT);
       		//set TRANSACTION_STATUS as Success in request map
       		requestMap.put(TXNSTATUS, InterfaceErrorCodesI.SUCCESS);
       		multFactorDouble=Double.parseDouble(multFactor);
       		try
       		{
       			String postBalanceStr = responseMap.get("accountValue1");
       			
       			if(InterfaceUtil.isNullString(postBalanceStr))
       			{
       				postBalanceStr="0";
       				try
       				{
       					String requestQuantity=requestMap.get(INTERFACEAMOUNT);
       					String previousBalance=requestMap.get(INTERFACEPREVBALANCE);
        				
       					int postBalance =Integer.parseInt(previousBalance)+Integer.parseInt(requestQuantity);
        					postBalanceStr=String.valueOf(postBalance);
       				}
       				catch(Exception ex){
        					postBalanceStr="0";
        					throw ex;
       				}
       			}
       			
       				
       			postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
       			requestMap.put(INTERFACEPOSTBALANCE,postBalanceStr);
       		}
       		catch(Exception e)
       		{
                log.errorTrace(PretupsI.EXCEPTION+methodName,e);
       			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[creditAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "NEWBALANCE  is not Numeric");
       			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
       		}				
       		//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
       		requestMap.put(POSTBALANCEENQSUCCESS, "N"); 
       	} 
		}
       catch (BTSLBaseException be)
       {
       	pRequestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
   		log.error(methodName,"BTSLBaseException be:"+be.getMessage());    		   		
   		if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
   			throw be;
   		try
			{    			
   			requestMap.put("TRANSACTION_TYPE","CR");
   			handleCancelTransaction();
   		}
   		catch(BTSLBaseException bte)
			{
				throw bte;
			}
			catch(Exception e)
			{
				 log.errorTrace(PretupsI.EXCEPTION+methodName,e);
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[creditAdjust]", referenceID,msisdn, (String) requestMap.get(NETCODE), "Exception while processing ambiguous case in creditAdjust");
				 throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
        catch (Exception e)
        {
            log.errorTrace(PretupsI.EXCEPTION+methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[creditAdjust]",referenceID,msisdn, (String) requestMap.get(NETCODE), "Exception while creditAdjust e:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (log.isDebugEnabled())
            	log.debug(methodName,PretupsI.EXITED+" requestMap=" + requestMap);
        }
   }//end of creditAdjust.
    /**
     * Implements the logic that debit the subscriber account on IN.
     * @param	HashMap  pRequestMap
     * @throws	BTSLBaseException, Exception
     */
    @Override
    public void debitAdjust(HashMap pRequestMap) throws BTSLBaseException, Exception
    {
    	final String methodName = "debitAdjust";
    	if (log.isDebugEnabled())
    		log.debug(methodName,PretupsI.ENTERED+" pRequestMap:" + pRequestMap);
        requestMap = pRequestMap;//assign map passed from InterfaceModule to requestMap(instance var) 
        double systemAmtDouble =0;
        String amountStr=null;
        
	
        double multFactorDouble=0;
        try
		{
        	if(requestMap.get(REQFROMNSMS)!=null && PretupsI.YES.equalsIgnoreCase((String)requestMap.get(REQFROMNSMS)))
        	{
        		requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);
        	}
        	else
        	{
        		userType=requestMap.get(USERTYPES);
        		interfaceID=requestMap.get(INTERFACEIDS);// get interface id form request map

        		inTXNID=getINTransactionID(requestMap);//Generate the IN transaction id and set in requestMap
        		requestMap.put(INTXNIDS,inTXNID);//get TRANSACTION_ID from request map (which has been passed by controller)
        		requestMap.put(INRECONID,inTXNID);

        		referenceID=requestMap.get(TRANSACTIONID);

        		msisdn=requestMap.get("MSISDN");//get MSISDN from request map
        		//	add transaction code and transaction type in request map
        		if(!InterfaceUtil.isNullString(msisdn))
        		{
        			msisdn =InterfaceUtil.getFilterMSISDN(interfaceID,msisdn);                        
        		}

        		requestMap.put(FILTERMSISDN,msisdn);



        		//Get the Multiplication factor from the FileCache with the help of interface id.
        		String multFactor = FileCache.getValue(interfaceID,MULTFACTOR);
        		if(InterfaceUtil.isNullString(multFactor))
        		{
        			log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[debitAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "MULT_FACTOR  is  not defined in the INFile");
        			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
        		}
        		multFactor = multFactor.trim();
        		//Set the interface parameters into requestMap
        		setInterfaceParameters();
        		
        		requestMap.put(SYSTEMSTATUSMAPPING,SYSTEMSTATUSMAPPINGDEBITADJ);
        		try
        		{
        			double multFactorDouble1=Double.parseDouble(multFactor);
        			double interfaceAmtDouble = Double.parseDouble((String)requestMap.get(INTERFACEAMOUNT));
        			systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble1);
        			amountStr=String.valueOf(systemAmtDouble);
        			//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
        			String roundFlag = FileCache.getValue(interfaceID,ROUNDFLAG);
        			if(log.isDebugEnabled())
        				log.debug(methodName,"From file cache roundFlag = "+roundFlag);
        			//If the ROUND_FLAG is not defined in the INFile 
        			if(InterfaceUtil.isNullString(roundFlag))
        			{
        				roundFlag="Y";
        				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5ClaroINHandler[debitAdjust]",REFID+referenceID+MSISDNMSG+msisdn ," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
        			}
        			//If rounding of amount is allowed, round the amount value and put this value in request map.
        			if("Y".equals(roundFlag.trim()))
        			{
        				amountStr=String.valueOf(Math.round(systemAmtDouble));
        				requestMap.put(INTERFACEROUNDAMOUNT,amountStr);
        			}
        		}
        		catch(Exception e)
        		{
                    log.errorTrace(PretupsI.EXCEPTION+methodName,e);
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[debitAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "INTERFACE_AMOUNT  is not Numeric");
        			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        		}
        		if(log.isDebugEnabled())
        			log.debug(methodName,"transfer_amount:"+amountStr+" multFactor:"+multFactor);
        		//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
        		requestMap.put("transfer_amount","-"+amountStr);


        		String inStr = cs5claroRequestFormatter.generateRequest(CS5ClaroI.ACTION_IMMEDIATE_DEBIT,requestMap);


        		//sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS5MobililI interface
        		sendRequestToIN(inStr,CS5ClaroI.ACTION_IMMEDIATE_DEBIT);
        		//set TRANSACTION_STATUS as Success in request map
        		requestMap.put(TXNSTATUS, InterfaceErrorCodesI.SUCCESS);
        		multFactorDouble=Double.parseDouble(multFactor);
        		try
        		{
        			String postBalanceStr = responseMap.get("accountValue1");
        			if(InterfaceUtil.isNullString(postBalanceStr))
        			{
        				try
        				{
        					String requestQuantity=requestMap.get(INTERFACEAMOUNT);
        					String previousBalance=requestMap.get(INTERFACEPREVBALANCE);
        				
        					int postBalance =Integer.parseInt(previousBalance)-Integer.parseInt(requestQuantity);
        					postBalanceStr=String.valueOf(postBalance);
        				}
        				catch(Exception ex){
        					postBalanceStr="0";
        					throw ex;
        				}
        			}
        				
        			postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
        			requestMap.put(INTERFACEPOSTBALANCE,postBalanceStr);
        		}
        		catch(Exception e)
        		{
                    log.errorTrace(PretupsI.EXCEPTION+methodName,e);
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[credit]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE), "NEWBALANCE  is not Numeric");
        			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
        		}				
        		//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
        		requestMap.put(POSTBALANCEENQSUCCESS, "N"); 
        	} 
		}
        catch (BTSLBaseException be)
        {
        	pRequestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
    		log.error(methodName,"BTSLBaseException be:"+be.getMessage());    		   		
    		if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
    			throw be;
    		try
			{    			
    			requestMap.put("TRANSACTION_TYPE","DR");
    			handleCancelTransaction();
    		}
    		catch(BTSLBaseException bte)
			{
				throw bte;
			}
			catch(Exception e)
			{
	              log.errorTrace(PretupsI.EXCEPTION+methodName,e);
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[debitAdjust]", referenceID,msisdn, (String) requestMap.get(NETCODE), "Exception while processing ambiguous case in creditAdjust");
				 throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
         catch (Exception e)
         {
             log.errorTrace(PretupsI.EXCEPTION+methodName,e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[debitAdjust]",referenceID,msisdn, (String) requestMap.get(NETCODE), "Exception while creditAdjust e:"+e.getMessage());
             throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (log.isDebugEnabled())
            	 log.debug(methodName,PretupsI.EXITED+" requestMap=" + requestMap);
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
     * @param	String pInRequestStr
     * @param	int pAction
     * @throws	BTSLBaseException
     */
    private void sendRequestToIN(String pInRequestStr, int pAction) throws BTSLBaseException
    {
    	final String methodName = SENDREQTOIN;

        if(log.isDebugEnabled())
        	log.debug(SENDREQTOIN,PretupsI.ENTERED+" pInRequestStr::"+pInRequestStr+" pAction::"+pAction);
        String  iNTraceEnabled=requestMap.get("UCIP_IN_DISABLED_FLAG");
		
		
        
        String responseStr = "";
        NodeVO cs5NodeVO=null;
        NodeScheduler cs5NodeScheduler=null;
        CS5ClaroUrlConnection cs5URLConnection = null;
        long startTime=0;
        long endTime=0;
        long warnTime=0;
        int readTimeOut=0;
        String inReconID=null;
      
        ArrayList<String> triedURLList = new ArrayList<>();
        ArrayList<String> triedNodeNumberList = new ArrayList<>();
        CS5Status cs5Status = CS5Status.getInstance();
        boolean retryRequired=true;
        String responseCode="";
		
        
        try
        {
        
        	if(pAction==CS5ClaroI.ACTION_RECHARGE_CREDIT){
        		responseMapC = new HashMap<>();
        		stage=PretupsI.TXN_LOG_TXNSTAGE_INTOP;
        	}
        	else{
        		responseMap = new HashMap<>();
        		stage=PretupsI.TXN_LOG_TXNSTAGE_INVAL;
        	}

            inReconID=requestMap.get(INRECONID);
			if(inReconID==null)
				inReconID=inTXNID;
            cs5NodeScheduler = NodeManager.getScheduler(interfaceID);
          //check if cs5NodeScheduler is null throw exception.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
            if(cs5NodeScheduler==null){
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_WHILE_GETTING_SCHEDULER_OBJECT);
            }
            if(iNTraceEnabled!=null && "Y".equalsIgnoreCase(iNTraceEnabled)){
            	TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,"[IN Request XML] : "+pInRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: [REQUEST_MAP] : "+requestMap);
            }else{
            	TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,"[IN Request XML] : Suppresed",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: [REQUEST_MAP] : "+requestMap);
   		 	}

            //Get the retry number from the object that is used to retry the getNode in case connection is failed.
            //Host name and userAgent may be set into the VO corresponding to each Node for authentication-CONFIRM, if it is not releted with the request xml.
            String hostName = cs5NodeScheduler.getHeaderHostName();
            String userAgent = cs5NodeScheduler.getUserAgent();
            
                        
					            for(int loop=1;loop<=cs5NodeScheduler.getNodeTable().size();loop++)
					            {
						          	
							            long startTimeNode = System.currentTimeMillis();
							            if(log.isDebugEnabled())
							            	log.debug(SENDREQTOIN,"Start time to find the scheduled Node startTimeNode::"+startTimeNode+"miliseconds");
							            //If the connection for corresponding node is failed, retry to get the node with configured number of times.
							            //If connection eshtablished then break the loop.
							              try
								            {
									            cs5NodeVO = cs5NodeScheduler.getNodeVO(inReconID);
									           
									            //Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
									            if(cs5NodeVO==null){
									                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_DETAIL_NOT_FOUND );
									            }
									            if(log.isDebugEnabled())
									            	log.error(SENDREQTOIN,"URL PICKED IS ......."+ cs5NodeVO.getUrl());
									            
									            if(triedNodeNumberList!= null && !(triedNodeNumberList.isEmpty()) && triedNodeNumberList.contains(""+cs5NodeVO.getNodeNumber())){
									            	cs5NodeVO.decrementConNumber(inReconID);
									        		for(int i=1;i<=cs5NodeScheduler.getNodeTable().size();i++){
														if(!triedNodeNumberList.contains(""+((NodeVO)(cs5NodeScheduler.getNodeTable().get(i))).getNodeNumber())){
															cs5NodeVO=(NodeVO)(cs5NodeScheduler.getNodeTable().get(i));
															cs5NodeVO.incrementConNumber(inReconID);
														}
													}
					
												}
									            TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":RETRY COUNT"+loop,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Connect AIR Number=["+cs5NodeVO.getNodeNumber()+"] Connect AIR URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
									            
					
											
									            
									            warnTime=cs5NodeVO.getWarnTime();
									            //Get the read time out based on the action.
									            if(CS5ClaroI.ACTION_ACCOUNT_INFO==pAction || CS5ClaroI.ACTION_ACCOUNT_DETAILS==pAction)
									                readTimeOut=cs5NodeVO.getValReadTimeOut();
									            else
									                readTimeOut=cs5NodeVO.getTopReadTimeOut();
									            //Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
									            cs5URLConnection = new CS5ClaroUrlConnection(cs5NodeVO.getUrl(),cs5NodeVO.getUsername(),cs5NodeVO.getPassword(),cs5NodeVO.getConnectionTimeOut(),readTimeOut,cs5NodeVO.getKeepAlive(),pInRequestStr.length(),hostName,userAgent);
									            
												
									            /////////////////////////
									            
									            try
									            {
										        	
													PrintWriter out = cs5URLConnection.getPrintWriter();
										            out.flush();
													startTime=System.currentTimeMillis();
													requestMap.put("IN_START_TIME",String.valueOf(startTime));
									                out.println(pInRequestStr);
									                out.flush();
									            }
										        catch(Exception e)
										        {
										        	retryRequired=false;
									                log.error(SENDREQTOIN,PretupsI.EXCEPTION+e);
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get(NETCODE),"While sending request to cs5Claro IN INTERFACE_ID=["+interfaceID +"]and Node URL=["+cs5NodeVO.getUrl()+"] Exception::"+e.getMessage());
									                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":IN Request Writing Failure",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"] Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
									                requestMap.put(INTSTATUS,InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);	       
													throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
										        }
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
													    log.info(SENDREQTOIN, "WARN time reaches, startTime::"+startTime+" endTime::"+endTime+" From file cache warnTime::"+warnTime+ " time taken (endTime-startTime)::"+(endTime-startTime));
													    EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.MAJOR,"CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"CS5ClaroIN is taking more time than the warning threshold. Time: "+(endTime-startTime)+"INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] and Node URL=["+cs5NodeVO.getUrl()+"]");
													}
										        }
										        catch(SocketTimeoutException ste)
									            {
										        	retryRequired=false;
									                
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"AIR Read Time Out Exception INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":AIR Read Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
							
									                
										            if((pAction==CS5ClaroI.ACTION_ACCOUNT_INFO || pAction==CS5ClaroI.ACTION_ACCOUNT_DETAILS) && "V".equals(requestMap.get(INTERFACEACTION)))
										            {
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,  "Read timeout from IN.  so throwing Fail in validation exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
														requestMap.put(INTSTATUS,InterfaceErrorCodesI.ERROR_RESPONSE);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
										            }else{
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,  "Read timeout from IN.  so throwing AMBIGOUS exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
										            	TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Read Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										            	requestMap.put(INTSTATUS,InterfaceErrorCodesI.AMBIGOUS);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
										            }
										        }//end of catch-Exception
										        catch(Exception e)
										        {
										        	retryRequired=false;
									                
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"AIR Read Exception INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":AIR Read Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
							
									                
										            if((pAction==CS5ClaroI.ACTION_ACCOUNT_INFO || pAction==CS5ClaroI.ACTION_ACCOUNT_DETAILS) && "V".equals(requestMap.get(INTERFACEACTION)))
										            {
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,  "Read Exception from IN.  so throwing Fail in validation exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
														requestMap.put(INTSTATUS,InterfaceErrorCodesI.ERROR_RESPONSE);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
										            }else{
										          
										            	EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,  "Read Exception from IN.  so throwing AMBIGOUS exception and Node Number=["+cs5NodeVO.getNodeNumber()+"] and NODE URL=["+cs5NodeVO.getUrl()+"]");
										            	TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Read Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										            	requestMap.put(INTSTATUS,InterfaceErrorCodesI.AMBIGOUS);	       
														throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
										            }
										          
										        }//end of catch-Exception
										        finally
										        {
										        	retryRequired=false;
													if(endTime==0)
														endTime=System.currentTimeMillis();
													requestMap.put("IN_END_TIME",String.valueOf(endTime));
													log.error(SENDREQTOIN,"Request sent to IN at:"+startTime+" Response received from IN at:"+endTime+" defined read time out is:"+readTimeOut);
										        }//end of finally
										        
										        
										        responseStr = buffer.toString();
										           
												   if("Y".equalsIgnoreCase(iNTraceEnabled))
													   TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":IN Response Received",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] [Previously attempted URL =:"+triedURLList+"] [IN Response=:"+responseStr+"]");
												   
									            		
										           
										           if (log.isDebugEnabled())
										        	   log.debug(SENDREQTOIN, "responseStr::" + responseStr);
										           
										            String httpStatus = cs5URLConnection.getResponseCode();
										            requestMap.put("PROTOCOL_STATUS", httpStatus);
										            if(!CS5ClaroI.HTTP_STATUS_200.equalsIgnoreCase(httpStatus))
										                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
										            
										        
										        
										            if (InterfaceUtil.isNullString(responseStr))
										            {
										                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Blank response from CS5ClaroIN  NODE URL=["+cs5NodeVO.getUrl()+"]");
										                if((pAction==CS5ClaroI.ACTION_ACCOUNT_INFO || pAction==CS5ClaroI.ACTION_ACCOUNT_DETAILS) && "V".equals(requestMap.get(INTERFACEACTION)))
											            {
											            	TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":AIR Blank Response Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										                	requestMap.put(INTSTATUS,InterfaceErrorCodesI.ERROR_RESPONSE);	       
															throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
											            }else{
											          
											            	TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP,":AIR Blank Response Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
											            	requestMap.put(INTSTATUS,InterfaceErrorCodesI.AMBIGOUS);	       
															throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
											            }
											            
											    		
										            }
										            cs5Status.reintializeCouter(cs5NodeVO.getUrl());
										    			
										            //Parse the response string and get the response Map.
										            if(pAction==CS5ClaroI.ACTION_RECHARGE_CREDIT){
										            	responseMapC=cs5claroRequestFormatter.parseResponse(pAction,responseStr,responseMapC);
										            	if(!"Y".equalsIgnoreCase(iNTraceEnabled))
															   TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":IN Response Received",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] [Previously attempted URL =:"+triedURLList+"] [IN Response Map =:"+responseMapC+"]");
													}
										            else{
										            	responseMap=cs5claroRequestFormatter.parseResponse(pAction,responseStr,responseMap);
										            	if(!"Y".equalsIgnoreCase(iNTraceEnabled))
															   TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":IN Response Received",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl()+"] [Previously attempted URL =:"+triedURLList+"] [IN Response Map =:"+responseMap+"]");
													}
										            	
										            
										            
										            String faultCode="";
										            if(pAction==CS5ClaroI.ACTION_RECHARGE_CREDIT)
										            	faultCode = responseMapC.get("faultCode");
										            else
										             faultCode = responseMap.get("faultCode");
										            
										            if(!InterfaceUtil.isNullString(faultCode))
										            {
										                //Log the value of executionStatus for corresponding msisdn,recieved from the response.
										                log.info(SENDREQTOIN, "faultCode::"+faultCode +"inTXNID::"+inTXNID+" msisdn::"+msisdn);
										                requestMap.put(INTSTATUS, faultCode);//Put the interface_status in requestMap
										                if(pAction==CS5ClaroI.ACTION_RECHARGE_CREDIT)
										                	 log.error(SENDREQTOIN,"faultCode="+responseMapC.get("faultCode")+"faultString = "+responseMapC.get("faultString"));	
										                else
										                	log.error(SENDREQTOIN,"faultCode="+responseMap.get("faultCode")+"faultString = "+responseMap.get("faultString"));
										                
										                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,(String)responseMap.get("faultString"));
										               	throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
										            }
										            
										            
										            
										            responseCode="";
										            if(pAction==CS5ClaroI.ACTION_RECHARGE_CREDIT)
										            {
										            	responseCode = responseMapC.get("responseCode");}
										            else
										            	responseCode = responseMap.get("responseCode");
										            
										            System.out.println("VIKAS CHAUDHARY IN "+" responseCode "+responseCode+" pAction"+pAction);
										            requestMap.put(INTSTATUS,responseCode);
										            requestMap.put("CREDIT_INTERFACE_STATUS",responseCode);
										        
										        
										            if (pAction==CS5ClaroI.ACTION_ACCOUNT_INFO || pAction==CS5ClaroI.ACTION_ACCOUNT_DETAILS)
										            {
										            	String valRetryForResCode[];
										        		  boolean valRetryForResCodeFlag = false;
										        		  try {
										        			  valRetryForResCode=requestMap.get(CS5VALRESPONSECODEFORRETRIES).split(",");
										            		  for(int i=0;i<valRetryForResCode.length;i++){
										            			  if(!BTSLUtil.isNullString(valRetryForResCode[i]) && valRetryForResCode[i].equalsIgnoreCase(responseCode)){
										            				  valRetryForResCodeFlag = true;
										            				  break;
										            			  }
										            		  }
														} catch (Exception e) {
															valRetryForResCodeFlag = false;
															throw e;
														}
														if(valRetryForResCodeFlag){

															System.out.println("triedNodeNumberList = "+triedNodeNumberList+", triedNodeNumberList.size = "+triedNodeNumberList.size()+" , cs5NodeScheduler.getNodeTable().size() = "+cs5NodeScheduler.getNodeTable().size());
															if(triedNodeNumberList!= null && !(triedNodeNumberList.isEmpty()) &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
																retryRequired=false;
															}else{
																retryRequired=true;
															}
															
											            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
											                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
											                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_100_VAL_STAGE);
														}
										            }
									            //////////////////////////////
									            
									            
									            break;
									        }
								            catch(BTSLBaseException be)
								            {
								            	triedURLList.add(cs5NodeVO.getUrl());
								                triedNodeNumberList.add(""+cs5NodeVO.getNodeNumber());
								                cs5NodeVO.decrementConNumber(inTXNID);
								                
								                if(retryRequired){
								                	EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"BTSLBaseException for CS5ClaroIN with INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":BTSLBaseException ",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
						
									                
									                if (cs5Status.isFailCountReached(cs5NodeVO.getUrl(),requestMap)) {
														cs5NodeVO.setBlocked(true);
														cs5NodeVO.setBlokedAt(System.currentTimeMillis());
										                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"AIR Barred with INTERFACE_ID=["+interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl());
										                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":HTTP Connect Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: AIR Barred with INTERFACE_ID=["+interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl()+"] AIR Blocked Duration ="+cs5NodeVO.getExpiryDuration() +"]");
						
									                }
									                
									                requestMap.put(INTSTATUS,InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
									                if(triedNodeNumberList!= null && !(triedNodeNumberList.isEmpty()) &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
									            		EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"All AIR IP Tried for CS5ClaroIN with INTERFACE_ID=["+interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":ALL AIR IP Tried",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList+", BTSLBaseException = "+be.getMessage());
										                requestMap.put(INTSTATUS,InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
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
								                cs5NodeVO.decrementConNumber(inTXNID);
								                
								            	 if(retryRequired){
										                
										                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"Connect Time Out Exception for CS5ClaroIN with INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
										                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":HTTP Connect Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
							
										                
										                if (cs5Status.isFailCountReached(cs5NodeVO.getUrl(),requestMap)) {
															cs5NodeVO.setBlocked(true);
															cs5NodeVO.setBlokedAt(System.currentTimeMillis());
											                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"AIR Barred with INTERFACE_ID=["+interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl());
											                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":HTTP Connect Time Out Exception",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: AIR Barred with INTERFACE_ID=["+interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl()+"] AIR Blocked Duration ="+cs5NodeVO.getExpiryDuration() +"]");
							
										                }
										                requestMap.put(INTSTATUS,InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
										                if(triedNodeNumberList!= null && !(triedNodeNumberList.isEmpty()) &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
										            		EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"All AIR IP Tried for CS5ClaroIN with INTERFACE_ID=["+interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
											                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":ALL AIR IP Tried",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList+", SocketTimeoutException = "+ste.getMessage());
											                requestMap.put(INTSTATUS,InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
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
								                cs5NodeVO.decrementConNumber(inTXNID);
								                
								            	if(retryRequired){
									                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"Exception for CS5ClaroIN with INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
									                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,PretupsI.EXCEPTION,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] NODE URL =:"+cs5NodeVO.getUrl());
						
									                if (cs5Status.isFailCountReached(cs5NodeVO.getUrl(),requestMap)) {
														cs5NodeVO.setBlocked(true);
														cs5NodeVO.setBlokedAt(System.currentTimeMillis());
										                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"AIR Barred with INTERFACE_ID=["+interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =:"+cs5NodeVO.getUrl());
										                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,PretupsI.EXCEPTION,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: AIR Barred with INTERFACE_ID=["+interfaceID +"]and AIR Number=["+cs5NodeVO.getNodeNumber()+"] AIR URL =["+cs5NodeVO.getUrl() +"] AIR Blocked Duration ="+cs5NodeVO.getExpiryDuration() +"]");
						
									                }
									                 requestMap.put(INTSTATUS,InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
									                if(triedNodeNumberList!= null && !(triedNodeNumberList.isEmpty()) &&triedNodeNumberList.size()==cs5NodeScheduler.getNodeTable().size()){
									            		EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get(NETCODE),"All AIR IP Tried for CS5ClaroIN with INTERFACE_ID=["+interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList);
										                TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get(NETCODE),PretupsI.TXN_LOG_REQTYPE_REQ,stage,":ALL AIR IP Tried",PretupsI.TXN_LOG_STATUS_UNDERPROCESS," ::: [SENDER MSISDN] : "+requestMap.get(SENDERMSISDNMSG)+" ::: INTERFACE_ID=["+interfaceID +"]and Last Node Number=["+cs5NodeVO.getNodeNumber()+"] Last NODE URL =:"+cs5NodeVO.getUrl()+"] Previously attempted URL =:"+triedURLList+", Exception = "+e.getMessage());
										                requestMap.put(INTSTATUS,InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										                throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
									            	}
													continue;
								            	}else{
								                	throw e;
								                }
								            }finally{
								            	
								            	if(cs5NodeVO.getUrl() != null){
								            	
									            	requestMap.put("URL_PICKED", cs5NodeVO.getUrl());
										            if(pAction==CS5ClaroI.ACTION_RECHARGE_CREDIT) {
										            	requestMap.put("IP_CREDIT",cs5NodeVO.getUrl().substring(7, cs5NodeVO.getUrl().lastIndexOf(':')));
										            } else {
										            	requestMap.put("IP",cs5NodeVO.getUrl().substring(7, cs5NodeVO.getUrl().lastIndexOf(':')));
										            }
								            	}
								            }
								            
								            
								            
								            
								    
					            	} //end of for loop
						            
				          	
						          	try
							        {

		            if(!Arrays.asList(successList).contains(responseCode))
		            {
		            	
			            if(CS5ClaroI.SUBSCRIBER_NOT_FOUND.equals(responseCode))//102
			            {
			                log.error(SENDREQTOIN,"Subscriber not found with MSISDN::"+msisdn);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Subscriber is not found at IN");
			                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
			            }//end of checking the subscriber existance.
			            else if(CS5ClaroI.OLD_TRANSACTION_ID.equals(responseCode))//162
			            {
			            	
								if(log.isDebugEnabled())
									log.debug(SENDREQTOIN,"Transaction ID mismatch");
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn,requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Transaction ID mismatch");
								throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// recharge request with old transaction id								
									            	
			            }
			            else if(CS5ClaroI.OTHER_IN_EXCEPTION.equals(responseCode))//162
			            {
			            		if(log.isDebugEnabled())
			            			log.debug(SENDREQTOIN,"Other IN Exception 999");
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS5ClaroINHandler[sendRequestToIN]",inTXNID,msisdn,requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Other IN Exception 999");
								throw new BTSLBaseException(InterfaceErrorCodesI.OTHER_IN_EXCEPTION);// recharge request with old transaction id								
									            	
			            }
			            else if(Arrays.asList(errorList).contains(responseCode))//105,106,107,108,109,110,111,112,114,116,117,118,119,127,121,122,137,160,161,163,164,167,204,212,999
			            {
			            	
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			            }
			            else if (CS5ClaroI.ACCOUNT_BARRED_FROM_REFILL.equals(responseCode))//103
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn,  requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_BARRED_FROM_REFILL);
			            }
			            else if (CS5ClaroI.ACCOUNT_TEMPORARY_BLOCKED.equals(responseCode))//104
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_TEMPORARY_BLOCKED);
			            }
			            else if (CS5ClaroI.INVALID_PAYMENT_PROFILE.equals(responseCode))//120
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_PAYMENT_PROFILE);
			            }
			            else if (CS5ClaroI.MAX_CREDIT_LIMIT.equals(responseCode))//123
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_MAX_CREDIT_LIMIT);
			            }
			            else if (CS5ClaroI.SYSTEM_UNAVAILABLE.equals(responseCode))//125
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_SYSTEM_UNAVAILABLE);
			            }
			            else if (CS5ClaroI.DEDICATED_ACCOUNT_NOT_ACTIVE.equals(responseCode))//139
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_NOT_ACTIVE);
			            }
			            else if (CS5ClaroI.DATE_ADJUSTMENT_ISSUE.equals(responseCode))//136
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DATE_ADJUSTMENT_ISSUE);
			            }
			            else if (CS5ClaroI.ACC_MAX_CREDIT_LIMIT.equals(responseCode))//153
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACC_MAX_CREDIT_LIMIT);
			            }
			            else if (CS5ClaroI.VOUCHER_STATUS_PENDING.equals(responseCode))//113
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VOUCHER_STATUS_PENDING);
			            }
			            else if (CS5ClaroI.VOUCHER_GROUP_SERVICE_CLASS.equals(responseCode))//115
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VOUCHER_GROUP_SERVICE_CLASS);
			            }
			            else if (CS5ClaroI.BELOW_MIN_BAL.equals(responseCode))//124
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BELOW_MIN_BAL);
			            }
			            else  if (CS5ClaroI.ACCOUNT_NOT_ACTIVE.equals(responseCode))//126
			            {
			            	
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			               if(pAction	== CS5ClaroI.ACTION_RECHARGE_CREDIT)
			            	   throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			            }
			            else if("true".equalsIgnoreCase((String)requestMap.get(RESPONSECODE100)) && (requestMap.get(RESPONSECODE100HANDLING)).contains(responseCode)&& pAction==CS5ClaroI.ACTION_RECHARGE_CREDIT)
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                String previousbalance = requestMap.get(INTERFACEPREVBALANCE);
			            }
			            /*else if (CS5ClaroI.RESPONSE_CODE_100_HANDLING.equals(responseCode)&& (pAction==CS5ClaroI.ACTION_ACCOUNT_INFO || pAction==CS5ClaroI.ACTION_ACCOUNT_DETAILS))
			            {
			            	log.error(SENDREQTOIN,"Error code received from IN ::"+responseCode);
			                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"Error code received from IN "+responseCode);
			                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_100_VAL_STAGE);
			            }*/
			            
			            
		            }else{
		            	
		            	if ((!BTSLUtil.NullToString(requestMap.get("GATEWAY_TYPE")).equalsIgnoreCase(PretupsI.GATEWAY_TYPE_EXTGW))&& (pAction==CS5ClaroI.ACTION_ACCOUNT_INFO ||pAction==CS5ClaroI.ACTION_ACCOUNT_DETAILS)){
		            		if(Long.valueOf(responseMap.get("serviceClassCurrent")) >= Long.valueOf(requestMap.get(PREPAIDPOSTPAIDSERVICECLASSTHRESHOLDVALUE))){
		            			log.error(SENDREQTOIN,"service class threshold reached "+responseMap.get("serviceClassCurrent"));
				                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5ClaroINHandler[sendRequestToIN]",referenceID,msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction,"service class threshold reached "+responseMap.get("serviceClassCurrent"));
				                requestMap.put(TXNSTATUS,InterfaceErrorCodesI.FAIL);
				                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NOT_PREPAID_SERVICE_CLASS);
		            		}
		            		
		            	}
		            	
		            }
		        
		        }
		        catch(BTSLBaseException be)
		        {
		            throw be;
		        }//end of catch-BTSLBaseException
		        catch(Exception e)
		        {
		            log.errorTrace(PretupsI.EXCEPTION+methodName,e);
		            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		        }//end of catch-Exception
            
		      
        }
        catch(BTSLBaseException be)
        {
            log.error(SENDREQTOIN,"BTSLBaseException be::"+be.getMessage());
            throw be;
        }//end of catch-BTSLBaseException
        catch(Exception e)
        {
            log.errorTrace(PretupsI.EXCEPTION+methodName,e);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }//end of catch-Exception
        finally
        {
            try
            {
            	//Closing the HttpUrl connection
                if (cs5URLConnection != null)
                	cs5URLConnection.close();
                if(cs5NodeVO!=null)
                {
                    log.info(SENDREQTOIN,"Connection of Node ["+cs5NodeVO.getNodeNumber()+"] for INTERFACE_ID="+interfaceID+" is closed");
                    //Decrement the connection number for the current Node.
                    cs5NodeVO.decrementConNumber(inReconID);
                    log.info(SENDREQTOIN,"After closing the connection for Node ["+cs5NodeVO.getNodeNumber()+"] USED connections are ::["+cs5NodeVO.getConNumber()+"]");
                }
            }
            catch (Exception e)
            {
                log.errorTrace(PretupsI.EXCEPTION+methodName,e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[sendRequestToIN]",referenceID, msisdn, requestMap.get(NETCODE)+" INTERFACE ID = "+interfaceID+" Stage = "+pAction, "Not able to close connection:" + e.getMessage()+"INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
            }
            if (log.isDebugEnabled())
                log.debug(SENDREQTOIN, "Exiting  interfaceID::"+interfaceID+" Stage::"+pAction + " responseStr::" + responseStr);
        }//end of finally
    
    }//end of sendRequestToIN
    
    /**
    	 * This method would be used to adjust the validity of the subscriber account at the IN.
    	 * @param	HashMap pRequestMap
    	 * @throws	BTSLBaseException, Exception
    	 */   
    	@Override
    	public void validityAdjust(HashMap pRequestMap) throws BTSLBaseException, Exception 
    	{
			/*
			 * /
			 */
    	}//end of validityAdjust

    	/**
         * This method is used to set the interface parameters into requestMap, these parameters are as bellow
         * 1.Origin node type.
         * 2.Origin host type.
         * @throws Exception
         */
    	private void setInterfaceParameters() throws Exception,BTSLBaseException
	    {	       
    		final String methodName="setInterfaceParameters";
    		if(log.isDebugEnabled())
    			log.debug(methodName,PretupsI.ENTERED);
	        try
	        {        	
		        String cancelTxnAllowed = FileCache.getValue(interfaceID,requestMap.get(NETCODE)+"_"+CANCELTXNALLOWED);
		    	if(InterfaceUtil.isNullString(cancelTxnAllowed))
		    	{
		    	    log.error(methodName,"Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
		    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	requestMap.put(CANCELTXNALLOWED,cancelTxnAllowed.trim());
		    	
		    	String systemStatusMappingCr = FileCache.getValue(interfaceID,requestMap.get(NETCODE)+"_"+SYSTEMSTATUSMAPPINGCREDIT);
		    	if(InterfaceUtil.isNullString(systemStatusMappingCr))
		    	{
		    	    log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
		    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	requestMap.put(SYSTEMSTATUSMAPPINGCREDIT,systemStatusMappingCr.trim());
		    	
		    	String systemStatusMappingCrAdj = FileCache.getValue(interfaceID,requestMap.get(NETCODE)+"_"+"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
		    	if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
		    	{
		    	    log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
		    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());
		    	
		    	String systemStatusMappingDbtAdj = FileCache.getValue(interfaceID,requestMap.get(NETCODE)+"_"+SYSTEMSTATUSMAPPINGDEBITADJ);
		    	if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
		    	{
		    	    log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
		    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	requestMap.put(SYSTEMSTATUSMAPPINGDEBITADJ,systemStatusMappingDbtAdj.trim());
		    	
		    	String systemStatusMappingCrBck = FileCache.getValue(interfaceID,requestMap.get(NETCODE)+"_"+"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
		    	if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
		    	{
		    	    log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
		    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());
		    	
		    	String cancelCommandStatusMapping = FileCache.getValue(interfaceID,requestMap.get(NETCODE)+"_"+CANCELCOMMANDSTATUSMAPPING);
		    	if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
		    	{
		    	    log.error(methodName,"Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
		    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	requestMap.put(CANCELCOMMANDSTATUSMAPPING,cancelCommandStatusMapping.trim());
		    	
		    	
		    	String cancelNA = FileCache.getValue(interfaceID,requestMap.get(NETCODE)+"_"+CANCELNA);
		    	if(InterfaceUtil.isNullString(cancelNA))
		    	{
		    	    log.error(methodName,"Value of CANCEL_NA is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "CANCEL_NA is not defined in the INFile.");
		    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	requestMap.put(CANCELNA,cancelNA.trim());
		    	
		    	String warnTimeStr=FileCache.getValue(interfaceID, requestMap.get(NETCODE)+"_"+"WARN_TIMEOUT");
				if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, "Network code " + requestMap.get(NETCODE) , "WARN_TIMEOUT is not defined in IN File or not numeric");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());
				
				
				//required
				String nodeType=FileCache.getValue(interfaceID, requestMap.get(NETCODE)+"_"+"NODE_TYPE");
				if(InterfaceUtil.isNullString(nodeType))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "NODE_TYPE is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("NODE_TYPE",nodeType.trim());
				
				String hostName=FileCache.getValue(interfaceID,"HOST_NAME");
				if(InterfaceUtil.isNullString(hostName))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "HOST_NAME is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("HOST_NAME",hostName.trim());
				
				
				
				String currency=FileCache.getValue(interfaceID, requestMap.get(NETCODE)+"_"+"CURRENCY");
				if(InterfaceUtil.isNullString(currency))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "CURRENCY is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("CURRENCY",currency.trim());
				
				String refillAccountAfterFlag="";
				try {
					refillAccountAfterFlag=FileCache.getValue(interfaceID, requestMap.get(NETCODE)+"_"+"REFILL_ACNT_AFTER_FLAG");
				} catch (Exception e) {
					refillAccountAfterFlag = "0";
					throw e;
				}
				requestMap.put("REFILL_ACNT_AFTER_FLAG",refillAccountAfterFlag.trim());
				
				String originOperatorID=FileCache.getValue(interfaceID, requestMap.get(NETCODE)+"_"+"ORIGIN_OPERATOR_ID");
				if(InterfaceUtil.isNullString(originOperatorID))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "ORIGIN_OPERATOR_ID is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("ORIGIN_OPERATOR_ID",originOperatorID.trim());
				
				
				String intbarFlag=FileCache.getValue(interfaceID,CS5BARFLAG);
				if(InterfaceUtil.isNullString(intbarFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "CS5_BAR_FLAG is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put(CS5BARFLAG,intbarFlag.trim());
				
				String subsNoNAI=FileCache.getValue(interfaceID, requestMap.get(NETCODE)+"_"+"NAI");
				if(InterfaceUtil.isNullString(subsNoNAI)||!InterfaceUtil.isNumeric(subsNoNAI))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "SUBSCRIBER_NO_NAI is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("SubscriberNumberNAI",subsNoNAI.trim());
				
				String reponsecodeflag=FileCache.getValue(interfaceID, RESPONSECODE100);
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "RESPONSE_CODE_100 flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put(RESPONSECODE100,reponsecodeflag.trim());
				
				String messageCapabilityFlag=FileCache.getValue(interfaceID, "MESSAGE_CAPABILITY_FLAG");
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "MESSAGE_CAPABILITY_FLAG flag is not defined in IN File ");
				}
				requestMap.put("MESSAGE_CAPABILITY_FLAG",messageCapabilityFlag.trim());
				
				String promotionNotificationFlag=FileCache.getValue(interfaceID, "PROMOTION_NOTIFICATION_FLAG");
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "PROMOTION_NOTIFICATION_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("PROMOTION_NOTIFICATION_FLAG",promotionNotificationFlag.trim());
				
				String firstIVRCallSetFlag=FileCache.getValue(interfaceID, "FIRST_IVR_CALL_SET_FLAG");
				if(InterfaceUtil.isNullString(reponsecodeflag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "FIRST_IVR_CALL_SET_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("FIRST_IVR_CALL_SET_FLAG",firstIVRCallSetFlag.trim());
				
				String responseCode100handled=FileCache.getValue(interfaceID, RESPONSECODE100HANDLING);
				if(InterfaceUtil.isNullString(responseCode100handled))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "RESPONSE_CODE_100_HANDLING String is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put(RESPONSECODE100HANDLING,responseCode100handled.trim());
				
				String accountActivationFlag=FileCache.getValue(interfaceID, "ACCOUNT_ACTIVATION_FLAG");
				if(InterfaceUtil.isNullString(accountActivationFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "ACCOUNT_ACTIVATION_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("ACCOUNT_ACTIVATION_FLAG",accountActivationFlag.trim());
				
				String daSelectionFlagManual=FileCache.getValue(interfaceID, "DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL");
				if(InterfaceUtil.isNullString(daSelectionFlagManual))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("DEDICATED_ACCOUNT_SELECTION_FLAG_MANUAL",daSelectionFlagManual.trim());
				
				String daSelectedAccID=FileCache.getValue(interfaceID, "DEDICATED_ACCOUNT_SELECTION_ACCOUNTID");
				if(InterfaceUtil.isNullString(accountActivationFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_ACCOUNT_SELECTION_ACCOUNTID flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("DEDICATED_ACCOUNT_SELECTION_ACCOUNTID",daSelectedAccID.trim());
				
				String daFirstSelectionFlag=FileCache.getValue(interfaceID, "DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE");
				if(InterfaceUtil.isNullString(daFirstSelectionFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("DEDICATED_ACCOUNT_SELECTION_FLAG_RANGE",daFirstSelectionFlag.trim());
				
				String daDefaultFirstRange=FileCache.getValue(interfaceID, "DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE");
				if(InterfaceUtil.isNullString(daDefaultFirstRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("DEDICATED_DEFAULT_ACCOUNT_ID_FIRST_RANGE",daDefaultFirstRange.trim());
				String daDefaultLastRange=FileCache.getValue(interfaceID, "DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE");
				if(InterfaceUtil.isNullString(daDefaultLastRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE flag is not defined in IN File ");
				   throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("DEDICATED_DEFAULT_ACCOUNT_ID_LAST_RANGE",daDefaultLastRange.trim());
				String daFirstRange=FileCache.getValue(interfaceID, "DEDICATED_ACCOUNT_ID_FIRST_RANGE");
				if(InterfaceUtil.isNullString(daFirstRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_ACCOUNT_ID_FIRST_RANGE flag is not defined in IN File ");
				   throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("DEDICATED_ACCOUNT_ID_FIRST_RANGE",daFirstRange.trim());
				String daLastRange=FileCache.getValue(interfaceID, "DEDICATED_ACCOUNT_ID_LAST_RANGE");
				if(InterfaceUtil.isNullString(daLastRange))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_ACCOUNT_ID_LAST_RANGE flag is not defined in IN File ");
				   throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("DEDICATED_ACCOUNT_ID_LAST_RANGE",daLastRange);
				
				String ucipINdisabledFlag=FileCache.getValue(interfaceID, "IN_RESPONSE_PRINT_FLAG");
				if(InterfaceUtil.isNullString(ucipINdisabledFlag))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "IN_RESPONSE_PRINT_FLAG flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put("UCIP_IN_DISABLED_FLAG",ucipINdisabledFlag.trim());
				
				String daPostpaid=FileCache.getValue(interfaceID, DEDICATEDACCOUNTFORPOSTPAID);
				if(InterfaceUtil.isNullString(daPostpaid))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",REFID+referenceID+MSISDNMSG+msisdn," INTERFACE ID = "+interfaceID, (String) requestMap.get(NETCODE) , "DEDICATED_ACCOUNT_FOR_POSTPAID flag is not defined in IN File ");
				    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
				}
				requestMap.put(DEDICATEDACCOUNTFORPOSTPAID,daPostpaid.trim());
				
				
				 String cs5MaxFailAirCount = FileCache.getValue(interfaceID,"CS5_MAX_Fail_AIR_COUNT");

			    	if(InterfaceUtil.isNullString(cs5MaxFailAirCount))
			    	{
			    	    log.error(methodName,"Value of CS5_MAX_Fail_AIR_COUNT is not defined in the INFile");
			    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "CS5_MAX_Fail_AIR_COUNT is not defined in the INFile.");
			    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			    	}
			    	requestMap.put("CS5_MAX_Fail_AIR_COUNT",cs5MaxFailAirCount.trim());
			    	
			    	String cs5ValResponseCodeForRetries = FileCache.getValue(interfaceID,CS5VALRESPONSECODEFORRETRIES);		
			    	if(InterfaceUtil.isNullString(cs5ValResponseCodeForRetries))
			    	{
			    	    log.error(methodName,"Value of CS5_VAL_RESPONSE_CODE_FOR_RETRIES is not defined in the INFile");
			    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "CS5_VAL_RESPONSE_CODE_FOR_RETRIES is not defined in the INFile.");
			    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			    	}
			    	requestMap.put(CS5VALRESPONSECODEFORRETRIES,cs5ValResponseCodeForRetries.trim());
			    	
			    	
			    	
			    	String prepaidPostpaidServiceCassThresholdValue = FileCache.getValue(interfaceID,PREPAIDPOSTPAIDSERVICECLASSTHRESHOLDVALUE);		
			    	if(InterfaceUtil.isNullString(prepaidPostpaidServiceCassThresholdValue))
			    	{
			    	    log.error(methodName,"Value of PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE is not defined in the INFile");
			    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[setInterfaceParameters]",referenceID,INTID+interfaceID+" MSISDN "+msisdn , (String) requestMap.get(NETCODE), "PREPAID_POSTPAID_SERVICE_CLASS_THRESHOLD_VALUE is not defined in the INFile.");
			    		throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			    	}
			    	requestMap.put(PREPAIDPOSTPAIDSERVICECLASSTHRESHOLDVALUE,prepaidPostpaidServiceCassThresholdValue.trim());
			    	
				
	        }//end of try block
	        catch(BTSLBaseException be)
			{
	        	throw be;
			}
	        catch(Exception e)
	        {
	            log.error(methodName,PretupsI.EXCEPTION+e.getMessage());
	            throw e;
	        }//end of catch-Exception
	        finally
	        {
	            if (log.isDebugEnabled())
	            	log.debug(methodName,PretupsI.EXITED+" requestMap:" + requestMap);
	        }//end of finally
	    }//end of setInterfaceParameters
    	
    	
      
        
    	/**
    	 * Method to send cancel request to IN for any ambiguous transaction.
    	 * This method also makes reconciliation log entry. 	
    	 * @throws	BTSLBaseException 
    	 */
        private void handleCancelTransaction() throws BTSLBaseException
        {
        	final String methodName = "handleCancelTransaction";
        	if (log.isDebugEnabled())
        		log.debug("handleCancelTransaction",PretupsI.ENTERED);
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
    			requestMap.put("REMARK1",FileCache.getValue(interfaceID,"REMARK1"));
    			requestMap.put("REMARK2",FileCache.getValue(interfaceID,"REMARK2"));
    			//get reconciliation log object associated with interface
    		    reconLog = ReconcialiationLog.getLogObject(interfaceID);		    
    		    if (log.isDebugEnabled())
    		    	log.debug("handleCancelTransaction", "reconLog."+reconLog);
    		    cancelTxnAllowed=requestMap.get(CANCELTXNALLOWED);
    		    //if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
    		    //into recon log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
    			if("N".equals(cancelTxnAllowed))
    			{
    				cancelNA=requestMap.get(CANCELNA);//Cancel command status as NA.
    				cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(requestMap,cancelNA,CANCELCOMMANDSTATUSMAPPING);
    				requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);				
    				interfaceStatus=requestMap.get(INTSTATUS);
    				systemStatusMapping=requestMap.get(SYSTEMSTATUSMAPPING);				
    				cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING
    				requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
    				reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(requestMap);
    				reconLog.info("",reconciliationLogStr);
    				if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
    					throw new BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.	??????)    			
    				requestMap.put(TXNSTATUS,InterfaceErrorCodesI.SUCCESS);	
    				//added to discard amount field from the message.
    				requestMap.put(POSTBALANCEENQSUCCESS, "N");
    			}  			
    		}
    		catch(BTSLBaseException be)
    		{
    			throw be;
    		}
    		catch(Exception e)
    		{
    			log.errorTrace(PretupsI.EXCEPTION+methodName,e);
    			throw new BTSLBaseException(this,"handleCancelTransaction",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    		}
    		finally
    		{
    			 if (log.isDebugEnabled())
    				 log.debug("handleCancelTransaction",PretupsI.EXITED);
    		}
        }
        
        
        protected static synchronized String getINTransactionID(Map pRequestMap) throws BTSLBaseException
    	{
    		String methodName="getINTransactionID";
        	String instanceID=null;
    		int maxCounter=9999;
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
    				
    			userType = (String)pRequestMap.get(USERTYPES);
    			if("S".equals(userType))
    				userType="3";
    			else if("R".equals(userType))	
    				userType="2";
				else 
    				userType="1";
    			instanceID = FileCache.getValue((String)pRequestMap.get(INTERFACEIDS),"INSTANCE_ID");
    			if(InterfaceUtil.isNullString(instanceID))
    			{
    				log.error(methodName,"Parameter INSTANCE_ID is not defined in the INFile");
    				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5ClaroINHandler[validate]","","" , (String) pRequestMap.get(NETCODE), "Instance id[INSTANCE_ID] is not defined in IN File");
    				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    			}
    			mydate = new Date();	
    			dateStr = sdf.format(mydate);
    			minut2Compare = dateStr.substring(8,10);
    			int currentMinut=Integer.parseInt(minut2Compare);  
    			if(currentMinut !=prevMinut)
    			{
    				transactionIDCounter=1;
    				prevMinut=currentMinut;
    			}
    			else if(transactionIDCounter > maxCounter)
    				transactionIDCounter=1;
    			else
    				transactionIDCounter++;
    			String txnid =String.valueOf(transactionIDCounter);
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
    			throw e;
    		}
    		finally
    		{
    			/*
    			 *
    			 */
    		}
    		return transactionId;		
    	}
}
