/*
 * Created on Jun 10, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodafoneghana.locationservice;

import gh.com.vodafone.locationsvc.svcintfc.LocationService;
import gh.com.vodafone.locationsvc.svcintfc.ServiceRequest;

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
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.inter.vodafoneghana.locationservice.scheduler.NodeManager;
import com.inter.vodafoneghana.locationservice.scheduler.NodeScheduler;
import com.inter.vodafoneghana.locationservice.scheduler.NodeVO;

/**
 * @author Vipan Kumar
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class LSWSINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(LSWSINHandler.class.getName());
    private LSWSRequestFormatter _formatter = null;
    private LSWSResponseParser _parser = null;
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inReconID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.

    public void validityAdjust(HashMap p_map) throws BTSLBaseException, Exception {
    }

    /**
	 * 
	 */
    public LSWSINHandler() {
        _formatter = new LSWSRequestFormatter();
        _parser = new LSWSResponseParser();
    }

    /**
     * @author vipan.kumar
     *         This method is used to validate the subscriber
     * 
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Exit p_requestMap:" + p_requestMap);

    }

    /**
     * This method is used to credit the Subscriber
     */

    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);
    }

    public HashMap locationServiceCredit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        final String methodName = "LSWSINHandler[locationServiceCredit]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap: " + p_requestMap);
        _requestMap = p_requestMap;
        try {
            String multFactor = (String) _requestMap.get("MULT_FACTOR");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");

            if (!BTSLUtil.isNullString(_msisdn)) {
                InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
            }

            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _referenceID);

            setInterfaceParameters(LSWSI.ACTION_LS_VALIDATE);

            sendRequestToIN(LSWSI.ACTION_LS_VALIDATE);

            _requestMap.put("LS_STATUS", InterfaceErrorCodesI.SUCCESS);

            _requestMap.put("LS_INTERFACE_STATUS", _responseMap.get("INTERFACE_STATUS"));
            _requestMap.put("SUBSCRIBER_CELL_ID", _responseMap.get("CELL_ID"));
            _requestMap.put("SUBSCRIBER_SWITCH_ID", _responseMap.get("SWITCH_ID"));
            _requestMap.put("LS_ENQUIRY", "N");
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be:" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While promotion locationServiceCredit, get the BTSL Exception e:" + be.getMessage());
            _requestMap.put("LS_STATUS", be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error(methodName, "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While promotion locationServiceCredit, get the Exception e:" + e.getMessage());
            _requestMap.put("LS_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.AMBIGOUS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited _requestMap=" + _requestMap);
            if (TransactionLog.getLogger().isDebugEnabled())
                TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), methodName, "Exiting ", " _requestMap string:" + _requestMap.toString(), "", "");
        }
        return _requestMap;
    }

    /**
	 * 
	 */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered p_requestMap: " + p_requestMap);
    }

    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap: " + p_requestMap);
    }

    /**
     * This method used to send the request to the IN
     * 
     * @param p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(int p_action) throws BTSLBaseException {
        final String methodName = "LSWSINHandler[sendRequestToIN]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, " p_action=" + p_action + " __msisdn=" + _msisdn);

        String actionLevel = "";
        switch (p_action) {
        case LSWSI.ACTION_LS_VALIDATE: {
            actionLevel = "ACTION_PROMO_CREDIT";
            break;
        }

        }
        if (!BTSLUtil.isNullString(_msisdn)) {
            InterfaceUtil.getFilterMSISDN(_interfaceID, _msisdn);
        }
        if (_log.isDebugEnabled())
            _log.debug(methodName, " p_action=" + actionLevel + " __msisdn=" + _msisdn);
        long startTime = 0, endTime = 0, sleepTime, warnTime = 0;
        LocationService clientStub = null;

        NodeScheduler nodeScheduler = null;
        NodeVO nodeVO = null;
        int retryNumber = 0;
        LSWSConnectionManager serviceConnection = null;
        try {
            nodeScheduler = NodeManager.getScheduler(_interfaceID);
            retryNumber = nodeScheduler.getRetryNum();
            if (nodeScheduler == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_WHILE_GETTING_SCHEDULER_OBJECT);
            for (int loop = 1; loop <= retryNumber; loop++) {
                try {
                    nodeVO = nodeScheduler.getNodeVO(_inReconID);
                    TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), methodName, PretupsI.TXN_LOG_REQTYPE_REQ, "Node information NodeVO:" + nodeVO, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + actionLevel);
                    _requestMap.put("IN_URL", nodeVO.getUrl());
                    // Check if Node is foud or not.Confirm for Error
                    // code(INTERFACE_CONNECTION_NULL)if required-It should be
                    // new code like ERROR_NODE_FOUND!
                    if (nodeVO == null)
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_NODE_DETAIL_NOT_FOUND);
                    warnTime = nodeVO.getWarnTime();
                    serviceConnection = new LSWSConnectionManager(nodeVO, _interfaceID);
                    clientStub = serviceConnection.getService();
                    if (clientStub == null) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Unable to get Client Object");
                        _log.error(methodName, "Unable to get Client Object");
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                    }
                    try {
                        try {
                            startTime = System.currentTimeMillis();
                            _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                            switch (p_action) {
                            case LSWSI.ACTION_LS_VALIDATE: {
                                Object object = _formatter.generateRequest(LSWSI.ACTION_LS_VALIDATE, _requestMap);
                                Object responseObj = null;
                                responseObj = clientStub.requestPosition((ServiceRequest) object);
                                _requestMap.put("RESPONSE_OBJECT", responseObj);
                                break;
                            }
                            }
                        } catch (java.rmi.RemoteException re) {
                            re.printStackTrace();
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RemoteException Error Message:" + re.getMessage());
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
                                    _log.error(methodName, "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for LSDataWS Soap Stub with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "]");

                                    _log.info(methodName, "Setting the Node [" + nodeVO.getNodeNumber() + "] as blocked for duration ::" + nodeVO.getExpiryDuration() + " miliseconds");
                                    nodeVO.incrementBarredCount();
                                    nodeVO.setBlocked(true);
                                    nodeVO.setBlokedAt(System.currentTimeMillis());
                                    if (loop == retryNumber) {
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
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
                                        _log.error(methodName, "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for LSDataWS Soap Stub with INTERFACE_ID=[" + _interfaceID + "]and Node Number=[" + nodeVO.getNodeNumber() + "]");

                                        _log.info(methodName, "Setting the Node [" + nodeVO.getNodeNumber() + "] as blocked for duration ::" + nodeVO.getExpiryDuration() + " miliseconds");
                                        nodeVO.incrementBarredCount();
                                        nodeVO.setBlocked(true);
                                        nodeVO.setBlokedAt(System.currentTimeMillis());

                                        if (loop == retryNumber) {
                                            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, methodName, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "RMI  java.net.ConnectException MAXIMUM SHIFTING OF NODE IS REACHED");
                                            throw new BTSLBaseException(InterfaceErrorCodesI.RETRY_ATTEMPT_FAILED);
                                        }
                                        continue;
                                    }
                                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketTimeoutException Message:" + re.getMessage());
                                    _log.error(methodName, "RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                } else if (re.getMessage().contains("java.net.SocketException")) {
                                    re.printStackTrace();
                                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "RMI java.net.SocketException Message:" + re.getMessage());
                                    _log.error(methodName, "RMI java.net.SocketException Error Message :" + re.getMessage());
                                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                                } else
                                    throw new Exception(re);
                            }
                            respCode = requestStr.substring(index + "<ErrorCode>".length(), requestStr.indexOf("</ErrorCode>", index));

                            index = requestStr.indexOf("<ErrorDescription>");
                            String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(), requestStr.indexOf("</ErrorDescription>", index));
                            _log.error(methodName, "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                            _requestMap.put("INTERFACE_STATUS", respCode);
                            _requestMap.put("INTERFACE_DESC", respCodeDesc);
                            _log.error(methodName, "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

                        } catch (SocketTimeoutException se) {
                            se.printStackTrace();
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "SocketTimeoutException Error Message:" + se.getMessage());
                            _log.error(methodName, "SocketTimeoutException Error Message :" + se.getMessage());
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        } catch (Exception e) {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Exception Error Message:" + e.getMessage());
                            _log.error(methodName, "Exception Error Message :" + e.getMessage());
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        } finally {
                            endTime = System.currentTimeMillis();
                            nodeVO.resetBarredCount();
                        }

                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "Error Message:" + e.getMessage());
                        _log.error(methodName, "Error Message :" + e.getMessage());
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                    } finally {
                        if (endTime == 0)
                            endTime = System.currentTimeMillis();
                        _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                        _log.error(methodName, "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
                    }

                    if (_log.isDebugEnabled())
                        _log.debug(methodName, "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + nodeVO.getNodeNumber() + "] created after the attempt number(loop)::" + loop);
                    break;
                } catch (BTSLBaseException be) {
                    _log.error(methodName, "BTSLBaseException be::" + be.getMessage());
                    throw be;// Confirm should we come out of loop or do another
                             // retry
                }// end of catch-BTSLBaseException
                catch (Exception e) {
                    _log.error(methodName, "Exception be::" + e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }// end of catch-Exception
            }
            _responseMap = _parser.parseResponse(p_action, _requestMap);
            // put value of response
            TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response Map: " + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + actionLevel);
            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            if (endTime - startTime >= warnTime) {
                _log.info(methodName, "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "REFERENCE ID = " + _referenceID + " MSISDN = " + _msisdn, "CCWS IP= " + nodeVO.getUrl(), "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + actionLevel, "LSDataWS IN is taking more time than the warning threshold. Time= " + (endTime - startTime));
            }
            String status = (String) _responseMap.get("INTERFACE_STATUS");
            _requestMap.put("INTERFACE_STATUS", status);

        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error(methodName, "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "LSAPIINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            _requestMap.remove("RESPONSE_OBJECT");
            clientStub = null;
            serviceConnection = null;
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting p_action=" + p_action);
        }// end of finally
    }

    /**
     * @author vipan.kumar
     * @param p_action
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void setInterfaceParameters(int p_action) throws BTSLBaseException, Exception {
        final String methodName = "LSWSINHandler[setInterfaceParameters]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered Action =" + p_action);
        try {
            String lsReplyExp = FileCache.getValue(_interfaceID, "LS_REPLY_EXPECTED");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "LS_REPLY_EXPECTED:" + lsReplyExp);
            if (InterfaceUtil.isNullString(lsReplyExp)) {
                _log.error(methodName, "LS_REPLY_EXPECTED  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LS_REPLY_EXPECTED  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            lsReplyExp = lsReplyExp.trim();
            _requestMap.put("LS_REPLY_EXPECTED", lsReplyExp);

            String lsSource = FileCache.getValue(_interfaceID, "LS_SOURCE");
            if (_log.isDebugEnabled())
                _log.debug(methodName, "LS_SOURCE:" + lsSource);
            if (InterfaceUtil.isNullString(lsSource)) {
                _log.error(methodName, "LS_SOURCE  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LS_SOURCE  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            lsSource = lsSource.trim();
            _requestMap.put("LS_SOURCE", lsSource);

        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error(methodName, e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), e.getMessage());
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited _requestMap:" + _requestMap);
        }// end of finally
    }

    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("ss");
    private static int _txnCounter = 1;
    private static int _prevSec = 0;
    public int IN_TRANSACTION_ID_PAD_LENGTH = 2;

    public synchronized String getINReconID() throws BTSLBaseException {
        // This method will be used when we have transID based on database
        // sequence.
        String inTransactionID = "";
        try {
            String secToCompare = null;
            Date mydate = null;

            mydate = new Date();

            secToCompare = _sdfCompare.format(mydate);
            int currentSec = Integer.parseInt(secToCompare);

            if (currentSec != _prevSec) {
                _txnCounter = 1;
                _prevSec = currentSec;
            } else if (_txnCounter >= 99) {
                _txnCounter = 1;
            } else {
                _txnCounter++;
            }
            if (_txnCounter == 0)
                throw new BTSLBaseException("this", "getINReconID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);

            inTransactionID = BTSLUtil.padZeroesToLeft(String.valueOf(Constants.getProperty("INSTANCE_ID")), IN_TRANSACTION_ID_PAD_LENGTH) + currentTimeFormatStringTillSec(mydate) + BTSLUtil.padZeroesToLeft(String.valueOf(_txnCounter), IN_TRANSACTION_ID_PAD_LENGTH);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _requestMap.put("IN_RECON_ID", inTransactionID);
            _requestMap.put("IN_TXN_ID", inTransactionID);
            return inTransactionID;
        }
    }

    public String currentTimeFormatStringTillSec(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
        String dateString = sdf.format(p_date);
        return dateString;
    }

}
