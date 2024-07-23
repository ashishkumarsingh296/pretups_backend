/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroChannelUserValWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroChannelUserValWS.stub.AudiTypeRequest;
import com.inter.claroChannelUserValWS.stub.EvaluaPedidoRequest;
import com.inter.claroChannelUserValWS.stub.RequestOpcionalComplexType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;


/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroCUValWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroCUValWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroCUValWSRequestFormatter()
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
			case ClaroCUValWSI.ACTION_ACCOUNT_DETAILS: 
			{
				EvaluaPedidoRequest recargasRequest=generateCUValRequest(p_map);
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

	private EvaluaPedidoRequest generateCUValRequest(HashMap p_map) throws Exception{
		if(_log.isDebugEnabled()) _log.debug("generateCUValRequest","Entered p_requestMap::"+p_map);
		EvaluaPedidoRequest consultarEvaluacionCrediticiaPedidosRequest= null;
		try
		{
			consultarEvaluacionCrediticiaPedidosRequest=new EvaluaPedidoRequest();
			RequestOpcionalComplexType _adicionalRequest=new RequestOpcionalComplexType();
			_adicionalRequest.setClave("");
			_adicionalRequest.setValor("");
			RequestOpcionalComplexType[] _adicionalRequests={_adicionalRequest};
			AudiTypeRequest audiTypeRequest=new AudiTypeRequest();
			audiTypeRequest.setIdTransaccion(p_map.get("IN_TXN_ID").toString());
			audiTypeRequest.setIpAplicacion(p_map.get("IP").toString());
			audiTypeRequest.setNombreAplicacion(p_map.get("nombre").toString());
			audiTypeRequest.setUsuarioAplicacion(p_map.get("usuario").toString());
			consultarEvaluacionCrediticiaPedidosRequest.setCodigoDAC(p_map.get("CODIGO").toString());
			consultarEvaluacionCrediticiaPedidosRequest.setMontoPedido(p_map.get("transfer_amount").toString());
			consultarEvaluacionCrediticiaPedidosRequest.setAudit(audiTypeRequest);
			consultarEvaluacionCrediticiaPedidosRequest.setListaOpcionalRequest(_adicionalRequests);

		}
		catch(Exception e)
		{
			_log.error("generateRechargeDebitRequest","Exception e: "+e);
			throw new BTSLBaseException(this,"generateCUValRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateCUValRequest","Exiting Request debitoRequest::"+consultarEvaluacionCrediticiaPedidosRequest);
		}
		return consultarEvaluacionCrediticiaPedidosRequest;
	}



}
