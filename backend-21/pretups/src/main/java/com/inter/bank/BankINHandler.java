package com.inter.bank;

/**
 * @(#)BankINHandler.java
 *                        Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        vikas c yadav Nov 5,2007 Initial Creation
 * 
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 *                        Handler class for the interface
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

public class BankINHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog("BankINHandler".getClass().getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private static BankINRequestFormatter _formatter = null;
    private String _msisdn = null;
    private String _referenceID = null;
    private String _inTXNID = null;
    private String _interfaceID = null;
    private String _userType = null;
    private String _reqService = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private String _interfaceLiveStatus = null;
    private boolean _isSameRequest = false;
    private String _interfaceClosureSupport = null;

    // Static block that would be responsible to create an instance of Request
    // Formatter class
    static {
        if (_log.isDebugEnabled())
            _log.debug("BankINHandler[static block]", "Entered");
        // Creating an instance of BankINRequestFormatter.
        _formatter = new BankINRequestFormatter();
        if (_log.isDebugEnabled())
            _log.debug("BankINHandler[static block]", "Exited");
    }

    /**
     * validate Method is used for getting the account information of user
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_map) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_map:" + p_map);
        _requestMap = p_map;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _msisdn = (String) _requestMap.get("MSISDN");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            String userTypeValue = (String) FileCache.getValue(_interfaceID, "USER_TYPE");
            // Fetch value of VALIDATION key from IN File. (this is used to
            // ensure that validation will be done on IN or not)
            // If validation of subscriber is not required set the SUCCESS code
            // into request map and return.
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            // String validateRequired="Y";
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
        }// try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate");
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug(this, "validate", "Exited _requestMap=" + _requestMap);
        }// finally
    }// end of validate

    /**
     * This method is responsible for the credit of subscriber account.
     * 
     * @param HashMap
     *            p_map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_map:" + p_map);
    }

    /**
     * debitAdjust Method is used for debit.
     * 
     * @param p_map
     *            HashMap
     * @throws Exception
     */
    public void debitAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_map:" + p_map);
        try {
            _requestMap = p_map;
            String multplicaionFactor = null;
            String bankPIN = null;
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");// get
                                                                    // intreface
                                                                    // id form
                                                                    // request
                                                                    // map
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();// Check Interface Status
                                                 // before sending request to
                                                 // Bank

            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            // _inTXNID=_formatter.getINTransactionID();//to be discussed
            _inTXNID = _referenceID;
            _requestMap.put("IN_TXN_ID", _inTXNID);// it can set as _referenceID
            setInterfaceParameters(_interfaceID);
            bankPIN = (String) _requestMap.get("BANK_PIN");
            if (InterfaceUtil.isNullString(bankPIN)) {
                _log.error("debitAdjust", "BANK_PIN is null in the request");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[debitAdjust]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "BANK_PIN is null in the request.");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            String paymentMethod = (String) _requestMap.get("TAS_ORIGIN_ST_CODE");
            String tasOriginSTCode = (String) FileCache.getValue(_interfaceID, paymentMethod);
            if (InterfaceUtil.isNullString(tasOriginSTCode)) {
                _log.error("debitAdjust", "Value of TAS_ORIGIN_ST_CODE is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[debitAdjust]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "tasOriginSTCode is not defined in IN file");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.ERROR_PARAMETER_MISSING);
            }
            _requestMap.put("TAS_ORIGIN_ST_CODE", tasOriginSTCode.trim());
            _requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            multplicaionFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multplicaionFactor)) {
                _log.error("credit", "multFactor:" + multplicaionFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            // Get Round flag from IN file. If it is null, put it as .Y., in
            // _requestMap
            String roundFlag = null;
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "Y";
                _requestMap.put("ROUND_FLAG", roundFlag);
            }
            // Get INTERFACE_AMOUNT from requestMap and change it into transfer
            // amount. Put transfer_amount into requestMap.
            double rerquestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(rerquestedAmtDouble, Double.parseDouble(multplicaionFactor));
            String amountStr = null;
            if ("Y".equals(roundFlag)) {
                amountStr = String.valueOf(Math.round(inAmount));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }
            // Set the rounded amount into request map with key as
            // transfer_amount.
            _requestMap.put("transfer_amount", amountStr);
            // Generate request string by calling generateRequest(action, map)
            // of BankINRequestFormatter
            String inStr = _formatter.generateRequest(BankINI.ACTION_IMMEDIATE_DEBIT, _requestMap);
            // sending the credit request to IN
            sendRequestToIN(inStr, BankINI.ACTION_IMMEDIATE_DEBIT);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
        } catch (BTSLBaseException be) {
            p_map.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
                _requestMap.put("TRANSACTION_TYPE", "DR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("debitAdjust", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }// end of finally
    }

    /**
         * 
         */
    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
    }// end of creditAdjust

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
     * This method is responsible to send the request to IN.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered p_inRequestStr:" + p_inRequestStr + " p_action:" + p_action);
        TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, " INTERFACE_ID:" + _interfaceID + " Request string:" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "p_action=" + p_action);
        String responseStr = "";
        BankINUrlConnection bankINUrlConnection = null;
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        int topUpTimeOut = 0;
        try {
            _responseMap = new HashMap();

            try {
                String topUpTimeOutStr = (String) FileCache.getValue(_interfaceID, "READ_TIMEOUT_TOP");
                // Handle the event if the Multiplication factor is not defined
                // to the INFile
                if (InterfaceUtil.isNullString(topUpTimeOutStr)) {
                    _log.error("sendRequestToIN", "top Up Time Out Str:" + topUpTimeOutStr);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "top up Time Out is not defined in IN File");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                topUpTimeOut = Integer.parseInt(topUpTimeOutStr);
                String url = (String) FileCache.getValue(_interfaceID, "URL");
                if (InterfaceUtil.isNullString(url)) {
                    _log.error("sendRequestToIN", "url:" + url);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "url is not defined in IN File");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                // Get READ_TIMEOUT_VAL , READ_TIMEOUT_TOP from IN file
                // CONNECT TIME OUT and KEEP_ALIVE
                String connectTimeOut = (String) FileCache.getValue(_interfaceID, "CONNECT_TIME_OUT");
                if (InterfaceUtil.isNullString(connectTimeOut)) {
                    _log.error("sendRequestToIN", "connectTimeOut" + connectTimeOut);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "connectTimeOut is not defined in IN File");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                int ctOut = Integer.parseInt(connectTimeOut);
                String keepalive = (String) FileCache.getValue(_interfaceID, "KEEP_ALIVE");
                if (InterfaceUtil.isNullString(keepalive)) {
                    _log.error("sendRequestToIN", "keepalive" + keepalive);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "keepalive is not defined in IN File");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }

                String baseAuth = (String) FileCache.getValue(_interfaceID, "BASIC_AUTH_VAL");
                if (InterfaceUtil.isNullString(baseAuth)) {
                    _log.error("sendRequestToIN", "baseAuth" + baseAuth);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "baseAuth is not defined in IN File");
                    throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                _requestMap.put("BASIC_AUTH_VAL", baseAuth);
                bankINUrlConnection = new BankINUrlConnection(url, ctOut, topUpTimeOut, keepalive, baseAuth);
            } catch (BTSLBaseException be) {
                throw be;
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Error while getting the url connection  for INTERFACE_ID=[" + _interfaceID + "]" + " Exception ::" + e.getMessage() + "action :" + p_action);
                _log.error("sendRequestToIN", "Exception e::" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }// end of catch-Exception

            try {
                // In creditAdjust (sender credit back )don't check interface
                // status, simply send the request to IN.
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                    _isSameRequest = true;
                    checkInterfaceB4SendingRequest();
                }
                // After the successful connection creation get a print writer
                // object from
                // the BankINUrlConnection class.
                PrintWriter out = bankINUrlConnection.getPrintWriter();
                out.flush();
                // Post the request string to the connection out put stream and
                // Store the time when request is send to IN under the key
                // IN_START_TIME into requestMap.
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(p_inRequestStr);
                out.flush();
            }
            /*
             * catch(BTSLBaseException be)
             * {
             * throw be;
             * }
             */
            // While writing the request to connections out put steam if any
            // error occurs does the following.
            catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequest", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Action = " + p_action, "Exception while sending request to Bank IN" + " action :" + p_action);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
            }
            // Create buffered reader and Read Response from the IN
            StringBuffer buffer = new StringBuffer();
            String response = "";
            try {
                // After sending the request to IN set the Buffered Reader
                // object to
                bankINUrlConnection.setBufferedReader();
                BufferedReader in = bankINUrlConnection.getBufferedReader();
                while ((response = in.readLine()) != null)
                    buffer.append(response);
                endTime = System.currentTimeMillis();
                // Check the difference of start time and end time of IN request
                // response
                // against the warn time, if it takes more time Handle the event
                // with level INFO and
            } // try
            catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", " response form interface is null exception is " + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "BankINHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while getting response from Bank IN e: " + e.getMessage());
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            } finally {
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));

                _log.error("sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + topUpTimeOut);

            }
            responseStr = buffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "responseStr:" + responseStr);
            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, " INTERFACE_ID:" + _interfaceID + " Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
            // Get the HTTP Status code and check whether it is OK or Not??
            String httpStatus = bankINUrlConnection.getResponseCode();
            _requestMap.put("PROTOCOL_STATUS", httpStatus);
            if (!BankINI.RESULT_HTTP_OK.equals(httpStatus))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            if (InterfaceUtil.isNullString(responseStr)) {
                // send alert message(TO BE IMPLEMENTED)
                // reconciliation case
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from bank IN");
                _log.error("sendRequestToIN", "NULL response from interface");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                // commented code may be used in future to support on line
                // cancel request
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            // Parse the response using the parseResponse method of
            // BankINRequest Formatter class.
            _responseMap = _formatter.parseResponse(p_action, responseStr);

            // Get the interface status and set to the requestMap
            String result = (String) _responseMap.get("status");
            _requestMap.put("INTERFACE_STATUS", result);

            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType))) {
                if (_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    _interfaceCloser.resetCounters(_interfaceCloserVO, _requestMap);
                _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO);
            }

            if (!InterfaceUtil.isNullString(result) && !(result.equals(BankINI.RESULT_OK))) {
                _log.info("sendRequestToIN", " result=" + result);
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Status of the response, result: " + result);
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            // send alert message(TO BE IMPLEMENTED)
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "System Exception:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                if (bankINUrlConnection != null)
                    bankINUrlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception while closing BankINUrl Connection:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Not able to close connection:" + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action:" + p_action + " responseStr:" + responseStr);
        }// end of finally
    }// end of sendRequestToIN

    /**
     * This method is used to get interface specific values from FileCache(load
     * at starting)based on
     * interface id and set to the requested map.These parameters are
     * 
     * @param String
     *            p_interfaceID
     * @throws Exception
     */
    private void setInterfaceParameters(String p_interfaceID) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_interfaceID=" + p_interfaceID);

        try {
            String cancelTxnAllowed = FileCache.getValue(p_interfaceID, "CANCEL_TXN_ALLOWED");
            if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
            if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(p_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
                _log.error("setInterfaceParameters", "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(p_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
            if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

            String cancelNA = FileCache.getValue(p_interfaceID, "CANCEL_NA");
            if (InterfaceUtil.isNullString(cancelNA)) {
                _log.error("setInterfaceParameters", "Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _requestMap.put("CANCEL_NA", cancelNA.trim());

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap" + _requestMap);
        }// end of finally

    }// end of setInterfaceParameters

    /**
     * This method used to get the system language mapped in FileCache based on
     * the INLanguge.Includes following
     * If the Mapping key not defined in IN file handle the event as System
     * Error with level FATAL.
     * If the Mapping is not defined handle the event as SYSTEM INFO with level
     * MAJOR and set empty string.
     * 
     * @throws Exception
     */
    private void setLanguageFromMapping() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setLanguageFromMapping", "Entered");
        String mappedLang = "";
        String[] mappingArr;
        String[] tempArr;
        boolean mappingNotFound = true;// Flag defines whether the mapping of
                                       // language is found or not.
        String langFromIN = null;
        try {
            // Get the mapping string from the FileCache and storing all the
            // mappings into array which are separated by ','.
            String mappingString = (String) FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
            if (InterfaceUtil.isNullString(mappingString)) {
                mappingString = "";
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "BankINHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "LANGUAGE_MAPPING is not defined in IN file,Hence setting the Default language");
            }
            langFromIN = (String) _responseMap.get("prof_lang");
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "mappingString = " + mappingString + " langFromIN = " + langFromIN);
            mappingArr = mappingString.split(",");
            // Iterating the mapping array to map the IN language from the
            // system language,if found break the loop.
            for (int in = 0; in < mappingArr.length; in++) {
                tempArr = mappingArr[in].split(":");
                if (langFromIN.equals(tempArr[0].trim())) {
                    mappedLang = tempArr[1];
                    mappingNotFound = false;
                    break;
                }
            }// end of for loop
             // if the mapping of IN language with our system is not
             // found,handle the event
            if (mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "BankINHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  " + langFromIN + " is not defined in IN file Hence setting the Default language");
            // Set the mapped language to the requested map with key as
            // IN_LANGUAGE.
            _requestMap.put("IN_LANG", mappedLang);
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("setLanguageFromMapping", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BankINHandler[setLanguageFromMapping]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Mapping KEY for language is not defined in IN file and Getting Excption=" + e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setLanguageFromMapping", "Exited mappedLang =" + mappedLang);
        }// end of finally setLanguageFromMapping

    }// end of setLanguageFromMapping

    /**
     * Method to Check interface status before sending request.
     * 
     * @throws BTSLBaseException
     */
    private void checkInterfaceB4SendingRequest() throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("checkInterfaceB4SendingRequest", "Entered");

        try {
            _interfaceCloserVO = (InterfaceCloserVO) InterfaceCloserController._interfaceCloserVOTable.get(_interfaceID);
            _interfaceLiveStatus = (String) _requestMap.get("INT_ST_TYPE");
            _interfaceCloserVO.setControllerIntStatus(_interfaceLiveStatus);
            _interfaceCloser = _interfaceCloserVO.getInterfaceCloser();

            // Get AUTO_RESUME_SUPPORT property from IN FILE. If it is not
            // defined then set it as 'N'.
            String autoResumeSupported = FileCache.getValue(_interfaceID, "AUTO_RESUME_SUPPORT");
            if (InterfaceUtil.isNullString(autoResumeSupported)) {
                autoResumeSupported = "N";
                _log.error("checkInterfaceB4SendingRequest", "Value of AUTO_RESUME_SUPPORT is not defined in the INFile");
            }

            // If Controller sends 'A' and interface status is suspended, expiry
            // is checked.
            // If Controller sends 'M', request is forwarded to IN after
            // resetting counters.
            if (InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(_interfaceLiveStatus) && _interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND)) {
                // Check if Auto Resume is supported by IN or not.If not then
                // throw exception. request would not be sent to IN.
                if ("N".equals(autoResumeSupported)) {
                    _log.error("checkInterfaceB4SendingRequest", "Interface Suspended.");
                    throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
                }
                // If "Auto Resume" is supported then only check the expiry of
                // interface, if expired then only request would be sent to IN
                // otherwise checkExpiry method throws exception
                if (_isSameRequest)
                    _interfaceCloser.checkExpiryWithoutExpiryFlag(_interfaceCloserVO);
                else
                    _interfaceCloser.checkExpiry(_interfaceCloserVO);
            }
            // this block is executed when Interface is manually resumed
            // (Controller sends 'M')from suspend state
            else if (InterfaceCloserI.INTERFACE_MANNUAL_ACTIVE.equals(_interfaceCloserVO.getControllerIntStatus()) && _interfaceCloserVO.getFirstSuspendAt() != 0)
                _interfaceCloser.resetCounters(_interfaceCloserVO, null);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkInterfaceB4SendingRequest", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkInterfaceB4SendingRequest", "Exited");
        }

    }

    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * 
     * @throws BTSLBaseException
     */
    private void handleCancelTransaction() throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("handleCancelTransaction", "Entered.");
        String cancelTxnAllowed = null;
        String cancelTxnStatus = null;
        String reconciliationLogStr = null;
        String cancelCommandStatus = null;
        String cancelNA = null;
        String interfaceStatus = null;
        Log reconLog = null;
        String systemStatusMapping = null;
        // int cancelRetryCount=0;
        try {
            _requestMap.put("REMARK1", FileCache.getValue(_interfaceID, "REMARK1"));
            _requestMap.put("REMARK2", FileCache.getValue(_interfaceID, "REMARK2"));
            // get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "reconLog." + reconLog);
            cancelTxnAllowed = (String) _requestMap.get("CANCEL_TXN_ALLOWED");
            // if cancel transaction is not supported by IN, get error codes
            // from mapping present in IN fILE,write it
            // into recon log and throw exception (This exception tells the
            // final status of transaction which was ambiguous) which would be
            // handled by validate, credit or debitAdjust methods
            if ("N".equals(cancelTxnAllowed)) {
                cancelNA = (String) _requestMap.get("CANCEL_NA");// Cancel
                                                                 // command
                                                                 // status as
                                                                 // NA.
                cancelCommandStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, cancelNA, "CANCEL_COMMAND_STATUS_MAPPING");
                _requestMap.put("MAPPED_CANCEL_STATUS", cancelCommandStatus);
                interfaceStatus = (String) _requestMap.get("INTERFACE_STATUS");
                systemStatusMapping = (String) _requestMap.get("SYSTEM_STATUS_MAPPING");
                cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, interfaceStatus, systemStatusMapping); // PreTUPs
                                                                                                                            // Transaction
                                                                                                                            // status
                                                                                                                            // as
                                                                                                                            // FAIL/AMBIGUOUS
                                                                                                                            // based
                                                                                                                            // on
                                                                                                                            // value
                                                                                                                            // of
                                                                                                                            // SYSTEM_STATUS_MAPPING
                _requestMap.put("MAPPED_SYS_STATUS", cancelTxnStatus);
                reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
                reconLog.info("", reconciliationLogStr);
                if (!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
                    throw new BTSLBaseException(this, "handleCancelTransaction", cancelTxnStatus); // //Based
                                                                                                   // on
                                                                                                   // the
                                                                                                   // value
                                                                                                   // of
                                                                                                   // SYSTEM_STATUS
                                                                                                   // mark
                                                                                                   // the
                                                                                                   // transaction
                                                                                                   // as
                                                                                                   // FAIL
                                                                                                   // or
                                                                                                   // AMBIGUOUS
                                                                                                   // to
                                                                                                   // the
                                                                                                   // system.(//should
                                                                                                   // these
                                                                                                   // be
                                                                                                   // put
                                                                                                   // in
                                                                                                   // error
                                                                                                   // log
                                                                                                   // also.
                                                                                                   // ??????)
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                // added to discard amount field from the message.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("handleCancelTransaction", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "handleCancelTransaction", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "Exited");
        }

    }

}// BankINHandler
