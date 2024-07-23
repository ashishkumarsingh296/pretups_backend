
package com.inter.vodaidea.idea.cs5;


import java.io.BufferedReader;
import java.io.PrintWriter;
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
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.inter.vodaidea.idea.cs5.cs5scheduler.NodeCloser;
import com.inter.vodaidea.idea.cs5.cs5scheduler.NodeManager;
import com.inter.vodaidea.idea.cs5.cs5scheduler.NodeScheduler;
import com.inter.vodaidea.idea.cs5.cs5scheduler.NodeVO;
import com.inter.vodaidea.logging.InterfaceRequestResponseLog;


public class CS5IdeaINHandler implements InterfaceHandler{

	private static Log _log = LogFactory.getLog(CS5IdeaINHandler.class.getName());
	private HashMap _requestMap = null;//Contains the respose of the request as key and value pair.
	//private HashMap _responseMap_v = null;//Contains the request parameter as key and value pair.
	private HashMap _responseMap_c = null;
	private HashMap _responseMap = null;
	private String _interfaceID=null;//Contains the interfaceID
	private String _inTXNID=null;//UNodesed to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;
	private String  _userType=null;
	
	private static CS5IdeaRequestFormatter _cs5ideaRequestFormatter=null;

	private NodeCloser _nodeCloser =null;

