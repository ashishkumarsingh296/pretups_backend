package com.inter.mobinilvoms;

/*
 * @(#)MobinilVOMSHandler.java
 * ----------------------------------------------------------------------
 * Name Date History
 * ------------------------------------------------------------------------
 * Vinay Kumar Singh 22/11/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * Handler class for Voucher Management System.
 */

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.pretups.inter.util.VOMSProductVO;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;

public class MobinilVOMSINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(MobinilVOMSINHandler.class.getName());
    private HashMap<String, String> _requestMap = null;
    private HashMap<String, String> _responseMap = null;
    private String _inTXNID = null;
    private String _msisdn = null;
    private String _interfaceID = null;
    private String _referenceID = null;
    private MobinilVOMSReqResFormatter _formatter = null;
    private String _serviceClass = null;
    private String _errorMsg = null;
    private int _checkAmbRetryCount = 0;
    private int _checkFailRetryCount = 0;
    private int _retryCount = 0;
    Connection _con = null;
    MComConnectionI _mcomCon = null;

    public MobinilVOMSINHandler() {
        _formatter = new MobinilVOMSReqResFormatter();
    }

    /**
     * validate Method is used for getting the pin and serial number
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validate(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered " + InterfaceUtil.getPrintMap(p_map));
        try {
            _requestMap = p_map;
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _interfaceID = _requestMap.get("INTERFACE_ID");

            setInterfaceParameters();

            // Get the oracle connection.
            if (_mcomCon == null){
            	_mcomCon = new MComConnection();
            	_con=_mcomCon.getConnection();
            }
            processVoucherRechargeRequest();
            _serviceClass = _requestMap.get("SERVICE_CLASS");
            if (InterfaceUtil.isNullString(_serviceClass)) {
                String serviceClassCode = FileCache.getValue(_interfaceID, "SERVICE_CLASS");
                if (InterfaceUtil.isNullString(serviceClassCode)) {
                    serviceClassCode = "NOT_RECEIVED";

                }
                _requestMap.put("SERVICE_CLASS", serviceClassCode);
            }
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            throw e;
        } catch (Throwable tre) {
            tre.printStackTrace();
        } finally {
			if (_mcomCon != null) {
				_mcomCon.close("MobinilVOMSINHandler#validate");
				_mcomCon = null;
			}
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap=" + _requestMap);
        }
    }

    /**
     * credit Method is used for updating the status to consume in voms_vouches
     * table.
     * 
     * @param p_requestMap
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap=" + p_requestMap);
        try {
            _requestMap = p_requestMap;
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _interfaceID = _requestMap.get("INTERFACE_ID");

            // Get the oracle connection.
            if (_mcomCon == null){
            	_mcomCon = new MComConnection();
            	_con=_mcomCon.getConnection();
            }

            // Now update the voucher state in the data base
            updateVoucherInDb(_con, _requestMap);

            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
			if (_mcomCon != null) {
				_mcomCon.close("MobinilVOMSINHandler#credit");
				_mcomCon = null;
			}
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
        if (_log.isDebugEnabled())
            _log.debug("debit Entered ", " _requestMap: " + _requestMap);
        try {
            credit(p_map);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debit Exited ", " _requestMap: " + _requestMap);
        }
    }

    /**
     * creditAdjust Method is used for updating the status to ambiguous in
     * voms_vouches table.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust", "Entered _requestMap=" + _requestMap);
        try {
            credit(p_map);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust Exited ", " _requestMap: " + _requestMap);
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

    private void processVoucherRechargeRequest() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("processVoucherRechargeRequest", "Entered");

        try {
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _interfaceID = _requestMap.get("INTERFACE_ID");

            // Get the voucher serial number from the data base
            _requestMap.put("UPDATE_STATUS", "UP");
            _requestMap = getVoucherSerialNumber(_con, _requestMap);

            try {
                // Get the PIN from the IN against the serial number picked from
                // data base
                sendRequestToIN(MobinilVOMSI.ACTION_GET_VOUCHER_DETAILS);
            } catch (BTSLBaseException be) {
                be.printStackTrace();
                // Now update the voucher state in the data base
                _requestMap.put("PREVIOUS_STATUS", "UP");
                _requestMap.put("UPDATE_STATUS", MobinilVOMSI.VERIFY_ON_HOLD);
                updateVoucherInDb(_con, _requestMap);
                throw be;
            }

            try {
                // If voucher state is 5(Unavailable), then send a update
                // request to IN for the state.
                if (_requestMap.get("VOUCHER_STATE_AT_IN").equals(_requestMap.get("VOUCHER_UNAVAILABLE_STATE_CODE")))
                    sendRequestToIN(MobinilVOMSI.ACTION_UPDATE_VOUCHER_STATE);
            } catch (BTSLBaseException be) {
                be.printStackTrace();
                // Now update the voucher state in the data base
                _requestMap.put("PREVIOUS_STATUS", "UP");
                _requestMap.put("UPDATE_STATUS", MobinilVOMSI.VERIFY_ON_HOLD);
                updateVoucherInDb(_con, _requestMap);
                throw be;
            }

            // Update the PIN in the data base
            _requestMap.put("PREVIOUS_STATUS", "UP");
            _requestMap.put("UPDATE_STATUS", "UP");
            updateVoucherInDb(_con, _requestMap);
        } catch (BTSLBaseException be) {
            be.printStackTrace();

            _errorMsg = be.getMessage();
            _log.error("processVoucherRechargeRequest", "BTSLBaseException be:" + be.getMessage());

            // Check whether retry is allowed for the ambiguous response and
            // it's count
            if (_errorMsg.equals(InterfaceErrorCodesI.AMBIGOUS) && "Y".equals(_requestMap.get("RETRY_ALLOWED_FOR_AMBIGUOUS"))) {
                ++_checkAmbRetryCount;
                _retryCount = _checkAmbRetryCount;
                int ambRetryCount = Integer.parseInt(_requestMap.get("AMBIGUOUS_RETRY_CNT"));
                if (_log.isDebugEnabled())
                    _log.debug("processVoucherRechargeRequest", "AMBIGOUS exception block, _checkAmbRetryCount=" + _checkAmbRetryCount + " and ambRetryCount=" + ambRetryCount);
                if (_checkAmbRetryCount < ambRetryCount)
                    processVoucherRechargeRequest();
                else {
                    if (_log.isDebugEnabled())
                        _log.debug("processVoucherRechargeRequest", "AMBIGOUS retry exceeded, so throwing AMBIGOUS exception.");
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(this, "processVoucherRechargeRequest", InterfaceErrorCodesI.AMBIGOUS);// AMBIGOUS
                }
            }
            // Check whether retry is allowed for the fail response and it's
            // count
            else if ((_errorMsg.equals(InterfaceErrorCodesI.ERROR_RESPONSE) || _errorMsg.equals(InterfaceErrorCodesI.VOMS_STATE_NOT_ALLOWED_AT_IN)) && "Y".equals(_requestMap.get("RETRY_ALLOWED_FOR_FAIL"))) {
                ++_checkFailRetryCount;
                _retryCount = _checkFailRetryCount;
                int failRetryCount = Integer.parseInt(_requestMap.get("NO_OF_PIN_REQUEST"));
                if (_log.isDebugEnabled())
                    _log.debug("processVoucherRechargeRequest", "ERROR_RESPONSE or VOMS_STATE_NOT_ALLOWED_AT_IN exception block, _checkFailRetryCount=" + _checkFailRetryCount + " and failRetryCount=" + failRetryCount);
                if (_checkFailRetryCount < failRetryCount)
                    processVoucherRechargeRequest();
                else {
                    if (_log.isDebugEnabled())
                        _log.debug("processVoucherRechargeRequest", "ERROR_RESPONSE retry exceeded, so throwing ERROR_RESPONSE exception.");
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
                    throw new BTSLBaseException(this, "processVoucherRechargeRequest", InterfaceErrorCodesI.ERROR_RESPONSE);// ERROR_RESPONSE
                }
            } else
                throw be;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("processVoucherRechargeRequest", "Exited _requestMap=" + _requestMap);
        }
    }

    /**
     * This method is responsible to retrieve the serial number from the data
     * base as per requested amount.
     * 
     * @param Connection
     *            p_con
     * @param HashMap
     *            <String, String> p_requestMap
     * @return HashMap<String, String>
     * @throws BTSLBaseException
     */
    private HashMap<String, String> getVoucherSerialNumber(Connection p_con, HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getVoucherSerialNumber", "Entered");
        HashMap profileMap = null;
        VOMSProductVO productVO = null;

        try {
            String networkCode = p_requestMap.get("NETWORK_CODE");

            Date currDate = InterfaceUtil.getDateTimeFromDateString(p_requestMap.get("TRANSFER_DATE"));
            long requestedAmt = Long.parseLong(p_requestMap.get("INTERFACE_AMOUNT"));
            String currentStatus = p_requestMap.get("UPDATE_STATUS");
            String modifiedBy = p_requestMap.get("SENDER_USER_ID");
            String statusChangeSource = p_requestMap.get("SOURCE");
            if (_log.isDebugEnabled())
                _log.debug("getVoucherSerialNumber", "UPDATE_STATUS=" + currentStatus);

            ArrayList<String> transferIdList = new ArrayList<String>();
            transferIdList.add(p_requestMap.get("TRANSACTION_ID"));

            // Load the active profiles for specified network for current date
            VOMSVoucherDAO vomsDAO = new VOMSVoucherDAO();

            String timeStampCheck = FileCache.getValue(_interfaceID, "TIME_STAMP_CHK");
            boolean timeStmpChk = false;
            if ("Y".equals(timeStampCheck))
                timeStmpChk = true;

            profileMap = vomsDAO.loadActiveProfiles(_con, networkCode, currDate, timeStmpChk);

            productVO = (VOMSProductVO) profileMap.get(String.valueOf(requestedAmt));

            if (productVO == null)
                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_MRP_REQUESTED);

            // Get the Serial Number details of voucher. Only voucher with
            // status enabled will be picked from VOMS_VOUCHERS table.
            ArrayList voucherList = VOMSVoucherDAO.loadPINAndSerialNumber(p_con, productVO, modifiedBy, currDate, statusChangeSource, currentStatus, transferIdList, _requestMap.get("NETWORK_CODE"), 1);
            VOMSVoucherVO vomsVO = (VOMSVoucherVO) voucherList.get(0);
            if (vomsDAO.insertDetailsInVoucherAudit(p_con, vomsVO) > 0)
                p_con.commit();
            else
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VOMSHandler[validate]", "", "", networkCode, "Not able to add Serial No=" + vomsVO.getSerialNo() + " in voucher history tables");

            // Set the transaction status and other details in map
            p_requestMap.put("PRODUCT_ID", productVO.getProductID());
            p_requestMap.put("SERIAL_NUMBER", vomsVO.getSerialNo());
            p_requestMap.put("TALK_TIME", String.valueOf(productVO.getTalkTime()));
            p_requestMap.put("VALIDITY", String.valueOf(productVO.getValidity()));
            p_requestMap.put("PAYABLE_AMT", String.valueOf(requestedAmt));
            p_requestMap.put("PIN", vomsVO.getPinNo());
            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
                p_requestMap.put("RECEIVER_PAYABLE_AMT", String.valueOf(vomsVO.getPayableAmount()));

            return p_requestMap;
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getVoucherSerialNumber", "Exited");
        }
    }

    /**
     * This method is responsible to retrieve the serial number from the data
     * base as per requested amount.
     * 
     * @param Connection
     *            p_con
     * @param HashMap
     *            <String, String> p_requestMap
     * @return HashMap<String, String>
     * @throws BTSLBaseException
     */
    private void updateVoucherInDb(Connection p_con, HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("updateVoucherInDb", "Entered with p_requestMap=" + p_requestMap);
        VOMSVoucherVO vomsVO = null;
        VOMSVoucherDAO vomsDAO = null;
        int updateCount = 0;

        try {
            String action = _requestMap.get("INTERFACE_ACTION");
            if (_log.isDebugEnabled())
                _log.debug("updateVoucherInDb", "action=" + action);

            Date currDate = InterfaceUtil.getDateTimeFromDateString(_requestMap.get("TRANSFER_DATE"));
            String currentStatus = _requestMap.get("UPDATE_STATUS");
            String previousStatus = _requestMap.get("PREVIOUS_STATUS");
            String modifiedBy = _requestMap.get("SENDER_USER_ID");
            String statusChangeSource = _requestMap.get("SOURCE");
            String pinRecFromIN = _requestMap.get("PIN");

            // Update the voucher status to consume
            vomsVO = new VOMSVoucherVO();
            // Update the Voucher status to under process if the previous status
            // is enabled.
            vomsVO.setPreviousStatus(previousStatus);
            vomsVO.setCurrentStatus(currentStatus);
            vomsVO.setModifiedBy(modifiedBy);
            vomsVO.setModifiedOn(currDate);
            vomsVO.setStatusChangeSource(statusChangeSource);
            vomsVO.setStatusChangePartnerID(modifiedBy);
            vomsVO.setSerialNo(_requestMap.get("SERIAL_NUMBER"));
            vomsVO.setTransactionID(_requestMap.get("TRANSACTION_ID"));
            vomsVO.setUserLocationCode(_requestMap.get("NETWORK_CODE"));
            if (!InterfaceUtil.isNullString(pinRecFromIN))
                vomsVO.setPinNo(pinRecFromIN);
            else
                vomsVO.setPinNo("");

            vomsDAO = new VOMSVoucherDAO();
            if ("V".equals(action))
                updateCount = VOMSVoucherDAO.updateVoucherStatusWithPIN(p_con, vomsVO);
            else
                updateCount = VOMSVoucherDAO.updateVoucherStatus(p_con, vomsVO);

            if (updateCount > 0) {
                if (vomsDAO.insertDetailsInVoucherAudit(p_con, vomsVO) > 0)
                    p_con.commit();
                else {
                    p_con.rollback();
                    throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_INSERTION_AUDIT_TABLE);
                }
            } else
                throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_UPDATION);
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("updateVoucherInDb", "Exited");
        }
    }

    /**
     * This method is responsible to send the request to IN.
     * 
     * @param int p_action
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(int p_action) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", "Entered with p_action=" + p_action);

        String requestString = "";
        String responseStr = "";
        MobinilVOMSUrlConnection urlConnection = null;
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        int readTimeOut = 0;
        String inReconID = null;
        StringBuffer buffer = null;
        try {
            _responseMap = new HashMap<String, String>();
            inReconID = _requestMap.get("IN_RECON_ID");
            if (inReconID == null)
                inReconID = _inTXNID;
            try {
                requestString = _formatter.generateRequest(p_action, _requestMap);
                // Fetch the URL,connect timeout,read timeout(validation or
                // top-up) and keep alive values from the INFile
                // Get URL from request map
                String connectionURL = _requestMap.get("CONNECTION_URL");

                // Get connect time out from request map
                String connectTimeOutStr = _requestMap.get("CONNECT_TIMEOUT");
                int connectTimeOut = Integer.parseInt(connectTimeOutStr);
                // Get keep alive from INFile
                String keepAlive = _requestMap.get("KEEP_ALIVE");

                // Get read time out from INFile
                String readTimeOutStr = _requestMap.get("READ_TIMEOUT");
                readTimeOut = Integer.parseInt(readTimeOutStr);

                String userAgent = _requestMap.get("USER_AGENT");
                String contentType = _requestMap.get("CONTENT_TYPE");
                String authorization = _requestMap.get("AUTHORIZATION");

                // URL connection for sending and retrieving request and
                // response respectively.
                urlConnection = new MobinilVOMSUrlConnection(connectionURL, connectTimeOut, readTimeOut, keepAlive, userAgent, contentType, authorization);

                TransactionLog.log(_inTXNID, _referenceID, _msisdn, _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, " INTERFACE_ID:" + _interfaceID + " Request string=" + requestString, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "p_action=" + p_action);
                // After the successful connection creation get a print writer
                // object from the MobinilVOMSUrlConnection class.
                PrintWriter out = urlConnection.getPrintWriter();
                out.flush();
                // Post the request string to the connection out put stream and
                // Store the time when request is send to IN under the key
                // IN_START_TIME into requestMap.
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                out.println(requestString);
                out.flush();
            } catch (BTSLBaseException be) {
                throw be;
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, _requestMap.get("NETWORK_CODE"), "Error while getting the http connection for INTERFACE_ID=[" + _interfaceID + "]" + " Exception : " + e.getMessage() + " action: " + p_action);
                _log.error("sendRequestToIN", "Exception e=" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }// end of catch-Exception
            String response = "";
            try {
                // Create buffered reader and Read Response from the IN
                buffer = new StringBuffer();

                // After sending the request to IN set the Buffered Reader
                // object to read the connection input stream of the
                // urlConnection class.
                urlConnection.setBufferedReader();
                BufferedReader in = urlConnection.getBufferedReader();
                while ((response = in.readLine()) != null)
                    buffer.append(response);

                endTime = System.currentTimeMillis();
                // Check the difference of start time and end time of IN request
                // response
                // against the warn time, if it takes more time Handle the event
                // with level INFO and
                // message as AlcatelOCI IN is taking more time than the
                // threshold time.
                // Warn time
                warnTime = Long.parseLong(_requestMap.get("WARN_TIMEOUT"));
                if (endTime - startTime > warnTime) {
                    _log.info("sendRequestToIN", "WARN time reaches for the Alcatel462IN, startTime: " + startTime + " endTime: " + endTime + " warnTime: " + warnTime + " time taken: " + (endTime - startTime));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVOMSINHandler[sendRequestToIN]", _inTXNID, _msisdn, _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "AlcatelOCI452 IN is taking more time than the warning threshold. Time: " + (endTime - startTime) + " action: " + p_action);
                }
            } // try
            catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", " Response form interface is null, exception is=" + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVOMSINHandler[sendRequestToIN]", _inTXNID, _msisdn, _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Exception while getting response from Alcatel462IN e: " + e.getMessage());
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            } finally {
                if (endTime == 0)
                    endTime = System.currentTimeMillis();
                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                _log.error("sendRequestToIN", "Request sent to IN at=" + startTime + ", Response received from IN at=" + endTime + ", defined read time out is=" + readTimeOut + " ms, and Time taken by IN is=" + (endTime - startTime) + " mili-seconds.");
            }
            responseStr = buffer.toString();
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Response Str=" + responseStr);

            if (p_action == MobinilVOMSI.ACTION_GET_VOUCHER_DETAILS) {
                String responseStrWithHidePin = _formatter.writeTransactionLogWithHidePin(responseStr);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, " INTERFACE_ID:" + _interfaceID + " Request String=" + requestString + " Response string=" + responseStrWithHidePin, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
            }

            else {
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, " INTERFACE_ID:" + _interfaceID + " Request String=" + requestString + " Response string=" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " INTERFACE ID = " + _interfaceID + " action=" + p_action);
            }
            // System.out.println("Old String is :"+responseStr);
            // Get the HTTP Status code and check whether it is OK or Not??
            String httpStatus = urlConnection.getResponseCode();
            _requestMap.put("PROTOCOL_STATUS", httpStatus);
            if (!MobinilVOMSI.HTTP_STATUS_OK.equals(httpStatus))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);

            if (InterfaceUtil.isNullString(responseStr)) {
                _log.error("sendRequestToIN", "NULL response from interface.");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            // Parse the response using the parseResponse method of
            // MobinilVOMSReqResFormatter class.
            _responseMap = _formatter.parseResponse(p_action, responseStr, _interfaceID);

            // Get the interface responseCode and set to the requestMap
            String responseCode = _responseMap.get("responseCode");
            _requestMap.put("INTERFACE_STATUS", responseCode);
            if (InterfaceUtil.isNullString(responseCode) || !responseCode.equals(MobinilVOMSI.RESULT_OK)) {
                VoucherAmbiFailStatusLog.log(_requestMap.get("TRANSACTION_ID"), _requestMap.get("SERIAL_NUMBER"), String.valueOf(_retryCount), String.valueOf(p_action), responseCode, ", ErrorMsg=" + _errorMsg);

                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            if (p_action == MobinilVOMSI.ACTION_GET_VOUCHER_DETAILS) {
                _requestMap.put("GET_VOUCHER_DETAIL_STATUS", responseCode);
                String pinFromIN = (String) _responseMap.get("activationCode");
                // If PIN is not received from the IN, throw an exception.
                if (!InterfaceUtil.isNullString(pinFromIN)) {
                    String encryptedPIN = new CryptoUtil().encrypt(pinFromIN, Constants.KEY);
                    _requestMap.put("PIN", encryptedPIN);
                } else
                    throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_PIN_NOT_RECEIVED_FROM_IN);

                _requestMap.put("VOUCHER_EXPIRY_DATE", _responseMap.get("expiryDate"));
                String state = _responseMap.get("state");
                _requestMap.put("VOUCHER_STATE_AT_IN", state);

                Object[] successList = MobinilVOMSI.VOUCHER_ALLOWED_STATE.split(",");
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Allowed Status=" + MobinilVOMSI.VOUCHER_ALLOWED_STATE + ", received state from IN is=" + state);

                if (!Arrays.asList(successList).contains(state)) {
                    _log.error("sendRequestToIN", "Allowed state of voucher on IN are=" + MobinilVOMSI.VOUCHER_ALLOWED_STATE + ", state received from IN is=" + state);

                    VoucherAmbiFailStatusLog.log(_inTXNID, _requestMap.get("SERIAL_NUMBER"), String.valueOf(_retryCount), String.valueOf(p_action), responseCode, "STATE received from IN is=" + state);
                    throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_STATE_NOT_ALLOWED_AT_IN);
                }
            } else if (p_action == MobinilVOMSI.ACTION_UPDATE_VOUCHER_STATE)
                _requestMap.put("UPDATE_VOUCHER_STATE_STATUS", responseCode);
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, _requestMap.get("NETWORK_CODE") + " INTERFACE ID=" + _interfaceID + " Stage=" + p_action, " System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            try {
                if (urlConnection != null)
                    urlConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendRequestToIN", "Exception while closing urlConnection Connection:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVOMSINHandler[sendRequestToIN]", _referenceID, _msisdn, _requestMap.get("NETWORK_CODE") + " INTERFACE ID=" + _interfaceID + " Stage=" + p_action, " Not able to close connection=" + e.getMessage());
            }
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exiting p_action=" + p_action);
        }// end of finally
    }// end of sendRequestToIN

    /**
     * This method is used to get interface specific values from FileCache(load
     * at starting)based on
     * interface id and set to the requested map.These parameters are
     * 1. CONNECT_TIMEOUT
     * 2. READ_TIMEOUT
     * 3. WARN_TIMEOUT
     * 4. AMBIGUOUS_RETRY_CNT
     * 5. CONNECTION_URL
     * 6. VOUCHER_UNAVAILABLE_STATE_CODE
     * 7. NO_OF_PIN_REQUEST
     * 
     * @throws BTSLBaseException
     *             , Exception
     */
    private void setInterfaceParameters() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", " Entered");
        try {
            String connetionURL = FileCache.getValue(_interfaceID, "CONNECTION_URL");
            if (InterfaceUtil.isNullString(connetionURL)) {
                _log.error("setInterfaceParameters", "Value of CONNECTION_URL is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CONNECTION_URL", connetionURL.trim());

            String connectTimeOut = FileCache.getValue(_interfaceID, "CONNECT_TIMEOUT");
            if (InterfaceUtil.isNullString(connectTimeOut)) {
                _log.error("setInterfaceParameters", "Value of CONNECT_TIMEOUT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CONNECT_TIMEOUT", connectTimeOut.trim());

            String readTimeOut = FileCache.getValue(_interfaceID, "READ_TIMEOUT");
            if (InterfaceUtil.isNullString(readTimeOut)) {
                _log.error("setInterfaceParameters", "Value of READ_TIMEOUT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("READ_TIMEOUT", readTimeOut.trim());

            String warnTimeOut = FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeOut)) {
                _log.error("setInterfaceParameters", "Value of WARN_TIMEOUT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT", warnTimeOut.trim());

            String keepAlive = FileCache.getValue(_interfaceID, "KEEP_ALIVE");
            if (InterfaceUtil.isNullString(keepAlive)) {
                keepAlive = "N";
                _log.error("setInterfaceParameters", "Value of KEEP_ALIVE is not defined in the INFile, so setting it's value N.");
            }
            _requestMap.put("KEEP_ALIVE", keepAlive.trim());

            String voucherUnavailableState = FileCache.getValue(_interfaceID, "VOUCHER_UNAVAILABLE_STATE_CODE");
            if (InterfaceUtil.isNullString(voucherUnavailableState)) {
                voucherUnavailableState = "5";
                _log.error("setInterfaceParameters", "Value of VOUCHER_UNAVAILABLE_STATE_CODE is not defined in the INFile, so setting it's value 5.");
            }
            _requestMap.put("VOUCHER_UNAVAILABLE_STATE_CODE", voucherUnavailableState.trim());

            // RETRY_ALLOWED_FOR_AMBIGUOUS parameter specifies whether retry
            // allowed
            // in case of GetVoucherDetails or UpdateVoucherState response is
            // ambiguous from IN.
            String retryCountForAmbiguous = FileCache.getValue(_interfaceID, "RETRY_ALLOWED_FOR_AMBIGUOUS");
            if (InterfaceUtil.isNullString(retryCountForAmbiguous)) {
                retryCountForAmbiguous = "N";
                _log.error("setInterfaceParameters", "Value of RETRY_ALLOWED_FOR_AMBIGUOUS is not defined in the INFile, so setting it's value N.");
            }
            _requestMap.put("RETRY_ALLOWED_FOR_AMBIGUOUS", retryCountForAmbiguous.trim());

            String ambRetryCount = FileCache.getValue(_interfaceID, "AMBIGUOUS_RETRY_CNT");
            if (InterfaceUtil.isNullString(ambRetryCount)) {
                ambRetryCount = "0";
                _log.error("setInterfaceParameters", "Value of AMBIGUOUS_RETRY_CNT is not defined in the INFile, so setting it's value 0.");
            }
            _requestMap.put("AMBIGUOUS_RETRY_CNT", ambRetryCount.trim());

            // RETRY_ALLOWED_FOR_FAIL parameter specifies whether retry allowed
            // in case of GetVoucherDetails or UpdateVoucherState response is
            // fail from IN.
            String retryCountForFail = FileCache.getValue(_interfaceID, "RETRY_ALLOWED_FOR_FAIL");
            if (InterfaceUtil.isNullString(retryCountForFail)) {
                retryCountForFail = "N";
                _log.error("setInterfaceParameters", "Value of RETRY_ALLOWED_FOR_FAIL is not defined in the INFile, so setting it's value N.");
            }
            _requestMap.put("RETRY_ALLOWED_FOR_FAIL", retryCountForFail.trim());

            // NO_OF_PIN_REQUEST parameter specifies the number of request for
            // different PIN in case
            // voucher PIN is neither available(0) nor unavailable(5) on the IN.
            String pinReqRetryCount = FileCache.getValue(_interfaceID, "NO_OF_PIN_REQUEST");
            if (InterfaceUtil.isNullString(pinReqRetryCount)) {
                pinReqRetryCount = "0";
                _log.error("setInterfaceParameters", "Value of NO_OF_PIN_REQUEST is not defined in the INFile, so setting it's value 0.");
            }
            _requestMap.put("NO_OF_PIN_REQUEST", pinReqRetryCount.trim());

            String userAgent = FileCache.getValue(_interfaceID, "USER_AGENT");
            if (InterfaceUtil.isNullString(userAgent)) {
                _log.error("setInterfaceParameters", "Value of USER_AGENT is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("USER_AGENT", userAgent.trim());

            String contentType = FileCache.getValue(_interfaceID, "CONTENT_TYPE");
            if (InterfaceUtil.isNullString(contentType)) {
                _log.error("setInterfaceParameters", "Value of CONTENT_TYPE is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CONTENT_TYPE", contentType.trim());

            String authorization = FileCache.getValue(_interfaceID, "AUTHORIZATION");
            if (InterfaceUtil.isNullString(authorization)) {
                _log.error("setInterfaceParameters", "Value of AUTHORIZATION is not defined in the INFile.");
                throw new BTSLBaseException(this, "setInterfaceParameters ", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("AUTHORIZATION", authorization.trim());

        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exiting with _requestMap=" + _requestMap);
        }// end of finally
    }// end of setInterfaceParameters

    public static void main(String[] a) {
        try {
            MobinilVOMSINHandler vomshandler = new MobinilVOMSINHandler();
            HashMap<String, String> requestMap = new HashMap<String, String>();
            requestMap.put("CONNECTION_URL", "http://172.16.1.121:5540/pretups/Alcatel462SimulatorServlet");
            requestMap.put("READ_TIMEOUT", "4000");
            requestMap.put("KEEP_ALIVE", "N");
            requestMap.put("WARN_TIMEOUT", "5000");
            requestMap.put("AMBIGUOUS_RETRY_CNT", "1");
            requestMap.put("VOUCHER_UNAVAILABLE_STATE_CODE", "5");
            requestMap.put("NO_OF_PIN_REQUEST", "2");
            vomshandler.credit(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}