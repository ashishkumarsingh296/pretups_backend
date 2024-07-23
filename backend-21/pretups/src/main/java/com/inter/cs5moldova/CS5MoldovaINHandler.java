package com.inter.cs5moldova;

/**
 * @(#)CS5MoldovaINHandler.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Zeeshan Aleem        Jul 08, 2017		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.PrintWriter;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.inter.cs5moldova.cs5scheduler.NodeCloser;
import com.inter.cs5moldova.cs5scheduler.NodeManager;
import com.inter.cs5moldova.cs5scheduler.NodeScheduler;
import com.inter.cs5moldova.cs5scheduler.NodeVO;
import com.btsl.pretups.logging.TransactionLog;

public class CS5MoldovaINHandler implements InterfaceHandler
{
	private static Log log = LogFactory.getLog(CS5MoldovaINHandler.class.getName());

	private HashMap<String,String> requestMap = null;//Contains the response of the request as key and value pair.
	private HashMap<String,String> responseMap = null;//Contains the request parameter as key and value pair.
	private String interfaceID=null;//Contains the interfaceID
	private String inTXNID=null;//UNodesed to represent the Transaction ID
	private String msisdn=null;//Used to store the MSISDN
	private String referenceID=null;
	private String  userType=null;
	private static  SimpleDateFormat sdf = new SimpleDateFormat ("yyMMddHHmm");
	private static int transactionIDCounter=0;
	private static int prevMinut=0;
	private static CS5MoldovaRequestFormatter cs5MoldovaRequestFormatter=null;
	private int ambgMaxRetryCount=0;
	private int ambgCurrentRetryCount=0;
	private boolean isRetryRequest=false;
	private NodeCloser nodeCloser =null;

	static
	{
		if(log.isDebugEnabled()) log.debug("CS5MoldovaINHandler[static]","Entered");
		try
		{
			cs5MoldovaRequestFormatter = new CS5MoldovaRequestFormatter();
		}
		catch(Exception e)
		{
			log.error("CS5MoldovaINHandler[static]","While instantiation of CS5MoldovaRequestFormatter get Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[static]","","", "","While instantiation of CS5MoldovaRequestFormatter get Exception e::"+e.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled()) log.debug("CS5MobililINHandler[static]","Exited");
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
		final String methodName = "validate";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered p_requestMap:"+p_requestMap);
		requestMap = p_requestMap;
		String selectorValue="";
		try
		{
			interfaceID=requestMap.get("INTERFACE_ID");
			msisdn=requestMap.get("MSISDN"); 		
			selectorValue=requestMap.get("CARD_GROUP_SELECTOR");
			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.
			String validateRequired = FileCache.getValue(interfaceID,requestMap.get("REQ_SERVICE")+"_"+requestMap.get("USER_TYPE"));
			if("N".equals(validateRequired))
			{
				requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				return ;
			}
			inTXNID=getINTransactionID(requestMap);	
			requestMap.put("IN_RECON_ID",inTXNID);
			requestMap.put("IN_TXN_ID",inTXNID);
			referenceID=requestMap.get("TRANSACTION_ID");
			String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
			if(log.isDebugEnabled()) log.debug(methodName,"multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[validate]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor=multFactor.trim();
			//Set the interface parameters into requestMap
			setInterfaceParameters();
			//key value of requestMap is formatted into XML string for the validate request.
			String inStr = cs5MoldovaRequestFormatter.generateRequest(CS5MoldovaI.ACTION_ACCOUNT_INFO,requestMap);
			//sending the AccountInfo request to IN along with validate action defined in CS5MoldovaI interface
			sendRequestToIN(inStr,CS5MoldovaI.ACTION_ACCOUNT_INFO);
			//set TRANSACTION_STATUS as Success in request map
			requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			//get value of accountValue1 from response map (accountValue1 was set in response map in sendRequestToIN method.)
			String amountStr=(String)responseMap.get("accountValue1");
			try
			{
				if(!InterfaceUtil.isNullString(selectorValue) && Integer.parseInt(selectorValue)>3 && "S".equals((String)requestMap.get("USER_TYPE")))
				{
					if(!InterfaceUtil.isNullString(selectorValue))
						amountStr=getDedicatedAccountValue(responseMap,selectorValue);
				}
				amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
				requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
				//temporary to run CS5 Moldova as postpaid interface
				requestMap.put("BILL_AMOUNT_BAL","0");
			}
			catch(Exception e)
			{
				log.errorTrace(methodName, e);
				log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[validate]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:"+e.getMessage());
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
			}
			if("1".equals(responseMap.get("temporaryBlockedFlag")))
			{
				requestMap.put("ACCOUNT_STATUS","BARRED");
			}
			Date supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String)responseMap.get("supervisionExpiryDate"), "yyyyMMdd");
			Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String)responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");
			Date currentDate = new Date();
			if(supervisionExpiryDate.after(currentDate))
				requestMap.put("ACCOUNT_STATUS","ACTIVE");
			else if((currentDate.after(supervisionExpiryDate)) && (serviceFeeExpiryDate.after(currentDate)))
				requestMap.put("ACCOUNT_STATUS","INACTIVE");
			else if(currentDate.after(serviceFeeExpiryDate))
				requestMap.put("ACCOUNT_STATUS","DEACTIVE");
			requestMap.put("SERVICE_CLASS", (String)responseMap.get("serviceClassCurrent"));
			//set OLD_EXPIRY_DATE in request map as returned from responseMap.
			requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) responseMap.get("supervisionExpiryDate")).trim(), "yyyyMMdd"));
			requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) responseMap.get("serviceFeeExpiryDate")).trim(), "yyyyMMdd"));
			//set the mapping language of our system from FileCache mapping based on the responsed language.
			setLanguageFromMapping();
			int requestAmount=0;
			if(requestMap.get("REQ_AMOUNT")!=null)
				requestAmount=Integer.parseInt((String)requestMap.get("REQ_AMOUNT"));
			int interfacePrevAmount=Integer.parseInt(amountStr);
			// if account status in INACTIVE or requestAmount>interfacePrevAmount then generating Account Details 
			// request to check if subscriber is LDCC subscriber or not.
			if((requestMap.get("ACCOUNT_STATUS").equals("INACTIVE") || requestAmount>interfacePrevAmount ) && ("S".equals(requestMap.get("USER_TYPE"))))	
			{
				inStr= cs5MoldovaRequestFormatter.generateRequest(CS5MoldovaI.ACTION_ACCOUNT_DETAILS,requestMap);
				ambgCurrentRetryCount--;
				sendRequestToIN(inStr,CS5MoldovaI.ACTION_ACCOUNT_DETAILS);
				if(CS5MoldovaI.LDCC_SERVICE_OFFERING_ACT_FLAG.equals(responseMap.get("serviceOfferingActiveFlag")))
				{
					// if ldcc then it should go to post-paid IN from controller.
					//setting MSISDN_NOT_FOUND .so that it will go to 
					requestMap.put("SERVICE_CLASS","LDCC");
					requestMap.put("INTERFACE_STATUS", CS5MoldovaI.SUBSCRIBER_NOT_FOUND);
					requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
					throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
				}
			}
			requestMap.put("DEDICATED_ACC_ID", (String) responseMap.get("DEDICATED_ACC_ID"));
			requestMap.put("DEDICATED_ACC_VALUE", (String) responseMap.get("DEDICATED_ACC_VALUE"));
			requestMap.put("DEDICATED_ACC_EXPIRY", (String) responseMap.get("DEDICATED_ACC_EXPIRY"));
		}
		catch (BTSLBaseException be)
		{
			log.error(methodName,"BTSLBaseException be="+be.getMessage());
			throw be; 	   	
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[validate]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String)requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug("validate","Exiting with  requestMap: "+requestMap);
		}		 
	}

	/**
	 * Implements the logic that credit the subscriber account on IN.
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception  
	 */
	public void credit(HashMap<String,String> requestMapParam) throws BTSLBaseException, Exception 
	{
		final String methodName = "credit";
		if (log.isDebugEnabled()) log.debug(methodName,"Entered p_requestMap: " + requestMapParam);
		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
		requestMap = requestMapParam;
		String transactionCode=null;
		String transactionType=null;
		try
		{
			interfaceID=requestMap.get("INTERFACE_ID");
			transactionCode=FileCache.getValue(interfaceID,"TRANSACTION_CODE_RF");
			transactionType=FileCache.getValue(interfaceID,"TRANSACTION_TYPE_RF");
			inTXNID=getINTransactionID(requestMap);
			requestMap.put("IN_RECON_ID",inTXNID);
			requestMap.put("IN_TXN_ID",inTXNID);
			referenceID = requestMap.get("TRANSACTION_ID");
			msisdn=requestMap.get("MSISDN");
			userType=requestMap.get("USER_TYPE");
			if(!InterfaceUtil.isNullString(transactionCode))
				requestMap.put("TRANSACTION_CODE_RF",transactionCode);

			if(!InterfaceUtil.isNullString(transactionType))
				requestMap.put("TRANSACTION_TYPE_RF",transactionType);
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
			if(log.isDebugEnabled())log.debug(methodName,"multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[credit]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			requestMap.put("MULT_FACTOR",multFactor);
			setInterfaceParameters();//Set the interface parameters into requestMap
			requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");

			try
			{
				multFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(interfaceID,"ROUND_FLAG");
				if(log.isDebugEnabled()) log.debug(methodName,"From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5MobililINHandler[credit]",referenceID+"MSISDN = "+msisdn ," INTERFACE ID = "+interfaceID, "Network code = "+(String) requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
					amountStr=String.valueOf(Math.round(systemAmtDouble));
					requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
				}
			}
			catch(Exception e)
			{
				log.errorTrace(methodName, e);
				log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[credit]","REFERENCE ID = "+referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(log.isDebugEnabled()) 
				log.debug(methodName,"transfer_amount:"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			requestMap.put("transfer_amount",amountStr);
			//key value of requestMap is formatted into XML string for the validate request.
			String inStr = cs5MoldovaRequestFormatter.generateRequest(CS5MoldovaI.ACTION_RECHARGE_CREDIT,requestMap);
			try
			{
				ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(interfaceID,"CREDIT_RETRY_CNT"));
			}
			catch(Exception e)
			{
				ambgMaxRetryCount=1;
			}
			//sending the Re-charge request to IN along with re-charge action defined in CS5MobililI interface
			sendRequestToIN(inStr,CS5MoldovaI.ACTION_RECHARGE_CREDIT);
			//set TRANSACTION_STATUS as Success in request map
			requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			// set NEW_EXPIRY_DATE into request map
			if(!isRetryRequest)
			{
				requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) responseMap.get("supervisionExpiryDate"), "yyyyMMdd"));
				requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd"));
				//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
				try
				{
					String postBalanceStr = (String) responseMap.get("accountValue1");
					postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
					requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
				}
				catch(Exception e)
				{
					log.errorTrace(methodName, e);
					log.error(methodName,"Exception e:"+e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[credit]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
					throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
				}
				requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
			}
			else
				requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
		} 
		catch (BTSLBaseException be)
		{
			requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
				log.errorTrace(methodName, e);
				log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[credit]", referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
		catch (Exception e)
		{
			log.errorTrace(methodName, e);
			log.error(methodName, "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[credit]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (log.isDebugEnabled()) 
				log.debug(methodName, "Exited requestMap=" + requestMap);
		}
	}

	/**
	 * This method is used to adjust the following
	 * 1.Amount
	 * 2.ValidityDays
	 * 3.GraceDays
	 */
	public void creditAdjust(HashMap<String,String> requestMapParam) throws BTSLBaseException, Exception
	{
		final String methodName = "creditAdjust";
		if (log.isDebugEnabled()) log.debug(methodName," Entered p_requestMap:" + requestMapParam);
		requestMap = requestMapParam;//assign map passed from InterfaceModule to requestMap(instance var) 
		double systemAmtDouble =0;
		String amountStr=null;
		int validityDays=0;//Defines the validity days by which adjustment to be made.
		int graceDays=0;//Defines the grace period by which adjustment to be made.
		String transactionCode=null;
		String transactionType=null;
		int selectorCode;
		int action;
		try
		{
			userType=requestMap.get("USER_TYPE");
			interfaceID=requestMap.get("INTERFACE_ID");// get interface id form request map
			transactionCode=FileCache.getValue(interfaceID,"TRANSACTION_CODE_CR");
			transactionType=FileCache.getValue(interfaceID,"TRANSACTION_TYPE_CR");
			inTXNID=getINTransactionID(requestMap);//Generate the IN transaction id and set in requestMap
			requestMap.put("IN_RECON_ID",inTXNID);
			requestMap.put("IN_TXN_ID",inTXNID);
			referenceID=(String)requestMap.get("TRANSACTION_ID");
			msisdn=requestMap.get("MSISDN");//get MSISDN from request map
			//add transaction code and transaction type in request map
			if(!InterfaceUtil.isNullString(transactionCode))
				requestMap.put("TRANSACTION_CODE_CR",transactionCode);
			if(!InterfaceUtil.isNullString(transactionType))
				requestMap.put("TRANSACTION_TYPE_CR",transactionType);
			//Get the Multiplication factor from the FileCache with the help of interface id.
			String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[creditAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			//Set the interface parameters into requestMap
			setInterfaceParameters();
			if("R".equals(userType))
				requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
			else if("S".equals(userType))
				requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT_BCK");
			try
			{
				double huaweiMultFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,huaweiMultFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(interfaceID,"ROUND_FLAG");
				if(log.isDebugEnabled()) 
					log.debug(methodName,"From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5MobililINHandler[creditAdjust]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn ," INTERFACE ID = "+interfaceID, "Network code = "+(String) requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
					amountStr=String.valueOf(Math.round(systemAmtDouble));
					requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
				}
			}
			catch(Exception e)
			{
				log.errorTrace(methodName,e);
				log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[creditAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(log.isDebugEnabled()) log.debug(methodName,"transfer_amount:"+amountStr+" multFactor:"+multFactor);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			requestMap.put("transfer_amount",amountStr);
			//Check for the validity Adjustment-VALIDITY_DAYS
			if(!InterfaceUtil.isNullString((String)requestMap.get("VALIDITY_DAYS")))
			{
				try
				{
					//For the adjust ment make validity days as negative.
					validityDays =Integer.parseInt(((String)requestMap.get("VALIDITY_DAYS")).trim());
					if(log.isDebugEnabled()) 
						log.debug(methodName,"validityDays::"+validityDays);
					//Set the validity days into request map with key as 'relative_date_adjustment_service_fee'
					requestMap.put("supervisionExpiryDateRelative",String.valueOf(validityDays));
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,"Exception e::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
			}
			//Check for the grace Adjustment-GRACE_DAYS
			if(!InterfaceUtil.isNullString((String)requestMap.get("GRACE_DAYS")))
			{
				try
				{
					//For the adjustment make grace days as negative.
					graceDays =Integer.parseInt(((String)requestMap.get("GRACE_DAYS")).trim());
					if(log.isDebugEnabled()) 
						log.debug(methodName,"graceDays::"+graceDays);
					//Set the grace days into request map with key as 'relative_date_adjustment_supervision'
					requestMap.put("serviceFeeExpiryDateRelative",String.valueOf(graceDays));
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,"Exception e::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
			}//end of checking graceAdjustment

			try
			{
				ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(interfaceID,"ADJ_RETRY_CNT"));
			}
			catch(Exception e)
			{
				ambgMaxRetryCount=1;
			}
			//Based on the selector, request will be formed
			selectorCode=Integer.valueOf((String)requestMap.get("CARD_GROUP_SELECTOR"));
			String inStr =null;
			if(selectorCode<=3)
				action=CS5MoldovaI.ACTION_IMMEDIATE_DEBIT;
			else
				action=CS5MoldovaI.ACTION_DEDICATED_ACCOUNT_CD;
			inStr = cs5MoldovaRequestFormatter.generateRequest(action,requestMap);
			//sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS5MobililI interface
			sendRequestToIN(inStr,action);
			//set TRANSACTION_STATUS as Success in request map
			requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
			requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N"); 
		} 
		catch (BTSLBaseException be)
		{
			requestMapParam.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
				log.errorTrace(methodName,e);
				log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[creditAdjust]", referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
		catch (Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(methodName, "Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[creditAdjust]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (log.isDebugEnabled()) log.debug(methodName, "Exited requestMap=" + requestMap);
		}
	}

	/**
	 * Implements the logic that debit the subscriber account on IN.
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */
	public void debitAdjust(HashMap<String,String> requestMapParam) throws BTSLBaseException, Exception
	{
		final String methodName = "debitAdjust"; 
		if (log.isDebugEnabled()) log.debug(methodName," Entered p_requestMap:" + requestMapParam);
		requestMap = requestMapParam;//assign map passed from InterfaceModule to requestMap(instance var) 
		double systemAmtDouble =0;
		String amountStr=null;
		int validityDays=0;//Defines the validity days by which adjustment to be made.
		int graceDays=0;//Defines the grace period by which adjustment to be made.
		String transactionCode=null;
		String transactionType=null;
		int selectorCode;
		int action;
		try
		{
			userType=requestMap.get("USER_TYPE");
			interfaceID=requestMap.get("INTERFACE_ID");// get interface id form request map
			transactionCode=FileCache.getValue(interfaceID,"TRANSACTION_CODE_DR");
			transactionType=FileCache.getValue(interfaceID,"TRANSACTION_TYPE_DR");
			inTXNID=getINTransactionID(requestMap);//Generate the IN transaction id and set in requestMap
			requestMap.put("IN_TXN_ID",inTXNID);//get TRANSACTION_ID from request map (which has been passed by controller)
			requestMap.put("IN_RECON_ID",inTXNID);
			referenceID=requestMap.get("TRANSACTION_ID");
			msisdn=requestMap.get("MSISDN");//get MSISDN from request map
			//	add transaction code and transaction type in request map
			if(!InterfaceUtil.isNullString(transactionCode))
				requestMap.put("TRANSACTION_CODE_DR",transactionCode);
			if(!InterfaceUtil.isNullString(transactionType))
				requestMap.put("TRANSACTION_TYPE_DR",transactionType);
			//Get the Multiplication factor from the FileCache with the help of interface id.
			String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[debitAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			//Set the interface parameters into requestMap
			setInterfaceParameters();
			requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			try
			{
				double huaweiMultFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,huaweiMultFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(interfaceID,"ROUND_FLAG");
				if(log.isDebugEnabled()) log.debug(methodName,"From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5MobililINHandler[debitAdjust]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn ," INTERFACE ID = "+interfaceID, "Network code = "+(String) requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
					amountStr=String.valueOf(Math.round(systemAmtDouble));
					requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
				}
			}
			catch(Exception e)
			{
				log.errorTrace(methodName,e);
				log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[debitAdjust]",referenceID,msisdn +" INTERFACE ID = "+interfaceID, (String) requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(log.isDebugEnabled()) log.debug(methodName,"transfer_amount:"+amountStr+" multFactor:"+multFactor);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			requestMap.put("transfer_amount","-"+amountStr);
			//Check for the validity Adjustment-VALIDITY_DAYS
			if(!InterfaceUtil.isNullString((String)requestMap.get("VALIDITY_DAYS")))
			{
				try
				{
					//For the adjustment make validity days as negative.
					validityDays =Integer.parseInt(((String)requestMap.get("VALIDITY_DAYS")).trim());
					if(log.isDebugEnabled()) log.debug(methodName,"validityDays::"+validityDays);
					//Set the validity days into request map with key as 'relative_date_adjustment_service_fee'
					requestMap.put("supervisionExpiryDateRelative","-"+String.valueOf(validityDays));
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,"Exception e::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
			}
			//Check for the grace Adjustment-GRACE_DAYS
			if(!InterfaceUtil.isNullString((String)requestMap.get("GRACE_DAYS")))
			{
				try
				{
					//For the adjustment make grace days as negative.
					graceDays =Integer.parseInt((requestMap.get("GRACE_DAYS")).trim());
					if(log.isDebugEnabled()) log.debug(methodName,"graceDays::"+graceDays);
					//Set the grace days into request map with key as 'relative_date_adjustment_supervision'
					requestMap.put("serviceFeeExpiryDateRelative","-"+String.valueOf(graceDays));
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,"Exception e::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
			}
			selectorCode=Integer.valueOf((String)requestMap.get("CARD_GROUP_SELECTOR"));
			if(selectorCode<=3)
				action=CS5MoldovaI.ACTION_IMMEDIATE_DEBIT;
			else
				action=CS5MoldovaI.ACTION_DEDICATED_ACCOUNT_CD;
			String inStr = cs5MoldovaRequestFormatter.generateRequest(action,requestMap);

			try
			{
				ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(interfaceID,"ADJ_RETRY_CNT"));
			}
			catch(Exception e)
			{
				ambgMaxRetryCount=1;
			}
			//sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS5MobililI interface
			sendRequestToIN(inStr,action);
			//set TRANSACTION_STATUS as Success in request map
			requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
			requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N"); 
		} 
		catch (BTSLBaseException be)
		{
			requestMapParam.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
				log.errorTrace(methodName,e);
				log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[debitAdjust]", referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
		catch (Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(methodName, "Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[debitAdjust]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (log.isDebugEnabled()) log.debug(methodName, "Exited requestMap=" + requestMap);
		}
	}

	/**
	 * This method is used to send the request to IN and stored the response after parsing.
	 * This method also take care about to handle the error situation to send the alarm and set the error code.
	 * 1.Invoke the getNodeVO method of NodeScheduler class and pass the Transaction Id.
	 * 2.If the VO is Null then mark the request as fail and throw exception(New Error code that defines No connection for any Node is available).
	 * 3.If the VO is not NULL then pass the Node detail to CS5UrlConnection class and get connection.
	 * 4.After the processing the request(may be successful or fail) decrement the connection counter and pass the
	 * transaction id that is removed from the transNodeList.
	 * @param	String inRequestStr
	 * @param	int action
	 * @throws	BTSLBaseException
	 */
	private void sendRequestToIN(String inRequestStr, int action) throws BTSLBaseException
	{
		final String methodName = "sendRequestToIN";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered msisdn="+msisdn+" action="+action+" inRequestStr="+inRequestStr);
		TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get("NETWORK_CODE"),String.valueOf(action),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string::"+inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action::"+action);
		String responseStr = "";
		NodeVO cs5NodeVO=null;
		NodeScheduler cs5NodeScheduler=null;
		CS5MoldovaUrlConnection cs5URLConnection = null;		
		long startTime=0;
		long endTime=0;
		int conRetryNumber=0;
		long warnTime=0;
		int readTimeOut=0;
		String inReconID=null;
		long retrySleepTime=0;
		try
		{
			if(action!=CS5MoldovaI.ACTION_ACCOUNT_DETAILS)
				responseMap = new HashMap<>();
			inReconID=requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=inTXNID;
			retrySleepTime = Long.parseLong(FileCache.getValue(interfaceID,"RETRY_SLEEP_TIME"));
			//Get the instance of NodeScheduler based on interfaceId.
			cs5NodeScheduler = NodeManager.getScheduler(interfaceID);
			//Get the retry number from the object that is used to retry the getNode in case connection is failed.
			conRetryNumber = cs5NodeScheduler.getRetryNum();
			//Host name and userAgent may be set into the VO corresponding to each Node for authentication-CONFIRM, if it is not releted with the request xml.
			String hostName = cs5NodeScheduler.getHeaderHostName();
			String userAgent = cs5NodeScheduler.getUserAgent();
			//check if CS5NodeScheduler is null throw exception.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
			if(cs5NodeScheduler==null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT);
			while(ambgCurrentRetryCount++ <=ambgMaxRetryCount)
			{
				try
				{	
					if(action!=CS5MoldovaI.ACTION_ACCOUNT_INFO && action!=CS5MoldovaI.ACTION_ACCOUNT_DETAILS)
					{
						if(log.isDebugEnabled())log.error(methodName,"SENDING RETRY........"+ (ambgCurrentRetryCount-1)+ "isRetryRequest "+isRetryRequest + "IN Transaction Id" +(String)requestMap.get("IN_RECON_ID"));
					}
					long startTimeNode = System.currentTimeMillis();
					if(log.isDebugEnabled())log.debug(methodName,"Start time to find the scheduled Node startTimeNode::"+startTimeNode+"miliseconds");
					//If the connection for corresponding node is failed, retry to get the node with configured number of times.
					//If connection established then break the loop.
					for(int loop=1;loop<=conRetryNumber;loop++)
					{
						try
						{
							cs5NodeVO = cs5NodeScheduler.getNodeVO(inReconID);
							if(log.isDebugEnabled())log.error(methodName,"URL PICKED IS ......."+ cs5NodeVO.getUrl());
							//Check if Node is found or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
							if(cs5NodeVO==null)
								throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_DETAIL_NOT_FOUND );
							nodeCloser = cs5NodeVO.getNodeCloser();
							try
							{
								if(cs5NodeVO.isSuspended())
									nodeCloser.checkExpiry(cs5NodeVO);
							}
							catch(BTSLBaseException be)
							{
								if(loop==conRetryNumber)
								{
									if("Y".equals(requestMap.get("ADJUST")) && "C".equals(requestMap.get("INTERFACE_ACTION")) && "S".equals(userType))
									{
										requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	  
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get("NETWORK_CODE"),"Sender Credit Back case. But all nodes are suspended. So marking the response as AMBIGUOUS");
										throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
									}
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get("NETWORK_CODE"),"MAXIMUM SHIFTING OF NODE IS REACHED");
									throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
								}
								cs5NodeVO.decrementConNumber(inTXNID);
								continue;
							}
							catch(Exception e)
							{
								log.error(methodName,"Exception e::"+e.getMessage());
								throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
							}
							warnTime=cs5NodeVO.getWarnTime();
							//Get the read time out based on the action.
							if(CS5MoldovaI.ACTION_ACCOUNT_INFO==action || CS5MoldovaI.ACTION_ACCOUNT_DETAILS==action)
								readTimeOut=cs5NodeVO.getValReadTimeOut();
							else
								readTimeOut=cs5NodeVO.getTopReadTimeOut();
							//Confirm for the service name SERVLET for the URL construction whether URL will be specified in INFile or IP,PORT and ServletName.
							cs5URLConnection = new CS5MoldovaUrlConnection(cs5NodeVO.getUrl(),cs5NodeVO.getUsername(),cs5NodeVO.getPassword(),cs5NodeVO.getConnectionTimeOut(),readTimeOut,cs5NodeVO.getKeepAlive(),inRequestStr.length(),hostName,userAgent);
							//break the loop on getting the successful connection for the node;
							if(log.isDebugEnabled()) log.debug(methodName,"Connection of interfaceID ["+interfaceID+"] for the Node Number ["+cs5NodeVO.getNodeNumber()+"] created after the attempt number(loop)::"+loop);
							break;
						}
						catch(BTSLBaseException be)
						{
							log.error(methodName,"BTSLBaseException be::"+be.getMessage());
							throw be;//Confirm should we come out of loop or do another retry
						}
						catch(Exception e)
						{
							//In case of connection failure 
							//1.Decrement the connection counter
							//2.set the Node as blocked 
							//3.set the blocked time
							//4.Handle the event with level INFO, show the message that Node is blocked for some time (expiry time).
							//Continue the retry loop till success;
							//Check if the max retry attempt is reached raise exception with error code.
							log.error(methodName,"Exception while creating connection e::"+e.getMessage());
							EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get("NETWORK_CODE"),"Exception while getting the connection for CS5MoldovaIN with INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
							log.info(methodName,"Setting the Node ["+cs5NodeVO.getNodeNumber()+"] as blocked for duration ::"+cs5NodeVO.getExpiryDuration() +" miliseconds");
							cs5NodeVO.setBlocked(true);
							cs5NodeVO.setBlokedAt(System.currentTimeMillis());
							nodeCloser.resetCounters(cs5NodeVO);
							if(loop==conRetryNumber)
							{
								if("Y".equals(requestMap.get("ADJUST")) && "C".equals(requestMap.get("INTERFACE_ACTION")) && "S".equals(userType))
								{
									requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	  
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get("NETWORK_CODE"),"Sender Credit Back case. But all nodes are Blocked. So marking the response as AMBIGUOUS");
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get("NETWORK_CODE"),"MAXIMUM SHIFTING OF NODE IS REACHED");
								throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
							}
							cs5NodeVO.decrementConNumber(inTXNID);
							continue;
						}
					}
					long totalTimeNode =System.currentTimeMillis()-startTimeNode;
					if(log.isDebugEnabled())
						log.debug(methodName,"Total time to find the scheduled Node totalTimeNode: "+totalTimeNode);
				}
				catch(BTSLBaseException be)
				{
					throw be;
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn,(String) requestMap.get("NETWORK_CODE"),"Error while getting the scheduled Node for INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]"+" Exception ::"+e.getMessage());
					log.error(methodName,"Exception e::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				try
				{
					PrintWriter out = cs5URLConnection.getPrintWriter();
					out.flush();
					startTime=System.currentTimeMillis();
					requestMap.put("IN_START_TIME",String.valueOf(startTime));
					out.println(inRequestStr);
					out.flush();
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,"Exception e::"+e);
					EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"),"While sending request to CS5Moldova IN INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] Exception::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
				}
				try
				{
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
							log.info(methodName, "WARN time reaches, startTime::"+startTime+" endTime::"+endTime+" From file cache warnTime::"+warnTime+ " time taken (endTime-startTime)::"+(endTime-startTime));
							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.MAJOR,"CS5MoldovaINHandler[sendRequestToIN]",inTXNID,msisdn,(String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,"CS5MoldovaIN is taking more time than the warning threshold. Time: "+(endTime-startTime)+"INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
						}
					}
					catch(Exception e)
					{
						log.errorTrace(methodName,e);
						log.error(methodName,"Exception e::"+e.getMessage());
						EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE"),"While getting the response from the CS5MoldovaIN for INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]"+"Exception="+e.getMessage());
						nodeCloser.updateCountersOnAmbiguousResp(cs5NodeVO);
						if(ambgCurrentRetryCount > ambgMaxRetryCount)
						{
							log.error(methodName,"Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
							EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Ferma6INHandler[sendRequestToIN]",inTXNID,msisdn,(String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,  "Read timeout from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
							requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						Thread.sleep(retrySleepTime);
						isRetryRequest=true;
						requestMap.put("IN_RECON_ID",inTXNID+"01");
						requestMap.put("IN_TXN_ID",inTXNID+"01");
						continue;
					}
					finally
					{
						if(endTime==0) endTime=System.currentTimeMillis();
						requestMap.put("IN_END_TIME",String.valueOf(endTime));
						log.error(methodName,"Request sent to IN at:"+startTime+" Response received from IN at:"+endTime+" defined read time out is:"+readTimeOut);
					}
					responseStr = buffer.toString();
					TransactionLog.log(inTXNID,referenceID,msisdn,(String)requestMap.get("NETWORK_CODE"),String.valueOf(action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+action);
					if(log.isDebugEnabled()) 
						log.debug(methodName,"msisdn="+msisdn+" action="+action+" responseStr="+responseStr);
					String httpStatus = cs5URLConnection.getResponseCode();
					requestMap.put("PROTOCOL_STATUS", httpStatus);
					if(!CS5MoldovaI.HTTP_STATUS_200.equals(httpStatus))
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
					if (InterfaceUtil.isNullString(responseStr))
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,"Blank response from CS5MoldovaIN");
						log.error(methodName, "NULL response from interface");
						nodeCloser.updateCountersOnAmbiguousResp(cs5NodeVO);
						if(ambgCurrentRetryCount > ambgMaxRetryCount)
						{
							log.error(methodName,"Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
							EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5MoldovaINHandler[sendRequestToIN]",inTXNID,msisdn,(String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,  "NULL response from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
							requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						} 
						Thread.sleep(retrySleepTime);
						isRetryRequest=true;
						requestMap.put("IN_RECON_ID",inTXNID+"01");
						requestMap.put("IN_TXN_ID",inTXNID+"01");
						continue;
					}
					if(cs5NodeVO.isSuspended())
						nodeCloser.resetCounters(cs5NodeVO);
					responseMap=cs5MoldovaRequestFormatter.parseResponse(action,responseStr,responseMap);
					//Here the various checks would be done based on the response.
					//Check the fault code if it is not null then handle the event with message as fault string and error code.
					//First check whether the responseCode is null
					//If the response code is null,check the fault code,if present get the fault string and 
					//a.throw the exception with error code INTERFACE_PROCESS_REQUEST_ERROR.
					//b.Handle the event with Level FATAL and message as fault strring
					//1.If the responseCode is other than 0 
					//a.check if the code is 102 then throw BTSLBaseException 
					//2.If the responseCode is 0 then checks the following.
					String faultCode = (String)responseMap.get("faultCode");
					if(!InterfaceUtil.isNullString(faultCode))
					{
						//Log the value of executionStatus for corresponding msisdn,recieved from the response.
						log.info(methodName, "faultCode::"+faultCode +"inTXNID::"+inTXNID+" msisdn::"+msisdn);
						requestMap.put("INTERFACE_STATUS", faultCode);//Put the interface_status in requestMap
						log.error(methodName,"faultCode="+responseMap.get("faultCode")+"faultString = "+responseMap.get("faultString"));
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,(String)responseMap.get("faultString"));
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
					String responseCode = (String)responseMap.get("responseCode");
					requestMap.put("INTERFACE_STATUS",responseCode);
					Object[] successList=CS5MoldovaI.RESULT_OK.split(",");
					if(!Arrays.asList(successList).contains(responseCode))
					{
						if(CS5MoldovaI.SUBSCRIBER_NOT_FOUND.equals(responseCode))
						{
							log.error(methodName,"Subscriber not found with MSISDN::"+msisdn);
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,"Subscriber is not found at IN");
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
						}
						else if(CS5MoldovaI.OLD_TRANSACTION_ID.equals(responseCode))
						{
							if(!isRetryRequest)
							{
								if(log.isDebugEnabled())log.debug(methodName,"isRetryRequest:"+isRetryRequest);
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS5MoldovaINHandler[sendRequestToIN]",inTXNID,msisdn,(String)requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,"Transaction ID mismatch");
								throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// recharge request with old transaction id								
							}			            	
						}
						else
						{
							log.error(methodName,"Error code received from IN ::"+responseCode);
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5MoldovaINHandler[sendRequestToIN]",referenceID,msisdn, (String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action,"Error code received from IN "+responseCode);
							throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
						}
					}
				}
				catch(BTSLBaseException be)
				{
					throw be;
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,"Exception e::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				break;
			}
		}
		catch(BTSLBaseException be)
		{
			log.error(methodName,"BTSLBaseException be::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(methodName,"Exception e::"+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			try
			{
				//Closing the HttpUrl connection
				if (cs5URLConnection != null) cs5URLConnection.close();
				if(cs5NodeVO!=null)
				{
					log.info(methodName,"Connection of Node ["+cs5NodeVO.getNodeNumber()+"] for INTERFACE_ID="+interfaceID+" is closed");
					//Decrement the connection number for the current Node.
					cs5NodeVO.decrementConNumber(inReconID);
					log.info(methodName,"After closing the connection for Node ["+cs5NodeVO.getNodeNumber()+"] USED connections are ::["+cs5NodeVO.getConNumber()+"]");
				}
			}
			catch (Exception e)
			{
				log.errorTrace(methodName,e);
				log.error(methodName, "While closing CS5MoldovaIN Connection Exception e::" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[sendRequestToIN]",referenceID, msisdn, (String) requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+interfaceID+" Stage = "+action, "Not able to close connection:" + e.getMessage()+"INTERFACE_ID=["+interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
			}
			if (log.isDebugEnabled())
				log.debug(methodName, "Exiting  interfaceID::"+interfaceID+" Stage::"+action + " responseStr::" + responseStr);
		}
	}

	/**
	 * This method would be used to adjust the validity of the subscriber account at the IN.
	 * @param	HashMap p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */   
	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{}

	/**
	 * This method is used to set the interface parameters into requestMap, these parameters are as bellow
	 * 1.Origin node type.
	 * 2.Origin host type.
	 * @throws Exception
	 */
	private void setInterfaceParameters() throws Exception,BTSLBaseException
	{
		final String methodName = "setInterfaceParameters";
		if(log.isDebugEnabled()) log.debug(methodName,"Entered");
		try
		{        	
			String cancelTxnAllowed = FileCache.getValue(interfaceID,"CANCEL_TXN_ALLOWED");
			if(InterfaceUtil.isNullString(cancelTxnAllowed))
			{
				log.error(methodName,"Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]",referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn , (String) requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());

			String systemStatusMappingCr = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
			if(InterfaceUtil.isNullString(systemStatusMappingCr))
			{
				log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]",referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn , (String) requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());

			String systemStatusMappingCrAdj = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
			if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
			{
				log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]",referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn , (String) requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());

			String systemStatusMappingDbtAdj = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
			{
				log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]",referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn , (String) requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());

			String systemStatusMappingCrBck = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
			if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
			{
				log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]",referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn , (String) requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());

			String cancelCommandStatusMapping = FileCache.getValue(interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
			if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
			{
				log.error(methodName,"Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]",referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn , (String) requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());


			String cancelNA = FileCache.getValue(interfaceID,"CANCEL_NA");
			if(InterfaceUtil.isNullString(cancelNA))
			{
				log.error(methodName,"Value of CANCEL_NA is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]",referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn , (String) requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("CANCEL_NA",cancelNA.trim());

			String warnTimeStr=FileCache.getValue(interfaceID, "WARN_TIMEOUT");
			if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());

			String nodeType=FileCache.getValue(interfaceID, "NODE_TYPE");
			if(InterfaceUtil.isNullString(nodeType))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "NODE_TYPE is not defined in IN File ");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("NODE_TYPE",nodeType.trim());

			String hostName=FileCache.getValue(interfaceID, "HOST_NAME");
			if(InterfaceUtil.isNullString(hostName))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "HOST_NAME is not defined in IN File ");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("HOST_NAME",hostName.trim());

			String subscriberNAI=FileCache.getValue(interfaceID, "NAI");
			if(InterfaceUtil.isNullString(subscriberNAI)||!InterfaceUtil.isNumeric(subscriberNAI))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "NAI is not defined in IN File or not numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("SubscriberNumberNAI",subscriberNAI.trim());

			String currency=FileCache.getValue(interfaceID, "CURRENCY");
			if(InterfaceUtil.isNullString(currency))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "CURRENCY is not defined in IN File ");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("CURRENCY",currency.trim());

			String RefillAccountAfterFlag=FileCache.getValue(interfaceID, "REQUEST_REFILL_ACCOUNT_AFTER_FLAG");
			if(InterfaceUtil.isNullString(RefillAccountAfterFlag))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "REQUEST_REFILL_ACCOUNT_AFTER_FLAG is not defined in IN File ");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("REQUEST_REFILL_ACCOUNT_AFTER_FLAG",RefillAccountAfterFlag.trim());
			
			String refillAccountBeforeFlag=FileCache.getValue(interfaceID, "REQUEST_REFILL_ACCOUNT_BEFORE_FLAG");
			if(InterfaceUtil.isNullString(refillAccountBeforeFlag))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "REQUEST_REFILL_ACCOUNT_BEFORE_FLAG is not defined in IN File ");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("REQUEST_REFILL_ACCOUNT_BEFORE_FLAG",refillAccountBeforeFlag.trim());
			
			String requestRefillDetailsFlag=FileCache.getValue(interfaceID, "REQUEST_REFILL_DETAILS_FLAG");
			if(InterfaceUtil.isNullString(requestRefillDetailsFlag))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "REQUEST_REFILL_DETAILS_FLAG is not defined in IN File ");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			requestMap.put("REQUEST_REFILL_DETAILS_FLAG",requestRefillDetailsFlag.trim());	
			
	
			String extData1=FileCache.getValue(interfaceID, "EXTERNAL_DATA1");
			if(InterfaceUtil.isNullString(extData1))
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "EXTERNAL_DATA1 is not defined in IN File ");
			requestMap.put("EXTERNAL_DATA1",extData1.trim());

			String extData2=FileCache.getValue(interfaceID, "EXTERNAL_DATA2");
			if(InterfaceUtil.isNullString(extData2))
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS5MoldovaINHandler[setInterfaceParameters]","REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+(String) requestMap.get("NETWORK_CODE") , "EXTERNAL_DATA2 is not defined in IN File ");
			requestMap.put("EXTERNAL_DATA2",extData2.trim());
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception e="+e.getMessage());
			throw e;
		}
		finally
		{
			if (log.isDebugEnabled())
				log.debug(methodName, "Exited requestMap:" + requestMap);
		}
	}

	/**
	 * This method used to get the system language mapped in FileCache based on the INLanguge.Includes following
	 * If the Mapping key not defined in IN file handle the event as System Error with level FATAL.
	 * If the Mapping is not defined handle the event as SYSTEM INFO with level MAJOR and set empty string.
	 * @throws Exception
	 */
	private void setLanguageFromMapping() throws Exception
	{
		final String methodName = "setLanguageFromMapping";
		if(log.isDebugEnabled()) 
			log.debug(methodName,"Entered");
		String mappedLang="";
		String[] mappingArr;
		String[] tempArr;
		boolean mappingNotFound = true;//Flag defines whether the mapping of language is found or not.
		String langFromIN = null;
		try
		{
			//Get the mapping string from the FileCache and storing all the mappings into array which are separated by ','.
			String mappingString = (String)FileCache.getValue(interfaceID, "LANGUAGE_MAPPING");
			langFromIN = (String)responseMap.get("languageIDCurrent");
			if(log.isDebugEnabled()) log.debug(methodName,"mappingString::"+mappingString +" langFromIN::"+langFromIN);
			mappingArr = mappingString.split(",");
			//Iterating the mapping array to map the IN language from the system language,if found break the loop.
			for(int in=0;in<mappingArr.length;in++)
			{
				tempArr = mappingArr[in].split(":");
				if(langFromIN.equals(tempArr[0].trim()))
				{
					mappedLang = tempArr[1];
					mappingNotFound=false;
					break;
				}
			}
			if(mappingNotFound)
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5MoldovaINHandler[setLanguageFromMapping]",referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  "+langFromIN +" is not defined in IN file Hence setting the Default language");
			//Set the mapped language to the requested map with key as IN_LANGUAGE.
			requestMap.put("IN_LANG",mappedLang);
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(methodName,"Exception e::"+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[setLanguageFromMapping]",referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled()) 
				log.debug(methodName,"Exited mappedLang::"+mappedLang);
		}
	}

	/**
	 * Method to send cancel request to IN for any ambiguous transaction.
	 * This method also makes reconciliation log entry. 	
	 * @throws	BTSLBaseException 
	 */
	private void handleCancelTransaction() throws BTSLBaseException
	{
		final String methodName = "handleCancelTransaction";
		if (log.isDebugEnabled())
			log.debug(methodName, "Entered.");
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
			if (log.isDebugEnabled())log.debug(methodName, "reconLog."+reconLog);
			cancelTxnAllowed=(String)requestMap.get("CANCEL_TXN_ALLOWED");
			//if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
			//into recon log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
			if("N".equals(cancelTxnAllowed))
			{
				cancelNA=(String)requestMap.get("CANCEL_NA");//Cancel command status as NA.
				cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(requestMap,cancelNA,"CANCEL_COMMAND_STATUS_MAPPING");
				requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);				
				interfaceStatus=(String)requestMap.get("INTERFACE_STATUS");
				systemStatusMapping=(String)requestMap.get("SYSTEM_STATUS_MAPPING");				
				cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING
				requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
				reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(requestMap);
				reconLog.info("",reconciliationLogStr);
				if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
					throw new BTSLBaseException(this,methodName,cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.	??????)    			
				requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);	
				//added to discard amount field from the message.
				requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
			}  			
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(methodName,"Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (log.isDebugEnabled())
				log.debug(methodName, "Exited");
		}
	}

	/**
	 * Method to generate the transaction ID.
	 * @throws	BTSLBaseException 
	 */      
	protected static synchronized String getINTransactionID(HashMap requestMap) throws BTSLBaseException
	{
		final String methodName = "getINTransactionID";
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
			serviceType = (String)requestMap.get("REQ_SERVICE");
			if("RC".equals(serviceType))
				serviceType="8";
			else if("PRCMDA".equals(serviceType))
				serviceType="7";
			//else if("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType) || "C2SENQ".equals(serviceType)  )
			//modified by harsh for Scheduled Credit Transfer
			else if("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType) || "C2SENQ".equals(serviceType))
				serviceType="6";
			userType = (String)requestMap.get("USER_TYPE");
			if("S".equals(userType))
				userType="3";
			else if("R".equals(userType))	
				userType="2";
			instanceID = FileCache.getValue((String)requestMap.get("INTERFACE_ID"),"INSTANCE_ID");
			if(InterfaceUtil.isNullString(instanceID))
			{
				log.error(methodName,"Parameter INSTANCE_ID is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MoldovaINHandler[getINTransactionID]","","" , (String) requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
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
			else if(transactionIDCounter > MAX_COUNTER)
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
			log.errorTrace(methodName,e);
		}
		finally
		{			
		}
		return transactionId;		
	}

	/**
	 * Method to is to calculate the dedicated account value for the sender.
	 * @throws	BTSLBaseException 
	 */ 

	public String  getDedicatedAccountValue(HashMap p_responseMap, String p_dedicatedAccID) throws Exception
	{
		final String methodName = "getDedicatedAccountValue";
		if (log.isDebugEnabled()) log.debug(methodName, "Entered with p_dedicatedAccID="+p_dedicatedAccID);
		String dedicatedAccValue="";
		String split="\\|";
		try
		{
			if(!InterfaceUtil.isNullString((String)p_responseMap.get("DEDICATED_ACC_ID")))
			{
				Object[] dedicatedID=((String)p_responseMap.get("DEDICATED_ACC_ID")).split(split);
				if(Arrays.asList(dedicatedID).contains(p_dedicatedAccID))
				{
					String[] dedAccIDArray=((String)p_responseMap.get("DEDICATED_ACC_ID")).split(split);
					String[] dedAccValueArray=((String)p_responseMap.get("DEDICATED_ACC_VALUE")).split(split);
					for(int i=0; i<dedAccIDArray.length; i++)
					{
						if(p_dedicatedAccID.equals(dedAccIDArray[i]))
						{
							dedicatedAccValue=dedAccValueArray[i];
							break;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
			if (log.isDebugEnabled())
				log.debug(methodName, "Exited with dedicatedAccValue="+dedicatedAccValue);
		}
		return dedicatedAccValue;
	}
}

