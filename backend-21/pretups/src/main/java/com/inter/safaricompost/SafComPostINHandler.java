
package com.inter.safaricompost;
/**
 * @(#)SafComPostINHandler.java
 * Copyright(c) 2008, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 *Manisha Jain			09 june	2008	Initial creation
 * ------------------------------------------------------------------------------------------------
 * Handler class for the interface Post Paid billing System
 */
import java.io.BufferedReader;
import java.io.PrintWriter;
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


public class SafComPostINHandler implements InterfaceHandler
{

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    SafComPostRequestFormatter _formatter = new SafComPostRequestFormatter();
    private String _interfaceID=null;
	private String _inTXNID=null;
	private String _msisdn=null;
	private String _referenceID=null;
	private String _interfaceLiveStatus=null;
	private InterfaceCloserVO _interfaceCloserVO= null;
	private InterfaceCloser _interfaceCloser=null;
	private boolean _isSameRequest=false;
	private String _userType=null;
	private String _interfaceClosureSupport=null;
	private String _multFactor =null;
	
	/**
     * validate Method is used for getting the account information of user
     * @param p_map HashMap
     * @throws BTSLBaseException,Exception
     */
    public void validate(HashMap p_map) throws BTSLBaseException, Exception
    {
        if (_log.isDebugEnabled())_log.debug("validate", "Entered ");
        _requestMap = p_map;        
        try
        {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");	
			_inTXNID=_referenceID;
			_msisdn=(String)_requestMap.get("MSISDN");
			_requestMap.put("IN_TXN_ID",_inTXNID);
            setInterfaceParameters(_interfaceID);
            String inStr = _formatter.generateRequest(SafaricomPostI.ACTION_ACCOUNT_INFO, _requestMap);
            sendRequestToIN(inStr, SafaricomPostI.ACTION_ACCOUNT_INFO);// Sending the request to IN
            
        	_multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+_multFactor);
			if(InterfaceUtil.isNullString(_multFactor))
			{
			    _log.error("validate","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafComPostINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_multFactor=_multFactor.trim();
			
			_requestMap.put("MULT_FACTOR",_multFactor);
            _requestMap.put("IN_RECON_ID",(String)_responseMap.get("ACCOUNTNO"));
            _requestMap.put("SERVICE_CLASS", (String)_responseMap.get("SERVICECLASS"));
            _requestMap.put("ACCOUNT_STATUS", (String)_responseMap.get("ACCOUNTSTATUS"));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        	
        }
        catch (BTSLBaseException be)
        {
        	_log.error("validate","BTSLBaseException be="+be.getMessage());
        	if("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
        		_interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);      	
    		throw be; 	   	
        }
        catch (Exception e)
        {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[validate]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw e;
        } finally
        { 
        	if (_log.isDebugEnabled())_log.debug("validate", "Exited _requestMap=" + _requestMap);
        }
    }//end of validate

    /**
     * credit Method is used for recharge.
     * @param p_map HashMap
     * @throws BTSLBaseException,Exception
     */
    public void credit(HashMap p_map) throws BTSLBaseException, Exception
    {
    	if (_log.isDebugEnabled())_log.debug("credit", "Entered ");
    	_requestMap = p_map;        
        try
        {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	
        	_multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+_multFactor);
			if(InterfaceUtil.isNullString(_multFactor))
			{
			    _log.error("validate","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafComPostINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_multFactor=_multFactor.trim();
			_requestMap.put("MULT_FACTOR",_multFactor);
			
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");	
			_inTXNID=_referenceID;
			_msisdn=(String)_requestMap.get("MSISDN");
			_requestMap.put("IN_TXN_ID",_inTXNID);
			setInterfaceParameters(_interfaceID);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
			String inStr = null;
            inStr = _formatter.generateRequest(SafaricomPostI.ACTION_CREDIT, _requestMap);
            sendRequestToIN(inStr, SafaricomPostI.ACTION_CREDIT);
            _requestMap.put("IN_RECON_ID",_requestMap.get("ACCOUNT_ID"));
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } 
        catch (BTSLBaseException be)
        {
        	p_map.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
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
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				 throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
        catch (Exception e)
        {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception in credit method");
            throw e;
        }
        finally
        {
        	if (_log.isDebugEnabled())_log.debug("credit", "Exited _requestMap=" + _requestMap );
        }
    }//end credit

    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception
    {}

    /**
     * debitAdjust Method is used for debit.
     * @param p_map HashMap
     * @throws BTSLBaseException,Exception
     */
    public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception
    {}//end debitAdjust
    
    
    /**
	 * This method would be used to adjust the validity of the subscriber account at the IN.
	 * @param	HashMap p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */   
	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{}//end of validityAdjust


    /**
     * This method is used to set the interface parameters into request map.
     * @param	String p_interfaceID
     * @throws	BTSLBaseException,Exception
     */
    public void setInterfaceParameters(String p_interfaceID) throws BTSLBaseException,Exception 
    {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_interfaceID=" + p_interfaceID);
        
        try
		{
	        String cancelTxnAllowed = FileCache.getValue(p_interfaceID,"CANCEL_TXN_ALLOWED");
	    	if(InterfaceUtil.isNullString(cancelTxnAllowed))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
	    	}
	    	_requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());
	    	
	    	String systemStatusMappingCr = FileCache.getValue(p_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
	    	if(InterfaceUtil.isNullString(systemStatusMappingCr))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());
	    	String cancelCommandStatusMapping = FileCache.getValue(p_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
	    	if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
	    	}
	    	_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());
	    
	    	
	    	String cancelNA = FileCache.getValue(p_interfaceID,"CANCEL_NA");
	    	if(InterfaceUtil.isNullString(cancelNA))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_NA is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
	    	}
	    	_requestMap.put("CANCEL_NA",cancelNA.trim());
	    	
	    	String source = FileCache.getValue(p_interfaceID,"SOURCE");
	    	if(InterfaceUtil.isNullString(source))
	    	{
	    	    _log.error("setInterfaceParameters","Value of source is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SOURCE is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
	    	}
	    	_requestMap.put("SOURCE",source.trim());
	    }
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
    }//end setInterfaceParameters
    
