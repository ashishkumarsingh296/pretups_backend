package com.inter.mobinilpost;
/** MobinilPostpaidINHandler.java
* @(#)
* Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* Author				Date			History
*-------------------------------------------------------------------------------------------------
* Ranjana Chouhan    Jun 10,2009		Initial Creation
* ------------------------------------------------------------------------------------------------
* Interface class for the PostPaid Online Interface
*/


import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.rpc.holders.StringHolder;

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
import com.btsl.pretups.inter.mobinilpost.mobinilpoststub.MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_Stub;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.OracleUtil;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;

public class MobinilPostINHandler implements InterfaceHandler{

	private static Log _log = LogFactory.getLog("MobinilPostINHandler".getClass().getName());
	private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private String _msisdn=null;
    private String _referenceID=null;
    private String _interfaceID=null;
	private String _inTXNID=null;
	private String _interfaceLiveStatus=null;
	private InterfaceCloserVO _interfaceCloserVO= null;
	private InterfaceCloser _interfaceCloser=null;
	private boolean _isSameRequest=false;
	private String _userType=null;
	private String _interfaceClosureSupport=null;
	private int _QUEUE_ID_PADDING_LENGTH=12;
		
	/**
     * validate Method is used for getting the account information of user
     * @param p_map	HashMap
     * @throws BTSLBaseException,Exception
     */
	
