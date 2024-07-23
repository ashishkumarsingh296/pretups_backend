package com.inter.righttel.crmWebService;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl;

public class CRMWebServiceResponseParser {

	private static Log _log = LogFactory.getLog(CRMWebServiceResponseParser.class.getName());

	/**
	 * This method is used to parse the response.
	 * @param	int p_action
	 * @param	String p_responseStr
	 * @return	HashMap
	 * @throws	Exception
	 */
	public HashMap parseResponse(int p_action,HashMap _requestMap,HashMap _responseMap) throws BTSLBaseException,Exception
	{
		if(_log.isDebugEnabled())_log.debug("parseResponse","Entered p_action::"+p_action+" p_responseStr:: "+_requestMap);
		try
		{
			switch(p_action)
			{
			case CRMWebServiceI.ACTION_ACCOUNT_DETAILS: 
			{
				_requestMap=parseNewQueryProfileResponse(_requestMap,_responseMap);
				break;	
			}
			case CRMWebServiceI.ACTION_RECHARGE_CREDIT: 
			{
				_requestMap=rechargePPSNew(_requestMap,_responseMap);
				break;	
			}
			case CRMWebServiceI.ACTION_QUERY_TOPUP: 
			{
				_requestMap=parseQueryRechargeResult(_requestMap,_responseMap);
				break;	
			}
			case CRMWebServiceI.ACTION_ACCOUNT_DETAILS_OTHER: 
			{
				_requestMap=checkCreditLimit(_requestMap,_responseMap);
				break;	
			}
			case CRMWebServiceI.ACTION_ACCOUNT_DETAILS_BALANCE: 
			{
				_requestMap=checkAllBalance(_requestMap,_responseMap);
				break;	
			}
			case CRMWebServiceI.ACTION_VAS_CREDIT: 
			{
				_requestMap=orderPricePlanOffer(_requestMap,_responseMap);
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
			if(_log.isDebugEnabled())_log.debug("parseResponse","Exiting map::"+_requestMap);
		}
		return _requestMap;	
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap parseNewQueryProfileResponse(HashMap _reqMap,HashMap responseMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseNewQueryProfileResponse","Entered object::"+_reqMap);

		try
		{

			if(responseMap.get("returnCode")==null){
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
			}
			_reqMap.put("INTERFACE_STATUS",responseMap.get("returnCode").toString());

			if(responseMap.get("returnCode").toString().equalsIgnoreCase(CRMWebServiceI.RESPONSE_SUCCESS)){

				_reqMap.put("SERVICE_CLASS",responseMap.get("BrandCode"));	
				
				if(responseMap.get("ISPREPAID").toString().equalsIgnoreCase("0")){
					_reqMap.put("SUBSCRIBER_TYPE","PRE");
				}else{
					_reqMap.put("SUBSCRIBER_TYPE","POST");
				}
				
				_reqMap.put("SUBSCRIBER_BLACKLIST",responseMap.get("VCBlackList"));

				if(responseMap.get("SIMStatus").toString().equalsIgnoreCase("A")){
					_reqMap.put("ACCOUNT_STATUS","ACTIVE");

				}else if(responseMap.get("SIMStatus").toString().equalsIgnoreCase("G")){
					_reqMap.put("ACCOUNT_STATUS","INACTIVE");

				}else if(responseMap.get("SIMStatus").toString().equalsIgnoreCase("D")){
					_reqMap.put("ACCOUNT_STATUS","ONEWAYBLOCK");

				}else if(responseMap.get("SIMStatus").toString().equalsIgnoreCase("E")){
					_reqMap.put("ACCOUNT_STATUS","TWOWAYBLOCK");

				}else if(responseMap.get("SIMStatus").toString().equalsIgnoreCase("B")){
					_reqMap.put("ACCOUNT_STATUS","TERMIATION");
					_reqMap.put("SIM_SUB_STATUS",responseMap.get("SIMSubStatus"));

					if(!responseMap.get("SIM_SUB_STATUS").toString().equalsIgnoreCase("Subscriber debit") && !responseMap.get("SIM_SUB_STATUS").toString().equalsIgnoreCase("balance expired"))
					{
						throw new BTSLBaseException(InterfaceErrorCodesI.RC_SUBSCRIBER_NOT_ACTIVATED);				
					}
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
				if(_reqMap.get("REQ_SERVICE").toString().equalsIgnoreCase("VCN")){
					if(responseMap.get("VCBlackList").toString().equalsIgnoreCase("Y")){
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);
					}
				}

			}else{
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);	
			}

		}
		catch(BTSLBaseException be)
		{
			_log.error("parseNewQueryProfileResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parseNewQueryProfileResponse","Exception e::"+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseNewQueryProfileResponse","Exited responseMap::"+responseMap);
		}
		return _reqMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap parseQueryRechargeResult(HashMap _reqMap,HashMap responseMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseQueryRechargeResult","Entered object::"+_reqMap);

		try
		{
			if(responseMap.get("returnCode")==null){
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
			}

			if(responseMap.get("returnCode").toString().equalsIgnoreCase(CRMWebServiceI.RESPONSE_SUCCESS)){

				_reqMap.put("result",responseMap.get("result").toString());	
				_reqMap.put("ExceptionCode",responseMap.get("exceptionCode").toString());

				if(_reqMap.get("result").toString().equalsIgnoreCase("0"))
				{
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.SUCCESS);
					_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.SUCCESS);
				}else if(_reqMap.get("result").toString().equalsIgnoreCase("1"))
				{
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.FAIL);
					_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.FAIL);
				}
				else if(_reqMap.get("result").toString().equalsIgnoreCase("2"))
				{
					if(_reqMap.get("ExceptionCode").toString().equalsIgnoreCase("0") || _reqMap.get("ExceptionCode").toString().equalsIgnoreCase("2"))
					{
						responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
					}else{
						responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.FAIL);
					}
				}
			}else{
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);	
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error("parseQueryRechargeResult","Exception e::"+be.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		catch(Exception e)
		{
			_log.error("parseQueryRechargeResult","Exception e::"+e.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseQueryRechargeResult","Exited responseMap::"+_reqMap);
		}
		return _reqMap;
	}


	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap rechargePPSNew(HashMap _reqMap,HashMap responseMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("rechargePPSNew","Entered object::"+_reqMap);
		try
		{
			if(responseMap.get("returnCode")==null){
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
			}
			_reqMap.put("INTERFACE_STATUS",responseMap.get("returnCode").toString());

			if(responseMap.get("returnCode").toString().equalsIgnoreCase(CRMWebServiceI.RESPONSE_SUCCESS)){

				_reqMap.put("balance",responseMap.get("balance").toString());	

				//_reqMap.put("expDate",responseMap.get("expDate").toString());

				if(_reqMap.get("balance")==null){
					throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
				}
			}else{
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);	
			}

		}
		catch(BTSLBaseException be)
		{
			_log.error("rechargePPSNew","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("rechargePPSNew","Exception e::"+e.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("rechargePPSNew","Exited responseMap::"+_reqMap);
		}
		return _reqMap;
	}


	public HashMap orderPricePlanOffer(HashMap _reqMap,HashMap responseMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("orderPricePlanOffer","Entered object::"+_reqMap);
		try
		{
			if(responseMap.get("returnCode")==null){
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
			}
			_reqMap.put("INTERFACE_STATUS",responseMap.get("returnCode").toString());

			if(responseMap.get("returnCode").toString().equalsIgnoreCase(CRMWebServiceI.RESPONSE_SUCCESS)){
			}else{
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);	
			}

		}
		catch(BTSLBaseException be)
		{
			_log.error("orderPricePlanOffer","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("orderPricePlanOffer","Exception e::"+e.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("orderPricePlanOffer","Exited responseMap::"+_reqMap);
		}
		return _reqMap;
	}


	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap checkCreditLimit(HashMap _reqMap,HashMap responseMap) throws BTSLBaseException,Exception
	{
		if(_log.isDebugEnabled()) _log.debug("checkCreditLimit","Entered object::"+_reqMap);
		try
		{
			if(responseMap.get("returnCode")==null){
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
			}
			_reqMap.put("INTERFACE_STATUS",responseMap.get("returnCode").toString());

			if(responseMap.get("returnCode").toString().equalsIgnoreCase(CRMWebServiceI.RESPONSE_SUCCESS)){

				_reqMap.put("balance",responseMap.get("balance").toString());	

				_reqMap.put("creditLimit",responseMap.get("creditLimit").toString());

				if(_reqMap.get("balance")==null){

					throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

				}

				if(_reqMap.get("REQ_SERVICE").toString().equalsIgnoreCase("PRC") &&  _reqMap.get("USER_TYPE").toString().equalsIgnoreCase("S")){

					double balance= Double.parseDouble(_reqMap.get("balance").toString());
					double transfer_amount= Double.parseDouble(_reqMap.get("transfer_amount").toString());
					double creditLimit= Double.parseDouble(_reqMap.get("creditLimit").toString());
					if(balance+transfer_amount>creditLimit){
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_ACC_MAX_CREDIT_LIMIT);
					}
				}
			}else{
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);	
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error("checkCreditLimit","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("checkCreditLimit","Exception e::"+e.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("checkCreditLimit","Exited responseMap::"+_reqMap);
		}
		return _reqMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap checkAllBalance(HashMap _reqMap,HashMap responseMap) throws BTSLBaseException,Exception
	{
		if(_log.isDebugEnabled()) _log.debug("checkAllBalance","Entered object::"+_reqMap);
		try
		{


			if(responseMap.get("returnCode")==null){
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
			}
			_reqMap.put("INTERFACE_STATUS",responseMap.get("returnCode").toString());


			Object object=responseMap.get("RESPONSE_OBJECT");
			AllBalanceDtoListimpl[] allBalanceDtoListimpl=null;
			if(object!=null)
				allBalanceDtoListimpl= (AllBalanceDtoListimpl[])object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(responseMap.get("returnCode").toString().equalsIgnoreCase(CRMWebServiceI.RESPONSE_SUCCESS)){			
				for (int i = 0; i < allBalanceDtoListimpl.length; i++) {
					AllBalanceDtoListimpl allBalanceDtoListimp=allBalanceDtoListimpl[i];
					if(allBalanceDtoListimp.getBalanceType().equalsIgnoreCase(CRMWebServiceI.BALANCE_TYPE))
					{
						_reqMap.put("balance",allBalanceDtoListimp.getBalanceValue());
						_reqMap.put("EXPIRY_DATE",allBalanceDtoListimp.getExpDate());
						_reqMap.put("EFFECTIVE_DATE",allBalanceDtoListimp.getEffDate());
						break;
					}
				}

			}else{
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);	
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error("checkAllBalance","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("checkAllBalance","Exception e::"+e.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("checkAllBalance","Exited responseMap::"+_reqMap);
		}
		return _reqMap;
	}
}
