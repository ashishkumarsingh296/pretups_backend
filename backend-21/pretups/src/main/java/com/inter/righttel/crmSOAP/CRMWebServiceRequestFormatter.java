/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.righttel.crmSOAP;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.righttel.crmSOAP.stub.InitTopupRequestType;
import com.inter.righttel.crmSOAP.stub.MSISDN;
import com.inter.righttel.crmSOAP.stub.QueryTopupRequestType;
import com.inter.righttel.crmSOAP.stub.SourceType;
import com.inter.righttel.crmSOAP.stub.SubscriberType;
import com.inter.righttel.crmSOAP.stub.TopupRequestType;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CRMWebServiceRequestFormatter 
{
	public static Log _log = LogFactory.getLog(CRMWebServiceRequestFormatter.class);
	String lineSep = null;
	//String _soapAction="";

	public CRMWebServiceRequestFormatter()
	{
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
			case CRMWebServiceI.ACTION_ACCOUNT_DETAILS: 
			{
				InitTopupRequestType initTopupRequest=generateInitTopupRequest(p_map);
				object= initTopupRequest;
				break;	
			}
			case CRMWebServiceI.ACTION_RECHARGE_CREDIT: 
			{

				TopupRequestType recargasRequest=generateTopupRequest(p_map);
				object= recargasRequest;
				break;	
			}
			case CRMWebServiceI.ACTION_QUERY_TOPUP: 
			{

				QueryTopupRequestType recargasRequest=generateQueryTopupRequest(p_map);
				object= recargasRequest;
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
	private TopupRequestType generateTopupRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateTopupRequest","Entered p_requestMap::"+p_requestMap);
		TopupRequestType topupRequest= null;
		try
		{
			topupRequest=new TopupRequestType();
			SubscriberType subscriber=new SubscriberType();
			MSISDN msisdn=new MSISDN(p_requestMap.get("FILTER_MSISDN").toString());
			subscriber.setMsisdn(msisdn);
			topupRequest.setSubscriber(subscriber);
			topupRequest.setAmount(Long.parseLong(p_requestMap.get("transfer_amount").toString()));
			SourceType source=new SourceType();
			source.setDistributorId(p_requestMap.get("DISTRIBUTOR_ID").toString());
			topupRequest.setSource(source);
			topupRequest.setReferenceId(p_requestMap.get("IN_TXN_ID").toString());
			//topupRequest.setTransactionId(p_requestMap.get("IN_TXN_ID").toString());
			
		}
		catch(Exception e)
		{
			_log.error("generateTopupRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateTopupRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateTopupRequest","Exiting Request recargasRequest::"+topupRequest);
		}
		return topupRequest;
	}

	private InitTopupRequestType generateInitTopupRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateInitTopupRequest","Entered p_requestMap::"+p_requestMap);
		InitTopupRequestType initTopupRequest= null;
		try
		{
			initTopupRequest=new InitTopupRequestType();
			initTopupRequest.setAmount(Long.parseLong("10"));
			SubscriberType subscriber=new SubscriberType();
			MSISDN msisdn=new MSISDN(p_requestMap.get("FILTER_MSISDN").toString());
			subscriber.setMsisdn(msisdn);
			initTopupRequest.setSubscriber(subscriber);
			SourceType source=new SourceType();
			source.setDistributorId(p_requestMap.get("DISTRIBUTOR_ID").toString());
			initTopupRequest.setSource(source);
			initTopupRequest.setReferenceId(p_requestMap.get("IN_TXN_ID").toString());
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeCreditRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request recargasRequest::"+initTopupRequest);
		}
		return initTopupRequest;
	}
	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private QueryTopupRequestType generateQueryTopupRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateQueryTopupRequest","Entered p_requestMap::"+p_requestMap);
		QueryTopupRequestType queryRequest= null;
		try
		{
			queryRequest=new QueryTopupRequestType();
			SubscriberType subscriber=new SubscriberType();
			MSISDN msisdn=new MSISDN(p_requestMap.get("FILTER_MSISDN").toString());
			subscriber.setMsisdn(msisdn);
			SourceType source=new SourceType();
			source.setDistributorId(p_requestMap.get("DISTRIBUTOR_ID").toString());
			queryRequest.setSource(source);
			queryRequest.setReferenceId(p_requestMap.get("IN_TXN_ID").toString());
			queryRequest.setTransactionId(p_requestMap.get("IN_TXN_ID").toString());
			
		}
		catch(Exception e)
		{
			_log.error("generateQueryTopupRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateQueryTopupRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateQueryTopupRequest","Exiting Request recargasRequest::"+queryRequest);
		}
		return queryRequest;
	}



}