	public void validate(HashMap p_map) throws BTSLBaseException,Exception
	{
		if (_log.isDebugEnabled())_log.debug("validate", "Entered p_map:"+p_map);
		_requestMap = p_map;
		_responseMap=new HashMap();
		String multplicationFactor=null;
		Object [] _requestObj=null;
		Connection con=null;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID"); 
			_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	
			if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();

			//Generate the IN transaction id 
			_inTXNID=InterfaceUtil.getINTransactionID();
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			
			//call the method to set the interface parameter		
			setInterfaceParameters();
			
			multplicationFactor=(String)_requestMap.get("MULTIPLICATION_FACTOR");
			_msisdn=InterfaceUtil.getFilterMSISDN((String) _requestMap.get("INTERFACE_ID"),(String)_requestMap.get("MSISDN"));
			
			//Sending the request to IN
	        sendRequestToIN(MobinilPostpaidI.ACTION_ACCOUNT_INFO,_responseMap);
	        			
	        //On successful response, set TRANSACTION_STATUS as SUCCES into request map.
	        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
	        
            try
            {
            //	_requestMap.put("IN_RECON_ID",_responseMap.get("account_id"));
            	
            	String balanceStr=(String)_responseMap.get("balance");
    			String creditLimit=(String)_responseMap.get("credit_limit");
    			
    			if(balanceStr==null || balanceStr.equalsIgnoreCase("null"))
    			    balanceStr=creditLimit;
    			
    			double multFactorDouble=Double.parseDouble(multplicationFactor.trim());
                balanceStr = InterfaceUtil.getSystemAmountFromINAmount(balanceStr,multFactorDouble);
                creditLimit= InterfaceUtil.getSystemAmountFromINAmount(creditLimit,multFactorDouble);
                
                if("PPB".equalsIgnoreCase((String)_requestMap.get("REQ_SERVICE")))
                	_requestMap.put("INTERFACE_PREV_BALANCE",balanceStr);
                else
                	_requestMap.put("INTERFACE_PREV_BALANCE",creditLimit);
                
                _requestMap.put("INTERFACE_PREV_BALANCE",creditLimit);
                _requestMap.put("BILL_AMOUNT_BAL",balanceStr);
                _requestMap.put("CREDIT_LIMIT",creditLimit);                                	
                _requestMap.put("ACCOUNT_STATUS",(String)_responseMap.get("account_status"));
                
    	    }
            catch(Exception e)
            {
            	_log.error("validate","Exception e:"+e.getMessage());
            	e.printStackTrace();
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
			    throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            
			//Setting the Servicve class into request map,if it is present in the response elso its value is NULL ,Controller will set ALL, if it is not set by the Handler.
			_requestMap.put("SERVICE_CLASS", (String)_responseMap.get("service_class"));
			_requestMap.put("ACCOUNT_STATUS", (String) _responseMap.get("acccount_status"));           
           			        	
			//set the mapping language of our system from FileCache mapping based on the responsed language.
            setLanguageFromMapping();
           
			///////////////
					
			MobinilPostToPreDAO mobinilPostToPreDAO=new MobinilPostToPreDAO();
			
            //validate the size of queue table if size check required field contains the service type
			if(InterfaceUtil.isStringIn((String)_requestMap.get("SERVICE_TYPE"),FileCache.getValue(_interfaceID, "SIZE_CHECK_REQUIRED")))
			{
				//get the service type from the request
				String serviceType=(String)_requestMap.get("SERVICE_TYPE");
				
				//If this is blank or set to all then size of queue table will be calculated irrecpective of service type
				String serviceTypeForSizeCalc=null;
				if(!InterfaceUtil.isNullString(FileCache.getValue(_interfaceID,serviceType+"_SERVICE_TYPE")))
						serviceTypeForSizeCalc=(FileCache.getValue(_interfaceID,serviceType+"_SERVICE_TYPE")).trim();
				if(!InterfaceUtil.isNullString(serviceTypeForSizeCalc)&&!PretupsI.ALL.equalsIgnoreCase(serviceTypeForSizeCalc))
				{
					StringTokenizer stringToken=new StringTokenizer(serviceTypeForSizeCalc,",");
					serviceTypeForSizeCalc="";
					while(stringToken.hasMoreTokens())
					{
						String tokenvalue=stringToken.nextToken();
						serviceTypeForSizeCalc=serviceTypeForSizeCalc+"'"+tokenvalue+"',";
					}
					serviceTypeForSizeCalc=serviceTypeForSizeCalc.substring(0,serviceTypeForSizeCalc.length()-1);
				}
				else
					serviceTypeForSizeCalc=PretupsI.ALL;
				
				int allowedSize=0;
				//get the allowed queue size for the service type
				//if this is not defined then queue size without service type is used.
				try{allowedSize=Integer.parseInt(FileCache.getValue(_interfaceID, serviceType+"_ALLOWED_QUEUE_SIZE"));}
				catch (Exception e){try{allowedSize=Integer.parseInt(FileCache.getValue(_interfaceID,"ALLOWED_QUEUE_SIZE"));}catch(Exception ex){EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[validate]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Queue table size not defined and queue table size check required="+FileCache.getValue(_interfaceID, "SIZE_CHECK_REQUIRED"));}}
				if (_log.isDebugEnabled())_log.debug("validate", "allowedSize=" + allowedSize);
				
				if(allowedSize>0)
				{
					//check the size in DB
					con=OracleUtil.getConnection();
					int queueTableSize=mobinilPostToPreDAO.calculateQueueTableSize(con,serviceTypeForSizeCalc,_interfaceID);
					
					if (_log.isDebugEnabled())_log.debug("validate", " queueTableSize="+queueTableSize);
					 
					//If queue table size is greater or equal to allowed size the refuse the request.
					if(queueTableSize>=allowedSize)
					{
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_QUEUE_SIZE_FULL);
					}
				}				
			}//end of if
					
			//////////////////
		}//try
		catch (BTSLBaseException be)
        {
			_log.errorTrace("validate", be);
        	_log.error("validate","BTSLBaseException be="+be.getMessage());
        	if("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
        		_interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
    		throw be; 	   	
        }//catch
        catch (Exception e)
        {
        	_log.errorTrace("validate",e);
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[validate]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }//catch
        finally
        {
        	try{if(con!=null)con.close();}catch(Exception e){}
        	if (_log.isDebugEnabled())
                _log.debug(this,"validate", "Exited _requestMap=" + _requestMap + " , multplicationFactor :"+multplicationFactor );
        }//finally
	}//end of validate
        
       /**
         * This method is responsible to send the request to IN.
         * @param	String p_inRequestStr
         * @param	int p_action
         * @throws BTSLBaseException
         */
        public void sendRequestToIN(int p_action,HashMap responseMap) throws BTSLBaseException
        {
            if(_log.isDebugEnabled()) _log.debug("sendRequestToIN"," p_action="+p_action);
            
    		//Put the request string, action, interface id, network code in the Transaction log.
    		TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"_requestMap: "+_requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+p_action );
    		
    		long startTime=0,endTime=0,warnTime=0;
    		MEAI_OnlineServices_webServices_PostToPreCreditTransferPortType_Stub clientStub=null; 
//		MobinilPostToPreTest_Stub testStub=null;
    		StringHolder transactionNumber=null;
    		StringHolder status=null;
    		StringHolder serviceClass=null;
    		StringHolder accountId=null;
    		StringHolder accountStatus=null;
    		StringHolder creditLimit=null;
    		StringHolder languageId=null;
    		StringHolder imsi=null;
    		StringHolder balance=null;
    		String serviceAdd=(String)_requestMap.get("END_POINT");
    		String timeOut=(String)_requestMap.get("TIME_OUT");
            try
            {
            	if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))&& "S".equals(_userType)))
    			{
    				_isSameRequest=true;
    				checkInterfaceB4SendingRequest();
    			}
                //Get the start time when the request is send to IN.
    			startTime=System.currentTimeMillis();
    			try
    			{
    				MobinilPostToPreConnector serviceConnection = new MobinilPostToPreConnector(serviceAdd,timeOut);
    				clientStub = serviceConnection.getClientStub();
    				
    				if(clientStub==null)
    				{
    					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Unable to get Client Object");
    					_log.error("sendRequestToIN","Unable to get Client Object");
    					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
    				}
    				else if(clientStub!=null)
	    			{
    					try
	    				{
    					transactionNumber=new StringHolder(_referenceID);
    				        status = new StringHolder();
    				        serviceClass=new StringHolder();
    				        accountId=new StringHolder();
    				        accountStatus=new StringHolder();
    				        creditLimit=new StringHolder();
    				        languageId=new StringHolder();
    				    	imsi=new StringHolder();
    				    	balance=new StringHolder();
    						
    				    	clientStub.getPostDialInfo(_msisdn,transactionNumber,status,serviceClass,accountId,accountStatus,creditLimit,languageId,imsi,balance);
//	    			testStub.getPostDialInfo(_msisdn,transactionNumber,status,serviceClass,accountId,accountStatus,creditLimit,languageId,imsi,balance);    								    						
	    					endTime=System.currentTimeMillis();
	    				}
	    				catch(java.rmi.RemoteException re)
	    				{       re.printStackTrace();
	    					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Remote Exception occured.");
	    					_log.error("sendRequestToIN","Remote Exception occured. So marking the response as AMBIGUOUS");
	    					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
	    					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
	    				}
	    				catch(Exception e)
	    				{
	    					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
	    					_log.error("sendRequestToIN","Error Message :"+e.getMessage());
	    					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
	    				}
    				}
    			}
    			catch(BTSLBaseException be)
    			{
    				_log.errorTrace("sendRequestToIN", be);		
    				throw be;
    			}
    			catch(Exception e)
    			{
    				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
    				_log.error("sendRequestToIN","Error Message :"+e.getMessage());
    				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    			}
    			finally
    			{
    				if(endTime==0) endTime=System.currentTimeMillis();
    			    _requestMap.put("IN_END_TIME",String.valueOf(endTime));			        
    			    _log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime);
    			 }
    			
    			
    			TransactionLog.log( _interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"_requestMap: "+_requestMap+", _responseMap: "+_responseMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+p_action);
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
    				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Ericssion IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
    			}
    			
    		    if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)))
    			 {
    			 	if(_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
    					_interfaceCloser.resetCounters(_interfaceCloserVO,_requestMap);  
    				_interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO); 
    			 }
    			
    		    // to get all the values from Stringholder object and put it into the response map.
    		   	//responseMap=new HashMap();
		    	String responseStatus="";
				responseStatus=status.value;
			if (_log.isDebugEnabled())
						_log.debug("sendRequestToIN", "responseStatus="+responseStatus);
				if(!InterfaceUtil.isNullString(responseStatus) && MobinilPostpaidI.RESULT_SUCCESSFUL.equalsIgnoreCase(responseStatus))
				{
					responseMap.put("transaction_status",responseStatus);
					responseMap.put("service_class",serviceClass.value);
					responseMap.put("account_id",accountId.value);
					responseMap.put("account_status",accountStatus.value);
					responseMap.put("credit_limit",creditLimit.value);
					responseMap.put("language_id",languageId.value);
					responseMap.put("imsi",imsi.value);
					responseMap.put("balance",balance.value);
										
					if (_log.isDebugEnabled())
						_log.debug("sendRequestToIN", "Msisdn ="+_msisdn+",transaction_status="+responseStatus+",service_class="+serviceClass.value+",account_id="+accountId.value+",account_status="+accountStatus.value+",credit_limit="+creditLimit.value+",language_id="+languageId.value+",imsi="+imsi.value+",balance="+balance.value);
					
				}else if(!InterfaceUtil.isNullString(responseStatus) && !MobinilPostpaidI.RESULT_SUCCESSFUL.equalsIgnoreCase(responseStatus))
				{
					if(MobinilPostpaidI.SUBSCRIBER_NOT_FOUND.equalsIgnoreCase(responseStatus))
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on Postpaid IN");
	    				_log.error("sendRequestToIN","MSISDN does not exist on IN");
	    				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
					}
					else if(MobinilPostpaidI.SUBSCRIBER_COMMERCIAL_USE.equalsIgnoreCase(responseStatus))
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on Postpaid IN");
	    				_log.error("sendRequestToIN","MSISDN is of commercial use");
	    				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
					else if(MobinilPostpaidI.SUBSCRIBER_INACTIVE.equalsIgnoreCase(responseStatus))
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on Postpaid IN");
	    				_log.error("sendRequestToIN","MSISDN is in INACTIVE state");
	    				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
					else if(MobinilPostpaidI.SUBSCRIBER_SOFTDISCONNECTED.equalsIgnoreCase(responseStatus))
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on Postpaid IN");
	    				_log.error("sendRequestToIN","MSISDN is in soft disconnected");
	    				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
					else if(MobinilPostpaidI.SUBSCRIBER_GROUP_SHARE_INITIATOR.equalsIgnoreCase(responseStatus))
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on Postpaid IN");
	    				_log.error("sendRequestToIN","MSISDN is in group share not an initiator ");
	    				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
					else if(MobinilPostpaidI.CORPORATE_SUBSCRIBER.equalsIgnoreCase(responseStatus))
					{
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on Postpaid IN");
	    				_log.error("sendRequestToIN","MSISDN is of corporate subscriber ");
	    				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					}
				}else if(InterfaceUtil.isNullString(responseStatus))
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"STATUS response is null in the response object");
					_log.error("sendRequestToIN","Response parameter is null in response object");
					throw new BTSLBaseException(InterfaceErrorCodesI.NULL_INTERFACE_RESPONSE);
				}
				else 
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"STATUS response is null in the response object");
					_log.error("sendRequestToIN","Invalid response status");
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
    			  			   		    
       		}
    		catch(BTSLBaseException be)
    		{
    			_log.errorTrace("sendRequestToIN", be);
    		    _log.error("sendRequestToIN","BTSLBaseException be = "+be.getMessage());
    		    throw be;
    		}//end of BTSLBaseException
    		catch(Exception e)
    		{
    			_log.errorTrace("sendRequestToIN", e);
    		    _log.error("sendRequestToIN","Exception="+e.getMessage());
    		    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
    		    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    		}//end of catch-Exception
    		finally
    		{
    			if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Exiting p_action="+p_action);
    		}//end of finally
        }//end of sendRequestToIN
        
	     /**
         * This method is used to get interface specific values from FileCache(load at starting)based on
         * interface id and set to the requested map.These parameters are
         * 1.cp_id
         * 2.application
         * 3.transaction_currency
         * @param	String p_interfaceID
         * @throws	Exception
         */
        private void setInterfaceParameters() throws BTSLBaseException,Exception
        {
            if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered");
            try
            {
            	// this parameter is used to get the cancel txn allowed on interface or not.            	
    	        String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
    	    	if(InterfaceUtil.isNullString(cancelTxnAllowed))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
    	    	}
    	    	_requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());
    	    	
    	    	String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
    	    	if(InterfaceUtil.isNullString(systemStatusMappingCr))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
    	    	}
    	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());
    	    	
    	    	String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
    	    	if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
    	    	}
    	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());
    	    	
    	    	String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
    	    	if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
    	    	}
    	    	_requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());
    	    	
    	    	String systemStatusMappingCrBck = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
    	    	if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
    	    	}
    	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());
    	    	
    	    	String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
    	    	if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
    	    	}
    	    	_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());
    	    	
    	    	
    	    	String cancelNA = FileCache.getValue(_interfaceID,"CANCEL_NA");
    	    	if(InterfaceUtil.isNullString(cancelNA))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of CANCEL_NA is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
    	    	}
    	    	_requestMap.put("CANCEL_NA",cancelNA.trim());
    	    	
    	    	String timeOut = FileCache.getValue(_interfaceID,"TIME_OUT");
    	    	if(InterfaceUtil.isNullString(timeOut))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of TIME_OUT is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "TIME_OUT is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
    	    	}
    	    	_requestMap.put("TIME_OUT",timeOut.trim());
    	    	
		String warnTimeOut = FileCache.getValue(_interfaceID,"WARN_TIMEOUT");
    	    	if(InterfaceUtil.isNullString(timeOut))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of TIME_OUT is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "TIME_OUT is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
    	    	}
    	    	_requestMap.put("WARN_TIMEOUT",warnTimeOut.trim());

    	    	String endPoint = FileCache.getValue(_interfaceID,"END_POINT");
    	    	if(InterfaceUtil.isNullString(endPoint))
    	    	{
    	    	    _log.error("setInterfaceParameters","Value of END_POINT is not defined in the INFile");
    	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
    	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
    	    	}
    	    	_requestMap.put("END_POINT",endPoint.trim());
    	    	
    	    	String multplicationFactor= (String)FileCache.getValue(_interfaceID,"MULTIPLICATION_FACTOR");
    			if(InterfaceUtil.isNullString(multplicationFactor))
    			{
    			    _log.error("validate","Multiplication factor is not defined in INFile");
    			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
    			    throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    			}	
    			_requestMap.put("MULTIPLICATION_FACTOR",multplicationFactor.trim());
    			
            }//end of try block
            catch(BTSLBaseException be)
			{
            	_log.errorTrace("setInterfaceParameters", be);
            	throw be;
			}
            catch(Exception e)
            {
            	_log.errorTrace("setInterfaceParameters", e);
                _log.error("setInterfaceParameters","Exception e="+e.getMessage());
                throw e;
            }//end of catch-Exception
            finally
            {
                if (_log.isDebugEnabled())_log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
            }//end of finally
        }//end of setInterfaceParameters
        
        /**
         * This method used to get the system language mapped in FileCache based on the INLanguge.Includes following
         * If the Mapping key not defined in IN file handle the event as System Error with level FATAL.
         * If the Mapping is not defined handle the event as SYSTEM INFO with level MAJOR and set empty string.
         * @throws Exception
         */
        private void setLanguageFromMapping() throws Exception
        {
            if(_log.isDebugEnabled()) _log.debug("setLanguageFromMapping","Entered");
            String mappedLang="";
    	    String[] mappingArr;
    	    String[] tempArr;
    	    boolean mappingNotFound = true;//Flag defines whether the mapping of language is found or not.
    	    String langFromIN = null;
            try
            {
                //Get the mapping string from the FileCache and storing all the mappings into array which are separated by ','.
             	String mappingString = (String)FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
             	if(InterfaceUtil.isNullString(mappingString))
             	{
             	    mappingString="";
             	    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilPostINHandler[setLanguageFromMapping]",_referenceID, _msisdn + " INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LANGUAGE_MAPPING is not defined in IN file,Hence setting the Default language");
             	}
             	langFromIN = (String)_responseMap.get("langauge_id");
             	if(_log.isDebugEnabled()) _log.debug("setLanguageFromMapping","mappingString = "+mappingString +" langFromIN = "+langFromIN);
        	  
		if(!InterfaceUtil.isNullString(langFromIN))
				{
					mappingArr = mappingString.split(",");
					//Iterating the mapping array to map the IN language from the system language,if found break the loop.
					for(int in=0;in<mappingArr.length;in++)
					{
						tempArr = mappingArr[in].split(":");
						if(langFromIN.equals(tempArr[0].trim()))
						{
							mappedLang = tempArr[1];
							mappingNotFound=false;
							break;
						}
					}//end of for loop
					//if the mapping of IN language with our system is not found,handle the event
					if(mappingNotFound)
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilPostINHandler[setLanguageFromMapping]",_referenceID, _msisdn + " INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  "+langFromIN +" is not defined in IN file Hence setting the Default language");
				}
				
		 else	{
					if(PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(SystemPreferences.DEFAULT_LANGUAGE))
						mappedLang="0";
					else if("ar".equalsIgnoreCase(SystemPreferences.DEFAULT_LANGUAGE))
						mappedLang="1";
				}//et the mapped language to the requested map with key as IN_LANGUAGE.
        	    _requestMap.put("IN_LANG",mappedLang);
            }//end of try
            catch(Exception e)
            {
            	_log.errorTrace("setLanguageFromMapping", e);
                _log.error("setLanguageFromMapping","Exception e="+e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilPostINHandler[setLanguageFromMapping]",_referenceID, _msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
            }//end of catch-Exception
            finally
            {
                if(_log.isDebugEnabled()) _log.debug("setLanguageFromMapping","Exited mappedLang ="+mappedLang);
            }//end of finally setLanguageFromMapping
        }//end of setLanguageFromMapping
        
        /**
    	 * Method to Check interface status before sending request.	
    	 * @throws	BTSLBaseException 
    	 */
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
        		_log.errorTrace("checkInterfaceB4SendingRequest", be);
        		throw be;
    		}
        	catch(Exception e)
    		{
        		_log.errorTrace("checkInterfaceB4SendingRequest", e);
    			 _log.error("checkInterfaceB4SendingRequest","Exception e:"+e.getMessage());
    			 throw new BTSLBaseException(this,"checkInterfaceB4SendingRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
    		}
        	finally
    		{
        		if(_log.isDebugEnabled()) _log.debug("checkInterfaceB4SendingRequest","Exited");
    		}	
    	}
        
        /**
         * This method credit the balance of user in case of credit-back.
         * @param p_map HashMap
         * @throws BTSLBaseException,Exception
         */
        	public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception
        	{
        		if (_log.isDebugEnabled())
                    _log.debug("creditAdjust", "Entered " + InterfaceUtil.getPrintMap(p_map));
                _requestMap = p_map;
        		try
        		{
        			_inTXNID=InterfaceUtil.getINTransactionID();
        			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
        			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        			_msisdn=(String)_requestMap.get("MSISDN");
        			_requestMap.put("IN_TXN_ID",_inTXNID);
        			_requestMap.put("Stage", "CreditAdjust");
        			_requestMap.put("ENTRY_TYPE",PretupsI.CREDIT);
        			sendRequestToDB(_requestMap,"CreditAdjust");
        	        _requestMap .put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        			long postBalance=0;
        			try{postBalance=Long.valueOf((String)_requestMap.get("INTERFACE_PREV_BALANCE")).longValue()+Long.valueOf((String)_requestMap.get("INTERFACE_AMOUNT")).longValue();}
        			catch(Exception e){postBalance=Long.valueOf((String)_requestMap.get("INTERFACE_AMOUNT")).longValue();}
        			_requestMap .put("INTERFACE_POST_BALANCE",(String.valueOf(postBalance)));
        			//Following parameters is sent by ankit z on date 3/8/06, to restrict the post balance,validatiy and grace to be send in message.
        			_requestMap.put("POST_BALANCE_ENQ_SUCCESS",FileCache.getValue(_interfaceID, "POST_BALANCE_ENQ_SUCCESS"));
        		}
        		catch(BTSLBaseException be)
        		{
        			_log.errorTrace("creditAdjust", be);
        			throw be;
        		}
        		catch(Exception e)
        		{
        			_log.errorTrace("creditAdjust", e);
        			_log.error("creditAdjust","Exception e:"+e.getMessage()+InterfaceUtil.getPrintMap(_requestMap));
        			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[creditAdjust]",(String)_requestMap.get("TRANSACTION_ID"),(String)_requestMap.get("MSISDN"),(String)_requestMap.get("NETWORK_CODE"),"Exception while credit adjust");
        			throw e;
        		}
        		finally{if (_log.isDebugEnabled())_log.debug("creditAdjust", "Exited _requestMap=" + _requestMap); }
        	}
        /**
         * Method to perform actual communication with database 
         * @param HashMap p_requestMap
         * @param String p_stage
         * @throws BTSLBaseException
         */
        	public void sendRequestToDB(HashMap p_requestMap,String p_stage) throws BTSLBaseException
        	{
        		if(_log.isDebugEnabled()) _log.debug("sendRequestToDB","Entered p_requestMap:"+p_requestMap+" p_stage:"+p_stage);
        		TransactionLog.log((String)_requestMap.get("IN_TXN_ID"),(String)_requestMap.get("IN_TXN_ID"),(String)_requestMap.get("MSISDN"),(String)_requestMap.get("NETWORK_CODE"),p_stage,PretupsI.TXN_LOG_REQTYPE_REQ,"Request map:"+p_requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_stage);
        		Connection con=null;
        		MobinilPostToPreDAO mobinilPostToPreDAO=null;
        		MobinilPostToPreVO mobinilPostToPreVO=null;
        		try
        		{
        			mobinilPostToPreDAO=new MobinilPostToPreDAO();
        			con=OracleUtil.getConnection();
        			p_requestMap.put("IN_START_TIME",String.valueOf(System.currentTimeMillis()));
        			mobinilPostToPreVO=new MobinilPostToPreVO();
        			//get the multiplication factor from the file cache
        			double multiplicationFactor=Double.parseDouble((FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR")).trim());
        			double currAmount=Double.parseDouble((String)p_requestMap.get("INTERFACE_AMOUNT"));
        			//convert the amount that is request into display amount
        			double interfaceAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(currAmount,multiplicationFactor);
        			populateVOfromMap(mobinilPostToPreVO);
        			p_requestMap.put("IN_RECON_ID",mobinilPostToPreVO.getAccountID());
        			mobinilPostToPreVO.setInterfaceAmount(interfaceAmount);
        			
        			getQueueID(con,mobinilPostToPreVO);
        			
        			int addCount=-1;
        			//if request is of credit adjust then update the record otherwise insert the record
        			if("CreditAdjust".equals(p_stage))
        				addCount=mobinilPostToPreDAO.updateDataInQueueTable(con,mobinilPostToPreVO);
        			else
        				addCount=mobinilPostToPreDAO.insertDataInQueueTable(con,mobinilPostToPreVO);
        			
        			if(addCount<=0)
        			{
        				//rollback the connection
        				con.rollback();
        				throw new BTSLBaseException(PretupsErrorCodesI.TXN_STATUS_FAIL);
        			}
        			
        			//If add count is more then zero then commit the connection and set the interface status to success
        			con.commit();
        			_requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
        			
        			//set the end time of transcation. this time will be entered in interface transaction table
        			p_requestMap.put("IN_END_TIME",String.valueOf(System.currentTimeMillis()));
        			TransactionLog.log((String)_requestMap.get("IN_TXN_ID"),(String)_requestMap.get("IN_TXN_ID"),(String)_requestMap.get("MSISDN"),(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_stage),PretupsI.TXN_LOG_REQTYPE_RES,"mobinilPostToPreVO:"+mobinilPostToPreVO,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_stage);
        	   }//end of try
        	   catch(BTSLBaseException be)
        	   {
        		   _log.errorTrace("sendRequestToDB", be);
        		   throw be;
        	   }
        	   catch(Exception e)
        	   {
        		   _log.errorTrace("sendRequestToDB", e);
        		   _log.error("sendRequestToDB","Exception:"+e.getMessage());
        		   EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION ,EventComponentI.INTERFACES ,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandlerHandler[sendRequestToDB]",(String)_requestMap.get("TRANSACTION_ID"),(String)_requestMap.get("MSISDN"),(String)_requestMap.get("NETWORK_CODE"),"System Exception:"+e.getMessage());
        		   throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        	   }
        	   finally
        	   {
        		   //if connection is not null then close the connection.
        		   try{if(con!=null)con.close();}catch(Exception e){}
        		   if(_log.isDebugEnabled())
        			_log.debug("sendRequestToDB","Exiting p_stage:"+p_stage);
        	   }
        	}
        /**
         * This method credit the balance of user.
         * @param p_map HashMap
         * @throws BTSLBaseException,Exception
         */
        	public void credit(HashMap p_map) throws BTSLBaseException, Exception
        	{
        		if (_log.isDebugEnabled())
                    _log.debug("credit", "Entered " + InterfaceUtil.getPrintMap(p_map));
                _requestMap = p_map;
        		try
        		{
        			_inTXNID=InterfaceUtil.getINTransactionID();
        			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
        			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        			_msisdn=(String)_requestMap.get("MSISDN");
        			_requestMap.put("IN_TXN_ID",_inTXNID);
        			_requestMap.put("Stage", "Credit");
        			_requestMap.put("ENTRY_TYPE",PretupsI.CREDIT);
        			sendRequestToDB(_requestMap,"Credit");
        	        _requestMap .put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        			long postBalance=0;
        			try{postBalance=Long.valueOf((String)_requestMap.get("INTERFACE_PREV_BALANCE")).longValue()+Long.valueOf((String)_requestMap.get("INTERFACE_AMOUNT")).longValue();}
        			catch(Exception e){postBalance=Long.valueOf((String)_requestMap.get("INTERFACE_AMOUNT")).longValue();}
        			_requestMap.put("INTERFACE_POST_BALANCE",(String.valueOf(postBalance)));
        			//Following parameters is sent by ankit z on date 3/8/06, to restrict the post balance,validatiy and grace to be send in message.
        			_requestMap.put("POST_BALANCE_ENQ_SUCCESS",FileCache.getValue(_interfaceID, "POST_BALANCE_ENQ_SUCCESS"));
        		}
        		catch(BTSLBaseException be)
        		{
        			_log.errorTrace("credit", be);
        			throw be;
        		}
        		catch(Exception e)
        		{
        			_log.errorTrace("credit", e);
        			_log.error("credit","Exception e:"+e.getMessage()+InterfaceUtil.getPrintMap(_requestMap));
        			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[credit]",(String)_requestMap.get("TRANSACTION_ID"),(String)_requestMap.get("MSISDN"),(String)_requestMap.get("NETWORK_CODE"),"Exception while credit");
        			throw e;
        		}
        		finally{if (_log.isDebugEnabled())_log.debug("credit", "Exited _requestMap=" + _requestMap); }
        	}
        /**
         * This method debit the balance of user.
         * @param p_map HashMap
         * @throws BTSLBaseException,Exception
         */
        	public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception
        	{
        		if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "Entered " + InterfaceUtil.getPrintMap(p_map));
                _requestMap = p_map;
        		try
        		{
        			_inTXNID=InterfaceUtil.getINTransactionID();
        			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
        			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        			_msisdn=(String)_requestMap.get("MSISDN");
        			_requestMap.put("IN_TXN_ID",_inTXNID);
        			_requestMap.put("Stage", "DebitAdjust");
        			_requestMap.put("ENTRY_TYPE",PretupsI.DEBIT);
        			sendRequestToDB(_requestMap,"DebitAdjust");
        	        _requestMap .put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        			long postBalance=0;
        			try{postBalance=Long.valueOf((String)_requestMap.get("INTERFACE_PREV_BALANCE")).longValue()-Long.valueOf((String)_requestMap.get("INTERFACE_AMOUNT")).longValue();}
        			catch(Exception e){postBalance=Long.valueOf((String)_requestMap.get("INTERFACE_AMOUNT")).longValue();}
        			_requestMap .put("INTERFACE_POST_BALANCE",(String.valueOf(postBalance)));
        			//Following parameters is sent by ankit z on date 3/8/06, to restrict the post balance,validatiy and grace to be send in message.
        			_requestMap.put("POST_BALANCE_ENQ_SUCCESS",FileCache.getValue(_interfaceID, "POST_BALANCE_ENQ_SUCCESS"));
        		}
        		catch(BTSLBaseException be)
        		{
        			_log.errorTrace("debitAdjust", be);
        			throw be;
        		}
        		catch(Exception e)
        		{
        			_log.errorTrace("debitAdjust", e);
        			_log.error("debitAdjust","Exception e:"+e.getMessage()+InterfaceUtil.getPrintMap(_requestMap));
        			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"MobinilPostINHandler[debitAdjust]",(String)_requestMap.get("TRANSACTION_ID"),(String)_requestMap.get("MSISDN"),(String)_requestMap.get("NETWORK_CODE"),"Exception while credit");
        			throw e;
        		}
        		finally{if (_log.isDebugEnabled())_log.debug("debitAdjust", "Exited _requestMap=" + _requestMap); }
        	}
        /**
         * This method would be used to adjust the validity of the subscriber account at the IN.
         * @param	HashMap p_requestMap
         * @throws	BTSLBaseException, Exception
         */   
        	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
        	{}//end of validityAdjust
        
        /***
         * Method to get the queue ID. this method also format the queueID
         * After getting queueID this method set the values in queue table vo to enter into queue table
         * @param p_con Connection
         * @param p_mobinilPostToPreVO MobinilPostToPreVO
         * @return void
         */
        	public void getQueueID(Connection p_con, MobinilPostToPreVO p_mobinilPostToPreVO) throws BTSLBaseException, Exception
        	{
        		if (_log.isDebugEnabled())_log.debug("getQueueID", "Entered p_queueTableVO=" + p_mobinilPostToPreVO.toString());
        		try
        		{
        			java.util.Date mydate = new java.util.Date();
        			SimpleDateFormat sdf = new SimpleDateFormat ("yyMMddHH");
        			String dateString = sdf.format(mydate);
        			MobinilPostToPreDAO mobinilPostToPreDAO=new MobinilPostToPreDAO();
        			String queueID=mobinilPostToPreDAO.getQueueID(p_con);
        			while(queueID.length()<_QUEUE_ID_PADDING_LENGTH)
        			{
        				queueID="0"+queueID;
        			}
        			queueID=dateString+queueID;
        			p_mobinilPostToPreVO.setQueueID(queueID);
        		}
        		catch(BTSLBaseException be)
        		{
        			_log.errorTrace("getQueueID", be);
        			throw be;
        		}
        		catch(Exception e)
        		{
        			_log.errorTrace("getQueueID", e);
        			throw e;
        		}
        		if (_log.isDebugEnabled())_log.debug("getQueueID", "Exited queueID=" + p_mobinilPostToPreVO.getQueueID());
        	}
        
       
