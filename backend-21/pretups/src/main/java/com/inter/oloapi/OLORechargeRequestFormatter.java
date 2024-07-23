/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.oloapi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.oloapi.stub.Correlation;
import com.inter.oloapi.stub.PrepayRechargeResponse;
import com.inter.oloapi.stub.Security;
import com.inter.oloapi.stub.PrepayRechargeRequest;
import com.inter.oloapi.stub.ValidateCustomerPlan;
import com.inter.oloapi.stub.ValidateCustomerPreTupsRequest;
import com.inter.oloapi.stub.ValidateCustomerPreTupsResponse;
import com.btsl.pretups.inter.cache.FileCache;
import com.inter.claroPINRechargeWS.stub.RecargaPinVirtualRequest;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OLORechargeRequestFormatter 
{
	public static Log _log = LogFactory.getLog(OLORechargeRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public OLORechargeRequestFormatter()
	{
		//lineSep = System.getProperty("line.separator")+"\r";
		lineSep = System.getProperty("line.separator")+"";
	}

	/**
	 * This method is used to parse the response string based on the type of Action.
	 * @param	int p_action
	 * @param	HashMap p_map
	 * @return	String.
	 * @throws	Exception
	 */
	protected Object generateRequest(int p_action, HashMap p_map) throws Exception 
	{
		if(_log.isDebugEnabled())_log.debug("generateRequest","Entered p_action::"+p_action+" map::"+p_map);
		Object object = null;
		p_map.put("action", String.valueOf(p_action));
		try
		{
			switch(p_action)
			{
			case OLORechargeI.ACTION_ACCOUNT_DETAILS: 
			{
				ValidateCustomerPreTupsRequest validateRequest = generateGetAccountInfoRequest(p_map);
				object= validateRequest;
				break;	
			}
			
			case OLORechargeI.ACTION_RECHARGE_CREDIT: 
			{

				PrepayRechargeRequest recargasRequest=generateRechargeCreditRequest(p_map);
				object= recargasRequest;
				break;	
			}
			case OLORechargeI.ACTION_IMMEDIATE_DEBIT: 
			{
				PrepayRechargeRequest debitoRequest=generateRechargeDebitRequest(p_map);
				object= debitoRequest;
				break;	
			}	
			}
		}
		catch(Exception e)
		{
			_log.error("generateRequest","Exception e ::"+e.getMessage());
			throw e;
		} 
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRequest","Exited Request String: object::"+object);
		}
		return object;
	}


	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private PrepayRechargeRequest generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
		PrepayRechargeRequest recargasRequest= null;
		try
		{
			Security security=new Security();
			
			security.setLogin(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "LOGIN"));
			security.setPassword(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "PASSWORD"));
			recargasRequest=new PrepayRechargeRequest();
			recargasRequest.setCustomerId(p_requestMap.get("MSISDN").toString());
			recargasRequest.setMobiquityTransactionId(p_requestMap.get("TRANSACTION_ID").toString());
			recargasRequest.setCompanyCode(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "COMPANY_CODE"));
			recargasRequest.setIdplan(p_requestMap.get("PLAN_ID").toString());
			recargasRequest.setSecurity(security);
			
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String currentDate = sdf.format(date);
			
			recargasRequest.setInserted(currentDate);
			//recargasRequest.setCustomerId(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "CUST_ID_PREPAY"));
			//recargasRequest.setMobiquityTransactionId(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "TRANSACTION_ID_PREPAY"));
			//recargasRequest.setIdplan(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "ID_PLAN_PREPAY"));
			//recargasRequest.setInserted(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "DATE_PREPAY"));
			
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeCreditRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request recargasRequest::"+recargasRequest);
		}
		return recargasRequest;
	}


	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private PrepayRechargeRequest generateRechargeDebitRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequest","Entered p_requestMap::"+p_requestMap);
		PrepayRechargeRequest recargasRequest= null;
		StringBuffer stringBuffer = null;
		try
		{
Security security=new Security();
			
			security.setLogin(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "LOGIN"));
			security.setPassword(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "PASSWORD"));
			recargasRequest=new PrepayRechargeRequest();
			recargasRequest.setCustomerId(p_requestMap.get("MSISDN").toString());
			recargasRequest.setMobiquityTransactionId(p_requestMap.get("TRANSACTION_ID").toString());
			recargasRequest.setCompanyCode(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "COMPANY_CODE"));
			recargasRequest.setIdplan(p_requestMap.get("PLAN_ID").toString());
			recargasRequest.setSecurity(security);
			
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH24:mm:ss");
			String currentDate = sdf.format(date);
			
			recargasRequest.setInserted(currentDate);
			//recargasRequest.setCustomerId(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "CUST_ID_PREPAY"));
			//recargasRequest.setMobiquityTransactionId(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "TRANSACTION_ID_PREPAY"));
			//recargasRequest.setIdplan(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "ID_PLAN_PREPAY"));
			//recargasRequest.setInserted(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "DATE_PREPAY"));
		}
		catch(Exception e)
		{
			_log.error("generateRechargeDebitRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeDebitRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
		return recargasRequest;
		}
	}

	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private ValidateCustomerPreTupsRequest generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateGetAccountInfoRequest","Entered p_requestMap::"+p_requestMap);
		ValidateCustomerPreTupsRequest validateRequest= null;
		try
		{
			Security security=new Security();
			
			security.setLogin(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "LOGIN"));
			security.setPassword(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "PASSWORD"));
			validateRequest=new ValidateCustomerPreTupsRequest();
			validateRequest.setCustomerId(p_requestMap.get("MSISDN").toString());
			validateRequest.setSecurity(security);
			validateRequest.setMobiquityTransactionId(p_requestMap.get("TRANSACTION_ID").toString());
			//validateRequest.setCustomerId(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "CUST_ID_VALIDATE"));
			//validateRequest.setMobiquityTransactionId(FileCache.getValue(p_requestMap.get("INTERFACE_ID").toString(), "TRANSACTION_ID_VALIDATE"));
		}
		catch(Exception e)
		{
			_log.error("generateGetAccountInfoRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateGetAccountInfoRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequest","Exiting Request validateRequest::"+validateRequest);
		}
		return validateRequest;
	}
	
	protected Object generateResponse(Object p_object,int p_action) throws Exception 
	{
		if(_log.isDebugEnabled())_log.debug("generateResponse","Entered p_action::"+p_action);
		Object object = null;
		try
		{
			switch(p_action)
			{
			case OLORechargeI.ACTION_ACCOUNT_DETAILS: 
			{
				ValidateCustomerPreTupsResponse validateResponse = generateGetAccountInfoResponse(p_object);
				object= validateResponse;
				break;	
			}
			
			case OLORechargeI.ACTION_RECHARGE_CREDIT: 
			{

				PrepayRechargeResponse recargasResponse=generateRechargeCreditResponse(p_object);
				object= recargasResponse;
				break;	
			}
			case OLORechargeI.ACTION_IMMEDIATE_DEBIT: 
			{
				PrepayRechargeResponse debitoResponse=generateRechargeDebitResponse(p_object);
				object= debitoResponse;
				break;	
			}	
			}
		}
		catch(Exception e)
		{
			_log.error("generateResponse","Exception e ::"+e.getMessage());
			throw e;
		} 
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateResponse","Exited Request String: object::"+object);
		}
		return object;
	}
	
	private ValidateCustomerPreTupsResponse generateGetAccountInfoResponse(Object p_object) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateGetAccountInfoResponse","Entered ");
		ValidateCustomerPreTupsResponse validateResponse= null;
		try
		{
			validateResponse=new ValidateCustomerPreTupsResponse();
			validateResponse.setName("JULIO EDUARDO");
			validateResponse.setStatus("IE000");
			validateResponse.setType("0");
			validateResponse.setNum_plans("1");
			ValidateCustomerPlan validateCustomerPlan = new ValidateCustomerPlan("533","OLO Ilim 5Mbps veloc. 30 d (TopUp) (S/.129)","10");
			ValidateCustomerPlan[] plans = new ValidateCustomerPlan[]{validateCustomerPlan};
			validateResponse.setPlans(plans);
		}
		catch(Exception e)
		{
			_log.error("generateGetAccountInfoResponse","Exception e: "+e);
			throw new BTSLBaseException(this,"generateGetAccountInfoResponse",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoResponse","Exiting");
		}
		return validateResponse;
	}
	
	private PrepayRechargeResponse generateRechargeCreditResponse(Object p_object) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditResponse","Entered ");
		PrepayRechargeResponse rechargeResponse= null;
		try
		{
			rechargeResponse=new PrepayRechargeResponse();
			rechargeResponse.setStatus("IE000");
			Correlation correlation = new Correlation();
			correlation.setMobiquityTransactionId("18364982");
			correlation.setThirdPartyTransactionId("000116932705701");
			rechargeResponse.setCorrelation(correlation);
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditResponse","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeCreditResponse",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditResponse","Exiting");
		}
		return rechargeResponse;
	}

	
	private PrepayRechargeResponse generateRechargeDebitResponse(Object p_object) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitResponse","Entered ");
		PrepayRechargeResponse rechargeResponse= null;
		try
		{
			rechargeResponse=new PrepayRechargeResponse();
			rechargeResponse.setStatus("IE000");
			Correlation correlation = new Correlation();
			correlation.setMobiquityTransactionId("18364982");
			correlation.setThirdPartyTransactionId("000116932705701");
			rechargeResponse.setCorrelation(correlation);
		}
		catch(Exception e)
		{
			_log.error("generateRechargeDebitResponse","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeDebitResponse",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeDebitResponse","Exiting");
		}
		return rechargeResponse;
	}


}
