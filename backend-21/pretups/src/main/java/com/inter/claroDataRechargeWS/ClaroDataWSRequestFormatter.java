/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroDataRechargeWS;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroDataRechargeWS.stub.EjecutarRecargaDatosRequest;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroDataWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroDataWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroDataWSRequestFormatter()
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
			case ClaroDataWSI.ACTION_RECHARGE_CREDIT: 
			{

				EjecutarRecargaDatosRequest recargasRequest=generateRechargeCreditRequest(p_map);
				object= (Object)recargasRequest;
				break;	
			}
			case ClaroDataWSI.ACTION_IMMEDIATE_DEBIT: 
			{
				EjecutarRecargaDatosRequest recargasRequest=generateRechargeDebitRequest(p_map);
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
	private EjecutarRecargaDatosRequest generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
		EjecutarRecargaDatosRequest recargarDataPrepagoRequest= null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
		SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);
		
		try
		{
			recargarDataPrepagoRequest=new EjecutarRecargaDatosRequest();
			recargarDataPrepagoRequest.setIdTransaccion( p_requestMap.get("IN_TXN_ID").toString());
			recargarDataPrepagoRequest.setIpAplicacion(p_requestMap.get("IP").toString());
			recargarDataPrepagoRequest.setNombreAplicacion(p_requestMap.get("nombreAplicacion").toString() );
			recargarDataPrepagoRequest.setFechaTX(sdf.format(new Date()));
			recargarDataPrepagoRequest.setHoraTX(sdf1.format(new Date()));
			recargarDataPrepagoRequest.setBinAdquiriente(p_requestMap.get("binAdquiriente").toString() );
			recargarDataPrepagoRequest.setForwardInstitution(p_requestMap.get("forwardInstitucion").toString() );
			recargarDataPrepagoRequest.setProducto(p_requestMap.get("producto").toString() );
			recargarDataPrepagoRequest.setTelefono(p_requestMap.get("MSISDN").toString());
			recargarDataPrepagoRequest.setMonto(new DecimalFormat("#############").parseObject(p_requestMap.get("transfer_amount").toString()).toString() );
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeCreditRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request recargasDataRequest::"+recargarDataPrepagoRequest);
		}
		return recargarDataPrepagoRequest;
	}


	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private EjecutarRecargaDatosRequest generateRechargeDebitRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequest","Entered p_requestMap::"+p_requestMap);
		EjecutarRecargaDatosRequest recargarDataPrepagoRequest= null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
		SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss",Locale.ENGLISH);
		try
		{
			recargarDataPrepagoRequest=new EjecutarRecargaDatosRequest();
			recargarDataPrepagoRequest.setIdTransaccion( p_requestMap.get("IN_TXN_ID").toString());
			recargarDataPrepagoRequest.setIpAplicacion(p_requestMap.get("IP").toString());
			recargarDataPrepagoRequest.setNombreAplicacion(p_requestMap.get("nombreAplicacion").toString() );
			recargarDataPrepagoRequest.setFechaTX(sdf.format(new Date()));
			recargarDataPrepagoRequest.setHoraTX(sdf1.format(new Date()));
			recargarDataPrepagoRequest.setBinAdquiriente(p_requestMap.get("binAdquiriente").toString() );
			recargarDataPrepagoRequest.setForwardInstitution(p_requestMap.get("forwardInstitucion").toString() );
			recargarDataPrepagoRequest.setProducto(p_requestMap.get("producto").toString() );
			recargarDataPrepagoRequest.setTelefono(p_requestMap.get("MSISDN").toString());
			recargarDataPrepagoRequest.setMonto(new DecimalFormat("#############").parseObject(p_requestMap.get("transfer_amount").toString()).toString() );
		}
		catch(Exception e)
		{
			_log.error("generateRechargeDebitRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateRechargeDebitRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeDebitRequest","Exiting Request debitoRequest::"+recargarDataPrepagoRequest);
		}
		return recargarDataPrepagoRequest;
	}


}