/***
 * Method to set the values in mobinilPostToPreVO to enter the transaction details into database table
 * @param p_queueTableVO MobinilPostToPreVO
 * @return void
 */
	public void populateVOfromMap(MobinilPostToPreVO p_mobinilPostToPreVO)
	{
		if (_log.isDebugEnabled())_log.debug("populateVOfromMap", "Entered");
		
		p_mobinilPostToPreVO.setTransferID((String)_requestMap.get("TRANSACTION_ID"));
		p_mobinilPostToPreVO.setAccountID((String)_requestMap.get("ACCOUNT_ID"));
		p_mobinilPostToPreVO.setMsisdn((String)_requestMap.get("MSISDN"));
		p_mobinilPostToPreVO.setAmount(Long.valueOf((String)_requestMap.get("INTERFACE_AMOUNT")).longValue());
		p_mobinilPostToPreVO.setStatus(PretupsI.STATUS_QUEUE_AVAILABLE);
		p_mobinilPostToPreVO.setNetworkID((String)_requestMap.get("NETWORK_CODE"));
		p_mobinilPostToPreVO.setEntryOn(new Date());
		p_mobinilPostToPreVO.setCreatedOn(new Date());
		p_mobinilPostToPreVO.setDescription((String)_requestMap.get("GATEWAY_TYPE")+"_MSISDN_"+(String)_requestMap.get("SENDER_MSISDN"));
		p_mobinilPostToPreVO.setServiceType((String)_requestMap.get("SERVICE_TYPE"));
		p_mobinilPostToPreVO.setSourceType((String)_requestMap.get("SOURCE_TYPE"));
		p_mobinilPostToPreVO.setInterfaceID((String)_requestMap.get("INTERFACE_ID"));
		p_mobinilPostToPreVO.setExternalInterfaceID((String)_requestMap.get("EXTERNAL_ID"));
		p_mobinilPostToPreVO.setSenderID((String)_requestMap.get("SENDER_ID"));
		p_mobinilPostToPreVO.setSenderMsisdn((String)_requestMap.get("SENDER_MSISDN"));
		p_mobinilPostToPreVO.setModule((String)_requestMap.get("MODULE"));
		p_mobinilPostToPreVO.setServiceClass((String)_requestMap.get("SERVICE_CLASS"));
		p_mobinilPostToPreVO.setProductCode((String)_requestMap.get("PRODUCT_CODE"));
		p_mobinilPostToPreVO.setTaxAmount((Long.parseLong((String)_requestMap.get("TAX_AMOUNT"))));
		p_mobinilPostToPreVO.setAccessFee((Long.parseLong((String)_requestMap.get("ACCESS_FEE"))));
		if(!InterfaceUtil.isNullString((String)_requestMap.get("BONUS_AMOUNT")))
			p_mobinilPostToPreVO.setBonusAmount((Long.parseLong((String)_requestMap.get("BONUS_AMOUNT"))));
		p_mobinilPostToPreVO.setEntryFor((String)_requestMap.get("USER_TYPE"));
		p_mobinilPostToPreVO.setEntryType((String)_requestMap.get("ENTRY_TYPE"));
		p_mobinilPostToPreVO.setGatewayCode((String)_requestMap.get("GATEWAY_CODE"));
		p_mobinilPostToPreVO.setImsi((String)_requestMap.get("IMSI"));
		p_mobinilPostToPreVO.setReceiverMsisdn((String)_requestMap.get("RECEIVER_MSISDN"));
		p_mobinilPostToPreVO.setType((String)_requestMap.get("REQ_SERVICE"));
		
		if (_log.isDebugEnabled())_log.debug("populateVOfromMap", "Exited p_mobinilPostToPreVO=" + p_mobinilPostToPreVO.toString());
	}
}
