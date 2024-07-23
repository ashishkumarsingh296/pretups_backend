/*
 * Copyright(c) 2012, Comviva Technologies Ltd.
 * All Rights Reserved
 * 
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gopal 20/04/2012 Initial Creation
 */
package com.inter.vastrixtg;

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
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.SMSServicePortType;
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.VASTrixRechargeRequestParms;
import com.btsl.pretups.inter.vastrixtg.vastrixtgstub.VASTrixRechargeResponseParms;
import com.btsl.pretups.logging.TransactionLog;
import com.inter.vasth.VASTHI;
import com.inter.vastrixtg.scheduler.NodeManager;
import com.inter.vastrixtg.scheduler.NodeScheduler;
import com.inter.vastrixtg.scheduler.NodeVO;

public class VASTGINHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(VASTGINHandler.class.getName());
    private static VASRequestResponseParser _vasRequestResponseParser = null; // Used
                                                                              // to
                                                                              // generate
                                                                              // reqeusts
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inReconID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _userType = null;
    private String _productCode = null;// Product code to be sent to INH
    private String _cardGroupCode = null;

    static {
        if (_log.isDebugEnabled())
            _log.debug("VASTGINHandler[static]", "Entered");
        try {

            _vasRequestResponseParser = new VASRequestResponseParser();
        } catch (Exception e) {
            _log.error("VASTGINHandler[static]", "While instantiation of VASTGINHandler get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[static]", "", "", "", "While instantiation of VASResponseParser get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("VASTGINHandler[static]", "Exited");
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
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        return;
    }// end of validate

    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        int validityDays = 0;
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");

            _inTXNID = getINTransactionID(_requestMap);
            _requestMap.put("IN_RECON_ID", _inTXNID);
            _requestMap.put("IN_TXN_ID", _inTXNID);

            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            _userType = (String) _requestMap.get("USER_TYPE");
            _productCode = (String) _requestMap.get("CARD_GROUP_SELECTOR");
            _cardGroupCode = (String) _requestMap.get("CARD_GROUP");
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("credit", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters();

            try {
                multFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                if (_log.isDebugEnabled())
                    _log.debug("credit", "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "VASTHINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if (_log.isDebugEnabled())
                _log.debug("credit", "Transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            _requestMap.put("TRANSFER_AMOUNT", amountStr);

            // sending the Re-charge request to IN along with re-charge action
            // defined in VAS interface
            sendRequestToIN(VASTGI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            _requestMap.put("IN_RECHARGE_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));

        } catch (BTSLBaseException be) {

            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }

    }

    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }

    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }

    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {

    }

    private void setInterfaceParameters() throws Exception, BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String url = FileCache.getValue(_interfaceID, "VASTRIX_URL");
            if (InterfaceUtil.isNullString(url)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "VASTRIX_URL is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("VASTRIX_URL", url.trim());

            String connectTimeout = FileCache.getValue(_interfaceID, "CONNECT_TIME_OUT");
            if (InterfaceUtil.isNullString(connectTimeout)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CONNECT_TIME_OUT is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CONNECT_TIME_OUT", connectTimeout.trim());

        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
        }// end of finally

    }

    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered  p_action::" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action + "product code" + _productCode);
        long startTime = 0, endTime = 0, sleepTime, warnTime = 0;
        SMSServicePortType vasClientStub = null;
        NodeScheduler vasNodeScheduler = null;
        NodeVO vasNodeVO = null;
        int retryNumber = 0;
        int readTimeOut = 0;
        VASTGConnector vasServiceConnection = null;
        VASTrixRechargeResponseParms responseObject = null;
        VASTrixRechargeRequestParms vasRequestObject = null;
        try {

            // Get the start time when the request is send to IN.

            vasNodeScheduler = NodeManager.getScheduler(_interfaceID);
            // Get the retry number from the object that is used to retry the
            // getNode in case connection is failed.
            retryNumber = vasNodeScheduler.getRetryNum();
            // check if VASNodeScheduler is null throw exception.Confirm for
            // Error code(INTERFACE_CONNECTION_NULL)if required-It should be new
            // code like ERROR_NODE_FOUND!
            if (vasNodeScheduler == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VAS_WHILE_GETTING_SCHEDULER_OBJECT);

            for (int loop = 1; loop <= retryNumber; loop++) {
                try {
                    vasNodeVO = vasNodeScheduler.getNodeVO(_inReconID);

                    TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "VASTGINHandler[sendRequestToIN]", PretupsI.TXN_LOG_REQTYPE_REQ, "Node information vasNodeVO:" + vasNodeVO, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");
                    _requestMap.put("IN_URL", vasNodeVO.getUrl());
                    // Check if Node is foud or not.Confirm for Error
                    // code(INTERFACE_CONNECTION_NULL)if required-It should be
                    // new code like ERROR_NODE_FOUND!
                    if (vasNodeVO == null)
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_VAS_NODE_DETAIL_NOT_FOUND);

                    warnTime = vasNodeVO.getWarnTime();

                    readTimeOut = vasNodeVO.getReadTimeOut();

                    // Confirm for the service name servlet for the url
                    // consturction whether URL will be specified in INFile or
                    // IP,PORT and ServletName.
                    vasServiceConnection = new VASTGConnector(vasNodeVO, _interfaceID);

                    // break the loop on getting the successfull connection for
                    // the node;
                    vasClientStub = vasServiceConnection.getService();

                    if (vasClientStub == null) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
                        _log.error("sendRequestToIN", "Unable to get Client Object");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    }
                    try {
                        try {
                            startTime = System.currentTimeMillis();
                            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                            switch (p_action) {
                            case VASTGI.ACTION_RECHARGE_CREDIT: {

                                vasRequestObject = _vasRequestResponseParser.generateVASRequest(VASTGI.ACTION_RECHARGE_CREDIT, _requestMap);
                                responseObject = vasClientStub.VASTrixRecharge(vasRequestObject);
                                if (responseObject == null) {
                                    _requestMap.put("RECHARGE_INTERFACE_STATUS", "ERR_RESP");
                                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                                }
                                break;
                            }

                            }
                        } catch (java.rmi.RemoteException re) {
                            re.printStackTrace();
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "RemoteException Error Message:" + re.getMessage());
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
                                    // Continue the retry loop till success;
                                    // Check if the max retry attempt is reached
                                    // raise exception with error code.
                                    _log.error("sendRequestToIN", "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for VAS Soap Stub with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + vasNodeVO.getNodeNumber() + "]");

                                    _log.info("sendRequestToIN", "Setting the Node [" + vasNodeVO.getNodeNumber() + "] as blocked for duration ::" + vasNodeVO.getExpiryDuration() + " miliseconds");
                                    vasNodeVO.incrementBarredCount();

                                    if (loop == retryNumber) {
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
                                        throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                                    }
                                    continue;
                                } else if (re.getMessage().contains("java.net.SocketTimeoutException")) {
                                    re.printStackTrace();
                                    if (re.getMessage().contains("connect")) {
                                        // In case of connection failure
                                        // 1.Decrement the connection counter
                                        // 2.set the Node as blocked
                                        // 3.set the blocked time
                                        // 4.Handle the event with level INFO,
                                        // show the message that Node is blocked
                                        // for some time (expiry time).
                                        // Continue the retry loop till success;
                                        // Check if the max retry attempt is
                                        // reached raise exception with error
                                        // code.
                                        _log.error("sendRequestToIN", "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for VAS Soap Stub with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + vasNodeVO.getNodeNumber() + "]");

                                        _log.info("sendRequestToIN", "Setting the Node [" + vasNodeVO.getNodeNumber() + "] as blocked for duration ::" + vasNodeVO.getExpiryDuration() + " miliseconds");
                                        vasNodeVO.incrementBarredCount();
                                        // vasNodeVO.setBlocked(true);
                                        // vasNodeVO.setBlokedAt(System.currentTimeMillis());

                                        if (loop == retryNumber) {
                                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
                                            throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                                        }
                                        continue;
                                    }
                                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.SocketTimeoutException Message:" + re.getMessage());
                                    _log.error("sendRequestToIN", "RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                } else if (re.getMessage().contains("java.net.SocketException")) {
                                    re.printStackTrace();
                                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.SocketException Message:" + re.getMessage());
                                    _log.error("sendRequestToIN", "RMI java.net.SocketException Error Message :" + re.getMessage());
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                } else
                                    throw new Exception(re);
                            }
                            respCode = requestStr.substring(index + "<ErrorCode>".length(), requestStr.indexOf("</ErrorCode>", index));

                            index = requestStr.indexOf("<ErrorDescription>");
                            String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(), requestStr.indexOf("</ErrorDescription>", index));
                            _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);

                            if (p_action == VASTGI.ACTION_RECHARGE_CREDIT) {

                                _requestMap.put("INTERFACE_STATUS", respCode);
                                _requestMap.put("INTERFACE_DESC", respCodeDesc);
                            }

                            if (respCode.equals(VASTGI.MSISDN_NOT_FOUND)) {
                                _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                            } else {
                                _log.error("sendRequestToIN", "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                            }
                        }

                        catch (Exception e) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Exception Error Message:" + e.getMessage());
                            _log.error("sendRequestToIN", "Exception Error Message :" + e.getMessage());
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }

                        endTime = System.currentTimeMillis();
                        vasNodeVO.resetBarredCount();
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Error Message:" + e.getMessage());
                        _log.error("sendRequestToIN", "Error Message :" + e.getMessage());
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    } finally {
                        if (endTime == 0)
                            endTime = System.currentTimeMillis();
                        _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                        _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
                    }

                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + vasNodeVO.getNodeNumber() + "] created after the attempt number(loop)::" + loop);
                    break;
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
                    throw be;// Confirm should we come out of loop or do another
                             // retry
                }// end of catch-BTSLBaseException
                catch (Exception e) {
                    _log.error("sendRequestToIN", "Exception be::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }// end of catch-Exception
            }
            _responseMap = _vasRequestResponseParser.parseResponseObject(p_action, responseObject);
            // put value of response
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response Map: " + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");
            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            if (endTime - startTime >= warnTime) {
                _log.info("sendRequestToIN", "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "CCWS IP= " + vasNodeVO.getUrl(), "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "VAS IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
            }
            String responseCode = (String) _responseMap.get("RESP_CODE");
            _requestMap.put("INTERFACE_STATUS", responseCode);
            Object[] successList = VASTGI.RESULT_OK.split(",");
            if (!Arrays.asList(successList).contains(responseCode)) {
                _requestMap.put("VAS_ERROR_MSG_REQD", PretupsI.YES);
                if (VASTGI.VAS_INCORRECT_CODE_2.equals(responseCode)) {
                    _log.error("sendRequestToIN", "Invalid response  found with MSISDN::" + _msisdn);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Invalid Response Code from WAP IN :" + responseCode);
                    throw new BTSLBaseException(InterfaceErrorCodesI.VAS_INCORRECT_CODE_2);
                } else {
                    _log.error("sendRequestToIN", "Invalid response code from WAP IN::" + responseCode);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTGINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Invalid Response Code from WAP IN :" + responseCode);
                    throw new BTSLBaseException(InterfaceErrorCodesI.VAS_INCORRECT_CODE);
                }// end of checking the subscriber existance.
            }

        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTGINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            vasClientStub = null;
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action=" + p_action);
        }// end of finally
    }

    protected static String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Entered");
        String userType = (String) p_requestMap.get("USER_TYPE");
        String inTxnId = (String) p_requestMap.get("TRANSACTION_ID");

        if (!InterfaceUtil.isNullString(userType))
            inTxnId = inTxnId + userType;

        p_requestMap.put("IN_RECON_ID", inTxnId);
        p_requestMap.put("IN_TXN_ID", inTxnId);
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "exited");
        return inTxnId;
    }

}
