/* 
 * #RadixAirtelINHandler.java
 *
 *------------------------------------------------------------------------------------------------
 *  Name                  Version		 Date            	History
 *-------------------------------------------------------------------------------------------------
 *  Mahindra Comviva       1.0     		04/09/2014         	Initial Creation
 *-------------------------------------------------------------------------------------------------
 *
 * Copyright(c) 2005 Comviva Technologies Ltd.
 *
 */
package com.inter.radix;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.inter.radix.RadixAirtelUrlConnection;
import com.inter.radix.RadixAirtelRequestFormatter;
import com.inter.radix.RadixAirtelI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;


public class RadixAirtelINHandler implements InterfaceHandler{

	private static Log _log = LogFactory.getLog(RadixAirtelINHandler.class.getName());
	private HashMap _requestMap = null;//Contains the respose of the request as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inTXNID=null;//UNodesed to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;
	private static RadixAirtelRequestFormatter _radixAirtelRequestFormatter=null;

	static
	{
		if(_log.isDebugEnabled()) _log.debug("RadixAirtelINHandler[static]","Entered");
		try
		{
			_radixAirtelRequestFormatter = new RadixAirtelRequestFormatter();
		}
		catch(Exception e)
		{
			_log.error("RadixAirtelINHandler[static]","While instantiation of _othersBangladeshRequestFormatter get Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[static]","","", "","While instantiation of _radixAirtelRequestFormatter get Exception e::"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("RadixAirtelINHandler[static]","Exited");
		}
	}

	/**
	 * Implement's the system flow as Validate request is not going on IN System to validate the subscriber on third Party Interface
	 * so All validate request response marked as success for the transaction.
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception 
	 * @author abhilasha
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception
	{
		if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)p_requestMap.get("INTERFACE_ID");
			_msisdn=(String)p_requestMap.get("MSISDN"); 
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");

			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("SERVICE_CLASS", RadixAirtelI.SERVICE_CLASS_ALL);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");

			setInterfaceParameters();

			return ;

		}
		catch (BTSLBaseException be)
		{
			_log.error("validate","BTSLBaseException be="+be.getMessage());
			throw be; 	   	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("validate","Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);
		}		 
	}//end of validate
	/**
	 * Implements the logic that credit the subscriber account on IN.
	 * On the basis of selector code ,subscription IN picked in credit request and activate the VAS request on the basis of selector code
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception  
	 * @author abhilasha
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{

		if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
		_requestMap = p_requestMap;
		try
		{
			// pick the interface id for IN configuration parameters 
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");

			//Third party IN does not need any IN transaction ID or reference ID ,so similar transaction ID put in that case

			_inTXNID=getINTransactionID(_requestMap);	
			_requestMap.put("IN_RECON_ID",_inTXNID);
			_requestMap.put("IN_TXN_ID",_inTXNID);

			//Set the interface parameters into requestMap
			setInterfaceParameters();
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");	
			_requestMap.put("transfer_amount",String.valueOf(InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT")),Double.parseDouble(FileCache.getValue(_interfaceID,"MULT_FACTOR")))));

			// key value of requestMap is formatted into XML string for the validate request.
			String inStr = _radixAirtelRequestFormatter.generateRequest(RadixAirtelI.ACTION_SUBMIT_PROVISION,_requestMap);

			TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(RadixAirtelI.ACTION_SUBMIT_PROVISION),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string::"+inStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action::"+String.valueOf(RadixAirtelI.ACTION_SUBMIT_PROVISION));
			//sending the Re-charge request to IN along with re-charge action defined in CS3MobililI interface
			sendRequestToIN(inStr);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			// set NEW_EXPIRY_DATE into request map
		} 
		catch (BTSLBaseException be)
		{
			p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			_log.error("credit","BTSLBaseException be:"+be.getMessage());    		   		
			//if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
			throw be;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.error("credit", "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
		}


	}//end of credit

	/**
	 *  This is simple post request on Https post based IN
	 * @param	String p_inRequestStr
	 * @param	int p_action
	 * @throws	BTSLBaseException
	 * @author abhilasha
	 */

	private void sendRequestToIN(String p_inRequestStr) throws BTSLBaseException
	{

		if(_log.isDebugEnabled()) _log.debug("sendRequestToIN","Entered p_inRequestStr::"+p_inRequestStr+" p_action::"+RadixAirtelI.ACTION_SUBMIT_PROVISION);
		TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(RadixAirtelI.ACTION_SUBMIT_PROVISION),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string::"+p_inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action::"+String.valueOf(RadixAirtelI.ACTION_SUBMIT_PROVISION));
		String responseStr = "";
		RadixAirtelUrlConnection radixURLConnection = null;
		long startTime=0;
		long endTime=0;
		long warnTime=0;
		int readTimeOut=0;
		int  connectTimeout=0;
		String inReconID=null;
		String statusCode=null;
		int noOfRetries = 0;
		int retryAttempt = 1;
		long delayTime=0;
		try
		{
			inReconID=(String)_requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=_inTXNID;
			warnTime=Long.parseLong((String)_requestMap.get("WARN_TIMEOUT"));
			connectTimeout=Integer.parseInt((String)_requestMap.get("CONN_TIMEOUT"));
			readTimeOut=Integer.parseInt((String)_requestMap.get("READ_TIMEOUT"));
			try {
				delayTime =Long.parseLong(FileCache.getValue(_interfaceID,"DELAY_TIME"));
			} catch (Exception e) {	
				delayTime = 2000;
			}
			try {
				noOfRetries = Integer.parseInt(FileCache.getValue(_interfaceID,"NO_OF_RETRIES"));
			} catch (Exception e) {	
				noOfRetries = 2;
			}
			long startTimeNode = System.currentTimeMillis();

			if(_log.isDebugEnabled())_log.debug("sendRequestToIN","Start time to estabishing the URL connection ::"+startTimeNode+"miliseconds");		

			try
			{  
				radixURLConnection = new RadixAirtelUrlConnection(p_inRequestStr,_interfaceID,connectTimeout,readTimeOut);
			}
			catch(BTSLBaseException be)
			{
				throw be;
			}//end of catch-BTSLBaseException
			catch(Exception e)
			{
				e.printStackTrace();
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}//end of catch-Exception
			long totalTimeNode =System.currentTimeMillis()-startTimeNode;

			if(_log.isDebugEnabled())_log.debug("sendRequestToIN","Total time to find the scheduled Node totalTimeNode: "+totalTimeNode);
			//Send request to IN for Single Node
			try
			{
				PrintWriter out = radixURLConnection.getPrintWriter();
				out.flush();
				startTime=System.currentTimeMillis();
				_requestMap.put("IN_START_TIME",String.valueOf(startTime));
				out.println(p_inRequestStr);
				out.flush();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN","Exception e::"+e);
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
			}
			//getting response from IN for Single Node
			try
			{
				StringBuffer buffer = new StringBuffer();
				String response = "";
				try
				{
					//Get the response from the IN 
					radixURLConnection.setBufferedReader();
					BufferedReader in = radixURLConnection.getBufferedReader();
					//Reading the response from buffered reader.
					while ((response = in.readLine()) != null)
					{
						buffer.append(response);
					}
					endTime=System.currentTimeMillis();
					if(warnTime<=(endTime-startTime))
						_log.info("sendRequestToIN", "WARN time reaches, startTime::"+startTime+" endTime::"+endTime+" From file cache warnTime::"+warnTime+ " time taken (endTime-startTime)::"+(endTime-startTime));

				}
				catch(Exception e)
				{
					e.printStackTrace();
					_log.error("sendRequestToIN","Exception e::"+e.getMessage());
					EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),"While getting the response from the CS3VASBangladeshIN for INTERFACE_ID=["+_interfaceID +"]and "+"Exception="+e.getMessage());
				}//end of catch-Exception
				finally
				{
					if(endTime==0) endTime=System.currentTimeMillis();
					_requestMap.put("IN_END_TIME",String.valueOf(endTime));
					_log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime+" defined read time out is:"+readTimeOut);
				}//end of finally


