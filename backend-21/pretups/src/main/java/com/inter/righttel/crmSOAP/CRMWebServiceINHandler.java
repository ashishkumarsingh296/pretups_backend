/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.righttel.crmSOAP;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.inter.righttel.crmSOAP.scheduler.NodeManager;
import com.inter.righttel.crmSOAP.scheduler.NodeScheduler;
import com.inter.righttel.crmSOAP.scheduler.NodeVO;
import com.inter.righttel.crmSOAP.stub.InitTopupRequestType;
import com.inter.righttel.crmSOAP.stub.QueryTopupRequestType;
import com.inter.righttel.crmSOAP.stub.TopupRequestType;
import com.inter.righttel.crmSOAP.stub.TopupServicePortType;

/**
 * @author Vipan Kumar
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CRMWebServiceINHandler implements InterfaceHandler{	
	private Log _log = LogFactory.getLog(CRMWebServiceINHandler.class.getName());
	private CRMWebServiceRequestFormatter _formatter=null;
	private CRMWebServiceResponseParser _parser=null;
	private HashMap _requestMap = null;//Contains the request parameter as key and value pair.
	private HashMap _responseMap = null;//Contains the response of the request as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inReconID=null;//Used to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;//Used to store the reference of transaction id.	

	/**
	 * 
	 */
	public CRMWebServiceINHandler() {
		_formatter=new CRMWebServiceRequestFormatter();
		_parser=new CRMWebServiceResponseParser();	
	}

	/**
	 * This method is used to validate the subscriber
	 * 
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		Double systemAmtDouble=0.0;
    	double multFactorDouble=0;
		String amountStr="";
		String promoAmountStr="";
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.

			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=_referenceID.replace(".", "");
			_requestMap.put("IN_TXN_ID",_inReconID);

			if("N".equals(validateRequired))
			{
				//Setting default Response; 
				_responseMap=_parser.parseResponse(CRMWebServiceI.ACTION_ACCOUNT_DETAILS, _requestMap);
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("INTERFACE_PREV_BALANCE",_responseMap.get("INTERFACE_PREV_BALANCE"));
				_requestMap.put("SERVICE_CLASS",_responseMap.get("SERVICE_CLASS"));
				_requestMap.put("OLD_EXPIRY_DATE",_responseMap.get("OLD_EXPIRY_DATE"));
				_requestMap.put("ACCOUNT_STATUS",_responseMap.get("ACCOUNT_STATUS"));
				return ;
			}

			setInterfaceParameters(CRMWebServiceI.ACTION_ACCOUNT_DETAILS);

			//sending the AccountInfo request to IN along with validate action defined in interface
			sendRequestToIN(CRMWebServiceI.ACTION_ACCOUNT_DETAILS);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);

			String expdate="";
			if(_responseMap.get("OLD_EXPIRY_DATE")!=null)
			 expdate=BTSLUtil.getDateTimeStringFromDate(((Calendar)_responseMap.get("OLD_EXPIRY_DATE")).getTime(), "ddMMyyyy");
			
			_requestMap.put("OLD_EXPIRY_DATE",BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyyyy"));
			
			
			_requestMap.put("AON",BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyyyy"));
			_requestMap.put("INTERFACE_PREV_BALANCE","10000000");
			_requestMap.put("ACCOUNT_STATUS",(String)_responseMap.get("ACCOUNT_STATUS"));
			_requestMap.put("SERVICE_CLASS",(String)_responseMap.get("SERVICE_CLASS"));
			
			_requestMap.put("INTERFACE_TXN_ID",(String)_responseMap.get("INTERFACE_TXN_ID"));
			
		}
		catch (BTSLBaseException be)
		{
			if(be.getMessage()!=null && be.getMessage().contains(InterfaceErrorCodesI.AMBIGOUS))
			{
				_log.error("validate","BTSLBaseException be="+be.getMessage());
	            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			}
			else if(be.getMessage()!=null)
			{
				_log.error("validate","BTSLBaseException be="+be.getMessage());
	            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Got Error response from IN so throwing Fail in validation exception");
				throw be;
			}
			else
			{
			_log.error("validate","BTSLBaseException be="+be.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
			_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.VALIDATION_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[validate]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}

	/**
	 * This method is used to credit the Subscriber
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
		if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
		//int retryCountCredit=0;
		_requestMap = p_requestMap;
		try
		{
			// For LOGS 
			_requestMap.put("IN_START_TIME","0");
			_requestMap.put("IN_END_TIME","0");
			_requestMap.put("IN_RECHARGE_TIME","0");
			_requestMap.put("IN_CREDIT_VAL_TIME","0");

			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");		
			_inReconID=_referenceID.replace(".", "");
			_requestMap.put("IN_TXN_ID",_referenceID);

			_msisdn=(String)_requestMap.get("MSISDN");
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error("credit","MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters(CRMWebServiceI.ACTION_RECHARGE_CREDIT);

			try
			{
				multFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CRMWebServiceINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
					amountStr=String.valueOf(Math.round(systemAmtDouble));
					_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("credit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			
			sendRequestToIN(CRMWebServiceI.ACTION_RECHARGE_CREDIT);            
			//set IN_RECHARGE_STATUS as Success in request map
			_requestMap.put("RECHARGE_ENQUIRY", "N"); 
			_requestMap.put("IN_RECHARGE_TIME",String.valueOf(((Long.valueOf((String)_requestMap.get("IN_END_TIME"))).longValue())-((Long.valueOf((String)_requestMap.get("IN_START_TIME"))).longValue())));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS); 
			_requestMap.put("INTERFACE_PRE_BALANCE", _responseMap.get("INTERFACE_PRE_BALANCE"));
			_requestMap.put("INTERFACE_STATUS", _responseMap.get("INTERFACE_STATUS"));
			_requestMap.put("INTERFACE_POST_BALANCE", _responseMap.get("INTERFACE_POST_BALANCE"));
			_requestMap.put("NEW_EXPIRY_DATE", _responseMap.get("NEW_EXPIRY_DATE"));
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");

		} 
		catch (BTSLBaseException be)
		{
			
			try
			{
			if(be.getMessage().contains(InterfaceErrorCodesI.AMBIGOUS))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit, get the Base Exception be:"+be.getMessage()+"Going for Query Topup");
				creditAdjust(p_requestMap);
			}
			else
			{
				throw be;
			}
			}
			catch (BTSLBaseException be1)
			{
				throw be1;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[credit]","credit complete."," _requestMap string:"+_requestMap.toString(),"","");
		}

		if (_log.isDebugEnabled())_log.debug("credit","Exiting _interfaceID"+_interfaceID+"_referenceID"+_referenceID+"_msisdn"+_msisdn+"requestMap="+_requestMap.toString()); 
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
		case CRMWebServiceI.ACTION_ACCOUNT_DETAILS:
		{
			actionLevel="ACTION_ACCOUNT_DETAILS";
			break;
		}
		case CRMWebServiceI.ACTION_RECHARGE_CREDIT:
		{
			actionLevel="ACTION_RECHARGE_CREDIT";
			break;
		}
		case CRMWebServiceI.ACTION_QUERY_TOPUP:
		{
			actionLevel="ACTION_QUERY_TOPUP";
			break;
		}

		} 
		if(!BTSLUtil.isNullString(_msisdn))
		{
			_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn);
		}
		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+actionLevel+" __msisdn="+_msisdn);
		long startTime=0,endTime=0,warnTime=0;
		TopupServicePortType clientStub=null;

		NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;
		//int readTimeOut=0;
		CRMWebServiceConnectionManager serviceConnection =null;
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
					TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[sendRequestToIN]",PretupsI.TXN_LOG_REQTYPE_REQ,"Node information NodeVO:"+nodeVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+actionLevel);
					//_requestMap.put("IP", nodeVO.getUrl());
					_requestMap.put("IP",nodeVO.getUrl().substring(7, nodeVO.getUrl().lastIndexOf(":")));
					//Check if Node is foud or not.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
					if(nodeVO==null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );
					warnTime=nodeVO.getWarnTime();
					//readTimeOut=nodeVO.getConnectionTimeOut();
					//Confirm for the service name servlet for the url consturction whether URL will be specified in INFile or IP,PORT and ServletName.
					serviceConnection = new CRMWebServiceConnectionManager(nodeVO,_interfaceID);
					//break the loop on getting the successfull connection for the node;		            
					clientStub =serviceConnection.getService();			
					if(clientStub==null)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Unable to get Client Object");
						_log.error("sendRequestToIN","Unable to get Client Object");
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
					}		            
					try
					{
						try
						{
							startTime=System.currentTimeMillis();
							_requestMap.put("IN_START_TIME",String.valueOf(startTime));
							switch(p_action)
							{	
							case CRMWebServiceI.ACTION_ACCOUNT_DETAILS: 
							{
								Object object =_formatter.generateRequest(CRMWebServiceI.ACTION_ACCOUNT_DETAILS,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.initTopup((InitTopupRequestType)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								break;
							}
							case CRMWebServiceI.ACTION_RECHARGE_CREDIT: 
							{
								Object object =_formatter.generateRequest(CRMWebServiceI.ACTION_RECHARGE_CREDIT,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.topup((TopupRequestType)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								if(FileCache.getValue(_interfaceID,"QUERY_TOPUP_REQ")!=null && FileCache.getValue(_interfaceID,"QUERY_TOPUP_REQ").equalsIgnoreCase("Y"))
								{
									throw new SocketTimeoutException(InterfaceErrorCodesI.AMBIGOUS);
								}
								break;	
							}
							case CRMWebServiceI.ACTION_QUERY_TOPUP: 
							{
								Object object =_formatter.generateRequest(CRMWebServiceI.ACTION_QUERY_TOPUP,_requestMap);
								Object responseObj=null;
								responseObj=clientStub.queryTopup((QueryTopupRequestType)object);
								_requestMap.put("RESPONSE_OBJECT",responseObj);
								if(FileCache.getValue(_interfaceID,"QUERY_TOPUP_REQ")!=null && FileCache.getValue(_interfaceID,"QUERY_TOPUP_REQ").equalsIgnoreCase("Y"))
								{
									throw new SocketTimeoutException(InterfaceErrorCodesI.AMBIGOUS);
								}
								break;	
							}
							}
						}				
						catch(java.rmi.RemoteException re)
						{
							re.printStackTrace();
				            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read timeout from IN.  so throwing Fail in validation exception and Node Number=["+nodeVO.getNodeNumber()+"] and NODE URL=["+nodeVO.getUrl()+"]");
							_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);	
						}
						catch(SocketTimeoutException se)
						{
							se.printStackTrace();
				            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "Read timeout from IN.  so throwing Fail in validation exception and Node Number=["+nodeVO.getNodeNumber()+"] and NODE URL=["+nodeVO.getUrl()+"]");
							_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Exception Error Message:"+e.getMessage());
							_log.error("sendRequestToIN","Exception Error Message :"+e.getMessage());
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						}
						finally
						{
							endTime=System.currentTimeMillis();
						}

					}
					catch(BTSLBaseException be)
					{
						throw be;
					}
					catch(Exception e)
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel,"Error Message:"+e.getMessage());
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
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+" MSISDN = "+_msisdn,"CCWS IP= "+nodeVO.getUrl(),"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+actionLevel," IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
			}
			String status=String.valueOf(_requestMap.get("INTERFACE_STATUS"));
			_requestMap.put("INTERFACE_STATUS",status);

		}
		catch(BTSLBaseException be)
		{
			_log.error("sendRequestToIN","BTSLBaseException be = "+be.getMessage());
			String status=String.valueOf(_requestMap.get("INTERFACE_STATUS"));
			_requestMap.put("INTERFACE_STATUS",status);
			throw be;
		}//end of BTSLBaseException
		catch(Exception e)
		{
			e.printStackTrace();	    
			_log.error("sendRequestToIN","Exception="+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CRMWebServiceINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
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

	public void setInterfaceParameters(int p_action) throws BTSLBaseException,Exception 
	{
		if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered Action ="+p_action);
		try
		{        	
			String distID = FileCache.getValue(_interfaceID,"DISTRIBUTOR_ID");
			if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","password:"+distID);
			if(InterfaceUtil.isNullString(distID))
			{
				_log.error("setInterfaceParameters","password  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CRMWebServiceINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "password  is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			distID= distID.trim();
			_requestMap.put("DISTRIBUTOR_ID",distID);
		}//end of try block
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			_log.error("setInterfaceParameters","Exception e="+e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if (_log.isDebugEnabled())_log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
		}//end of finally
	}

	public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat ("hhmmss");
		String dateString = sdf.format(p_date);
		return dateString;
	}
	public void debitAdjust(HashMap<String, String> p_map)
			throws BTSLBaseException, Exception {
		// TODO Auto-generated method stub
		
	}

	public void validityAdjust(HashMap<String, String> p_map)
			throws BTSLBaseException, Exception {
		// TODO Auto-generated method stub
		
	}


	public void creditAdjust(HashMap<String, String> p_requestMap)
			throws BTSLBaseException, Exception {
		if(_log.isDebugEnabled()) _log.debug("creditAdjust","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		Double systemAmtDouble=0.0;
    	double multFactorDouble=0;
		String amountStr="";
		String promoAmountStr="";
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN"); 	
			if(!BTSLUtil.isNullString(_msisdn))
			{
				_msisdn=InterfaceUtil.getFilterMSISDN(_interfaceID,_msisdn); 				
			}
			_requestMap.put("FILTER_MSISDN",_msisdn);

			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.

			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_inReconID=_referenceID.replace(".", "");
			_requestMap.put("IN_TXN_ID",_inReconID);

			setInterfaceParameters(CRMWebServiceI.ACTION_QUERY_TOPUP);

			//sending the AccountInfo request to IN along with validate action defined in interface
			sendRequestToIN(CRMWebServiceI.ACTION_QUERY_TOPUP);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
		}
		catch (BTSLBaseException be)
		{
			if(be.getMessage()!=null && be.getMessage().contains(InterfaceErrorCodesI.AMBIGOUS))
			{
				_log.error("validate","BTSLBaseException be="+be.getMessage());
	            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
			}
			else if(be.getMessage()!=null)
			{
				_log.error("validate","BTSLBaseException be="+be.getMessage());
	            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Got Error response from IN so throwing Fail in validation exception");
	            _requestMap.put("INTERFACE_STATUS",be.getMessage());	
	            throw be;
			}
			else
			{
			_log.error("validate","BTSLBaseException be="+be.getMessage());
            EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+"  ",  "Read timeout from IN.  so throwing Fail in validation exception");
			_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);	       
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CRMWebServiceINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber, get Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);        	
			if(TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CRMWebServiceINHandler[validate]","Exiting "," _requestMap string:"+_requestMap.toString(),"","");
		}
	}
}

