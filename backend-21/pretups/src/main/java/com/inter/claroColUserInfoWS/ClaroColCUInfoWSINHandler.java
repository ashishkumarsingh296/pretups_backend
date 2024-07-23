/*
 * Created on Nov 11, 2013
 * 
 *  To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroColUserInfoWS;

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
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.inter.claroColUserInfoWS.scheduler.NodeManager;
import com.inter.claroColUserInfoWS.scheduler.NodeScheduler;
import com.inter.claroColUserInfoWS.scheduler.NodeVO;
import com.inter.claroColUserInfoWS.stub.ObtenerDatosDistribuidor;
import com.inter.claroColUserInfoWS.stub.WSConsultaSAP;

/**
 * @(#)ClaroColCUInfoWSINHandler Copyright(c) 2016, Comviva Technologies Ltd.
 *                               All Rights Reserved
 *                               -----------------------------------------------
 *                               -------------
 *                               ------------------------------------- Author
 *                               Date History
 *                               -----------------------------------------------
 *                               -------------
 *                               ------------------------------------- Pankaj
 *                               Sharma Spt 28,2016 Initial Creation
 *                               -----------------------------------------------
 *                               -------------
 *                               ------------------------------------
 */
public class ClaroColCUInfoWSINHandler implements InterfaceHandler {
	private Log log = LogFactory.getLog(ClaroColCUInfoWSINHandler.class.getName());
	private ClaroColCUInfoWSResponseParser parser = null;
	private ClaroColCUInfoWSRequestFormatter _formatter = null;

	private HashMap _requestMap = null;// Contains the request parameter as key
										// and value pair.
	private HashMap _responseMap = null;// Contains the response of the request
										// as key and value pair.
	private String interfaceID = null;// Contains the interfaceID
	private String inReconID = null;// Used to represent the Transaction ID
	private String msisdn = null;// Used to store the MSISDN
	private String referenceID = null;// Used to store the reference of
										// transaction id.
	private SimpleDateFormat sdfCompare = new SimpleDateFormat("ss");
	private static int txnCounter = 1;
	private static int prevSec = 0;
	public static final int IN_TRANSACTION_ID_PAD_LENGTH = 2;

	public ClaroColCUInfoWSINHandler() {
		parser = new ClaroColCUInfoWSResponseParser();
		_formatter = new ClaroColCUInfoWSRequestFormatter();

	}

	@Override
	public void validityAdjust(HashMap pMap) throws BTSLBaseException {
		// auto
	}

