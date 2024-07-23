package com.inter.claroChannelUserValWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroChannelUserValWS.stub.EvaluaPedidoResponse;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class ClaroCUValWSResponseParser {

	private static Log _log = LogFactory.getLog(ClaroCUValWSResponseParser.class.getName());

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
			case ClaroCUValWSI.ACTION_ACCOUNT_DETAILS: 
			{
				map=parseCUValResponse(_requestMap);
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
	public HashMap parseCUValResponse(HashMap _requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseCUValResponse","Entered ");
		HashMap responseMap = null;
		EvaluaPedidoResponse response=null;
		try
		{
			Object object=_requestMap.get("RESPONSE_OBJECT");
			responseMap = new HashMap();

			if(object!=null)
				response=(EvaluaPedidoResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);


			if(response!=null && !BTSLUtil.isNullString(response.getCodigoRespuesta())){

					responseMap.put("INTERFACE_STATUS",response.getCodigoRespuesta());

					if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_SUCCESS))
					{
						if(!response.getValorResultadoCrediticio().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_NOTAUTO_APPROVED))
						{
							responseMap.put("VALUE_RESULT_STATUS",response.getValorResultadoCrediticio());
							throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
						}
					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_TIMEOUT_INVALID)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_MONTO_INVALID)){
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);
					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_CLIENT_INVALID_RANGE)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);
					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_CUSTOMER_INVALID)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_MSISDN_INVALID)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_NUMBER);

					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_MONTO_AMOUNT_INVALID)){
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_ERRRO)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_ERROR1)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_ERROR2)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_ERROR3)){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

					}else if(response.getCodigoRespuesta().equalsIgnoreCase(ClaroCUValWSI.RESPONSE_ERROR6)){
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
			_log.error("parseCUValResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parseCUValResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseCUValResponse","Exited responseMap::"+responseMap);
		}
		return responseMap;
	}


}
