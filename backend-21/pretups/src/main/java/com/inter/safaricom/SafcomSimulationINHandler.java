/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.safaricom;

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
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;
import com.inter.safaricom.safaricomstub.Mediator_Stub;
import com.inter.safaricom.safaricomstub.Request;
import com.inter.safaricom.safaricomstub.Response;
import com.inter.safaricom.scheduler.NodeManager;
import com.inter.safaricom.scheduler.NodeScheduler;
import com.inter.safaricom.scheduler.NodeVO;




/**
 * @author dhiraj.tiwari
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SafcomSimulationINHandler implements InterfaceHandler{	
	private Log _log = LogFactory.getLog(SafcomSimulationINHandler.class.getName());
	private SafcomRequestFormatter _formatter=null;
    private HashMap _requestMap = null;//Contains the request parameter as key and value pair.
    private HashMap _responseMap = null;//Contains the response of the request as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inTXNID=null;//Used to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;//Used to store the reference of transaction id.
	private String _interfaceLiveStatus=null;
	private InterfaceCloserVO _interfaceCloserVO= null;
	private InterfaceCloser _interfaceCloser=null;
	private boolean _isSameRequest=false;
	private String _userType=null;
	private String _interfaceClosureSupport=null;
	

	/**
	 * 
	 */
	public SafcomSimulationINHandler() {
		_formatter=new SafcomRequestFormatter();
	}
	
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
        if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);
        _requestMap = p_requestMap;
        try
        {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_userType=(String)_requestMap.get("USER_TYPE");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	        	
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
 			_msisdn=(String)_requestMap.get("MSISDN"); 			
			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.
			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
			//String validateRequired="Y";
 			if("N".equals(validateRequired))
			{
			    _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			    return ;
			}
			_inTXNID=_formatter.getINTransactionID(_requestMap);			
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
			    _log.error("validate","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor=multFactor.trim();
            //Set the interface parameters into requestMap
			setInterfaceParameters(SafcomI.ACTION_ACCOUNT_INFO);
			//generate Request Object
			Request requestObj = _formatter.generateRequestObject(SafcomI.ACTION_ACCOUNT_INFO,_requestMap);
			//sending the AccountInfo request to IN along with validate action defined in Huawei84I interface
            sendRequestToIN(SafcomI.ACTION_ACCOUNT_INFO,requestObj);
			//set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
            //get value of BALANCE from response map (BALANCE was set in response map in sendRequestToIN method.)
			String amountStr=(String)_responseMap.get("RESP_BALANCE");
            try
			{
            	amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
				_requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
			}
            catch(Exception e)
			{
            	e.printStackTrace();
            	_log.error("validate","Exception e:"+e.getMessage());
            	EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:"+e.getMessage());
            	throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
			}
        }
        catch (BTSLBaseException be)
        {
        	_log.error("validate","BTSLBaseException be="+be.getMessage());
        	if("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
        		_interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);      	
    		throw be; 	   	
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("validate","Exception e:"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
        	if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);
        }
    }
	
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
    	if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
    	double systemAmtDouble=0;
    	double huaweiMultFactorDouble=0;
    	String amountStr=null;
        _requestMap = p_requestMap;
        try
         {
        	if (_log.isDebugEnabled())_log.debug("credit","Entered _interfaceID: " + _interfaceID);
        	
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	if (_log.isDebugEnabled())_log.debug("credit","Entered _interfaceClosureSupport: " + _interfaceClosureSupport);
        	
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
			_inTXNID=_formatter.getINTransactionID(_requestMap);
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
 			_msisdn=(String)_requestMap.get("MSISDN");
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("credit","multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
			    _log.error("credit","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);
						
			//Set the interface parameters into requestMap
			setInterfaceParameters(SafcomI.ACTION_RECHARGE_CREDIT);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
			try
			{
			    huaweiMultFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,huaweiMultFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				if(_log.isDebugEnabled()) _log.debug("credit","From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
				    roundFlag="Y";
				    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "SafcomSimulationINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("credit","transfer_amount:"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			Request requestObj = _formatter.generateRequestObject(SafcomI.ACTION_RECHARGE_CREDIT,_requestMap);
			 //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
             sendRequestToIN(SafcomI.ACTION_RECHARGE_CREDIT,requestObj);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
         } 
        catch (BTSLBaseException be)
        {
        	p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
    		_log.error("credit","BTSLBaseException be:"+be.getMessage());    		   		
    		if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
    			throw be;
    		try
			{ 
    			if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
    				_interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
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
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				 throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
         catch (Exception e)
         {
             e.printStackTrace();
             _log.error("credit", "Exception e:" + e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
             throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
         }
    }
	
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
	
	}
	
	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
	    
    	if (_log.isDebugEnabled())_log.debug("debitAdjust","Entered p_requestMap: " + p_requestMap);
    	double systemAmtDouble=0;
    	double huaweiMultFactorDouble=0;
    	String amountStr=null;
        _requestMap = p_requestMap;
        try
         {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
			_inTXNID=_formatter.getINTransactionID(_requestMap);
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
 			_msisdn=(String)_requestMap.get("MSISDN");
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("debitAdjust","multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
			    _log.error("debitAdjust","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);
						
			//Set the interface parameters into requestMap
			setInterfaceParameters(SafcomI.ACTION_IMMEDIATE_DEBIT);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			try
			{
			    huaweiMultFactorDouble=Double.parseDouble(multFactor);
				double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,huaweiMultFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				if(_log.isDebugEnabled()) _log.debug("debitAdjust","From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
				    roundFlag="Y";
				    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "SafcomSimulationINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
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
			    _log.error("debitAdjust","Exception e:"+e.getMessage());
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
			    throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("debitAdjust","transfer_amount:"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			Request requestObj = _formatter.generateRequestObject(SafcomI.ACTION_IMMEDIATE_DEBIT,_requestMap);
			 //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
             sendRequestToIN(SafcomI.ACTION_IMMEDIATE_DEBIT,requestObj);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
         } 
        catch (BTSLBaseException be)
        {
        	p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
    		_log.error("debitAdjust","BTSLBaseException be:"+be.getMessage());    		   		
    		if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
    			throw be;
    		try
			{ 
    			if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
    				_interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
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
				 _log.error("debitAdjust","Exception e:"+e.getMessage());
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[debitAdjust]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
				 throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
         catch (Exception e)
         {
             e.printStackTrace();
             _log.error("debitAdjust", "Exception e:" + e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:"+e.getMessage());
             throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (_log.isDebugEnabled()) _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
         }
    
	
	}
	
	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{
	
	}
	
	private void sendRequestToIN(int p_action,Request p_requestObj) throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+p_action);
		//Put the request string, action, interface id, network code in the Transaction log.
		TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"Request Parameters : SAFCOM_ACNT_ID="+(String)_requestMap.get("SAFCOM_ACNT_ID")+ " ,transfer_amount="+(String)_requestMap.get("transfer_amount")+" ,MSISDN="+(String)_requestMap.get("MSISDN")+" ,SENDER_MSISDN="+(String)_requestMap.get("SENDER_MSISDN")+(String)_requestMap.get("TRANSACTION_ID")+", SAFCOM_PASSWD="+(String)_requestMap.get("SAFCOM_PASSWD")+", SAFCOM_TERM_ID="+(String)_requestMap.get("SAFCOM_TERM_ID")+",IN_TXN_ID="+(String)_requestMap.get("IN_TXN_ID")+", SAFCOM_USER_ID="+(String)_requestMap.get("SAFCOM_USER_ID"),PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+p_action );
		long startTime=0,endTime=0,sleepTime,warnTime=0;
        Response responseObj=null;
        Mediator_Stub clientStub=null;
    	NodeScheduler nodeScheduler=null;
		NodeVO nodeVO=null;
		int retryNumber=0;
		int readTimeOut=0;
		try
        {
			
			startTime=System.currentTimeMillis();
_requestMap.put("IN_START_TIME",String.valueOf(startTime));
							
							
        	//Get the start time when the request is send to IN.
			nodeScheduler = NodeManager.getScheduler(_interfaceID);
			//Get the retry number from the object that is used to retry the getNode in case connection is failed.
			retryNumber = nodeScheduler.getRetryNum();
			//check if NodeScheduler is null throw exception.Confirm for Error code(INTERFACE_CONNECTION_NULL)if required-It should be new code like ERROR_NODE_FOUND!
			if(nodeScheduler==null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);
		
			for(int loop=1;loop<=retryNumber;loop++)
			{
				if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))&& "S".equals(_userType)))
				{
					_isSameRequest=true;
					checkInterfaceB4SendingRequest();
				}
				//Get the start time when the request is send to IN.
				startTime=System.currentTimeMillis();
				nodeVO = nodeScheduler.getNodeVO(_inTXNID);

				_requestMap.put("IN_URL", nodeVO.getUrl());
				if(nodeVO==null)
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND );
				warnTime=nodeVO.getWarnTime();
				readTimeOut=nodeVO.getReadTimeOut();
				
				//WebServiceConnector serviceConnection = new WebServiceConnector(_requestMap.get("SERVICE_URL").toString());
				WebServiceConnector serviceConnection = new WebServiceConnector(nodeVO.getUrl());
				clientStub =serviceConnection.getClient();

				if(clientStub==null)
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Unable to get Client Object");
					_log.error("sendRequestToIN","Unable to get Client Object");
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
				}
				try
				{
					switch(p_action)
					{
					case SafcomI.ACTION_ACCOUNT_INFO: 
					{
						try
						{
							responseObj=clientStub.balanceAmount(p_requestObj,p_requestObj);//updated by rahul as implementation not required
						}
						catch(java.rmi.RemoteException re)
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
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"OLORechargeINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for   Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

								_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
								nodeVO.incrementBarredCount();
								nodeVO.setBlocked(true);
								nodeVO.setBlokedAt(System.currentTimeMillis());
								if(loop==retryNumber)
								{
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"OLORechargeINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
									throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
								}
								continue;
							}
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Validation Request Action = "+p_action,"Remote Exception occured. So marking the transaction as FAIL");
							_log.error("sendRequestToIN","Remote Exception occured. So marking the response as FAIL");
							_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
							_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
							_log.error("sendRequestToIN","Error Message :"+e.getMessage());
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						}
						break;	
					}
					case SafcomI.ACTION_RECHARGE_CREDIT: 
					{
						if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," rechargeAmountSFC Called");
						try
						{
							//responseObj=clientStub.rechargeAmountSFC(p_requestObj);//commented as per new wsdl shared by client by rahul on 09 oct 12
						//	responseObj=clientStub.rechargeAmountSFC(p_requestObj, p_requestObj);
							responseObj =	new Response(p_requestObj.getTransactionID(), p_requestObj.getTerminalID(),p_requestObj.getOpermsisdn(), p_requestObj.getMsisdn(), p_requestObj.getAccountID(), p_requestObj.getAmount(),1);
							if(false)
								throw new java.rmi.RemoteException();
						}
						catch(java.rmi.RemoteException re)
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
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"OLORechargeINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for   Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

								_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
								nodeVO.incrementBarredCount();
								nodeVO.setBlocked(true);
								nodeVO.setBlokedAt(System.currentTimeMillis());
								if(loop==retryNumber)
								{
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"OLORechargeINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
									throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
								}
								continue;
							}
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Credit Request Action = "+p_action,"Remote Exception occured. So marking the transaction as AMBIGUOUS");
							_log.error("sendRequestToIN","Remote Exception occured. So marking the response as AMBIGUOUS"+re);
							re.printStackTrace();
							_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);

						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
							_log.error("sendRequestToIN","Error Message :"+e.getMessage());
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						}
						break;	
					}	
					case SafcomI.ACTION_IMMEDIATE_DEBIT: 
					{
						if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," chargeAmount Called");
						try
						{
							responseObj=clientStub.chargeAmount(p_requestObj,p_requestObj);
						}
						catch(java.rmi.RemoteException re)
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
								EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"OLORechargeINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI java.net.ConnectException while getting the connection for   Stub with INTERFACE_ID=["+_interfaceID +"]and Node Number=["+nodeVO.getNodeNumber()+"]");

								_log.info("sendRequestToIN","Setting the Node ["+nodeVO.getNodeNumber()+"] as blocked for duration ::"+nodeVO.getExpiryDuration() +" miliseconds");
								nodeVO.incrementBarredCount();
								nodeVO.setBlocked(true);
								nodeVO.setBlokedAt(System.currentTimeMillis());
								if(loop==retryNumber)
								{
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"OLORechargeINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
									throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
								}
								continue;
							}
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Credit Request Action = "+p_action,"Remote Exception occured. So marking the transaction as AMBIGUOUS");
							_log.error("sendRequestToIN","Remote Exception occured. So marking the response as AMBIGUOUS");
							_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
							throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						}
						catch(Exception e)
						{
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
							_log.error("sendRequestToIN","Error Message :"+e.getMessage());
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						}
						break;	
					}
					}//end of switch block	
					endTime=System.currentTimeMillis();

				}
				catch(BTSLBaseException be)
				{
					_log.error("sendRequestToIN","BTSLBaseException:"+be.getMessage());
					throw be;
				}
				catch(Exception e)
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
					_log.error("sendRequestToIN","Error Message :"+e.getMessage());
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				finally
				{
					nodeVO.resetBarredCount();	
					if(endTime==0) endTime=System.currentTimeMillis();
					_requestMap.put("IN_END_TIME",String.valueOf(endTime));			        
					_log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime);
				}

				if(responseObj==null)
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Response object found null");
					_log.error("sendRequestToIN","Response object is null");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				}

				TransactionLog.log( _interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response Status: "+responseObj.getStatus()+",AccountID: "+responseObj.getAccountID()+", Amount: "+responseObj.getAmount()+", Msisdn: "+responseObj.getMsisdn()+", Opermsisdn: "+responseObj.getOpermsisdn()+", TerminalID: "+responseObj.getTerminalID()+", TransactionID: "+responseObj.getTransactionID(),PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+p_action);
				//End time would be stored into request map with 
				//key as IN_END_TIME as soon as the response of the request is fetched from the IN.
				//Difference of start and end time would be compared against the warn time, 
				//if request and response takes more time than that of the warn time,
				//an event with level INFO is handled

				//Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled
				warnTime=Long.parseLong((String)_requestMap.get("WARN_TIMEOUT"));

				if(endTime-startTime>warnTime)
				{
					_log.info("sendRequestToIN", "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Ericssion IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
				}
			}
		//parse the response message by using CS3CCAPIRequestFormatter and fetch the execution status.
        _responseMap=_formatter.parseResponseObject(p_action,responseObj);
		String status=(String)_responseMap.get("RESP_STATUS");
		_requestMap.put("INTERFACE_STATUS",status);
		
		if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)))
		 {
		 	if(_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
				_interfaceCloser.resetCounters(_interfaceCloserVO,_requestMap);  
			_interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO); 
		 }
		
		//If the status is Not OK, exception with error code as RESPONSE_ERROR is thrown.
		if(!SafcomI.RESULT_OK.equals(status))
		{
			//Check the status whether the subscriber's msisdn defined in the IN
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Response Status fron IN is: "+status+ ". So marking response as FAIL for IN_TXN_ID="+(String)_requestMap.get("IN_TXN_ID"));
			_log.error("sendRequestToIN","Response Status fron IN is: "+status+ ". So marking response as FAIL for IN_TXN_ID="+(String)_requestMap.get("IN_TXN_ID"));
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
		}
		String inRespID=(String)_responseMap.get("RESP_TXN_ID");
		if(!_inTXNID.equals(inRespID))
		{
			_log.error("sendRequestToIN","Transaction id set in the request ["+_inTXNID+"] does not match with the transaction id fetched from response["+inRespID+"]");
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"SafcomSimulationINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " p_action = "+p_action,"Transaction id set in the request ["+_inTXNID+"] does not match with the transaction id fetched from response["+inRespID+"],Hence marking the transaction as AMBIGUOUS");
			_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
			throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.AMBIGOUS);
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
	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS3CCAPIINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
	    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
	}//end of catch-Exception
	finally
	{
		responseObj=null;
	    clientStub=null;
	    if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Exiting p_action="+p_action);
	}//end of finally
}
	
	public void setInterfaceParameters(int p_action) throws BTSLBaseException,Exception 
	{
        if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered");
        try
        {        	
	        String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
	    	if(InterfaceUtil.isNullString(cancelTxnAllowed))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());
	    	
	    	String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
	    	if(InterfaceUtil.isNullString(systemStatusMappingCr))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());
	    	
	    	String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
	    	if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());
	    	
	    	String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
	    	if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());
	    	
	    	String systemStatusMappingCrBck = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
	    	if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());
	    	
	    	String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
	    	if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());
	    	
	    	
	    	String cancelNA = FileCache.getValue(_interfaceID,"CANCEL_NA");
	    	if(InterfaceUtil.isNullString(cancelNA))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_NA is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("CANCEL_NA",cancelNA.trim());
	    	
	    	String userId = FileCache.getValue(_interfaceID,"SAFCOM_USER_ID");
	    	if(InterfaceUtil.isNullString(userId))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SAFCOM_USER_ID is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SAFCOM_USER_ID is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SAFCOM_USER_ID",userId.trim());
	    	
	    	String passwd = FileCache.getValue(_interfaceID,"SAFCOM_PASSWD");
	    	if(InterfaceUtil.isNullString(passwd))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SAFCOM_PASSWD is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SAFCOM_PASSWD is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SAFCOM_PASSWD",passwd.trim());
	    	
	    	String acntId = FileCache.getValue(_interfaceID,"SAFCOM_ACNT_ID");
	    	if(InterfaceUtil.isNullString(acntId))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SAFCOM_ACNT_ID is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SAFCOM_ACNT_ID is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SAFCOM_ACNT_ID",acntId.trim());
	    	
	    	String termId = FileCache.getValue(_interfaceID,"SAFCOM_TERM_ID");
	    	if(InterfaceUtil.isNullString(termId))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SAFCOM_TERM_ID is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SAFCOM_TERM_ID is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SAFCOM_TERM_ID",termId.trim());
	    	
	    	String serviceUrl = FileCache.getValue(_interfaceID,"SERVICE_URL");
	    	if(InterfaceUtil.isNullString(serviceUrl))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SERVICE_URL is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SERVICE_URL is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SERVICE_URL",serviceUrl.trim());
	    	
	    	String warnTimeStr=(String)FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
			if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafcomSimulationINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
			    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());
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
	
	private void checkInterfaceB4SendingRequest() throws BTSLBaseException
	{
    	if(_log.isDebugEnabled()) _log.debug("checkInterfaceB4SendingRequest","Entered");
    	try
		{
    		_interfaceCloserVO=(InterfaceCloserVO)InterfaceCloserController._interfaceCloserVOTable.get(_interfaceID);
    		_interfaceLiveStatus=(String)_requestMap.get("INT_ST_TYPE");
    		_interfaceCloserVO.setControllerIntStatus(_interfaceLiveStatus);
    		_interfaceCloser=_interfaceCloserVO.getInterfaceCloser();
    		
    		//Get AUTO_RESUME_SUPPORT property from IN FILE. If it is not defined then set it as 'N'.
    		String autoResumeSupported = FileCache.getValue(_interfaceID,"AUTO_RESUME_SUPPORT");
	    	if(InterfaceUtil.isNullString(autoResumeSupported))
	    	{
	    		autoResumeSupported="N";
	    	    _log.error("checkInterfaceB4SendingRequest","Value of AUTO_RESUME_SUPPORT is not defined in the INFile");	    		
	    	}  	    		    		
    		//If Controller sends 'A' and interface status is suspended, expiry is checked.
	    	//If Controller sends 'M', request is forwarded to IN after resetting counters.
    		if(InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(_interfaceLiveStatus)&& _interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
    		{
    			//Check if Auto Resume is supported by IN or not.If not then throw exception. request would not be sent to IN.
    			if("N".equals(autoResumeSupported))
    			{
    				_log.error("checkInterfaceB4SendingRequest","Interface Suspended.");
    				throw new BTSLBaseException(this,"checkInterfaceB4SendingRequest",InterfaceErrorCodesI.INTERFACE_SUSPENDED);
    			}
    			//If "Auto Resume" is supported then only check the expiry of interface, if expired then only request would be sent to IN
    			//otherwise checkExpiry method throws exception
    			if(_isSameRequest)
    				_interfaceCloser.checkExpiryWithoutExpiryFlag(_interfaceCloserVO);
    			else   			
    				_interfaceCloser.checkExpiry(_interfaceCloserVO);
    		}
    		//this block is executed when Interface is manually resumed (Controller sends 'M')from suspend state
    		else if(InterfaceCloserI.INTERFACE_MANNUAL_ACTIVE.equals(_interfaceCloserVO.getControllerIntStatus()) && _interfaceCloserVO.getFirstSuspendAt()!=0)
    			_interfaceCloser.resetCounters(_interfaceCloserVO,null);            
		}
    	catch(BTSLBaseException be)
		{
    		throw be;
		}
    	catch(Exception e)
		{
    		 e.printStackTrace();
			 _log.error("checkInterfaceB4SendingRequest","Exception e:"+e.getMessage());
			 throw new BTSLBaseException(this,"checkInterfaceB4SendingRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
    	finally
		{
    		if(_log.isDebugEnabled()) _log.debug("checkInterfaceB4SendingRequest","Exited");
		}	
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
		    //into reconciliation log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
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
					throw new BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.	??????)    			
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);	
				//added to discard amount field from the message.
				_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
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
