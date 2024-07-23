/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroPINRechargeWS;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.inter.claroPINRechargeWS.scheduler.NodeManager;
import com.inter.claroPINRechargeWS.scheduler.NodeScheduler;
import com.inter.claroPINRechargeWS.scheduler.NodeVO;
import com.inter.claroPINRechargeWS.stub.EbsPinVirtual;
import com.inter.claroPINRechargeWS.stub.RecargaPinVirtualRequest;
import com.inter.claroPromoWS.ClaroPromoWSINHandler;
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
public class ClaroPINWSINHandler implements InterfaceHandler{	
	private Log _log = LogFactory.getLog(ClaroPINWSINHandler.class.getName());
	private ClaroPINWSRequestFormatter _formatter=null;
	private ClaroPINWSResponseParser _parser=null;
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
	public ClaroPINWSINHandler() {
		_formatter=new ClaroPINWSRequestFormatter();
		_parser=new ClaroPINWSResponseParser();	
	}

	/**
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
			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.
			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=getINReconID();
			_requestMap.put("IN_TXN_ID",_referenceID);

			if("N".equals(validateRequired))
			{
				//Setting default Response; 
				_responseMap=_parser.parseResponse(ClaroPINWSI.ACTION_ACCOUNT_DETAILS, _requestMap);
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("INTERFACE_PREV_BALANCE",_responseMap.get("INTERFACE_PREV_BALANCE"));
				_requestMap.put("SERVICE_CLASS",_responseMap.get("SERVICE_CLASS"));
				_requestMap.put("OLD_EXPIRY_DATE",_responseMap.get("OLD_EXPIRY_DATE"));
				_requestMap.put("ACCOUNT_STATUS",_responseMap.get("ACCOUNT_STATUS"));
				return ;
			}

			setInterfaceParameters(ClaroPINWSI.ACTION_ACCOUNT_DETAILS);

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("validate","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSTGINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor=multFactor.trim();


			//sending the AccountInfo request to IN along with validate action defined in interface
			sendRequestToIN(ClaroPINWSI.ACTION_ACCOUNT_DETAILS);
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric amountStr=:"+amountStr+", while parsing the Balance get Exception e:"+e.getMessage());
				throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
			}

			String expdate=BTSLUtil.getDateTimeStringFromDate(((Calendar)_responseMap.get("OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
			_requestMap.put("OLD_EXPIRY_DATE",expdate);
			_requestMap.put("ACCOUNT_STATUS",(String)_responseMap.get("ACCOUNT_STATUS"));
			_requestMap.put("SERVICE_CLASS",(String)_responseMap.get("SERVICE_CLASS"));            
			setLanguageFromMapping();
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ClaroPINWSINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPINWSINHandler[validate]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroPINWSI.ACTION_RECHARGE_CREDIT);
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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroPINWSINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroPINWSTRI interface
			sendRequestToIN(ClaroPINWSI.ACTION_RECHARGE_CREDIT);            
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPINWSINHandler[credit]","credit complete."," _requestMap string:"+_requestMap.toString(),"","");
			//For Promotional Start
			if(InterfaceUtil.NullToString((String)_requestMap.get("TRANSACTION_STATUS")).equals(InterfaceErrorCodesI.SUCCESS))
			{
				try
				{
					String bundleIds=(String)p_requestMap.get("BONUS_BUNDLE_IDS");
					if (_log.isDebugEnabled()) _log.debug("credit", "Bundle Id="+bundleIds+", In case of No Bundle ID Promo request will not go to IN.");
					if (!InterfaceUtil.isNullString((String)p_requestMap.get("BONUS_BUNDLE_IDS")))
					{
						String promoFileId = Constants.getProperty("CLARO_PROMO_IN_IDS");
						if(InterfaceUtil.isNullString(promoFileId))
						{
							_log.error("credit","PROMO_INTERFACE_ID  is not defined in the INFile");
							EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PROMO_INTERFACE_ID  is not defined in the INFile");
							throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.AMBIGOUS);
						}
						_requestMap.put("INTERFACE_ID",promoFileId);
						_requestMap=new ClaroPromoWSINHandler().PromoCredit(_requestMap);
						_requestMap.put("INTERFACE_ID",_interfaceID);
						_requestMap.put("IN_PROMO_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
					}
					else
						if(TransactionLog.getLogger().isDebugEnabled())TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPINWSINHandler[credit]","PromotionCredit","Bundle Id="+bundleIds+", No Bundle ID found so no promo request will go to IN.","");
				}
				catch (BTSLBaseException be)
				{
					p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit, get the Base Exception be:"+be.getMessage());
					_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(this,"credit", InterfaceErrorCodesI.AMBIGOUS);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
					_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.AMBIGOUS);
				}
				finally
				{
					//if(_log.isDebugEnabled()) _log.debug("promotion","Exiting with  _requestMap: "+_requestMap);
				}

			}         
			//For Promotional End
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
			setInterfaceParameters(ClaroPINWSI.ACTION_IMMEDIATE_CREDIT);

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("creditAdjust","multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("creditAdjust","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroPINWSINHandler[creditAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[creditAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[creditAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Unable to set the expiry date");
				throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroPINWSTRI interface
			sendRequestToIN(ClaroPINWSI.ACTION_IMMEDIATE_CREDIT);
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[creditAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit adjust, get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled()) TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPINWSINHandler[creditAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
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
			_requestMap.put("IN_TXN_ID",_referenceID);
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(ClaroPINWSI.ACTION_IMMEDIATE_DEBIT);

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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroPINWSINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ClaroPINWSTRI interface
			sendRequestToIN(ClaroPINWSI.ACTION_IMMEDIATE_DEBIT);
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debit adjust, get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPINWSINHandler[debitAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
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
		case ClaroPINWSI.ACTION_ACCOUNT_DETAILS:
		{
			actionLevel="ACTION_ACCOUNT_DETAILS";
			break;
		}
		case ClaroPINWSI.ACTION_RECHARGE_CREDIT:
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
		EbsPinVirtual clientStub=null;

		NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;
		int readTimeOut=0;
		ClaroPINWSConnectionManager serviceConnection =null;
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
					TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPINWSINHandler[sendRequestToIN]",PretupsI.TXN_LOG_REQTYPE_REQ,"Node information NodeVO:"+nodeVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
					_requestMap.put("IN_URL", nodeVO.getUrl());
					//Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
					if(nodeVO==null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );
					warnTime=nodeVO.getWarnTime();
					readTimeOut=nodeVO.getReadTimeOut();
					//Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
					serviceConnection = new ClaroPINWSConnectionManager(nodeVO,_interfaceID);
					//break the loop on getting the successfull connection for the node;		            
					clientStub =serviceConnection.getService();			
					if(clientStub==null)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object");
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
							case ClaroPINWSI.ACTION_ACCOUNT_DETAILS: 
							{
								_requestMap.put("RESPONSE_OBJECT","");
								break;	
							}
							case ClaroPINWSI.ACTION_RECHARGE_CREDIT: 
							{
								Object object =_formatter.generateRequest(ClaroPINWSI.ACTION_RECHARGE_CREDIT,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.recargaPinVirtual((RecargaPinVirtualRequest)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								break;	
							}
							case ClaroPINWSI.ACTION_IMMEDIATE_DEBIT: 
							{
								Object object =_formatter.generateRequest(ClaroPINWSI.ACTION_RECHARGE_CREDIT,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.recargaPinVirtual((RecargaPinVirtualRequest)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								break;	
							}
							}
						}				
						catch(java.rmi.RemoteException re)
						{
							re.printStackTrace();
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RemoteException Error Message:"+re.getMessage());
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
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPINWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroPINWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

									_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
									nodeVO.incrementBarredCount();
									nodeVO.setBlocked(true);
									nodeVO.setBlokedAt(System.currentTimeMillis());
									if(loop==retryNumber)
									{
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPINWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
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
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPINWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroPINWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

										_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
										nodeVO.incrementBarredCount();
										nodeVO.setBlocked(true);
										nodeVO.setBlokedAt(System.currentTimeMillis());

										if(loop==retryNumber)
										{
											EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPINWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
											throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										}
										continue;									
									}									
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketTimeoutException Message:"+re.getMessage());
									_log.error("sendRequestToIN","RMI java.net.SocketTimeoutException Error Message :"+re.getMessage());
									 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								else if(re.getMessage().contains("java.net.SocketException"))
								{
									re.printStackTrace();
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketException Message:"+re.getMessage());
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
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"SocketTimeoutException Error Message:"+se.getMessage());
							_log.error("sendRequestToIN","SocketTimeoutException Error Message :"+se.getMessage());			    	
							 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Exception Error Message:"+e.getMessage());
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
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Error Message:"+e.getMessage());
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
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"ClaroPINWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"ClaroPINWS IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS3CCAPIINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
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
             
			InetAddress addr = InetAddress.getLocalHost();
			String ipAddress = addr.getHostAddress();
			_requestMap.put("IP",ipAddress);

			
			String newExpiryDate = FileCache.getValue(_interfaceID,"NEW_EXPIRY_DATE");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","newExpiryDate:"+newExpiryDate);
			if(InterfaceUtil.isNullString(newExpiryDate))
			{
				_log.error("setInterfaceParameters","newExpiryDate  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "newExpiryDate  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			newExpiryDate = newExpiryDate.trim();
			_requestMap.put("newexpirydate",newExpiryDate);
			
			
			String forwardInstitucion = FileCache.getValue(_interfaceID,"FORWARD_INSTITUCTION");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","forwardInstitucion:"+forwardInstitucion);
			if(InterfaceUtil.isNullString(forwardInstitucion))
			{
				_log.error("setInterfaceParameters","forwardInstitucion  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "forwardInstitucion  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			forwardInstitucion = forwardInstitucion.trim();
			_requestMap.put("forwardInstitucion",forwardInstitucion);	

			String binAdquiriente = FileCache.getValue(_interfaceID,"BIN_ADQUIRIENTE");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","binAdquiriente:"+binAdquiriente);
			if(InterfaceUtil.isNullString(binAdquiriente))
			{
				_log.error("setInterfaceParameters","binAdquiriente  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "binAdquiriente  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			binAdquiriente = binAdquiriente.trim();
			_requestMap.put("binAdquiriente",binAdquiriente);	

			if(((String)_requestMap.get("REQ_SERVICE")).equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PMD))
			{
				_requestMap.put("TIPO",ClaroPINWSI.TIPO_PMD);
			}else if(((String)_requestMap.get("REQ_SERVICE")).equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PIN))
			{
				_requestMap.put("TIPO",ClaroPINWSI.TIPO_PIN);
			}else{
				_log.error("setInterfaceParameters","Service Type  is not defined");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "tipoProducto  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				
			}

			String tipoProducto =(String)_requestMap.get("TIPO");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","tipoProducto:"+tipoProducto);
			if(InterfaceUtil.isNullString(tipoProducto))
			{
				_log.error("setInterfaceParameters","tipoProducto  is not defined ");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "tipoProducto  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			tipoProducto = tipoProducto.trim();
			_requestMap.put("tipo",tipoProducto);	

			String nombreAplicacion = FileCache.getValue(_interfaceID,"NOMBRE_APLICATION");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","nombreAplicacion:"+nombreAplicacion);
			if(InterfaceUtil.isNullString(nombreAplicacion))
			{
				_log.error("setInterfaceParameters","nombreAplicacion  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "nombreAplicacion  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nombreAplicacion = nombreAplicacion.trim();
			_requestMap.put("nombreAplicacion",nombreAplicacion);
			
			
			
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
	 * This method used to get the system language mapped in FileCache based on the INLanguge.Includes following
	 * If the Mapping key not defined in IN file handle the event as System Error with level FATAL.
	 * If the Mapping is not defined handle the event as SYSTEM INFO with level MAJOR and set empty string.
	 * @throws Exception
	 */
	private void setLanguageFromMapping() throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("setLanguageFromMapping","Entered");
		String mappedLang="";
		String[] mappingArr;
		String[] tempArr;
		boolean mappingNotFound = true;//Flag defines whether the mapping of language is found or not.
		String langFromIN = null;
		try
		{
			//Get the mapping string from the FileCache and storing all the mappings into array which are separated by ','.
			String mappingString = (String)FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
			//langFromIN = (String)_responseMap.get("LanguageName");
			langFromIN = (String)_responseMap.get("IN_LANG");
			if(_log.isDebugEnabled()) _log.debug("setLanguageFromMapping","mappingString::"+mappingString +" langFromIN::"+langFromIN);
			mappingArr = mappingString.split(",");
			//Iterating the mapping array to map the IN language from the system language,if found break the loop.
			for(int in=0;in<mappingArr.length;in++)
			{
				tempArr = mappingArr[in].split(":");
				if(langFromIN.equalsIgnoreCase(tempArr[0].trim()))
				{
					mappedLang = tempArr[1];
					mappingNotFound=false;
					break;
				}
			}//end of for loop
			//if the mapping of IN language with our system is not found,handle the event
			if(mappingNotFound)
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ClaroPINWSINHandler[setLanguageFromMapping]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  "+langFromIN +" is not defined in IN file Hence setting the Default language");
			//Set the mapped language to the requested map with key as IN_LANGUAGE.
			_requestMap.put("IN_LANG",mappedLang);
		}//end of try
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("setLanguageFromMapping","Exception e::"+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPINWSINHandler[setLanguageFromMapping]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While mapping the IN Language with the system Language getting the Exception =" + e.getMessage());
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("setLanguageFromMapping","Exited mappedLang::"+mappedLang);
		}
	}//end of setLanguageFromMapping
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