				responseStr = buffer.toString();
				TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CREDIT",PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+String.valueOf(RadixAirtelI.ACTION_SUBMIT_PROVISION));
				if (_log.isDebugEnabled())_log.debug("sendRequestToIN", "responseStr::" + responseStr);
				String httpStatus = radixURLConnection.getResponseCode();
				_requestMap.put("PROTOCOL_STATUS", httpStatus);

				if (!RadixAirtelI.HTTP_STATUS_200.equals(httpStatus))
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);

				if (InterfaceUtil.isNullString(responseStr))
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID,"Blank response from RadixAirtelIN");
					_log.error("sendRequestToIN", "NULL response from interface");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				}
				else
				{
					if(RadixAirtelI.RESPONSE_QUEUED.equals(responseStr)){

						//check response code from header 
						//0 for queued 
						Map responseMap=radixURLConnection.responseHeader();
						statusCode = responseMap.get("Status-Code").toString().replace("[", "");
						statusCode=statusCode.replace("]", "");

						if(_log.isDebugEnabled())_log.debug("sendRequestToIN","Status Code from Submit Provision Request"+statusCode);
						if(RadixAirtelI.STATUS_QUEUED.equalsIgnoreCase(statusCode))
						{
							Thread.sleep(delayTime);
							String requestId=null;
							String transId=null;
							requestId=responseMap.get("Request-Id").toString().replace("[", "");
							requestId=requestId.replace("]", "");
							_requestMap.put("REQUEST_ID",requestId);
							transId=responseMap.get("Transaction-Id").toString().replace("[", "");
							transId=transId.replace("]", "");
							_requestMap.put("TRANSACTION_ID",transId);

							//For Retrieve of response String call IN method
							String inString=_radixAirtelRequestFormatter.generateRequest(RadixAirtelI.ACTION_RETRIEVE_PROVISION,_requestMap);
							TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(RadixAirtelI.ACTION_RETRIEVE_PROVISION),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string of Retrieve Provision::"+inString,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action::"+String.valueOf(RadixAirtelI.ACTION_RETRIEVE_PROVISION));
							try
							{
								responseMap=retryConnect(inString);
							}
							catch(Exception ex)
							{
								_log.error("retryConnect", "Exception occuerd during retry :"+ex.getMessage());
								throw ex;
							}

							String responseCode1 = responseMap.get("Status-Code").toString().replace("[", "");
							responseCode1=responseCode1.replace("]", "");
							if(responseMap!=null && (RadixAirtelI.STATUS_QUEUED.equalsIgnoreCase(responseCode1)))
							{
								while((RadixAirtelI.STATUS_QUEUED.equalsIgnoreCase(responseMap.get("Status-Code").toString())) && (noOfRetries>=retryAttempt))
								{
									//Transaction logs 
									responseMap=retryConnect(inString);
									retryAttempt++;
								}
								String responseCode = responseMap.get("Status-Code").toString().replace("[", "");
								responseCode=responseCode.replace("]", "");
								_requestMap.put("INTERFACE_STATUS",responseCode);

								if(noOfRetries<retryAttempt)
								{
									if(responseMap!=null && (RadixAirtelI.STATUS_SUCCESS.equalsIgnoreCase(responseCode)))
									{
										_requestMap.put("INTERFACE_STATUS",responseCode);
										_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
									}
									else
									{
										mapError(responseCode);
									}
								}
								else
								{
									if(responseMap!=null && (RadixAirtelI.STATUS_SUCCESS.equalsIgnoreCase(responseCode)))
									{
										_requestMap.put("INTERFACE_STATUS",responseCode);
										_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
									}
									else
									{
										mapError(responseCode);
									}
								}
							}
							else
							{
								if(responseMap!=null && (RadixAirtelI.STATUS_SUCCESS.equalsIgnoreCase(responseCode1)))
								{
									_requestMap.put("INTERFACE_STATUS",responseCode1);
									_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
								}
								else
								{
									mapError(responseMap.get("Status-Code").toString());
								}
							}
						}
						else
						{
							if(responseMap!=null && (RadixAirtelI.STATUS_SUCCESS.equalsIgnoreCase(statusCode)))
							{
								_requestMap.put("INTERFACE_STATUS",statusCode);
								_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
							}
							else
							{
								mapError(statusCode);
							}
						}
					}
				}
			}

			catch(BTSLBaseException be)
			{
				_log.error("credit","BTSLBaseException be:"+be.getMessage());
				_requestMap.put("TRANSACTION_TYPE","CR");
				if((be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
					_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());

				throw be;
			}//end of catch-BTSLBaseException
			catch(Exception e)
			{
				e.printStackTrace();
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}//end of catch-Exception
		}
		catch(BTSLBaseException be)
		{
			_log.error("sendRequestToIN","BTSLBaseException be::"+be.getMessage());
			throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("sendRequestToIN","Exception e::"+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			try
			{
				//Closing the HttpUrl connection
				if (radixURLConnection != null) radixURLConnection.close();

			}
			catch (Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN", "While closing CS3VASBangladeshIN Connection Exception e::" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_SUBMIT_PROVISION, "Not able to close connection:" + e.getMessage()+"INTERFACE_ID=["+_interfaceID +"]and Node Number=[]");
			}
			if (_log.isDebugEnabled())
				_log.debug("sendRequestToIN", "Exiting  _interfaceID::"+_interfaceID+" Stage::"+RadixAirtelI.ACTION_SUBMIT_PROVISION + " responseStr::" + responseStr);
		}//end of finally

	} //end of sendRequestToIN

	/**
	 * This method is used to set the interface parameters into requestMap, these parameters are as bellow
	 * 1.Origin node type.
	 * 2.Origin host type.
	 * @throws Exception
	 */
	private void setInterfaceParameters() throws Exception,BTSLBaseException
	{
		String methodName="setInterfaceParameters";
		if(_log.isDebugEnabled())_log.debug(methodName,"Entered");
		try
		{        	

			String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
			if(InterfaceUtil.isNullString(systemStatusMappingCr))
			{
				_log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());

			String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
			if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
			{
				_log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());

			String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
			{
				_log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());

			String systemStatusMappingCrBck = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
			if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
			{
				_log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());

			String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
			if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
			{
				_log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());

			String warnTimeStr=(String)FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
			if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());

			String connectTimeout=(String)FileCache.getValue(_interfaceID,"CONN_TIMEOUT");
			if(InterfaceUtil.isNullString(connectTimeout)||!InterfaceUtil.isNumeric(connectTimeout))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CONN_TIMEOUT is not defined in IN File or not numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CONN_TIMEOUT",connectTimeout.trim());
			String readTimeOut=FileCache.getValue(_interfaceID,"READ_TIMEOUT");
			if(InterfaceUtil.isNullString(readTimeOut)||!InterfaceUtil.isNumeric(readTimeOut))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "READ_TIMEOUT is not defined in IN File or not numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("READ_TIMEOUT",readTimeOut.trim());

			String authKey = (String)FileCache.getValue(_interfaceID,"AUTH_KEY");
			if(InterfaceUtil.isNullString(authKey))
			{
				_log.error("setInterfaceParameters","Value of Authentication Key is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "AUTH_KEY is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("AUTH_KEY",authKey.trim());

			String accountId = (String)FileCache.getValue(_interfaceID,"ACCOUNT_ID");
			if(InterfaceUtil.isNullString(accountId))
			{
				_log.error("setInterfaceParameters","Value of Account ID is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "ACCOUNT_ID is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("ACCOUNT_ID",accountId.trim());

			String urlSubmitProvision = (String)FileCache.getValue(_interfaceID,"URL_SUBMITPROVISION");
			if(InterfaceUtil.isNullString(urlSubmitProvision))
			{
				_log.error("setInterfaceParameters","Value of URL for Submit Provision request is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "URL_SUBMITPROVISION is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("URL_SUBMITPROVISION",urlSubmitProvision.trim());

			String urlRetrieveProvision = (String)FileCache.getValue(_interfaceID,"URL_RETRIEVEPROVISION");
			if(InterfaceUtil.isNullString(urlRetrieveProvision))
			{
				_log.error("setInterfaceParameters","Value of URL for Retrieve Provision request is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "URL_RETRIEVEPROVISION is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("URL_RETRIEVEPROVISION",urlRetrieveProvision.trim());

			//Put the optional value of ExternalData1.
			String externalData1Required = FileCache.getValue(_interfaceID,"EXTERNAL_DATA1_REQUIRED").trim();
			if (_log.isDebugEnabled())
				_log.debug(methodName,"externalData1Required"+externalData1Required);
			
			if(!InterfaceUtil.isNullString(externalData1Required))
				_requestMap.put("EXTERNALDATA1REQ",externalData1Required);
			
			if(!InterfaceUtil.isNullString(externalData1Required)&& "Y".equals(externalData1Required))
			{
				String externalData1=FileCache.getValue(_interfaceID,"EXTERNAL_DATA1").trim();
				if (_log.isDebugEnabled())
					_log.debug(methodName,"externalData1"+externalData1);
				if(!InterfaceUtil.isNullString(externalData1))
					_requestMap.put("EXTERNAL_DATA1",externalData1.trim());

			}
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
	}//end of setInterfaceParameters

	public Map<String, List<String>> retryConnect(String reqString) throws Exception{
		if (_log.isDebugEnabled())_log.debug("retryConnect", "Entered Request String:" + reqString);
		TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"retryConnect",PretupsI.TXN_LOG_REQTYPE_RES,"Request string:"+reqString,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+String.valueOf(RadixAirtelI.ACTION_SUBMIT_PROVISION));
		RadixAirtelUrlConnection retrieveURLConnection = null;
		String response =null;
		Map responseMap=null;
		int readTimeOut=0;
		int  connectTimeout=0;
		String inReconID=null;
		String statusCode=null;
		long startTime=0;
		long endTime=0;
		long warnTime=0;
		String responseStr = null;
		try
		{
			inReconID=(String)_requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=_inTXNID;
			warnTime=Long.parseLong((String)_requestMap.get("WARN_TIMEOUT"));
			connectTimeout=Integer.parseInt((String)_requestMap.get("CONN_TIMEOUT"));
			readTimeOut=Integer.parseInt((String)_requestMap.get("READ_TIMEOUT"));

			long startTimeNode = System.currentTimeMillis();

			if(_log.isDebugEnabled())_log.debug("retryConnect","Start time to estabishing the URL connection ::"+startTimeNode+"miliseconds");		

			try
			{  
				retrieveURLConnection = new RadixAirtelUrlConnection(reqString,_interfaceID,connectTimeout,readTimeOut);
			}
			catch(BTSLBaseException be)
			{
				throw be;
			}//end of catch-BTSLBaseException
			catch(Exception e)
			{
				e.printStackTrace();
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}//end of catch-Exception
			long totalTimeNode1 =System.currentTimeMillis()-startTimeNode;

			if(_log.isDebugEnabled())_log.debug("retryConnect","Total time to find the scheduled Node totalTimeNode: "+totalTimeNode1);
			//Send request to IN for Single Node
			try
			{
				PrintWriter out = retrieveURLConnection.getPrintWriter();
				startTime=System.currentTimeMillis();
				_requestMap.put("IN_START_TIME",String.valueOf(startTime));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				_log.error("sendRequestToIN","Exception e::"+e);
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
			}
			//getting response from IN for Single Node
			try
			{
				StringBuffer buffer = new StringBuffer();
				try
				{
					//Get the response from the IN 
					retrieveURLConnection.setBufferedReader();
					BufferedReader in = retrieveURLConnection.getBufferedReader();
					//Reading the response from buffered reader.
					while ((response = in.readLine()) != null)
					{
						buffer.append(response);
					}
					endTime=System.currentTimeMillis();
					if(warnTime<=(endTime-startTime))
						_log.info("retryConnect", "WARN time reaches, startTime::"+startTime+" endTime::"+endTime+" From file cache warnTime::"+warnTime+ " time taken (endTime-startTime)::"+(endTime-startTime));

				}
				catch(Exception e)
				{
					e.printStackTrace();
					_log.error("sendRequestToIN","Exception e::"+e.getMessage());
					EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),"While getting the response from the CS3VASBangladeshIN for INTERFACE_ID=["+_interfaceID +"]and "+"Exception="+e.getMessage());
				}//end of catch-Exception

				responseStr = buffer.toString();
				TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),"CREDIT",PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+String.valueOf(RadixAirtelI.ACTION_SUBMIT_PROVISION));
				if (_log.isDebugEnabled())_log.debug("retryConnect", "responseStr::" + responseStr);
				String httpStatus = retrieveURLConnection.getResponseCode();
				_requestMap.put("PROTOCOL_STATUS", httpStatus);
				if(!RadixAirtelI.HTTP_STATUS_200.equals(httpStatus))
					throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);


				if (InterfaceUtil.isNullString(responseStr))
				{
					EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID,"Blank response from RadixAirtelIN");
					_log.error("retryConnect", "NULL response from interface");
					_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
					throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				}

			}
			catch(BTSLBaseException be)
			{
				throw be;
			}//end of catch-BTSLBaseException
			catch(Exception e)
			{
				e.printStackTrace();
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error("retryConnect","BTSLBaseException be::"+be.getMessage());
			throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{
			e.printStackTrace();
			_log.error("retryConnect","Exception e::"+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			try
			{
				//Closing the HttpUrl connection
				if (retrieveURLConnection != null) retrieveURLConnection.close();

			}
			catch (Exception e)
			{
				e.printStackTrace();
				_log.error("retryConnect", "While closing CS3VASBangladeshIN Connection Exception e::" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RadixAirtelINHandler[sendRequestToIN]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_SUBMIT_PROVISION, "Not able to close connection:" + e.getMessage()+"INTERFACE_ID=["+_interfaceID +"]and Node Number=[]");
			}
			if(endTime==0) endTime=System.currentTimeMillis();
			_requestMap.put("IN_END_TIME",String.valueOf(endTime));
			_log.error("retryConnect","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime+" defined read time out is:"+readTimeOut);
			if (_log.isDebugEnabled())
				_log.debug("retryConnect", "Exiting  _interfaceID::"+_interfaceID+" Stage::"+RadixAirtelI.ACTION_SUBMIT_PROVISION + " responseStr::" + responseStr);
		}//end of finally
		responseMap=retrieveURLConnection.responseHeader();
		return responseMap;
	}

	public void mapError(String responsecode) throws BTSLBaseException
	{

		if (_log.isDebugEnabled())_log.debug("mapError", "Entered With Response Code" + responsecode);
		if(RadixAirtelI.STATUS_QUEUED.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Got Queued response for Retrieve Provision Request after max retry count::"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Got Queued response for Retrieve Provision Request after max retry count::");
			throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
		}
		else if(RadixAirtelI.STATUS_MISSING_PARAM.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Missing Parameter"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Missing Parameter");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		else if(RadixAirtelI.STATUS_INSUFFICIENT_FUNDS.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Insufficient funds available"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Insufficient funds available");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		else if(RadixAirtelI.STATUS_ERROR.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Error Response from IN"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Error Response from IN");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}

		else if(RadixAirtelI.STATUS_INVALID_PACKAGEID.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Invalid Package Id"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Invalid Package Id");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}

		else if(RadixAirtelI.STATUS_PACKAGE_CONFLICT.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Package Id Conflict"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Package Id Conflict");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}

		else if(RadixAirtelI.STATUS_INVALID_TXNID.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Invalid Txn Id"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Invalid Txn Id");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		else if(RadixAirtelI.STATUS_INVALID_REQID.equalsIgnoreCase(responsecode))
		{
			_log.error("sendRequestToIN","Invalid Request Id"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = ","Invalid Request Id");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		else
		{
			_log.error("sendRequestToIN","Unknown Error"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = ","Unknown Error");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
	}
	protected static synchronized String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException
	{
		String instanceID=null;
		int MAX_COUNTER=9999;
		int inTxnLength=4;
		//String serviceType=null;
		String userType=null;
		Date mydate =null;
		String minut2Compare=null;
		String dateStr=null;
		String transactionId=null;
		int  _prevMinut=0;
		int _transactionIDCounter=0;
		SimpleDateFormat _sdf = new SimpleDateFormat ("yyMMddHHmm");
		try
		{
			userType = (String)p_requestMap.get("USER_TYPE");
			if("S".equals(userType))
				userType="3";
			else if("R".equals(userType))	
				userType="2";


			instanceID = FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"INSTANCE_ID");
			if(InterfaceUtil.isNullString(instanceID))
			{
				_log.error("validate","Parameter INSTANCE_ID is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3NigeriaINHandler[validate]","","" , (String) p_requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}

			mydate = new Date();	
			dateStr = _sdf.format(mydate);
			minut2Compare = dateStr.substring(8,10);
			int currentMinut=Integer.parseInt(minut2Compare);  

			if(currentMinut !=_prevMinut)
			{
				_transactionIDCounter=1;
				_prevMinut=currentMinut;
			}
			else if(_transactionIDCounter > MAX_COUNTER)
				_transactionIDCounter=1;
			else
				_transactionIDCounter++;

			String txnid =String.valueOf(_transactionIDCounter);

			int length = txnid.length();
			int tmpLength=inTxnLength-length;
			if(length<inTxnLength)
			{
				for(int i=0;i<tmpLength;i++)
					txnid = "0"+txnid;
			}

			transactionId = dateStr+instanceID+txnid+userType;		  	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{			
		}
		if(_log.isDebugEnabled())_log.debug("getINTransactionID","Exiting with IN Transaction Id::"+transactionId);
		return transactionId;		
	}
	/*public void mapError(int responsecode) throws BTSLBaseException
	{
		switch(responsecode)
		{
		case RadixAirtelI.STATUS_QUEUED:
		{
			_log.error("sendRequestToIN","Got Queued response for Retrieve Provision Request after max retry count::"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Got Queued response for Retrieve Provision Request after max retry count::");
			throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
		}
		case RadixAirtelI.STATUS_MISSING_PARAM:
		{
			_log.error("sendRequestToIN","Missing Parameter"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Missing Parameter");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		case RadixAirtelI.STATUS_INSUFFICIENT_FUNDS:

		{
			_log.error("sendRequestToIN","Insufficient funds available"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Insufficient funds available");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}

		case RadixAirtelI.STATUS_ERROR:	

		{
			_log.error("sendRequestToIN","Error Response from IN"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Error Response from IN");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}

		case RadixAirtelI.STATUS_INVALID_PACKAGEID:

		{
			_log.error("sendRequestToIN","Invalid Package Id"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Invalid Package Id");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}

		case RadixAirtelI.STATUS_PACKAGE_CONFLICT:

		{
			_log.error("sendRequestToIN","Package Id Conflict"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Package Id Conflict");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}

		case RadixAirtelI.STATUS_INVALID_TXNID:

		{
			_log.error("sendRequestToIN","Invalid Txn Id"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+RadixAirtelI.ACTION_RETRIEVE_PROVISION,"Invalid Txn Id");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		case RadixAirtelI.STATUS_INVALID_REQID:
		{
			_log.error("sendRequestToIN","Invalid Request Id"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = ","Invalid Request Id");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		default:
		{
			_log.error("sendRequestToIN","Unknown Error"+_msisdn);
			EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"RadixAirtelINHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = ","Unknown Error");
			throw new BTSLBaseException(InterfaceErrorCodesI.DRC_ERROR_RESPONSE);
		}
		}

	}*/
	/**
	 * This method is used to adjust the following
	 * 1.Amount
	 * 2.ValidityDays
	 * 3.GraceDays
	 */
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{


	}//end of creditAdjust

	/**
	 * Implements the logic that debit the subscriber account on IN.
	 * @param	HashMap  p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */
	public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{

	}//end of debitAdjust.

	/**
	 * This method would be used to adjust the validity of the subscriber account at the IN.
	 * @param	HashMap p_requestMap
	 * @throws	BTSLBaseException, Exception
	 */   
	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception 
	{}//end of validityAdjust


}