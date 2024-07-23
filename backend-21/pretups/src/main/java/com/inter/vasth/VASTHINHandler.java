package com.inter.vasth;

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
import com.btsl.pretups.logging.TransactionLog;

public class VASTHINHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(VASTHINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the respose of the request as
                                       // key and value pair.
    private HashMap _responseMap = null;// Contains the request parameter as key
                                        // and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _userType = null;
    private String _referenceID = null;
    private String _productCode = null;// Product code to be sent to INH
    private String _cardGroupCode = null;
    private static VASResponseParser _vasResponseParser = null;

    static {
        if (_log.isDebugEnabled())
            _log.debug("VASTHINHandler[static]", "Entered");
        try {

            _vasResponseParser = new VASResponseParser();
        } catch (Exception e) {
            _log.error("VASTHINHandler[static]", "While instantiation of VASTHINHandler get Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ComverseINHandler[static]", "", "", "", "While instantiation of VASResponseParser get Exception e::" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("VASTHINHandler[static]", "Exited");
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

    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }

    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }

    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }

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
            // defined in Comverse interface
            sendRequestToIN(VASTHI.ACTION_RECHARGE_REQUEST);

            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            _requestMap.put("IN_RECHARGE_TIME", String.valueOf(((Long.valueOf((String) _requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) _requestMap.get("IN_START_TIME"))).longValue())));

        } catch (BTSLBaseException be) {

            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            // if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
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

    private void sendRequestToIN(int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered  p_action::" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action + "product code" + _productCode);
        String responseStr = "";
        VASUrlConnection vasUrlConnection = null;
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        int connectTimeOut = 0;
        int readTimeOut = 0;
        String url = "";
        String inReconID = null;
        int retryCount = 0;
        try {
            _responseMap = new HashMap();
            inReconID = (String) _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            try {
                long startTimeNode = System.currentTimeMillis();
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
                try {
                    warnTime = Long.parseLong(_requestMap.get("WARN_TIMEOUT").toString());
                    // url =
                    // _requestMap.get("VASTRIX_URL").toString()+"?phone="+_msisdn+"&code="+_productCode+"&amount="+_requestMap.get("TRANSFER_AMOUNT").toString()+"&Seller="+_requestMap.get("SENDER_MSISDN").toString()+"&TransactionID="+_inTXNID;
                    // modified on 17 feb 2012 for url change as per the doc
                    // url =
                    // _requestMap.get("VASTRIX_URL").toString()+"?phone="+_msisdn+"&code="+_productCode+"&amount="+_requestMap.get("TRANSFER_AMOUNT").toString()+"&seller="+_requestMap.get("SENDER_MSISDN").toString()+"&transactionid="+_inTXNID;
                    url = _requestMap.get("VASTRIX_URL").toString() + "?phone=" + _msisdn + "&code=" + _productCode + "&amount=" + _cardGroupCode + "&seller=" + _requestMap.get("SENDER_MSISDN").toString() + "&transactionid=" + _inTXNID;
                    String keepAlive = _requestMap.get("KEEP_ALIVE").toString();

                    readTimeOut = Integer.parseInt(FileCache.getValue(_interfaceID, "READ_TIMEOUT_CREDIT"));

                    connectTimeOut = Integer.parseInt(_requestMap.get("CONNECT_TIME_OUT").toString());

                    vasUrlConnection = new VASUrlConnection(url, connectTimeOut, readTimeOut, keepAlive, 0);

                    if (_log.isDebugEnabled())
                        _log.debug("sendRequestToIN", "Connection of _interfaceID [" + _interfaceID + "] for URL [" + url + "] created");
                } catch (BTSLBaseException be) {
                    _log.error("sendRequestToIN", "BTSLBaseException be::" + be.getMessage());
                    throw be;
                } catch (Exception e) {
                    _log.error("sendRequestToIN", "Exception while creating connection e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while getting the connection for VASTRIX IN with INTERFACE_ID=[" + _interfaceID + "]");
                    throw e;
                }

                long totalTimeNode = System.currentTimeMillis() - startTimeNode;

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Total time to find the scheduled Node totalTimeNode: " + totalTimeNode);
            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the scheduled Node for INTERFACE_ID=[" + _interfaceID + "]" + " Exception ::" + e.getMessage());
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                PrintWriter out = vasUrlConnection.getPrintWriter();
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception e::" + e);
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While sending request to Comverse IN IN INTERFACE_ID=[" + _interfaceID + "] Exception::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
            }

            try {
                StringBuffer buffer = new StringBuffer();
                String response = "";
                try {
                    // Get the response from the IN
                    vasUrlConnection.setBufferedReader();
                    BufferedReader in = vasUrlConnection.getBufferedReader();
                    // Reading the response from buffered reader.
                    while ((response = in.readLine()) != null) {
                        buffer.append(response);
                    }
                    endTime = System.currentTimeMillis();
                    if (warnTime <= (endTime - startTime)) {
                        _log.info("sendRequestToIN", "WARN time reaches, startTime::" + startTime + " endTime::" + endTime + " From file cache warnTime::" + warnTime + " time taken (endTime-startTime)::" + (endTime - startTime));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "VASTHINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "VASTRIX IN is taking more time than the warning threshold. Time: " + (endTime - startTime) + "INTERFACE_ID=[" + _interfaceID + "]");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While getting the response from the IN for INTERFACE_ID=[" + _interfaceID + "] " + "Exception=" + e.getMessage());
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

                String httpStatus = vasUrlConnection.getResponseCode();
                _requestMap.put("PROTOCOL_STATUS", httpStatus);
                if (!VASTHI.HTTP_STATUS_SUCCESS.equals(httpStatus))
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                // Check if there is no response, handle the event showing Blank
                // response from Comverse stop further processing.
                if (InterfaceUtil.isNullString(responseStr)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from Comverse IN");
                    _log.error("sendRequestToIN", "NULL response from interface");
                    _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                _responseMap = _vasResponseParser.parseResponse(p_action, responseStr);

                String responseCode = (String) _responseMap.get("RESPONSE_CODE");

                if (!responseCode.equals(VASTHI.VAS_SUCCESS)) {
                    // handle different error response from IN
                    _requestMap.put("VAS_ERROR_MSG_REQD", PretupsI.YES);
                    if (VASTHI.VAS_PROMO_NOT_AVL_SUBSTYPE.equals(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.VAS_PROMO_NOT_AVL_SUBSTYPE);
                    } else if (VASTHI.VAS_PROMO_INCOMPATIBLE.equals(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.VAS_PROMO_INCOMPATIBLE);
                    }

                    else if (VASTHI.VAS_INCORRECT_MSISDN.equals(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }

                    else if (VASTHI.VAS_INCORRECT_CODE.equals(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.VAS_INCORRECT_CODE);
                    } else if (VASTHI.VAS_INCORRECT_AMOUNT.equals(responseCode)) {
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.VAS_INCORRECT_AMOUNT);
                    }
                    /*
                     * else
                     * if(VASTHI.VAS_INVALID_TRANSID_PARAM.equals(responseCode))
                     * {
                     * 
                     * _log.error("sendRequestToIN","Error code received from IN ::"
                     * +responseCode);
                     * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
                     * _interfaceID, EventComponentI.INTERFACES,
                     * EventStatusI.RAISED,
                     * EventLevelI.INFO,"VASTHINHandler[sendRequestToIN]"
                     * ,_referenceID,_msisdn, (String)
                     * _requestMap.get("NETWORK_CODE"
                     * )+" INTERFACE ID = "+_interfaceID
                     * +" Stage = "+p_action,"Error code received from IN "
                     * +responseCode);
                     * throw new BTSLBaseException(InterfaceErrorCodesI.
                     * VAS_INVALID_TRANSID_PARAM);
                     * 
                     * }
                     * else
                     * if(VASTHI.VAS_INVALID_SUBS_PHONE_PARAM.equals(responseCode
                     * ))
                     * {
                     * _log.error("sendRequestToIN","Error code received from IN ::"
                     * +responseCode);
                     * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
                     * _interfaceID, EventComponentI.INTERFACES,
                     * EventStatusI.RAISED,
                     * EventLevelI.INFO,"VASTHINHandler[sendRequestToIN]"
                     * ,_referenceID,_msisdn, (String)
                     * _requestMap.get("NETWORK_CODE"
                     * )+" INTERFACE ID = "+_interfaceID
                     * +" Stage = "+p_action,"Error code received from IN "
                     * +responseCode);
                     * throw new BTSLBaseException(InterfaceErrorCodesI.
                     * VAS_INVALID_TRANSID_PARAM);
                     * }
                     * else
                     * if(VASTHI.VAS_INVALID_CODE_PARAM.equals(responseCode))
                     * {
                     * _log.error("sendRequestToIN","Error code received from IN ::"
                     * +responseCode);
                     * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
                     * _interfaceID, EventComponentI.INTERFACES,
                     * EventStatusI.RAISED,
                     * EventLevelI.INFO,"VASTHINHandler[sendRequestToIN]"
                     * ,_referenceID,_msisdn, (String)
                     * _requestMap.get("NETWORK_CODE"
                     * )+" INTERFACE ID = "+_interfaceID
                     * +" Stage = "+p_action,"Error code received from IN "
                     * +responseCode);
                     * throw new
                     * BTSLBaseException(InterfaceErrorCodesI.VAS_INVALID_CODE_PARAM
                     * );
                     * }
                     * else
                     * if(VASTHI.VAS_INVALID_AMOUNT_PARAM.equals(responseCode))
                     * {
                     * _log.error("sendRequestToIN","Error code received from IN ::"
                     * +responseCode);
                     * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
                     * _interfaceID, EventComponentI.INTERFACES,
                     * EventStatusI.RAISED,
                     * EventLevelI.INFO,"VASTHINHandler[sendRequestToIN]"
                     * ,_referenceID,_msisdn, (String)
                     * _requestMap.get("NETWORK_CODE"
                     * )+" INTERFACE ID = "+_interfaceID
                     * +" Stage = "+p_action,"Error code received from IN "
                     * +responseCode);
                     * throw new BTSLBaseException(InterfaceErrorCodesI.
                     * VAS_INVALID_AMOUNT_PARAM);
                     * }
                     * else
                     * if(VASTHI.VAS_INVALID_SELLER_PARAM.equals(responseCode))
                     * {
                     * _log.error("sendRequestToIN","Error code received from IN ::"
                     * +responseCode);
                     * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
                     * _interfaceID, EventComponentI.INTERFACES,
                     * EventStatusI.RAISED,
                     * EventLevelI.INFO,"VASTHINHandler[sendRequestToIN]"
                     * ,_referenceID,_msisdn, (String)
                     * _requestMap.get("NETWORK_CODE"
                     * )+" INTERFACE ID = "+_interfaceID
                     * +" Stage = "+p_action,"Error code received from IN "
                     * +responseCode);
                     * throw new BTSLBaseException(InterfaceErrorCodesI.
                     * VAS_INVALID_SELLER_PARAM);
                     * }
                     * else if(VASTHI.VAS_UNKNOWN_ERROR.equals(responseCode))
                     * {
                     * _log.error("sendRequestToIN","Error code received from IN ::"
                     * +responseCode);
                     * EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,
                     * _interfaceID, EventComponentI.INTERFACES,
                     * EventStatusI.RAISED,
                     * EventLevelI.INFO,"VASTHINHandler[sendRequestToIN]"
                     * ,_referenceID,_msisdn, (String)
                     * _requestMap.get("NETWORK_CODE"
                     * )+" INTERFACE ID = "+_interfaceID
                     * +" Stage = "+p_action,"Error code received from IN "
                     * +responseCode);
                     * throw new
                     * BTSLBaseException(InterfaceErrorCodesI.VAS_UNKNOWN_ERROR
                     * );
                     * }
                     */
                    else {
                        _requestMap.put("VAS_ERROR_MSG_REQD", PretupsI.NO);
                        _log.error("sendRequestToIN", "Error code received from IN ::" + responseCode);
                        EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.VAS_REQUEST_FAIL);
                    }
                }

                _requestMap.put("INTERFACE_STATUS", responseCode);
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
                if (vasUrlConnection != null)
                    vasUrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "While closing Comverse IN Connection Exception e::" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage() + "INTERFACE_ID=[" + _interfaceID + "]and IP=[" + url + "]");
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting  _interfaceID::" + _interfaceID + " Stage::" + p_action + " responseStr::" + responseStr);
        }

    }

    private void setInterfaceParameters() throws Exception, BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered");
        try {
            String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

            String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String currency = FileCache.getValue(_interfaceID, "CURRENCY");
            if (InterfaceUtil.isNullString(currency)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CURRENCY", currency.trim());

            String url = FileCache.getValue(_interfaceID, "VASTRIX_URL");
            if (InterfaceUtil.isNullString(url)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "VASTRIX_URL is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("VASTRIX_URL", url.trim());

            String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
            if (InterfaceUtil.isNullString(keepAlive)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "KEEP_ALIVE is not defined in IN File ");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("KEEP_ALIVE", keepAlive.trim());

            String connectTimeout = FileCache.getValue(_interfaceID, "CONNECT_TIME_OUT");
            if (InterfaceUtil.isNullString(connectTimeout)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "ComverseINHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CONNECT_TIME_OUT is not defined in IN File ");
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
