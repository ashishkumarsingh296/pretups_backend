/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroCollectionEnqWS;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.inter.claroCollectionEnqWS.scheduler.NodeManager;
import com.inter.claroCollectionEnqWS.scheduler.NodeScheduler;
import com.inter.claroCollectionEnqWS.scheduler.NodeVO;
import com.inter.claroCollectionEnqWS.stub.ConsultaDeuda;
import com.inter.claroCollectionEnqWS.stub.ConsultaPagos_PortType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author Vipan Kumar
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroCollEnqWSINHandler implements InterfaceHandler{	
	private Log _log = LogFactory.getLog(ClaroCollEnqWSINHandler.class.getName());
	private ClaroCollEnqWSRequestFormatter _formatter=null;
	private ClaroCollEnqWSResponseParser _parser=null;
	private HashMap _requestMap = null;//Contains the request parameter as key and value pair.
	private HashMap _responseMap = null;//Contains the response of the request as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inReconID=null;//Used to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;//Used to store the reference of transaction id.	

	public void validityAdjust (HashMap p_map) throws BTSLBaseException,Exception
	{}

	/**
	 * 
	 */
	public ClaroCollEnqWSINHandler() {
		_formatter=new ClaroCollEnqWSRequestFormatter();
		_parser=new ClaroCollEnqWSResponseParser();	
	}

	/**
	 *@author vipan.kumar
	 * This method is used to validate the subscriber
	 * 
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		String amountStr="";
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	
			if(!BTSLUtil.isNullString(_msisdn))
			{
				InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			

			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("SOURCE_TYPE")+"_VAL"+"_"+_requestMap.get("USER_TYPE"));

			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=getINReconID();
			//_requestMap.put("IN_TXN_ID",_referenceID);
			if("N".equals(validateRequired))
			{
				//Setting default Response; 
				
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("ACCOUNT_STATUS",FileCache.getValue(_interfaceID,"ACCOUNT_STATUS"));
			    _requestMap.put("SERVICE_CLASS",FileCache.getValue(_interfaceID,"SERVICE_CLASS"));
				return ;
			}	
			setInterfaceParameters(ClaroCollEnqWSI.ACTION_ACCOUNT_DETAILS);

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("validate","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor=multFactor.trim();

			//sending the AccountInfo request to IN along with validate action defined in interface
			sendRequestToIN(ClaroCollEnqWSI.ACTION_ACCOUNT_DETAILS);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			copyMaps();
			_requestMap.put("CREDIT_REQUIRED",FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_CREDITREQUIRED"));
			_requestMap.put("ACCOUNT_STATUS",FileCache.getValue(_interfaceID,"ACCOUNT_STATUS"));
			_requestMap.put("SERVICE_CLASS",FileCache.getValue(_interfaceID,"SERVICE_CLASS"));
			
		}
		catch (BTSLBaseException be)
		{
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ClaroCollEnqWSINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get BTSL Exception e:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.VALIDATION_ERROR);
			throw new BTSLBaseException(this,"validate", be.getMessage()); 	   	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ClaroCollEnqWSINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollEnqWSINHandler[validate]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}

	/**
	 * This method is used to credit the Subscriber
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
		int retryCountCredit=0;
		_requestMap = p_requestMap;
		try
		{
			// For LOGS 
			_requestMap.put("IN_START_TIME","0");
			_requestMap.put("IN_END_TIME","0");
			_requestMap.put("IN_RECHARGE_TIME","0");
			_requestMap.put("IN_CREDIT_VAL_TIME","0");

			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");		
			_inReconID=getINReconID();
			//		_requestMap.put("IN_TXN_ID",_referenceID);

			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}

			String topRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("SOURCE_TYPE")+"_TOP"+"_"+_requestMap.get("USER_TYPE"));

			if("N".equals(topRequired))
			{
				//Setting default Response; 
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				//_requestMap.put("INTERFACE_PREV_BALANCE",_responseMap.get("INTERFACE_PREV_BALANCE"));
				//_requestMap.put("SERVICE_CLASS",_responseMap.get("SERVICE_CLASS"));
				//_requestMap.put("OLD_EXPIRY_DATE",_responseMap.get("OLD_EXPIRY_DATE"));
			//	_requestMap.put("ACCOUNT_STATUS",_responseMap.get("ACCOUNT_STATUS"));
				return ;
			}
			
			
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("credit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroCollEnqWSI.ACTION_RECHARGE_CREDIT);

			try
			{
				multFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroCollEnqWSINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroDataWSTRI interface
			sendRequestToIN(ClaroCollEnqWSI.ACTION_RECHARGE_CREDIT);            
			//set IN_RECHARGE_STATUS as Success in request map
			_requestMap.put("RECHARGE_ENQUIRY", "N"); 
			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS); 

			_requestMap.put("INTERFACE_STATUS", _responseMap.get("INTERFACE_STATUS"));
			_requestMap.put("INTERFACE_POST_BALANCE", "0");
			_requestMap.put("INTERFACE_PRE_BALANCE", "0");
			_requestMap.put("NEW_EXPIRY_DATE", _requestMap.get("newexpirydate"));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");

		} 
		catch (BTSLBaseException be)
		{
			p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"credit", be.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollEnqWSINHandler[credit]","credit complete."," _requestMap string:"+_requestMap.toString(),"","");
		}

		if (_log.isDebugEnabled())_log.debug("credit","Exiting _interfaceID"+_interfaceID+"_referenceID"+_referenceID+"_msisdn"+_msisdn+"requestMap="+_requestMap.toString()); 
	}



	/**
	 * 
	 */ 
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("creditAdjust","Entered p_requestMap: " + p_requestMap);
		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
		int retryCountCreditAdjust=0;
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");	
			_inReconID=getINReconID();
			//_requestMap.put("IN_TXN_ID",_referenceID);
			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroCollEnqWSI.ACTION_IMMEDIATE_CREDIT);

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("creditAdjust","multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("creditAdjust","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);			
			try
			{
				multFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroCollEnqWSINHandler[creditAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[creditAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}

			try
			{
				int credit_validity_days=Integer.parseInt((String)_requestMap.get("VALIDITY_DAYS"));
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE,credit_validity_days);

				if(calendar.getTimeInMillis()>Long.parseLong(((String)_requestMap.get("CAL_OLD_EXPIRY_DATE"))))
				{
					_requestMap.put("CAL_OLD_EXPIRY_DATE",String.valueOf(calendar.getTimeInMillis()));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("creditAdjust","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[creditAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Unable to set the expiry date");
				throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroDataWSTRI interface
			sendRequestToIN(ClaroCollEnqWSI.ACTION_IMMEDIATE_CREDIT);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("CREDIT_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("CREDIT_ENQUIRY", "N");
		} 
		catch (BTSLBaseException be)
		{

			_log.error("creditAdjust","BTSLBaseException be:"+be.getMessage());    		   		
			_requestMap.put("CREDIT_STATUS",be.getMessage());
			_requestMap.put("TRANSACTION_STATUS",be.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.AMBIGOUS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("creditAdjust", "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[creditAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit adjust, get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled()) TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollEnqWSINHandler[creditAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}  


	}

	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("debitAdjust","Entered p_requestMap: " + p_requestMap);
		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
		int retryCountDebit=0;
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");	
			_inReconID=getINReconID();
			//_requestMap.put("IN_TXN_ID",_referenceID);
			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("debitAdjust","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroCollEnqWSI.ACTION_IMMEDIATE_DEBIT);

			try
			{
				multFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroCollEnqWSINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				_log.error("debitAdjust","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroDataWSTRI interface
			sendRequestToIN(ClaroCollEnqWSI.ACTION_IMMEDIATE_DEBIT);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("DEBIT_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("DEBIT_ENQUIRY", "N");
		} 
		catch (BTSLBaseException be)
		{
			_log.error("debitAdjust","BTSLBaseException be:"+be.getMessage());    		   		
			retryCountDebit=Integer.parseInt((String)_requestMap.get("RETRY_COUNT_DEBIT"));
			_requestMap.put("DEBIT_STATUS", be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.AMBIGOUS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("debitAdjust", "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debit adjust, get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollEnqWSINHandler[debitAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}         
	}

	/**
	 * This method used to send the request to the IN
	 * @param p_action
	 * @throws BTSLBaseException
	 */
	private void sendRequestToIN(int p_action) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+p_action+" __msisdn="+_msisdn);

		String actionLevel="";
		switch(p_action)
		{
		case ClaroCollEnqWSI.ACTION_ACCOUNT_DETAILS:
		{
			actionLevel="ACTION_ACCOUNT_DETAILS";
			break;
		}
		case ClaroCollEnqWSI.ACTION_RECHARGE_CREDIT:
		{
			actionLevel="ACTION_RECHARGE_CREDIT";
			break;
		}
		} 
		if(!BTSLUtil.isNullString(_msisdn))
		{
			InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);
		}
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+actionLevel+" __msisdn="+_msisdn);
		long startTime=0,endTime=0,sleepTime,warnTime=0;
		ConsultaPagos_PortType clientStub=null;

		NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;
		int readTimeOut=0;
		ClaroCollEnqWSConnectionManager serviceConnection =null;
		try
		{
			//Get the start time when the request is send to IN.
			nodeScheduler = NodeManager.getScheduler(_interfaceID);
			//Get the retry number from the object that is used to retry the getNode in case connection is failed.
			retryNumber = nodeScheduler.getRetryNum();
			//check if NodeScheduler is null throw exception.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
			if(nodeScheduler==null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);
			for(int loop=1;loop<=retryNumber;loop++)
			{
				try
				{
					nodeVO = nodeScheduler.getNodeVO(_inReconID);
					TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollEnqWSINHandler[sendRequestToIN]",PretupsI.TXN_LOG_REQTYPE_REQ,"Node information NodeVO:"+nodeVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
					_requestMap.put("IN_URL", nodeVO.getUrl());
					//Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
					if(nodeVO==null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );
					warnTime=nodeVO.getWarnTime();
					readTimeOut=nodeVO.getReadTimeOut();
					//Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
					serviceConnection = new ClaroCollEnqWSConnectionManager(nodeVO,_interfaceID);
					//break the loop on getting the successfull connection for the node;		            
					clientStub =serviceConnection.getService();			
					if(clientStub==null)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object");
						_log.error("sendRequestToIN","Unable to get Client Object");
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
					}		            
					try
					{
						try
						{
							startTime=System.currentTimeMillis();
							_requestMap.put("IN_START_TIME",String.valueOf(startTime));
							switch(p_action)
							{	
							case ClaroCollEnqWSI.ACTION_ACCOUNT_DETAILS: 
							{
								Object object =_formatter.generateRequest(ClaroCollEnqWSI.ACTION_ACCOUNT_DETAILS,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.consultaDeuda((ConsultaDeuda)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								break;	
							}
							}
						}				
						catch(java.rmi.RemoteException re)
						{
							re.printStackTrace();
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RemoteException Error Message:"+re.getMessage());
							String respCode=null;
							//parse error code 
							String requestStr=re.getMessage();
							int index=requestStr.indexOf("<ErrorCode>");
							if(index ==-1)
							{
								if(re.getMessage().contains("java.net.ConnectException"))
								{
									//In case of connection failure 
									//1.Decrement the connection counter
									//2.set the Node as blocked 
									//3.set the blocked time
									//4.Handle the event with level INFO, show the message that Node is blocked for some time (expiry time).
									//Continue the retry loop till success;
									//Check if the max retry attempt is reached raise exception with error code.
									_log.error("sendRequestToIN","RMI java.net.ConnectException while creating connection re::"+re.getMessage());
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollEnqWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroDataWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

									_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
									nodeVO.incrementBarredCount();
									nodeVO.setBlocked(true);
									nodeVO.setBlokedAt(System.currentTimeMillis());
									if(loop==retryNumber)
									{
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollEnqWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
										throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
									}
									continue;
								}
								else if (re.getMessage().contains("java.net.SocketTimeoutException"))
								{
									re.printStackTrace();
									if(re.getMessage().contains("connect"))
									{
										//In case of connection failure 
										//1.Decrement the connection counter
										//2.set the Node as blocked 
										//3.set the blocked time
										//4.Handle the event with level INFO, show the message that Node is blocked for some time (expiry time).
										//Continue the retry loop till success;
										//Check if the max retry attempt is reached raise exception with error code.
										_log.error("sendRequestToIN","RMI java.net.ConnectException while creating connection re::"+re.getMessage());
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollEnqWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroDataWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

										_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
										nodeVO.incrementBarredCount();
										nodeVO.setBlocked(true);
										nodeVO.setBlokedAt(System.currentTimeMillis());

										if(loop==retryNumber)
										{
											EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollEnqWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
											throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										}
										continue;									
									}									
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketTimeoutException Message:"+re.getMessage());
									_log.error("sendRequestToIN","RMI java.net.SocketTimeoutException Error Message :"+re.getMessage());			    	
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								else if(re.getMessage().contains("java.net.SocketException"))
								{
									re.printStackTrace();
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketException Message:"+re.getMessage());
									_log.error("sendRequestToIN","RMI java.net.SocketException Error Message :"+re.getMessage());			    	
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								else
									throw new Exception(re);
							}							  
							respCode=requestStr.substring(index+"<ErrorCode>".length(),requestStr.indexOf("</ErrorCode>",index));

							index=requestStr.indexOf("<ErrorDescription>");
							String respCodeDesc=requestStr.substring(index+"<ErrorDescription>".length(),requestStr.indexOf("</ErrorDescription>",index));
							_log.error("sendRequestToIN","Error Message respCode="+respCode+"  respCodeDesc:"+respCodeDesc);
							_requestMap.put("INTERFACE_STATUS",respCode);
							_requestMap.put("INTERFACE_DESC",respCodeDesc);								
							_log.error("sendRequestToIN","Error Message respCode="+respCode+"  respCodeDesc:"+respCodeDesc);
							throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

						}
						catch(SocketTimeoutException se)
						{
							se.printStackTrace();
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"SocketTimeoutException Error Message:"+se.getMessage());
							_log.error("sendRequestToIN","SocketTimeoutException Error Message :"+se.getMessage());			    	
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Exception Error Message:"+e.getMessage());
							_log.error("sendRequestToIN","Exception Error Message :"+e.getMessage());
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						}
						finally
						{
							endTime=System.currentTimeMillis();
							nodeVO.resetBarredCount();	
						}

					}
					catch(BTSLBaseException be)
					{
						throw be;
					}
					catch(Exception e)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Error Message:"+e.getMessage());
						_log.error("sendRequestToIN","Error Message :"+e.getMessage());
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
					}
					finally
					{
						if(endTime==0) endTime=System.currentTimeMillis();
						_requestMap.put("IN_END_TIME",String.valueOf(endTime));			        
						_log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime);
					}

					if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Connection of _interfaceID ["+_interfaceID+"] for the Node Number ["+nodeVO.getNodeNumber()+"] created after the attempt number(loop)::"+loop);
					break;
				}
				catch(BTSLBaseException be)
				{
					_log.error("sendRequestToIN","BTSLBaseException be::"+be.getMessage());
					throw be;//Confirm should we come out of loop or do another retry
				}//end of catch-BTSLBaseException
				catch(Exception e)
				{
					_log.error("sendRequestToIN","Exception be::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}//end of catch-Exception            
			}            
			_responseMap=_parser.parseResponse(p_action,_requestMap);
			//put value of response
			TransactionLog.log( _interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response Map: "+_responseMap ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
			//Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled
			if(endTime-startTime>=warnTime)
			{
				_log.info("sendRequestToIN", "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"ClaroCollEnqWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"ClaroDataWS IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
			}
			
			//FIXME Modified by Emmanuel Leguizamon. Nullpointer when ACTION_RECHARGE_CREDIT
			if(_responseMap == null) {
			    _responseMap = new HashMap();
			    _responseMap.put("INTERFACE_STATUS", "0");
			}
			String status=(String)_responseMap.get("INTERFACE_STATUS");
			_requestMap.put("INTERFACE_STATUS",status);

		}
		catch(BTSLBaseException be)
		{
			_log.error("sendRequestToIN","BTSLBaseException be = "+be.getMessage());
			throw be;
		}//end of BTSLBaseException
		catch(Exception e)
		{
			e.printStackTrace();	    
			// FIXME modified by Emmanuel Leguizamon to get more failure detail
			_log.error("sendRequestToIN Exception="+e.getMessage(), e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroAPIINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			_requestMap.remove("RESPONSE_OBJECT");
			clientStub=null;
			serviceConnection=null;
			if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Exiting p_action="+p_action);
		}//end of finally
	}

	/**
	 * @author vipan.kumar
	 * @param p_action
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public void setInterfaceParameters(int p_action) throws BTSLBaseException,Exception 
	{
		if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered Action ="+p_action);
		try
		{        	

			String nroReferencia = (String)_requestMap.get("TRANSACTION_ID");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","nroReferencia:"+nroReferencia);
			if(InterfaceUtil.isNullString(nroReferencia))
			{
				_log.error("setInterfaceParameters","nroReferencia  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "nroReferencia  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nroReferencia = nroReferencia.trim();
			_requestMap.put("NROREFERENCIA",nroReferencia);

			String plaza = FileCache.getValue(_interfaceID,"Plaza");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","plaza:"+plaza);
			if(InterfaceUtil.isNullString(plaza))
			{
				_log.error("setInterfaceParameters","plaza  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "plaza  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			plaza = plaza.trim();
			_requestMap.put("PLAZA",plaza);

			String nroTerminal = FileCache.getValue(_interfaceID,"NroTerminal");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","nroTerminal:"+nroTerminal);
			if(InterfaceUtil.isNullString(nroTerminal))
			{
				_log.error("setInterfaceParameters","nroTerminal  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "nroTerminal  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nroTerminal = nroTerminal.trim();
			_requestMap.put("NROTERMINAL",nroTerminal);

			String codCiudad = FileCache.getValue(_interfaceID,"CodCiudad");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codCiudad:"+codCiudad);
			if(InterfaceUtil.isNullString(codCiudad))
			{
				_log.error("setInterfaceParameters","codCiudad  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codCiudad  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codCiudad = codCiudad.trim();
			_requestMap.put("CODCIUDAD",codCiudad);

			String codCanal = FileCache.getValue(_interfaceID,"CodCanal");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codCanal:"+codCanal);
			if(InterfaceUtil.isNullString(codCanal))
			{
				_log.error("setInterfaceParameters","codCanal  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codCanal  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codCanal = codCanal.trim();
			_requestMap.put("CODCANAL",codCanal);

			String codAgencia = FileCache.getValue(_interfaceID,"CodAgencia");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codAgencia:"+codAgencia);
			if(InterfaceUtil.isNullString(codAgencia))
			{
				_log.error("setInterfaceParameters","codAgencia  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codAgencia  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codAgencia = codAgencia.trim();
			_requestMap.put("CODAGENCIA",codAgencia);


			String tipoIdentific = FileCache.getValue(_interfaceID,"TipoIdentific");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","TipoIdentific:"+tipoIdentific);
			if(InterfaceUtil.isNullString(tipoIdentific))
			{
				_log.error("setInterfaceParameters","TipoIdentific  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "TipoIdentific  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			tipoIdentific = tipoIdentific.trim();
			_requestMap.put("TIPOIDETIFIC",tipoIdentific);

			String posUltDocumento = FileCache.getValue(_interfaceID,"PosUltDocumento");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","posUltDocumento:"+posUltDocumento);
			if(InterfaceUtil.isNullString(posUltDocumento))
			{
				_log.error("setInterfaceParameters","posUltDocumento  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "posUltDocumento  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			posUltDocumento = posUltDocumento.trim();
			_requestMap.put("POSUITDOCUMENTO",posUltDocumento);

			String codMoneda = FileCache.getValue(_interfaceID,"CodMoneda");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codMoneda:"+codMoneda);
			if(InterfaceUtil.isNullString(codMoneda))
			{
				_log.error("setInterfaceParameters","codMoneda  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codMoneda  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codMoneda = codMoneda.trim();
			_requestMap.put("CODMONEDA",codMoneda);

			String codReenvia = (String)_requestMap.get("COMPANY");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codReenvia:"+codReenvia);
			if(InterfaceUtil.isNullString(codReenvia))
			{
				_log.error("setInterfaceParameters","codReenvia  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codReenvia  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codReenvia = codReenvia.trim();
			_requestMap.put("CODREENVIA",codReenvia);

			String codBanco = (String)_requestMap.get("COMPANY");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codBanco:"+codBanco);
			if(InterfaceUtil.isNullString(codBanco))
			{
				_log.error("setInterfaceParameters","codBanco  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codBanco  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codBanco = codBanco.trim();
			_requestMap.put("CODBANCO",codBanco);


			String codAplicacion = FileCache.getValue(_interfaceID,"CodAplicacion");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codAplicacion:"+codAplicacion);
			if(InterfaceUtil.isNullString(codAplicacion))
			{
				_log.error("setInterfaceParameters","codAplicacion  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollEnqWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codAplicacion  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codAplicacion = codAplicacion.trim();
			_requestMap.put("CODAPLICACION",codAplicacion);


			String nombreComercio = (String)_requestMap.get("OWNER_NAME");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","User Name:"+nombreComercio);
			if(InterfaceUtil.isNullString(nombreComercio))
			{
				_log.error("setInterfaceParameters","nombreComercio  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "User Name  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nombreComercio = nombreComercio.trim();
			_requestMap.put("NOMBRECOMERCIO",nombreComercio);	


			String numeroComercio = (String)_requestMap.get("OWNER_ID");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","User Id:"+numeroComercio);
			if(InterfaceUtil.isNullString(numeroComercio))
			{
				_log.error("setInterfaceParameters","numeroComercio  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "User Id  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			numeroComercio = numeroComercio.trim();
			_requestMap.put("NUMEROCOMERCIO",numeroComercio);	

			String inSubServiceName="";
			try{
				inSubServiceName= FileCache.getValue(_interfaceID,(String)_requestMap.get("SUB_SERVICE")+"_"+_requestMap.get("REQ_SERVICE"));
				if(BTSLUtil.isNullString(inSubServiceName))
				{
					inSubServiceName=_requestMap.get("SUB_SERVICE").toString()	;
				}
				
			}catch (Exception e) {
				inSubServiceName=_requestMap.get("SUB_SERVICE").toString();
			}
			
			String codTipoServicio = inSubServiceName;
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codTipoServicio:"+codTipoServicio);
			if(InterfaceUtil.isNullString(codTipoServicio))
			{
				_log.error("setInterfaceParameters","codTipoServicio  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codTipoServicio  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codTipoServicio = codTipoServicio.trim();
			_requestMap.put("CODTIPOSERVICIO",codTipoServicio);	

		}//end of try block
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			_log.error("setInterfaceParameters",e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), e.getMessage());
			throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			if (_log.isDebugEnabled())_log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
		}//end of finally
	}


	/**
	 * Method to send cancel request to IN for any ambiguous transaction.
	 * This method also makes reconciliation log entry. 	
	 * @throws	BTSLBaseException 
	 */
	//private void handleCancelTransaction() throws BTSLBaseException
	// {}

	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat ("ss");
	private static int  _txnCounter = 1;
	private static int  _prevSec=0;
	public int IN_TRANSACTION_ID_PAD_LENGTH=2;
	

	public synchronized String getINReconID() throws BTSLBaseException
	{
		//This method will be used when we have transID based on database sequence.
		String inTransactionID="";
		try
		{
			String secToCompare=null;
			Date mydate = null;

			mydate = new Date();

			secToCompare = _sdfCompare.format(mydate);
			int currentSec=Integer.parseInt(secToCompare);  		

			if(currentSec !=_prevSec)
			{
				_txnCounter=1;
				_prevSec=currentSec;
			}
			else if(_txnCounter >= 99)
			{
				_txnCounter=1;	  			 
			}
			else
			{
				_txnCounter++;  			 
			}
			if(_txnCounter==0)
				throw new BTSLBaseException("this","getINReconID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);


			inTransactionID=BTSLUtil.padZeroesToLeft(String.valueOf(Constants.getProperty("INSTANCE_ID")),IN_TRANSACTION_ID_PAD_LENGTH)+currentTimeFormatStringTillSec(mydate)+BTSLUtil.padZeroesToLeft(String.valueOf(_txnCounter),IN_TRANSACTION_ID_PAD_LENGTH);
		}	
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			_requestMap.put("IN_RECON_ID",inTransactionID);
			_requestMap.put("IN_TXN_ID",inTransactionID);
			return inTransactionID;
		}
	}
	
	public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat ("hhmmss");
		String dateString = sdf.format(p_date);
		return dateString;
	}
	/**
	 * @author vipan.kumar
	 * Copy Map Value 
	 */
	private void copyMaps()
	{
		if (_log.isDebugEnabled()) _log.debug("compareMaps()","Entered requestMap "+_requestMap+"  Response Map"+_responseMap);
		try
		{
			Iterator resIterator = null;
			resIterator = _responseMap.keySet().iterator();
			while (resIterator.hasNext()) 
			{
				String key = (String)resIterator.next();
				if(!_requestMap.containsKey(key))
				{
					_requestMap.put(key, _responseMap.get(key));
				}
			}
		}
		catch(Exception e)
		{
			_log.error("Error in compareMaps",e.getMessage());
			e.printStackTrace();
		}
		if (_log.isDebugEnabled()) _log.debug("compareMaps()","Exit requestMap "+_requestMap);

	}

}

