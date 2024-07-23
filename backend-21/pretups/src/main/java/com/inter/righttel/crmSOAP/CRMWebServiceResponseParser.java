package com.inter.righttel.crmSOAP;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.util.PretupsBL;
import com.inter.righttel.crmSOAP.stub.AccountType;
import com.inter.righttel.crmSOAP.stub.InitTopupResponseType;
import com.inter.righttel.crmSOAP.stub.QueryTopupResponseType;
import com.inter.righttel.crmSOAP.stub.TopupResponseType;

public class CRMWebServiceResponseParser {

	private static Log _log = LogFactory.getLog(CRMWebServiceResponseParser.class.getName());

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
			case CRMWebServiceI.ACTION_ACCOUNT_DETAILS: 
			{
				map=parseInitTopupResponse(_requestMap);
				break;	
			}
			case CRMWebServiceI.ACTION_RECHARGE_CREDIT: 
			{
				map=parseTopupResponse(_requestMap);
				break;	
			}
			case CRMWebServiceI.ACTION_QUERY_TOPUP: 
			{
				map=parseQueryTopupResponse(_requestMap);
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
	public HashMap parseTopupResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseTopupResponse","Entered object::"+_reqMap);
		HashMap responseMap = null;
		TopupResponseType topupResponse=null;
		try
		{
			responseMap = new HashMap();

			Object object=_reqMap.get("RESPONSE_OBJECT");

			if(object!=null)
				topupResponse=(TopupResponseType)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(topupResponse!=null){

				responseMap.put("INTERFACE_STATUS",String.valueOf(topupResponse.getResponseCode()));
				_reqMap.put("INTERFACE_STATUS",String.valueOf(topupResponse.getResponseCode()));

				if(topupResponse.getResponseCode()==0){
					long preBalance=0;
					try{
						responseMap.put("INTERFACE_TXN_ID", topupResponse.getTransactionId());
						AccountType accType=topupResponse.getRechargedAccount();
						long unitRelation=accType.getUnitRelation();
						Long postbalance=0l;
						try{
							double a=accType.getNewBalance()/unitRelation;
							double b=(accType.getNewBalance()-accType.getAmount())/unitRelation;
							postbalance=PretupsBL.getSystemAmount(a);
							preBalance=PretupsBL.getSystemAmount(b);
						}catch (Exception e) {
							postbalance=accType.getNewBalance();
						}
						responseMap.put("INTERFACE_POST_BALANCE", String.valueOf(postbalance));
						responseMap.put("INTERFACE_PRE_BALANCE",String.valueOf(preBalance));
						responseMap.put("NEW_EXPIRY_DATE", accType.getExpiryDate().toString());
					}catch (Exception e) {
						preBalance=0;
					}
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}			}else{
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
	public HashMap parseInitTopupResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseinitTopupResponse","Entered object::"+_reqMap);
		HashMap responseMap = null;
		InitTopupResponseType initTopupResponse=null;
		try
		{
			responseMap = new HashMap();

			Object object=_reqMap.get("RESPONSE_OBJECT");

			if(object!=null)
				initTopupResponse=(InitTopupResponseType)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

			if(initTopupResponse!=null){

				responseMap.put("INTERFACE_STATUS",String.valueOf(initTopupResponse.getResponseCode()));
				_reqMap.put("INTERFACE_STATUS",String.valueOf(initTopupResponse.getResponseCode()));
				if(initTopupResponse.getResponseCode()==0){
					responseMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
					String _interfaceID=(String)_reqMap.get("INTERFACE_ID");
					String accountStatus = FileCache.getValue(_interfaceID,"ACCOUNT_STATUS");
					if(_log.isDebugEnabled())_log.debug("parseRechargeAccountDetailsResponse","accountStatus:"+accountStatus);
					if(InterfaceUtil.isNullString(accountStatus))
					{
						accountStatus="ACTIVE";
					}
					accountStatus = accountStatus.trim();
					responseMap.put("ACCOUNT_STATUS",accountStatus);
					String serviceClass = FileCache.getValue(_interfaceID,"SERVICE_CLASS");
					if(_log.isDebugEnabled())_log.debug("parseRechargeAccountDetailsResponse","serviceClass:"+serviceClass);
					if(InterfaceUtil.isNullString(serviceClass))
					{
						serviceClass="ALL";
					}
					serviceClass = serviceClass.trim();
					responseMap.put("SERVICE_CLASS",serviceClass);	

					responseMap.put("INTERFACE_TXN_ID", initTopupResponse.getTransactionId());
					AccountType acc=initTopupResponse.getRechargedAccount();
					if(acc!=null)
					{
						responseMap.put("INTERFACE_PREV_BALANCE",acc.getNewBalance());
						responseMap.put("OLD_EXPIRY_DATE",acc.getExpiryDate());
					}
				}else{
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
			}else{
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_log.error("parseinitTopupResponse","Invalid Error Code::");
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);	
			}	
		}
		catch(BTSLBaseException be)
		{
			_log.error("parseinitTopupResponse","Exception e::"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.error("parseinitTopupResponse","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseinitTopupResponse","Exited responseMap::"+responseMap);
		}
		return responseMap;
	}

	/**
	 * This method is used to parse the credit response. 
	 * @param p_responseStr
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap parseQueryTopupResponse(HashMap _reqMap) throws Exception
	{
		if(_log.isDebugEnabled()) _log.debug("parseQueryTopupResponse","Entered object::"+_reqMap);
		HashMap responseMap = null;
		QueryTopupResponseType queryResponse=null;
		try
		{
			responseMap = new HashMap();
			Object object=_reqMap.get("RESPONSE_OBJECT");
			if(object!=null)
				queryResponse=(QueryTopupResponseType)object;
			else
				throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
			if(queryResponse!=null){
				responseMap.put("INTERFACE_STATUS",String.valueOf(queryResponse.getResponseCode()));
				_reqMap.put("INTERFACE_STATUS",String.valueOf(queryResponse.getResponseCode()));
				
				if(queryResponse.getResponseCode()==0){
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.SUCCESS);
					_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.SUCCESS);
					
				}else if(queryResponse.getResponseCode()==1){
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.FAIL);
					_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.FAIL);
				}else if(queryResponse.getResponseCode()==2){
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
					_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				}

			}else{
				responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				_log.error("parseRechargeCreditResponse","Null Error Code::");
			}	
		}
		catch(BTSLBaseException be)
		{
			_log.error("parseQueryTopupResponse","Exception e::"+be.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		catch(Exception e)
		{
			_log.error("parseQueryTopupResponse","Exception e::"+e.getMessage());
			responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
			_reqMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("parseQueryTopupResponse","Exited responseMap::"+responseMap);
		}
		return responseMap;
	}
}
