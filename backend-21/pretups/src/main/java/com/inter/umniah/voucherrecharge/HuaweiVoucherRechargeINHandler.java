package com.inter.umniah.voucherrecharge;
/**
 * @(#)HuaweiVoucherRechargeINHandler.java
 * Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *     Author				     Date			        History
 *-------------------------------------------------------------------------------------------------
 * Narendra Kumar		May 02, 2014		Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This class is the Handler class for the HuaweiEVR interface.
 */
import java.util.HashMap;
import java.net.URLDecoder;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeRequest;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResult;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrBindingStub;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeRequestMsg;
import com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeader;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeaderRequestType;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader;
import com.inter.umniah.huawei.www.bme.cbsinterface.common.SessionEntityType;

public class HuaweiVoucherRechargeINHandler implements InterfaceHandler {
	private Log _log = LogFactory.getLog(HuaweiVoucherRechargeINHandler.class.getName());
	private HashMap _requestMap = null;//Contains the request parameter as key and value pair.
	private HashMap _responseMap = null;//Contains the response of the request as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inTXNID=null;//Used to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;//Used to store the reference of transaction id.
	private InterfaceCloserVO _interfaceCloserVO= null;
	private InterfaceCloser _interfaceCloser=null;
	private String _interfaceClosureSupport=null;
	/**
	 * This method would be used to validate the subscriber's account at the IN.
	 * @param	HashMap p_requestMap
	 * @throws	BTSLBaseException,Exception
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
		if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");			
			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
			if("N".equals(validateRequired))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				return ;
			}
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);			            
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiVoucherRechargeINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:"+e.getMessage());
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS); 
		}
		finally
		{	
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);
		}
	}//end of validate
	
	/**
	 * This method would be used to credit the subscriber's account at the IN.
	 * @param	HashMap p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
		double huaweiMultFactorDouble=0;
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_inTXNID=InterfaceUtil.getINTransactionID();
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_msisdn=(String)_requestMap.get("MSISDN");
			if(InterfaceUtil.isNullString((String)_requestMap.get("PIN")) || !InterfaceUtil.isNumeric((String)_requestMap.get("PIN")))
			{
				_log.error("credit","PIN is either is null or non-numeric");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiVoucherRechargeINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PIN is either is null or non-numeric");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//Fetching the HUAWEI_MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String huaweiMultiplicationFactor = FileCache.getValue(_interfaceID,"HUAWEI_MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("credit","huaweiMultiplicationFactor:"+huaweiMultiplicationFactor);
			if(InterfaceUtil.isNullString(huaweiMultiplicationFactor))
			{
			    _log.error("credit","HUAWEI_MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiVoucherRechargeINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "HUAWEI_MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			huaweiMultiplicationFactor = huaweiMultiplicationFactor.trim();
			_requestMap.put("HUAWEI_MULT_FACTOR",huaweiMultiplicationFactor);
			huaweiMultFactorDouble=Double.parseDouble(huaweiMultiplicationFactor);
			//Set the interface parameters into requestMap
			setInterfaceParameters(HuaweiVoucherRechargeI.ACTION_VOUCHER_RECHARGE);
			//sending the Voucher Re-charge request to IN along with recharge action defined in HuaweiI interface
			sendRequestToIN(HuaweiVoucherRechargeI.ACTION_VOUCHER_RECHARGE,_requestMap);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
			try
			{
				String postBalanceStr = String.valueOf((String) _responseMap.get("NEW_BALANCE"));
				postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,huaweiMultFactorDouble);
				
				_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("credit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiVoucherRechargeINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
				//throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.ERROR_RESPONSE);
			}
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
		} 
		catch (BTSLBaseException be)
        {
    		if((be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
    		{
    			p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
        		_log.error("credit","BTSLBaseException be:"+be.getMessage());   
        		throw be;
    		}
    		throw be;
    		/*try
			{    			
        		_requestMap.put("TRANSACTION_TYPE","CR");
    			handleCancelTransaction();
    		}
    		catch(BTSLBaseException bte)
			{
				throw bte;
			}
			catch(Exception e)
			{
				 e.printStackTrace();
				 _log.error("credit","Exception e:"+e.getMessage());
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				 throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}		*/	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("credit", "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiVoucherRechargeINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
		}
	}//end of credit
	/**
	 * This method would be used to adjust the credit of subscriber account at the IN.
	 * @param	HashMap p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}
	/**
	 * This method would be used to adjust the debit of subscriber account at the IN.
	 * @param	HashMap p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */
	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}//end of debitAdjust

	/**
	 * This method is responsible to send the request to IN.
	 * @param	String p_inRequestStr
	 * @param	int p_action
	 * @throws BTSLBaseException
	 */
	public void sendRequestToIN(int p_action,HashMap<String,String> p_requestMap) throws BTSLBaseException
		{
			if(_log.isDebugEnabled()) 
				_log.debug("sendRequestToIN"," p_action="+p_action);
		
			//Put the request string, action, interface id, network code in the Transaction log.
			TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"_requestMap: "+_requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+p_action );	
			try
			{
						 if(p_action==HuaweiVoucherRechargeI.ACTION_VOUCHER_RECHARGE)
			                {
			                        _responseMap=sendVoucherRechargeRequestToHuwaiServer();
			                         if(_log.isDebugEnabled()) _log.debug("sendRequestToIN : ACTION_VOUCHER_RECHARGE ","Received Response Map ="+_responseMap);
			                         TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+_responseMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"ACTION_VOUCHER_RECHARGE="+p_action);
			                }							
			}
			catch(BTSLBaseException be)
			{
				_log.error("sendRequestToIN","BTSLBaseException be = "+be.getMessage());
				throw be;
			}//end of BTSLBaseException
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN","Exception="+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"HuaweiVoucherRechargeINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}//end of catch-Exception
			finally
			{
				if (_log.isDebugEnabled())_log.debug("sendRequestToIN","Exited _responseMap: " + _responseMap);
				
			}//end of finally
		}//end of sendRequestToIN

	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}//end of validityAdjust


	/**
	 * This method is used to set the interface parameters into request map.
	 * @param	int p_action
	 * @throws	BTSLBaseException,Exception
	 */
	public void setInterfaceParameters(int p_action) throws BTSLBaseException,Exception 
	{
		if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered p_action = "+p_action);
		try
		{
			switch(p_action)
			{
			case HuaweiVoucherRechargeI.ACTION_ACCOUNT_INFO:
				break;
			case HuaweiVoucherRechargeI.ACTION_VOUCHER_RECHARGE: // Need to get the values for Voucher recharge
				
				String versionNumber = FileCache.getValue(_interfaceID,"VERSION_NUMBER");
				_requestMap.put("VERSION_NUMBER",versionNumber.trim());

				String bankCode = FileCache.getValue(_interfaceID,"BANK_CODE");
				_requestMap.put("BANK_CODE",bankCode.trim());
				
				String serialNumber = (String)_requestMap.get("SERIAL_NUMBER");
				_requestMap.put("SERIAL_NUMBER",serialNumber.trim());
				
				String reqType = FileCache.getValue(_interfaceID,"REQ_TYPE");
				_requestMap.put("REQ_TYPE",reqType.trim());
				
				String RemoteAddress = FileCache.getValue(_interfaceID,"REMOTE_ADDRESS");
				_requestMap.put("REMOTE_ADDRESS",RemoteAddress.trim());
				
				String url = FileCache.getValue(_interfaceID,"URL_1");
                if(InterfaceUtil.isNullString(url))
                {
                    _log.error("setInterfaceParameters","Value of URL is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiVoucherRechargeINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "URL is not defined in the INFile.");
                        throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("URL",url.trim());
				
				String userName = FileCache.getValue(_interfaceID,"USER_NAME");
                _requestMap.put("USER_NAME",userName.trim());
				
				String password = FileCache.getValue(_interfaceID,"PASSWORD");
                _requestMap.put("PASSWORD",password.trim());
				
				String timeout = FileCache.getValue(_interfaceID,"TIME_OUT");
                _requestMap.put("TIME_OUT",timeout.trim());
				
				break;
			case HuaweiVoucherRechargeI.ACTION_IMMEDIATE_DEBIT:
				break;
			}	       
		}//end of try block
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			_log.error("setInterfaceParameters","Exception e = "+e.getMessage());
			throw e;
		}//end of catch-Exception 
		finally
		{
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Exited _requestMap"+_requestMap);
		}//end of finally
	}//end of setInterfaceParameters
	/**
	* Method to send EVR request to IN to Recharge. 	
	* @throws	BTSLBaseException 
	*/
	private HashMap sendVoucherRechargeRequestToHuwaiServer() throws BTSLBaseException
	    {
	            if(_log.isDebugEnabled())_log.debug("sendVoucherRechargeRequestToHuwaiServer","Entered");
	            //System.out.println("Entered the logger@@@@");

	        HashMap responseMap = new HashMap<String,String>();
			CardRechargeMgrBindingStub _servicePayment=null;
			VoucherRechargeRequestMsg  voucherRechargeRequestMsg = null;
			VoucherRechargeResultMsg voucherRechargeResultMsg = null;
			VoucherRechargeRequest voucherRechargeRequest=null;
			VoucherTestConnector  serviceConnection =null;
			VoucherRechargeResult voucherRechargeResult=null;
			ResultHeader resultHeader = null;
			long startTime=0;
			long endTime=0;
				
			//Put the request string, action, interface id, network code in the Transaction log.
			try
			{	
				voucherRechargeRequest= new VoucherRechargeRequest(); 
				RequestHeader requestHeader= new RequestHeader();
				SessionEntityType entityType =new SessionEntityType(); 
				requestHeader.setCommandId("VoucherRecharge");
				requestHeader.setVersion((String)_requestMap.get("VERSION_NUMBER"));
				requestHeader.setTransactionId((String)_requestMap.get("TRANSACTION_ID"));
				requestHeader.setSequenceId((String)_requestMap.get("IN_TXN_ID"));
				requestHeader.setSerialNo((String)_requestMap.get("SERIAL_NUMBER"));
				requestHeader.setRequestType(new RequestHeaderRequestType((String)_requestMap.get("REQ_TYPE")));
				entityType.setName((String)_requestMap.get("USER_NAME"));
				entityType.setPassword((String)_requestMap.get("PASSWORD"));
				entityType.setRemoteAddress((String)_requestMap.get("REMOTE_ADDRESS"));
				requestHeader.setSessionEntity(entityType);
				voucherRechargeRequest.setSubscriberNo((String)_requestMap.get("MSISDN"));
				voucherRechargeRequest.setCardPinNumber((String)_requestMap.get("PIN"));
				voucherRechargeRequest.setBankCode((String)_requestMap.get("BANK_CODE"));
				voucherRechargeRequest.setCellID(BTSLUtil.NullToString((String)_requestMap.get("CELL_ID")));
				voucherRechargeRequestMsg= new VoucherRechargeRequestMsg(requestHeader,voucherRechargeRequest);
				//System.out.println("Entered the logger!!!!");
				try
				{
					serviceConnection = new VoucherTestConnector(_requestMap);
					_servicePayment =(CardRechargeMgrBindingStub)serviceConnection.getService();	
					//System.out.println("Entered the logger$$$$"+_servicePayment);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if(_servicePayment==null)
				{
					_log.error("sendVoucherRechargeRequestToHuwaiServer: ", "Remote exception from interface.Connection not Established properly.");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
					throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
				}
				else
				{
					startTime=System.currentTimeMillis();  //Start Time of Request.
					_requestMap.put("IN_START_TIME",String.valueOf(startTime));
					voucherRechargeResultMsg= _servicePayment.voucherRecharge(voucherRechargeRequestMsg);
					if(_log.isDebugEnabled())_log.debug("sendVoucherRechargeRequestToHuwaiServer","voucherRechargeResultMsg="+voucherRechargeResultMsg);
				}
				if(voucherRechargeResultMsg==null){
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
					_log.error("sendVoucherRechargeRequestToHuwaiServer: ", "Response Object is not coming from WSDL.");
					throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
				}

				voucherRechargeResult= voucherRechargeResultMsg.getVoucherRechargeResult();
				if(_log.isDebugEnabled())_log.debug("sendVoucherRechargeRequestToHuwaiServer","voucherRechargeResult="+voucherRechargeResult);
				resultHeader = voucherRechargeResultMsg.getResultHeader();
				if(_log.isDebugEnabled())_log.debug("sendVoucherRechargeRequestToHuwaiServer","resultHeader="+resultHeader);
				/*if(voucherRechargeResult==null || resultHeader==null)
				{
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				}*/
				/*System.out.println("voucherRechargeResultMsg1@@@"+voucherRechargeResultMsg);
				System.out.println("voucherRechargeResult1@@@"+voucherRechargeResult);
				System.out.println("resultHeader1@@@"+resultHeader);
				*/endTime=System.currentTimeMillis();//End Time of Request.
				_requestMap.put("IN_END_TIME",String.valueOf(endTime));
				_requestMap.put("IP",URLDecoder.decode(_requestMap.get("URL").toString()));
				String timeOutStr=FileCache.getValue(_interfaceID, "TIME_OUT");
				if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","WAITING FOR IN RESPONSE _socketConnection:" );
				try
				{
					if(!InterfaceUtil.isNullString(timeOutStr))
					{	
						long timeOut=Long.parseLong(timeOutStr);
						if(endTime-startTime>timeOut)
						{
							_log.info("sendVoucherRechargeRequestToHuwaiServer", "TIME_OUT time reaches startTime: "+startTime+" endTime: "+endTime+" timeOut: "+timeOut+" time taken: "+(endTime-startTime));
							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.MAJOR,"sendVoucherRechargeRequestToHuwaiServer[sendVoucherRechargeRequestToHuwaiServer]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID,"Huawei IN is taking more time than the warning threshold. Total Time taken is: "+(endTime-startTime));
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					_log.error("sendVoucherRechargeRequestToHuwaiServer"," Error occoured while reading response message Exception e :"+e.getMessage());
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"sendVoucherRechargeRequestToHuwaiServer[sendVoucherRechargeRequestToHuwaiServer]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID,"Error occoured while reading response message Exception e :"+e.getMessage());
				}
				responseMap.put("VERSION_NUMBER", resultHeader.getVersion());
				responseMap.put("TRANSACTION_ID", resultHeader.getTransactionId());
				responseMap.put("IN_TXN_ID", resultHeader.getSequenceId());
				responseMap.put("RESULT_CODE", resultHeader.getResultCode());
				responseMap.put("RESULT_DESC", resultHeader.getResultDesc());
				responseMap.put("OPERATION_TIME", resultHeader.getOperationTime());
				try
				{	
					responseMap.put("FACE_VALUE", new Long(voucherRechargeResult.getFaceValue()).toString());
					responseMap.put("NEW_BALANCE", new Long(voucherRechargeResult.getNewBalance()).toString());
					responseMap.put("NEW_ACTIVE_STOP", voucherRechargeResult.getNewActiveStop());
					responseMap.put("VALIDITY_PERIOD", new Integer(voucherRechargeResult.getValidityPeriod()).toString());
					responseMap.put("RECHARGE_BONUS", voucherRechargeResult.getRechargeBonus());
//					Not present in Stub   _requestMap.put("VERSION_NUMBER", voucherRechargeResult.getPrmAcctType());    
					responseMap.put("LOAN_AMOUNT", voucherRechargeResult.getLoanAmount());
					responseMap.put("LOAN_POUNDAGE", voucherRechargeResult.getLoanPoundage());
				}
				catch(Exception e)
				{
					_log.error("sendVoucherRechargeRequestToHuwaiServer: ", "NULL response from interface in voucherRechargeResult.");
					e.printStackTrace();
				}
				//System.out.println("bahar"+voucherRechargeResult);
				String resultCode=	(String)responseMap.get("RESULT_CODE");
				responseMap.put("INTERFACE_STATUS",resultCode);
				if(InterfaceUtil.isNullString(resultCode))
				{
					_log.error("sendVoucherRechargeRequestToHuwaiServer: ", "NULL response from interface.");
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				}
				if(HuaweiVoucherRechargeI.SUCESS.equals(resultCode))
				{
					_log.error("sendVoucherRechargeRequestToHuwaiServer: ", "Response is succesfull from the IN");
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.SUCCESS);
				}
				else if(!(InterfaceUtil.isNullString(resultCode)) && !(HuaweiVoucherRechargeI.SUCESS.equals(resultCode)))
				{
					_log.error("sendVoucherRechargeRequestToHuwaiServer: ", "FAIL response from interface. ResponseCode="+responseMap.get("RESULT_CODE"));
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.FAIL);
					throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
				}		
				else
				{
					_log.error("sendVoucherRechargeRequestToHuwaiServer: ", resultCode+" =Invalid response from interface.");
					responseMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				}
			}
			catch (BTSLBaseException be) 
			{
				be.printStackTrace();
				throw be;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"HuaweiVoucherRechargeINHandler[sendVoucherRechargeRequestToHuwaiServer] Credit","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE"),"Remote Exception occured while getting the connection Object.");
				_log.error("sendVoucherRechargeRequestToHuwaiServer","Remote Exception occured while getting the connection Object.");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
			}
			finally
			{
				_servicePayment.clearAttachments();
				_servicePayment.clearHeaders();
				if (_log.isDebugEnabled())_log.debug("sendVoucherRechargeRequestToHuwaiServer","Exited responseMap: " + responseMap);
			}
			return responseMap;
	    }

	/**
	 * Method to send cancel request to IN for any ambiguous transaction.
	 * This method also makes reconciliation log entry. 	
	 * @throws	BTSLBaseException 
	 */
	private void handleCancelTransaction() throws BTSLBaseException
	{
		if (_log.isDebugEnabled())_log.debug("handleCancelTransaction", "Entered.");
		String cancelTxnAllowed = null;
		String cancelTxnStatus = null;
		String reconciliationLogStr = null;
		String cancelCommandStatus=null;
		String cancelNA=null;
		String interfaceStatus=null;
		Log reconLog = null;
		String systemStatusMapping=null;
		//int cancelRetryCount=0;
		try
		{
			_requestMap.put("REMARK1",FileCache.getValue(_interfaceID,"REMARK1"));
			_requestMap.put("REMARK2",FileCache.getValue(_interfaceID,"REMARK2"));
			//get reconciliation log object associated with interface
			reconLog = ReconcialiationLog.getLogObject(_interfaceID);		    
			if (_log.isDebugEnabled())_log.debug("handleCancelTransaction", "reconLog."+reconLog);
			cancelTxnAllowed=(String)_requestMap.get("CANCEL_TXN_ALLOWED");
			//if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
			//into recon log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
			if("N".equals(cancelTxnAllowed))
			{
				//System.out.println("@@@@@@@@@"+cancelTxnAllowed);
				cancelNA=(String)_requestMap.get("CANCEL_NA");//Cancel command status as NA.
				cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap,cancelNA,"CANCEL_COMMAND_STATUS_MAPPING");
				_requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);				
				interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");
				systemStatusMapping=(String)_requestMap.get("SYSTEM_STATUS_MAPPING");				
				cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING

				_requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
				reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
				reconLog.info("",reconciliationLogStr);
				if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
					throw new BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.	??????)    			
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);	
				//added to discard amount field from the message.
				_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error("handleCancelTransaction","Exception be:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("handleCancelTransaction","Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"handleCancelTransaction",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled())_log.debug("handleCancelTransaction", "Exited");
		}
	}
}

