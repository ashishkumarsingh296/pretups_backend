package com.inter.claroCollectionEnqWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroCollectionEnqWS.stub.ConsultaDeudaResponse;
import com.inter.claroCollectionEnqWS.stub.DeudaDocumentoType;
import com.inter.claroCollectionEnqWS.stub.DeudaServicioType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class ClaroCollEnqWSResponseParser {

	private static Log _log = LogFactory.getLog(ClaroCollEnqWSResponseParser.class.getName());

	/**
	 * This method is used to parse the response.
	 * @param	int p_action
	 * @param	String p_responseStr
	 * @return	HashMap
	 * @throws	Exception
	 */
	public HashMap parseResponse(int p_action,HashMap _requestMap) throws Exception
	{
		if(_log.isDebugEnabled())_log.debug("parseResponse","Entered p_action::"+p_action+" _requestMap:: "+_requestMap);
		HashMap map=null;
		try
		{
			switch(p_action)
			{
			case ClaroCollEnqWSI.ACTION_ACCOUNT_DETAILS: 
			{
				map=parseRechargeAccountDetailsResponse(_requestMap);
				break;	
			}
			}
		}
		catch(Exception e)
		{
			_log.error("parseResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("parseResponse","Exiting map::"+map);
		}
		return map;	
	}


	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private HashMap parseRechargeAccountDetailsResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Entered ");
		HashMap responseMap = null;
		ConsultaDeudaResponse recargasResponse=null;
		try
		{
			Object object=_reqMap.get("RESPONSE_OBJECT");
			responseMap = new HashMap();

			if(object!=null)
				recargasResponse=(ConsultaDeudaResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(recargasResponse.getAudit()!=null && !BTSLUtil.isNullString(recargasResponse.getAudit().getErrorCode()))
			{
				if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Response Code "+recargasResponse.getAudit().getErrorCode());
				responseMap.put("INTERFACE_STATUS",recargasResponse.getAudit().getErrorCode());
	
				if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_SUCCESS)){
				responseMap=parseValidateResponseObject(responseMap,_reqMap,recargasResponse);

				}else if(BTSLUtil.isStringContain(ClaroCollEnqWSI.RESPONSE_NOBILLPAY_SUCCESS,recargasResponse.getAudit().getErrorCode())){
				
					responseMap.put("INTERFACE_STATUS",recargasResponse.getAudit().getErrorCode());

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_MONTO_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_CLIENT_INVALID_RANGE)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_CUSTOMER_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_MSISDN_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_NUMBER);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_MONTO_AMOUNT_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERRRO)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR1)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR2)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR3)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAudit().getErrorCode().equalsIgnoreCase(ClaroCollEnqWSI.RESPONSE_ERROR6)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}

			}else{
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_log.error("parseRechargeCreditResponse","Invalid Error Code::");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}

		}
		catch(BTSLBaseException be)
		{
			_log.error("parseRechargeCreditResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parseRechargeCreditResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Exited responseMap::"+responseMap);
		}
		return responseMap;
	}


	/**
	 * @author vipan.kumar
	 * @param responseMap
	 * @param requestMap
	 * @param recargasResponse
	 * @return
	 * @throws Exception
	 */
	private HashMap parseValidateResponseObject(HashMap responseMap,HashMap requestMap, ConsultaDeudaResponse recargasResponse) throws Exception{
		if(_log.isDebugEnabled()) _log.debug("parseValidateResponseObject","Started recargasResponse::"+recargasResponse);
		try{
			
			boolean flag=false;
			DeudaServicioType[] deudaServicioType=recargasResponse.getXDeudaCliente();
			boolean invoicenofound =false; 
			if(deudaServicioType!=null)
			{

				for (int i = 0; i < deudaServicioType.length; i++) {

					if(deudaServicioType[i].getXCodTipoServicio().equalsIgnoreCase((String)requestMap.get("CODTIPOSERVICIO")))
					{
						responseMap.put("TOTAL_PENDING_BALANCE",String.valueOf(deudaServicioType[i].getXMontoDeudaTotal().doubleValue()));
						responseMap.put("SERVICE_NAME",deudaServicioType[i].getXOpcionRecaudacion());
						responseMap.put("SERVICE_CODE",deudaServicioType[i].getXCodTipoServicio());

						DeudaDocumentoType[] deudaDocumentoType=deudaServicioType[i].getXDeudaDocs();
						if(deudaDocumentoType!=null)
						{	
							responseMap.put("INVOICE_SIZE", String.valueOf(deudaDocumentoType.length));
							for (int j = 0; j < deudaDocumentoType.length; j++) {

								responseMap.put("SERVICE_NAME_"+j,deudaDocumentoType[j].getXDescripServ());
								responseMap.put("SERVICE_CODE_"+j,deudaDocumentoType[j].getXTipoServicio());
								responseMap.put("INVOICE_NUM_"+j,deudaDocumentoType[j].getXNumeroDoc());
								responseMap.put("PERIOD_PENDING_BALANCE_"+j,String.valueOf(deudaDocumentoType[j].getXMontoDebe().doubleValue()));
								responseMap.put("MIN_PENDING_BALANCE_"+j,String.valueOf(deudaDocumentoType[j].getXMontoFact().doubleValue()));
								responseMap.put("INVOICED_PENDING_BALANCE_"+j,String.valueOf(deudaDocumentoType[j].getXImportePagoMin().doubleValue()));
								responseMap.put("BILL_PERIOD_START_"+j,BTSLUtil.getDateStringFromDate(deudaDocumentoType[j].getXFechaEmision().getTime(),"dd/MM/yy"));
								responseMap.put("BILL_PERIOD_END_"+j,BTSLUtil.getDateStringFromDate(deudaDocumentoType[j].getXFechaVenc().getTime(),"dd/MM/yy"));
								flag=true;
								 if (deudaDocumentoType[j].getXNumeroDoc().equals(requestMap.get("INVOICE_NUMBER"))&&!invoicenofound)
										invoicenofound=true;	
								
							}
							if(!BTSLUtil.isNullString((String)requestMap.get("INVOICE_NUMBER"))&&!invoicenofound)
							{
							 throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_INVOICE_NO);	
							}
							else if(BTSLUtil.isNullString((String)requestMap.get("INVOICE_NUMBER")))
								responseMap.put("INVOICE_NUMBER", deudaDocumentoType[0].getXNumeroDoc());
						}else{
							_log.error("parseValidateResponseObject","Invalid Response object deudaDocumentoType:");
							flag=false;

						}
					}
				}


			}else{
				_log.error("parseValidateResponseObject","Invalid Response object DeudaServicioType:");
				flag=false;

			}

			if(!flag){
				_log.error("parseValidateResponseObject","Invalid Response Object ");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}

		}catch (Exception e) {
			_log.error("parseValidateResponseObject","Exception e::"+e.getMessage());
			if(InterfaceErrorCodesI.ERROR_INVALID_INVOICE_NO.equals(e.getMessage()))
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_INVOICE_NO);	
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
		}
		// TODO Auto-generated method stub
		return responseMap;
	}

}
