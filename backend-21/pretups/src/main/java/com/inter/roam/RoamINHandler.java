package com.inter.roam;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
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
import com.inter.roam.RoamI;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;

/**
 * @(#)RoamINHandler
 *                   Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                   All Rights Reserved
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Vikasy Feb 19,2009 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 *                   Handler class for the Roam interface
 */
public class RoamINHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(RoamINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the respose of the request as
                                       // key and value pair.
    private HashMap _responseMap = null;// Contains the request parameter as key
                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;

    private static RoamRequestFormatter _roamRequestFormatter = null;

    static {
        if (_log.isDebugEnabled())
            _log.debug("RoamINHandler[static]", "Entered");
        try {
            _roamRequestFormatter = new RoamRequestFormatter();
        } catch (Exception e) {
            _log.error("RoamINHandler[static]", "While instantiation of RoamRequestFormatter get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[static]", "", "", "", "While instantiation of RoamRequestFormatter get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("RoamINHandler[static]", "Exited");
        }
    }

    /**
     * Implements the logic that validate the subscriber and get the subscriber
     * information
     * from the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap ::" + p_requestMap);
        _requestMap = p_requestMap;
        _interfaceID = (String) _requestMap.get("INTERFACE_ID");
        _inTXNID = (String) _requestMap.get("TRANSACTION_ID");
        _referenceID = (String) _requestMap.get("TRANSACTION_ID");
        _msisdn = (String) _requestMap.get("MSISDN");
        _requestMap.put("IN_TXN_ID", _inTXNID);
        String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
        if ("N".equals(validateRequired)) {
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            return;
        }

    }// end of validate

    /**
     * Implements the logic that credit the subscriber account on IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap ::" + p_requestMap);
        _requestMap = p_requestMap;
        String multFactor = null;
        String recNetworkCode = null;
        long requestedAmount = 0;
        int multFactorInt = 0;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _inTXNID = (String) _requestMap.get("TRANSACTION_ID");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            recNetworkCode = (String) _requestMap.get("NETWORK_CODE");

            multFactor = (String) FileCache.getValue(_interfaceID, recNetworkCode + "_MULTIPLICATION_FACTOR");
            _requestMap.put("MULTFACTOR", multFactor);
            if (_log.isDebugEnabled())
                _log.debug("credit", "MULTIPLICATION_FACTOR::" + multFactor);
            try {
                multFactorInt = Integer.parseInt(multFactor.trim());
                // Because all calculation (Cardgroup etc... ) regarding
                // interface amount will be taken care by Receiver side.
                // so requested amount would be send to Receiver
                requestedAmount = Long.parseLong((String) _requestMap.get("REQUESTED_AMOUNT"));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            setInterfaceParameters();

            String amountStr = InterfaceUtil.getDisplayAmount(requestedAmount, multFactorInt);

            String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (_log.isDebugEnabled())
                _log.debug("credit", "From file cache roundFlag::" + roundFlag);
            _requestMap.put("ROUND_FLAG", roundFlag);
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "N";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "RoamINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
            }
            double amountDouble = Double.parseDouble(amountStr);
            // If rounding of amount is allowed, round the amount value and put
            // this value in request map.
            if ("Y".equals(roundFlag.trim())) {
                amountStr = String.valueOf(Math.round(amountDouble));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            // Put the amount in request map with key as transfer_amuont
            _requestMap.put("transfer_amount", amountStr);
            int action = RoamI.ACTION_CREDIT;

            String inStr = _roamRequestFormatter.generateRequest(action, _requestMap);

            // Send the request xml to IN.
            sendRequestToIN(inStr, action);

            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            if (!BTSLUtil.isNullString((String) _responseMap.get("balance"))) {
                // long postBalanceLong =
                // Long.parseLong((String)_responseMap.get("balance"));
                // long postBalanceSystemAmount =
                // InterfaceUtil.getSystemAmount(postBalanceLong,multFactorInt);
                Double postBalanceDouble = Double.parseDouble((String) _responseMap.get("balance"));
                Double postBalanceSystemAmount = (double) InterfaceUtil.getSystemAmount(postBalanceDouble, multFactorInt);
                // Put the balance to the request map after credit
                _requestMap.put("INTERFACE_POST_BALANCE", String.valueOf(postBalanceSystemAmount));
                // Put new expiry date after crediting the subscriber
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("valDate"), "yyyyMMdd"));
                // Put new grace date after creditting the subscriber.
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("graceDate"), "yyyyMMdd"));
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            }
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while credit Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exiting _requestMap::" + _requestMap);
        }// end of finally
    }// end of credit

    /**
     * This method is used to adjust the following
     * 1.Amount
     * 2.ValidityDays
     * 3.GraceDays
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }// end of creditAdjust

    /**
     * Implements the logic that debit the subscriber account on IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {


        if(_log.isDebugEnabled())_log.debug("debitAdjust","Entered p_requestMap ::"+p_requestMap);
        _requestMap = p_requestMap;
        String multFactor=null;
        String recNetworkCode=null;
        long requestedAmount = 0;
        int multFactorInt=0;
        String _dummyTransactionID=null;
        try
        {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_inTXNID=(String)_requestMap.get("TRANSACTION_ID");
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_dummyTransactionID=(String)_requestMap.get("INTERFACE_REFERENCE_ID");
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_msisdn=(String)_requestMap.get("MSISDN");
			_requestMap.put("DUMMY_TRANSACTION_ID",_dummyTransactionID);
			 recNetworkCode=(String)_requestMap.get("NETWORK_CODE");
			 

	            multFactor = (String) FileCache.getValue(_interfaceID, recNetworkCode + "_MULTIPLICATION_FACTOR");
	            _requestMap.put("MULTFACTOR", multFactor);
	            if (_log.isDebugEnabled())
	                _log.debug("credit", "MULTIPLICATION_FACTOR::" + multFactor);
			try
			{
			    multFactorInt=Integer.parseInt(multFactor.trim());
			    requestedAmount =Long.parseLong((String)_requestMap.get("REQUESTED_AMOUNT"));
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Requested Amount[INTERFACE_AMOUNT] is not Numeric");
			    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
		
            setInterfaceParameters();
            
			String amountStr=InterfaceUtil.getDisplayAmount(requestedAmount,multFactorInt);
		
			String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
			if(_log.isDebugEnabled()) _log.debug("credit","From file cache roundFlag::"+roundFlag);
			_requestMap.put("ROUND_FLAG",roundFlag);
			if(InterfaceUtil.isNullString(roundFlag))
			{
			    roundFlag="N";
			    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "RoamINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
			}
			double amountDouble = Double.parseDouble(amountStr);
			//If rounding of amount is allowed, round the amount value and put this value in request map.
			if("Y".equals(roundFlag.trim()))
			{
			    amountStr = String.valueOf(Math.round(amountDouble));
			    _requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
			}
			//Put the amount in request map with key as transfer_amuont
			_requestMap.put("transfer_amount",amountStr);
				int action=RoamI.ACTION_DEBIT;
			
			String inStr = _roamRequestFormatter.generateRequest(action,_requestMap);
			
			//Send the request xml to IN.
			sendRequestToIN(inStr,action);
			
			
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
        }
        catch (BTSLBaseException be)
        {
        	p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
    		_log.error("debitAdjust","BTSLBaseException be:"+be.getMessage());    		   		
   			throw be;
		}//end of catch-BTSLBaseException
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while credit Exception e::"+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("debitAdjust","Exiting _requestMap::"+_requestMap);
        }//end of finally
    	
    }// end of debitAdjust.

    /**
     * This method would be used to adjust the validity of the subscriber
     * account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }// end of validityAdjust

    /**
     * This method is used to send the request to IN and stored the response
     * after parsing.
     * This method also take care about to handle the errornious satuation to
     * send the alarm and set the error code.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_inRequestStr + " p_action:" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
        String responseStr = "";
        String result = null;
        RoamUrlConnection roamUrlConnection = null;
        String url = null;
        String recNetworkCode = null;
        long startTime = 0;
        try {
            recNetworkCode = (String) _requestMap.get("NETWORK_CODE");
            url = FileCache.getValue(_interfaceID, recNetworkCode.trim() + "_URL");
            _responseMap = new HashMap();
            String inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            int readTimeOut;
            if (RoamI.ACTION_ACCOUNT_INFO == p_action) {
                String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_VAL");
                if (readTimeOutStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Read time out VAL is not defined in INFile");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("READ_TIMEOUT_VAL", readTimeOutStr);
                readTimeOut = Integer.parseInt(readTimeOutStr);

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " READ TIMEOUT VAL " + readTimeOut);
            } else {
                String readTimeOutStr = FileCache.getValue(_interfaceID, "READ_TIMEOUT_TOP");
                if (readTimeOutStr == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE") + " Stage = " + p_action, "Read time out TOP is not defined in INFile");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("READ_TIMEOUT_TOP", readTimeOutStr);
                readTimeOut = Integer.parseInt(readTimeOutStr);

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", " READ TIMEOUT TOP " + readTimeOut);
            }// /end of if read timeout

            try {
                roamUrlConnection = new RoamUrlConnection(url, Integer.parseInt(FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT")), readTimeOut, FileCache.getValue(_interfaceID, "KEEP_ALIVE"));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[sendRequestToIN]", " INTERFACE ID = " + _interfaceID, " Stage = " + p_action, "", "Not able to create connection, getting Exception:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }
            try {
                PrintWriter out = roamUrlConnection.getPrintWriter();
                // out.flush();
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
                // out.close();
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while sending request to Roam IN");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            }

            try {
                // Create buffered reader and Read Response from the IN
                StringBuffer buffer = new StringBuffer();
                // String buffer = null;
                String response = "";
                long endTime = 0;

                try {
                    roamUrlConnection.setBufferedReader();
                    BufferedReader in = roamUrlConnection.getBufferedReader();

                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                    if (!InterfaceUtil.isNullString(warnTimeStr)) {
                        long warnTime = Long.parseLong(warnTimeStr);
                        if (endTime - startTime > warnTime) {
                            _log.info("sendRequestToIN", "WARN time reaches startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "RoamINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Roam IN is taking more time than the warning threshold. Time: " + (endTime - startTime));
                        }
                    }
                } catch (Exception e) {
                    _log.error("sendRequestToIN", " response form interface is null exception is " + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "RoamINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while getting response from Roam IN e: " + e.getMessage());
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    if (p_action == RoamI.ACTION_ACCOUNT_INFO)
                        _log.error("sendRequestToIN", "IN_START_TIME=" + String.valueOf(startTime) + " IN_END_TIME=" + String.valueOf(endTime) + " READ_TIMEOUT_VAL =" + _requestMap.get("READ_TIMEOUT_VAL"));
                    else
                        _log.error("sendRequestToIN", "IN_START_TIME=" + String.valueOf(startTime) + " IN_END_TIME=" + String.valueOf(endTime) + " READ_TIMEOUT_TOP=" + _requestMap.get("READ_TIMEOUT_TOP"));
                }

                responseStr = buffer.toString();

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr:" + responseStr);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
                String httpStatus = roamUrlConnection.getResponseCode();

                _requestMap.put("PROTOCOL_STATUS", httpStatus);

                if (InterfaceUtil.isNullString(responseStr)) {
                    _log.error("sendRequestToIN", " Blank response from Roam IN");
                    EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "RoamINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from Roam IN ");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    // commented code may be used in future to support on line
                    // cancel request
                    /*
                     * if(AlcatelI.ACTION_TXN_CANCEL == p_action)
                     * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                     * AMBIGOUS);
                     */
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }

                _responseMap = _roamRequestFormatter.parseResponse(p_action, responseStr);
                String responseCode = (String) _responseMap.get("status");
                _requestMap.put("INTERFACE_STATUS", responseCode);
                String txnid =  (String) _responseMap.get("txnID");
                _requestMap.put("IN_TXN_ID", txnid);
                String errormsg = (String) _responseMap.get("message"); // for
                                                                        // error
                                                                        // code
                                                                        // 9007
                _requestMap.put("ERRORMSG", errormsg); // for error code 9007

                Object[] successList = RoamI.RESULT_OK.split(",");

                // is result is not ok
                if (!Arrays.asList(successList).contains(responseCode)) {
                    if (RoamI.TRANSACTION_AMBIGUOUS.equals(responseCode)) {
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                        throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                    }

                    _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode + ",Error Msg from IN " + errormsg);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "RoamINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                _log.error("sendRequestToIN", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "RoamINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while getting response from IN :" + e.getMessage());
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // may be used in future to support on line cancel request
                /*
                 * if(AlcatelI.ACTION_TXN_CANCEL == p_action)
                 * _requestMap.put("CANCEL_RESP_STATUS",InterfaceErrorCodesI.
                 * AMBIGOUS);
                 */
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            // send alert message(TO BE IMPLEMENTED)
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                if (roamUrlConnection != null)
                    roamUrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception ehile closing Roam Connection:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action:" + p_action + " responseStr:" + responseStr);
        }// end of finally
    }// end of sendRequestToIN

    /**
     * This method is used to set the interface parameters into requestMap,
     * these parameters are as bellow
     * 1.Origin node type.
     * 2.Origin host type.
     * 
     * @throws Exception
     */
    private void setInterfaceParameters() throws Exception, BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            String recNetworkCode = (String) _requestMap.get("NETWORK_CODE");
            _requestMap.put("EXTNWCODE", recNetworkCode.trim());
            // Getting the instance id from the IN file and add to the request
            // map, that would be used to be included in the IN_RECON_ID.
            String instanceID = FileCache.getValue((String) _requestMap.get("INTERFACE_ID"), "INSTANCE_ID");
            if (InterfaceUtil.isNullString(instanceID)) {
                _log.error("getINReconTxnID", "Parameter INSTANCE_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[setInterfaceParameters]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _requestMap.put("INSTANCE_ID", instanceID.trim());

            String dummyMsisdn = FileCache.getValue(_interfaceID, recNetworkCode.trim() + "_MSISDN");
            if (InterfaceUtil.isNullString(dummyMsisdn)) {
                _log.error("setInterfaceParameters", "Value of dummy msisdn is not defined in the INFile for network :" + recNetworkCode.trim());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Value of dummy msisdn is not defined in the INFile for network :" + recNetworkCode.trim());
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("DUMMYMSISDN", dummyMsisdn.trim());

            String dummyPIN = FileCache.getValue(_interfaceID, recNetworkCode.trim() + "_PIN");
            if (InterfaceUtil.isNullString(dummyMsisdn)) {
                _log.error("setInterfaceParameters", "Value of dummy PIN is not defined in the INFile for network :" + recNetworkCode.trim());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Value of dummy PIN is not defined in the INFile for network :" + recNetworkCode.trim());
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("PIN", dummyPIN.trim());

            String validationDone = FileCache.getValue(_interfaceID, recNetworkCode.trim() + "_VALIDATIONDONE");
            if (InterfaceUtil.isNullString(validationDone)) {
                _log.error("setInterfaceParameters", "Value of validation Done is not defined in the INFile for network :" + recNetworkCode.trim());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Value of validation Done is not defined in the INFile for network :" + recNetworkCode.trim());
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("VALIDATIONDONE", validationDone.trim());

            String recLanguage = FileCache.getValue(_interfaceID, recNetworkCode.trim() + "_LANGUAGE");
            if (InterfaceUtil.isNullString(recLanguage)) {
                _log.error("setInterfaceParameters", "Value of receiver LANGUAGE is not defined in the INFile for network :" + recNetworkCode.trim());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Value of receiver LANGUAGE is not defined in the INFile for network :" + recNetworkCode.trim());
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("LANGUAGE", recLanguage.trim());
            _requestMap.put("EXTREFNUM", _referenceID);

            String addPrefix = FileCache.getValue(_interfaceID, recNetworkCode.trim() + "_MSISDN_ADD_PREFIX");
            if (InterfaceUtil.isNullString(addPrefix)) {
                _log.error("setInterfaceParameters", "Value of addPrefix is not defined in the INFile for network :" + recNetworkCode.trim());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Value of add prefix is not defined in the INFile for network :" + recNetworkCode.trim());
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("MSISDN_ADD_PREFIX", addPrefix.trim());

            String removePrefix = FileCache.getValue(_interfaceID, recNetworkCode.trim() + "_MSISDN_REMOVE_PREFIX");
            if (InterfaceUtil.isNullString(removePrefix)) {
                _log.error("setInterfaceParameters", "Value of removePrefix is not defined in the INFile for network :" + recNetworkCode.trim());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "RoamINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Value of removePrefix is not defined in the INFile for network :" + recNetworkCode.trim());
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("MSISDN_REMOVE_PREFIX", removePrefix.trim());

        }// end of try block
        catch (BTSLBaseException be) {
            _log.error("setInterfaceParameters", "BTSLBaseException be::" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setInterfaceParameters", "Exception e=" + e + " Check the NODE_TYPE,HOST_NAME or CURRENCY1 into IN file with _interfaceID::" + _interfaceID);
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "RoamINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_ID=" + _interfaceID + " Getting exception e=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap::" + _requestMap);
        }// end of finally
    }// end of setInterfaceParameters

}
