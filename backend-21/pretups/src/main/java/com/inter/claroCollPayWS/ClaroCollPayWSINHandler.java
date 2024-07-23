/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroCollPayWS;

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
import com.inter.claroCollPayWS.scheduler.NodeManager;
import com.inter.claroCollPayWS.scheduler.NodeScheduler;
import com.inter.claroCollPayWS.scheduler.NodeVO;
import com.inter.claroCollPayWS.stub.CrearAnulacion;
import com.inter.claroCollPayWS.stub.CrearPago;
import com.inter.claroCollPayWS.stub.TransaccionPagos_PortType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author Vipan Kumar
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroCollPayWSINHandler implements InterfaceHandler{	
	private Log _log = LogFactory.getLog(ClaroCollPayWSINHandler.class.getName());
	private ClaroCollPayWSRequestFormatter _formatter=null;
	private ClaroCollPayWSResponseParser _parser=null;
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
	public ClaroCollPayWSINHandler() {
		_formatter=new ClaroCollPayWSRequestFormatter();
		_parser=new ClaroCollPayWSResponseParser();	
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
		String promoAmountStr="";
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	
			if(!BTSLUtil.isNullString(_msisdn))
			{
				InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}


			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.

			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=getINReconID();
			_requestMap.put("IN_TXN_ID",_referenceID);

			if("N".equals(validateRequired))
			{
				//Setting default Response; 
				_responseMap=_parser.parseResponse(ClaroCollPayWSI.ACTION_ACCOUNT_DETAILS, _requestMap);
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("INTERFACE_PREV_BALANCE",_responseMap.get("INTERFACE_PREV_BALANCE"));
				_requestMap.put("SERVICE_CLASS",_responseMap.get("SERVICE_CLASS"));
				_requestMap.put("OLD_EXPIRY_DATE",_responseMap.get("OLD_EXPIRY_DATE"));
				_requestMap.put("ACCOUNT_STATUS",_responseMap.get("ACCOUNT_STATUS"));
				return ;
			}

			setInterfaceParameters(ClaroCollPayWSI.ACTION_ACCOUNT_DETAILS);

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("validate","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseTGINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor=multFactor.trim();


			//sending the AccountInfo request to IN along with validate action defined in interface
			sendRequestToIN(ClaroCollPayWSI.ACTION_ACCOUNT_DETAILS);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);

			//get value of BALANCE from response map (BALANCE was set in response map in sendRequestToIN method.)
			try
			{
				amountStr=(Double)_responseMap.get("RESP_BALANCE")+"";
				amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
				_requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("validate","Exception e:"+e.getMessage()+" amountStr:"+amountStr);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric amountStr=:"+amountStr+", while parsing the Balance get Exception e:"+e.getMessage());
				throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
			}

			String expdate=BTSLUtil.getDateTimeStringFromDate(((Calendar)_responseMap.get("OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
			_requestMap.put("OLD_EXPIRY_DATE",expdate);
			_requestMap.put("ACCOUNT_STATUS",(String)_responseMap.get("ACCOUNT_STATUS"));
			_requestMap.put("SERVICE_CLASS",(String)_responseMap.get("SERVICE_CLASS"));            
			
		}
		catch (BTSLBaseException be)
		{
			_log.error("validate","BTSLBaseException be="+be.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATION_ERROR); 	   	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ClaroCollPayWSINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollPayWSINHandler[validate]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
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
				_log.error("credit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroCollPayWSI.ACTION_RECHARGE_CREDIT);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroCollPayWSINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("PAGOTOTAL",amountStr);
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroDTHWSTRI interface
			sendRequestToIN(ClaroCollPayWSI.ACTION_RECHARGE_CREDIT);            
			//set IN_RECHARGE_STATUS as Success in request map
			_requestMap.put("RECHARGE_ENQUIRY", "N"); 
			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS); 

			_requestMap.put("INTERFACE_STATUS", _responseMap.get("INTERFACE_STATUS"));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

		} 
		catch (BTSLBaseException be)
		{
			p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
				e.printStackTrace();
				_log.error("credit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Claro[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollPayWSINHandler[credit]","credit complete."," _requestMap string:"+_requestMap.toString(),"","");
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
			_requestMap.put("IN_TXN_ID",_referenceID);
			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroCollPayWSI.ACTION_IMMEDIATE_CREDIT);

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("creditAdjust","multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("creditAdjust","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroCollPayWSINHandler[creditAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[creditAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[creditAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Unable to set the expiry date");
				throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroDataWSTRI interface
			sendRequestToIN(ClaroCollPayWSI.ACTION_IMMEDIATE_CREDIT);
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[creditAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit adjust, get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled()) TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollPayWSINHandler[creditAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}  


	}

	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("debitAdjust","Entered p_requestMap: " + p_requestMap);
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroCollPayWSI.ACTION_IMMEDIATE_DEBIT);

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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroCollPayWSINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
				}
				
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
					amountStr=String.valueOf(Math.round(systemAmtDouble));
					_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
					_requestMap.put("IMPORTEPAGO",amountStr);
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("debitAdjust","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("IMPORTEPAGO",amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroDTHWSTRI interface
			sendRequestToIN(ClaroCollPayWSI.ACTION_IMMEDIATE_DEBIT);            
			//set IN_RECHARGE_STATUS as Success in request map
			_requestMap.put("RECHARGE_ENQUIRY", "N"); 
			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS); 

			_requestMap.put("INTERFACE_STATUS", _responseMap.get("INTERFACE_STATUS"));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

		} 
		catch (BTSLBaseException be)
		{
			p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"debitAdjust", be.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollPayWSINHandler[debitAdjust]","debitAdjust complete."," _requestMap string:"+_requestMap.toString(),"","");
		}

		if (_log.isDebugEnabled())_log.debug("debitAdjust","Exiting _interfaceID"+_interfaceID+"_referenceID"+_referenceID+"_msisdn"+_msisdn+"requestMap="+_requestMap.toString()); 
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
		case ClaroCollPayWSI.ACTION_ACCOUNT_DETAILS:
		{
			actionLevel="ACTION_ACCOUNT_DETAILS";
			break;
		}
		case ClaroCollPayWSI.ACTION_RECHARGE_CREDIT:
		{
			actionLevel="ACTION_RECHARGE_CREDIT";
			break;
		}
		case ClaroCollPayWSI.ACTION_IMMEDIATE_DEBIT:
		{
			actionLevel="ACTION_IMMEDIATE_DEBIT";
			break;
		}
		} 
		if(!BTSLUtil.isNullString(_msisdn))
		{
			InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);
		}
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+actionLevel+" __msisdn="+_msisdn);
		long startTime=0,endTime=0,sleepTime,warnTime=0;
		TransaccionPagos_PortType clientStub=null;

		NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;
		int readTimeOut=0;
		ClaroCollPayWSConnectionManager serviceConnection =null;
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
					TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroCollPayWSINHandler[sendRequestToIN]",PretupsI.TXN_LOG_REQTYPE_REQ,"Node information NodeVO:"+nodeVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
					_requestMap.put("IN_URL", nodeVO.getUrl());
					//Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
					if(nodeVO==null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );
					warnTime=nodeVO.getWarnTime();
					readTimeOut=nodeVO.getReadTimeOut();
					//Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
					serviceConnection = new ClaroCollPayWSConnectionManager(nodeVO,_interfaceID);
					//break the loop on getting the successfull connection for the node;		            
					clientStub =serviceConnection.getService();			
					if(clientStub==null)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object");
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
							case ClaroCollPayWSI.ACTION_RECHARGE_CREDIT: 
							{
								Object object =_formatter.generateRequest(ClaroCollPayWSI.ACTION_RECHARGE_CREDIT,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.crearPago((CrearPago)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								break;	
							}
							case ClaroCollPayWSI.ACTION_IMMEDIATE_DEBIT: 
							{
								Object object =_formatter.generateRequest(ClaroCollPayWSI.ACTION_IMMEDIATE_DEBIT,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.crearAnulacion((CrearAnulacion)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								break;	
							}
							}
						}				
						catch(java.rmi.RemoteException re)
						{
							re.printStackTrace();
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RemoteException Error Message:"+re.getMessage());
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
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollPayWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroDataWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

									_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
									nodeVO.incrementBarredCount();
									nodeVO.setBlocked(true);
									nodeVO.setBlokedAt(System.currentTimeMillis());
									if(loop==retryNumber)
									{
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollPayWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
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
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollPayWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroDataWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

										_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
										nodeVO.incrementBarredCount();
										nodeVO.setBlocked(true);
										nodeVO.setBlokedAt(System.currentTimeMillis());

										if(loop==retryNumber)
										{
											EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroCollPayWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
											throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										}
										continue;									
									}									
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketTimeoutException Message:"+re.getMessage());
									_log.error("sendRequestToIN","RMI java.net.SocketTimeoutException Error Message :"+re.getMessage());			    	
									 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								else if(re.getMessage().contains("java.net.SocketException"))
								{
									re.printStackTrace();
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketException Message:"+re.getMessage());
									_log.error("sendRequestToIN","RMI java.net.SocketException Error Message :"+re.getMessage());			    	
									 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
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
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"SocketTimeoutException Error Message:"+se.getMessage());
							_log.error("sendRequestToIN","SocketTimeoutException Error Message :"+se.getMessage());
							 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Exception Error Message:"+e.getMessage());
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
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Error Message:"+e.getMessage());
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
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"ClaroCollPayWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"ClaroDataWS IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
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
			_log.error("sendRequestToIN","Exception="+e.getMessage());
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

			String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
			if(InterfaceUtil.isNullString(cancelTxnAllowed))
			{
				_log.error("setInterfaceParameters","Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());

		
			String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
			if(InterfaceUtil.isNullString(systemStatusMappingCr))
			{
				_log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());


			String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
			if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
			{
				_log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());


			String cancelNA = FileCache.getValue(_interfaceID,"CANCEL_NA");
			if(InterfaceUtil.isNullString(cancelNA))
			{
				_log.error("setInterfaceParameters","Value of CANCEL_NA is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_NA",cancelNA.trim());
			
			if(p_action == ClaroCollPayWSI.ACTION_RECHARGE_CREDIT)
			{
				String numeroDoc = (String)_requestMap.get("INVOICE_NUMBER");
				if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","numeroDoc:"+numeroDoc);
				if(InterfaceUtil.isNullString(numeroDoc))
				{
					_log.error("setInterfaceParameters","numeroDoc  is not defined in the INFile");
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "numeroDoc  is not defined in the INFile");
					throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				numeroDoc = numeroDoc.trim();
				_requestMap.put("NUMERODOC",numeroDoc);
			}
			String codAplicacion = FileCache.getValue(_interfaceID,"CodAplicacion");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codAplicacion:"+codAplicacion);
			if(InterfaceUtil.isNullString(codAplicacion))
			{
				_log.error("setInterfaceParameters","codAplicacion  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codAplicacion  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codAplicacion = codAplicacion.trim();
			_requestMap.put("CODAPLICACION",codAplicacion);

			String codBanco = (String)_requestMap.get("COMPANY");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codBanco:"+codBanco);
			if(InterfaceUtil.isNullString(codBanco))
			{
				_log.error("setInterfaceParameters","codBanco  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codBanco  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codBanco = codBanco.trim();
			_requestMap.put("CODBANCO",codBanco);


			
			String codMoneda = FileCache.getValue(_interfaceID,"CodMoneda");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codMoneda:"+codMoneda);
			if(InterfaceUtil.isNullString(codMoneda))
			{
				_log.error("setInterfaceParameters","codMoneda  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codMoneda  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codMoneda = codMoneda.trim();
			_requestMap.put("CODMONEDA",codMoneda);

			String codReenvia = (String)_requestMap.get("COMPANY");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codReenvia:"+codReenvia);
			if(InterfaceUtil.isNullString(codReenvia))
			{
				_log.error("setInterfaceParameters","codReenvia  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codReenvia  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codReenvia = codReenvia.trim();
			_requestMap.put("CODREENVIA",codReenvia);


			String tipoIdentific = FileCache.getValue(_interfaceID,"TipoIdentific");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","TipoIdentific:"+tipoIdentific);
			if(InterfaceUtil.isNullString(tipoIdentific))
			{
				_log.error("setInterfaceParameters","TipoIdentific  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "TipoIdentific  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			tipoIdentific = tipoIdentific.trim();
			_requestMap.put("TIPOIDETIFIC",tipoIdentific);
			
			String nroDocs = FileCache.getValue(_interfaceID,"NroDocs");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","NroDocs:"+nroDocs);
			if(InterfaceUtil.isNullString(nroDocs))
			{
				_log.error("setInterfaceParameters","NroDocs  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NroDocs  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nroDocs = nroDocs.trim();
			_requestMap.put("NRODOCS",nroDocs);
			
			String codAgencia = FileCache.getValue(_interfaceID,"CodAgencia");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codAgencia:"+codAgencia);
			if(InterfaceUtil.isNullString(codAgencia))
			{
				_log.error("setInterfaceParameters","codAgencia  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codAgencia  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codAgencia = codAgencia.trim();
			_requestMap.put("CODAGENCIA",codAgencia);


			String codCiudad = FileCache.getValue(_interfaceID,"CodCiudad");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codCiudad:"+codCiudad);
			if(InterfaceUtil.isNullString(codCiudad))
			{
				_log.error("setInterfaceParameters","codCiudad  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codCiudad  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codCiudad = codCiudad.trim();
			_requestMap.put("CODCIUDAD",codCiudad);

			String codCanal = FileCache.getValue(_interfaceID,"CodCanal");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codCanal:"+codCanal);
			if(InterfaceUtil.isNullString(codCanal))
			{
				_log.error("setInterfaceParameters","codCanal  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "codCanal  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			codCanal = codCanal.trim();
			_requestMap.put("CODCANAL",codCanal);

			String nroTerminal = FileCache.getValue(_interfaceID,"NroTerminal");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","nroTerminal:"+nroTerminal);
			if(InterfaceUtil.isNullString(nroTerminal))
			{
				_log.error("setInterfaceParameters","nroTerminal  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "nroTerminal  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nroTerminal = nroTerminal.trim();
			_requestMap.put("NROTERMINAL",nroTerminal);


			String nroReferencia = (String)_requestMap.get("TRANSACTION_ID");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","nroReferencia:"+nroReferencia);
			if(InterfaceUtil.isNullString(nroReferencia))
			{
				_log.error("setInterfaceParameters","nroReferencia  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "nroReferencia  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nroReferencia = nroReferencia.trim();
			_requestMap.put("NROREFERENCIA",nroReferencia);

			String plaza = FileCache.getValue(_interfaceID,"Plaza");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","plaza:"+plaza);
			if(InterfaceUtil.isNullString(plaza))
			{
				_log.error("setInterfaceParameters","plaza  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroCollPayWSINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "plaza  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			plaza = plaza.trim();
			_requestMap.put("PLAZA",plaza);

			
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

			String extorno =  FileCache.getValue(_interfaceID,"Extorno");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Extorno:"+extorno);
			if(InterfaceUtil.isNullString(extorno))
			{
				_log.error("setInterfaceParameters","extorno  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Extorno  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			extorno = extorno.trim();
			_requestMap.put("EXTORNO",extorno);	


			String trace =  FileCache.getValue(_interfaceID,"Trace");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","trace:"+trace);
			if(InterfaceUtil.isNullString(trace))
			{
				_log.error("setInterfaceParameters","trace  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "trace  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			trace = trace.trim();
			_requestMap.put("TRACE",trace);	

			String nROOPERACION =  FileCache.getValue(_interfaceID,"NROOPERACION");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","nROOPERACION:"+nROOPERACION);
			if(InterfaceUtil.isNullString(nROOPERACION))
			{
				_log.error("setInterfaceParameters","nROOPERACION  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "nROOPERACION  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nROOPERACION = nROOPERACION.trim();
			_requestMap.put("NROOPERACION",nROOPERACION);	
			
			String medioPago =  FileCache.getValue(_interfaceID,"MedioPago");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","medioPago:"+medioPago);
			if(InterfaceUtil.isNullString(medioPago))
			{
				_log.error("setInterfaceParameters","medioPago  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "medioPago  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			medioPago = medioPago.trim();
			_requestMap.put("MEDIOPAGO",medioPago);	
			
			String datoTransaccion =  FileCache.getValue(_interfaceID,"DatoTransaccion");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","datoTransaccion:"+datoTransaccion);
			if(InterfaceUtil.isNullString(datoTransaccion))
				_requestMap.put("DATOTRANSACCION","");	
			
			datoTransaccion = datoTransaccion.trim();
			_requestMap.put("DATOTRANSACCION",datoTransaccion);	

			String codConcepto5 =  FileCache.getValue(_interfaceID,"CodConcepto5");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","codConcepto5:"+codConcepto5);
			if(InterfaceUtil.isNullString(codConcepto5))
				_requestMap.put("CODCONCEPTO5","");	
			codConcepto5 = codConcepto5.trim();
			_requestMap.put("CODCONCEPTO5",codConcepto5);	
			
			String importeConcepto1 =  FileCache.getValue(_interfaceID,"ImporteConcepto1");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","importeConcepto1:"+importeConcepto1);
			_requestMap.put("IMPORTECONCEPTO1","");	
			
			importeConcepto1 = importeConcepto1.trim();
			_requestMap.put("IMPORTECONCEPTO1",importeConcepto1);	


			String importeConcepto2 =  FileCache.getValue(_interfaceID,"ImporteConcepto2");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","importeConcepto2:"+importeConcepto2);
			if(InterfaceUtil.isNullString(importeConcepto2))
				_requestMap.put("IMPORTECONCEPTO2","");	
			
			importeConcepto2 = importeConcepto2.trim();
			_requestMap.put("IMPORTECONCEPTO2",importeConcepto2);	

			String importeConcepto3 =  FileCache.getValue(_interfaceID,"ImporteConcepto3");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","importeConcepto3:"+importeConcepto3);
			if(InterfaceUtil.isNullString(importeConcepto3))
				_requestMap.put("IMPORTECONCEPTO3","");	
			
			importeConcepto3 = importeConcepto3.trim();
			_requestMap.put("IMPORTECONCEPTO3",importeConcepto3);	
			


			String importeConcepto4 =  FileCache.getValue(_interfaceID,"ImporteConcepto4");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","importeConcepto4:"+importeConcepto4);
			_requestMap.put("IMPORTECONCEPTO4","");	
			
			importeConcepto4 = importeConcepto4.trim();
			_requestMap.put("IMPORTECONCEPTO4",importeConcepto4);	
			
			String importeConcepto5 =  FileCache.getValue(_interfaceID,"ImporteConcepto5");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","importeConcepto5:"+importeConcepto5);
			if(InterfaceUtil.isNullString(importeConcepto5))
				_requestMap.put("IMPORTECONCEPTO5",importeConcepto4);	
			
			importeConcepto5 = importeConcepto5.trim();
			_requestMap.put("IMPORTECONCEPTO5",importeConcepto5);	
			
			String estadoDeudor =  FileCache.getValue(_interfaceID,"EstadoDeudor");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","estadoDeudor:"+estadoDeudor);
			if(InterfaceUtil.isNullString(estadoDeudor))
			{
				_log.error("setInterfaceParameters","estadoDeudor  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "estadoDeudor  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			estadoDeudor = estadoDeudor.trim();
			_requestMap.put("ESTADODEUDOR",estadoDeudor);	
			
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


	 /**
    * Method to send cancel request to IN for any ambiguous transaction.
    * This method also makes reconciliation log entry.
    * @throws      BTSLBaseException
    */
   private void handleCancelTransaction() throws BTSLBaseException
   {
           if (_log.isDebugEnabled())_log.debug("handleCancelTransaction", "Entered.");
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
               if (_log.isDebugEnabled())_log.debug("handleCancelTransaction", "reconLog."+reconLog);
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
                                   throw new BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.   ??????)
                           _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
                   }
           }
           catch(BTSLBaseException be)
           {
                   throw be;
           }
           catch(Exception e)
           {
               e.printStackTrace();
               _log.error("handleCancelTransaction","Exception e:"+e.getMessage());
                   throw new BTSLBaseException(this,"handleCancelTransaction",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
           }
           finally
           {
                    if (_log.isDebugEnabled())_log.debug("handleCancelTransaction", "Exited");
           }
   }



}

