/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroPromoWS;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.inter.claroPromoWS.scheduler.NodeManager;
import com.inter.claroPromoWS.scheduler.NodeScheduler;
import com.inter.claroPromoWS.scheduler.NodeVO;
import com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType;
import com.inter.claroPromoWS.stub.EntregaPromocionRequest;
import com.inter.claroPromoWS.stub.PromocionReq;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author Vipan Kumar
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClaroPromoWSINHandler implements InterfaceHandler{	
	private Log _log = LogFactory.getLog(ClaroPromoWSINHandler.class.getName());
	private ClaroPromoWSRequestFormatter _formatter=null;
	private ClaroPromoWSResponseParser _parser=null;
	private HashMap _requestMap = null;//Contains the request parameter as key and value pair.
	private HashMap _responseMap = null;//Contains the response of the request as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inReconID=null;//Used to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;//Used to store the reference of transaction id.	

	public void validityAdjust (HashMap p_map) throws BTSLBaseException,Exception
	{}

	/**
	 * 
	 */
	public ClaroPromoWSINHandler() {
		_formatter=new ClaroPromoWSRequestFormatter();
		_parser=new ClaroPromoWSResponseParser();	
	}

	/**
	 *@author vipan.kumar
	 * This method is used to validate the subscriber
	 * 
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if(_log.isDebugEnabled()) _log.debug("validate","Exit p_requestMap:"+p_requestMap);

	}

	/**
	 * This method is used to credit the Subscriber
	 */

	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
	}


	public HashMap PromoCredit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("PromoCredit","Entered p_requestMap: " + p_requestMap);
		double systemAmtDouble=0;
		double comMultFactorDouble=0;
		String amountStr=null;
		int retryCountCreditAdjust=0;
		_requestMap = p_requestMap;
		try
		{
			String multFactor =(String)_requestMap.get("MULT_FACTOR");					
			try
			{
				_interfaceID=(String)_requestMap.get("INTERFACE_ID");
				_msisdn=(String)_requestMap.get("MSISDN"); 	
				if(!BTSLUtil.isNullString(_msisdn))
				{
					InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
				}


				_referenceID=(String)_requestMap.get("TRANSACTION_ID");
				_requestMap.put("IN_TXN_ID",_referenceID);


				comMultFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("PROMOTION_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,comMultFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ClaroPromoWSINHandler[PromoCredit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
					amountStr=String.valueOf(Math.round(systemAmtDouble));
					_requestMap.put("INTERFACE_PROMO_ROUND_AMOUNT",amountStr);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("PromoCredit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPromoWSINHandler[PromoCredit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PROMOTION_AMOUNT is not Numeric");
				throw new BTSLBaseException(this,"PromoCredit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}				
			//set promotion transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("promotion_transfer_amount",amountStr);
			//sending the Re-charge request to IN along with re-charge action defined in ComverseTRI interface				
			setInterfaceParameters(ClaroPromoWSI.ACTION_PROMO_CREDIT);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
			
			String[] bundleCodes=null;
			String[] bundleIds=null;
			String[] bundleValidities=null;
			String[] bundleValues=null;
			String[] bundleName=null;
			String[] bundleTypes=null;
			int bonusBundleCount=0;
			bundleCodes=(p_requestMap.get("BONUS_BUNDLE_CODES").toString()).split("\\|");
			bundleIds=(p_requestMap.get("BONUS_BUNDLE_IDS").toString()).split("\\|");
			bundleValidities=(p_requestMap.get("BONUS_BUNDLE_VALIDITIES").toString()).split("\\|");
			bundleValues=(p_requestMap.get("BONUS_BUNDLE_VALUES").toString()).split("\\|");
			bundleName=(p_requestMap.get("BONUS_BUNDLE_NAMES").toString()).split("\\|");
			bundleTypes=(p_requestMap.get("BONUS_BUNDLE_TYPES").toString()).split("\\|");

			String amtMultFactor=FileCache.getValue(_interfaceID,"AMT_MULT_FACTOR");
			String unitMultFactor=FileCache.getValue(_interfaceID,"UNIT_MULT_FACTOR");
			if(InterfaceUtil.isNullString(amtMultFactor) || InterfaceUtil.isNullString(unitMultFactor))
			{
				_log.error("setInterfaceParameters","nombreAplicacion  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPromoWSINHandler[PromoCredit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Value of AMT_MULT_FACTOR or UNIT_MULT_FACTOR is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			bonusBundleCount=bundleIds.length;
			
			PromocionReq listPromocionesType=null;
			PromocionReq[] listPromocionesType2=null;
			listPromocionesType2=new PromocionReq[bonusBundleCount];
			for(int i=0; i<bonusBundleCount;i++)
			{
				
				listPromocionesType=new PromocionReq();
				String bundleValue=bundleValues[i];
				String bundleValidity=bundleValidities[i];
				double bundleValueDbl=Double.parseDouble(bundleValue);
				double inbundleValueDbl=0;
				if(bundleValueDbl>0 || Long.parseLong(bundleValidity)>0)
				{
					//Calculate the amount by the help of multiplication factor.
					if("AMT".equals(bundleTypes[i]))
						inbundleValueDbl=InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl,Double.parseDouble(amtMultFactor));
					else if("UNIT".equals(bundleTypes[i]))
						inbundleValueDbl=InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl,Double.parseDouble(unitMultFactor));

					String tipoPromocion = bundleName[i];
					if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","tipoPromocion:"+tipoPromocion);
					if(InterfaceUtil.isNullString(tipoPromocion))
					{
						_log.error("setInterfaceParameters","tipoPromocion  is not defined ");
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "tipoPromocion  is not defined ");
						throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
					}
					
					tipoPromocion = tipoPromocion.trim();
					listPromocionesType.setCantidad(Long.toString((long)inbundleValueDbl));
					listPromocionesType.setTipoPromocion(tipoPromocion);
					listPromocionesType2[i]=listPromocionesType;
				}				
			}
			_requestMap.put("ListPromocionesType",listPromocionesType2);	
			sendRequestToIN(ClaroPromoWSI.ACTION_PROMO_CREDIT);	   
			
			_requestMap.put("PROMO_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("PROMO_INTERFACE_STATUS", _responseMap.get("INTERFACE_STATUS"));
			_requestMap.put("PROMO_ENQUIRY", "N");
		} 
		catch (BTSLBaseException be)
		{	        
			_log.error("PromoCredit","BTSLBaseException be:"+be.getMessage());    		   		
		
			if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
				throw be;
			try
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
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Claro[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}

			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("PromoCredit", "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroPromoWSINHandler[PromoCredit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While promotion PromoCredit, get the Exception e:"+e.getMessage());
			_requestMap.put("PROMO_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			throw new BTSLBaseException(this,"PromoCredit",InterfaceErrorCodesI.AMBIGOUS);
		}
		finally
		{
			_requestMap.remove("ListPromocionesType");
			if (_log.isDebugEnabled()) _log.debug("PromoCredit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled()) TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPromoWSINHandler[PromoCredit]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");	             
		}
		return _requestMap;
	}


	/**
	 * 
	 */ 
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("creditAdjust","Entered p_requestMap: " + p_requestMap);
	}

	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("debitAdjust","Entered p_requestMap: " + p_requestMap);
	}

	/**
	 * This method used to send the request to the IN
	 * @param p_action
	 * @throws BTSLBaseException
	 */
	private void sendRequestToIN(int p_action) throws BTSLBaseException
	{
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+p_action+" __msisdn="+_msisdn);

		String actionLevel="";
		switch(p_action)
		{
		case ClaroPromoWSI.ACTION_PROMO_CREDIT:
		{
			actionLevel="ACTION_PROMO_CREDIT";
			break;
		}

		} 
		if(!BTSLUtil.isNullString(_msisdn))
		{
			InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);
		}
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+actionLevel+" __msisdn="+_msisdn);
		long startTime=0,endTime=0,sleepTime,warnTime=0;
		EbsEntregaPromocionPortType clientStub=null;

		NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;
		int readTimeOut=0;
		ClaroPromoWSConnectionManager serviceConnection =null;
		try
		{
			//Get the start time when the request is send to IN.
			nodeScheduler = NodeManager.getScheduler(_interfaceID);
			//Get the retry number from the object that is used to retry the getNode in case connection is failed.
			retryNumber = nodeScheduler.getRetryNum();
			//check if NodeScheduler is null throw exception.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
			if(nodeScheduler==null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);
			for(int loop=1;loop<=retryNumber;loop++)
			{
				try
				{
					nodeVO = nodeScheduler.getNodeVO(_inReconID);
					TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"ClaroPromoWSINHandler[sendRequestToIN]",PretupsI.TXN_LOG_REQTYPE_REQ,"Node information NodeVO:"+nodeVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
					_requestMap.put("IN_URL", nodeVO.getUrl());
					//Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
					if(nodeVO==null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );
					warnTime=nodeVO.getWarnTime();
					readTimeOut=nodeVO.getReadTimeOut();
					//Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
					serviceConnection = new ClaroPromoWSConnectionManager(nodeVO,_interfaceID);
					//break the loop on getting the successfull connection for the node;		            
					clientStub =serviceConnection.getService();			
					if(clientStub==null)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object");
						_log.error("sendRequestToIN","Unable to get Client Object");
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
					}		            
					try
					{
						try
						{
							startTime=System.currentTimeMillis();
							switch(p_action)
							{	
							case ClaroPromoWSI.ACTION_PROMO_CREDIT: 
							{
								Object object =_formatter.generateRequest(ClaroPromoWSI.ACTION_PROMO_CREDIT,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.entregarPromocion((EntregaPromocionRequest)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								break;	
							}

							}
						}				
						catch(java.rmi.RemoteException re)
						{
							re.printStackTrace();
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RemoteException Error Message:"+re.getMessage());
							String respCode=null;
							//parse error code 
							String requestStr=re.getMessage();
							int index=requestStr.indexOf("<ErrorCode>");
							if(index ==-1)
							{
								if(re.getMessage().contains("java.net.ConnectException"))
								{
									//In case of connection failure 
									//1.Decrement the connection counter
									//2.set the Node as blocked 
									//3.set the blocked time
									//4.Handle the event with level INFO, show the message that Node is blocked for some time (expiry time).
									//Continue the retry loop till success;
									//Check if the max retry attempt is reached raise exception with error code.
									_log.error("sendRequestToIN","RMI java.net.ConnectException while creating connection re::"+re.getMessage());
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPromoWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroDataWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

									_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
									nodeVO.incrementBarredCount();
									nodeVO.setBlocked(true);
									nodeVO.setBlokedAt(System.currentTimeMillis());
									if(loop==retryNumber)
									{
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPromoWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
										throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
									}
									continue;
								}
								else if (re.getMessage().contains("java.net.SocketTimeoutException"))
								{
									re.printStackTrace();
									if(re.getMessage().contains("connect"))
									{
										//In case of connection failure 
										//1.Decrement the connection counter
										//2.set the Node as blocked 
										//3.set the blocked time
										//4.Handle the event with level INFO, show the message that Node is blocked for some time (expiry time).
										//Continue the retry loop till success;
										//Check if the max retry attempt is reached raise exception with error code.
										_log.error("sendRequestToIN","RMI java.net.ConnectException while creating connection re::"+re.getMessage());
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPromoWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for ClaroDataWS Soap Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

										_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
										nodeVO.incrementBarredCount();
										nodeVO.setBlocked(true);
										nodeVO.setBlokedAt(System.currentTimeMillis());

										if(loop==retryNumber)
										{
											EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"ClaroPromoWSINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
											throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										}
										continue;									
									}									
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketTimeoutException Message:"+re.getMessage());
									_log.error("sendRequestToIN","RMI java.net.SocketTimeoutException Error Message :"+re.getMessage());	
									 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								else if(re.getMessage().contains("java.net.SocketException"))
								{
									re.printStackTrace();
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"RMI java.net.SocketException Message:"+re.getMessage());
									_log.error("sendRequestToIN","RMI java.net.SocketException Error Message :"+re.getMessage());			    	
									 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								}
								else
									throw new Exception(re);
							}							  
							respCode=requestStr.substring(index+"<ErrorCode>".length(),requestStr.indexOf("</ErrorCode>",index));

							index=requestStr.indexOf("<ErrorDescription>");
							String respCodeDesc=requestStr.substring(index+"<ErrorDescription>".length(),requestStr.indexOf("</ErrorDescription>",index));
							_log.error("sendRequestToIN","Error Message respCode="+respCode+"  respCodeDesc:"+respCodeDesc);
							_requestMap.put("INTERFACE_STATUS",respCode);
							_requestMap.put("INTERFACE_DESC",respCodeDesc);								
							_log.error("sendRequestToIN","Error Message respCode="+respCode+"  respCodeDesc:"+respCodeDesc);
							throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

						}
						catch(SocketTimeoutException se)
						{
							se.printStackTrace();
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"SocketTimeoutException Error Message:"+se.getMessage());
							_log.error("sendRequestToIN","SocketTimeoutException Error Message :"+se.getMessage());			    	
							 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);  
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Exception Error Message:"+e.getMessage());
							_log.error("sendRequestToIN","Exception Error Message :"+e.getMessage());
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						}
						finally
						{
							endTime=System.currentTimeMillis();
							nodeVO.resetBarredCount();	
						}

					}
					catch(BTSLBaseException be)
					{
						throw be;
					}
					catch(Exception e)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Error Message:"+e.getMessage());
						_log.error("sendRequestToIN","Error Message :"+e.getMessage());
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
					}
					finally
					{
						if(endTime==0) endTime=System.currentTimeMillis();
						_requestMap.put("IN_END_TIME",String.valueOf(endTime));			        
						_log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime);
					}

					if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Connection of _interfaceID ["+_interfaceID+"] for the Node Number ["+nodeVO.getNodeNumber()+"] created after the attempt number(loop)::"+loop);
					break;
				}
				catch(BTSLBaseException be)
				{
					_log.error("sendRequestToIN","BTSLBaseException be::"+be.getMessage());
					throw be;//Confirm should we come out of loop or do another retry
				}//end of catch-BTSLBaseException
				catch(Exception e)
				{
					_log.error("sendRequestToIN","Exception be::"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}//end of catch-Exception            
			}            
			_responseMap=_parser.parseResponse(p_action,_requestMap);
			//put value of response
			TransactionLog.log( _interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response Map: "+_responseMap ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
			//Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled
			if(endTime-startTime>=warnTime)
			{
				_log.info("sendRequestToIN", "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"ClaroPromoWSINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"ClaroDataWS IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
			}
			String status=(String)_responseMap.get("INTERFACE_STATUS");
			_requestMap.put("INTERFACE_STATUS",status);

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
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ClaroAPIINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			_requestMap.remove("RESPONSE_OBJECT");
			clientStub=null;
			serviceConnection=null;
			if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Exiting p_action="+p_action);
		}//end of finally
	}

	/**
	 * @author vipan.kumar
	 * @param p_action
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public void setInterfaceParameters(int p_action) throws BTSLBaseException,Exception 
	{
		if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered Action ="+p_action);
		try
		{
			 String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
             if(InterfaceUtil.isNullString(cancelTxnAllowed))
             {
                 _log.error("setInterfaceParameters","Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                     EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                     throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
             }
             _requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());

             String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
             if(InterfaceUtil.isNullString(systemStatusMappingCr))
             {
                 _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                     EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                     throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
             }
             _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());

             
             String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
             if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
             {
                 _log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                     EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                     throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
             }
             _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());


             String cancelNA = FileCache.getValue(_interfaceID,"CANCEL_NA");
             if(InterfaceUtil.isNullString(cancelNA))
             {
                 _log.error("setInterfaceParameters","Value of CANCEL_NA is not defined in the INFile");
                     EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWSINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                     throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
             }
             _requestMap.put("CANCEL_NA",cancelNA.trim());


			InetAddress addr = InetAddress.getLocalHost();

			String ipAddress = addr.getHostAddress();
			_requestMap.put("IP",ipAddress);

			String nombreAplicacion =  FileCache.getValue(_interfaceID,"NombreAplicacion");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","nombreAplicacion:"+nombreAplicacion);
			if(InterfaceUtil.isNullString(nombreAplicacion))
			{
				_log.error("setInterfaceParameters","nombreAplicacion  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "nombreAplicacion  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			nombreAplicacion = nombreAplicacion.trim();
			_requestMap.put("NOMBREAPLICACION",nombreAplicacion);	

			String usuarioAplicacion =  FileCache.getValue(_interfaceID,"UsuarioAplicacion");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","usuarioAplicacion:"+usuarioAplicacion);
			if(InterfaceUtil.isNullString(usuarioAplicacion))
			{
				_log.error("setInterfaceParameters","usuarioAplicacion  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "usuarioAplicacion  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			usuarioAplicacion = usuarioAplicacion.trim();
			_requestMap.put("USUASRIOAPLICACION",usuarioAplicacion);	

		}//end of try block
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			_log.error("setInterfaceParameters",e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), e.getMessage());
			throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			if (_log.isDebugEnabled())_log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
		}//end of finally
	}


	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat ("ss");
	private static int  _txnCounter = 1;
	private static int  _prevSec=0;
	public int IN_TRANSACTION_ID_PAD_LENGTH=2;
	

	public synchronized String getINReconID() throws BTSLBaseException
	{
		//This method will be used when we have transID based on database sequence.
		String inTransactionID="";
		try
		{
			String secToCompare=null;
			Date mydate = null;

			mydate = new Date();

			secToCompare = _sdfCompare.format(mydate);
			int currentSec=Integer.parseInt(secToCompare);  		

			if(currentSec !=_prevSec)
			{
				_txnCounter=1;
				_prevSec=currentSec;
			}
			else if(_txnCounter >= 99)
			{
				_txnCounter=1;	  			 
			}
			else
			{
				_txnCounter++;  			 
			}
			if(_txnCounter==0)
				throw new BTSLBaseException("this","getINReconID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);


			inTransactionID=BTSLUtil.padZeroesToLeft(String.valueOf(Constants.getProperty("INSTANCE_ID")),IN_TRANSACTION_ID_PAD_LENGTH)+currentTimeFormatStringTillSec(mydate)+BTSLUtil.padZeroesToLeft(String.valueOf(_txnCounter),IN_TRANSACTION_ID_PAD_LENGTH);
		}	
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			_requestMap.put("IN_RECON_ID",inTransactionID);
			_requestMap.put("IN_TXN_ID",inTransactionID);
			return inTransactionID;
		}
	}
	
	public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat ("hhmmss");
		String dateString = sdf.format(p_date);
		return dateString;
	}

	 /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * @throws      BTSLBaseException
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
                                    throw new BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.   ??????)
                            _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
                    }
            }
            catch(BTSLBaseException be)
            {
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

