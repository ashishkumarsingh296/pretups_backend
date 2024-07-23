/*
 * Created on November 6, 2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodaidea.vodafone.ericson;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

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
import com.inter.roam.RoamI;



/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UCIPRoamINHandler implements InterfaceHandler{

	private Hashtable<String,String> valuesHash = new Hashtable<String,String>();
	private static Log _log = LogFactory.getLog(UCIP4xINHandler.class.getName());
	private HashMap _requestMap = null;//Contains the respose of the request as key and value pair.
	private HashMap _responseMap = null;//Contains the request parameter as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inTXNID=null;//UNodesed to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;
	private String  _userType=null;
	private static  SimpleDateFormat _sdf = new SimpleDateFormat ("yyMMddHHmm");
	private static int _transactionIDCounter=0;
	private static int  _prevMinut=0;
	private static UCIP4xRequestFormatter _ucip4xRequestFormatter=null;
	private static UCIP4xResponseParser _ucip4xResponseParser=null;

	String pamimessage = null;


	private boolean _isRetryRequest=false;
	//private NodeCloser _nodeCloser =null;
	private String _interfaceBonusValue=null;
	//private String isGetAccntDetailsRqd=null;//added to validate firstIVRCallflag

	static
	{
		if(_log.isDebugEnabled()) _log.debug("UCIP4xINHandler[static]","Entered");
		try
		{
			_ucip4xRequestFormatter = new UCIP4xRequestFormatter();
			_ucip4xResponseParser=new UCIP4xResponseParser();
		}
		catch(Exception e)
		{
			_log.error("UCIP4xINHandler[static]","While instantiation of UCIP4xRequestFormatter get Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler[static]","","", "","While instantiation of UCIP4xRequestFormatter get Exception e::"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("UCIP4xINHandler[static]","Exited");
		}
	}

	/**
	 * Implements the logic that validate the subscriber and get the subscriber information
	 * from the IN.
	 * @param   HashMap  p_requestMap
	 * @throws  BTSLBaseException, Exception
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception
	{
		final String methodName = "validate";
		if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN");
			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
		
			if("N".equals(validateRequired))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				return ;
			}

			_inTXNID=getINTransactionID(_requestMap);
			_requestMap.put("IN_RECON_ID",_inTXNID);
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			
			setInterfaceParameters();
			String inStr = _ucip4xRequestFormatter.generateRequest(UCIP4xI.ACTION_ACCOUNT_DETAILS_ROAM,_requestMap);
			sendRequestToIN(inStr,UCIP4xI.ACTION_ACCOUNT_DETAILS_ROAM);

			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);

		}
		catch (BTSLBaseException be)
		{
			_log.error(methodName,"BTSLBaseException be="+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName,"Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(methodName,"Exiting with  _requestMap: "+_requestMap);
		}
	}//end of validate
	/**
	 * Implements the logic that credit the subscriber account on IN.
	 * @param   HashMap  p_requestMap
	 * @throws  BTSLBaseException, Exception
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}//end of credit


	/**
	 * This method is used to adjust the following
	 * 1.Amount
	 * 2.ValidityDays
	 * 3.GraceDays
	 */
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}//end of creditAdjust

	/**
	 * Implements the logic that debit the subscriber account on IN.
	 * @param   HashMap  p_requestMap
	 * @throws  BTSLBaseException, Exception
	 */
	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}//end of debitAdjust.


	/**
	 * This method would be used to adjust the validity of the subscriber account at the IN.
	 * @param       HashMap p_requestMap
	 * @throws      BTSLBaseException, Exception
	 */
	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}//end of validityAdjust

	/**
	 * This method is used to send the request to IN and stored the response after parsing.
	 * This method also take care about to handle the errornious satuation to send the alarm and set the error code.
	 * @param	String p_inRequestStr
	 * @param	int p_action
	 * @throws	BTSLBaseException
	 */
	private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException
	{
		if (_log.isDebugEnabled())
			_log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_inRequestStr + " p_action:" + p_action);
		TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string:"+p_inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
		String responseStr = "";
		String result=null;
		UCIP4xUrlConnection roamUrlConnection = null;
		String url=null;
		String recNetworkCode=null;
		long startTime=0;
		try
		{
			recNetworkCode=(String)_requestMap.get("REC_NETWORK_CODE");
			url=FileCache.getValue(_interfaceID, recNetworkCode.trim()+"_URL");
			_responseMap = new HashMap();
			String inReconID=(String)_requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=_inTXNID;
			int readTimeOut ;
			if(RoamI.ACTION_ACCOUNT_INFO == p_action)
			{
				String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_VAL");
				if(readTimeOutStr==null)
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[sendRequestToIN]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE")+" Stage = "+p_action, "Read time out VAL is not defined in INFile");
					throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				_requestMap.put("READ_TIMEOUT_VAL",readTimeOutStr);
				readTimeOut = Integer.parseInt(readTimeOutStr);

				if (_log.isDebugEnabled())
					_log.debug("sendRequestToIN"," READ TIMEOUT VAL "+readTimeOut);
			}
			else
			{
				String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_TOP");
				if(readTimeOutStr==null)
				{
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[sendRequestToIN]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE")+" Stage = "+p_action, "Read time out TOP is not defined in INFile");
					throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				_requestMap.put("READ_TIMEOUT_TOP",readTimeOutStr);
				readTimeOut = Integer.parseInt(readTimeOutStr);

				if (_log.isDebugEnabled())
					_log.debug("sendRequestToIN"," READ TIMEOUT TOP "+readTimeOut);
			}///end of if read timeout

			try
			{
				roamUrlConnection = new UCIP4xUrlConnection(url,Integer.parseInt(FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT")),readTimeOut,FileCache.getValue(_interfaceID, "KEEP_ALIVE"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[sendRequestToIN]", " INTERFACE ID = "+_interfaceID, " Stage = "+p_action, "", "Not able to create connection, getting Exception:" + e.getMessage());
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
			}
			try
			{
				PrintWriter out = roamUrlConnection.getPrintWriter();
				//out.flush();
				startTime=System.currentTimeMillis();
				_requestMap.put("IN_START_TIME",String.valueOf(startTime));
				out.println(p_inRequestStr);
				out.flush();
				//out.close();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"UCIPRoamINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_action,"Exception while sending request to Roam IN");
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
			}

			try
			{
				// Create buffered reader and Read Response from the IN
				StringBuffer buffer = new StringBuffer();
				//String buffer = null;
				String response = "";
				long endTime=0;

				try
				{
					roamUrlConnection.setBufferedReader();
					BufferedReader in = roamUrlConnection.getBufferedReader();

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
//							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"UCIPRoamINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_action,"Roam IN is taking more time than the warning threshold. Time: "+(endTime-startTime));
						}
					}
				} 
				catch (Exception e)
				{
					_log.error("sendRequestToIN", " response form interface is null exception is " + e.getMessage());
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "UCIPRoamINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_action, "Exception while getting response from Roam IN e: "+e.getMessage());
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);										
				}
				finally
				{
					if(endTime==0) endTime=System.currentTimeMillis();
					_requestMap.put("IN_END_TIME",String.valueOf(endTime));
					if(p_action==RoamI.ACTION_ACCOUNT_INFO)
						_log.error("sendRequestToIN","IN_START_TIME="+String.valueOf(startTime)+" IN_END_TIME="+String.valueOf(endTime)+" READ_TIMEOUT_VAL ="+_requestMap.get("READ_TIMEOUT_VAL"));
					else 
						_log.error("sendRequestToIN","IN_START_TIME="+String.valueOf(startTime)+" IN_END_TIME="+String.valueOf(endTime)+" READ_TIMEOUT_TOP="+_requestMap.get("READ_TIMEOUT_TOP"));
				}


				responseStr = buffer.toString();

				if (_log.isDebugEnabled())
					_log.debug("sendRequestToIN", "responseStr:" + responseStr);
				TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," INTERFACE ID = "+_interfaceID+" action="+p_action);
				String httpStatus = roamUrlConnection.getResponseCode();

				_requestMap.put("PROTOCOL_STATUS", httpStatus);

				if (InterfaceUtil.isNullString(responseStr) )
				{                	
					_log.error("sendRequestToIN", " Blank response from Roam IN");
					EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "UCIPRoamINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_action, "Blank response from Roam IN ");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
					//commented code may be used in future to support on line cancel request
					/* if(AlcatelI.ACTION_TXN_CANCEL == p_action)
	                  	_requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.AMBIGOUS);*/
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);						             	
				}

				_responseMap = _ucip4xResponseParser.parseResponse(p_action, responseStr);
				String responseCode=(String) _responseMap.get("status");				
				_requestMap.put("INTERFACE_STATUS",responseCode);
				Object[] successList=RoamI.RESULT_OK.split(",");

				//is result is not ok
				
				if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(responseCode))
				{
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
				}
			
				
				if(!Arrays.asList(successList).contains(responseCode))
				{
					if(RoamI.TRANSACTION_AMBIGUOUS.equals(responseCode))
					{
						_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
						throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
					}
					if(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(responseCode))
					{
						_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
					}
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"UCIPRoamINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
				}
			}
			catch (BTSLBaseException be)
			{
				throw be;
			} 
			catch (Exception e)
			{    	
				_log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
				EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "UCIPRoamINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_action, "Exception while getting response from IN :" + e.getMessage());
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
				//may be used in future to support on line cancel request
				/*if(AlcatelI.ACTION_TXN_CANCEL == p_action)
                  	_requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.AMBIGOUS);*/
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);		             	
			}			
		}
		catch (BTSLBaseException be)
		{
			throw be;
		} 
		catch (Exception e)
		{
			//send alert message(TO BE IMPLEMENTED)
			e.printStackTrace();
			_log.error("sendRequestToIN", "Exception:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_action, "System Exception:" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		} 
		finally
		{
			try
			{
				if (roamUrlConnection != null)
					roamUrlConnection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN", "Exception ehile closing Roam Connection:" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+ " Stage = "+p_action, "Not able to close connection:" + e.getMessage());
			}
			if (_log.isDebugEnabled())
				_log.debug("sendRequestToIN", "Exiting p_action:" + p_action + " responseStr:" + responseStr);
		}//end of finally
	}//end of sendRequestToIN

	/**
	 * This method is used to set the interface parameters into requestMap, these parameters are as bellow
	 * 1.Origin node type.
	 * 2.Origin host type.
	 * @throws Exception
	 */
	private void setInterfaceParameters() throws Exception,BTSLBaseException
	{
		if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered");
		try
		{
			String recNetworkCode=(String)_requestMap.get("REC_NETWORK_CODE");
			_requestMap.put("EXTNWCODE",recNetworkCode.trim());
			//Getting the instance id from the IN file and add to the request map, that would be used to be included in the IN_RECON_ID.
			String instanceID = FileCache.getValue((String)_requestMap.get("INTERFACE_ID"),"INSTANCE_ID");
			if(InterfaceUtil.isNullString(instanceID))
			{
				_log.error("getINReconTxnID","Parameter INSTANCE_ID is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[setInterfaceParameters]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			_requestMap.put("INSTANCE_ID",instanceID.trim());


			String dummyMsisdn = FileCache.getValue(_interfaceID,recNetworkCode.trim()+"_MSISDN");
			if(InterfaceUtil.isNullString(dummyMsisdn))
			{
				_log.error("setInterfaceParameters","Value of dummy msisdn is not defined in the INFile for network :"+recNetworkCode.trim());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "Value of dummy msisdn is not defined in the INFile for network :"+recNetworkCode.trim());
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			_requestMap.put("DUMMYMSISDN",dummyMsisdn.trim());

			String dummyPIN = FileCache.getValue(_interfaceID,recNetworkCode.trim()+"_PIN");
			if(InterfaceUtil.isNullString(dummyMsisdn))
			{
				_log.error("setInterfaceParameters","Value of dummy PIN is not defined in the INFile for network :"+recNetworkCode.trim());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "Value of dummy PIN is not defined in the INFile for network :"+recNetworkCode.trim());
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			_requestMap.put("PIN",dummyPIN.trim());

			String validationDone = FileCache.getValue(_interfaceID,recNetworkCode.trim()+"_VALIDATIONDONE");
			if(InterfaceUtil.isNullString(validationDone))
			{
				_log.error("setInterfaceParameters","Value of validation Done is not defined in the INFile for network :"+recNetworkCode.trim());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "Value of validation Done is not defined in the INFile for network :"+recNetworkCode.trim());
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			_requestMap.put("VALIDATIONDONE",validationDone.trim());

			String recLanguage = FileCache.getValue(_interfaceID,recNetworkCode.trim()+"_LANGUAGE");
			if(InterfaceUtil.isNullString(recLanguage))
			{
				_log.error("setInterfaceParameters","Value of receiver LANGUAGE is not defined in the INFile for network :"+recNetworkCode.trim());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "Value of receiver LANGUAGE is not defined in the INFile for network :"+recNetworkCode.trim());
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			_requestMap.put("LANGUAGE",recLanguage.trim());
			_requestMap.put("EXTREFNUM",_referenceID);

			String addPrefix = FileCache.getValue(_interfaceID,recNetworkCode.trim()+"_MSISDN_ADD_PREFIX");
			if(InterfaceUtil.isNullString(addPrefix))
			{
				_log.error("setInterfaceParameters","Value of addPrefix is not defined in the INFile for network :"+recNetworkCode.trim());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "Value of add prefix is not defined in the INFile for network :"+recNetworkCode.trim());
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			_requestMap.put("MSISDN_ADD_PREFIX",addPrefix.trim());

			String removePrefix = FileCache.getValue(_interfaceID,recNetworkCode.trim()+"_MSISDN_REMOVE_PREFIX");
			if(InterfaceUtil.isNullString(removePrefix))
			{
				_log.error("setInterfaceParameters","Value of removePrefix is not defined in the INFile for network :"+recNetworkCode.trim());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIPRoamINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "Value of removePrefix is not defined in the INFile for network :"+recNetworkCode.trim());
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			_requestMap.put("MSISDN_REMOVE_PREFIX",removePrefix.trim());



		}//end of try block
		catch(BTSLBaseException be)
		{
			_log.error("setInterfaceParameters","BTSLBaseException be::"+be.getMessage());
			throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{ 
			e.printStackTrace();
			_log.error("setInterfaceParameters","Exception e="+e +" Check the NODE_TYPE,HOST_NAME or CURRENCY1 into IN file with _interfaceID::"+_interfaceID);
			EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"UCIPRoamINHandler[sendRequestToIN]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"INTERFACE_ID="+_interfaceID+" Getting exception e="+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			if (_log.isDebugEnabled())_log.debug("setInterfaceParameters", "Exited _requestMap::"+ _requestMap);
		}//end of finally
	}//end of setInterfaceParameters


	protected static synchronized String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException
	{
		final String methodName = "getINTransactionID";

		String transactionId=null;
		String inNetCode=null;
		String netCode=null;
		try
		{
			transactionId = (String)p_requestMap.get("TRANSACTION_ID");
			netCode=(String)p_requestMap.get("NETWORK_CODE");
			inNetCode=FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),netCode);
			if(InterfaceUtil.isNullString(inNetCode))
			{
				_log.error(methodName,"Mapping of Netowrk code " +netCode +" is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","","" , (String) p_requestMap.get("NETWORK_CODE"), "Mapping of Netowrk code " +netCode +" is not defined in the INFile");
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			transactionId=inNetCode+transactionId.substring(3,transactionId.length());   
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
		}
		finally
		{
		}
		return transactionId;
	}




}

