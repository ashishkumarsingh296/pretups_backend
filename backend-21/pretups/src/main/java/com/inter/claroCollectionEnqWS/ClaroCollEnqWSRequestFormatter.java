/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroCollectionEnqWS;

import java.math.BigDecimal;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroCollectionEnqWS.stub.ConsultaDeuda;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroCollEnqWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroCollEnqWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroCollEnqWSRequestFormatter()
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
			case ClaroCollEnqWSI.ACTION_ACCOUNT_DETAILS: 
			{
				ConsultaDeuda recargasRequest=generateRechargeValidateRequest(p_map);
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
	private ConsultaDeuda generateRechargeValidateRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeValidateRequest","Entered p_requestMap::"+p_requestMap);
		ConsultaDeuda consultaDeudaRequest= null;
		try
		{
			consultaDeudaRequest=new ConsultaDeuda();
			consultaDeudaRequest.setTxId(p_requestMap.get("IN_TXN_ID").toString());
			consultaDeudaRequest.setpCodAgencia(p_requestMap.get("CODAGENCIA").toString());
			consultaDeudaRequest.setpCodAplicacion(p_requestMap.get("CODAPLICACION").toString());
			consultaDeudaRequest.setpCodBanco(p_requestMap.get("CODBANCO").toString());
			consultaDeudaRequest.setpCodCanal(p_requestMap.get("CODCANAL").toString());
			consultaDeudaRequest.setpCodCiudad(p_requestMap.get("CODCIUDAD").toString());
			consultaDeudaRequest.setpCodMoneda(p_requestMap.get("CODMONEDA").toString());
			consultaDeudaRequest.setpCodReenvia(p_requestMap.get("CODREENVIA").toString());
			consultaDeudaRequest.setpCodTipoServicio(p_requestMap.get("CODTIPOSERVICIO").toString());//Sub Service Code
			consultaDeudaRequest.setpDatoIdentific(p_requestMap.get("MSISDN").toString());//Mobile Numbre
			consultaDeudaRequest.setpNumeroComercio(p_requestMap.get("NUMEROCOMERCIO").toString());//User Id
			consultaDeudaRequest.setpNombreComercio(p_requestMap.get("NOMBRECOMERCIO").toString());//User Name
			consultaDeudaRequest.setpNroReferencia(p_requestMap.get("NROREFERENCIA").toString());
			consultaDeudaRequest.setpNroTerminal(p_requestMap.get("NROTERMINAL").toString());
			consultaDeudaRequest.setpPlaza(p_requestMap.get("PLAZA").toString());
			consultaDeudaRequest.setpPosUltDocumento(new BigDecimal(p_requestMap.get("POSUITDOCUMENTO").toString()));
			consultaDeudaRequest.setpTipoIdentific(p_requestMap.get("TIPOIDETIFIC").toString());
			
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest",e.getMessage());
			throw new BTSLBaseException(this,"generateRechargeValidateRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeValidateRequest","Exiting Request consultaDeudaRequest::"+consultaDeudaRequest);
		}
		return consultaDeudaRequest;
	}


	


}
