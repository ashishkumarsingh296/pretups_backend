package com.inter.claroRechargeWS;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.inter.claroDTHRechargeWS.ClaroDTHWSI;
import com.inter.claroRechargeWS.stub.DatosClienteComplexType;
import com.inter.claroRechargeWS.stub.EjecutarRecargaResponse;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class ClaroWebServiceResponseParser {

	private static Log _log = LogFactory.getLog(ClaroWebServiceResponseParser.class.getName());

	/**
	 * This method is used to parse the response.
	 * @param	int p_action
	 * @param	String p_responseStr
	 * @return	HashMap
	 * @throws	Exception
	 */
	public HashMap parseResponse(int p_action,HashMap _requestMap) throws Exception
	{
		if(_log.isDebugEnabled())_log.debug("parseResponse","Entered p_action::"+p_action+" p_responseStr:: "+_requestMap);
		HashMap map=null;
		try
		{
			switch(p_action)
			{
			case ClaroDTHWSI.ACTION_ACCOUNT_DETAILS: 
			{
				map=parseRechargeAccountDetailsResponse(_requestMap);
				break;	
			}
			case ClaroWebServiceI.ACTION_RECHARGE_CREDIT: 
			{
				map=parseRechargeCreditResponse(_requestMap);
				break;	
			}
			case ClaroWebServiceI.ACTION_IMMEDIATE_DEBIT:
			{
				map=parseImmediateDebitResponse(_requestMap);
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
	public HashMap parseRechargeCreditResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Entered object::"+_reqMap);
		HashMap responseMap = null;
		EjecutarRecargaResponse recargasResponse=null;
		try
		{
			responseMap = new HashMap();

			Object object=_reqMap.get("RESPONSE_OBJECT");

			if(object!=null)
				recargasResponse=(EjecutarRecargaResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(recargasResponse!=null && !BTSLUtil.isNullString(recargasResponse.getAuditResponse().getCodigoRespuesta())){

				responseMap.put("INTERFACE_STATUS",recargasResponse.getAuditResponse().getCodigoRespuesta());

				if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_SUCCESS)){
					double preBalance=0;
					try{
						
						DatosClienteComplexType[] clienteComplexTypes  =recargasResponse.getCliente();
						
						for (int i = 0; i < 1; i++) {
							preBalance=Double.parseDouble(clienteComplexTypes[i].getSaldoActual())-Double.parseDouble(InterfaceUtil.getSystemAmountFromINAmount(_reqMap.get("transfer_amount").toString(),Double.parseDouble(_reqMap.get("MULT_FACTOR").toString())));	
							responseMap.put("INTERFACE_POST_BALANCE", clienteComplexTypes[i].getSaldoActual());
							responseMap.put("INTERFACE_PRE_BALANCE",new DecimalFormat("#############").format(preBalance));
							responseMap.put("NEW_EXPIRY_DATE", BTSLUtil.formatDateFromOnetoAnother(clienteComplexTypes[i].getFechaExpiracion(),"yyyy-MM-dd HH:mm:ss", "ddMMyyyyHHmmss"));   
						}
						
					}catch (Exception e) {
						preBalance=0;
					}
					
					
				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_TIMEOUT_INVALID)){

					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_MONTO_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_CLIENT_INVALID_RANGE)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_CUSTOMER_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_MSISDN_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_NUMBER);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_MONTO_AMOUNT_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERRRO)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR1)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR2)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR3)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR6)){
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
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	private HashMap parseImmediateDebitResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Entered object::"+_reqMap);
		HashMap responseMap = null;
		EjecutarRecargaResponse recargasResponse=null;
		try
		{
			responseMap = new HashMap();

			Object object=_reqMap.get("RESPONSE_OBJECT");

			if(object!=null)
				recargasResponse=(EjecutarRecargaResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(recargasResponse!=null && !BTSLUtil.isNullString(recargasResponse.getAuditResponse().getCodigoRespuesta())){

				responseMap.put("INTERFACE_STATUS",recargasResponse.getAuditResponse().getCodigoRespuesta());

				if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_SUCCESS)){
					double preBalance=0;
					try{
						
						DatosClienteComplexType[] clienteComplexTypes  =recargasResponse.getCliente();
						
						for (int i = 0; i < 1; i++) {
							preBalance=Double.parseDouble(clienteComplexTypes[i].getSaldoActual())-Double.parseDouble(InterfaceUtil.getSystemAmountFromINAmount(_reqMap.get("transfer_amount").toString(),Double.parseDouble(_reqMap.get("MULT_FACTOR").toString())));	
							responseMap.put("INTERFACE_POST_BALANCE", clienteComplexTypes[i].getSaldoActual());
							responseMap.put("INTERFACE_PRE_BALANCE",new DecimalFormat("#############").format(preBalance));	
							responseMap.put("NEW_EXPIRY_DATE", BTSLUtil.formatDateFromOnetoAnother(clienteComplexTypes[i].getFechaExpiracion(),"yyyy-MM-dd HH:mm:ss", "ddMMyyyyHHmmss"));
						}
						
					}catch (Exception e) {
						preBalance=0;
					}
					
					
				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_TIMEOUT_INVALID)){

					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_MONTO_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_CLIENT_INVALID_RANGE)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_CUSTOMER_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_MSISDN_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_NUMBER);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_MONTO_AMOUNT_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERRRO)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR1)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR2)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR3)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getAuditResponse().getCodigoRespuesta().equalsIgnoreCase(ClaroWebServiceI.RESPONSE_ERROR6)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
			}else{
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_log.error("parseImmediateDebitResponse","Invalid Error Code::");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}	
		}
		catch(BTSLBaseException be)
		{
			_log.error("parseImmediateDebitResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parseImmediateDebitResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap parseRechargeAccountDetailsResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Entered ");
		HashMap responseMap = null;
		try
		{
			String _interfaceID=(String)_reqMap.get("INTERFACE_ID");

			responseMap = new HashMap();

			String accountStatus = FileCache.getValue(_interfaceID,"ACCOUNT_STATUS");
			if(_log.isDebugEnabled())_log.debug("parseRechargeAccountDetailsResponse","accountStatus:"+accountStatus);
			if(InterfaceUtil.isNullString(accountStatus))
			{
				accountStatus="ACTIVE";
			}
			accountStatus = accountStatus.trim();
			responseMap.put("ACCOUNT_STATUS",accountStatus);	

			String interfacePreviousBalance = FileCache.getValue(_interfaceID,"INTERFACE_PREV_BALANCE");
			if(_log.isDebugEnabled())_log.debug("parseRechargeAccountDetailsResponse","interfacePreviousBalance:"+interfacePreviousBalance);
			if(InterfaceUtil.isNullString(interfacePreviousBalance))
			{
				interfacePreviousBalance="0";
			}
			interfacePreviousBalance = interfacePreviousBalance.trim();
			responseMap.put("INTERFACE_PREV_BALANCE",interfacePreviousBalance);	

			String expiryDate = FileCache.getValue(_interfaceID,"OLD_EXPIRY_DATE");
			if(_log.isDebugEnabled())_log.debug("parseRechargeAccountDetailsResponse","serviceClass:"+expiryDate);
			if(InterfaceUtil.isNullString(expiryDate))
			{
				expiryDate="01012090";
			}
			expiryDate = expiryDate.trim();
			responseMap.put("OLD_EXPIRY_DATE",expiryDate);	

			String serviceClass = FileCache.getValue(_interfaceID,"SERVICE_CLASS");
			if(_log.isDebugEnabled())_log.debug("parseRechargeAccountDetailsResponse","serviceClass:"+serviceClass);
			if(InterfaceUtil.isNullString(serviceClass))
			{
				serviceClass="ALL";
			}
			serviceClass = serviceClass.trim();
			responseMap.put("SERVICE_CLASS",serviceClass);	

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
}