	/**
	 * This method is used to validate the subscriber.
	 * 
	 * @param HashMap
	 *            pRequestMap
	 * @return void
	 * @throws BTSLBaseException
	 */
	@Override
	public void validate(HashMap pRequestMap) throws BTSLBaseException {
		final String methodName = "validate";
		if (log.isDebugEnabled())
			log.debug(methodName, "Entered pRequestMap:" + pRequestMap);
		_requestMap = pRequestMap;
		try {
			interfaceID = (String) _requestMap.get("INTERFACE_ID");
			msisdn = (String) _requestMap.get("MSISDN");
			if (!BTSLUtil.isNullString(msisdn)) {
				InterfaceUtil.getFilterMSISDN(interfaceID, msisdn);
			}

			referenceID = (String) _requestMap.get("TRANSACTION_ID");
			inReconID = getINReconID();
			_requestMap.put("IN_TXN_ID", referenceID);

			setInterfaceParameters(ClaroColCUInfoWSI.ACTION_ACCOUNT_DETAILS);

			// sending the AccountInfo request to IN along with validate action
			// defined in interface
			sendRequestToIN(ClaroColCUInfoWSI.ACTION_ACCOUNT_DETAILS);

			// set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("CHANNELUSER_NAME", (String) _responseMap.get("CHANNELUSER_NAME"));
			_requestMap.put("CHANNELUSER_ADDRESS", (String) _responseMap.get("CHANNELUSER_ADDRESS"));
			_requestMap.put("CHANNELUSER_EMAIL", (String) _responseMap.get("CHANNELUSER_EMAIL"));
			_requestMap.put("CHANNELUSER_TELEPHONE", (String) _responseMap.get("CHANNELUSER_TELEPHONE"));
			_requestMap.put("CHANNELUSER_CITY", (String) _responseMap.get("CHANNELUSER_CITY"));
			_requestMap.put("CHANNELUSER_STATE", (String) _responseMap.get("CHANNELUSER_STATE"));
			_requestMap.put("CHANNELUSER_CREDITLIMIT", (String) _responseMap.get("CHANNELUSER_CREDITLIMIT"));
			_requestMap.put("CHANNELUSER_PAYCYCLEPERIOD", (String) _responseMap.get("CHANNELUSER_PAYCYCLEPERIOD"));
			_requestMap.put("CHANNELUSER_PAYPERIOD", (String) _responseMap.get("CHANNELUSER_PAYPERIOD"));

		} catch (BTSLBaseException be) {
			log.errorTrace("BTSLBaseException be ::" + methodName, be);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED,
					EventLevelI.FATAL, "ClaroColCUInfoWSINHandler[validate]", referenceID, msisdn,
					(String) _requestMap.get("NETWORK_CODE"),
					"While validate, get the Base Exception be:" + be.getMessage());
			_requestMap.put("TRANSACTION_STATUS", be.getMessage());
			throw new BTSLBaseException(this, "credit", be.getMessage());
		} catch (Exception e) {
			log.errorTrace("BTSLBaseException be: :" + methodName, e);
			log.error(methodName, "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED,
					EventLevelI.MAJOR, "ClaroColCUInfoWSINHandler[validate]", referenceID,
					msisdn + " INTERFACE ID = " + interfaceID, (String) _requestMap.get("NETWORK_CODE"),
					"While validating the subscriber, get Exception e:" + e.getMessage());
			throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.VALIDATION_ERROR);
		} finally {
			if (log.isDebugEnabled())
				log.debug(methodName, "Exiting with  _requestMap: " + _requestMap);
			if (TransactionLog.getLogger().isDebugEnabled())
				TransactionLog.log(interfaceID, referenceID, msisdn, (String) _requestMap.get("NETWORK_CODE"),
						"ClaroColCUInfoWSINHandler[validate]", "Exiting ",
						" _requestMap string:" + _requestMap.toString(), "", "");
		}
	}

	/**
	 * This method is used to credit the Subscriber
	 */
	@Override
	public void credit(HashMap pRequestMap) throws BTSLBaseException {
		// auto
	}

	/**
	 * 
	 */
	@Override
	public void creditAdjust(HashMap pRequestMap) throws BTSLBaseException {
		// auto
	}

	@Override
	public void debitAdjust(HashMap pRequestMap) throws BTSLBaseException {
		// auto
	}

	/**
	 * This method used to send the request to the IN
	 * 
	 * @param int
	 *            pAction
	 * @throws BTSLBaseException
	 */
	private void sendRequestToIN(int pAction) throws BTSLBaseException {
		final String methodName = "ClaroColCUInfoWSINHandler[sendRequestToIN]";
		if (log.isDebugEnabled())
			log.debug(methodName, " pAction=" + pAction + " _msisdn=" + msisdn);

		String actionLevel = "";
		if (ClaroColCUInfoWSI.ACTION_ACCOUNT_DETAILS == pAction)
			actionLevel = "ACTION_ACCOUNT_DETAILS";

		if (!BTSLUtil.isNullString(msisdn)) {
			InterfaceUtil.getFilterMSISDN(interfaceID, msisdn);
		}
		if (log.isDebugEnabled())
			log.debug(methodName, " pAction=" + actionLevel + " _msisdn=" + msisdn);
		long startTime = 0;
		long endTime = 0;
		long warnTime = 0;
		WSConsultaSAP clientStub = null;
		
		NodeScheduler nodeScheduler = null;
		NodeVO nodeVO = null;
		int retryNumber = 0;
		ClaroColCUInfoWSConnectionManager serviceConnection = null;
		try {
			// Get the start time when the request is send to IN.
			nodeScheduler = NodeManager.getScheduler(interfaceID);
			// Get the retry number from the object that is used to retry the
			// getNode in case connection is failed.
			retryNumber = nodeScheduler.getRetryNum();
			// check if NodeScheduler is null throw exception.Confirm for Error
			// code(INTERFACE_CONNECTION_NULL)if required-It should be new code
			// like ERROR_NODE_FOUND!
			if (nodeScheduler == null)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);
			for (int loop = 1; loop <= retryNumber; loop++) {
				try {
					nodeVO = nodeScheduler.getNodeVO(inReconID);
					TransactionLog.log(interfaceID, referenceID, msisdn, (String) _requestMap.get("NETWORK_CODE"),
							methodName, PretupsI.TXN_LOG_REQTYPE_REQ, "Node information NodeVO:" + nodeVO,
							PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + actionLevel);
					_requestMap.put("IN_URL", nodeVO.getUrl());
					// Check if Node is foud or not.Confirm for Error
					// code(INTERFACE_CONNECTION_NULL)if required-It should be
					// new code like ERROR_NODE_FOUND!
					if (nodeVO == null)
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND);
					warnTime = nodeVO.getWarnTime();
					// Confirm for the service name servlet for the url
					// consturction whether URL will be specified in INFile or
					// IP,PORT and ServletName.
					serviceConnection = new ClaroColCUInfoWSConnectionManager(nodeVO, interfaceID);
					// break the loop on getting the successfull connection for
					clientStub = serviceConnection.getService();
					if (clientStub == null) {
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES,
								EventStatusI.RAISED, EventLevelI.FATAL, methodName,
								"REFERENCE ID = " + referenceID + " MSISDN = " + msisdn,
								"INTERFACE ID = " + interfaceID, "Network code = "
										+ (String) _requestMap.get("NETWORK_CODE") + " Action =" + actionLevel,
								"Unable to get Client Object");
						log.error(methodName, "Unable to get Client Object");
						throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
					}
					try {
						try {
							startTime = System.currentTimeMillis();
							_requestMap.put("IN_START_TIME", String.valueOf(startTime));

							if (ClaroColCUInfoWSI.ACTION_ACCOUNT_DETAILS == pAction) {
								Object responseObj = null;
								Object object = _formatter.generateRequest(ClaroColCUInfoWSI.ACTION_ACCOUNT_DETAILS,
										_requestMap);
								responseObj = clientStub.obtenerDatosDistribuidor((ObtenerDatosDistribuidor) object);
								_requestMap.put("RESPONSE_OBJECT", responseObj);

								// responseObj =
								// clientStub.obtenerDatosDistribuidor(new
								// ObtenerDatosDistribuidor(_requestMap.get("NIT").toString(),_requestMap.get("CODIGO").toString(),_requestMap.get("USUARIO").toString(),_requestMap.get("COUNTRASENIA").toString()));
								// _requestMap.put("RESPONSE_OBJECT",
								// responseObj);
							}

						} catch (java.rmi.RemoteException re) {
							log.errorTrace(" BTSLBaseException be::" + methodName, re);
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES,
									EventStatusI.RAISED, EventLevelI.FATAL, methodName,
									"REFERENCE ID = " + referenceID + "MSISDN = " + msisdn,
									"INTERFACE ID = " + interfaceID, "Network code = "
											+ (String) _requestMap.get("NETWORK_CODE") + " Action =" + actionLevel,
									"RemoteException Error Message:" + re.getMessage());
							String respCode = null;
							// parse error code
							String requestStr = re.getMessage();
							int index = requestStr.indexOf("<ErrorCode>");
							if (index == -1) {
								if (re.getMessage().contains("java.net.ConnectException")) {
									// In case of connection failure
									// 1.Decrement the connection counter
									// 2.set the Node as blocked
									// 3.set the blocked time
									// 4.Handle the event with level INFO, show
									// the message that Node is blocked for some
									// time (expiry time).
									// Check if the max retry attempt is reached
									// raise exception with error code.
									log.error(methodName, "RMI java.net.ConnectException while creating connection re::"
											+ re.getMessage());
									EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,
											EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
											methodName, referenceID, msisdn, (String) _requestMap.get("NETWORK_CODE"),
											"RMI java.net.ConnectException while getting the connection for ClaroColCUInfoWSINHandler Soap Stub with INTERFACE_ID=["
													+ interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber()
													+ "]");

									log.info(methodName,
											"Setting the Node [" + nodeVO.getNodeNumber()
													+ "] as blocked for duration ::" + nodeVO.getExpiryDuration()
													+ " miliseconds");
									nodeVO.incrementBarredCount();
									nodeVO.setBlocked(true);
									nodeVO.setBlokedAt(System.currentTimeMillis());
									if (loop == retryNumber) {
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,
												EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
												methodName, referenceID, msisdn,
												(String) _requestMap.get("NETWORK_CODE"),
												"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
										throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
									}
									continue;
								} else if (re.getMessage().contains("java.net.SocketTimeoutException")) {
									log.errorTrace(" BTSLBaseException be::" + methodName, re);
									if (re.getMessage().contains("connect")) {
										// In case of connection failure
										// 1.Decrement the connection counter
										// 2.set the Node as blocked
										// 3.set the blocked time
										// 4.Handle the event with level INFO,
										// show the message that Node is blocked
										// for some time (expiry time).
										// Check if the max retry attempt is
										// reached raise exception with error
										// code.
										log.error(methodName,
												"RMI java.net.ConnectException while creating connection re::"
														+ re.getMessage());
										EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,
												EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
												methodName, referenceID, msisdn,
												(String) _requestMap.get("NETWORK_CODE"),
												"RMI java.net.ConnectException while getting the connection for ClaroColCUInfoWSINHandler Soap Stub with INTERFACE_ID=["
														+ interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber()
														+ "]");

										log.info(methodName,
												"Setting the Node [" + nodeVO.getNodeNumber()
														+ "] as blocked for duration ::" + nodeVO.getExpiryDuration()
														+ " miliseconds");
										nodeVO.incrementBarredCount();
										nodeVO.setBlocked(true);
										nodeVO.setBlokedAt(System.currentTimeMillis());

										if (loop == retryNumber) {
											EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,
													EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
													methodName, referenceID, msisdn,
													(String) _requestMap.get("NETWORK_CODE"),
													"RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
											throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
										}
										continue;
									}
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES,
											EventStatusI.RAISED, EventLevelI.FATAL, methodName,
											"REFERENCE ID = " + referenceID + "MSISDN = " + msisdn,
											"INTERFACE ID = " + interfaceID,
											"Network code = " + (String) _requestMap.get("NETWORK_CODE") + "Action = "
													+ actionLevel,
											"RMI java.net.SocketTimeoutException Message:" + re.getMessage());
									log.error(methodName,
											"RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								} else if (re.getMessage().contains("java.net.SocketException")) {
									log.errorTrace("BTSLBaseException be:: " + methodName, re);
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES,
											EventStatusI.RAISED, EventLevelI.FATAL, methodName,
											"REFERENCE ID = " + referenceID + "MSISDN = " + msisdn,
											"INTERFACE ID = " + interfaceID,
											"Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action= "
													+ actionLevel,
											"RMI java.net.SocketException Message:" + re.getMessage());
									log.error(methodName,
											"RMI java.net.SocketException Error Message :" + re.getMessage());
									throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
								} else
									throw new Exception(re);
							}
							respCode = requestStr.substring(index + "<ErrorCode>".length(),
									requestStr.indexOf("</ErrorCode>", index));

							index = requestStr.indexOf("<ErrorDescription>");
							String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(),
									requestStr.indexOf("</ErrorDescription>", index));
							log.error(methodName,
									"Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
							_requestMap.put("INTERFACE_STATUS", respCode);
							_requestMap.put("INTERFACE_DESC", respCodeDesc);
							log.error(methodName,
									"Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
							throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

						}

						catch (Exception e) {
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES,
									EventStatusI.RAISED, EventLevelI.FATAL, methodName,
									"REFERENCE ID = " + referenceID + "MSISDN = " + msisdn,
									"INTERFACE ID = " + interfaceID, "Network code = "
											+ (String) _requestMap.get("NETWORK_CODE") + " Action =  " + actionLevel,
									"Exception Error Message:" + e.getMessage());
							log.errorTrace("BTSLBaseException be:: " + methodName, e);
							throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
						} finally {
							endTime = System.currentTimeMillis();
							nodeVO.resetBarredCount();
						}

					} catch (BTSLBaseException be) {
						throw be;
					} catch (Exception e) {
						EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES,
								EventStatusI.RAISED, EventLevelI.FATAL, methodName,
								"REFERENCE ID = " + referenceID + "MSISDN = " + msisdn,
								"INTERFACE ID = " + interfaceID, "Network code = "
										+ (String) _requestMap.get("NETWORK_CODE") + "  Action = " + actionLevel,
								"Error Message:" + e.getMessage());
						log.errorTrace("BTSLBaseException be::  " + methodName, e);
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
					} finally {
						if (endTime == 0)
							endTime = System.currentTimeMillis();
						_requestMap.put("IN_END_TIME", String.valueOf(endTime));
						log.error(methodName,
								"Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
					}

					if (log.isDebugEnabled())
						log.debug(methodName, "Connection of interfaceID [" + interfaceID + "] for the Node Number ["
								+ nodeVO.getNodeNumber() + "] created after the attempt number(loop)::" + loop);
					break;
				} catch (BTSLBaseException be) {
					log.error(methodName, "BTSLBaseException be ::" + be.getMessage());
					throw be;// Confirm should we come out of loop or do another
								// retry
				} // end of catch-BTSLBaseException
				catch (Exception e) {
					log.errorTrace("BTSLBaseException be::" + methodName, e);
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				} // end of catch-Exception
			}
			_responseMap = parser.parseResponse(pAction, _requestMap);
			// put value of response
			TransactionLog.log(interfaceID, referenceID, msisdn, (String) _requestMap.get("NETWORK_CODE"),
					String.valueOf(pAction), PretupsI.TXN_LOG_REQTYPE_REQ, "Response Map: " + _responseMap,
					PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + actionLevel);
			// Difference of start and end time would be compared against the
			// warn time, if request and response takes more time than that of
			// the warn time, an event with level INFO is handled
			if (endTime - startTime >= warnTime) {
				log.info(methodName, "WARN time reaches startTime= " + startTime + " endTime= " + endTime
						+ " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.MAJOR, methodName, "REFERENCE ID = " + referenceID + " MSISDN = " + msisdn,
						"CCWS IP= " + nodeVO.getUrl(),
						"Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel,
						"ClaroColCUInfoWSINHandler IN is taking more time than the warning threshold. Time= "
								+ (endTime - startTime));
			}
			String status = (String) _responseMap.get("INTERFACE_STATUS");
			_requestMap.put("INTERFACE_STATUS", status);

		} catch (BTSLBaseException be) {
			log.error(methodName, "BTSLBaseException be = " + be.getMessage());
			throw be;
		} // end of BTSLBaseException
		catch (Exception e) {
			log.errorTrace("BTSLBaseException be::" + methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED,
					EventLevelI.FATAL, "ClaroAPIINHandler[sendRequestToIN]",
					"REFERENCE ID = " + referenceID + "MSISDN = " + msisdn, "INTERFACE ID = " + interfaceID,
					"Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + pAction,
					"System Exception=" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		} // end of catch-Exception
		finally {
			_requestMap.remove("RESPONSE_OBJECT");
			clientStub = null;
			serviceConnection = null;
			if (log.isDebugEnabled())
				log.debug(methodName, "Exiting pAction=" + pAction);
		} // end of finally
	}

	/**
	 * This method is used to set the parameters from the IN file.
	 * 
	 * @param int
	 *            pAction
	 * @return void
	 * @throws BTSLBaseException
	 */
	public void setInterfaceParameters(int pAction) throws BTSLBaseException {
		final String mathodName = "setInterfaceParameters";
		if (log.isDebugEnabled())
			log.debug(mathodName, "Entered Action =" + pAction);
		try {
			
			String cuExtCode = (String) _requestMap.get("CUEXTCODE");
			if (log.isDebugEnabled())
				log.debug(mathodName, "cuExtCode:" + cuExtCode);
			if (InterfaceUtil.isNullString(cuExtCode)) {
				log.error(mathodName, "cuExtCode  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED,
						EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]", referenceID,
						msisdn + " INTERFACE ID = " + interfaceID, (String) _requestMap.get("NETWORK_CODE"),
						"cuExtCode  is not defined in the INFile");
				throw new BTSLBaseException(this, mathodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			cuExtCode = cuExtCode.trim();
			//_requestMap.put("CODIGO", "MAY1100302");
			_requestMap.put("NIT", cuExtCode);
			 String usuario = FileCache.getValue(interfaceID,"USUARIO");
		    	if(InterfaceUtil.isNullString(usuario))
		    	{
		    	    log.error(mathodName,"Value of USUARIO is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",referenceID,"INTID="+interfaceID+" MSISDN "+msisdn ,"" , "USUARIO is not defined in the INFile.");
		    		throw new BTSLBaseException(this,mathodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    	_requestMap.put("USUARIO",usuario.trim());
		  
		    		String countrasenia = FileCache.getValue(interfaceID,"COUNTRASENIA");
		    	if(InterfaceUtil.isNullString(countrasenia))
		    	{
		    	    log.error(mathodName,"Value of COUNTRASENIA is not defined in the INFile");
		    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ClaroWebServiceINHandler[setInterfaceParameters]",referenceID,"INTID="+interfaceID+" MSISDN "+msisdn ,"", "COUNTRASENIA is not defined in the INFile.");
		    		throw new BTSLBaseException(this,mathodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
		    	}
		    
			_requestMap.put("COUNTRASENIA", countrasenia);

		} // end of try block
		catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.error(mathodName, "Exception e=" + e.getMessage());
			throw e;
		} // end of catch-Exception
		finally {
			if (log.isDebugEnabled())
				log.debug(mathodName, "Exited _requestMap:" + _requestMap);
		} // end of finally
	}

	/**
	 * Method to send cancel request to IN for any ambiguous transaction. This
	 * method also makes reconciliation log entry.
	 * 
	 * @return String.
	 * @throws BTSLBaseException
	 */
	public synchronized String getINReconID() throws BTSLBaseException {
		// This method will be used when we have transID based on database
		// sequence.
		String inTransactionID = "";
		try {
			String secToCompare = null;
			Date mydate = null;

			mydate = new Date();

			secToCompare = sdfCompare.format(mydate);
			int currentSec = Integer.parseInt(secToCompare);

			if (currentSec != prevSec) {
				txnCounter = 1;
				prevSec = currentSec;
			} else if (txnCounter >= 99) {
				txnCounter = 1;
			} else {
				txnCounter++;
			}
			if (txnCounter == 0)
				throw new BTSLBaseException("this", "getINReconID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);

			inTransactionID = BTSLUtil.padZeroesToLeft(String.valueOf(Constants.getProperty("INSTANCE_ID")),
					IN_TRANSACTION_ID_PAD_LENGTH) + currentTimeFormatStringTillSec(mydate)
					+ BTSLUtil.padZeroesToLeft(String.valueOf(txnCounter), IN_TRANSACTION_ID_PAD_LENGTH);
		} catch (Exception e) {
			log.errorTrace("BTSLBaseException be::getINReconID", e);
		} finally {
			_requestMap.put("IN_RECON_ID", inTransactionID);
			_requestMap.put("IN_TXN_ID", inTransactionID);
		}
		return inTransactionID;
	}

	/**
	 * This method is used to parse the date in hhmmss format
	 * 
	 * @param Date
	 *            pDate
	 * @return String.
	 * @throws ParseException
	 */
	public String currentTimeFormatStringTillSec(Date pDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
		return sdf.format(pDate);
	}

}
