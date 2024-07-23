/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.righttel.crmWebService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.rpc.holders.StringHolder;

import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import org.w3c.dom.NodeList;

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
import com.inter.righttel.crmWebService.scheduler.NodeManager;
import com.inter.righttel.crmWebService.scheduler.NodeScheduler;
import com.inter.righttel.crmWebService.scheduler.NodeVO;
import com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl;
import com.inter.righttel.crmWebService.stub.FaceValueDtoimpl;
import com.inter.righttel.crmWebService.stub.ServicePortalPortType;
import com.inter.righttel.crmWebService.stub.holders.BenefitBalDtoListimplArrayHolder;


/**
 * @author Vipan Kumar
 * TODO To change the template for this generated type comment go
 *  to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CRMWebServiceINHandler implements InterfaceHandler{	
	private Log _log = LogFactory.getLog(CRMWebServiceINHandler.class.getName());


	private CRMWebServiceResponseParser _parser=null;
	private HashMap _responseMap = null;
	private HashMap _requestMap = null;
	private String _interfaceID=null;
	private String _inReconID=null;
	private String _msisdn=null;
	private String _referenceID=null;//Used to store the reference of transaction id.	
	private Stub _stubSuper=null;
	/**
	 * 
	 */
	public CRMWebServiceINHandler() {

		_responseMap=new HashMap();
		_parser=new CRMWebServiceResponseParser();	
	}

	/**
	 * This method is used to validate the subscriber
	 * 
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);

		_requestMap = p_requestMap;

		Double systemAmtDouble=0.0;
		double multFactorDouble=0;

		String amountStr="";

		try
		{
			_requestMap.put("IN_START_TIME","0");
			_requestMap.put("IN_END_TIME","0");
			_requestMap.put("IN_RECHARGE_TIME","0");
			_requestMap.put("IN_CREDIT_VAL_TIME","0");
			
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	

			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=_referenceID.replace(".", "");
			_requestMap.put("IN_TXN_ID",_inReconID);

			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));

			if("N".equals(validateRequired))
			{
				//Setting default Response; 
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("INTERFACE_PREV_BALANCE","0");
				_requestMap.put("SERVICE_CLASS","ALL");
				_requestMap.put("ACCOUNT_STATUS","ACTIVE");
				return ;
			}

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("credit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();

			_requestMap.put("MULT_FACTOR",multFactor);

			//setInterfaceParameters(CRMWebServiceI.ACTION_ACCOUNT_DETAILS);


			sendRequestToIN(CRMWebServiceI.ACTION_ACCOUNT_DETAILS);


			_requestMap=checkALLbalance(_requestMap);	


			if(_requestMap.get("REQ_SERVICE").toString().equalsIgnoreCase("PRC") &&  _requestMap.get("USER_TYPE").toString().equalsIgnoreCase("S") &&  _requestMap.get("SUBSCRIBER_TYPE").toString().equalsIgnoreCase("POST")){
				try
				{
					multFactorDouble=Double.parseDouble(multFactor);
					double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("REQ_AMOUNT"));
					systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
					amountStr=String.valueOf(systemAmtDouble);
					//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
					String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
					//If the ROUND_FLAG is not defined in the INFile 
					if(InterfaceUtil.isNullString(roundFlag))
					{
						roundFlag="Y";
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CRMWebServiceINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
					}
					//If rounding of amount is allowed, round the amount value and put this value in request map.
					if("Y".equals(roundFlag.trim()))
					{
						amountStr=String.valueOf(Math.round(systemAmtDouble));
						_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
					}
					_requestMap.put("transfer_amount",amountStr);

					_requestMap=checkCreditLimit(_requestMap);	

					try {
						amountStr = _requestMap.get("creditLimit") + "";
						amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
						_requestMap.put("BILL_AMOUNT_BAL", amountStr);
						
						
					} catch (Exception e) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric amountStr=:" + amountStr + ", while parsing the Balance get Exception e:" + e.getMessage());
						_requestMap.put("BILL_AMOUNT_BAL", "0");
					}

				}
				catch(Exception e)
				{
					e.printStackTrace();
					_log.error("credit","Exception e:"+e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
					throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
			}


			try{
				_requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _requestMap.get("EXPIRY_DATE")).trim(), "yyyy-MM-dd"));
			} catch (Exception e) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "OLD_EXPIRY_DATE obtained from the IN is not valid=: while parsing the Balance get Exception e:" + e.getMessage());
				_requestMap.put("OLD_EXPIRY_DATE",BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyyyy"));
			}
			
			try{
				_requestMap.put("AON", InterfaceUtil.getInterfaceDateFromDateString(((String) _requestMap.get("EFFECTIVE_DATE")).trim(), "yyyy-MM-dd"));
			} catch (Exception e) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "OLD_EXPIRY_DATE obtained from the IN is not valid=: while parsing the Balance get Exception e:" + e.getMessage());
				_requestMap.put("AON",BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyyyy"));
			}


			try {
				amountStr = _requestMap.get("balance") + "";
				amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
				_requestMap.put("INTERFACE_PREV_BALANCE", amountStr);
				
				
			} catch (Exception e) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric amountStr=:" + amountStr + ", while parsing the Balance get Exception e:" + e.getMessage());
				_requestMap.put("INTERFACE_PREV_BALANCE", "0");
			}


			
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);

			_requestMap.put("INTERFACE_TXN_ID",(String)_requestMap.get("IN_TXN_ID"));

		}
		catch (BTSLBaseException be)
		{
			_log.error("validate","BTSLBaseException be="+be.getMessage());
			
			try
			{
				if(be.getMessage()==null)
				{
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
				else
				{
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  " validation exception="+be.getMessage());
					
					_requestMap.put("INTERFACE_STATUS",be.getMessage());	
					throw be;
				}
			}
			catch (BTSLBaseException be1)
			{
				throw be1;
			}
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	

			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[validate]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}



	public HashMap checkCreditLimit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if(_log.isDebugEnabled()) _log.debug("checkCreditLimit","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	

			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=_referenceID.replace(".", "");
			_requestMap.put("IN_TXN_ID",_inReconID);

			sendRequestToIN(CRMWebServiceI.ACTION_ACCOUNT_DETAILS_OTHER);

					
		}
		catch (BTSLBaseException be)
		{
			if(be.getMessage().contains(InterfaceErrorCodesI.ERROR_ACC_MAX_CREDIT_LIMIT))
			{
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACC_MAX_CREDIT_LIMIT);
			}
			_log.error("checkCreditLimit","BTSLBaseException be="+be.getMessage());
			EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[checkCreditLimit]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
			_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("checkCreditLimit","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[checkCreditLimit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("checkCreditLimit","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[checkCreditLimit]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
		return _requestMap;
	}

	public HashMap checkALLbalance(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if(_log.isDebugEnabled()) _log.debug("checkALLbalance","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;

		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	

			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=_referenceID.replace(".", "");
			_requestMap.put("IN_TXN_ID",_inReconID);

			//setInterfaceParameters(CRMWebServiceI.ACTION_ACCOUNT_DETAILS_OTHER);

			sendRequestToIN(CRMWebServiceI.ACTION_ACCOUNT_DETAILS_BALANCE);

		}
		catch (BTSLBaseException be)
		{
			_log.error("checkALLbalance","BTSLBaseException be="+be.getMessage());
			EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[checkALLbalance]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
			_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("checkALLbalance","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[checkALLbalance]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("checkALLbalance","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[checkALLbalance]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
		return _requestMap;
	}

	/**
	 * This method is used to credit the Subscriber
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
			if(p_requestMap.get("REQ_SERVICE").toString().equalsIgnoreCase("VAS"))
			{
				vasCredit(p_requestMap);
			}else if(p_requestMap.get("REQ_SERVICE").toString().equalsIgnoreCase("VCN"))
			{
				voucherCredit(p_requestMap);
			}else{
				rechargeCredit(p_requestMap);
			}
	}

	/**
	 * This method is used to credit the Subscriber
	 */
	public void rechargeCredit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("rechargeCredit","Entered p_requestMap: " + p_requestMap);

		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
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

			_inReconID=_referenceID.replace(".", "");

			_requestMap.put("IN_TXN_ID",_referenceID);

			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("rechargeCredit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[rechargeCredit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"rechargeCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(CRMWebServiceI.ACTION_RECHARGE_CREDIT);

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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CRMWebServiceINHandler[rechargeCredit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				_log.error("rechargeCredit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[rechargeCredit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"rechargeCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			
			_requestMap.put("transfer_amount",amountStr);

			if(((String)_requestMap.get("REQ_SERVICE")).equalsIgnoreCase("RC") && ((String)_requestMap.get("CARD_GROUP_SELECTOR")).equalsIgnoreCase("1")){
				_requestMap.put("paymentType","1");	
				_requestMap.put("paymentMethod","1");
				_requestMap.put("callerID","Normal Payment");	
				_requestMap.put("REQUEST_ID",(String)_requestMap.get("TRANSACTION_ID"));
				
			}else if(((String)_requestMap.get("REQ_SERVICE")).equalsIgnoreCase("RC") && ((String)_requestMap.get("CARD_GROUP_SELECTOR")).equalsIgnoreCase("2")){
				_requestMap.put("paymentType","2");	
				_requestMap.put("paymentMethod","1");
				_requestMap.put("callerID","Exciting Payment");
				_requestMap.put("REQUEST_ID",(String)_requestMap.get("TRANSACTION_ID"));
				
			}else{
				_requestMap.put("paymentType","1");	
				_requestMap.put("paymentMethod","1");
				_requestMap.put("callerID","Normal Payment");	
				_requestMap.put("REQUEST_ID",(String)_requestMap.get("TRANSACTION_ID"));	
				
			}

			sendRequestToIN(CRMWebServiceI.ACTION_RECHARGE_CREDIT);            

			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));

			_requestMap.put("INTERFACE_STATUS", "0");
			try {
				amountStr = (String) _requestMap.get("balance") + "";
				amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
				_requestMap.put("INTERFACE_POST_BALANCE", amountStr);
			} catch (Exception e) {
				e.printStackTrace();
				_log.error("validate", "Exception e:" + e.getMessage() + " amountStr:" + amountStr);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric amountStr=:" + amountStr + ", while parsing the Balance get Exception e:" + e.getMessage());
				_requestMap.put("INTERFACE_POST_BALANCE", "0");
			}
			String expdate="";
			if(_requestMap.get("expDate")!=null)
				expdate=BTSLUtil.getDateTimeStringFromDate(((Calendar)_requestMap.get("expDate")).getTime(), "ddMMyyyy");

			_requestMap.put("NEW_EXPIRY_DATE", expdate);
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

		} 
		catch (BTSLBaseException be)
		{
			if(be.getMessage().equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
				p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[rechargeCredit]",_msisdn,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While rechargeCredit, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"rechargeCredit", be.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[rechargeCredit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While rechargeCredit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"rechargeCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("rechargeCredit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[rechargeCredit]","rechargeCredit complete."," _requestMap string:"+_requestMap.toString(),"","");
		}

		if (_log.isDebugEnabled())_log.debug("rechargeCredit","Exiting _interfaceID"+_interfaceID+"_referenceID"+_referenceID+"_msisdn"+_msisdn+"requestMap="+_requestMap.toString()); 
	}


	
	
	/**
	 * This method is used to credit the Subscriber
	 */
	public void vasCredit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("vasCredit","Entered p_requestMap: " + p_requestMap);

		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
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

			_inReconID=_referenceID.replace(".", "");

			_requestMap.put("IN_TXN_ID",_referenceID);

			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("vasCredit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[vasCredit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"vasCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(CRMWebServiceI.ACTION_VAS_CREDIT);

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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CRMWebServiceINHandler[vasCredit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				_log.error("vasCredit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[vasCredit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"vasCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)

			
			_requestMap.put("REQUEST_ID",(String)_requestMap.get("TRANSACTION_ID"));	
			
			String payflag = FileCache.getValue(_interfaceID,"VAS_PAY_FLAG");
		    
			if(InterfaceUtil.isNullString(payflag))
			{
				payflag="1";
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of VAS_PAY_FLAG in INFile ");
			}   
			
			String channelId = FileCache.getValue(_interfaceID,"VAS_CHANNEL_ID");
		    
			if(InterfaceUtil.isNullString(channelId))
			{
				channelId="29";
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of VAS_CHANNEL_ID in INFile ");
			}   
			
			
			_requestMap.put("transfer_amount",amountStr);
			_requestMap.put("PayFlag",payflag);	
			_requestMap.put("ChannelID",channelId);	


			sendRequestToIN(CRMWebServiceI.ACTION_VAS_CREDIT);            

			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));

			_requestMap.put("INTERFACE_POST_BALANCE", "0");
			
			String expdate="";
			if(_requestMap.get("expDate")!=null)
				expdate=BTSLUtil.getDateTimeStringFromDate(((Calendar)_requestMap.get("expDate")).getTime(), "ddMMyyyy");

			_requestMap.put("NEW_EXPIRY_DATE", expdate);
			
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

		} 
		catch (BTSLBaseException be)
		{
			if(be.getMessage().equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
				p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[vasCredit]",_msisdn,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While vasCredit, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"vasCredit", be.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[vasCredit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While vasCredit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"vasCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("vasCredit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[vasCredit]","vasCredit complete."," _requestMap string:"+_requestMap.toString(),"","");
		}

		if (_log.isDebugEnabled())_log.debug("vasCredit","Exiting _interfaceID"+_interfaceID+"_referenceID"+_referenceID+"_msisdn"+_msisdn+"requestMap="+_requestMap.toString()); 
	}

	/**
	 * This method used to send the request to the IN
	 * @param p_action
	 * @throws BTSLBaseException
	 */
	private void sendRequestToIN(int p_action) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+p_action+" __msisdn="+_msisdn);

		StringHolder brandName=new StringHolder();
		StringHolder SIMStatus=new StringHolder();
		StringHolder defLang=new StringHolder();
		StringHolder SIMSubStatus=new StringHolder();
		StringHolder custGrade=new StringHolder();
		StringHolder fixContact=new StringHolder();
		StringHolder docNum=new StringHolder();
		StringHolder docType=new StringHolder();
		StringHolder customerName=new StringHolder();
		StringHolder ISPREPAID=new StringHolder();
		StringHolder brandCode=new StringHolder();
		StringHolder custType=new StringHolder();
		StringHolder VCBlackList=new StringHolder();

		StringHolder balance=new StringHolder();
		StringHolder expDate=new StringHolder();
		StringHolder addBalance=new StringHolder();
		BenefitBalDtoListimplArrayHolder benefitBalDtoList=new BenefitBalDtoListimplArrayHolder();

		StringHolder creditLimit=new StringHolder();
		StringHolder defaultCL=new StringHolder();
		StringHolder nonDefaultCL=new StringHolder();
		StringHolder creditUsed=new StringHolder();
		StringHolder creditAvailable=new StringHolder();


		StringHolder result=new StringHolder();
		StringHolder exceptionCode=new StringHolder();


		StringHolder MSISDN=new StringHolder(_requestMap.get("FILTER_MSISDN").toString());

		
		String MSISDNQuery=_requestMap.get("FILTER_MSISDN").toString();

		String requestID="";
		String amount="";
		String paymentType="";

		String bankId ="";
		String AU="";
		FaceValueDtoimpl dtoimpl=new FaceValueDtoimpl();
		String callerID="";
		String paymentMethod="";

		String offerCode="";
        String channelID="";
        String payFlag="";
       
        String discountFee="";
        String bankID="";
       
        

		String actionLevel="";
		switch(p_action)
		{
		case CRMWebServiceI.ACTION_ACCOUNT_DETAILS:
		{
			actionLevel="ACTION_ACCOUNT_DETAILS";
			break;
		}
		case CRMWebServiceI.ACTION_RECHARGE_CREDIT:
		{
			requestID=_requestMap.get("REQUEST_ID").toString();
			amount=_requestMap.get("transfer_amount").toString();
			paymentType=_requestMap.get("paymentType").toString();
			paymentMethod=_requestMap.get("paymentMethod").toString();
			callerID=_requestMap.get("callerID").toString();
			dtoimpl.setE_AMOUNT(_requestMap.get("transfer_amount").toString());
			dtoimpl.setE_COUNT("1");
			break;
		}
		case CRMWebServiceI.ACTION_QUERY_TOPUP:
		{
			requestID=_requestMap.get("TRANSACTION_ID").toString();
			actionLevel="ACTION_QUERY_TOPUP";
			break;
		}
		case CRMWebServiceI.ACTION_ACCOUNT_DETAILS_OTHER:
		{
			actionLevel="ACTION_ACCOUNT_DETAILS_OTHER";
			break;
		}
		case CRMWebServiceI.ACTION_VAS_CREDIT:
		{
			offerCode=_requestMap.get("CARD_GROUP").toString();
			payFlag=_requestMap.get("PayFlag").toString();
			channelID=_requestMap.get("ChannelID").toString();
			actionLevel="ACTION_VAS_CREDIT";
			break;
		}
		}
		FaceValueDtoimpl[] faceValueDtoList ={dtoimpl};

		if(!BTSLUtil.isNullString(_msisdn))
		{
			_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);
		}

		long startTime=0,endTime=0,warnTime=0;
		ServicePortalPortType clientStub=null;

		NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;

		CRMWebServiceConnectionManager serviceConnection =null;
		try
		{

			nodeScheduler = NodeManager.getScheduler(_interfaceID);

			retryNumber = nodeScheduler.getRetryNum();

			if(nodeScheduler==null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);


			for(int loop=1;loop<=retryNumber;loop++)
			{
				try
				{
					nodeVO = nodeScheduler.getNodeVO(_inReconID);
					TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[sendRequestToIN]",PretupsI.TXN_LOG_REQTYPE_REQ,"Node information NodeVO:"+nodeVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
					//_requestMap.put("IP", nodeVO.getUrl());

					_requestMap.put("IP",nodeVO.getUrl().substring(7, nodeVO.getUrl().lastIndexOf(":")));

					if(nodeVO==null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );

					warnTime=nodeVO.getWarnTime();

					if(_requestMap.get("REQ_SERVICE").toString().equalsIgnoreCase("VCN"))
					{
						serviceConnection = new CRMWebServiceConnectionManager(nodeVO,_interfaceID,_requestMap.get("SERIAL_NO").toString());	
					}else{
						serviceConnection = new CRMWebServiceConnectionManager(nodeVO,_interfaceID,_requestMap.get("REQUEST_ID").toString());
					}


					clientStub =serviceConnection.getService();			
					if(clientStub==null)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object");
						_log.error("sendRequestToIN","Unable to get Client Object");
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
					}		            
					try
					{
						startTime=System.currentTimeMillis();
						_requestMap.put("IN_START_TIME",String.valueOf(startTime));
						switch(p_action)
						{	
						case CRMWebServiceI.ACTION_ACCOUNT_DETAILS: 
						{
							clientStub.newQueryProfile2(MSISDN, brandName, SIMStatus, defLang, SIMSubStatus, custGrade, fixContact, docNum, docType, customerName, ISPREPAID, brandCode, custType, VCBlackList);
							_stubSuper=(Stub)clientStub;
							SOAPHeaderElement[] soapheader=_stubSuper.getResponseHeaders();
							String returnCode="";
							for (int i = 0; i < soapheader.length; i++) {
								SOAPHeaderElement element=soapheader[i];
								NodeList s=element.getLastChild().getChildNodes();
								returnCode=s.item(0).toString();
								break;
							}
							_responseMap.put("returnCode",returnCode);
							
							_responseMap.put("MSISDN", MSISDN.value);
							_responseMap.put("brandName", brandName.value);
							_responseMap.put("SIMStatus", SIMStatus.value);
							_responseMap.put("ISPREPAID", ISPREPAID.value);
							_responseMap.put("BrandCode", brandCode.value);
							_responseMap.put("VCBlackList", VCBlackList.value);
							_responseMap.put("SIMSubStatus", SIMSubStatus.value);

							break;
						}
						case CRMWebServiceI.ACTION_QUERY_TOPUP: 
						{
							clientStub.queryRechargeResult(MSISDNQuery, requestID, result, exceptionCode);
							_stubSuper=(Stub)clientStub;
							SOAPHeaderElement[] soapheader=_stubSuper.getResponseHeaders();
							String returnCode="";
							
							for (int i = 0; i < soapheader.length; i++) {
								SOAPHeaderElement element=soapheader[i];
								NodeList s=element.getLastChild().getChildNodes();
								returnCode=s.item(0).toString();
								break;
							}
							_responseMap.put("returnCode",returnCode);
							_responseMap.put("result", result.value);
							_responseMap.put("exceptionCode", exceptionCode.value);

							break;	
						}
						case CRMWebServiceI.ACTION_RECHARGE_CREDIT: 
						{
							clientStub.rechargePPSNew(requestID, MSISDNQuery, amount, bankId, AU, paymentType, faceValueDtoList, callerID, paymentMethod, balance, expDate, addBalance, benefitBalDtoList);
							_stubSuper=(Stub)clientStub;
							SOAPHeaderElement[] soapheader=_stubSuper.getResponseHeaders();
							String returnCode="";
							
							for (int i = 0; i < soapheader.length; i++) {
								SOAPHeaderElement element=soapheader[i];
								NodeList s=element.getLastChild().getChildNodes();
								returnCode=s.item(0).toString();
								break;
							}
							_responseMap.put("returnCode",returnCode);
							_responseMap.put("balance", balance.value);
							_responseMap.put("expDate", expDate.value);
							_responseMap.put("benefitBalDtoList", benefitBalDtoList.value);
							break;	
						}
						case CRMWebServiceI.ACTION_VAS_CREDIT: 
						{
							clientStub.orderPricePlanOffer(MSISDNQuery, offerCode, channelID, payFlag, AU, amount, discountFee, bankID, callerID);
							_stubSuper=(Stub)clientStub;
							SOAPHeaderElement[] soapheader=_stubSuper.getResponseHeaders();
							String returnCode="";
							
							for (int i = 0; i < soapheader.length; i++) {
								SOAPHeaderElement element=soapheader[i];
								NodeList s=element.getLastChild().getChildNodes();
								returnCode=s.item(0).toString();
								break;
							}
							_responseMap.put("returnCode",returnCode);
							break;	
						}
						case CRMWebServiceI.ACTION_ACCOUNT_DETAILS_OTHER: 
						{
							clientStub.checkCreditLimit(MSISDNQuery, balance, creditLimit, defaultCL, nonDefaultCL, creditUsed, creditAvailable);
							_stubSuper=(Stub)clientStub;
							SOAPHeaderElement[] soapheader=_stubSuper.getResponseHeaders();
							String returnCode="";
							for (int i = 0; i < soapheader.length; i++) {
								SOAPHeaderElement element=soapheader[i];
								NodeList s=element.getLastChild().getChildNodes();
								returnCode=s.item(0).toString();
								break;
							}
							_responseMap.put("returnCode",returnCode);
							
							_responseMap.put("balance", balance.value);
							_responseMap.put("creditLimit", creditLimit.value);
							_responseMap.put("creditUsed", creditUsed.value);
							_responseMap.put("creditAvailable", creditAvailable.value);
							break;	
						}

						case CRMWebServiceI.ACTION_ACCOUNT_DETAILS_BALANCE: 
						{
							AllBalanceDtoListimpl[] allBalanceDtoListimpl= clientStub.queryAllBalance(MSISDNQuery);
							_stubSuper=(Stub)clientStub;
							SOAPHeaderElement[] soapheader=_stubSuper.getResponseHeaders();
							String returnCode="";
							
							for (int i = 0; i < soapheader.length; i++) {
								SOAPHeaderElement element=soapheader[i];
								NodeList s=element.getLastChild().getChildNodes();
								returnCode=s.item(0).toString();
								break;
							}
							_responseMap.put("returnCode",returnCode);
							_responseMap.put("RESPONSE_OBJECT", allBalanceDtoListimpl);

							break;	
						}
						}	
					}
					catch (java.rmi.RemoteException re) {
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RemoteException Error Message:" + re.getMessage());
						String respCode = null;
						// parse error code
						String requestStr = re.getMessage();
						int index = requestStr.indexOf("<ErrorCode>");
						if (index == -1) {
							if (re.getMessage().contains("java.net.ConnectException")) {

								_log.error("sendRequestToIN", "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CRMWebServiceINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection ");
								throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
							} else if (re.getMessage().contains("java.net.SocketTimeoutException")) {
								re.printStackTrace();
								if (re.getMessage().contains("connect")) {
									throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
								}
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketTimeoutException Message:" + re.getMessage());
								_log.error("sendRequestToIN", "RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
								throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
							} else if (re.getMessage().contains("java.net.SocketException")) {
								re.printStackTrace();
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketException Message:" + re.getMessage());
								_log.error("sendRequestToIN", "RMI java.net.SocketException Error Message :" + re.getMessage());
								throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
							} else
								throw new Exception(re);
						}
						respCode = requestStr.substring(index + "<ErrorCode>".length(), requestStr.indexOf("</ErrorCode>", index));
						index = requestStr.indexOf("<ErrorDescription>");
						String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(), requestStr.indexOf("</ErrorDescription>", index));
						_log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
						_requestMap.put("INTERFACE_STATUS", respCode);
						_requestMap.put("INTERFACE_DESC", respCodeDesc);
						_log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

					}
					catch(Exception e)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Exception Error Message:"+e.getMessage());
						_log.error("sendRequestToIN","Exception Error Message :"+e.getMessage());
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
					}
					finally
					{
						endTime=System.currentTimeMillis();
					}

				}
				catch(BTSLBaseException be)
				{
					throw be;
				}

				catch(Exception e)
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Error Message:"+e.getMessage());
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

			_requestMap=_parser.parseResponse(p_action,_requestMap,_responseMap);
			TransactionLog.log( _interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response Map: "+_requestMap ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
			//Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled
			if(endTime-startTime>=warnTime)
			{
				_log.info("sendRequestToIN", "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel," IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
			}

		}
		catch(BTSLBaseException be)
		{
			_log.error("sendRequestToIN","BTSLBaseException be = "+be.getMessage());
			String status=String.valueOf(_requestMap.get("INTERFACE_STATUS"));
			_requestMap.put("INTERFACE_STATUS",status);
			throw be;
		}//end of BTSLBaseException
		catch(Exception e)
		{
			e.printStackTrace();	    
			_log.error("sendRequestToIN","Exception="+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
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

	public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat ("hhmmss");
		String dateString = sdf.format(p_date);
		return dateString;
	}
	
	
	public void debitAdjust(HashMap<String, String> p_requestMap)
			throws BTSLBaseException, Exception {
		if (_log.isDebugEnabled())_log.debug("debitAdjust","Entered p_requestMap: " + p_requestMap);

		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
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

			_inReconID=_referenceID.replace(".", "");

			_requestMap.put("IN_TXN_ID",_referenceID);

			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");

			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("debitAdjust","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			String debit_user_name = FileCache.getValue(_interfaceID,"DEBIT_USERNAME");
		    
			if(InterfaceUtil.isNullString(debit_user_name))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of USERNAME in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("DEBIT_USERNAME",debit_user_name);

			String debit_user_password = FileCache.getValue(_interfaceID,"DEBIT_PASSWORD");
		    
			if(InterfaceUtil.isNullString(debit_user_password))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of DEBIT_PASSWORD in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("DEBIT_USERPASSWORD",debit_user_password);

			String debit_user_url = FileCache.getValue(_interfaceID,"DEBIT_URL");
		    
			if(InterfaceUtil.isNullString(debit_user_url))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of DEBIT_URL in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("DEBIT_USERURL",debit_user_url);
			
			String debit_timeout = FileCache.getValue(_interfaceID,"DEBIT_TIMEOUT");
		    
			if(InterfaceUtil.isNullString(debit_timeout))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of DEBIT_TIMEOUT in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			_requestMap.put("DEBIT_TIMEOUT",debit_timeout);
			
			setInterfaceParameters(CRMWebServiceI.ACTION_RECHARGE_DEBIT);

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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CRMWebServiceINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)

			_requestMap.put("transfer_amount",amountStr);
			
			CRMWebServiceDebitINHandler crmWebServiceDebitINHandler=new CRMWebServiceDebitINHandler();
			
			p_requestMap=crmWebServiceDebitINHandler.getDEBITREQUEST(p_requestMap, _interfaceID);
			
			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
			
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			
			_requestMap.put("INTERFACE_POST_BALANCE", "0");
			
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

		} 
		catch (BTSLBaseException be)
		{
			if(be.getMessage().equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
				p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[debitAdjust]",_msisdn,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"debitAdjust", be.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[credit]","credit complete."," _requestMap string:"+_requestMap.toString(),"","");
		}

		if (_log.isDebugEnabled())_log.debug("credit","Exiting _interfaceID"+_interfaceID+"_referenceID"+_referenceID+"_msisdn"+_msisdn+"requestMap="+_requestMap.toString()); 
	}

	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

		if(_log.isDebugEnabled()) _log.debug("validityAdjust","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			
			_inReconID=_referenceID.replace(".", "");
			
			_requestMap.put("IN_TXN_ID",_inReconID);


			setInterfaceParameters(CRMWebServiceI.ACTION_QUERY_TOPUP);

			sendRequestToIN(CRMWebServiceI.ACTION_QUERY_TOPUP);

			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);

		}
		catch (BTSLBaseException be)
		{
			if(be.getMessage()!=null && be.getMessage().contains(InterfaceErrorCodesI.AMBIGOUS))
			{
				_log.error("validityAdjust","BTSLBaseException be="+be.getMessage());
				EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[validityAdjust]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			}
			else if(be.getMessage()!=null)
			{
				_log.error("validate","BTSLBaseException be="+be.getMessage());
				EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[validityAdjust]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Got Error response from IN so throwing Fail in validation exception");
				throw be;
			}
			else
			{
				_log.error("validityAdjust","BTSLBaseException be="+be.getMessage());
				EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[validityAdjust]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validityAdjust","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[validityAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validityAdjust","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[validityAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}


	public void creditAdjust(HashMap<String, String> p_requestMap)	throws BTSLBaseException, Exception {
		if(_log.isDebugEnabled()) _log.debug("creditAdjust","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		Double systemAmtDouble=0.0;
		double multFactorDouble=0;
		String amountStr="";
		String promoAmountStr="";
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=_referenceID.replace(".", "");
			_requestMap.put("IN_TXN_ID",_inReconID);

			setInterfaceParameters(CRMWebServiceI.ACTION_QUERY_TOPUP);

			//sending the AccountInfo request to IN along with validate action defined in interface
			sendRequestToIN(CRMWebServiceI.ACTION_QUERY_TOPUP);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
		}
		catch (BTSLBaseException be)
		{
			if(be.getMessage()!=null && be.getMessage().contains(InterfaceErrorCodesI.AMBIGOUS))
			{
				_log.error("validate","BTSLBaseException be="+be.getMessage());
				EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[creditAdjust]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
			}
			else if(be.getMessage()!=null)
			{
				_log.error("creditAdjust","BTSLBaseException be="+be.getMessage());
				EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[creditAdjust]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Got Error response from IN so throwing Fail in validation exception");
				_requestMap.put("INTERFACE_STATUS",be.getMessage());	
				throw be;
			}
			else
			{
				_log.error("creditAdjust","BTSLBaseException be="+be.getMessage());
				EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[creditAdjust]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("creditAdjust","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[creditAdjust]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}
	
	public void voucherCredit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("voucherCredit","Entered p_requestMap: " + p_requestMap);

		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
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

			_inReconID=_referenceID.replace(".", "");

			_requestMap.put("IN_TXN_ID",_referenceID);

			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("voucherCredit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[voucherCredit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"voucherCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(CRMWebServiceI.ACTION_RECHARGE_CREDIT);

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
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CRMWebServiceINHandler[voucherCredit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
				_log.error("voucherCredit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[voucherCredit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"voucherCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)

			String type = FileCache.getValue(_interfaceID,"VCN_VOUCHER_TYPE_NORMAL");
		    
			if(InterfaceUtil.isNullString(type))
			{
			    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "NodeScheduler[constructor]", "INTERFACE_ID::"+_interfaceID,"", "", "Check the value of type in INFile ");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}   
			
			_requestMap.put("transfer_amount",amountStr);
			
			if(((String)_requestMap.get("REQ_SERVICE")).equalsIgnoreCase("VCN") && BTSLUtil.isStringContain(type, ((String)_requestMap.get("VOUCHER_TYPE")))){
				_requestMap.put("paymentType","1");	
				_requestMap.put("paymentMethod","3");	
				_requestMap.put("callerID","Physical VC Recharge");	
				_requestMap.put("REQUEST_ID",(String)_requestMap.get("VOUCHER_CODE"));
				_requestMap.put("SERIAL_NO",(String)_requestMap.get("SERIAL_NUMBER"));
			}else if(((String)_requestMap.get("REQ_SERVICE")).equalsIgnoreCase("VCN") && !BTSLUtil.isStringContain(type, ((String)_requestMap.get("VOUCHER_TYPE")))){
				_requestMap.put("paymentType","1");	
				_requestMap.put("paymentMethod","3");	
				_requestMap.put("callerID","electronica VC Recharge");	
				_requestMap.put("REQUEST_ID",(String)_requestMap.get("VOUCHER_CODE"));
				_requestMap.put("SERIAL_NO",(String)_requestMap.get("SERIAL_NUMBER"));
			}else{
				_requestMap.put("paymentType","1");	
				_requestMap.put("paymentMethod","3");
				_requestMap.put("callerID","Physical VC Recharge");	
				_requestMap.put("REQUEST_ID",(String)_requestMap.get("TRANSACTION_ID"));	
				_requestMap.put("SERIAL_NO",(String)_requestMap.get("SERIAL_NUMBER"));
				
			}

			sendRequestToIN(CRMWebServiceI.ACTION_RECHARGE_CREDIT);            

			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));

			_requestMap.put("INTERFACE_STATUS", "0");
			try {
				amountStr = (String) _requestMap.get("balance") + "";
				amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr, Double.parseDouble(multFactor));
				_requestMap.put("INTERFACE_POST_BALANCE", amountStr);
			} catch (Exception e) {
				e.printStackTrace();
				_log.error("validate", "Exception e:" + e.getMessage() + " amountStr:" + amountStr);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric amountStr=:" + amountStr + ", while parsing the Balance get Exception e:" + e.getMessage());
				_requestMap.put("INTERFACE_POST_BALANCE", "0");
			}

			String expdate="";
			if(_requestMap.get("expDate")!=null)
				expdate=BTSLUtil.getDateTimeStringFromDate(((Calendar)_requestMap.get("expDate")).getTime(), "ddMMyyyy");

			_requestMap.put("NEW_EXPIRY_DATE", expdate);
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

		} 
		catch (BTSLBaseException be)
		{
			if(be.getMessage().equalsIgnoreCase(InterfaceErrorCodesI.AMBIGOUS)){
				p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[voucherCredit]",_msisdn,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While voucherCredit, get the Base Exception be:"+be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this,"voucherCredit", be.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[voucherCredit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While voucherCredit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"voucherCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("voucherCredit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[voucherCredit]","voucherCredit complete."," _requestMap string:"+_requestMap.toString(),"","");
		}

		if (_log.isDebugEnabled())_log.debug("voucherCredit","Exiting _interfaceID"+_interfaceID+"_referenceID"+_referenceID+"_msisdn"+_msisdn+"requestMap="+_requestMap.toString()); 
	}

}

