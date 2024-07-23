package com.inter.claroUserInfoWS;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class ClaroCUInfoWSResponseParser {

	private static Log _log = LogFactory.getLog(ClaroCUInfoWSResponseParser.class.getName());

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
			case ClaroCUInfoWSI.ACTION_ACCOUNT_DETAILS: 
			{
				map=parseCUInfoResponse(_requestMap);
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
	public HashMap parseCUInfoResponse(HashMap _requestMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseCUInfoResponse","Entered ");
		HashMap responseMap = null;
		DistribuidorDataResponseType response=null;
		try
		{
			Object object=_requestMap.get("RESPONSE_OBJECT");
			responseMap = new HashMap();

			if(object!=null)
				response=(DistribuidorDataResponseType)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);


			if(response!=null && response.getRetorno()!=null){

				responseMap.put("INTERFACE_STATUS",response.getRetorno().getResultado());

				if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_SUCCESS))
				{
					if(response.getDatos().getT_Datos()!= null)
					{

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getNombre()))
						{
							responseMap.put("CHANNELUSER_NAME","NA");
						}else{
							responseMap.put("CHANNELUSER_NAME",response.getDatos().getT_Datos().getNombre());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getDireccion()))
						{
							responseMap.put("CHANNELUSER_ADDRESS","NA");
						}else{
							responseMap.put("CHANNELUSER_ADDRESS",response.getDatos().getT_Datos().getDireccion());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getEmail()))
						{
							responseMap.put("CHANNELUSER_EMAIL","NA");
						}else{
							responseMap.put("CHANNELUSER_EMAIL",response.getDatos().getT_Datos().getEmail());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getTelefono()))
						{

							responseMap.put("CHANNELUSER_TELEPHONE","NA");
						}else{
							if(!BTSLUtil.isNumeric(response.getDatos().getT_Datos().getTelefono()))
							{
								responseMap.put("CHANNELUSER_TELEPHONE","");
								_log.debug("parseCUInfoResponse","User Telephone Number is not numeric ");
							}
							else
								responseMap.put("CHANNELUSER_TELEPHONE",response.getDatos().getT_Datos().getTelefono());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getCiudad()))
						{
							responseMap.put("CHANNELUSER_CITY","NA");
						}else{
							responseMap.put("CHANNELUSER_CITY",response.getDatos().getT_Datos().getCiudad());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getEstado()))
						{
							responseMap.put("CHANNELUSER_STATE","NA");
						}else{
							responseMap.put("CHANNELUSER_STATE",response.getDatos().getT_Datos().getEstado());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getLimiteCredito()))
						{
							responseMap.put("CHANNELUSER_CREDITLIMIT","NA");
						}else{
							responseMap.put("CHANNELUSER_CREDITLIMIT",response.getDatos().getT_Datos().getLimiteCredito());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getPeriodoCicloPago()))
						{
							responseMap.put("CHANNELUSER_PAYCYCLEPERIOD","NA");
						}else{
							responseMap.put("CHANNELUSER_PAYCYCLEPERIOD",response.getDatos().getT_Datos().getPeriodoCicloPago());
						}

						if(BTSLUtil.isNullString(response.getDatos().getT_Datos().getPeriodoPago()))
						{
							responseMap.put("CHANNELUSER_PAYPERIOD","NA");
						}else{
							responseMap.put("CHANNELUSER_PAYPERIOD",response.getDatos().getT_Datos().getPeriodoPago());
						}

					}else{
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INVALID_RESPONSE_OBJECT);
					}
				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_MONTO_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_CLIENT_INVALID_RANGE)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_CUSTOMER_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_MSISDN_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_MONTO_AMOUNT_INVALID)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_ERRRO)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_ERROR1)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_ERROR2)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_ERROR3)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);

				}else if(response.getRetorno().getResultado().equalsIgnoreCase(ClaroCUInfoWSI.RESPONSE_ERROR6)){
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
				}

			}else{
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_log.error("parseCUInfoResponse","Invalid Error Code::");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			}

		}
		catch(BTSLBaseException be)
		{
			_log.error("parseCUInfoResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parseCUInfoResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseCUInfoResponse","Exited responseMap::"+responseMap);
		}
		return responseMap;
	}


}