	static
	{
		if(_log.isDebugEnabled()) _log.debug("CS5IdeaINHandler[static]","Entered");
		try
		{
			_cs5ideaRequestFormatter = new CS5IdeaRequestFormatter();
		}
		catch(Exception e)
		{
			_log.error("CS5IdeaINHandler[static]","While instantiation of cs5ideaRequestFormatter get Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[static]","","", "","While instantiation of cs5ideaRequestFormatter get Exception e::"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("CS5IdeaINHandler[static]","Exited");
		}
	}

	/**
	 * Implements the logic that validate the subscriber and get the subscriber information 
	 * from the IN.
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception 
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception
	{
		if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);
		final String methodName = "CS5IdeaINHandler[validate]";
		_requestMap = p_requestMap;
		
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
			
			if((_requestMap.get("REQ_FROM_NSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)_requestMap.get("REQ_FROM_NSMS"))) || "N".equals(validateRequired))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("SERVICE_CLASS", PretupsI.ALL);
				_requestMap.put("ACCOUNT_STATUS","ACTIVE");
				return;
			}

			_interfaceID=(String)_requestMap.get("INTERFACE_ID");

			_msisdn=(String)_requestMap.get("MSISDN"); 	

			if(!InterfaceUtil.isNullString(_msisdn))
			{
				_msisdn =InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);                        
			}

			_requestMap.put("FILTER_MSISDN",_msisdn);

			//String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));

			if(!BTSLUtil.isNullString(FileCache.getValue(_interfaceID, "SOURCE_MTV_SERVER")) && FileCache.getValue(_interfaceID, "SOURCE_MTV_SERVER").equalsIgnoreCase("Y"))
            {
                  if(!BTSLUtil.isNullString(FileCache.getValue(_interfaceID, (String)_requestMap.get("SENDER_MSISDN")+"_NOT_ALLOWED_LOCATION")) && BTSLUtil.isStringContain(FileCache.getValue(_interfaceID, (String)_requestMap.get("SENDER_MSISDN")+"_NOT_ALLOWED_LOCATION"),(String)_requestMap.get("NETWORK_CODE")))
                  {                       
                        _log.error("validate","Customer Location not allowed");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Customer Location not allowed");
                        throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
                  }
                  
            }

			//String validateRequired="Y";
			if("N".equals(validateRequired) || (_requestMap.get("SERVICECLASS")!=null && "TRUE".equalsIgnoreCase((String)_requestMap.get("SERVICECLASS"))))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("INTERFACE_PREV_BALANCE","0");
				_requestMap.put("OLD_EXPIRY_DATE",BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyyyy"));
				_requestMap.put("ACCOUNT_STATUS","Active");
				return ;
			}

			_inTXNID=getINTransactionID(_requestMap);	

			_requestMap.put("IN_RECON_ID",_inTXNID);

			_requestMap.put("IN_TXN_ID",_inTXNID);

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("validate","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
			}
			multFactor=multFactor.trim();
			//Set the interface parameters into requestMap
			setInterfaceParameters();

			//key value of requestMap is formatted into XML string for the validate request.
			String inStr = _cs5ideaRequestFormatter.generateRequest(CS5IdeaI.ACTION_ACCOUNT_INFO,_requestMap);

			//sending the AccountInfo request to IN along with validate action defined in CS5IdeaI interface

			sendRequestToIN(inStr,CS5IdeaI.ACTION_ACCOUNT_INFO);

			//set TRANSACTION_STATUS as Success in request map

			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			
			_requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
			
			//get value of accountValue1 from response map (accountValue1 was set in response map in sendRequestToIN method.)
			String amountStr=(String)_responseMap.get("accountValue1");
			try
			{
				//amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
				_requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
				_requestMap.put("AvailableBalance",amountStr);				
				//temporary to run CS5 idea as postpaid interface
				_requestMap.put("BILL_AMOUNT_BAL","0");
			}
			catch(Exception e)
			{
				_log.error("validate","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:"+e.getMessage());
				_requestMap.put("AvailableBalance","0");
				_requestMap.put("INTERFACE_PREV_BALANCE","0");
				//throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
			}

			_requestMap.put("ACCOUNT_STATUS","Active");

			_requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
			//set OLD_EXPIRY_DATE in request map as returned from _responseMap.
			try{
			 _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("supervisionExpiryDate")).trim(), "yyyyMMdd"));
			}catch (Exception e) {
				 _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getCurrentDateString("yyyyMMdd"));
			}
			//Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");
			try{
				_requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("serviceFeeExpiryDate")).trim(), "yyyyMMdd"));
			}catch (Exception e) {

				_requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getCurrentDateString("yyyyMMdd"));
			}
		}
		catch (BTSLBaseException be)
		{
			_log.error("validate","BTSLBaseException be="+be.getMessage());
			throw be; 	   	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);
		}		 
	}//end of validate
	/**
	 * Implements the logic that credit the subscriber account on IN.
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception  
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
		_requestMap = p_requestMap;

		try
		{
			if(_requestMap.get("REQ_FROM_NSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)_requestMap.get("REQ_FROM_NSMS")))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				return;
			}

			_interfaceID=(String)_requestMap.get("INTERFACE_ID");


			_inTXNID=getINTransactionID(_requestMap);
			_requestMap.put("IN_RECON_ID",_inTXNID);
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_msisdn=(String)_requestMap.get("MSISDN");
			if(!InterfaceUtil.isNullString(_msisdn))
			{
				_msisdn =InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);                        
			}

			_requestMap.put("FILTER_MSISDN",_msisdn);
			_userType=(String)_requestMap.get("USER_TYPE");


			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("credit","multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("credit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters();
			if(!BTSLUtil.isNullString(FileCache.getValue(_interfaceID, "SOURCE_MTV_SERVER")) && FileCache.getValue(_interfaceID, "SOURCE_MTV_SERVER").equalsIgnoreCase("Y"))
			{
				String originOperatorID=_requestMap.get("ENTITY_NAME").toString()+(String)_requestMap.get("IN_TXN_ID");
				_requestMap.put("ORIGIN_OPERATOR_ID",originOperatorID.trim());
			}else{
				String originOperatorID=FileCache.getValue(_interfaceID, "ORIGIN_OPERATOR_ID");
				if(InterfaceUtil.isNullString(originOperatorID))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "ORIGIN_OPERATOR_ID is not defined in IN File ");
					throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				originOperatorID=originOperatorID.trim()+(String)_requestMap.get("TRANSACTION_ID");
				_requestMap.put("ORIGIN_OPERATOR_ID",originOperatorID.trim());	

			}
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
			try
			{
				multFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				if(_log.isDebugEnabled()) _log.debug("credit","From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5IdeaINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
				e.printStackTrace();
				_log.error("credit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("credit","transfer_amount:"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);

			//key value of requestMap is formatted into XML string for the validate request.
			String inStr = _cs5ideaRequestFormatter.generateRequest(CS5IdeaI.ACTION_RECHARGE_CREDIT,_requestMap);
			//sending the Re-charge request to IN along with re-charge action defined in CS5IdeaI interface
			sendRequestToIN(inStr,CS5IdeaI.ACTION_RECHARGE_CREDIT);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			// set NEW_EXPIRY_DATE into request map

			try{
				 _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap_c.get("supervisionExpiryDate")).trim(), "yyyyMMdd"));
				}catch (Exception e) {
					 _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getCurrentDateString("yyyyMMdd"));
				}
				//Date serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");
				try{
					_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap_c.get("serviceFeeExpiryDate")).trim(), "yyyyMMdd"));
				}catch (Exception e) {

					_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getCurrentDateString("yyyyMMdd"));
				}
				
			try
			{
				String segId="";
				segId = (String) _responseMap_c.get("SegmentationID");
				_requestMap.put("SEGMENTATION_ID",segId);
				if(_log.isDebugEnabled()) _log.debug("SEGMENTATION_ID=",segId);

			}
			catch(Exception e)
			{
				e.printStackTrace();
				_requestMap.put("SEGMENTATION_ID","");
			}

			
			//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
			try
			{
				String postBalanceStr="";

				postBalanceStr = (String) _responseMap_c.get("accountValue1");
				//postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
				_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);

				String postBalancePromoStr1 = (String) _responseMap_c.get("refillAmount1");

				if(!InterfaceUtil.isNullString(postBalancePromoStr1))
					postBalancePromoStr1 = InterfaceUtil.getSystemAmountFromINAmount(postBalancePromoStr1,multFactorDouble);
				else 
					postBalancePromoStr1="0";

				_requestMap.put("INTERFACE_PROMO_POST_BALANCE",postBalancePromoStr1);
				if(_log.isDebugEnabled()) _log.debug("postBalancePromoStr=",postBalancePromoStr1);

			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("credit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
				_requestMap.put("INTERFACE_PROMO_POST_BALANCE","0");
				_requestMap.put("INTERFACE_POST_BALANCE","0");
			}
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");


		} 
		catch (BTSLBaseException be)
		{
			if(be.getMessage().equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
				p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"credit", be.getMessage());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("credit", "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
		}
	}//end of credit


	/**
	 * This method is used to adjust the following
	 * 1.Amount
	 * 2.ValidityDays
	 * 3.GraceDays
	 */
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{

	}//end of creditAdjust

	/**
	 * Implements the logic that debit the subscriber account on IN.
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */
	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{
		if (_log.isDebugEnabled())_log.debug("debitAdjust"," Entered p_requestMap:" + p_requestMap);
		_requestMap = p_requestMap;//assign map passed from InterfaceModule to _requestMap(instance var) 
		double systemAmtDouble =0;
		String amountStr=null;
		//int validityDays=0;//Defines the validity days by which adjustment to be made.
		//int graceDays=0;//Defines the grace period by which adjustment to be made.

		double multFactorDouble=0;
		try
		{
			if(_requestMap.get("REQ_FROM_NSMS")!=null && PretupsI.YES.equalsIgnoreCase((String)_requestMap.get("REQ_FROM_NSMS")))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				return;
			}

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
				_log.error("creditAdjust","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.ERROR_INTERFACE_MULTFACTOR);
			}
			multFactor = multFactor.trim();
			//Set the interface parameters into requestMap
			setInterfaceParameters();
			if(!BTSLUtil.isNullString(FileCache.getValue(_interfaceID, "SOURCE_MTV_SERVER")) && FileCache.getValue(_interfaceID, "SOURCE_MTV_SERVER").equalsIgnoreCase("Y"))
			{
				String originOperatorID=_requestMap.get("ENTITY_NAME").toString()+(String)_requestMap.get("IN_TXN_ID");
				_requestMap.put("ORIGIN_OPERATOR_ID",originOperatorID.trim());
			}else{
				String originOperatorID=FileCache.getValue(_interfaceID, "ORIGIN_OPERATOR_ID");
				if(InterfaceUtil.isNullString(originOperatorID))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "ORIGIN_OPERATOR_ID is not defined in IN File ");
					throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				originOperatorID=originOperatorID.trim()+(String)_requestMap.get("TRANSACTION_ID");
				_requestMap.put("ORIGIN_OPERATOR_ID",originOperatorID.trim());	

			}
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			try
			{
				double MultFactorDouble1=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,MultFactorDouble1);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				if(_log.isDebugEnabled()) _log.debug("debitAdjust","From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS5IdeaINHandler[debitAdjust]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
				e.printStackTrace();
				_log.error("creditAdjust","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("debitAdjust","transfer_amount:"+amountStr+" multFactor:"+multFactor);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount","-"+amountStr);


			String inStr = _cs5ideaRequestFormatter.generateRequest(CS5IdeaI.ACTION_IMMEDIATE_DEBIT,_requestMap);


			//sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS5MobililI interface
			sendRequestToIN(inStr,CS5IdeaI.ACTION_IMMEDIATE_DEBIT);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			multFactorDouble=Double.parseDouble(multFactor);
			try
			{
				String postBalanceStr = (String) _responseMap.get("accountValue1");
				//postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
				_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("credit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
				_requestMap.put("INTERFACE_POST_BALANCE","0");
				//throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.ERROR_RESPONSE);
			}				
			//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N"); 

		}
		catch (BTSLBaseException be)
		{
			if(be.getMessage().equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
				p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"debitAdjust", be.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("debitAdjust", "Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:"+e.getMessage());
			throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
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

		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Entered p_inRequestStr::"+p_inRequestStr+" p_action::"+p_action);
		TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string::"+p_inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action::"+p_action);
		String responseStr = "";
		NodeVO cs5NodeVO=null;
		NodeScheduler cs5NodeScheduler=null;
		CS5IdeaUrlConnection cs5URLConnection = null;
		long startTime=0;
		long endTime=0;
		int conRetryNumber=0;
		long warnTime=0;
		int readTimeOut=0;
		String inReconID=null;

		try
		{

			startTime=System.currentTimeMillis();
			_requestMap.put("IN_START_TIME",String.valueOf(startTime));
		
			if(p_action==CS5IdeaI.ACTION_RECHARGE_CREDIT)
				_responseMap_c = new HashMap();
			else
				_responseMap = new HashMap();

			inReconID=(String)_requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=_inTXNID;

			//Get the instance of NodeScheduler based on interfaceId.
			cs5NodeScheduler = NodeManager.getScheduler(_interfaceID);
			//Get the retry number from the object that is used to retry the getNode in case connection is failed.
			conRetryNumber = cs5NodeScheduler.getRetryNum();
			//Host name and userAgent may be set into the VO corresponding to each Node for authentication-CONFIRM, if it is not releted with the request xml.
			String hostName = cs5NodeScheduler.getHeaderHostName();
			String userAgent = cs5NodeScheduler.getUserAgent();
			//check if cs5NodeScheduler is null throw exception.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
			if(cs5NodeScheduler==null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_WHILE_GETTING_SCHEDULER_OBJECT);


			try
			{	

				long startTimeNode = System.currentTimeMillis();
				if(_log.isDebugEnabled())_log.debug("sendRequestToIN","Start time to find the scheduled Node startTimeNode::"+startTimeNode+"miliseconds");
				//If the connection for corresponding node is failed, retry to get the node with configured number of times.
				//If connection eshtablished then break the loop.

				for(int loop=1;loop<=conRetryNumber;loop++)
				{
					try
					{
						cs5NodeVO = cs5NodeScheduler.getNodeVO(inReconID);
						if(_log.isDebugEnabled())_log.error("sendRequestToIN","URL PICKED IS ......."+ cs5NodeVO.getUrl());
						//Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
						if(cs5NodeVO==null)
							throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS5_NODE_DETAIL_NOT_FOUND );

						_requestMap.put("IN IP", cs5NodeVO.getIP());
						_requestMap.put("IN PORT", cs5NodeVO.getPort());
						_nodeCloser = cs5NodeVO.getNodeCloser();
						try
						{
							if(cs5NodeVO.isSuspended())
								_nodeCloser.checkExpiry(cs5NodeVO);
						}
						catch(BTSLBaseException be)
						{

							if(loop==conRetryNumber)
							{
								if("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))
								{
									_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	  
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"Sender Credit Back case. But all nodes are suspended. So marking the response as AMBIGUOUS");
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"MAXIMUM SHIFTING OF NODE IS REACHED");
								throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
							}
							cs5NodeVO.decrementConNumber(_inTXNID);
							continue;
						}
						catch(Exception e)
						{
							_log.error("sendRequestToIN","Exception e::"+e.getMessage());
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						}


						warnTime=cs5NodeVO.getWarnTime();
						//Get the read time out based on the action.
						if(CS5IdeaI.ACTION_ACCOUNT_INFO==p_action /*|| CS5IdeaI.ACTION_ACCOUNT_DETAILS==p_action*/)
							readTimeOut=cs5NodeVO.getValReadTimeOut();
						else
							readTimeOut=cs5NodeVO.getTopReadTimeOut();

						_requestMap.put("URL_PICKED", cs5NodeVO.getUrl());
						//Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
						cs5URLConnection = new CS5IdeaUrlConnection(cs5NodeVO.getUrl(),cs5NodeVO.getUsername(),cs5NodeVO.getPassword(),cs5NodeVO.getConnectionTimeOut(),readTimeOut,cs5NodeVO.getKeepAlive(),p_inRequestStr.length(),hostName,userAgent);
						//break the loop on getting the successfull connection for the node;
						if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Connection of _interfaceID ["+_interfaceID+"] for the Node Number ["+cs5NodeVO.getNodeNumber()+"] created after the attempt number(loop)::"+loop);
						break;
					}
					catch(BTSLBaseException be)
					{
						_log.error("sendRequestToIN","BTSLBaseException be::"+be.getMessage());
						throw be;//Confirm should we come out of loop or do another retry
					}//end of catch-BTSLBaseException
					catch(Exception e)
					{
						//In case of connection failure 
						//1.Decrement the connection counter
						//2.set the Node as blocked 
						//3.set the blocked time
						//4.Handle the event with level INFO, show the message that Node is blocked for some time (expiry time).
						//Continue the retry loop till success;
						//Check if the max retry attempt is reached raise exception with error code.
						_log.error("sendRequestToIN","Exception while creating connection e::"+e.getMessage());
						EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"Exception while getting the connection for CS5IdeaIN with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");

						_log.info("sendRequestToIN","Setting the Node ["+cs5NodeVO.getNodeNumber()+"] as blocked for duration ::"+cs5NodeVO.getExpiryDuration() +" miliseconds");
						cs5NodeVO.setBlocked(true);
						cs5NodeVO.setBlokedAt(System.currentTimeMillis());
						_nodeCloser.resetCounters(cs5NodeVO);
						if(loop==conRetryNumber)
						{

							if("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))
							{
								_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	  
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"Sender Credit Back case. But all nodes are Blocked. So marking the response as AMBIGUOUS");
								throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
							}
							EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"MAXIMUM SHIFTING OF NODE IS REACHED");
							throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
						}
						cs5NodeVO.decrementConNumber(_inTXNID);
						continue;
					}//end of catch-Exception
				}//end of for loop
				long totalTimeNode =System.currentTimeMillis()-startTimeNode;
				if(_log.isDebugEnabled())_log.debug("sendRequestToIN","Total time to find the scheduled Node totalTimeNode: "+totalTimeNode);
			}
			catch(BTSLBaseException be)
			{
				//try{cs5NodeVO.decrementConNumber();}catch(Exception e){}-to Check properly
				throw be;
			}//end of catch-BTSLBaseException
			catch(Exception e)
			{
				e.printStackTrace();
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"Error while getting the scheduled Node for INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]"+" Exception ::"+e.getMessage());
				_log.error("sendRequestToIN","Exception e::"+e.getMessage());
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}//end of catch-Exception
			try
			{
				//System.out.println("HI"+cs5URLConnection);
				PrintWriter out = cs5URLConnection.getPrintWriter();
				out.flush();
				out.println(p_inRequestStr);
				out.flush();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN","Exception e::"+e);
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),"While sending request to cs5idea IN INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] Exception::"+e.getMessage());
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
						_log.info("sendRequestToIN", "WARN time reaches, startTime::"+startTime+" endTime::"+endTime+" From file cache warnTime::"+warnTime+ " time taken (endTime-startTime)::"+(endTime-startTime));
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.MAJOR,"CS5IdeaINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"CS5IdeaIN is taking more time than the warning threshold. Time: "+(endTime-startTime)+"INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					_log.error("sendRequestToIN","Exception e::"+e.getMessage());
					EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),"While getting the response from the CS5IdeaIN for INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]"+"Exception="+e.getMessage());

					if(p_action==CS5IdeaI.ACTION_ACCOUNT_INFO)
					{
						_log.error("sendRequestToIN","Read time out occured.  so throwing Fail exception");

						EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5IdeaINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read timeout from IN.  so throwing Fail in validation exception");
						_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}

					_log.error("sendRequestToIN","Read time out occured.  so throwing AMBIGOUS exception");
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5IdeaINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read timeout from IN.  so throwing AMBIGOUS exception");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);


				}//end of catch-Exception
				finally
				{
				}//end of finally

				responseStr = buffer.toString();
				TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
				if (_log.isDebugEnabled())_log.debug("sendRequestToIN", "responseStr::" + responseStr);
				//TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);

				String httpStatus = cs5URLConnection.getResponseCode();
				_requestMap.put("PROTOCOL_STATUS", httpStatus);
				if(!CS5IdeaI.HTTP_STATUS_200.equals(httpStatus))
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);


				//Check if there is no response, handle the event showing Blank response from CS5Idea and stop further processing. 
				if (InterfaceUtil.isNullString(responseStr))
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Blank response from CS5IdeaIN");
					_log.error("sendRequestToIN", "NULL response from interface");

					_log.error("sendRequestToIN","Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CS5IdeaINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "NULL response from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);

				}

				if(cs5NodeVO.isSuspended())
					_nodeCloser.resetCounters(cs5NodeVO);
				//Parse the response string and get the response Map.
				if(p_action==CS5IdeaI.ACTION_RECHARGE_CREDIT)
					_responseMap_c=_cs5ideaRequestFormatter.parseResponse(p_action,responseStr,_responseMap_c);
				else
					_responseMap=_cs5ideaRequestFormatter.parseResponse(p_action,responseStr,_responseMap);

				//_responseMap1=_cs5ideaRequestFormatter1.parseResponse(p_action,responseStr,_responseMap1);//for response code 100
				//Here the various checks would be done based on the response.
				//Check the fault code if it is not null then handle the event with message as fault string and error code.
				//First check whether the responseCode is null
				//If the response code is null,check the fault code,if present get the fault string and 
				//a.throw the exception with error code INTERFACE_PROCESS_REQUEST_ERROR.
				//b.Handle the event with Level FATAL and message as fault strring
				//1.If the responseCode is other than 0 
				//a.check if the code is 102 then throw BTSLBaseException 
				//2.If the responseCode is 0 then checks the following.
				String faultCode="";
				if(p_action==CS5IdeaI.ACTION_RECHARGE_CREDIT)
					faultCode = (String)_responseMap_c.get("faultCode");
				else
					faultCode = (String)_responseMap.get("faultCode");
				
				if(!InterfaceUtil.isNullString(faultCode))
				{
					//Log the value of executionStatus for corresponding msisdn,recieved from the response.
					_log.info("sendRequestToIN", "faultCode::"+faultCode +"_inTXNID::"+_inTXNID+" _msisdn::"+_msisdn);
					_requestMap.put("INTERFACE_STATUS", faultCode);//Put the interface_status in requestMap
					if(p_action==CS5IdeaI.ACTION_RECHARGE_CREDIT)
						_log.error("sendRequestToIN","faultCode="+_responseMap_c.get("faultCode")+"faultString = "+_responseMap_c.get("faultString"));	
					else
						_log.error("sendRequestToIN","faultCode="+_responseMap.get("faultCode")+"faultString = "+_responseMap.get("faultString"));
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,(String)_responseMap.get("faultString"));
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}

				//Check if responseCode=102,then throw exception with message INTERFACE_MSISDN_NOT_FOUND.
				String responseCode="";
				if(p_action==CS5IdeaI.ACTION_RECHARGE_CREDIT)
					responseCode = (String)_responseMap_c.get("responseCode");
				else
					responseCode = (String)_responseMap.get("responseCode");
				_requestMap.put("INTERFACE_STATUS",responseCode);

				String tempResponseStr=new String();
				tempResponseStr="{ ";
				tempResponseStr=tempResponseStr + " Stage = "+p_action;
				tempResponseStr=tempResponseStr + " transactionId="+_requestMap.get("TRANSACTION_ID");
				tempResponseStr=tempResponseStr + " MSISDN="+_requestMap.get("MSISDN");
				tempResponseStr=tempResponseStr + " senderMSISDN="+_requestMap.get("SENDER_MSISDN");
				tempResponseStr=tempResponseStr + " Balance="+_responseMap_c.get("accountValue1");
				if(String.valueOf(p_action).equalsIgnoreCase("1"))
				{
					tempResponseStr=tempResponseStr + " supervisionExpiryDate="+_responseMap.get("supervisionExpiryDate");
					tempResponseStr=tempResponseStr + " serviceFeeExpiryDate="+_responseMap.get("serviceFeeExpiryDate");
					tempResponseStr=tempResponseStr + " serviceRemovalDate="+_responseMap.get("serviceRemovalDate");
					//tempResponseStr=tempResponseStr + "supervisionExpiryDate="+_responseMap.get("supervisionExpiryDate");
					tempResponseStr=tempResponseStr + " serviceClass="+_responseMap.get("serviceClassCurrent");
				}
				
				tempResponseStr=tempResponseStr + " responseCode="+_responseMap_c.get("responseCode");
				tempResponseStr=tempResponseStr+" }";

				 InterfaceRequestResponseLog.log(cs5NodeVO.getUrl(),_requestMap,p_inRequestStr,tempResponseStr);

				

				Object[] successList=CS5IdeaI.RESULT_OK.split(",");
				Object[] errorList=CS5IdeaI.RESULT_NOT_OK.split(",");


				if(!Arrays.asList(successList).contains(responseCode))
				{

					if(CS5IdeaI.SUBSCRIBER_NOT_FOUND.equals(responseCode))
					{
						_log.error("sendRequestToIN","Subscriber not found with MSISDN::"+_msisdn);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Subscriber is not found at IN");
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
					}//end of checking the subscriber existance.
					else if(CS5IdeaI.OLD_TRANSACTION_ID.equals(responseCode))
					{

						if(_log.isDebugEnabled())_log.debug("sendRequestToIN","Transaction ID mismatch");
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS5IdeaINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String)_requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Transaction ID mismatch");
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// recharge request with old transaction id								

					}
					else if("true".equalsIgnoreCase((String)_requestMap.get("RESPONSE_CODE_100")) && CS5IdeaI.RESPONSE_CODE_100_HANDLING.equals(responseCode)&& p_action==CS5IdeaI.ACTION_RECHARGE_CREDIT){

						String multFactor;
						String previousbalance = _requestMap.get("INTERFACE_PREV_BALANCE").toString();
						System.out.println("Previous balance"+previousbalance);
						if (_log.isDebugEnabled())_log.debug("enquiryForResponsecode100Transaction", "Entered. ");

						setInterfaceParameters();
						String inStr1 = _cs5ideaRequestFormatter.generateRequest(CS5IdeaI.ACTION_ACCOUNT_INFO,_requestMap);
						sendRequestToIN(inStr1,CS5IdeaI.ACTION_ACCOUNT_INFO);

						String currentbalance=(String)_responseMap.get("accountValue1");

						multFactor=(String)_requestMap.get("MULT_FACTOR");
						try
						{
							currentbalance = InterfaceUtil.getSystemAmountFromINAmount(currentbalance,Double.parseDouble(multFactor));

						}
						catch(Exception e)
						{
							e.printStackTrace();
							_log.error("sendRequestToIN","Exception e:"+e.getMessage());
							EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:"+e.getMessage());
							throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.ERROR_RESPONSE);
						}


						if(Double.parseDouble(currentbalance)<=Double.parseDouble(previousbalance))  
						{	
							_log.error("sendRequestToIN","After again calling validate Previous and Final Balance are same, No Top Up on IN, Old Balance="+previousbalance+" Final balance="+currentbalance);
							_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.FAIL);
							throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
						}
						else if(Double.parseDouble(currentbalance)>Double.parseDouble(previousbalance))
						{
							_log.info("sendRequestToIN","After again calling validate Previous and Final Balance are not same, Top Up successful on IN, Old Balance="+previousbalance+" Final balance="+currentbalance);
							_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
							//whether to set reponse code as 0 here or not
						}
					}
					else  if(Arrays.asList(errorList).contains(responseCode))
					{

						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
					else if (CS5IdeaI.ACCOUNT_BARRED_FROM_REFILL.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_BARRED_FROM_REFILL);
					}
					else if (CS5IdeaI.INVALID_PAYMENT_PROFILE.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_PAYMENT_PROFILE);
					}
					else if (CS5IdeaI.SYSTEM_UNAVAILABLE.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_SYSTEM_UNAVAILABLE);
					}
					else if (CS5IdeaI.ACCOUNT_NOT_ACTIVE.equals(responseCode) || CS5IdeaI.DEDICATED_ACCOUNT_NOT_ACTIVE.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACCOUNT_NOT_ACTIVE);
					}
					else if (CS5IdeaI.DATE_ADJUSTMENT_ISSUE.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DATE_ADJUSTMENT_ISSUE);
					}
					else if (CS5IdeaI.IN_CONN_FAIL.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_IN_CONN_FAIL);
					}
					else if (CS5IdeaI.IN_RESPONSE_FAIL.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_IN_RESPONSE_FAIL);
					}
					else if (CS5IdeaI.ACC_MAX_CREDIT_LIMIT.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACC_MAX_CREDIT_LIMIT);
					}
					else if (CS5IdeaI.VOUCHER_STATUS_PENDING.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VOUCHER_STATUS_PENDING);
					}
					else if (CS5IdeaI.VOUCHER_GROUP_SERVICE_CLASS.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VOUCHER_GROUP_SERVICE_CLASS);
					}
					else if (CS5IdeaI.BELOW_MIN_BAL.equals(responseCode))
					{
						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BELOW_MIN_BAL);
					}
					else  
					{

						_log.error("sendRequestToIN","Error code received from IN ::"+responseCode);
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"CS5IdeaINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}

				}
			}

			catch(BTSLBaseException be)
			{
				throw be;
			}//end of catch-BTSLBaseException
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN","Exception e::"+e.getMessage());
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}//end of catch-Exception

		}
		catch(BTSLBaseException be)
		{
			_log.error("sendRequestToIN","BTSLBaseException be::"+be.getMessage());
			throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("sendRequestToIN","Exception e::"+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			if(endTime==0) endTime=System.currentTimeMillis();
			_requestMap.put("IN_END_TIME",String.valueOf(endTime));
			_log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime+" defined read time out is:"+readTimeOut);
	
			try
			{
				//Closing the HttpUrl connection
				if (cs5URLConnection != null) cs5URLConnection.close();
				
				if(cs5NodeVO!=null)
				{
					_log.info("sendRequestToIN","Connection of Node ["+cs5NodeVO.getNodeNumber()+"] for INTERFACE_ID="+_interfaceID+" is closed");
					//Decrement the connection number for the current Node.
					cs5NodeVO.decrementConNumber(inReconID);
					_log.info("sendRequestToIN","After closing the connection for Node ["+cs5NodeVO.getNodeNumber()+"] USED connections are ::["+cs5NodeVO.getConNumber()+"]");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN", "While closing CS5IdeaIN Connection Exception e::" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action, "Not able to close connection:" + e.getMessage()+"INTERFACE_ID=["+_interfaceID +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
			}
			if (_log.isDebugEnabled())
				_log.debug("sendRequestToIN", "Exiting  _interfaceID::"+_interfaceID+" Stage::"+p_action + " responseStr::" + responseStr);
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
	private void setInterfaceParameters() throws Exception,BTSLBaseException
	{
		if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered");
		try
		{        	

			//required
			String nodeType=FileCache.getValue(_interfaceID,"NODE_TYPE");
			if(InterfaceUtil.isNullString(nodeType))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "NODE_TYPE is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("NODE_TYPE",nodeType.trim());

			String hostName=FileCache.getValue(_interfaceID,"HOST_NAME");
			if(InterfaceUtil.isNullString(hostName))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "HOST_NAME is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("HOST_NAME",hostName.trim());

			String currency=FileCache.getValue(_interfaceID,"CURRENCY");
			if(InterfaceUtil.isNullString(currency))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CURRENCY is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("CURRENCY",currency.trim());

			String RefillAccountAfterFlag=FileCache.getValue(_interfaceID, "REFILL_ACNT_AFTER_FLAG");
			if(InterfaceUtil.isNullString(RefillAccountAfterFlag))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "REFILL_ACNT_AFTER_FLAG is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("REFILL_ACNT_AFTER_FLAG",RefillAccountAfterFlag.trim());

			String requestRefillDetailsFlag=FileCache.getValue(_interfaceID, "REFILL_DETAILS_FLAG");
			if(InterfaceUtil.isNullString(requestRefillDetailsFlag))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "REFILL_DETAILS_FLAG is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("REFILL_DETAILS_FLAG",requestRefillDetailsFlag.trim());

			


			String retailerMSISDNrequired=FileCache.getValue(_interfaceID,"CS5_RETAILER_MSISDN_REQUIRED_AT_IN");
			if(InterfaceUtil.isNullString(retailerMSISDNrequired))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CS5_RETAILER_MSISDN_REQUIRED_AT_IN is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("CS5_RETAILER_MSISDN_REQUIRED_AT_IN",retailerMSISDNrequired.trim());


			String retailerMSISDNrequiredLocation=FileCache.getValue(_interfaceID, "RETAILER_MSISDN_REQUIRED_LOCATION");
			if(InterfaceUtil.isNullString(retailerMSISDNrequiredLocation))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "RETAILER_MSISDN_REQUIRED_LOCATION is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("RETAILER_MSISDN_REQUIRED_LOCATION",retailerMSISDNrequiredLocation.trim());



			String fitIsApplicable=FileCache.getValue(_interfaceID, "FTT_IS_APPLICABLE");
			if(InterfaceUtil.isNullString(fitIsApplicable))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "FTT_IS_APPLICABLE is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("FTT_IS_APPLICABLE",fitIsApplicable.trim());


			String transactionCode=null;
			String transactionType=null;
			String locAtCS5=FileCache.getValue(_interfaceID, "LOC_AT_CS5");
			if(InterfaceUtil.isNullString(locAtCS5))
			{
				locAtCS5 = "N";
			}
			if("Y".equalsIgnoreCase(locAtCS5)){
				String subsNoNAI=FileCache.getValue(_interfaceID,"NAI");
				if(InterfaceUtil.isNullString(subsNoNAI)||!InterfaceUtil.isNumeric(subsNoNAI))
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "SUBSCRIBER_NO_NAI is not defined in IN File ");
					throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				_requestMap.put("SubscriberNumberNAI",subsNoNAI.trim());

				transactionCode=FileCache.getValue(_interfaceID,"TRANSACTION_CODE_RF");
				if(!InterfaceUtil.isNullString(transactionCode))
				{
					_requestMap.put("TRANSACTION_CODE_RF",transactionCode);
				}
				else
				{
					transactionCode="VTOPUP";
					_requestMap.put("TRANSACTION_CODE_RF",transactionCode);
				}	

				transactionType=FileCache.getValue(_interfaceID,"TRANSACTION_TYPE_RF");
				if(!InterfaceUtil.isNullString(transactionType))
				{
					_requestMap.put("TRANSACTION_TYPE_RF",transactionType);
				}
				else
				{
					transactionType="Refill";
					_requestMap.put("TRANSACTION_TYPE_RF",transactionType);
				}

				transactionCode=FileCache.getValue(_interfaceID,"TRANSACTION_CODE_DR");
				if(!InterfaceUtil.isNullString(transactionCode))
				{
					_requestMap.put("TRANSACTION_CODE_DR",transactionCode);
				}
				else
				{
					transactionCode="VTOPUP";
					_requestMap.put("TRANSACTION_CODE_DR",transactionCode);
				}	

				transactionType=FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"TRANSACTION_TYPE_DR");
				if(!InterfaceUtil.isNullString(transactionType))
				{
					_requestMap.put("TRANSACTION_TYPE_DR",transactionType);
				}
				else
				{
					transactionType="WrongRechargeReversal";
					_requestMap.put("TRANSACTION_TYPE_DR",transactionType);
				}

			}//end of locatcs5 if

			String reponsecodeflag=FileCache.getValue(_interfaceID, "RESPONSE_CODE_100");
			if(InterfaceUtil.isNullString(reponsecodeflag))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "RESPONSE_CODE_100 flag is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("RESPONSE_CODE_100",reponsecodeflag.trim());

			String cgrp4xServices=FileCache.getValue(_interfaceID, "CGRP_DET_4.X_SERVICES");
			if(InterfaceUtil.isNullString(cgrp4xServices))
			{
				cgrp4xServices="RC";
			}
			_requestMap.put("CGRP_DET_4.X_SERVICES",cgrp4xServices.trim());
			
			String cgrp4xFlag=FileCache.getValue(_interfaceID, "CGRP_DET_4.X_REQ");
			if(InterfaceUtil.isNullString(cgrp4xFlag))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CGRP_DET_4.X_REQ flag is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("CGRP_DET_4.X_REQ",cgrp4xFlag.trim());
			
			String migratedCircles=FileCache.getValue(_interfaceID,"MIGRATED_6.X_CIRCLES");
			if(InterfaceUtil.isNullString(migratedCircles))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5IdeaINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "migratedCircles is not defined in IN File ");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("MIGRATED_6.X_CIRCLES",migratedCircles.trim());

			
			

		}//end of try block
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			_log.error("setInterfaceParameters","Exception e="+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if (_log.isDebugEnabled())_log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
		}//end of finally
	}//end of setInterfaceParameters







	protected String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException
	{
		final String methodName = "getINTransactionID";

		String transactionId=null;
		String inNetCode=null;
		String netCode=null;

		try
		{
			transactionId = (String)p_requestMap.get("TRANSACTION_ID");
			
			netCode=(String)p_requestMap.get("NETWORK_CODE");
			
			inNetCode=FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),netCode);

			if(InterfaceUtil.isNullString(inNetCode))
			{
				_log.error(methodName,"Mapping of Netowrk code " +netCode +" is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5INHandler["+methodName+"]","","" , (String) p_requestMap.get("NETWORK_CODE"), "Mapping of Netowrk code " +netCode +" is not defined in the INFile");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INTERFACE_NETW_CODE_NOT_DEF);
			}
			String originTransactionId=transactionId.substring(2);		
			transactionId=inNetCode+originTransactionId;

		}
		catch(Exception e)
		{
			transactionId=(String)p_requestMap.get("TRANSACTION_ID");
			_log.errorTrace(methodName, e);
		}
		finally
		{
		}
		return transactionId;
	}

}
