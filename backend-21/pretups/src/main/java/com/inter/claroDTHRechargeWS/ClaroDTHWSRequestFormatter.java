/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroDTHRechargeWS;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroDTHRechargeWS.stub.RecargarDTHPrepagoRequest;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroDTHWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroDTHWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroDTHWSRequestFormatter()
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
			case ClaroDTHWSI.ACTION_RECHARGE_CREDIT: 
			{

				RecargarDTHPrepagoRequest recargasRequest=generateRechargeCreditRequest(p_map);
				object= (Object)recargasRequest;
				break;	
			}
			case ClaroDTHWSI.ACTION_IMMEDIATE_DEBIT: 
			{
				RecargarDTHPrepagoRequest recargasRequest=generateRechargeDebitRequest(p_map);
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
	private RecargarDTHPrepagoRequest generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
		RecargarDTHPrepagoRequest recargarDTHPrepagoRequest= null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
		SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);
		
		try
		{
			recargarDTHPrepagoRequest=new RecargarDTHPrepagoRequest();
			recargarDTHPrepagoRequest.setIdTransacion( p_requestMap.get("IN_TXN_ID").toString());
			recargarDTHPrepagoRequest.setCodigoRecarga(p_requestMap.get("MSISDN").toString());
			recargarDTHPrepagoRequest.setMonto(Double.parseDouble(p_requestMap.get("transfer_amount").toString() ));
			recargarDTHPrepagoRequest.setCodigoAplicacion(p_requestMap.get("codigoAplicacion").toString() );
			recargarDTHPrepagoRequest.setIpAplicacion(p_requestMap.get("IP").toString());
			recargarDTHPrepagoRequest.setFechaTransaccion(sdf.format(new Date()));
			recargarDTHPrepagoRequest.setHoraTransaccion(sdf1.format(new Date()));
			recargarDTHPrepagoRequest.setTipoProducto(p_requestMap.get("tipoProducto").toString() );
			recargarDTHPrepagoRequest.setBinAdquiriente(p_requestMap.get("binAdquiriente").toString() );
			recargarDTHPrepagoRequest.setForwardInstitucion(p_requestMap.get("forwardInstitucion").toString() );
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeCreditRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request recargasRequest::"+recargarDTHPrepagoRequest);
		}
		return recargarDTHPrepagoRequest;
	}


	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private RecargarDTHPrepagoRequest generateRechargeDebitRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequest","Entered p_requestMap::"+p_requestMap);
		RecargarDTHPrepagoRequest recargarDTHPrepagoRequest= null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
		SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);
		try
		{
			recargarDTHPrepagoRequest=new RecargarDTHPrepagoRequest();
			recargarDTHPrepagoRequest.setIdTransacion( p_requestMap.get("IN_TXN_ID").toString());
			recargarDTHPrepagoRequest.setCodigoRecarga(p_requestMap.get("MSISDN").toString());
			recargarDTHPrepagoRequest.setMonto(Double.parseDouble(p_requestMap.get("transfer_amount").toString() ));
			recargarDTHPrepagoRequest.setCodigoAplicacion(p_requestMap.get("codigoAplicacion").toString() );
			recargarDTHPrepagoRequest.setIpAplicacion(p_requestMap.get("IP").toString());
			recargarDTHPrepagoRequest.setFechaTransaccion(sdf.format(new Date()));
			recargarDTHPrepagoRequest.setHoraTransaccion(sdf1.format(new Date()));
			recargarDTHPrepagoRequest.setTipoProducto(p_requestMap.get("tipoProducto").toString() );
			recargarDTHPrepagoRequest.setBinAdquiriente(p_requestMap.get("binAdquiriente").toString() );
			recargarDTHPrepagoRequest.setForwardInstitucion(p_requestMap.get("forwardInstitucion").toString() );
		}
		catch(Exception e)
		{
			_log.error("generateRechargeDebitRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeDebitRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeDebitRequest","Exiting Request debitoRequest::"+recargarDTHPrepagoRequest);
		}
		return recargarDTHPrepagoRequest;
	}


}
