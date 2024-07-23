package com.inter.oloapi;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;
import com.inter.claroDTHRechargeWS.ClaroDTHWSI;
import com.inter.oloapi.stub.PrepayRechargeResponse;
import com.inter.oloapi.stub.ValidateCustomerPreTupsResponse;

public class OLORechargeResponseParser {

	private static Log _log = LogFactory.getLog(OLORechargeResponseParser.class.getName());

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
			case OLORechargeI.ACTION_RECHARGE_CREDIT: 
			{
				map=parseRechargeCreditResponse(_requestMap);
				break;	
			}
			case OLORechargeI.ACTION_IMMEDIATE_DEBIT:
			{
				map=parseImmediateDebitResponse(_requestMap);
				break;	
			}
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error("parseResponse","Exception e::"+be.getMessage());
			throw be;
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
		if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Entered object::"+_reqMap+" response::: "+(PrepayRechargeResponse)_reqMap.get("RESPONSE_OBJECT")+"response status::::: "+((PrepayRechargeResponse)_reqMap.get("RESPONSE_OBJECT")).getStatus());
		HashMap responseMap = null;
		PrepayRechargeResponse recargasResponse=null;
		try
		{
			responseMap = new HashMap();

			Object object=_reqMap.get("RESPONSE_OBJECT");

			if(object!=null)
				recargasResponse=(PrepayRechargeResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(recargasResponse!=null && !BTSLUtil.isNullString(recargasResponse.getStatus())){
				_reqMap.put("INTERFACE_STATUS", recargasResponse.getStatus());

				responseMap.put("INTERFACE_STATUS",recargasResponse.getStatus());

				if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.RESPONSE_SUCCESS)){
					responseMap.put("CORRELATION", recargasResponse.getCorrelation());
					
					_log.debug("parseRechargeCreditResponse", "response status = "+recargasResponse.getStatus());
					
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.RESPONSE_INVALID_KEY)){

					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_INVALID_KEY);

				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.APPLICATION_FAIL)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_APPLICATION_FAIL);

				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.CUSTOMER_NOT_EXIST)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_CUSTOMER_NOT_EXIST);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.DUPLICATE_ENTRY)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_DUPLICATE_ENTRY);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.EXT_REF_CODE_LENGTH_EXCEEDS)){
					throw new BTSLBaseException(InterfaceErrorCodesI.EXT_REF_CODE_LENGTH_EXCEEDS);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.BANK_CODE_NOT_EXIST)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_BANK_CODE_NOT_EXIST);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.BANK_CODE_LENGTH_EXCEEDS)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_BANK_CODE_LENGTH_EXCEEDS);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.DATE_LENGTH_EXCEEDS)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_DATE_LENGTH_EXCEEDS);
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
		PrepayRechargeResponse recargasResponse=null;
		try
		{
			responseMap = new HashMap();

			Object object=_reqMap.get("RESPONSE_OBJECT");

			if(object!=null)
				recargasResponse=(PrepayRechargeResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(recargasResponse!=null && !BTSLUtil.isNullString(recargasResponse.getStatus())){

				_reqMap.put("INTERFACE_STATUS",recargasResponse.getStatus());
				responseMap.put("INTERFACE_STATUS",recargasResponse.getStatus());

				if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.RESPONSE_SUCCESS)){
					responseMap.put("CORRELATION", recargasResponse.getCorrelation());
					
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.RESPONSE_INVALID_KEY)){

					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_INVALID_KEY);

				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.APPLICATION_FAIL)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_APPLICATION_FAIL);

				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.CUSTOMER_NOT_EXIST)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_CUSTOMER_NOT_EXIST);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.DUPLICATE_ENTRY)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_DUPLICATE_ENTRY);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.EXT_REF_CODE_LENGTH_EXCEEDS)){
					throw new BTSLBaseException(InterfaceErrorCodesI.EXT_REF_CODE_LENGTH_EXCEEDS);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.BANK_CODE_NOT_EXIST)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_BANK_CODE_NOT_EXIST);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.BANK_CODE_LENGTH_EXCEEDS)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_BANK_CODE_LENGTH_EXCEEDS);
				}else if(recargasResponse.getStatus().equalsIgnoreCase(OLORechargeI.DATE_LENGTH_EXCEEDS)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_DATE_LENGTH_EXCEEDS);	
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
			}else{
				_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
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
		if(_log.isDebugEnabled()) _log.debug("parseRechargeAccountDetailsResponse","Entered object::"+_reqMap);
		HashMap responseMap = null;
		ValidateCustomerPreTupsResponse validateResponse=null;
		try
		{
			String _interfaceID=(String)_reqMap.get("INTERFACE_ID");
			responseMap = new HashMap();

			String validateRequired = FileCache.getValue(_interfaceID,_reqMap.get("REQ_SERVICE")+"_"+_reqMap.get("USER_TYPE"));
			if("Y".equals(validateRequired)){

			Object object=_reqMap.get("RESPONSE_OBJECT");

			if(object!=null)
				validateResponse=(ValidateCustomerPreTupsResponse)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(validateResponse!=null && !BTSLUtil.isNullString(validateResponse.getStatus())){
				_reqMap.put("INTERFACE_STATUS",validateResponse.getStatus());

				responseMap.put("INTERFACE_STATUS",validateResponse.getStatus());				
				
				if(validateResponse.getStatus().equalsIgnoreCase(OLORechargeI.RESPONSE_SUCCESS)){
					responseMap.put("RECEIVER_NAME",validateResponse.getName());
					responseMap.put("RECEIVER_TYPE",validateResponse.getType());
					responseMap.put("NO_OF_PLANS",validateResponse.getNum_plans());
					responseMap.put("PLANS",validateResponse.getPlans());
					
					String requestedAmount = ""+(Long.parseLong((String)_reqMap.get("REQUESTED_AMOUNT")))/100;
					
				/*	boolean flag =false;
				for(int i=0; i<Integer.parseInt(validateResponse.getNum_plans()); i++){
					if(((String)_reqMap.get("PLAN_ID")).equalsIgnoreCase(((ValidateCustomerPlan[])validateResponse.getPlans())[i].getId()) && requestedAmount.equalsIgnoreCase(((ValidateCustomerPlan[])validateResponse.getPlans())[i].getPrice())){
						flag = true;
					}
				}
				if(flag == false)
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);*/
					
				}else if(validateResponse.getStatus().equalsIgnoreCase(OLORechargeI.RESPONSE_INVALID_KEY)){

					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_INVALID_KEY);

				}else if(validateResponse.getStatus().equalsIgnoreCase(OLORechargeI.APPLICATION_FAIL)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_APPLICATION_FAIL);

				}else if(validateResponse.getStatus().equalsIgnoreCase(OLORechargeI.CUSTOMER_NOT_EXIST)){
					throw new BTSLBaseException(InterfaceErrorCodesI.OLO_ERROR_RESPONSE_CUSTOMER_NOT_EXIST);
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
			}else{
				_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_log.error("parseRechargeAccountDetailsResponse","Invalid Error Code::");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}
			}
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
		catch(BTSLBaseException be)
		{
			_log.error("parseRechargeAccountDetailsResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parseRechargeAccountDetailsResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseRechargeAccountDetailsResponse","Exited responseMap::"+responseMap);
			/*for(int i=0;i<Integer.parseInt((String)responseMap.get("NO_OF_PLANS"));i++){
				 _log.debug("parseRechargeAccountDetailsResponse",(((ValidateCustomerPlan[])responseMap.get("PLANS"))[i]).getId());
			}*/
		}
		return responseMap;
	}
}
