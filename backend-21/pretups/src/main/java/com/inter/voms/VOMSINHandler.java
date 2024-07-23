package com.inter.voms;

/*
 * @(#)VOMSINHandler.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Ruwali 04/09/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Controller class for Voucher Management System.
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
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.RequestGatewayVO;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;


public class VOMSINHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(VOMSINHandler.class.getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private static VOMSRequestFormatter _vomsRequestFormatter = null;
    private static VOMSResponseParser _vomsResponseParse = null;
    static {
        if (_log.isDebugEnabled())
            _log.debug("VOMSINHandler[static]", "Entered");
        try {
            _vomsRequestFormatter = new VOMSRequestFormatter();
            _vomsResponseParse = new VOMSResponseParser();
        } catch (Exception e) {
            _log.error("VOMSINHandler[static]", "While instantiation of VOMSRequestFormatter get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[static]", "", "", "", "While instantiation of VOMSRequestFormatter get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("VOMSINHandler[static]", "Exited");
        }
    }

    public VOMSINHandler() {
        super();
    }

    /**
     * getVoucher Method is used for getting the pin and serial number
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void validate(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered " + InterfaceUtil.getPrintMap(p_map));

        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        try {
            _requestMap = p_map;

            _inTXNID = InterfaceUtil.getINTransactionID();

            _requestMap.put("IN_TXN_ID", _inTXNID);

            _interfaceID = (String) _requestMap.get("INTERFACE_ID");

            _msisdn = (String) _requestMap.get("MSISDN");

            String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
            if (InterfaceUtil.isNullString(multFactor)) {
                _log.error("validate", "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR", multFactor);

            try {
                multFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "VOMSINHandler[credit]", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[credit]", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)

            _requestMap.put("MRP", amountStr);

            String type = FileCache.getValue(_interfaceID, "PINTYPE");
            if (InterfaceUtil.isNullString(type)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VOMSINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "TYPE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("TYPE", type.trim());

            setInterfaceParameters();

            String inStr = _vomsRequestFormatter.generateRequest(VOMSI.ACTION_SERIALNO_PIN_DETAILS, _requestMap);

            sendRequestToIN(inStr, VOMSI.ACTION_SERIALNO_PIN_DETAILS);

            String pin = null;
            String serialNO = null;
            String state = null;

            state = (String) _responseMap.get("State");

            if (!InterfaceUtil.isNullString(state)) {
                _requestMap.put("State", state);// Put the interface_status in
                                                // requestMap
            } else {
                _log.error("sendRequestToIN", "State is Invalid");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            pin = (String) _responseMap.get("PIN");

            if (!InterfaceUtil.isNullString(pin)) {
                _requestMap.put("PIN", pin);// Put the interface_status in
                                            // requestMap
            } else {
                _log.error("sendRequestToIN", "Pin is Invalid");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            serialNO = (String) _responseMap.get("SERIAL_NUMBER");

            if (!InterfaceUtil.isNullString(serialNO)) {
                _requestMap.put("SERIAL_NUMBER", serialNO);// Put the
                                                           // interface_status
                                                           // in requestMap
            } else {
                _log.error("sendRequestToIN", "SERIAL NUMBER is Invalid");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            String expiryDate = null;
            expiryDate = (String) _responseMap.get("Expire_Date");

            if (!InterfaceUtil.isNullString(expiryDate)) {
                _requestMap.put("VOUCHER_EXPIRY_DATE", BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(expiryDate, "yyyy-MM-dd"), "yyyyMMdd"));
            } else {
                _log.error("sendRequestToIN", "Expire_Date NUMBER is Invalid");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            String validity = null;
            validity = (String) _responseMap.get("VALIDITY");
            if (!InterfaceUtil.isNullString(validity)) {
                _requestMap.put("VALIDITY", validity);// Put the
                                                      // interface_status in
                                                      // requestMap
            } else {
                _log.error("sendRequestToIN", "VALIDITY NUMBER is Invalid");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            String talkTime = null;
            talkTime = (String) _responseMap.get("TALKTIME");
            if (!InterfaceUtil.isNullString(talkTime)) {
                _requestMap.put("TALK_TIME", talkTime);// Put the
                                                       // interface_status in
                                                       // requestMap
            } else {
                _log.error("sendRequestToIN", "TALKTIME NUMBER is Invalid");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
            // Set the transaction status and other details in map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            
            if (pin.contains(",")){ // it contains more than one PIN
        		_requestMap.put("PAYABLE_AMT", Integer.toString((Integer.parseInt((String)_requestMap.get("INTERFACE_AMOUNT")) * Integer.parseInt((String)_requestMap.get("QUANTITY")))) );
            }
            else{
            	_requestMap.put("PAYABLE_AMT", (String) _requestMap.get("INTERFACE_AMOUNT"));
            }
            _requestMap.put("SERVICE_CLASS", BTSLUtil.NullToString(FileCache.getValue(_interfaceID, "SERVICE_CLASS")));

        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate in =" + _requestMap.get("INTERFACE_ID") + " BTSLBaseException =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate in =" + _requestMap.get("INTERFACE_ID") + " Exception =" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {

            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap=" + _requestMap);
        }
    }

    /**
     * credit Method is used for updating the status to consume in voms_vouches
     * table.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void credit(HashMap p_map) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("credit Entered ", " _requestMap: " + _requestMap);
        try {
            _requestMap = p_map;
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);

            _msisdn = (String) _requestMap.get("MSISDN");

            _interfaceID = (String) _requestMap.get("INTERFACE_ID");

            String type = FileCache.getValue(_interfaceID, "PINROLLTYPE");
            if (InterfaceUtil.isNullString(type)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "TYPE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("TYPE", type.trim());

            setInterfaceParameters();

            String inStr = _vomsRequestFormatter.generateRequest(VOMSI.ACTION_VOUCHER_RET_ROLLBACK, _requestMap);

            sendRequestToIN(inStr, VOMSI.ACTION_VOUCHER_RET_ROLLBACK);

            // Set the transaction status and other details in map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);

        } catch (BTSLBaseException be) {
            _log.error("credit", "BTSLBaseException e:" + be.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ConsumeVoucherINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "BTSLBaseException while updating voucher =" + _requestMap.get("PIN") + " for Interface=" + _requestMap.get("INTERFACE_ID") + " BTSLBaseException e:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ConsumeVoucherINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while updating voucher =" + _requestMap.get("SERIAL_NUMBER") + " status to " + _requestMap.get("UPDATE_STATUS") + " for Interface=" + _requestMap.get("INTERFACE_ID") + " BTSLBaseException e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }

    }

    /**
     * debit Method is used for updating the status to enable in voms_vouches
     * table.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void debit(HashMap p_map) throws BTSLBaseException, Exception {
    }

    /**
     * creditAdjust Method is used to get vouchers on the basis of txnID from
     * voms_vouches table.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {

        try {
            _requestMap = p_map;

            _inTXNID = InterfaceUtil.getINTransactionID();

            _requestMap.put("IN_TXN_ID", _inTXNID);

            _interfaceID = (String) _requestMap.get("INTERFACE_ID");

            setInterfaceParameters();

            String inStr = _vomsRequestFormatter.generateRequest(VOMSI.ACTION_VOUCHER_DETAILS_AGAIN, _requestMap);

            sendRequestToIN(inStr, VOMSI.ACTION_VOUCHER_INFO);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("TALK_TIME", (String) _requestMap.get("INTERFACE_AMOUNT"));
            _requestMap.put("PAYABLE_AMT", (String) _requestMap.get("INTERFACE_AMOUNT"));
            _requestMap.put("VALIDITY", (String) _requestMap.get("INTERFACE_AMOUNT"));
            _requestMap.put("SERIAL_NUMBER", _requestMap.get("SERIAL_NUMBER"));
            _requestMap.put("PIN", _requestMap.get("PIN"));

            _requestMap.put("SERVICE_CLASS", BTSLUtil.NullToString(FileCache.getValue(_interfaceID, "SERVICE_CLASS")));
        } catch (Exception e) {
            _log.errorTrace("creditAdjust", e);
            _log.error("creditAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while updating voucher =" + _requestMap.get("SERIAL_NUMBER") + " status to " + _requestMap.get("UPDATE_STATUS") + " for Interface=" + _requestMap.get("INTERFACE_ID") + " ");
            throw e;
        }

    }

    public void debitAdjust(HashMap p_map) {
    }

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
     * 1.Invoke the getNodeVO method of NodeScheduler class and pass the
     * Transaction Id.
     * 2.If the VO is Null then mark the request as fail and throw exception(New
     * Error code that defines No connection for any Node is available).
     * 3.If the VO is not NULL then pass the Node detail to
     * ComverseUrlConnection class and get connection.
     * 4.After the proccessing the request(may be successful or fail) decrement
     * the connection counter and pass the
     * transaction id that is removed from the transNodeList.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr::" + p_inRequestStr + " p_action::" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
        String responseStr = "";
        VOMSUrlConnection vomsURLConnection = null;
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        int connectTimeOut = 0;
        int readTimeOut = 0;
        String url = "";
        StringBuilder stb = null;

        int retryCount = 0;
        try {
            _responseMap = new HashMap();
            try {
                long startTimeNode = System.currentTimeMillis();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                try {
                    warnTime = Long.parseLong(_requestMap.get("WARN_TIMEOUT").toString());
                    url = _requestMap.get("VOMS_INTERFACE_URL").toString();
                    stb = new StringBuilder(url);
                    stb.append("?REQUEST_GATEWAY_CODE=");
                    stb.append(_requestMap.get("REQUEST_GATEWAY_CODE").toString());
                    stb.append("&REQUEST_GATEWAY_TYPE=");
                    stb.append(_requestMap.get("REQUEST_GATEWAY_TYPE").toString());
                    stb.append("&LOGIN=");
                    stb.append(_requestMap.get("LOGIN").toString());
                    stb.append("&PASSWORD=");
                    stb.append(_requestMap.get("PASSWORD").toString());
                    stb.append("&SOURCE_TYPE=");
                    stb.append(_requestMap.get("SOURCE_TYPE").toString());
                    stb.append("&SERVICE_PORT=");
                    stb.append(_requestMap.get("SERVICE_PORT").toString());
                    url = stb.toString();
                    String keepAlive = _requestMap.get("KEEP_ALIVE").toString();
                    String soapAction = "";

                    soapAction = "RetrieveVoucherDetails";
                    readTimeOut = Integer.parseInt(FileCache.getValue(_interfaceID, "READ_TIMEOUT_VAL"));
                    retryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "RETRY_COUNT_VAL"));

                    connectTimeOut = Integer.parseInt(_requestMap.get("CONNECT_TIME_OUT").toString());
                    long length = p_inRequestStr.length();

                    int i = 0;
                    while (i++ < retryCount) {
                        vomsURLConnection = new VOMSUrlConnection(url, connectTimeOut, readTimeOut, keepAlive, length, soapAction);
                    }
                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Connection of _interfaceID [" + _interfaceID + "] for the Node Number [" + url + "] created");
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                    _log.error("sendRequestToIN", "Exception while creating connection e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for Comverse IN with INTERFACE_ID=[" + _interfaceID + "]");
                    throw e;
                }

                long totalTimeNode = System.currentTimeMillis() - startTimeNode;

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Total time to find the scheduled Node totalTimeNode: " + totalTimeNode);
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]" + " Exception ::" + e.getMessage());
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                PrintWriter out = vomsURLConnection.getPrintWriter();
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e::" + e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to Comverse IN IN INTERFACE_ID=[" + _interfaceID + "] Exception::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
            }

            try {
                StringBuffer buffer = new StringBuffer();
                String response = "";
                try {
                    // Get the response from the IN
                    vomsURLConnection.setBufferedReader();
                    BufferedReader in = vomsURLConnection.getBufferedReader();
                    // Reading the response from buffered reader.
                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    if (warnTime <= (endTime - startTime)) {
                        if (_log.isInfoEnabled())
                            _log.info("sendRequestToIN", "WARN time reaches, startTime::" + startTime + " endTime::" + endTime + " From file cache warnTime::" + warnTime + " time taken (endTime-startTime)::" + (endTime - startTime));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "VOMSINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Comverse IN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the Comverse IN for INTERFACE_ID=[" + _interfaceID + "] " + "Exception=" + e.getMessage());
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + readTimeOut);
                }
                responseStr = buffer.toString();

                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "responseStr::" + responseStr);

                String httpStatus = vomsURLConnection.getResponseCode();

                _requestMap.put("PROTOCOL_STATUS", httpStatus);

                if (!VOMSI.HTTP_STATUS_200.equals(httpStatus))
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                // Check if there is no response, handle the event showing Blank
                // response from Comverse stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from Comverse IN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                _responseMap = _vomsResponseParse.parseResponse(p_action, responseStr);

                String errorCode = (String) _responseMap.get("ErrorCode");

                if (InterfaceUtil.isNullString(errorCode) || !errorCode.equals("0")) {
                    _log.error("sendRequestToIN", "Error code received from IN ::" + errorCode);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + errorCode);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }

                _requestMap.put("INTERFACE_STATUS", errorCode);
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                // Closing the HttpUrl connection
                if (vomsURLConnection != null)
                    vomsURLConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "While closing Comverse IN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and IP=[" + url + "]");
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting  _interfaceID::" + _interfaceID + " Stage::" + p_action + " responseStr::" + responseStr);
        }
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
            String service = (String) _requestMap.get("REQ_SERVICE");
            if (InterfaceUtil.isNullString(service)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Service is not defined ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SERVICE", service.trim());

            String selector = (String) _requestMap.get("CARD_GROUP_SELECTOR");
            if (InterfaceUtil.isNullString(selector)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "Selector is not defined ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SELECTOR", selector.trim());

            String VOMS_INTERFACE_URL = FileCache.getValue(_interfaceID, "VOMS_INTERFACE_URL");
            if (InterfaceUtil.isNullString(VOMS_INTERFACE_URL)) {
                _log.error("setInterfaceParameters", "Value of IP is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "VOMS_INTERFACE_URL is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("VOMS_INTERFACE_URL", VOMS_INTERFACE_URL.trim());

//            String REQUEST_GATEWAY_CODE = (String) _requestMap.get("REQUEST_GATEWAY_CODE");
            String REQUEST_GATEWAY_CODE = "EXTGW";
            if (InterfaceUtil.isNullString(REQUEST_GATEWAY_CODE)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "REQUEST_GATEWAY_CODE is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("REQUEST_GATEWAY_CODE", REQUEST_GATEWAY_CODE.trim());
//            String REQUEST_GATEWAY_TYPE = (String) _requestMap.get("REQUEST_GATEWAY_TYPE");
            String REQUEST_GATEWAY_TYPE = "EXTGW";
            if (InterfaceUtil.isNullString(REQUEST_GATEWAY_TYPE)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VOMSINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "REQUEST_GATEWAY_TYPE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
             _requestMap.put("REQUEST_GATEWAY_TYPE", REQUEST_GATEWAY_TYPE.trim());
           // String LOGIN = (String) _requestMap.get("LOGIN");
             final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(REQUEST_GATEWAY_CODE);
             final RequestGatewayVO requestGatewayVO = messageGatewayVO.getRequestGatewayVO();
             
             String LOGIN=requestGatewayVO.getLoginID();
            if (InterfaceUtil.isNullString(LOGIN)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VOMSINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "LOGIN is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("LOGIN", LOGIN.trim());
          //  String PASSWORD = (String) _requestMap.get("PASSWORD");
            String PASSWORD=BTSLUtil.decryptText(requestGatewayVO.getPassword());
            if (InterfaceUtil.isNullString(PASSWORD)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VOMSINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("PASSWORD", PASSWORD);
//            String SOURCE_TYPE = (String) _requestMap.get("SOURCE_TYPE");
            String SOURCE_TYPE = "XML";
            if (InterfaceUtil.isNullString(SOURCE_TYPE)) {
                _log.error("setInterfaceParameters", "Value of DATASOURCE_URL is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SOURCE_TYPE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SOURCE_TYPE", SOURCE_TYPE.trim());
          //  String SERVICE_PORT = (String) _requestMap.get("SERVICE_PORT");
            String SERVICE_PORT =requestGatewayVO.getServicePort();
            if (InterfaceUtil.isNullString(SERVICE_PORT)) {
                _log.error("setInterfaceParameters", "Value of USER_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SERVICE_PORT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SERVICE_PORT", SERVICE_PORT.trim());

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String connTimeStr = (String) FileCache.getValue(_interfaceID, "CONNECT_TIME_OUT");
            if (InterfaceUtil.isNullString(connTimeStr) || !InterfaceUtil.isNumeric(connTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CONNECT_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CONNECT_TIME_OUT", connTimeStr.trim());

            String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
            if (InterfaceUtil.isNullString(keepAlive)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "KEEP_ALIVE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("KEEP_ALIVE", keepAlive.trim());

            String vtype = (String) _requestMap.get("VTYPE");

            _requestMap.put("VTYPE", vtype);

            _requestMap.put("SUBID", _msisdn);

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
    }// end of setInterfaceParameters
}
