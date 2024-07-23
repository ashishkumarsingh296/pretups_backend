/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroRechargeWS;

import java.text.DecimalFormat;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroRechargeWS.stub.AudiTypeRequest;
import com.inter.claroRechargeWS.stub.EjecutarRecargaRequest;
import com.inter.claroRechargeWS.stub.RequestOpcionalComplexType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroWebServiceRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroWebServiceRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroWebServiceRequestFormatter()
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
			case ClaroWebServiceI.ACTION_RECHARGE_CREDIT: 
			{

				EjecutarRecargaRequest recargasRequest=generateRechargeCreditRequest(p_map);
				object= recargasRequest;
				break;	
			}
			case ClaroWebServiceI.ACTION_IMMEDIATE_DEBIT: 
			{
				EjecutarRecargaRequest debitoRequest=generateRechargeDebitRequest(p_map);
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
	private EjecutarRecargaRequest generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
		EjecutarRecargaRequest recargasRequest= null;
		try
		{
			AudiTypeRequest autenticacionType=new AudiTypeRequest();
			autenticacionType.setIpAplicacion(p_requestMap.get("IP").toString());
			autenticacionType.setUsuarioAplicacion(p_requestMap.get("usuario").toString());
			autenticacionType.setIdTransaccion( p_requestMap.get("IN_TXN_ID").toString());
			autenticacionType.setNombreAplicacion(p_requestMap.get("codigoServicioEAI").toString());
			recargasRequest=new EjecutarRecargaRequest();
			recargasRequest.setMsisdn(p_requestMap.get("MSISDN").toString());
			recargasRequest.setMontoRecarga(p_requestMap.get("transfer_amount").toString() );
			recargasRequest.setAuditRequest(autenticacionType);
			RequestOpcionalComplexType complexType=new RequestOpcionalComplexType();
			complexType.setClave("");
			complexType.setValor("");
			RequestOpcionalComplexType[] opcionalComplexTypes={complexType};
			recargasRequest.setListaAdicional(opcionalComplexTypes);
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
	private EjecutarRecargaRequest generateRechargeDebitRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequest","Entered p_requestMap::"+p_requestMap);
		EjecutarRecargaRequest recargasRequest= null;
		StringBuffer stringBuffer = null;
		try
		{
			AudiTypeRequest autenticacionType=new AudiTypeRequest();
			autenticacionType.setIpAplicacion(p_requestMap.get("IP").toString());
			autenticacionType.setUsuarioAplicacion(p_requestMap.get("usuario").toString());
			autenticacionType.setIdTransaccion( p_requestMap.get("IN_TXN_ID").toString());
			autenticacionType.setNombreAplicacion(p_requestMap.get("codigoServicioEAI").toString());
			recargasRequest=new EjecutarRecargaRequest();
			recargasRequest.setMsisdn(p_requestMap.get("MSISDN").toString());
			recargasRequest.setMontoRecarga(new DecimalFormat("#############").parseObject(p_requestMap.get("transfer_amount").toString()).toString() );
			recargasRequest.setAuditRequest(autenticacionType);
			RequestOpcionalComplexType complexType=new RequestOpcionalComplexType();
			complexType.setClave("");
			complexType.setValor("");
			RequestOpcionalComplexType[] opcionalComplexTypes={complexType};
			recargasRequest.setListaAdicional(opcionalComplexTypes);
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


}
