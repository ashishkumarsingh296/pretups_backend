/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroCollPayWS;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroCollPayWS.stub.CrearAnulacion;
import com.inter.claroCollPayWS.stub.CrearPago;
import com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoReqType;
import com.inter.claroCollPayWS.stub.CrearPagoDetServicioReqType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

/**
 * @author vipan.kumar
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroCollPayWSRequestFormatter 
{
	public static Log _log = LogFactory.getLog(ClaroCollPayWSRequestFormatter.class);
	String lineSep = null;
	String _soapAction="";

	public ClaroCollPayWSRequestFormatter()
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
			case ClaroCollPayWSI.ACTION_RECHARGE_CREDIT: 
			{
				CrearPago recargasRequest=generateRechargeCreditRequest(p_map);
				object= (Object)recargasRequest;
				break;	
			}
			case ClaroCollPayWSI.ACTION_IMMEDIATE_DEBIT: 
			{
				CrearAnulacion recargasRequest=generateRechargeDebitRequest(p_map);
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
	private CrearAnulacion generateRechargeDebitRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequest","Entered p_requestMap::"+p_requestMap);
		CrearAnulacion crearPagoRequest= null;
		try
		{
			Calendar cal = Calendar.getInstance();
			
			crearPagoRequest=new CrearAnulacion();
			crearPagoRequest.setTxId(p_requestMap.get("IN_TXN_ID").toString());
			crearPagoRequest.setpCodAplicacion(p_requestMap.get("CODAPLICACION").toString());
			crearPagoRequest.setpExtorno(p_requestMap.get("EXTORNO").toString());//Need to add
			
			
			crearPagoRequest.setpCodBanco(p_requestMap.get("CODBANCO").toString());
			
			crearPagoRequest.setpCodReenvia(p_requestMap.get("CODREENVIA").toString());
			
			crearPagoRequest.setpCodMoneda(p_requestMap.get("CODMONEDA").toString());
			crearPagoRequest.setpTipoIdentific(p_requestMap.get("TIPOIDETIFIC").toString());
			crearPagoRequest.setpDatoidentific(p_requestMap.get("MSISDN").toString());//Mobile Numbre
			
			if(BTSLUtil.isNullString((String)p_requestMap.get("TXN_DATE")))
			{
				crearPagoRequest.setpFechaHora(cal);
			}else{
				Date date;
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault()).parse((String)p_requestMap.get("TXN_DATE"));
				cal.setTime(date);
				crearPagoRequest.setpFechaHora(cal);
			}
		
			if(BTSLUtil.isNullString((String)p_requestMap.get("INFO2")))
			{
				crearPagoRequest.setpTrace(p_requestMap.get("TRACE").toString());//Need to add
			}else{
				crearPagoRequest.setpTrace(p_requestMap.get("INFO2").toString());//Need to add
			}
			
			if(BTSLUtil.isNullString((String)p_requestMap.get("INFO1")))
			{
				crearPagoRequest.setpNroOperacion(p_requestMap.get("NROOPERACION").toString());//Need to add
			}else{
				crearPagoRequest.setpNroOperacion(p_requestMap.get("INFO1").toString());//Need to add
			}
			
			
			crearPagoRequest.setpCodAgencia(p_requestMap.get("CODAGENCIA").toString());
			crearPagoRequest.setpCodCanal(p_requestMap.get("CODCANAL").toString());
			crearPagoRequest.setpCodCiudad(p_requestMap.get("CODCIUDAD").toString());
			crearPagoRequest.setpPlaza(p_requestMap.get("PLAZA").toString());
			
			crearPagoRequest.setpNombreComercio(p_requestMap.get("NOMBRECOMERCIO").toString());
			crearPagoRequest.setpNroComercio(p_requestMap.get("NUMEROCOMERCIO").toString());
			crearPagoRequest.setpNroReferencia("");
			
			
			crearPagoRequest.setpNroTerminal(p_requestMap.get("NROTERMINAL").toString());//Need to add
			crearPagoRequest.setpImportePago(new BigDecimal(p_requestMap.get("IMPORTEPAGO").toString()));//Amount
			
			crearPagoRequest.setpCodBancoOrig(p_requestMap.get("CODBANCO").toString());//Need to add
			
			crearPagoRequest.setpCodReenviaOrig(p_requestMap.get("CODREENVIA").toString());//Need to add
			
			if(BTSLUtil.isNullString((String)p_requestMap.get("BILL_PAY_INFO2")))
			{
				crearPagoRequest.setpTraceOrig(p_requestMap.get("TRACE").toString());//Need to add
			}else{
				crearPagoRequest.setpTraceOrig(p_requestMap.get("BILL_PAY_INFO2").toString());//Need to add
			}
			
			if(BTSLUtil.isNullString((String)p_requestMap.get("BILL_PAY_TXN_DATE")))
			{
				crearPagoRequest.setpFechaHoraOrig(cal);
			}else{
				Calendar cal1 = Calendar.getInstance();
				Date date;
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault()).parse((String)p_requestMap.get("BILL_PAY_TXN_DATE"));
				cal1.setTime(date);
				crearPagoRequest.setpFechaHoraOrig(cal1);
			}
		
			if(BTSLUtil.isNullString((String)p_requestMap.get("BILL_PAY_INFO1")))
			{
				crearPagoRequest.setpNroOperCobrOrig(p_requestMap.get("NROOPERACION").toString());//Need to add
			}else{
				crearPagoRequest.setpNroOperCobrOrig(p_requestMap.get("BILL_PAY_INFO1").toString());//Need to add
			}
			if(BTSLUtil.isNullString((String)p_requestMap.get("BILL_PAY_INFO1")))
			{
				crearPagoRequest.setpNroOperAcreOrig(p_requestMap.get("NROOPERACION").toString());//Need to add
			}else{
				crearPagoRequest.setpNroOperAcreOrig(p_requestMap.get("BILL_PAY_INFO1").toString());//Need to add
			}
			crearPagoRequest.setpCodTipoServicio(p_requestMap.get("CODTIPOSERVICIO").toString());//Sub Service Code
			
			
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest",e.getMessage());
			throw new BTSLBaseException(this,"generateRechargeDebitRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeDebitRequest","Exiting Request crearPagoRequest::"+crearPagoRequest);
		}
		return crearPagoRequest;
	}


	/**
	 * 
	 * @param p_map
	 * @return
	 * @throws Exception
	 */
	private CrearPago generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
		CrearPago crearPagoRequest= null;
		try
		{
			Calendar cal = Calendar.getInstance();
			
			crearPagoRequest=new CrearPago();
			crearPagoRequest.setTxId(p_requestMap.get("IN_TXN_ID").toString());
			crearPagoRequest.setpCodAplicacion(p_requestMap.get("CODAPLICACION").toString());
			crearPagoRequest.setpExtorno(p_requestMap.get("EXTORNO").toString());//Need to add
			
			crearPagoRequest.setpCodBanco(p_requestMap.get("CODBANCO").toString());
			
				crearPagoRequest.setpCodReenvia(p_requestMap.get("CODREENVIA").toString());
			
			crearPagoRequest.setpCodMoneda(p_requestMap.get("CODMONEDA").toString());
			crearPagoRequest.setpTipoIdentific(p_requestMap.get("TIPOIDETIFIC").toString());
			crearPagoRequest.setpDatoIdentific(p_requestMap.get("MSISDN").toString());//Mobile Numbre
			
			if(BTSLUtil.isNullString((String)p_requestMap.get("TXN_DATE")))
			{
				crearPagoRequest.setpFechaHora(cal);
			}else{
				Date date;
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault()).parse((String)p_requestMap.get("TXN_DATE"));
				cal.setTime(date);
				crearPagoRequest.setpFechaHora(cal);
			}
		
			if(BTSLUtil.isNullString((String)p_requestMap.get("INFO2")))
			{
				crearPagoRequest.setpTrace(p_requestMap.get("TRACE").toString());//Need to add
			}else{
				crearPagoRequest.setpTrace(p_requestMap.get("INFO2").toString());//Need to add
			}
			
			if(BTSLUtil.isNullString((String)p_requestMap.get("INFO1")))
			{
				crearPagoRequest.setpNroOperacion(p_requestMap.get("NROOPERACION").toString());//Need to add
			}else{
				crearPagoRequest.setpNroOperacion(p_requestMap.get("INFO1").toString());//Need to add
			}
			crearPagoRequest.setpCodAgencia(p_requestMap.get("CODAGENCIA").toString());
			crearPagoRequest.setpCodCanal(p_requestMap.get("CODCANAL").toString());
			crearPagoRequest.setpCodCiudad(p_requestMap.get("CODCIUDAD").toString());
			crearPagoRequest.setpNroTerminal(p_requestMap.get("NROTERMINAL").toString());
			crearPagoRequest.setpPlaza(p_requestMap.get("PLAZA").toString());
			crearPagoRequest.setpMedioPago(p_requestMap.get("MEDIOPAGO").toString());//Need to add
			crearPagoRequest.setpNroReferencia(p_requestMap.get("NROREFERENCIA").toString());
			crearPagoRequest.setpPagoEfectivo(new BigDecimal(p_requestMap.get("PAGOTOTAL").toString()));//Amount
			crearPagoRequest.setpPagoTotal(new BigDecimal(p_requestMap.get("PAGOTOTAL").toString()));//Amount
			crearPagoRequest.setpDatoTransaccion(p_requestMap.get("DATOTRANSACCION").toString());//Need to add
			crearPagoRequest.setpNombreComercio(p_requestMap.get("NOMBRECOMERCIO").toString());
			crearPagoRequest.setpNroComercio(p_requestMap.get("NUMEROCOMERCIO").toString());
			crearPagoRequest.setpNroCheque1("");
			crearPagoRequest.setpNroCheque2("");
			crearPagoRequest.setpNroCheque3("");
			crearPagoRequest.setpPlazaBcoCheque1("");
			crearPagoRequest.setpPlazaBcoCheque2("");
			crearPagoRequest.setpPlazaBcoCheque3("");
			crearPagoRequest.setpBcoGiradCheque1("");
			crearPagoRequest.setpBcoGiradCheque2("");
			crearPagoRequest.setpBcoGiradCheque3("");
			
			CrearPagoDetServicioReqType crearPagoDetServicioReqType=new CrearPagoDetServicioReqType();
			
			CrearPagoDetDocumentoReqType crearPagoDetDocumentoReqType=new CrearPagoDetDocumentoReqType();
			crearPagoDetDocumentoReqType.setpTipoServicio(p_requestMap.get("CODTIPOSERVICIO").toString());//Service Code
			crearPagoDetDocumentoReqType.setpNumeroDoc(p_requestMap.get("NUMERODOC").toString());//Invoice Number
			crearPagoDetDocumentoReqType.setpMontoOrigDeuda(new BigDecimal(p_requestMap.get("PAGOTOTAL").toString()));//Need to add
			crearPagoDetDocumentoReqType.setpMontoPagado(new BigDecimal(p_requestMap.get("PAGOTOTAL").toString()));//Need to add
			crearPagoDetDocumentoReqType.setpCodConcepto5(p_requestMap.get("CODCONCEPTO5").toString());//Need to add
			if(!InterfaceUtil.isNullString(p_requestMap.get("IMPORTECONCEPTO1").toString()))
				crearPagoDetDocumentoReqType.setpImporteConcepto1(new BigDecimal(p_requestMap.get("IMPORTECONCEPTO1").toString()));//Need to add
			if(!InterfaceUtil.isNullString(p_requestMap.get("IMPORTECONCEPTO2").toString()))
				crearPagoDetDocumentoReqType.setpImporteConcepto2(new BigDecimal(p_requestMap.get("IMPORTECONCEPTO2").toString()));//Need to add
			if(!InterfaceUtil.isNullString(p_requestMap.get("IMPORTECONCEPTO3").toString()))
				crearPagoDetDocumentoReqType.setpImporteConcepto3(new BigDecimal(p_requestMap.get("IMPORTECONCEPTO3").toString()));//Need to add
			if(!InterfaceUtil.isNullString(p_requestMap.get("IMPORTECONCEPTO4").toString()))
				crearPagoDetDocumentoReqType.setpImporteConcepto4(new BigDecimal(p_requestMap.get("IMPORTECONCEPTO4").toString()));//Need to add
			if(!InterfaceUtil.isNullString(p_requestMap.get("IMPORTECONCEPTO5").toString()))
				crearPagoDetDocumentoReqType.setpImporteConcepto5(new BigDecimal(p_requestMap.get("IMPORTECONCEPTO5").toString()));//Need to add
			crearPagoDetDocumentoReqType.setpPeriodoCotizacion("");
			crearPagoDetDocumentoReqType.setpCodConcepto1("");
			crearPagoDetDocumentoReqType.setpCodConcepto2("");
			crearPagoDetDocumentoReqType.setpCodConcepto3("");
			crearPagoDetDocumentoReqType.setpCodConcepto4("");
			crearPagoDetDocumentoReqType.setpCodConcepto5("");
			crearPagoDetDocumentoReqType.setpDatoDocumento("");
			CrearPagoDetDocumentoReqType[] crearPagoDetDocumentoReqType2={crearPagoDetDocumentoReqType};//Need to add
			crearPagoDetServicioReqType.setpDetalleDocs(crearPagoDetDocumentoReqType2);//Need to add
			crearPagoDetServicioReqType.setpEstadoDeudor(p_requestMap.get("ESTADODEUDOR").toString());//Need to add
			crearPagoDetServicioReqType.setpCodTipoServicio(p_requestMap.get("CODTIPOSERVICIO").toString());//Service Code
			crearPagoDetServicioReqType.setpNroDocs(new BigDecimal(p_requestMap.get("NRODOCS").toString()));//Invoice Number
			crearPagoDetServicioReqType.setpMontoPagado(new BigDecimal(p_requestMap.get("PAGOTOTAL").toString()));//Need to add
			crearPagoDetServicioReqType.setpDatoServicio("");
			CrearPagoDetServicioReqType[] crearPagoDetServicioReqType2={crearPagoDetServicioReqType};
			
			crearPagoRequest.setpDetDocumentos(crearPagoDetServicioReqType2);
			
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest",e.getMessage());
			throw new BTSLBaseException(this,"generateRechargeCreditRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request crearPagoRequest::"+crearPagoRequest);
		}
		return crearPagoRequest;
	}


	


}
