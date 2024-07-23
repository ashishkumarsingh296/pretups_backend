/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroPromoWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroPromoWS.stub.AudiTypeReq;
import com.inter.claroPromoWS.stub.DataAdicionalReq;
import com.inter.claroPromoWS.stub.EntregaPromocionRequest;
import com.inter.claroPromoWS.stub.PromocionReq;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroPromoWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroPromoWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroPromoWSRequestFormatter()
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
			case ClaroPromoWSI.ACTION_PROMO_CREDIT: 
			{
				EntregaPromocionRequest recargasRequest=generatePromoCreditRequest(p_map);
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
	private EntregaPromocionRequest generatePromoCreditRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generatePromoCreditRequest","Entered p_requestMap::"+p_requestMap);
		EntregaPromocionRequest crearPagoRequest= null;
		try
		{
			crearPagoRequest=new EntregaPromocionRequest();
			DataAdicionalReq adicionalRequest2=new DataAdicionalReq();
			adicionalRequest2.setClave("");
			adicionalRequest2.setValor("");
			DataAdicionalReq[] adicionalRequest={adicionalRequest2};
			
			AudiTypeReq audiTypeRequest=new AudiTypeReq();
			audiTypeRequest.setIdTransaccion(p_requestMap.get("IN_TXN_ID").toString());
			audiTypeRequest.setIpAplicacion(p_requestMap.get("IP").toString());
			audiTypeRequest.setUsuarioAplicacion(p_requestMap.get("USUASRIOAPLICACION").toString());
			audiTypeRequest.setNombreAplicacion(p_requestMap.get("NOMBREAPLICACION").toString());
			crearPagoRequest.setMsisdn(p_requestMap.get("MSISDN").toString());
			crearPagoRequest.setAudit(audiTypeRequest);
			crearPagoRequest.setListaAdicionalReq(adicionalRequest);
			Object object=p_requestMap.get("ListPromocionesType");
			PromocionReq[] listPromocionesTypes=null;
			if(object!=null)
				listPromocionesTypes=(PromocionReq[])object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			crearPagoRequest.setListaPromocionesReq(listPromocionesTypes);

		}
		catch(Exception e)
		{
			_log.error("generatePromoCreditRequest",e.getMessage());
			throw new BTSLBaseException(this,"generatePromoCreditRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generatePromoCreditRequest","Exiting Request crearPagoRequest::"+crearPagoRequest);
		}
		return crearPagoRequest;
	}


}
