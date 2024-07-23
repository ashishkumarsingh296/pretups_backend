package com.inter.claroPromoWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroPromoWS.stub.EntregaPromocionResponse;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class ClaroPromoWSResponseParser {

	private static Log _log = LogFactory.getLog(ClaroPromoWSResponseParser.class.getName());

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
			case ClaroPromoWSI.ACTION_PROMO_CREDIT: 
			{
				map=parsePromoCreditDetailsResponse(_requestMap);
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
	private HashMap parsePromoCreditDetailsResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parsePromoCreditDetailsResponse","Entered ");
		HashMap responseMap = null;
		EntregaPromocionResponse recargasResponse=null;
		try
		{
			Object object=_reqMap.get("RESPONSE_OBJECT");
			responseMap = new HashMap();

			if(object!=null)
				recargasResponse=(EntregaPromocionResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(recargasResponse!=null && !BTSLUtil.isNullString(recargasResponse.getCodigoRespuesta()))
			{
				if(_log.isDebugEnabled()) _log.debug("parsePromoCreditDetailsResponse","Response Code "+recargasResponse.getCodigoRespuesta());

				if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_SUCCESS)){
					responseMap.put("INTERFACE_STATUS",recargasResponse.getCodigoRespuesta());

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_TIMEOUT_INVALID)){

					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_MONTO_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_CLIENT_INVALID_RANGE)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_CUSTOMER_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_CLIENT);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_MSISDN_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_NUMBER);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_MONTO_AMOUNT_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_ERRRO)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_ERROR1)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_ERROR2)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_ERROR3)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

				}else if(recargasResponse.getCodigoRespuesta().equalsIgnoreCase(ClaroPromoWSI.RESPONSE_ERROR6)){
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}

			}else{
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_log.error("parsePromoCreditDetailsResponse","Invalid Error Code::");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}

		}
		catch(BTSLBaseException be)
		{
			_log.error("parsePromoCreditDetailsResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parsePromoCreditDetailsResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parsePromoCreditDetailsResponse","Exited responseMap::"+responseMap);
		}
		return responseMap;
	}







}
