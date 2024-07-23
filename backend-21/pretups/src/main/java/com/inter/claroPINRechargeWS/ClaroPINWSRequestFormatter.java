/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroPINRechargeWS;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroPINRechargeWS.stub.RecargaPinVirtualRequest;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroPINWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroPINWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroPINWSRequestFormatter()
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
			case ClaroPINWSI.ACTION_RECHARGE_CREDIT: 
			{

				RecargaPinVirtualRequest recargasRequest=generateRechargeCreditRequest(p_map);
				object= (Object)recargasRequest;
				break;	
			}
			case ClaroPINWSI.ACTION_IMMEDIATE_DEBIT: 
			{
				RecargaPinVirtualRequest recargasRequest=generateRechargeDebitRequest(p_map);
				object= (Object)recargasRequest;
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
	private RecargaPinVirtualRequest generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
		RecargaPinVirtualRequest recargarPINPrepagoRequest= null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
		SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);
		
		try
		{
			recargarPINPrepagoRequest=new RecargaPinVirtualRequest();
			recargarPINPrepagoRequest.setIdTransaccion( p_requestMap.get("IN_TXN_ID").toString());
			recargarPINPrepagoRequest.setMsisdn(p_requestMap.get("MSISDN").toString());
			recargarPINPrepagoRequest.setMonto(p_requestMap.get("transfer_amount").toString());
			recargarPINPrepagoRequest.setNombreAplicacion(p_requestMap.get("nombreAplicacion").toString() );
			recargarPINPrepagoRequest.setIpAplicacion(p_requestMap.get("IP").toString());
			recargarPINPrepagoRequest.setFecha(sdf.format(new Date()));
			recargarPINPrepagoRequest.setHora(sdf1.format(new Date()));
			recargarPINPrepagoRequest.setTipo(p_requestMap.get("tipo").toString() );
			recargarPINPrepagoRequest.setBinadquiriente(p_requestMap.get("binAdquiriente").toString() );
			recargarPINPrepagoRequest.setForwardinst(p_requestMap.get("forwardInstitucion").toString() );
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeCreditRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request recargasRequest::"+recargarPINPrepagoRequest);
		}
		return recargarPINPrepagoRequest;
	}


	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private RecargaPinVirtualRequest generateRechargeDebitRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequest","Entered p_requestMap::"+p_requestMap);
		RecargaPinVirtualRequest recargarPINPrepagoRequest= null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
		SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);
		try
		{
			recargarPINPrepagoRequest=new RecargaPinVirtualRequest();
			recargarPINPrepagoRequest.setIdTransaccion( p_requestMap.get("IN_TXN_ID").toString());
			recargarPINPrepagoRequest.setMsisdn(p_requestMap.get("MSISDN").toString());
			recargarPINPrepagoRequest.setMonto(p_requestMap.get("transfer_amount").toString());
			recargarPINPrepagoRequest.setNombreAplicacion(p_requestMap.get("nombreAplicacion").toString() );
			recargarPINPrepagoRequest.setIpAplicacion(p_requestMap.get("IP").toString());
			recargarPINPrepagoRequest.setFecha(sdf.format(new Date()));
			recargarPINPrepagoRequest.setHora(sdf1.format(new Date()));
			recargarPINPrepagoRequest.setTipo(p_requestMap.get("tipo").toString() );
			recargarPINPrepagoRequest.setBinadquiriente(p_requestMap.get("binAdquiriente").toString() );
			recargarPINPrepagoRequest.setForwardinst(p_requestMap.get("forwardInstitucion").toString() );
		}
		catch(Exception e)
		{
			_log.error("generateRechargeDebitRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeDebitRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeDebitRequest","Exiting Request debitoRequest::"+recargarPINPrepagoRequest);
		}
		return recargarPINPrepagoRequest;
	}


}