    /**
	 * This method would be used to send validate, credit, creditAdjust, debitAdjust requests to IN depending on the p_stage value.
	 * @param	String p_inRequestStr
	 * @param   int p_stage 
	 * @throws	BTSLBaseException
	 */
    public void sendRequestToIN(String p_inRequestStr, int p_stage) throws BTSLBaseException
    {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_inRequestStr + " p_stage:" + p_stage);
        TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_stage),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string:"+p_inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_stage);
        String responseStr = "";
        String status=null;
        SafComURLConnection safComURLConnection = null;
		long startTime=0;
        try
        {
            _responseMap = new HashMap();
            String inReconID=(String)_requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=_inTXNID;
			int readTimeOut ;
			String url=null;
			if(SafaricomPostI.ACTION_ACCOUNT_INFO == p_stage)
			{
	            String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_VAL");
	            if(readTimeOutStr==null)
	            {
				    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[sendRequestToIN]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE")+" Stage = "+p_stage, "Read time out VAL is not defined in INFile");
				    throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
	            }
	            _requestMap.put("READ_TIMEOUT_VAL",readTimeOutStr);
	            readTimeOut = Integer.parseInt(readTimeOutStr);
	            url=FileCache.getValue(_interfaceID, "URL_ACCOUNT_INFO");
	            
	            if (_log.isDebugEnabled())
					_log.debug("sendRequestToIN"," READ TIMEOUT VAL "+readTimeOut);
			}
			else
			{
	            String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_TOP");
	            if(readTimeOutStr==null)
	            {
				    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[sendRequestToIN]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE")+" Stage = "+p_stage, "Read time out TOP is not defined in INFile");
				    throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
	            }
	            _requestMap.put("READ_TIMEOUT_TOP",readTimeOutStr);
	            readTimeOut = Integer.parseInt(readTimeOutStr);
	            
	            url=FileCache.getValue(_interfaceID, "URL");
	            
	            if (_log.isDebugEnabled())
					_log.debug("sendRequestToIN"," READ TIMEOUT TOP "+readTimeOut);
	            
			}///end of if read timeout
			
			//In creditAdjust (sender credit back )don't check interface status, simply send the request to IN.  
			if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))&& "S".equals(_userType)))
			{
				_isSameRequest=true;
				checkInterfaceB4SendingRequest();
			}
			try
			{
            	safComURLConnection = new SafComURLConnection(url,Integer.parseInt(FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT")),readTimeOut,FileCache.getValue(_interfaceID, "KEEP_ALIVE"));
            }
			catch(Exception e)
			{
				e.printStackTrace();
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[sendRequestToIN]", " INTERFACE ID = "+_interfaceID, " Stage = "+p_stage, "", "Not able to create connection, getting Exception:" + e.getMessage());
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
			}
			try
            {
				
				
				PrintWriter out = safComURLConnection.getPrintWriter();
				
				
	            out.flush();
	           
				startTime=System.currentTimeMillis();
				_requestMap.put("IN_START_TIME",String.valueOf(startTime));
			    if (_log.isDebugEnabled())
	                _log.debug("sendRequestToIN", "Entered startTime:" + startTime + " p_inRequestStr:" + p_inRequestStr);
	        
                out.println(p_inRequestStr);
                out.flush();
            } 
            catch (Exception e)
            {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"SafaricomPostINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage,"Exception while sending request to Safari com Post paid IN");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }
            try
			{

                // Create buffered reader and Read Response from the IN
                StringBuffer buffer = new StringBuffer();
                String response = "";
				long endTime=0;
                try
                {
                    safComURLConnection.setBufferedReader();
                    BufferedReader in = safComURLConnection.getBufferedReader();
					
                    while ((response = in.readLine()) != null)
                    {
                        buffer.append(response);
                    }
					endTime=System.currentTimeMillis();
					String warnTimeStr=(String)FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
					if(!InterfaceUtil.isNullString(warnTimeStr))
					{	
						long warnTime=Long.parseLong(warnTimeStr);
						if(endTime-startTime>warnTime)
						{
							_log.info("sendRequestToIN", "WARN time reaches startTime: "+startTime+" endTime: "+endTime+" warnTime: "+warnTime+" time taken: "+(endTime-startTime));
							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"SafaricomPostINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage,"Safari com Post paid IN is taking more time than the warning threshold. Time: "+(endTime-startTime));
						}
					}
				} 
				catch (Exception e)
                {
					_log.error("sendRequestToIN", " response form interface is null exception is " + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "SafaricomPostINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage, "Exception while getting response from Safari com Post paid IN e: "+e.getMessage());
                    _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);										
                }
				finally
				{
					if(endTime==0) endTime=System.currentTimeMillis();
					_requestMap.put("IN_END_TIME",String.valueOf(endTime));
					if(p_stage==SafaricomPostI.ACTION_ACCOUNT_INFO)
						_log.error("sendRequestToIN","IN_START_TIME="+String.valueOf(startTime)+" IN_END_TIME="+String.valueOf(endTime)+" READ_TIMEOUT_VAL ="+_requestMap.get("READ_TIMEOUT_VAL"));
					else 
						_log.error("sendRequestToIN","IN_START_TIME="+String.valueOf(startTime)+" IN_END_TIME="+String.valueOf(endTime)+" READ_TIMEOUT_TOP="+_requestMap.get("READ_TIMEOUT_TOP"));
				}
                responseStr = buffer.toString();

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr:" + responseStr);
				TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_stage),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," INTERFACE ID = "+_interfaceID+" action="+p_stage);
                String httpStatus = safComURLConnection.getResponseCode();

                _requestMap.put("PROTOCOL_STATUS", httpStatus);

                if (InterfaceUtil.isNullString(responseStr) )
                {                	
					_log.error("sendRequestToIN", " Blank response from  IN");
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "SafaricomPostINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage, "Blank response from Safari com Post paid IN ");
                    _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);						             	
                }
                _responseMap = _formatter.parseResponse(p_stage, responseStr);
				status=(String) _responseMap.get("STATUS");				
				String respTransID=(String)_responseMap.get("TXNID");
				_requestMap.put("INTERFACE_STATUS",status);
				
				if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)))
				{
					if(_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
						_interfaceCloser.resetCounters(_interfaceCloserVO,_requestMap);  
					_interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
				}
						
				if (!InterfaceUtil.isNullString(status)&& !status.equals(SafaricomPostI.RESULT_OK))
                {
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"SafaricomPostINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage,"Parameters values blank in response result: "+status);
				    if (status.equals(SafaricomPostI.RESULT_ERROR_201))
				        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);//MSISDN Not Found
				    else if (status.equals(SafaricomPostI.RESULT_ERROR_1))
				        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_VALID_POSTPAID_NO);//Not a valid postpaid number
				    else if (status.equals(SafaricomPostI.RESULT_ERROR_2))
				        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_UNKONOW_ERROR_ON_POSTPAID);//Unknown Error.
				    else if (status.equals(SafaricomPostI.RESULT_ERROR_3))
				        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_AMT_PAID_NEGATIVE_OR_ZERO);//Amount Paid is Negative or Zero.
				    else if (status.equals(SafaricomPostI.RESULT_ERROR_4))
				        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_VALID_POSTPAID_NO);//Not a valid postpaid number
				    else if (status.equals(SafaricomPostI.RESULT_ERROR_5))
				        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_VALID_POSTPAID_NO_AND_NEGATIVE_OR_ZERO);//Not a valid postpaid number and Amount Paid is Negative or Zero 
				    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
				else if (InterfaceUtil.isNullString(respTransID))
                {
                    _log.info("sendRequestToIN", "transID:" + respTransID  + " status=" +status);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"SafaricomPostINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage,"Parameters values blank in response result: "+status);
                    throw new BTSLBaseException(InterfaceErrorCodesI.NULL_INTERFACE_RESPONSE);
                }
				if (!respTransID.equals(inReconID))
                {
                    _log.info("sendRequestToIN", "inReconID:" + inReconID + " current TransId=" + respTransID + " Mismatch");
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"SafaricomPostINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage,"Request and Response Transaction id from Safari com Post paid IN does not match");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);
                }
				_log.info("sendRequestToIN", "result: " + status + " p_inRequestStr: " + p_inRequestStr);
                _requestMap.put("INTERFACE_STATUS", status);
                _requestMap.put("ACCOUNT_STATUS", _responseMap.get("ACCOUNTSTATUS"));
                if (!httpStatus.equals(SafaricomPostI.HTTP_STATUS_200))
                	throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);// invalid
			}
            catch (BTSLBaseException be)
            {
                throw be;
            } 
			catch (Exception e)
            {    	
        		_log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "SafaricomPostINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage, "Exception while getting response from IN :" + e.getMessage());
                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);		             	
            }			
        }
        catch (BTSLBaseException be)
        {
            throw be;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } 
		finally
        {
            try
            {
                if (safComURLConnection != null)
                    safComURLConnection.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception ehile closing Safari com Post paid Connection:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SafaricomPostINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_stage, "Not able to close connection:" + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_stage:" + p_stage + " responseStr:" + responseStr);
        }//end of finally
    }//end of sendRequestToIN
    
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
    		
    		if(_log.isDebugEnabled()) _log.debug("checkInterfaceB4SendingRequest","_interfaceCloser"+_interfaceCloser.printInterfaceCloserVO(_interfaceCloserVO));
    		
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
